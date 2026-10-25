// A throwaway signing identity for the demo.
//
// It is generated per run, never committed and never reused: self-issued, two days
// long, and explicitly marked as a non-production fixture. Its only job is to let the
// extension produce a real signature that OpenSSL can check afterwards.
import {spawnSync} from 'node:child_process';
import {readFileSync} from 'node:fs';
import {join} from 'node:path';

export const DEFAULT_PIN = 'demo-password';

const SUBJECT = '/C=ES/O=Local non-production demo fixture/CN=AutoFirme Demo Test';
const CERT_NAME = 'AutoFirme Demo Test';

function openssl(args) {
  const result = spawnSync('openssl', args, {encoding: 'utf8'});
  if (result.status !== 0) throw Error(`openssl ${args[0]} failed: ${(result.stderr ?? '').trim()}`);
  return result.stdout;
}

/**
 * Create a self-signed RSA certificate plus its PKCS#12 container in `directory`.
 *
 * @param {string} directory Scratch directory.
 * @param {string} [pin] PIN protecting the PKCS#12 container.
 * @returns {{pkcs12: string, certificate: string, subject: string, pin: string, subjectName: string}}
 */
export function createTestIdentity(directory, pin = DEFAULT_PIN) {
  const keyPath = join(directory, 'demo.key');
  const certificatePath = join(directory, 'demo.pem');
  const pkcs12Path = join(directory, 'demo.p12');
  openssl([
    'req', '-x509', '-newkey', 'rsa:2048', '-nodes', '-sha256', '-days', '2',
    '-subj', SUBJECT,
    // The chooser refuses CA certificates and keys without a signing usage.
    '-addext', 'basicConstraints=critical,CA:FALSE',
    '-addext', 'keyUsage=critical,digitalSignature,nonRepudiation',
    '-keyout', keyPath, '-out', certificatePath,
  ]);
  openssl(['pkcs12', '-export', '-inkey', keyPath, '-in', certificatePath, '-passout', `pass:${pin}`, '-out', pkcs12Path]);
  return {
    pkcs12: pkcs12Path,
    certificate: readFileSync(certificatePath, 'utf8'),
    subject: CERT_NAME,
    subjectName: CERT_NAME,
    pin,
  };
}