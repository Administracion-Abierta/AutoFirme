<script lang="ts">
	import { Button } from '#lib/components/ui/button/index.js';
	import {
		EMPTY_BATCH_STATE,
		buildZip,
		type BatchQueue,
		type BatchSigner,
		type BatchState
	} from '#lib/batch';
	import {
		Tooltip,
		TooltipContent,
		TooltipTrigger
	} from '#lib/components/ui/tooltip/index.js';

	import type { Copy } from '#lib/i18n';
	interface Props {
		copy: Copy;
		queue: BatchQueue;
		/** Format engine; null while the format's engine (#6 CAdES, #7 XAdES, #11 PAdES) is missing. */
		signer: BatchSigner | null;
		/** Why the queue cannot run yet — announced with the run button. */
		hint: string;
		/** Called with the final state after a run, so a caller can keep the signed bytes (#29). */
		onsigned?: (state: BatchState) => void;
	}

	let { copy, queue, signer, hint, onsigned }: Props = $props();

	let state = $state<BatchState>(EMPTY_BATCH_STATE);
	// The run loop writes to the store from async code, so the component mirrors it. Returning the
	// store's unsubscribe keeps the subscriber list clean when the queue is unmounted.
	$effect(() => queue.subscribe((value) => (state = value)));

	const signed = $derived(state.documents.filter((document) => document.status === 'signed').length);
	// One label per phase, so a stopped run says so instead of reading as a finished one.
	const progressLabel = $derived(
		{
			idle: copy.queueProgress,
			running: copy.queueRunning,
			done: copy.queueProgress,
			cancelled: copy.queueCancelled
		}[state.phase]
	);


	async function run() {
		if (!signer) return;
		// Two statements on purpose: in `f?.(await g())` the arguments are not evaluated when `f`
		// is nullish, so the run would be skipped whenever no `onsigned` was passed.
		const done = await queue.run(signer);
		onsigned?.(done);
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

<section class="flex flex-col gap-3">
	<header class="flex flex-wrap items-center justify-between gap-2">
		<h3 class="text-sm font-medium">{copy.queueTitle}</h3>
		<div class="flex flex-wrap items-center gap-2">
			<p class="text-muted-foreground text-sm" aria-live="polite">
				{`${progressLabel}: ${signed}/${state.documents.length}`}
			</p>
			<Button
				variant="outline"
				size="sm"
				class="h-8"
				disabled={signed === 0}
				onclick={() => save(copy.queueArchive, buildZip(state), 'application/zip')}
			>
				{copy.queueDownload}
			</Button>
		</div>
	</header>


	{#snippet signButton()}
		{#if state.phase === 'running'}
			<!-- The primary action becomes its own cancel: one button, no second place to look. -->
			<Button
				size="lg"
				variant="outline"
				class="h-11 w-full text-base"
				onclick={() => queue.cancel()}
			>
				{copy.queueCancel}
			</Button>
		{:else}
			<Button
				size="lg"
				class="h-11 w-full text-base"
				disabled={signer === null || state.documents.length === 0}
				aria-describedby="sign-hint"
				onclick={run}
			>
				{copy.signButton}
			</Button>
		{/if}
	{/snippet}

	<div class="flex flex-col gap-2">
		{#if hint}
			<!-- The hint explains why signing is blocked, so it belongs on the button itself too. -->
			<Tooltip>
				<TooltipTrigger>
					{#snippet child({ props })}
						<!-- Disabled buttons swallow pointer events, so the hover lives on a wrapper. -->
						<span {...props} class="contents">{@render signButton()}</span>
					{/snippet}
				</TooltipTrigger>
				<TooltipContent>{hint}</TooltipContent>
			</Tooltip>
		{:else}
			{@render signButton()}
		{/if}
		{#if hint}
			<p id="sign-hint" class="text-muted-foreground text-sm">{hint}</p>
		{/if}
	</div>
</section>
