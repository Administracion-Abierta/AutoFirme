#!/usr/bin/env node
/**
 * Build the self-contained `autofirme-mcp` sidecar binary with `bun build --compile`.
 *
 * Output lands in `src-tauri/binaries/autofirme-mcp-<rust-triplet>`, the layout Tauri
 * `bundle.externalBin` resolves at pack time (Refs #107).
 *
 * Usage: `bun ./build.mjs` (host binary, what `tauri dev`/`tauri build` need) or
 * `bun ./build.mjs --all` (every OS/arch matrix entry, for release CI).
 * Runs under node or bun; shells out to the `bun` binary on PATH.
 */
import { execFileSync } from 'node:child_process';
import { existsSync, mkdirSync } from 'node:fs';
import { arch, platform } from 'node:os';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const here = dirname(fileURLToPath(import.meta.url));
const outDir = join(here, '..', 'src-tauri', 'binaries');

/** bun --target -> Rust target triplet (the suffix Tauri sidecars require). */
const MATRIX = {
	'bun-linux-x64': 'x86_64-unknown-linux-gnu',
	'bun-linux-arm64': 'aarch64-unknown-linux-gnu',
	'bun-darwin-x64': 'x86_64-apple-darwin',
	'bun-darwin-arm64': 'aarch64-apple-darwin',
	'bun-windows-x64': 'x86_64-pc-windows-msvc'
};

const HOST_BUN_TARGET = `bun-${platform() === 'win32' ? 'windows' : platform()}-${arch() === 'arm64' ? 'arm64' : 'x64'}`;

function build(bunTarget) {
	const triplet = MATRIX[bunTarget];
	if (!triplet) throw new Error(`build.mjs: no Rust triplet mapped for ${bunTarget}`);
	const name = `autofirme-mcp-${triplet}${bunTarget.startsWith('bun-windows') ? '.exe' : ''}`;
	mkdirSync(outDir, { recursive: true });
	execFileSync('bun', ['build', join(here, 'src', 'server.ts'), '--compile', `--target=${bunTarget}`, `--outfile=${join(outDir, name)}`], { stdio: 'inherit' });
	console.error(`build.mjs: wrote ${name}`);
}

const targets = process.argv.includes('--all') ? Object.keys(MATRIX) : [HOST_BUN_TARGET];
for (const target of targets) build(target);
