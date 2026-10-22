/**
 * EU DSS verdicts for #125 — XAdES-T and CAdES-X Long type 1/2.
 *
 *   node --import ./scripts/ts-hook.mjs scripts/dss-issue-125.mjs
 *
 * Builds the fixtures, posts each one to the EU DSS demo REST endpoint and writes the verdicts and
 * DSS's own validation reports to `docs/evidence/issue-125/`. Network-dependent (the DSS demo plus
 * an RFC 3161 TSA), so it is deliberately not part of `pnpm test`: a remote oracle cannot gate the
 * suite. Evidence and the reading of it: `docs/evidence/issue-125/README.md`.
 */

import { mkdirSync, readFileSync, writeFileSync } from 'node:fs';
import { JSDOM } from 'jsdom';
import { createCadesFixtures, createPkcs12Fixtures, FIXTURE_PIN } from '../src/test/fixtures.ts';
import { signCadesDetached } from '../src/lib/cms.ts';
import { loadSignerSession } from '../src/lib/keystore.ts';
import { signXades } from '../src/lib/xades.ts';
import { verifyArtifact } from '../src/lib/verify/index.ts';

// XAdES signs through the DOM; the app runs in a browser and the tests in jsdom, so give this
// script the same globals instead of a second parsing path.
const dom = new JSDOM('<!doctype html><html><body></body></html>');
globalThis.DOMParser = dom.window.DOMParser;
globalThis.XMLSerializer = dom.window.XMLSerializer;

/** The reachable RFC 3161 TSA from this container. Any TSA works; the DSS verdict does not depend
 * on which one issued the token, only on the token being real. */
const TSA = process.env.TSA_URL ?? 'https://freetsa.org/tsr';
const DSS =
	'https://ec.europa.eu/digital-building-blocks/DSS/webapp-demo/services/rest/validation/validateSignature';
const OUT = 'docs/evidence/issue-125';

/** One `signedDocument` (singular — the plural field is rejected) plus the detached original. */
async function validate(name, bytes, originals = []) {
	const response = await fetch(DSS, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
		body: JSON.stringify({
			signedDocument: { bytes: Buffer.from(bytes).toString('base64'), name },
			...(originals.length
				? { originalDocuments: originals.map(([file, data]) => ({ name: file, bytes: Buffer.from(data).toString('base64') })) }
				: {})
		})
	});
	const report = await response.json();
	const signature = report.SimpleReport.signatureOrTimestampOrEvidenceRecord[0].Signature;
	return {
		http: response.status,
		dssSignatureFormat: signature.SignatureFormat,
		dssIndication: `${signature.Indication} / ${signature.SubIndication}`,
		dssTimestamps: (signature.Timestamps?.Timestamp ?? []).length,
		producedBy: [...new Set((signature.Timestamps?.Timestamp ?? []).map((entry) => entry.ProducedBy))],
		xml: Buffer.from(report.validationReportDataHandler, 'base64').toString('utf8')
	};
}

mkdirSync(OUT, { recursive: true });
const verdicts = {};

/** Our verifier's view of the same bytes, for the side-by-side in the README. */
async function ours(file, bytes, roots, detached) {
	const report = await verifyArtifact(bytes, { trustedRoots: roots, detached });
	return report.checks.filter((check) => check.id === 'timestamp').map((check) => `${check.status}: ${check.detail}`);
}

// --- CAdES -------------------------------------------------------------------------------------
const cades = createCadesFixtures();
const cadesSession = await loadSignerSession(cades.p12, FIXTURE_PIN);
const cadesRoots = [readFileSync(cades.caPath, 'utf8')];
writeFileSync(`${OUT}/cades.content.bin`, cades.content);

for (const level of /** @type {const} */ ([1, 2])) {
	const bytes = await signCadesDetached(cadesSession.signer, cades.content, {
		timeStamp: TSA,
		xLong: level,
		chain: cadesSession.certificates
	});
	const file = `cades-xlong${level}.p7s`;
	writeFileSync(`${OUT}/${file}`, bytes);
	const verdict = await validate(file, bytes, [['cades.content.bin', cades.content]]);
	verdicts[file] = { ...verdict, ours: await ours(file, bytes, cadesRoots, cades.content) };
	writeFileSync(`${OUT}/dss.${file}.validation-report.xml`, verdict.xml);
}

// --- XAdES -------------------------------------------------------------------------------------
const xadesFixtures = createPkcs12Fixtures();
const xadesSession = await loadSignerSession(xadesFixtures.modern, FIXTURE_PIN);
const xml = '<documento><saludo>hola</saludo></documento>\n';

for (const [file, timeStamp] of [
	['xades-bes.xsig', undefined],
	['xades-t.xsig', TSA]
]) {
	const text = await signXades({
		xml,
		certificates: xadesSession.certificates,
		signer: xadesSession.signer,
		...(timeStamp ? { timeStamp } : {})
	});
	const bytes = new TextEncoder().encode(text);
	writeFileSync(`${OUT}/${file}`, bytes);
	const verdict = await validate(file, bytes);
	verdicts[file] = { ...verdict, dssSignatureFormat: verdict.dssSignatureFormat };
	if (timeStamp) writeFileSync(`${OUT}/dss.${file}.validation-report.xml`, verdict.xml);
}

// --- negative controls -------------------------------------------------------------------------
// Rewriting the X-level timestamp's attribute OID to the pre-fix …16.2.14 (same DER length) is
// what DSS reads to decide `CAdES-X` vs `CAdES-C`; flipping a byte inside the certificate refs
// leaves the label alone but turns DSS's cryptographic check to HASH_FAILURE.
const xlong2 = new Uint8Array(readFileSync(`${OUT}/cades-xlong2.p7s`));
const certCrl = Uint8Array.from([0x2a, 0x86, 0x48, 0x86, 0xf7, 0x0d, 0x01, 0x09, 0x10, 0x02, 0x1a]);
const oldOid = new Uint8Array(readFileSync(`${OUT}/cades-xlong2.p7s`));
for (let i = 0; i + certCrl.length <= oldOid.length; i++) {
	if (oldOid[i - 2] === 0x06 && oldOid[i - 1] === 0x0b && certCrl.every((byte, j) => oldOid[i + j] === byte)) {
		oldOid[i + certCrl.length - 1] = 0x0e;
	}
}
writeFileSync(`${OUT}/cades-xlong2-oldoid.p7s`, oldOid);

const badRefs = new Uint8Array(xlong2);
const { parseDer, oid, explicit } = await import('../src/lib/asn1.ts');
const certRefsOid = '1.2.840.113549.1.9.16.2.21';
const signerInfo = explicit(parseDer(badRefs).children[1], 0xa0).children[4].children[0];
const refs = signerInfo.children[6].children.find((attribute) => oid(attribute.children[0]) === certRefsOid);
if (!refs) throw new Error('the X Long fixture has no complete-certificate-references attribute');
badRefs[refs.children[1].children[0].value.byteOffset + 3] ^= 0x01;
writeFileSync(`${OUT}/cades-xlong2-badrefs.p7s`, badRefs);

for (const file of ['cades-xlong2-oldoid.p7s', 'cades-xlong2-badrefs.p7s']) {
	const bytes = new Uint8Array(readFileSync(`${OUT}/${file}`));
	const verdict = await validate(file, bytes, [['cades.content.bin', cades.content]]);
	verdicts[file] = { ...verdict, ours: await ours(file, bytes, cadesRoots, cades.content) };
	writeFileSync(`${OUT}/dss.${file}.validation-report.xml`, verdict.xml);
}

writeFileSync(`${OUT}/dss.issue-125.verdicts.json`, JSON.stringify(verdicts, null, 1));
for (const [file, verdict] of Object.entries(verdicts)) {
	console.log(`${file}: HTTP ${verdict.http} ${verdict.dssSignatureFormat} (${verdict.dssIndication})`);
}
