#!/usr/bin/env python3
"""Package the verified local MV3 build; no signing keys or test profiles included."""
import hashlib
import json
from pathlib import Path
import subprocess
import zipfile

root = Path(__file__).resolve().parent.parent
output = root / 'dist' / 'extension'
manifest = json.loads((output / 'manifest.json').read_text())
subprocess.run(['node', 'scripts/check-extension.mjs'], cwd=root, check=True)
package = json.loads((root / 'package.json').read_text())
notices = [
    'AutoFirme Chrome extension — local development build\n',
    'Upstream: https://github.com/Administracion-Abierta/AutoFirme\n',
    'Upstream revision: d6dfa434d3be2aa2ed53b3956f7d10c3ab8c1035\n',
    'No declared license was found at this upstream revision. Clarify distribution rights before publication.\n',
    'Dependency notices follow, including development-listed packages used by the bundled UI.\n',
]
for name in sorted(set(package.get('dependencies', {})) | set(package.get('devDependencies', {}))):
    directory = root / 'node_modules' / name
    if not (directory / 'package.json').is_file():
        continue
    details = json.loads((directory / 'package.json').read_text())
    notices.append(f"\n## {name} {details.get('version', '')}\nDeclared license: {details.get('license', 'undeclared')}\n")
    for license_path in sorted(directory.iterdir()):
        if license_path.is_file() and license_path.name.lower().startswith(('license', 'licence', 'copying', 'ofl')):
            notices.append(license_path.read_text(errors='replace') + '\n')
(output / 'THIRD_PARTY_NOTICES.txt').write_text(''.join(notices))
(output / 'SOURCE_REVISION.json').write_text(json.dumps({
    'upstream': 'https://github.com/Administracion-Abierta/AutoFirme',
    'upstreamRevision': 'd6dfa434d3be2aa2ed53b3956f7d10c3ab8c1035',
    'localRevision': subprocess.check_output(['git', 'rev-parse', 'HEAD'], cwd=root, text=True).strip(),
    'extensionVersion': manifest['version'],
}, indent=2) + '\n')
archive = root / 'dist' / f"autofirme-extension-{manifest['version']}.zip"
with zipfile.ZipFile(archive, 'w', zipfile.ZIP_DEFLATED) as bundle:
    for path in sorted(output.rglob('*')):
        if path.is_file():
            entry = zipfile.ZipInfo(path.relative_to(output).as_posix(), (1980, 1, 1, 0, 0, 0))
            entry.compress_type = zipfile.ZIP_DEFLATED
            entry.external_attr = 0o100644 << 16
            bundle.writestr(entry, path.read_bytes())
with zipfile.ZipFile(archive) as bundle:
    assert bundle.testzip() is None
    assert {'index.html', 'manifest.json', 'background.js', 'THIRD_PARTY_NOTICES.txt', 'SOURCE_REVISION.json'} <= set(bundle.namelist())
digest = hashlib.sha256(archive.read_bytes()).hexdigest()
(root / 'dist' / 'SHA256SUMS').write_text(f'{digest}  {archive.name}\n')
print(f'{archive.relative_to(root)} ({archive.stat().st_size} bytes)\nSHA-256 {digest}')
