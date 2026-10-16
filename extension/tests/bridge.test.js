import test from 'node:test';
import assert from 'node:assert/strict';
import {parseCall, validateRequest, publicResult} from '../core/contract.js';
import {wrapAutoScript} from '../core/adapter.js';
import {Coordinator, AtCapacity, MAX_GLOBAL_REQUESTS, trustedApp, trustedChooser} from '../core/coordinator.js';

const sender = (documentId='document-a', frameId=0) => ({id:'extension',url:'https://site.test/form',tab:{id:4},frameId,documentId});
const sign = {operation:'sign',data:'YQ==',algorithm:'SHA256withRSA',format:'CAdES',mode:'implicit'};
const root = 'chrome-extension://extension/';
const ui = id => ({id:'extension',url:`${root}index.html?request=${id}`,tab:{id:15,url:`${root}index.html?request=${id}`},frameId:0});
const flush = () => new Promise(resolve=>setImmediate(resolve));

test('contract limits interception to supported RSA CAdES calls and canonical base64', () => {
  assert.deepEqual(parseCall('sign',['YQ==','SHA256withRSA','CAdES',null,()=>{},()=>{}]),sign);
  for(const [data,algorithm,format,params] of [['YR==','SHA256withRSA','CAdES',''],['YQ==','SHA256withRSA','PAdES',''],['YQ==','SHA1withRSA','CAdES',''],['YQ==','SHA256withRSA','CAdES','mode=implicit\nother=x']]) assert.equal(parseCall('sign',[data,algorithm,format,params,()=>{},()=>{}]),null);
  assert.throws(()=>validateRequest({...sign,origin:'https://spoof.test'}));
  assert.deepEqual(publicResult({certificate:'YQ==',signature:'Yg==',privateKey:'secret'},'sign'),{kind:'result',certificate:'YQ==',signature:'Yg=='});
});

test('adapter preserves original receiver and callbacks and replays fallback exactly once', async () => {
  let calls=0; const receiver={};const args=['YQ==','SHA256withRSA','CAdES','',()=>{},()=>{}];
  const api={sign(...values){calls++;assert.equal(this,receiver);assert.deepEqual(values,args);return 42;}};
  wrapAutoScript(api,()=>Promise.resolve({kind:'fallback'}));
  api.sign.apply(receiver,args); await flush(); assert.equal(calls,1);
});

test('adapter failures never replay and cancellation uses original error contract', async () => {
  let originals=0;const errors=[];const api={selectCertificate(..._args){originals++;}};
  wrapAutoScript(api,()=>Promise.reject(Error('worker lost')));
  api.selectCertificate('',()=>assert.fail(),(...values)=>errors.push(values));await flush();
  assert.equal(originals,0);assert.equal(errors[0][0],'java.lang.Exception');
});

test('capture derives origin and document identity from sender and enforces per-document limits',()=>{
  const c=new Coordinator();
  assert.throws(()=>c.capture({...sender(),documentId:undefined},sign,()=>{}));
  for(let i=0;i<8;i++){const item=c.capture(sender(),sign,()=>{});assert.equal(item.origin,'https://site.test');}
  assert.throws(()=>c.capture(sender(),sign,()=>{}),/pending/i);
  assert.ok(c.capture(sender('document-b'),sign,()=>{}));
});

test('the global in-flight cap rejects with AtCapacity and frees on every terminal path',()=>{
  let now=100;const c=new Coordinator({now:()=>now});
  for(let i=0;i<MAX_GLOBAL_REQUESTS;i++) c.capture(sender(`document-${i}`),sign,r=>r);
  assert.throws(()=>c.capture(sender('overflow'),sign,()=>{}),AtCapacity);
  const [oldest]=c.requests.values();
  c.finish(oldest.id,{kind:'cancel'});
  const freed=c.capture(sender('overflow'),sign,()=>{});
  c.finish(freed.id,{kind:'fallback'});
  const accepted=c.capture(sender('accepted'),sign,r=>r);
  assert.ok(c.start(accepted.id));
  assert.equal(c.requests.size,MAX_GLOBAL_REQUESTS);
  assert.ok(c.finish(accepted.id,{kind:'result'}));
  assert.equal(c.requests.size,MAX_GLOBAL_REQUESTS-1);
  const closed=c.capture(sender('closed'),sign,r=>r);
  assert.equal(c.requests.size,MAX_GLOBAL_REQUESTS);
  c.abandon(closed.id);
  assert.equal(c.requests.size,MAX_GLOBAL_REQUESTS-1);
  now+=300001;
  for(const id of [...c.requests.keys()]) c.get(id);
  assert.equal(c.requests.size,0);
  assert.ok(c.capture(sender('after-expiry'),sign,()=>{}));
});

test('chooser authorization binds exact own URL, request and recorded tab',()=>{
  const c=new Coordinator();const item=c.capture(sender(),sign,()=>{});item.chooserTabId=15;
  assert.equal(trustedChooser(ui(item.id),item,'extension',root),true);
  for(const bad of [{...ui(item.id),tab:{id:16,url:ui(item.id).url}},{...ui(item.id),url:root+'index.html.evil?request='+item.id},{...ui(item.id),url:root+'index.html?request='+item.id+'&x=1'},sender(),{...ui(item.id),frameId:2}])assert.equal(trustedChooser(bad,item,'extension',root),false);
  assert.equal(trustedApp({...ui(item.id),url:root+'index.html',tab:{id:15,url:root+'index.html'}},'extension',root),true);
});

test('requests remain isolated and signing permits only one terminal result',()=>{
  const c=new Coordinator();const resultsA=[],resultsB=[];
  const a=c.capture(sender(),sign,r=>resultsA.push(r));const b=c.capture(sender(),{operation:'selectCertificate'},r=>resultsB.push(r));
  assert.equal(c.finish(a.id,{kind:'result',certificate:'YQ==',signature:'Yg=='}),false);
  assert.equal(c.start(a.id),true);assert.equal(c.start(a.id),false);
  assert.equal(c.finish(a.id,{kind:'fallback'}),false);assert.equal(c.finish(a.id,{kind:'cancel'}),false);
  assert.equal(c.finish(b.id,{kind:'fallback'}),true);assert.equal(c.finish(b.id,{kind:'fallback'}),false);
  assert.equal(c.finish(a.id,{kind:'result',certificate:'YQ==',signature:'Yg=='}),true);
  assert.equal(resultsA.length,1);assert.deepEqual(resultsB,[{kind:'fallback'}]);
});

test('expiry and stale document invalidation reject late results without fallback',()=>{
  let now=100;const c=new Coordinator({now:()=>now}); const results=[];
  const item=c.capture(sender(),sign,r=>results.push(r));now+=300001;
  assert.equal(c.start(item.id),false);assert.equal(c.get(item.id),undefined);assert.deepEqual(results,[{kind:'error'}]);
  const stale=c.capture(sender(),sign,r=>results.push(r));c.invalidateDocument(4,0,'document-new');
  assert.equal(c.get(stale.id),undefined);assert.equal(c.finish(stale.id,{kind:'fallback'}),false);assert.equal(results.at(-1)?.kind,'error');
});

test('main app management survives SPA search changes while both document and tab remain own app',()=>{
  const app={id:'extension',url:root+'index.html',tab:{id:15,url:root+'index.html?tab=verificar'},frameId:0};
  assert.equal(trustedApp(app,'extension',root),true);
  const forbidden=[
    'https://outside.test/index.html',
    'chrome-extension://outside/index.html',
    root+'index.html.evil',
    root+'other.html',
    root+'index.html#section',
  ];
  for(const url of forbidden){
    assert.equal(trustedApp({...app,url},'extension',root),false,url);
    assert.equal(trustedApp({...app,tab:{...app.tab,url}},'extension',root),false,url);
  }
});

test('chooser tab URL must still exactly match its request after main app query relaxation',()=>{
  const c=new Coordinator();const item=c.capture(sender(),sign,()=>{});item.chooserTabId=15;
  for(const url of [root+'index.html',root+'index.html?request='+item.id+'&tab=verificar',root+'index.html?request='+crypto.randomUUID()]){
    assert.equal(trustedChooser({...ui(item.id),tab:{id:15,url}},item,'extension',root),false,url);
  }
});
