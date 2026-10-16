import { afterEach, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/svelte';
import SiteAccess from './SiteAccess.svelte';
afterEach(()=>{cleanup();vi.unstubAllGlobals();});
function mount() {
 const events: unknown[]=[];
 let disabledOrigins:string[]=[];
 vi.stubGlobal('location',{protocol:'chrome-extension:'});
 vi.stubGlobal('chrome',{runtime:{},permissions:{request:async (value:unknown)=>{events.push(value);return true;}}});
 const send=async (message:{type:string;origin?:string;enabled?:boolean})=>{
  if(message.type==='sites:list')return {disabledOrigins,suggestedOrigin:'https://example.com'};
  events.push(message);disabledOrigins=message.enabled?[]:[message.origin!];return true;
 };
 render(SiteAccess,{props:{lang:'en',send}});
 return events;
}
it('shows all-sites default and disables then restores a site without changing Chrome permissions',async()=>{
 const events=mount();
 await screen.findByText('No disabled sites.');
 await waitFor(()=>expect((screen.getByLabelText('Website origin') as HTMLInputElement).value).toBe('https://example.com'));
 await fireEvent.click(screen.getByRole('button',{name:'Disable site'}));
 await screen.findByText('https://example.com');
 await fireEvent.click(screen.getByRole('button',{name:'Re-enable'}));
 await screen.findByText('No disabled sites.');
 expect(events).toEqual([{type:'sites:set',origin:'https://example.com',enabled:false},{type:'sites:set',origin:'https://example.com',enabled:true}]);
});
it('grants TSA network permission without changing site exceptions',async()=>{
 const events=mount();
 await fireEvent.input(screen.getByLabelText('TSA URL'),{target:{value:'https://tsa.example.com/stamp'}});
 await fireEvent.click(screen.getByRole('button',{name:'Authorize TSA'}));
 await waitFor(()=>expect(events).toEqual([{origins:['https://tsa.example.com/*']} ]));
});
it('translates the origin validation error in the Spanish panel',async()=>{
 vi.stubGlobal('location',{protocol:'chrome-extension:'});
 vi.stubGlobal('chrome',{runtime:{},permissions:{request:async()=>true}});
 render(SiteAccess,{props:{lang:'es',send:async(message:{type:string})=>{if(message.type==='sites:list')return {disabledOrigins:[],suggestedOrigin:'https://example.com'};return true;}}});
 await fireEvent.input(screen.getByLabelText('Origen del sitio'),{target:{value:'ftp://example.com'}});
 await fireEvent.click(screen.getByRole('button',{name:'Deshabilitar sitio'}));
 expect((await screen.findByRole('alert')).textContent).toBe('Usa un origen HTTP o HTTPS.');
});
