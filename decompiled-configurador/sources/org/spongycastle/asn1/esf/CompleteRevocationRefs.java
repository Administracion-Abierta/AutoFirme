package org.spongycastle.asn1.esf;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/esf/CompleteRevocationRefs.class */
public class CompleteRevocationRefs extends ASN1Object {
    private ASN1Sequence crlOcspRefs;

    public static CompleteRevocationRefs getInstance(Object obj) {
        if (obj instanceof CompleteRevocationRefs) {
            return (CompleteRevocationRefs) obj;
        }
        if (obj != null) {
            return new CompleteRevocationRefs(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private CompleteRevocationRefs(ASN1Sequence seq) {
        Enumeration seqEnum = seq.getObjects();
        while (seqEnum.hasMoreElements()) {
            CrlOcspRef.getInstance(seqEnum.nextElement());
        }
        this.crlOcspRefs = seq;
    }

    public CompleteRevocationRefs(CrlOcspRef[] crlOcspRefs) {
        this.crlOcspRefs = new DERSequence(crlOcspRefs);
    }

    public CrlOcspRef[] getCrlOcspRefs() {
        CrlOcspRef[] result = new CrlOcspRef[this.crlOcspRefs.size()];
        for (int idx = 0; idx < result.length; idx++) {
            result[idx] = CrlOcspRef.getInstance(this.crlOcspRefs.getObjectAt(idx));
        }
        return result;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.crlOcspRefs;
    }
}
