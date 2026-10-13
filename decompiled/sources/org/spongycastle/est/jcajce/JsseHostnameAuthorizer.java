package org.spongycastle.est.jcajce;

import java.io.IOException;
import javax.net.ssl.SSLSession;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/est/jcajce/JsseHostnameAuthorizer.class */
public interface JsseHostnameAuthorizer {
    boolean verified(String str, SSLSession sSLSession) throws IOException;
}
