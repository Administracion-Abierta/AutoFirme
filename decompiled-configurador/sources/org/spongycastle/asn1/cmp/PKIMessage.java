package org.spongycastle.asn1.cmp;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cmp/PKIMessage.class */
public class PKIMessage extends ASN1Object {
    private PKIHeader header;
    private PKIBody body;
    private DERBitString protection;
    private ASN1Sequence extraCerts;

    private PKIMessage(ASN1Sequence seq) {
        Enumeration en = seq.getObjects();
        this.header = PKIHeader.getInstance(en.nextElement());
        this.body = PKIBody.getInstance(en.nextElement());
        while (en.hasMoreElements()) {
            ASN1TaggedObject tObj = (ASN1TaggedObject) en.nextElement();
            if (tObj.getTagNo() == 0) {
                this.protection = DERBitString.getInstance(tObj, true);
            } else {
                this.extraCerts = ASN1Sequence.getInstance(tObj, true);
            }
        }
    }

    public static PKIMessage getInstance(Object o) {
        if (o instanceof PKIMessage) {
            return (PKIMessage) o;
        }
        if (o != null) {
            return new PKIMessage(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public PKIMessage(PKIHeader header, PKIBody body, DERBitString protection, CMPCertificate[] extraCerts) {
        this.header = header;
        this.body = body;
        this.protection = protection;
        if (extraCerts != null) {
            ASN1EncodableVector v = new ASN1EncodableVector();
            for (CMPCertificate cMPCertificate : extraCerts) {
                v.add(cMPCertificate);
            }
            this.extraCerts = new DERSequence(v);
        }
    }

    public PKIMessage(PKIHeader header, PKIBody body, DERBitString protection) {
        this(header, body, protection, null);
    }

    public PKIMessage(PKIHeader header, PKIBody body) {
        this(header, body, null, null);
    }

    public PKIHeader getHeader() {
        return this.header;
    }

    public PKIBody getBody() {
        return this.body;
    }

    public DERBitString getProtection() {
        return this.protection;
    }

    public CMPCertificate[] getExtraCerts() {
        if (this.extraCerts == null) {
            return null;
        }
        CMPCertificate[] results = new CMPCertificate[this.extraCerts.size()];
        for (int i = 0; i < results.length; i++) {
            results[i] = CMPCertificate.getInstance(this.extraCerts.getObjectAt(i));
        }
        return results;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.header);
        v.add(this.body);
        addOptional(v, 0, this.protection);
        addOptional(v, 1, this.extraCerts);
        return new DERSequence(v);
    }

    private void addOptional(ASN1EncodableVector v, int tagNo, ASN1Encodable obj) {
        if (obj != null) {
            v.add(new DERTaggedObject(true, tagNo, obj));
        }
    }
}
