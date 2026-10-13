package org.bouncycastle.asn1;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ASN1Util.class */
public abstract class ASN1Util {
    static ASN1TaggedObject checkContextTag(ASN1TaggedObject aSN1TaggedObject, int i) {
        return checkTag(aSN1TaggedObject, 128, i);
    }

    static ASN1TaggedObjectParser checkContextTag(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i) {
        return checkTag(aSN1TaggedObjectParser, 128, i);
    }

    static ASN1TaggedObject checkContextTagClass(ASN1TaggedObject aSN1TaggedObject) {
        return checkTagClass(aSN1TaggedObject, 128);
    }

    static ASN1TaggedObjectParser checkContextTagClass(ASN1TaggedObjectParser aSN1TaggedObjectParser) {
        return checkTagClass(aSN1TaggedObjectParser, 128);
    }

    static ASN1TaggedObject checkTag(ASN1TaggedObject aSN1TaggedObject, int i, int i2) {
        if (aSN1TaggedObject.hasTag(i, i2)) {
            return aSN1TaggedObject;
        }
        throw new IllegalStateException("Expected " + getTagText(i, i2) + " tag but found " + getTagText(aSN1TaggedObject));
    }

    static ASN1TaggedObjectParser checkTag(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i, int i2) {
        if (aSN1TaggedObjectParser.hasTag(i, i2)) {
            return aSN1TaggedObjectParser;
        }
        throw new IllegalStateException("Expected " + getTagText(i, i2) + " tag but found " + getTagText(aSN1TaggedObjectParser));
    }

    static ASN1TaggedObject checkTagClass(ASN1TaggedObject aSN1TaggedObject, int i) {
        if (aSN1TaggedObject.hasTagClass(i)) {
            return aSN1TaggedObject;
        }
        throw new IllegalStateException("Expected " + getTagClassText(i) + " tag but found " + getTagClassText(aSN1TaggedObject));
    }

    static ASN1TaggedObjectParser checkTagClass(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i) {
        if (aSN1TaggedObjectParser.hasTagClass(i)) {
            return aSN1TaggedObjectParser;
        }
        throw new IllegalStateException("Expected " + getTagClassText(i) + " tag but found " + getTagClassText(aSN1TaggedObjectParser));
    }

    static String getTagClassText(ASN1Tag aSN1Tag) {
        return getTagClassText(aSN1Tag.getTagClass());
    }

    public static String getTagClassText(ASN1TaggedObject aSN1TaggedObject) {
        return getTagClassText(aSN1TaggedObject.getTagClass());
    }

    public static String getTagClassText(ASN1TaggedObjectParser aSN1TaggedObjectParser) {
        return getTagClassText(aSN1TaggedObjectParser.getTagClass());
    }

    public static String getTagClassText(int i) {
        switch (i) {
            case 64:
                return "APPLICATION";
            case 128:
                return "CONTEXT";
            case 192:
                return "PRIVATE";
            default:
                return "UNIVERSAL";
        }
    }

    static String getTagText(ASN1Tag aSN1Tag) {
        return getTagText(aSN1Tag.getTagClass(), aSN1Tag.getTagNumber());
    }

    public static String getTagText(ASN1TaggedObject aSN1TaggedObject) {
        return getTagText(aSN1TaggedObject.getTagClass(), aSN1TaggedObject.getTagNo());
    }

    public static String getTagText(ASN1TaggedObjectParser aSN1TaggedObjectParser) {
        return getTagText(aSN1TaggedObjectParser.getTagClass(), aSN1TaggedObjectParser.getTagNo());
    }

    public static String getTagText(int i, int i2) {
        switch (i) {
            case 64:
                return "[APPLICATION " + i2 + "]";
            case 128:
                return "[CONTEXT " + i2 + "]";
            case 192:
                return "[PRIVATE " + i2 + "]";
            default:
                return "[UNIVERSAL " + i2 + "]";
        }
    }

    public static ASN1Object getExplicitBaseObject(ASN1TaggedObject aSN1TaggedObject, int i, int i2) {
        return checkTag(aSN1TaggedObject, i, i2).getExplicitBaseObject();
    }

    public static ASN1Object getExplicitContextBaseObject(ASN1TaggedObject aSN1TaggedObject, int i) {
        return getExplicitBaseObject(aSN1TaggedObject, 128, i);
    }

    public static ASN1Object tryGetExplicitBaseObject(ASN1TaggedObject aSN1TaggedObject, int i, int i2) {
        if (aSN1TaggedObject.hasTag(i, i2)) {
            return aSN1TaggedObject.getExplicitBaseObject();
        }
        return null;
    }

    public static ASN1Object tryGetExplicitContextBaseObject(ASN1TaggedObject aSN1TaggedObject, int i) {
        return tryGetExplicitBaseObject(aSN1TaggedObject, 128, i);
    }

    public static ASN1TaggedObject getExplicitBaseTagged(ASN1TaggedObject aSN1TaggedObject, int i) {
        return checkTagClass(aSN1TaggedObject, i).getExplicitBaseTagged();
    }

    public static ASN1TaggedObject getExplicitBaseTagged(ASN1TaggedObject aSN1TaggedObject, int i, int i2) {
        return checkTag(aSN1TaggedObject, i, i2).getExplicitBaseTagged();
    }

    public static ASN1TaggedObject getExplicitContextBaseTagged(ASN1TaggedObject aSN1TaggedObject) {
        return getExplicitBaseTagged(aSN1TaggedObject, 128);
    }

    public static ASN1TaggedObject getExplicitContextBaseTagged(ASN1TaggedObject aSN1TaggedObject, int i) {
        return getExplicitBaseTagged(aSN1TaggedObject, 128, i);
    }

    public static ASN1TaggedObject tryGetExplicitBaseTagged(ASN1TaggedObject aSN1TaggedObject, int i) {
        if (aSN1TaggedObject.hasTagClass(i)) {
            return aSN1TaggedObject.getExplicitBaseTagged();
        }
        return null;
    }

    public static ASN1TaggedObject tryGetExplicitBaseTagged(ASN1TaggedObject aSN1TaggedObject, int i, int i2) {
        if (aSN1TaggedObject.hasTag(i, i2)) {
            return aSN1TaggedObject.getExplicitBaseTagged();
        }
        return null;
    }

    public static ASN1TaggedObject tryGetExplicitContextBaseTagged(ASN1TaggedObject aSN1TaggedObject) {
        return tryGetExplicitBaseTagged(aSN1TaggedObject, 128);
    }

    public static ASN1TaggedObject tryGetExplicitContextBaseTagged(ASN1TaggedObject aSN1TaggedObject, int i) {
        return tryGetExplicitBaseTagged(aSN1TaggedObject, 128, i);
    }

    public static ASN1TaggedObject getImplicitBaseTagged(ASN1TaggedObject aSN1TaggedObject, int i, int i2, int i3, int i4) {
        return checkTag(aSN1TaggedObject, i, i2).getImplicitBaseTagged(i3, i4);
    }

    public static ASN1TaggedObject getImplicitContextBaseTagged(ASN1TaggedObject aSN1TaggedObject, int i, int i2, int i3) {
        return getImplicitBaseTagged(aSN1TaggedObject, 128, i, i2, i3);
    }

    public static ASN1TaggedObject tryGetImplicitBaseTagged(ASN1TaggedObject aSN1TaggedObject, int i, int i2, int i3, int i4) {
        if (aSN1TaggedObject.hasTag(i, i2)) {
            return aSN1TaggedObject.getImplicitBaseTagged(i3, i4);
        }
        return null;
    }

    public static ASN1TaggedObject tryGetImplicitContextBaseTagged(ASN1TaggedObject aSN1TaggedObject, int i, int i2, int i3) {
        return tryGetImplicitBaseTagged(aSN1TaggedObject, 128, i, i2, i3);
    }

    public static ASN1Primitive getBaseUniversal(ASN1TaggedObject aSN1TaggedObject, int i, int i2, boolean z, int i3) {
        return checkTag(aSN1TaggedObject, i, i2).getBaseUniversal(z, i3);
    }

    public static ASN1Primitive getContextBaseUniversal(ASN1TaggedObject aSN1TaggedObject, int i, boolean z, int i2) {
        return getBaseUniversal(aSN1TaggedObject, 128, i, z, i2);
    }

    public static ASN1Primitive tryGetBaseUniversal(ASN1TaggedObject aSN1TaggedObject, int i, int i2, boolean z, int i3) {
        if (aSN1TaggedObject.hasTag(i, i2)) {
            return aSN1TaggedObject.getBaseUniversal(z, i3);
        }
        return null;
    }

    public static ASN1Primitive tryGetContextBaseUniversal(ASN1TaggedObject aSN1TaggedObject, int i, boolean z, int i2) {
        return tryGetBaseUniversal(aSN1TaggedObject, 128, i, z, i2);
    }

    public static ASN1TaggedObjectParser parseExplicitBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i) throws IOException {
        return checkTagClass(aSN1TaggedObjectParser, i).parseExplicitBaseTagged();
    }

    public static ASN1TaggedObjectParser parseExplicitBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i, int i2) throws IOException {
        return checkTag(aSN1TaggedObjectParser, i, i2).parseExplicitBaseTagged();
    }

    public static ASN1TaggedObjectParser parseExplicitContextBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser) throws IOException {
        return parseExplicitBaseTagged(aSN1TaggedObjectParser, 128);
    }

    public static ASN1TaggedObjectParser parseExplicitContextBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i) throws IOException {
        return parseExplicitBaseTagged(aSN1TaggedObjectParser, 128, i);
    }

    public static ASN1TaggedObjectParser tryParseExplicitBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i) throws IOException {
        if (aSN1TaggedObjectParser.hasTagClass(i)) {
            return aSN1TaggedObjectParser.parseExplicitBaseTagged();
        }
        return null;
    }

    public static ASN1TaggedObjectParser tryParseExplicitBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i, int i2) throws IOException {
        if (aSN1TaggedObjectParser.hasTag(i, i2)) {
            return aSN1TaggedObjectParser.parseExplicitBaseTagged();
        }
        return null;
    }

    public static ASN1TaggedObjectParser tryParseExplicitContextBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser) throws IOException {
        return tryParseExplicitBaseTagged(aSN1TaggedObjectParser, 128);
    }

    public static ASN1TaggedObjectParser tryParseExplicitContextBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i) throws IOException {
        return tryParseExplicitBaseTagged(aSN1TaggedObjectParser, 128, i);
    }

    public static ASN1TaggedObjectParser parseImplicitBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i, int i2, int i3, int i4) throws IOException {
        return checkTag(aSN1TaggedObjectParser, i, i2).parseImplicitBaseTagged(i3, i4);
    }

    public static ASN1TaggedObjectParser parseImplicitContextBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i, int i2, int i3) throws IOException {
        return parseImplicitBaseTagged(aSN1TaggedObjectParser, 128, i, i2, i3);
    }

    public static ASN1TaggedObjectParser tryParseImplicitBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i, int i2, int i3, int i4) throws IOException {
        if (aSN1TaggedObjectParser.hasTag(i, i2)) {
            return aSN1TaggedObjectParser.parseImplicitBaseTagged(i3, i4);
        }
        return null;
    }

    public static ASN1TaggedObjectParser tryParseImplicitContextBaseTagged(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i, int i2, int i3) throws IOException {
        return tryParseImplicitBaseTagged(aSN1TaggedObjectParser, 128, i, i2, i3);
    }

    public static ASN1Encodable parseBaseUniversal(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i, int i2, boolean z, int i3) throws IOException {
        return checkTag(aSN1TaggedObjectParser, i, i2).parseBaseUniversal(z, i3);
    }

    public static ASN1Encodable parseContextBaseUniversal(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i, boolean z, int i2) throws IOException {
        return parseBaseUniversal(aSN1TaggedObjectParser, 128, i, z, i2);
    }

    public static ASN1Encodable tryParseBaseUniversal(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i, int i2, boolean z, int i3) throws IOException {
        if (aSN1TaggedObjectParser.hasTag(i, i2)) {
            return aSN1TaggedObjectParser.parseBaseUniversal(z, i3);
        }
        return null;
    }

    public static ASN1Encodable tryParseContextBaseUniversal(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i, boolean z, int i2) throws IOException {
        return tryParseBaseUniversal(aSN1TaggedObjectParser, 128, i, z, i2);
    }

    public static ASN1Encodable parseExplicitBaseObject(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i, int i2) throws IOException {
        return checkTag(aSN1TaggedObjectParser, i, i2).parseExplicitBaseObject();
    }

    public static ASN1Encodable parseExplicitContextBaseObject(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i) throws IOException {
        return parseExplicitBaseObject(aSN1TaggedObjectParser, 128, i);
    }

    public static ASN1Encodable tryParseExplicitBaseObject(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i, int i2) throws IOException {
        if (aSN1TaggedObjectParser.hasTag(i, i2)) {
            return aSN1TaggedObjectParser.parseExplicitBaseObject();
        }
        return null;
    }

    public static ASN1Encodable tryParseExplicitContextBaseObject(ASN1TaggedObjectParser aSN1TaggedObjectParser, int i) throws IOException {
        return tryParseExplicitBaseObject(aSN1TaggedObjectParser, 128, i);
    }
}
