package org.bouncycastle.oer;

import com.sun.jna.platform.win32.Msi;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/OEROutputStream.class */
public class OEROutputStream extends OutputStream {
    private static final int[] bits = {1, 2, 4, 8, 16, 32, 64, 128};
    private final OutputStream out;
    protected PrintWriter debugOutput = null;

    public OEROutputStream(OutputStream outputStream) {
        this.out = outputStream;
    }

    public static int byteLength(long j) {
        int i = 8;
        while (i > 0 && (j & (-72057594037927936L)) == 0) {
            j <<= 8;
            i--;
        }
        return i;
    }

    /* JADX WARN: Code duplicated, block: B:101:0x02f3  */
    /* JADX WARN: Code duplicated, block: B:104:0x030a  */
    /* JADX WARN: Code duplicated, block: B:110:0x0334  */
    /* JADX WARN: Code duplicated, block: B:113:0x034e  */
    /* JADX WARN: Code duplicated, block: B:114:0x0356  */
    /* JADX WARN: Code duplicated, block: B:118:0x0373  */
    /* JADX WARN: Code duplicated, block: B:120:0x037c  */
    /* JADX WARN: Code duplicated, block: B:252:0x02c5 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:254:0x02bf A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:258:0x0324 A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:262:0x03a1 A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:30:0x00fb  */
    /* JADX WARN: Code duplicated, block: B:74:0x0232  */
    /* JADX WARN: Code duplicated, block: B:78:0x0250  */
    /* JADX WARN: Code duplicated, block: B:81:0x026e  */
    /* JADX WARN: Code duplicated, block: B:84:0x0279  */
    /* JADX WARN: Code duplicated, block: B:86:0x0289  */
    /* JADX WARN: Code duplicated, block: B:92:0x02b7  */
    /* JADX WARN: Code duplicated, block: B:96:0x02ca  */
    /* JADX WARN: Code duplicated, block: B:99:0x02ee  */
    public void write(ASN1Encodable aSN1Encodable, Element element) throws IOException {
        byte[] bArrLongToBigEndian;
        Enumeration objects;
        List<Element> children;
        int i;
        ByteArrayOutputStream byteArrayOutputStream;
        int i2;
        int i3;
        int i4;
        Element elementResult;
        ASN1Encodable objectAt;
        if (aSN1Encodable == OEROptional.ABSENT) {
            return;
        }
        if (aSN1Encodable instanceof OEROptional) {
            write(((OEROptional) aSN1Encodable).get(), element);
            return;
        }
        ASN1Primitive aSN1Primitive = aSN1Encodable.toASN1Primitive();
        switch (element.getBaseType()) {
            case Supplier:
                write(aSN1Primitive, element.getElementSupplier().build());
                return;
            case SEQ:
                ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(aSN1Primitive);
                int i5 = 7;
                int i6 = 0;
                boolean z = false;
                if (element.isExtensionsInDefinition()) {
                    for (int i7 = 0; i7 < element.getChildren().size(); i7++) {
                        Element element2 = element.getChildren().get(i7);
                        if (element2.getBaseType() != OERDefinition.BaseType.EXTENSION) {
                            if (element2.getBlock() > 0 && i7 < aSN1Sequence.size() && !OEROptional.ABSENT.equals(aSN1Sequence.getObjectAt(i7))) {
                                z = true;
                            }
                        }
                        i6 = z ? 0 | bits[7] : 0;
                        i5 = 7 - 1;
                    }
                    if (z) {
                    }
                    i5 = 7 - 1;
                }
                for (int i8 = 0; i8 < element.getChildren().size(); i8++) {
                    Element element3 = element.getChildren().get(i8);
                    if (element3.getBaseType() != OERDefinition.BaseType.EXTENSION) {
                        if (element3.getBlock() > 0) {
                            if (i5 != 7) {
                                this.out.write(i6);
                            }
                            children = element.getChildren();
                            i = 0;
                            while (i < children.size()) {
                                elementResult = element.getChildren().get(i);
                                if (elementResult.getBaseType() != OERDefinition.BaseType.EXTENSION) {
                                    if (elementResult.getBlock() > 0) {
                                        if (z) {
                                            byteArrayOutputStream = new ByteArrayOutputStream();
                                            i2 = 7;
                                            i3 = 0;
                                            for (i4 = i; i4 < children.size(); i4++) {
                                                if (i2 < 0) {
                                                    byteArrayOutputStream.write(i3);
                                                    i2 = 7;
                                                    i3 = 0;
                                                }
                                                if (i4 >= aSN1Sequence.size() && !OEROptional.ABSENT.equals(aSN1Sequence.getObjectAt(i4))) {
                                                    i3 |= bits[i2];
                                                }
                                                i2--;
                                            }
                                            if (i2 != 7) {
                                                byteArrayOutputStream.write(i3);
                                            }
                                            encodeLength(byteArrayOutputStream.size() + 1);
                                            if (i2 == 7) {
                                                write(0);
                                            } else {
                                                write(i2 + 1);
                                            }
                                            write(byteArrayOutputStream.toByteArray());
                                            while (i < children.size()) {
                                                if (i >= aSN1Sequence.size() && !OEROptional.ABSENT.equals(aSN1Sequence.getObjectAt(i))) {
                                                    writePlainType(aSN1Sequence.getObjectAt(i), children.get(i));
                                                }
                                                i++;
                                            }
                                        }
                                        this.out.flush();
                                        debugPrint(element.appendLabel(""));
                                        return;
                                    }
                                    objectAt = aSN1Sequence.getObjectAt(i);
                                    if (elementResult.getaSwitch() != null) {
                                        elementResult = elementResult.getaSwitch().result(new SwitchIndexer.Asn1SequenceIndexer(aSN1Sequence));
                                    }
                                    if (elementResult.getDefaultValue() != null || !elementResult.getDefaultValue().equals(objectAt)) {
                                        write(objectAt, elementResult);
                                    }
                                }
                                i++;
                            }
                            if (z) {
                                byteArrayOutputStream = new ByteArrayOutputStream();
                                i2 = 7;
                                i3 = 0;
                                while (i4 < children.size()) {
                                    if (i2 < 0) {
                                        byteArrayOutputStream.write(i3);
                                        i2 = 7;
                                        i3 = 0;
                                    }
                                    if (i4 >= aSN1Sequence.size()) {
                                    }
                                    i2--;
                                }
                                if (i2 != 7) {
                                    byteArrayOutputStream.write(i3);
                                }
                                encodeLength(byteArrayOutputStream.size() + 1);
                                if (i2 == 7) {
                                    write(0);
                                } else {
                                    write(i2 + 1);
                                }
                                write(byteArrayOutputStream.toByteArray());
                                while (i < children.size()) {
                                    if (i >= aSN1Sequence.size()) {
                                    }
                                    i++;
                                }
                            }
                            this.out.flush();
                            debugPrint(element.appendLabel(""));
                            return;
                        }
                        Element elementExpandDeferredDefinition = Element.expandDeferredDefinition(element3, element);
                        if (element.getaSwitch() != null) {
                            elementExpandDeferredDefinition = Element.expandDeferredDefinition(element.getaSwitch().result(new SwitchIndexer.Asn1SequenceIndexer(aSN1Sequence)), element);
                        }
                        if (i5 < 0) {
                            this.out.write(i6);
                            i5 = 7;
                            i6 = 0;
                        }
                        ASN1Encodable objectAt2 = aSN1Sequence.getObjectAt(i8);
                        if (elementExpandDeferredDefinition.isExplicit() && (objectAt2 instanceof OEROptional)) {
                            throw new IllegalStateException("absent sequence element that is required by oer definition");
                        }
                        if (!elementExpandDeferredDefinition.isExplicit()) {
                            ASN1Encodable objectAt3 = aSN1Sequence.getObjectAt(i8);
                            if (elementExpandDeferredDefinition.getDefaultValue() != null) {
                                if (objectAt3 instanceof OEROptional) {
                                    if (((OEROptional) objectAt3).isDefined() && !((OEROptional) objectAt3).get().equals(elementExpandDeferredDefinition.getDefaultValue())) {
                                        i6 |= bits[i5];
                                    }
                                } else if (!elementExpandDeferredDefinition.getDefaultValue().equals(objectAt3)) {
                                    i6 |= bits[i5];
                                }
                            } else if (objectAt2 != OEROptional.ABSENT) {
                                i6 |= bits[i5];
                            }
                            i5--;
                        }
                    }
                }
                if (i5 != 7) {
                    this.out.write(i6);
                }
                children = element.getChildren();
                i = 0;
                while (i < children.size()) {
                    elementResult = element.getChildren().get(i);
                    if (elementResult.getBaseType() != OERDefinition.BaseType.EXTENSION) {
                        if (elementResult.getBlock() > 0) {
                            if (z) {
                                byteArrayOutputStream = new ByteArrayOutputStream();
                                i2 = 7;
                                i3 = 0;
                                while (i4 < children.size()) {
                                    if (i2 < 0) {
                                        byteArrayOutputStream.write(i3);
                                        i2 = 7;
                                        i3 = 0;
                                    }
                                    if (i4 >= aSN1Sequence.size()) {
                                    }
                                    i2--;
                                }
                                if (i2 != 7) {
                                    byteArrayOutputStream.write(i3);
                                }
                                encodeLength(byteArrayOutputStream.size() + 1);
                                if (i2 == 7) {
                                    write(0);
                                } else {
                                    write(i2 + 1);
                                }
                                write(byteArrayOutputStream.toByteArray());
                                while (i < children.size()) {
                                    if (i >= aSN1Sequence.size()) {
                                    }
                                    i++;
                                }
                            }
                            this.out.flush();
                            debugPrint(element.appendLabel(""));
                            return;
                        }
                        objectAt = aSN1Sequence.getObjectAt(i);
                        if (elementResult.getaSwitch() != null) {
                            elementResult = elementResult.getaSwitch().result(new SwitchIndexer.Asn1SequenceIndexer(aSN1Sequence));
                        }
                        if (elementResult.getDefaultValue() != null) {
                            write(objectAt, elementResult);
                        } else {
                            write(objectAt, elementResult);
                        }
                    }
                    i++;
                }
                if (z) {
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    i2 = 7;
                    i3 = 0;
                    while (i4 < children.size()) {
                        if (i2 < 0) {
                            byteArrayOutputStream.write(i3);
                            i2 = 7;
                            i3 = 0;
                        }
                        if (i4 >= aSN1Sequence.size()) {
                        }
                        i2--;
                    }
                    if (i2 != 7) {
                        byteArrayOutputStream.write(i3);
                    }
                    encodeLength(byteArrayOutputStream.size() + 1);
                    if (i2 == 7) {
                        write(0);
                    } else {
                        write(i2 + 1);
                    }
                    write(byteArrayOutputStream.toByteArray());
                    while (i < children.size()) {
                        if (i >= aSN1Sequence.size()) {
                        }
                        i++;
                    }
                }
                this.out.flush();
                debugPrint(element.appendLabel(""));
                return;
            case SEQ_OF:
                if (aSN1Primitive instanceof ASN1Set) {
                    objects = ((ASN1Set) aSN1Primitive).getObjects();
                    encodeQuantity(((ASN1Set) aSN1Primitive).size());
                } else {
                    if (!(aSN1Primitive instanceof ASN1Sequence)) {
                        throw new IllegalStateException("encodable at for SEQ_OF is not a container");
                    }
                    objects = ((ASN1Sequence) aSN1Primitive).getObjects();
                    encodeQuantity(((ASN1Sequence) aSN1Primitive).size());
                }
                Element elementExpandDeferredDefinition2 = Element.expandDeferredDefinition(element.getFirstChid(), element);
                while (objects.hasMoreElements()) {
                    write((ASN1Encodable) objects.nextElement(), elementExpandDeferredDefinition2);
                }
                this.out.flush();
                debugPrint(element.appendLabel(""));
                return;
            case CHOICE:
                ASN1Primitive aSN1Primitive2 = aSN1Primitive.toASN1Primitive();
                BitBuilder bitBuilder = new BitBuilder();
                if (!(aSN1Primitive2 instanceof ASN1TaggedObject)) {
                    throw new IllegalStateException("only support tagged objects");
                }
                ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject) aSN1Primitive2;
                int tagClass = aSN1TaggedObject.getTagClass();
                bitBuilder.writeBit(tagClass & 128).writeBit(tagClass & 64);
                int tagNo = aSN1TaggedObject.getTagNo();
                ASN1Primitive aSN1Primitive3 = aSN1TaggedObject.getBaseObject().toASN1Primitive();
                if (tagNo <= 63) {
                    bitBuilder.writeBits(tagNo, 6);
                } else {
                    bitBuilder.writeBits(255L, 6);
                    bitBuilder.write7BitBytes(tagNo);
                }
                if (this.debugOutput != null && (aSN1Primitive2 instanceof ASN1TaggedObject)) {
                    switch (((ASN1TaggedObject) aSN1Primitive2).getTagClass()) {
                        case 64:
                            debugPrint(element.appendLabel("AS"));
                            break;
                        case 128:
                            debugPrint(element.appendLabel(OperatorName.STROKING_COLORSPACE));
                            break;
                        case 192:
                            debugPrint(element.appendLabel("PR"));
                            break;
                    }
                }
                bitBuilder.writeAndClear(this.out);
                Element elementExpandDeferredDefinition3 = Element.expandDeferredDefinition(element.getChildren().get(tagNo), element);
                if (elementExpandDeferredDefinition3.getBlock() > 0) {
                    writePlainType(aSN1Primitive3, elementExpandDeferredDefinition3);
                } else {
                    write(aSN1Primitive3, elementExpandDeferredDefinition3);
                }
                this.out.flush();
                return;
            case ENUM:
                BigInteger value = aSN1Primitive instanceof ASN1Integer ? ASN1Integer.getInstance(aSN1Primitive).getValue() : ASN1Enumerated.getInstance(aSN1Primitive).getValue();
                Iterator<Element> it = element.getChildren().iterator();
                while (it.hasNext()) {
                    if (Element.expandDeferredDefinition(it.next(), element).getEnumValue().equals(value)) {
                        if (value.compareTo(BigInteger.valueOf(127L)) > 0) {
                            byte[] byteArray = value.toByteArray();
                            this.out.write(128 | (byteArray.length & 255));
                            this.out.write(byteArray);
                        } else {
                            this.out.write(value.intValue() & 127);
                        }
                        this.out.flush();
                        debugPrint(element.appendLabel(element.rangeExpression()));
                        return;
                    }
                }
                throw new IllegalArgumentException("enum value " + value + " " + Hex.toHexString(value.toByteArray()) + " no in defined child list");
            case INT:
                ASN1Integer aSN1Integer = ASN1Integer.getInstance(aSN1Primitive);
                int iIntBytesForRange = element.intBytesForRange();
                if (iIntBytesForRange > 0) {
                    byte[] bArrAsUnsignedByteArray = BigIntegers.asUnsignedByteArray(iIntBytesForRange, aSN1Integer.getValue());
                    switch (iIntBytesForRange) {
                        case 1:
                        case 2:
                        case 4:
                        case 8:
                            this.out.write(bArrAsUnsignedByteArray);
                            break;
                        case 3:
                        case 5:
                        case 6:
                        case 7:
                        default:
                            throw new IllegalStateException("unknown uint length " + iIntBytesForRange);
                    }
                } else if (iIntBytesForRange < 0) {
                    BigInteger value2 = aSN1Integer.getValue();
                    switch (iIntBytesForRange) {
                        case -8:
                            bArrLongToBigEndian = Pack.longToBigEndian(BigIntegers.longValueExact(value2));
                            break;
                        case Msi.INSTALLSTATE_NOTUSED /* -7 */:
                        case -6:
                        case Msi.INSTALLSTATE_INCOMPLETE /* -5 */:
                        case -3:
                        default:
                            throw new IllegalStateException("unknown twos compliment length");
                        case -4:
                            bArrLongToBigEndian = Pack.intToBigEndian(BigIntegers.intValueExact(value2));
                            break;
                        case -2:
                            bArrLongToBigEndian = Pack.shortToBigEndian(BigIntegers.shortValueExact(value2));
                            break;
                        case -1:
                            bArrLongToBigEndian = new byte[]{BigIntegers.byteValueExact(value2)};
                            break;
                    }
                    this.out.write(bArrLongToBigEndian);
                } else {
                    byte[] bArrAsUnsignedByteArray2 = element.isLowerRangeZero() ? BigIntegers.asUnsignedByteArray(aSN1Integer.getValue()) : aSN1Integer.getValue().toByteArray();
                    encodeLength(bArrAsUnsignedByteArray2.length);
                    this.out.write(bArrAsUnsignedByteArray2);
                }
                debugPrint(element.appendLabel(element.rangeExpression()));
                this.out.flush();
                return;
            case OCTET_STRING:
                byte[] octets = ASN1OctetString.getInstance(aSN1Primitive).getOctets();
                if (element.isFixedLength()) {
                    this.out.write(octets);
                } else {
                    encodeLength(octets.length);
                    this.out.write(octets);
                }
                debugPrint(element.appendLabel(element.rangeExpression()));
                this.out.flush();
                return;
            case IA5String:
                byte[] octets2 = ASN1IA5String.getInstance(aSN1Primitive).getOctets();
                if (element.isFixedLength() && element.getUpperBound().intValue() != octets2.length) {
                    throw new IOException("IA5String string length does not equal declared fixed length " + octets2.length + " " + element.getUpperBound());
                }
                if (element.isFixedLength()) {
                    this.out.write(octets2);
                } else {
                    encodeLength(octets2.length);
                    this.out.write(octets2);
                }
                debugPrint(element.appendLabel(""));
                this.out.flush();
                return;
            case UTF8_STRING:
                byte[] uTF8ByteArray = Strings.toUTF8ByteArray(ASN1UTF8String.getInstance(aSN1Primitive).getString());
                encodeLength(uTF8ByteArray.length);
                this.out.write(uTF8ByteArray);
                debugPrint(element.appendLabel(""));
                this.out.flush();
                return;
            case BIT_STRING:
                ASN1BitString aSN1BitString = ASN1BitString.getInstance(aSN1Primitive);
                byte[] bytes = aSN1BitString.getBytes();
                if (element.isFixedLength()) {
                    this.out.write(bytes);
                    debugPrint(element.appendLabel(element.rangeExpression()));
                } else {
                    int padBits = aSN1BitString.getPadBits();
                    encodeLength(bytes.length + 1);
                    this.out.write(padBits);
                    this.out.write(bytes);
                    debugPrint(element.appendLabel(element.rangeExpression()));
                }
                this.out.flush();
                return;
            case NULL:
            case ENUM_ITEM:
            default:
                return;
            case EXTENSION:
                byte[] octets3 = ASN1OctetString.getInstance(aSN1Primitive).getOctets();
                if (element.isFixedLength()) {
                    this.out.write(octets3);
                } else {
                    encodeLength(octets3.length);
                    this.out.write(octets3);
                }
                debugPrint(element.appendLabel(element.rangeExpression()));
                this.out.flush();
                return;
            case BOOLEAN:
                debugPrint(element.getLabel());
                if (ASN1Boolean.getInstance(aSN1Primitive).isTrue()) {
                    this.out.write(255);
                } else {
                    this.out.write(0);
                }
                this.out.flush();
                return;
        }
    }

    protected void debugPrint(String str) {
        if (this.debugOutput != null) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            int i = -1;
            for (int i2 = 0; i2 != stackTrace.length; i2++) {
                StackTraceElement stackTraceElement = stackTrace[i2];
                if (stackTraceElement.getMethodName().equals("debugPrint")) {
                    i = 0;
                } else if (stackTraceElement.getClassName().contains("OERInput")) {
                    i++;
                }
            }
            while (i > 0) {
                this.debugOutput.append((CharSequence) "    ");
                i--;
            }
            this.debugOutput.append((CharSequence) str).append((CharSequence) "\n");
            this.debugOutput.flush();
        }
    }

    private void encodeLength(long j) throws IOException {
        if (j <= 127) {
            this.out.write((int) j);
            return;
        }
        byte[] bArrAsUnsignedByteArray = BigIntegers.asUnsignedByteArray(BigInteger.valueOf(j));
        this.out.write(bArrAsUnsignedByteArray.length | 128);
        this.out.write(bArrAsUnsignedByteArray);
    }

    private void encodeQuantity(long j) throws IOException {
        byte[] bArrAsUnsignedByteArray = BigIntegers.asUnsignedByteArray(BigInteger.valueOf(j));
        this.out.write(bArrAsUnsignedByteArray.length);
        this.out.write(bArrAsUnsignedByteArray);
    }

    @Override // java.io.OutputStream
    public void write(int i) throws IOException {
        this.out.write(i);
    }

    public void writePlainType(ASN1Encodable aSN1Encodable, Element element) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OEROutputStream oEROutputStream = new OEROutputStream(byteArrayOutputStream);
        oEROutputStream.write(aSN1Encodable, element);
        oEROutputStream.flush();
        oEROutputStream.close();
        encodeLength(byteArrayOutputStream.size());
        write(byteArrayOutputStream.toByteArray());
    }
}
