import test from 'node:test';
import assert from 'node:assert/strict';
import {installPage} from '../page.js';
import {installContent} from '../content.js';

function win() {
  const listeners=new Map();const sent=[];
  return {location:{origin:'https://site.test'},sent,addEventListener(name,fn){const handlers=listeners.get(name)||[];handlers.push(fn);listeners.set(name,handlers);},
    emit(name,event){for(const fn of listeners.get(name)||[])fn(event);},postMessage(message,origin){sent.push({message,origin});},setInterval(){return 1;},clearInterval(){},setTimeout(){return 2;},clearTimeout(){}};
}
const event=()=>({listeners:[],addListener(fn){this.listeners.push(fn);},emit(value){this.listeners.forEach(fn=>fn(value));}});

test('isolated transport checks source and origin; disconnection reports error once without fallback',()=>{
  const window=win();const port={onMessage:event(),onDisconnect:event(),messages:[],postMessage(m){this.messages.push(m);},disconnect(){this.onDisconnect.emit();}};
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
