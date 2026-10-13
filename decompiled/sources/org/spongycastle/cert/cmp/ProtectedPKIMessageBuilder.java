package org.spongycastle.cert.cmp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.cmp.CMPCertificate;
import org.spongycastle.asn1.cmp.InfoTypeAndValue;
import org.spongycastle.asn1.cmp.PKIBody;
import org.spongycastle.asn1.cmp.PKIFreeText;
import org.spongycastle.asn1.cmp.PKIHeader;
import org.spongycastle.asn1.cmp.PKIHeaderBuilder;
import org.spongycastle.asn1.cmp.PKIMessage;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.MacCalculator;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/cmp/ProtectedPKIMessageBuilder.class */
public class ProtectedPKIMessageBuilder {
    private PKIHeaderBuilder hdrBuilder;
    private PKIBody body;
    private List generalInfos;
    private List extraCerts;

    public ProtectedPKIMessageBuilder(GeneralName sender, GeneralName recipient) {
        this(2, sender, recipient);
    }

    public ProtectedPKIMessageBuilder(int pvno, GeneralName sender, GeneralName recipient) {
        this.generalInfos = new ArrayList();
        this.extraCerts = new ArrayList();
        this.hdrBuilder = new PKIHeaderBuilder(pvno, sender, recipient);
    }

    public ProtectedPKIMessageBuilder setTransactionID(byte[] tid) {
        this.hdrBuilder.setTransactionID(tid);
        return this;
    }

    public ProtectedPKIMessageBuilder setFreeText(PKIFreeText freeText) {
        this.hdrBuilder.setFreeText(freeText);
        return this;
    }

    public ProtectedPKIMessageBuilder addGeneralInfo(InfoTypeAndValue genInfo) {
        this.generalInfos.add(genInfo);
        return this;
    }

    public ProtectedPKIMessageBuilder setMessageTime(Date time) {
        this.hdrBuilder.setMessageTime(new ASN1GeneralizedTime(time));
        return this;
    }

    public ProtectedPKIMessageBuilder setRecipKID(byte[] kid) {
        this.hdrBuilder.setRecipKID(kid);
        return this;
    }

    public ProtectedPKIMessageBuilder setRecipNonce(byte[] nonce) {
        this.hdrBuilder.setRecipNonce(nonce);
        return this;
    }

    public ProtectedPKIMessageBuilder setSenderKID(byte[] kid) {
        this.hdrBuilder.setSenderKID(kid);
        return this;
    }

    public ProtectedPKIMessageBuilder setSenderNonce(byte[] nonce) {
        this.hdrBuilder.setSenderNonce(nonce);
        return this;
    }

    public ProtectedPKIMessageBuilder setBody(PKIBody body) {
        this.body = body;
        return this;
    }

    public ProtectedPKIMessageBuilder addCMPCertificate(X509CertificateHolder extraCert) {
        this.extraCerts.add(extraCert);
        return this;
    }

    public ProtectedPKIMessage build(MacCalculator macCalculator) throws CMPException {
        finaliseHeader(macCalculator.getAlgorithmIdentifier());
        PKIHeader header = this.hdrBuilder.build();
        try {
            DERBitString protection = new DERBitString(calculateMac(macCalculator, header, this.body));
            return finaliseMessage(header, protection);
        } catch (IOException e) {
            throw new CMPException("unable to encode MAC input: " + e.getMessage(), e);
        }
    }

    public ProtectedPKIMessage build(ContentSigner signer) throws CMPException {
        finaliseHeader(signer.getAlgorithmIdentifier());
        PKIHeader header = this.hdrBuilder.build();
        try {
            DERBitString protection = new DERBitString(calculateSignature(signer, header, this.body));
            return finaliseMessage(header, protection);
        } catch (IOException e) {
            throw new CMPException("unable to encode signature input: " + e.getMessage(), e);
        }
    }

    private void finaliseHeader(AlgorithmIdentifier algorithmIdentifier) {
        this.hdrBuilder.setProtectionAlg(algorithmIdentifier);
        if (!this.generalInfos.isEmpty()) {
            InfoTypeAndValue[] genInfos = new InfoTypeAndValue[this.generalInfos.size()];
            this.hdrBuilder.setGeneralInfo((InfoTypeAndValue[]) this.generalInfos.toArray(genInfos));
        }
    }

    private ProtectedPKIMessage finaliseMessage(PKIHeader header, DERBitString protection) {
        if (!this.extraCerts.isEmpty()) {
            CMPCertificate[] cmpCerts = new CMPCertificate[this.extraCerts.size()];
            for (int i = 0; i != cmpCerts.length; i++) {
                cmpCerts[i] = new CMPCertificate(((X509CertificateHolder) this.extraCerts.get(i)).toASN1Structure());
            }
            return new ProtectedPKIMessage(new PKIMessage(header, this.body, protection, cmpCerts));
        }
        return new ProtectedPKIMessage(new PKIMessage(header, this.body, protection));
    }

    private byte[] calculateSignature(ContentSigner signer, PKIHeader header, PKIBody body) throws IOException {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(header);
        v.add(body);
        OutputStream sOut = signer.getOutputStream();
        sOut.write(new DERSequence(v).getEncoded("DER"));
        sOut.close();
        return signer.getSignature();
    }

    private byte[] calculateMac(MacCalculator macCalculator, PKIHeader header, PKIBody body) throws IOException {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(header);
        v.add(body);
        OutputStream sOut = macCalculator.getOutputStream();
        sOut.write(new DERSequence(v).getEncoded("DER"));
        sOut.close();
        return macCalculator.getMac();
    }
}
