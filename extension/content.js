export function installContent(win, chrome) {
  if (win.__autofirmeContent) return;
  win.__autofirmeContent = true;
  let port;
  const pending = new Set();
  const reply = (id,response) => win.postMessage({channel:'autofirme-v1-result',id,response},win.location.origin);
  function connect() {
    port = chrome.runtime.connect({name:'autofirme-page'});
    port.onMessage.addListener(message => {
      if (!message || !pending.delete(message.id)) return;
      reply(message.id,message.response);
    });
    port.onDisconnect.addListener(() => {
      // The worker could have disappeared after signing started. Never replay.
      for (const id of pending) reply(id,{kind:'error'});
      pending.clear();port = null;
    });
  }
  win.addEventListener('message', event => {
    if (event.source !== win || event.origin !== win.location.origin || event.data?.channel !== 'autofirme-v1-request') return;
    const {id,request} = event.data;
    if (typeof id !== 'string' || !/^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$/.test(id) || pending.has(id)) return;
    if (pending.size >= 8) { reply(id,{kind:'fallback'});return; }
    pending.add(id);
    try { if (!port) connect();port.postMessage({id,request}); }
    catch { pending.delete(id);reply(id,{kind:'error'}); }
  });
  win.addEventListener('pagehide', () => { port?.disconnect();pending.clear(); }, {once:true});
}
if (typeof window !== 'undefined' && globalThis.chrome?.runtime) installContent(window,globalThis.chrome);
