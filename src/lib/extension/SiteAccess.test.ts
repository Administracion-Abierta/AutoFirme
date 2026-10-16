import { afterEach, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/svelte';
import SiteAccess from './SiteAccess.svelte';
afterEach(()=>{cleanup();vi.unstubAllGlobals();});
async function clickReady(name:string) {
 await waitFor(()=>expect((screen.getByRole('button',{name}) as HTMLButtonElement).disabled).toBe(false));
 await fireEvent.click(screen.getByRole('button',{name}));
}
function mount() {
 const events: unknown[]=[];
 let disabledOrigins:string[]=[];
 let grantedOrigins:string[]=[];
 let allSites=false;
 vi.stubGlobal('location',{protocol:'chrome-extension:'});
 vi.stubGlobal('chrome',{runtime:{},permissions:{
  request:async (value:{origins:string[]})=>{events.push(value);if(value.origins.includes('https://example.com/*'))grantedOrigins=['https://example.com'];if(value.origins.includes('http://*/*'))allSites=true;return true;},
  remove:async (value:{origins:string[]})=>{events.push(value);if(value.origins.includes('https://example.com/*'))grantedOrigins=[];if(value.origins.includes('http://*/*'))allSites=false;return true;},
 }});
 const send=async (message:{type:string;origin?:string;enabled?:boolean})=>{
  if(message.type==='sites:list')return {disabledOrigins,suggestedOrigin:'https://example.com',grantedOrigins,allSites};
  events.push(message);disabledOrigins=message.enabled?[]:[message.origin!];return true;
 };
 render(SiteAccess,{props:{lang:'en',send}});
 return events;
}
it('starts with no interception, authorizes one site then revokes its access',async()=>{
 const events=mount();
 await screen.findByText(/does not intercept any website/);
 expect(screen.queryByText('Remove access')).toBeNull();
 await waitFor(()=>expect((screen.getByLabelText('Website origin') as HTMLInputElement).value).toBe('https://example.com'));
 await clickReady('Authorize site');
 await screen.findByText('Authorized sites');
 expect(events).toEqual([{origins:['https://example.com/*']}]);
 await clickReady('Remove access');
 await screen.findByText(/does not intercept any website/);
 expect(events).toEqual([{origins:['https://example.com/*']},{origins:['https://example.com/*']}]);
});
it('toggles the optional all-sites permission and offers per-site disable while active',async()=>{
 const events=mount();
 await screen.findByText(/does not intercept any website/);
 await clickReady('Enable all websites');
 expect(events).toEqual([{origins:['http://*/*','https://*/*']}]);
 await clickReady('Disable site');
 await waitFor(()=>expect(events.length).toBe(2));
 await screen.findByText('https://example.com');
 await clickReady('Re-enable');
 await waitFor(()=>expect(events.length).toBe(3));
 await clickReady('Disable all websites');
 expect(events.at(-1)).toEqual({origins:['http://*/*','https://*/*']});
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
 render(SiteAccess,{props:{lang:'es',send:async(message:{type:string})=>{if(message.type==='sites:list')return {disabledOrigins:[],suggestedOrigin:'https://example.com',grantedOrigins:[],allSites:false};return true;}}});
 await fireEvent.input(screen.getByLabelText('Origen del sitio'),{target:{value:'ftp://example.com'}});
 await fireEvent.click(screen.getByRole('button',{name:'Autorizar sitio'}));
 expect((await screen.findByRole('alert')).textContent).toBe('Usa un origen HTTP o HTTPS.');
});
