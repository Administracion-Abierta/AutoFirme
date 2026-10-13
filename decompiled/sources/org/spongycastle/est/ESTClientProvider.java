package org.spongycastle.est;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/est/ESTClientProvider.class */
public interface ESTClientProvider {
    ESTClient makeClient() throws ESTException;

    boolean isTrusted();
}
