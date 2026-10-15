<script lang="ts">
 import App from '#lib/App.svelte';
 import { Button } from '#lib/components/ui/button/index.js';
 import { Dialog,DialogContent,DialogHeader,DialogTitle,DialogDescription } from '#lib/components/ui/dialog/index.js';
 import type { Lang } from '#lib/i18n';
 import SiteAccess from './SiteAccess.svelte';
 import { rpc } from './runtime';
 import { extensionCopy } from './copy';
 let open=$state(false);
 let settingsLang=$state<Lang>('es');
 let error=$state('');
 async function openTab(){try{await rpc({type:'app:open',tab:true});}catch(reason){error=reason instanceof Error?reason.message:String(reason);}}
</script>
{#snippet controls(lang:Lang)}
 <Button id="site-access-open" variant="outline" size="sm" class="h-auto min-h-8 whitespace-normal" onclick={()=>{settingsLang=lang;open=true;}}>{extensionCopy[lang].access}</Button>
 <Button id="extension-full-tab" variant="outline" size="sm" class="h-auto min-h-8 whitespace-normal" onclick={openTab}>{extensionCopy[lang].fullTab}</Button>
 {#if error}<p role="alert" class="text-destructive text-sm">{error}</p>{/if}
{/snippet}
<App extensionControls={controls}/>
<Dialog bind:open>
 <DialogContent class="max-h-[90dvh] overflow-y-auto sm:max-w-lg">
  <DialogHeader><DialogTitle>{extensionCopy[settingsLang].access}</DialogTitle><DialogDescription>AutoFirme</DialogDescription></DialogHeader>
  <SiteAccess lang={settingsLang}/>
 </DialogContent>
</Dialog>
