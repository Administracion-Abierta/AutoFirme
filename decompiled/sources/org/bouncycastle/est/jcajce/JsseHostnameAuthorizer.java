package org.bouncycastle.est.jcajce;

import java.io.IOException;
import javax.net.ssl.SSLSession;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/jcajce/JsseHostnameAuthorizer.class */
public interface JsseHostnameAuthorizer {
    boolean verified(String str, SSLSession sSLSession) throws IOException;
}
