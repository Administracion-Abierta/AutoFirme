package org.spongycastle.asn1.cmc;

import java.math.BigInteger;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERUTF8String;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.CRLReason;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cmc/RevokeRequest.class */
public class RevokeRequest extends ASN1Object {
    private final X500Name name;
    private final ASN1Integer serialNumber;
    private final CRLReason reason;
    private ASN1GeneralizedTime invalidityDate;
    private ASN1OctetString passphrase;
    private DERUTF8String comment;

    public RevokeRequest(X500Name name, ASN1Integer serialNumber, CRLReason reason, ASN1GeneralizedTime invalidityDate, ASN1OctetString passphrase, DERUTF8String comment) {
        this.name = name;
        this.serialNumber = serialNumber;
        this.reason = reason;
        this.invalidityDate = invalidityDate;
        this.passphrase = passphrase;
        this.comment = comment;
    }

    private RevokeRequest(ASN1Sequence seq) {
        if (seq.size() < 3 || seq.size() > 6) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.name = X500Name.getInstance(seq.getObjectAt(0));
        this.serialNumber = ASN1Integer.getInstance(seq.getObjectAt(1));
        this.reason = CRLReason.getInstance(seq.getObjectAt(2));
        int index = 3;
        if (seq.size() > 3 && (seq.getObjectAt(3).toASN1Primitive() instanceof ASN1GeneralizedTime)) {
            index = 3 + 1;
            this.invalidityDate = ASN1GeneralizedTime.getInstance(seq.getObjectAt(3));
        }
        if (seq.size() > index && (seq.getObjectAt(index).toASN1Primitive() instanceof ASN1OctetString)) {
            int i = index;
            index++;
            this.passphrase = ASN1OctetString.getInstance(seq.getObjectAt(i));
        }
        if (seq.size() > index && (seq.getObjectAt(index).toASN1Primitive() instanceof DERUTF8String)) {
            this.comment = DERUTF8String.getInstance(seq.getObjectAt(index));
        }
    }

    public static RevokeRequest getInstance(Object o) {
        if (o instanceof RevokeRequest) {
            return (RevokeRequest) o;
        }
        if (o != null) {
            return new RevokeRequest(ASN1Sequence.getInstance(o));
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

    public void setInvalidityDate(ASN1GeneralizedTime invalidityDate) {
        this.invalidityDate = invalidityDate;
    }

    public ASN1OctetString getPassphrase() {
        return this.passphrase;
    }

    public void setPassphrase(ASN1OctetString passphrase) {
        this.passphrase = passphrase;
    }

    public DERUTF8String getComment() {
        return this.comment;
    }

    public void setComment(DERUTF8String comment) {
        this.comment = comment;
    }

    public byte[] getPassPhrase() {
        if (this.passphrase != null) {
            return Arrays.clone(this.passphrase.getOctets());
        }
        return null;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.name);
        v.add(this.serialNumber);
        v.add(this.reason);
        if (this.invalidityDate != null) {
            v.add(this.invalidityDate);
        }
        if (this.passphrase != null) {
            v.add(this.passphrase);
        }
        if (this.comment != null) {
            v.add(this.comment);
        }
        return new DERSequence(v);
    }
}
