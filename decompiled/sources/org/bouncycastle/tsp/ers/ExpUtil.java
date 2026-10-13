package org.bouncycastle.tsp.ers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/ers/ExpUtil.class */
class ExpUtil {
    ExpUtil() {
    }

    static IllegalStateException createIllegalState(String str, Throwable th) {
        return new IllegalStateException(str, th);
    }
}
