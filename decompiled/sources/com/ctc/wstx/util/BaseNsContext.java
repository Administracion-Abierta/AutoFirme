package com.ctc.wstx.util;

import com.ctc.wstx.cfg.ErrorConsts;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Namespace;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/util/BaseNsContext.class */
public abstract class BaseNsContext implements NamespaceContext {
    protected static final String UNDECLARED_NS_URI = "";

    public abstract Iterator<Namespace> getNamespaces();

    public abstract void outputNamespaceDeclarations(Writer writer) throws IOException;

    public abstract void outputNamespaceDeclarations(XMLStreamWriter xMLStreamWriter) throws XMLStreamException;

    public abstract String doGetNamespaceURI(String str);

    public abstract String doGetPrefix(String str);

    public abstract Iterator<String> doGetPrefixes(String str);

    @Override // javax.xml.namespace.NamespaceContext
    public final String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException(ErrorConsts.ERR_NULL_ARG);
        }
        if (prefix.length() > 0) {
            if (prefix.equals("xml")) {
                return "http://www.w3.org/XML/1998/namespace";
            }
            if (prefix.equals(CanonicalizerBase.XMLNS)) {
                return "http://www.w3.org/2000/xmlns/";
            }
        }
        return doGetNamespaceURI(prefix);
    }

    @Override // javax.xml.namespace.NamespaceContext
    public final String getPrefix(String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return CanonicalizerBase.XMLNS;
        }
        return doGetPrefix(nsURI);
    }

    @Override // javax.xml.namespace.NamespaceContext
    public final Iterator<String> getPrefixes(String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return DataUtil.singletonIterator("xml");
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return DataUtil.singletonIterator(CanonicalizerBase.XMLNS);
        }
        return doGetPrefixes(nsURI);
    }
}
