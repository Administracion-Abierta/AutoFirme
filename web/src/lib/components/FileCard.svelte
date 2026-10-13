<script lang="ts">
	import type { Copy } from '#lib/i18n';
	import type { SelectedFile } from '#lib/sign';
	import { formatBytes } from '#lib/sign';

	interface Props {
		copy: Copy;
		file: SelectedFile;
		onremove: () => void;
	}

	let { copy, file, onremove }: Props = $props();
</script>

<div class="flex items-center justify-between gap-4 rounded-xl bg-ink-100 px-4 py-3 dark:bg-ink-700">
	<div class="min-w-0">
		<p class="text-xs uppercase tracking-wide opacity-60">{copy.fileLabel}</p>
		<p class="truncate font-medium" title={file.name}>{file.name}</p>
		<p class="text-sm opacity-70">
			{formatBytes(file.size)}{file.type ? ` · ${file.type}` : ''}
		</p>
	</div>
	<button
		type="button"
		class="shrink-0 rounded-lg border px-3 py-1.5 text-sm hover:bg-brand-500 hover:text-white focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-600"
		onclick={onremove}
	>
		{copy.remove}
	</button>
</div>
