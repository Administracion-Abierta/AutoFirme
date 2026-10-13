package org.spongycastle.asn1.cmc;

import org.spongycastle.asn1.ASN1Boolean;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.crmf.CertTemplate;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/cmc/ModCertTemplate.class */
public class ModCertTemplate extends ASN1Object {
    private final BodyPartPath pkiDataReference;
    private final BodyPartList certReferences;
    private final boolean replace;
    private final CertTemplate certTemplate;

    public ModCertTemplate(BodyPartPath pkiDataReference, BodyPartList certReferences, boolean replace, CertTemplate certTemplate) {
        this.pkiDataReference = pkiDataReference;
        this.certReferences = certReferences;
        this.replace = replace;
        this.certTemplate = certTemplate;
    }

    private ModCertTemplate(ASN1Sequence seq) {
        if (seq.size() != 4 && seq.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.pkiDataReference = BodyPartPath.getInstance(seq.getObjectAt(0));
        this.certReferences = BodyPartList.getInstance(seq.getObjectAt(1));
        if (seq.size() == 4) {
            this.replace = ASN1Boolean.getInstance(seq.getObjectAt(2)).isTrue();
            this.certTemplate = CertTemplate.getInstance(seq.getObjectAt(3));
        } else {
            this.replace = true;
            this.certTemplate = CertTemplate.getInstance(seq.getObjectAt(2));
        }
    }

    public static ModCertTemplate getInstance(Object o) {
        if (o instanceof ModCertTemplate) {
            return (ModCertTemplate) o;
        }
        if (o != null) {
            return new ModCertTemplate(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public BodyPartPath getPkiDataReference() {
        return this.pkiDataReference;
    }

    public BodyPartList getCertReferences() {
        return this.certReferences;
    }

    public boolean isReplacingFields() {
        return this.replace;
    }

    public CertTemplate getCertTemplate() {
        return this.certTemplate;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.pkiDataReference);
        v.add(this.certReferences);
        if (!this.replace) {
            v.add(ASN1Boolean.getInstance(this.replace));
        }
        v.add(this.certTemplate);
        return new DERSequence(v);
    }
}
