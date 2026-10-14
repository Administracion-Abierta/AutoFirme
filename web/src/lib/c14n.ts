/**
 * XML Canonicalization 1.0 (http://www.w3.org/TR/2001/REC-xml-c14n-20010315) and Exclusive
 * Canonicalization (http://www.w3.org/2001/10/xml-exc-c14n#), without comments.
 *
 * Signature engines digest canonicalized octets, so this must byte-match what verifiers
 * (xmlsec1, LibreOffice, Java XMLDSig) compute. Kept dependency-free: the browser has no c14n.
 */

export const C14N_INCLUSIVE = 'http://www.w3.org/TR/2001/REC-xml-c14n-20010315';
export const C14N_EXCLUSIVE = 'http://www.w3.org/2001/10/xml-exc-c14n#';
export const XMLNS_NS = 'http://www.w3.org/2000/xmlns/';
export const XML_NS = 'http://www.w3.org/XML/1998/namespace';

const ENCODER = new TextEncoder();

function escapeText(value: string): string {
	return value.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/\r/g, '&#xD;');
}

function escapeAttribute(value: string): string {
	return value
		.replace(/&/g, '&amp;')
		.replace(/</g, '&lt;')
		.replace(/"/g, '&quot;')
		.replace(/\t/g, '&#x9;')
		.replace(/\n/g, '&#xA;')
		.replace(/\r/g, '&#xD;');
}

/** Sorted list of `xmlns` declarations carried by a single element. */
function declaredNamespaces(el: Element): Array<[string, string]> {
	const out: Array<[string, string]> = [];
	for (const attr of Array.from(el.attributes)) {
		if (attr.namespaceURI === XMLNS_NS) out.push([attr.prefix === 'xmlns' ? attr.localName : '', attr.value]);
		else if (attr.name === 'xmlns') out.push(['', attr.value]);
	}
	return out;
}

/** Every namespace in scope at `el`, from the document root down to `el` itself. */
function inScopeNamespaces(el: Element): Map<string, string> {
	const chain: Element[] = [];
	for (let node: Element | null = el; node; node = node.parentElement) chain.unshift(node);
	const scope = new Map<string, string>();
	for (const node of chain) for (const [prefix, uri] of declaredNamespaces(node)) scope.set(prefix, uri);
	return scope;
}

function compareNames(a: string, b: string): number {
	return a < b ? -1 : a > b ? 1 : 0;
}

interface AttributeRef {
	ns: string;
	local: string;
	prefix: string;
	value: string;
}

function elementAttributes(el: Element): AttributeRef[] {
	const attrs: AttributeRef[] = [];
	for (const attr of Array.from(el.attributes)) {
		if (attr.name === 'xmlns' || attr.namespaceURI === XMLNS_NS) continue;
		attrs.push({ ns: attr.namespaceURI ?? '', local: attr.localName ?? attr.name, prefix: attr.prefix ?? '', value: attr.value });
	}
	return attrs.sort((a, b) => compareNames(a.ns, b.ns) || compareNames(a.local, b.local));
}

/**
 * Canonicalize `node` (an Element, or a Document to canonicalize its whole content).
 * `exclusive` renders only visibly-utilized namespace declarations.
 */
export function canonicalize(node: Node, exclusive = false): string {
	const out: string[] = [];
	const rendered = new Map<string, string>();
	render(node, out, rendered, exclusive);
	return out.join('');
}

export function canonicalizeBytes(node: Node, exclusive = false): Uint8Array {
	return ENCODER.encode(canonicalize(node, exclusive));
}

/**
 * Namespace declarations to emit for `el`, in canonical order (default namespace first).
 * Inclusive canonicalization emits every declaration not already rendered by an ancestor;
 * exclusive emits only the ones the element visibly utilizes.
 */
function namespacesToRender(el: Element, rendered: Map<string, string>, exclusive: boolean): Array<[string, string]> {
	const attrs = elementAttributes(el);
	const utilized = (prefix: string) =>
		prefix === ''
			? !el.prefix && el.namespaceURI !== null
			: el.prefix === prefix || attrs.some((a) => a.prefix === prefix && a.ns !== '');
	const pending: Array<[string, string]> = [];
	for (const [prefix, uri] of inScopeNamespaces(el)) {
		if (rendered.get(prefix) === uri) continue;
		if (exclusive && !utilized(prefix)) {
			// exc-c14n still renders xmlns="" when an unprefixed element carries no namespace
			// while a non-empty default namespace is in the output: skipping it would change meaning.
			const unsetDefault = prefix === '' && uri === '' && !el.prefix && !!rendered.get('');
			if (!unsetDefault) continue;
		}
		pending.push([prefix, uri]);
	}
	return pending.sort((a, b) => compareNames(a[0], b[0]));
}

function render(node: Node, out: string[], rendered: Map<string, string>, exclusive: boolean): void {
	switch (node.nodeType) {
		case 1: {
			const el = node as Element;
			const attrs = elementAttributes(el);
			const ns = namespacesToRender(el, rendered, exclusive);
			const nextRendered = new Map(rendered);
			out.push('<', el.prefix ? `${el.prefix}:${el.localName}` : el.localName);
			for (const [prefix, uri] of ns) {
				out.push(prefix ? ` xmlns:${prefix}="${escapeAttribute(uri)}"` : ` xmlns="${escapeAttribute(uri)}"`);
				nextRendered.set(prefix, uri);
			}
			for (const attr of attrs) {
				const name = attr.prefix ? `${attr.prefix}:${attr.local}` : attr.local;
				out.push(` ${name}="${escapeAttribute(attr.value)}"`);
			}
			const children = Array.from(el.childNodes).filter((child) => child.nodeType !== 8);
			out.push('>');
			for (const child of children) render(child, out, nextRendered, exclusive);
			out.push('</', el.prefix ? `${el.prefix}:${el.localName}` : el.localName, '>');
			return;
		}
		case 3:
		case 4:
			out.push(escapeText(node.nodeValue ?? ''));
			return;
		case 7: {
			const pi = node as ProcessingInstruction;
			out.push('<?', pi.target, pi.data ? ` ${pi.data}` : '', '?>');
			return;
		}
		case 8:
			return;
		case 9: {
			const children = Array.from(node.childNodes).filter((child) => child.nodeType !== 8);
			for (const child of children) render(child, out, rendered, exclusive);
			return;
		}
		default: {
			const children = Array.from(node.childNodes).filter((child) => child.nodeType !== 8);
			for (const child of children) render(child, out, rendered, exclusive);
		}
	}
}
