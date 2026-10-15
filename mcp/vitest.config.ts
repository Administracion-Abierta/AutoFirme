import { defineConfig } from 'vitest/config';

export default defineConfig({
	test: {
		// The whole point of the study: the engine must run on plain Node, not in a DOM.
		environment: 'node',
		include: ['test/**/*.test.ts']
	}
});
