/** Entry point of the verify use case: bytes in, validity report out. */

import { verifyCades } from './cms';
import { detectFormat } from './detect';
import { verifyPades } from './pades';
import { verifyXades } from './xades';
import type { Bytes, VerificationReport, VerifyOptions } from './types';

export type { Bytes, Check, CheckId, CheckStatus, Signer, VerificationReport, VerificationStatus, ArtifactFormat, VerifyOptions } from './types';
export { detectFormat } from './detect';
export { extractPadesSignatures, verifyPades } from './pades';

export async function verifyArtifact(bytes: Bytes, options: VerifyOptions = {}): Promise<VerificationReport> {
	const format = detectFormat(bytes);
	try {
		switch (format) {
			case 'cades':
				return await verifyCades(bytes, options);
			case 'xades':
				return await verifyXades(bytes, options);
			case 'pdf':
				return await verifyPades(bytes, options);
			default:
				return {
					format,
					ok: false,
					status: 'invalid',
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
	} catch (error) {
		// Malformed input (e.g. invalid base64 in an XMLDSig element) must surface as a
		// red structure card with the reason, never as an unhandled rejection.
		return {
			format,
			ok: false,
			status: 'invalid',
			signer: null,
			signingTime: null,
			checks: [{ id: 'structure', status: 'fail', detail: `verification aborted: ${(error as Error).message}` }]
		};
	}
}
