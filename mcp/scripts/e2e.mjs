#!/usr/bin/env node
/**
 * End-to-end check of the stdio server, driven the way an agent drives it: a real MCP client over
 * stdio, a throwaway PKCS#12 container, and `openssl cms -verify` as the independent oracle.
 *
 * A signature the server produces must verify under openssl; a document tampered with afterwards
 * must not. Requires `openssl` on PATH and a build (`pnpm build`).
 */

import { execFileSync } from 'node:child_process';
import { existsSync, mkdirSync, mkdtempSync, readFileSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';
import { Client } from '@modelcontextprotocol/sdk/client/index.js';
import { StdioClientTransport } from '@modelcontextprotocol/sdk/client/stdio.js';

const PIN = '1234';
const SIGNING_TIME = '2026-10-14T09:00:00.000Z';
const here = dirname(fileURLToPath(import.meta.url));
const server = join(here, '..', 'dist', 'server.js');
const dir = mkdtempSync(join(tmpdir(), 'autofirme-mcp-e2e-'));
const certDir = join(dir, 'certs');
const workspace = join(dir, 'workspace');
const auditLog = join(dir, 'audit.jsonl');

function check(condition, message) {
	if (!condition) throw new Error(`e2e: ${message}`);
	return true;
}

const openssl = (...args) => execFileSync('openssl', args, { cwd: dir, stdio: 'pipe' }).toString();

/** A CA, a CA-signed leaf in a `.p12`, and the document to sign — all throwaway. */
function fixture() {
	mkdirSync(certDir);
	mkdirSync(workspace);
	openssl(
		'req', '-x509', '-newkey', 'rsa:2048', '-keyout', 'ca.key', '-out', 'ca.pem',
		'-days', '1', '-nodes', '-sha256', '-subj', '/C=ES/O=AutoFirme/CN=AutoFirme MCP e2e CA'
	);
	openssl(
		'req', '-newkey', 'rsa:2048', '-keyout', 'leaf.key', '-out', 'leaf.csr',
		'-nodes', '-sha256', '-subj', '/C=ES/O=AutoFirme/CN=Firma MCP e2e'
	);
	openssl('x509', '-req', '-in', 'leaf.csr', '-CA', 'ca.pem', '-CAkey', 'ca.key', '-CAcreateserial',
		'-out', 'leaf.pem', '-days', '1', '-sha256');
	openssl('pkcs12', '-export', '-inkey', 'leaf.key', '-in', 'leaf.pem', '-certfile', 'ca.pem',
		'-out', join(certDir, 'e2e.p12'), '-passout', `pass:${PIN}`,
		'-keypbe', 'AES-256-CBC', '-certpbe', 'AES-256-CBC', '-macalg', 'sha256');
	const content = Buffer.from('documento de prueba e2e\n');
	writeFileSync(join(workspace, 'documento.txt'), content);
	writeFileSync(join(dir, 'alterado.txt'), Buffer.concat([content, Buffer.from('alterado\n')]));
	return content;
}

/**
 * `openssl cms -verify` on a detached signature: throws when the signature does not check out, and
 * returns the content openssl recovered from it — which must be the signed document, byte for byte.
 */
function opensslVerify(documentPath, outPath) {
	execFileSync('openssl', ['cms', '-verify', '-binary', '-inform', 'DER', '-in', join(dir, 'firma.csig'),
		'-content', documentPath, '-CAfile', join(dir, 'ca.pem'), '-out', outPath], { cwd: dir, stdio: 'pipe' });
	return readFileSync(outPath);
}

const content = fixture();
check(existsSync(server), `build the server first: ${server} is missing`);

const client = new Client({ name: 'autofirme-mcp-e2e', version: '0.0.0' });
await client.connect(
	new StdioClientTransport({
		command: process.execPath,
		args: [server],
		env: {
			...process.env,
			AUTOFIRME_MCP_CERTS: certDir,
			AUTOFIRME_MCP_PIN: PIN,
			AUTOFIRME_MCP_WORKSPACE: workspace,
			AUTOFIRME_MCP_TRUSTED_ROOTS: join(dir, 'ca.pem'),
			AUTOFIRME_MCP_AUDIT_LOG: auditLog
		}
	})
);

const text = (result) => result.content.find((part) => part.type === 'text')?.text ?? '';
/** Call a tool and return its JSON payload, converting an `isError` answer into a throw. */
async function call(name, args) {
	const result = await client.callTool({ name, arguments: args });
	check(!result.isError, `${name} failed: ${text(result)}`);
	return JSON.parse(text(result));
}

try {
	const tools = (await client.listTools()).tools.map((tool) => tool.name).sort();
	check(
		JSON.stringify(tools) === JSON.stringify(['autofirma_list_certs', 'autofirma_sign', 'autofirma_verify']),
		`unexpected tool list: ${tools.join(', ')}`
	);

	const certs = await call('autofirma_list_certs', {});
	check(certs.signing_ready, 'signing_ready is false — the container did not load');
	check(!/PRIVATE KEY/.test(JSON.stringify(certs)), 'list_certs leaked key material');
	const certId = certs.certificates.find((cert) => cert.signable).cert_id;
	check(certId, 'no signable certificate came back from list_certs');

	const signed = await call('autofirma_sign', {
		path: 'documento.txt',
		format: 'cades',
		cert_id: certId,
		signing_time: SIGNING_TIME
	});
	check(signed.detached === true, 'a CAdES-BES signature must be reported as detached');
	check(!/PRIVATE KEY/.test(JSON.stringify(signed)), 'sign leaked key material');
	writeFileSync(join(dir, 'firma.csig'), Buffer.from(signed.signed_b64, 'base64'));

	// The oracle: an independent implementation accepts the signature the server made, and what it
	// recovers from it is the document that was signed.
	const recovered = opensslVerify(join(workspace, 'documento.txt'), join(dir, 'recuperado.txt'));
	check(recovered.equals(content), 'openssl verified the signature but recovered other bytes');

	// …and rejects it over a document tampered with after signing.
	let tamperedAccepted = false;
	try {
		opensslVerify(join(dir, 'alterado.txt'), join(dir, 'recuperado-alterado.txt'));
		tamperedAccepted = true;
	} catch {
		// expected: the content digest no longer matches
	}
	check(!tamperedAccepted, 'openssl verified a tampered document');

	const good = await call('autofirma_verify', { artifact_b64: signed.signed_b64, detached_b64: content.toString('base64') });
	check(good.report.ok === true, `server-side verify rejected its own signature: ${JSON.stringify(good.report.checks)}`);
	const bad = await call('autofirma_verify', {
		artifact_b64: signed.signed_b64,
		detached_b64: readFileSync(join(dir, 'alterado.txt')).toString('base64')
	});
	check(bad.report.ok === false, 'server-side verify accepted a tampered document');

	const refused = await client.callTool({
		name: 'autofirma_sign',
		arguments: { path: 'documento.txt', format: 'cades', cert_id: 'no-existe' }
	});
	check(refused.isError === true, 'an unknown cert_id was accepted');
	check(/"error":"unknown_cert_id"/.test(text(refused)), `wrong error for an unknown cert_id: ${text(refused)}`);

	const events = readFileSync(auditLog, 'utf8').trim().split('\n').map((line) => JSON.parse(line));
	check(
		events.some((event) => event.event === 'sign' && event.ok === true && event.documentSha256),
		'no successful sign event in the audit log'
	);
	check(!readFileSync(auditLog, 'utf8').includes('documento de prueba'), 'the audit log kept document content');

	console.log(
		`e2e ok: server signed ${signed.bytes} bytes with ${certId}; openssl verified it, tampering failed verification`
	);
} finally {
	await client.close();
}
