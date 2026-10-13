<script lang="ts">
	import type { Copy } from '#lib/i18n';
	import type { SignerCert } from '#lib/sign';

	interface Props {
		copy: Copy;
		/** Null until #5 wires PKCS#12 loading; the picker is inert by design. */
		cert: SignerCert | null;
		/** PKCS#12 / PIN inputs land here in #5. */
		onpick?: (cert: SignerCert | null) => void;
	}

	let { copy, cert, onpick: _onpick }: Props = $props();
</script>

<div class="flex flex-col gap-2">
	<label class="text-sm font-medium" for="cert-picker">{copy.certLabel}</label>
	<select
		id="cert-picker"
		class="rounded-lg border bg-white/60 px-3 py-2 disabled:opacity-60 dark:bg-ink-900/40"
		disabled
		onchange={() => {}}
	>
		<option selected={cert === null}>{copy.certPlaceholder}</option>
	</select>
	<p class="text-sm opacity-70">{copy.certPending}</p>
</div>
