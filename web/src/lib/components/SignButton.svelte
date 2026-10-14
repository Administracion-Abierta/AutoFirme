<script lang="ts">
	interface Props {
		label: string;
		disabled: boolean;
		/** Why the button is disabled — announced with the button. */
		hint: string;
		onsign?: () => void;
	}

	let { label, disabled, hint, onsign }: Props = $props();
</script>

<div class="flex flex-col items-start gap-2">
	<button
		type="button"
		{disabled}
		onclick={onsign}
		aria-describedby="sign-hint"
		class="rounded-lg bg-emerald-600 px-6 py-2.5 font-medium text-white hover:bg-emerald-700 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-emerald-700 disabled:cursor-not-allowed disabled:bg-ink-700 disabled:opacity-60"
	>
		{label}
	</button>
	<p id="sign-hint" class="text-sm opacity-70">{hint}</p>
</div>
