# OpenSC probe — P1.4a (Refs #98)

Goal: prove the cheap path first. If `opensc-pkcs11.so` signs a DNIe digest,
CWA-14890 secure messaging is unnecessary and ~25 days of work disappear.

## Environment probed

| Component | Version | Source |
|---|---|---|
| OpenSC | `opensc-0.23.0-6.el9.x86_64` | `rpm -q opensc` (Rocky Linux 9 AppStream) |
| pcsc-lite | `pcsc-lite-1.9.4-2.el9.x86_64` | `rpm -q pcsc-lite` |
| pcsc-lite-devel | `pcsc-lite-devel-1.9.4-2.el9` | CRB repo (needed by `pcsc-sys`) |
| Module | `/usr/lib64/opensc-pkcs11.so` → also `/usr/lib64/pkcs11/opensc-pkcs11.so` | `pkcs11-tool -I` |

OpenSC **0.23.0**, not the 0.25 the issue asked for: Rocky 9 AppStream carries
only `0.23.0-6.el9`, EPEL carries nothing, and CRB has no newer build.
Upgrading would mean building from source — explicitly out of scope.

## Probe commands and raw results

```
$ pkcs11-tool --module /usr/lib64/opensc-pkcs11.so -I
Cryptoki version 3.0
Manufacturer     OpenSC Project
Library          OpenSC smartcard framework (ver 0.23)

$ pkcs11-tool --module /usr/lib64/opensc-pkcs11.so -L
No slots.
Available slots:

$ pkcs11-tool --module /usr/lib64/opensc-pkcs11.so --sign \
    --mechanism SHA256-RSA-PKCS --input-file <digest> --output-file /tmp/probe.sig
No slots.                       # exit 1, no output file written

$ opensc-tool --list-readers
No smart card readers found.    # exit 0

$ opensc-tool --list-drivers | grep dnie
  dnie             DNIe: Spanish eID card
```

## Verdict

**Negative by environment, not by capability.** There is no PC/SC reader and no
DNIe on this box, so `pkcs11-tool --sign` stops at `No slots` before it can reach
a card. Nothing in the output contradicts the cheap path:

- The OpenSC **DNIe driver ships in this build** (`opensc-tool --list-drivers`),
  so card recognition is not the blocker.
- The module loads as cryptoki 3.0 and exposes the PKCS#11 surface the Rust
  layer drives (`pkcs11.rs`).

To separate "no card" from "OpenSC cannot sign", the identical Rust sign path was
run end-to-end against **SoftHSM2 2.6.1** (`/usr/lib64/pkcs11/libsofthsm2.so`) —
the same PKCS#11 API OpenSC implements:

```
SIGN_OK module=/usr/lib64/pkcs11/libsofthsm2.so slot=... sig_len=256
```

That run signs a 32-byte digest (2048-bit RSA → 256-byte signature), returns
typed errors for an unknown key label and for an unsupported digest length, and
is deterministic across repeats. The signature is checked by rebuilding the
expected EMSA-PKCS1-v1_5 block from the signed `DigestInfo`, not just by the
token's own verifier: hashing the digest a second time is self-consistent and
would pass a symmetric check while failing in every real CMS verifier. SoftHSM
has no hot-plug, so the mid-session pull case cannot be exercised there; on PC/SC
that path arrives as `SCARD_W_REMOVED_CARD` and is covered by
`error.rs`/`pkcs11.rs` unit tests.

### Reproducing this environment (host state, outside the repo)

The Rust crate needs `libpcsclite.pc`, and a non-GUI client needs polkit
permission to talk to `pcscd`. Nothing here is committed; both are box setup:

```
$ sudo dnf config-manager --set-enabled crb      # provides pcsc-lite-devel on EL9
$ sudo dnf install -y pcsc-lite-devel pcsc-tools opensc softhsm
$ sudo tee /etc/polkit-1/rules.d/49-pcsc-allow.rules >/dev/null <<'EOF'
polkit.addRule(function(action, subject) {
    if (action.id == "org.debian.pcsc-lite.access_pcsc" &&
        subject.isInGroup("wheel")) {
        return polkit.Result.YES;
    }
});
EOF
$ sudo systemctl enable --now pcscd.socket
```

Without the polkit rule `SCardEstablishContext` fails for any non-GUI client and
the reader test sees `CommError` instead of a real answer. SoftHSM setup for the
smoke run:

```
$ mkdir -p /tmp/hsm/tokens
$ printf 'directories.tokendir = /tmp/hsm/tokens\nobjectstore.backend = file\nlog.level = ERROR\n' > /tmp/hsm/softhsm2.conf
$ SOFTHSM2_CONF=/tmp/hsm/softhsm2.conf softhsm2-util --init-token --slot 0 \
    --label smoke --so-pin 12345678 --pin 1234
$ cd src-tauri && SOFTHSM2_CONF=/tmp/hsm/softhsm2.conf \
    cargo run --example sign_smoke -- /usr/lib64/pkcs11/libsofthsm2.so
```

## What a human must decide

Signing a real DNIe 3.x requires hardware this box does not have. The remaining
step is a human running, with a DNIe 3.x in an attached reader:

```
$ openssl dgst -sha256 -binary FILE > /tmp/dnie.digest   # 32-byte digest
$ pkcs11-tool --module /usr/lib64/opensc-pkcs11.so --login --pin <PIN> \
    --sign --mechanism RSA-PKCS --input-file /tmp/dnie.digest \
    --output-file /tmp/dnie.sig
```

**`--mechanism RSA-PKCS`, not `SHA256-RSA-PKCS`:** `--input-file` is already a
digest here, and `SHA256-RSA-PKCS` hashes its input again, so it would produce a
signature that no CMS verifier accepts (the same mistake the Rust layer made
before review — see `pkcs11.rs`, `digest_info_prefix`). `RSA-PKCS` signs the
bytes as given. With `SHA256-RSA-PKCS` the correct input is the *file*, not its
digest.

If that succeeds, secure messaging stays dead and P1.4 continues on the OpenSC
path. If it fails with a card-support error rather than a missing-reader error,
the CWA-14890 work comes back. See the issue thread for the recorded verdict
(Refs #98).
