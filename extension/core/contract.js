export const REQUEST_TIMEOUT_MS = 5 * 60 * 1000;
export const MAX_DATA_BYTES = 4 * 1024 * 1024;
export const MAX_BASE64 = 4 * Math.ceil(MAX_DATA_BYTES / 3);
const MAX_PARAMS = 4096;
const SIGN_FORMATS = {CAdES: true, PAdES: true, XAdES: true, AUTO: true};
const FORMAT_ALIASES = {'CMS/PKCS#7': 'CAdES'};
// Formats the local engines cannot build; the page's server performs the structure and
// the extension only contributes the PKCS#1 signature.
const TRIPHASE_ALGORITHMS = {SHA256withRSA: 'SHA-256', SHA512withRSA: 'SHA-512'};
const MIME_TYPE = /^[A-Za-z0-9!#$&^_.+-]+\/[A-Za-z0-9!#$&^_.+-]+$/;
export function validBase64(value, max = MAX_BASE64) {
  if(typeof value!=='string' || value.length===0 || value.length>max || value.length%4!==0 || !/^[A-Za-z0-9+/]+={0,2}$/.test(value))return false;
  const padding=value.endsWith('==')?2:value.endsWith('=')?1:0;
  if(padding){const last='ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'.indexOf(value[value.length-padding-1]);if(last & (padding===2?15:3))return false;}
  return true;
}
/** Content sniffing for AutoFirma's AUTO format: PDF → PAdES, XML → XAdES, otherwise CAdES. */
function contentFormat(data) {
  const head = Uint8Array.from(atob(data.slice(0, 16)), (character) => character.charCodeAt(0));
  if (head[0] === 0x25 && head[1] === 0x50 && head[2] === 0x44 && head[3] === 0x46 && head[4] === 0x2d) return 'PAdES';
  let index = head[0] === 0xef && head[1] === 0xbb && head[2] === 0xbf ? 3 : 0;
  while (index < head.length && [0x09, 0x0a, 0x0d, 0x20].includes(head[index])) index++;
  return head[index] === 0x3c ? 'XAdES' : 'CAdES';
}
/** `key=value` lines, as AutoFirma's signing parameters. Duplicates and junk are refused. */
function parseParams(params) {
  if (params == null || params === '') return [];
  if (typeof params !== 'string' || params.length > MAX_PARAMS) return null;
  const entries = [];
  const seen = new Set();
  for (const line of params.split(/\r?\n/)) {
    if (line === '') continue;
    const match = /^([A-Za-z][A-Za-z0-9]*)=(.*)$/.exec(line);
    if (!match || seen.has(match[1])) return null;
    seen.add(match[1]);
    entries.push([match[1], match[2]]);
  }
  return entries;
}
function serverUrl(value) {
  if (typeof value !== 'string' || value.length > 2048) return null;
  try {
    const url = new URL(value);
    return (url.protocol === 'https:' || url.protocol === 'http:') && !url.username && !url.password && !url.hash ? value : null;
  } catch { return null; }
}
/**
 * Local formats accept only what their engines honour. CARM passes mode, filters and
 * mimeType; anything else falls back to native AutoFirma rather than being ignored.
 */
function localOptions(entries) {
  const options = {mode: 'implicit', params: {}};
  for (const [key, value] of entries) {
    if (key === 'mode') {
      if (value !== 'implicit' && value !== 'explicit') return null;
      options.mode = value;
    } else if (key === 'filters') {
      if (value !== 'nonexpired:') return null;
      options.params.filters = value;
    } else if (key === 'mimeType') {
      if (value.length > 128 || !MIME_TYPE.test(value)) return null;
      options.params.mimeType = value;
    } else return null;
  }
  return options;
}
function triOptions(entries, algorithm) {
  const options = {mode: 'implicit', params: {}};
  /** @type {string | null} */
  let server = null;
  for (const [key, value] of entries) {
    if (key === 'serverUrl') {
      server = serverUrl(value);
      if (!server) return null;
    } else if (key === 'precalculatedHashAlgorithm') {
      if (value !== TRIPHASE_ALGORITHMS[algorithm]) return null;
      options.params[key] = value;
    } else if (key === 'mode') {
      if (value !== 'implicit' && value !== 'explicit') return null;
      options.mode = value;
      options.params[key] = value;
    } else if (key === 'filters') {
      if (value !== 'nonexpired:') return null;
      options.params[key] = value;
    } else if (key === 'mimeType') {
      if (value.length > 128 || !MIME_TYPE.test(value)) return null;
      options.params[key] = value;
    } else if (key === 'includeOnlySignningCertificate') {
      if (value !== 'true' && value !== 'false') return null;
      options.params[key] = value;
    } else return null;
  }
  return server ? {...options, serverUrl: server} : null;
}
export function parseCall(method, args) {
  if (method === 'selectCertificate') {
    if (args.length !== 3 || (args[0] != null && args[0] !== '') || typeof args[1] !== 'function' || typeof args[2] !== 'function') return null;
    return {operation: 'selectCertificate'};
  }
  if (method !== 'sign' || args.length !== 6) return null;
  const [data, algorithm, rawFormat, params, success, error] = args;
  if (typeof success !== 'function' || typeof error !== 'function' || !validBase64(data)) return null;
  const padding=data.endsWith('==')?2:data.endsWith('=')?1:0;
  if(data.length/4*3-padding>MAX_DATA_BYTES)return null;
  const entries = parseParams(params);
  if (!entries) return null;

  if (rawFormat === 'CAdEStri') {
    if (!Object.hasOwn(TRIPHASE_ALGORITHMS, algorithm)) return null;
    const options = triOptions(entries, algorithm);
    if (!options) return null;
    return {operation: 'sign', data, algorithm, format: 'CAdES', mode: options.mode, serverUrl: options.serverUrl, params: options.params};
  }

  const format = Object.hasOwn(FORMAT_ALIASES, rawFormat) ? FORMAT_ALIASES[rawFormat] : rawFormat;
  if (algorithm !== 'SHA256withRSA' || !Object.hasOwn(SIGN_FORMATS, format)) return null;
  const options = localOptions(entries);
  if (!options) return null;
  // AUTO resolves from the bytes, as AutoFirma does. The XML engine ships enveloped
  // signatures only, so detached (explicit) XAdES requests keep using native AutoFirma.
  const sniffed = contentFormat(data);
  const type = format === 'AUTO' ? sniffed : format;
  if (type === 'XAdES' && (options.mode !== 'implicit' || sniffed !== 'XAdES')) return null;
  if (type === 'PAdES' && sniffed !== 'PAdES') return null;
  return {
    operation: 'sign', data, algorithm, format: type, mode: options.mode,
    ...(Object.keys(options.params).length ? {params: options.params} : {})
  };
}
export function validateRequest(value) {
  if (!value || typeof value !== 'object' || Array.isArray(value)) throw Error('Invalid signing request');
  const select = value.operation === 'selectCertificate';
  const keys = select ? ['operation'] : ['operation', 'data', 'algorithm', 'format', 'mode', ...(Object.hasOwn(value, 'params') ? ['params'] : []), ...(Object.hasOwn(value, 'serverUrl') ? ['serverUrl'] : [])];
  if (Object.keys(value).some(k => !keys.includes(k)) || Object.keys(value).length !== keys.length) throw Error('Unsupported request fields');
  if (select) return {operation: 'selectCertificate'};
  if (value.operation !== 'sign' || !['explicit', 'implicit'].includes(value.mode)) throw Error('Unsupported operation');
  if (value.serverUrl !== undefined && value.format !== 'CAdES') throw Error('Unsupported signing parameters');
  if (value.params !== undefined && (!value.params || typeof value.params !== 'object' || Array.isArray(value.params) || Object.keys(value.params).length > 8)) throw Error('Unsupported signing parameters');
  if (value.serverUrl !== undefined && typeof value.serverUrl !== 'string') throw Error('Unsupported signing parameters');
  const lines = value.serverUrl !== undefined ? [`serverUrl=${value.serverUrl}`] : [`mode=${value.mode}`];
  for (const [key, parameter] of Object.entries(value.params ?? {})) {
    if (typeof parameter !== 'string' || parameter.length > 2048 || /[\r\n]/.test(parameter)) throw Error('Unsupported signing parameters');
    lines.push(`${key}=${parameter}`);
  }
  const normalized = parseCall('sign', [value.data, value.algorithm, value.serverUrl !== undefined ? 'CAdEStri' : value.format, lines.join('\n'), () => {}, () => {}]);
  if (!normalized || normalized.mode !== value.mode) throw Error('Unsupported signing parameters');
  return normalized;
}
export function publicResult(value, operation) {
  if(!value || !validBase64(value.certificate,65536) || (operation==='sign' && !validBase64(value.signature,MAX_BASE64+262144)))throw Error('Provider returned an invalid result');
  return {kind:'result',certificate:value.certificate,...(operation==='sign'?{signature:value.signature}:{})};
}
