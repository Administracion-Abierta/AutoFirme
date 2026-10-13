package org.bouncycastle.asn1.x509;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1PrintableString;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/X509NameEntryConverter.class */
public abstract class X509NameEntryConverter {
    protected ASN1Primitive convertHexEncoded(String str, int i) throws IOException {
        return ASN1Primitive.fromByteArray(Hex.decodeStrict(str, i, str.length() - i));
    }

    protected boolean canBePrintable(String str) {
        return ASN1PrintableString.isPrintableString(str);
    }

    public abstract ASN1Primitive getConvertedValue(ASN1ObjectIdentifier aSN1ObjectIdentifier, String str);
}
