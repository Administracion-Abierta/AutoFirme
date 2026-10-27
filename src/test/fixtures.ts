/**
 * PKCS#12 fixtures for tests — generated on the fly with the `openssl` CLI into a temp directory,
 * so nothing binary is committed. Returns the bytes, the password and openssl's own view of the
 * leaf certificate, to cross-check what our parser reads.
 */

import { execFileSync, spawnSync } from 'node:child_process';
import { createHash } from 'node:crypto';
import { mkdtempSync, readFileSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { parseDer, type Bytes, type Der } from '../lib/asn1';
import { toBytes } from '../lib/der';

/**
 * How long the asynchronous UI waits give WebCrypto under jsdom. A PKCS#12 load (PBKDF2 + AES +
 * RSA import), a 250k-iteration seal or a signing round trip take anywhere between a few hundred
 * milliseconds and a couple of seconds on a loaded machine — past testing-library's 1 s default,
 * which made whole test files flake without any behaviour changing.
 */
export const ASYNC = { timeout: 15_000 };

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
	/** Two self-signed certificates that share one serial number. */
	dupSerial: Bytes;
	/**
	 * Self-signed certificate whose whole validity window is in the past (2024 → 2025) — the
	 * expired-but-still-signs case of #40. Needs OpenSSL 3.4+ for `-not_before`/`-not_after`.
	 */
	expired: Bytes;
	/** `notAfter` of `expired`, as the certificate states it. */
	expiredNotAfter: string;
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
	// Two self-signed certificates sharing one serial number (RFC 5280 only requires serials
	// to be unique per issuer) — the picker's keyed list must not crash on them.
	openssl(
		[
			'req', '-x509', '-key', 'leaf.key', '-out', 'dupa.pem', '-days', '365', '-sha256',
			'-set_serial', '0x01', '-subj', '/C=ES/O=AutoFirme/CN=Dup Serial Uno'
		],
		dir
	);
	openssl(
		[
			'req', '-x509', '-newkey', 'rsa:2048', '-nodes', '-keyout', 'dupb.key', '-out', 'dupb.pem',
			'-days', '365', '-sha256', '-set_serial', '0x01', '-subj', '/C=ES/O=AutoFirme/CN=Dup Serial Dos'
		],
		dir
	);
	openssl(
		[
			...exportArgs('dup.p12', 'leaf.key', 'dupa.pem'),
			'-certfile', 'dupb.pem', '-keypbe', 'AES-256-CBC', '-certpbe', 'AES-256-CBC',
			'-macalg', 'sha256'
		],
		dir
	);

	// A self-signed certificate whose window is entirely in the past (#40): loading must flag it
	// and still hand out a usable signing key.
	openssl(
		[
			'req', '-x509', '-newkey', 'rsa:2048', '-keyout', 'expired.key', '-out', 'expired.pem',
			'-nodes', '-sha256', '-utf8', '-subj', '/C=ES/O=AutoFirme/CN=Caducada Prueba',
			'-not_before', '20240101000000Z', '-not_after', '20250101000000Z'
		],
		dir
	);
	openssl([...exportArgs('expired.p12', 'expired.key', 'expired.pem'), '-keypbe', 'AES-256-CBC', '-certpbe', 'AES-256-CBC', '-macalg', 'sha256'], dir);

	openssl(['x509', '-in', 'leaf.pem', '-pubkey', '-noout', '-out', 'leaf.pub.pem'], dir);

	return {
		dir,
		modern: new Uint8Array(readFileSync(join(dir, 'modern.p12'))),
		legacy: new Uint8Array(readFileSync(join(dir, 'legacy.p12'))),
		dupSerial: new Uint8Array(readFileSync(join(dir, 'dup.p12'))),
		ec: new Uint8Array(readFileSync(join(dir, 'ec.p12'))),
		expired: new Uint8Array(readFileSync(join(dir, 'expired.p12'))),
		expiredNotAfter: new Date(
			openssl(['x509', '-in', 'expired.pem', '-noout', '-enddate', '-dateopt', 'iso_8601'], dir)
				.trim()
				.slice('notAfter='.length)
				.replace(' ', 'T')
		).toISOString(),
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
 * Verify a `.xsig` — or any XMLDSig document — with `xmlsec1`, the independent XMLDSig verifier.
 * `trustedPem` is the trust anchor (the fixture leaf is self-signed). Requires `xmlsec1` on
 * `PATH`; not an app dependency.
 *
 * `cwd` matters when the signature carries relative `Reference` URIs (the ODF package refs are
 * package-relative): xmlsec1 resolves them against the process working directory, not against the
 * signature file's location, so an ODF package must be verified from its own root.
 *
 * Returns the tool's own verdict, not its prose: 1.2.x prints a bare `OK`/`FAIL` line, 1.3.x
 * prefixes it with `status: `, and 1.3.12 prints `Verification status: OK` / `... FAILED`. `ERROR`
 * (a file the tool could not parse) and any other output mean the oracle did not run, which is an
 * error rather than a verdict.
 */
export function xmlsec1Verify(path: string, trustedPem: string, cwd?: string): 'OK' | 'FAIL' {
	const { status, stdout, stderr } = spawnSync(
		'xmlsec1',
		['--verify', '--enabled-key-data', 'x509', '--trusted-pem', trustedPem, path],
		{ encoding: 'utf8', cwd }
	);
	const output = `${stdout}${stderr}`;
	// xmlsec1 ≤1.3.0 prints `status: OK` / a bare `OK`; 1.3.12 prints `Verification status: OK`
	// (and its failure wording). Same oracle, three shapes — the verdict must still be explicit.
	const verdict =
		/^(?:Verification )?status:\s*(OK|FAIL|FAILED)/m.exec(output)?.[1] ??
		/^(OK|FAIL)$/m.exec(output)?.[1];
	if (!verdict) throw new Error(`xmlsec1 printed no verdict (exit ${status}): ${output.trim()}`);
	return verdict === 'OK' ? 'OK' : 'FAIL';
}

export interface CadesFixtures {
	dir: string;
	/** CA-signed leaf (RSA) plus its CA, for `loadSignerSession` — the #5 flow. */
	p12: Bytes;
	/** Same, with an EC P-256 leaf. */
	ecP12: Bytes;
	/**
	 * CA-signed leaf whose whole validity window is in the past (2024 → 2025). Signing and
	 * verifying still work — the point of #40. Needs OpenSSL 3.4+, which is where `-not_before`/
	 * `-not_after` appear; the CLI here is 4.0.
	 */
	expiredP12: Bytes;
	/** `notAfter` of that leaf, as the certificate states it. */
	expiredNotAfter: string;
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
	// An expired leaf, signed by the same CA: the certificate is validly issued, it is simply
	// outside its window. `-set_serial` instead of a second `-CAcreateserial`, which refuses to
	// overwrite the serial file the loop already made.
	openssl(
		[
			'req', '-newkey', 'rsa:2048', '-keyout', 'expired.key', '-out', 'expired.csr', '-nodes',
			'-sha256', '-subj', '/C=ES/O=AutoFirme/CN=Firma Caducada'
		],
		dir
	);
	openssl(
		[
			'x509', '-req', '-in', 'expired.csr', '-CA', 'ca.pem', '-CAkey', 'ca.key',
			'-set_serial', '0x40', '-out', 'expired.pem', '-sha256',
			'-not_before', '20240101000000Z', '-not_after', '20250101000000Z'
		],
		dir
	);
	openssl(exportArgs('expired.p12', 'expired.key', 'expired.pem'), dir);

	return {
		dir,
		p12: new Uint8Array(readFileSync(join(dir, 'rsa.p12'))),
		ecP12: new Uint8Array(readFileSync(join(dir, 'ec.p12'))),
		expiredP12: new Uint8Array(readFileSync(join(dir, 'expired.p12'))),
		expiredNotAfter: new Date(
			openssl(['x509', '-in', 'expired.pem', '-noout', '-enddate', '-dateopt', 'iso_8601'], dir)
				.trim()
				.slice('notAfter='.length)
				.replace(' ', 'T')
		).toISOString(),
		caPath: join(dir, 'ca.pem'),
		leafPath: join(dir, 'rsa.pem'),
		contentPath,
		content: new Uint8Array(readFileSync(contentPath))
	};
}

export interface TsaFixtures {
	dir: string;
	/** Path to the (self-signed) TSA certificate: the trust anchor for `openssl ts -verify`. */
	certPath: string;
	/** Answer a `TimeStampReq` with a real `TimeStampResp`, signed by that certificate. */
	respond: (query: Uint8Array) => Bytes;
}

/**
 * A throwaway RFC 3161 TSA: a self-signed timestamping certificate plus an `openssl ts` config, so
 * a test can hand a client's own `TimeStampReq` to a real TSA implementation and get a real
 * `TimeStampResp` back — no network, no committed TSA key.
 */
export function createTsaFixtures(): TsaFixtures {
	const dir = mkdtempSync(join(tmpdir(), 'autofirme-tsa-'));
	openssl(
		[
			'req', '-x509', '-newkey', 'rsa:2048', '-keyout', 'tsa.key', '-out', 'tsa.pem',
			'-days', '3650', '-nodes', '-sha256', '-subj', '/C=ES/O=AutoFirme/CN=AutoFirme Test TSA',
			'-addext', 'basicConstraints=critical,CA:FALSE',
			'-addext', 'keyUsage=critical,digitalSignature',
			'-addext', 'extendedKeyUsage=critical,timeStamping'
		],
		dir
	);
	writeFileSync(
		join(dir, 'tsa.cnf'),
		[
			'[tsa_config]',
			'serial = serial',
			'signer_cert = tsa.pem',
			'signer_key = tsa.key',
			'signer_digest = sha256',
			'default_policy = 1.3.6.1.4.1.13762.3',
			'digests = sha256, sha384, sha512',
			'certs = tsa.pem',
			'accuracy = secs:1',
			'ordering = yes',
			'tsa_name = yes',
			'ess_cert_id_chain = no',
			''
		].join('\n')
	);
	writeFileSync(join(dir, 'serial'), '01\n');

	return {
		dir,
		certPath: join(dir, 'tsa.pem'),
		respond: (query) => {
			writeFileSync(join(dir, 'query.tsq'), query);
			openssl(
				[
					'ts', '-reply', '-config', 'tsa.cnf', '-section', 'tsa_config',
					'-queryfile', 'query.tsq', '-out', 'reply.tsr'
				],
				dir
			);
			return new Uint8Array(readFileSync(join(dir, 'reply.tsr')));
		}
	};
}

/** Walk a parsed DER tree; throws instead of returning `undefined` on an unexpected shape. */
function derAt(node: Der, path: readonly number[]): Der {
	let current = node;
	for (const index of path) {
		const next: Der | undefined = current.children[index];
		if (!next) throw new Error(`no DER child ${index} here`);
		current = next;
	}
	return current;
}

/**
 * The `SignerInfo.signature` value and the RFC 3161 `id-aa-timeStampToken` a CMS signature carries
 * (`ContentInfo > SignedData > signerInfos > SignerInfo > unsignedAttrs`). `cms` must end at the
 * DER structure — trim the `/Contents` zero padding first.
 */
export function cmsTimeStamp(cms: Uint8Array): { signature: Bytes; token: Bytes } {
	const signedData = derAt(parseDer(toBytes(cms)), [1, 0]);
	const signerInfo = derAt(signedData, [4, 0]);
	return {
		signature: derAt(signerInfo, [5]).value,
		// unsignedAttrs [1] > Attribute > attrValues SET > the token, complete with its own header.
		token: derAt(signerInfo, [6, 0, 1, 0]).raw
	};
}

/** `openssl ts -verify` of a token against a signature value — the TSA-side oracle. */
export function tsVerifyToken(
	dir: string,
	certPath: string,
	token: Bytes,
	signature: Bytes
): string {
	const tokenPath = join(dir, 'token.p7');
	writeFileSync(tokenPath, token);
	const digest = createHash('sha256').update(signature).digest('hex');
	const result = spawnSync(
		'openssl',
		['ts', '-verify', '-in', tokenPath, '-token_in', '-digest', digest, '-CAfile', certPath],
		{ encoding: 'utf8' }
	);
	return `${result.stdout}${result.stderr}`;
}
