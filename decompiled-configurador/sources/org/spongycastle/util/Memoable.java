package org.spongycastle.util;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/util/Memoable.class */
public interface Memoable {
    Memoable copy();

    void reset(Memoable memoable);
}
