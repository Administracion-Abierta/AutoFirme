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

/** Both manifest strings the user reads outside the app: they carry the same `ES / EN` shape. */
function expectBilingual(label: string, value: unknown): void {
	expect(typeof value, `${label} must be a string`).toBe('string');
	const parts = (value as string).split(' / ');
	expect(parts.length, `${label} must be exactly ES / EN`).toBe(2);
	for (const [index, part] of parts.entries()) {
		expect(part.trim(), `${label} part ${index + 1} is blank`).not.toBe('');
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
		expect((description as string).length).toBeLessThanOrEqual(132);
		expectBilingual('manifest description', description);
	});

	it('keeps the toolbar title bilingual', () => {
		const { action } = JSON.parse(readFileSync('extension/manifest.json', 'utf8'));
		expectBilingual('manifest action.default_title', action?.default_title);
	});
});
