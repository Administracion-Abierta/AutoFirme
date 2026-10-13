package org.spongycastle.asn1.cmp;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.GeneralName;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cmp/PKIHeader.class */
public class PKIHeader extends ASN1Object {
    public static final GeneralName NULL_NAME = new GeneralName(X500Name.getInstance(new DERSequence()));
    public static final int CMP_1999 = 1;
    public static final int CMP_2000 = 2;
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

    private PKIHeader(ASN1Sequence seq) {
        Enumeration en = seq.getObjects();
        this.pvno = ASN1Integer.getInstance(en.nextElement());
        this.sender = GeneralName.getInstance(en.nextElement());
        this.recipient = GeneralName.getInstance(en.nextElement());
        while (en.hasMoreElements()) {
            ASN1TaggedObject tObj = (ASN1TaggedObject) en.nextElement();
            switch (tObj.getTagNo()) {
                case 0:
                    this.messageTime = ASN1GeneralizedTime.getInstance(tObj, true);
                    break;
                case 1:
                    this.protectionAlg = AlgorithmIdentifier.getInstance(tObj, true);
                    break;
                case 2:
                    this.senderKID = ASN1OctetString.getInstance(tObj, true);
                    break;
                case 3:
                    this.recipKID = ASN1OctetString.getInstance(tObj, true);
                    break;
                case 4:
                    this.transactionID = ASN1OctetString.getInstance(tObj, true);
                    break;
                case 5:
                    this.senderNonce = ASN1OctetString.getInstance(tObj, true);
                    break;
                case 6:
                    this.recipNonce = ASN1OctetString.getInstance(tObj, true);
                    break;
                case 7:
                    this.freeText = PKIFreeText.getInstance(tObj, true);
                    break;
                case 8:
                    this.generalInfo = ASN1Sequence.getInstance(tObj, true);
                    break;
                default:
                    throw new IllegalArgumentException("unknown tag number: " + tObj.getTagNo());
            }
        }
    }

    public static PKIHeader getInstance(Object o) {
        if (o instanceof PKIHeader) {
            return (PKIHeader) o;
        }
        if (o != null) {
            return new PKIHeader(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public PKIHeader(int pvno, GeneralName sender, GeneralName recipient) {
        this(new ASN1Integer(pvno), sender, recipient);
    }

    private PKIHeader(ASN1Integer pvno, GeneralName sender, GeneralName recipient) {
        this.pvno = pvno;
        this.sender = sender;
        this.recipient = recipient;
    }

    public ASN1Integer getPvno() {
        return this.pvno;
    }

    public GeneralName getSender() {
        return this.sender;
    }

    public GeneralName getRecipient() {
        return this.recipient;
    }

    public ASN1GeneralizedTime getMessageTime() {
        return this.messageTime;
    }

    public AlgorithmIdentifier getProtectionAlg() {
        return this.protectionAlg;
    }

    public ASN1OctetString getSenderKID() {
        return this.senderKID;
    }

    public ASN1OctetString getRecipKID() {
        return this.recipKID;
    }

    public ASN1OctetString getTransactionID() {
        return this.transactionID;
    }

    public ASN1OctetString getSenderNonce() {
        return this.senderNonce;
    }

    public ASN1OctetString getRecipNonce() {
        return this.recipNonce;
    }

    public PKIFreeText getFreeText() {
        return this.freeText;
    }

    public InfoTypeAndValue[] getGeneralInfo() {
        if (this.generalInfo == null) {
            return null;
        }
        InfoTypeAndValue[] results = new InfoTypeAndValue[this.generalInfo.size()];
        for (int i = 0; i < results.length; i++) {
            results[i] = InfoTypeAndValue.getInstance(this.generalInfo.getObjectAt(i));
        }
        return results;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
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
        return new DERSequence(v);
    }

    private void addOptional(ASN1EncodableVector v, int tagNo, ASN1Encodable obj) {
        if (obj != null) {
            v.add(new DERTaggedObject(true, tagNo, obj));
        }
    }
}
