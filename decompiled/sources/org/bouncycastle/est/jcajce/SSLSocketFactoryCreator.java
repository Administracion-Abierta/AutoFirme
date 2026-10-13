package org.bouncycastle.est.jcajce;

import javax.net.ssl.SSLSocketFactory;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/jcajce/SSLSocketFactoryCreator.class */
public interface SSLSocketFactoryCreator {
    SSLSocketFactory createFactory() throws Exception;

    boolean isTrusted();
}
