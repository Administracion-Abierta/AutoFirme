package org.spongycastle.asn1.bc;

import java.math.BigInteger;
import java.util.Date;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1GeneralizedTime;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERGeneralizedTime;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERUTF8String;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/bc/ObjectData.class */
public class ObjectData extends ASN1Object {
    private final BigInteger type;
    private final String identifier;
    private final ASN1GeneralizedTime creationDate;
    private final ASN1GeneralizedTime lastModifiedDate;
    private final ASN1OctetString data;
    private final String comment;

    private ObjectData(ASN1Sequence seq) {
        this.type = ASN1Integer.getInstance(seq.getObjectAt(0)).getValue();
        this.identifier = DERUTF8String.getInstance(seq.getObjectAt(1)).getString();
        this.creationDate = ASN1GeneralizedTime.getInstance(seq.getObjectAt(2));
        this.lastModifiedDate = ASN1GeneralizedTime.getInstance(seq.getObjectAt(3));
        this.data = ASN1OctetString.getInstance(seq.getObjectAt(4));
        this.comment = seq.size() == 6 ? DERUTF8String.getInstance(seq.getObjectAt(5)).getString() : null;
    }

    public ObjectData(BigInteger type, String identifier, Date creationDate, Date lastModifiedDate, byte[] data, String comment) {
        this.type = type;
        this.identifier = identifier;
        this.creationDate = new DERGeneralizedTime(creationDate);
        this.lastModifiedDate = new DERGeneralizedTime(lastModifiedDate);
        this.data = new DEROctetString(Arrays.clone(data));
        this.comment = comment;
    }

    public static ObjectData getInstance(Object obj) {
        if (obj instanceof ObjectData) {
            return (ObjectData) obj;
        }
        if (obj != null) {
            return new ObjectData(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public String getComment() {
        return this.comment;
    }

    public ASN1GeneralizedTime getCreationDate() {
        return this.creationDate;
    }

    public byte[] getData() {
        return Arrays.clone(this.data.getOctets());
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public ASN1GeneralizedTime getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public BigInteger getType() {
        return this.type;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(this.type));
        v.add(new DERUTF8String(this.identifier));
        v.add(this.creationDate);
        v.add(this.lastModifiedDate);
        v.add(this.data);
        if (this.comment != null) {
            v.add(new DERUTF8String(this.comment));
        }
        return new DERSequence(v);
    }
}
