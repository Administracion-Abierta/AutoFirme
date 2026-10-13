package org.bouncycastle.util;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/Selector.class */
public interface Selector<T> extends Cloneable {
    boolean match(T t);

    Object clone();
}
