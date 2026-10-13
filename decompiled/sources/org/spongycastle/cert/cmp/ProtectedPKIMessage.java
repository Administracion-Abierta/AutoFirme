package org.spongycastle.cert.cmp;

import java.io.IOException;
import java.io.OutputStream;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.cmp.CMPCertificate;
import org.spongycastle.asn1.cmp.CMPObjectIdentifiers;
import org.spongycastle.asn1.cmp.PBMParameter;
import org.spongycastle.asn1.cmp.PKIBody;
import org.spongycastle.asn1.cmp.PKIHeader;
import org.spongycastle.asn1.cmp.PKIMessage;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.crmf.PKMACBuilder;
import org.spongycastle.operator.ContentVerifier;
import org.spongycastle.operator.ContentVerifierProvider;
import org.spongycastle.operator.MacCalculator;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/cmp/ProtectedPKIMessage.class */
public class ProtectedPKIMessage {
    private PKIMessage pkiMessage;

    public ProtectedPKIMessage(GeneralPKIMessage pkiMessage) {
        if (!pkiMessage.hasProtection()) {
            throw new IllegalArgumentException("PKIMessage not protected");
        }
        this.pkiMessage = pkiMessage.toASN1Structure();
    }

    ProtectedPKIMessage(PKIMessage pkiMessage) {
        if (pkiMessage.getHeader().getProtectionAlg() == null) {
            throw new IllegalArgumentException("PKIMessage not protected");
        }
        this.pkiMessage = pkiMessage;
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
        return this.pkiMessage.getHeader().getProtectionAlg().getAlgorithm().equals(CMPObjectIdentifiers.passwordBasedMac);
    }

    public X509CertificateHolder[] getCertificates() {
        CMPCertificate[] certs = this.pkiMessage.getExtraCerts();
        if (certs == null) {
            return new X509CertificateHolder[0];
        }
        X509CertificateHolder[] res = new X509CertificateHolder[certs.length];
        for (int i = 0; i != certs.length; i++) {
            res[i] = new X509CertificateHolder(certs[i].getX509v3PKCert());
        }
        return res;
    }

    public boolean verify(ContentVerifierProvider verifierProvider) throws CMPException {
        try {
            ContentVerifier verifier = verifierProvider.get(this.pkiMessage.getHeader().getProtectionAlg());
            return verifySignature(this.pkiMessage.getProtection().getBytes(), verifier);
        } catch (Exception e) {
            throw new CMPException("unable to verify signature: " + e.getMessage(), e);
        }
    }

    public boolean verify(PKMACBuilder pkMacBuilder, char[] password) throws CMPException {
        if (!CMPObjectIdentifiers.passwordBasedMac.equals(this.pkiMessage.getHeader().getProtectionAlg().getAlgorithm())) {
            throw new CMPException("protection algorithm not mac based");
        }
        try {
            pkMacBuilder.setParameters(PBMParameter.getInstance(this.pkiMessage.getHeader().getProtectionAlg().getParameters()));
            MacCalculator calculator = pkMacBuilder.build(password);
            OutputStream macOut = calculator.getOutputStream();
            ASN1EncodableVector v = new ASN1EncodableVector();
            v.add(this.pkiMessage.getHeader());
            v.add(this.pkiMessage.getBody());
            macOut.write(new DERSequence(v).getEncoded("DER"));
            macOut.close();
            return Arrays.areEqual(calculator.getMac(), this.pkiMessage.getProtection().getBytes());
        } catch (Exception e) {
            throw new CMPException("unable to verify MAC: " + e.getMessage(), e);
        }
    }

    private boolean verifySignature(byte[] signature, ContentVerifier verifier) throws IOException {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.pkiMessage.getHeader());
        v.add(this.pkiMessage.getBody());
        OutputStream sOut = verifier.getOutputStream();
        sOut.write(new DERSequence(v).getEncoded("DER"));
        sOut.close();
        return verifier.verify(signature);
    }
}
