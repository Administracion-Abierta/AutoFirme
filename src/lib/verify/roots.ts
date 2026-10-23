/**
 * Trust anchors bundled at build time (Refs #135). Today the bundle holds one
 * anchor, AC RAIZ FNMT-RCM, fetched from fnmt.es by `scripts/fetch-roots.sh`;
 * `scripts/refresh-tsl.mjs` diffs the Spanish TSL + EU LOTL against it on a
 * schedule and proposes the trust-service certs to add. Compiled in: the
 * signing-time path makes zero network requests and resolves chains offline via
 * `trustedRoots()`. Only the PEMs are trust-relevant; the metadata below is
 * provenance for review and for `/diagnostics`.
 */
import { FNMT_RACM_PEM } from './roots/fnmt-racm';
import { fromPem } from './asn1';
import { parseCertificate, type Certificate } from './x509';

export const ROOT_PEMS: string[] = [FNMT_RACM_PEM];

/** One bundled anchor: enough to review a refresh PR without opening the PEM. */
export interface TrustAnchorMeta {
	subject: string;
	fingerprintSha256: string;
	source: 'es-tsl' | 'eu-lotl' | 'fnmt.es';
}

/**
 * Bundle provenance. `fetchedAt` is when the TSL/LOTL were last pulled;
 * `nextUpdate` is the TSL `NextUpdate` when known, else `fetchedAt` + 180 days.
 * (ponytail: record the real TSL NextUpdate per refresh once the refresh job
 * runs with network; the 180-day ceiling only bounds staleness meanwhile.)
 */
export interface TrustBundleMeta {
	fetchedAt: string;
	nextUpdate: string;
	tslUrl: string;
	lotlUrl: string;
	anchors: TrustAnchorMeta[];
}

export const TRUST_BUNDLE_META: TrustBundleMeta = {
	// Source timestamps observed 2026-10-23: ES TSL issued 2026-09-24, NextUpdate 2027-03-21 (real value, not the 180-day ceiling).
	fetchedAt: '2026-10-23T00:00:00.000Z',
	nextUpdate: '2027-03-21T04:00:00.000Z',
	tslUrl: 'https://tsl.digital.gob.es/TSL.xml',
	lotlUrl: 'https://ec.europa.eu/tools/lotl/eu-lotl.xml',
	anchors: [
		{
			subject: 'C=ES, O=FNMT-RCM, OU=AC RAIZ FNMT-RCM',
			fingerprintSha256:
				'EB:C5:57:0C:29:01:8C:4D:67:B1:AA:12:7B:AF:12:F7:03:B4:61:1E:BC:17:B7:DA:B5:57:38:94:17:9B:93:FA',
			source: 'fnmt.es'
		}
	]
};

/** Offline bundle health for `/diagnostics`: expired is reported, never silent. */
export interface BundleStatus {
	expired: boolean;
	anchorCount: number;
	fetchedAt: string;
	nextUpdate: string;
	detail: string;
}

/** Pure/offline: `expired` is true when `now` is past `nextUpdate`. */
export function bundleStatus(now: Date = new Date(), meta: TrustBundleMeta = TRUST_BUNDLE_META): BundleStatus {
	const expired = now > new Date(meta.nextUpdate);
	return {
		expired,
		anchorCount: meta.anchors.length,
		fetchedAt: meta.fetchedAt,
		nextUpdate: meta.nextUpdate,
		detail: expired
			? `trust bundle expired ${meta.nextUpdate} (fetched ${meta.fetchedAt}) — trust is unknown until refreshed`
			: `trust bundle fresh: ${meta.anchors.length} anchor(s), fetched ${meta.fetchedAt}, next update ${meta.nextUpdate}`
	};
}

/** Trust anchors as parsed certificates; unknown/unparsable PEM blocks are skipped. */
export function trustedRoots(pems: string[] = ROOT_PEMS): Certificate[] {
	const out: Certificate[] = [];
	for (const pem of pems) {
		for (const der of fromPem(pem)) {
			try {
				out.push(parseCertificate(der));
			} catch {
				// A PEM block that is not a certificate cannot anchor anything.
			}
		}
	}
	return out;
}
