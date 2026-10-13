package com.ctc.wstx.dtd;

import com.ctc.wstx.util.PrefixedName;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/PrefixedNameSet.class */
public abstract class PrefixedNameSet {
    public abstract boolean hasMultiple();

    public abstract boolean contains(PrefixedName prefixedName);

    public abstract void appendNames(StringBuilder sb, String str);

    protected PrefixedNameSet() {
    }

    public final String toString() {
        return toString(", ");
    }

    public final String toString(String sep) {
        StringBuilder sb = new StringBuilder();
        appendNames(sb, sep);
        return sb.toString();
    }
}
