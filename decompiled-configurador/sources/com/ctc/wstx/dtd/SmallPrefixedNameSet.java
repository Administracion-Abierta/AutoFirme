package com.ctc.wstx.dtd;

import com.ctc.wstx.util.PrefixedName;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dtd/SmallPrefixedNameSet.class */
public final class SmallPrefixedNameSet extends PrefixedNameSet {
    final boolean mNsAware;
    final String[] mStrings;

    public SmallPrefixedNameSet(boolean nsAware, PrefixedName[] names) {
        this.mNsAware = nsAware;
        int len = names.length;
        if (len == 0) {
            throw new IllegalStateException("Trying to construct empty PrefixedNameSet");
        }
        this.mStrings = new String[nsAware ? len + len : len];
        int out = 0;
        for (PrefixedName nk : names) {
            if (nsAware) {
                int i = out;
                out++;
                this.mStrings[i] = nk.getPrefix();
            }
            int i2 = out;
            out++;
            this.mStrings[i2] = nk.getLocalName();
        }
    }

    @Override // com.ctc.wstx.dtd.PrefixedNameSet
    public boolean hasMultiple() {
        return this.mStrings.length > 1;
    }

    @Override // com.ctc.wstx.dtd.PrefixedNameSet
    public boolean contains(PrefixedName name) {
        int len = this.mStrings.length;
        String ln = name.getLocalName();
        String[] strs = this.mStrings;
        if (this.mNsAware) {
            String prefix = name.getPrefix();
            if (strs[1] == ln && strs[0] == prefix) {
                return true;
            }
            for (int i = 2; i < len; i += 2) {
                if (strs[i + 1] == ln && strs[i] == prefix) {
                    return true;
                }
            }
            return false;
        }
        if (strs[0] == ln) {
            return true;
        }
        for (int i2 = 1; i2 < len; i2++) {
            if (strs[i2] == ln) {
                return true;
            }
        }
        return false;
    }

    @Override // com.ctc.wstx.dtd.PrefixedNameSet
    public void appendNames(StringBuilder sb, String sep) {
        int i = 0;
        while (i < this.mStrings.length) {
            if (i > 0) {
                sb.append(sep);
            }
            if (this.mNsAware) {
                int i2 = i;
                i++;
                String prefix = this.mStrings[i2];
                if (prefix != null) {
                    sb.append(prefix);
                    sb.append(':');
                }
            }
            int i3 = i;
            i++;
            sb.append(this.mStrings[i3]);
        }
    }
}
