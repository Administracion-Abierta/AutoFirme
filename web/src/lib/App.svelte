<script lang="ts">
	import { copy, type Copy, type Lang } from '#lib/i18n';
	import {
		SIGN_FORMATS,
		download,
		toSelectedFile,
		type SelectedFile,
		type SignFormat,
		type SignResult,
		type SignerCert
	} from '#lib/sign';
	import { signXades } from '#lib/xades';
	import { signCadesDetached } from '#lib/cms';
	import { verifyArtifact, type Bytes, type VerificationReport } from '#lib/verify';
	import FileDrop from '#lib/components/FileDrop.svelte';
	import FileCard from '#lib/components/FileCard.svelte';
	import CertPicker from '#lib/components/CertPicker.svelte';
	import SignButton from '#lib/components/SignButton.svelte';
	import ResultCard from '#lib/components/ResultCard.svelte';
	import VerifyReport from '#lib/components/VerifyReport.svelte';

	let lang = $state<Lang>('es');
	const t = $derived<Copy>(copy[lang]);
	let mode = $state<'sign' | 'verify'>('sign');

	let file = $state<SelectedFile | null>(null);
	let format = $state<SignFormat>('cades');
	// Only the certificate that owns the container's private key can sign.
	let cert = $state<SignerCert | null>(null);
	// Full container chain of the loaded PKCS#12 (#5) — the KeyInfo of the signature embeds it.
	let chain = $state<readonly SignerCert[]>([]);
	let result = $state<SignResult | null>(null);
	let signing = $state(false);

	const selected = $derived(SIGN_FORMATS.find((option) => option.id === format)!);
	const canSign = $derived(file !== null && cert?.key != null && selected.implemented && !signing);
	const signHint = $derived(
		file === null
			? t.signNeedsFile
			: cert === null
				? t.signNeedsCert
				: selected.implemented
					? ''
					: t.signUnsupported
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

	async function pickSignature(picked: File) {
		verifyBytes = new Uint8Array(await picked.arrayBuffer());
		verifyFile = toSelectedFile(picked);
		detachedFile = null;
		detachedBytes = null;
		report = null;
		await runVerify();
	}

	async function pickDocument(picked: File) {
		detachedBytes = new Uint8Array(await picked.arrayBuffer());
		detachedFile = toSelectedFile(picked);
		await runVerify();
	}

	function clearFile() {
		file = null;
		result = null;
	}

	/** Sign the picked document with the chosen format; keys never leave the page. */
	async function sign() {
		if (!file || !cert) return;
		result = null;
		if (!selected.implemented) {
			result = { ok: false, message: t.signUnsupported };
			return;
		}
		signing = true;
		try {
			const bytes = new Uint8Array(await file.source.arrayBuffer());
			if (format === 'xades') {
				const xsig = await signXades({
					xml: bytes,
					certificates: chain,
					signer: cert,
					dataObject: { mimeType: file.source.type || 'application/xml' }
				});
				download(`${file.name}${selected.extension}`, xsig, 'application/xml');
			} else {
				download(
					`${file.name}${selected.extension}`,
					await signCadesDetached(cert, bytes),
					'application/octet-stream'
				);
			}
			result = { ok: true, message: t.signDone };
		} catch (reason) {
			result = {
				ok: false,
				message: `${t.signFailed} ${reason instanceof Error ? reason.message : ''}`.trim()
			};
		} finally {
			signing = false;
		}
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

<div class="mx-auto flex min-h-dvh max-w-3xl flex-col gap-8 px-5 py-10">
	<header class="flex flex-wrap items-start justify-between gap-4">
		<div>
			<h1 class="text-3xl font-semibold tracking-tight">{t.brand}</h1>
			<p class="mt-1 opacity-70">{t.tagline}</p>
		</div>
		<div class="flex flex-col items-end gap-2 text-sm">
			<div class="flex items-center gap-2">
				<span class="opacity-70">{t.langLabel}</span>
				<button
					type="button"
					class="rounded-lg border px-2.5 py-1 {lang === 'es' ? 'bg-brand-500 text-white' : ''}"
					aria-pressed={lang === 'es'}
					onclick={() => (lang = 'es')}>ES</button
				>
				<button
					type="button"
					class="rounded-lg border px-2.5 py-1 {lang === 'en' ? 'bg-brand-500 text-white' : ''}"
					aria-pressed={lang === 'en'}
					onclick={() => (lang = 'en')}>EN</button
				>
			</div>
			<div class="flex items-center gap-2" role="tablist" aria-label={t.modesLabel}>
				<button
					type="button"
					role="tab"
					aria-selected={mode === 'sign'}
					aria-controls="panel-sign"
					class="rounded-lg border px-3 py-1 font-medium {mode === 'sign'
						? 'bg-brand-500 text-white'
						: ''}"
					onclick={() => (mode = 'sign')}>{t.modeSign}</button
				>
				<button
					type="button"
					role="tab"
					aria-selected={mode === 'verify'}
					aria-controls="panel-verify"
					class="rounded-lg border px-3 py-1 font-medium {mode === 'verify'
						? 'bg-brand-500 text-white'
						: ''}"
					onclick={() => (mode = 'verify')}>{t.modeVerify}</button
				>
			</div>
		</div>
	</header>

	<main class="flex flex-col gap-8">
		{#if mode === 'sign'}
			<div id="panel-sign" role="tabpanel" aria-label={t.modeSign} class="flex flex-col gap-8">
			<section class="flex flex-col gap-3">
				<h2 class="text-lg font-semibold">{t.step1Title}</h2>
				{#if file === null}
					<FileDrop
						copy={t}
						onselect={(picked) => {
							file = toSelectedFile(picked);
						}}
					/>
				{:else}
					<FileCard copy={t} {file} onremove={clearFile} />
				{/if}
			</section>

			{#if file !== null}
				<section class="flex flex-col gap-5">
					<h2 class="text-lg font-semibold">{t.step2Title}</h2>

					<CertPicker
						copy={t}
						{cert}
						onpick={(picked, certificates) => {
							cert = picked;
							chain = certificates;
							result = null;
						}}
					/>

					<fieldset class="flex flex-col gap-2">
						<legend class="text-sm font-medium">{t.formatTitle}</legend>
						{#each SIGN_FORMATS as option (option.id)}
							<label class="flex items-center gap-3 rounded-lg border px-3 py-2">
								<input
									type="radio"
									name="format"
									value={option.id}
									checked={format === option.id}
									onchange={() => (format = option.id)}
								/>
								<span class="font-medium">{option.label}</span>
								<span class="text-sm opacity-70">{option.extension}</span>
								{#if !option.implemented}
									<span class="ml-auto rounded-full bg-ink-100 px-2 py-0.5 text-xs dark:bg-ink-700">
										{t.pendingIssue} #{option.issue}
									</span>
								{/if}
							</label>
						{/each}
					</fieldset>

					<SignButton
						label={t.signButton}
						disabled={!canSign}
						hint={signHint}
						onsign={sign}
					/>

					<ResultCard copy={t} {result} />
				</section>
			{/if}
			</div>
		{:else}
			<div id="panel-verify" role="tabpanel" aria-label={t.modeVerify} class="flex flex-col gap-8">
			<section class="flex flex-col gap-3">
				<h2 class="text-lg font-semibold">{t.verifyStepTitle}</h2>
				{#if verifyFile === null}
					<FileDrop copy={t} onselect={pickSignature} title={t.verifyDropTitle} hint={t.verifyDropHint} />
				{:else}
					<FileCard copy={t} file={verifyFile} onremove={clearVerification} />
				{/if}
			</section>

			{#if verifyFile !== null}
				<VerifyReport copy={t} {report} {running} />

				{#if needsDocument}
					<section class="flex flex-col gap-3">
						<h2 class="text-lg font-semibold">{t.verifyAddDocument}</h2>
						<FileDrop
							copy={t}
							onselect={pickDocument}
							title={t.verifyAddDocument}
							hint={t.verifyAddDocumentHint}
						/>
					</section>
				{:else if detachedFile !== null}
					<section class="flex flex-col gap-3">
						<h2 class="text-lg font-semibold">{t.verifyDocumentLabel}</h2>
						<FileCard copy={t} file={detachedFile} onremove={clearDocument} />
					</section>
				{/if}
			{/if}
			</div>
		{/if}
	</main>

	<footer class="mt-auto border-t pt-4 text-sm opacity-70">{t.privacyNote}</footer>
</div>
