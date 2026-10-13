package es.gob.afirma.core.misc.http;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/misc/http/SslSecurityManager.class */
public final class SslSecurityManager {
    private static final String KEYSTORE = "javax.net.ssl.keyStore";
    private static final String KEYSTORE_PASS = "javax.net.ssl.keyStorePassword";
    private static final String KEYSTORE_TYPE = "javax.net.ssl.keyStoreType";
    private static final String KEYSTORE_DEFAULT_TYPE = "JKS";
    private static final String SSL_CONTEXT = "SSL";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    static final TrustManager[] DUMMY_TRUST_MANAGER = {new X509TrustManager() { // from class: es.gob.afirma.core.misc.http.SslSecurityManager.1
        @Override // javax.net.ssl.X509TrustManager
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override // javax.net.ssl.X509TrustManager
        public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) {
        }

        @Override // javax.net.ssl.X509TrustManager
        public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) {
        }
    }};
    static final HostnameVerifier DUMMY_HOSTNAME_VERIFIER = new HostnameVerifier() { // from class: es.gob.afirma.core.misc.http.SslSecurityManager.2
        @Override // javax.net.ssl.HostnameVerifier
        public boolean verify(String str, SSLSession sSLSession) {
            return true;
        }
    };
    private static final HostnameVerifier DEFAULT_HOSTNAME_VERIFIER = HttpsURLConnection.getDefaultHostnameVerifier();
    private static final SSLSocketFactory DEFAULT_SSL_SOCKET_FACTORY = HttpsURLConnection.getDefaultSSLSocketFactory();
    private static final SecureRandom secureRandom = new SecureRandom();
    private static boolean afirmaTrustStoreConfigured = false;

    private SslSecurityManager() {
    }

    public static void disableSslChecks(HttpsURLConnection httpsURLConnection) throws GeneralSecurityException {
        SSLContext sSLContext = SSLContext.getInstance(SSL_CONTEXT);
        sSLContext.init(null, DUMMY_TRUST_MANAGER, secureRandom);
        httpsURLConnection.setSSLSocketFactory(sSLContext.getSocketFactory());
        httpsURLConnection.setHostnameVerifier(DUMMY_HOSTNAME_VERIFIER);
    }

    public static void disableSslChecks() throws GeneralSecurityException {
        setTrustManagerAndKeyManager(DUMMY_TRUST_MANAGER, DUMMY_HOSTNAME_VERIFIER, null, null);
    }

    public static void enableSslChecks() throws GeneralSecurityException, IOException {
        if (afirmaTrustStoreConfigured) {
            configureAfirmaTrustManagers();
        } else {
            HttpsURLConnection.setDefaultSSLSocketFactory(DEFAULT_SSL_SOCKET_FACTORY);
            HttpsURLConnection.setDefaultHostnameVerifier(DEFAULT_HOSTNAME_VERIFIER);
        }
        LOGGER.fine("Habilitadas comprobaciones SSL");
    }

    private static void setTrustManagerAndKeyManager(TrustManager[] trustManagerArr, HostnameVerifier hostnameVerifier, KeyStore keyStore, PasswordCallback passwordCallback) throws NoSuchAlgorithmException, KeyManagementException {
        KeyManager[] keyManager;
        if (trustManagerArr == null || trustManagerArr.length < 1) {
            throw new IllegalArgumentException("Es necesario proporcionar al menos un TrustManager");
        }
        if (hostnameVerifier == null) {
            throw new IllegalArgumentException("Es necesario proporcionar un HostnameVerifier");
        }
        SSLContext sSLContext = SSLContext.getInstance(SSL_CONTEXT);
        try {
            keyManager = getKeyManager(keyStore, passwordCallback);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "No ha sido posible obtener el KeyManager con el KeyStore '" + System.getProperty(KEYSTORE) + "', se usara null: " + e, (Throwable) e);
            keyManager = null;
        }
        sSLContext.init(keyManager, trustManagerArr, secureRandom);
        HttpsURLConnection.setDefaultSSLSocketFactory(sSLContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x00fe */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static javax.net.ssl.KeyManager[] getKeyManager(java.security.KeyStore r4, javax.security.auth.callback.PasswordCallback r5) throws java.security.NoSuchAlgorithmException, java.security.UnrecoverableKeyException, java.io.IOException, java.security.KeyStoreException, java.security.cert.CertificateException {
        /*
            Method dump skipped, instruction units count: 332
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.core.misc.http.SslSecurityManager.getKeyManager(java.security.KeyStore, javax.security.auth.callback.PasswordCallback):javax.net.ssl.KeyManager[]");
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x00c8 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void configureAfirmaTrustManagers() throws java.security.GeneralSecurityException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 381
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.core.misc.http.SslSecurityManager.configureAfirmaTrustManagers():void");
    }

    public static void setSslSecurity(HttpsURLConnection httpsURLConnection, SSLConfig sSLConfig) {
        if (sSLConfig.getSSLSocketFactory() != null) {
            httpsURLConnection.setSSLSocketFactory(sSLConfig.getSSLSocketFactory());
        }
        if (sSLConfig.getHostnameVerifier() != null) {
            httpsURLConnection.setHostnameVerifier(sSLConfig.getHostnameVerifier());
        }
    }
}
