#!/usr/bin/env node
/**
 * Scheduled TSL/LOTL refresh (Refs #135): fetches the Spanish TSL + EU LOTL,
 * extracts the trust-service X509Certificates, fingerprints them (sha256),
 * diffs against `src/lib/verify/roots.ts` (`TRUST_BUNDLE_META`), and prints a
 * reviewable PR body with the source timestamps to stdout.
 *
 * Stdlib only (`node:crypto`, `node:fs`). Never writes outside the owned files;
 * proposes patch text on stdout — the operator (or a scheduled job with
 * network) applies it after review.
 *
 * Schedule (no committed workflow: `.github/workflows/*` is outside this
 * issue's owned files — workflow owner wires this):
 *   0 6 * * 1  pnpm exec node scripts/refresh-tsl.mjs --check \
 *     || gh workflow run tsl-refresh.yml
 * Weekly check; on drift the job runs without `--check`, posts the stdout PR
 * body via `gh pr create --body-file -`, and a human merges.
 *
 * Only the certificate serial travels beyond the XML in a diff (fingerprints +
 * subjects); no signing-time request exists — the app resolves chains offline
 * against the compiled bundle.
 *
 * Usage:
 *   node scripts/refresh-tsl.mjs --help
 *   node scripts/refresh-tsl.mjs --check            # offline: staleness vs bundle, exit 2 when expired
 *   node scripts/refresh-tsl.mjs [--tsl-url U] [--lotl-url U] [--tsl-xml F] [--lotl-xml F]
 */
import { createHash } from 'node:crypto';
import { readFileSync } from 'node:fs';

const TSL_URL = 'https://tsl.digital.gob.es/TSL.xml';
const LOTL_URL = 'https://ec.europa.eu/tools/lotl/eu-lotl.xml';
const STALE_AFTER_DAYS = 180;

const args = process.argv.slice(2);
if (args.includes('--help') || args.includes('-h')) {
	console.log('refresh-tsl.mjs [--check] [--tsl-url U] [--lotl-url U] [--tsl-xml F] [--lotl-xml F]\n  --check: offline staleness vs bundle (exit 2 when stale); default fetches TSL+LOTL and prints a PR body.');
	process.exit(0);
}


/** sha256 fingerprint of a DER cert, colon-separated like openssl. */
export function fingerprintSha256(der) {
	return createHash('sha256')
		.update(der)
		.digest('hex')
		.toUpperCase()
		.replace(/../g, '$&:')
		.slice(0, -1);
}

/** All `<X509Certificate>` base64 blobs in a TSL/LOTL XML document. */
export function extractCertsBase64(xml) {
	const out = [];
	const re = /<(?:ds:)?X509Certificate>([^<]+)<\/(?:ds:)?X509Certificate>/g;
	let m;
	while ((m = re.exec(xml))) out.push(m[1].replace(/\s+/g, ''));
	return out;
}

/** `<NextUpdate>` / `<ListIssueDateTime>` timestamps when present, else null. */
export function extractTimestamps(xml) {
	const pick = (tag) => {
		const m = xml.match(new RegExp(`<${tag}[^>]*>(?:\\s*<dateTime>)?([^<]+)`));
		return m ? m[1].trim() : null;
	};
	return { nextUpdate: pick('NextUpdate'), issued: pick('ListIssueDateTime') };
}

function loadBundleMeta() {
	const src = readFileSync(new URL('../src/lib/verify/roots.ts', import.meta.url), 'utf8');
	const meta = {};
	for (const key of ['fetchedAt', 'nextUpdate', 'tslUrl', 'lotlUrl']) {
		const m = src.match(new RegExp(`${key}: '([^']+)'`));
		meta[key] = m ? m[1] : null;
	}
	meta.anchors = [...src.matchAll(/fingerprintSha256:\s*'([^']+)'/g)].map((m) => m[1]);
	return meta;
}

async function fetchText(url) {
	const res = await fetch(url, { headers: { 'User-Agent': 'AutoFirme-TSL-refresh' } });
	if (!res.ok) throw new Error(`fetch ${url}: HTTP ${res.status}`);
	return await res.text();
}

/** Reviewable PR body: diff + source timestamps, nothing else. */
export function renderPrBody({ tsl, lotl, meta, now }) {
	const lines = [
		'## TSL/LOTL trust-bundle refresh',
		'',
		`- Spanish TSL: ${meta.tslUrl} (issued ${tsl.timestamps.issued ?? 'unknown'}, NextUpdate ${tsl.timestamps.nextUpdate ?? 'unknown'})`,
		`- EU LOTL: ${meta.lotlUrl} (issued ${lotl.timestamps.issued ?? 'unknown'}, NextUpdate ${lotl.timestamps.nextUpdate ?? 'unknown'})`,
		`- Refreshed at: ${now}`,
		`- Bundle fetchedAt: ${meta.fetchedAt}, nextUpdate: ${meta.nextUpdate}`,
		'',
		`### Anchors: TSL ${tsl.certs.length} cert(s), LOTL ${lotl.certs.length} cert(s), bundle ${meta.anchors.length} anchor(s)`,
		''
	];
	const known = new Set(meta.anchors);
	for (const fp of tsl.fps) lines.push(`- ${known.has(fp) ? 'KEEP' : 'ADD '} TSL ${fp}`);
	for (const fp of lotl.fps) lines.push(`- ${known.has(fp) ? 'KEEP' : 'ADD '} LOTL ${fp}`);
	for (const fp of meta.anchors) {
		if (!tsl.fps.has(fp) && !lotl.fps.has(fp)) lines.push(`- DROP (absent from both lists) ${fp}`);
	}
	lines.push('', 'Refs #135');
	return lines.join('\n');
}

async function main() {
	const opt = (flag, fallback) => {
		const i = args.indexOf(flag);
		return i >= 0 && args[i + 1] ? args[i + 1] : fallback;
	};
	const meta = loadBundleMeta();
	const now = new Date().toISOString();

	if (args.includes('--check')) {
		const expired = new Date() > new Date(meta.nextUpdate);
		const ageDays = (Date.now() - new Date(meta.fetchedAt).getTime()) / 86_400_000;
		console.log(
			`bundle: ${meta.anchors.length} anchor(s), fetched ${meta.fetchedAt}, nextUpdate ${meta.nextUpdate} — ${expired || ageDays > STALE_AFTER_DAYS ? 'STALE' : 'FRESH'}`
		);
		process.exit(expired || ageDays > STALE_AFTER_DAYS ? 2 : 0);
	}

	const tslXml = opt('--tsl-xml', null) ? readFileSync(opt('--tsl-xml', ''), 'utf8') : await fetchText(opt('--tsl-url', meta.tslUrl ?? TSL_URL));
	const lotlXml = opt('--lotl-xml', null) ? readFileSync(opt('--lotl-xml', ''), 'utf8') : await fetchText(opt('--lotl-url', meta.lotlUrl ?? LOTL_URL));

	const side = (xml) => {
		const certs = extractCertsBase64(xml);
		const fps = new Set(certs.map((b64) => fingerprintSha256(Buffer.from(b64, 'base64'))));
		return { certs, fps, timestamps: extractTimestamps(xml) };
	};
	const tsl = side(tslXml);
	const lotl = side(lotlXml);
	console.log(renderPrBody({ tsl, lotl, meta, now }));
}

if (import.meta.url === `file://${process.argv[1]}`) await main();
