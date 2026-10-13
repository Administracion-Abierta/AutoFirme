package org.codehaus.stax2.ri;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/EmptyNamespaceContext.class */
public class EmptyNamespaceContext implements NamespaceContext {
    static final EmptyNamespaceContext sInstance = new EmptyNamespaceContext();

    private EmptyNamespaceContext() {
    }

    public static EmptyNamespaceContext getInstance() {
        return sInstance;
    }

    @Override // javax.xml.namespace.NamespaceContext
    public final String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (prefix.length() > 0) {
            if (prefix.equals(CanonicalizerBase.XML)) {
                return "http://www.w3.org/XML/1998/namespace";
            }
            if (prefix.equals(CanonicalizerBase.XMLNS)) {
                return "http://www.w3.org/2000/xmlns/";
            }
            return null;
        }
        return null;
    }

    @Override // javax.xml.namespace.NamespaceContext
    public String getPrefix(String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty URI as argument.");
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return CanonicalizerBase.XML;
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return CanonicalizerBase.XMLNS;
        }
        return null;
    }

    @Override // javax.xml.namespace.NamespaceContext
    public Iterator<String> getPrefixes(String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return SingletonIterator.create(CanonicalizerBase.XML);
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return SingletonIterator.create(CanonicalizerBase.XMLNS);
        }
        return EmptyIterator.getInstance();
    }
}
