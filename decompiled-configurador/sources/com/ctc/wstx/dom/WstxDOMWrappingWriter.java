package com.ctc.wstx.dom;

import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import java.util.HashMap;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMResult;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.codehaus.stax2.ri.EmptyNamespaceContext;
import org.codehaus.stax2.ri.dom.DOMWrappingWriter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dom/WstxDOMWrappingWriter.class */
public class WstxDOMWrappingWriter extends DOMWrappingWriter {
    protected static final String ERR_NSDECL_WRONG_STATE = "Trying to write a namespace declaration when there is no open start element.";
    protected final WriterConfig mConfig;
    protected DOMOutputElement mCurrElem;
    protected DOMOutputElement mOpenElement;
    protected int[] mAutoNsSeq;
    protected String mSuggestedDefNs;
    protected String mAutomaticNsPrefix;
    HashMap<String, String> mSuggestedPrefixes;

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private WstxDOMWrappingWriter(WriterConfig cfg, Node treeRoot) throws XMLStreamException {
        super(treeRoot, cfg.willSupportNamespaces(), cfg.automaticNamespacesEnabled());
        this.mSuggestedDefNs = null;
        this.mSuggestedPrefixes = null;
        this.mConfig = cfg;
        this.mAutoNsSeq = null;
        this.mAutomaticNsPrefix = this.mNsRepairing ? this.mConfig.getAutomaticNsPrefix() : null;
        switch (treeRoot.getNodeType()) {
            case 1:
                DOMOutputElement root = DOMOutputElement.createRoot(treeRoot);
                Element elem = (Element) treeRoot;
                DOMOutputElement dOMOutputElementCreateChild = root.createChild(elem);
                this.mCurrElem = dOMOutputElementCreateChild;
                this.mOpenElement = dOMOutputElementCreateChild;
                return;
            case 9:
            case 11:
                this.mCurrElem = DOMOutputElement.createRoot(treeRoot);
                this.mOpenElement = null;
                return;
            default:
                throw new XMLStreamException("Can not create an XMLStreamWriter for a DOM node of type " + treeRoot.getClass());
        }
    }

    public static WstxDOMWrappingWriter createFrom(WriterConfig cfg, DOMResult dst) throws XMLStreamException {
        Node rootNode = dst.getNode();
        return new WstxDOMWrappingWriter(cfg, rootNode);
    }

    @Override // org.codehaus.stax2.ri.dom.DOMWrappingWriter
    public NamespaceContext getNamespaceContext() {
        if (!this.mNsAware) {
            return EmptyNamespaceContext.getInstance();
        }
        return this.mCurrElem;
    }

    @Override // org.codehaus.stax2.ri.dom.DOMWrappingWriter
    public String getPrefix(String uri) {
        String prefix;
        if (!this.mNsAware) {
            return null;
        }
        if (this.mNsContext != null && (prefix = this.mNsContext.getPrefix(uri)) != null) {
            return prefix;
        }
        return this.mCurrElem.getPrefix(uri);
    }

    @Override // org.codehaus.stax2.ri.dom.DOMWrappingWriter
    public Object getProperty(String name) {
        return this.mConfig.getProperty(name);
    }

    @Override // org.codehaus.stax2.ri.dom.DOMWrappingWriter
    public void setDefaultNamespace(String uri) {
        this.mSuggestedDefNs = (uri == null || uri.length() == 0) ? null : uri;
    }

    @Override // org.codehaus.stax2.ri.dom.DOMWrappingWriter
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        if (prefix == null) {
            throw new NullPointerException("Can not pass null 'prefix' value");
        }
        if (prefix.length() == 0) {
            setDefaultNamespace(uri);
            return;
        }
        if (uri == null) {
            throw new NullPointerException("Can not pass null 'uri' value");
        }
        if (prefix.equals(CanonicalizerBase.XML)) {
            if (!uri.equals("http://www.w3.org/XML/1998/namespace")) {
                throwOutputError(ErrorConsts.ERR_NS_REDECL_XML, uri);
            }
        } else if (prefix.equals(CanonicalizerBase.XMLNS)) {
            if (!uri.equals("http://www.w3.org/2000/xmlns/")) {
                throwOutputError(ErrorConsts.ERR_NS_REDECL_XMLNS, uri);
                return;
            }
            return;
        } else if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
            throwOutputError(ErrorConsts.ERR_NS_REDECL_XML_URI, prefix);
        } else if (uri.equals("http://www.w3.org/2000/xmlns/")) {
            throwOutputError(ErrorConsts.ERR_NS_REDECL_XMLNS_URI, prefix);
        }
        if (this.mSuggestedPrefixes == null) {
            this.mSuggestedPrefixes = new HashMap<>(16);
        }
        this.mSuggestedPrefixes.put(uri, prefix);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.dom.DOMWrappingWriter
    public void writeAttribute(String localName, String value) throws XMLStreamException {
        outputAttribute(null, null, localName, value);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.dom.DOMWrappingWriter
    public void writeAttribute(String nsURI, String localName, String value) throws XMLStreamException {
        outputAttribute(nsURI, null, localName, value);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.dom.DOMWrappingWriter
    public void writeAttribute(String prefix, String nsURI, String localName, String value) throws XMLStreamException {
        outputAttribute(nsURI, prefix, localName, value);
    }

    @Override // org.codehaus.stax2.ri.dom.DOMWrappingWriter
    public void writeDefaultNamespace(String nsURI) {
        if (this.mOpenElement == null) {
            throw new IllegalStateException("No currently open START_ELEMENT, cannot write attribute");
        }
        setDefaultNamespace(nsURI);
        this.mOpenElement.addAttribute("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS, nsURI);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.dom.DOMWrappingWriter
    public void writeEmptyElement(String localName) throws XMLStreamException {
        writeEmptyElement(null, localName);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.dom.DOMWrappingWriter
    public void writeEmptyElement(String nsURI, String localName) throws XMLStreamException {
        createStartElem(nsURI, null, localName, true);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.ri.dom.DOMWrappingWriter
    public void writeEmptyElement(String prefix, String localName, String nsURI) throws XMLStreamException {
        if (prefix == null) {
            prefix = "";
        }
        createStartElem(nsURI, prefix, localName, true);
    }

    @Override // org.codehaus.stax2.ri.dom.DOMWrappingWriter
    public void writeEndDocument() {
        this.mOpenElement = null;
        this.mCurrElem = null;
    }

    public void writeEndElement() {
        if (this.mCurrElem == null || this.mCurrElem.isRoot()) {
            throw new IllegalStateException("No open start element to close");
        }
        this.mOpenElement = null;
        this.mCurrElem = this.mCurrElem.getParent();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeNamespace(String prefix, String nsURI) throws XMLStreamException {
        if (prefix == null || prefix.length() == 0) {
            writeDefaultNamespace(nsURI);
            return;
        }
        if (!this.mNsAware) {
            throwOutputError("Can not write namespaces with non-namespace writer.");
        }
        outputAttribute("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS, prefix, nsURI);
        this.mCurrElem.addPrefix(prefix, nsURI);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeStartElement(String localName) throws XMLStreamException {
        writeStartElement(null, localName);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeStartElement(String nsURI, String localName) throws XMLStreamException {
        createStartElem(nsURI, null, localName, false);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void writeStartElement(String prefix, String localName, String nsURI) throws XMLStreamException {
        createStartElem(nsURI, prefix, localName, false);
    }

    @Override // org.codehaus.stax2.ri.dom.DOMWrappingWriter, org.codehaus.stax2.XMLStreamWriter2
    public boolean isPropertySupported(String name) {
        return this.mConfig.isPropertySupported(name);
    }

    @Override // org.codehaus.stax2.ri.dom.DOMWrappingWriter, org.codehaus.stax2.XMLStreamWriter2
    public boolean setProperty(String name, Object value) {
        return this.mConfig.setProperty(name, value);
    }

    @Override // org.codehaus.stax2.ri.dom.DOMWrappingWriter, org.codehaus.stax2.XMLStreamWriter2
    public void writeDTD(String rootName, String systemId, String publicId, String internalSubset) throws XMLStreamException {
        if (this.mCurrElem != null) {
            throw new IllegalStateException("Operation only allowed to the document before adding root element");
        }
        reportUnsupported("writeDTD()");
    }

    @Override // org.codehaus.stax2.ri.dom.DOMWrappingWriter
    protected void appendLeaf(Node n) throws IllegalStateException {
        this.mCurrElem.appendNode(n);
        this.mOpenElement = null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void createStartElem(String nsURI, String prefix, String localName, boolean isEmpty) throws XMLStreamException {
        DOMOutputElement elem;
        if (!this.mNsAware) {
            if (nsURI != null && nsURI.length() > 0) {
                throwOutputError("Can not specify non-empty uri/prefix in non-namespace mode");
            }
            elem = this.mCurrElem.createAndAttachChild(this.mDocument.createElement(localName));
        } else if (this.mNsRepairing) {
            String actPrefix = validateElemPrefix(prefix, nsURI, this.mCurrElem);
            if (actPrefix != null) {
                elem = actPrefix.length() != 0 ? this.mCurrElem.createAndAttachChild(this.mDocument.createElementNS(nsURI, actPrefix + ":" + localName)) : this.mCurrElem.createAndAttachChild(this.mDocument.createElementNS(nsURI, localName));
            } else {
                if (prefix == null) {
                    prefix = "";
                }
                String actPrefix2 = generateElemPrefix(prefix, nsURI, this.mCurrElem);
                boolean hasPrefix = actPrefix2.length() != 0;
                if (hasPrefix) {
                    localName = actPrefix2 + ":" + localName;
                }
                elem = this.mCurrElem.createAndAttachChild(this.mDocument.createElementNS(nsURI, localName));
                this.mOpenElement = elem;
                if (hasPrefix) {
                    writeNamespace(actPrefix2, nsURI);
                    elem.addPrefix(actPrefix2, nsURI);
                } else {
                    writeDefaultNamespace(nsURI);
                    elem.setDefaultNsUri(nsURI);
                }
            }
        } else {
            if (prefix == null && nsURI != null && nsURI.length() > 0) {
                prefix = this.mSuggestedPrefixes == null ? null : this.mSuggestedPrefixes.get(nsURI);
                if (prefix == null) {
                    throwOutputError("Can not find prefix for namespace \"" + nsURI + "\"");
                }
            }
            if (prefix != null && prefix.length() != 0) {
                localName = prefix + ":" + localName;
            }
            elem = this.mCurrElem.createAndAttachChild(this.mDocument.createElementNS(nsURI, localName));
        }
        this.mOpenElement = elem;
        if (!isEmpty) {
            this.mCurrElem = elem;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void outputAttribute(String nsURI, String prefix, String localName, String value) throws XMLStreamException {
        if (this.mOpenElement == null) {
            throw new IllegalStateException("No currently open START_ELEMENT, cannot write attribute");
        }
        if (this.mNsAware) {
            if (this.mNsRepairing) {
                prefix = findOrCreateAttrPrefix(prefix, nsURI, this.mOpenElement);
            }
            if (prefix != null && prefix.length() > 0) {
                localName = prefix + ":" + localName;
            }
            this.mOpenElement.addAttribute(nsURI, localName, value);
            return;
        }
        if (prefix != null && prefix.length() > 0) {
            localName = prefix + ":" + localName;
        }
        this.mOpenElement.addAttribute(localName, value);
    }

    private final String validateElemPrefix(String prefix, String nsURI, DOMOutputElement elem) throws XMLStreamException {
        if (nsURI == null || nsURI.length() == 0) {
            String currURL = elem.getDefaultNsUri();
            if (currURL == null || currURL.length() == 0) {
                return "";
            }
            return null;
        }
        int status = elem.isPrefixValid(prefix, nsURI, true);
        if (status == 1) {
            return prefix;
        }
        return null;
    }

    protected final String findElemPrefix(String nsURI, DOMOutputElement elem) throws XMLStreamException {
        if (nsURI == null || nsURI.length() == 0) {
            String currDefNsURI = elem.getDefaultNsUri();
            if (currDefNsURI != null && currDefNsURI.length() > 0) {
                return null;
            }
            return "";
        }
        return this.mCurrElem.getPrefix(nsURI);
    }

    protected final String generateElemPrefix(String suggPrefix, String nsURI, DOMOutputElement elem) throws XMLStreamException {
        if (nsURI == null || nsURI.length() == 0) {
            return "";
        }
        if (suggPrefix == null) {
            if (this.mSuggestedDefNs != null && this.mSuggestedDefNs.equals(nsURI)) {
                suggPrefix = "";
            } else {
                suggPrefix = this.mSuggestedPrefixes == null ? null : this.mSuggestedPrefixes.get(nsURI);
                if (suggPrefix == null) {
                    if (this.mAutoNsSeq == null) {
                        this.mAutoNsSeq = new int[1];
                        this.mAutoNsSeq[0] = 1;
                    }
                    suggPrefix = elem.generateMapping(this.mAutomaticNsPrefix, nsURI, this.mAutoNsSeq);
                }
            }
        }
        return suggPrefix;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected final String findOrCreateAttrPrefix(String suggPrefix, String nsURI, DOMOutputElement elem) throws XMLStreamException {
        if (nsURI == null || nsURI.length() == 0) {
            return null;
        }
        if (suggPrefix != null) {
            int status = elem.isPrefixValid(suggPrefix, nsURI, false);
            if (status == 1) {
                return suggPrefix;
            }
            if (status == 0) {
                elem.addPrefix(suggPrefix, nsURI);
                writeNamespace(suggPrefix, nsURI);
                return suggPrefix;
            }
        }
        String prefix = elem.getExplicitPrefix(nsURI);
        if (prefix != null) {
            return prefix;
        }
        if (suggPrefix != null) {
            prefix = suggPrefix;
        } else if (this.mSuggestedPrefixes != null) {
            prefix = this.mSuggestedPrefixes.get(nsURI);
        }
        if (prefix != null && (prefix.length() == 0 || elem.getNamespaceURI(prefix) != null)) {
            prefix = null;
        }
        if (prefix == null) {
            if (this.mAutoNsSeq == null) {
                this.mAutoNsSeq = new int[1];
                this.mAutoNsSeq[0] = 1;
            }
            prefix = this.mCurrElem.generateMapping(this.mAutomaticNsPrefix, nsURI, this.mAutoNsSeq);
        }
        elem.addPrefix(prefix, nsURI);
        writeNamespace(prefix, nsURI);
        return prefix;
    }
}
