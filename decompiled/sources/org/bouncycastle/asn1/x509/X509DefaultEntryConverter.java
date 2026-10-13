package org.bouncycastle.asn1.x509;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x500.style.BCStyle;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/X509DefaultEntryConverter.class */
public class X509DefaultEntryConverter extends X509NameEntryConverter {
    @Override // org.bouncycastle.asn1.x509.X509NameEntryConverter
    public ASN1Primitive getConvertedValue(ASN1ObjectIdentifier aSN1ObjectIdentifier, String str) {
        if (str.length() != 0 && str.charAt(0) == '#') {
            try {
                return convertHexEncoded(str, 1);
            } catch (IOException e) {
                throw new RuntimeException("can't recode value for oid " + aSN1ObjectIdentifier.getId());
            }
        }
        if (str.length() != 0 && str.charAt(0) == '\\') {
            str = str.substring(1);
        }
        if (aSN1ObjectIdentifier.equals((ASN1Primitive) BCStyle.EmailAddress) || aSN1ObjectIdentifier.equals((ASN1Primitive) BCStyle.DC)) {
            return new DERIA5String(str);
        }
        if (aSN1ObjectIdentifier.equals((ASN1Primitive) BCStyle.DATE_OF_BIRTH)) {
            return new DERGeneralizedTime(str);
        }
        return (aSN1ObjectIdentifier.equals((ASN1Primitive) BCStyle.C) || aSN1ObjectIdentifier.equals((ASN1Primitive) BCStyle.SERIALNUMBER) || aSN1ObjectIdentifier.equals((ASN1Primitive) BCStyle.DN_QUALIFIER) || aSN1ObjectIdentifier.equals((ASN1Primitive) BCStyle.TELEPHONE_NUMBER)) ? new DERPrintableString(str) : new DERUTF8String(str);
    }
}
