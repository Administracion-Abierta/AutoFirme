import test from 'node:test';
import assert from 'node:assert/strict';
import {installPage} from '../page.js';
import {installContent} from '../content.js';

function win() {
  const listeners=new Map();const sent=[];const intervals=new Map();let sequence=0;
  return {location:{origin:'https://site.test'},sent,intervals,addEventListener(name,fn,options={}){const handlers=listeners.get(name)||[];handlers.push({fn,once:options.once});listeners.set(name,handlers);},
    emit(name,event){for(const handler of [...listeners.get(name)||[]]){if(handler.once)listeners.set(name,listeners.get(name).filter(value=>value!==handler));handler.fn(event);}},postMessage(message,origin){sent.push({message,origin});},setInterval(fn){const id=++sequence;intervals.set(id,fn);return id;},clearInterval(id){intervals.delete(id);},setTimeout(){return 2;},clearTimeout(){}};
}
const event=()=>({listeners:[],addListener(fn){this.listeners.push(fn);},emit(value){this.listeners.forEach(fn=>fn(value));}});

test('isolated transport checks source and origin; disconnection reports error once without fallback',()=>{
  const window=win();const port={onMessage:event(),onDisconnect:event(),messages:[],postMessage(m){this.messages.push(m);},disconnect(){this.disconnected=true;}};
  installContent(window,{runtime:{connect:()=>port}});const id=crypto.randomUUID();const data={channel:'autofirme-v1-request',id,request:{operation:'selectCertificate'}};
  window.emit('message',{source:{},origin:window.location.origin,data});window.emit('message',{source:window,origin:'https://other.test',data});assert.equal(port.messages.length,0);
  window.emit('message',{source:window,origin:window.location.origin,data});window.emit('message',{source:window,origin:window.location.origin,data});assert.equal(port.messages.length,1);
  port.onDisconnect.emit();port.onMessage.emit({id,response:{kind:'fallback'}});assert.deepEqual(window.sent.map(m=>m.message.response),[{kind:'error'}]);
});

test('page results settle only matching request once and preserve callback arguments',async()=>{
  const window=win();const values=[];window.AutoScript={sign(){assert.fail('Original should not run');}};
  installPage(window);window.AutoScript.sign('YQ==','SHA256withRSA','CAdES','',(...args)=>values.push(args),()=>assert.fail());
  await Promise.resolve();const id=window.sent[0].message.id;
  const data={channel:'autofirme-v1-result',id,response:{kind:'result',signature:'Yg==',certificate:'YQ=='}};
  window.emit('message',{source:window,origin:'https://other.test',data});assert.equal(values.length,0);
  window.emit('message',{source:window,origin:window.location.origin,data});window.emit('message',{source:window,origin:window.location.origin,data});
  await new Promise(resolve=>setImmediate(resolve));assert.deepEqual(values,[['Yg==','YQ==',null]]);
});


test('restored cached documents reconnect for new calls and never replay old pending calls',()=>{
  const window=win();const ports=[];
  installContent(window,{runtime:{connect(){const port={onMessage:event(),onDisconnect:event(),messages:[],postMessage(m){if(this.disconnected)throw Error('Disconnected port');this.messages.push(m);},disconnect(){this.disconnected=true;}};ports.push(port);return port;}}});
  const request=()=>{const id=crypto.randomUUID();window.emit('message',{source:window,origin:window.location.origin,data:{channel:'autofirme-v1-request',id,request:{operation:'selectCertificate'}}});return id;};
  const first=request();window.emit('pagehide',{});window.emit('pageshow',{persisted:true});
  const second=request();assert.equal(ports.length,2);assert.equal(ports[1].messages[0].id,second);
  ports[0].onMessage.emit({id:first,response:{kind:'fallback'}});ports[0].onDisconnect.emit();
  assert.deepEqual(window.sent.map(value=>value.message),[{channel:'autofirme-v1-result',id:first,response:{kind:'error'}}]);
  window.emit('pagehide',{});window.emit('pageshow',{persisted:true});
  const third=request();assert.equal(ports.length,3);assert.equal(ports[2].messages[0].id,third);
  assert.deepEqual(window.sent.map(value=>value.message.response.kind),['error','error']);
});

test('adapter polling resumes after each cached-document restoration',async()=>{
  const window=win();Object.defineProperty(window,'AutoScript',{value:{selectCertificate(){}},configurable:false,writable:true});
  installPage(window);assert.equal(window.intervals.size,1);
  for(let i=0;i<2;i++){
    window.emit('pagehide',{});assert.equal(window.intervals.size,0);
    window.AutoScript={selectCertificate(){assert.fail('Restored call should still be intercepted');}};
    window.emit('pageshow',{persisted:true});assert.equal(window.intervals.size,1);
    for(const poll of window.intervals.values())poll();
    window.AutoScript.selectCertificate('',()=>{},()=>{});await Promise.resolve();
  }
  assert.equal(window.sent.length,2);
});
