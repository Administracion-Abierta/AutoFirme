package org.bouncycastle.asn1.x500.style;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1UniversalString;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x500/style/IETFUtils.class */
public class IETFUtils {
    private static String unescape(String str) {
        if (str.length() == 0) {
            return str;
        }
        if (str.indexOf(92) < 0 && str.indexOf(34) < 0) {
            return str.trim();
        }
        boolean z = false;
        boolean z2 = false;
        StringBuffer stringBuffer = new StringBuffer(str.length());
        int i = 0;
        if (str.charAt(0) == '\\' && str.charAt(1) == '#') {
            i = 2;
            stringBuffer.append("\\#");
        }
        boolean z3 = false;
        int length = 0;
        char c = 0;
        for (int i2 = i; i2 != str.length(); i2++) {
            char cCharAt = str.charAt(i2);
            if (cCharAt != ' ') {
                z3 = true;
            }
            if (cCharAt == '\"') {
                if (z) {
                    stringBuffer.append(cCharAt);
                    z = false;
                } else {
                    z2 = !z2;
                }
            } else if (cCharAt == '\\' && !z && !z2) {
                z = true;
                length = stringBuffer.length();
            } else if (cCharAt != ' ' || z || z3) {
                if (!z || !isHexDigit(cCharAt)) {
                    stringBuffer.append(cCharAt);
                    z = false;
                } else if (c != 0) {
                    stringBuffer.append((char) ((convertHex(c) * 16) + convertHex(cCharAt)));
                    z = false;
                    c = 0;
                } else {
                    c = cCharAt;
                }
            }
        }
        if (stringBuffer.length() > 0) {
            while (stringBuffer.charAt(stringBuffer.length() - 1) == ' ' && length != stringBuffer.length() - 1) {
                stringBuffer.setLength(stringBuffer.length() - 1);
            }
        }
        return stringBuffer.toString();
    }

    private static boolean isHexDigit(char c) {
        return ('0' <= c && c <= '9') || ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F');
    }

    private static int convertHex(char c) {
        if ('0' > c || c > '9') {
            return ('a' > c || c > 'f') ? (c - 'A') + 10 : (c - 'a') + 10;
        }
        return c - '0';
    }

    public static RDN[] rDNsFromString(String str, X500NameStyle x500NameStyle) {
        X500NameTokenizer x500NameTokenizer = new X500NameTokenizer(str);
        X500NameBuilder x500NameBuilder = new X500NameBuilder(x500NameStyle);
        addRDNs(x500NameStyle, x500NameBuilder, x500NameTokenizer);
        return x500NameBuilder.build().getRDNs();
    }

    private static void addRDNs(X500NameStyle x500NameStyle, X500NameBuilder x500NameBuilder, X500NameTokenizer x500NameTokenizer) {
        while (true) {
            String strNextToken = x500NameTokenizer.nextToken();
            if (strNextToken == null) {
                return;
            }
            if (strNextToken.indexOf(43) >= 0) {
                addMultiValuedRDN(x500NameStyle, x500NameBuilder, new X500NameTokenizer(strNextToken, '+'));
            } else {
                addRDN(x500NameStyle, x500NameBuilder, strNextToken);
            }
        }
    }

    private static void addMultiValuedRDN(X500NameStyle x500NameStyle, X500NameBuilder x500NameBuilder, X500NameTokenizer x500NameTokenizer) {
        String strNextToken = x500NameTokenizer.nextToken();
        if (strNextToken == null) {
            throw new IllegalArgumentException("badly formatted directory string");
        }
        if (!x500NameTokenizer.hasMoreTokens()) {
            addRDN(x500NameStyle, x500NameBuilder, strNextToken);
            return;
        }
        Vector vector = new Vector();
        Vector vector2 = new Vector();
        do {
            collectAttributeTypeAndValue(x500NameStyle, vector, vector2, strNextToken);
            strNextToken = x500NameTokenizer.nextToken();
        } while (strNextToken != null);
        x500NameBuilder.addMultiValuedRDN(toOIDArray(vector), toValueArray(vector2));
    }

    private static void addRDN(X500NameStyle x500NameStyle, X500NameBuilder x500NameBuilder, String str) {
        X500NameTokenizer x500NameTokenizer = new X500NameTokenizer(str, '=');
        x500NameBuilder.addRDN(x500NameStyle.attrNameToOID(nextToken(x500NameTokenizer, true).trim()), unescape(nextToken(x500NameTokenizer, false)));
    }

    private static void collectAttributeTypeAndValue(X500NameStyle x500NameStyle, Vector vector, Vector vector2, String str) {
        X500NameTokenizer x500NameTokenizer = new X500NameTokenizer(str, '=');
        String strNextToken = nextToken(x500NameTokenizer, true);
        String strNextToken2 = nextToken(x500NameTokenizer, false);
        ASN1ObjectIdentifier aSN1ObjectIdentifierAttrNameToOID = x500NameStyle.attrNameToOID(strNextToken.trim());
        String strUnescape = unescape(strNextToken2);
        vector.addElement(aSN1ObjectIdentifierAttrNameToOID);
        vector2.addElement(strUnescape);
    }

    private static String nextToken(X500NameTokenizer x500NameTokenizer, boolean z) {
        String strNextToken = x500NameTokenizer.nextToken();
        if (strNextToken == null || x500NameTokenizer.hasMoreTokens() != z) {
            throw new IllegalArgumentException("badly formatted directory string");
        }
        return strNextToken;
    }

    private static String[] toValueArray(Vector vector) {
        String[] strArr = new String[vector.size()];
        for (int i = 0; i != strArr.length; i++) {
            strArr[i] = (String) vector.elementAt(i);
        }
        return strArr;
    }

    private static ASN1ObjectIdentifier[] toOIDArray(Vector vector) {
        ASN1ObjectIdentifier[] aSN1ObjectIdentifierArr = new ASN1ObjectIdentifier[vector.size()];
        for (int i = 0; i != aSN1ObjectIdentifierArr.length; i++) {
            aSN1ObjectIdentifierArr[i] = (ASN1ObjectIdentifier) vector.elementAt(i);
        }
        return aSN1ObjectIdentifierArr;
    }

    public static String[] findAttrNamesForOID(ASN1ObjectIdentifier aSN1ObjectIdentifier, Hashtable hashtable) {
        int i = 0;
        Enumeration enumerationElements = hashtable.elements();
        while (enumerationElements.hasMoreElements()) {
            if (aSN1ObjectIdentifier.equals(enumerationElements.nextElement())) {
                i++;
            }
        }
        String[] strArr = new String[i];
        int i2 = 0;
        Enumeration enumerationKeys = hashtable.keys();
        while (enumerationKeys.hasMoreElements()) {
            String str = (String) enumerationKeys.nextElement();
            if (aSN1ObjectIdentifier.equals(hashtable.get(str))) {
                int i3 = i2;
                i2++;
                strArr[i3] = str;
            }
        }
        return strArr;
    }

    public static ASN1ObjectIdentifier decodeAttrName(String str, Hashtable hashtable) {
        if (str.regionMatches(true, 0, "OID.", 0, 4)) {
            return new ASN1ObjectIdentifier(str.substring(4));
        }
        ASN1ObjectIdentifier aSN1ObjectIdentifierTryFromID = ASN1ObjectIdentifier.tryFromID(str);
        if (aSN1ObjectIdentifierTryFromID != null) {
            return aSN1ObjectIdentifierTryFromID;
        }
        ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier) hashtable.get(Strings.toLowerCase(str));
        if (aSN1ObjectIdentifier != null) {
            return aSN1ObjectIdentifier;
        }
        throw new IllegalArgumentException("Unknown object id - " + str + " - passed to distinguished name");
    }

    public static ASN1Encodable valueFromHexString(String str, int i) throws IOException {
        byte[] bArr = new byte[(str.length() - i) / 2];
        for (int i2 = 0; i2 != bArr.length; i2++) {
            bArr[i2] = (byte) ((convertHex(str.charAt((i2 * 2) + i)) << 4) | convertHex(str.charAt((i2 * 2) + i + 1)));
        }
        return ASN1Primitive.fromByteArray(bArr);
    }

    public static void appendRDN(StringBuffer stringBuffer, RDN rdn, Hashtable hashtable) {
        if (!rdn.isMultiValued()) {
            if (rdn.getFirst() != null) {
                appendTypeAndValue(stringBuffer, rdn.getFirst(), hashtable);
                return;
            }
            return;
        }
        AttributeTypeAndValue[] typesAndValues = rdn.getTypesAndValues();
        boolean z = true;
        for (int i = 0; i != typesAndValues.length; i++) {
            if (z) {
                z = false;
            } else {
                stringBuffer.append('+');
            }
            appendTypeAndValue(stringBuffer, typesAndValues[i], hashtable);
        }
    }

    public static void appendTypeAndValue(StringBuffer stringBuffer, AttributeTypeAndValue attributeTypeAndValue, Hashtable hashtable) {
        String str = (String) hashtable.get(attributeTypeAndValue.getType());
        if (str != null) {
            stringBuffer.append(str);
        } else {
            stringBuffer.append(attributeTypeAndValue.getType().getId());
        }
        stringBuffer.append('=');
        stringBuffer.append(valueToString(attributeTypeAndValue.getValue()));
    }

    public static String valueToString(ASN1Encodable aSN1Encodable) {
        StringBuffer stringBuffer = new StringBuffer();
        if (!(aSN1Encodable instanceof ASN1String) || (aSN1Encodable instanceof ASN1UniversalString)) {
            try {
                stringBuffer.append('#');
                stringBuffer.append(Hex.toHexString(aSN1Encodable.toASN1Primitive().getEncoded("DER")));
            } catch (IOException e) {
                throw new IllegalArgumentException("Other value has no encoded form");
            }
        } else {
            String string = ((ASN1String) aSN1Encodable).getString();
            if (string.length() > 0 && string.charAt(0) == '#') {
                stringBuffer.append('\\');
            }
            stringBuffer.append(string);
        }
        int length = stringBuffer.length();
        int i = 0;
        if (stringBuffer.length() >= 2 && stringBuffer.charAt(0) == '\\' && stringBuffer.charAt(1) == '#') {
            i = 0 + 2;
        }
        while (i != length) {
            switch (stringBuffer.charAt(i)) {
                case '\"':
                case '+':
                case ',':
                case ';':
                case '<':
                case '=':
                case '>':
                case '\\':
                    stringBuffer.insert(i, "\\");
                    i += 2;
                    length++;
                    break;
                default:
                    i++;
                    break;
            }
        }
        int i2 = 0;
        if (stringBuffer.length() > 0) {
            while (stringBuffer.length() > i2 && stringBuffer.charAt(i2) == ' ') {
                stringBuffer.insert(i2, "\\");
                i2 += 2;
            }
        }
        for (int length2 = stringBuffer.length() - 1; length2 >= i2 && stringBuffer.charAt(length2) == ' '; length2--) {
            stringBuffer.insert(length2, '\\');
        }
        return stringBuffer.toString();
    }

    public static String canonicalize(String str) {
        if (str.length() > 0 && str.charAt(0) == '#') {
            ASN1Encodable aSN1EncodableDecodeObject = decodeObject(str);
            if (aSN1EncodableDecodeObject instanceof ASN1String) {
                str = ((ASN1String) aSN1EncodableDecodeObject).getString();
            }
        }
        String lowerCase = Strings.toLowerCase(str);
        int length = lowerCase.length();
        if (length < 2) {
            return lowerCase;
        }
        int i = 0;
        int i2 = length - 1;
        while (i < i2 && lowerCase.charAt(i) == '\\' && lowerCase.charAt(i + 1) == ' ') {
            i += 2;
        }
        int i3 = i2;
        int i4 = i + 1;
        while (i3 > i4 && lowerCase.charAt(i3 - 1) == '\\' && lowerCase.charAt(i3) == ' ') {
            i3 -= 2;
        }
        if (i > 0 || i3 < i2) {
            lowerCase = lowerCase.substring(i, i3 + 1);
        }
        return stripInternalSpaces(lowerCase);
    }

    public static String canonicalString(ASN1Encodable aSN1Encodable) {
        return canonicalize(valueToString(aSN1Encodable));
    }

    private static ASN1Primitive decodeObject(String str) {
        try {
            return ASN1Primitive.fromByteArray(Hex.decodeStrict(str, 1, str.length() - 1));
        } catch (IOException e) {
            throw new IllegalStateException("unknown encoding in name: " + e);
        }
    }

    public static String stripInternalSpaces(String str) {
        if (str.indexOf("  ") < 0) {
            return str;
        }
        StringBuffer stringBuffer = new StringBuffer();
        char cCharAt = str.charAt(0);
        stringBuffer.append(cCharAt);
        for (int i = 1; i < str.length(); i++) {
            char cCharAt2 = str.charAt(i);
            if (cCharAt != ' ' || cCharAt2 != ' ') {
                stringBuffer.append(cCharAt2);
                cCharAt = cCharAt2;
            }
        }
        return stringBuffer.toString();
    }

    public static boolean rDNAreEqual(RDN rdn, RDN rdn2) {
        if (rdn.size() != rdn2.size()) {
            return false;
        }
        AttributeTypeAndValue[] typesAndValues = rdn.getTypesAndValues();
        AttributeTypeAndValue[] typesAndValues2 = rdn2.getTypesAndValues();
        if (typesAndValues.length != typesAndValues2.length) {
            return false;
        }
        for (int i = 0; i != typesAndValues.length; i++) {
            if (!atvAreEqual(typesAndValues[i], typesAndValues2[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean atvAreEqual(AttributeTypeAndValue attributeTypeAndValue, AttributeTypeAndValue attributeTypeAndValue2) {
        if (attributeTypeAndValue == attributeTypeAndValue2) {
            return true;
        }
        return null != attributeTypeAndValue && null != attributeTypeAndValue2 && attributeTypeAndValue.getType().equals((ASN1Primitive) attributeTypeAndValue2.getType()) && canonicalString(attributeTypeAndValue.getValue()).equals(canonicalString(attributeTypeAndValue2.getValue()));
    }
}
