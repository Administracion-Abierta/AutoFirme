#!/usr/bin/env bash
#
# Regenerates the OCSP/CRL fixtures for `src/lib/verify/revocation.test.ts` (#136).
#
#   ca.der            test CA certificate (the issuer)
#   leaf.der          leaf certificate signed by the CA, serial pinned below
#   resp.der          CA-signed OCSP response: leaf is *revoked*
#   good_ca.der       CA-signed OCSP response: leaf is *good*
#   forged_embed.der  rogue self-signed responder claiming *good* for the revoked serial,
#                     embedding its own cert — a forgery the parser MUST reject
#   crl.der           CA-signed CRL listing the leaf as revoked
#
# Requires: bash, openssl. No network. Idempotent.
# Keys land in keys/ (gitignored); only public DER artifacts are committed.
#
set -euo pipefail
cd "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

command -v openssl >/dev/null || { echo 'openssl required' >&2; exit 1; }

# Any openssl >= 1.1 handles everything here; version pinning would only fight the generator.
SERIAL=0x39DED1487D2ECC4366F66475FB53A6777FD788A5
NOT_BEFORE=20260101000000Z
NOT_AFTER=20360101000000Z

rm -rf keys db index.txt index_good.txt
mkdir -p keys db
touch index.txt

# --- CA -------------------------------------------------------------------
openssl req -x509 -newkey rsa:2048 -nodes -sha256 -days 3650 \
	-keyout keys/ca.key -out keys/ca.pem -subj '/CN=AutoFirme Revocation Review CA' 2>/dev/null
openssl x509 -in keys/ca.pem -out ca.der -outform der

# --- leaf -----------------------------------------------------------------
printf 'basicConstraints=CA:FALSE\nkeyUsage=critical,digitalSignature\nauthorityInfoAccess=OCSP;URI:http://ocsp.rev136.test/\ncrlDistributionPoints=URI:http://crl.rev136.test/revoked.crl\n' > keys/leaf.ext
openssl req -newkey rsa:2048 -nodes -sha256 -keyout keys/leaf.key -out keys/leaf.csr \
	-subj '/CN=AutoFirme Revocation Review Leaf' 2>/dev/null
openssl x509 -req -in keys/leaf.csr -CA keys/ca.pem -CAkey keys/ca.key -CAcreateserial \
	-set_serial "$SERIAL" -days 3650 -sha256 -extfile keys/leaf.ext -out keys/leaf.pem 2>/dev/null
openssl x509 -in keys/leaf.pem -out leaf.der -outform der

# --- CA-signed OCSP responses --------------------------------------------
openssl ocsp -issuer keys/ca.pem -cert keys/leaf.pem -no_nonce -reqout keys/req.der 2>/dev/null
# openssl index.txt dates are YYMMDDHHMMSSZ (two-digit year), unlike the cert validity above.
INDEX_EXPIRY=360101000000Z
INDEX_REVOKED=260101000000Z
printf 'V\t%s\t\t%s\tunknown\t/CN=AutoFirme Revocation Review Leaf\n' "$INDEX_EXPIRY" "${SERIAL#0x}" > index_good.txt
# Revoked entry: R <expiry> <revocationDate> <serial> ... — the revocation date must be present.
printf 'R\t%s\t%s\t%s\tunknown\t/CN=AutoFirme Revocation Review Leaf\n' "$INDEX_EXPIRY" "$INDEX_REVOKED" "${SERIAL#0x}" > index.txt
openssl ocsp -index index.txt -CA keys/ca.pem -rsigner keys/ca.pem -rkey keys/ca.key \
	-reqin keys/req.der -respout resp.der 2>/dev/null
openssl ocsp -index index_good.txt -CA keys/ca.pem -rsigner keys/ca.pem -rkey keys/ca.key \
	-reqin keys/req.der -respout good_ca.der 2>/dev/null
# Self-signed, not issued by the CA, no OCSPSigning EKU: its word means nothing.
openssl req -x509 -newkey rsa:2048 -nodes -sha256 -days 3650 -addext 'extendedKeyUsage=OCSPSigning' \
	-keyout keys/rogue.key -out keys/rogue.pem -subj '/CN=Rogue Revocation Responder' 2>/dev/null
openssl ocsp -index index_good.txt -CA keys/ca.pem -rsigner keys/rogue.pem -rkey keys/rogue.key \
	-reqin keys/req.der -respout forged_embed.der -rother keys/rogue.pem 2>/dev/null

# --- CRL ------------------------------------------------------------------
cat > keys/ca.cnf <<EOF
[ca]
default_ca = CA_default
[CA_default]
database = $PWD/index.txt
crlnumber = $PWD/keys/crlnumber
default_md = sha256
default_crl_days = 30
EOF
printf '01\n' > keys/crlnumber
openssl ca -config keys/ca.cnf -gencrl -cert keys/ca.pem -keyfile keys/ca.key \
	-out crl.pem -batch 2>/dev/null
openssl crl -in crl.pem -outform der -out crl.der

echo 'revocation fixtures regenerated'
ls -1 ca.der leaf.der resp.der good_ca.der forged_embed.der crl.der
