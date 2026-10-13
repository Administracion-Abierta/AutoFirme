package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/GroupLinkageValue.class */
public class GroupLinkageValue extends ASN1Object {
    private final ASN1OctetString jValue;
    private final ASN1OctetString value;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/GroupLinkageValue$Builder.class */
    public static class Builder {
        private ASN1OctetString jValue;
        private ASN1OctetString value;

        public Builder setJValue(ASN1OctetString aSN1OctetString) {
            this.jValue = aSN1OctetString;
            return this;
        }

        public Builder setJValue(byte[] bArr) {
            return setJValue(new DEROctetString(Arrays.clone(bArr)));
        }

        public Builder setValue(ASN1OctetString aSN1OctetString) {
            this.value = aSN1OctetString;
            return this;
        }

        public Builder setValue(byte[] bArr) {
            return setValue(new DEROctetString(Arrays.clone(bArr)));
        }

        public GroupLinkageValue createGroupLinkageValue() {
            return new GroupLinkageValue(this.jValue, this.value);
        }
    }

    private GroupLinkageValue(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.jValue = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0));
        this.value = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1));
        assertValues();
    }

    public GroupLinkageValue(ASN1OctetString aSN1OctetString, ASN1OctetString aSN1OctetString2) {
        this.jValue = aSN1OctetString;
        this.value = aSN1OctetString2;
        assertValues();
    }

    private void assertValues() {
        if (this.jValue == null || this.jValue.getOctets().length != 4) {
            throw new IllegalArgumentException("jValue is null or not four bytes long");
        }
        if (this.value == null || this.value.getOctets().length != 9) {
            throw new IllegalArgumentException("value is null or not nine bytes long");
        }
    }

    public static GroupLinkageValue getInstance(Object obj) {
        if (obj instanceof GroupLinkageValue) {
            return (GroupLinkageValue) obj;
        }
        if (obj != null) {
            return new GroupLinkageValue(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public ASN1OctetString getJValue() {
        return this.jValue;
    }

    public ASN1OctetString getValue() {
        return this.value;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.jValue, this.value);
    }

    public static Builder builder() {
        return new Builder();
    }
}
