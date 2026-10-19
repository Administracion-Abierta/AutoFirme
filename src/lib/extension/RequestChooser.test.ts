import { afterAll, afterEach, beforeAll, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor, within } from '@testing-library/svelte';
import { rmSync } from 'node:fs';
import { createCadesFixtures, FIXTURE_PIN, type CadesFixtures } from '../../test/fixtures';
import { createVault, memoryBackend, type Vault } from '../certstore';
import { loadSignerSession } from '../keystore';
import { copy } from '../i18n';
import RequestChooser from './RequestChooser.svelte';
import type { ExtensionRequest } from './runtime';
let fixtures: CadesFixtures;
let vault: Vault;
let expiresAt: number;
beforeAll(async () => {
 fixtures = createCadesFixtures();
 vault = createVault(memoryBackend());
 const session = await loadSignerSession(fixtures.p12, FIXTURE_PIN);
 expiresAt = session.signer.notAfter.getTime();
 await vault.saveIdentity({label:'My signing certificate',session,container:fixtures.p12,pin:FIXTURE_PIN});
});
afterAll(() => rmSync(fixtures.dir,{recursive:true,force:true}));
afterEach(()=>{cleanup();vi.restoreAllMocks();vi.unstubAllGlobals();});
const request: ExtensionRequest = {id:'id',origin:'https://example.com',operation:'selectCertificate',digest:'abcdef'};
function mount(failAt = '', value = request) {
 const calls: string[] = [];
 const send = async (m: {type:string}) => { calls.push(m.type); if (m.type === 'request:get') return value; if (m.type === failAt) throw new Error('Request expired'); return true; };
 render(RequestChooser, {props:{id:'id',vault,send}});
 return calls;
}
async function choose() {
 await fireEvent.click(await screen.findByRole('button',{name:/My signing certificate/}));
 await fireEvent.input(document.querySelector('#store-pin')!,{target:{value:FIXTURE_PIN}});
 await fireEvent.click(screen.getByRole('button',{name:copy.es.storeOpen}));
 await waitFor(() => expect((document.querySelector('#request-confirm') as HTMLButtonElement).disabled).toBe(false), {timeout:15000});
}
describe('request chooser', () => {
 it('shows website context and requires explicit confirmation after real stored identity unlock', async () => {
  const calls = mount();
  await screen.findByText('https://example.com');
  await choose();
  expect(calls).toEqual(['request:get']);
  await fireEvent.click(document.querySelector('#request-confirm')!);
  await waitFor(() => expect(calls).toEqual(['request:get','request:begin','request:complete']));
  expect((document.querySelector('#request-fallback') as HTMLButtonElement).disabled).toBe(true);
 });
 it('reports cancellation and fallback as separate explicit actions',async () => {
  const calls=mount(); await screen.findByText('https://example.com');
  await fireEvent.click(document.querySelector('#request-fallback')!);
  await waitFor(() => expect(calls).toEqual(['request:get','request:fallback']));
 });
 it('reports cancellation without beginning or signing',async () => {
  const calls=mount(); await screen.findByText('https://example.com');
  await fireEvent.click(document.querySelector('#request-cancel')!);
  await waitFor(() => expect(calls).toEqual(['request:get','request:cancel']));
 });
 it('fails safely when the request expires before consent reaches the worker',async () => {
  const calls=mount('request:begin'); await choose();
  await fireEvent.click(document.querySelector('#request-confirm')!);
  await waitFor(() => expect(document.querySelector('#request-error')?.textContent).toContain('Request expired'));
  expect(calls).toEqual(['request:get','request:begin']);
  expect((document.querySelector('#request-fallback') as HTMLButtonElement).disabled).toBe(true);
 });
});


it('keeps certificate selection, cancellation and fallback available when the certificate expires before begin', async () => {
 const calls = mount();
 await choose();
 // The window stays open after unlock; eligibility cached by the rendered UI is now stale.
 vi.spyOn(Date, 'now').mockReturnValue(expiresAt + 1);
 await fireEvent.click(document.querySelector('#request-confirm')!);
 await waitFor(() => expect((document.querySelector('#request-fallback') as HTMLButtonElement).disabled).toBe(false));
 expect((document.querySelector('#request-cancel') as HTMLButtonElement).disabled).toBe(false);
 expect((document.querySelector('#request-confirm') as HTMLButtonElement).disabled).toBe(true);
 expect(document.querySelector('#pkcs12-choices')).not.toBeNull();
 expect(document.querySelector('#request-error')?.textContent).toContain('El certificado ha caducado.');
 expect(calls).toEqual(['request:get']);
 await fireEvent.click(document.querySelector('#request-fallback')!);
 await waitFor(() => expect(calls).toEqual(['request:get', 'request:fallback']));
});


it('keeps fallback disabled after begin when completing the operation fails', async () => {
 const calls = mount('request:complete');
 await choose();
 await fireEvent.click(document.querySelector('#request-confirm')!);
 await waitFor(() => expect(calls).toEqual(['request:get', 'request:begin', 'request:complete', 'request:fail']));
 expect((document.querySelector('#request-fallback') as HTMLButtonElement).disabled).toBe(true);
 expect((document.querySelector('#request-cancel') as HTMLButtonElement).disabled).toBe(true);
 expect(document.querySelector('#pkcs12-choices')).toBeNull();
});


it('shows real saved certificate choices beside the website immediately without a nested dialog', async () => {
 const calls = mount();
 await screen.findByRole('button', {name:/My signing certificate/});
 expect(document.querySelector('#request-origin')?.textContent).toBe('https://example.com');
 expect(screen.queryByRole('dialog')).toBeNull();
 expect(screen.getByRole('button', {name:new RegExp(copy.es.addSign)})).toBeTruthy();
 expect(calls).toEqual(['request:get']);
});


it('puts the request title in the header and collapses technical request details',async()=>{
 mount('',{...request,operation:'sign',format:'CAdES',mode:'implicit'} as typeof request);
 const title=await screen.findByRole('heading',{name:'Firmar documento'});
 expect(title.closest('header')).not.toBeNull();
 const details=document.querySelector<HTMLDetailsElement>('#request-details')!;
 expect(details).not.toBeNull();expect(details.open).toBe(false);
 await fireEvent.click(within(details).getByText('Detalles de la solicitud'));
 expect(details.open).toBe(true);
});

it('copies the full SHA256 without showing it in the site section',async()=>{
 const writeText=vi.fn().mockResolvedValue(undefined);
 vi.stubGlobal('navigator',Object.create(navigator,{clipboard:{value:{writeText}}}));
 mount('',{...request,operation:'sign'});
 const button=await screen.findByRole('button',{name:'Copiar SHA256'});
 expect(document.querySelector('#request-site')?.textContent).not.toContain('abcdef');
 await fireEvent.click(button);
 expect(writeText).toHaveBeenCalledWith('abcdef');
 expect((await screen.findByRole('status')).textContent).toContain('SHA256 copiado');
});

it('keeps the selected certificate out of saved choices and makes certificate details optional',async()=>{
 mount();await choose();
 const choices=document.querySelector('#pkcs12-choices')!;
 expect(within(choices as HTMLElement).queryByRole('button',{name:/My signing certificate/})).toBeNull();
 const details=document.querySelector<HTMLDetailsElement>('#selected-certificate-details')!;
 expect(details).not.toBeNull();expect(details.open).toBe(false);
 await fireEvent.click(within(details).getByText('Detalles del certificado'));
 expect(details.open).toBe(true);
 const toolbar=document.querySelector('#pkcs12-choices-header')!;
 expect(within(toolbar as HTMLElement).getByRole('heading',{name:copy.es.storeTitle})).toBeTruthy();
 expect(within(toolbar as HTMLElement).getByRole('button',{name:copy.es.addSign})).toBeTruthy();
});

it('resizes after disclosure changes even when paint-based observation is paused',async()=>{
 let height=460;
 const sizes:number[]=[];
 vi.stubGlobal('ResizeObserver',class {observe(){} disconnect(){}});
 const bounds=HTMLElement.prototype.getBoundingClientRect;
 vi.spyOn(HTMLElement.prototype,'getBoundingClientRect').mockImplementation(function(this:HTMLElement){
  return this.id==='request-chooser' ? new DOMRect(0,0,480,height) : bounds.call(this);
 });
 vi.spyOn(window.screen,'availHeight','get').mockReturnValue(900);
 const send=async (message:{type:string;height?:number})=>{
  if(message.type==='request:get')return request;
  if(message.type==='request:resize')sizes.push(message.height!);
  return true;
 };
 render(RequestChooser,{props:{id:'id',vault,send}});
 await waitFor(()=>expect(sizes.at(-1)).toBe(462));
 const details=document.querySelector<HTMLDetailsElement>('#request-details')!;
 height=640;details.open=true;
 await waitFor(()=>expect(sizes.at(-1)).toBe(642));
 height=460;details.open=false;
 await waitFor(()=>expect(sizes.at(-1)).toBe(462));
});


it('keeps review and fallback available if clipboard access fails',async()=>{
 vi.stubGlobal('navigator',Object.create(navigator,{clipboard:{value:{writeText:vi.fn().mockRejectedValue(new Error('Denied'))}}}));
 const calls=mount('',{...request,operation:'sign'});
 await fireEvent.click(await screen.findByRole('button',{name:'Copiar SHA256'}));
 expect((await screen.findByRole('alert')).textContent).toContain('No se pudo copiar');
 expect(calls).toEqual(['request:get']);
 expect((document.querySelector('#request-fallback') as HTMLButtonElement).disabled).toBe(false);
 expect(document.querySelector('#request-digest')?.textContent).toBe('abcdef');
});

it('keeps focus on the open-app button after cancelling', async () => {
 mount();
 await screen.findByText('https://example.com');
 await fireEvent.click(document.querySelector('#request-cancel')!);
 expect(document.activeElement?.id).toBe('request-open-app');
});


it('keeps focus on the open-app button after handing over to AutoFirma', async () => {
 mount();
 await screen.findByText('https://example.com');
 await fireEvent.click(document.querySelector('#request-fallback')!);
 expect(document.activeElement?.id).toBe('request-open-app');
});


it('keeps keyboard focus on the PIN field and the selection through a compact unlock', async () => {
 mount();
 await fireEvent.click(await screen.findByRole('button',{name:/My signing certificate/}));
 await waitFor(()=>expect(document.activeElement?.id).toBe('store-pin'));
 await fireEvent.input(document.querySelector('#store-pin')!,{target:{value:FIXTURE_PIN}});
 await fireEvent.click(screen.getByRole('button',{name:copy.es.storeOpen}));
 await waitFor(()=>expect(document.activeElement?.id).toBe('pkcs12-selection'),{timeout:15000});
 await fireEvent.click(document.querySelector('#request-confirm')!);
 expect(document.activeElement?.id).toBe('request-open-app');
});


it('offers an MCP agent only the identity it requested', async () => {
 const [identity] = await vault.listIdentities();
 mount('', {...request, bridge:true, certId:identity.id});
 expect(await screen.findByText('Agente MCP')).toBeTruthy();
 await waitFor(() => expect(document.querySelector('#store-pin')).not.toBeNull());
 expect(screen.queryByRole('button',{name:copy.es.addSign})).toBeNull();
 cleanup();
 mount('', {...request, bridge:true, certId:'another-identity'});
 expect(await screen.findByText('Agente MCP')).toBeTruthy();
 await waitFor(() => expect(screen.queryByRole('button',{name:/My signing certificate/})).toBeNull());
 expect(document.querySelector('#store-pin')).toBeNull();
 expect(screen.queryByRole('button',{name:copy.es.addSign})).toBeNull();
});

it('shows worker failure messages in the default Spanish UI', async () => {
 render(RequestChooser,{props:{id:'id',vault,send:async()=>{throw new Error('The requesting document has changed');}}});
 expect((await screen.findByRole('alert')).textContent).toBe('El documento de la página solicitante ha cambiado.');
});
