package org.spongycastle.jce;

import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.x509.TBSCertList;
import org.spongycastle.asn1.x509.TBSCertificateStructure;
import org.spongycastle.asn1.x509.X509Name;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/PrincipalUtil.class */
public class PrincipalUtil {
    public static X509Principal getIssuerX509Principal(X509Certificate cert) throws CertificateEncodingException {
        try {
            TBSCertificateStructure tbsCert = TBSCertificateStructure.getInstance(ASN1Primitive.fromByteArray(cert.getTBSCertificate()));
            return new X509Principal(X509Name.getInstance(tbsCert.getIssuer()));
        } catch (IOException e) {
            throw new CertificateEncodingException(e.toString());
        }
    }

    public static X509Principal getSubjectX509Principal(X509Certificate cert) throws CertificateEncodingException {
        try {
            TBSCertificateStructure tbsCert = TBSCertificateStructure.getInstance(ASN1Primitive.fromByteArray(cert.getTBSCertificate()));
            return new X509Principal(X509Name.getInstance(tbsCert.getSubject()));
        } catch (IOException e) {
            throw new CertificateEncodingException(e.toString());
        }
    }

    public static X509Principal getIssuerX509Principal(X509CRL crl) throws CRLException {
        try {
            TBSCertList tbsCertList = TBSCertList.getInstance(ASN1Primitive.fromByteArray(crl.getTBSCertList()));
            return new X509Principal(X509Name.getInstance(tbsCertList.getIssuer()));
        } catch (IOException e) {
            throw new CRLException(e.toString());
        }
    }
}
