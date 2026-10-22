// Manual end-to-end smoke for the PKCS#11 sign path (Refs #98).
//
// The box has no DNIe, so this drives the same code through SoftHSM2, which
// speaks the same PKCS#11 API OpenSC exposes. It proves the sign path returns
// real signature bytes and typed errors — the DNIe-specific part is only the
// module path and the card driver.
//
//   SOFTHSM2_CONF=/tmp/hsm/softhsm2.conf \
//   softhsm2-util --init-token --slot 0 --label smoke --so-pin 12345678 --pin 1234
//   SOFTHSM2_CONF=/tmp/hsm/softhsm2.conf \
//   cargo run --example sign_smoke -- /usr/lib64/pkcs11/libsofthsm2.so
//
// Set SMOKE_TOKEN_DIR=<tokendir> to also yank the token mid-flow.

use cryptoki::context::{CInitializeArgs, CInitializeFlags, Pkcs11};
use cryptoki::mechanism::Mechanism;
use cryptoki::object::{Attribute as A, KeyType, ObjectClass};
use cryptoki::session::UserType;
use cryptoki::types::AuthPin;

use autofirme_smartcard::pkcs11::Pkcs11Signer;
use autofirme_smartcard::SmartcardError;

fn main() {
    let module = std::env::args().nth(1).expect("pass module path");
    let pin: AuthPin = "1234".into();

    // 1. Provision a token key, the way a card already has one.
    let ctx = Pkcs11::new(&module).expect("load module");
    ctx.initialize(CInitializeArgs::new(CInitializeFlags::OS_LOCKING_OK))
        .expect("initialize");
    let slot = *ctx
        .get_slots_with_token()
        .expect("list slots")
        .first()
        .expect("one token slot");
    let session = ctx.open_rw_session(slot).expect("rw session");
    session.login(UserType::User, Some(&pin)).expect("login");

    let (_pub, _priv) = session
        .generate_key_pair(
            &Mechanism::RsaPkcsKeyPairGen,
            &[
                A::Class(ObjectClass::PUBLIC_KEY),
                A::KeyType(KeyType::RSA),
                A::ModulusBits(2048.into()),
                A::PublicExponent(vec![0x01, 0x00, 0x01]),
                A::Verify(true),
                A::Token(true),
            ],
            &[
                A::Class(ObjectClass::PRIVATE_KEY),
                A::KeyType(KeyType::RSA),
                A::Sign(true),
                A::Token(true),
                A::Private(true),
                A::Label(b"smoke-key".to_vec()),
            ],
        )
        .expect("generate key pair");

    // 2. Sign a 32-byte digest through the module under test.
    let signer = Pkcs11Signer::open(Some(std::path::Path::new(&module))).expect("open signer");
    let digest = [0xABu8; 32];
    let sig = signer
        .sign_digest(slot, Some("smoke-key"), &digest, &pin)
        .expect("sign digest");
    assert_eq!(sig.len(), 256, "2048-bit RSA signature is 256 bytes");

    // 3. Independent check, not the token's own verifier. `sign_digest` promises
    //    "digest in", so the signature must be PKCS#1 v1.5 over
    //    `DigestInfo(digest)` — raw `CKM_RSA_PKCS`, no second hashing.
    let mut info = vec![
        0x30, 0x31, 0x30, 0x0d, 0x06, 0x09, 0x60, 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02, 0x01,
        0x05, 0x00, 0x04, 0x20,
    ];
    info.extend_from_slice(&digest);
    session
        .verify(&Mechanism::RsaPkcs, _pub, &info, &sig)
        .expect("signature must be raw PKCS#1 v1.5 over DigestInfo(digest)");

    // 3b. Regression trap for the mechanism mistake this test exists to catch:
    //     hashing the digest *again* (`CKM_SHA256_RSA_PKCS`) is self-consistent,
    //     so the token's own verifier cannot tell the two apart. This can:
    //     verifying the bare digest with the hash-and-sign mechanism succeeds
    //     if and only if the digest was hashed a second time. It must fail.
    assert!(
        session
            .verify(&Mechanism::Sha256RsaPkcs, _pub, &digest, &sig)
            .is_err(),
        "digest was hashed twice: no CMS verifier would accept this signature"
    );

    // 5. Unknown key label → typed error, no panic.
    assert!(
        matches!(
            signer.sign_digest(slot, Some("nope"), &digest, &pin),
            Err(SmartcardError::UnsupportedCard(_))
        ),
        "missing key must be typed"
    );

    // 6. Unsupported digest length → typed error.
    assert!(
        matches!(
            signer.sign_digest(slot, Some("smoke-key"), &[0u8; 20], &pin),
            Err(SmartcardError::UnsupportedCard(_))
        ),
        "short digest must be typed"
    );

    // 7. Token pulled mid-flow: the next call must fail and hand out no bytes.
    if let Ok(dir) = std::env::var("SMOKE_TOKEN_DIR") {
        std::fs::remove_dir_all(&dir).expect("yank token");
        let after = signer.sign_digest(slot, Some("smoke-key"), &digest, &pin);
        println!("AFTER_REMOVAL={after:?}");
        assert!(after.is_err(), "yanked token must not return a signature");
    }

    println!(
        "SIGN_OK module={module} slot={slot:?} sig_len={}",
        sig.len()
    );
}
