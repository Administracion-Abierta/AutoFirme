<script lang="ts">
 import { onMount } from 'svelte';
 import CopyIcon from '@lucide/svelte/icons/copy';
 import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '#lib/components/ui/tooltip/index.js';
 import CertPicker from '#lib/components/CertPicker.svelte';
 import { Button } from '#lib/components/ui/button/index.js';
 import { browserVault, type Vault } from '#lib/certstore';
 import { copy, type Lang } from '#lib/i18n';
 import type { SignerCert } from '#lib/sign';
 import { applyTheme, readTheme } from '#lib/theme';
 import { rpc, type Rpc, type ExtensionRequest } from './runtime';
 import { extensionCopy, errorCopy } from './copy';
 import { certificateProblem } from './certificate';
 import { confirmRequest, RequestValidationError } from './sign-request';
 let { id, vault = browserVault(), send = rpc }: { id:string; vault?:Vault; send?:Rpc } = $props();
 let lang = $state<Lang>('es');
 const t = $derived(extensionCopy[lang]);
 let request = $state<ExtensionRequest | null>(null);
 let cert = $state<SignerCert | null>(null);
 let chain = $state<readonly SignerCert[]>([]);
 let phase = $state<'loading' | 'waiting' | 'signing' | 'done' | 'cancelled' | 'continued' | 'failed'>('loading');
 let error = $state('');
 let copied = $state(false);
 let digestHintOpen = $state(false);
 let content = $state<HTMLElement | null>(null);
 let eligibilityTime = $state(Date.now());
 const problem = $derived(cert ? certificateProblem(cert, eligibilityTime) : null);
 const hash = $derived((request?.algorithm ?? 'SHA256withRSA').replace('withRSA', '').replace(/^SHA(\d+)/, 'SHA-$1'));
 const waiting = $derived(phase === 'waiting');
 onMount(() => {
  const cleanup = applyTheme(readTheme());
  void send({type:'request:get',id}).then((value) => { request = value as ExtensionRequest; phase='waiting'; }).catch((reason) => { error=String(reason instanceof Error ? reason.message : reason); phase='failed'; });
  return cleanup;
 });
 // Measure natural content height, not the viewport, to avoid resize feedback loops.
 $effect(() => {
  if (!content || !request || typeof ResizeObserver === 'undefined') return;
  const element=content;
  let timer:ReturnType<typeof setTimeout>;
  let lastHeight=0;
  const frame=Math.max(0,window.outerHeight-window.innerHeight);
  function schedule() {
   clearTimeout(timer);
   timer=setTimeout(() => {
    const limit=Math.min(1200,window.screen.availHeight || 900);
    const height=Math.max(320,Math.min(limit,Math.ceil(element.getBoundingClientRect().height+frame+2)));
    if(height===lastHeight)return;
    lastHeight=height;
    // Resizing is cosmetic: failure must never affect signing or fallback.
    void send({type:'request:resize',id,height}).catch(()=>{});
   },80);
  }
  const observer=new ResizeObserver(schedule);
  observer.observe(element);
  // Native disclosure and PIN/import updates must also resize without a paint.
  const mutations=new MutationObserver(schedule);
  mutations.observe(element,{subtree:true,childList:true,characterData:true,attributes:true,attributeFilter:['open','class','hidden']});
  window.addEventListener('resize',schedule);
  schedule();
  return () => {observer.disconnect();mutations.disconnect();clearTimeout(timer);window.removeEventListener('resize',schedule);};
 });
 async function copyDigest() {
  if (!request?.digest) return;
  copied=false;
  try {await navigator.clipboard.writeText(request.digest);copied=true;}
  catch {error=t.copyFailed;}
 }
 // The action buttons disable themselves as the phase leaves 'waiting', which would drop the
 // pressed button and send focus to <body>; park it on the always-enabled open-app button first.
 function keepFocus() { document.getElementById('request-open-app')?.focus(); }
 async function confirm() {
  if (!waiting || !cert || !request || problem) return;
  keepFocus();
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
  keepFocus();
  phase='signing';
  try { await send({type:`request:${action}`,id}); phase=action==='cancel' ? 'cancelled' : 'continued'; }
  catch (reason) { error=reason instanceof Error ? reason.message : String(reason); phase='failed'; }
 }
 async function openApp() {
  try { await send({type:'app:open',tab:true}); }
  catch (reason) { error=reason instanceof Error ? reason.message : String(reason); }
 }
</script>

<main bind:this={content} id="request-chooser" class="mx-auto flex w-full max-w-lg flex-col gap-3 p-3 sm:p-4">
 <header class="flex items-center justify-between gap-2 border-b pb-2">
  <div class="flex min-w-0 flex-wrap items-baseline gap-x-2">
   <span class="font-display text-lg font-semibold">AutoFirme</span>
   <h1 class="text-muted-foreground text-xs font-medium">{request?.operation==='sign' ? t.sign : t.request}</h1>
  </div>
  <div class="flex shrink-0 gap-1" role="group" aria-label={copy[lang].langLabel}>
   <Button size="sm" variant={lang==='es'?'default':'ghost'} aria-pressed={lang==='es'} onclick={()=>lang='es'}>ES</Button>
   <Button size="sm" variant={lang==='en'?'default':'ghost'} aria-pressed={lang==='en'} onclick={()=>lang='en'}>EN</Button>
  </div>
 </header>
 <section id="request-site" class="flex min-w-0 items-center justify-between gap-2">
  <div class="min-w-0">
   <p class="text-muted-foreground text-xs">{t.requestedBy}</p>
   <p id="request-origin" class="break-all text-sm font-semibold">{request ? (request.bridge ? t.mcpOrigin : request.origin) : '…'}</p>
  </div>
  {#if request?.operation==='sign' && request.digest}
   <TooltipProvider><Tooltip bind:open={digestHintOpen}><TooltipTrigger>
    {#snippet child({props})}
     <Button {...props} id="request-copy-digest" size="icon-sm" variant="ghost" aria-label={t.copyDigest} onclick={copyDigest}><CopyIcon class="size-3.5"/></Button>
    {/snippet}
   </TooltipTrigger>{#if digestHintOpen}<TooltipContent>{copied ? t.digestCopied : t.copyDigest}</TooltipContent>{/if}</Tooltip></TooltipProvider>
  {/if}
 </section>
 {#if copied}<p role="status" class="sr-only">{t.digestCopied}</p>{/if}
 {#if request}
  <details id="request-details" class="text-xs">
   <summary class="text-muted-foreground cursor-pointer py-1">{t.requestDetails}</summary>
   <div class="bg-muted/50 mt-1 grid gap-2 rounded-lg p-2">
    <p>{request.operation==='sign' ? `${request.format}${request.serverUrl ? ' · ' + t.server : ''} · ${hash} · ${request.mode}` : t.select}</p>
    {#if request.operation==='sign'}
     <p class="text-muted-foreground">{t.digest}</p>
     <code id="request-digest" class="break-all">{request.digest ?? '—'}</code>
    {:else}<p class="text-muted-foreground">{t.noDigest}</p>{/if}
    <p class="text-muted-foreground">{t.consent}</p>
   </div>
  </details>
  {#if waiting}
   <CertPicker copy={copy[lang]} {cert} {vault} compact detailsLabel={t.certDetails} initialId={request.certId} onpick={(picked, certificates, identityId)=>{
    if (request?.bridge && request.certId && identityId !== request.certId) { cert=null; chain=[]; error=t.mcpWrongCertificate; return; }
    cert=picked;chain=certificates;eligibilityTime=Date.now();error='';
   }} />
   {#if problem}<p role="alert" class="text-destructive text-sm">{t[problem]}</p>{/if}
  {/if}
 {/if}
 {#if error}<p id="request-error" role="alert" class="text-destructive break-words text-sm">{errorCopy(lang,error)}</p>{/if}
 {#if phase!=='waiting'}<p role="status" class="text-sm">{phase==='loading' ? t.loading : phase==='signing' ? t.signing : phase==='done' ? t.done : phase==='cancelled' ? t.cancelled : phase==='continued' ? t.continued : t.failed}</p>{/if}
 <footer class="grid gap-2 border-t pt-3">
  <Button id="request-confirm" class="h-10 w-full whitespace-normal text-center leading-tight" disabled={!waiting || !cert || !!problem} onclick={confirm}>{request?.operation==='selectCertificate' ? t.confirmSelect : t.confirm}</Button>
  <div class={request?.bridge ? 'grid gap-2' : 'grid grid-cols-2 gap-2'}>
   <Button id="request-cancel" variant="outline" class="h-10 w-full min-w-0 whitespace-normal text-center leading-tight" disabled={!waiting} onclick={()=>finish('cancel')}>{t.cancel}</Button>
   {#if !request?.bridge}
    <Button id="request-fallback" variant="outline" class="h-10 w-full min-w-0 whitespace-normal text-center leading-tight" disabled={!waiting} onclick={()=>finish('fallback')}>{t.fallback}</Button>
   {/if}
  </div>
  <Button id="request-open-app" variant="ghost" class="h-8 w-full whitespace-normal text-center text-xs" onclick={openApp}>{t.fullTab}</Button>
 </footer>
</main>
