package org.bouncycastle.asn1;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ASN1UniversalTypes.class */
final class ASN1UniversalTypes {
    private ASN1UniversalTypes() {
    }

    static ASN1UniversalType get(int i) {
        switch (i) {
            case 1:
                return ASN1Boolean.TYPE;
            case 2:
                return ASN1Integer.TYPE;
            case 3:
                return ASN1BitString.TYPE;
            case 4:
                return ASN1OctetString.TYPE;
            case 5:
                return ASN1Null.TYPE;
            case 6:
                return ASN1ObjectIdentifier.TYPE;
            case 7:
                return ASN1ObjectDescriptor.TYPE;
            case 8:
                return ASN1External.TYPE;
            case 9:
            case 11:
            case 14:
            case 15:
            case 29:
            default:
                return null;
            case 10:
                return ASN1Enumerated.TYPE;
            case 12:
                return ASN1UTF8String.TYPE;
            case 13:
                return ASN1RelativeOID.TYPE;
            case 16:
                return ASN1Sequence.TYPE;
            case 17:
                return ASN1Set.TYPE;
            case 18:
                return ASN1NumericString.TYPE;
            case 19:
                return ASN1PrintableString.TYPE;
            case 20:
                return ASN1T61String.TYPE;
            case 21:
                return ASN1VideotexString.TYPE;
            case 22:
                return ASN1IA5String.TYPE;
            case 23:
                return ASN1UTCTime.TYPE;
            case 24:
                return ASN1GeneralizedTime.TYPE;
            case 25:
                return ASN1GraphicString.TYPE;
            case 26:
                return ASN1VisibleString.TYPE;
            case 27:
                return ASN1GeneralString.TYPE;
            case 28:
                return ASN1UniversalString.TYPE;
            case 30:
                return ASN1BMPString.TYPE;
        }
    }
}
