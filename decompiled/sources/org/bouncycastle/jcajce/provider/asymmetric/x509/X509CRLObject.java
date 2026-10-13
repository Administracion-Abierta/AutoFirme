package org.bouncycastle.jcajce.provider.asymmetric.x509;

import java.io.IOException;
import java.security.cert.CRLException;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/x509/X509CRLObject.class */
class X509CRLObject extends X509CRLImpl {
    private final Object cacheLock;
    private X509CRLInternal internalCRLValue;
    private volatile boolean hashValueSet;
    private volatile int hashValue;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/x509/X509CRLObject$X509CRLException.class */
    private static class X509CRLException extends CRLException {
        private final Throwable cause;

        X509CRLException(String str, Throwable th) {
            super(str);
            this.cause = th;
        }

        X509CRLException(Throwable th) {
            this.cause = th;
        }

        @Override // java.lang.Throwable
        public Throwable getCause() {
            return this.cause;
        }
    }

    X509CRLObject(JcaJceHelper jcaJceHelper, CertificateList certificateList) throws CRLException {
        super(jcaJceHelper, certificateList, createSigAlgName(certificateList), createSigAlgParams(certificateList), isIndirectCRL(certificateList));
        this.cacheLock = new Object();
    }

    @Override // java.security.cert.X509CRL
    public byte[] getEncoded() throws CRLException {
        return Arrays.clone(getInternalCRL().getEncoded());
    }

    @Override // java.security.cert.X509CRL
    public boolean equals(Object obj) {
        ASN1BitString signature;
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof X509CRLObject)) {
            return getInternalCRL().equals(obj);
        }
        X509CRLObject x509CRLObject = (X509CRLObject) obj;
        if (this.hashValueSet && x509CRLObject.hashValueSet) {
            if (this.hashValue != x509CRLObject.hashValue) {
                return false;
            }
        } else if ((null == this.internalCRLValue || null == x509CRLObject.internalCRLValue) && null != (signature = this.c.getSignature()) && !signature.equals((ASN1Primitive) x509CRLObject.c.getSignature())) {
            return false;
        }
        return getInternalCRL().equals(x509CRLObject.getInternalCRL());
    }

    @Override // java.security.cert.X509CRL
    public int hashCode() {
        if (!this.hashValueSet) {
            this.hashValue = getInternalCRL().hashCode();
            this.hashValueSet = true;
        }
        return this.hashValue;
    }

    private X509CRLInternal getInternalCRL() {
        X509CRLInternal x509CRLInternal;
        synchronized (this.cacheLock) {
            if (null != this.internalCRLValue) {
                return this.internalCRLValue;
            }
            byte[] encoded = null;
            X509CRLException x509CRLException = null;
            try {
                encoded = this.c.getEncoded("DER");
            } catch (IOException e) {
                x509CRLException = new X509CRLException(e);
            }
            X509CRLInternal x509CRLInternal2 = new X509CRLInternal(this.bcHelper, this.c, this.sigAlgName, this.sigAlgParams, this.isIndirect, encoded, x509CRLException);
            synchronized (this.cacheLock) {
                if (null == this.internalCRLValue) {
                    this.internalCRLValue = x509CRLInternal2;
                }
                x509CRLInternal = this.internalCRLValue;
            }
            return x509CRLInternal;
        }
    }

    private static String createSigAlgName(CertificateList certificateList) throws CRLException {
        try {
            return X509SignatureUtil.getSignatureName(certificateList.getSignatureAlgorithm());
        } catch (Exception e) {
            throw new X509CRLException("CRL contents invalid: " + e.getMessage(), e);
        }
    }

    private static byte[] createSigAlgParams(CertificateList certificateList) throws CRLException {
        try {
            ASN1Encodable parameters = certificateList.getSignatureAlgorithm().getParameters();
            if (null == parameters) {
                return null;
            }
            return parameters.toASN1Primitive().getEncoded("DER");
        } catch (Exception e) {
            throw new CRLException("CRL contents invalid: " + e);
        }
    }

    private static boolean isIndirectCRL(CertificateList certificateList) throws CRLException {
        try {
            byte[] extensionOctets = getExtensionOctets(certificateList, Extension.issuingDistributionPoint);
            if (null == extensionOctets) {
                return false;
            }
            return IssuingDistributionPoint.getInstance(extensionOctets).isIndirectCRL();
        } catch (Exception e) {
            throw new ExtCRLException("Exception reading IssuingDistributionPoint", e);
        }
    }
}
