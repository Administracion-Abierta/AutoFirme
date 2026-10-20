/**
 * ES/EN parity. TypeScript guards the `copy` table through `Record<Lang, Copy>`; the extension
 * chooser's `extensionCopy` is a plain literal, so drift between the two languages is only
 * visible at runtime. Both tables must have the same leaves, no blank string, and the same
 * `{placeholder}` names.
 */

import { readFileSync } from 'node:fs';
import { describe, expect, it } from 'vitest';
import { extensionCopy } from './extension/copy';
import { copy } from './i18n';

type Locales = Record<'es' | 'en', unknown>;

function leaves(value: unknown, prefix = '', out: Record<string, string> = {}): Record<string, string> {
	if (typeof value === 'string') {
		out[prefix] = value;
	} else if (value && typeof value === 'object') {
		for (const [key, child] of Object.entries(value)) {
			leaves(child, prefix ? `${prefix}.${key}` : key, out);
		}
	}
	return out;
}

const placeholders = (text: string): string[] =>
	[...text.matchAll(/\{(\w+)\}/g)].map((match) => match[1]).sort();

function expectParity(name: string, table: Locales): void {
	const es = leaves(table.es);
	const en = leaves(table.en);
	expect(Object.keys(en).sort(), `${name}: keys missing from ES`).toEqual(Object.keys(es).sort());
	for (const [path, text] of Object.entries(es)) {
		const translated = en[path];
		expect(text.trim(), `${name}.es.${path} is blank`).not.toBe('');
		expect(translated.trim(), `${name}.en.${path} is blank`).not.toBe('');
		expect(placeholders(translated), `${name}.${path} placeholders differ`).toEqual(
			placeholders(text)
		);
	}
}

describe('ES/EN copy parity', () => {
	it('keeps the app copy table complete in both languages', () => {
		expectParity('copy', copy as unknown as Locales);
	});

	it('keeps the extension copy table complete in both languages', () => {
		expectParity('extensionCopy', extensionCopy);
	});

	it('keeps the manifest description bilingual', () => {
		// Chrome truncates store descriptions past 132 characters.
		const { description } = JSON.parse(readFileSync('extension/manifest.json', 'utf8'));
		expect(typeof description).toBe('string');
		expect(description.length).toBeLessThanOrEqual(132);
		const [es, en, ...extra] = description.split(' / ');
		expect(extra, 'manifest description must be exactly ES / EN').toEqual([]);
		expect(es.trim(), 'manifest description lacks ES').not.toBe('');
		expect(en.trim(), 'manifest description lacks EN').not.toBe('');
	});
});
