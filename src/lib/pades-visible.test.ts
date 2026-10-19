/**
 * Visible signature placement and image handling (#45).
 *
 * The rectangle maths is pure and checked here; the drawn block inside a *signed* PDF (and that the
 * signature still verifies) is checked in `pades.test.ts`, against the real signing pipeline.
 */

import { describe, expect, it } from 'vitest';
import { PDFDocument } from 'pdf-lib';
import { buildAppearance, mapAutoFirmaRect, rectForCorner } from './pades-visible';

const A4 = { width: 595.28, height: 841.89 };

describe('visible signature block', () => {
	it('places the block inside the page, 24 pt from the chosen corner', () => {
		expect(rectForCorner(A4, 'bottom-left')).toEqual([24, 24, 224, 94]);
		expect(rectForCorner(A4, 'bottom-right')).toEqual([371.28, 24, 571.28, 94]);
		expect(rectForCorner(A4, 'top-left')).toEqual([24, 747.89, 224, 817.89]);
		expect(rectForCorner(A4, 'top-right')).toEqual([371.28, 747.89, 571.28, 817.89]);
	});

	it('takes a custom block size and margin', () => {
		expect(rectForCorner({ width: 400, height: 300 }, 'top-right', { width: 100, height: 50 }, 10)).toEqual([
			290, 240, 390, 290
		]);
	});

	it('refuses an image that is neither PNG nor JPEG', async () => {
		const document = await PDFDocument.create();
		await expect(
			buildAppearance(document, [0, 0, 200, 70], {
				lines: ['signer'],
				image: new Uint8Array([0x47, 0x49, 0x46, 0x38])
			})
		).rejects.toThrow(/PNG or JPEG/);
	});

	it('maps AutoFirma coordinates through the page rotation', () => {
		const box = { x: 0, y: 0, width: 600, height: 800 };
		expect(mapAutoFirmaRect(box, 0, [10, 20, 110, 70])).toEqual([10, 20, 110, 70]);
		expect(mapAutoFirmaRect(box, 90, [10, 20, 110, 70])).toEqual([530, 10, 580, 110]);
		expect(mapAutoFirmaRect(box, 180, [10, 20, 110, 70])).toEqual([490, 730, 590, 780]);
		expect(mapAutoFirmaRect(box, 270, [10, 20, 110, 70])).toEqual([20, 690, 70, 790]);
	});

	it('leaves the stamp background transparent', async () => {
		const document = await PDFDocument.create();
		const appearance = await buildAppearance(document, [0, 0, 200, 70], { lines: ['signer'] });
		expect(appearance.content).not.toMatch(/\bre f\b/);
		expect(appearance.content).toContain('re S');
	});

	it('embeds the selected standard font in the appearance', async () => {
		const document = await PDFDocument.create();
		const appearance = await buildAppearance(document, [0, 0, 200, 70], {
			lines: ['signer'],
			fontFamily: 2,
			fontSize: 10,
			fontStyle: 1,
			fontColor: [1, 0, 0]
		});
		expect(appearance.fontBase).toBe('Times-Bold');
		expect(appearance.content).toContain('BT /F1 10 Tf 1 0 0 rg');
	});
});
