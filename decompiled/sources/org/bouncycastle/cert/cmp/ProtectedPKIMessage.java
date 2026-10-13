package org.bouncycastle.cert.cmp;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CMPObjectIdentifiers;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.PBEMacCalculatorProvider;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/cmp/ProtectedPKIMessage.class */
public class ProtectedPKIMessage {
    private PKIMessage pkiMessage;

    public ProtectedPKIMessage(GeneralPKIMessage generalPKIMessage) {
        if (!generalPKIMessage.hasProtection()) {
            throw new IllegalArgumentException("PKIMessage not protected");
        }
        this.pkiMessage = generalPKIMessage.toASN1Structure();
    }

    ProtectedPKIMessage(PKIMessage pKIMessage) {
        if (pKIMessage.getHeader().getProtectionAlg() == null) {
            throw new IllegalArgumentException("PKIMessage not protected");
        }
        this.pkiMessage = pKIMessage;
    }

    public PKIHeader getHeader() {
        return this.pkiMessage.getHeader();
    }

    public PKIBody getBody() {
        return this.pkiMessage.getBody();
    }

    public PKIMessage toASN1Structure() {
        return this.pkiMessage;
    }

    public boolean hasPasswordBasedMacProtection() {
        return CMPObjectIdentifiers.passwordBasedMac.equals((ASN1Primitive) getProtectionAlgorithm().getAlgorithm());
    }

    public AlgorithmIdentifier getProtectionAlgorithm() {
        return this.pkiMessage.getHeader().getProtectionAlg();
    }

    public X509CertificateHolder[] getCertificates() {
        CMPCertificate[] extraCerts = this.pkiMessage.getExtraCerts();
        if (extraCerts == null) {
            return new X509CertificateHolder[0];
        }
        X509CertificateHolder[] x509CertificateHolderArr = new X509CertificateHolder[extraCerts.length];
        for (int i = 0; i != extraCerts.length; i++) {
            x509CertificateHolderArr[i] = new X509CertificateHolder(extraCerts[i].getX509v3PKCert());
        }
        return x509CertificateHolderArr;
    }

    public boolean verify(ContentVerifierProvider contentVerifierProvider) throws CMPException {
        try {
            return verifySignature(this.pkiMessage.getProtection().getOctets(), contentVerifierProvider.get(getProtectionAlgorithm()));
        } catch (Exception e) {
            throw new CMPException("unable to verify signature: " + e.getMessage(), e);
        }
    }

    public boolean verify(PBEMacCalculatorProvider pBEMacCalculatorProvider, char[] cArr) throws CMPException {
        try {
            MacCalculator macCalculator = pBEMacCalculatorProvider.get(getProtectionAlgorithm(), cArr);
            CMPUtil.derEncodeToStream(createProtected(), macCalculator.getOutputStream());
            return Arrays.constantTimeAreEqual(macCalculator.getMac(), this.pkiMessage.getProtection().getOctets());
        } catch (Exception e) {
            throw new CMPException("unable to verify MAC: " + e.getMessage(), e);
        }
    }

    private boolean verifySignature(byte[] bArr, ContentVerifier contentVerifier) {
        CMPUtil.derEncodeToStream(createProtected(), contentVerifier.getOutputStream());
        return contentVerifier.verify(bArr);
    }

    private DERSequence createProtected() {
        return new DERSequence(this.pkiMessage.getHeader(), this.pkiMessage.getBody());
    }
}
