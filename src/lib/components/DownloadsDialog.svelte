<script lang="ts">
	import { StoreError, type OutputMeta, type Vault } from '#lib/certstore';
	import { Button } from '#lib/components/ui/button/index.js';
	import type { Copy } from '#lib/i18n';
	import { formatBytes } from '#lib/sign';

	interface Props {
		copy: Copy;
		vault: Vault;
	}

	let { copy, vault }: Props = $props();

	let panel = $state<HTMLDialogElement | null>(null);
	let outputs = $state<OutputMeta[]>([]);
	let error = $state('');

	/** Opens the dialog and re-reads the store, so signatures of this visit show up right away. */
	export function open() {
		panel?.showModal();
		void refresh();
	}

	async function refresh() {
		try {
			outputs = await vault.listOutputs();
		} catch (reason) {
			error = reason instanceof StoreError ? copy.storeErrors[reason.code] : copy.storeErrors.unavailable;
		}
	}

	async function download(meta: OutputMeta) {
		const bytes = await vault.readOutput(meta.id);
		if (!bytes) return;
		const url = URL.createObjectURL(new Blob([bytes], { type: meta.mime }));
		const link = document.createElement('a');
		link.href = url;
		link.download = meta.name;
		link.click();
		URL.revokeObjectURL(url);
	}

	async function remove(id: string) {
		error = '';
		try {
			await vault.removeOutput(id);
			await refresh();
		} catch (reason) {
			error = reason instanceof StoreError ? copy.storeErrors[reason.code] : copy.storeErrors.unavailable;
		}
	}
</script>

<Button variant="outline" size="sm" class="h-8" aria-haspopup="dialog" onclick={open}
	>{copy.downloadsLabel}</Button
>

<!-- Native dialog on purpose: the vault must work even if bundled UI code fails to load. -->
<dialog
	bind:this={panel}
	class="bg-card text-card-foreground max-w-lg rounded-xl border p-5 backdrop:bg-black/40"
>
	<div class="flex flex-col gap-4">
		<header class="flex items-center justify-between gap-3">
			<h2 class="font-display text-lg font-semibold">{copy.downloadsTitle}</h2>
			<Button variant="ghost" size="sm" class="h-8" onclick={() => panel?.close()}>
				{copy.downloadsClose}
			</Button>
		</header>

		{#if outputs.length === 0}
			<p class="text-muted-foreground text-sm">{copy.downloadsEmpty}</p>
		{:else}
			<ul class="flex flex-col gap-2">
				{#each outputs as output (output.id)}
					<li class="bg-muted/60 flex flex-wrap items-center gap-2 rounded-lg border px-3 py-2 text-sm">
						<span class="font-medium">{output.name}</span>
						<span class="text-muted-foreground">{output.commonName}</span>
						<span class="text-muted-foreground tnum">{formatBytes(output.size)}</span>
						<span class="text-muted-foreground tnum font-mono text-xs" title={output.sha256}>
							{output.sha256.slice(0, 12)}…
						</span>
						<div class="ms-auto flex items-center gap-1.5">
							<Button variant="outline" size="sm" class="h-8" onclick={() => download(output)}>
								{copy.downloadsDownload}
							</Button>
							<Button
								variant="ghost"
								size="sm"
								class="text-destructive hover:bg-destructive/10 hover:text-destructive h-8"
								onclick={() => remove(output.id)}>{copy.remove}</Button
							>
						</div>
					</li>
				{/each}
			</ul>
		{/if}

		{#if error}
			<p class="text-destructive text-sm" role="alert">{error}</p>
		{/if}
	</div>
</dialog>
