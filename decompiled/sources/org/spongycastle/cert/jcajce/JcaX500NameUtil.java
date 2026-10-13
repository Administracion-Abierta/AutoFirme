package org.spongycastle.cert.jcajce;

import java.security.cert.X509Certificate;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x500.X500NameStyle;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/jcajce/JcaX500NameUtil.class */
public class JcaX500NameUtil {
    public static X500Name getIssuer(X509Certificate certificate) {
        return X500Name.getInstance(certificate.getIssuerX500Principal().getEncoded());
    }

    public static X500Name getSubject(X509Certificate certificate) {
        return X500Name.getInstance(certificate.getSubjectX500Principal().getEncoded());
    }

    public static X500Name getIssuer(X500NameStyle style, X509Certificate certificate) {
        return X500Name.getInstance(style, certificate.getIssuerX500Principal().getEncoded());
    }

    public static X500Name getSubject(X500NameStyle style, X509Certificate certificate) {
        return X500Name.getInstance(style, certificate.getSubjectX500Principal().getEncoded());
    }
}
