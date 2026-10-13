package org.spongycastle.asn1.cmp;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.GeneralName;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmp/PKIHeaderBuilder.class */
public class PKIHeaderBuilder {
    private ASN1Integer pvno;
    private GeneralName sender;
    private GeneralName recipient;
    private ASN1GeneralizedTime messageTime;
    private AlgorithmIdentifier protectionAlg;
    private ASN1OctetString senderKID;
    private ASN1OctetString recipKID;
    private ASN1OctetString transactionID;
    private ASN1OctetString senderNonce;
    private ASN1OctetString recipNonce;
    private PKIFreeText freeText;
    private ASN1Sequence generalInfo;

    public PKIHeaderBuilder(int pvno, GeneralName sender, GeneralName recipient) {
        this(new ASN1Integer(pvno), sender, recipient);
    }

    private PKIHeaderBuilder(ASN1Integer pvno, GeneralName sender, GeneralName recipient) {
        this.pvno = pvno;
        this.sender = sender;
        this.recipient = recipient;
    }

    public PKIHeaderBuilder setMessageTime(ASN1GeneralizedTime time) {
        this.messageTime = time;
        return this;
    }

    public PKIHeaderBuilder setProtectionAlg(AlgorithmIdentifier aid) {
        this.protectionAlg = aid;
        return this;
    }

    public PKIHeaderBuilder setSenderKID(byte[] kid) {
        return setSenderKID(kid == null ? null : new DEROctetString(kid));
    }

    public PKIHeaderBuilder setSenderKID(ASN1OctetString kid) {
        this.senderKID = kid;
        return this;
    }

    public PKIHeaderBuilder setRecipKID(byte[] kid) {
        return setRecipKID(kid == null ? null : new DEROctetString(kid));
    }

    public PKIHeaderBuilder setRecipKID(DEROctetString kid) {
        this.recipKID = kid;
        return this;
    }

    public PKIHeaderBuilder setTransactionID(byte[] tid) {
        return setTransactionID(tid == null ? null : new DEROctetString(tid));
    }

    public PKIHeaderBuilder setTransactionID(ASN1OctetString tid) {
        this.transactionID = tid;
        return this;
    }

    public PKIHeaderBuilder setSenderNonce(byte[] nonce) {
        return setSenderNonce(nonce == null ? null : new DEROctetString(nonce));
    }

    public PKIHeaderBuilder setSenderNonce(ASN1OctetString nonce) {
        this.senderNonce = nonce;
        return this;
    }

    public PKIHeaderBuilder setRecipNonce(byte[] nonce) {
        return setRecipNonce(nonce == null ? null : new DEROctetString(nonce));
    }

    public PKIHeaderBuilder setRecipNonce(ASN1OctetString nonce) {
        this.recipNonce = nonce;
        return this;
    }

    public PKIHeaderBuilder setFreeText(PKIFreeText text) {
        this.freeText = text;
        return this;
    }

    public PKIHeaderBuilder setGeneralInfo(InfoTypeAndValue genInfo) {
        return setGeneralInfo(makeGeneralInfoSeq(genInfo));
    }

    public PKIHeaderBuilder setGeneralInfo(InfoTypeAndValue[] genInfos) {
        return setGeneralInfo(makeGeneralInfoSeq(genInfos));
    }

    public PKIHeaderBuilder setGeneralInfo(ASN1Sequence seqOfInfoTypeAndValue) {
        this.generalInfo = seqOfInfoTypeAndValue;
        return this;
    }

    private static ASN1Sequence makeGeneralInfoSeq(InfoTypeAndValue generalInfo) {
        return new DERSequence(generalInfo);
    }

    private static ASN1Sequence makeGeneralInfoSeq(InfoTypeAndValue[] generalInfos) {
        ASN1Sequence genInfoSeq = null;
        if (generalInfos != null) {
            ASN1EncodableVector v = new ASN1EncodableVector();
            for (InfoTypeAndValue infoTypeAndValue : generalInfos) {
                v.add(infoTypeAndValue);
            }
            genInfoSeq = new DERSequence(v);
        }
        return genInfoSeq;
    }

    public PKIHeader build() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.pvno);
        v.add(this.sender);
        v.add(this.recipient);
        addOptional(v, 0, this.messageTime);
        addOptional(v, 1, this.protectionAlg);
        addOptional(v, 2, this.senderKID);
        addOptional(v, 3, this.recipKID);
        addOptional(v, 4, this.transactionID);
        addOptional(v, 5, this.senderNonce);
        addOptional(v, 6, this.recipNonce);
        addOptional(v, 7, this.freeText);
        addOptional(v, 8, this.generalInfo);
        this.messageTime = null;
        this.protectionAlg = null;
        this.senderKID = null;
        this.recipKID = null;
        this.transactionID = null;
        this.senderNonce = null;
        this.recipNonce = null;
        this.freeText = null;
        this.generalInfo = null;
        return PKIHeader.getInstance(new DERSequence(v));
    }

    private void addOptional(ASN1EncodableVector v, int tagNo, ASN1Encodable obj) {
        if (obj != null) {
            v.add(new DERTaggedObject(true, tagNo, obj));
        }
    }
}
