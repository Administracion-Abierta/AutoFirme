import {installAdapter} from './core/adapter.js';
import {REQUEST_TIMEOUT_MS} from './core/contract.js';

const installation = Symbol.for('autofirme.page.v1');

export function installPage(win) {
  if (win[installation]) return;
  win[installation] = true;
  const pending = new Map();
  win.addEventListener('message', event => {
    if (event.source !== win || event.origin !== win.location.origin || event.data?.channel !== 'autofirme-v1-result') return;
    const {id,response} = event.data;
    const item = pending.get(id);
    if (!item) return;
    pending.delete(id);win.clearTimeout(item.timer);
    item.resolve(response);
  });
  win.addEventListener('pagehide', () => {
    for (const item of pending.values()) { win.clearTimeout(item.timer);item.resolve({kind:'error'}); }
    pending.clear();
  });
  installAdapter(win, request => new Promise(resolve => {
    if (pending.size >= 8) { resolve({kind:'fallback'}); return; }
    const id = crypto.randomUUID();
    // No response can mean worker loss after signing; timeout is always terminal.
    const timer = win.setTimeout(() => {
      if (pending.delete(id)) resolve({kind:'error'});
    }, REQUEST_TIMEOUT_MS);
    pending.set(id,{resolve,timer});
    win.postMessage({channel:'autofirme-v1-request',id,request},win.location.origin);
  }));
}
if (typeof window !== 'undefined') installPage(window);
