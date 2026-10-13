package org.bouncycastle.est;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/TLSUniqueProvider.class */
public interface TLSUniqueProvider {
    boolean isTLSUniqueAvailable();

    byte[] getTLSUnique();
}
