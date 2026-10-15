/**
 * Signs a PDF with a real keystore through the shipped PAdES path and verifies the result with the
 * app's own CAdES verifier. Driven by `scripts/smoke-real.sh` (Refs #35); not a test — it needs the
 * real PIN, so it never runs in CI.
 *
 *   node scripts/smoke-real.mjs <keystore.p12> <out.pdf> [input.pdf]
 *
 * The PIN rides in `$P12_PIN` (never in argv, so it stays out of `ps`). The CMS and the bytes the
 * digest covers are dropped in `$SMOKE_TMP` so the shell wrapper can cross-check them with
 * `openssl cms -verify`. Node strips the TypeScript types natively.
 */

import { readFileSync, writeFileSync } from 'node:fs';
import { join } from 'node:path';
import { PDFArray, PDFDict, PDFDocument, PDFHexString, PDFName, PDFNumber, PDFRef } from 'pdf-lib';

import { loadSignerSession } from '../src/lib/keystore.ts';
import { signPdf } from '../src/lib/pades.ts';
import { verifyArtifact } from '../src/lib/verify/index.ts';

const [keystorePath, outPath, inputPath] = process.argv.slice(2);
if (!keystorePath || !outPath) {
	console.error('usage: node scripts/smoke-real.mjs <keystore.p12> <out.pdf> [input.pdf]');
	process.exit(2);
}
const pin = process.env.P12_PIN;
if (!pin) {
	console.error('P12_PIN is not set (scripts/smoke-real.sh reads it from .p12pass)');
	process.exit(2);
}
const scratch = process.env.SMOKE_TMP ?? '.';

const line = (label, value) => console.log(`${label.padEnd(14)}${value}`);

// ── 1. keystore ────────────────────────────────────────────────────────────────────────────────
const bytes = new Uint8Array(readFileSync(keystorePath));
const session = await loadSignerSession(bytes, pin);
const signer = session.signer;
line('signer', `${signer.commonName} <${signer.subject}>`);
line('issuer', signer.issuer);
line('serial', signer.serial);
line('validity', `${signer.notBefore.toISOString()} → ${signer.notAfter.toISOString()}${
	signer.expired ? '  (expired — signature still verifies)' : ''
}`);
line('key', `${signer.algorithm.name} / ${signer.algorithm.hash}`);
line('certs', `${session.certificates.length} in the container`);

// ── 2. source document ─────────────────────────────────────────────────────────────────────────
let source;
if (inputPath) {
	source = new Uint8Array(readFileSync(inputPath));
	line('document', `${inputPath} (${source.length} B)`);
} else {
	const document = await PDFDocument.create();
	const page = document.addPage([595.28, 841.89]);
	page.drawText('AutoFirma Web — real-certificate smoke test (Refs #35)', { x: 56, y: 780, size: 12 });
	page.drawText(`Signer: ${signer.commonName}`, { x: 56, y: 760, size: 10 });
	page.drawText(`Signed: ${new Date().toISOString()}`, { x: 56, y: 744, size: 10 });
	source = await document.save({ useObjectStreams: false });
	line('document', `generated placeholder PDF (${source.length} B)`);
}

// ── 3. PAdES B-B signature ─────────────────────────────────────────────────────────────────────
const signed = await signPdf(source, {
	certificate: signer.der,
	chain: session.certificates.filter((certificate) => certificate !== signer).map((c) => c.der),
	privateKey: signer.key,
	signingTime: new Date(),
	name: signer.commonName,
	reason: 'Prueba de humo con certificado real',
	location: 'España'
});
writeFileSync(outPath, signed);
line('signed', `${outPath} (${signed.length} B, +${signed.length - source.length} B incremental update)`);

// ── 4. read the signature back the way a viewer would ──────────────────────────────────────────
const document = await PDFDocument.load(signed);
const acroEntry = document.catalog.get(PDFName.of('AcroForm'));
const acroForm =
	acroEntry instanceof PDFRef ? document.context.lookup(acroEntry, PDFDict) : (acroEntry ?? null);
const fields = acroForm.lookup(PDFName.of('Fields'), PDFArray);
const field = document.context.lookup(fields.get(0), PDFDict);
const dictionary = document.context.lookup(field.get(PDFName.of('V')), PDFDict);
const byteRange = dictionary.lookup(PDFName.of('ByteRange'), PDFArray).asArray().map((n) => n.asNumber());
const contents = dictionary.lookup(PDFName.of('Contents'), PDFHexString).asBytes();
const content = new Uint8Array(byteRange[1] + byteRange[3]);
content.set(signed.subarray(byteRange[0], byteRange[0] + byteRange[1]), 0);
content.set(signed.subarray(byteRange[2], byteRange[2] + byteRange[3]), byteRange[1]);

line('field', field.lookup(PDFName.of('T')).decodeText());
line('subfilter', dictionary.lookup(PDFName.of('SubFilter'), PDFName).asString());
line('byterange', `[${byteRange.join(' ')}] → ${content.length} B covered`);

// For the wrapper's independent `openssl cms -verify` (the /Contents is zero-padded over its DER).
const derEnd = (() => {
	let end = contents.length;
	while (end > 0 && contents[end - 1] === 0) end -= 1;
	return end;
})();
writeFileSync(join(scratch, 'signature.cms'), contents.subarray(0, derEnd));
writeFileSync(join(scratch, 'content.bin'), content);

// ── 5. the app's own verification path, over the real CMS ──────────────────────────────────────
const report = await verifyArtifact(contents.subarray(0, derEnd), { detached: content });
console.log('\nverify report');
for (const check of report.checks) {
	console.log(`  ${check.status.padEnd(5)}${check.id.padEnd(14)}${check.detail}`);
}
if (report.signer) line('report', `${report.signer.subject} · issued by ${report.signer.issuer}`);
if (report.signingTime) line('signingTime', report.signingTime);

// Trust/date checks are informational here (the real cert is old by design); the cryptographic ones
// are not — those are the whole point of the smoke test.
const crypto = ['structure', 'contentDigest', 'signature'];
const failed = crypto.filter((id) => report.checks.find((check) => check.id === id)?.status !== 'pass');
if (failed.length > 0) {
	console.error(`\nFAIL — ${failed.join(', ')} did not pass`);
	process.exit(1);
}
console.log(`\napp verify: OK (${crypto.join(', ')} pass)`);
