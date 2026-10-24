/**
 * Canonical XML — C14N 1.0 (`http://www.w3.org/TR/2001/REC-xml-c14n-20010315`) and Exclusive C14N
 * (`http://www.w3.org/2001/10/xml-exc-c14n#`), over DOM nodes.
 *
 * This is what AutoFirma delegates to Apache Santuario; #7 needs it in the browser, so it is
 * written here from the W3C spec: UTF-8, no XML declaration/doctype, comments dropped (unless
 * asked), namespace declarations first (default then by prefix), attributes sorted by
 * (namespace URI, local name), text escaped for `& < >` and CR, empty elements written `<a></a>`.
 *
 * Scope: an element subtree, or a whole document node-set — processing instructions and comments
 * outside the root included, as the `URI=""` enveloped reference of #7 requires.
 */

import type { Bytes } from './asn1';

export const C14N_1_0 = 'http://www.w3.org/TR/2001/REC-xml-c14n-20010315';
export const EXC_C14N = 'http://www.w3.org/2001/10/xml-exc-c14n#';
export const ENVELOPED_SIGNATURE = 'http://www.w3.org/2000/09/xmldsig#enveloped-signature';

export interface C14nOptions {
	/** Exclusive C14N instead of inclusive C14N 1.0 (default). */
	exclusive?: boolean;
	/** Exclusive C14N `InclusiveNamespaces` PrefixList; `#default` means the default namespace. */
	inclusivePrefixes?: readonly string[];
	/** Keep comments — XMLDSig digests drop them, so this defaults to false. */
	withComments?: boolean;
}

const ELEMENT = 1;
const TEXT = 3;
const CDATA = 4;
const PI = 7;
const COMMENT = 8;
const DOCUMENT = 9;

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

/** Canonical form of a processing instruction: `<?target data?>`. */
function writePi(node: Node, out: string[]): void {
	out.push(`<?${(node as ProcessingInstruction).target} ${(node.nodeValue ?? '').trim()}?>`);
}

/** Canonical form of a comment — dropped unless `withComments`. */
function writeComment(node: Node, out: string[], options: C14nOptions): void {
	if (options.withComments) out.push(`<!--${(node.nodeValue ?? '').replace(/\r/g, '&#xD;')}-->`);
}

/** Namespaces in scope at `element`, the document ancestors included (nearest declaration wins). */
function inScope(element: Element): Map<string, string> {
	const ancestors: Element[] = [];
	for (let node: Node | null = element; node !== null && node.nodeType === ELEMENT; node = node.parentNode) {
		ancestors.push(node as Element);
	}
	const scope = new Map<string, string>();
	for (let i = ancestors.length - 1; i >= 0; i--) {
		for (const attribute of ancestors[i].attributes) {
			if (attribute.name === 'xmlns') scope.set('', attribute.value);
			else if (attribute.prefix === 'xmlns') scope.set(attribute.localName, attribute.value);
		}
	}
	return scope;
}

/**
 * `ancestorScope` are the namespaces in scope at the parent — C14N 1.0 compares a declaration
 * against them. `rendered` are the declarations the ancestors *in the node-set* have emitted —
 * Exclusive C14N compares against those, which is why a sibling re-declares a prefix its uncle
 * already declared.
 */
function writeElement(
	element: Element,
	out: string[],
	options: C14nOptions,
	ancestorScope: Map<string, string>,
	rendered: Map<string, string>
): void {
	const scope = inScope(element);
	const name = element.tagName;
	const above = (prefix: string) =>
		options.exclusive ? rendered.get(prefix) : ancestorScope.get(prefix);

	// Which prefixes this element has to declare: every namespace in scope (inclusive) or only the
	// visibly utilized ones (exclusive: the element's own prefix + its attributes' prefixes).
	const wanted = new Set<string>(options.exclusive ? (options.inclusivePrefixes ?? []) : scope.keys());
	if (options.exclusive) {
		const own = element.prefix ?? '';
		if (own === '' ? (scope.get('') ?? '') !== '' : scope.has(own)) wanted.add(own);
		// Unprefixed attributes are in no namespace (Exclusive C14N §2), so they never pull
		// the default namespace in — only prefixed attributes pull their own prefix.
		for (const attribute of element.attributes) {
			if (attribute.name === 'xmlns' || attribute.prefix === 'xmlns') continue;
			if (attribute.prefix) wanted.add(attribute.prefix);
		}
	}

	const declarations: Array<[string, string]> = [];
	for (const prefix of wanted) {
		const wantedPrefix = prefix === '#default' ? '' : prefix;
		const uri = scope.get(wantedPrefix) ?? '';
		// An empty URI is "no namespace": nothing to declare, only to undo (below).
		if (uri === '' || above(wantedPrefix) === uri) continue;
		declarations.push([wantedPrefix, uri]);
	}
	// This element left the default namespace: it has to say so. Exclusive C14N only for unprefixed
	// elements (a prefixed one is in a namespace of its own).
	const defaultNamespaceLeft =
		(scope.get('') ?? '') === '' && (above('') ?? '') !== '' && (!options.exclusive || !element.prefix);
	if (defaultNamespaceLeft) declarations.push(['', '']);
	declarations.sort((a, b) => (a[0] === '' || b[0] === '' ? (a[0] === b[0] ? 0 : a[0] === '' ? -1 : 1) : a[0] < b[0] ? -1 : 1));
	// Only the descendants of this element inherit its declarations, never its siblings.
	const emitted = new Map(rendered);
	for (const [prefix, uri] of declarations) emitted.set(prefix, uri);

	const attributes = [...element.attributes]
		.filter((attribute) => !(attribute.name === 'xmlns' || attribute.prefix === 'xmlns'))
		.map((attribute) => ({
			uri: attribute.namespaceURI ?? '',
			local: attribute.localName,
			name: attribute.prefix ? `${attribute.prefix}:${attribute.localName}` : attribute.localName,
			value: attribute.value
		}))
		.sort((a, b) => (a.uri === b.uri ? (a.local < b.local ? -1 : a.local > b.local ? 1 : 0) : a.uri < b.uri ? -1 : 1));

	out.push(`<${name}`);
	for (const [prefix, uri] of declarations) {
		out.push(prefix === '' ? ` xmlns="${escapeAttribute(uri)}"` : ` xmlns:${prefix}="${escapeAttribute(uri)}"`);
	}
	for (const attribute of attributes) out.push(` ${attribute.name}="${escapeAttribute(attribute.value)}"`);
	out.push('>');

	const children = element.childNodes;
	for (let i = 0; i < children.length; i++) {
		const child = children[i];
		switch (child.nodeType) {
			case ELEMENT:
				writeElement(child as Element, out, options, scope, emitted);
				break;
			case TEXT:
			case CDATA: {
				// Adjacent text and CDATA nodes are one text node in the canonical form.
				let text = '';
				while (i < children.length && (children[i].nodeType === TEXT || children[i].nodeType === CDATA)) {
					text += children[i].nodeValue ?? '';
					i++;
				}
				i--;
				out.push(escapeText(text));
				break;
			}
			case COMMENT:
				writeComment(child, out, options);
				break;
			case PI:
				writePi(child, out);
				break;
		}
	}

	out.push(`</${name}>`);
}

/** Canonicalize one element subtree (or a whole document), UTF-8 encoded. */
export function canonicalize(node: Node, options: C14nOptions = {}): Bytes {
	const out: string[] = [];
	if (node.nodeType === DOCUMENT) {
		// Document-level nodes are separated by a newline — what xmllint, lxml and xmlsec1 emit.
		const parts: string[] = [];
		for (let child = node.firstChild; child !== null; child = child.nextSibling) {
			const part: string[] = [];
			if (child.nodeType === ELEMENT) writeElement(child as Element, part, options, new Map(), new Map());
			else if (child.nodeType === PI) writePi(child, part);
			else if (child.nodeType === COMMENT) writeComment(child, part, options);
			else continue;
			// A comment dropped by a without-comments c14n takes its separator with it.
			if (part.length > 0) parts.push(part.join(''));
		}
		return new TextEncoder().encode(parts.join('\n'));
	}
	if (node.nodeType !== ELEMENT) throw new Error('c14n: expected a document or element node');
	writeElement(node as Element, out, options, new Map(), new Map());
	return new TextEncoder().encode(out.join(''));
}

/**
 * OOXML (#10) entry points. The package signature digests the octets (`canonicalizeBytes`) and its
 * tests compare the text against `xmllint`, so the string form is exposed directly; `exclusive`
 * selects Exclusive C14N over the default inclusive C14N 1.0.
 */
export function canonicalizeBytes(node: Node, exclusive = false): Bytes {
	return canonicalize(node, exclusive ? { exclusive: true } : {});
}

/** Canonical form of `node` as text — the shape `xmllint --c14n` prints. */
export function canonicalizeToString(node: Node, exclusive = false): string {
	return new TextDecoder().decode(canonicalizeBytes(node, exclusive));
}
