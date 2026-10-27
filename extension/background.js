import {Coordinator, AtCapacity, normalizeOrigin, trustedApp, trustedChooser} from './core/coordinator.js';
import {publicResult, REQUEST_TIMEOUT_MS} from './core/contract.js';

const SCRIPT_IDS = ['autofirme-content', 'autofirme-page'];
const UUID = /^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$/;
const BRIDGE_KEY = 'mcpBridge';
const BRIDGE_PROTOCOL = 'autofirme-bridge.v1';
/** Chrome suspends an idle MV3 worker after 30 s; app-level traffic keeps the bridge alive. */
const BRIDGE_HEARTBEAT_MS = 20_000;

/** `ws(s)://…/bridge?token=…`, derived from the endpoint and token the operator configured. */
export function bridgeUrl(settings) {
  const url = new URL(settings.url);
  if (!['http:', 'https:'].includes(url.protocol) || url.username || url.password) throw Error('Invalid MCP endpoint');
  url.protocol = url.protocol === 'https:' ? 'wss:' : 'ws:';
  // A deployment under a path prefix serves the bridge beside its `/mcp` entry.
  const endpoint = new URL(url);
  endpoint.search = '';
  endpoint.pathname = endpoint.pathname.replace(/\/+$/, '');
  url.pathname = new URL('bridge', endpoint).pathname;
  url.hash = '';
  url.search = '';
  url.searchParams.set('token', settings.token);
  return url.toString();
}

/** Chrome plumbing stays outside the consent and lifecycle state machine. */
export function createBridge(chrome, deps = {}) {
  const coordinator = new Coordinator();
  const root = chrome.runtime.getURL('');
  let scriptsQueue = Promise.resolve();
  let sitesQueue = Promise.resolve();
  let bridgeSocket = null;
  let bridgeSettings = null;
  /** Last content-script registration failure, surfaced by `sites:list`; empty when healthy. */
  let scriptsError = '';
  let bridgeError = '';
  let bridgeAttempt = 0;
  let bridgeRetry = null;
  let bridgeHeartbeat = null;
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
    return {allSites: wildcards.some(pattern => http.includes(pattern)), grantedOrigins: [...new Set(granted)], scriptsError};
  }
  /** A refused registration leaves interception silently dead; keep the reason for the UI. */
  async function syncScripts() {
    try {
      await registerScripts();
      scriptsError = '';
    } catch (error) {
      scriptsError = error instanceof Error ? error.message : String(error);
      throw error;
    }
  }
  async function registerScripts() {
    const {origins = []} = await chrome.permissions.getAll();
    const granted = origins.filter(pattern => /^https?:\/\//.test(pattern));
    // Interception follows the installed wildcard policy only: host grants requested later
    // (e.g. the TSA origin) must never widen where the bridge gets injected.
    const wildcards = new Set(granted.filter(pattern => /^[a-z][a-z0-9+.-]*:\/\/\*\/\*$/i.test(pattern)));
    const matches = [...new Set(granted.filter(pattern => {
      const scheme = pattern.slice(0, pattern.indexOf('://') + 3);
      return wildcards.has(pattern) || !wildcards.has(`${scheme}*/*`);
    }))];
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
  function normalizeBridgeSettings(value) {
    if (!value || typeof value !== 'object') throw Error('Invalid MCP bridge settings');
    const enabled = value.enabled === true;
    const url = typeof value.url === 'string' ? value.url.trim() : '';
    const token = typeof value.token === 'string' ? value.token : '';
    const settings = {enabled,url,token};
    // Validate even while disabled, so a typo is reported when the operator saves it.
    if (url || token) bridgeUrl({url:url || 'https://invalid.example',token:token || 'x'});
    if (enabled && (!url || !token)) throw Error('The MCP bridge needs an endpoint and a token');
    return settings;
  }
  function bridgeStatus() {
    return {enabled:bridgeSettings?.enabled === true,online:bridgeSocket?.readyState === 1,error:bridgeError};
  }
  function broadcastBridge() {
    chrome.runtime.sendMessage({type:'mcp:status',status:bridgeStatus()}).catch(() => {});
  }
  function bridgeReply(socket, id, ok, value, error) {
    if (socket === bridgeSocket && socket.readyState === 1) socket.send(JSON.stringify({type:'result',id,ok,...(ok ? {value} : {error})}));
  }
  function connectBridge(settings) {
    clearTimeout(bridgeRetry);
    bridgeRetry = null;
    clearInterval(bridgeHeartbeat);
    bridgeHeartbeat = null;
    if (bridgeSocket) {
      const old = bridgeSocket;
      bridgeSocket = null;
      old.onclose = null;
      old.onmessage = null;
      old.close();
      // Reconfiguring or stopping the bridge strands any consent window under the old socket.
      for (const item of coordinator.requests.values()) if (item.bridge) terminate(item,'error');
    }
    bridgeSettings = settings;
    bridgeError = '';
    if (!settings?.enabled) {
      broadcastBridge();
      return bridgeStatus();
    }
    let url;
    try { url = bridgeUrl(settings); }
    catch (error) { bridgeError = error instanceof Error ? error.message : 'invalid-endpoint'; broadcastBridge(); return bridgeStatus(); }
    let socket;
    try { socket = new WebSocket(url, BRIDGE_PROTOCOL); }
    catch { bridgeError = 'connection-failed'; broadcastBridge(); return bridgeStatus(); }
    bridgeSocket = socket;
    socket.onopen = () => {
      if (socket !== bridgeSocket) return;
      bridgeAttempt = 0;
      bridgeError = '';
      // A text frame every 20 s counts as WebSocket activity, so Chrome does not suspend the
      // worker mid-session. Control-frame pings from the server do not reset that timer.
      bridgeHeartbeat = setInterval(() => {
        if (socket === bridgeSocket && socket.readyState === 1) socket.send(JSON.stringify({type:'ping'}));
      }, BRIDGE_HEARTBEAT_MS);
      bridgeHeartbeat.unref?.();
      broadcastBridge();
    };
    socket.onerror = () => {
      if (socket !== bridgeSocket) return;
      bridgeError = 'connection-failed';
      broadcastBridge();
    };
    socket.onclose = () => {
      if (socket !== bridgeSocket) return;
      bridgeSocket = null;
      clearInterval(bridgeHeartbeat);
      bridgeHeartbeat = null;
      bridgeError = 'disconnected';
      // Every pending chooser belongs to a server that can no longer receive the result.
      for (const item of coordinator.requests.values()) if (item.bridge) terminate(item,'error');
      broadcastBridge();
      const wait = Math.min(30_000, 1_000 * 2 ** bridgeAttempt++);
      bridgeRetry = setTimeout(() => {
        if (bridgeSettings?.enabled && !bridgeSocket) connectBridge(bridgeSettings);
      }, wait);
      bridgeRetry.unref?.();
    };
    socket.onmessage = event => {
      if (socket !== bridgeSocket || typeof event.data !== 'string') return;
      // Jobs are independent: a consent window can stay open while the agent lists identities.
      void handleBridgeJob(socket,event.data);
    };
    broadcastBridge();
    return bridgeStatus();
  }
  async function restoreBridge() {
    const stored = (await chrome.storage.local.get(BRIDGE_KEY))[BRIDGE_KEY];
    if (!stored) return;
    try { connectBridge(normalizeBridgeSettings(stored)); } catch { /* Stored before validation. */ }
  }
  async function mcpCertificates() {
    // Loaded lazily: the service worker only needs the vault when an agent asks for it.
    const {browserVault} = await import('#lib/certstore');
    const identities = await browserVault().listIdentities();
    const now = Date.now();
    return identities.map(identity => {
      const expires = Date.parse(identity.notAfter);
      return {
        cert_id:identity.id, common_name:identity.commonName, subject:identity.commonName,
        issuer:'', serial:'', not_before:'', not_after:identity.notAfter,
        expired:Number.isFinite(expires) && expires <= now, not_yet_valid:false, signable:true,
        container:'AutoFirme extension'
      };
    });
  }
  async function bridgeSign(message) {
    if (message.format !== 'cades') throw Error('Only CAdES is supported over the MCP bridge');
    if (typeof message.certId !== 'string' || !message.certId) throw Error('certId is required');
    if (typeof message.signingTime !== 'string' || Number.isNaN(Date.parse(message.signingTime))) throw Error('Invalid signing time');
    const automatic = await (deps.autoSign ?? (await import('#lib/extension/auto-sign')).signRemembered)(message);
    if (automatic) return automatic;
    const request = {operation:'sign',format:'CAdES',mode:'explicit',algorithm:'SHA256withRSA',data:message.data};
    const signingTime = new Date(message.signingTime).toISOString();
    const {promise,resolve,reject} = Promise.withResolvers();
    const item = coordinator.captureBridge(request,message.certId,result => {
      clearTimeout(timer);
      closeWindow(item);
      if (result.kind === 'result' && result.signature) resolve({signed_b64:result.signature});
      else if (result.kind === 'cancel' || result.kind === 'fallback') reject(Error('cancelled by the operator'));
      else reject(Error('the extension could not sign the document'));
    });
    item.signingTime = signingTime;
    const timer = setTimeout(() => terminate(item,'error'), REQUEST_TIMEOUT_MS);
    timer.unref?.();
    try {
      const bytes = Uint8Array.from(atob(item.request.data), value => value.charCodeAt(0));
      item.digest = Array.from(new Uint8Array(await crypto.subtle.digest('SHA-256',bytes)), byte => byte.toString(16).padStart(2,'0')).join('');
      const win = await chrome.windows.create({url:root + 'index.html?request=' + item.id,type:'popup',width:480,height:520});
      item.windowId = win.id;
      item.chooserTabId = win.tabs?.[0]?.id;
      if (!Number.isInteger(item.chooserTabId)) throw Error('Could not identify the chooser tab');
      try { await chrome.windows.get(item.windowId); }
      catch { if (coordinator.get(item.id)) terminate(item,'error'); }
    } catch (error) {
      if (coordinator.get(item.id)) terminate(item,'error');
      else reject(error);
    }
    return promise;
  }
  async function handleBridgeJob(socket, raw) {
    let message;
    try { message = JSON.parse(raw); }
    catch { return; }
    if (!message || typeof message.id !== 'string' || socket !== bridgeSocket) return;
    try {
      let value;
      if (message.type === 'certs') value = {certificates:await mcpCertificates()};
      else if (message.type === 'sign') value = await bridgeSign(message);
      else throw Error('Unknown bridge job');
      bridgeReply(socket,message.id,true,value);
    } catch (error) {
      bridgeReply(socket,message.id,false,undefined,error instanceof Error ? error.message : String(error));
    }
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
    if (message.type === 'mcp:status') return bridgeStatus();
    if (message.type === 'mcp:configure') {
      const settings = normalizeBridgeSettings(message.settings);
      await chrome.storage.local.set({[BRIDGE_KEY]:settings});
      return connectBridge(settings);
    }
    const item = coordinator.get(message.id);
    if (!item) throw Error('This request has expired. Return to the website and start again.');
    if (!trustedChooser(sender, item, chrome.runtime.id, root)) throw Error('This chooser is not authorized for the request');
    if (!item.bridge) await fresh(item);
    switch (message.type) {
      case 'request:resize':
        if (!Number.isInteger(message.height) || message.height < 320 || message.height > 1200) throw Error('Invalid chooser height');
        await chrome.windows.update(item.windowId,{height:message.height});
        return true;
      case 'request:get':
        return {id:item.id,origin:item.origin,...item.request,digest:item.digest,
          ...(item.bridge ? {bridge:true,certId:item.certId,signingTime:item.signingTime} : {})};
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
        // onRemoved can fire before windowId is assigned above, so the removal listener never
        // saw this window. Confirm it is still alive; otherwise the page request would idle
        // until REQUEST_TIMEOUT_MS instead of resolving now.
        try { await chrome.windows.get(item.windowId); }
        catch { if (coordinator.get(item.id)) terminate(item, item.lifecycle.state === 'signing' ? 'cancel' : 'fallback'); }
        if (!alive || !coordinator.get(item.id)) closeWindow(item);
      } catch (error) {
        if (item && coordinator.get(item.id)) terminate(item,'fallback');
        else if (!item && alive) port.postMessage({id:message.id,response:{kind:error instanceof AtCapacity ? 'fallback' : 'error'}});
      }
    });
  });
  chrome.windows.onRemoved.addListener(windowId => {
    // A chooser closed while signing reports the user's cancellation, not a generic error;
    // cancel is terminal in the adapter and never replays the original call.
    for (const item of coordinator.requests.values()) if (item.windowId === windowId) terminate(item,item.lifecycle.state === 'waiting' ? 'fallback' : 'cancel');
  });
  chrome.webNavigation.onCommitted.addListener(({tabId,frameId,documentId}) => coordinator.invalidateDocument(tabId,frameId,documentId));
  chrome.tabs.onRemoved.addListener(tabId => {
    for (const item of coordinator.requests.values()) if (item.tabId === tabId) terminate(item);
  });
  chrome.permissions.onAdded.addListener(() => updateScripts().catch(() => {}));
  chrome.permissions.onRemoved.addListener(async () => {
    await updateScripts().catch(() => {});
    for (const item of coordinator.requests.values()) if (!item.bridge && !await permitted(item.origin)) terminate(item);
  });
  chrome.action.onClicked.addListener(tab => {
    let origin = null;
    try { origin = normalizeOrigin(new URL(tab.url).origin); } catch { /* Browser-owned page. */ }
    return openApp(false,origin);
  });
  chrome.runtime.onInstalled.addListener(() => { updateScripts().catch(() => {}); restoreBridge().catch(() => {}); });
  chrome.runtime.onStartup.addListener(() => { updateScripts().catch(() => {}); restoreBridge().catch(() => {}); });
  const ready = chrome.storage.local.setAccessLevel({accessLevel:'TRUSTED_CONTEXTS'}).then(updateScripts);
  ready.then(restoreBridge).catch(() => {});
  ready.catch(() => {});
  return {coordinator,handleMessage,updateScripts,connectBridge,bridgeStatus,bridgeSign,ready};
}
if (globalThis.chrome?.runtime?.id) createBridge(globalThis.chrome);
