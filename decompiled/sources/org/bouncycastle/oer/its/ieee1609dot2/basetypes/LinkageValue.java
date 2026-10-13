package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/basetypes/LinkageValue.class */
public class LinkageValue extends DEROctetString {
    public LinkageValue(byte[] bArr) {
        super(bArr);
    }

    public LinkageValue(ASN1Encodable aSN1Encodable) throws IOException {
        super(aSN1Encodable);
    }

    public static LinkageValue getInstance(Object obj) {
        if (obj instanceof LinkageValue) {
            return (LinkageValue) obj;
        }
        if (obj != null) {
            return new LinkageValue(ASN1OctetString.getInstance(obj).getOctets());
        }
        return null;
    }
}
