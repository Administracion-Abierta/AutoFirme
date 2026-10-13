package org.bouncycastle.asn1.eac;

import java.util.Enumeration;
import java.util.Hashtable;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/eac/Flags.class */
public class Flags {
    int value;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/eac/Flags$StringJoiner.class */
    private static class StringJoiner {
        String mSeparator;
        boolean First = true;
        StringBuffer b = new StringBuffer();

        public StringJoiner(String str) {
            this.mSeparator = str;
        }

        public void add(String str) {
            if (this.First) {
                this.First = false;
            } else {
                this.b.append(this.mSeparator);
            }
            this.b.append(str);
        }

        public String toString() {
            return this.b.toString();
        }
    }

    public Flags() {
        this.value = 0;
    }

    public Flags(int i) {
        this.value = 0;
        this.value = i;
    }

    public void set(int i) {
        this.value |= i;
    }

    public boolean isSet(int i) {
        return (this.value & i) != 0;
    }

    public int getFlags() {
        return this.value;
    }

    String decode(Hashtable hashtable) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        Enumeration enumerationKeys = hashtable.keys();
        while (enumerationKeys.hasMoreElements()) {
            Integer num = (Integer) enumerationKeys.nextElement();
            if (isSet(num.intValue())) {
                stringJoiner.add((String) hashtable.get(num));
            }
        }
        return stringJoiner.toString();
    }
}
