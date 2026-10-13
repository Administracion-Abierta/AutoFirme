package com.ctc.wstx.util;

import javax.xml.namespace.QName;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/util/PrefixedName.class */
public final class PrefixedName implements Comparable<PrefixedName> {
    private String mPrefix;
    private String mLocalName;
    volatile int mHash = 0;

    public PrefixedName(String prefix, String localName) {
        this.mLocalName = localName;
        this.mPrefix = (prefix == null || prefix.length() != 0) ? prefix : null;
    }

    public PrefixedName reset(String prefix, String localName) {
        this.mLocalName = localName;
        this.mPrefix = (prefix == null || prefix.length() != 0) ? prefix : null;
        this.mHash = 0;
        return this;
    }

    public static PrefixedName valueOf(QName n) {
        return new PrefixedName(n.getPrefix(), n.getLocalPart());
    }

    public String getPrefix() {
        return this.mPrefix;
    }

    public String getLocalName() {
        return this.mLocalName;
    }

    public boolean isaNsDeclaration() {
        if (this.mPrefix == null) {
            return this.mLocalName == CanonicalizerBase.XMLNS;
        }
        return this.mPrefix == CanonicalizerBase.XMLNS;
    }

    public boolean isXmlReservedAttr(boolean nsAware, String localName) {
        if (nsAware) {
            return CanonicalizerBase.XML == this.mPrefix && this.mLocalName == localName;
        }
        return this.mLocalName.length() == 4 + localName.length() && this.mLocalName.startsWith("xml:") && this.mLocalName.endsWith(localName);
    }

    public String toString() {
        if (this.mPrefix == null || this.mPrefix.length() == 0) {
            return this.mLocalName;
        }
        StringBuilder sb = new StringBuilder(this.mPrefix.length() + 1 + this.mLocalName.length());
        sb.append(this.mPrefix);
        sb.append(':');
        sb.append(this.mLocalName);
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PrefixedName)) {
            return false;
        }
        PrefixedName other = (PrefixedName) o;
        return this.mLocalName == other.mLocalName && this.mPrefix == other.mPrefix;
    }

    public int hashCode() {
        int hash = this.mHash;
        if (hash == 0) {
            hash = this.mLocalName.hashCode();
            if (this.mPrefix != null) {
                hash ^= this.mPrefix.hashCode();
            }
            this.mHash = hash;
        }
        return hash;
    }

    @Override // java.lang.Comparable
    public int compareTo(PrefixedName other) {
        String op = other.mPrefix;
        if (op == null || op.length() == 0) {
            if (this.mPrefix != null && this.mPrefix.length() > 0) {
                return 1;
            }
        } else {
            if (this.mPrefix == null || this.mPrefix.length() == 0) {
                return -1;
            }
            int result = this.mPrefix.compareTo(op);
            if (result != 0) {
                return result;
            }
        }
        return this.mLocalName.compareTo(other.mLocalName);
    }
}
