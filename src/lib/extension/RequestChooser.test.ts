import { afterAll, afterEach, beforeAll, describe, expect, it, vi } from 'vitest';
import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/svelte';
import { rmSync } from 'node:fs';
import { createCadesFixtures, FIXTURE_PIN, type CadesFixtures } from '../../test/fixtures';
import { createVault, memoryBackend, type Vault } from '../certstore';
import { loadSignerSession } from '../keystore';
import { copy } from '../i18n';
import RequestChooser from './RequestChooser.svelte';
let fixtures: CadesFixtures;
let vault: Vault;
beforeAll(async () => {
 fixtures = createCadesFixtures();
 vault = createVault(memoryBackend());
 const session = await loadSignerSession(fixtures.p12, FIXTURE_PIN);
 await vault.saveIdentity({label:'My signing certificate',session,container:fixtures.p12,pin:FIXTURE_PIN});
});
afterAll(() => rmSync(fixtures.dir,{recursive:true,force:true}));
afterEach(cleanup);
const request = {id:'id',origin:'https://example.com',operation:'selectCertificate',digest:'abcdef'};
function mount(fail = false) {
 const calls: string[] = [];
 const send = async (m: {type:string}) => { calls.push(m.type); if (m.type === 'request:get') return request; if (fail && m.type === 'request:begin') throw new Error('Request expired'); return true; };
 render(RequestChooser, {props:{id:'id',vault,send}});
 return calls;
}
async function choose() {
 await fireEvent.click(await screen.findByRole('button',{name:new RegExp(copy.es.selectSign)}));
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
  const calls=mount(true); await choose();
  await fireEvent.click(document.querySelector('#request-confirm')!);
  await waitFor(() => expect(document.querySelector('#request-error')?.textContent).toContain('Request expired'));
  expect(calls).toEqual(['request:get','request:begin']);
  expect((document.querySelector('#request-fallback') as HTMLButtonElement).disabled).toBe(true);
 });
});
