<script lang="ts">
	import { Button } from '#lib/components/ui/button/index.js';
	import { Card, CardContent } from '#lib/components/ui/card/index.js';
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
	}

	let { copy, onselect, title, hint, multiple = true }: Props = $props();
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
		'min-h-44 w-full shrink-0 cursor-pointer border-2 border-dashed bg-card/50 shadow-none ring-0 transition-colors',
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
	<CardContent class="flex flex-col items-center gap-3 overflow-hidden px-6 py-12 text-center">
		<!-- Document-with-plus icon from the operator's reference, drawn in one stroke weight. -->
		<svg
			aria-hidden="true"
			class={cn(
				'size-12 transition-colors',
				dragging ? 'text-primary' : 'text-muted-foreground'
			)}
			viewBox="0 0 24 24"
			fill="none"
			stroke="currentColor"
			stroke-width="1.5"
		>
			<path
				d="M14 3v4a1 1 0 0 0 1 1h4"
				stroke-linecap="round"
				stroke-linejoin="round"
			/>
			<path
				d="M5 8V5a2 2 0 0 1 2-2h7l5 5v11a2 2 0 0 1-2 2h-3"
				stroke-linecap="round"
				stroke-linejoin="round"
			/>
			<path d="M9 21H5a2 2 0 0 1-2-2v-4m6 0h.01M12 15h.01M9 18h.01M12 18h.01" stroke-linecap="round" />
			{#if dragging}
				<path d="M12 3v6m0 0 2.5-2.5M12 9 9.5 6.5" stroke-linecap="round" stroke-linejoin="round" />
			{:else}
				<path d="M12 15v-3m0 0-1.5 1.5M12 12l1.5 1.5" stroke-linecap="round" stroke-linejoin="round" />
			{/if}
		</svg>
		<p class="text-lg font-medium">
			{dragging ? copy.dragging : (title ?? copy.dropTitle)}
			{#if !dragging}
				<!-- "browse" is the operator's teal accent word; the whole zone is the trigger. -->
				<span class="text-primary underline decoration-primary/40 underline-offset-4">
					{copy.browse}
				</span>
			{/if}
		</p>
		<p class="text-muted-foreground text-sm">{hint ?? copy.dropHint}</p>
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
