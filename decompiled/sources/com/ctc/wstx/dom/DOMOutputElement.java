package com.ctc.wstx.dom;

import com.ctc.wstx.sw.OutputElementBase;
import com.ctc.wstx.util.BijectiveNsMap;
import javax.xml.namespace.NamespaceContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dom/DOMOutputElement.class */
public final class DOMOutputElement extends OutputElementBase {
    private DOMOutputElement mParent;
    private final Node mRootNode;
    private Element mElement;
    private boolean mDefaultNsSet;

    private DOMOutputElement(Node rootNode) {
        this.mRootNode = rootNode;
        this.mParent = null;
        this.mElement = null;
        this.mNsMapping = null;
        this.mNsMapShared = false;
        this.mDefaultNsURI = "";
        this.mRootNsContext = null;
        this.mDefaultNsSet = false;
    }

    private DOMOutputElement(DOMOutputElement parent, Element element, BijectiveNsMap ns) {
        super(parent, ns);
        this.mRootNode = null;
        this.mParent = parent;
        this.mElement = element;
        this.mNsMapping = ns;
        this.mNsMapShared = ns != null;
        this.mDefaultNsURI = parent.mDefaultNsURI;
        this.mRootNsContext = parent.mRootNsContext;
        this.mDefaultNsSet = false;
    }

    private void relink(DOMOutputElement parent, Element element) {
        super.relink(parent);
        this.mParent = parent;
        this.mElement = element;
        parent.appendNode(element);
        this.mDefaultNsSet = false;
    }

    public static DOMOutputElement createRoot(Node rootNode) {
        return new DOMOutputElement(rootNode);
    }

    protected DOMOutputElement createAndAttachChild(Element element) {
        if (this.mRootNode != null) {
            this.mRootNode.appendChild(element);
        } else {
            this.mElement.appendChild(element);
        }
        return createChild(element);
    }

    protected DOMOutputElement createChild(Element element) {
        return new DOMOutputElement(this, element, this.mNsMapping);
    }

    protected DOMOutputElement reuseAsChild(DOMOutputElement parent, Element element) {
        DOMOutputElement poolHead = this.mParent;
        relink(parent, element);
        return poolHead;
    }

    protected void addToPool(DOMOutputElement poolHead) {
        this.mParent = poolHead;
    }

    public DOMOutputElement getParent() {
        return this.mParent;
    }

    @Override // com.ctc.wstx.sw.OutputElementBase
    public boolean isRoot() {
        return this.mParent == null;
    }

    @Override // com.ctc.wstx.sw.OutputElementBase
    public String getNameDesc() {
        if (this.mElement != null) {
            return this.mElement.getLocalName();
        }
        return "#error";
    }

    @Override // com.ctc.wstx.sw.OutputElementBase
    public void setDefaultNsUri(String uri) {
        this.mDefaultNsURI = uri;
        this.mDefaultNsSet = true;
    }

    @Override // com.ctc.wstx.sw.OutputElementBase
    protected void setRootNsContext(NamespaceContext ctxt) {
        String defURI;
        this.mRootNsContext = ctxt;
        if (!this.mDefaultNsSet && (defURI = ctxt.getNamespaceURI("")) != null && defURI.length() > 0) {
            this.mDefaultNsURI = defURI;
        }
    }

    protected void appendNode(Node n) {
        if (this.mRootNode != null) {
            this.mRootNode.appendChild(n);
        } else {
            this.mElement.appendChild(n);
        }
    }

    protected void addAttribute(String pname, String value) {
        this.mElement.setAttribute(pname, value);
    }

    protected void addAttribute(String uri, String qname, String value) {
        this.mElement.setAttributeNS(uri, qname, value);
    }

    public void appendChild(Node n) {
        this.mElement.appendChild(n);
    }
}
