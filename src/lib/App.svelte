<script lang="ts">
	import { copy, type Copy, type Lang } from '#lib/i18n';
	import { extensionCopy } from '#lib/extension/copy';
	import {
		EMPTY_BATCH_STATE,
		buildZip,
		createBatchQueue,
		type BatchSigner,
		type BatchState
	} from '#lib/batch';
	import { browserVault } from '#lib/certstore';
	import { signCadesDetached } from '#lib/cms';
	import { signOoxmlPackage } from '#lib/ooxml';
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
	import { saveBytes } from '#lib/save-file';
	import { suggestSignFormat } from '#lib/verify/detect';
	import { signXades } from '#lib/xades';
	import BatchQueue from '#lib/components/BatchQueue.svelte';
	import CertPicker from '#lib/components/CertPicker.svelte';
	import FileCard from '#lib/components/FileCard.svelte';
	import FileDrop from '#lib/components/FileDrop.svelte';
	import PwaControls from '#lib/components/PwaControls.svelte';
	import { isDesktop } from '#lib/desktop';
	import {
		deeplinkConfirm,
		deeplinkList,
		deeplinkRemove,
		deeplinkStatus,
		listenDeepLinks,
		payloadFromGated,
		type IncomingRequest,
		type SchemeStatus
	} from '#lib/deeplink';
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
		Table,
		TableBody,
		TableCell,
		TableHead,
		TableHeader,
		TableRow
	} from '#lib/components/ui/table/index.js';
	import { TooltipProvider } from '#lib/components/ui/tooltip/index.js';
	import { readMcp, saveMcp, configureMcpBridge, listenMcpBridge, mcpBridgeStatus, type BridgeStatus, type McpSettings } from '#lib/mcp';
	import { isExtension } from '#lib/extension/runtime';
	import { readPinMemorySettings, savePinMemorySettings, DEFAULT_MINUTES } from '#lib/pin-memory';
	import { applyTheme, readTheme, type Theme } from '#lib/theme';
	import MonitorIcon from '@lucide/svelte/icons/monitor';
	import MoonIcon from '@lucide/svelte/icons/moon';
	import SunIcon from '@lucide/svelte/icons/sun';
	import ChevronDownIcon from '@lucide/svelte/icons/chevron-down';
	import SettingsIcon from '@lucide/svelte/icons/settings';
	import LanguagesIcon from '@lucide/svelte/icons/languages';
	import CodeIcon from '@lucide/svelte/icons/code';
	import ShieldCheckIcon from '@lucide/svelte/icons/shield-check';
	import ActivityIcon from '@lucide/svelte/icons/activity';
	import PlugIcon from '@lucide/svelte/icons/plug';
	import GlobeIcon from '@lucide/svelte/icons/globe';
	import ArrowRightIcon from '@lucide/svelte/icons/arrow-right';
	import ArrowLeftIcon from '@lucide/svelte/icons/arrow-left';
	import CheckIcon from '@lucide/svelte/icons/check';
	import PlusIcon from '@lucide/svelte/icons/plus';
	import PenLineIcon from '@lucide/svelte/icons/pen-line';
	import DownloadIcon from '@lucide/svelte/icons/download';
	import UploadIcon from '@lucide/svelte/icons/upload';
	import { onMount, type Snippet } from 'svelte';
	import { Badge } from '#lib/components/ui/badge/index.js';
	import {
		buildDeepLinkReturn,
		encodeBase64,
		parseDeepLink,
		takeShare,
		type DeepLinkRequest,
		type DeepLinkResult
	} from '#lib/pwa-entry';

	let { extensionSettings }: { extensionSettings?: Snippet<[Lang]> } = $props();
	let lang = $state<Lang>('es');
	const t = $derived<Copy>(copy[lang]);
	// http away from localhost: the browser withholds WebCrypto from insecure contexts, so no
	// signature can be produced or verified here until the page is served over https.
	const secure = $derived(globalThis.crypto?.subtle !== undefined);
	let settingsOpen = $state(false);
	const SETTINGS_TABS = ['general', 'mcp', 'help'] as const;
	let settingsTab = $state<(typeof SETTINGS_TABS)[number]>('general');
	function moveSettingsTab(event: KeyboardEvent): void {
		if (!['ArrowLeft', 'ArrowRight', 'Home', 'End'].includes(event.key)) return;
		event.preventDefault();
		const focused = SETTINGS_TABS.findIndex(
			(tab) => event.target instanceof HTMLElement && event.target.id === `settings-tab-${tab}`
		);
		const current = focused >= 0 ? focused : SETTINGS_TABS.indexOf(settingsTab);
		const next =
			event.key === 'Home'
				? 0
				: event.key === 'End'
					? SETTINGS_TABS.length - 1
					: (current + (event.key === 'ArrowRight' ? 1 : -1) + SETTINGS_TABS.length) %
						SETTINGS_TABS.length;
		settingsTab = SETTINGS_TABS[next]!;
		document.getElementById(`settings-tab-${settingsTab}`)?.focus();
	}
	// Empty until mount, so the prerendered HTML and the hydrated one agree.
	let mcp = $state<McpSettings>({ url: '', token: '', enabled: false });
	let mcpCopied = $state(false);
	let bridge = $state<BridgeStatus>({ enabled: false, online: false, error: '' });
	const bridgeError = $derived(
		bridge.error === 'token required'
			? t.mcpBridgeNeedsToken
			: bridge.error === 'Invalid MCP endpoint'
				? t.mcpBridgeBadEndpoint
				: bridge.error
					? t.mcpBridgeUnreachable
					: ''
	);
	let pinMinutes = $state(DEFAULT_MINUTES);

	function updatePinMinutes(value: string): void {
		const minutes = Number(value);
		pinMinutes = Number.isInteger(minutes) && minutes >= 1 && minutes <= 120 ? minutes : DEFAULT_MINUTES;
		savePinMemorySettings({ ...readPinMemorySettings(), minutes: pinMinutes });
	}

	/** Applies the address/token the operator just typed and reports the socket state. */
	async function connectBridge(): Promise<void> {
		if (!isExtension()) return;
		bridge = await configureMcpBridge(mcp).catch((): BridgeStatus => ({
			enabled: mcp.enabled,
			online: false,
			error: 'connect-failed'
		}));
	}

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
		pinMinutes = readPinMemorySettings().minutes;
		void consumeEntry();
		const cleanups: (() => void)[] = [];
		if (isDesktop()) {
			void deeplinkStatus().then((status) => (afirmaScheme = status)).catch(() => {});
			void deeplinkList().then((domains) => (afirmaAllow = domains)).catch(() => {});
			void listenDeepLinks((request) => {
				afirma = request;
				afirmaRemember = false;
				afirmaNotice = '';
			})
				.then((detach) => cleanups.push(detach))
				.catch(() => {});
		}
		if (isExtension()) {
			cleanups.push(listenMcpBridge((status) => (bridge = status)));
			void mcpBridgeStatus().then((status) => (bridge = status)).catch(() => {});
			if (mcp.enabled) void connectBridge();
		}
		return () => {
			for (const cleanup of cleanups) cleanup();
		};
	});

	/** PWA sign entry (#47): share_target, file_handlers, shortcut, portal deep-link. */
	let deepLink = $state<DeepLinkRequest | null>(null);
	let deepLinkError = $state('');
	let entryNotice = $state('');
	let returned = $state<DeepLinkResult | null>(null);
	/** Desktop shell `afirma://` request (#103): gated portal call awaiting review. */
	let afirma = $state<IncomingRequest | null>(null);
	let afirmaScheme = $state<SchemeStatus | null>(null);
	let afirmaAllow = $state<string[]>([]);
	let afirmaRemember = $state(false);
	let afirmaNotice = $state('');

	/** One-shot intake: preload files/data, never sign without explicit user confirm. */
	async function consumeEntry(): Promise<void> {
		if (typeof location === 'undefined') return;
		const params = new URLSearchParams(location.search);
		const nonce = params.get('share');
		const parsed = parseDeepLink(location.search);
		if (parsed && 'error' in parsed) deepLinkError = parsed.error;
		if (parsed && 'request' in parsed) {
			deepLink = parsed.request;
			format = parsed.request.format;
			files = [new File([parsed.request.bytes.slice()], parsed.request.name)];
		}
		if (nonce) {
			try {
				const shared = await takeShare(nonce);
				const textFile =
					shared.text.length > 0 ? [new File([shared.text], 'compartido.txt', { type: 'text/plain' })] : [];
				const picked = [...shared.files, ...textFile];
				if (picked.length > 0) {
					await adoptFiles(picked);
					entryNotice = 'shared';
				}
			} catch {
				// Stash unreadable (private mode, cleared storage): fall through to manual pick.
			}
		}
	// `launchQueue` (file_handlers): the OS opens a file with the installed app.
	const launcher = (
		window as Window & { launchQueue?: { setConsumer: (fn: (p: { files: File[] }) => void) => void } }
	).launchQueue;
	launcher?.setConsumer((launch) => {
		if (launch.files.length > 0) {
			void adoptFiles(launch.files);
			step = 2;
		}
	});
		// Entry links land on step 2 with files preloaded; plain visits stay on step 1.
		if (files.length > 0) step = 2;
		if (params.has('entry') || params.has('shared') || (parsed && 'request' in parsed)) {
			history.replaceState(null, '', location.pathname);
		}
	}

	/**
	 * Hand the signed result back to the portal. Fires only on explicit click, shows the
	 * destination first, and navigates in the same tab so the user sees where they go.
	 */
	function returnToPortal(): void {
		if (!deepLink?.returnUrl || !returned) return;
		location.href = buildDeepLinkReturn(deepLink.returnUrl, returned);
	}
	/**
	 * Desktop `afirma://` review (#103): triggers the NATIVE per-request dialog
	 * (op + origin + params). `remember` allow-lists ONLY this request's domain.
	 * A confirmed request loads the portal's `dat` payload into the sign flow;
	 * cert pick + sign still happen inside the app.
	 */
	async function confirmAfirma(allow: boolean): Promise<void> {
		if (!afirma || !('gated' in afirma)) {
			afirma = null;
			return;
		}
		const gated = afirma.gated;
		try {
			const ok = await deeplinkConfirm(gated, allow, afirmaRemember);
			afirmaNotice = allow && ok ? t.afirmaConfirmed : '';
			if (ok && allow) {
				const payload = await payloadFromGated(gated);
				if (payload) {
					files = [payload.file];
					if (payload.format) format = payload.format;
					step = 2;
				} else if (gated.params.some(([key]) => key === 'dat')) {
					afirmaNotice = `${t.entryError}: portal dat payload is not valid base64/gzip`;
				}
			}
			if (ok) afirmaAllow = await deeplinkList().catch(() => afirmaAllow);
			afirma = null;
		} catch {
			afirmaNotice = '';
		}
	}
	async function removeAfirmaDomain(domain: string): Promise<void> {
		if (await deeplinkRemove(domain).catch(() => false)) {
			afirmaAllow = afirmaAllow.filter((entry) => entry !== domain);
		}
	}
	$effect(() => applyTheme(theme));
	const THEMES = $derived([
		{ id: 'light' as const, label: t.themeLight, icon: SunIcon },
		{ id: 'dark' as const, label: t.themeDark, icon: MoonIcon },
		{ id: 'system' as const, label: t.themeSystem, icon: MonitorIcon }
	]);

	let files = $state<File[]>([]);
	let fileListEl = $state<HTMLDivElement | null>(null);
	/** Horizontal scroll room left/right of the visible area, for the edge fades. */
	let scrollLeft = $state(0);
	let scrollRight = $state(0);
	function measureFileList() {
		if (!fileListEl) return;
		scrollLeft = fileListEl.scrollLeft;
		scrollRight = fileListEl.scrollWidth - fileListEl.clientWidth - fileListEl.scrollLeft;
	}
	$effect(() => {
		if (files.length >= 0) queueMicrotask(measureFileList);
	});
	/** Depth counter: dragenter/dragleave fire per child element, so a bool would flicker. */
	let dragDepth = $state(0);
	function hasFiles(event: DragEvent): boolean {
		return event.dataTransfer?.types.includes('Files') ?? false;
	}
	let format = $state<SignFormat>('cades');
	let formatOpen = $state(false);
	/**
	 * Signing-side auto-detect (#127): the first dropped file decides the batch format when it
	 * sniffs as something the default cannot sign (OOXML/PDF/XML). Reads only the leading
	 * bytes; a failure to read leaves the current format untouched.
	 */
	async function adoptFiles(picked: File[]): Promise<void> {
		if (picked.length === 0) return;
		files = [...files, ...picked];
		const first = picked[0];
		let suggestion: ReturnType<typeof suggestSignFormat> | null = null;
		try {
			const head = new Uint8Array(await first.slice(0, 72 * 1024).arrayBuffer());
			suggestion = suggestSignFormat(first.name, head as Uint8Array<ArrayBuffer>);
		} catch {
			suggestion = null;
		}
		// OOXML is the only new engine in this slice; ODF/ASiC stay manual (rejected loudly at
		// sign time), and PDF follows the existing deep-link behavior. Refs #127.
		if (suggestion === 'ooxml' || (suggestion === 'pades' && format === 'cades')) format = suggestion;
	}
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

	// Storage of saved identities (#29). IndexedDB in the browser, memory when it is unavailable
	// (prerender), so nothing here can fail at import time.
	const vault = browserVault();

	// The selected-files table repeats the queue's per-document status; the queue itself keeps
	// rendering the detailed result rows.
	let batch = $state<BatchState>(EMPTY_BATCH_STATE);
	$effect(() => queue.subscribe((value) => (batch = value)));

	/** The 3-step flow: 1 configure, 2 files, 3 progress + download. Not persisted. */
	let step = $state<1 | 2 | 3>(1);
	const dragging = $derived(dragDepth > 0 && step === 2);
	const signedCount = $derived(batch.documents.filter((entry) => entry.status === 'signed').length);

	/** Step 2 fires the run: the queue throws only when it is already running, which cannot happen
	 *  here because the button that reaches this is disabled while `phase === 'running'`. */
	function startBatch() {
		if (!signer || files.length === 0) return;
		step = 3;
		void queue.run(signer);
	}

	/** Batch ZIP: the queue holds the signed bytes, the archive is built on demand.
	 *  Native save dialog on desktop, anchor download in the browser. */
	async function downloadZip() {
		await saveBytes(t.queueArchive, buildZip(batch), 'application/zip');
	}


	/** Format engines (#6 CAdES, #7 XAdES, #11 PAdES) plug in here; the queue never looks inside.
	 *  One dispatcher: the format arrives per document (global choice unless overridden per file),
	 *  so a mixed batch signs each file with the engine its format names. */
	function makeSigner(certificate: SignerCert | null): BatchSigner | null {
		if (!certificate?.key || !certificate.algorithm) return null;
		const algorithm = signingAlgorithmName(certificate);
		const { key, der, commonName } = certificate;
		return async (file, bytes, kind) => {
			if (kind === 'cades') {
				const timeStamp = tsaUrl.trim() || undefined;
				return {
					bytes: await signCadesDetached(certificate, bytes, { timeStamp }),
					algorithm
				};
			}
			if (kind === 'xades') {
			return {
				bytes: new TextEncoder().encode(
					await signXades({
						xml: bytes,
						certificates: chain,
						signer: certificate,
						dataObject: { mimeType: file.type || 'application/xml' }
					})
				),
				algorithm
			};
		}
			if (kind === 'ooxml') {
				// RSA only: the package signature is RSASSA-PKCS1-v1_5; a non-RSA key
				// surfaces the engine's error on the document, no silent fallback. Refs #127.
				if (!key) throw new Error('The signing certificate has no private key');
				return {
					bytes: new Uint8Array(
						await signOoxmlPackage(bytes, {
							key,
							certificates: [
								der as Uint8Array,
								...chain.filter((entry) => entry !== certificate).map((entry) => entry.der as Uint8Array)
							],
							signingTime: new Date()
						})
					) as Uint8Array<ArrayBuffer>,
					algorithm
				};
			}
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

	const signer = $derived(makeSigner(cert));
	// Step 1 gates on the certificate alone now: the format lives on step 2, next to the files.
	const signHint = $derived(cert === null ? t.signNeedsCert : '');

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

	<svelte:window
		ondragenter={(event) => {
			if (step === 2 && hasFiles(event)) dragDepth++;
		}}
		ondragleave={(event) => {
			if (step === 2 && hasFiles(event)) dragDepth = Math.max(0, dragDepth - 1);
		}}
		ondragover={(event) => {
			if (step === 2 && hasFiles(event)) event.preventDefault();
		}}
		ondrop={(event) => {
			dragDepth = 0;
			if (step !== 2) return;
			event.preventDefault();
			const dropped = Array.from(event.dataTransfer?.files ?? []);
			if (dropped.length > 0) void adoptFiles(dropped);
		}}
	/>

<TooltipProvider>
	<div
		class="mx-auto flex h-dvh max-w-7xl flex-col gap-6 px-3 py-4 sm:px-5 sm:py-6 [--panel-offset:7.5rem] max-lg:h-auto max-lg:min-h-dvh"
	>
		<header class="flex items-center justify-between gap-4">
			<div class="flex min-w-0 items-center gap-3 sm:gap-4">
				{@render seal(28)}
				<h1 class="font-display text-lg font-semibold tracking-tight">{t.brand}</h1>
			</div>
			<div class="flex shrink-0 items-center gap-2">
				{#if !isDesktop()}<PwaControls copy={t} />{/if}
				<Button
					variant="ghost"
					size="icon"
					aria-label={t.settingsLabel}
					aria-haspopup="dialog"
					onclick={() => (settingsOpen = true)}
				>
					<SettingsIcon class="size-5" aria-hidden="true" />
				</Button>
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
			{#if afirma}
				<!-- Desktop `afirma://` request: gated by the shell, reviewed here, confirmed natively. -->
				<div class="mx-auto w-full max-w-3xl rounded-lg border p-3 text-sm" role="alert">
					{#if 'gated' in afirma}
						<p class="font-medium">{t.afirmaTitle}</p>
						<p class="text-muted-foreground mt-1 text-xs">{t.afirmaHint}</p>
						<p class="mt-2">{t.afirmaOp}: <span class="font-mono">{afirma.gated.op}</span></p>
						<p>{t.afirmaOrigin}: <span class="font-medium">{afirma.gated.origin ?? t.afirmaNoOrigin}</span></p>
						{#if afirma.gated.origin}
							<label class="mt-2 flex items-center gap-2 text-sm">
								<input type="checkbox" class="accent-primary size-4" bind:checked={afirmaRemember} />
								{t.afirmaRemember}
							</label>
						{/if}
						<div class="mt-2 flex gap-2">
							<Button size="sm" disabled={!afirma.gated.origin} onclick={() => void confirmAfirma(true)}>
								{t.afirmaReview}
							</Button>
							<Button size="sm" variant="outline" onclick={() => (afirma = null)}>
								{t.afirmaDismiss}
							</Button>
						</div>
					{:else}
						<p class="text-destructive font-medium">{t.entryError}: {afirma.refused.reason}</p>
						<p class="text-muted-foreground mt-1 font-mono text-xs">{afirma.refused.raw}</p>
						<div class="mt-2">
							<Button size="sm" variant="outline" onclick={() => (afirma = null)}>
								{t.afirmaDismiss}
							</Button>
						</div>
					{/if}
				</div>
			{/if}
			{#if afirmaNotice}
				<p class="mx-auto w-full max-w-3xl text-sm" role="status">{afirmaNotice}</p>
			{/if}
			<div
				id="panel-sign"
				role="tabpanel"
				aria-label={t.modeSign}
				class="mx-auto flex w-full max-w-3xl flex-col gap-6"
			>
				<!-- Step indicator: where the flow stands; back-navigation allowed when idle. -->
				<ol class="flex items-center gap-2" aria-label={t.modeSign}>
					{#each [1, 2, 3] as entry, index (entry)}
						{@const labels = [t.stepConfigure, t.stepFiles, t.stepDownload]}
						{@const reached = step >= entry}
						{@const canJump = entry < step && batch.phase !== 'running'}
						<li class="flex min-w-0 items-center gap-2" aria-current={step === entry ? 'step' : undefined}>
							{#if canJump}
								<button
									type="button"
									class="text-muted-foreground hover:text-foreground flex items-center gap-1.5 text-sm font-medium"
									onclick={() => (step = entry as 1 | 2 | 3)}
								>
									<CheckIcon class="size-4" aria-hidden="true" />
									{labels[index]}
								</button>
							{:else}
								<span
									class="flex items-center gap-1.5 text-sm font-medium {reached
										? 'text-foreground'
										: 'text-muted-foreground'}"
								>
									{#if step > entry}
										<CheckIcon class="size-4" aria-hidden="true" />
									{:else}
										<span
											class="bg-muted text-foreground inline-flex size-5 items-center justify-center rounded-full text-xs"
											aria-hidden="true">{entry}</span
										>
									{/if}
									{labels[index]}
								</span>
							{/if}
							{#if index < 2}
								<span class="bg-border h-px w-6 shrink-0" aria-hidden="true"></span>
							{/if}
						</li>
					{/each}
				</ol>

				{#if step === 1}
					<!-- Step 1: configure the signature — certificate and format. -->
					<Card>
						<CardHeader>
						<h2 class="font-display text-xl font-semibold">{t.step1Title}</h2>
						</CardHeader>
						<CardContent class="flex flex-col gap-4">
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

							<div class="flex items-center justify-between gap-3 border-t pt-4">
								{#if signHint}
									<p id="sign-hint" class="text-muted-foreground text-sm">{signHint}</p>
								{:else}
									<span class="text-sm" aria-hidden="true"></span>
								{/if}
								<Button
									disabled={signer === null}
									aria-describedby={signHint ? 'sign-hint' : undefined}
									onclick={() => (step = 2)}
								>
									{t.next}
									<ArrowRightIcon class="size-4" aria-hidden="true" />
								</Button>
							</div>
						</CardContent>
					</Card>
				{:else if step === 2}
					<!-- Step 2: documents and format — global for the batch, overridable per file;
					     Firmar starts the run and lands on step 3. -->
					<Card>
						<CardHeader>
						<h2 class="font-display text-xl font-semibold">{t.step2Title}</h2>
						</CardHeader>
						<CardContent class="flex flex-col gap-4">
							{#if deepLinkError || entryNotice === 'shared' || deepLink}
								<!-- PWA entry: what preloaded this batch and where it returns. -->
								<div class="rounded-lg border p-3 text-sm" role="status">
									{#if deepLinkError}
										<p class="text-destructive">{t.entryError}: {deepLinkError}</p>
									{:else if deepLink}
										<p>{t.entryPortal}: <span class="font-medium">{deepLink.name}</span> · {deepLink.format}</p>
										{#if deepLink.returnUrl}
											<p class="text-muted-foreground mt-1 text-xs">{t.entryReturn}: {deepLink.returnUrl}</p>
										{/if}
									{:else}
										<p>{t.entryShared}</p>
									{/if}
								</div>
							{/if}
							<fieldset class="flex w-full min-w-0 flex-col gap-2">
								<legend class="text-sm font-medium">{t.formatTitle}</legend>
								<Button
									variant="outline"
									class="w-full min-w-0 justify-between px-4 py-3 text-left h-auto whitespace-normal"
									aria-haspopup="dialog"
									onclick={() => (formatOpen = true)}
								>
									<span class="flex min-w-0 flex-1 items-center gap-3">
										<span class="font-medium truncate">{currentFormat.label}</span>
										<span class="text-muted-foreground tnum text-sm"
											>{currentFormat.extension}</span
										>
										<span class="text-muted-foreground min-w-0 truncate text-sm">{t.formatAll}</span>
									</span>
									<ChevronDownIcon class="text-muted-foreground size-4" aria-hidden="true" />
								</Button>
							</fieldset>

							{#if files.length === 0}
								<FileDrop copy={t} onselect={(picked) => void adoptFiles(picked)} />
							{/if}
							{#if files.length > 0}
								<div class="relative min-w-0">
								{#if scrollLeft > 0}<div aria-hidden="true" class="from-background pointer-events-none absolute inset-y-0 start-0 z-10 w-6 bg-gradient-to-r to-transparent"></div>{/if}
								{#if scrollRight > 0}<div aria-hidden="true" class="from-background pointer-events-none absolute inset-y-0 end-0 z-10 w-6 bg-gradient-to-l to-transparent"></div>{/if}
								<div class="max-h-80 overflow-x-hidden overflow-y-auto" onscroll={measureFileList} bind:this={fileListEl}>
								<div class="overflow-visible">
								<Table>
									<TableBody>
										<TableRow class="border-border/50 hover:bg-transparent">
											<TableCell class="p-0">
												<FileDrop copy={t} compact onselect={(picked) => void adoptFiles(picked)} />
											</TableCell>
										</TableRow>
										{#each files as file, index (index)}
											{@const entry = batch.documents[index] ?? null}
											{@const docFormat = entry?.format ?? format}
											<FileCard
												copy={t}
												file={toSelectedFile(file)}
												document={entry}
												removable={true}
												downloadable={false}
												onremove={() => {
													files = files.filter((_, position) => position !== index);
												}}
											>
																											{#snippet actions()}
																												<select
																													class="border-input bg-background h-8 shrink-0 rounded-md border px-2 text-sm"
																													aria-label="{t.formatTitle}: {file.name}"
																													value={docFormat}
																													onchange={(event) => {
																													if (entry) queue.setFormat(entry.id, event.currentTarget.value as SignFormat);
																													}}
																													>
																													{#each SIGN_FORMATS as option (option.id)}
																														<option value={option.id} disabled={!option.implemented}>
																															{option.label}
																														</option>
																													{/each}
																												</select>
																											{/snippet}
	</FileCard>
										{/each}
									</TableBody>
								</Table>
								</div>
								</div>
								</div>
							{/if}

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
												<UploadIcon class="size-4" aria-hidden="true" />
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

							<div class="flex items-center justify-between gap-3 border-t pt-4">
								<Button variant="outline" onclick={() => (step = 1)}>
									<ArrowLeftIcon class="size-4" aria-hidden="true" />
									{t.back}
								</Button>
								<Button disabled={files.length === 0} onclick={startBatch}>
									<PenLineIcon class="size-4" aria-hidden="true" />
									{t.signButton}
								</Button>
							</div>
						</CardContent>
					</Card>
				{:else}
					<!-- Step 3: progress and downloads. The run started on the Firmar click. -->
					<Card>
						<CardHeader>
							<h2 class="font-display text-xl font-semibold">{t.stepDownload}</h2>
						</CardHeader>
						<CardContent class="flex flex-col gap-4">
							{#if deepLink?.returnUrl}
								{@const doc = batch.documents.find((entry) => entry.status === 'signed' && entry.bytes)}
								<!-- Portal hand-back: explicit click only, destination shown first. -->
								<div class="rounded-lg border p-3 text-sm" role="status">
									{#if doc?.bytes}
										{@const result = {
											format: doc.format,
											signatureB64: encodeBase64(doc.bytes),
											algorithm: doc.algorithm,
											sha256: doc.sha256
										}}
										{@const href = buildDeepLinkReturn(deepLink.returnUrl, result)}
										<p>{t.entryReturnReady}</p>
										<p class="text-muted-foreground mt-1 break-all text-xs">{href}</p>
										<div class="mt-2">
											<Button
												onclick={() => {
													returned = result;
													returnToPortal();
												}}
											>
												{t.entryReturnButton}
											</Button>
										</div>
									{:else}
										<p class="text-muted-foreground">{t.entryReturnWait}</p>
									{/if}
								</div>
							{/if}
							<BatchQueue copy={t} {queue} />
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
												removable={false}
												onremove={() => {}}
											/>
										{/each}
									</TableBody>
								</Table>
							{/if}

							<div class="flex flex-wrap items-center justify-between gap-3 border-t pt-4">
								<Button
									variant="outline"
									disabled={batch.phase === 'running'}
									onclick={() => (step = 2)}
								>
									<ArrowLeftIcon class="size-4" aria-hidden="true" />
									{t.back}
								</Button>
								<div class="flex flex-wrap items-center gap-2">
									<Button variant="outline" disabled={signedCount === 0} onclick={downloadZip}>
										<DownloadIcon class="size-4" aria-hidden="true" />
										{t.queueDownload}
									</Button>
									<Button
										disabled={batch.phase === 'running'}
										onclick={() => {
											files = [];
											step = 2;
										}}
									>
										<PlusIcon class="size-4" aria-hidden="true" />
										{t.newBatch}
									</Button>
								</div>
							</div>
						</CardContent>
					</Card>
				{/if}
			</div>
		</main>
	</div>

	{#if dragging}
		<!-- Whole-screen target: the page takes any file dropped anywhere while step 2 is open. -->
		<div
			role="status"
			aria-live="polite"
			class="bg-background/60 border-primary fixed inset-0 z-50 flex items-center justify-center border-4 border-dashed backdrop-blur-sm"
		>
			<p class="text-primary font-display text-2xl font-semibold">{t.dropOverlay}</p>
		</div>
	{/if}

	<Dialog bind:open={settingsOpen}>
		<DialogContent class="max-h-[90dvh] overflow-y-auto sm:max-w-lg">
			<DialogHeader>
				<DialogTitle>{t.settingsTitle}</DialogTitle>
			</DialogHeader>

			<div
				class="bg-muted grid grid-cols-3 gap-1 rounded-lg p-1"
				role="tablist"
				tabindex="-1"
				aria-label={t.settingsTitle}
				onkeydown={moveSettingsTab}
			>
				<Button
					id="settings-tab-general"
					role="tab"
					variant={settingsTab === 'general' ? 'default' : 'ghost'}
					size="sm"
					class="h-8 min-w-0 px-2"
					aria-selected={settingsTab === 'general'}
					aria-controls="settings-panel-general"
					tabindex={settingsTab === 'general' ? 0 : -1}
					onclick={() => (settingsTab = 'general')}>{t.settingsTab}</Button
				>
				<Button
					id="settings-tab-mcp"
					role="tab"
					variant={settingsTab === 'mcp' ? 'default' : 'ghost'}
					size="sm"
					class="h-8 min-w-0 px-2"
					aria-selected={settingsTab === 'mcp'}
					aria-controls="settings-panel-mcp"
					tabindex={settingsTab === 'mcp' ? 0 : -1}
					onclick={() => (settingsTab = 'mcp')}>{t.mcpTab}</Button
				>
				<Button
					id="settings-tab-help"
					role="tab"
					variant={settingsTab === 'help' ? 'default' : 'ghost'}
					size="sm"
					class="h-8 min-w-0 px-2"
					aria-selected={settingsTab === 'help'}
					aria-controls="settings-panel-help"
					tabindex={settingsTab === 'help' ? 0 : -1}
					onclick={() => (settingsTab = 'help')}>{t.helpMenu}</Button
				>
			</div>

			{#if settingsTab === 'general'}
				<div
					id="settings-panel-general"
					role="tabpanel"
					aria-labelledby="settings-tab-general"
					tabindex="0"
					class="flex flex-col gap-4"
				>
					<!-- Idioma -->
					<section class="flex flex-col gap-2">
						<h3 class="flex items-center gap-2 text-sm font-medium">
							<LanguagesIcon class="size-4" aria-hidden="true" />
							{t.langLabel}
						</h3>
						<div class="bg-muted inline-flex w-fit items-center gap-1 rounded-lg p-1" role="group" aria-label={t.langLabel}>
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
					</section>

					<!-- Tema -->
					<section class="flex flex-col gap-2">
						<h3 class="flex items-center gap-2 text-sm font-medium">{t.themeLabel}</h3>
						<div class="bg-muted inline-flex w-fit items-center gap-1 rounded-lg p-1" role="group" aria-label={t.themeLabel}>
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
					</section>

					<!-- PIN en memoria: optional, never written to disk. -->
					<section class="flex flex-col gap-2">
						<h3 class="flex items-center gap-2 text-sm font-medium">{t.pinMemoryTitle}</h3>
						<div class="flex items-center gap-2">
							<label class="text-sm" for="pin-memory-minutes">{t.pinMemoryMinutes}</label>
							<Input
								id="pin-memory-minutes"
								type="number"
								min="1"
								max="120"
								class="w-20"
								value={pinMinutes}
								oninput={(event) => updatePinMinutes(event.currentTarget.value)}
							/>
						</div>
						<p class="text-muted-foreground text-xs">{t.pinMemoryHint}</p>
					</section>

					{#if extensionSettings}
						<!-- Acceso a sitios: extension build only; the snippet renders the SiteAccess panel. -->
						<section class="flex flex-col gap-2">
							<h3 class="flex items-center gap-2 text-sm font-medium">
								<GlobeIcon class="size-4" aria-hidden="true" />
								{extensionCopy[lang].access}
							</h3>
							{@render extensionSettings(lang)}
						</section>
					{/if}
					{#if isDesktop()}
						<!-- `afirma://` portals: desktop shell only; entries land here via per-request confirm. -->
						<section class="flex flex-col gap-2">
							<h3 class="flex items-center gap-2 text-sm font-medium">{t.afirmaTitle}</h3>
							<p class="text-muted-foreground text-xs" role="status">
								{afirmaScheme === 'ours'
									? t.afirmaSchemeOurs
									: afirmaScheme === 'none'
										? t.afirmaSchemeNone
										: afirmaScheme
											? t.afirmaSchemeOther
											: ''}
							</p>
							<p class="text-muted-foreground text-xs">{t.afirmaHint}</p>
							<h4 class="text-sm font-medium">{t.afirmaListTitle}</h4>
							{#if afirmaAllow.length === 0}
								<p class="text-muted-foreground text-sm">{t.afirmaListEmpty}</p>
							{:else}
								<ul class="flex flex-col gap-1">
									{#each afirmaAllow as domain (domain)}
										<li class="flex items-center justify-between gap-2 text-sm">
											<span class="font-mono">{domain}</span>
											<Button size="sm" variant="outline" onclick={() => void removeAfirmaDomain(domain)}>
												{t.afirmaRemove}
											</Button>
										</li>
									{/each}
								</ul>
							{/if}
						</section>
					{/if}
				</div>
			{:else if settingsTab === 'mcp'}
				<div
					id="settings-panel-mcp"
					role="tabpanel"
					aria-labelledby="settings-tab-mcp"
					tabindex="0"
					class="flex flex-col gap-3"
				>
					<h3 class="flex items-center gap-2 text-sm font-medium">
						<PlugIcon class="size-4" aria-hidden="true" />
						{t.mcpMenu}
					</h3>
					<p class="text-muted-foreground text-sm">{t.mcpHint}</p>
					<div class="bg-muted/40 grid gap-4 rounded-lg p-3">
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
						{#if isExtension()}
							<div class="grid gap-2 border-t pt-3">
								<h4 class="text-sm font-medium">{t.mcpBridgeTitle}</h4>
								<label class="flex items-center gap-2 text-sm">
									<input
										type="checkbox"
										class="accent-primary size-4"
										bind:checked={mcp.enabled}
										onchange={() => {
											updateMcp({ enabled: mcp.enabled });
											void connectBridge();
										}}
									/>
									{t.mcpBridgeEnable}
								</label>
								<p class="text-muted-foreground text-xs">{t.mcpBridgeHint}</p>
								<div class="flex items-center gap-3">
									<Button type="button" variant="outline" size="sm" onclick={connectBridge}>
										{t.mcpBridgeConnect}
									</Button>
									<p class="text-muted-foreground text-xs" role="status">
										{bridge.online ? t.mcpBridgeOnline : t.mcpBridgeOffline}
									</p>
								</div>
								{#if bridgeError}
									<p class="text-destructive text-xs" role="alert">{bridgeError}</p>
								{/if}
							</div>
						{/if}
					</div>
				</div>
			{:else}
				<div
					id="settings-panel-help"
					role="tabpanel"
					aria-labelledby="settings-tab-help"
					tabindex="0"
					class="flex flex-col gap-4"
				>
					<div class="flex items-start gap-2">
						<ShieldCheckIcon class="text-muted-foreground mt-0.5 size-4 shrink-0" aria-hidden="true" />
						<div>
							<p class="text-sm font-medium">{t.privacyTitle}</p>
							<p class="text-muted-foreground text-sm">{t.privacyNote}</p>
						</div>
					</div>

					<a
						class="text-primary flex w-fit items-center gap-1.5 text-sm underline decoration-primary/40 underline-offset-4"
						href="https://git.eduard3v.com/EduarD3V/AutoFirme"
						target="_blank"
						rel="noreferrer"
					>
						<CodeIcon class="size-4" aria-hidden="true" />
						{t.sourceCode}
					</a>

					<a
						class="text-primary flex w-fit items-center gap-1.5 text-sm underline decoration-primary/40 underline-offset-4"
						href="/diagnostics"
					>
						<ActivityIcon class="size-4" aria-hidden="true" />
						{t.diagnosticsLink}
					</a>
				</div>
			{/if}
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
						class="hover:border-primary/50 has-[:checked]:border-primary has-[:checked]:bg-primary/5 flex cursor-pointer items-center gap-3 rounded-lg border px-3.5 py-3 transition-colors {option.id === 'ooxml' ? 'flex-wrap' : ''}"
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
						{#if option.id === 'ooxml'}
							<span class="text-muted-foreground block w-full text-xs">{t.formatOoxmlNoVerify}</span>
						{/if}
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
