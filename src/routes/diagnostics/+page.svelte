<script lang="ts">
	import { onMount } from 'svelte';
	import { createEngineClient } from '#lib/engine';
	import { PDF_BUDGET, runBudget, runWebCryptoProbes, type ProbeResult } from '#lib/diagnostics';
	import { copy, type Lang } from '#lib/i18n';
	import { loadSignerSession } from '#lib/keystore';

	/**
	 * The diagnostics page is a standalone route, so it carries its own language state instead of
	 * the app shell's. The catalogue is the single source of copy (#119): every literal below
	 * comes from `copy[lang]`, which is why the hardcoded-copy gate passes.
	 */
	let lang = $state<Lang>('es');
	const t = $derived(copy[lang].diagnostics);

	/** The 12-item parity gate of #19, each item mapped to the suite that proves it. */
	const GATE: { item: string; proof: string }[] = [
		{ item: 'PKCS#12 import (PBES2 + legacy PBE)', proof: 'src/lib/pkcs12.test.ts (legacy PBE lives here)' },
		{ item: 'CAdES-BES', proof: 'src/lib/cms.test.ts' },
		{ item: 'CAdES-T', proof: 'src/lib/timestamp.test.ts' },
		{ item: 'XAdES-EPES + c14n', proof: 'src/lib/xades.test.ts, c14n.test.ts' },
		{ item: 'PAdES B-B', proof: 'src/lib/pades.test.ts' },
		{ item: 'PAdES visible stamp', proof: 'src/lib/pades-visible.test.ts' },
		{ item: 'PAdES B-T', proof: 'src/lib/timestamp.test.ts, pades.test.ts' },
		{ item: 'Batch + ZIP', proof: 'src/lib/batch.test.ts, scripts/verify-batch-zip.mjs' },
		{ item: 'Verify CAdES/XAdES/PAdES incl. byte-range', proof: 'src/lib/verify.test.ts' },
		{ item: 'IndexedDB cert store', proof: 'src/lib/certstore.test.ts' },
		{ item: 'i18n ES/EN', proof: 'src/lib/App.test.ts, i18n.ts' },
		{ item: 'Extension bridge handshake + portal interception', proof: 'extension/tests/*.test.js' }
	];

	let probes = $state<ProbeResult[]>([]);
	let probesRunning = $state(true);

	onMount(async () => {
		probes = await runWebCryptoProbes();
		probesRunning = false;
	});

	let pin = $state('');
	let p12: Uint8Array<ArrayBuffer> | null = $state(null);
	let phase = $state('');
	let fraction = $state<number | null>(null);
	let result = $state<{ signMs: number; verifyMs: number; signedBytes: number; verified: boolean; pass: boolean } | null>(null);
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
</script>

<svelte:head>
	<title>{t.title}</title>
</svelte:head>

<main class="mx-auto flex max-w-3xl flex-col gap-6 p-6">
	<header>
		<h1 class="font-display text-xl font-semibold">{t.heading}</h1>
		<p class="text-muted-foreground text-sm">
			{t.secureContext} {typeof window === 'undefined' ? 'n/a' : String(window.isSecureContext)}
		</p>
	</header>

	<section class="rounded-lg border p-4">
		<h2 class="mb-3 font-semibold">
			{t.webCryptoTitle} ({probesRunning ? '…' : probes.every((r) => r.ok) ? t.probeOk : t.probeFail})
		</h2>
		<ul class="flex flex-col gap-2 text-sm">
			{#each probes as probe (probe.id)}
				<li class="flex items-start gap-2">
					<span class="w-32 shrink-0 font-mono">{probe.id}</span>
					<span class={probe.ok ? 'text-emerald-600' : 'text-red-600'}>{probe.ok ? t.probeOk : t.probeFail}</span>
					<span class="text-muted-foreground break-all">{probe.detail}</span>
				</li>
			{:else}
				<li class="text-muted-foreground">{t.probesRunning}</li>
			{/each}
		</ul>
	</section>

	<section class="rounded-lg border p-4">
		<h2 class="mb-3 font-semibold">{t.parityTitle}</h2>
		<p class="text-muted-foreground mb-3 text-sm">
			{t.parityHint} <code>{t.parityCommand}</code>.
		</p>
		<ol class="list-decimal pl-5 text-sm">
			{#each GATE as { item, proof }}
				<li class="mb-1"><span class="font-medium">{item}</span> — <code>{proof}</code></li>
			{/each}
		</ol>
	</section>

	<section class="rounded-lg border p-4">
		<h2 class="mb-3 font-semibold">{t.budgetTitle}</h2>
		<p class="text-muted-foreground mb-3 text-sm">{t.budgetHint}</p>
		<input type="file" accept=".p12,.pfx" onchange={pick} />
		<input type="password" placeholder={t.pinPlaceholder} bind:value={pin} />
		<button onclick={run} disabled={!p12 || running}>{running ? t.running : t.runBudget}</button>
		{#if phase}<p role="status">{t.phase} {phase}{fraction === null ? '' : ` ${Math.round(fraction * 100)}%`}</p>{/if}
		{#if fraction !== null}<progress value={fraction ?? 0} max="1">{phase}</progress>{/if}
		{#if result}
			<table>
				<tbody>
					<tr><td>{t.signRow}</td><td>{t.msBudget.replace('{ms}', String(result.signMs)).replace('{budget}', String(PDF_BUDGET.signMs))}</td></tr>
					<tr><td>{t.verifyRow}</td><td>{t.msBudget.replace('{ms}', String(result.verifyMs)).replace('{budget}', String(PDF_BUDGET.verifyMs))}</td></tr>
					<tr><td>{t.signedRow}</td><td>{t.bytes.replace('{bytes}', String(result.signedBytes))}</td></tr>
					<tr><td>{t.verifiedRow}</td><td>{result.verified ? t.verifiedYes : t.verifiedNo}</td></tr>
					<tr><td>{t.verdictRow}</td><td>{result.pass ? t.verdictPass : t.verdictFail}</td></tr>
				</tbody>
			</table>
		{/if}
		{#if error}<p role="alert">{error}</p>{/if}
	</section>
</main>
