package org.bouncycastle.asn1.cmc;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/cmc/RevokeRequest.class */
public class RevokeRequest extends ASN1Object {
    private final X500Name name;
    private final ASN1Integer serialNumber;
    private final CRLReason reason;
    private ASN1GeneralizedTime invalidityDate;
    private ASN1OctetString passphrase;
    private ASN1UTF8String comment;

    public RevokeRequest(X500Name x500Name, ASN1Integer aSN1Integer, CRLReason cRLReason, ASN1GeneralizedTime aSN1GeneralizedTime, ASN1OctetString aSN1OctetString, ASN1UTF8String aSN1UTF8String) {
        this.name = x500Name;
        this.serialNumber = aSN1Integer;
        this.reason = cRLReason;
        this.invalidityDate = aSN1GeneralizedTime;
        this.passphrase = aSN1OctetString;
        this.comment = aSN1UTF8String;
    }

    private RevokeRequest(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() < 3 || aSN1Sequence.size() > 6) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.name = X500Name.getInstance(aSN1Sequence.getObjectAt(0));
        this.serialNumber = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(1));
        this.reason = CRLReason.getInstance(aSN1Sequence.getObjectAt(2));
        int i = 3;
        if (aSN1Sequence.size() > 3 && (aSN1Sequence.getObjectAt(3).toASN1Primitive() instanceof ASN1GeneralizedTime)) {
            i = 3 + 1;
            this.invalidityDate = ASN1GeneralizedTime.getInstance(aSN1Sequence.getObjectAt(3));
        }
        if (aSN1Sequence.size() > i && (aSN1Sequence.getObjectAt(i).toASN1Primitive() instanceof ASN1OctetString)) {
            int i2 = i;
            i++;
            this.passphrase = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(i2));
        }
        if (aSN1Sequence.size() <= i || !(aSN1Sequence.getObjectAt(i).toASN1Primitive() instanceof ASN1UTF8String)) {
            return;
        }
        this.comment = ASN1UTF8String.getInstance(aSN1Sequence.getObjectAt(i));
    }

    public static RevokeRequest getInstance(Object obj) {
        if (obj instanceof RevokeRequest) {
            return (RevokeRequest) obj;
        }
        if (obj != null) {
            return new RevokeRequest(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public X500Name getName() {
        return this.name;
    }

    public BigInteger getSerialNumber() {
        return this.serialNumber.getValue();
    }

    public CRLReason getReason() {
        return this.reason;
    }

    public ASN1GeneralizedTime getInvalidityDate() {
        return this.invalidityDate;
    }

    public void setInvalidityDate(ASN1GeneralizedTime aSN1GeneralizedTime) {
        this.invalidityDate = aSN1GeneralizedTime;
    }

    public ASN1OctetString getPassphrase() {
        return this.passphrase;
    }

    public void setPassphrase(ASN1OctetString aSN1OctetString) {
        this.passphrase = aSN1OctetString;
    }

    public DERUTF8String getComment() {
        return (null == this.comment || (this.comment instanceof DERUTF8String)) ? (DERUTF8String) this.comment : new DERUTF8String(this.comment.getString());
    }

    public ASN1UTF8String getCommentUTF8() {
        return this.comment;
    }

    public void setComment(ASN1UTF8String aSN1UTF8String) {
        this.comment = aSN1UTF8String;
    }

    public byte[] getPassPhrase() {
        if (this.passphrase != null) {
            return Arrays.clone(this.passphrase.getOctets());
        }
        return null;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(6);
        aSN1EncodableVector.add(this.name);
        aSN1EncodableVector.add(this.serialNumber);
        aSN1EncodableVector.add(this.reason);
        if (this.invalidityDate != null) {
            aSN1EncodableVector.add(this.invalidityDate);
        }
        if (this.passphrase != null) {
            aSN1EncodableVector.add(this.passphrase);
        }
        if (this.comment != null) {
            aSN1EncodableVector.add(this.comment);
        }
        return new DERSequence(aSN1EncodableVector);
    }
}
