import {validateRequest, REQUEST_TIMEOUT_MS} from './contract.js';
import {RequestState} from './lifecycle.js';

export const MAX_DOCUMENT_REQUESTS = 8;
export const MAX_GLOBAL_REQUESTS = 16;
/** In-flight capacity is exhausted; the website must fall back to the original AutoFirma. */
export class AtCapacity extends Error {}
export function normalizeOrigin(value) {
  if (typeof value !== 'string') throw Error('Invalid website origin');
  const url = new URL(value);
  if (!['http:', 'https:'].includes(url.protocol) || url.origin !== value) throw Error('Invalid website origin');
  return url.origin;
}
export function trustedApp(sender, extensionId, root) {
  if (sender?.id !== extensionId || !Number.isInteger(sender.tab?.id) || sender.frameId !== 0) return false;
  try {
    const appHost = new URL(root).host;
    // Chrome preserves the document's initial sender URL across SPA history changes.
    // Authenticate both app locations independently; chooser queries stay exact below.
    return [sender.url,sender.tab.url].every(value => {
      const url = new URL(value);
      return url.protocol === 'chrome-extension:' && url.host === appHost &&
        url.pathname === '/index.html' && !url.hash;
    });
  } catch { return false; }
}
export function trustedChooser(sender, item, extensionId, root) {
  return trustedApp(sender, extensionId, root) && sender.tab.id === item.chooserTabId &&
    sender.url === `${root}index.html?request=${item.id}` && sender.tab.url === sender.url;
}
export class Coordinator {
  requests = new Map();
  constructor({now = Date.now, uuid = () => crypto.randomUUID()} = {}) { this.now = now; this.uuid = uuid; }
  capture(sender, value, deliver) {
    if (!Number.isInteger(sender.tab?.id) || !Number.isInteger(sender.frameId) ||
        typeof sender.documentId !== 'string' || !sender.documentId) throw Error('Cannot identify requesting document');
    const url = new URL(sender.url);
    if (!['https:', 'http:'].includes(url.protocol) || url.origin === 'null') throw Error('Unsupported origin');
    return this.enqueue(value, url.origin, deliver, {tabId:sender.tab.id,frameId:sender.frameId,documentId:sender.documentId});
  }
  /** An MCP agent asks through the bridge: same consent window, no website document. */
  captureBridge(value, certId, deliver) {
    const item = this.enqueue(value, 'mcp', deliver, {tabId:null,frameId:null,documentId:null});
    item.bridge = true;
    item.certId = typeof certId === 'string' && certId ? certId : null;
    return item;
  }
  enqueue(value, origin, deliver, {tabId,frameId,documentId}) {
    const request = Object.freeze(validateRequest(value));
    if (this.requests.size >= MAX_GLOBAL_REQUESTS) throw new AtCapacity('Too many pending requests across the extension');
    let count = 0;
    for (const item of this.requests.values()) {
      if (tabId !== null && item.tabId === tabId && item.frameId === frameId && item.documentId === documentId && this.get(item.id)) count++;
    }
    if (count >= MAX_DOCUMENT_REQUESTS) throw Error('Too many pending requests for this document');
    const item = {id:this.uuid(),request,origin,tabId,frameId,documentId,lifecycle:new RequestState(),deliver,
      bridge:false,
      /** @type {string|null} */
      certId:null,
      windowId:/** @type {number|null} */ (null),chooserTabId:/** @type {number|null} */ (null),
      deadline:this.now() + REQUEST_TIMEOUT_MS};
    this.requests.set(item.id, item);
    return item;
  }
  get(id) {
    const item = this.requests.get(id);
    if (item && item.deadline <= this.now()) { this.finish(id, {kind:'error'}); return undefined; }
    return item;
  }
  start(id) { return this.get(id)?.lifecycle.start() ?? false; }
  finish(id, result) {
    const item = this.requests.get(id);
    if (!item || !item.lifecycle.finish(result.kind)) return false;
    this.requests.delete(id);
    try { item.deliver(result); } catch { /* Disconnected document: never replay. */ }
    return true;
  }
  abandon(id) { this.requests.delete(id); }
  invalidateDocument(tabId, frameId, documentId) {
    for (const item of this.requests.values()) {
      if (item.tabId === tabId && (frameId === 0 || item.frameId === frameId) && item.documentId !== documentId) this.finish(item.id, {kind:'error'});
    }
  }
}
