package org.apache.oro.util;

/* JADX INFO: loaded from: autofirma.jar:org/apache/oro/util/Cache.class */
public interface Cache {
    void addElement(Object obj, Object obj2);

    Object getElement(Object obj);

    int size();

    int capacity();
}
