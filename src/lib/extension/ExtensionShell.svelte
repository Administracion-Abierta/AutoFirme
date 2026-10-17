<script lang="ts">
	import App from '#lib/App.svelte';
	import { Button } from '#lib/components/ui/button/index.js';
	import type { Snippet } from 'svelte';
	import type { Lang } from '#lib/i18n';
	import SiteAccess from './SiteAccess.svelte';
	import { rpc } from './runtime';
	import { extensionCopy } from './copy';

	let error = $state('');

	async function openTab() {
		try {
			await rpc({ type: 'app:open', tab: true });
		} catch (reason) {
			error = reason instanceof Error ? reason.message : String(reason);
		}
	}
</script>

{#snippet extensionSettings(lang: Lang)}
	<!-- Rendered inside App's settings modal ("Ajustes y ayuda"), in the extension's section. -->
	<SiteAccess {lang} />
	<Button id="extension-full-tab" variant="outline" size="sm" class="h-auto min-h-8 whitespace-normal" onclick={openTab}>
		{extensionCopy[lang].fullTab}
	</Button>
	{#if error}<p role="alert" class="text-destructive text-sm">{error}</p>{/if}
{/snippet}

<App {extensionSettings} />
