/**
 * Canonicalization golden vectors.
 *
 * The expected strings were produced by `lxml` (`method="c14n"`, `exclusive=True`,
 * `with_comments=False`) — an implementation independent of this one. The cross-check against a
 * real XMLDSig implementation (`xmlsec1`) lives in `xades.test.ts`, where the canonicalization is
 * verified through the digests it recomputes.
 */

import { execFileSync } from 'node:child_process';
import { mkdtempSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { describe, expect, it } from 'vitest';
import { canonicalize, canonicalizeToString } from './c14n';

function parse(xml: string): Document {
	const doc = new DOMParser().parseFromString(xml, 'application/xml');
	if (doc.getElementsByTagName('parsererror').length !== 0) throw new Error(`bad fixture: ${xml}`);
	return doc;
}

function c14n(xml: string, exclusive = false): string {
	return new TextDecoder().decode(canonicalize(parse(xml), { exclusive }));
}

const VECTORS: Array<{ name: string; xml: string; inclusive: string; exclusive: string }> = [
	{
		name: 'namespace first, attributes sorted by name',
		xml: '<a xmlns:x="urn:x" b="2" a="1"><x:c/>t</a>',
		inclusive: '<a xmlns:x="urn:x" a="1" b="2"><x:c></x:c>t</a>',
		exclusive: '<a a="1" b="2"><x:c xmlns:x="urn:x"></x:c>t</a>'
	},
	{
		name: 'unused prefixes dropped by exclusive c14n, processing instruction kept',
		xml: '<a xmlns="urn:d" xmlns:p="urn:p"><p:b><c/></p:b><!-- c --><?pi hi?></a>',
		inclusive: '<a xmlns="urn:d" xmlns:p="urn:p"><p:b><c></c></p:b><?pi hi?></a>',
		exclusive: '<a xmlns="urn:d"><p:b xmlns:p="urn:p"><c></c></p:b><?pi hi?></a>'
	},
	{
		name: 'default namespace declared, undone and re-declared on a descendant',
		xml: '<r:r xmlns:r="urn:r" xmlns="urn:d"><e xmlns=""><f/></e><g>a&amp;b&lt;c&gt;d</g></r:r>',
		inclusive:
			'<r:r xmlns="urn:d" xmlns:r="urn:r"><e xmlns=""><f></f></e><g>a&amp;b&lt;c&gt;d</g></r:r>',
		exclusive: '<r:r xmlns:r="urn:r"><e><f></f></e><g xmlns="urn:d">a&amp;b&lt;c&gt;d</g></r:r>'
	},
	{
		name: 'attribute and text escaping, CR as a character reference',
		xml: '<a xmlns:x="urn:x" xmlns:y="urn:y" z="&#9;&#10;&#13;&quot;&apos;&amp;">x&#13;y</a>',
		inclusive: '<a xmlns:x="urn:x" xmlns:y="urn:y" z="&#x9;&#xA;&#xD;&quot;\'&amp;">x&#xD;y</a>',
		exclusive: '<a z="&#x9;&#xA;&#xD;&quot;\'&amp;">x&#xD;y</a>'
	}
];

describe('canonicalize', () => {
	for (const vector of VECTORS) {
		it(`matches lxml — ${vector.name}`, () => {
			expect(c14n(vector.xml)).toBe(vector.inclusive);
			expect(c14n(vector.xml, true)).toBe(vector.exclusive);
		});
	}

	it('renders the namespaces in scope of a subtree apex (inclusive) and only the used ones (exclusive)', () => {
		const doc = parse('<r xmlns="urn:d" xmlns:p="urn:p"><x><p:y/></x></r>');
		const apex = doc.documentElement.firstElementChild!;
		expect(new TextDecoder().decode(canonicalize(apex))).toBe(
			'<x xmlns="urn:d" xmlns:p="urn:p"><p:y></p:y></x>'
		);
		expect(new TextDecoder().decode(canonicalize(apex, { exclusive: true }))).toBe(
			'<x xmlns="urn:d"><p:y xmlns:p="urn:p"></p:y></x>'
		);
	});

	it('never copies the default namespace onto a prefixed element just for its unprefixed attributes', () => {
		const doc = parse('<r xmlns="urn:d" xmlns:p="urn:p"><p:y Id="1"/></r>');
		const apex = doc.documentElement.firstElementChild!;
		expect(new TextDecoder().decode(canonicalize(apex, { exclusive: true }))).toBe(
			'<p:y xmlns:p="urn:p" Id="1"></p:y>'
		);
	});

	it('keeps comments only when asked, and canonicalizes a whole Document', () => {
		const doc = parse('<a><!-- note --><b/></a>');
		expect(new TextDecoder().decode(canonicalize(doc))).toBe('<a><b></b></a>');
		expect(new TextDecoder().decode(canonicalize(doc, { withComments: true }))).toBe(
			'<a><!-- note --><b></b></a>'
		);
	});

	it('separates document-level nodes with a newline, as libxml2 does', () => {
		const doc = parse('<?pi a?><!-- note --><a/>');
		expect(new TextDecoder().decode(canonicalize(doc, { exclusive: true }))).toBe('<?pi a?>\n<a></a>');
		expect(new TextDecoder().decode(canonicalize(doc, { exclusive: true, withComments: true }))).toBe(
			'<?pi a?>\n<!-- note -->\n<a></a>'
		);
		const lone = parse('<!-- note --><a/>');
		expect(new TextDecoder().decode(canonicalize(lone, { exclusive: true }))).toBe('<a></a>');
		expect(new TextDecoder().decode(canonicalize(lone, { exclusive: true, withComments: true }))).toBe(
			'<!-- note -->\n<a></a>'
		);
	});

	it('honours the exclusive InclusiveNamespaces prefix list', () => {
		expect(c14n('<a xmlns:x="urn:x" xmlns:y="urn:y" b="1"/>', true)).toBe('<a b="1"></a>');
		expect(
			new TextDecoder().decode(
				canonicalize(parse('<a xmlns:x="urn:x" xmlns:y="urn:y" b="1"/>'), {
					exclusive: true,
					inclusivePrefixes: ['x', '#default']
				})
			)
		).toBe('<a xmlns:x="urn:x" b="1"></a>');
	});
});

/**
 * Byte-compatibility against `xmllint`, the oracle #10 pinned for the OOXML package digests —
 * the inherited-namespace and default-namespace-reset cases the signer depends on.
 */
const hasXmllint = (() => {
	try {
		execFileSync('xmllint', ['--version'], { stdio: 'ignore' });
		return true;
	} catch {
		return false;
	}
})();

const XMLLINT_SAMPLES = [
	// default namespace declared, reset on a prefixed element, unprefixed namespaced child
	'<r xmlns:p="urn:p" xmlns="urn:x"><p:c xmlns=""><n/></p:c></r>',
	// inherited prefixed namespaces over a signature-shaped subtree
	'<ds:Signature xmlns:ds="http://www.w3.org/2000/09/xmldsig#"><ds:SignedInfo><ds:Reference URI="#x"/></ds:SignedInfo></ds:Signature>',
	// mixed prefixes, attribute order, default ns on a prefixed root, text escaping
	'<a:b xmlns:a="urn:a" xmlns:z="urn:z" a:x="1" z:y="2" xmlns="urn:d">t&amp;x &lt;y</a:b>'
];

describe('canonicalize — xmllint parity', () => {
	const dir = mkdtempSync(join(tmpdir(), 'c14n-'));
	it.runIf(hasXmllint)('matches xmllint --c14n and --exc-c14n byte for byte', () => {
		for (const [index, xml] of XMLLINT_SAMPLES.entries()) {
			const path = join(dir, `sample-${index}.xml`);
			writeFileSync(path, xml);
			const doc = parse(xml);
			expect(canonicalizeToString(doc)).toBe(execFileSync('xmllint', ['--c14n', path], { encoding: 'utf8' }));
			expect(canonicalizeToString(doc, true)).toBe(execFileSync('xmllint', ['--exc-c14n', path], { encoding: 'utf8' }));
		}
	});
});
