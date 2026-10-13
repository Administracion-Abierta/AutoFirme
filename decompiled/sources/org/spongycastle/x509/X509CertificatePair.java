package org.spongycastle.x509;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.x509.Certificate;
import org.spongycastle.asn1.x509.CertificatePair;
import org.spongycastle.jcajce.util.BCJcaJceHelper;
import org.spongycastle.jcajce.util.JcaJceHelper;
import org.spongycastle.jce.provider.X509CertificateObject;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/x509/X509CertificatePair.class */
public class X509CertificatePair {
    private final JcaJceHelper bcHelper = new BCJcaJceHelper();
    private X509Certificate forward;
    private X509Certificate reverse;

    public X509CertificatePair(X509Certificate forward, X509Certificate reverse) {
        this.forward = forward;
        this.reverse = reverse;
    }

    public X509CertificatePair(CertificatePair pair) throws CertificateParsingException {
        if (pair.getForward() != null) {
            this.forward = new X509CertificateObject(pair.getForward());
        }
        if (pair.getReverse() != null) {
            this.reverse = new X509CertificateObject(pair.getReverse());
        }
    }

    public byte[] getEncoded() throws CertificateEncodingException {
        Certificate f = null;
        Certificate r = null;
        try {
            if (this.forward != null) {
                f = Certificate.getInstance(new ASN1InputStream(this.forward.getEncoded()).readObject());
                if (f == null) {
                    throw new CertificateEncodingException("unable to get encoding for forward");
                }
            }
            if (this.reverse != null) {
                r = Certificate.getInstance(new ASN1InputStream(this.reverse.getEncoded()).readObject());
                if (r == null) {
                    throw new CertificateEncodingException("unable to get encoding for reverse");
                }
            }
            return new CertificatePair(f, r).getEncoded("DER");
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

    public boolean equals(Object o) {
        if (o == null || !(o instanceof X509CertificatePair)) {
            return false;
        }
        X509CertificatePair pair = (X509CertificatePair) o;
        boolean equalReverse = true;
        boolean equalForward = true;
        if (this.forward != null) {
            equalForward = this.forward.equals(pair.forward);
        } else if (pair.forward != null) {
            equalForward = false;
        }
        if (this.reverse != null) {
            equalReverse = this.reverse.equals(pair.reverse);
        } else if (pair.reverse != null) {
            equalReverse = false;
        }
        return equalForward && equalReverse;
    }

    public int hashCode() {
        int hash = -1;
        if (this.forward != null) {
            hash = (-1) ^ this.forward.hashCode();
        }
        if (this.reverse != null) {
            hash = (hash * 17) ^ this.reverse.hashCode();
        }
        return hash;
    }
}
