<script lang="ts">
	import { Button } from '#lib/components/ui/button/index.js';
	import { EMPTY_BATCH_STATE, type BatchQueue, type BatchState } from '#lib/batch';

	import type { Copy } from '#lib/i18n';
	interface Props {
		copy: Copy;
		queue: BatchQueue;
	}

	let { copy, queue }: Props = $props();

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
</script>

<section class="flex flex-col gap-3">
	<header class="flex flex-wrap items-center justify-between gap-2">
		<h3 class="text-sm font-medium">{copy.queueTitle}</h3>
		<p class="text-muted-foreground text-sm" aria-live="polite">
			{`${progressLabel}: ${signed}/${state.documents.length}`}
		</p>
	</header>

	{#if state.phase === 'running'}
		<!-- The run was started on step 2; while it is in flight the only action is stopping it. -->
		<Button variant="outline" onclick={() => queue.cancel()}>{copy.queueCancel}</Button>
	{/if}
</section>
