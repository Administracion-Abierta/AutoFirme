import test from 'node:test';
import assert from 'node:assert/strict';
import {createBridge} from '../background.js';

const event = () => ({listeners:[],addListener(fn){this.listeners.push(fn);},async emit(...args){return Promise.all(this.listeners.map(fn=>fn(...args)));}});
function browser() {
  const state={enabled:[],granted:new Set(),registered:[],windows:[],removed:[],frame:{documentId:'doc-a',url:'https://site.test/form'}};
  const chrome={runtime:{id:'extension',getURL:path=>'chrome-extension://extension/'+path,onInstalled:event(),onStartup:event(),onMessage:event(),onConnect:event()},
    storage:{local:{async get(){return {enabledOrigins:state.enabled};},async set(v){state.enabled=v.enabledOrigins;},async setAccessLevel(){}}},
    permissions:{async contains({origins}){return origins.every(x=>state.granted.has(x));},onAdded:event(),onRemoved:event()},
    scripting:{async getRegisteredContentScripts(){return state.registered;},async unregisterContentScripts(){state.registered=[];},async registerContentScripts(v){state.registered=v;}},
    windows:{async create(options){const win={id:20+state.windows.length,tabs:[{id:15+state.windows.length}]};state.windows.push({...win,...options});return win;},async remove(id){state.removed.push(id);},onRemoved:event()},
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
  env.state.enabled=['https://site.test'];env.state.granted.add('https://site.test/*');
  const connected=await connect(env.chrome);
  await connected.port.onMessage.emit({id:crypto.randomUUID(),request:{operation:'selectCertificate'}});
  return {...connected,item:[...env.bridge.coordinator.requests.values()][0]};
}
function setup(){const env=browser();env.bridge=createBridge(env.chrome);return env;}

test('host grants alone never activate interception and enabling requires existing grant',async()=>{
  const env=setup();await env.bridge.ready;env.state.granted.add('https://tsa.test/*');
  await env.bridge.updateScripts();assert.deepEqual(env.state.registered,[]);
  await assert.rejects(env.bridge.handleMessage({type:'sites:set',origin:'https://site.test',enabled:true},app()),/permission/i);
  env.state.granted.add('https://site.test/*');
  await env.bridge.handleMessage({type:'sites:set',origin:'https://site.test',enabled:true},app());
  assert.deepEqual(env.state.enabled,['https://site.test']);assert.equal(env.state.registered.length,2);
  for(const script of env.state.registered){assert.deepEqual(script.matches,['https://site.test/*']);assert.equal(script.allFrames,true);assert.equal(script.runAt,'document_start');}
  env.state.granted.delete('https://site.test/*');await env.chrome.permissions.onRemoved.emit({origins:['https://site.test/*']});
  assert.deepEqual(env.state.registered,[]);assert.deepEqual(env.state.enabled,[]);
});

test('request reception checks enabled origin, validates payload and authenticates exact chooser tab',async()=>{
  const env=setup();await env.bridge.ready;
  const unenabled=await connect(env.chrome);await unenabled.port.onMessage.emit({id:crypto.randomUUID(),request:{operation:'selectCertificate'}});assert.equal(unenabled.replies[0].response.kind,'error');
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
  const env=setup();await env.bridge.ready;env.state.enabled=['https://site.test'];env.state.granted.add('https://site.test/*');
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
  assert.deepEqual(env.state.enabled,['https://site.test']);
});
