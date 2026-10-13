package org.bouncycastle.oer.its.ieee1609dot2;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/PsidGroupPermissions.class */
public class PsidGroupPermissions extends ASN1Object {
    private final SubjectPermissions subjectPermissions;
    private final ASN1Integer minChainLength;
    private final ASN1Integer chainLengthRange;
    private final EndEntityType eeType;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/PsidGroupPermissions$Builder.class */
    public static class Builder {
        private SubjectPermissions subjectPermissions;
        private ASN1Integer minChainLength;
        private ASN1Integer chainLengthRange;
        private EndEntityType eeType;

        public Builder setSubjectPermissions(SubjectPermissions subjectPermissions) {
            this.subjectPermissions = subjectPermissions;
            return this;
        }

        public Builder setMinChainLength(BigInteger bigInteger) {
            this.minChainLength = new ASN1Integer(bigInteger);
            return this;
        }

        public Builder setMinChainLength(long j) {
            this.minChainLength = new ASN1Integer(j);
            return this;
        }

        public Builder setChainLengthRange(ASN1Integer aSN1Integer) {
            this.chainLengthRange = aSN1Integer;
            return this;
        }

        public Builder setMinChainLength(ASN1Integer aSN1Integer) {
            this.minChainLength = aSN1Integer;
            return this;
        }

        public Builder setChainLengthRange(BigInteger bigInteger) {
            this.chainLengthRange = new ASN1Integer(bigInteger);
            return this;
        }

        public Builder setChainLengthRange(long j) {
            this.chainLengthRange = new ASN1Integer(j);
            return this;
        }

        public Builder setEeType(EndEntityType endEntityType) {
            this.eeType = endEntityType;
            return this;
        }

        public PsidGroupPermissions createPsidGroupPermissions() {
            return new PsidGroupPermissions(this.subjectPermissions, this.minChainLength, this.chainLengthRange, this.eeType);
        }
    }

    private PsidGroupPermissions(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 4) {
            throw new IllegalArgumentException("expected sequence size of 4");
        }
        this.subjectPermissions = SubjectPermissions.getInstance(aSN1Sequence.getObjectAt(0));
        this.minChainLength = (ASN1Integer) OEROptional.getInstance(aSN1Sequence.getObjectAt(1)).getObject(ASN1Integer.class);
        this.chainLengthRange = (ASN1Integer) OEROptional.getInstance(aSN1Sequence.getObjectAt(2)).getObject(ASN1Integer.class);
        this.eeType = (EndEntityType) OEROptional.getInstance(aSN1Sequence.getObjectAt(3)).getObject(EndEntityType.class);
    }

    public PsidGroupPermissions(SubjectPermissions subjectPermissions, ASN1Integer aSN1Integer, ASN1Integer aSN1Integer2, EndEntityType endEntityType) {
        this.subjectPermissions = subjectPermissions;
        this.minChainLength = aSN1Integer;
        this.chainLengthRange = aSN1Integer2;
        this.eeType = endEntityType;
    }

    public static PsidGroupPermissions getInstance(Object obj) {
        if (obj instanceof PsidGroupPermissions) {
            return (PsidGroupPermissions) obj;
        }
        if (obj != null) {
            return new PsidGroupPermissions(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public SubjectPermissions getSubjectPermissions() {
        return this.subjectPermissions;
    }

    public ASN1Integer getMinChainLength() {
        return this.minChainLength;
    }

    public EndEntityType getEeType() {
        return this.eeType;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.subjectPermissions, OEROptional.getInstance(this.minChainLength), OEROptional.getInstance(this.chainLengthRange), OEROptional.getInstance(this.eeType)});
    }

    public ASN1Integer getChainLengthRange() {
        return this.chainLengthRange;
    }
}
