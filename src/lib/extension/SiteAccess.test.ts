import { afterEach, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/svelte';
import SiteAccess from './SiteAccess.svelte';
afterEach(()=>{cleanup();vi.unstubAllGlobals();});
function mount(granted=true) {
 const events: unknown[]=[];
 vi.stubGlobal('location',{protocol:'chrome-extension:'});
 vi.stubGlobal('chrome',{runtime:{},permissions:{request:async (value:unknown)=>{events.push(value);return granted;},remove:async ()=>true}});
 const send=async (message:{type:string})=>{if(message.type==='sites:list')return {origins:[],suggestedOrigin:'https://example.com'};events.push(message);return true;};
 render(SiteAccess,{props:{lang:'en',send}});
 return events;
}
it('requests exact permission before enabling interception',async()=>{
 const events=mount();
 await waitFor(()=>expect((screen.getByLabelText('Website origin') as HTMLInputElement).value).toBe('https://example.com'));
 await fireEvent.click(screen.getByRole('button',{name:'Enable site'}));
 await waitFor(()=>expect(events).toEqual([{origins:['https://example.com/*']},{type:'sites:set',origin:'https://example.com',enabled:true}]));
});
it('never enables a site after permission denial',async()=>{
 const events=mount(false);
 await waitFor(()=>expect((screen.getByLabelText('Website origin') as HTMLInputElement).value).toBe('https://example.com'));
 await fireEvent.click(screen.getByRole('button',{name:'Enable site'}));
 await screen.findByRole('alert');expect(events).toHaveLength(1);
});
it('grants TSA network permission without enabling interception',async()=>{
 const events=mount();
 await fireEvent.input(screen.getByLabelText('TSA URL'),{target:{value:'https://tsa.example.com/stamp'}});
 await fireEvent.click(screen.getByRole('button',{name:'Authorize TSA'}));
 await waitFor(()=>expect(events).toEqual([{origins:['https://tsa.example.com/*']} ]));
});
