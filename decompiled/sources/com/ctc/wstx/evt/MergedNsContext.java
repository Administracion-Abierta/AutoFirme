package com.ctc.wstx.evt;

import com.ctc.wstx.util.BaseNsContext;
import com.ctc.wstx.util.DataUtil;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Namespace;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/evt/MergedNsContext.class */
public class MergedNsContext extends BaseNsContext {
    final NamespaceContext mParentCtxt;
    final List<Namespace> mNamespaces;
    Map<String, Namespace> mNsByPrefix = null;
    Map<String, Namespace> mNsByURI = null;

    protected MergedNsContext(NamespaceContext parentCtxt, List<Namespace> localNs) {
        this.mParentCtxt = parentCtxt;
        if (localNs == null) {
            this.mNamespaces = Collections.emptyList();
        } else {
            this.mNamespaces = localNs;
        }
    }

    public static BaseNsContext construct(NamespaceContext parentCtxt, List<Namespace> localNs) {
        return new MergedNsContext(parentCtxt, localNs);
    }

    @Override // com.ctc.wstx.util.BaseNsContext
    public String doGetNamespaceURI(String prefix) {
        if (this.mNsByPrefix == null) {
            this.mNsByPrefix = buildByPrefixMap();
        }
        Namespace ns = this.mNsByPrefix.get(prefix);
        if (ns == null && this.mParentCtxt != null) {
            return this.mParentCtxt.getNamespaceURI(prefix);
        }
        if (ns == null) {
            return null;
        }
        return ns.getNamespaceURI();
    }

    @Override // com.ctc.wstx.util.BaseNsContext
    public String doGetPrefix(String nsURI) {
        if (this.mNsByURI == null) {
            this.mNsByURI = buildByNsURIMap();
        }
        Namespace ns = this.mNsByURI.get(nsURI);
        if (ns == null && this.mParentCtxt != null) {
            return this.mParentCtxt.getPrefix(nsURI);
        }
        if (ns == null) {
            return null;
        }
        return ns.getPrefix();
    }

    @Override // com.ctc.wstx.util.BaseNsContext
    public Iterator<String> doGetPrefixes(String nsURI) {
        ArrayList<String> l = null;
        int len = this.mNamespaces.size();
        for (int i = 0; i < len; i++) {
            Namespace ns = this.mNamespaces.get(i);
            String uri = ns.getNamespaceURI();
            if (uri == null) {
                uri = "";
            }
            if (uri.equals(nsURI)) {
                if (l == null) {
                    l = new ArrayList<>();
                }
                String prefix = ns.getPrefix();
                l.add(prefix == null ? "" : prefix);
            }
        }
        if (this.mParentCtxt != null) {
            Iterator<String> it = this.mParentCtxt.getPrefixes(nsURI);
            if (l == null) {
                return it;
            }
            while (it.hasNext()) {
                l.add(it.next());
            }
        }
        if (l == null) {
            return DataUtil.emptyIterator();
        }
        return l.iterator();
    }

    @Override // com.ctc.wstx.util.BaseNsContext
    public Iterator<Namespace> getNamespaces() {
        return this.mNamespaces.iterator();
    }

    @Override // com.ctc.wstx.util.BaseNsContext
    public void outputNamespaceDeclarations(Writer w) throws IOException {
        int len = this.mNamespaces.size();
        for (int i = 0; i < len; i++) {
            Namespace ns = this.mNamespaces.get(i);
            w.write(32);
            w.write(CanonicalizerBase.XMLNS);
            if (!ns.isDefaultNamespaceDeclaration()) {
                w.write(58);
                w.write(ns.getPrefix());
            }
            w.write("=\"");
            w.write(ns.getNamespaceURI());
            w.write(34);
        }
    }

    @Override // com.ctc.wstx.util.BaseNsContext
    public void outputNamespaceDeclarations(XMLStreamWriter w) throws XMLStreamException {
        int len = this.mNamespaces.size();
        for (int i = 0; i < len; i++) {
            Namespace ns = this.mNamespaces.get(i);
            if (ns.isDefaultNamespaceDeclaration()) {
                w.writeDefaultNamespace(ns.getNamespaceURI());
            } else {
                w.writeNamespace(ns.getPrefix(), ns.getNamespaceURI());
            }
        }
    }

    private Map<String, Namespace> buildByPrefixMap() {
        int len = this.mNamespaces.size();
        if (len == 0) {
            return Collections.emptyMap();
        }
        LinkedHashMap<String, Namespace> m = new LinkedHashMap<>(1 + len + (len >> 1));
        for (int i = 0; i < len; i++) {
            Namespace ns = this.mNamespaces.get(i);
            String prefix = ns.getPrefix();
            if (prefix == null) {
                prefix = "";
            }
            m.put(prefix, ns);
        }
        return m;
    }

    private Map<String, Namespace> buildByNsURIMap() {
        int len = this.mNamespaces.size();
        if (len == 0) {
            return Collections.emptyMap();
        }
        LinkedHashMap<String, Namespace> m = new LinkedHashMap<>(1 + len + (len >> 1));
        for (int i = 0; i < len; i++) {
            Namespace ns = this.mNamespaces.get(i);
            String uri = ns.getNamespaceURI();
            if (uri == null) {
                uri = "";
            }
            m.put(uri, ns);
        }
        return m;
    }
}
