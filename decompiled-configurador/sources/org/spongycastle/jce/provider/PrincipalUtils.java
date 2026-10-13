package org.spongycastle.jce.provider;

import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.x509.X509AttributeCertificate;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/provider/PrincipalUtils.class */
class PrincipalUtils {
    PrincipalUtils() {
    }

    static X500Name getSubjectPrincipal(X509Certificate cert) {
        return X500Name.getInstance(cert.getSubjectX500Principal().getEncoded());
    }

    static X500Name getIssuerPrincipal(X509CRL crl) {
        return X500Name.getInstance(crl.getIssuerX500Principal().getEncoded());
    }

    static X500Name getIssuerPrincipal(X509Certificate cert) {
        return X500Name.getInstance(cert.getIssuerX500Principal().getEncoded());
    }

    static X500Name getCA(TrustAnchor trustAnchor) {
        return X500Name.getInstance(trustAnchor.getCA().getEncoded());
    }

    static X500Name getEncodedIssuerPrincipal(Object cert) {
        if (cert instanceof X509Certificate) {
            return getIssuerPrincipal((X509Certificate) cert);
        }
        return X500Name.getInstance(((X500Principal) ((X509AttributeCertificate) cert).getIssuer().getPrincipals()[0]).getEncoded());
    }
}
