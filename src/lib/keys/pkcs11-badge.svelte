<script lang="ts">
	/**
	 * Certificate source badge (#100): says which module a certificate came from, so the picker
	 * never shows a token key as if it were a file upload. Renders nothing for a PKCS#12 upload —
	 * there the file name is the badge.
	 */
	import { Badge } from '#lib/components/ui/badge/index.js';

	interface Props {
		/** Certificate source: `pkcs11:<module>` or `pkcs12` (see `certificateSource`). */
		source: string;
		/** Slot the certificate lives on; its description is the badge's tooltip. */
		slot?: { id: number; description: string } | null;
	}

	let { source, slot = null }: Props = $props();

	/** Slot identity only means something for a token; module alone is not enough to pick a key. */
	const title = $derived(slot ? `${source} — ${slot.description} (slot ${slot.id})` : source);
</script>

{#if source.startsWith('pkcs11:')}
	<Badge variant="outline" {title} data-cert-source={source}>{source}</Badge>
{/if}
