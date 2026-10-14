<script lang="ts">
	import type { Copy } from '#lib/i18n';
	import type { SignResult } from '#lib/sign';

	interface Props {
		copy: Copy;
		result: SignResult | null;
	}

	let { copy, result }: Props = $props();
</script>

<section
	class="rounded-2xl border px-4 py-3 {result === null
		? 'border-dashed opacity-70'
		: result.ok
			? 'border-emerald-600'
			: 'border-red-600'}"
	aria-live="polite"
>
	<h3 class="text-sm font-semibold uppercase tracking-wide opacity-70">{copy.resultTitle}</h3>
	{#if result === null}
		<p class="mt-1 text-sm">{copy.resultEmpty}</p>
	{:else}
		<p class="mt-1 font-medium">{result.message}</p>
	{/if}
</section>
