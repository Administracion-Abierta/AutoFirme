<script lang="ts">
	import type { Copy } from '#lib/i18n';

	interface Props {
		copy: Copy;
		/** Receives every dropped or picked file, in order. */
		onselect: (files: File[]) => void;
	}

	let { copy, onselect }: Props = $props();
	let dragging = $state(false);
	let input = $state<HTMLInputElement | null>(null);

	function pick(files: ArrayLike<File> | null) {
		const picked = Array.from(files ?? []);
		if (picked.length > 0) onselect(picked);
	}
</script>

<section
	aria-label={copy.dropTitle}
	data-dragging={dragging}
	class="dropzone rounded-2xl border-2 border-dashed px-6 py-10 text-center transition"
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
	<div class="mx-auto flex max-w-md flex-col items-center gap-3">
		<svg
			aria-hidden="true"
			class="size-10 text-brand-500"
			viewBox="0 0 24 24"
			fill="none"
			stroke="currentColor"
			stroke-width="1.5"
		>
			<path d="M12 16V4m0 0L8 8m4-4 4 4" stroke-linecap="round" stroke-linejoin="round" />
			<path d="M4 15v3a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-3" stroke-linecap="round" />
		</svg>
		<p class="text-lg font-medium">{dragging ? copy.dragging : copy.dropTitle}</p>
		<p class="text-sm opacity-70">{copy.dropHint}</p>
		<input
			bind:this={input}
			class="sr-only"
			type="file"
			multiple
			tabindex="-1"
			aria-hidden="true"
			onchange={(event) => pick(event.currentTarget.files)}
		/>
		<button
			type="button"
			class="rounded-lg bg-brand-500 px-4 py-2 text-sm font-medium text-white hover:bg-brand-600 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-600"
			onclick={() => input?.click()}
		>
			{copy.browse}
		</button>
	</div>
</section>

<style>
	.dropzone {
		border-color: color-mix(in srgb, currentColor 25%, transparent);
	}
	.dropzone:hover,
	.dropzone[data-dragging='true'] {
		border-color: var(--color-brand-500);
	}
</style>
