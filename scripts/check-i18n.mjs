/**
 * i18n gate (#119): `src/lib/i18n.ts` is the single copy source.
 *
 * 1. ES/EN parity — same key set, no empty string, same `{placeholders}`.
 * 2. Inventory — user-visible literals hardcoded in the app's own Svelte files
 *    (not in the vendored shadcn primitives) are an error: they can never be
 *    translated.
 * 3. Rust embed — regenerates `src-tauri/src/i18n.rs` from the catalogue, one
 *    `match` arm per key and language, so a key the shell asks for and the
 *    catalogue no longer has fails `cargo check` naming that key.
 *
 * Usage:
 *   node scripts/check-i18n.mjs           # report, rewrite src-tauri/src/i18n.rs
 *   node scripts/check-i18n.mjs --check   # report, fail if that file is stale (CI)
 *
 * Exit code 0 only when every check passes.
 */

import { mkdirSync, readdirSync, readFileSync, writeFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { dirname, join, relative } from 'node:path';

const root = fileURLToPath(new URL('..', import.meta.url));
const checkOnly = process.argv.includes('--check');

/**
 * Literals that stay untranslated on purpose: the brand renders the same in both
 * languages, `ES`/`EN` are the language picker's own labels, and the example URLs are
 * placeholders. Anything else found hardcoded is a failure.
 */
const LITERAL_ALLOWLIST = new Set(['AutoFirma', 'AutoFirma Web', 'AutoFirme', 'ES', 'EN']);
const EXAMPLE_URL = /^https?:\/\//;

const problems = [];
const note = (message) => problems.push(message);
/** Reports every problem found so far and stops: a broken catalogue is never embedded. */
function fail() {
	console.error(`i18n check failed (${problems.length}):`);
	for (const problem of problems) console.error(`  ${problem}`);
	process.exit(1);
}

/** Flattens `copy` into `key -> {es, en}`; nested records (`certErrors`) become `certErrors.*`. */
function flatten(node, prefix = '', out = new Map()) {
	for (const [name, value] of Object.entries(node)) {
		const path = prefix ? `${prefix}.${name}` : name;
		if (typeof value === 'string') out.set(path, value);
		else flatten(value, path, out);
	}
	return out;
}

const { copy } = await import(new URL('../src/lib/i18n.ts', import.meta.url).href).catch((error) => {
	console.error(
		`cannot import src/lib/i18n.ts (${error.message}).\n` +
			'Node 22.18+ strips TypeScript types itself; older runtimes need --experimental-strip-types.'
	);
	process.exit(2);
});

const es = flatten(copy.es);
const en = flatten(copy.en);

// 1. Parity ------------------------------------------------------------------
for (const key of es.keys()) if (!en.has(key)) note(`en is missing key "${key}"`);
for (const key of en.keys()) if (!es.has(key)) note(`es is missing key "${key}"`);
for (const [key, value] of [...es, ...en]) {
	if (value.trim() === '') note(`empty string for "${key}"`);
}
const placeholders = (value) => [...value.matchAll(/\{(\w+)\}/g)].map((m) => m[1]).sort().join(',');
for (const key of es.keys()) {
	if (!en.has(key)) continue;
	const [a, b] = [placeholders(es.get(key)), placeholders(en.get(key))];
	if (a !== b) note(`placeholders differ for "${key}": es {${a}} vs en {${b}}`);
}

// 2. Inventory ---------------------------------------------------------------
function svelteFiles(dir, out = []) {
	for (const entry of readdirSync(dir, { withFileTypes: true })) {
		const path = join(dir, entry.name);
		if (entry.isDirectory()) {
			// The vendored shadcn primitives ship their own English defaults; they are not our copy.
			if (path.endsWith('components/ui')) continue;
			svelteFiles(path, out);
		} else if (entry.name.endsWith('.svelte')) out.push(path);
	}
	return out;
}

const literalSources = [...svelteFiles(join(root, 'src'))];
for (const path of literalSources) {
	const source = readFileSync(path, 'utf8');
	const shown = relative(root, path);
	// Only the markup half is copy. A `<script>` block holds TypeScript, and `=>` reads as
	// a closing `>`, so generics and comments would otherwise parse as text nodes.
	const markup = source.replace(/<script[\s\S]*?<\/script>/g, '');
	const literals = [
		...markup.matchAll(/>([^<>{}]+)</g),
		...markup.matchAll(/\b(?:placeholder|title|aria-label|alt)=["']([^"'{}]*)["']/g)
	].map((match) => match[1].trim());
	for (const literal of literals) {
		if (!/\p{L}/u.test(literal)) continue;
		if (LITERAL_ALLOWLIST.has(literal) || EXAMPLE_URL.test(literal)) continue;
		note(`${shown}: hardcoded user-visible text ${JSON.stringify(literal)} — move it to src/lib/i18n.ts`);
	}
}

// A catalogue with parity or inventory problems must not be embedded: the tables below
// index `es`/`en` by the ES key set, so a key missing from EN would crash the generator
// instead of naming it. Both modes stop here, before anything is written.
if (problems.length > 0) fail();

// 3. Rust embed --------------------------------------------------------------
const pascal = (part) => part.charAt(0).toUpperCase() + part.slice(1);
/** `native.pinTitle` -> `NativePinTitle`; the `.name()` shown to users stays the dotted path. */
const variant = (key) => key.split(/[.-]/).map(pascal).join('');

const keys = [...es.keys()].sort();
const variants = new Set(keys.map(variant));
if (variants.size !== keys.length) note('two catalogue keys collapse to the same Rust variant');

const literal = (value) => {
	const encoded = JSON.stringify(value);
	if (encoded.includes('\\u')) note(`cannot embed non-ASCII control characters in "${value}"`);
	return encoded;
};

const rust = `//! ES/EN copy for the desktop shell, generated from \`src/lib/i18n.ts\` by
//! \`node scripts/check-i18n.mjs\`. Do not edit by hand: run the script instead, and
//! \`node scripts/check-i18n.mjs --check\` fails when this file drifts from the catalogue.
//!
//! \`get\` matches every key in every language, so a key that exists in one language and
//! not the other — or a caller naming a key the catalogue dropped — is a compile error
//! that names it.

/// Language the shell is running in. Spanish is the default.
#[derive(Clone, Copy, Debug, PartialEq, Eq, Hash)]
pub enum Lang {
	Es,
	En,
}

impl Lang {
	pub const ALL: &'static [Lang] = &[Lang::Es, Lang::En];

	/// Two-letter tag, the same one the SPA stores in its settings.
	pub const fn code(self) -> &'static str {
		match self {
			Lang::Es => "es",
			Lang::En => "en",
		}
	}

	/// Anything unknown falls back to Spanish, the app's default.
	pub fn parse(code: &str) -> Lang {
		match code {
			"en" => Lang::En,
			_ => Lang::Es,
		}
	}
}

/// Every catalogue key. Removing one from \`src/lib/i18n.ts\` removes the variant, so
/// callers that still name it stop compiling and the compiler names the key.
#[derive(Clone, Copy, Debug, PartialEq, Eq, Hash)]
pub enum Key {
${keys.map((key) => `\t${variant(key)},`).join('\n')}
}

impl Key {
	pub const ALL: &'static [Key] = &[
${keys.map((key) => `\t\tKey::${variant(key)},`).join('\n')}
\t];

	/// The dotted catalogue path, as it appears in \`src/lib/i18n.ts\`.
	pub fn name(self) -> &'static str {
		match self {
${keys.map((key) => `\t\t\tKey::${variant(key)} => ${literal(key)},`).join('\n')}
		}
	}

	pub fn parse(name: &str) -> Option<Key> {
		match name {
${keys.map((key) => `\t\t\t${literal(key)} => Some(Key::${variant(key)}),`).join('\n')}
			_ => None,
		}
	}
}

/// The copy for one key in one language.
pub fn get(lang: Lang, key: Key) -> &'static str {
	match (lang, key) {
${keys
	.map((key) => `\t\t(Lang::Es, Key::${variant(key)}) => ${literal(es.get(key))},\n\t\t(Lang::En, Key::${variant(key)}) => ${literal(en.get(key))},`)
	.join('\n')}
	}
}

#[cfg(test)]
mod tests {
	use super::*;

	#[test]
	fn every_key_is_translated_in_both_languages() {
		for &lang in Lang::ALL {
			for &key in Key::ALL {
				assert!(!get(lang, key).is_empty(), "{} has no copy for {}", key.name(), lang.code());
				assert_eq!(Key::parse(key.name()), Some(key), "{} does not round-trip", key.name());
			}
		}
	}
}
`;

// Everything the embed can go wrong on (colliding variants, non-embedable characters) is
// recorded while the tables above are built, so the guard has to run before the write:
// a file that does not compile must never reach disk.
if (problems.length > 0) fail();

const i18nRs = join(root, 'src-tauri/src/i18n.rs');
const current = (() => {
	try {
		return readFileSync(i18nRs, 'utf8');
	} catch {
		return null;
	}
})();

if (current !== rust) {
	if (checkOnly) {
		note(
			`${relative(root, i18nRs)} is stale: ${current === null ? 'missing' : 'does not match the catalogue'}. ` +
				'Run `node scripts/check-i18n.mjs`.'
		);
	} else {
		mkdirSync(dirname(i18nRs), { recursive: true });
		writeFileSync(i18nRs, rust);
		console.log(`wrote ${relative(root, i18nRs)} (${keys.length} keys, ${keys.length * 2} translations)`);
	}
}

if (problems.length > 0) fail();

console.log(
	`i18n ok: ${keys.length} keys, ES/EN parity, no hardcoded copy in ${literalSources.length} Svelte files`
);
