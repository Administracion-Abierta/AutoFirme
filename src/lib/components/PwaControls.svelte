<script lang="ts">
	import { Button } from '#lib/components/ui/button/index.js';
	import { isDesktop } from '#lib/desktop';
	import type { Copy } from '#lib/i18n';
	import DownloadIcon from '@lucide/svelte/icons/download';
	import RotateCwIcon from '@lucide/svelte/icons/rotate-cw';
	import { onMount } from 'svelte';

	interface Props {
		copy: Copy;
	}

	let { copy }: Props = $props();

	/** Chromium's deferred install prompt: cancelling it is what lets the button replay it. */
	type InstallPrompt = Event & { prompt: () => Promise<void> };

	/** Non-null while the browser is offering an install, i.e. while the button is worth showing. */
	let installable = $state<InstallPrompt | null>(null);
	/** The downloaded worker waiting to take over — a newer deploy of this app. */
	let waiting = $state<ServiceWorker | null>(null);

	onMount(() => {
		// No service worker and no install prompt under `tauri://localhost`.
		if (isDesktop() || location.protocol === 'chrome-extension:') return;
		// `beforeinstallprompt` is Chromium-only and missing from lib.dom, hence the bare EventTarget.
		const events = window as EventTarget;
		const onInstallable = (event: Event) => {
			if (event.cancelable) event.preventDefault();
			installable = event as InstallPrompt;
		};
		events.addEventListener('beforeinstallprompt', onInstallable);

		let reloading = false;
		// `clients.claim()` makes the very first worker's arrival fire `controllerchange` too —
		// reloading then would throw away whatever a new visitor typed seconds after arrival.
		// Real updates arrive under a controller, so only those reload.
		let claimed = false;
		const onControllerChange = () => {
			if (!claimed) {
				claimed = true;
				return;
			}
			// One reload only: activating a worker can fire this more than once.
			if (reloading) return;
			reloading = true;
			location.reload();
		};
		// jsdom and browsers with service workers disabled have no container to watch.
		if ('serviceWorker' in navigator) {
			claimed = !!navigator.serviceWorker.controller;
			navigator.serviceWorker.addEventListener('controllerchange', onControllerChange);
			// SvelteKit registers the worker itself; this only watches that registration for a new
			// version arriving in the background.
			void navigator.serviceWorker.getRegistration().then((registration) => {
				if (!registration) return;
				if (registration.waiting) waiting = registration.waiting;
				registration.addEventListener('updatefound', () => {
					const installing = registration.installing;
					installing?.addEventListener('statechange', () => {
						// Only worth offering when an older worker is still in control: on a first
						// install there is no previous version to be behind.
						if (installing.state === 'installed' && navigator.serviceWorker.controller) {
							waiting = installing;
						}
					});
				});
			});
		}

		return () => {
			events.removeEventListener('beforeinstallprompt', onInstallable);
			if ('serviceWorker' in navigator) {
				navigator.serviceWorker.removeEventListener('controllerchange', onControllerChange);
			}
		};
	});

	/** Replays the deferred prompt. It is single-use, so the button retires with it. */
	async function install() {
		const prompt = installable;
		installable = null;
		await prompt?.prompt();
	}
</script>

{#if installable}
	<Button variant="outline" size="sm" class="h-8 px-3" onclick={install}>
		<DownloadIcon class="size-4" />
		{copy.installApp}
	</Button>
{/if}

{#if waiting}
	<Button
		size="sm"
		class="h-8 px-3"
		onclick={() => waiting?.postMessage({ type: 'SKIP_WAITING' })}
	>
		<RotateCwIcon class="size-4" />
		{copy.updateApp}
	</Button>
{/if}
