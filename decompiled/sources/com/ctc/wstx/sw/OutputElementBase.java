package com.ctc.wstx.sw;

import com.ctc.wstx.util.BijectiveNsMap;
import com.ctc.wstx.util.DataUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sw/OutputElementBase.class */
public abstract class OutputElementBase implements NamespaceContext {
    public static final int PREFIX_UNBOUND = 0;
    public static final int PREFIX_OK = 1;
    public static final int PREFIX_MISBOUND = 2;
    protected static final String sXmlNsPrefix = "xml";
    protected static final String sXmlNsURI = "http://www.w3.org/XML/1998/namespace";
    protected static final BijectiveNsMap DEFAULT_XML_BINDINGS = BijectiveNsMap.createEmpty();
    protected NamespaceContext mRootNsContext;
    protected String mDefaultNsURI;
    protected BijectiveNsMap mNsMapping;
    protected boolean mNsMapShared;

    protected abstract void setRootNsContext(NamespaceContext namespaceContext);

    public abstract boolean isRoot();

    public abstract String getNameDesc();

    public abstract void setDefaultNsUri(String str);

    protected OutputElementBase() {
        this.mNsMapping = null;
        this.mNsMapShared = false;
        this.mDefaultNsURI = "";
        this.mRootNsContext = null;
    }

    protected OutputElementBase(OutputElementBase parent, BijectiveNsMap ns) {
        this.mNsMapping = ns;
        this.mNsMapShared = ns != null;
        this.mDefaultNsURI = parent.mDefaultNsURI;
        this.mRootNsContext = parent.mRootNsContext;
    }

    protected void relink(OutputElementBase parent) {
        this.mNsMapping = parent.mNsMapping;
        this.mNsMapShared = this.mNsMapping != null;
        this.mDefaultNsURI = parent.mDefaultNsURI;
        this.mRootNsContext = parent.mRootNsContext;
    }

    public final String getDefaultNsUri() {
        return this.mDefaultNsURI;
    }

    public final String getExplicitPrefix(String uri) {
        String prefix;
        BijectiveNsMap mappings = this.mNsMapping;
        if (mappings == null) {
            mappings = DEFAULT_XML_BINDINGS;
        }
        String prefix2 = mappings.findPrefixByUri(uri);
        if (prefix2 != null) {
            return prefix2;
        }
        if (this.mRootNsContext != null && (prefix = this.mRootNsContext.getPrefix(uri)) != null && prefix.length() > 0) {
            return prefix;
        }
        return null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public final int isPrefixValid(String prefix, String nsURI, boolean isElement) throws XMLStreamException {
        String act;
        if (nsURI == null) {
            nsURI = "";
        }
        if (prefix == null || prefix.length() == 0) {
            if (isElement) {
                if (nsURI == this.mDefaultNsURI || nsURI.equals(this.mDefaultNsURI)) {
                    return 1;
                }
                return 2;
            }
            if (nsURI.length() == 0) {
                return 1;
            }
            return 2;
        }
        if (prefix.equals("xml")) {
            if (!nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
                throwOutputError("Namespace prefix 'xml' can not be bound to non-default namespace ('" + nsURI + "'); has to be the default 'http://www.w3.org/XML/1998/namespace" + OperatorName.SHOW_TEXT_LINE);
                return 1;
            }
            return 1;
        }
        if (this.mNsMapping != null) {
            act = this.mNsMapping.findUriByPrefix(prefix);
        } else {
            act = null;
        }
        if (act == null && this.mRootNsContext != null) {
            act = this.mRootNsContext.getNamespaceURI(prefix);
        }
        if (act == null) {
            return 0;
        }
        return (act == nsURI || act.equals(nsURI)) ? 1 : 2;
    }

    public final String generateMapping(String prefixBase, String uri, int[] seqArr) {
        if (this.mNsMapping == null) {
            this.mNsMapping = BijectiveNsMap.createEmpty();
        } else if (this.mNsMapShared) {
            this.mNsMapping = this.mNsMapping.createChild();
            this.mNsMapShared = false;
        }
        return this.mNsMapping.addGeneratedMapping(prefixBase, this.mRootNsContext, uri, seqArr);
    }

    public final void addPrefix(String prefix, String uri) {
        if (this.mNsMapping == null) {
            this.mNsMapping = BijectiveNsMap.createEmpty();
        } else if (this.mNsMapShared) {
            this.mNsMapping = this.mNsMapping.createChild();
            this.mNsMapShared = false;
        }
        this.mNsMapping.addMapping(prefix, uri);
    }

    @Override // javax.xml.namespace.NamespaceContext
    public final String getNamespaceURI(String prefix) {
        if (prefix.length() == 0) {
            return this.mDefaultNsURI;
        }
        BijectiveNsMap mappings = this.mNsMapping;
        if (mappings == null) {
            mappings = DEFAULT_XML_BINDINGS;
        }
        String uri = mappings.findUriByPrefix(prefix);
        if (uri != null) {
            return uri;
        }
        if (this.mRootNsContext != null) {
            return this.mRootNsContext.getNamespaceURI(prefix);
        }
        return null;
    }

    @Override // javax.xml.namespace.NamespaceContext
    public final String getPrefix(String uri) {
        if (this.mDefaultNsURI.equals(uri)) {
            return "";
        }
        BijectiveNsMap mappings = this.mNsMapping;
        if (mappings == null) {
            mappings = DEFAULT_XML_BINDINGS;
        }
        String prefix = mappings.findPrefixByUri(uri);
        if (prefix != null) {
            return prefix;
        }
        if (this.mRootNsContext != null) {
            return this.mRootNsContext.getPrefix(uri);
        }
        return null;
    }

    @Override // javax.xml.namespace.NamespaceContext
    public final Iterator<String> getPrefixes(String uri) {
        List<String> l = null;
        if (this.mDefaultNsURI.equals(uri)) {
            l = new ArrayList();
            l.add("");
        }
        if (this.mNsMapping != null) {
            l = this.mNsMapping.getPrefixesBoundToUri(uri, l);
        }
        if (this.mRootNsContext != null) {
            Iterator<?> it = this.mRootNsContext.getPrefixes(uri);
            while (it.hasNext()) {
                String prefix = (String) it.next();
                if (prefix.length() != 0) {
                    if (l == null) {
                        l = new ArrayList();
                    } else if (l.contains(prefix)) {
                    }
                    l.add(prefix);
                }
            }
        }
        if (l == null) {
            return DataUtil.emptyIterator();
        }
        return l.iterator();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected final void throwOutputError(String msg) throws XMLStreamException {
        throw new XMLStreamException(msg);
    }
}
