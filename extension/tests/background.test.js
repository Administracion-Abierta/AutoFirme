import test from 'node:test';
import assert from 'node:assert/strict';
import {createBridge} from '../background.js';

const event = () => ({listeners:[],addListener(fn){this.listeners.push(fn);},async emit(...args){return Promise.all(this.listeners.map(fn=>fn(...args)));}});
function browser() {
  const state={disabled:[],enabled:[],granted:new Set(),registered:[],resized:[],windows:[],removed:[],frame:{documentId:'doc-a',url:'https://site.test/form'}};
  const chrome={runtime:{id:'extension',getURL:path=>'chrome-extension://extension/'+path,onInstalled:event(),onStartup:event(),onMessage:event(),onConnect:event()},
    storage:{local:{async get(){return {enabledOrigins:state.enabled,disabledOrigins:state.disabled};},async set(v){if(v.disabledOrigins)state.disabled=v.disabledOrigins;},async setAccessLevel(){}}},
    permissions:{async getAll(){return {origins:[...state.granted]};},async contains({origins}){return origins.every(x=>state.granted.has(x)||state.granted.has(new URL(x).protocol+'//*/*'));},onAdded:event(),onRemoved:event()},
    scripting:{async getRegisteredContentScripts(){return state.registered;},async unregisterContentScripts(){state.registered=[];},async registerContentScripts(v){state.registered=v;}},
    windows:{async update(id,options){state.resized.push({id,...options});return {id,...options};},async create(options){const win={id:20+state.windows.length,tabs:[{id:15+state.windows.length}]};state.windows.push({...win,...options});return win;},async remove(id){state.removed.push(id);},onRemoved:event()},
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
function setup(){const env=browser();env.bridge=createBridge(env.chrome);return env;}

test('a clean install registers nothing and delegates every website call',async()=>{
  const env=setup();await env.bridge.ready;
  assert.deepEqual(env.state.registered,[]);
  const list=await env.bridge.handleMessage({type:'sites:list'},app());
  assert.equal(list.allSites,false);assert.deepEqual(list.grantedOrigins,[]);
  const connected=await connect(env.chrome);await connected.port.onMessage.emit({id:crypto.randomUUID(),request:{operation:'selectCertificate'}});
  assert.deepEqual(connected.replies.map(reply=>reply.response.kind),['fallback']);
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
  await env.chrome.windows.onRemoved.emit(item.windowId);assert.equal(replies.length,1);assert.equal(replies[0].response.kind,'error');
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
