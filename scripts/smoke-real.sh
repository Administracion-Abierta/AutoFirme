#!/usr/bin/env bash
# Real-certificate smoke test (#40).
#
# Signs a document with the legacy FNMT keystore in work/certs/old-cert.p12 — whose certificate
# expired 2026-03-29 — and proves the signature comes out valid while the expiry is warned about:
# openssl re-verifies it and our own verifier reports `valid-but-expired`.
#
# The container and its PIN file are gitignored and absent from a fresh clone, so a missing input
# is a skip with exit 0, not a failure. The PIN is read from .p12pass into the environment by the
# test and is never echoed here.
#
#   bash scripts/smoke-real.sh
#   AUTOFIRME_CERT=work/certs/current-cert.p12 bash scripts/smoke-real.sh
set -euo pipefail

cd "$(dirname "$0")/.."

cert=${AUTOFIRME_CERT:-work/certs/old-cert.p12}
pass=${AUTOFIRME_PASS:-.p12pass}

if [[ ! -f $cert ]]; then
	echo "SKIP: $cert is not present (gitignored input: nothing to smoke-test)"
	exit 0
fi
if [[ ! -f $pass ]]; then
	echo "SKIP: $pass is not present (that is where the container's PIN lives)"
	exit 0
fi

echo "smoke: $cert, PIN read from $pass (never printed)"
AUTOFIRME_CERT=$cert AUTOFIRME_PASS=$pass pnpm exec vitest run src/lib/real-cert.test.ts --reporter=verbose
echo "smoke: exit 0 — expiry warned, signature produced and verified"
