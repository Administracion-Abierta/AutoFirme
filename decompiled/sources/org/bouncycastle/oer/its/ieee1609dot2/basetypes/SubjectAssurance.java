package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/SubjectAssurance.class */
public class SubjectAssurance extends DEROctetString {
    public SubjectAssurance(byte[] bArr) {
        super(bArr);
        if (bArr.length != 1) {
            throw new IllegalArgumentException("length is not 1");
        }
    }

    private SubjectAssurance(ASN1OctetString aSN1OctetString) {
        this(aSN1OctetString.getOctets());
    }

    public static SubjectAssurance getInstance(Object obj) {
        if (obj instanceof SubjectAssurance) {
            return (SubjectAssurance) obj;
        }
        if (obj != null) {
            return new SubjectAssurance(DEROctetString.getInstance(obj));
        }
        return null;
    }
}
