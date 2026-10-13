package org.spongycastle.est;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/est/TLSUniqueProvider.class */
public interface TLSUniqueProvider {
    boolean isTLSUniqueAvailable();

    byte[] getTLSUnique();
}
