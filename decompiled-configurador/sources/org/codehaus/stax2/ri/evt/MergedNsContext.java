package org.codehaus.stax2.ri.evt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.events.Namespace;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/evt/MergedNsContext.class */
public class MergedNsContext implements NamespaceContext {
    final NamespaceContext _parentCtxt;
    final List<Namespace> _namespaces;

    protected MergedNsContext(NamespaceContext parentCtxt, List<Namespace> localNs) {
        this._parentCtxt = parentCtxt;
        this._namespaces = localNs == null ? Collections.emptyList() : localNs;
    }

    public static MergedNsContext construct(NamespaceContext parentCtxt, List<Namespace> localNs) {
        return new MergedNsContext(parentCtxt, localNs);
    }

    @Override // javax.xml.namespace.NamespaceContext
    public String getNamespaceURI(String prefix) {
        String uri;
        if (prefix == null) {
            throw new IllegalArgumentException("Illegal to pass null prefix");
        }
        int len = this._namespaces.size();
        for (int i = 0; i < len; i++) {
            Namespace ns = this._namespaces.get(i);
            if (prefix.equals(ns.getPrefix())) {
                return ns.getNamespaceURI();
            }
        }
        if (this._parentCtxt != null && (uri = this._parentCtxt.getNamespaceURI(prefix)) != null) {
            return uri;
        }
        if (prefix.equals(CanonicalizerBase.XML)) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if (prefix.equals(CanonicalizerBase.XMLNS)) {
            return "http://www.w3.org/2000/xmlns/";
        }
        return null;
    }

    @Override // javax.xml.namespace.NamespaceContext
    public String getPrefix(String nsURI) {
        if (nsURI == null || nsURI.length() == 0) {
            throw new IllegalArgumentException("Illegal to pass null/empty prefix as argument.");
        }
        int len = this._namespaces.size();
        for (int i = 0; i < len; i++) {
            Namespace ns = this._namespaces.get(i);
            if (nsURI.equals(ns.getNamespaceURI())) {
                return ns.getPrefix();
            }
        }
        if (this._parentCtxt != null) {
            String prefix = this._parentCtxt.getPrefix(nsURI);
            if (prefix != null) {
                String uri2 = getNamespaceURI(prefix);
                if (uri2.equals(nsURI)) {
                    return prefix;
                }
            }
            Iterator<?> it = this._parentCtxt.getPrefixes(nsURI);
            while (it.hasNext()) {
                String p2 = (String) it.next();
                if (!p2.equals(prefix)) {
                    String uri3 = getNamespaceURI(p2);
                    if (uri3.equals(nsURI)) {
                        return p2;
                    }
                }
            }
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
        ArrayList<String> l = null;
        int len = this._namespaces.size();
        for (int i = 0; i < len; i++) {
            Namespace ns = this._namespaces.get(i);
            if (nsURI.equals(ns.getNamespaceURI())) {
                l = addToList(l, ns.getPrefix());
            }
        }
        if (this._parentCtxt != null) {
            Iterator<?> it = this._parentCtxt.getPrefixes(nsURI);
            while (it.hasNext()) {
                String p2 = (String) it.next();
                String uri2 = getNamespaceURI(p2);
                if (uri2.equals(nsURI)) {
                    l = addToList(l, p2);
                }
            }
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            l = addToList(l, CanonicalizerBase.XML);
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            addToList(l, CanonicalizerBase.XMLNS);
            return null;
        }
        return null;
    }

    protected <T> ArrayList<T> addToList(ArrayList<T> l, T value) {
        if (l == null) {
            l = new ArrayList<>();
        }
        l.add(value);
        return l;
    }
}
