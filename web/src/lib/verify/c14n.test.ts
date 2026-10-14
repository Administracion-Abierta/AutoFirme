/**
 * The canonicalizer is checked against vectors produced by python `ElementTree.canonicalize`
 * (an implementation that shares no code with this one) in `test/fixtures/c14n/`.
 */
import { readdirSync, readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { describe, expect, it } from 'vitest';
import { canonicalize } from '#lib/verify/c14n';

const directory = resolve(process.cwd(), 'test/fixtures/c14n') + '/';
const cases = readdirSync(directory)
	.filter((name) => name.endsWith('.xml'))
	.sort();

describe('canonicalization', () => {
	// python `ElementTree.canonicalize` implements C14N 2.0, which renders only visibly
	// utilized namespace prefixes — for these vectors the same result as exclusive C14N.
	it.each(cases)('%s matches the independent reference form (exclusive)', (name) => {
		const document = new DOMParser().parseFromString(readFileSync(directory + name, 'utf8'), 'application/xml');
		const expected = readFileSync(directory + name.replace(/\.xml$/, '.c14n'), 'utf8');

		expect(canonicalize(document, { exclusive: true })).toBe(expected);
	});

	it('inclusive C14N keeps an in-scope declaration that exclusive C14N drops', () => {
		const document = new DOMParser().parseFromString(
			'<a xmlns="urn:d" xmlns:p="urn:p"><b/></a>',
			'application/xml'
		);

		expect(canonicalize(document)).toBe('<a xmlns="urn:d" xmlns:p="urn:p"><b></b></a>');
		expect(canonicalize(document, { exclusive: true })).toBe('<a xmlns="urn:d"><b></b></a>');
	});

	it('emits the inherited default namespace for a subtree reference (the SignedInfo case)', () => {
		const document = new DOMParser().parseFromString(
			'<r xmlns="urn:d"><c a="1">x</c></r>',
			'application/xml'
		);
		const child = document.documentElement.firstElementChild as Element;

		expect(canonicalize(child)).toBe('<c xmlns="urn:d" a="1">x</c>');
	});
});
