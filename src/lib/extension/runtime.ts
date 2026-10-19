/** Chrome is read only after the origin gate; importing this module is safe in the web build. */
export interface ExtensionRequest {
 id: string;
 origin: string;
 operation: 'sign' | 'selectCertificate';
 format?: string;
 mode?: 'explicit' | 'implicit';
 algorithm?: string;
 serverUrl?: string;
 params?: Record<string, string>;
 data?: string;
 digest?: string;
 /** Set when an MCP agent (not a website) requested the signature. */
 bridge?: boolean;
 /** Identity the agent asked for; the chooser preselects it. */
 certId?: string | null;
 /** Agent-supplied ISO signing time, so the MCP response matches the signed attribute. */
 signingTime?: string;
}
export interface SignatureResult { certificate: string; signature?: string }
export type Message = { type: string; [key: string]: unknown };
export type Rpc = (message: Message) => Promise<unknown>;
interface ChromeApi {
 runtime: { sendMessage(message: Message): Promise<{ ok: boolean; value?: unknown; error?: string }> };
 permissions: { request(grant: { origins: string[] }): Promise<boolean>; remove(grant: { origins: string[] }): Promise<boolean> };
}
export function isExtension(locationLike: Pick<Location, 'protocol'> | undefined = typeof location === 'object' ? location : undefined): boolean {
 return locationLike?.protocol === 'chrome-extension:';
}
function api(): ChromeApi {
 if (!isExtension()) throw new Error('Extension context required');
 const value = (globalThis as typeof globalThis & { chrome?: ChromeApi }).chrome;
 if (!value?.runtime) throw new Error('Extension runtime unavailable');
 return value;
}
export async function rpc<T = unknown>(message: Message): Promise<T> {
 const response = await api().runtime.sendMessage(message);
 if (!response?.ok) throw new Error(response?.error || 'Extension request failed');
 return response.value as T;
}
export function exactOrigin(input: string): string {
 const url = new URL(input);
 if (!['https:', 'http:'].includes(url.protocol) || url.username || url.password) throw new Error('Use an HTTP or HTTPS origin');
 return url.origin;
}
export function requestOriginPermission(input: string): Promise<boolean> {
 return api().permissions.request({ origins: [`${exactOrigin(input)}/*`] });
}
