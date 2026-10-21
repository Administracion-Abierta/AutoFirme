<script lang="ts">
	import { createEngineClient } from '#lib/engine';
	import { PDF_BUDGET, runBudget } from '#lib/diagnostics';
	import { loadSignerSession } from '#lib/keystore';

	let pin = $state('');
	let p12: Uint8Array<ArrayBuffer> | null = $state(null);
	let phase = $state('');
	let fraction = $state<number | null>(null);
	let result = $state<{ signMs: number; verifyMs: number; signedBytes: number; pass: boolean } | null>(null);
	let error = $state('');
	let running = $state(false);

	async function pick(event: Event): Promise<void> {
		const file = (event.target as HTMLInputElement).files?.[0];
		if (!file) return;
		p12 = new Uint8Array(await file.arrayBuffer()) as Uint8Array<ArrayBuffer>;
	}

	async function run(): Promise<void> {
		if (!p12 || running) return;
		running = true;
		error = '';
		result = null;
		const client = createEngineClient();
		try {
			const { signer } = await loadSignerSession(p12, pin);
			if (!signer.key) throw new Error('certificate has no private key');
			const certificate = signer.der.buffer.slice(
				signer.der.byteOffset,
				signer.der.byteOffset + signer.der.byteLength
			) as ArrayBuffer;
			const key = signer.key;
			// Progress posts arrive on the main thread while the worker signs the 50 MB
			// PDF — the bar moves instead of the webview hanging.
			result = await runBudget(
				{ kind: 'sign-pades', certificate, chain: [], privateKey: key },
				(request, onProgress) => client.run(request, onProgress),
				(next, at) => {
					phase = next;
					fraction = at;
				}
			);
		} catch (failure) {
			error = (failure as Error).message;
		} finally {
			client.terminate();
			running = false;
		}
	}

	function budgetLabel(): string {
		return `${Math.round(PDF_BUDGET.bytes / 1024 / 1024)} MB`;
	}
</script>

<main>
	<h1>Diagnostics · {budgetLabel()} PAdES budget</h1>
	<p>Signs a synthetic {budgetLabel()} PDF in a Web Worker, then verifies it. Progress below proves the UI stays alive.</p>
	<input type="file" accept=".p12,.pfx" onchange={pick} />
	<input type="password" placeholder="PIN" bind:value={pin} />
	<button onclick={run} disabled={!p12 || running}>{running ? 'Running…' : 'Run budget'}</button>
	{#if phase}<p role="status">Phase: {phase}{fraction === null ? '' : ` ${Math.round(fraction * 100)}%`}</p>{/if}
	{#if fraction !== null}<progress value={fraction ?? 0} max="1">{phase}</progress>{/if}
	{#if result}
		<table>
			<tbody>
				<tr><td>Sign</td><td>{result.signMs} ms (budget {PDF_BUDGET.signMs})</td></tr>
				<tr><td>Verify</td><td>{result.verifyMs} ms (budget {PDF_BUDGET.verifyMs})</td></tr>
				<tr><td>Signed</td><td>{result.signedBytes} bytes</td></tr>
				<tr><td>Verdict</td><td>{result.pass ? 'PASS' : 'FAIL'}</td></tr>
			</tbody>
		</table>
	{/if}
	{#if error}<p role="alert">{error}</p>{/if}
</main>
