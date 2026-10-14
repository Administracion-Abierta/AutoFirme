/** Trust anchors. Only the PEM is committed; `scripts/fetch-roots.sh` refreshes it from fnmt.es. */

import { FNMT_RACM_PEM } from './roots/fnmt-racm';
import { fromPem } from './asn1';
import { parseCertificate, type Certificate } from './x509';

export const ROOT_PEMS: string[] = [FNMT_RACM_PEM];

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
