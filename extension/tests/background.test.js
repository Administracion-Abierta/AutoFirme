import test from 'node:test';
import assert from 'node:assert/strict';
import {createBridge,bridgeUrl} from '../background.js';
import {MAX_GLOBAL_REQUESTS} from '../core/coordinator.js';

const event = () => ({listeners: /** @type {((...args: any[]) => any)[]} */ ([]),addListener(fn){this.listeners.push(fn);},async emit(...args){return Promise.all(this.listeners.map(fn=>fn(...args)));}});
function browser() {
  /** @type {{disabled:string[],enabled:string[],granted:Set<string>,registered:any[],registrations:number,resized:any[],windows:any[],removed:any[],frame:any,windowDiesOnCreate:boolean,failRegistration:boolean}} */
  const state={disabled:[],enabled:[],granted:new Set(),registered:[],registrations:0,resized:[],windows:[],removed:[],frame:{documentId:'doc-a',url:'https://site.test/form'},windowDiesOnCreate:false,failRegistration:false};
  const live=new Set();
  const chrome={runtime:{id:'extension',getURL:path=>'chrome-extension://extension/'+path,onInstalled:event(),onStartup:event(),onMessage:event(),onConnect:event()},
    storage:{local:{async get(){return {enabledOrigins:state.enabled,disabledOrigins:state.disabled};},async set(v){if(v.disabledOrigins)state.disabled=v.disabledOrigins;},async setAccessLevel(){}}},
    permissions:{async getAll(){return {origins:[...state.granted]};},async contains({origins}){return origins.every(x=>state.granted.has(x)||state.granted.has(new URL(x).protocol+'//*/*'));},onAdded:event(),onRemoved:event()},
    scripting:{async getRegisteredContentScripts(){return state.registered;},async unregisterContentScripts(){state.registered=[];},async registerContentScripts(v){if(state.failRegistration)throw Error('registration refused');state.registered=v;state.registrations++;}},
    windows:{async update(id,options){state.resized.push({id,...options});return {id,...options};},async create(options){const win={id:20+state.windows.length,tabs:[{id:15+state.windows.length}]};state.windows.push({...win,...options});if(state.windowDiesOnCreate){chrome.windows.onRemoved.emit(win.id);}else live.add(win.id);return win;},async get(id){if(!live.has(id))throw Error('No window with id: '+id);return {id};},async remove(id){live.delete(id);state.removed.push(id);},onRemoved:event()},
    tabs:{async create(options){state.windows.push(options);},onRemoved:event()},
    webNavigation:{async getFrame(){return state.frame;},onCommitted:event()},action:{onClicked:event()}};
  return {chrome,state};
}
const source=()=>({id:'extension',url:'https://site.test/form',tab:{id:4},frameId:0,documentId:'doc-a'});
const app=()=>({id:'extension',url:'chrome-extension://extension/index.html',tab:{id:9,url:'chrome-extension://extension/index.html'},frameId:0});
const chooser=item=>({id:'extension',url:`chrome-extension://extension/index.html?request=${item.id}`,tab:{id:item.chooserTabId,url:`chrome-extension://extension/index.html?request=${item.id}`},frameId:0});
async function connect(chrome) {
  const replies=[];const port={name:'autofirme-page',sender:source(),onMessage:event(),onDisconnect:event(),postMessage(m){if(this.disconnected)throw Error('Disconnected port');replies.push(m);},disconnect(){this.disconnected=true;}};
  await chrome.runtime.onConnect.emit(port);return {port,replies};
}
async function capture(env) {
  env.state.granted.add('https://site.test/*');
  const connected=await connect(env.chrome);
  await connected.port.onMessage.emit({id:crypto.randomUUID(),request:{operation:'selectCertificate'}});
  return {...connected,item:[...env.bridge.coordinator.requests.values()][0]};
}
function setup(deps){const env=browser();env.bridge=createBridge(env.chrome,deps);return env;}
// `chrome.windows.create` resolves after a microtask-plus tick, so a fixed sleep is a coin
// flip on a loaded runner. Poll for the state the assertion needs instead.
const until=async(wanted,what)=>{for(let i=0;i<400;i++){const value=wanted();if(value)return value;await new Promise(resolve=>setTimeout(resolve,5));}throw Error('timed out waiting for '+what);};

test('a clean install registers the wildcard bridge and captures website calls',async()=>{
  const env=setup();env.state.granted.add('http://*/*');env.state.granted.add('https://*/*');await env.bridge.ready;
  assert.deepEqual(env.state.registered[0].matches,['http://*/*','https://*/*']);
  const list=await env.bridge.handleMessage({type:'sites:list'},app());
  assert.equal(list.allSites,true);assert.deepEqual(list.grantedOrigins,[]);
  const connected=await connect(env.chrome);
  await connected.port.onMessage.emit({id:crypto.randomUUID(),request:{operation:'selectCertificate'}});
  const item=[...env.bridge.coordinator.requests.values()][0];assert.ok(item);
  await env.bridge.handleMessage({type:'request:cancel',id:item.id},chooser(item));
});

test('a refused content-script registration is reported instead of silently disabling interception',async()=>{
  const env=browser();env.state.failRegistration=true;env.state.granted.add('https://*/*');
  const bridge=createBridge(env.chrome);await bridge.ready.catch(()=>{});
  const list=await bridge.handleMessage({type:'sites:list'},app());
  assert.match(list.scriptsError,/registration refused/);
  env.state.failRegistration=false;
  await bridge.updateScripts();
  assert.equal((await bridge.handleMessage({type:'sites:list'},app())).scriptsError,'');
});

test('sites:list reports per-origin grants and the all-sites wildcard separately',async()=>{
  const env=setup();env.state.granted.add('https://site.test/*');await env.bridge.ready;
  let list=await env.bridge.handleMessage({type:'sites:list'},app());
  assert.equal(list.allSites,false);assert.deepEqual(list.grantedOrigins,['https://site.test']);
  assert.deepEqual(env.state.registered.map(script=>script.matches),[['https://site.test/*'],['https://site.test/*']]);
  env.state.granted.add('https://*/*');await env.bridge.updateScripts();
  list=await env.bridge.handleMessage({type:'sites:list'},app());
  assert.equal(list.allSites,true);assert.deepEqual(list.grantedOrigins,['https://site.test']);
});

test('granting the all-sites optional permission activates every website',async()=>{
  const env=setup();env.state.enabled=['https://old-allowed.test'];env.state.granted.add('http://*/*');env.state.granted.add('https://*/*');await env.bridge.ready;
  assert.equal(env.state.registered.length,2);
  for(const script of env.state.registered){assert.deepEqual(script.matches,['http://*/*','https://*/*']);assert.equal(script.allFrames,true);assert.equal(script.runAt,'document_start');}
  const {port,replies}=await connect(env.chrome);
  await port.onMessage.emit({id:crypto.randomUUID(),request:{operation:'selectCertificate'}});
  const item=[...env.bridge.coordinator.requests.values()][0];assert.ok(item);assert.deepEqual(replies,[]);
  await env.bridge.handleMessage({type:'request:cancel',id:item.id},chooser(item));
});

test('site exceptions persist across restarts and restoring does not need a new host grant',async()=>{
  const env=setup();env.state.granted.add('https://*/*');await env.bridge.ready;
  await env.bridge.handleMessage({type:'sites:set',origin:'https://site.test',enabled:false},app());
  assert.deepEqual(env.state.disabled,['https://site.test']);
  for(const script of env.state.registered)assert.deepEqual(script.excludeMatches,['https://site.test/*']);
  const restarted=createBridge(env.chrome);await restarted.ready;
  const list=await restarted.handleMessage({type:'sites:list'},app());assert.deepEqual(list.disabledOrigins,['https://site.test']);
  await restarted.handleMessage({type:'sites:set',origin:'https://site.test',enabled:true},app());
  assert.deepEqual(env.state.disabled,[]);
});

test('request reception checks Chrome host access, validates payload and authenticates exact chooser tab',async()=>{
  const env=setup();await env.bridge.ready;
  const unenabled=await connect(env.chrome);await unenabled.port.onMessage.emit({id:crypto.randomUUID(),request:{operation:'selectCertificate'}});assert.equal(unenabled.replies[0].response.kind,'fallback');
  const {item}=await capture(env);assert.ok(item);assert.equal(env.state.windows[0].width,480);
  await assert.rejects(env.bridge.handleMessage({type:'request:get',id:item.id},app()),/authorized/i);
  await assert.rejects(env.bridge.handleMessage({type:'request:get',id:item.id},{...chooser(item),tab:{id:99,url:chooser(item).url}}),/authorized/i);
  const value=await env.bridge.handleMessage({type:'request:get',id:item.id},chooser(item));assert.equal(value.origin,'https://site.test');assert.equal(value.operation,'selectCertificate');
  await env.bridge.handleMessage({type:'request:cancel',id:item.id},chooser(item));
});

test('begin gates completion and chooser closure after signing never falls back',async()=>{
  const env=setup();await env.bridge.ready;const {item,replies}=await capture(env);
  await assert.rejects(env.bridge.handleMessage({type:'request:complete',id:item.id,result:{certificate:'YQ=='}},chooser(item)),/signing/i);
  assert.equal(await env.bridge.handleMessage({type:'request:begin',id:item.id},chooser(item)),true);
  await assert.rejects(env.bridge.handleMessage({type:'request:fallback',id:item.id},chooser(item)),/state/i);
  await env.chrome.windows.onRemoved.emit(item.windowId);assert.equal(replies.length,1);assert.equal(replies[0].response.kind,'cancel');
  await env.chrome.windows.onRemoved.emit(item.windowId);assert.equal(replies.length,1);
});

test('navigation rejects a stale document even before its disconnect event',async()=>{
  const env=setup();await env.bridge.ready;const {item,replies}=await capture(env);
  env.state.frame={documentId:'doc-new',url:'https://site.test/other'};
  await assert.rejects(env.bridge.handleMessage({type:'request:begin',id:item.id},chooser(item)),/document/i);
  assert.equal(replies[0].response.kind,'error');assert.equal(env.bridge.coordinator.get(item.id),undefined);
});

test('worker port loss never replays; waiting chooser closure delegates exactly once',async()=>{
  const env=setup();await env.bridge.ready;const first=await capture(env);
  await env.chrome.windows.onRemoved.emit(first.item.windowId);await env.chrome.windows.onRemoved.emit(first.item.windowId);
  assert.deepEqual(first.replies.map(r=>r.response.kind),['fallback']);
  const second=await capture(env);await second.port.onDisconnect.emit();await env.chrome.windows.onRemoved.emit(second.item.windowId);
  assert.deepEqual(second.replies,[]);assert.equal(env.bridge.coordinator.get(second.item.id),undefined);
});

test('opening complete app preserves pending request and successful result is public only',async()=>{
  const env=setup();await env.bridge.ready;const {item,replies}=await capture(env);
  await env.bridge.handleMessage({type:'app:open',tab:true},chooser(item));assert.ok(env.bridge.coordinator.get(item.id));assert.equal(env.state.windows.at(-1).url,'chrome-extension://extension/index.html');
  await env.bridge.handleMessage({type:'request:begin',id:item.id},chooser(item));
  await env.bridge.handleMessage({type:'request:complete',id:item.id,result:{certificate:'YQ==',pin:'secret'}},chooser(item));
  assert.deepEqual(replies[0].response,{kind:'result',certificate:'YQ=='});
});

test('cached documents cannot authorize signing even when their document ID still exists',async()=>{
  const env=setup();await env.bridge.ready;const {item,replies}=await capture(env);
  env.state.frame={documentId:'doc-a',url:'https://site.test/form',documentLifecycle:'cached'};
  await assert.rejects(env.bridge.handleMessage({type:'request:begin',id:item.id},chooser(item)),/document/i);
  assert.equal(replies[0].response.kind,'error');
});

test('signing view contains original data, explicit mode and SHA-256 document digest',async()=>{
  const env=setup();await env.bridge.ready;env.state.granted.add('https://site.test/*');
  const {port,replies}=await connect(env.chrome);
  await port.onMessage.emit({id:crypto.randomUUID(),request:{operation:'sign',data:'YQ==',algorithm:'SHA256withRSA',format:'CAdES',mode:'explicit'}});
  const item=[...env.bridge.coordinator.requests.values()][0];
  const view=await env.bridge.handleMessage({type:'request:get',id:item.id},chooser(item));
  assert.equal(view.data,'YQ==');assert.equal(view.mode,'explicit');assert.equal(view.digest,'ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb');
  await env.bridge.handleMessage({type:'request:fallback',id:item.id},chooser(item));assert.equal(replies[0].response.kind,'fallback');
});


test('worker-initiated disconnect immediately revokes outstanding chooser authorization',async()=>{
  const env=setup();await env.bridge.ready;const {item,port}=await capture(env);
  for(let i=0;i<128;i++) await port.onMessage.emit({id:crypto.randomUUID(),request:{operation:'selectCertificate'}});
  assert.equal(env.bridge.coordinator.get(item.id),undefined);
  assert.equal(env.bridge.coordinator.requests.size,0);
  assert.ok(env.state.removed.includes(item.windowId));
  await assert.rejects(env.bridge.handleMessage({type:'request:begin',id:item.id},chooser(item)),/expired/i);
  await port.onDisconnect.emit();assert.equal(env.bridge.coordinator.requests.size,0);
});

test('site management accepts main app after history replaces its search parameters',async()=>{
  const env=setup();await env.bridge.ready;env.state.granted.add('https://site.test/*');
  const navigated={...app(),tab:{id:9,url:'chrome-extension://extension/index.html?tab=verificar'}};
  assert.equal(await env.bridge.handleMessage({type:'sites:set',origin:'https://site.test',enabled:true},navigated),true);
  assert.deepEqual(env.state.disabled,[]);
});

test('new calls from an already loaded disabled site delegate exactly once without capture or chooser',async()=>{
  const env=setup();await env.bridge.ready;const {item,port,replies}=await capture(env);
  await env.bridge.handleMessage({type:'request:cancel',id:item.id},chooser(item));
  await env.bridge.handleMessage({type:'sites:set',origin:'https://site.test',enabled:false},app());
  const windowCount=env.state.windows.length;const id=crypto.randomUUID();
  await port.onMessage.emit({id,request:{operation:'selectCertificate'}});
  await port.onMessage.emit({id,request:{operation:'selectCertificate'}});
  assert.deepEqual(replies.map(reply=>reply.response.kind),['cancel','fallback']);
  assert.equal(env.bridge.coordinator.requests.size,0);assert.equal(env.state.windows.length,windowCount);
});

test('revocation terminates existing waiting and signing requests while later calls delegate',async()=>{
  const env=setup();await env.bridge.ready;const signing=await capture(env);const waiting=await capture(env);
  await env.bridge.handleMessage({type:'request:begin',id:signing.item.id},chooser(signing.item));
  env.state.granted.delete('https://site.test/*');await env.chrome.permissions.onRemoved.emit({origins:['https://site.test/*']});
  assert.deepEqual(signing.replies.map(reply=>reply.response.kind),['error']);
  assert.deepEqual(waiting.replies.map(reply=>reply.response.kind),['error']);
  await assert.rejects(env.bridge.handleMessage({type:'request:complete',id:signing.item.id,result:{certificate:'YQ=='}},chooser(signing.item)),/expired/i);
  const windowCount=env.state.windows.length;
  await signing.port.onMessage.emit({id:crypto.randomUUID(),request:{operation:'selectCertificate'}});
  assert.deepEqual(signing.replies.map(reply=>reply.response.kind),['error','fallback']);
  assert.equal(env.bridge.coordinator.requests.size,0);assert.equal(env.state.windows.length,windowCount);
});


test('only the bound chooser can resize its window without authorizing signing',async()=>{
 const env=setup();await env.bridge.ready;const {item,replies}=await capture(env);
 await assert.rejects(env.bridge.handleMessage({type:'request:resize',id:item.id,height:640},app()),/authorized/i);
 for(const height of [640,420])await env.bridge.handleMessage({type:'request:resize',id:item.id,height},chooser(item));
 assert.deepEqual(env.state.resized,[{id:item.windowId,height:640},{id:item.windowId,height:420}]);
 for(const height of [null,'640',NaN,Infinity,-1,2000])await assert.rejects(env.bridge.handleMessage({type:'request:resize',id:item.id,height},chooser(item)),/height/i);
 assert.deepEqual(replies,[]);assert.equal(item.lifecycle.state,'waiting');
 await env.bridge.handleMessage({type:'request:cancel',id:item.id},chooser(item));
});

test('the global cap degrades new port requests to fallback without opening another window, and releases on completion',async()=>{
  const env=setup();await env.bridge.ready;env.state.granted.add('https://site.test/*');
  env.chrome.webNavigation.getFrame=async({frameId})=>({documentId:`doc-${frameId}`,url:'https://site.test/form'});
  const openPort=frameId=>{
    const sender={...source(),frameId,documentId:`doc-${frameId}`};
    const replies=[];const port={name:'autofirme-page',sender,onMessage:event(),onDisconnect:event(),postMessage(m){replies.push(m);},disconnect(){}};
    return {port,replies};
  };
  const ports=[openPort(0),openPort(1),openPort(2)];
  for(const p of ports) await env.chrome.runtime.onConnect.emit(p.port);
  for(let i=0;i<MAX_GLOBAL_REQUESTS;i++) await ports[i%2].port.onMessage.emit({id:crypto.randomUUID(),request:{operation:'selectCertificate'}});
  assert.equal(env.bridge.coordinator.requests.size,MAX_GLOBAL_REQUESTS);
  assert.equal(env.state.windows.length,MAX_GLOBAL_REQUESTS);
  const overflowId=crypto.randomUUID();
  await ports[2].port.onMessage.emit({id:overflowId,request:{operation:'selectCertificate'}});
  assert.deepEqual(ports[2].replies.at(-1),{id:overflowId,response:{kind:'fallback'}});
  assert.equal(env.state.windows.length,MAX_GLOBAL_REQUESTS);
  assert.equal(env.bridge.coordinator.requests.size,MAX_GLOBAL_REQUESTS);
  const [oldest]=env.bridge.coordinator.requests.values();
  await env.bridge.handleMessage({type:'request:cancel',id:oldest.id},chooser(oldest));
  assert.deepEqual(ports[0].replies.at(-1).response,{kind:'cancel'});
  const nextId=crypto.randomUUID();
  await ports[2].port.onMessage.emit({id:nextId,request:{operation:'selectCertificate'}});
  assert.equal(env.bridge.coordinator.requests.size,MAX_GLOBAL_REQUESTS);
  assert.equal(env.state.windows.length,MAX_GLOBAL_REQUESTS+1);
  assert.ok(!ports[2].replies.some(reply=>reply.id===nextId),'the accepted request stays pending');
});

test('real onMessage listener keeps return true and answers through the ok envelope',async()=>{
  const env=setup();await env.bridge.ready;
  const listener=env.chrome.runtime.onMessage.listeners[0];
  const calls=[];const respond=(...args)=>calls.push(args);
  const flush=()=>new Promise(resolve=>setImmediate(resolve));
  assert.equal(listener({type:'app:open'},app(),respond),true,'the async reply requires return true');
  await flush();assert.deepEqual(calls.at(-1),[{ok:true,value:true}]);
  assert.equal(listener({type:'sites:list'},{...app(),id:'other'},respond),true);
  await flush();assert.equal(calls.at(-1)[0].ok,false);assert.match(calls.at(-1)[0].error,/authorized/i);
  listener({type:'app:open'},source(),respond);
  await flush();assert.equal(calls.at(-1)[0].ok,false,'a web-page sender under the extension id is rejected');
});

test('real onConnect listener rejects wrong port name, foreign sender id, and tabless senders',async()=>{
  const env=setup();await env.bridge.ready;env.state.granted.add('https://site.test/*');
  const opened=[...env.chrome.runtime.onConnect.listeners];assert.equal(opened.length,1);
  const attempt=async sender=>{
    const replies=[];const port={name:'autofirme-page',sender,onMessage:event(),onDisconnect:event(),postMessage(m){replies.push(m);},disconnect(){this.disconnected=true;}};
    await opened[0](port);
    await port.onMessage.emit({id:crypto.randomUUID(),request:{operation:'selectCertificate'}});
    return replies;
  };
  assert.equal((await attempt(source())).length,0,'a valid capture stays pending without a spurious reply');
  assert.equal(env.bridge.coordinator.requests.size,1);
  const base=source();
  for(const bad of /** @type {any[]} */ ([{...base,name:'autofirme-other'},{...base,id:'foreign-extension'},{...base,tab:undefined}])){
    const replies=[];
    const port={name:bad.name??'autofirme-page',sender:bad,onMessage:event(),onDisconnect:event(),postMessage(m){replies.push(m);},disconnect(){}};
    await opened[0](port);
    await port.onMessage.emit({id:crypto.randomUUID(),request:{operation:'selectCertificate'}});
    assert.deepEqual(replies,[],'a rejected port never responds');
  }
  assert.equal(env.bridge.coordinator.requests.size,1,'foreign, tabless, or nameless ports never capture');
});

test('sites:set rejects invalid origins and non-boolean enabled and terminating loop ends in-flight requests',async()=>{
  const env=setup();await env.bridge.ready;
  for(const bad of [{origin:'https://site.test/path',enabled:false},{origin:'https://evil.test#x',enabled:false},{origin:'ftp://site.test',enabled:false},{origin:42,enabled:false},{origin:'https://site.test'}]){
    await assert.rejects(env.bridge.handleMessage({type:'sites:set',...bad},app()),/origin|setting/i,String(bad.origin));
  }
  await assert.rejects(env.bridge.handleMessage({type:'sites:set',origin:'https://site.test',enabled:'false'},app()),/setting/i);
  assert.deepEqual(env.state.disabled,[],'a rejected change persists nothing');
  const {item,replies}=await capture(env);
  await env.bridge.handleMessage({type:'sites:set',origin:'https://site.test',enabled:false},app());
  assert.deepEqual(replies.map(r=>r.response.kind),['error'],'disabling the site terminates its in-flight request');
  assert.equal(env.bridge.coordinator.requests.size,0);
  assert.ok(env.state.removed.includes(item.windowId),'the orphaned chooser window is closed');
});

test('a chooser window dying during creation resolves fallback instead of idling until timeout',async()=>{
  const env=setup();await env.bridge.ready;
  env.state.granted.add('https://site.test/*');env.state.windowDiesOnCreate=true;
  const {port,replies}=await connect(env.chrome);
  const id=crypto.randomUUID();
  await port.onMessage.emit({id,request:{operation:'selectCertificate'}});
  assert.deepEqual(replies,[{id,response:{kind:'fallback'}}],'windowId race resolves immediately, not after REQUEST_TIMEOUT_MS');
  assert.equal(env.bridge.coordinator.requests.size,0);
});

test('post-install host grants never widen the interception matches',async()=>{
  const env=setup();env.state.granted.add('http://*/*');env.state.granted.add('https://*/*');await env.bridge.ready;
  assert.deepEqual(env.state.registered[0].matches,['http://*/*','https://*/*']);
  env.state.granted.add('https://tsa.example.com/*');
  await env.chrome.permissions.onAdded.emit({origins:['https://tsa.example.com/*']});
  assert.ok(env.state.registrations>1,'the onAdded grant did trigger a re-sync');
  for(const script of env.state.registered)assert.deepEqual(script.matches,['http://*/*','https://*/*'],'the TSA grant must not add hosts to the bridge');
  env.state.granted.delete('https://*/*');
  await env.chrome.permissions.onRemoved.emit({origins:['https://*/*']});
  for(const script of env.state.registered)assert.ok(script.matches.includes('https://tsa.example.com/*'),'without the wildcard the explicit grant still registers');
});

test('a chooser reloaded mid-signing exits through cancel and stays dead exactly once',async()=>{
  const env=setup();await env.bridge.ready;const {item,replies}=await capture(env);
  await env.bridge.handleMessage({type:'request:begin',id:item.id},chooser(item));
  const reloaded={...chooser(item)};
  await env.bridge.handleMessage({type:'request:get',id:item.id},reloaded);
  await assert.rejects(env.bridge.handleMessage({type:'request:begin',id:item.id},reloaded),/waiting/i);
  await assert.rejects(env.bridge.handleMessage({type:'request:fallback',id:item.id},reloaded),/state/i);
  assert.equal(await env.bridge.handleMessage({type:'request:cancel',id:item.id},reloaded),true);
  assert.deepEqual(replies.map(r=>r.response.kind),['cancel']);
  await assert.rejects(env.bridge.handleMessage({type:'request:cancel',id:item.id},reloaded),/expired/i);
  assert.equal(env.state.removed.includes(item.windowId),true,'the resolved request closed its own chooser window');
});

test('derives the bridge beside a prefixed endpoint',()=>{
  assert.equal(bridgeUrl({url:'https://host.test/autofirme/mcp/',token:'t'}),'wss://host.test/autofirme/bridge?token=t');
  assert.equal(bridgeUrl({url:'http://host.test/mcp',token:'t'}),'ws://host.test/bridge?token=t');
});
test('the MCP bridge returns a remembered-PIN signature without opening a chooser',async()=>{
  const env=setup({autoSign:async message=>({signed_b64:message.data})});
  const result=await env.bridge.bridgeSign({format:'cades',certId:'cert-1',data:'YQ==',signingTime:'2026-10-20T12:34:56.000Z'});
  assert.deepEqual(result,{signed_b64:'YQ=='});
  assert.equal(env.state.windows.length,0,'no consent window for an identity whose PIN is remembered');
  assert.equal(env.bridge.coordinator.requests.size,0);
});
test('the MCP bridge authenticates, opens a chooser and returns the finished signature',async()=>{
  const original=globalThis.WebSocket;
  class FakeSocket {
    static instances=[];
    /** @type {(() => void) | null} */ onclose=null;
    /** @type {(() => void) | null} */ onopen=null;
    /** @type {((event: {data: string}) => void) | null} */ onmessage=null;
    constructor(url,protocol){this.url=url;this.protocol=protocol;this.readyState=0;this.sent=[];FakeSocket.instances.push(this);}
    send(raw){this.sent.push(JSON.parse(raw));}
    close(){this.readyState=3;this.onclose?.();}
    open(){this.readyState=1;this.onopen?.();}
    message(value){this.onmessage?.({data:JSON.stringify(value)});}
  }
  globalThis.WebSocket = /** @type {typeof WebSocket} */ (/** @type {unknown} */ (FakeSocket));
  try {
    const env=setup({autoSign:async()=>null});await env.bridge.ready;
    env.chrome.runtime.sendMessage=async()=>{};
    await assert.rejects(env.bridge.handleMessage({type:'mcp:configure',settings:{enabled:true,url:'ftp://mcp.test',token:'t'}},app()),/endpoint/i);
    await assert.rejects(env.bridge.handleMessage({type:'mcp:configure',settings:{enabled:true,url:'https://mcp.test/mcp',token:''}},app()),/token/i);
    const status=await env.bridge.handleMessage({type:'mcp:configure',settings:{enabled:true,url:'https://mcp.test/mcp?stale=1',token:'secret'}},app());
    assert.deepEqual(status,{enabled:true,online:false,error:''});
    const socket=FakeSocket.instances.at(-1);
    assert.equal(socket.url,'wss://mcp.test/bridge?token=secret');
    assert.equal(socket.protocol,'autofirme-bridge.v1');
    socket.open();
    assert.equal((await env.bridge.handleMessage({type:'mcp:status'},app())).online,true);

    const signingTime='2026-10-20T12:34:56.000Z';
    socket.message({type:'sign',id:'job-1',protocol:'autofirme-bridge.v1',format:'cades',certId:'cert-1',data:'YQ==',signingTime});
    await until(()=>[...env.bridge.coordinator.requests.values()][0]?.chooserTabId,'the bridge chooser');
    const item=[...env.bridge.coordinator.requests.values()][0];
    assert.ok(item,'the bridge opened a consent request');
    assert.equal(item.bridge,true);assert.equal(item.origin,'mcp');assert.equal(item.certId,'cert-1');
    assert.equal(item.signingTime,signingTime);
    const view=await env.bridge.handleMessage({type:'request:get',id:item.id},chooser(item));
    assert.equal(view.bridge,true);assert.equal(view.certId,'cert-1');assert.equal(view.signingTime,signingTime);
    assert.equal(view.data,'YQ==');
    await env.bridge.handleMessage({type:'request:begin',id:item.id},chooser(item));
    await env.bridge.handleMessage({type:'request:complete',id:item.id,result:{certificate:'YQ==',signature:'Yg=='}},chooser(item));
    await until(()=>socket.sent.find(message=>message.type==='result'&&message.id==='job-1'),'the bridge result');
    assert.deepEqual(socket.sent.at(-1),{type:'result',id:'job-1',ok:true,value:{signed_b64:'Yg=='}});
    assert.equal(env.bridge.coordinator.requests.size,0);
    assert.ok(env.state.removed.includes(item.windowId),'the chooser closes after the answer');

    const offline=await env.bridge.handleMessage({type:'mcp:configure',settings:{enabled:false,url:'https://mcp.test/mcp',token:'secret'}},app());
    assert.deepEqual(offline,{enabled:false,online:false,error:''});
  } finally {
    globalThis.WebSocket=original;
  }
});
