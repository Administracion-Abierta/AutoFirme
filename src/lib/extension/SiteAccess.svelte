<script lang="ts">
 import { onMount } from 'svelte';
 import { Button } from '#lib/components/ui/button/index.js';
 import { Input } from '#lib/components/ui/input/index.js';
 import type { Lang } from '#lib/i18n';
 import { exactOrigin, requestOriginPermission, rpc, type Rpc } from './runtime';
 import { extensionCopy, errorCopy } from './copy';
 let {lang='es',send=rpc}: {lang?:Lang;send?:Rpc} = $props();
 const t=$derived(extensionCopy[lang]);
 let disabled=$state<string[]>([]);
 let granted=$state<string[]>([]);
 let allSites=$state(false);
 let interceptionError=$state('');
 let origin=$state('');
 let grantOrigin=$state('');
 let tsa=$state('');
 let error=$state('');
 let status=$state('');
 let busy=$state(false);
 onMount(()=>{void refresh().catch(report);});
 function report(reason:unknown) {error=errorCopy(lang,reason instanceof Error ? reason.message : String(reason));}
 async function refresh(){
  const result=await send({type:'sites:list'}) as {disabledOrigins:string[];suggestedOrigin:string|null;grantedOrigins:string[];allSites:boolean;scriptsError?:string};
  disabled=result.disabledOrigins;granted=result.grantedOrigins;allSites=result.allSites;interceptionError=result.scriptsError??'';
  if(!origin)origin=result.suggestedOrigin??'';
 }
 async function setEnabled(value:string,enabled:boolean){
  if(busy)return;
  busy=true;error='';status='';
  try {
   await send({type:'sites:set',origin:exactOrigin(value),enabled});
   await refresh();
  }catch(reason){report(reason);}finally{busy=false;}
 }
 async function grant(value:string){
  if(busy||!value)return;
  error='';status='';
  try {
   busy=true;
   if(!await requestOriginPermission(value))throw new Error(t.permissionDenied);
   status=t.granted;
  }catch(reason){report(reason);}finally{busy=false;}
 }
 async function authorizeTsa(){
  if(busy)return;
  error='';status='';
  try {
   const permission=requestOriginPermission(tsa);
   busy=true;
   if(!await permission)throw new Error(t.permissionDenied);
   status=t.granted;
  }catch(reason){report(reason);}finally{busy=false;}
 }
</script>
<div id="site-access-panel" class="grid min-w-0 gap-5">
 <section class="grid gap-3">
  <p class="text-muted-foreground text-sm">{t.sitesHint}</p>
  <label class="grid min-w-0 gap-1 text-sm" for="site-origin">{t.origin}<Input id="site-origin" type="url" placeholder="https://example.com" bind:value={origin}/></label>
  <div class="flex flex-wrap gap-2">
   <Button id="disable-site" variant="outline" disabled={busy || !origin} onclick={()=>setEnabled(origin,false)}>{t.disableSite}</Button>
  </div>
  {#if disabled.length}
   <p class="text-sm font-medium">{t.disabledSites}</p>
   <ul class="grid gap-2">
    {#each disabled as value (value)}
     <li class="flex min-w-0 flex-wrap items-center justify-between gap-2 rounded-lg border p-2 text-sm"><span class="break-all">{value}</span><Button variant="outline" size="sm" disabled={busy} onclick={()=>setEnabled(value,true)}>{t.restore}</Button></li>
    {/each}
   </ul>
  {/if}
 </section>
 <section class="grid gap-3 border-t pt-4">
  <h3 class="font-medium">{t.tsa}</h3>
  <p class="text-muted-foreground text-sm">{t.tsaHint}</p>
  <label class="grid gap-1 text-sm" for="tsa-permission-url">{t.tsaUrl}<Input id="tsa-permission-url" type="url" placeholder="https://tsa.example.com/" bind:value={tsa}/></label>
  <Button variant="outline" disabled={busy || !tsa} onclick={authorizeTsa}>{t.grantTsa}</Button>
  {#if !allSites}
   <p class="text-muted-foreground text-sm">{t.reenableHint}</p>
   <label class="grid min-w-0 gap-1 text-sm" for="grant-origin">{t.grantOrigin}<Input id="grant-origin" type="url" placeholder="https://example.com" bind:value={grantOrigin}/></label>
   <Button id="grant-site" variant="outline" disabled={busy || !grantOrigin} onclick={()=>grant(grantOrigin)}>{t.grantSite}</Button>
  {/if}
 </section>
 {#if interceptionError}<p role="alert" class="text-destructive break-words text-sm">{t.interceptionFailed} <span class="text-muted-foreground">({interceptionError})</span></p>{/if}
 {#if error}<p role="alert" class="text-destructive break-words text-sm">{error}</p>{/if}
 {#if status}<p role="status" class="text-sm">{status}</p>{/if}
</div>
