/**
 * Cross-checks the batch ZIP writer against tools that did not write it (Info-ZIP + coreutils),
 * the same way the signing issues cross-check a signature with `openssl`.
 *
 *   node scripts/verify-batch-zip.mjs
 *
 * The payloads here are synthetic — this script is about the archive and the digests; the real
 * CAdES signatures that go into it are verified by `openssl cms -verify` in `src/lib/batch.test.ts`.
 * Exits non-zero if `unzip -t` rejects the archive or if an extracted document does not hash back
 * to the digest the manifest claims. Node strips the TypeScript types natively.
 */

import { execFileSync } from 'node:child_process';
import { mkdtempSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { zipStore } from '../src/lib/zip.ts';

const documents = [
	{ name: 'contrato.txt.csig', data: new TextEncoder().encode('firma de contrato\n') },
	{ name: 'factura.txt.csig', data: new TextEncoder().encode('firma de factura\n') }
];

const digests = new Map(
	documents.map((document) => [
		document.name,
		execFileSync('sha256sum', { input: document.data }).toString().split(' ')[0]
	])
);

const manifest = {
	signbatch: { format: 'CAdES-BES', suboperation: 'sign', algorithm: 'SHA256withECDSA' },
	singlesigns: documents.map((document, index) => ({
		id: String(index + 1),
		datareference: document.name.replace(/\.csig$/, ''),
		sha256: digests.get(document.name),
		format: 'CAdES-BES',
		suboperation: 'sign',
		result: 'DONE_AND_SAVED',
		timestamp: new Date().toISOString(),
		output: document.name
	}))
};

const archive = zipStore([
	...documents,
	{ name: 'manifest.json', data: new TextEncoder().encode(`${JSON.stringify(manifest, null, 2)}\n`) }
]);

const directory = mkdtempSync(join(tmpdir(), 'autofirme-batch-'));
const path = join(directory, 'lote.zip');
writeFileSync(path, archive);

const test = execFileSync('unzip', ['-t', path]).toString();
console.log(test.trim());
if (!/No errors detected/.test(test)) throw new Error('unzip -t rejected the archive');

for (const document of documents) {
	const extracted = execFileSync('unzip', ['-p', path, document.name]);
	const digest = execFileSync('sha256sum', { input: extracted }).toString().split(' ')[0];
	if (digest !== digests.get(document.name)) {
		throw new Error(`${document.name}: sha256sum ${digest} does not match the manifest`);
	}
	console.log(`${document.name}: sha256sum matches the manifest digest (${digest.slice(0, 12)}…)`);
}

console.log(`OK — ${archive.length} bytes, ${documents.length + 1} entries, verified by unzip + sha256sum`);
console.log(`archive kept at ${path}`);
