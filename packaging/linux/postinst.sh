#!/bin/sh
# postinst for the AutoFirme .deb/.rpm: warn when the smartcard service is
# missing instead of failing install. Idempotent: only prints, never configures.
# Keep this file free of bashisms: maintainer scripts run under /bin/sh.
set -u

if ! command -v pcscd >/dev/null 2>&1; then
	cat >&2 <<'EOF'
AutoFirme: pcscd not found — smartcard (DNIe) signing is unavailable.
Install it with one of:
  Debian/Ubuntu: sudo apt install pcscd
  Fedora/RHEL:   sudo dnf install pcsc-lite
then: sudo systemctl enable --now pcscd.socket
Software-certificate (PKCS#12) signing works without it.
EOF
fi
exit 0
