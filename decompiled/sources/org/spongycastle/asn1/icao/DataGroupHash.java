package org.spongycastle.asn1.icao;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/icao/DataGroupHash.class */
public class DataGroupHash extends ASN1Object {
    ASN1Integer dataGroupNumber;
    ASN1OctetString dataGroupHashValue;

    public static DataGroupHash getInstance(Object obj) {
        if (obj instanceof DataGroupHash) {
            return (DataGroupHash) obj;
        }
        if (obj != null) {
            return new DataGroupHash(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private DataGroupHash(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.dataGroupNumber = ASN1Integer.getInstance(e.nextElement());
        this.dataGroupHashValue = ASN1OctetString.getInstance(e.nextElement());
    }

    public DataGroupHash(int dataGroupNumber, ASN1OctetString dataGroupHashValue) {
        this.dataGroupNumber = new ASN1Integer(dataGroupNumber);
        this.dataGroupHashValue = dataGroupHashValue;
    }

    public int getDataGroupNumber() {
        return this.dataGroupNumber.getValue().intValue();
    }

    public ASN1OctetString getDataGroupHashValue() {
        return this.dataGroupHashValue;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector seq = new ASN1EncodableVector();
        seq.add(this.dataGroupNumber);
        seq.add(this.dataGroupHashValue);
        return new DERSequence(seq);
    }
}
