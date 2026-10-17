<script lang="ts">
	import { Button } from '#lib/components/ui/button/index.js';
	import { Card, CardContent } from '#lib/components/ui/card/index.js';
	import FilePlusIcon from '@lucide/svelte/icons/file-plus';
	import { cn } from '#lib/utils.js';
	import type { Copy } from '#lib/i18n';

	interface Props {
		copy: Copy;
		/** Receives every dropped or picked file, in order. */
		onselect: (files: File[]) => void;
		/** Overrides the drop-step wording (the verify flow reuses this component). */
		title?: string;
		hint?: string;
		/** Single-file selection where only one artifact matters (the verify flow). */
		multiple?: boolean;
		/** Row-sized tile (same footprint as a file row) for adding more files beside a list. */
		compact?: boolean;
	}

	let { copy, onselect, title, hint, multiple = true, compact = false }: Props = $props();
	let dragging = $state(false);
	let input = $state<HTMLInputElement | null>(null);

	function pick(files: ArrayLike<File> | null) {
		const picked = Array.from(files ?? []);
		if (picked.length > 0) onselect(picked);
	}
</script>

<Card
	aria-label={title ?? copy.dropTitle}
	class={cn(
		// Fixed footprint: the zone keeps its height and full width however many documents the panel
		// holds, so the table below it is what scrolls.
		compact
			? 'w-full shrink-0 cursor-pointer rounded-none border-0 border-b border-dashed shadow-none ring-0 bg-transparent transition-colors'
			: 'min-h-44 w-full shrink-0 cursor-pointer border-2 border-dashed bg-card/50 shadow-none ring-0 transition-colors',
		dragging ? 'border-primary bg-primary/5' : 'border-border hover:border-primary/60'
	)}
	role="button"
	tabindex={0}
	onclick={() => input?.click()}
	onkeydown={(event) => {
		if (event.key === 'Enter' || event.key === ' ') {
			event.preventDefault();
			input?.click();
		}
	}}
	ondragover={(event) => {
		event.preventDefault();
		dragging = true;
	}}
	ondragleave={() => (dragging = false)}
	ondrop={(event) => {
		event.preventDefault();
		dragging = false;
		pick(event.dataTransfer?.files ?? null);
	}}
>
	<CardContent
		class={cn(
			compact
				? 'flex h-14 items-center gap-3 overflow-hidden px-3 py-0 text-left'
				: 'flex flex-col items-center gap-3 overflow-hidden px-6 py-12 text-center'
		)}
	>
		<FilePlusIcon
			aria-hidden="true"
			class={cn(
				compact ? 'size-6 shrink-0' : 'size-12',
				'transition-colors',
				dragging ? 'text-primary' : 'text-muted-foreground'
			)}
			strokeWidth={1.5}
		/>
		<p class={cn(compact ? 'truncate text-sm font-medium' : 'text-lg font-medium')}>
			{dragging ? copy.dragging : (title ?? copy.dropTitle)}
			{#if !dragging}
				<!-- "browse" is the operator's teal accent word; the whole zone is the trigger. -->
				<span class="text-primary underline decoration-primary/40 underline-offset-4">
					{copy.browse}
				</span>
			{/if}
		</p>
		{#if !compact}<p class="text-muted-foreground text-sm">{hint ?? copy.dropHint}</p>{/if}
		<input
			bind:this={input}
			class="sr-only"
			type="file"
			multiple={multiple}
			tabindex="-1"
			aria-hidden="true"
			onchange={(event) => pick(event.currentTarget.files)}
		/>
	</CardContent>
</Card>
