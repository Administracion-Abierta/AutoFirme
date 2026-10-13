package org.spongycastle.asn1.crmf;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/crmf/CertRequest.class */
public class CertRequest extends ASN1Object {
    private ASN1Integer certReqId;
    private CertTemplate certTemplate;
    private Controls controls;

    private CertRequest(ASN1Sequence seq) {
        this.certReqId = new ASN1Integer(ASN1Integer.getInstance(seq.getObjectAt(0)).getValue());
        this.certTemplate = CertTemplate.getInstance(seq.getObjectAt(1));
        if (seq.size() > 2) {
            this.controls = Controls.getInstance(seq.getObjectAt(2));
        }
    }

    public static CertRequest getInstance(Object o) {
        if (o instanceof CertRequest) {
            return (CertRequest) o;
        }
        if (o != null) {
            return new CertRequest(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public CertRequest(int certReqId, CertTemplate certTemplate, Controls controls) {
        this(new ASN1Integer(certReqId), certTemplate, controls);
    }

    public CertRequest(ASN1Integer certReqId, CertTemplate certTemplate, Controls controls) {
        this.certReqId = certReqId;
        this.certTemplate = certTemplate;
        this.controls = controls;
    }

    public ASN1Integer getCertReqId() {
        return this.certReqId;
    }

    public CertTemplate getCertTemplate() {
        return this.certTemplate;
    }

    public Controls getControls() {
        return this.controls;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.certReqId);
        v.add(this.certTemplate);
        if (this.controls != null) {
            v.add(this.controls);
        }
        return new DERSequence(v);
    }
}
