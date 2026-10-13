package org.apache.xml.security.utils;

import java.util.Iterator;
import java.util.Objects;
import javax.xml.namespace.NamespaceContext;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/utils/DOMNamespaceContext.class */
public class DOMNamespaceContext implements NamespaceContext {
    private Node context;

    public DOMNamespaceContext(Node context) {
        setContext(context);
    }

    public void setContext(Node context) {
        this.context = context;
    }

    @Override // javax.xml.namespace.NamespaceContext
    public String getNamespaceURI(String prefix) {
        String namespaceURI;
        if (prefix == null) {
            throw new IllegalArgumentException("prefix is null");
        }
        if (prefix.equals("")) {
            prefix = null;
        }
        if (this.context != null && (namespaceURI = this.context.lookupNamespaceURI(prefix)) != null) {
            return namespaceURI;
        }
        if (prefix == null) {
            return "";
        }
        if (prefix.equals(CanonicalizerBase.XML)) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if (prefix.equals(CanonicalizerBase.XMLNS)) {
            return "http://www.w3.org/2000/xmlns/";
        }
        return "";
    }

    @Override // javax.xml.namespace.NamespaceContext
    public String getPrefix(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespace URI is null");
        }
        if (namespaceURI.equals("")) {
            namespaceURI = null;
        }
        if (this.context != null) {
            String prefix = this.context.lookupPrefix(namespaceURI);
            if (prefix != null) {
                return prefix;
            }
            if (Objects.equals(this.context.lookupNamespaceURI(null), namespaceURI)) {
                return "";
            }
        }
        if (namespaceURI == null && this.context != null) {
            if (this.context.lookupNamespaceURI(null) != null) {
                return null;
            }
            return "";
        }
        if ("http://www.w3.org/XML/1998/namespace".equals(namespaceURI)) {
            return CanonicalizerBase.XML;
        }
        if ("http://www.w3.org/2000/xmlns/".equals(namespaceURI)) {
            return CanonicalizerBase.XMLNS;
        }
        return null;
    }

    @Override // javax.xml.namespace.NamespaceContext
    public Iterator<String> getPrefixes(String namespaceURI) {
        throw new UnsupportedOperationException();
    }
}
