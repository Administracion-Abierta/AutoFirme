<script lang="ts">
	import { copy, type Copy, type Lang } from '#lib/i18n';
	import { EMPTY_BATCH_STATE, createBatchQueue, type BatchSigner, type BatchState } from '#lib/batch';
	import { browserVault, StoreError } from '#lib/certstore';
	import { signCadesDetached } from '#lib/cms';
	import { signPdf } from '#lib/pades';
	import {
		SIGNATURE_CORNERS,
		imageFormat,
		type SignatureCorner
	} from '#lib/pades-visible';
	import {
		SIGN_FORMATS,
		formatBytes,
		signingAlgorithmName,
		toSelectedFile,
		type SelectedFile,
		type SignFormat,
		type SignerCert
	} from '#lib/sign';
	import { signXades } from '#lib/xades';
	import { verifyArtifact, type Bytes, type VerificationReport } from '#lib/verify';
	import BatchQueue from '#lib/components/BatchQueue.svelte';
	import CertPicker from '#lib/components/CertPicker.svelte';
	import DownloadsDialog from '#lib/components/DownloadsDialog.svelte';
	import FileCard from '#lib/components/FileCard.svelte';
	import FileDrop from '#lib/components/FileDrop.svelte';
	import PwaControls from '#lib/components/PwaControls.svelte';
	import VerifyReport from '#lib/components/VerifyReport.svelte';
	import { Button } from '#lib/components/ui/button/index.js';
	import { Alert, AlertDescription } from '#lib/components/ui/alert/index.js';
	import TriangleAlertIcon from '@lucide/svelte/icons/triangle-alert';
	import { Input } from '#lib/components/ui/input/index.js';
	import { Switch } from '#lib/components/ui/switch/index.js';
	import { Card, CardContent, CardHeader } from '#lib/components/ui/card/index.js';
	import {
		Dialog,
		DialogContent,
		DialogDescription,
		DialogHeader,
		DialogTitle
	} from '#lib/components/ui/dialog/index.js';
	import {
		DropdownMenu,
		DropdownMenuContent,
		DropdownMenuItem,
		DropdownMenuTrigger
	} from '#lib/components/ui/dropdown-menu/index.js';
	import {
		Table,
		TableBody,
		TableCell,
		TableHead,
		TableHeader,
		TableRow
	} from '#lib/components/ui/table/index.js';
	import { TooltipProvider } from '#lib/components/ui/tooltip/index.js';
	import { readMcp, saveMcp, type McpSettings } from '#lib/mcp';
	import { applyTheme, readTheme, type Theme } from '#lib/theme';
	import MonitorIcon from '@lucide/svelte/icons/monitor';
	import MoonIcon from '@lucide/svelte/icons/moon';
	import SunIcon from '@lucide/svelte/icons/sun';
	import ChevronDownIcon from '@lucide/svelte/icons/chevron-down';
	import { onMount, type Snippet } from 'svelte';
	import { Badge } from '#lib/components/ui/badge/index.js';

	let lang = $state<Lang>('es');
	const t = $derived<Copy>(copy[lang]);
	// http away from localhost: the browser withholds WebCrypto from insecure contexts, so no
	// signature can be produced or verified here until the page is served over https.
	const secure = $derived(globalThis.crypto?.subtle !== undefined);
	/** `?tab=` deep link (ES and EN spellings both accepted); also mirrored back into the URL. */
	function tabFromSearch(search: string): 'sign' | 'verify' {
		const tab = new URLSearchParams(search).get('tab');
		return tab === 'verificar' || tab === 'verify' ? 'verify' : 'sign';
	}

	// `location` exists only where a DOM exists — prerender runs without one.
	const onClient = typeof location === 'object';
	let mode = $state<'sign' | 'verify'>(onClient ? tabFromSearch(location.search) : 'sign');

	/** Switch tab and keep the address bar honest, so the current view is linkable. */
	function setMode(next: 'sign' | 'verify') {
		mode = next;
		if (!onClient) return;
		const url = new URL(location.href);
		url.searchParams.set('tab', next === 'verify' ? 'verificar' : 'firmar');
		history.replaceState(history.state, '', url);
	}
	let helpOpen = $state(false);
	let mcpOpen = $state(false);
	// Empty until mount, so the prerendered HTML and the hydrated one agree.
	let mcp = $state<McpSettings>({ url: '', token: '' });
	let mcpCopied = $state(false);

	function updateMcp(patch: Partial<McpSettings>): void {
		mcp = { ...mcp, ...patch };
		mcpCopied = false;
		saveMcp(mcp);
	}

	/** Clipboard access needs a secure context and a granted permission. Without either the field
	 *  is still selectable by hand, so a denial stays silent. */
	async function copyEndpoint(): Promise<void> {
		if (!navigator.clipboard?.writeText || !mcp.url) return;
		try {
			await navigator.clipboard.writeText(mcp.url);
			mcpCopied = true;
		} catch {
			// no gesture or the page is not a secure context — nothing to report
		}
	}

	// Starts on the boot script's default and adopts the stored choice on mount, so the prerendered
	// HTML and the hydrated one agree on which theme button is pressed.
	let theme = $state<Theme>('system');
	onMount(() => {
		theme = readTheme();
		mcp = readMcp();
	});
	$effect(() => applyTheme(theme));
	const THEMES = $derived([
		{ id: 'light' as const, label: t.themeLight, icon: SunIcon },
		{ id: 'dark' as const, label: t.themeDark, icon: MoonIcon },
		{ id: 'system' as const, label: t.themeSystem, icon: MonitorIcon }
	]);

	let files = $state<File[]>([]);
	let format = $state<SignFormat>('cades');
	let formatOpen = $state(false);
	// Visible signature block (#45), PAdES only and off by default: an uploaded image plus the
	// signer's name and the signing date, placed on a page and against a corner.
	let visibleOn = $state(false);
	let visiblePage = $state(1);
	let visibleCorner = $state<SignatureCorner>('bottom-right');
	let visibleImage = $state<{ name: string; bytes: Uint8Array } | null>(null);
	let visibleImageError = $state('');
	let visibleImageInput = $state<HTMLInputElement | null>(null);
	/** RFC 3161 TSA address (#43). Empty — the default — means no timestamp. */
	let tsaUrl = $state('');
	// Only the certificate that owns the container's private key can sign.
	let cert = $state<SignerCert | null>(null);
	// Full container chain of the loaded PKCS#12 (#5) — the KeyInfo of the signature embeds it.
	let chain = $state<readonly SignerCert[]>([]);

	const queue = createBatchQueue();
	$effect(() => queue.set(files, format));


	// The picker stays mounted in the new layout (the config card does not unmount with the last
	// document): configuring the signature before adding documents is a normal order, and nothing
	// drops a sign for want of documents — signing simply stays disabled until one exists.
	// Storage of saved identities and produced signatures (#29). IndexedDB in the browser, memory
	// when it is unavailable (prerender), so nothing here can fail at import time.
	const vault = browserVault();
	let saveError = $state('');
	/** Content type of a produced signature, from the extension the format declares. */
	const MIME_BY_EXTENSION: Record<string, string> = {
		'.csig': 'application/octet-stream',
		'.xsig': 'application/xml',
		'.pdf': 'application/pdf'
	};

	/** Keep every signature of a finished run in the browser store, for the downloads list. */
	async function keepSignatures(state: BatchState) {
		const extension = SIGN_FORMATS.find((entry) => entry.id === state.format)?.extension ?? '';
		for (const document of state.documents) {
			if (document.status !== 'signed' || !document.bytes) continue;
			try {
				await vault.saveOutput({
					name: document.output,
					format: state.format,
					mime: MIME_BY_EXTENSION[extension] ?? 'application/octet-stream',
					commonName: cert?.commonName ?? '',
					bytes: document.bytes
				});
				saveError = '';
			} catch (reason) {
				saveError =
					reason instanceof StoreError ? t.storeErrors[reason.code] : t.storeErrors.unavailable;
				console.error('[autofirme:app] keeping the produced signature failed', reason, saveError);
			}
		}
	}

	// The selected-files table repeats the queue's per-document status; the queue itself keeps
	// rendering the detailed result rows.
	let batch = $state<BatchState>(EMPTY_BATCH_STATE);
	$effect(() => queue.subscribe((value) => (batch = value)));

	/** Format engines (#6 CAdES, #7 XAdES, #11 PAdES) plug in here; the queue never looks inside. */
	function makeSigner(certificate: SignerCert | null, kind: SignFormat): BatchSigner | null {
		if (!certificate?.key || !certificate.algorithm) return null;
		const algorithm = signingAlgorithmName(certificate);
		const { key, der, commonName } = certificate;
		if (kind === 'cades') {
			const timeStamp = tsaUrl.trim() || undefined;
			return async (_file, bytes) => ({
				bytes: await signCadesDetached(certificate, bytes, { timeStamp }),
				algorithm
			});
		}
		if (kind === 'xades') {
			return async (file, bytes) => ({
				bytes: new TextEncoder().encode(
					await signXades({
						xml: bytes,
						certificates: chain,
						signer: certificate,
						dataObject: { mimeType: file.type || 'application/xml' }
					})
				),
				algorithm
			});
		}
		if (kind === 'pades') {
			return async (file, bytes) => {
				const signingTime = new Date();
				return {
					// The block is drawn inside the incremental update, so its bytes are part of what
					// the CMS signs — nothing is stamped on afterwards.
					bytes: await signPdf(bytes, {
						certificate: der,
						chain: chain.filter((entry) => entry !== certificate).map((entry) => entry.der),
						privateKey: key,
						signingTime,
						name: commonName,
						visible: visibleOn
							? {
									page: Math.max(0, visiblePage - 1),
									corner: visibleCorner,
									image: visibleImage?.bytes,
									lines: [commonName, `${t.signedOn} ${signedOnLabel(signingTime)}`]
								}
							: undefined
					}),
					algorithm
				};
			};
		}
		return null;
	}

	/** `dd/mm/aa, hh:mm` in the interface's language — the block's date line (#45). */
	function signedOnLabel(date: Date): string {
		return new Intl.DateTimeFormat(lang === 'es' ? 'es-ES' : 'en-GB', {
			dateStyle: 'short',
			timeStyle: 'short'
		}).format(date);
	}

	/** Signature images are PNG or JPEG only; anything else is rejected before it reaches a signing
	 * run, where it would fail once per document. */
	async function pickSignatureImage(picked: File | undefined) {
		visibleImage = null;
		visibleImageError = '';
		if (!picked) return;
		const bytes = new Uint8Array(await picked.arrayBuffer());
		if (imageFormat(bytes) === null) {
			visibleImageError = t.visibleImageInvalid;
			return;
		}
		visibleImage = { name: picked.name, bytes };
	}

	// The format chooser shows the picked format; the modal lists every one with its state.
	const currentFormat = $derived(SIGN_FORMATS.find((option) => option.id === format)!);

	const signer = $derived(makeSigner(cert, format));
	const signHint = $derived(
		files.length === 0
			? t.signNeedsFile
			: cert === null
				? t.signNeedsCert
				: signer === null
					? t.signPendingFormat
					: ''
	);

	let verifyFile = $state<SelectedFile | null>(null);
	let verifyBytes = $state<Bytes | null>(null);
	let detachedFile = $state<SelectedFile | null>(null);
	let detachedBytes = $state<Bytes | null>(null);
	let report = $state<VerificationReport | null>(null);
	let running = $state(false);
	const needsDocument = $derived(
		(report?.checks.some((entry) => entry.id === 'contentDigest' && entry.status === 'info') ?? false) &&
			detachedFile === null
	);

	async function runVerify() {
		if (verifyBytes === null) return;
		running = true;
		try {
			report = await verifyArtifact(verifyBytes, { detached: detachedBytes });
		} finally {
			running = false;
		}
	}

	async function pickSignature(picked: File[]) {
		const file = picked[0];
		if (!file) return;
		verifyBytes = new Uint8Array(await file.arrayBuffer());
		verifyFile = toSelectedFile(file);
		detachedFile = null;
		detachedBytes = null;
		report = null;
		await runVerify();
	}

	async function pickDocument(picked: File[]) {
		const file = picked[0];
		if (!file) return;
		detachedBytes = new Uint8Array(await file.arrayBuffer());
		detachedFile = toSelectedFile(file);
		await runVerify();
	}

	function clearVerification() {
		verifyFile = null;
		verifyBytes = null;
		detachedFile = null;
		detachedBytes = null;
		report = null;
	}

	function clearDocument() {
		detachedFile = null;
		detachedBytes = null;
		void runVerify();
	}
</script>

<!--
	The seal — the app's one signature motif: a notarial stamp carrying a signature stroke.
	Rings stay open where the stroke crosses, so the mark reads as stamped, not printed.
-->
{#snippet seal(size: number)}
	<svg
		aria-hidden="true"
		width={size}
		height={size}
		viewBox="0 0 48 48"
		fill="none"
		class="text-primary shrink-0"
	>
		<circle cx="24" cy="24" r="21.5" stroke="currentColor" stroke-width="2" />
		<circle
			cx="24"
			cy="24"
			r="16.5"
			stroke="currentColor"
			stroke-width="1"
			stroke-dasharray="2.5 3"
		/>
		<path
			d="M15 29c4.5 1.2 9.5.6 13-2.5M17.5 24.5c3-3.5 7.5-5.5 11-5M22 12l-3.5 14.5a3.4 3.4 0 0 0 4.6 3.8"
			stroke="currentColor"
			stroke-width="2.2"
			stroke-linecap="round"
		/>
	</svg>
{/snippet}

<TooltipProvider>
	<div
		class="mx-auto flex h-dvh max-w-7xl flex-col gap-6 px-5 py-6 [--panel-offset:7.5rem] max-lg:h-auto max-lg:min-h-dvh"
	>
		<header class="flex flex-wrap items-start justify-between gap-4">
			<div class="flex items-center gap-4">
				{@render seal(44)}
				<div>
					<h1 class="font-display text-3xl font-semibold tracking-tight text-balance">
						{t.brand}
					</h1>
					<p class="text-muted-foreground mt-1">{t.tagline}</p>
				</div>
			</div>
			<div class="flex flex-col items-end gap-2 text-sm">
				<div class="flex flex-wrap items-center justify-end gap-2">
					<PwaControls copy={t} />
					<DownloadsDialog copy={t} {vault} />
					<div
						class="bg-muted inline-flex items-center gap-1 rounded-lg p-1"
						role="group"
						aria-label={t.themeLabel}
					>
						{#each THEMES as option (option.id)}
							{@const Icon = option.icon}
							<Button
								variant={theme === option.id ? 'default' : 'ghost'}
								size="icon"
								class="size-8"
								aria-pressed={theme === option.id}
								aria-label={option.label}
								onclick={() => (theme = option.id)}
							>
								<Icon class="size-4" />
							</Button>
						{/each}
					</div>
					<div
						class="bg-muted inline-flex items-center gap-1 rounded-lg p-1"
						role="group"
						aria-label={t.langLabel}
					>
						<Button
							variant={lang === 'es' ? 'default' : 'ghost'}
							size="sm"
							class="h-8 px-3"
							aria-pressed={lang === 'es'}
							onclick={() => (lang = 'es')}>ES</Button
						>
						<Button
							variant={lang === 'en' ? 'default' : 'ghost'}
							size="sm"
							class="h-8 px-3"
							aria-pressed={lang === 'en'}
							onclick={() => (lang = 'en')}>EN</Button
						>
					</div>
					<DropdownMenu>
						<DropdownMenuTrigger>
							{#snippet child({ props })}
								<Button {...props} variant="ghost" size="sm" class="h-8 px-3">
									{t.helpMenu}
								</Button>
							{/snippet}
						</DropdownMenuTrigger>
						<DropdownMenuContent align="end">
							<DropdownMenuItem onSelect={() => (helpOpen = true)}>{t.privacyTitle}</DropdownMenuItem>
							<DropdownMenuItem onSelect={() => (mcpOpen = true)}>{t.mcpMenu}</DropdownMenuItem>
							<DropdownMenuItem>
								{#snippet child({ props })}
									<a
										{...props}
										href="https://git.eduard3v.com/EduarD3V/AutoFirme"
										target="_blank"
										rel="noreferrer">{t.sourceCode}</a
									>
								{/snippet}
							</DropdownMenuItem>
						</DropdownMenuContent>
					</DropdownMenu>
				</div>
				<div
					class="bg-muted inline-flex items-center gap-1 rounded-lg p-1"
					role="tablist"
					aria-label={t.modesLabel}
				>
					<Button
						role="tab"
						aria-selected={mode === 'sign'}
						aria-controls="panel-sign"
						variant={mode === 'sign' ? 'default' : 'ghost'}
						size="sm"
						class="h-8 px-4"
						onclick={() => setMode('sign')}>{t.modeSign}</Button
					>
					<Button
						role="tab"
						aria-selected={mode === 'verify'}
						aria-controls="panel-verify"
						variant={mode === 'verify' ? 'default' : 'ghost'}
						size="sm"
						class="h-8 px-4"
						onclick={() => setMode('verify')}>{t.modeVerify}</Button
					>
				</div>
			</div>
		</header>

		<main class="flex min-h-0 flex-1 flex-col gap-6 overflow-y-auto">
			{#if !secure}
				<!-- Not an app fault: WebCrypto only exists in secure contexts. -->
				<Alert class="border-amber-600/50 bg-amber-600/5 text-amber-800 dark:text-amber-300">
					<TriangleAlertIcon class="size-4" aria-hidden="true" />
					<AlertDescription>{t.insecureContext}</AlertDescription>
				</Alert>
			{/if}
			{#if mode === 'sign'}
				<div
					id="panel-sign"
					role="tabpanel"
					aria-label={t.modeSign}
					class="grid min-h-0 flex-1 gap-6 lg:grid-cols-3 lg:grid-rows-[minmax(0,1fr)_auto]"
				>
					<!-- Left (2/3): drop zone and the documents waiting in the batch. -->
					<Card class="flex min-h-0 flex-col overflow-hidden lg:col-span-2">
						<CardHeader>
							<h2 class="font-display text-xl font-semibold">{t.step1Title}</h2>
						</CardHeader>
						<CardContent class="flex min-h-0 flex-1 flex-col gap-4 overflow-y-auto">
							<FileDrop copy={t} onselect={(picked) => (files = [...files, ...picked])} />
							{#if files.length > 0}
								<Table>
									<TableHeader>
										<TableRow>
											<TableHead>{t.colFile}</TableHead>
											<TableHead class="text-right">{t.colActions}</TableHead>
										</TableRow>
									</TableHeader>
									<TableBody>
										{#each files as file, index (index)}
											<FileCard
												copy={t}
												file={toSelectedFile(file)}
												document={batch.documents[index] ?? null}
												onremove={() => {
													files = files.filter((_, position) => position !== index);
												}}
											/>
										{/each}
									</TableBody>
								</Table>
							{/if}
						</CardContent>
					</Card>
					<!-- Upper right: signature config (certificate + format chooser). -->
					<Card class="min-h-0 overflow-hidden">
						<CardHeader>
							<h2 class="font-display text-xl font-semibold">{t.step2Title}</h2>
						</CardHeader>
						<CardContent class="flex min-h-0 flex-col gap-4 overflow-y-auto">
							<CertPicker
								copy={t}
								{cert}
								{vault}
								onpick={(picked, certificates) => {
									if (picked === null) {
										console.error('[autofirme:app] picker cleared the signer');
									}
									cert = picked;
									chain = certificates;
								}}
							/>

							<fieldset class="flex flex-col gap-2">
								<legend class="text-sm font-medium">{t.formatTitle}</legend>
								<Button
									variant="outline"
									class="justify-between px-4 py-3 text-left h-auto"
									aria-haspopup="dialog"
									onclick={() => (formatOpen = true)}
								>
									<span class="flex min-w-0 items-center gap-3">
										<span class="font-medium">{currentFormat.label}</span>
										<span class="text-muted-foreground tnum text-sm"
											>{currentFormat.extension}</span
										>
										{#if !currentFormat.implemented}
											<Badge variant="secondary">
												{t.pendingIssue} #{currentFormat.issue}
											</Badge>
										{/if}
									</span>
									<span aria-hidden="true" class="text-muted-foreground text-sm">▾</span>
								</Button>
							</fieldset>

							{#if format === 'pades'}
								<!-- Visible signature block (#45), off by default. -->
								<fieldset class="flex flex-col gap-3 border-t pt-4">
									<legend class="sr-only">{t.visibleTitle}</legend>
									<div class="flex items-start justify-between gap-3">
										<label for="visible-signature" class="flex flex-col gap-0.5">
											<span class="text-sm font-medium">{t.visibleTitle}</span>
											<span class="text-muted-foreground text-xs">{t.visibleHint}</span>
										</label>
										<Switch id="visible-signature" bind:checked={visibleOn} />
									</div>

									{#if visibleOn}
										<div class="flex flex-wrap items-center gap-2">
											<input
												bind:this={visibleImageInput}
												class="sr-only"
												type="file"
												accept="image/png,image/jpeg"
												aria-label={t.visibleImage}
												onchange={(event) => pickSignatureImage(event.currentTarget.files?.[0])}
											/>
											<Button
												variant="outline"
												size="sm"
												onclick={() => visibleImageInput?.click()}
											>
												{t.visibleImageChoose}
											</Button>
											{#if visibleImage}
												<span class="text-muted-foreground text-xs">
													{visibleImage.name} · {formatBytes(visibleImage.bytes.length)}
												</span>
												<Button variant="ghost" size="sm" onclick={() => (visibleImage = null)}>
													{t.remove}
												</Button>
											{/if}
										</div>
										{#if visibleImageError}
											<p class="text-destructive text-xs" role="alert">{visibleImageError}</p>
										{/if}

										<div class="flex flex-wrap items-end gap-4">
											<div class="flex flex-col gap-1">
												<label for="visible-page" class="text-xs font-medium">{t.visiblePage}</label>
												<Input
													id="visible-page"
													type="number"
													min="1"
													class="w-20"
													bind:value={visiblePage}
												/>
											</div>
											<div class="flex flex-col gap-1">
												<span id="visible-corner" class="text-xs font-medium">{t.visibleCorner}</span>
												<div
													class="grid grid-cols-2 gap-1"
													role="group"
													aria-labelledby="visible-corner"
												>
													{#each SIGNATURE_CORNERS as corner (corner)}
														<Button
															variant={visibleCorner === corner ? 'default' : 'outline'}
															size="sm"
															aria-pressed={visibleCorner === corner}
															onclick={() => (visibleCorner = corner)}
														>
															{t.visibleCorners[corner]}
														</Button>
													{/each}
												</div>
											</div>
										</div>
										{/if}
										</fieldset>
									{/if}

							{#if format === 'cades'}
								<!-- Trusted timestamp (#43): collapsed until asked for — one click opens it. -->
								<details class="group flex flex-col gap-2 rounded-lg border p-3">
									<summary class="flex cursor-pointer list-none items-center justify-between text-sm font-medium select-none [&::-webkit-details-marker]:hidden">
										{t.timeStampTitle}
										<ChevronDownIcon
											class="text-muted-foreground size-4 transition-transform group-open:rotate-180"
											aria-hidden="true"
										/>
									</summary>
									<div class="flex flex-col gap-2">
										<Input
											id="tsa-url"
											type="url"
											placeholder={t.timeStampPlaceholder}
											bind:value={tsaUrl}
											autocomplete="off"
											spellcheck={false}
										/>
										<p class="text-muted-foreground text-xs">{t.timeStampHint}</p>
									</div>
								</details>
							{/if}
						</CardContent>
					</Card>

					<!-- Bottom: the signing queue — its own height, never stretched by the row above. -->
					<Card class="lg:col-span-3">
						<CardContent class="flex flex-col">
							<BatchQueue
								copy={t}
								{queue}
								{signer}
								hint={signHint}
								onsigned={keepSignatures}
							/>
							{#if saveError}
								<p class="text-sm text-amber-600 dark:text-amber-400" role="alert">
									{saveError}
								</p>
							{/if}
						</CardContent>
					</Card>
				</div>
			{:else}
				<div
					id="panel-verify"
					role="tabpanel"
					aria-label={t.modeVerify}
					class="mx-auto flex w-full max-w-3xl flex-col gap-8"
				>
					{#if verifyFile === null}
						<!-- One signature at a time: this card steps aside while one is loaded. -->
						<Card>
							<CardHeader>
								<h2 class="text-lg font-medium">{t.verifyStepTitle}</h2>
							</CardHeader>
							<CardContent class="flex flex-col gap-4">
								<FileDrop
									copy={t}
									onselect={pickSignature}
									title={t.verifyDropTitle}
									hint={t.verifyDropHint}
									multiple={false}
								/>
							</CardContent>
						</Card>
					{/if}

					{#if verifyFile !== null}
						<Card>
							<CardContent>
								<VerifyReport copy={t} {report} {running} />
							</CardContent>
						</Card>

						{#if needsDocument}
							<Card>
								<CardHeader>
									<h2 class="text-lg font-medium">{t.verifyAddDocument}</h2>
								</CardHeader>
								<CardContent>
									<FileDrop
										copy={t}
										onselect={pickDocument}
										title={t.verifyAddDocument}
										hint={t.verifyAddDocumentHint}
										multiple={false}
									/>
								</CardContent>
							</Card>
						{:else if detachedFile !== null}
							<Card>
								<CardHeader>
									<h2 class="text-lg font-medium">{t.verifyDocumentLabel}</h2>
								</CardHeader>
								<CardContent>
									<Table>
										<TableBody>
											<FileCard copy={t} file={detachedFile} onremove={clearDocument} />
										</TableBody>
									</Table>
								</CardContent>
							</Card>
						{/if}

						<!-- Cerrar: the way back to the drop zone — it drops the signature and report. -->
						<div class="flex justify-center">
							<Button variant="outline" onclick={clearVerification}>{t.verifyClose}</Button>
						</div>
					{/if}
				</div>
			{/if}
		</main>

		<footer
			class="text-muted-foreground flex items-center justify-between gap-4 border-t pt-3 text-sm"
		>
			<p class="truncate">{t.privacyNote}</p>
			<div class="max-sm:hidden opacity-60">{@render seal(28)}</div>
		</footer>
	</div>

	<Dialog bind:open={helpOpen}>
		<DialogContent>
			<DialogHeader>
				<DialogTitle>{t.privacyTitle}</DialogTitle>
				<DialogDescription>{t.privacyNote}</DialogDescription>
			</DialogHeader>
		</DialogContent>
	</Dialog>

	<Dialog bind:open={mcpOpen}>
		<DialogContent class="sm:max-w-lg">
			<DialogHeader>
				<DialogTitle>{t.mcpTitle}</DialogTitle>
				<DialogDescription>{t.mcpHint}</DialogDescription>
			</DialogHeader>
			<div class="grid gap-4">
				<div class="grid gap-2">
					<label class="text-sm font-medium" for="mcp-url">{t.mcpUrlLabel}</label>
					<div class="flex gap-2">
						<Input
							id="mcp-url"
							class="font-mono text-sm"
							placeholder="https://mcp.example.com/mcp"
							value={mcp.url}
							oninput={(event) => updateMcp({ url: event.currentTarget.value })}
						/>
						<Button type="button" variant="outline" onclick={copyEndpoint}>
							{mcpCopied ? t.mcpCopied : t.mcpCopy}
						</Button>
					</div>
					<p class="text-muted-foreground text-xs">{t.mcpUrlHint}</p>
					{#if mcpCopied}
						<p class="text-primary text-xs" role="status">{t.mcpCopiedNote}</p>
					{/if}
				</div>
				<div class="grid gap-2">
					<label class="text-sm font-medium" for="mcp-token">{t.mcpTokenLabel}</label>
					<Input
						id="mcp-token"
						type="password"
						class="font-mono text-sm"
						value={mcp.token}
						oninput={(event) => updateMcp({ token: event.currentTarget.value })}
					/>
					<p class="text-muted-foreground text-xs">{t.mcpTokenHint}</p>
				</div>
			</div>
		</DialogContent>
	</Dialog>

	<Dialog bind:open={formatOpen}>
		<DialogContent class="sm:max-w-md">
			<DialogHeader>
				<DialogTitle>{t.formatTitle}</DialogTitle>
				<DialogDescription>{t.formatHint}</DialogDescription>
			</DialogHeader>
			<div
				role="radiogroup"
				aria-label={t.formatTitle}
				class="flex flex-col gap-2"
			>
				{#each SIGN_FORMATS as option (option.id)}
					<label
						class="hover:border-primary/50 has-[:checked]:border-primary has-[:checked]:bg-primary/5 flex cursor-pointer items-center gap-3 rounded-lg border px-3.5 py-3 transition-colors"
					>
						<input
							type="radio"
							name="format"
							value={option.id}
							checked={format === option.id}
							class="accent-primary size-4"
							onchange={() => {
								format = option.id;
								formatOpen = false;
							}}
						/>
						<span class="font-medium">{option.label}</span>
						<span class="text-muted-foreground tnum text-sm">{option.extension}</span>
						{#if !option.implemented}
							<Badge variant="secondary" class="ms-auto">
								{t.pendingIssue} #{option.issue}
							</Badge>
						{/if}
					</label>
				{/each}
			</div>
		</DialogContent>
	</Dialog>
</TooltipProvider>
