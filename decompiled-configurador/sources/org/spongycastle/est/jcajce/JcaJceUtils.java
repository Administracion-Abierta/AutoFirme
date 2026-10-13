package org.spongycastle.est.jcajce;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CRL;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Set;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.spongycastle.asn1.x509.ExtendedKeyUsage;
import org.spongycastle.asn1.x509.KeyPurposeId;
import org.spongycastle.asn1.x509.KeyUsage;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.jce.provider.BouncyCastleProvider;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/est/jcajce/JcaJceUtils.class */
public class JcaJceUtils {
    public static X509TrustManager getTrustAllTrustManager() {
        return new X509TrustManager() { // from class: org.spongycastle.est.jcajce.JcaJceUtils.1
            @Override // javax.net.ssl.X509TrustManager
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override // javax.net.ssl.X509TrustManager
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override // javax.net.ssl.X509TrustManager
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    public static X509TrustManager[] getCertPathTrustManager(final Set<TrustAnchor> anchors, final CRL[] revocationLists) {
        final X509Certificate[] x509CertificateTrustAnchors = new X509Certificate[anchors.size()];
        int c = 0;
        for (TrustAnchor ta : anchors) {
            int i = c;
            c++;
            x509CertificateTrustAnchors[i] = ta.getTrustedCert();
        }
        return new X509TrustManager[]{new X509TrustManager() { // from class: org.spongycastle.est.jcajce.JcaJceUtils.2
            @Override // javax.net.ssl.X509TrustManager
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override // javax.net.ssl.X509TrustManager
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                try {
                    CertStore certStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(Arrays.asList(x509Certificates)), BouncyCastleProvider.PROVIDER_NAME);
                    CertPathBuilder pathBuilder = CertPathBuilder.getInstance("PKIX", BouncyCastleProvider.PROVIDER_NAME);
                    X509CertSelector constraints = new X509CertSelector();
                    constraints.setCertificate(x509Certificates[0]);
                    PKIXBuilderParameters param = new PKIXBuilderParameters((Set<TrustAnchor>) anchors, constraints);
                    param.addCertStore(certStore);
                    if (revocationLists != null) {
                        param.setRevocationEnabled(true);
                        param.addCertStore(CertStore.getInstance("Collection", new CollectionCertStoreParameters(Arrays.asList(revocationLists))));
                    } else {
                        param.setRevocationEnabled(false);
                    }
                    JcaJceUtils.validateServerCertUsage(x509Certificates[0]);
                } catch (CertificateException e) {
                    throw e;
                } catch (GeneralSecurityException e2) {
                    throw new CertificateException("unable to process certificates: " + e2.getMessage(), e2);
                }
            }

            @Override // javax.net.ssl.X509TrustManager
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] rv = new X509Certificate[x509CertificateTrustAnchors.length];
                System.arraycopy(x509CertificateTrustAnchors, 0, rv, 0, rv.length);
                return rv;
            }
        }};
    }

    public static void validateServerCertUsage(X509Certificate x509Certificate) throws CertificateException {
        try {
            X509CertificateHolder cert = new X509CertificateHolder(x509Certificate.getEncoded());
            KeyUsage keyUsage = KeyUsage.fromExtensions(cert.getExtensions());
            if (keyUsage != null) {
                if (keyUsage.hasUsages(4)) {
                    throw new CertificateException("Key usage must not contain keyCertSign");
                }
                if (!keyUsage.hasUsages(128) && !keyUsage.hasUsages(32)) {
                    throw new CertificateException("Key usage must be none, digitalSignature or keyEncipherment");
                }
            }
            ExtendedKeyUsage extendedKeyUsage = ExtendedKeyUsage.fromExtensions(cert.getExtensions());
            if (extendedKeyUsage != null && !extendedKeyUsage.hasKeyPurposeId(KeyPurposeId.id_kp_serverAuth) && !extendedKeyUsage.hasKeyPurposeId(KeyPurposeId.id_kp_msSGC) && !extendedKeyUsage.hasKeyPurposeId(KeyPurposeId.id_kp_nsSGC)) {
                throw new CertificateException("Certificate extended key usage must include serverAuth, msSGC or nsSGC");
            }
        } catch (CertificateException c) {
            throw c;
        } catch (Exception e) {
            throw new CertificateException(e.getMessage(), e);
        }
    }

    public static KeyManagerFactory createKeyManagerFactory(String type, String provider, KeyStore clientKeyStore, char[] clientKeyStorePass) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, NoSuchProviderException {
        KeyManagerFactory keyManagerFactory;
        if (type == null && provider == null) {
            keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        } else if (provider == null) {
            keyManagerFactory = KeyManagerFactory.getInstance(type);
        } else {
            keyManagerFactory = KeyManagerFactory.getInstance(type, provider);
        }
        keyManagerFactory.init(clientKeyStore, clientKeyStorePass);
        return keyManagerFactory;
    }
}
