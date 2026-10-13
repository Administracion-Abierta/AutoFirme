package org.bouncycastle.util;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/Memoable.class */
public interface Memoable {
    Memoable copy();

    void reset(Memoable memoable);
}
