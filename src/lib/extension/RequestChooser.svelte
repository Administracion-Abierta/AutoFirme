<script lang="ts">
 import { onMount } from 'svelte';
 import CertPicker from '#lib/components/CertPicker.svelte';
 import { Button } from '#lib/components/ui/button/index.js';
 import { browserVault, type Vault } from '#lib/certstore';
 import { copy, type Lang } from '#lib/i18n';
 import type { SignerCert } from '#lib/sign';
 import { applyTheme, readTheme } from '#lib/theme';
 import { rpc, type Rpc, type ExtensionRequest } from './runtime';
 import { certificateProblem, confirmRequest, RequestValidationError } from './sign-request';
 import { extensionCopy } from './copy';
 let { id, vault = browserVault(), send = rpc }: { id:string; vault?:Vault; send?:Rpc } = $props();
 let lang = $state<Lang>('es');
 const t = $derived(extensionCopy[lang]);
 let request = $state<ExtensionRequest | null>(null);
 let cert = $state<SignerCert | null>(null);
 let chain = $state<readonly SignerCert[]>([]);
 let phase = $state<'loading' | 'waiting' | 'signing' | 'done' | 'cancelled' | 'continued' | 'failed'>('loading');
 let error = $state('');
 let eligibilityTime = $state(Date.now());
 const problem = $derived(cert ? certificateProblem(cert, eligibilityTime) : null);
 const waiting = $derived(phase === 'waiting');
 onMount(() => {
  const cleanup = applyTheme(readTheme());
  void send({type:'request:get',id}).then((value) => { request = value as ExtensionRequest; phase='waiting'; }).catch((reason) => { error=String(reason instanceof Error ? reason.message : reason); phase='failed'; });
  return cleanup;
 });
 async function confirm() {
  if (!waiting || !cert || !request || problem) return;
  phase='signing'; error='';
  try { await confirmRequest(request, cert, chain, send); phase='done'; }
  catch (reason) {
   if (reason instanceof RequestValidationError) {
    eligibilityTime=Date.now();
    error=reason.certificateProblem ? t[reason.certificateProblem] : reason.message;
    phase='waiting';
   } else { error=reason instanceof Error ? reason.message : String(reason); phase='failed'; }
  }
 }
 async function finish(action: 'cancel' | 'fallback') {
  if (!waiting) return;
  phase='signing';
  try { await send({type:`request:${action}`,id}); phase=action==='cancel' ? 'cancelled' : 'continued'; }
  catch (reason) { error=reason instanceof Error ? reason.message : String(reason); phase='failed'; }
 }
 async function openApp() {
  try { await send({type:'app:open',tab:true}); }
  catch (reason) { error=reason instanceof Error ? reason.message : String(reason); }
 }
</script>

<main class="mx-auto flex min-h-dvh w-full max-w-lg flex-col gap-5 p-4 sm:p-6">
 <header class="flex flex-wrap items-center justify-between gap-2 border-b pb-3">
  <span class="font-display text-xl font-semibold">AutoFirme</span>
  <div class="flex gap-1" role="group" aria-label={copy[lang].langLabel}>
   <Button size="sm" variant={lang==='es'?'default':'ghost'} aria-pressed={lang==='es'} onclick={()=>lang='es'}>ES</Button>
   <Button size="sm" variant={lang==='en'?'default':'ghost'} aria-pressed={lang==='en'} onclick={()=>lang='en'}>EN</Button>
  </div>
 </header>
 <div>
  <h1 class="font-display text-2xl font-semibold">{request?.operation==='sign' ? t.sign : t.request}</h1>
  <p class="text-muted-foreground mt-2 text-sm">{t.requestedBy}</p>
  <p id="request-origin" class="mt-1 break-all font-semibold">{request?.origin ?? '…'}</p>
 </div>
 {#if request}
  <section class="bg-muted/50 grid gap-2 rounded-xl border p-3 text-sm">
   <p>{request.operation==='sign' ? `${request.format} · SHA-256 · ${request.mode}` : t.select}</p>
   {#if request.operation==='sign'}
    <p class="text-muted-foreground">{t.digest}</p>
    <code id="request-digest" class="break-all text-xs">{request.digest ?? '—'}</code>
   {:else}<p class="text-muted-foreground">{t.noDigest}</p>{/if}
  </section>
  {#if waiting}
   <p class="text-muted-foreground text-sm">{t.consent}</p>
   <CertPicker copy={copy[lang]} {cert} {vault} compact onpick={(picked, certificates)=>{cert=picked;chain=certificates;eligibilityTime=Date.now();error='';}} />
   {#if problem}<p role="alert" class="text-destructive text-sm">{t[problem]}</p>{/if}
  {/if}
 {/if}
 {#if error}<p id="request-error" role="alert" class="text-destructive break-words text-sm">{error}</p>{/if}
 {#if phase!=='waiting'}<p role="status" class="text-sm">{phase==='loading' ? t.loading : phase==='signing' ? t.signing : phase==='done' ? t.done : phase==='cancelled' ? t.cancelled : phase==='continued' ? t.continued : t.failed}</p>{/if}
 <div class="mt-auto grid gap-2 pt-3">
  <Button id="request-confirm" class="h-auto min-h-10 whitespace-normal" disabled={!waiting || !cert || !!problem} onclick={confirm}>{request?.operation==='selectCertificate' ? t.confirmSelect : t.confirm}</Button>
  <div class="grid grid-cols-2 gap-2">
   <Button id="request-cancel" variant="outline" disabled={!waiting} onclick={()=>finish('cancel')}>{t.cancel}</Button>
   <Button id="request-fallback" variant="outline" class="h-auto min-h-10 whitespace-normal" disabled={!waiting} onclick={()=>finish('fallback')}>{t.fallback}</Button>
  </div>
  <Button id="request-open-app" variant="ghost" class="h-auto min-h-10 whitespace-normal" onclick={openApp}>{t.fullTab}</Button>
 </div>
</main>
