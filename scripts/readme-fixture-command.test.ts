/**
 * Regression for review finding 2 on #39: the README's manual-evidence step for the generated
 * fixture named `modern.p12` but gave no way to materialize it, so the documented flow was
 * non-actionable.
 *
 * Not a wording pin: this extracts the one-line fixture command from the README itself, runs it
 * verbatim through `bash`, and checks that the path it prints really is an existing `modern.p12`.
 * If the command rots (wrong module path, wrong API name), the documented step breaks and this
 * fails. `TMPDIR` is pointed at a private directory so the throwaway fixture dir never lands in
 * the shared temp dir that `smoke-fixture-leak.test.ts` counts.
 */

import { execSync } from 'node:child_process';
import { existsSync, mkdtempSync, readFileSync, rmSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { fileURLToPath } from 'node:url';
import { expect, test } from 'vitest';

const ROOT = fileURLToPath(new URL('..', import.meta.url));

/** The documented fixture-materializing command, taken straight from README.md. */
function readmeFixtureCommand(): string | undefined {
	return readFileSync(`${ROOT}/README.md`, 'utf8')
		.split('\n')
		.map((line) => line.trim())
		.find((line) => line.startsWith('node --input-type=module'));
}

test('the README shows a runnable command that materializes modern.p12', () => {
	const command = readmeFixtureCommand();
	expect(command, 'the README must document a node command for the generated fixture').toBeTruthy();
	const sandbox = mkdtempSync(join(tmpdir(), 'readme-fixture-sandbox-'));
	try {
		const printed = execSync(`bash -c ${JSON.stringify(command)}`, {
			cwd: ROOT,
			encoding: 'utf8',
			env: { ...process.env, TMPDIR: sandbox },
			timeout: 120_000
		}).trim();
		expect(printed).toMatch(/modern\.p12$/);
		expect(existsSync(printed), `command printed a path that does not exist: ${printed}`).toBe(true);
	} finally {
		rmSync(sandbox, { recursive: true, force: true });
	}
});
