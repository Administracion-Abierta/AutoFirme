package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Certificate;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/RootCaEntry.class */
public class RootCaEntry extends ASN1Object {
    private final EtsiTs103097Certificate selfsignedRootCa;
    private final EtsiTs103097Certificate successorTo;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/RootCaEntry$Builder.class */
    public static class Builder {
        private EtsiTs103097Certificate selfsignedRootCa;
        private EtsiTs103097Certificate successorTo;

        public Builder setSelfsignedRootCa(EtsiTs103097Certificate etsiTs103097Certificate) {
            this.selfsignedRootCa = etsiTs103097Certificate;
            return this;
        }

        public Builder setSuccessorTo(EtsiTs103097Certificate etsiTs103097Certificate) {
            this.successorTo = etsiTs103097Certificate;
            return this;
        }

        public RootCaEntry createRootCaEntry() {
            return new RootCaEntry(this.selfsignedRootCa, this.successorTo);
        }
    }

    public RootCaEntry(EtsiTs103097Certificate etsiTs103097Certificate, EtsiTs103097Certificate etsiTs103097Certificate2) {
        this.selfsignedRootCa = etsiTs103097Certificate;
        this.successorTo = etsiTs103097Certificate2;
    }

    private RootCaEntry(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.selfsignedRootCa = EtsiTs103097Certificate.getInstance((Object) aSN1Sequence.getObjectAt(0));
        this.successorTo = (EtsiTs103097Certificate) OEROptional.getValue(EtsiTs103097Certificate.class, aSN1Sequence.getObjectAt(1));
    }

    public static RootCaEntry getInstance(Object obj) {
        if (obj instanceof RootCaEntry) {
            return (RootCaEntry) obj;
        }
        if (obj != null) {
            return new RootCaEntry(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public EtsiTs103097Certificate getSelfsignedRootCa() {
        return this.selfsignedRootCa;
    }

    public EtsiTs103097Certificate getSuccessorTo() {
        return this.successorTo;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.selfsignedRootCa, OEROptional.getInstance(this.successorTo)});
    }

    public static Builder builder() {
        return new Builder();
    }
}
