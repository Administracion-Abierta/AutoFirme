#!/usr/bin/env bash
#
# Cross-checks every fixture in test/fixtures/ (positive and negative),
# using only openssl and python3 (stdlib).  Prints one "ok ..." line per check,
# prints "FAIL ..." and exits 1 on the first mismatch.
#
# Run: bash test/fixtures/verify-fixtures.sh
#
set -euo pipefail

cd "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FIXTURES="$PWD"

ok() { printf 'ok   %s\n' "$1"; }
fail() { printf 'FAIL %s\n' "$1"; exit 1; }

expect_ok() {
  local name="$1"
  shift
  if "$@" >/dev/null 2>&1; then ok "$name"; else fail "$name"; fi
}

# --- OCSP / CRL revocation fixtures (test/fixtures/revocation, #136) -------
# `openssl ocsp` exits 0 even on "Response Verify Failure", so grep the report
# instead of trusting the exit status.

expect_ok "revocation/resp.der verifies against revocation/ca.der" \
  sh -c 'openssl ocsp -respin revocation/resp.der -CAfile revocation/ca.der 2>&1 | grep -q "Response verify OK"'

expect_ok "revocation/good_ca.der verifies against revocation/ca.der" \
  sh -c 'openssl ocsp -respin revocation/good_ca.der -CAfile revocation/ca.der 2>&1 | grep -q "Response verify OK"'

expect_ok "revocation/forged_embed.der is NOT trusted (self-signed rogue responder)" \
  sh -c 'openssl ocsp -respin revocation/forged_embed.der -CAfile revocation/ca.der 2>&1 | grep -q "Response Verify Failure"'

expect_ok "revocation/resp.der reports the pinned serial as revoked" \
  sh -c 'openssl ocsp -respin revocation/resp.der -text -noverify 2>&1 | grep -q "Cert Status: revoked"'

expect_ok "revocation/good_ca.der reports the pinned serial as good" \
  sh -c 'openssl ocsp -respin revocation/good_ca.der -text -noverify 2>&1 | grep -q "Cert Status: good"'

expect_ok "revocation/crl.der verifies against revocation/ca.der" \
  openssl crl -in revocation/crl.der -inform der -CAfile revocation/ca.der -noout

expect_fail() {
  local name="$1"
  shift
  if "$@" >/dev/null 2>&1; then fail "$name (command unexpectedly succeeded)"; else ok "$name"; fi
}

# --- CAdES / CMS -----------------------------------------------------------

expect_ok "cades-valid.csig is accepted (attached content, chain to ca/test-root.crt)" \
  openssl cms -verify -inform DER -in cades-valid.csig -CAfile ca/test-root.crt -purpose any -out /dev/null

expect_fail "cades-tampered.csig is rejected" \
  openssl cms -verify -inform DER -in cades-tampered.csig -CAfile ca/test-root.crt -purpose any -out /dev/null

expect_ok "cades-detached.csig verifies against document.txt" \
  openssl cms -verify -inform DER -in cades-detached.csig -CAfile ca/test-root.crt -content document.txt -out /dev/null

expect_fail "cades-detached.csig is rejected against document-tampered.txt" \
  openssl cms -verify -inform DER -in cades-detached.csig -CAfile ca/test-root.crt -content document-tampered.txt -out /dev/null

# --- documents, XAdES and c14n oracles -------------------------------------

FIXTURES="$FIXTURES" python3 - <<'PY'
import base64
import binascii
import ssl
import hashlib
import os
import re
import subprocess
import sys
import tempfile
import xml.etree.ElementTree as ET

DS = "http://www.w3.org/2000/09/xmldsig#"
XADES = "http://uri.etsi.org/01903/v1.3.2#"

fx = os.environ["FIXTURES"]
ET.register_namespace("ds", DS)
ET.register_namespace("xades", XADES)


def fail(name):
    print("FAIL %s" % name)
    sys.exit(1)


def check(name, condition):
    if condition:
        print("ok   %s" % name)
    else:
        fail(name)


def read(path):
    with open(path, encoding="utf-8", newline="") as fh:
        return fh.read()


def readb(path):
    with open(path, "rb") as fh:
        return fh.read()


def canon(text):
    return ET.canonicalize(xml_data=text, strip_text=False, with_comments=False)


def canon_subtree(doc, tag, in_scope):
    element = ET.fromstring(doc).find(tag)
    assert element is not None, tag
    inner = ET.tostring(element, encoding="unicode")
    missing = "".join(' xmlns:%s="%s"' % (p, u) for p, u in in_scope.items()
                      if ("xmlns:%s=" % p) not in inner)
    inner = re.sub(r"^<[^>\s/]+", lambda m: m.group(0) + missing, inner, count=1)
    out = canon("<wrap>" + inner + "</wrap>")
    assert out.startswith("<wrap>") and out.endswith("</wrap>"), repr(out)
    return out[len("<wrap>"):-len("</wrap>")]


def ref1_digest(doc):
    """Reference 1: whole document, enveloped-signature transform applied."""
    root = ET.fromstring(doc)
    signature = root.find("{%s}Signature" % DS)
    assert signature is not None
    root.remove(signature)
    return hashlib.sha256(canon(ET.tostring(root, encoding="unicode")).encode("utf-8")).digest()


def ref2_digest(doc):
    """Reference 2: the SignedProperties subtree."""
    text = canon_subtree(doc, ".//{%s}SignedProperties" % XADES, {"ds": DS, "xades": XADES})
    return hashlib.sha256(text.encode("utf-8")).digest()


def b64(digest):
    return base64.b64encode(digest).decode("ascii")


# --- document.txt ----------------------------------------------------------
doc = readb(os.path.join(fx, "document.txt"))
tampered_doc = readb(os.path.join(fx, "document-tampered.txt"))
check("document.txt is ASCII, has the marker line and >= 4 lines",
      all(b < 128 for b in doc)
      and b"FACTURA AUTOFIRME WEB DE PRUEBA" in doc
      and doc.count(b"\n") >= 4)
check("document-tampered.txt differs from document.txt in exactly one ASCII digit",
      len(doc) == len(tampered_doc)
      and sum(1 for a, b in zip(doc, tampered_doc) if a != b) == 1
      and all(chr(b).isdigit() for a, b in zip(doc, tampered_doc) if a != b))

# --- fixture structure -----------------------------------------------------
PEM_BLOCK = rb"-----BEGIN CERTIFICATE-----.*?-----END CERTIFICATE-----"
PEM_TRIM = re.compile(rb"-----BEGIN CERTIFICATE-----|-----END CERTIFICATE-----|\s")

blocks = re.findall(PEM_BLOCK, readb(os.path.join(fx, "ca", "chain.pem")), re.S)
check("ca/chain.pem is signer.crt followed by test-intermediate.crt (PEM, signer first)",
      len(blocks) == 2
      and PEM_TRIM.sub(b"", blocks[0]) == PEM_TRIM.sub(b"", readb(os.path.join(fx, "ca", "signer.crt")))
      and PEM_TRIM.sub(b"", blocks[1]) == PEM_TRIM.sub(b"", readb(os.path.join(fx, "ca", "test-intermediate.crt"))))

marker = b"FACTURA AUTOFIRME WEB DE PRUEBA"
valid_cms = readb(os.path.join(fx, "cades-valid.csig"))
detached_cms = readb(os.path.join(fx, "cades-detached.csig"))
check("cades-valid.csig embeds the document content, cades-detached.csig does not",
      marker in valid_cms and marker not in detached_cms)
check("cades-valid.csig carries contentType + messageDigest + signingTime signed attributes",
      all(bytes.fromhex(oid) in valid_cms for oid in
          ("06092a864886f70d010903", "06092a864886f70d010904", "06092a864886f70d010905")))

# --- XAdES -----------------------------------------------------------------
valid = read(os.path.join(fx, "xades-valid.xsig"))
tampered = read(os.path.join(fx, "xades-tampered.xsig"))
digests = [e.text for e in ET.fromstring(valid).iter("{%s}DigestValue" % DS)]

check("xades-valid.xsig Reference 1 digest (document + enveloped-signature + c14n) matches",
      b64(ref1_digest(valid)) == digests[0])
check("xades-valid.xsig Reference 2 digest (SignedProperties + c14n) matches",
      b64(ref2_digest(valid)) == digests[1])

valid_root = ET.fromstring(valid)
def cert_elements(root, name):
    pem = readb(os.path.join(fx, "ca", name)).decode()
    return base64.b64encode(ssl.PEM_cert_to_DER_cert(pem)).decode("ascii")

keyinfo = [e.text for e in valid_root.findall(".//{%s}X509Certificate" % DS)]
check("xades-valid.xsig KeyInfo carries base64(DER) signer + intermediate, signer first",
      keyinfo == [cert_elements(valid_root, "signer.crt"),
                  cert_elements(valid_root, "test-intermediate.crt")])

algorithms = [e.get("Algorithm") for e in valid_root.iter()]
references = valid_root.findall(".//{%s}Reference" % DS)
check("xades-valid.xsig has 2 references (document + #SignedProperties), c14n + rsa-sha256 algorithms",
      [r.get("URI") for r in references] == ["", "#SignedProperties"]
      and references[1].get("Type") == "http://uri.etsi.org/01903#SignedProperties"
      and "http://www.w3.org/TR/2001/REC-xml-c14n-20010315" in algorithms
      and "http://www.w3.org/2001/10/xml-exc-c14n#" in algorithms
      and "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256" in algorithms
      and "http://www.w3.org/2000/09/xmldsig#enveloped-signature" in algorithms
      and "http://www.w3.org/2001/04/xmlenc#sha256" in algorithms
      and valid_root.find(".//{%s}QualifyingProperties" % XADES).get("Target") == "#Signature"
      and valid_root.find(".//{%s}SignedProperties" % XADES).get("Id") == "SignedProperties"
      and valid_root.find(".//{%s}SigningTime" % XADES).text == "2026-01-15T10:30:00Z")

with tempfile.TemporaryDirectory() as tmp:
    pubkey = os.path.join(tmp, "signer.pub")
    sigfile = os.path.join(tmp, "signature.bin")
    datafile = os.path.join(tmp, "signed-info.c14n")
    with open(pubkey, "wb") as fh:
        subprocess.run(["openssl", "x509", "-in", os.path.join(fx, "ca", "signer.crt"),
                        "-pubkey", "-noout"], check=True, stdout=fh)
    signature_value = ET.fromstring(valid).find(".//{%s}SignatureValue" % DS).text
    with open(sigfile, "wb") as fh:
        fh.write(base64.b64decode(signature_value))
    with open(datafile, "wb") as fh:
        fh.write(canon_subtree(valid, ".//{%s}SignedInfo" % DS, {"ds": DS}).encode("utf-8"))
    verified = subprocess.run(
        ["openssl", "dgst", "-sha256", "-verify", pubkey, "-signature", sigfile, datafile],
        stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL).returncode == 0
check("xades-valid.xsig SignatureValue verifies over c14n(SignedInfo) with ca/signer.crt",
      verified)

check("xades-tampered.xsig Reference 1 digest no longer matches",
      b64(ref1_digest(tampered)) != digests[0])
check("xades-tampered.xsig Reference 2 digest still matches (SignedProperties untouched)",
      b64(ref2_digest(tampered)) == digests[1])


def between(text, start, end):
    return text[text.index(start):text.index(end) + len(end)]


check("xades-tampered.xsig differs from xades-valid.xsig in exactly one byte "
      "and SignedInfo/SignatureValue are untouched",
      len(valid) == len(tampered)
      and sum(1 for a, b in zip(valid, tampered) if a != b) == 1
      and between(valid, "<ds:SignedInfo>", "</ds:SignedInfo>")
      == between(tampered, "<ds:SignedInfo>", "</ds:SignedInfo>")
      and signature_value == ET.fromstring(tampered).find(".//{%s}SignatureValue" % DS).text)

# --- PAdES golden (app-produced, see README) -------------------------------
def pades_parts(pdf):
    byte_range = re.search(rb"/ByteRange\s*\[([^\]]*)\]", pdf)
    contents = re.search(rb"/Contents\s*<", pdf)
    if byte_range is None or contents is None:
        raise ValueError("no /ByteRange or /Contents")
    nums = [int(n) for n in byte_range.group(1).split()]
    start = contents.end()
    end = pdf.index(b">", start)
    hexs = pdf[start:end]
    covered = pdf[nums[0]:nums[0] + nums[1]] + pdf[nums[2]:nums[2] + nums[3]]
    return nums, hexs, binascii.unhexlify(hexs), covered

def pades_verify(pdf):
    nums, _, der, covered = pades_parts(pdf)
    with tempfile.TemporaryDirectory() as tmp:
        sigfile = os.path.join(tmp, "signature.der")
        datafile = os.path.join(tmp, "byte-range.bin")
        with open(sigfile, "wb") as fh:
            fh.write(der)
        with open(datafile, "wb") as fh:
            fh.write(covered)
        return subprocess.run(
            ["openssl", "cms", "-verify", "-inform", "DER", "-binary", "-in", sigfile,
             "-content", datafile, "-CAfile", os.path.join(fx, "pades-signer.crt"),
             "-purpose", "any", "-out", os.devnull],
            stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL).returncode == 0

pades = readb(os.path.join(fx, "pades-b-b-signed.pdf"))
pades_source = readb(os.path.join(fx, "pades-source.pdf"))
nums, hexs, pades_der, pades_covered = pades_parts(pades)
check("pades-b-b-signed.pdf /ByteRange leaves out only the /Contents hex string",
      nums[0] == 0
      and nums[2] == nums[1] + len(hexs) + 2
      and nums[2] + nums[3] == len(pades)
      and len(hexs) % 2 == 0)
check("pades-b-b-signed.pdf keeps pades-source.pdf as a byte-identical prefix inside /ByteRange",
      pades.startswith(pades_source) and pades_source in pades_covered)
check("pades-b-b-signed.pdf CMS verifies over /ByteRange with pades-signer.crt",
      pades_verify(pades))
patched = bytearray(pades)
patched[pades_covered.index(pades_source) + len(pades_source) // 2] ^= 0x01
check("pades-b-b-signed.pdf CMS rejects a patched byte inside /ByteRange",
      not pades_verify(bytes(patched)))

# --- c14n oracles ----------------------------------------------------------
names = sorted(n[:-4] for n in os.listdir(os.path.join(fx, "c14n")) if n.endswith(".xml"))
for name in names:
    source = read(os.path.join(fx, "c14n", name + ".xml"))
    expected = read(os.path.join(fx, "c14n", name + ".c14n"))
    check("c14n/%s.c14n is canonicalize(c14n/%s.xml)" % (name, name), canon(source) == expected)
check("c14n oracle pairs present (01..07)", names == ["%02d" % i for i in range(1, 8)])
PY

printf 'all fixture checks passed\n'
