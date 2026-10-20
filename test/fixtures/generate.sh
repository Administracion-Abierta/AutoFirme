#!/usr/bin/env bash
#
# Regenerates the generated fixtures in test/fixtures/ (the app-produced `pades-*` golden
# is not generated here — see README.md):
#
#   ca/            throwaway test PKI (root -> intermediate -> signer)
#   document.txt   invoice text, + document-tampered.txt
#   cades-*.csig   CAdES/CMS (CMS SignedData) signatures, DER, attached + detached
#   xades-*.xsig   XAdES-EPES enveloped XML signature over a small invoice
#   c14n/NN.*      canonicalization oracle pairs (input + expected c14n output)
#
# Requires: bash, openssl, python3 (stdlib only).  No xmlsec1, no network.
# Idempotent: re-running overwrites every artifact in place.
#
set -euo pipefail

# The pinned certificate window uses -not_before/-not_after, which OpenSSL only learned in 3.4.
# Fail before the rm -rf below: an older tool makes the script fall back to "now" or abort mid-wipe.
openssl_version=$(openssl version | awk '{print $2}')
openssl_major=${openssl_version%%.*}
openssl_rest=${openssl_version#*.}
openssl_minor=${openssl_rest%%.*}
if (( openssl_major < 3 || (openssl_major == 3 && openssl_minor < 4) )); then
  printf 'generate.sh needs OpenSSL >= 3.4 for -not_before/-not_after (found %s)\n' "$openssl_version" >&2
  exit 1
fi

cd "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FIXTURES="$PWD"

# Certificate window is pinned (instead of "notBefore = now") so that the fixed
# XAdES SigningTime below (2026-01-15) always falls inside the certificates'
# validity, and so regenerated certificates keep the same validity dates.
# The span is 10 years, i.e. the same lifetime as `-days 3650`.
NOT_BEFORE=20230101000000Z
NOT_AFTER=20330101000000Z
VALIDITY=(-not_before "$NOT_BEFORE" -not_after "$NOT_AFTER")

rm -rf "$FIXTURES/ca" "$FIXTURES/scratch" "$FIXTURES/c14n"
mkdir -p "$FIXTURES/ca" "$FIXTURES/scratch" "$FIXTURES/c14n"

# ---------------------------------------------------------------------------
# 1. Throwaway test PKI (RSA-2048 / SHA-256)
# ---------------------------------------------------------------------------
printf 'generating test PKI\n'

openssl req -x509 -newkey rsa:2048 -sha256 -nodes \
  -keyout ca/test-root.key -out ca/test-root.crt -set_serial 0x1000 "${VALIDITY[@]}" \
  -subj "/CN=AutoFirma Web Test Root CA/O=AutoFirma Web/C=ES" \
  -addext "basicConstraints=critical,CA:TRUE" \
  -addext "keyUsage=critical,keyCertSign,cRLSign"

openssl req -new -newkey rsa:2048 -sha256 -nodes \
  -keyout ca/test-intermediate.key -out scratch/test-intermediate.csr \
  -subj "/CN=AutoFirma Web Test Intermediate CA/O=AutoFirma Web/C=ES"

cat > scratch/test-intermediate.ext <<'EOF'
basicConstraints=critical,CA:TRUE,pathlen:0
keyUsage=critical,keyCertSign,cRLSign
EOF

openssl x509 -req -in scratch/test-intermediate.csr \
  -CA ca/test-root.crt -CAkey ca/test-root.key -set_serial 0x2000 "${VALIDITY[@]}" \
  -sha256 -out ca/test-intermediate.crt -extfile scratch/test-intermediate.ext

openssl req -new -newkey rsa:2048 -sha256 -nodes \
  -keyout ca/signer.key -out scratch/signer.csr \
  -subj "/CN=AutoFirma Web Test Signer/O=AutoFirma Web/C=ES"

cat > scratch/signer.ext <<'EOF'
basicConstraints=critical,CA:FALSE
keyUsage=critical,digitalSignature
extendedKeyUsage=emailProtection
EOF

openssl x509 -req -in scratch/signer.csr \
  -CA ca/test-intermediate.crt -CAkey ca/test-intermediate.key -set_serial 0x3000 "${VALIDITY[@]}" \
  -sha256 -out ca/signer.crt -extfile scratch/signer.ext

# signer first, then its issuing intermediate (what a verifier needs to build
# the chain up to ca/test-root.crt).
cat ca/signer.crt ca/test-intermediate.crt > ca/chain.pem

# ---------------------------------------------------------------------------
# 2. Invoice text + one-character tampered variant
# ---------------------------------------------------------------------------
printf 'writing document.txt / document-tampered.txt\n'

FIXTURES="$FIXTURES" python3 - <<'PY'
import os

fx = os.environ["FIXTURES"]
LINES = [
    "FACTURA AUTOFIRME WEB DE PRUEBA",
    "Numero: FA-2026-0042",
    "Fecha: 2026-01-15",
    "Importe: 1234.56 EUR",
    "Emisor: AutoFirma Web, S.A.",
]
# CRLF line endings are deliberate: `openssl cms -verify -content document.txt`
# canonicalizes text to CRLF, and signing used `-binary` (raw bytes), so CRLF is
# the only line ending for which both see exactly the same bytes.
doc = "\r\n".join(LINES) + "\r\n"
assert all(ord(ch) < 128 for ch in doc)
assert len(doc) >= 4 * len(LINES)

with open(os.path.join(fx, "document.txt"), "w", encoding="ascii", newline="") as fh:
    fh.write(doc)

tampered = doc.replace("Importe: 1234.56", "Importe: 9234.56")
assert tampered != doc and len(tampered) == len(doc)
with open(os.path.join(fx, "document-tampered.txt"), "w", encoding="ascii", newline="") as fh:
    fh.write(tampered)

print("  document.txt: %d bytes / %d lines" % (len(doc), len(LINES)))
PY

# ---------------------------------------------------------------------------
# 3. CAdES (CMS SignedData, DER): attached + detached
# ---------------------------------------------------------------------------
printf 'signing cades-valid.csig / cades-detached.csig\n'

openssl cms -sign -nodetach -binary -md sha256 \
  -inkey ca/signer.key -signer ca/signer.crt -certfile ca/chain.pem \
  -in document.txt -outform DER -out cades-valid.csig

openssl cms -sign -binary -md sha256 \
  -inkey ca/signer.key -signer ca/signer.crt -certfile ca/chain.pem \
  -in document.txt -outform DER -out cades-detached.csig

# cades-tampered.csig: cades-valid.csig with exactly one ASCII digit of the
# embedded document content patched in place (same length, DER kept intact).
FIXTURES="$FIXTURES" python3 - <<'PY'
import os

fx = os.environ["FIXTURES"]
data = open(os.path.join(fx, "cades-valid.csig"), "rb").read()

marker = b"FACTURA AUTOFIRME WEB DE PRUEBA"
assert data.count(marker) == 1, "marker not found exactly once in the DER"

i = data.index(marker) + len(marker)
while not 0x30 <= data[i] <= 0x39:  # first ASCII digit after the marker line
    i += 1
assert data[i] != ord("9")
out = data[:i] + b"9" + data[i + 1:]
assert len(out) == len(data) and out != data

with open(os.path.join(fx, "cades-tampered.csig"), "wb") as fh:
    fh.write(out)
print("  cades-tampered.csig: patched byte %d (%s)" % (i, chr(data[i])))
PY

# ---------------------------------------------------------------------------
# 4. XAdES-EPES enveloped signature (+ one-character tampered variant)
# ---------------------------------------------------------------------------
printf 'signing xades-valid.xsig / xades-tampered.xsig\n'

FIXTURES="$FIXTURES" python3 - <<'PY'
import base64
import hashlib
import os
import re
import ssl
import subprocess
import sys
import xml.etree.ElementTree as ET

DS = "http://www.w3.org/2000/09/xmldsig#"
XADES = "http://uri.etsi.org/01903/v1.3.2#"
C14N = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315"
EXC_C14N = "http://www.w3.org/2001/10/xml-exc-c14n#"
SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256"
RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"
ENVELOPED = "http://www.w3.org/2000/09/xmldsig#enveloped-signature"
SIGNED_PROPS_TYPE = "http://uri.etsi.org/01903#SignedProperties"
SIGNING_TIME = "2026-01-15T10:30:00Z"

fx = os.environ["FIXTURES"]
ET.register_namespace("ds", DS)
ET.register_namespace("xades", XADES)


def canon(text):
    return ET.canonicalize(xml_data=text, strip_text=False, with_comments=False)


def canon_subtree(doc, tag, in_scope):
    """Inclusive C14N of the `tag` subtree, canonicalized as its own apex.

    The subtree is placed in a namespace-free wrapper document so that the
    in-scope namespace declarations are rendered on the subtree's own element;
    the wrapper's start/end tags are then stripped.
    """
    element = ET.fromstring(doc).find(tag)
    assert element is not None, tag
    inner = ET.tostring(element, encoding="unicode")
    missing = "".join(' xmlns:%s="%s"' % (p, u) for p, u in in_scope.items()
                      if ("xmlns:%s=" % p) not in inner)
    inner = re.sub(r"^<[^>\s/]+", lambda m: m.group(0) + missing, inner, count=1)
    out = canon("<wrap>" + inner + "</wrap>")
    assert out.startswith("<wrap>") and out.endswith("</wrap>"), repr(out)
    return out[len("<wrap>"):-len("</wrap>")]


def build(digest1, digest2, signature, cert_b64, chain_b64):
    return (
        '<Factura Id="f1">'
        '<Numero>FA-2026-0042</Numero>'
        '<Fecha>2026-01-15</Fecha>'
        '<Importe>1234.56</Importe>'
        '<Emisor>AutoFirme Web, S.A.</Emisor>'
        '<ds:Signature xmlns:ds="%s" Id="Signature">'
        '<ds:SignedInfo>'
        '<ds:CanonicalizationMethod Algorithm="%s"/>'
        '<ds:SignatureMethod Algorithm="%s"/>'
        '<ds:Reference URI="">'
        '<ds:Transforms>'
        '<ds:Transform Algorithm="%s"/>'
        '<ds:Transform Algorithm="%s"/>'
        '</ds:Transforms>'
        '<ds:DigestMethod Algorithm="%s"/>'
        '<ds:DigestValue>%s</ds:DigestValue>'
        '</ds:Reference>'
        '<ds:Reference URI="#SignedProperties" Type="%s">'
        '<ds:Transforms><ds:Transform Algorithm="%s"/></ds:Transforms>'
        '<ds:DigestMethod Algorithm="%s"/>'
        '<ds:DigestValue>%s</ds:DigestValue>'
        '</ds:Reference>'
        '</ds:SignedInfo>'
        '<ds:SignatureValue>%s</ds:SignatureValue>'
        '<ds:KeyInfo><ds:X509Data>%s</ds:X509Data></ds:KeyInfo>'
        '<ds:Object>'
        '<xades:QualifyingProperties xmlns:xades="%s" Target="#Signature">'
        '<xades:SignedProperties Id="SignedProperties">'
        '<xades:SignedSignatureProperties>'
        '<xades:SigningTime>%s</xades:SigningTime>'
        '</xades:SignedSignatureProperties>'
        '</xades:SignedProperties>'
        '</xades:QualifyingProperties>'
        '</ds:Object>'
        '</ds:Signature>'
        '</Factura>'
    ) % (DS, C14N, RSA_SHA256, ENVELOPED, C14N, SHA256, digest1,
         SIGNED_PROPS_TYPE, EXC_C14N, SHA256, digest2, signature,
         cert_b64 + chain_b64, XADES, SIGNING_TIME)


def ref1_digest(doc):
    """Reference 1: whole document with the ds:Signature element removed."""
    root = ET.fromstring(doc)
    signature = root.find("{%s}Signature" % DS)
    assert signature is not None
    root.remove(signature)
    return hashlib.sha256(canon(ET.tostring(root, encoding="unicode")).encode("utf-8")).digest()


def ref2_digest(doc):
    """Reference 2: the SignedProperties subtree."""
    text = canon_subtree(doc, ".//{%s}SignedProperties" % XADES, {"ds": DS, "xades": XADES})
    return hashlib.sha256(text.encode("utf-8")).digest()


def signed_info_c14n(doc):
    return canon_subtree(doc, ".//{%s}SignedInfo" % DS, {"ds": DS})


# X509Certificate carries base64 of the DER cert (XMLDSig §4.4.4), never the PEM armour;
# KeyInfo carries the whole chain (signer first), as Spanish XAdES producers do.
def cert_element_b64(name):
    pem = open(os.path.join(fx, "ca", name), "r").read()
    der = base64.b64encode(ssl.PEM_cert_to_DER_cert(pem)).decode("ascii")
    return "<ds:X509Certificate>%s</ds:X509Certificate>" % der

cert_b64 = cert_element_b64("signer.crt")
chain_b64 = cert_element_b64("test-intermediate.crt")
placeholder1 = base64.b64encode(b"\x01" * 32).decode("ascii")
placeholder2 = base64.b64encode(b"\x02" * 32).decode("ascii")
placeholder_sig = base64.b64encode(b"\x03" * 256).decode("ascii")

# Digest values do not feed into either reference, so they can be computed from a
# document that still carries the placeholders.
draft = build(placeholder1, placeholder2, placeholder_sig, cert_b64, chain_b64)
digest1 = ref1_digest(draft)
digest2 = ref2_digest(draft)
b64_1 = base64.b64encode(digest1).decode("ascii")
b64_2 = base64.b64encode(digest2).decode("ascii")

# c14n(SignedInfo) has to be taken from the document that already carries the
# final DigestValues.
with_digests = build(b64_1, b64_2, placeholder_sig, cert_b64, chain_b64)
to_sign = signed_info_c14n(with_digests)
der_signature = subprocess.run(
    ["openssl", "dgst", "-sha256", "-sign", os.path.join(fx, "ca", "signer.key")],
    input=to_sign.encode("utf-8"), capture_output=True, check=True).stdout
signature_b64 = base64.b64encode(der_signature).decode("ascii")
assert len(signature_b64) == 344, len(signature_b64)

final = build(b64_1, b64_2, signature_b64, cert_b64, chain_b64)
valid_path = os.path.join(fx, "xades-valid.xsig")
with open(valid_path, "w", encoding="ascii", newline="") as fh:
    fh.write(final)

# --- assertions: re-read the finished file and re-derive everything ---------
on_disk = open(valid_path, encoding="ascii", newline="").read()
assert on_disk == final
root = ET.fromstring(on_disk)
written = [e.text for e in root.iter("{%s}DigestValue" % DS)]
assert written == [b64_1, b64_2], written
assert ref1_digest(on_disk) == digest1, "reference 1 digest does not reproduce"
assert ref2_digest(on_disk) == digest2, "reference 2 digest does not reproduce"
assert signed_info_c14n(on_disk) == to_sign, "c14n(SignedInfo) does not reproduce"

pubkey = os.path.join(fx, "scratch", "signer.pub")
subprocess.run(["openssl", "x509", "-in", os.path.join(fx, "ca", "signer.crt"),
                "-pubkey", "-noout"], stdout=open(pubkey, "wb"), check=True)
sigfile = os.path.join(fx, "scratch", "xades-si.bin")
sifile = os.path.join(fx, "scratch", "xades-si.c14n")
open(sigfile, "wb").write(base64.b64decode(root.find(".//{%s}SignatureValue" % DS).text))
open(sifile, "wb").write(to_sign.encode("utf-8"))
subprocess.run(["openssl", "dgst", "-sha256", "-verify", pubkey,
                "-signature", sigfile, sifile], check=True,
               stdout=subprocess.DEVNULL)

# --- tampered copy: one character of the <Importe> text node ---------------
tampered = final.replace("<Importe>1234.56</Importe>", "<Importe>9234.56</Importe>")
assert tampered != final and len(tampered) == len(final)
assert tampered.count("<Importe>9234.56</Importe>") == 1
with open(os.path.join(fx, "xades-tampered.xsig"), "w", encoding="ascii", newline="") as fh:
    fh.write(tampered)

print("  xades-valid.xsig: %d bytes, SignatureValue verifies against ca/signer.crt" % len(final))
PY

# ---------------------------------------------------------------------------
# 5. Canonicalization oracle pairs (python ElementTree, same options as above)
# ---------------------------------------------------------------------------
printf 'writing c14n/NN.xml + c14n/NN.c14n oracle pairs\n'

FIXTURES="$FIXTURES" python3 - <<'PY'
import os
import xml.etree.ElementTree as ET

fx = os.environ["FIXTURES"]
out_dir = os.path.join(fx, "c14n")

PAIRS = [
    # 01 attribute ordering, including a namespaced attribute
    ("01", '<doc b="2" xmlns:ns="urn:af:ns" a="1" ns:c="3" c="4">'
           '<child z="9" y="8"/>tail</doc>'),
    # 02 default namespace + prefixed namespace + one declared-but-unused prefix
    ("02", '<doc xmlns="urn:af:default" xmlns:unused="urn:af:unused" xmlns:p="urn:af:p">'
           '<p:child a="1"><inner/></p:child></doc>'),
    # 03 text escaping: &, <, >, tab, LF, CR
    ("03", '<doc>amp &amp; lt &lt; gt &gt; tab&#x9;end lf&#xA;end cr&#xD;end</doc>'),
    # 04 attribute-value escaping: &, <, ", tab, LF, CR (and > left alone)
    ("04", '<doc a="amp &amp; lt &lt; quot &quot; gt &gt; tab&#x9; lf&#xA; cr&#xD;"/>'),
    # 05 comments are dropped from the canonical form
    ("05", '<!-- lead --><doc><!-- inner -->x<!-- tail --></doc><!-- trail -->'),
    # 06 repeated namespace declarations + empty elements
    ("06", '<doc xmlns:p="urn:af:p"><p:a xmlns:p="urn:af:p"><p:b/><p:c>t</p:c></p:a>'
           '<p:empty/></doc>'),
    # 07 namespaces declared on an inner element (used there by its own tag and by
    # one of its attributes) and inherited by deeper children: the oracle shows
    # each declaration rendered once, at the element that declares it, with a
    # redundant re-declaration deeper down dropped.
    ("07", '<doc><q:outer xmlns:q="urn:af:q" xmlns:r="urn:af:r" r:a="1">'
           '<q:middle xmlns:q="urn:af:q"><r:leaf>v</r:leaf></q:middle>'
           '</q:outer></doc>'),
]

for name, source in PAIRS:
    canonical = ET.canonicalize(xml_data=source, strip_text=False, with_comments=False)
    with open(os.path.join(out_dir, name + ".xml"), "w", encoding="utf-8", newline="") as fh:
        fh.write(source)
    with open(os.path.join(out_dir, name + ".c14n"), "w", encoding="utf-8", newline="") as fh:
        fh.write(canonical)
    print("  %s.xml -> %s.c14n (%d bytes)" % (name, name, len(canonical)))
PY

printf 'done\n'
