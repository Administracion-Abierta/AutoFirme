export const REQUEST_TIMEOUT_MS = 5 * 60 * 1000;
export const MAX_DATA_BYTES = 4 * 1024 * 1024;
export const MAX_BASE64 = 4 * Math.ceil(MAX_DATA_BYTES / 3);
export function validBase64(value, max = MAX_BASE64) {
  if(typeof value!=='string' || value.length===0 || value.length>max || value.length%4!==0 || !/^[A-Za-z0-9+/]+={0,2}$/.test(value))return false;
  const padding=value.endsWith('==')?2:value.endsWith('=')?1:0;
  if(padding){const last='ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'.indexOf(value[value.length-padding-1]);if(last & (padding===2?15:3))return false;}
  return true;
}
const SIGN_FORMATS = {CAdES: true, PAdES: true, XAdES: true, AUTO: true};
/** Content sniffing for AutoFirma's AUTO format: PDF → PAdES, XML → XAdES, otherwise CAdES. */
function contentFormat(data) {
  const head = Uint8Array.from(atob(data.slice(0, 16)), (character) => character.charCodeAt(0));
  if (head[0] === 0x25 && head[1] === 0x50 && head[2] === 0x44 && head[3] === 0x46 && head[4] === 0x2d) return 'PAdES';
  let index = head[0] === 0xef && head[1] === 0xbb && head[2] === 0xbf ? 3 : 0;
  while (index < head.length && [0x09, 0x0a, 0x0d, 0x20].includes(head[index])) index++;
  return head[index] === 0x3c ? 'XAdES' : 'CAdES';
}
export function parseCall(method, args) {
  if (method === 'selectCertificate') {
    if (args.length !== 3 || (args[0] != null && args[0] !== '') || typeof args[1] !== 'function' || typeof args[2] !== 'function') return null;
    return {operation: 'selectCertificate'};
  }
  if (method !== 'sign' || args.length !== 6) return null;
  const [data, algorithm, format, params, success, error] = args;
  if (typeof success !== 'function' || typeof error !== 'function' || !validBase64(data) || algorithm !== 'SHA256withRSA' || !Object.hasOwn(SIGN_FORMATS, format)) return null;
  const padding=data.endsWith('==')?2:data.endsWith('=')?1:0;
  if(data.length/4*3-padding>MAX_DATA_BYTES)return null;
  let mode = 'implicit';
  if (params != null && params !== '') {
    if (typeof params !== 'string') return null;
    const match = /^mode=(implicit|explicit)\s*$/.exec(params);
    if (!match) return null;
    mode = match[1];
  }
  // AUTO resolves from the bytes, as AutoFirma does. The XML engine ships enveloped
  // signatures only, so detached (explicit) XAdES requests keep using native AutoFirma.
  const sniffed = contentFormat(data);
  const type = format === 'AUTO' ? sniffed : format;
  if (type === 'XAdES' && (mode !== 'implicit' || sniffed !== 'XAdES')) return null;
  if (type === 'PAdES' && sniffed !== 'PAdES') return null;
  return {operation: 'sign', data, algorithm, format: type, mode};
}
export function validateRequest(value) {
  if (!value || typeof value !== 'object' || Array.isArray(value)) throw Error('Invalid signing request');
  const select = value.operation === 'selectCertificate';
  const keys = select ? ['operation'] : ['operation', 'data', 'algorithm', 'format', 'mode'];
  if (Object.keys(value).some(k => !keys.includes(k)) || Object.keys(value).length !== keys.length) throw Error('Unsupported request fields');
  if (select) return {operation: 'selectCertificate'};
  if (value.operation !== 'sign' || !['explicit', 'implicit'].includes(value.mode)) throw Error('Unsupported operation');
  const normalized = parseCall('sign', [value.data, value.algorithm, value.format, `mode=${value.mode}`, () => {}, () => {}]);
  if (!normalized) throw Error('Unsupported signing parameters');
  return normalized;
}
export function publicResult(value, operation) {
  if(!value || !validBase64(value.certificate,65536) || (operation==='sign' && !validBase64(value.signature,MAX_BASE64+262144)))throw Error('Provider returned an invalid result');
  return {kind:'result',certificate:value.certificate,...(operation==='sign'?{signature:value.signature}:{})};
}
