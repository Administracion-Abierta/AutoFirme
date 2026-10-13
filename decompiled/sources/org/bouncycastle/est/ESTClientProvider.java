package org.bouncycastle.est;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/ESTClientProvider.class */
public interface ESTClientProvider {
    ESTClient makeClient() throws ESTException;

    boolean isTrusted();
}
