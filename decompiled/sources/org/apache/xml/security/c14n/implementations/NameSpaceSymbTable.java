package org.apache.xml.security.c14n.implementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/c14n/implementations/NameSpaceSymbTable.class */
public class NameSpaceSymbTable {
    private static final String XMLNS = "xmlns";
    private SymbMap symb;
    private final List<SymbMap> level = new ArrayList();
    private boolean cloned = true;
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) NameSpaceSymbTable.class);
    private static final SymbMap initialMap = new SymbMap();

    static {
        NameSpaceSymbEntry ne = new NameSpaceSymbEntry("", null, true, "xmlns");
        ne.lastrendered = "";
        initialMap.put("xmlns", ne);
    }

    public NameSpaceSymbTable() {
        try {
            this.symb = initialMap.m1322clone();
        } catch (CloneNotSupportedException e) {
            LOG.error("Error cloning the initial map");
        }
    }

    public void getUnrenderedNodes(Collection<Attr> result) {
        for (NameSpaceSymbEntry nsEntry : this.symb.entrySet()) {
            if (!nsEntry.rendered && nsEntry.n != null) {
                NameSpaceSymbEntry nsEntry2 = nsEntry.m1320clone();
                needsClone();
                this.symb.put(nsEntry2.prefix, nsEntry2);
                nsEntry2.lastrendered = nsEntry2.uri;
                nsEntry2.rendered = true;
                result.add(nsEntry2.n);
            }
        }
    }

    public void outputNodePush() {
        push();
    }

    public void outputNodePop() {
        pop();
    }

    public void push() {
        this.level.add(null);
        this.cloned = false;
    }

    public void pop() {
        int size = this.level.size() - 1;
        Object ob = this.level.remove(size);
        if (ob != null) {
            this.symb = (SymbMap) ob;
            if (size == 0) {
                this.cloned = false;
                return;
            } else {
                this.cloned = this.level.get(size - 1) != this.symb;
                return;
            }
        }
        this.cloned = false;
    }

    final void needsClone() {
        if (!this.cloned) {
            this.level.set(this.level.size() - 1, this.symb);
            try {
                this.symb = this.symb.m1322clone();
            } catch (CloneNotSupportedException e) {
                LOG.error("Error cloning the symbol map");
            }
            this.cloned = true;
        }
    }

    public Attr getMapping(String prefix) {
        NameSpaceSymbEntry entry = this.symb.get(prefix);
        if (entry == null || entry.rendered) {
            return null;
        }
        NameSpaceSymbEntry entry2 = entry.m1320clone();
        needsClone();
        this.symb.put(prefix, entry2);
        entry2.rendered = true;
        entry2.lastrendered = entry2.uri;
        return entry2.n;
    }

    public Attr getMappingWithoutRendered(String prefix) {
        NameSpaceSymbEntry entry = this.symb.get(prefix);
        if (entry == null || entry.rendered) {
            return null;
        }
        return entry.n;
    }

    public boolean addMapping(String prefix, String uri, Attr n) {
        NameSpaceSymbEntry ob = this.symb.get(prefix);
        if (ob != null && uri.equals(ob.uri)) {
            return false;
        }
        NameSpaceSymbEntry ne = new NameSpaceSymbEntry(uri, n, false, prefix);
        needsClone();
        this.symb.put(prefix, ne);
        if (ob != null) {
            ne.lastrendered = ob.lastrendered;
            if (ob.lastrendered != null && ob.lastrendered.equals(uri)) {
                ne.rendered = true;
                return true;
            }
            return true;
        }
        return true;
    }

    public Node addMappingAndRender(String prefix, String uri, Attr n) {
        NameSpaceSymbEntry ob = this.symb.get(prefix);
        if (ob != null && uri.equals(ob.uri)) {
            if (!ob.rendered) {
                NameSpaceSymbEntry ob2 = ob.m1320clone();
                needsClone();
                this.symb.put(prefix, ob2);
                ob2.lastrendered = uri;
                ob2.rendered = true;
                return ob2.n;
            }
            return null;
        }
        NameSpaceSymbEntry ne = new NameSpaceSymbEntry(uri, n, true, prefix);
        ne.lastrendered = uri;
        needsClone();
        this.symb.put(prefix, ne);
        if (ob != null && ob.lastrendered != null && ob.lastrendered.equals(uri)) {
            ne.rendered = true;
            return null;
        }
        return ne.n;
    }

    public int getLevel() {
        return this.level.size();
    }

    public void removeMapping(String prefix) {
        NameSpaceSymbEntry ob = this.symb.get(prefix);
        if (ob != null) {
            needsClone();
            this.symb.put(prefix, null);
        }
    }

    public void removeMappingIfNotRender(String prefix) {
        NameSpaceSymbEntry ob = this.symb.get(prefix);
        if (ob != null && !ob.rendered) {
            needsClone();
            this.symb.put(prefix, null);
        }
    }

    public boolean removeMappingIfRender(String prefix) {
        NameSpaceSymbEntry ob = this.symb.get(prefix);
        if (ob != null && ob.rendered) {
            needsClone();
            this.symb.put(prefix, null);
            return false;
        }
        return false;
    }
}
