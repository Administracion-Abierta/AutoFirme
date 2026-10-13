package es.gob.afirma.core.misc.http;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/misc/http/SSLConfig.class */
public class SSLConfig {
    private HostnameVerifier hostnameVerifier = null;
    private SSLSocketFactory socketFactory = null;

    public HostnameVerifier getHostnameVerifier() {
        return this.hostnameVerifier;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public SSLSocketFactory getSSLSocketFactory() {
        return this.socketFactory;
    }

    public void setSSLSocketFactory(SSLSocketFactory sSLSocketFactory) {
        this.socketFactory = sSLSocketFactory;
    }
}
