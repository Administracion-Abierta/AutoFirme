import {Coordinator, AtCapacity, normalizeOrigin, trustedApp, trustedChooser, REQUEST_TIMEOUT_MS} from './core/coordinator.js';
import {publicResult} from './core/contract.js';

const SCRIPT_IDS = ['autofirme-content', 'autofirme-page'];
const UUID = /^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$/;

/** Chrome plumbing stays outside the consent and lifecycle state machine. */
export function createBridge(chrome) {
  const coordinator = new Coordinator();
  const root = chrome.runtime.getURL('');
  let scriptsQueue = Promise.resolve();
  let sitesQueue = Promise.resolve();
  async function disabledOrigins() {
    const {disabledOrigins = []} = await chrome.storage.local.get('disabledOrigins');
    return Array.isArray(disabledOrigins) ? [...new Set(disabledOrigins.filter(origin => {
      try { return normalizeOrigin(origin) === origin; } catch { return false; }
    }))] : [];
  }
  const hostGranted = origin => chrome.permissions.contains({origins:[origin + '/*']});
  async function permitted(origin) { return !(await disabledOrigins()).includes(origin) && await hostGranted(origin); }
  async function siteState() {
    const {origins = []} = await chrome.permissions.getAll();
    const http = origins.filter(pattern => /^https?:\/\//.test(pattern));
    const wildcards = ['http://*/*', 'https://*/*'];
    const granted = [];
    for (const pattern of http.filter(pattern => !wildcards.includes(pattern))) {
      try { granted.push(normalizeOrigin(pattern.slice(0, -2))); } catch { /* Not an exact-origin pattern. */ }
    }
    return {allSites: wildcards.some(pattern => http.includes(pattern)), grantedOrigins: [...new Set(granted)]};
  }
  async function syncScripts() {
    const {origins = []} = await chrome.permissions.getAll();
    const matches = origins.filter(pattern => /^https?:\/\//.test(pattern));
    const excludeMatches = (await disabledOrigins()).map(origin => origin + '/*');
    const current = await chrome.scripting.getRegisteredContentScripts();
    const ids = current.filter(script => SCRIPT_IDS.includes(script.id)).map(script => script.id);
    if (ids.length) await chrome.scripting.unregisterContentScripts({ids});
    if (matches.length) await chrome.scripting.registerContentScripts([
      {id:SCRIPT_IDS[0],matches,excludeMatches,js:['content.js'],runAt:'document_start',allFrames:true,world:'ISOLATED',persistAcrossSessions:true},
      {id:SCRIPT_IDS[1],matches,excludeMatches,js:['page.js'],runAt:'document_start',allFrames:true,world:'MAIN',persistAcrossSessions:true},
    ]);
  }
  function updateScripts() { scriptsQueue = scriptsQueue.catch(() => {}).then(syncScripts); return scriptsQueue; }
  function editSites(change) {
    sitesQueue = sitesQueue.catch(() => {}).then(async () => {
      const origins = await change(await disabledOrigins());
      await chrome.storage.local.set({disabledOrigins:origins});
      await updateScripts();
    });
    return sitesQueue;
  }
  function closeWindow(item) {
    if (item.windowId != null) chrome.windows.remove(item.windowId).catch(() => {});
  }
  function terminate(item, kind = 'error') { return coordinator.finish(item.id, {kind}); }
  async function fresh(item) {
    let frame;
    try { frame = await chrome.webNavigation.getFrame({tabId:item.tabId,frameId:item.frameId}); } catch { /* Navigation or tab close. */ }
    if (!coordinator.get(item.id)) throw Error('This request has expired');
    if (!frame || frame.documentId !== item.documentId || (frame.documentLifecycle && frame.documentLifecycle !== 'active')) {
      terminate(item);
      throw Error('The requesting document has changed');
    }
    let origin;
    try { origin = new URL(frame.url).origin; } catch { /* Invalid frame. */ }
    if (origin !== item.origin || !await permitted(item.origin)) {
      terminate(item);
      throw Error('Website permission was removed or the document has changed');
    }
    if (!coordinator.get(item.id)) throw Error('This request has expired');
  }
  /** @param {boolean} tab @param {string|null} [origin] */
  async function openApp(tab = false, origin = null) {
    const url = root + 'index.html' + (origin ? '?site=' + encodeURIComponent(origin) : '');
    if (tab) await chrome.tabs.create({url});
    else await chrome.windows.create({url,type:'popup',width:520,height:760});
    return true;
  }
  async function handleMessage(message, sender) {
    if (!message || typeof message.type !== 'string' || !trustedApp(sender, chrome.runtime.id, root)) throw Error('This page is not authorized');
    if (message.type === 'app:open') return openApp(message.tab === true);
    if (message.type === 'sites:list') {
      let suggestedOrigin = null;
      try { suggestedOrigin = normalizeOrigin(new URL(sender.url).searchParams.get('site')); } catch { /* No website was supplied. */ }
      return {disabledOrigins:await disabledOrigins(),suggestedOrigin,...await siteState()};
    }
    if (message.type === 'sites:set') {
      const origin = normalizeOrigin(message.origin);
      if (typeof message.enabled !== 'boolean') throw Error('Invalid site setting');
      await editSites(async origins => {
        return message.enabled ? origins.filter(value => value !== origin) : [...new Set([...origins,origin])];
      });
      if (!message.enabled) for (const item of coordinator.requests.values()) if (item.origin === origin) terminate(item);
      return true;
    }
    const item = coordinator.get(message.id);
    if (!item) throw Error('This request has expired. Return to the website and start again.');
    if (!trustedChooser(sender, item, chrome.runtime.id, root)) throw Error('This chooser is not authorized for the request');
    await fresh(item);
    switch (message.type) {
      case 'request:resize':
        if (!Number.isInteger(message.height) || message.height < 320 || message.height > 1200) throw Error('Invalid chooser height');
        await chrome.windows.update(item.windowId,{height:message.height});
        return true;
      case 'request:get':
        return {id:item.id,origin:item.origin,...item.request,digest:item.digest};
      case 'request:begin':
        if (!coordinator.start(item.id)) throw Error('Request is no longer waiting for confirmation');
        return true;
      case 'request:complete': {
        if (item.lifecycle.state !== 'signing') throw Error('Request is not signing');
        const result = publicResult(message.result, item.request.operation);
        if (!coordinator.finish(item.id,result)) throw Error('Request has already completed');
        return true;
      }
      case 'request:fail':
      case 'request:cancel':
      case 'request:fallback': {
        const kind = {'request:fail':'error','request:cancel':'cancel','request:fallback':'fallback'}[message.type];
        if (!terminate(item,kind)) throw Error('Request state does not permit this action');
        return true;
      }
      default: throw Error('Unknown extension action');
    }
  }
  chrome.runtime.onMessage.addListener((message,sender,respond) => {
    handleMessage(message,sender).then(value => respond({ok:true,value}), error => respond({ok:false,error:error.message || 'Operation failed'}));
    return true;
  });
  chrome.runtime.onConnect.addListener(port => {
    if (port.name !== 'autofirme-page' || port.sender?.id !== chrome.runtime.id || !port.sender.tab) return;
    const ids = new Set();
    const seen = new Set();
    const timers = new Map();
    let alive = true;
    function cleanup() {
      if (!alive) return;
      alive = false;
      for (const id of ids) {
        const item = coordinator.requests.get(id);
        coordinator.abandon(id);
        clearTimeout(timers.get(id));
        if (item) closeWindow(item);
      }
      ids.clear();timers.clear();
    }
    port.onDisconnect.addListener(cleanup);
    port.onMessage.addListener(async message => {
      if (!alive || !message || typeof message.id !== 'string' || !UUID.test(message.id) || seen.has(message.id)) return;
      seen.add(message.id);
      if (seen.size > 128) { cleanup();port.disconnect();return; }
      let item;
      try {
        const origin = new URL(port.sender.url).origin;
        if (!await permitted(origin)) {
          // Existing document wrappers remain after unregistering future scripts.
          // This call has not been captured or started, so original AutoFirma is safe.
          if (alive) port.postMessage({id:message.id,response:{kind:'fallback'}});
          return;
        }
        if (!alive) return;
        item = coordinator.capture(port.sender,message.request,response => {
          ids.delete(item.id);
          clearTimeout(timers.get(item.id));timers.delete(item.id);
          closeWindow(item);
          if (alive) port.postMessage({id:message.id,response});
        });
        ids.add(item.id);
        const timer = setTimeout(() => terminate(item), REQUEST_TIMEOUT_MS);
        timer.unref?.();
        timers.set(item.id,timer);
        await fresh(item);
        if (item.request.operation === 'sign') {
          const bytes = Uint8Array.from(atob(item.request.data), value => value.charCodeAt(0));
          item.digest = Array.from(new Uint8Array(await crypto.subtle.digest('SHA-256',bytes)), byte => byte.toString(16).padStart(2,'0')).join('');
        }
        if (!alive || !coordinator.get(item.id)) return;
        const win = await chrome.windows.create({url:root + 'index.html?request=' + item.id,type:'popup',width:480,height:520});
        item.windowId = win.id;
        item.chooserTabId = win.tabs?.[0]?.id;
        if (!Number.isInteger(item.chooserTabId)) throw Error('Could not identify the chooser tab');
        if (!alive || !coordinator.get(item.id)) closeWindow(item);
      } catch (error) {
        if (item && coordinator.get(item.id)) terminate(item,'fallback');
        else if (!item && alive) port.postMessage({id:message.id,response:{kind:error instanceof AtCapacity ? 'fallback' : 'error'}});
      }
    });
  });
  chrome.windows.onRemoved.addListener(windowId => {
    for (const item of coordinator.requests.values()) if (item.windowId === windowId) terminate(item,item.lifecycle.state === 'waiting' ? 'fallback' : 'error');
  });
  chrome.webNavigation.onCommitted.addListener(({tabId,frameId,documentId}) => coordinator.invalidateDocument(tabId,frameId,documentId));
  chrome.tabs.onRemoved.addListener(tabId => {
    for (const item of coordinator.requests.values()) if (item.tabId === tabId) terminate(item);
  });
  chrome.permissions.onAdded.addListener(() => updateScripts().catch(() => {}));
  chrome.permissions.onRemoved.addListener(async () => {
    await updateScripts();
    for (const item of coordinator.requests.values()) if (!await permitted(item.origin)) terminate(item);
  });
  chrome.action.onClicked.addListener(tab => {
    let origin = null;
    try { origin = normalizeOrigin(new URL(tab.url).origin); } catch { /* Browser-owned page. */ }
    return openApp(false,origin);
  });
  chrome.runtime.onInstalled.addListener(() => updateScripts().catch(() => {}));
  chrome.runtime.onStartup.addListener(() => updateScripts().catch(() => {}));
  const ready = chrome.storage.local.setAccessLevel({accessLevel:'TRUSTED_CONTEXTS'}).then(updateScripts);
  ready.catch(() => {});
  return {coordinator,handleMessage,updateScripts,ready};
}
if (globalThis.chrome?.runtime?.id) createBridge(globalThis.chrome);
