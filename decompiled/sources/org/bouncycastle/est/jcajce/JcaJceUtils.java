package org.bouncycastle.est.jcajce;

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
import java.util.Iterator;
import java.util.Set;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/jcajce/JcaJceUtils.class */
public class JcaJceUtils {
    public static X509TrustManager getTrustAllTrustManager() {
        return new X509TrustManager() { // from class: org.bouncycastle.est.jcajce.JcaJceUtils.1
            @Override // javax.net.ssl.X509TrustManager
            public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
                throw new IllegalStateException("attempt to authenticate client connection");
            }

            @Override // javax.net.ssl.X509TrustManager
            public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
            }

            @Override // javax.net.ssl.X509TrustManager
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    public static X509TrustManager[] getCertPathTrustManager(final Set<TrustAnchor> set, final CRL[] crlArr) {
        final X509Certificate[] x509CertificateArr = new X509Certificate[set.size()];
        int i = 0;
        Iterator<TrustAnchor> it = set.iterator();
        while (it.hasNext()) {
            int i2 = i;
            i++;
            x509CertificateArr[i2] = it.next().getTrustedCert();
        }
        return new X509TrustManager[]{new X509TrustManager() { // from class: org.bouncycastle.est.jcajce.JcaJceUtils.2
            @Override // javax.net.ssl.X509TrustManager
            public void checkClientTrusted(X509Certificate[] x509CertificateArr2, String str) throws CertificateException {
                throw new IllegalStateException("attempt to authenticate client connection");
            }

            @Override // javax.net.ssl.X509TrustManager
            public void checkServerTrusted(X509Certificate[] x509CertificateArr2, String str) throws CertificateException {
                try {
                    CertStore certStore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(Arrays.asList(x509CertificateArr2)), BouncyCastleProvider.PROVIDER_NAME);
                    CertPathBuilder certPathBuilder = CertPathBuilder.getInstance("PKIX", BouncyCastleProvider.PROVIDER_NAME);
                    X509CertSelector x509CertSelector = new X509CertSelector();
                    x509CertSelector.setCertificate(x509CertificateArr2[0]);
                    PKIXBuilderParameters pKIXBuilderParameters = new PKIXBuilderParameters((Set<TrustAnchor>) set, x509CertSelector);
                    pKIXBuilderParameters.addCertStore(certStore);
                    if (crlArr != null) {
                        pKIXBuilderParameters.setRevocationEnabled(true);
                        pKIXBuilderParameters.addCertStore(CertStore.getInstance("Collection", new CollectionCertStoreParameters(Arrays.asList(crlArr))));
                    } else {
                        pKIXBuilderParameters.setRevocationEnabled(false);
                    }
                    JcaJceUtils.validateServerCertUsage(x509CertificateArr2[0]);
                } catch (CertificateException e) {
                    throw e;
                } catch (GeneralSecurityException e2) {
                    throw new CertificateException("unable to process certificates: " + e2.getMessage(), e2);
                }
            }

            @Override // javax.net.ssl.X509TrustManager
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509CertificateArr2 = new X509Certificate[x509CertificateArr.length];
                System.arraycopy(x509CertificateArr, 0, x509CertificateArr2, 0, x509CertificateArr2.length);
                return x509CertificateArr2;
            }
        }};
    }

    public static void validateServerCertUsage(X509Certificate x509Certificate) throws CertificateException {
        try {
            X509CertificateHolder x509CertificateHolder = new X509CertificateHolder(x509Certificate.getEncoded());
            KeyUsage keyUsageFromExtensions = KeyUsage.fromExtensions(x509CertificateHolder.getExtensions());
            if (keyUsageFromExtensions != null) {
                if (keyUsageFromExtensions.hasUsages(4)) {
                    throw new CertificateException("Key usage must not contain keyCertSign");
                }
                if (!keyUsageFromExtensions.hasUsages(128) && !keyUsageFromExtensions.hasUsages(32)) {
                    throw new CertificateException("Key usage must be none, digitalSignature or keyEncipherment");
                }
            }
            ExtendedKeyUsage extendedKeyUsageFromExtensions = ExtendedKeyUsage.fromExtensions(x509CertificateHolder.getExtensions());
            if (extendedKeyUsageFromExtensions != null && !extendedKeyUsageFromExtensions.hasKeyPurposeId(KeyPurposeId.id_kp_serverAuth) && !extendedKeyUsageFromExtensions.hasKeyPurposeId(KeyPurposeId.id_kp_msSGC) && !extendedKeyUsageFromExtensions.hasKeyPurposeId(KeyPurposeId.id_kp_nsSGC)) {
                throw new CertificateException("Certificate extended key usage must include serverAuth, msSGC or nsSGC");
            }
        } catch (CertificateException e) {
            throw e;
        } catch (Exception e2) {
            throw new CertificateException(e2.getMessage(), e2);
        }
    }

    public static KeyManagerFactory createKeyManagerFactory(String str, String str2, KeyStore keyStore, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, NoSuchProviderException {
        KeyManagerFactory keyManagerFactory;
        if (str == null && str2 == null) {
            keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        } else {
            keyManagerFactory = str2 == null ? KeyManagerFactory.getInstance(str) : KeyManagerFactory.getInstance(str, str2);
        }
        keyManagerFactory.init(keyStore, cArr);
        return keyManagerFactory;
    }
}
