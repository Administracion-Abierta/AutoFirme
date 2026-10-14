<script lang="ts">
	import { copy, type Copy, type Lang } from '#lib/i18n';
	import {
		SIGN_FORMATS,
		downloadText,
		toSelectedFile,
		type SelectedFile,
		type SignFormat,
		type SignResult,
		type SignerCert
	} from '#lib/sign';
	import { signXades } from '#lib/xades';
	import FileDrop from '#lib/components/FileDrop.svelte';
	import FileCard from '#lib/components/FileCard.svelte';
	import CertPicker from '#lib/components/CertPicker.svelte';
	import SignButton from '#lib/components/SignButton.svelte';
	import ResultCard from '#lib/components/ResultCard.svelte';

	let lang = $state<Lang>('es');
	const t = $derived<Copy>(copy[lang]);

	let file = $state<SelectedFile | null>(null);
	let format = $state<SignFormat>('cades');
	// Only the certificate that owns the container's private key can sign.
	let cert = $state<SignerCert | null>(null);
	// Full container chain of the loaded PKCS#12 (#5) — the KeyInfo of the signature embeds it.
	let chain = $state<readonly SignerCert[]>([]);
	let result = $state<SignResult | null>(null);
	let signing = $state(false);

	const canSign = $derived(file !== null && cert?.key != null);
	const signHint = $derived(
		file === null ? t.signNeedsFile : cert === null ? t.signNeedsCert : ''
	);

	function clearFile() {
		file = null;
		result = null;
	}

	async function sign() {
		const selected = file;
		if (!selected || !cert) return;
		result = null;
		if (format !== 'xades') {
			result = { ok: false, message: t.signUnsupported };
			return;
		}
		signing = true;
		try {
			const xsig = await signXades({
				xml: new Uint8Array(await selected.file.arrayBuffer()),
				certificates: chain,
				signer: cert,
				dataObject: { mimeType: selected.file.type || 'application/xml' }
			});
			const extension = SIGN_FORMATS.find((option) => option.id === format)!.extension;
			downloadText(`${selected.name}${extension}`, xsig, 'application/xml');
			result = { ok: true, message: t.signDone };
		} catch (reason) {
			result = { ok: false, message: `${t.signFailed} ${reason instanceof Error ? reason.message : ''}`.trim() };
		} finally {
			signing = false;
		}
	}
</script>

<div class="mx-auto flex min-h-dvh max-w-3xl flex-col gap-8 px-5 py-10">
	<header class="flex flex-wrap items-start justify-between gap-4">
		<div>
			<h1 class="text-3xl font-semibold tracking-tight">{t.brand}</h1>
			<p class="mt-1 opacity-70">{t.tagline}</p>
		</div>
		<div class="flex items-center gap-2 text-sm">
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
	</header>

	<main class="flex flex-col gap-8">
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
					label={signing ? t.certLoading : t.signButton}
					disabled={!canSign || signing}
					hint={signHint}
					onsign={sign}
				/>

				<ResultCard copy={t} {result} />
			</section>
		{/if}
	</main>

	<footer class="mt-auto border-t pt-4 text-sm opacity-70">{t.privacyNote}</footer>
</div>
