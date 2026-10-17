<script lang="ts">
	import { Alert, AlertDescription, AlertTitle } from '#lib/components/ui/alert/index.js';
	import CircleCheckIcon from '@lucide/svelte/icons/circle-check';
	import CircleXIcon from '@lucide/svelte/icons/circle-x';
	import TriangleAlertIcon from '@lucide/svelte/icons/triangle-alert';
	import InfoIcon from '@lucide/svelte/icons/info';
	import ArrowRightIcon from '@lucide/svelte/icons/arrow-right';
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
	const styles: Record<Check['status'], string> = {
		pass: 'border-emerald-600/60 bg-emerald-600/5',
		warn: 'border-amber-600/60 bg-amber-600/5',
		fail: 'border-destructive/60 bg-destructive/5',
		info: 'border-dashed opacity-80'
	};
</script>

<section class="flex flex-col gap-3" aria-live="polite">
	<h2 class="font-display text-xl font-semibold">{copy.verifyReportTitle}</h2>

	{#if running}
		<p class="text-muted-foreground text-sm">{copy.verifyRunning}</p>
	{:else if report}
		{#if report.status === 'invalid'}
			<Alert variant="destructive">
				<CircleXIcon class="size-4" aria-hidden="true" />
				<AlertTitle>
					{copy.verifyFail}
					<span class="ml-1 font-normal opacity-70">{formatLabels[report.format]}</span>
				</AlertTitle>
			</Alert>
		{:else if report.status === 'valid-but-expired'}
			<!-- Valid signature, certificate out of its window: warning, never a rejection (#40). -->
			<Alert class="border-amber-600/50 bg-amber-600/5 text-amber-800 dark:text-amber-300">
				<TriangleAlertIcon class="size-4" aria-hidden="true" />
				<AlertTitle>
					{copy.verifyExpired}
					<span class="ml-1 font-normal opacity-70">{formatLabels[report.format]}</span>
				</AlertTitle>
			</Alert>
		{:else}
			<Alert class="border-emerald-600/50 bg-emerald-600/5 text-emerald-800 dark:text-emerald-300">
				<CircleCheckIcon class="size-4" aria-hidden="true" />
				<AlertTitle>
					{copy.verifyOk}
					<span class="ml-1 font-normal opacity-70">{formatLabels[report.format]}</span>
				</AlertTitle>
			</Alert>
		{/if}

		<ul class="flex flex-col gap-2">
			{#each report.checks as entry, index (index)}
				<li
				class="flex items-start gap-3 rounded-xl border px-4 py-3 {styles[entry.status]}"
					data-check={entry.id}
					data-status={entry.status}
				>
					{#if entry.status === 'info'}
						<InfoIcon class="mt-1 size-4 shrink-0" aria-hidden="true" />
					{:else if entry.status === 'warn'}
						<TriangleAlertIcon class="mt-1 size-4 shrink-0" aria-hidden="true" />
					{:else if entry.status === 'fail'}
						<CircleXIcon class="mt-1 size-4 shrink-0" aria-hidden="true" />
					{:else}
						<CircleCheckIcon class="mt-1 size-4 shrink-0" aria-hidden="true" />
					{/if}
					<div class="min-w-0">
						<p class="font-medium">{copy.checkLabels[entry.id] ?? entry.id}</p>
						<p class="break-words text-sm opacity-80">{entry.detail}</p>
					</div>
				</li>
			{/each}
		</ul>

		{#if report.signer}
			<dl class="bg-muted/60 tnum grid grid-cols-[auto_1fr] gap-x-3 gap-y-1 rounded-lg border px-3 py-2.5 text-sm">
				<dt class="text-muted-foreground col-span-2 text-xs font-medium uppercase tracking-wide">
					{copy.verifySigner}
				</dt>
				<dt class="sr-only">subject</dt>
				<dd class="col-span-2 break-words font-medium">{report.signer.subject}</dd>
				<dt class="sr-only">issuer</dt>
				<dd class="text-muted-foreground col-span-2 break-words">{report.signer.issuer}</dd>
				<dt class="sr-only">serial</dt>
				<dd class="text-muted-foreground font-mono text-xs break-all">{report.signer.serial}</dd>
				<dt class="sr-only">validity</dt>
				<dd class="text-muted-foreground font-mono text-xs">
				{report.signer.notBefore} <ArrowRightIcon class="inline size-3 align-[-0.125em]" aria-hidden="true" /> {report.signer.notAfter}
				</dd>
			</dl>
		{:else}
			<p class="text-muted-foreground text-sm">{copy.verifyNoSigner}</p>
		{/if}

		{#if report.signingTime}
			<p class="text-muted-foreground tnum text-sm">
				{copy.verifySigningTime}: {report.signingTime}
			</p>
		{/if}
	{/if}
</section>
