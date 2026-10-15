export function installContent(win, chrome) {
  if (win.__autofirmeContent) return;
  win.__autofirmeContent = true;
  let port;
  const pending = new Set();
  const reply = (id,response) => win.postMessage({channel:'autofirme-v1-result',id,response},win.location.origin);
  function cleanup(expectedPort = port) {
    if (expectedPort !== port) return;
    port = null;
    // The worker could have disappeared after signing started. Never replay.
    for (const id of pending) reply(id,{kind:'error'});
    pending.clear();
  }
  function connect() {
    const connectedPort = chrome.runtime.connect({name:'autofirme-page'});
    port = connectedPort;
    connectedPort.onMessage.addListener(message => {
      if (port !== connectedPort || !message || !pending.delete(message.id)) return;
      reply(message.id,message.response);
    });
    connectedPort.onDisconnect.addListener(() => cleanup(connectedPort));
  }
  win.addEventListener('message', event => {
    if (event.source !== win || event.origin !== win.location.origin || event.data?.channel !== 'autofirme-v1-request') return;
    const {id,request} = event.data;
    if (typeof id !== 'string' || !/^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$/.test(id) || pending.has(id)) return;
    if (pending.size >= 8) { reply(id,{kind:'fallback'});return; }
    pending.add(id);
    try { if (!port) connect();port.postMessage({id,request}); }
    catch { cleanup(); }
  });
  win.addEventListener('pagehide', () => {
    const connectedPort = port;
    cleanup();
    connectedPort?.disconnect();
  });
}
if (typeof window !== 'undefined' && globalThis.chrome?.runtime) installContent(window,globalThis.chrome);
