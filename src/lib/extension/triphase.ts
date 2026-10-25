import type { SignerCert } from '../sign';
import type { Bytes } from '../asn1';
import type { ExtensionRequest } from './runtime';

/**
 * AutoFirma's three-phase (triphase) CAdES protocol, as used by Aragon's portafirmas.
 *
 * The page names its own signing server. That server builds the CAdES structure, returns
 * the signed attributes that still need the private key, receives the PKCS#1 signature
 * back, and then builds the final container. The document never leaves the server; only
 * the selected certificate and a signature travel there.
 */
const MAX_SERVER_RESPONSE = 8 * 1024 * 1024;

function urlSafeBase64(bytes: Bytes): string {
	let raw = '';
	for (let offset = 0; offset < bytes.length; offset += 8192) {
		raw += String.fromCharCode(...bytes.subarray(offset, offset + 8192));
	}
	return btoa(raw).replace(/\+/g, '-').replace(/\//g, '_');
}

/** The server mixes standard and URL-safe alphabets; Java's decoder tolerates either. */
function decodeServerBase64(value: string): Bytes {
	const compact = value.replace(/\s+/g, '').replace(/-/g, '+').replace(/_/g, '/').replace(/=+$/, '');
	if (!compact || !/^[A-Za-z0-9+/]+$/.test(compact) || compact.length % 4 === 1) {
		throw new Error('The signing server returned invalid base64');
	}
	const padded = compact + '='.repeat((4 - (compact.length % 4)) % 4);
	const raw = atob(padded);
	return Uint8Array.from(raw, (character) => character.charCodeAt(0));
}

function endpoint(server: string, values: Record<string, string>): string {
	return `${server}${server.includes('?') ? '&' : '?'}${new URLSearchParams(values)}`;
}

async function callServer(server: string, values: Record<string, string>): Promise<string> {
	const response = await fetch(endpoint(server, values), {
		method: 'POST',
		cache: 'no-store',
		credentials: 'omit',
		referrerPolicy: 'no-referrer'
	});
	const bytes = new Uint8Array(await response.arrayBuffer());
	if (!response.ok) throw new Error(`The signing server returned HTTP ${response.status}`);
	if (bytes.length > MAX_SERVER_RESPONSE) throw new Error('The signing server response is too large');
	const text = new TextDecoder().decode(bytes).trim();
	if (text.startsWith('ERR-')) throw new Error(text.slice(0, 300));
	return text;
}

function transportKey(cert: SignerCert, algorithm: string): CryptoKey {
	if (cert.algorithm?.name !== 'RSASSA-PKCS1-v1_5') throw new Error('The signing certificate is not RSA');
	if (algorithm === 'SHA512withRSA') {
		if (!cert.sha512Key) throw new Error('This certificate cannot sign with SHA-512');
		return cert.sha512Key;
	}
	if (!cert.key) throw new Error('The signing certificate has no private key');
	return cert.key;
}

export async function signCadesTriPhase(
	request: ExtensionRequest,
	cert: SignerCert,
	chain: readonly SignerCert[],
	content: Bytes
): Promise<Bytes> {
	const { serverUrl, algorithm } = request;
	if (!serverUrl || !algorithm) throw new Error('Incomplete three-phase request');
	const key = transportKey(cert, algorithm);
	const certificates = [
		cert,
		...(request.params?.includeOnlySignningCertificate === 'true'
			? []
			: chain.filter((entry) => entry.id !== cert.id))
	];
	// AutoFirma sends the signing certificate first and the rest of the chain after it.
	const certParam = certificates.map((entry) => urlSafeBase64(entry.der)).join(',');
	const doc = urlSafeBase64(content);
	const entries = Object.entries(request.params ?? {});
	const properties = entries.length
		? urlSafeBase64(
				new TextEncoder().encode(`${entries.map(([name, value]) => `${name}=${value}`).join('\n')}\n`)
			)
		: null;
	const base = {
		cop: 'sign',
		format: 'CAdES',
		algo: algorithm,
		cert: certParam,
		...(properties ? { params: properties } : {})
	};

	const preResponse = decodeServerBase64(
		await callServer(serverUrl, { op: 'pre', ...base, doc })
	);
	const document = new DOMParser().parseFromString(
		new TextDecoder().decode(preResponse),
		'application/xml'
	);
	if (document.getElementsByTagName('parsererror').length) {
		throw new Error('The signing server returned invalid XML');
	}
	const firms = [...document.querySelectorAll('firmas > firma')];
	if (firms.length === 0) throw new Error('The signing server returned no signature to complete');

	for (const firma of firms) {
		const params = [...firma.children].filter((child) => child.tagName === 'param');
		const pre = params.find((child) => child.getAttribute('n') === 'PRE');
		if (!pre) throw new Error('The signing server returned no data to sign');
		const preBytes = decodeServerBase64(pre.textContent ?? '');
		const signature = new Uint8Array(
			await crypto.subtle.sign({ name: 'RSASSA-PKCS1-v1_5' }, key, preBytes)
		);
		let pk1 = params.find((child) => child.getAttribute('n') === 'PK1');
		if (!pk1) {
			pk1 = document.createElement('param');
			pk1.setAttribute('n', 'PK1');
			firma.appendChild(pk1);
		}
		pk1.textContent = urlSafeBase64(signature);
		const needsPre = params
			.find((child) => child.getAttribute('n') === 'NEED_PRE')
			?.textContent?.trim()
			.toLowerCase();
		if (needsPre !== 'true') pre.remove();
	}

	const session = urlSafeBase64(new TextEncoder().encode(new XMLSerializer().serializeToString(document)));
	const postResponse = await callServer(serverUrl, { op: 'post', ...base, session });
	const prefix = 'OK NEWID=';
	if (!postResponse.startsWith(prefix)) {
		throw new Error('The signing server did not return a signature');
	}
	return decodeServerBase64(postResponse.slice(prefix.length).split(/\s+/)[0]);
}
