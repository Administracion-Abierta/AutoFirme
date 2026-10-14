/**
 * PKCS#12 fixtures for tests — generated on the fly with the `openssl` CLI into a temp directory,
 * so nothing binary is committed. Returns the bytes, the password and openssl's own view of the
 * leaf certificate, to cross-check what our parser reads.
 */

import { execFileSync, spawnSync } from 'node:child_process';
import { mkdtempSync, readFileSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import type { Bytes } from '../lib/asn1';

export const FIXTURE_PIN = '1234';
export const LEAF_COMMON_NAME = 'Ñandú Prueba';

export interface Pkcs12Fixtures {
	dir: string;
	/** Leaf key + its CA cert, AES-256-CBC/PBKDF2-SHA256 (what OpenSSL 3+ and Java 9+ write). */
	modern: Bytes;
	/** Leaf public key in PEM (for openssl signature checks). */
	leafPublicKeyPem: string;
	/** openssl's `notBefore`/`notAfter` for the leaf, as ISO strings. */
	notBefore: string;
	notAfter: string;
	/** Same container written with legacy PBES1 (3DES/RC2) encryption. */
	legacy: Bytes;
	/** Self-signed EC P-256 container. */
	ec: Bytes;
}

function openssl(args: string[], cwd: string): string {
	return execFileSync('openssl', args, { cwd, stdio: 'pipe' }).toString();
}

/** `-dateopt iso_8601` prints `notAfter=2027-10-14 07:15:06Z`. */
function readDates(cwd: string): { notBefore: string; notAfter: string } {
	const lines = openssl(['x509', '-in', 'leaf.pem', '-noout', '-dates', '-dateopt', 'iso_8601'], cwd)
		.trim()
		.split('\n');
	const iso = (line: string) => new Date(line.slice(line.indexOf('=') + 1).replace(' ', 'T')).toISOString();
	return { notBefore: iso(lines[0]), notAfter: iso(lines[1]) };
}

export function createPkcs12Fixtures(): Pkcs12Fixtures {
	const dir = mkdtempSync(join(tmpdir(), 'autofirme-p12-'));
	const exportArgs = (name: string, key: string, cert: string) => [
		'pkcs12',
		'-export',
		'-inkey',
		key,
		'-in',
		cert,
		'-out',
		name,
		'-passout',
		`pass:${FIXTURE_PIN}`
	];

	openssl(
		[
			'req', '-x509', '-newkey', 'rsa:2048', '-keyout', 'leaf.key', '-out', 'leaf.pem',
			'-days', '365', '-nodes', '-sha256', '-utf8', '-subj', `/C=ES/O=AutoFirme/CN=${LEAF_COMMON_NAME}`
		],
		dir
	);
	openssl(
		[
			'req', '-x509', '-newkey', 'rsa:2048', '-keyout', 'ca.key', '-out', 'ca.pem',
			'-days', '365', '-nodes', '-sha256', '-subj', '/C=ES/O=AutoFirme/CN=AutoFirme Test CA'
		],
		dir
	);
	openssl(
		[
			...exportArgs('modern.p12', 'leaf.key', 'leaf.pem'),
			'-certfile', 'ca.pem', '-keypbe', 'AES-256-CBC', '-certpbe', 'AES-256-CBC',
			'-macalg', 'sha256', '-iter', '2048'
		],
		dir
	);
	openssl(
		[...exportArgs('legacy.p12', 'leaf.key', 'leaf.pem'), '-legacy', '-macalg', 'sha1'],
		dir
	);

	openssl(['ecparam', '-genkey', '-name', 'prime256v1', '-noout', '-out', 'ec.key'], dir);
	openssl(
		[
			'req', '-x509', '-key', 'ec.key', '-out', 'ec.pem', '-days', '365', '-sha256',
			'-subj', '/C=ES/O=AutoFirme/CN=EC Prueba'
		],
		dir
	);
	openssl(
		[
			...exportArgs('ec.p12', 'ec.key', 'ec.pem'),
			'-keypbe', 'AES-256-CBC', '-certpbe', 'AES-256-CBC', '-macalg', 'sha256'
		],
		dir
	);

	openssl(['x509', '-in', 'leaf.pem', '-pubkey', '-noout', '-out', 'leaf.pub.pem'], dir);

	return {
		dir,
		modern: new Uint8Array(readFileSync(join(dir, 'modern.p12'))),
		legacy: new Uint8Array(readFileSync(join(dir, 'legacy.p12'))),
		ec: new Uint8Array(readFileSync(join(dir, 'ec.p12'))),
		leafPublicKeyPem: readFileSync(join(dir, 'leaf.pub.pem'), 'utf8'),
		...readDates(dir)
	};
}

/** Feed bytes from a test into openssl (signature verification, for instance). */
export function writeFixtureFile(dir: string, name: string, bytes: Bytes): string {
	const path = join(dir, name);
	writeFileSync(path, bytes);
	return path;
}

/**
 * Verify a `.xsig` with `xmlsec1` — the independent XMLDSig oracle. `trustedPem` is the trust
 * anchor (the fixture leaf is self-signed). Requires `xmlsec1` on `PATH`; not an app dependency.
 */
export function xmlsec1Verify(path: string, trustedPem: string): string {
	const { status, stdout, stderr } = spawnSync(
		'xmlsec1',
		['--verify', '--enabled-key-data', 'x509', '--trusted-pem', trustedPem, path],
		{ encoding: 'utf8' }
	);
	const output = `${stdout}${stderr}`;
	if (status !== 0) throw new Error(`xmlsec1 exit ${status}: ${output.trim()}`);
	return output;
}

export interface CadesFixtures {
	dir: string;
	/** CA-signed leaf (RSA) plus its CA, for `loadSignerSession` — the #5 flow. */
	p12: Bytes;
	/** Same, with an EC P-256 leaf. */
	ecP12: Bytes;
	/** Path to the CA certificate in PEM: the `-CAfile` for `openssl cms -verify`. */
	caPath: string;
	/** Path to the leaf certificate in PEM. */
	leafPath: string;
	/** Path to the document to sign, and its bytes. */
	contentPath: string;
	content: Bytes;
}

/** A CA, a CA-signed RSA leaf and a CA-signed EC leaf, all inside PKCS#12 containers. */
export function createCadesFixtures(content = 'documento de prueba\n'): CadesFixtures {
	const dir = mkdtempSync(join(tmpdir(), 'autofirme-cades-'));
	const exportArgs = (name: string, key: string, cert: string) => [
		'pkcs12', '-export', '-inkey', key, '-in', cert, '-certfile', 'ca.pem', '-out', name,
		'-passout', `pass:${FIXTURE_PIN}`, '-keypbe', 'AES-256-CBC', '-certpbe', 'AES-256-CBC',
		'-macalg', 'sha256'
	];

	openssl(
		[
			'req', '-x509', '-newkey', 'rsa:2048', '-keyout', 'ca.key', '-out', 'ca.pem',
			'-days', '3650', '-nodes', '-sha256', '-subj', '/C=ES/O=AutoFirme/CN=AutoFirme CAdES CA'
		],
		dir
	);
	for (const [name, subject] of [
		['rsa', '/C=ES/O=AutoFirme/CN=Firma CAdES'],
		['ec', '/C=ES/O=AutoFirme/CN=Firma CAdES EC']
	]) {
		const keyType =
			name === 'rsa'
				? ['-newkey', 'rsa:2048']
				: ['-newkey', 'ec', '-pkeyopt', 'ec_paramgen_curve:prime256v1'];
		openssl(['req', ...keyType, '-keyout', `${name}.key`, '-out', `${name}.csr`, '-nodes', '-sha256', '-subj', subject], dir);
		openssl(
			['x509', '-req', '-in', `${name}.csr`, '-CA', 'ca.pem', '-CAkey', 'ca.key', '-CAcreateserial', '-out', `${name}.pem`, '-days', '365', '-sha256'],
			dir
		);
		openssl(exportArgs(`${name}.p12`, `${name}.key`, `${name}.pem`), dir);
	}

	const contentPath = writeFixtureFile(dir, 'content.bin', new TextEncoder().encode(content));
	return {
		dir,
		p12: new Uint8Array(readFileSync(join(dir, 'rsa.p12'))),
		ecP12: new Uint8Array(readFileSync(join(dir, 'ec.p12'))),
		caPath: join(dir, 'ca.pem'),
		leafPath: join(dir, 'rsa.pem'),
		contentPath,
		content: new Uint8Array(readFileSync(contentPath))
	};
}
