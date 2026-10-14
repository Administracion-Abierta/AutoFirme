<script lang="ts">
	import { EMPTY_BATCH_STATE, buildZip, type BatchQueue, type BatchSigner, type BatchState } from '#lib/batch';
	import type { Copy } from '#lib/i18n';
	import { formatBytes } from '#lib/sign';

	interface Props {
		copy: Copy;
		queue: BatchQueue;
		/** Format engine; null while the format's engine (#6 CAdES, #7 XAdES, #11 PAdES) is missing. */
		signer: BatchSigner | null;
		/** Why the queue cannot run yet — announced with the run button. */
		hint: string;
	}

	let { copy, queue, signer, hint }: Props = $props();

	let state = $state<BatchState>(EMPTY_BATCH_STATE);
	// The run loop writes to the store from async code, so the component mirrors it.
	$effect(() => queue.subscribe((value) => (state = value)));

	const signed = $derived(state.documents.filter((document) => document.status === 'signed').length);

	async function run() {
		if (!signer) return;
		await queue.run(signer);
	}

	function save(name: string, bytes: Uint8Array<ArrayBuffer>, type: string) {
		const url = URL.createObjectURL(new Blob([bytes], { type }));
		const link = document.createElement('a');
		link.href = url;
		link.download = name;
		link.click();
		URL.revokeObjectURL(url);
	}
</script>

<section class="flex flex-col gap-4 rounded-2xl border px-4 py-4">
	<header class="flex flex-wrap items-baseline justify-between gap-2">
		<h3 class="font-semibold">{copy.queueTitle}</h3>
		<p class="text-sm opacity-70" aria-live="polite">
			{`${state.phase === 'running' ? copy.queueRunning : copy.queueProgress}: ${signed}/${state.documents.length}`}
		</p>
	</header>

	<ol class="flex flex-col gap-2">
		{#each state.documents as document (document.id)}
			{@const status = copy.queueStatus[document.status]}
			<li
				class="flex flex-wrap items-center gap-2 rounded-xl bg-ink-100 px-3 py-2 text-sm dark:bg-ink-700"
				data-status={document.status}
			>
				<span
					class="rounded-full px-2 py-0.5 text-xs font-medium {document.status === 'signed'
						? 'bg-emerald-600 text-white'
						: document.status === 'error'
							? 'bg-red-600 text-white'
							: 'bg-ink-900/10 dark:bg-ink-50/10'}">{status}</span
				>
				<span class="truncate font-medium" title={document.file.name}>{document.file.name}</span>
				<span class="opacity-70">{formatBytes(document.file.size)}</span>
				{#if document.status === 'signed'}
					<span class="opacity-70" title={document.sha256}>
						{copy.queueHash}: {document.sha256.slice(0, 12)}…
					</span>
					<span class="ms-auto truncate opacity-70">{document.output}</span>
				{:else if document.status === 'error'}
					<span class="ms-auto text-red-600 dark:text-red-400" role="alert">
						{document.description}
					</span>
				{/if}
			</li>
		{/each}
	</ol>

	<div class="flex flex-wrap items-center gap-3">
		<button
			type="button"
			disabled={signer === null || state.phase === 'running' || state.documents.length === 0}
			aria-describedby="sign-hint"
			onclick={run}
			class="rounded-lg bg-emerald-600 px-5 py-2 font-medium text-white hover:bg-emerald-700 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-emerald-700 disabled:cursor-not-allowed disabled:bg-ink-700 disabled:opacity-60"
		>
			{state.phase === 'running' ? copy.queueRunning : copy.signButton}
		</button>
		<button
			type="button"
			disabled={signed === 0}
			onclick={() => save(copy.queueArchive, buildZip(state), 'application/zip')}
			class="rounded-lg border px-5 py-2 font-medium hover:bg-brand-500 hover:text-white focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-600 disabled:cursor-not-allowed disabled:opacity-50 disabled:hover:bg-transparent disabled:hover:text-inherit"
		>
			{copy.queueDownload}
		</button>
	</div>

	{#if hint}
		<p id="sign-hint" class="text-sm opacity-70">{hint}</p>
	{/if}
</section>
