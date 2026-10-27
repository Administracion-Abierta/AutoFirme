import { afterEach, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/svelte';
import SiteAccess from './SiteAccess.svelte';
afterEach(()=>{cleanup();vi.unstubAllGlobals();});
async function clickReady(name:string) {
 await waitFor(()=>expect((screen.getByRole('button',{name}) as HTMLButtonElement).disabled).toBe(false));
 await fireEvent.click(screen.getByRole('button',{name}));
}
function mount(allSites=true) {
 const events: unknown[]=[];
 let disabledOrigins:string[]=[];
 let grantedOrigins:string[]=[];
 vi.stubGlobal('location',{protocol:'chrome-extension:'});
 vi.stubGlobal('chrome',{runtime:{},permissions:{
  request:async (value:{origins:string[]})=>{events.push(value);if(value.origins.includes('https://example.com/*'))grantedOrigins=['https://example.com'];return true;},
  remove:async (value:{origins:string[]})=>{events.push(value);if(value.origins.includes('https://example.com/*'))grantedOrigins=[];return true;},
 }});
 const send=async (message:{type:string;origin?:string;enabled?:boolean})=>{
  if(message.type==='sites:list')return {disabledOrigins,suggestedOrigin:'https://example.com',grantedOrigins,allSites};
  events.push(message);disabledOrigins=message.enabled?[]:[message.origin!];return true;
 };
 render(SiteAccess,{props:{lang:'en',send}});
 return events;
}
it('intercepts every site by default and disables an origin without any Chrome prompt',async()=>{
 const events=mount();
 await screen.findByText(/intercepts every HTTP and HTTPS website/);
 await waitFor(()=>expect((screen.getByLabelText('Website origin') as HTMLInputElement).value).toBe('https://example.com'));
 await clickReady('Disable site');
 await screen.findByText('Disabled sites');
 expect(events).toEqual([{type:'sites:set',origin:'https://example.com',enabled:false}]);
 expect(screen.queryByText('Grant access')).toBeNull();
});
it('re-enables a disabled site with a restore button and no new host grant',async()=>{
 const events=mount();
 await waitFor(()=>expect((screen.getByLabelText('Website origin') as HTMLInputElement).value).toBe('https://example.com'));
 await clickReady('Disable site');
 await screen.findByText('Disabled sites');
 await clickReady('Re-enable');
 await waitFor(()=>expect(screen.queryByText('Disabled sites')).toBeNull());
 expect(events).toEqual([
  {type:'sites:set',origin:'https://example.com',enabled:false},
  {type:'sites:set',origin:'https://example.com',enabled:true},
 ]);
});
it('offers a per-origin host grant when Chrome lost the all-sites wildcard',async()=>{
 const events=mount(false);
 await screen.findByText(/intercepts every HTTP and HTTPS website/);
 await fireEvent.input(screen.getByLabelText('Origin to grant'),{target:{value:'https://example.com'}});
 await clickReady('Grant access');
 await waitFor(()=>expect(events).toEqual([{origins:['https://example.com/*']}]));
});
it('grants TSA network permission without changing site access',async()=>{
 const events=mount();
 await fireEvent.input(screen.getByLabelText('TSA URL'),{target:{value:'https://tsa.example.com/stamp'}});
 await clickReady('Authorize TSA');
 await waitFor(()=>expect(events).toEqual([{origins:['https://tsa.example.com/*']} ]));
});
it('translates the origin validation error in the Spanish panel',async()=>{
 vi.stubGlobal('location',{protocol:'chrome-extension:'});
 vi.stubGlobal('chrome',{runtime:{},permissions:{request:async()=>true}});
 render(SiteAccess,{props:{lang:'es',send:async(message:{type:string})=>{if(message.type==='sites:list')return {disabledOrigins:[],suggestedOrigin:'https://example.com',grantedOrigins:[],allSites:true};return true;}}});
 await fireEvent.input(screen.getByLabelText('Origen del sitio'),{target:{value:'ftp://example.com'}});
 await fireEvent.click(screen.getByRole('button',{name:'Deshabilitar sitio'}));
 expect((await screen.findByRole('alert')).textContent).toBe('Usa un origen HTTP o HTTPS.');
});
