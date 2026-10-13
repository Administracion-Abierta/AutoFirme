package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ItsUtils;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/PsidSsp.class */
public class PsidSsp extends ASN1Object {
    private final Psid psid;
    private final ServiceSpecificPermissions ssp;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/PsidSsp$Builder.class */
    public static class Builder {
        private Psid psid;
        private ServiceSpecificPermissions ssp;

        public Builder setPsid(Psid psid) {
            this.psid = psid;
            return this;
        }

        public Builder setSsp(ServiceSpecificPermissions serviceSpecificPermissions) {
            this.ssp = serviceSpecificPermissions;
            return this;
        }

        public PsidSsp createPsidSsp() {
            return new PsidSsp(this.psid, this.ssp);
        }
    }

    public PsidSsp(Psid psid, ServiceSpecificPermissions serviceSpecificPermissions) {
        this.psid = psid;
        this.ssp = serviceSpecificPermissions;
    }

    private PsidSsp(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.psid = Psid.getInstance(aSN1Sequence.getObjectAt(0));
        this.ssp = (ServiceSpecificPermissions) OEROptional.getValue(ServiceSpecificPermissions.class, aSN1Sequence.getObjectAt(1));
    }

    public static PsidSsp getInstance(Object obj) {
        if (obj instanceof PsidSsp) {
            return (PsidSsp) obj;
        }
        if (obj != null) {
            return new PsidSsp(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Psid getPsid() {
        return this.psid;
    }

    public ServiceSpecificPermissions getSsp() {
        return this.ssp;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.psid, OEROptional.getInstance(this.ssp));
    }
}
