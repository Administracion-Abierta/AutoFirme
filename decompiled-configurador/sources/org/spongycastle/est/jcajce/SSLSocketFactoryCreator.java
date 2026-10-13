package org.spongycastle.est.jcajce;

import javax.net.ssl.SSLSocketFactory;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/est/jcajce/SSLSocketFactoryCreator.class */
public interface SSLSocketFactoryCreator {
    SSLSocketFactory createFactory() throws Exception;

    boolean isTrusted();
}
