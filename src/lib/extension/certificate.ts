import { oid, parseDer } from '../asn1';
import type { SignerCert } from '../sign';
import { certificateIdentity } from '../x509';

export type CertificateProblem = 'expired' | 'notYetValid' | 'privateKey' | 'rsa' | 'invalid' | 'ca' | 'keyUsage';

/**
 * Reject a certificate before signing, in both the chooser and the MCP auto-sign path.
 * Kept out of `sign-request.ts` so the service worker does not bundle PAdES/XAdES to use it.
 */
export function certificateProblem(cert: SignerCert, now = Date.now()): CertificateProblem | null {
  if (!Number.isFinite(cert.notBefore.getTime()) || !Number.isFinite(cert.notAfter.getTime())) return 'invalid';
  if (cert.notAfter.getTime() <= now) return 'expired';
  if (cert.notBefore.getTime() > now) return 'notYetValid';
  if (!cert.key || cert.key.type !== 'private' || !cert.key.usages.includes('sign')) return 'privateKey';
  const keyAlgorithm = cert.key.algorithm as RsaHashedKeyAlgorithm;
  if (cert.algorithm?.name !== 'RSASSA-PKCS1-v1_5' || cert.algorithm.hash !== 'SHA-256' || keyAlgorithm.name !== 'RSASSA-PKCS1-v1_5' || (!Number.isInteger(keyAlgorithm.modulusLength) || keyAlgorithm.modulusLength < 2048)) return 'rsa';
  try {
    certificateIdentity(cert.der);
    const tbs = parseDer(cert.der).children[0];
    const extensions = tbs.children.find((node) => node.tag === 0xa3)?.children[0].children ?? [];
    for (const extension of extensions) {
      const id = oid(extension.children[0]);
      const value = parseDer(extension.children.at(-1)!.value);
      if (id === '2.5.29.19' && value.children[0]?.tag === 0x01 && value.children[0].value[0] !== 0) return 'ca';
      if (id === '2.5.29.15' && (value.tag !== 0x03 || !((value.value[1] ?? 0) & 0xc0))) return 'keyUsage';
    }
  } catch { return 'invalid'; }
  return null;
}
