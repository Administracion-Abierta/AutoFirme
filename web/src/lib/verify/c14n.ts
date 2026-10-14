/**
 * XML canonicalization (XML-C14N 1.0 inclusive + exclusive, without comments) and the
 * node-set plumbing XMLDSig transforms need. Written here because the app takes no
 * crypto/XML dependencies; validated against python `ElementTree.canonicalize` vectors
 * in `test/fixtures/c14n/`.
 */

export interface C14nOptions {
	/** Exclusive canonicalization (exc-c14n); defaults to inclusive C14N 1.0. */
	exclusive?: boolean;
	/** `InclusiveNamespaces/@PrefixList` from the exclusive transform. */
	inclusivePrefixes?: string[];
}

const XML_NAMESPACE = 'http://www.w3.org/XML/1998/namespace';

function escapeText(text: string): string {
	return text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/\r/g, '&#xD;');
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

/** Prefix → URI for every namespace in scope on `element`, from its declarations and its ancestors'. */
function inScopeNamespaces(element: Element): Map<string, string> {
	const stack: Element[] = [];
	for (let node: Element | null = element; node; node = node.parentElement) stack.unshift(node);
	const namespaces = new Map<string, string>();
	for (const node of stack) {
		for (const attribute of Array.from(node.attributes)) {
			if (attribute.name === 'xmlns') namespaces.set('', attribute.value);
			else if (attribute.name.startsWith('xmlns:')) namespaces.set(attribute.name.slice(6), attribute.value);
		}
	}
	namespaces.set('xml', XML_NAMESPACE);
	return namespaces;
}

function ownDeclarations(element: Element): Map<string, string> {
	const declarations = new Map<string, string>();
	for (const attribute of Array.from(element.attributes)) {
		if (attribute.name === 'xmlns') declarations.set('', attribute.value);
		else if (attribute.name.startsWith('xmlns:')) declarations.set(attribute.name.slice(6), attribute.value);
	}
	return declarations;
}

function renderNode(node: Node, rendered: Map<string, string>, options: C14nOptions): string {
	switch (node.nodeType) {
		case 3: // text
		case 4: // CDATA sections are output as their text
			return escapeText(node.nodeValue ?? '');
		case 7: {
			const instruction = node as ProcessingInstruction;
			return `<?${instruction.target}${instruction.data ? ` ${instruction.data}` : ''}?>`;
		}
		case 8: // comments are not part of the canonical form we verify
			return '';
		case 9: {
			const document = node as Document;
			return Array.from(document.childNodes)
				.map((child) => renderNode(child, rendered, options))
				.join('');
		}
		case 1: {
			const element = node as Element;
			const name = element.prefix ? `${element.prefix}:${element.localName}` : element.localName;
			const emitted = new Map(rendered);
			const declarations: [string, string][] = [];
			const inScope = inScopeNamespaces(element);
			if (options.exclusive) {
				const visible = new Set<string>(options.inclusivePrefixes ?? []);
				if (element.namespaceURI) visible.add(element.prefix ?? '');
				for (const attribute of Array.from(element.attributes)) {
					if (attribute.namespaceURI && attribute.namespaceURI !== XML_NAMESPACE) visible.add(attribute.prefix ?? '');
				}
				for (const prefix of [...visible].sort()) {
					const uri = inScope.get(prefix);
					if (uri !== undefined && uri !== emitted.get(prefix) && prefix !== 'xml') {
						declarations.push([prefix, uri]);
						emitted.set(prefix, uri);
					}
				}
			} else {
				for (const [prefix, uri] of inScope) {
					if (prefix === 'xml') continue;
					if (prefix === '' && uri === '' && !emitted.has('')) continue;
					if (uri !== emitted.get(prefix)) {
						declarations.push([prefix, uri]);
						emitted.set(prefix, uri);
					}
				}
			}
			declarations.sort(([a], [b]) => a.localeCompare(b));
			const declarationText = declarations
				.map(([prefix, uri]) => ` ${prefix === '' ? 'xmlns' : `xmlns:${prefix}`}="${escapeAttribute(uri)}"`)
				.join('');
			const attributes = Array.from(element.attributes)
				.filter((attribute) => attribute.name !== 'xmlns' && !attribute.name.startsWith('xmlns:'))
				.sort((a, b) => {
					const byNamespace = (a.namespaceURI ?? '').localeCompare(b.namespaceURI ?? '');
					return byNamespace !== 0 ? byNamespace : (a.localName ?? a.name).localeCompare(b.localName ?? b.name);
				})
				.map((attribute) => {
					const attributeName = attribute.prefix
						? `${attribute.prefix}:${attribute.localName}`
						: (attribute.localName ?? attribute.name);
					return ` ${attributeName}="${escapeAttribute(attribute.value)}"`;
				})
				.join('');
			const children = Array.from(element.childNodes)
				.map((child) => renderNode(child, emitted, options))
				.join('');
			return `<${name}${declarationText}${attributes}>${children}</${name}>`;
		}
		default:
			return '';
	}
}

export function canonicalize(node: Node, options: C14nOptions = {}): string {
	return renderNode(node, new Map(), options);
}

/** Path of child indices from `root` down to `node` — lets a transform act on a clone. */
function nodePath(root: Node, node: Node): number[] {
	const path: number[] = [];
	for (let current: Node | null = node; current && current !== root; current = current.parentNode) {
		const parent: Node | null = current.parentNode;
		path.unshift(parent ? Array.prototype.indexOf.call(parent.childNodes, current) : 0);
	}
	return path;
}

function nodeAtPath(root: Node, path: number[]): Node {
	let current: Node = root;
	for (const index of path) {
		const next = current.childNodes[index];
		if (!next) throw new Error('cannot re-locate a node in the clone');
		current = next;
	}
	return current;
}

/**
 * The `enveloped-signature` transform: a copy of the document with the signature
 * (and any signature nested inside it) removed. Node identity survives because the
 * clone is walked by index path rather than by re-parsing serialized XML.
 */
export function withoutSignature(document: Document, signature: Element): Document {
	const clone = document.cloneNode(true) as Document;
	const target = nodeAtPath(clone, nodePath(document, signature));
	target.parentNode?.removeChild(target);
	return clone;
}
