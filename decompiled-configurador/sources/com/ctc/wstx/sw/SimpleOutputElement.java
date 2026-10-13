package com.ctc.wstx.sw;

import com.ctc.wstx.compat.QNameCreator;
import com.ctc.wstx.util.BijectiveNsMap;
import java.util.HashSet;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/sw/SimpleOutputElement.class */
public final class SimpleOutputElement extends OutputElementBase {
    protected SimpleOutputElement mParent;
    protected String mPrefix;
    protected String mLocalName;
    protected String mURI;
    protected HashSet<AttrName> mAttrSet;

    private SimpleOutputElement() {
        this.mAttrSet = null;
        this.mParent = null;
        this.mPrefix = null;
        this.mLocalName = "";
        this.mURI = null;
    }

    private SimpleOutputElement(SimpleOutputElement parent, String prefix, String localName, String uri, BijectiveNsMap ns) {
        super(parent, ns);
        this.mAttrSet = null;
        this.mParent = parent;
        this.mPrefix = prefix;
        this.mLocalName = localName;
        this.mURI = uri;
    }

    private void relink(SimpleOutputElement parent, String prefix, String localName, String uri) {
        super.relink(parent);
        this.mParent = parent;
        this.mPrefix = prefix;
        this.mLocalName = localName;
        this.mURI = uri;
        this.mNsMapping = parent.mNsMapping;
        this.mNsMapShared = this.mNsMapping != null;
        this.mDefaultNsURI = parent.mDefaultNsURI;
        this.mRootNsContext = parent.mRootNsContext;
    }

    public static SimpleOutputElement createRoot() {
        return new SimpleOutputElement();
    }

    protected SimpleOutputElement createChild(String localName) {
        this.mAttrSet = null;
        return new SimpleOutputElement(this, null, localName, this.mDefaultNsURI, this.mNsMapping);
    }

    protected SimpleOutputElement reuseAsChild(SimpleOutputElement parent, String localName) {
        this.mAttrSet = null;
        SimpleOutputElement poolHead = this.mParent;
        relink(parent, null, localName, this.mDefaultNsURI);
        return poolHead;
    }

    protected SimpleOutputElement reuseAsChild(SimpleOutputElement parent, String prefix, String localName, String uri) {
        this.mAttrSet = null;
        SimpleOutputElement poolHead = this.mParent;
        relink(parent, prefix, localName, uri);
        return poolHead;
    }

    protected SimpleOutputElement createChild(String prefix, String localName, String uri) {
        this.mAttrSet = null;
        return new SimpleOutputElement(this, prefix, localName, uri, this.mNsMapping);
    }

    protected void addToPool(SimpleOutputElement poolHead) {
        this.mParent = poolHead;
    }

    public SimpleOutputElement getParent() {
        return this.mParent;
    }

    @Override // com.ctc.wstx.sw.OutputElementBase
    public boolean isRoot() {
        return this.mParent == null;
    }

    @Override // com.ctc.wstx.sw.OutputElementBase
    public String getNameDesc() {
        if (this.mPrefix != null && this.mPrefix.length() > 0) {
            return this.mPrefix + ":" + this.mLocalName;
        }
        if (this.mLocalName != null && this.mLocalName.length() > 0) {
            return this.mLocalName;
        }
        return "#error";
    }

    public String getPrefix() {
        return this.mPrefix;
    }

    public String getLocalName() {
        return this.mLocalName;
    }

    public String getNamespaceURI() {
        return this.mURI;
    }

    public QName getName() {
        return QNameCreator.create(this.mURI, this.mLocalName, this.mPrefix);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void checkAttrWrite(String nsURI, String localName) throws XMLStreamException {
        AttrName an = new AttrName(nsURI, localName);
        if (this.mAttrSet == null) {
            this.mAttrSet = new HashSet<>();
        }
        if (!this.mAttrSet.add(an)) {
            throw new XMLStreamException("Duplicate attribute write for attribute '" + an + "'");
        }
    }

    public void setPrefix(String prefix) {
        this.mPrefix = prefix;
    }

    @Override // com.ctc.wstx.sw.OutputElementBase
    public void setDefaultNsUri(String uri) {
        this.mDefaultNsURI = uri;
    }

    @Override // com.ctc.wstx.sw.OutputElementBase
    protected final void setRootNsContext(NamespaceContext ctxt) {
        this.mRootNsContext = ctxt;
        String defURI = ctxt.getNamespaceURI("");
        if (defURI != null && defURI.length() > 0) {
            this.mDefaultNsURI = defURI;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/sw/SimpleOutputElement$AttrName.class */
    static final class AttrName implements Comparable<AttrName> {
        final String mNsURI;
        final String mLocalName;
        final int mHashCode;

        public AttrName(String nsURI, String localName) {
            this.mNsURI = nsURI == null ? "" : nsURI;
            this.mLocalName = localName;
            this.mHashCode = (this.mNsURI.hashCode() * 31) ^ this.mLocalName.hashCode();
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof AttrName)) {
                return false;
            }
            AttrName other = (AttrName) o;
            String otherLN = other.mLocalName;
            if (otherLN != this.mLocalName && !otherLN.equals(this.mLocalName)) {
                return false;
            }
            String otherURI = other.mNsURI;
            return otherURI == this.mNsURI || otherURI.equals(this.mNsURI);
        }

        public String toString() {
            if (this.mNsURI.length() > 0) {
                return "{" + this.mNsURI + "} " + this.mLocalName;
            }
            return this.mLocalName;
        }

        public int hashCode() {
            return this.mHashCode;
        }

        @Override // java.lang.Comparable
        public int compareTo(AttrName other) {
            int result = this.mNsURI.compareTo(other.mNsURI);
            if (result == 0) {
                result = this.mLocalName.compareTo(other.mLocalName);
            }
            return result;
        }
    }
}
