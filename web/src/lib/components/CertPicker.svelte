<script lang="ts">
	import type { Copy } from '#lib/i18n';
	import { loadSignerSession } from '#lib/keystore';
	import { Pkcs12Error } from '#lib/pkcs12';
	import type { SignerCert } from '#lib/sign';
	import { onDestroy } from 'svelte';

	interface Props {
		copy: Copy;
		/** Selected certificate, or null while nothing is loaded. */
		cert: SignerCert | null;
		onpick: (cert: SignerCert | null) => void;
	}

	let { copy, cert, onpick }: Props = $props();

	let pin = $state('');
	let file = $state<File | null>(null);
	let certs = $state<SignerCert[]>([]);
	let error = $state('');
	let loading = $state(false);
	/** Bumped whenever the picked file changes; a load holding an older id is discarded. */
	let loadId = 0;
	// Unmounted (the document was removed) invalidates an in-flight load, so it cannot
	// push the old container's signer back into the app after clearFile().
	onDestroy(() => {
		loadId += 1;
	});

	const dateFormat = new Intl.DateTimeFormat(undefined, { dateStyle: 'medium' });

	async function load() {
		const picked = file;
		if (!picked) return;
		const id = loadId;
		error = '';
		loading = true;
		certs = [];
		onpick(null);
		try {
			const session = await loadSignerSession(new Uint8Array(await picked.arrayBuffer()), pin);
			if (id !== loadId) return; // another file was picked while this load ran
			certs = session.certificates;
			// The container's own key decides the default; other certificates stay selectable but
			// cannot sign (no key), so they are disabled in the list.
			onpick(session.signer);
			pin = ''; // the key is imported now; the PIN is no longer needed in memory
		} catch (reason) {
			if (id !== loadId) return;
			error = copy.certErrors[reason instanceof Pkcs12Error ? reason.code : 'unknown'];
		} finally {
			if (id === loadId) loading = false;
		}
	}
</script>

<div class="flex flex-col gap-3 rounded-xl border p-4">
	<h3 class="font-medium">{copy.certTitle}</h3>

	<label class="text-sm font-medium" for="pkcs12-file">{copy.certFileLabel}</label>
	<input
		id="pkcs12-file"
		type="file"
		accept=".p12,.pfx,application/x-pkcs12"
		class="rounded-lg border bg-white/60 px-3 py-2 text-sm file:mr-3 file:rounded-md file:border-0 file:bg-brand-500 file:px-3 file:py-1 file:text-white dark:bg-ink-900/40"
		onchange={(event) => {
			// A different file invalidates whatever was loaded from the previous one.
			file = event.currentTarget.files?.[0] ?? null;
			loadId += 1;
			loading = false;
			certs = [];
			error = '';
			onpick(null);
		}}
	/>

	<label class="text-sm font-medium" for="pkcs12-pin">{copy.certPinLabel}</label>
	<div class="flex flex-wrap items-center gap-2">
		<input
			id="pkcs12-pin"
			type="password"
			autocomplete="off"
			bind:value={pin}
			class="min-w-40 flex-1 rounded-lg border bg-white/60 px-3 py-2 dark:bg-ink-900/40"
		/>
		<button
			type="button"
			class="rounded-lg bg-brand-500 px-3 py-2 text-sm font-medium text-white disabled:opacity-50"
			disabled={loading || file === null}
			onclick={load}>{loading ? copy.certLoading : copy.certLoad}</button
		>
	</div>

	{#if error}
		<p class="text-sm text-red-600 dark:text-red-400" role="alert">{error}</p>
	{:else}
		<p class="text-sm opacity-70">{copy.certHint}</p>
	{/if}

	<div class="flex flex-col gap-2">
		<label class="text-sm font-medium" for="cert-picker">{copy.certLabel}</label>
		<select
			id="cert-picker"
			class="rounded-lg border bg-white/60 px-3 py-2 disabled:opacity-60 dark:bg-ink-900/40"
			disabled={certs.length === 0}
			value={cert?.id ?? ''}
			onchange={(event) =>
				onpick(certs.find((option) => option.id === event.currentTarget.value) ?? null)}
		>
			{#if certs.length === 0}
				<option value="">{copy.certPlaceholder}</option>
			{/if}
			{#each certs as option (option.id)}
				<option value={option.id} disabled={option.key === null}>
					{option.commonName}{option.key === null ? ` (${copy.certNoKey})` : ''}
				</option>
			{/each}
		</select>

		{#if cert}
			<p class="text-sm opacity-70">{cert.subject}</p>
			<p class="text-sm opacity-70">{copy.certIssuer}: {cert.issuer}</p>
			<p class="text-sm opacity-70">
				{copy.certValidFrom}: {dateFormat.format(cert.notBefore)}
			</p>
			<p class="text-sm opacity-70">
				{copy.certValidity}: {dateFormat.format(cert.notAfter)}
			</p>
		{/if}
		{#if cert?.expired}
			<p class="text-sm text-amber-600 dark:text-amber-400">{copy.certExpired}</p>
		{/if}
		{#if cert?.notYetValid}
			<p class="text-sm text-amber-600 dark:text-amber-400">{copy.certNotYetValid}</p>
		{/if}
	</div>
</div>
