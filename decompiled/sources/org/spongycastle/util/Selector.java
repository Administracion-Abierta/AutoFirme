package org.spongycastle.util;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/util/Selector.class */
public interface Selector<T> extends Cloneable {
    boolean match(T t);

    Object clone();
}
