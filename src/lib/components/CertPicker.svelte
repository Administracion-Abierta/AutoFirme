<script lang="ts">
	import { tick } from 'svelte';
	import { StoreError, type IdentityMeta, type Vault } from '#lib/certstore';
	import { Alert, AlertDescription, AlertTitle } from '#lib/components/ui/alert/index.js';
	import { Badge } from '#lib/components/ui/badge/index.js';
	import { Button } from '#lib/components/ui/button/index.js';
	import {
		Dialog,
		DialogContent,
		DialogDescription,
		DialogHeader,
		DialogTitle
	} from '#lib/components/ui/dialog/index.js';
	import { Input } from '#lib/components/ui/input/index.js';
	import type { Copy } from '#lib/i18n';
	import { loadSignerSession } from '#lib/keystore';
	import {
		forgetPin,
		readPinMemorySettings,
		rememberedPin,
		rememberPin as cachePin,
		savePinMemorySettings
	} from '#lib/pin-memory';
	import { Pkcs12Error } from '#lib/pkcs12';
	import type { SignerCert, SignerSession } from '#lib/sign';
	import { cn } from '#lib/utils.js';
	import FileIcon from '@lucide/svelte/icons/file';
	import ArrowRightIcon from '@lucide/svelte/icons/arrow-right';
	import PlusIcon from '@lucide/svelte/icons/plus';
	import PencilIcon from '@lucide/svelte/icons/pencil';
	import TriangleAlertIcon from '@lucide/svelte/icons/triangle-alert';
	import XIcon from '@lucide/svelte/icons/x';

	interface Props {
		/** Compact extension chooser trigger and identity rows. */
		compact?: boolean;
		detailsLabel?: string;
		copy: Copy;
		/** Selected certificate, or null while nothing is loaded. */
		cert: SignerCert | null;
		/** Client-side store of saved identities (#29). */
		vault: Vault;
		/** Cert, chain, and saved-identity id (null for a freshly imported container). */
		onpick: (cert: SignerCert | null, chain: readonly SignerCert[], identityId?: string | null) => void;
		/** Identity an extension request asks for; the picker opens its PIN prompt directly. */
		initialId?: string | null;
	}

	let {
		copy,
		cert,
		vault,
		onpick,
		compact = false,
		detailsLabel = 'Certificate details',
		initialId = null
	}: Props = $props();

	let file = $state<File | null>(null);
	// A picked file already counts as a chosen signature: the dotted dim look is only the empty state.
	const chosen = $derived(file !== null || cert !== null);
	/** Name shown on the picker button: the chosen file, or the label of an unlocked saved sign. */
	let pickedName = $state('');
	let pickerOpen = $state(false);
	let identities = $state<IdentityMeta[]>([]);
	// An MCP job names one identity: hide every other row and the import/remove paths, so the
	// operator cannot hand the agent a certificate it did not ask for.
	const offered = $derived(initialId ? identities.filter(entry => entry.id === initialId) : identities);
	const visibleIdentities = $derived(compact ? offered.filter(entry => entry.id !== usedId) : offered);
	// `vault.persistent` is a plain getter: the store can fall back to memory at any moment, and
	// only a state of our own makes the notice appear when it does.
	let persistent = $state(true);
	/** Identity whose unlock form is open; unlocking is per session, so the PINs never persist. */
	let openId = $state<string | null>(null);
	/** The saved signature currently in use, so a click on it means "select this one". */
	let usedId = $state<string | null>(null);
	/** Identity whose alias is being edited, plus the draft text. */
	let renameId = $state<string | null>(null);
	let renameLabel = $state('');
	let storePin = $state('');
	/** PIN of the sign being added from the modal — distinct from `storePin`, which unlocks saved ones. */
	let addPin = $state('');
	let adding = $state(false);
	const pinSettings = readPinMemorySettings();
	let rememberPin = $state(pinSettings.remember);
	const rememberLabel = $derived(copy.rememberPin.replace('{minutes}', String(pinSettings.minutes)));
	let autoSelected = $state(false);
	/**
	 * Sessions opened this visit, keyed by identity id: a sign already unsealed can be selected
	 * again without its PIN, so the next click on it just picks it and closes the modal.
	 */
	const unlockedSessions = new Map<string, SignerSession>();
	/** The container opened for the sign in use; null while nothing is loaded. */
	let session = $state<SignerSession | null>(null);
	let storeError = $state('');
	let fileInput = $state<HTMLInputElement | null>(null);
	// The compact chooser has no dialog focus trap: the pressed row unmounts when its PIN form
	// opens, and an applied sign leaves the list. Park focus on a node that survives each change.
	$effect(() => {
		if (compact && openId) void tick().then(() => document.getElementById('store-pin')?.focus());
	});
	$effect(() => {
		if (compact && usedId && cert)
			void tick().then(() => document.getElementById('pkcs12-selection')?.focus());
	});

	const dateFormat = new Intl.DateTimeFormat(undefined, { dateStyle: 'medium' });

	/** The sign in memory, when a file was added and the grid does not list it yet: the first cell. */
	const pending = $derived(
		file !== null &&
			!identities.some((entry) => {
				const signer = session?.signer ?? cert;
				return (
					signer !== null &&
					entry.commonName === signer.commonName &&
					entry.notAfter === signer.notAfter.toISOString()
				);
			})
	);

	// Load the same saved identities for the full-app modal and compact request chooser.
	$effect(() => {
		void refresh();
	});

	// A request from an MCP agent names the certificate it wants: open that identity at once,
	// reusing a remembered PIN when the operator opted in.
	$effect(() => {
		if (!initialId || autoSelected || !identities.some((entry) => entry.id === initialId)) return;
		autoSelected = true;
		void selectInitial(initialId);
	});

	function persistRemember(): void {
		savePinMemorySettings({ ...pinSettings, remember: rememberPin });
	}

	async function selectInitial(id: string): Promise<void> {
		const pin = await rememberedPin(id);
		if (pin !== null) await unlock(id, pin, true);
		else openId = id;
	}

	function reasonText(reason: unknown): string {
		if (reason instanceof StoreError) return copy.storeErrors[reason.code];
		// An insecure origin (http away from localhost): the browser hides `crypto.subtle` and the
		// load dies with a TypeError. The cause is the page, not the file — say that instead.
		if (!globalThis.crypto?.subtle) return copy.insecureContext;
		if (reason instanceof Pkcs12Error) return copy.certErrors[reason.code];
		return copy.certErrors.unknown;
	}

	async function refresh() {
		try {
			identities = await vault.listIdentities();
			persistent = vault.persistent;
			console.debug('[autofirme:picker] identities refreshed', identities.length);
		} catch (reason) {
			storeError = reasonText(reason);
			persistent = vault.persistent;
			console.error('[autofirme:picker] listIdentities failed', reason, storeError);
		}
	}

	function reset() {
		session = null;
		addPin = '';
		usedId = null;
		onpick(null, [], null);
	}

	/**
	 * Clicking a saved signature selects it and closes the modal — unless this visit has never
	 * opened it, in which case its PIN is asked for first: the private key cannot be used before
	 * that PIN has unsealed the record.
	 */
	function select(identity: IdentityMeta) {
		storeError = '';
		const sign = unlockedSessions.get(identity.id);
		if (sign) {
			apply(identity.id, sign);
			pickerOpen = false;
			return;
		}
		openId = openId === identity.id ? null : identity.id;
		storePin = '';
		void autoSelect(identity.id);
	}

	/** Reuse a remembered PIN without showing the prompt; a rejected PIN reopens it. */
	async function autoSelect(id: string): Promise<void> {
		const pin = await rememberedPin(id);
		if (pin !== null) await unlock(id, pin, true);
	}

	/** Hands an opened session to the app and marks its identity as the sign in use. */
	function apply(id: string, sign: SignerSession) {
		session = sign;
		onpick(sign.signer, sign.certificates, id);
		pickedName = identities.find((entry) => entry.id === id)?.label ?? copy.storeTitle;
		usedId = id;
		openId = null;
		storePin = '';
	}

	function picked(next: File | null) {
		if (compact) openId = null;
		file = next;
		pickedName = next?.name ?? '';
		if (next) console.debug('[autofirme:picker] file picked', next.name, next.size);
		reset();
	}

	/** Opens a saved signature: its PIN unseals the blob and opens the container inside it. */
	async function unlock(id: string, pin = storePin, silent = false) {
		storeError = '';
		console.debug('[autofirme:picker] unlock start', id);
		try {
			const sign = await vault.unlockIdentity(id, pin);
			unlockedSessions.set(id, sign);
			if (rememberPin) await cachePin(id, pin);
			else await forgetPin(id);
			apply(id, sign);
			pickerOpen = false;
		} catch (reason) {
			if (silent) await forgetPin(id);
			storeError = reasonText(reason);
			persistent = vault.persistent;
			openId = id;
			storePin = '';
			console.error('[autofirme:picker] unlock failed', reason, storeError);
		}
	}

	/**
	 * The modal's add flow: the picked file is opened with its PIN and sealed into the store at
	 * once, so an upload outlives the visit and every further sign joins the grid instead of
	 * replacing the one held in memory. The fresh sign is selected on the spot and the modal
	 * closes with it.
	 */
	async function saveAdded() {
		const source = file;
		const enteredPin = addPin;
		if (!source || enteredPin === '' || adding) return;
		storeError = '';
		adding = true;
		console.debug('[autofirme:picker] add start', source.name);
		try {
			const bytes = new Uint8Array(await source.arrayBuffer());
			const sign = await loadSignerSession(bytes, enteredPin);
			const meta = await vault.saveIdentity({
				label: source.name,
				session: sign,
				container: bytes,
				pin: enteredPin
			});
			unlockedSessions.set(meta.id, sign);
			if (rememberPin) await cachePin(meta.id, enteredPin);
			await refresh();
			// Another file picked while this one was sealed: the record stands, the new pick
			// drives the panel — selecting the old sign here would override it.
			if (file === source) {
				addPin = '';
				apply(meta.id, sign);
				pickerOpen = false;
			}
			console.debug('[autofirme:picker] add saved', meta.label);
		} catch (reason) {
			storeError = reasonText(reason);
			persistent = vault.persistent;
			console.error('[autofirme:picker] add failed', reason, storeError);
		} finally {
			adding = false;
		}
	}

	async function forget(id: string) {
		storeError = '';
		try {
			await vault.removeIdentity(id);
			unlockedSessions.delete(id);
			if (openId === id) openId = null;
			if (renameId === id) renameId = null;
			await refresh();
		} catch (reason) {
			storeError = reasonText(reason);
			console.error('[autofirme:picker] forget failed', reason, storeError);
		}
	}

	/** Opens the alias editor for one saved signature; the PIN and sealed blob stay untouched. */
	function startRename(identity: IdentityMeta): void {
		storeError = '';
		openId = null;
		renameId = identity.id;
		renameLabel = identity.label;
	}

	async function rename(id: string): Promise<void> {
		storeError = '';
		try {
			await vault.renameIdentity(id, renameLabel);
			renameId = null;
			await refresh();
		} catch (reason) {
			storeError = reasonText(reason);
			console.error('[autofirme:picker] rename failed', reason, storeError);
		}
	}
</script>

<div class="flex flex-col gap-3">
	<!-- The trigger is a file-shaped target: dashed and dim until a sign is loaded, then solid. -->
	{#if compact}
		{#if cert}
			<div id="pkcs12-selection" tabindex="-1" class="bg-muted/40 grid min-w-0 gap-1 rounded-lg p-2.5">
				<div class="flex min-w-0 items-center gap-2">
					<FileIcon class="text-primary size-4 shrink-0"/>
					<div class="min-w-0">
						<p class="truncate text-sm font-medium">{cert.commonName}</p>
						{#if pickedName && pickedName!==cert.commonName}<p class="text-muted-foreground truncate text-xs">{pickedName}</p>{/if}
					</div>
				</div>
				<details id="selected-certificate-details" class="text-xs">
					<summary class="text-muted-foreground cursor-pointer py-1">{detailsLabel}</summary>
					<div class="text-muted-foreground grid gap-1 pt-1">
						<p class="break-words">{cert.subject}</p>
						<p class="break-words">{copy.signIssuer}: {cert.issuer}</p>
					<p class="tnum">{copy.signValidity}: {dateFormat.format(cert.notBefore)} <ArrowRightIcon class="inline size-3 align-[-0.125em]" aria-hidden="true" /> {dateFormat.format(cert.notAfter)}</p>
					</div>
				</details>
			</div>
		{/if}
	{:else}
		<Button
			id="pkcs12-picker"
			variant="outline"
			class={cn(
				'min-h-28 h-auto w-full flex-col gap-2 px-4 py-4 text-center',
				chosen
					? 'border-primary/60'
					: 'border-2 border-dashed border-muted-foreground/40 text-muted-foreground/70'
			)}
			aria-haspopup="dialog"
			onclick={() => {
				pickerOpen = true;
				console.debug('[autofirme:picker] modal opened');
			}}
		>
			{@render certificateSummary()}
		</Button>
	{/if}

	<input
		bind:this={fileInput}
		id="pkcs12-file"
		class="sr-only"
		type="file"
		accept=".p12,.pfx,application/x-pkcs12"
		aria-label={copy.addSign}
		onchange={(event) => picked(event.currentTarget.files?.[0] ?? null)}
	/>

	{#if cert?.expired}
		<!-- Amber, not red: an expired certificate still signs (#40) — it only has to be said out loud. -->
		<Alert class="border-amber-600/50 bg-amber-600/5 text-amber-800 dark:text-amber-300">
			<TriangleAlertIcon class="size-4" aria-hidden="true" />
			<AlertTitle>{copy.certExpired}</AlertTitle>
			<AlertDescription>{copy.storeExpiry}: {dateFormat.format(cert.notAfter)}</AlertDescription>
		</Alert>
	{/if}
	{#if cert?.notYetValid}
		<p class="text-destructive text-sm" role="alert">{copy.certNotYetValid}</p>
	{/if}
	{#if compact}
		<section id="pkcs12-choices" aria-label={copy.storeTitle} class="grid min-w-0 gap-3">
			<div id="pkcs12-choices-header" class="flex min-w-0 items-center justify-between gap-2">
				<h2 class="min-w-0 text-sm font-medium">{copy.storeTitle}</h2>
				{#if !initialId}
					<Button size="sm" variant="outline" class="h-8 shrink-0 gap-1 text-xs" onclick={()=>fileInput?.click()}><PlusIcon class="size-3.5"/>{copy.addSign}</Button>
				{/if}
			</div>
			{@render identityContent()}
		</section>
	{/if}
</div>

{#if !compact}
	<Dialog bind:open={pickerOpen}>
		<DialogContent class="max-h-[90dvh] overflow-y-auto sm:max-w-2xl">
			<DialogHeader>
				<DialogTitle>{copy.storeTitle}</DialogTitle>
				<DialogDescription>{copy.storeHint}</DialogDescription>
			</DialogHeader>

			{@render identityContent()}
		</DialogContent>
	</Dialog>
{/if}


{#snippet identityContent()}
	{#if !persistent}
		<!-- No usable database: say it, and let the user throw the broken one away. -->
		<div class="flex flex-col gap-2 rounded-lg bg-amber-500/5 p-3">
			<p class="text-sm text-amber-700 dark:text-amber-400" role="alert">{copy.storeEphemeral}</p>
			<Button variant="outline" size="sm" class="self-start" onclick={() => vault.reset()}
				>{copy.storeReset}</Button
			>
		</div>
	{/if}

	{#if identities.length === 0 && !pending}
		<!-- Only when the grid has nothing at all: the in-memory sign is a cell of its own. -->
		<p class="text-muted-foreground text-sm">{copy.storeEmpty}</p>
	{/if}

	{#if openId}
		{@const target = identities.find((entry) => entry.id === openId)}
		<!-- One signature clicked: ask for its PIN alone, then select it and get out of the way. -->
		<div class="bg-muted/40 flex flex-col gap-3 rounded-lg p-3">
			<div class="flex items-center gap-2">
				<FileIcon class="text-muted-foreground size-5 shrink-0" />
				<span class="min-w-0">
					<span class="block truncate text-sm font-medium">{target?.label ?? ''}</span>
					<span class="text-muted-foreground block truncate text-xs">{target?.commonName ?? ''}</span>
				</span>
			</div>
			<label class="flex flex-col gap-1 text-xs" for="store-pin">
				{copy.storePin}
				<Input
					id="store-pin"
					type="password"
					autocomplete="off"
					bind:value={storePin}
					onkeydown={(event) => {
						if (event.key === 'Enter') void unlock(openId!);
					}}
				/>
			</label>
			<label class="flex items-center gap-2 text-xs">
				<input
					type="checkbox"
					class="accent-primary size-4"
					bind:checked={rememberPin}
					onchange={persistRemember}
				/>
				{rememberLabel}
			</label>
			<div class="flex items-center gap-2">
				<Button size="sm" class="h-9" onclick={() => unlock(openId!)}
					>{copy.storeOpen}</Button
				>
				<Button
					variant="ghost"
					size="sm"
					class="h-9"
					onclick={() => {
						openId = null;
						storePin = '';
					}}>{copy.storeBack}</Button
				>
			</div>
		</div>
	{:else}
	<!-- Four columns, four rows tall, the rest scrolls. -->
	<div class={cn("grid max-h-80 gap-3 overflow-y-auto pr-1", compact ? "max-h-52 grid-cols-1 gap-2" : "grid-cols-2 sm:grid-cols-4")}>
		{#if pending}
			<!-- The sign sitting in memory: first cell, and no button — there is nothing to re-pick. -->
			<div class="bg-muted/40 relative rounded-lg">
				<div class="flex h-full w-full flex-col gap-1 p-3 text-left text-xs">
					<FileIcon class="text-muted-foreground size-5" />
					<span class="w-full truncate font-medium">{file?.name}</span>
				</div>
			</div>
		{/if}

		{#each visibleIdentities as identity (identity.id)}
			{#if renameId === identity.id}
				<form
					class="bg-muted/40 flex flex-col gap-2 rounded-lg p-3 text-xs"
					onsubmit={(event) => {
						event.preventDefault();
						void rename(identity.id);
					}}
				>
					<label class="flex flex-col gap-1" for={`store-alias-${identity.id}`}>
						{copy.storeAlias}
						<Input id={`store-alias-${identity.id}`} autocomplete="off" bind:value={renameLabel} />
					</label>
					<div class="flex items-center gap-2">
						<Button type="submit" size="sm" class="h-8">{copy.storeSave}</Button>
						<Button
							type="button"
							variant="ghost"
							size="sm"
							class="h-8"
							onclick={() => (renameId = null)}>{copy.storeBack}</Button
						>
					</div>
				</form>
			{:else}
			<div class="bg-muted/40 relative rounded-lg">
				{#if !initialId}
					<div class="absolute end-1 top-1 flex gap-0.5">
						<Button
							variant="ghost"
							size="icon"
							class="size-6"
							aria-label={copy.storeRename}
							onclick={() => startRename(identity)}
						>
							<PencilIcon class="size-3.5" />
						</Button>
						<Button
							variant="ghost"
							size="icon"
							class="size-6"
							aria-label={copy.remove}
							onclick={() => forget(identity.id)}
						>
							<XIcon class="size-3.5" />
						</Button>
					</div>
				{/if}
				<button
					type="button"
					class={cn(
						'hover:bg-accent/50 flex h-full w-full flex-col gap-1 rounded-lg p-3 text-left text-xs transition-colors',
						identity.id === usedId && 'bg-brand-soft'
					)}
					aria-expanded={openId === identity.id}
					onclick={() => select(identity)}
				>
					{#if !compact}<FileIcon class="text-muted-foreground size-5" />{/if}
					<span class="w-full truncate pr-12 font-medium">{identity.label}</span>
					<span class="text-muted-foreground w-full truncate">{identity.commonName}</span>
					{#if !compact}
					<span
						class={cn(
							'tnum w-full truncate',
							new Date(identity.notAfter).getTime() < Date.now()
								? 'text-amber-700 dark:text-amber-400'
								: 'text-muted-foreground'
						)}
					>
						{copy.storeExpiry}: {dateFormat.format(new Date(identity.notAfter))}
					</span>
					{/if}
					{#if identity.id === usedId}
						<Badge variant="secondary" class="self-start">{copy.storeCurrent}</Badge>
					{/if}
				</button>
			</div>
			{/if}
		{/each}

		{#if !compact}
		<button
			type="button"
			class="bg-muted/40 hover:bg-muted hover:text-foreground text-muted-foreground flex min-h-28 flex-col items-center justify-center gap-2 rounded-lg p-3 text-xs transition-colors"
			onclick={() => fileInput?.click()}
		>
			<PlusIcon class="size-5" />
			{copy.addSign}
		</button>
		{/if}
	</div>

	{#if pending}
		<!-- File added from the modal and not sealed yet: its PIN opens it and stores it at once. -->
		<div class="bg-muted/40 flex flex-wrap items-end gap-2 rounded-lg p-3">
			<label class="flex min-w-40 flex-1 flex-col gap-1 text-xs" for="add-pin">
				{copy.storePin}
				<Input
					id="add-pin"
					type="password"
					autocomplete="off"
					bind:value={addPin}
					onkeydown={(event) => {
						if (event.key === 'Enter') void saveAdded();
					}}
				/>
			</label>
			<label class="flex items-center gap-2 pb-2 text-xs">
				<input
					type="checkbox"
					class="accent-primary size-4"
					bind:checked={rememberPin}
					onchange={persistRemember}
				/>
				{rememberLabel}
			</label>
			<Button size="sm" class="h-9" disabled={adding || addPin === ''} onclick={saveAdded}
				>{adding ? copy.certLoading : copy.storeSave}</Button
			>
		</div>
	{/if}

	{/if}

	{#if storeError}
		<p class="text-destructive text-sm" role="alert">{storeError}</p>
	{/if}
{/snippet}

{#snippet certificateSummary()}
	<FileIcon
		class={cn('size-12 shrink-0', chosen ? 'text-primary' : 'text-muted-foreground/70')}
		stroke-width={1.25}
	/>
	<span
		class={cn(
			'max-w-full truncate',
			chosen
				? 'text-foreground font-medium'
				: 'underline decoration-dotted decoration-from-font'
		)}>{pickedName || copy.selectSign}</span
	>
	{#if cert}
		<span class="text-muted-foreground max-w-full truncate text-xs">{cert.commonName}</span>
		<!-- The sign at a glance: who issued it and how long the certificate holds. -->
		<span class="text-muted-foreground flex w-full flex-col gap-0.5 text-xs">
			<span class="max-w-full truncate">{copy.signIssuer}: {cert.issuer}</span>
			<span
				class={cn(
					'tnum max-w-full truncate',
					cert.expired && 'text-amber-700 dark:text-amber-400'
				)}
				>
			{copy.signValidity}: {dateFormat.format(cert.notBefore)} <ArrowRightIcon class="inline size-3 align-[-0.125em]" aria-hidden="true" /> {dateFormat.format(cert.notAfter)}
			</span>
		</span>
	{/if}
{/snippet}
