package org.spongycastle.est;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/est/TLSUniqueProvider.class */
public interface TLSUniqueProvider {
    boolean isTLSUniqueAvailable();

    byte[] getTLSUnique();
}
