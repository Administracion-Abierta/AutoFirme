<script lang="ts">
 import { onMount } from 'svelte';
 import { Button } from '#lib/components/ui/button/index.js';
 import { Input } from '#lib/components/ui/input/index.js';
 import type { Lang } from '#lib/i18n';
 import { exactOrigin, removeAllSitesPermission, removeOriginPermission, requestAllSitesPermission, requestOriginPermission, rpc, type Rpc } from './runtime';
 import { extensionCopy } from './copy';
 let {lang='es',send=rpc}: {lang?:Lang;send?:Rpc} = $props();
 const t=$derived(extensionCopy[lang]);
 let disabled=$state<string[]>([]);
 let granted=$state<string[]>([]);
 let allSites=$state(false);
 let origin=$state('');
 let tsa=$state('');
 let error=$state('');
 let status=$state('');
 let busy=$state(false);
 onMount(()=>{void refresh().catch(report);});
 function report(reason:unknown) {error=reason instanceof Error ? reason.message : String(reason);}
 async function refresh(){
  const result=await send({type:'sites:list'}) as {disabledOrigins:string[];suggestedOrigin:string|null;grantedOrigins:string[];allSites:boolean};
  disabled=result.disabledOrigins;granted=result.grantedOrigins;allSites=result.allSites;
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
 async function authorize(){
  if(busy||!origin)return;
  error='';status='';
  try {
   busy=true;
   if(!await requestOriginPermission(origin))throw new Error(t.permissionDenied);
   status=t.granted;
   await refresh();
  }catch(reason){report(reason);}finally{busy=false;}
 }
 async function revoke(value:string){
  if(busy)return;
  busy=true;error='';status='';
  try {
   await removeOriginPermission(value);
   await refresh();
  }catch(reason){report(reason);}finally{busy=false;}
 }
 async function toggleAllSites(){
  if(busy)return;
  error='';status='';
  try {
   busy=true;
   if(allSites) await removeAllSitesPermission();
   else if(!await requestAllSitesPermission())throw new Error(t.permissionDenied);
   await refresh();
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
   <Button id="authorize-site" disabled={busy || !origin} onclick={authorize}>{t.authorizeSite}</Button>
   {#if allSites}<Button id="disable-site" variant="outline" disabled={busy || !origin} onclick={()=>setEnabled(origin,false)}>{t.disableSite}</Button>{/if}
  </div>
  {#if granted.length && !allSites}
   <p class="text-sm font-medium">{t.grantedSites}</p>
   <ul class="grid gap-2">
    {#each granted as value (value)}
     <li class="flex min-w-0 flex-wrap items-center justify-between gap-2 rounded-lg border p-2 text-sm"><span class="break-all">{value}</span><Button variant="outline" size="sm" disabled={busy} onclick={()=>revoke(value)}>{t.revokeSite}</Button></li>
    {/each}
   </ul>
  {/if}
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
  <h3 class="font-medium">{t.allSites}</h3>
  <p class="text-muted-foreground text-sm">{t.allSitesHint}</p>
  <Button id="all-sites-toggle" variant={allSites ? 'outline' : 'default'} disabled={busy} aria-pressed={allSites} onclick={toggleAllSites}>{allSites ? t.disableAllSites : t.enableAllSites}</Button>
 </section>
 <section class="grid gap-3 border-t pt-4">
  <h3 class="font-medium">{t.tsa}</h3>
  <p class="text-muted-foreground text-sm">{t.tsaHint}</p>
  <label class="grid gap-1 text-sm" for="tsa-permission-url">{t.tsaUrl}<Input id="tsa-permission-url" type="url" placeholder="https://tsa.example.com/" bind:value={tsa}/></label>
  <Button variant="outline" disabled={busy || !tsa} onclick={authorizeTsa}>{t.grantTsa}</Button>
 </section>
 {#if error}<p role="alert" class="text-destructive break-words text-sm">{error}</p>{/if}
 {#if status}<p role="status" class="text-sm">{status}</p>{/if}
</div>
