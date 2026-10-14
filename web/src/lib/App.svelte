<script lang="ts">
	import { copy, type Copy, type Lang } from '#lib/i18n';
	import { createBatchQueue, type BatchSigner } from '#lib/batch';
	import { signCadesDetached } from '#lib/cms';
	import {
		SIGN_FORMATS,
		signingAlgorithmName,
		toSelectedFile,
		type SignFormat,
		type SignerCert
	} from '#lib/sign';
	import BatchQueue from '#lib/components/BatchQueue.svelte';
	import CertPicker from '#lib/components/CertPicker.svelte';
	import FileCard from '#lib/components/FileCard.svelte';
	import FileDrop from '#lib/components/FileDrop.svelte';

	let lang = $state<Lang>('es');
	const t = $derived<Copy>(copy[lang]);

	let files = $state<File[]>([]);
	let format = $state<SignFormat>('cades');
	// Only the certificate that owns the container's private key can sign.
	let cert = $state<SignerCert | null>(null);

	const queue = createBatchQueue();
	$effect(() => queue.set(files, format));

	/** Format engines (#6 CAdES, #7 XAdES, #11 PAdES) plug in here; the queue never looks inside. */
	function makeSigner(certificate: SignerCert | null, kind: SignFormat): BatchSigner | null {
		if (!certificate?.key || !certificate.algorithm || kind !== 'cades') return null;
		const algorithm = signingAlgorithmName(certificate);
		return async (_file, bytes) => ({
			bytes: await signCadesDetached(certificate, bytes),
			algorithm
		});
	}

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
			<FileDrop copy={t} onselect={(picked) => (files = [...files, ...picked])} />
			{#if files.length > 0}
				<ul class="flex flex-col gap-2">
					{#each files as file, index (index)}
						<li>
							<FileCard
								copy={t}
								file={toSelectedFile(file)}
								onremove={() => (files = files.filter((_, position) => position !== index))}
							/>
						</li>
					{/each}
				</ul>
			{/if}
		</section>

		{#if files.length > 0}
			<section class="flex flex-col gap-5">
				<h2 class="text-lg font-semibold">{t.step2Title}</h2>

				<CertPicker copy={t} {cert} onpick={(picked) => (cert = picked)} />

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

				<BatchQueue copy={t} {queue} {signer} hint={signHint} />
			</section>
		{/if}
	</main>

	<footer class="mt-auto border-t pt-4 text-sm opacity-70">{t.privacyNote}</footer>
</div>
