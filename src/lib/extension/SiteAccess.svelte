<script lang="ts">
 import { onMount } from 'svelte';
 import { Button } from '#lib/components/ui/button/index.js';
 import { Input } from '#lib/components/ui/input/index.js';
 import type { Lang } from '#lib/i18n';
 import { exactOrigin, requestOriginPermission, rpc, type Rpc } from './runtime';
 import { extensionCopy } from './copy';
 let {lang='es',send=rpc}: {lang?:Lang;send?:Rpc} = $props();
 const t=$derived(extensionCopy[lang]);
 let origins=$state<string[]>([]);
 let origin=$state('');
 let tsa=$state('');
 let error=$state('');
 let status=$state('');
 let busy=$state(false);
 onMount(()=>{void refresh().catch(report);});
 function report(reason:unknown) {error=reason instanceof Error ? reason.message : String(reason);}
 async function refresh(){
  const result=await send({type:'sites:list'}) as {disabledOrigins:string[];suggestedOrigin:string|null};
  origins=result.disabledOrigins;
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
  <label class="grid gap-1 text-sm" for="site-origin">{t.origin}<Input id="site-origin" type="url" placeholder="https://example.com" bind:value={origin}/></label>
  <Button disabled={busy || !origin} onclick={()=>setEnabled(origin,false)}>{t.disableSite}</Button>
  <ul class="grid gap-2">
   {#each origins as value (value)}
    <li class="flex min-w-0 flex-wrap items-center justify-between gap-2 rounded-lg border p-2 text-sm"><span class="break-all">{value}</span><Button variant="outline" size="sm" disabled={busy} onclick={()=>setEnabled(value,true)}>{t.restore}</Button></li>
   {/each}
  </ul>
  {#if origins.length===0}<p class="text-muted-foreground text-sm">{t.noSites}</p>{/if}
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
