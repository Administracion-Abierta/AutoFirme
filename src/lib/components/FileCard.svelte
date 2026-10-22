<script lang="ts">
	import { Badge } from '#lib/components/ui/badge/index.js';
	import { Button } from '#lib/components/ui/button/index.js';
	import { Spinner } from '#lib/components/ui/spinner/index.js';
	import { TableCell, TableRow } from '#lib/components/ui/table/index.js';
	import Trash2Icon from '@lucide/svelte/icons/trash-2';
	import FileIcon from '@lucide/svelte/icons/file';
	import { saveBytes } from '#lib/save-file';
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
		/** Rendered in the name cell beside the icon (no longer used on step 2). */
		extra?: Snippet;
		/** Rendered in the actions cell before the remove button (step 2: per-file format). */
		actions?: Snippet;
		/** False hides the remove button (step 3, where removing a signed row makes no sense). */
		removable?: boolean;
		/** False hides the per-file download (step 2 has nothing signed to download yet). */
		downloadable?: boolean;
	}

	let {
		copy,
		file,
		onremove,
		document: entry = null,
		extra,
		actions,
		removable = true,
		downloadable = true
	}: Props = $props();

	const status = $derived<DocumentStatus | null>(entry?.status ?? null);

	/** Saves the signature this row already holds in the queue — no re-signing, no fetch.
	 *  Native save dialog on desktop, anchor download in the browser. */
	async function downloadSigned() {
		if (!entry?.bytes) return;
		await saveBytes(
			entry.output || `${file.name}.csig`,
			entry.bytes as Uint8Array<ArrayBuffer>,
			'application/octet-stream'
		);
	}
</script>

<TableRow data-status={status ?? undefined} aria-busy={status === 'signing'}>
	<TableCell class="max-w-0 py-3" title={file.name}>
		<span class="flex min-w-0 items-center gap-3">
			{#if status === 'signing'}
				<!-- The signing animation lives on the file's own row. -->
				<Spinner class="size-4 shrink-0" />
			{/if}
			<FileIcon class="text-muted-foreground size-5 shrink-0" aria-hidden="true" />
			{#if extra}{@render extra()}{/if}
			<span class="flex min-w-0 flex-1 flex-col w-full">
				<span class="truncate text-base font-medium">{file.name}</span>
				<span class="block text-xs font-normal tracking-wide text-muted-foreground">
					{formatBytes(file.size)}
				</span>
			</span>
			{#if status && status !== 'signing' && status !== 'pending'}
				<Badge variant={status === 'signed' ? 'default' : status === 'error' ? 'destructive' : 'outline'}>
					{copy.queueStatus[status]}
				</Badge>
			{/if}
		</span>
	</TableCell>
	<TableCell class="w-0 py-3 pe-3 ps-0 text-right whitespace-nowrap">
		{#if actions}{@render actions()}{/if}
		{#if removable}
			<Button
				variant="ghost"
				size="icon"
				class="text-destructive hover:text-destructive size-6"
				aria-label={copy.remove}
				title={copy.remove}
				onclick={onremove}
			>
				<Trash2Icon class="size-3.5" aria-hidden="true" />
			</Button>
		{/if}
		{#if downloadable}
			<Button
				variant="ghost"
				size="sm"
				class="h-7"
				disabled={status !== 'signed' || !entry?.bytes}
				onclick={downloadSigned}
			>
				{copy.downloadOne}
			</Button>
		{/if}
	</TableCell>
</TableRow>
