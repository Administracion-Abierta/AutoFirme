/** Entry point of the verify use case: bytes in, validity report out. */

import { verifyCades } from './cms';
import { detectFormat } from './detect';
import { detectLevel, type SignatureLevel } from './level';
import { verifyPades } from './pades';
import { verifyXades } from './xades';
import type { Bytes, VerificationReport, VerifyOptions } from './types';

export type { Bytes, Check, CheckId, CheckStatus, Signer, VerificationReport, VerificationStatus, ArtifactFormat, VerifyOptions } from './types';
export { detectFormat, suggestSignFormat, type SignSuggestion } from './detect';
export { extractPadesSignatures, verifyPades } from './pades';
export { detectLevel, levelFromEvidence, type LevelEvidence, type SignatureLevel } from './level';

/**
 * A verification report with the signature level attached. The level rides the same
 * object the engine worker structured-clones to the UI and `autofirma_verify` serialises
 * to MCP clients, so all three surfaces report the same level with no consumer edits
 * (Refs #139). The one exception is MCP's hand-built `dom_unavailable` fallback report
 * in `mcp/src/tools.ts`, which is outside this issue's file scope — follow-up.
 */
export type LeveledReport = VerificationReport & { level: SignatureLevel };

export async function verifyArtifact(bytes: Bytes, options: VerifyOptions = {}): Promise<LeveledReport> {
	const format = detectFormat(bytes);
	try {
		let report: VerificationReport;
		switch (format) {
			case 'cades':
				report = await verifyCades(bytes, options);
				break;
			case 'xades':
				report = await verifyXades(bytes, options);
				break;
			case 'pdf':
				report = await verifyPades(bytes, options);
				break;
			default:
				return {
					format,
					ok: false,
					status: 'invalid',
					signer: null,
					signingTime: null,
					level: 'unknown',
					checks: [
						{
							id: 'structure',
							status: 'fail',
							detail: 'not a recognised signed artifact (CMS SignedData, XMLDSig or PDF expected)'
						}
					]
				};
		}
		return { ...report, level: detectLevel(report.format, bytes, report.checks) };
	} catch (error) {
		// Malformed input (e.g. invalid base64 in an XMLDSig element) must surface as a
		// red structure card with the reason, never as an unhandled rejection.
		return {
			format,
			ok: false,
			status: 'invalid',
			signer: null,
			signingTime: null,
			level: 'unknown',
			checks: [{ id: 'structure', status: 'fail', detail: `verification aborted: ${(error as Error).message}` }]
		};
	}
}
