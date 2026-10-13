package org.spongycastle.est;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/est/ESTClientProvider.class */
public interface ESTClientProvider {
    ESTClient makeClient() throws ESTException;

    boolean isTrusted();
}
