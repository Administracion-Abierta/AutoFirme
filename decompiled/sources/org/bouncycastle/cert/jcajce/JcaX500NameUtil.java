package org.bouncycastle.cert.jcajce;

import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.jcajce.interfaces.BCX509Certificate;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/jcajce/JcaX500NameUtil.class */
public class JcaX500NameUtil {
    /* JADX WARN: Multi-variable type inference failed */
    public static X500Name getIssuer(X509Certificate x509Certificate) {
        return x509Certificate instanceof BCX509Certificate ? notNull(((BCX509Certificate) x509Certificate).getIssuerX500Name()) : getX500Name(x509Certificate.getIssuerX500Principal());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static X500Name getIssuer(X500NameStyle x500NameStyle, X509Certificate x509Certificate) {
        return x509Certificate instanceof BCX509Certificate ? X500Name.getInstance(x500NameStyle, notNull(((BCX509Certificate) x509Certificate).getIssuerX500Name())) : getX500Name(x500NameStyle, x509Certificate.getIssuerX500Principal());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static X500Name getSubject(X509Certificate x509Certificate) {
        return x509Certificate instanceof BCX509Certificate ? notNull(((BCX509Certificate) x509Certificate).getSubjectX500Name()) : getX500Name(x509Certificate.getSubjectX500Principal());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static X500Name getSubject(X500NameStyle x500NameStyle, X509Certificate x509Certificate) {
        return x509Certificate instanceof BCX509Certificate ? X500Name.getInstance(x500NameStyle, notNull(((BCX509Certificate) x509Certificate).getSubjectX500Name())) : getX500Name(x500NameStyle, x509Certificate.getSubjectX500Principal());
    }

    public static X500Name getX500Name(X500Principal x500Principal) {
        return X500Name.getInstance(getEncoded(x500Principal));
    }

    public static X500Name getX500Name(X500NameStyle x500NameStyle, X500Principal x500Principal) {
        return X500Name.getInstance(x500NameStyle, getEncoded(x500Principal));
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

    private static byte[] getEncoded(X500Principal x500Principal) {
        return notNull(x500Principal).getEncoded();
    }
}
