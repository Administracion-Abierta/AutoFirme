import { spawnSync } from 'node:child_process';
import { cpSync, mkdtempSync, readFileSync, rmSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join, resolve } from 'node:path';
import { afterAll, beforeAll, describe, expect, it } from 'vitest';

// Regression pins for the review findings on `scripts/check-i18n.mjs` (Refs #119). The checker is
// the only guard the hardcoded-copy inventory has, so its own blind spots are worth a test:
// the review found three (a single-quote-only attribute regex, quotes inside the value truncating
// the match, and text runs that a tag did not delimit on both sides). Each was fixed by widening
// the rule; these tests keep a later simplification from narrowing them again.
//
// The checker derives its root from its own path, so the fixture needs a copy of the script next
// to a real `src/` tree: it imports `src/lib/i18n.ts` and scans every `.svelte` under `src`.
const repo = resolve(process.cwd());
let fixture = '';

/** Writes `svelte` over the probe component and runs the checker on the fixture. */
const check = (svelte: string) => {
	writeFileSync(join(fixture, 'src/lib/__probe__.svelte'), svelte);
	return spawnSync(process.execPath, ['scripts/check-i18n.mjs', '--check'], {
		cwd: fixture,
		encoding: 'utf8'
	});
};

beforeAll(() => {
	fixture = mkdtempSync(join(tmpdir(), 'check-i18n-'));
	// Node picks the module type by the nearest `package.json`; without this the fixture inherits
	// whatever sits above the temp dir and warns about `i18n.ts` on stderr.
	cpSync(join(repo, 'package.json'), join(fixture, 'package.json'));
	cpSync(join(repo, 'src'), join(fixture, 'src'), { recursive: true });
	cpSync(join(repo, 'scripts'), join(fixture, 'scripts'), { recursive: true });
	cpSync(join(repo, 'src-tauri'), join(fixture, 'src-tauri'), { recursive: true });
});

afterAll(() => rmSync(fixture, { recursive: true, force: true }));

describe('check-i18n inventory', () => {
	it('accepts the catalogue as it stands', () => {
		const result = check('');
		expect(result.stderr).toBe('');
		expect(result.status).toBe(0);
	});

	// The regex must match a value against its own delimiter: matching the two quote styles
	// independently truncated `title="don't save"` to `don`, and `placeholder="'Guardar'"` matched
	// nothing at all.
	it('catches untranslated copy in both quote styles, untruncated', () => {
		for (const [attribute, expected] of [
			['placeholder="Guardar fichero"', 'Guardar fichero'],
			["aria-label='Guardar fichero'", 'Guardar fichero'],
			['title="don\'t save"', "don't save"],
			['placeholder="\'Guardar fichero\'"', "'Guardar fichero'"]
		] as const) {
			const result = check(`<button ${attribute}></button>`);
			expect(result.status, attribute).toBe(1);
			expect(result.stderr, attribute).toContain(`hardcoded user-visible text ${JSON.stringify(expected)}`);
		}
	});

	// Copy wrapped in a brace expression is the same defect, but only a value that is *nothing but*
	// one quoted string is flagged: the lookups below are how translated copy is rendered.
	it('catches a whole-value brace expression but not a lookup', () => {
		for (const svelte of ["<button aria-label={'Guardar fichero'}></button>", "<p>{'Guardar fichero'}</p>"]) {
			const result = check(svelte);
			expect(result.status, svelte).toBe(1);
			expect(result.stderr, svelte).toContain('hardcoded user-visible text "Guardar fichero"');
		}

		for (const svelte of [
			'<button aria-label={t.formatTitle}></button>',
			'<button aria-label="{t.formatTitle}: {file.name}"></button>',
			'<p>{t.formatTitle}</p>'
		]) {
			const result = check(svelte);
			expect(result.status, svelte).toBe(0);
		}
	});

	// The text rule used to demand a `<` on both sides of a run, so it only saw copy hugging two
	// tags. Copy sitting next to a block or inside a text run with an interpolation was invisible:
	// every expression is lifted out and the rest is split on tags, which covers all of these.
	it('catches copy delimited by blocks or interpolation, not just by tags', () => {
		for (const svelte of [
			'{#if x}\n\tGuardar fichero\n{/if}',
			'{#each files as f}\n\tGuardar fichero\n{/each}',
			'<p>Guardar fichero {t.name}</p>',
			'<p>{t.name} Guardar fichero</p>',
			'<p>{`Guardar fichero`}</p>'
		]) {
			const result = check(svelte);
			expect(result.status, svelte).toBe(1);
			expect(result.stderr, svelte).toContain('hardcoded user-visible text "Guardar fichero"');
		}

		for (const svelte of [
			'{#if x}<p>{t.title}</p>{/if}',
			'<p>{t.title} {t.name}</p>',
			'<p>{`${t.title} ${t.name}`}</p>',
			'<button onclick={() => (open = true)}></button>'
		]) {
			const result = check(svelte);
			expect(result.status, svelte).toBe(0);
		}
	});
});

describe('check-i18n Rust embed', () => {
	// The crate's own sources are 4-space, so a tab-indented embed stood out in every diff and
	// `cargo fmt` would rewrite it wholesale. The generator is run (not just the copied artifact
	// read) so this pins the template itself; rustfmt is deliberately not invoked, it is not a
	// build dependency here.
	it('generates the module indented the way the crate is', () => {
		const result = spawnSync(process.execPath, ['scripts/check-i18n.mjs'], {
			cwd: fixture,
			encoding: 'utf8'
		});
		expect(result.status).toBe(0);

		const generated = readFileSync(join(fixture, 'src-tauri/src/i18n.rs'), 'utf8');
		expect(generated).not.toContain('\t');
		expect(generated).toContain('\n    Es,\n    En,\n');
	});
});
