package org.apache.xml.security.stax.impl.util;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/impl/util/KeyValue.class */
public class KeyValue<E, K> {
    private E key;
    private K value;

    public KeyValue(E key, K value) {
        this.key = key;
        this.value = value;
    }

    public E getKey() {
        return this.key;
    }

    public K getValue() {
        return this.value;
    }
}
