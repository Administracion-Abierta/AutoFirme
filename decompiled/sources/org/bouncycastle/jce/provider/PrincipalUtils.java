package org.bouncycastle.jce.provider;

import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.jcajce.interfaces.BCX509Certificate;
import org.bouncycastle.x509.X509AttributeCertificate;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/provider/PrincipalUtils.class */
class PrincipalUtils {
    PrincipalUtils() {
    }

    static X500Name getCA(TrustAnchor trustAnchor) {
        return getX500Name(notNull(trustAnchor).getCA());
    }

    static X500Name getEncodedIssuerPrincipal(Object obj) {
        return obj instanceof X509Certificate ? getIssuerPrincipal((X509Certificate) obj) : getX500Name((X500Principal) ((X509AttributeCertificate) obj).getIssuer().getPrincipals()[0]);
    }

    /* JADX WARN: Multi-variable type inference failed */
    static X500Name getIssuerPrincipal(X509Certificate x509Certificate) {
        return x509Certificate instanceof BCX509Certificate ? notNull(((BCX509Certificate) x509Certificate).getIssuerX500Name()) : getX500Name(notNull(x509Certificate).getIssuerX500Principal());
    }

    static X500Name getIssuerPrincipal(X509CRL x509crl) {
        return getX500Name(notNull(x509crl).getIssuerX500Principal());
    }

    /* JADX WARN: Multi-variable type inference failed */
    static X500Name getSubjectPrincipal(X509Certificate x509Certificate) {
        return x509Certificate instanceof BCX509Certificate ? notNull(((BCX509Certificate) x509Certificate).getSubjectX500Name()) : getX500Name(notNull(x509Certificate).getSubjectX500Principal());
    }

    static X500Name getX500Name(X500Principal x500Principal) {
        return notNull(X500Name.getInstance(getEncoded(x500Principal)));
    }

    static X500Name getX500Name(X500NameStyle x500NameStyle, X500Principal x500Principal) {
        return notNull(X500Name.getInstance(x500NameStyle, getEncoded(x500Principal)));
    }

    private static byte[] getEncoded(X500Principal x500Principal) {
        return notNull(notNull(x500Principal).getEncoded());
    }

    private static byte[] notNull(byte[] bArr) {
        if (null == bArr) {
            throw new IllegalStateException();
        }
        return bArr;
    }

    private static TrustAnchor notNull(TrustAnchor trustAnchor) {
        if (null == trustAnchor) {
            throw new IllegalStateException();
        }
        return trustAnchor;
    }

    private static X509Certificate notNull(X509Certificate x509Certificate) {
        if (null == x509Certificate) {
            throw new IllegalStateException();
        }
        return x509Certificate;
    }

    private static X509CRL notNull(X509CRL x509crl) {
        if (null == x509crl) {
            throw new IllegalStateException();
        }
        return x509crl;
    }

    private static X500Name notNull(X500Name x500Name) {
        if (null == x500Name) {
            throw new IllegalStateException();
        }
        return x500Name;
    }

    private static X500Principal notNull(X500Principal x500Principal) {
        if (null == x500Principal) {
            throw new IllegalStateException();
        }
        return x500Principal;
    }
}
