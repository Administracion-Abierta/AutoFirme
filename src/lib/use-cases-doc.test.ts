import { existsSync, readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { describe, expect, it } from 'vitest';

// Regression pins for the #41 review findings (Refs #41). The matrix in
// docs/USE-CASES.md is a map of AutoFirma 1.9; these tests keep the reviewed
// invariants from silently drifting back.
const lines = readFileSync(resolve(process.cwd(), 'docs/USE-CASES.md'), 'utf8').split('\n');

const gapItem = (n: number): string => {
	const p0 = lines.findIndex((l) => l.includes('**P0 — required'));
	const p1 = lines.findIndex((l) => l.includes('**P1 — parity'));
	const start = lines.findIndex((l, i) => i > p0 && i < p1 && l.startsWith(`${n}.`));
	if (start < 0) throw new Error(`P0 gap item ${n} not found`);
	// A list item runs until the next numbered item or the next section.
	let end = start + 1;
	while (end < p1 && !/^\d+\.\s/.test(lines[end]) && !lines[end].startsWith('**')) end++;
	return lines.slice(start, end).join(' ');
};

describe('USE-CASES.md embeddable-API priority (review finding 1)', () => {
	it('marks the embeddable API P0 everywhere, never P1/P2', () => {
		const embedLines = lines.filter((l) => /embed/i.test(l) && /P[012]/.test(l));
		expect(embedLines.length).toBeGreaterThanOrEqual(2);
		for (const line of embedLines) {
			expect(line).toMatch(/P0/);
			expect(line).not.toMatch(/P[12]/);
		}
	});

	it('lists gap item 8 (embeddable API) under P0, §8-referenced', () => {
		const item8 = gapItem(8);
		expect(item8).toMatch(/Embeddable API/);
		expect(item8).toMatch(/§8/);
	});
});

describe('USE-CASES.md visible-signature citations (review finding 2)', () => {
	it('cites SignPdfUiPanelPreview.java:565-574 for text layer/font/rotation', () => {
		const hit = lines.find((l) => l.includes('Visible signature: page, X/Y'));
		expect(hit).toBeDefined();
		expect(hit).toContain('SignPdfUiPanelPreview.java:565-574');
	});

	// work/decompiled is gitignored scratch recovered from tag reference/decompile-1.9;
	// when present, verify the cited lines literally carry the claimed properties.
	const previewPath = resolve(
		process.cwd(),
		'work/decompiled/sources/es/gob/afirma/standalone/ui/pdf/SignPdfUiPanelPreview.java'
	);
	it(`cited preview lines contain the font/rotation properties${existsSync(previewPath) ? '' : ' [scratch tree absent]'}`, () => {
		if (!existsSync(previewPath)) return;
		const cited = readFileSync(previewPath, 'utf8').split('\n').slice(564, 574).join('\n');
		expect(cited).toContain('LAYER2_FONTFAMILY');
		expect(cited).toContain('LAYER2_FONTSIZE');
		expect(cited).toContain('LAYER2_FONTCOLOR');
		expect(cited).toContain('SIGNATURE_ROTATION');
	});
});

describe('USE-CASES.md revocation gap (review finding 3)', () => {
	it('gap item 3 is revocation/trust depth, not expired-cert reporting', () => {
		const item3 = gapItem(3);
		expect(item3).toMatch(/OCSP\/CRL/);
		expect(item3).toMatch(/CA_NOT_SUPPORTED/);
		expect(item3).not.toMatch(/CERTIFICATE_EXPIRED/);
	});
});
