import {parseCall} from './contract.js';
const wrappedMarker = Symbol.for('autofirme.AutoScript.v1');
export function wrapAutoScript(api, transport) {
  if (!api || typeof api !== 'object' || api[wrappedMarker] || !Object.isExtensible(api)) return;
  Object.defineProperty(api, wrappedMarker, {value: true});
  let sticky = false;
  let customStore = false;
  for (const [name, update] of /** @type {const} */ ([
    ['setStickySignatory', /** @param {any} value */ (value) => { sticky = Boolean(value); }],
    ['setKeyStore', /** @param {any} value */ (value) => { customStore = value != null; }],
  ])) {
    const original = api[name];
    if (typeof original === 'function') api[name] = function (...args) {
      update(args[0]);
      return original.apply(this, args);
    };
  }
  for (const name of ['sign', 'selectCertificate']) {
    const original = api[name];
    if (typeof original !== 'function') continue;
    api[name] = function (...args) {
      const receiver = this;
      const request = sticky || customStore ? null : parseCall(name, args);
      if (!request) return original.apply(receiver, args);
      const success = args[name === 'sign' ? 4 : 1];
      const error = args[name === 'sign' ? 5 : 2];
      // Transport errors are terminal: replay is unsafe if signing might have begun.
      Promise.resolve().then(() => transport(request)).catch(() => ({kind: 'error'})).then(response => {
        if (response?.kind === 'fallback') { original.apply(receiver, args); return; }
        if (response?.kind === 'cancel') { error('es.gob.afirma.core.AOCancelledOperationException', 'Operation cancelled'); return; }
        if (response?.kind !== 'result') { error('java.lang.Exception', 'AutoFirme could not complete the request. Check the extension.'); return; }
        if (name === 'sign') success(response.signature, response.certificate, null);
        else success(response.certificate);
      });
    };
  }
}
// Sedes expose the signer as `AutoScript`; older integrations use a distinct
// `MiniApplet` global. Both are wrapped — a shared identity re-wrap is a no-op.
const ALIASES = ['AutoScript', 'MiniApplet'];
export function installAdapter(win, transport) {
  const apply = () => { for (const name of ALIASES) wrapAutoScript(win[name], transport); };
  // Polling also handles replacement objects; the setter covers immediate initialization.
  for (const name of ALIASES) {
    const descriptor = Object.getOwnPropertyDescriptor(win, name);
    if (!descriptor || (descriptor.configurable && 'value' in descriptor)) {
      let current = win[name];
      Object.defineProperty(win, name, {configurable: true, enumerable: true,
        get: () => current, set(value) { current = value; wrapAutoScript(value, transport); }});
    }
  }
  apply();
  let timer = win.setInterval(apply, 250);
  win.addEventListener('pagehide', () => {
    if (timer !== null) win.clearInterval(timer);
    timer = null;
  });
  win.addEventListener('pageshow', () => {
    apply();
    if (timer === null) timer = win.setInterval(apply, 250);
  });
}
