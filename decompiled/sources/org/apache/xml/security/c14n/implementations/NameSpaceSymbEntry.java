package org.apache.xml.security.c14n.implementations;

import org.w3c.dom.Attr;

/* JADX INFO: compiled from: NameSpaceSymbTable.java */
/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/c14n/implementations/NameSpaceSymbEntry.class */
class NameSpaceSymbEntry implements Cloneable {
    final String prefix;
    final String uri;
    final Attr n;
    String lastrendered = null;
    boolean rendered;

    NameSpaceSymbEntry(String name, Attr n, boolean rendered, String prefix) {
        this.rendered = false;
        this.uri = name;
        this.rendered = rendered;
        this.n = n;
        this.prefix = prefix;
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public NameSpaceSymbEntry m1320clone() {
        try {
            return (NameSpaceSymbEntry) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
