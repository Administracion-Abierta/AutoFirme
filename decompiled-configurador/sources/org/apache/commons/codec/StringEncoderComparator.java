package org.apache.commons.codec;

import java.util.Comparator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/StringEncoderComparator.class */
public class StringEncoderComparator implements Comparator {
    private final StringEncoder stringEncoder;

    @Deprecated
    public StringEncoderComparator() {
        this.stringEncoder = null;
    }

    public StringEncoderComparator(StringEncoder stringEncoder) {
        this.stringEncoder = stringEncoder;
    }

    @Override // java.util.Comparator
    public int compare(Object o1, Object o2) {
        int compareCode;
        try {
            Comparable<Comparable<?>> s1 = (Comparable) this.stringEncoder.encode(o1);
            Comparable<?> s2 = (Comparable) this.stringEncoder.encode(o2);
            compareCode = s1.compareTo(s2);
        } catch (EncoderException e) {
            compareCode = 0;
        }
        return compareCode;
    }
}
