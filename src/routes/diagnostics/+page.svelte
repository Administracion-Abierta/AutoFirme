<script lang="ts">
	import { onMount } from 'svelte';
	import { runWebCryptoProbes, type ProbeResult } from '#lib/diagnostics';

	/** The 12-item parity gate of #19, each item mapped to the suite that proves it. */
	const GATE: { item: string; proof: string }[] = [
		{ item: 'PKCS#12 import (PBES2 + legacy PBE)', proof: 'src/lib/pkcs12.test.ts, legacy-pbe.test.ts' },
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

	let results = $state<ProbeResult[]>([]);
	let running = $state(true);

	onMount(async () => {
		results = await runWebCryptoProbes();
		running = false;
	});
</script>

<svelte:head>
	<title>Diagnósticos · AutoFirme</title>
</svelte:head>

<main class="mx-auto flex max-w-3xl flex-col gap-6 p-6">
	<header>
		<h1 class="font-display text-xl font-semibold">Diagnósticos / Diagnostics</h1>
		<p class="text-muted-foreground text-sm">
			WebCrypto bajo este contexto / WebCrypto under this context: {typeof window === 'undefined'
				? 'n/a'
				: String(window.isSecureContext)}
		</p>
	</header>

	<section class="rounded-lg border p-4">
		<h2 class="mb-3 font-semibold">WebCrypto ({running ? '…' : results.every((r) => r.ok) ? 'ok' : 'falla'})</h2>
		<ul class="flex flex-col gap-2 text-sm">
			{#each results as result (result.id)}
				<li class="flex items-start gap-2">
					<span class="w-32 shrink-0 font-mono">{result.id}</span>
					<span class={result.ok ? 'text-emerald-600' : 'text-red-600'}>{result.ok ? 'ok' : 'FAIL'}</span>
					<span class="text-muted-foreground break-all">{result.detail}</span>
				</li>
			{:else}
				<li class="text-muted-foreground">Ejecutando… / running…</li>
			{/each}
		</ul>
	</section>

	<section class="rounded-lg border p-4">
		<h2 class="mb-3 font-semibold">12-item parity gate</h2>
		<p class="text-muted-foreground mb-3 text-sm">
			Each item is proven by the suite named next to it; run them with <code>pnpm test</code>.
		</p>
		<ol class="list-decimal pl-5 text-sm">
			{#each GATE as { item, proof }}
				<li class="mb-1"><span class="font-medium">{item}</span> — <code>{proof}</code></li>
			{/each}
		</ol>
	</section>
</main>
