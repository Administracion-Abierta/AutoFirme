package org.bouncycastle.x509;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificatePair;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.provider.X509CertificateObject;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/x509/X509CertificatePair.class */
public class X509CertificatePair {
    private final JcaJceHelper bcHelper = new BCJcaJceHelper();
    private X509Certificate forward;
    private X509Certificate reverse;

    public X509CertificatePair(X509Certificate x509Certificate, X509Certificate x509Certificate2) {
        this.forward = x509Certificate;
        this.reverse = x509Certificate2;
    }

    public X509CertificatePair(CertificatePair certificatePair) throws CertificateParsingException {
        if (certificatePair.getForward() != null) {
            this.forward = new X509CertificateObject(certificatePair.getForward());
        }
        if (certificatePair.getReverse() != null) {
            this.reverse = new X509CertificateObject(certificatePair.getReverse());
        }
    }

    public byte[] getEncoded() throws CertificateEncodingException {
        Certificate certificate = null;
        Certificate certificate2 = null;
        try {
            if (this.forward != null) {
                certificate = Certificate.getInstance(new ASN1InputStream(this.forward.getEncoded()).readObject());
                if (certificate == null) {
                    throw new CertificateEncodingException("unable to get encoding for forward");
                }
            }
            if (this.reverse != null) {
                certificate2 = Certificate.getInstance(new ASN1InputStream(this.reverse.getEncoded()).readObject());
                if (certificate2 == null) {
                    throw new CertificateEncodingException("unable to get encoding for reverse");
                }
            }
            return new CertificatePair(certificate, certificate2).getEncoded("DER");
        } catch (IOException e) {
            throw new ExtCertificateEncodingException(e.toString(), e);
        } catch (IllegalArgumentException e2) {
            throw new ExtCertificateEncodingException(e2.toString(), e2);
        }
    }

    public X509Certificate getForward() {
        return this.forward;
    }

    public X509Certificate getReverse() {
        return this.reverse;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof X509CertificatePair)) {
            return false;
        }
        X509CertificatePair x509CertificatePair = (X509CertificatePair) obj;
        boolean zEquals = true;
        boolean zEquals2 = true;
        if (this.forward != null) {
            zEquals2 = this.forward.equals(x509CertificatePair.forward);
        } else if (x509CertificatePair.forward != null) {
            zEquals2 = false;
        }
        if (this.reverse != null) {
            zEquals = this.reverse.equals(x509CertificatePair.reverse);
        } else if (x509CertificatePair.reverse != null) {
            zEquals = false;
        }
        return zEquals2 && zEquals;
    }

    public int hashCode() {
        int iHashCode = -1;
        if (this.forward != null) {
            iHashCode = (-1) ^ this.forward.hashCode();
        }
        if (this.reverse != null) {
            iHashCode = (iHashCode * 17) ^ this.reverse.hashCode();
        }
        return iHashCode;
    }
}
