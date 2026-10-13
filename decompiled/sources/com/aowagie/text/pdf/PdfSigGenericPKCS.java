package com.aowagie.text.pdf;

import com.aowagie.text.ExceptionConverter;
import es.gob.afirma.core.signers.AOSignConstants;
import java.io.ByteArrayOutputStream;
import java.security.PrivateKey;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfSigGenericPKCS.class */
public abstract class PdfSigGenericPKCS extends PdfSignature {
    String hashAlgorithm;
    String provider;
    private PdfPKCS7 pkcs;
    private String name;
    private byte[] externalDigest;
    private byte[] externalRSAdata;
    private String digestEncryptionAlgorithm;

    private PdfSigGenericPKCS(PdfName filter, PdfName subFilter) {
        super(filter, subFilter);
        this.provider = null;
    }

    void setSignInfo(PrivateKey privKey, Certificate[] certChain, CRL[] crlList) {
        try {
            this.pkcs = new PdfPKCS7(privKey, certChain, crlList, this.hashAlgorithm, this.provider, PdfName.ADBE_PKCS7_SHA1.equals(get(PdfName.SUBFILTER)));
            this.pkcs.setExternalDigest(this.externalDigest, this.externalRSAdata, this.digestEncryptionAlgorithm);
            if (PdfName.ADBE_X509_RSA_SHA1.equals(get(PdfName.SUBFILTER))) {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                for (Certificate element : certChain) {
                    bout.write(element.getEncoded());
                }
                bout.close();
                setCert(bout.toByteArray());
                setContents(this.pkcs.getEncodedPKCS1());
            } else {
                setContents(this.pkcs.getEncodedPKCS7());
            }
            this.name = PdfPKCS7.getSubjectFields(this.pkcs.getSigningCertificate()).getField("CN");
            if (this.name != null) {
                put(PdfName.NAME, new PdfString(this.name, PdfObject.TEXT_UNICODE));
            }
            this.pkcs = new PdfPKCS7(privKey, certChain, crlList, this.hashAlgorithm, this.provider, PdfName.ADBE_PKCS7_SHA1.equals(get(PdfName.SUBFILTER)));
            this.pkcs.setExternalDigest(this.externalDigest, this.externalRSAdata, this.digestEncryptionAlgorithm);
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    void setExternalDigest(byte[] digest, byte[] RSAdata, String digestEncryptionAlgorithm) {
        this.externalDigest = digest;
        this.externalRSAdata = RSAdata;
        this.digestEncryptionAlgorithm = digestEncryptionAlgorithm;
    }

    public String getName() {
        return this.name;
    }

    public PdfPKCS7 getSigner() {
        return this.pkcs;
    }

    public byte[] getSignerContents() {
        if (PdfName.ADBE_X509_RSA_SHA1.equals(get(PdfName.SUBFILTER))) {
            return this.pkcs.getEncodedPKCS1();
        }
        return this.pkcs.getEncodedPKCS7();
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfSigGenericPKCS$VeriSign.class */
    static class VeriSign extends PdfSigGenericPKCS {
        public VeriSign() {
            super(PdfName.VERISIGN_PPKVS, PdfName.ADBE_PKCS7_DETACHED);
            this.hashAlgorithm = MessageDigestAlgorithms.MD5;
            put(PdfName.R, new PdfNumber(65537));
        }

        VeriSign(String provider) {
            this();
            this.provider = provider;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfSigGenericPKCS$PPKLite.class */
    static class PPKLite extends PdfSigGenericPKCS {
        public PPKLite() {
            super(PdfName.ADOBE_PPKLITE, PdfName.ADBE_X509_RSA_SHA1);
            this.hashAlgorithm = AOSignConstants.DIGEST_ALGORITHM_SHA1;
            put(PdfName.R, new PdfNumber(65541));
        }

        PPKLite(String provider) {
            this();
            this.provider = provider;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfSigGenericPKCS$PPKMS.class */
    static class PPKMS extends PdfSigGenericPKCS {
        public PPKMS() {
            super(PdfName.ADOBE_PPKMS, PdfName.ADBE_PKCS7_SHA1);
            this.hashAlgorithm = AOSignConstants.DIGEST_ALGORITHM_SHA1;
        }

        PPKMS(String provider) {
            this();
            this.provider = provider;
        }
    }
}
