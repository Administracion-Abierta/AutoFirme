/** Entry point of the verify use case: bytes in, validity report out. */

import { verifyCades } from './cms';
import { detectFormat } from './detect';
import { verifyXades } from './xades';
import type { Bytes, VerificationReport, VerifyOptions } from './types';

export type { Bytes, Check, CheckId, CheckStatus, Signer, VerificationReport, ArtifactFormat, VerifyOptions } from './types';
export { detectFormat } from './detect';

export async function verifyArtifact(bytes: Bytes, options: VerifyOptions = {}): Promise<VerificationReport> {
	switch (detectFormat(bytes)) {
		case 'cades':
			return verifyCades(bytes, options);
		case 'xades':
			return verifyXades(bytes, options);
		case 'pdf':
			return {
				format: 'pdf',
				ok: false,
				signer: null,
				signingTime: null,
				checks: [
					{
						id: 'unsupported',
						status: 'info',
						detail: 'PDF/PAdES signatures are not verified yet — tracked by issue #11'
					}
				]
			};
		default:
			return {
				format: 'unknown',
				ok: false,
				signer: null,
				signingTime: null,
				checks: [
					{
						id: 'structure',
						status: 'fail',
						detail: 'not a recognised signed artifact (CMS SignedData, XMLDSig or PDF expected)'
					}
				]
			};
	}
}
