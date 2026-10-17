<script lang="ts">
	import { Badge } from '#lib/components/ui/badge/index.js';
	import { Button } from '#lib/components/ui/button/index.js';
	import { Spinner } from '#lib/components/ui/spinner/index.js';
	import { TableCell, TableRow } from '#lib/components/ui/table/index.js';
	import { formatBytes } from '#lib/sign';
	import type { Snippet } from 'svelte';
	import type { BatchDocument, DocumentStatus } from '#lib/batch';
	import type { Copy } from '#lib/i18n';
	import type { SelectedFile } from '#lib/sign';

	interface Props {
		copy: Copy;
		file: SelectedFile;
		onremove: () => void;
		/** Queue entry for this document; null/absent while it is not in a batch (verify flow). */
		document?: BatchDocument | null;
		/** Cells rendered between the file and the actions column (step 2: per-file format). */
		extra?: Snippet;
		/** False hides the remove button (step 3, where removing a signed row makes no sense). */
		removable?: boolean;
	}

	let {
		copy,
		file,
		onremove,
		document: entry = null,
		extra,
		removable = true
	}: Props = $props();

	const status = $derived<DocumentStatus | null>(entry?.status ?? null);

	/** Saves the signature this row already holds in the queue — no re-signing, no fetch. */
	function downloadSigned() {
		if (!entry?.bytes) return;
		const url = URL.createObjectURL(new Blob([entry.bytes], { type: 'application/octet-stream' }));
		const link = document.createElement('a');
		link.href = url;
		link.download = entry.output || `${file.name}.csig`;
		link.click();
		URL.revokeObjectURL(url);
	}
</script>

<TableRow data-status={status ?? undefined} aria-busy={status === 'signing'}>
	<TableCell class="max-w-0 py-3" title={file.name}>
		<span class="flex items-center gap-2">
			{#if status === 'signing'}
				<!-- The signing animation lives on the file's own row. -->
				<Spinner class="size-4 shrink-0" />
			{/if}
			<span class="truncate font-medium">{file.name}</span>
			{#if status && status !== 'signing'}
				<Badge variant={status === 'signed' ? 'default' : status === 'error' ? 'destructive' : 'outline'}>
					{copy.queueStatus[status]}
				</Badge>
			{/if}
		</span>
		<span class="block text-xs font-normal tracking-wide text-muted-foreground">
			{formatBytes(file.size)}
		</span>
	</TableCell>
	{#if extra}{@render extra()}{/if}
	<TableCell class="py-3 text-right">
		{#if removable}
			<Button variant="ghost" size="sm" class="h-7" onclick={onremove}>{copy.remove}</Button>
		{/if}
		<Button
			variant="ghost"
			size="sm"
			class="h-7"
			disabled={status !== 'signed' || !entry?.bytes}
			onclick={downloadSigned}
		>
			{copy.downloadOne}
		</Button>
	</TableCell>
</TableRow>
