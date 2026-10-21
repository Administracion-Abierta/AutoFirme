// Cross-checks a signature produced in the browser with an independent tool.
// Nothing here trusts the extension's own verdict: OpenSSL re-derives it.
import {spawnSync} from 'node:child_process';
import {readFileSync, writeFileSync} from 'node:fs';
import {join} from 'node:path';

function openssl(args) {
  const result = spawnSync('openssl', args, {encoding: 'utf8'});
  return {status: result.status, stdout: result.stdout ?? '', stderr: result.stderr ?? ''};
}

export function opensslVersion() {
  const {status, stdout, stderr} = openssl(['version']);
  if (status !== 0) throw Error(`openssl is required by the demo: ${stderr.trim()}`);
  return stdout.trim();
}

/**
 * Verify a CAdES signature with OpenSSL against scratch files in `directory`.
 *
 * @param {object} options
 * @param {string} options.directory Scratch directory for the files OpenSSL reads.
 * @param {Buffer} options.signature Raw CAdES (DER) exactly as the website received it.
 * @param {string} options.certificate PEM of the signer certificate.
 * @param {Buffer} [options.detachedContent] Original bytes, required for `mode=explicit`.
 * @returns {{verified: boolean, content: Buffer|null, error: string}}
 */
export function verifyCades({directory, signature, certificate, detachedContent}) {
  const signaturePath = join(directory, 'signature.der');
  const certificatePath = join(directory, 'signer.pem');
  const contentOutPath = join(directory, 'recovered.bin');
  writeFileSync(signaturePath, signature);
  writeFileSync(certificatePath, certificate);
  const command = [
    'cms', '-verify', '-cades', '-binary', '-inform', 'DER',
    '-in', signaturePath,
    '-CAfile', certificatePath,
    // The demo certificate is self-issued, so chain trust is not what is under test;
    // what matters is that OpenSSL validates the signed attributes and the digest.
    '-purpose', 'any',
    '-out', contentOutPath,
  ];
  if (detachedContent) {
    writeFileSync(join(directory, 'original.bin'), detachedContent);
    command.push('-content', join(directory, 'original.bin'));
  }
  const {status, stderr} = openssl(command);
  let content = null;
  try {
    content = readFileSync(contentOutPath);
  } catch {
    content = null;
  }
  return {verified: status === 0, content, error: stderr.trim()};
}

/**
 * Verify the signature embedded in a signed PDF, using only its `/ByteRange` and `/Contents`.
 *
 * @param {object} options
 * @param {string} options.directory Scratch directory for the files OpenSSL reads.
 * @param {Buffer} options.pdf The whole signed PDF exactly as the website received it.
 * @param {string} options.certificate PEM of the signer certificate.
 * @returns {{verified: boolean, content: Buffer|null, error: string}}
 */
export function verifyPades({directory, pdf, certificate}) {
  const text = pdf.toString('latin1');
  const ranges = [...text.matchAll(/\/ByteRange\s*\[\s*(\d+)\s+(\d+)\s+(\d+)\s+(\d+)\s*\]/g)].at(-1);
  const contents = [...text.matchAll(/\/Contents\s*<([a-fA-F0-9]+)>/g)].at(-1);
  if (!ranges || !contents) return {verified: false, content: null, error: 'no PDF signature found'};
  const [start, length, restStart, restLength] = ranges.slice(1).map(Number);
  const signaturePath = join(directory, 'pdf-signature.der');
  const contentPath = join(directory, 'pdf-content.bin');
  const contentOutPath = join(directory, 'pdf-recovered.bin');
  const certificatePath = join(directory, 'signer.pem');
  // The excluded `/Contents` hex string is exactly what the signature covers.
  writeFileSync(signaturePath, Buffer.from(contents[1], 'hex'));
  writeFileSync(contentPath, Buffer.concat([pdf.subarray(start, start + length), pdf.subarray(restStart, restStart + restLength)]));
  writeFileSync(certificatePath, certificate);
  const {status, stderr} = openssl([
    'cms', '-verify', '-cades', '-binary', '-inform', 'DER',
    '-in', signaturePath, '-content', contentPath, '-CAfile', certificatePath,
    '-purpose', 'any', '-out', contentOutPath,
  ]);
  let content = null;
  try {
    content = readFileSync(contentOutPath);
  } catch {
    content = null;
  }
  return {verified: status === 0, content, error: stderr.trim()};
}