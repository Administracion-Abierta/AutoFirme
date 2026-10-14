/**
 * C14N byte-compatibility against xmllint (the plan's oracle for `c14n.ts`), covering the
 * inherited-namespace and default-namespace-reset cases the signer's digests depend on.
 */
import { execFileSync } from 'node:child_process';
import { mkdtempSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { describe, expect, it } from 'vitest';
import { canonicalize } from './c14n';

const dir = mkdtempSync(join(tmpdir(), 'c14n-'));
const hasXmllint = (() => {
	try {
		execFileSync('xmllint', ['--version'], { stdio: 'ignore' });
		return true;
	} catch {
		return false;
	}
})();

const samples = [
	// default namespace declared, reset on a prefixed element, unprefixed namespaced child
	'<r xmlns:p="urn:p" xmlns="urn:x"><p:c xmlns=""><n/></p:c></r>',
	// inherited prefixed namespaces over a signature-shaped subtree
	'<ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#"><ds:SignedInfo><ds:Reference URI="#x"/></ds:SignedInfo></ds:Signature>',
	// mixed prefixes, attribute order, default ns on a prefixed root, text escaping
	'<a:b xmlns:a="urn:a" xmlns:z="urn:z" a:x="1" z:y="2" xmlns="urn:d">t&amp;x &lt;y</a:b>'
];

describe('canonicalize', () => {
	it.runIf(hasXmllint)('matches xmllint --c14n and --exc-c14n byte for byte', () => {
		for (const [index, xml] of samples.entries()) {
			const path = join(dir, `sample-${index}.xml`);
			writeFileSync(path, xml);
			const doc = new DOMParser().parseFromString(xml, 'application/xml');
			expect(canonicalize(doc)).toBe(execFileSync('xmllint', ['--c14n', path], { encoding: 'utf8' }));
			expect(canonicalize(doc, true)).toBe(execFileSync('xmllint', ['--exc-c14n', path], { encoding: 'utf8' }));
		}
	});
});
