<script lang="ts">
	import App from '#lib/App.svelte';
	import { isExtension } from '#lib/extension/runtime';
	import ExtensionShell from '#lib/extension/ExtensionShell.svelte';
	import RequestChooser from '#lib/extension/RequestChooser.svelte';
	const extension = isExtension();
	const requestId = extension ? new URLSearchParams(location.search).get('request') : null;
</script>

<svelte:head>
	<title>AutoFirma Web</title>
</svelte:head>

{#if extension}
	{#if requestId}<RequestChooser id={requestId}/>{:else}<ExtensionShell/>{/if}
{:else}
	<App />
{/if}
