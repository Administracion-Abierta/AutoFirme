import test from 'node:test';
import assert from 'node:assert/strict';
import {parseCall, validateRequest, publicResult, REQUEST_TIMEOUT_MS, MAX_DATA_BYTES} from '../core/contract.js';
import {wrapAutoScript} from '../core/adapter.js';
import {Coordinator, AtCapacity, MAX_GLOBAL_REQUESTS, trustedApp, trustedChooser} from '../core/coordinator.js';

const sender = (documentId='document-a', frameId=0) => ({id:'extension',url:'https://site.test/form',tab:{id:4},frameId,documentId});
const sign = {operation:'sign',data:'YQ==',algorithm:'SHA256withRSA',format:'CAdES',mode:'implicit'};
const root = 'chrome-extension://extension/';
const ui = id => ({id:'extension',url:`${root}index.html?request=${id}`,tab:{id:15,url:`${root}index.html?request=${id}`},frameId:0});
const flush = () => new Promise(resolve=>setImmediate(resolve));

test('contract accepts supported RSA formats and resolves AUTO from the content', () => {
  const call=(data,algorithm,format,params)=>parseCall('sign',[data,algorithm,format,params,()=>{},()=>{}]);
  assert.deepEqual(call('YQ==','SHA256withRSA','CAdES'),sign);
  const pdf='JVBERi0xLjM=';
  assert.deepEqual(call(pdf,'SHA256withRSA','PAdES','mode=explicit'),{...sign,data:pdf,format:'PAdES',mode:'explicit'});
  assert.deepEqual(call(pdf,'SHA256withRSA','AUTO','mode=explicit'),{...sign,data:pdf,format:'PAdES',mode:'explicit'});
  assert.deepEqual(call('YQ==','SHA256withRSA','AUTO'),sign);
  const xml='PHgvPg==';
  assert.deepEqual(call(xml,'SHA256withRSA','XAdES'),{...sign,data:xml,format:'XAdES'});
  const legacy=call(xml,'SHA1withRSA','XAdES');
  assert.deepEqual(legacy,{...sign,data:xml,algorithm:'SHA1withRSA',format:'XAdES'});
  assert.deepEqual(validateRequest(legacy),legacy);
  assert.deepEqual(call(xml,'SHA256withRSA','AUTO'),{...sign,data:xml,format:'XAdES'});
  for(const [data,algorithm,format,params] of [['YR==','SHA256withRSA','CAdES',''],['YQ==','SHA256withRSA','PAdES',''],['YQ==','SHA1withRSA','CAdES',''],['YQ==','SHA1withRSA','PAdES',''],['PHgvPg==','SHA1withRSA','XAdES','mode=explicit'],['YQ==','SHA256withRSA','CAdES','mode=implicit\nother=x'],['PHgvPg==','SHA256withRSA','XAdES','mode=explicit'],['PHgvPg==','SHA256withRSA','AUTO','mode=explicit'],['YQ==','SHA256withRSA','ASiC',''],['YQ==','SHA256withRSA','constructor','']]) assert.equal(call(data,algorithm,format,params),null);
  assert.throws(()=>validateRequest({...sign,origin:'https://spoof.test'}));
  assert.deepEqual(publicResult({certificate:'YQ==',signature:'Yg==',privateKey:'secret'},'sign'),{kind:'result',certificate:'YQ==',signature:'Yg=='});
});

test('accepts AutoFirma visible-signature parameters for PDF only', () => {
  const call=(data,algorithm,format,params)=>parseCall('sign',[data,algorithm,format,params,()=>{},()=>{}]);
  const pdf='JVBERi0xLjM=';
  const png='iVBORw0KGgo=';
  const stamp=[
    'visibleSignature=want',
    'signaturePositionOnPageLowerLeftX=45',
    'signaturePositionOnPageLowerLeftY=80',
    'signaturePositionOnPageUpperRightX=560',
    'signaturePositionOnPageUpperRightY=140',
    'signaturePage=-1',
    'layer2Text=Firmado por $$SUBJECTCN$$ el $$SIGNDATE=dd/MM/yyyy (HH:mm:ss)$$',
    'layer2FontFamily=1',
    'layer2FontSize=8',
    'layer2FontStyle=2',
    'layer2FontColor=gray',
    'signReason=Conforme',
    'signatureProductionCity=Madrid',
    'signerContact=signer@example.test'
  ].join('\n');
  const expected={...sign,data:pdf,format:'PAdES',params:{
    visibleSignature:'want',
    signaturePositionOnPageLowerLeftX:'45',
    signaturePositionOnPageLowerLeftY:'80',
    signaturePositionOnPageUpperRightX:'560',
    signaturePositionOnPageUpperRightY:'140',
    signaturePage:'-1',
    layer2Text:'Firmado por $$SUBJECTCN$$ el $$SIGNDATE=dd/MM/yyyy (HH:mm:ss)$$',
    layer2FontFamily:'1',
    layer2FontSize:'8',
    layer2FontStyle:'2',
    layer2FontColor:'gray',
    signReason:'Conforme',
    signatureProductionCity:'Madrid',
    signerContact:'signer@example.test'
  }};
  const parsed=call(pdf,'SHA256withRSA','PAdES',stamp);
  assert.deepEqual(parsed,expected);
  assert.deepEqual(validateRequest(parsed),expected);
  assert.equal(/** @type {any} */ (call(pdf,'SHA256withRSA','PAdES',stamp+'\nsignatureRubricImage='+png))?.params?.signatureRubricImage,png);
  const largeImage=Buffer.concat([Buffer.from(png,'base64'),Buffer.alloc(4096)]).toString('base64');
  assert.equal(/** @type {any} */ (call(pdf,'SHA256withRSA','PAdES',stamp+'\nsignatureRubricImage='+largeImage))?.params?.signatureRubricImage,largeImage);
  assert.equal(/** @type {any} */ (call(pdf,'SHA256withRSA','PAdES','signaturePositionOnPageLowerLeftX=1\nsignaturePositionOnPageLowerLeftY=2\nsignaturePositionOnPageUpperRightX=3\nsignaturePositionOnPageUpperRightY=4'))?.params?.signaturePage,'-1');
  for(const params of [
    'visibleSignature=want',
    'signaturePositionOnPageLowerLeftX=1\nsignaturePositionOnPageLowerLeftY=2\nsignaturePositionOnPageUpperRightX=3',
    'signaturePositionOnPageLowerLeftX=10\nsignaturePositionOnPageLowerLeftY=20\nsignaturePositionOnPageUpperRightX=5\nsignaturePositionOnPageUpperRightY=30',
    'signaturePositionOnPageLowerLeftX=1\nsignaturePositionOnPageLowerLeftY=2\nsignaturePositionOnPageUpperRightX=3\nsignaturePositionOnPageUpperRightY=4\nsignaturePage=0',
    'signaturePositionOnPageLowerLeftX=1\nsignaturePositionOnPageLowerLeftY=2\nsignaturePositionOnPageUpperRightX=3\nsignaturePositionOnPageUpperRightY=4\nsignaturePage=1\nsignaturePages=1',
    'signaturePositionOnPageLowerLeftX=1\nsignaturePositionOnPageLowerLeftY=2\nsignaturePositionOnPageUpperRightX=3\nsignaturePositionOnPageUpperRightY=4\nsignatureRotation=90',
    'signaturePositionOnPageLowerLeftX=1\nsignaturePositionOnPageLowerLeftY=2\nsignaturePositionOnPageUpperRightX=3\nsignaturePositionOnPageUpperRightY=4\nlayer2FontFamily=9',
    'signaturePositionOnPageLowerLeftX=1\nsignaturePositionOnPageLowerLeftY=2\nsignaturePositionOnPageUpperRightX=3\nsignaturePositionOnPageUpperRightY=4\nlayer2FontColor=blue',
    'signaturePositionOnPageLowerLeftX=1\nsignaturePositionOnPageLowerLeftY=2\nsignaturePositionOnPageUpperRightX=3\nsignaturePositionOnPageUpperRightY=4\nsignatureRubricImage=not-base64',
  ]) assert.equal(call(pdf,'SHA256withRSA','PAdES',params),null,params);
  assert.equal(call(pdf,'SHA256withRSA','AUTO',stamp)?.format,'PAdES');
  assert.equal(call('YQ==','SHA256withRSA','CAdES',stamp),null);
  assert.equal(call(pdf,'SHA256withRSA','CAdES',stamp),null);
});

test('accepts CARM aliases and tri-phase calls without widening local formats', () => {
  const call=(data,algorithm,format,params)=>parseCall('sign',[data,algorithm,format,params,()=>{},()=>{}]);
  const pdf='JVBERi0xLjM=';
  const carm=call(pdf,'SHA256withRSA','CMS/PKCS#7','mode=implicit\nfilters=nonexpired:\nmimeType=text/xml');
  assert.deepEqual(carm,{operation:'sign',data:pdf,algorithm:'SHA256withRSA',format:'CAdES',mode:'implicit',params:{filters:'nonexpired:',mimeType:'text/xml'}});
  assert.deepEqual(validateRequest(carm),carm);
  const tri=call('YQ==','SHA512withRSA','CAdEStri','serverUrl=https://sign.test/api?x=1\nprecalculatedHashAlgorithm=SHA-512');
  if(!tri) throw new Error('tri-phase call was rejected');
  assert.deepEqual(tri,{operation:'sign',data:'YQ==',algorithm:'SHA512withRSA',format:'CAdES',mode:'implicit',serverUrl:'https://sign.test/api?x=1',params:{precalculatedHashAlgorithm:'SHA-512'}});
  assert.deepEqual(validateRequest(tri),tri);
  for(const [algorithm,format,params] of [
    ['SHA512withRSA','CAdEStri','precalculatedHashAlgorithm=SHA-512'],
    ['SHA1withRSA','CAdEStri','serverUrl=https://sign.test'],
    ['SHA512withRSA','CAdEStri','serverUrl=https://sign.test\nprecalculatedHashAlgorithm=SHA-256'],
    ['SHA512withRSA','CAdEStri','serverUrl=javascript:alert(1)'],
    ['SHA512withRSA','CAdEStri','serverUrl=https://user:pass@sign.test'],
    ['SHA512withRSA','CAdEStri','serverUrl=https://sign.test\nfoo=bar'],
    ['SHA512withRSA','CAdEStri','serverUrl=https://sign.test\nserverUrl=https://evil.test'],
    ['SHA256withRSA','CAdES','serverUrl=https://sign.test'],
  ]) assert.equal(call('YQ==',algorithm,format,params),null,`${format} ${params}`);
  assert.throws(()=>validateRequest({...tri,format:'PAdES'}),/Unsupported/);
  assert.throws(()=>validateRequest({...tri,serverUrl:'javascript:alert(1)'}),/Unsupported/);
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

test('transport errors and cancellations reach the page in the portal error shape', async () => {
  const errors = [];
  const api = { sign(..._args) { void _args; }, selectCertificate(..._args) { void _args; } };
  wrapAutoScript(api, async () => ({ kind: 'error' }));
  api.sign('YQ==', 'SHA256withRSA', 'CAdES', '', () => assert.fail(), (...values) => errors.push(values));
  api.selectCertificate('', () => assert.fail(), (...values) => errors.push(values));
  await flush();
  assert.equal(errors.length, 2);
  for (const [type, message] of errors) {
    assert.equal(type, 'java.lang.Exception');
    assert.match(message, /AutoFirme/);
  }
  const cancels = [];
  const cancelApi = { sign(..._args) { void _args; }, selectCertificate(..._args) { void _args; } };
  wrapAutoScript(cancelApi, async () => ({ kind: 'cancel' }));
  cancelApi.sign('YQ==', 'SHA256withRSA', 'CAdES', '', () => assert.fail(), (...values) => cancels.push(values));
  cancelApi.selectCertificate('', () => assert.fail(), (...values) => cancels.push(values));
  await flush();
  assert.deepEqual(cancels, [
    ['es.gob.afirma.core.AOCancelledOperationException', 'Operation cancelled'],
    ['es.gob.afirma.core.AOCancelledOperationException', 'Operation cancelled'],
  ]);
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
  assert.equal(c.finish(a.id,{kind:'fallback'}),false);
  assert.equal(c.finish(b.id,{kind:'fallback'}),true);assert.equal(c.finish(b.id,{kind:'fallback'}),false);
  assert.equal(c.finish(a.id,{kind:'result',certificate:'YQ==',signature:'Yg=='}),true);
  // Cancel stays valid from signing (reloaded-chooser exit); fallback stays forbidden there.
  const d=c.capture(sender(),{operation:'selectCertificate'},r=>resultsB.push(r));
  assert.equal(c.start(d.id),true);
  assert.equal(c.finish(d.id,{kind:'fallback'}),false);
  assert.equal(c.finish(d.id,{kind:'cancel'}),true);
  assert.equal(c.finish(d.id,{kind:'result',certificate:'YQ=='}),false);
  assert.equal(resultsA.length,1);assert.deepEqual(resultsB,[{kind:'fallback'},{kind:'cancel'}]);
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

test('sticky signatory or custom keystore disables interception exactly once and restores it',async()=>{
  let transport=0;const originals=[];const delivered=[];
  const failToBridge=()=>assert.fail('bridge must not touch a bypassed call');
  const api={
    setStickySignatory(v){originals.push(['sticky',v]);return 'native';},
    setKeyStore(v){originals.push(['store',v]);return 'native';},
    sign(...a){originals.push(['sign',a]);return 'native';},
    selectCertificate(...a){originals.push(['select',a]);return 'native';},
  };
  wrapAutoScript(api,()=>{transport++;return Promise.resolve({kind:'cancel'});});
  const signArgs=()=>['YQ==','SHA256withRSA','CAdES','',failToBridge,(...r)=>delivered.push(r)];
  const selectArgs=()=>['',failToBridge,(...r)=>delivered.push(r)];
  assert.equal(api.setStickySignatory(12),'native');
  assert.equal(api.sign(...signArgs()),'native');
  assert.equal(api.selectCertificate(...selectArgs()),'native');
  assert.equal(transport,0,'sticky requests never reach the bridge, not even postMessage');
  assert.deepEqual(originals.map(entry=>entry[0]),['sticky','sign','select']);
  await flush();assert.deepEqual(delivered,[],'no bridge callback fires while bypassed');
  assert.equal(api.setStickySignatory(null),'native');
  assert.equal(api.sign(...signArgs()),undefined);
  await flush();
  assert.equal(transport,1,'interception returns after clearing sticky');
  assert.equal(delivered[0][0],'es.gob.afirma.core.AOCancelledOperationException');
  assert.equal(api.setKeyStore(3),'native');
  assert.equal(api.sign(...signArgs()),'native');
  assert.equal(transport,1,'a custom keystore bypasses interception');
  assert.equal(api.setKeyStore(null),'native');
  assert.equal(api.selectCertificate(...selectArgs()),undefined);
  await flush();
  assert.equal(transport,2,'interception returns after clearing the keystore');
  assert.equal(originals.filter(entry=>entry[0]==='sign').length,2,'the original signs only for the two bypassed calls');
});

test('the 4 MiB payload ceiling is enforced at exactly 4 MiB plus one byte',()=>{
  const exact=Buffer.alloc(MAX_DATA_BYTES,97).toString('base64');
  assert.equal(Buffer.from(exact,'base64').length,MAX_DATA_BYTES,'fixture decodes to exactly 4 MiB');
  assert.ok(parseCall('sign',[exact,'SHA256withRSA','CAdES','',()=>{},()=>{}]),'4 MiB exactly is still intercepted');
  const over=Buffer.alloc(MAX_DATA_BYTES+1,97).toString('base64');
  assert.equal(parseCall('sign',[over,'SHA256withRSA','CAdES','',()=>{},()=>{}]),null);
  assert.throws(()=>validateRequest({operation:'sign',data:over,algorithm:'SHA256withRSA',format:'CAdES',mode:'implicit'}),/Unsupported/);
});

test('publicResult rejects non-canonical, oversized, and signature-less provider results',()=>{
  assert.deepEqual(publicResult({certificate:'YQ==',signature:'Yg=='},'sign'),{kind:'result',certificate:'YQ==',signature:'Yg=='});
  assert.throws(()=>publicResult({certificate:'not base64!',signature:'Yg=='},'sign'),/invalid result/);
  assert.throws(()=>publicResult({certificate:'A'.repeat(65540),signature:'Yg=='},'sign'),/invalid result/,'certificates are capped at 64 KiB of base64');
  assert.throws(()=>publicResult({certificate:'YR==',signature:'Yg=='},'sign'),/invalid result/,'canonical padding only');
  assert.throws(()=>publicResult({certificate:'YQ=='},'sign'),/invalid result/,'sign without a signature is never a result');
  assert.deepEqual(publicResult({certificate:'YQ=='},'selectCertificate'),{kind:'result',certificate:'YQ=='});
});

test('coordinator deadline and contract expose one shared 5-minute timeout',()=>{
  assert.equal(REQUEST_TIMEOUT_MS,5*60*1000);
  let now=1000;const c=new Coordinator({now:()=>now});
  const item=c.capture(sender(),{operation:'selectCertificate'},()=>{});
  assert.equal(item.deadline-now,REQUEST_TIMEOUT_MS,'page timer must never be shorter than this deadline (asserted in transport.test.js)');
});
