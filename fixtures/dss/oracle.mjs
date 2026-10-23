/**
 * EU DSS oracle over the fixture corpus (#140).
 *
 * An independent validator (the EU DSS demo, the one #96/#125 already use — DSS ships no CLI
 * artifact on Maven Central and publishes no image) runs on every push, its verdict is compared
 * field by field against ours, and any difference that is not in `allowlist.json` fails the job.
 *
 * Two normalisations make the comparison meaningful; both are one-way and documented here:
 *   - `format`: DSS says `CMS-NOT-ETSI` / `XML-NOT-ETSI` / `PAdES-BES`, we say `cades` / `xades` /
 *     `pdf`. Different vocabularies for the same families, so both collapse to the family.
 *   - `verdict`: DSS `TOTAL_PASSED`/`TOTAL_FAILED`/`INDETERMINATE`, we `valid`/`invalid`. Kept
 *     three-valued so DSS's INDETERMINATE (its own trust-anchor story) never masquerades as
 *     agreement or disagreement.
 *
 * Fields DSS reports but we have no counterpart for (SubIndication, SignatureLevel, CertificateChain)
 * are deliberately not compared — they are context for the triager, printed on mismatch.
 *
 * Exit 0 = verdicts agree (every difference is allowlisted), or the endpoint was unreachable.
 * Exit 1 = a new disagreement, or an allowlisted pair drifted.
 *
 *   node fixtures/dss/oracle.mjs             # compare
 *   node fixtures/dss/oracle.mjs --record    # print observed pairs for every difference
 */
import { existsSync, readFileSync, readdirSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { JSDOM } from 'jsdom';
import { verifyArtifact } from '../../src/lib/verify/index.ts';

// XAdES signs through the DOM and the app runs in a browser, so give this the same globals the
// test suite and `scripts/dss-issue-125.mjs` give it instead of a second parsing path.
const dom = new JSDOM('<!doctype html><html><body></body></html>');
globalThis.DOMParser = dom.window.DOMParser;
globalThis.XMLSerializer = dom.window.XMLSerializer;

const DSS =
	process.env.DSS_URL ??
	'https://ec.europa.eu/digital-building-blocks/DSS/webapp-demo/services/rest/validation/validateSignature';
const FIXTURES = fileURLToPath(new URL('../../test/fixtures/', import.meta.url));
const ALLOWLIST = JSON.parse(
	readFileSync(fileURLToPath(new URL('./allowlist.json', import.meta.url)), 'utf8')
);
const RECORD = process.argv.includes('--record');
const FIELDS = ['format', 'verdict', 'signedBy', 'signingTime'];

/**
 * The corpus and the parameters each entry needs. `roots` is what `src/lib/verify.test.ts` hands
 * the verifier for the same artifact: the PAdES golden's signer is its own anchor, everything else
 * chains to the throwaway test root.
 */
const CORPUS = [
	{ id: 'cades-valid.csig', signed: 'cades-valid.csig', roots: 'ca/test-root.crt' },
	{ id: 'cades-tampered.csig', signed: 'cades-tampered.csig', roots: 'ca/test-root.crt' },
	{
		id: 'cades-detached.csig + document.txt',
		signed: 'cades-detached.csig',
		detached: 'document.txt',
		roots: 'ca/test-root.crt'
	},
	{
		id: 'cades-detached.csig + document-tampered.txt',
		signed: 'cades-detached.csig',
		detached: 'document-tampered.txt',
		roots: 'ca/test-root.crt'
	},
	{ id: 'xades-valid.xsig', signed: 'xades-valid.xsig', roots: 'ca/test-root.crt' },
	{ id: 'xades-tampered.xsig', signed: 'xades-tampered.xsig', roots: 'ca/test-root.crt' },
	{ id: 'pades-b-b-signed.pdf', signed: 'pades-b-b-signed.pdf', roots: 'pades-signer.crt' }
];

const read = (name) => new Uint8Array(readFileSync(FIXTURES + name));

/** `CN=AutoFirma Web Test Signer, O=…` / `O=…, CN=AutoFirma Web Test Signer` → `AutoFirma Web Test Signer`. */
const cn = (subject) => subject?.match(/(?:^|,)\s*CN=([^,]+)/)?.[1]?.trim() ?? null;

/** DSS writes `2026-10-14T09:18:32Z`, we write `2026-10-14T09:18:32.000Z`. */
const iso = (value) => (value ? new Date(value).toISOString() : null);

const family = (format) =>
	/^(CMS|CAdES)/.test(format) ? 'cades' : /^(XML|XAdES)/.test(format) ? 'xades' : format?.startsWith('PAdES') ? 'pdf' : format;

const verdict = (indication) =>
	indication === 'TOTAL_PASSED'
		? 'valid'
		: indication === 'TOTAL_FAILED'
			? 'invalid'
			: indication === 'INDETERMINATE'
				? 'indeterminate'
				: indication;

/** Our verifier's verdict for the entry, reduced to the fields DSS also speaks. */
async function ours(entry) {
	const report = await verifyArtifact(read(entry.signed), {
		trustedRoots: [readFileSync(FIXTURES + entry.roots, 'utf8')],
		detached: entry.detached ? read(entry.detached) : null
	});
	return {
		format: report.format,
		verdict: report.ok ? 'valid' : 'invalid',
		signedBy: cn(report.signer?.subject),
		signingTime: iso(report.signingTime)
	};
}

/** DSS's verdict for the entry. Throws on transport/HTTP failure so the caller can skip the job. */
async function dss(entry) {
	const body = { signedDocument: { bytes: Buffer.from(read(entry.signed)).toString('base64'), name: entry.signed } };
	if (entry.detached) {
		body.originalDocuments = [
			{ bytes: Buffer.from(read(entry.detached)).toString('base64'), name: entry.detached }
		];
	}
	const response = await fetch(DSS, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
		body: JSON.stringify(body),
		signal: AbortSignal.timeout(60_000)
	});
	if (!response.ok) throw new Error(`HTTP ${response.status} ${(await response.text()).slice(0, 120)}`);
	const signature = (await response.json()).SimpleReport.signatureOrTimestampOrEvidenceRecord[0].Signature;
	return {
		fields: {
			format: family(signature.SignatureFormat),
			verdict: verdict(signature.Indication),
			signedBy: signature.SignedBy ?? null,
			signingTime: iso(signature.SigningTime)
		},
		// DSS-only context, printed for the triager; never compared.
		context: `${signature.SignatureFormat} ${signature.Indication}/${signature.SubIndication}`
	};
}

/**
 * Compare one fixture's four fields. Pure: no network, no verifier, so the workflow can exercise
 * the failure paths offline (`--self-check`) even when the DSS endpoint is down.
 *
 * A field is `ok` when both sides agree and nothing is pinned; `pinned` when the two sides differ
 * on exactly the pair the allowlist records. Everything else is a failure — an unpinned
 * disagreement, or a pinned pair whose observed values no longer match the pin.
 */
export function compare(id, mine, theirs, entries) {
	const lines = [];
	const records = [];
	const failures = [];
	for (const field of FIELDS) {
		const pin = entries.find((entry) => entry.fixture === id && entry.field === field);
		const same = mine[field] === theirs.fields[field];
		if (!pin && same) {
			lines.push(`ok     ${id} ${field}: ${mine[field]}`);
			continue;
		}
		records.push({ fixture: id, field, dss: theirs.fields[field], ours: mine[field] });
		if (pin && !same && mine[field] === pin.ours && theirs.fields[field] === pin.dss) {
			lines.push(`pinned ${id} ${field}: ours=${mine[field]} dss=${theirs.fields[field]}`);
			continue;
		}
		failures.push(
			`${id} ${field}: ours=${JSON.stringify(mine[field])} dss=${JSON.stringify(theirs.fields[field])}` +
				(pin
					? ` — allowlist pins ours=${JSON.stringify(pin.ours)} dss=${JSON.stringify(pin.dss)} (${pin.reason})`
					: ` — not in fixtures/dss/allowlist.json (DSS: ${theirs.context})`)
		);
	}
	return { lines, records, failures };
}

/**
 * Offline guard on the corpus itself, so the "hand-picked CORPUS" claim cannot rot: every
 * signature artifact in `test/fixtures/` must be posted (a new `.csig`/`.xsig` can no longer be
 * skipped silently), and every file CORPUS names must exist (a rename fails here rather than
 * passing as "DSS unreachable" on the live half). No network.
 */
export function corpusProblems(fixtures, corpus) {
	const problems = [];
	const referenced = corpus.flatMap((entry) => [entry.signed, entry.detached, entry.roots].filter(Boolean));
	for (const name of referenced) {
		if (!existsSync(fixtures + name)) problems.push(`CORPUS names a missing file: ${name}`);
	}
	const posted = new Set(corpus.map((entry) => entry.signed));
	for (const file of readdirSync(fixtures)) {
		if (/\.(csig|xsig)$/.test(file) && !posted.has(file)) {
			problems.push(`${file} lives in test/fixtures/ but CORPUS never posts it to DSS`);
		}
	}
	return problems;
}

if (process.argv.includes('--self-check')) {
	// Offline proof that the gate bites: disagreeing verdicts fail unless the allowlist pins the
	// exact pair, and a pin whose observed values moved fails too. Runs before any network call.
	const observed = (verdict) => ({
		fields: { format: 'cades', verdict, signedBy: 'A', signingTime: 'T' },
		context: 'self-check'
	});
	const mine = { format: 'cades', verdict: 'valid', signedBy: 'A', signingTime: 'T' };
	const checks = [
		['agreement passes', compare('f', mine, observed('valid'), []).failures.length, 0],
		['unpinned disagreement fails', compare('f', mine, observed('invalid'), []).failures.length, 1],
		[
			'matching pin passes',
			compare('f', mine, observed('indeterminate'), [
				{ fixture: 'f', field: 'verdict', ours: 'valid', dss: 'indeterminate', reason: 'x' }
			]).failures.length,
			0
		],
		[
			'stale pin fails',
			compare('f', mine, observed('invalid'), [
				{ fixture: 'f', field: 'verdict', ours: 'valid', dss: 'indeterminate', reason: 'x' }
			]).failures.length,
			1
		]
	];
	for (const [name, actual, expected] of checks) {
		if (actual !== expected) throw new Error(`self-check failed: ${name} (${actual} !== ${expected})`);
		console.log(`ok     self-check: ${name}`);
	}
	const corpus = corpusProblems(FIXTURES, CORPUS);
	if (corpus.length > 0) throw new Error(`self-check failed: corpus\n - ${corpus.join('\n - ')}`);
	console.log(`ok     self-check: corpus (${CORPUS.length} entries, every signature fixture posted)`);
	// Proof the guard itself bites: a corpus entry naming a file that is not there must be caught.
	const drifted = corpusProblems(FIXTURES, [{ id: 'gone', signed: 'gone.csig', roots: 'ca/test-root.crt' }]);
	if (!drifted.some((problem) => problem.includes('gone.csig'))) {
		throw new Error(`self-check failed: corpus guard did not catch a missing fixture (${drifted})`);
	}
	console.log('ok     self-check: corpus guard catches a phantom fixture');
	process.exit(0);
}

const failures = [];
const records = [];
let reached = 0;

for (const entry of CORPUS) {
	let theirs;
	try {
		theirs = await dss(entry);
		reached += 1;
	} catch (error) {
		console.log(`skip   ${entry.id}: DSS unreachable (${error.message})`);
		continue;
	}

	const result = compare(entry.id, await ours(entry), theirs, ALLOWLIST.entries);
	for (const line of result.lines) console.log(line);
	records.push(...result.records);
	failures.push(...result.failures);
}

if (reached === 0) {
	console.log('\nDSS oracle skipped: endpoint unreachable. A network oracle cannot gate the build.');
	process.exit(0);
}
if (reached < CORPUS.length) {
	console.log(`\nDSS oracle partially skipped: ${reached}/${CORPUS.length} fixtures reached.`);
}

if (RECORD) {
	console.log(`\n${JSON.stringify(records, null, '\t')}`);
}

if (failures.length > 0) {
	console.error(`\n${failures.length} disagreement(s):`);
	for (const failure of failures) console.error(` - ${failure}`);
	process.exit(1);
}
console.log(`\nDSS agrees on ${reached} fixture(s) (field by field, allowlisted differences pinned).`);
