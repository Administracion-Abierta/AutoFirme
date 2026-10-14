<script lang="ts">
	import type { Copy } from '#lib/i18n';
	import type { Check, VerificationReport } from '#lib/verify';

	interface Props {
		copy: Copy;
		report: VerificationReport | null;
		running: boolean;
	}

	let { copy, report, running }: Props = $props();

	const formatLabels: Record<VerificationReport['format'], string> = {
		cades: 'CAdES',
		xades: 'XAdES',
		pdf: 'PAdES',
		unknown: '?'
	};

	const styles: Record<Check['status'], { card: string; dot: string }> = {
		pass: { card: 'border-emerald-600/60 bg-emerald-600/5', dot: 'bg-emerald-600' },
		fail: { card: 'border-red-600/60 bg-red-600/5', dot: 'bg-red-600' },
		info: { card: 'border-dashed opacity-80', dot: 'bg-ink-700 dark:bg-ink-100' }
	};
</script>

<section class="flex flex-col gap-3" aria-live="polite">
	<h2 class="text-lg font-semibold">{copy.verifyReportTitle}</h2>

	{#if running}
		<p class="text-sm opacity-70">{copy.verifyRunning}</p>
	{:else if report}
		<p
			class="rounded-xl border px-4 py-3 font-medium {report.ok
				? 'border-emerald-600 text-emerald-700 dark:text-emerald-400'
				: 'border-red-600 text-red-700 dark:text-red-400'}"
		>
			{report.ok ? copy.verifyOk : copy.verifyFail}
			<span class="ml-1 font-normal opacity-70">{formatLabels[report.format]}</span>
		</p>

		<ul class="flex flex-col gap-2">
			{#each report.checks as entry, index (index)}
				<li
					class="flex items-start gap-3 rounded-xl border px-4 py-3 {styles[entry.status].card}"
					data-check={entry.id}
					data-status={entry.status}
				>
					<span class="mt-1.5 size-2.5 shrink-0 rounded-full {styles[entry.status].dot}" aria-hidden="true"
					></span>
					<div class="min-w-0">
						<p class="font-medium">{copy.checkLabels[entry.id] ?? entry.id}</p>
						<p class="break-words text-sm opacity-80">{entry.detail}</p>
					</div>
					<span class="ml-auto shrink-0 text-xs uppercase tracking-wide opacity-60">
						{entry.status === 'pass' ? '✓' : entry.status === 'fail' ? '✗' : '—'}
					</span>
				</li>
			{/each}
		</ul>

		{#if report.signer}
			<dl class="grid gap-1 rounded-xl bg-ink-100 px-4 py-3 text-sm dark:bg-ink-700">
				<dt class="text-xs uppercase tracking-wide opacity-60">{copy.verifySigner}</dt>
				<dd class="break-words">{report.signer.subject}</dd>
				<dd class="break-words opacity-70">{report.signer.issuer}</dd>
				<dd class="opacity-70">serial {report.signer.serial}</dd>
				<dd class="opacity-70">{report.signer.notBefore} → {report.signer.notAfter}</dd>
			</dl>
		{:else}
			<p class="text-sm opacity-70">{copy.verifyNoSigner}</p>
		{/if}

		{#if report.signingTime}
			<p class="text-sm opacity-70">{copy.verifySigningTime}: {report.signingTime}</p>
		{/if}
	{/if}
</section>
