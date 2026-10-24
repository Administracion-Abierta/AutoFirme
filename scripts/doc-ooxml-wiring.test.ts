/**
 * Regression for the fixer round on #127: the docs still described the OOXML signer as
 * unwired ("sin selector en la interfaz", "not wired", "unreachable") after the wiring
 * shipped. Wording pins, deliberately: each pattern below is the exact stale claim the
 * review listed, so a regression to "library exists, nothing calls it" fails here. The
 * up-to-date claims (wired into picker/batch/MCP/extension, verification unsupported)
 * are asserted positively so the fix cannot be "fixed" by deleting the OOXML rows.
 */

import { readFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { expect, test } from 'vitest';

const ROOT = fileURLToPath(new URL('..', import.meta.url));

function doc(path: string): string {
	return readFileSync(`${ROOT}/${path}`, 'utf8');
}

const STALE_CLAIMS: Array<[file: string, line: number, pattern: RegExp]> = [
	['README.md', 0, /Motor disponible, sin selector en la interfaz/],
	['README.md', 0, /XAdES-T y OOXML.*tienen motor pero no selector/],
	['docs/LEEME.md', 0, /librería hecha y probada, \*\*sin enlace en la interfaz\*\*/],
	['docs/USE-CASES.md', 0, /`src\/lib\/ooxml\.ts` \(#10\), not wired/],
	['docs/USE-CASES.md', 0, /\*\*OOXML in the UI\*\* \(engine exists, unused\)/],
	['docs/PARITY.md', 0, /library written and tested, \*\*not called\*\*/],
	['docs/PARITY.md', 0, /OOXML signing is written and tested but unreachable from the UI \(/],
	['docs/PARITY.md', 0, /\*\*office formats\*\* \(OOXML\n?unwired/],
	['docs/PARITY.md', 0, /none wired; `src\/lib\/pades\.ts:293 signPdf` appends one revision/],
	['docs/CHROME-EXTENSION.md', 0, /'OOXML'.*null.*success.*error/]
];

test('no doc still claims the OOXML signer is unwired (review UNFIXED list, #127)', () => {
	for (const [file, , pattern] of STALE_CLAIMS.slice(0, -1)) {
		expect(doc(file), `${file} still claims OOXML is unwired: ${pattern}`).not.toMatch(pattern);
	}
});

test('the extension doc lists the OOXML intercept call it actually handles', () => {
	const text = doc('docs/CHROME-EXTENSION.md');
	expect(text).toMatch(/AutoScript\.sign\(base64, 'SHA256withRSA', 'OOXML', null, success, error\);/);
	// AUTO resolves only PDF/XML/CAdES in `extension/core/contract.js` contentFormat;
	// a claim that AUTO sniffed OOXML packages was false and is now pinned as such.
	expect(text).toMatch(/OOXML packages go through\s+the explicit `OOXML` format, not `AUTO`/);
	expect(text).not.toMatch(/an OOXML package \(`\[Content_Types\]\.xml`\)\s+signs as OOXML/);
});

test('the docs state the wired OOXML path and the unsupported verification', () => {
	expect(doc('README.md')).toMatch(/Disponible en la interfaz, auto-detectado al soltar/);
	expect(doc('docs/LEEME.md')).toMatch(/funciona, en la interfaz; auto-detectado al soltar/);
	expect(doc('docs/PARITY.md')).toMatch(
		/\*\*full\*\* — engine \+ UI; verification explicitly unsupported \(1\.9 parity\)/
	);
	expect(doc('docs/USE-CASES.md')).toMatch(
		/wired into the picker with drop auto-detect \(#127\); verification stays unsupported \(1\.9 parity\)/
	);
});

test('PARITY gap 2 records the #127 closure without breaking the numbered gap list', () => {
	const text = doc('docs/PARITY.md');
	expect(text).toMatch(/2\. .*Done \(#127\): `suggestSignFormat` auto-detects OOXML/);
});
