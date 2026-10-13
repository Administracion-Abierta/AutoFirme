package com.ctc.wstx.sr;

import com.ctc.wstx.util.BaseNsContext;
import com.ctc.wstx.util.DataUtil;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Namespace;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.codehaus.stax2.ri.evt.NamespaceEventImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sr/CompactNsContext.class */
public final class CompactNsContext extends BaseNsContext {
    final Location mLocation;
    final String[] mNamespaces;
    final int mNsLength;
    final int mFirstLocalNs;
    transient ArrayList<Namespace> mNsList;

    public CompactNsContext(Location loc, String[] namespaces, int nsLen, int firstLocal) {
        this.mLocation = loc;
        this.mNamespaces = namespaces;
        this.mNsLength = nsLen;
        this.mFirstLocalNs = firstLocal;
    }

    @Override // com.ctc.wstx.util.BaseNsContext
    public String doGetNamespaceURI(String prefix) {
        String[] ns = this.mNamespaces;
        if (prefix.length() == 0) {
            for (int i = this.mNsLength - 2; i >= 0; i -= 2) {
                if (ns[i] == null) {
                    return ns[i + 1];
                }
            }
            return null;
        }
        for (int i2 = this.mNsLength - 2; i2 >= 0; i2 -= 2) {
            if (prefix.equals(ns[i2])) {
                return ns[i2 + 1];
            }
        }
        return null;
    }

    @Override // com.ctc.wstx.util.BaseNsContext
    public String doGetPrefix(String nsURI) {
        String[] ns = this.mNamespaces;
        int len = this.mNsLength;
        for (int i = len - 1; i > 0; i -= 2) {
            if (nsURI.equals(ns[i])) {
                String prefix = ns[i - 1];
                int j = i + 1;
                while (true) {
                    if (j < len) {
                        if (ns[j] == prefix) {
                            break;
                        }
                        j += 2;
                    } else {
                        String uri = ns[i - 1];
                        return uri == null ? "" : uri;
                    }
                }
            }
        }
        return null;
    }

    @Override // com.ctc.wstx.util.BaseNsContext
    public Iterator<String> doGetPrefixes(String nsURI) {
        String[] ns = this.mNamespaces;
        int len = this.mNsLength;
        String first = null;
        ArrayList<String> all = null;
        for (int i = len - 1; i > 0; i -= 2) {
            String currNS = ns[i];
            if (currNS == nsURI || currNS.equals(nsURI)) {
                String prefix = ns[i - 1];
                int j = i + 1;
                while (true) {
                    if (j < len) {
                        if (ns[j] == prefix) {
                            break;
                        }
                        j += 2;
                    } else {
                        if (prefix == null) {
                            prefix = "";
                        }
                        if (first == null) {
                            first = prefix;
                            break;
                        }
                        if (all == null) {
                            all = new ArrayList<>();
                            all.add(first);
                        }
                        all.add(prefix);
                        break;
                    }
                }
            }
        }
        if (all != null) {
            return all.iterator();
        }
        if (first != null) {
            return DataUtil.singletonIterator(first);
        }
        return DataUtil.emptyIterator();
    }

    @Override // com.ctc.wstx.util.BaseNsContext
    public Iterator<Namespace> getNamespaces() {
        if (this.mNsList == null) {
            int firstLocal = this.mFirstLocalNs;
            int len = this.mNsLength - firstLocal;
            if (len == 0) {
                return DataUtil.emptyIterator();
            }
            if (len == 2) {
                return DataUtil.singletonIterator(NamespaceEventImpl.constructNamespace(this.mLocation, this.mNamespaces[firstLocal], this.mNamespaces[firstLocal + 1]));
            }
            ArrayList<Namespace> l = new ArrayList<>(len >> 1);
            String[] ns = this.mNamespaces;
            int len2 = this.mNsLength;
            while (firstLocal < len2) {
                l.add(NamespaceEventImpl.constructNamespace(this.mLocation, ns[firstLocal], ns[firstLocal + 1]));
                firstLocal += 2;
            }
            this.mNsList = l;
        }
        return this.mNsList.iterator();
    }

    @Override // com.ctc.wstx.util.BaseNsContext
    public void outputNamespaceDeclarations(Writer w) throws IOException {
        String[] ns = this.mNamespaces;
        int len = this.mNsLength;
        for (int i = this.mFirstLocalNs; i < len; i += 2) {
            w.write(32);
            w.write(CanonicalizerBase.XMLNS);
            String prefix = ns[i];
            if (prefix != null && prefix.length() > 0) {
                w.write(58);
                w.write(prefix);
            }
            w.write("=\"");
            w.write(ns[i + 1]);
            w.write(34);
        }
    }

    @Override // com.ctc.wstx.util.BaseNsContext
    public void outputNamespaceDeclarations(XMLStreamWriter w) throws XMLStreamException {
        String[] ns = this.mNamespaces;
        int len = this.mNsLength;
        for (int i = this.mFirstLocalNs; i < len; i += 2) {
            String nsURI = ns[i + 1];
            String prefix = ns[i];
            if (prefix != null && prefix.length() > 0) {
                w.writeNamespace(prefix, nsURI);
            } else {
                w.writeDefaultNamespace(nsURI);
            }
        }
    }
}
