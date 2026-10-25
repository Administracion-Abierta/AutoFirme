/**
 * Visible signature block: where to put it and what it looks like.
 *
 * `pades.ts` owns the PDF plumbing (incremental update, `/ByteRange`, xref); this module owns the
 * drawing. It turns a page + corner (or explicit coordinates) into the annotation rectangle, builds
 * the appearance content stream — an optional signature image plus text lines (CN, date, reason) —
 * and embeds the image through `pdf-lib`, handing back the raw objects the update must append.
 *
 * Everything here is written into the incremental update, so the drawn bytes are covered by
 * `/ByteRange` and the CMS digest is computed over the final document.
 */

import { PDFDocument, PDFName, PDFRef, PDFStream, type PDFObject } from 'pdf-lib';

import { toBytes, type Bytes } from './der';

/** Where the block sits when it is placed by corner instead of by coordinates. */
export type SignatureCorner = 'bottom-left' | 'bottom-right' | 'top-left' | 'top-right';

export const SIGNATURE_CORNERS: readonly SignatureCorner[] = [
	'bottom-left',
	'bottom-right',
	'top-left',
	'top-right'
];

/** Default block size in PDF points, and its distance to the page edge. */
export const BLOCK_SIZE = { width: 200, height: 70 } as const;
export const BLOCK_MARGIN = 24;

export interface VisibleSignatureOptions {
	/** Zero-based page index. Negative counts from the end (`-1` is the last page). Defaults to the first page. */
	page?: number;
	/** Placement in PDF points; wins over `corner`. */
	rect?: [number, number, number, number];
	/**
	 * AutoFirma's `signaturePositionOnPage*` box. Unlike `rect`, this is in the page's
	 * unrotated user space; `pades.ts` maps it to the widget rectangle with the page's
	 * `/MediaBox` and `/Rotate`, as AutoFirma's iText layer does.
	 */
	autoFirmaRect?: [number, number, number, number];
	corner?: SignatureCorner;
	/** Block size used by `corner`. */
	size?: { width: number; height: number };
	/** Signature image: PNG or JPEG bytes, fitted into the left of the block. */
	image?: ArrayBuffer | ArrayBufferView;
	/** Text lines drawn in the block. Defaults to the signature's `/Name`. */
	lines?: readonly string[];
	/** Text layer 2 font. Defaults match AutoFirma: Helvetica, 8 pt, black. */
	fontFamily?: 0 | 1 | 2;
	fontSize?: number;
	fontStyle?: 0 | 1 | 2 | 3;
	fontColor?: [number, number, number];
}

const STANDARD_FONTS = [
	['Courier', 'Courier-Bold', 'Courier-Oblique', 'Courier-BoldOblique'],
	['Helvetica', 'Helvetica-Bold', 'Helvetica-Oblique', 'Helvetica-BoldOblique'],
	['Times-Roman', 'Times-Bold', 'Times-Italic', 'Times-BoldItalic']
] as const;
// Average glyph width as a fraction of the font size, used only to wrap text.
const FONT_WIDTH = [0.6, 0.55, 0.5];

/** Map AutoFirma's unrotated placement box to the page's rotated user space. */
export function mapAutoFirmaRect(
	geometry: { x: number; y: number; width: number; height: number },
	rotation: number,
	rect: [number, number, number, number]
): [number, number, number, number] {
	const [x1, y1, x2, y2] = rect;
	const right = geometry.x + geometry.width;
	const top = geometry.y + geometry.height;
	const angle = ((rotation % 360) + 360) % 360;
	const map = (x: number, y: number): [number, number] =>
		angle === 90
			? [right - y, x]
			: angle === 180
				? [right - x, top - y]
				: angle === 270
					? [y, top - x]
					: [x, y];
	const [ax, ay] = map(x1, y1);
	const [bx, by] = map(x2, y2);
	return [Math.min(ax, bx), Math.min(ay, by), Math.max(ax, bx), Math.max(ay, by)];
}

/** Rectangle of a corner block: `[x1, y1, x2, y2]` in PDF coordinates (origin bottom-left). */
export function rectForCorner(
	geometry: { width: number; height: number },
	corner: SignatureCorner,
	size: { width: number; height: number } = BLOCK_SIZE,
	margin = BLOCK_MARGIN
): [number, number, number, number] {
	const left = margin;
	const right = geometry.width - margin - size.width;
	const bottom = margin;
	const top = geometry.height - margin - size.height;
	const [x, y] =
		corner === 'bottom-left'
			? [left, bottom]
			: corner === 'bottom-right'
				? [right, bottom]
				: corner === 'top-left'
					? [left, top]
					: [right, top];
	// Two decimals: the page size is a float, and 371.28000000000003 does not belong in a PDF.
	const round = (value: number) => Math.round(value * 100) / 100;
	return [round(x), round(y), round(x + size.width), round(y + size.height)];
}

/**
 * Content-stream literal: WinAnsi bytes, non-printables octal-escaped (WinAnsi font in `/AP`).
 * ISO 32000-1 §7.9.3.2.
 */
function pdfStreamString(value: string): string {
	let out = '';
	for (const char of value) {
		const cp = char.codePointAt(0) ?? 0x3f;
		// ponytail: identity ≤ U+00FF + '?' beyond; add the cp1252 0x80–0x9F block (€ “ ” –)
		// or subset embedded fonts when copy needs more than Spanish Latin-1.
		const byte = cp <= 0xff ? cp : 0x3f;
		if (byte === 0x28 || byte === 0x29 || byte === 0x5c) out += '\\' + String.fromCharCode(byte);
		else if (byte < 0x20 || byte > 0x7e) out += '\\' + byte.toString(8).padStart(3, '0');
		else out += String.fromCharCode(byte);
	}
	return out;
}

/** pdf-lib's canonical object serialiser, for objects written straight into the incremental update. */
export function serializeObject(node: PDFObject): Bytes {
	const buffer = new Uint8Array(node.sizeInBytes());
	node.copyBytesInto(buffer, 0);
	return buffer;
}

export interface Appearance {
	/** Form XObject content stream. */
	content: string;
	/** `/BaseFont` for the content stream's `/F1` resource. */
	fontBase: string;
	/** `/XObject` entry for the image, absent when the block has none. */
	imageRef: PDFRef | null;
	/** Image streams (the image, plus its `/SMask` when it has alpha) to append to the update. */
	objects: { ref: PDFRef; bytes: Bytes }[];
}

const raster = {
	png: [0x89, 0x50, 0x4e, 0x47],
	jpeg: [0xff, 0xd8, 0xff]
} as const;

/** PNG or JPEG, from the magic bytes — the only two raster formats pdf-lib embeds directly. */
export function imageFormat(bytes: Bytes): keyof typeof raster | null {
	for (const [format, magic] of Object.entries(raster) as [keyof typeof raster, readonly number[]][]) {
		if (magic.every((byte, index) => bytes[index] === byte)) return format;
	}
	return null;
}

/**
 * Build the appearance stream for a visible signature: the block's text lines, plus the signature
 * image fitted into its left side. Image streams come back as raw objects to append to the update.
 */
export async function buildAppearance(
	document: PDFDocument,
	rect: [number, number, number, number],
	options: {
		lines: readonly string[];
		image?: ArrayBuffer | ArrayBufferView;
		fontFamily?: 0 | 1 | 2;
		fontSize?: number;
		fontStyle?: 0 | 1 | 2 | 3;
		fontColor?: [number, number, number];
	}
): Promise<Appearance> {
	const width = Math.abs(rect[2] - rect[0]);
	const height = Math.abs(rect[3] - rect[1]);
	const fontFamily = options.fontFamily ?? 1;
	const fontSize = options.fontSize ?? 8;
	const fontBase = STANDARD_FONTS[fontFamily][options.fontStyle ?? 0];
	const [red, green, blue] = options.fontColor ?? [0, 0, 0];

	let imageRef: PDFRef | null = null;
	const objects: Appearance['objects'] = [];
	let imageColumn = 0;
	let imageCommand = '';

	if (options.image) {
		const bytes = toBytes(options.image);
		const format = imageFormat(bytes);
		if (format === null) throw new Error('signature image must be PNG or JPEG');
		const embedded =
			format === 'png' ? await document.embedPng(bytes) : await document.embedJpg(bytes);
		// `embed()` returns void; the ref is the image's own, assigned when it was created.
		await embedded.embed();
		imageRef = embedded.ref;

		const box = { width: width * 0.38, height: height - 12 };
		const scale = Math.min(box.width / embedded.width, box.height / embedded.height);
		const drawn = { width: embedded.width * scale, height: embedded.height * scale };
		imageColumn = drawn.width + 12;
		imageCommand =
			`q ${drawn.width.toFixed(2)} 0 0 ${drawn.height.toFixed(2)} 6 ` +
			`${((height - drawn.height) / 2).toFixed(2)} cm /Im0 Do Q`;

		const context = document.context;
		const stream = context.lookup(imageRef, PDFStream);
		objects.push({ ref: imageRef, bytes: serializeObject(stream) });
		const mask = stream.dict.get(PDFName.of('SMask'));
		if (mask instanceof PDFRef) {
			objects.push({ ref: mask, bytes: serializeObject(context.lookup(mask, PDFStream)) });
		}
	}

	const available = Math.max(width - imageColumn - 8, 8);
	// Rough average glyph width for the chosen standard font.
	const perLine = Math.max(1, Math.floor(available / (FONT_WIDTH[fontFamily] * fontSize)));
	const leading = Math.max(1, Math.ceil(fontSize * 1.25));
	const fits = Math.max(1, Math.floor((height - 12) / leading));
	// Wrap instead of truncating: a date cut in half is worse than a shorter line.
	const wrapped: string[] = [];
	for (const line of options.lines) {
		let current = '';
		for (const word of line.split(' ')) {
			const candidate = current.length > 0 ? `${current} ${word}` : word;
			if (candidate.length > perLine) {
				if (current.length > 0) wrapped.push(current);
				// A single word wider than the block is cut; nothing else can be done without shaping.
				current = word.slice(0, perLine);
			} else {
				current = candidate;
			}
		}
		wrapped.push(current.slice(0, perLine));
	}
	const lines = wrapped.slice(0, fits).map((line) => `(${pdfStreamString(line)}) Tj`);

	const content = [
		'q',
		'0.55 0.55 0.55 RG 0.5 w',
		`0.25 0.25 ${Math.max(width - 0.5, 0)} ${Math.max(height - 0.5, 0)} re S`,
		...(imageCommand ? [imageCommand] : []),
		`BT /F1 ${fontSize} Tf ${red} ${green} ${blue} rg ${imageColumn + 4} ${Math.max(height - 12, 1)} Td`,
		// First line sits at Td; the rest step down with the same offset.
		lines.join(` 0 -${leading} Td `),
		'ET',
		'Q',
		''
	].join('\n');

	return { content, fontBase, imageRef, objects };
}
