package org.bouncycastle.oer;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/OERInputStream.class */
public class OERInputStream extends FilterInputStream {
    private static final int[] bits = {1, 2, 4, 8, 16, 32, 64, 128};
    private static final int[] bitsR = {128, 64, 32, 16, 8, 4, 2, 1};
    protected PrintWriter debugOutput;
    private int maxByteAllocation;
    protected PrintWriter debugStream;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/OERInputStream$Choice.class */
    public static class Choice extends OERInputStream {
        final int preamble;
        final int tag;
        final int tagClass;

        public Choice(InputStream inputStream) throws IOException {
            int i;
            super(inputStream);
            this.preamble = read();
            if (this.preamble < 0) {
                throw new EOFException("expecting preamble byte of choice");
            }
            this.tagClass = this.preamble & 192;
            int i2 = this.preamble & 63;
            if (i2 >= 63) {
                i2 = 0;
                do {
                    i = inputStream.read();
                    if (i < 0) {
                        throw new EOFException("expecting further tag bytes");
                    }
                    i2 = (i2 << 7) | (i & 127);
                } while ((i & 128) != 0);
            }
            this.tag = i2;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("CHOICE(");
            switch (this.tagClass) {
                case 0:
                    sb.append("Universal ");
                    break;
                case 64:
                    sb.append("Application ");
                    break;
                case 128:
                    sb.append("ContextSpecific ");
                    break;
                case 192:
                    sb.append("Private ");
                    break;
            }
            sb.append("Tag = " + this.tag);
            sb.append(")");
            return sb.toString();
        }

        public int getTagClass() {
            return this.tagClass;
        }

        public int getTag() {
            return this.tag;
        }

        public boolean isContextSpecific() {
            return this.tagClass == 128;
        }

        public boolean isUniversalTagClass() {
            return this.tagClass == 0;
        }

        public boolean isApplicationTagClass() {
            return this.tagClass == 64;
        }

        public boolean isPrivateTagClass() {
            return this.tagClass == 192;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/OERInputStream$LengthInfo.class */
    private static final class LengthInfo {
        private final BigInteger length;
        private final boolean shortForm;

        public LengthInfo(BigInteger bigInteger, boolean z) {
            this.length = bigInteger;
            this.shortForm = z;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int intLength() {
            return BigIntegers.intValueExact(this.length);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/OERInputStream$Sequence.class */
    public static class Sequence extends OERInputStream {
        private final int preamble;
        private final boolean[] valuePresent;
        private final boolean extensionFlagSet;

        public Sequence(InputStream inputStream, Element element) throws IOException {
            super(inputStream);
            if (!element.hasPopulatedExtension() && element.getOptionals() <= 0 && !element.hasDefaultChildren()) {
                this.preamble = 0;
                this.extensionFlagSet = false;
                this.valuePresent = null;
                return;
            }
            this.preamble = this.in.read();
            if (this.preamble < 0) {
                throw new EOFException("expecting preamble byte of sequence");
            }
            this.extensionFlagSet = element.hasPopulatedExtension() && (this.preamble & 128) == 128;
            this.valuePresent = new boolean[element.getChildren().size()];
            int i = element.hasPopulatedExtension() ? 6 : 7;
            int i2 = this.preamble;
            int i3 = 0;
            for (Element element2 : element.getChildren()) {
                if (element2.getBaseType() != OERDefinition.BaseType.EXTENSION) {
                    if (element2.getBlock() != 0) {
                        return;
                    }
                    if (element2.isExplicit()) {
                        int i4 = i3;
                        i3++;
                        this.valuePresent[i4] = true;
                    } else {
                        if (i < 0) {
                            i2 = inputStream.read();
                            if (i2 < 0) {
                                throw new EOFException("expecting mask byte sequence");
                            }
                            i = 7;
                        }
                        int i5 = i3;
                        i3++;
                        this.valuePresent[i5] = (i2 & OERInputStream.bits[i]) > 0;
                        i--;
                    }
                }
            }
        }

        public boolean hasOptional(int i) {
            return this.valuePresent[i];
        }

        public boolean hasExtension() {
            return this.extensionFlagSet;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("SEQ(");
            sb.append(hasExtension() ? "Ext " : "");
            if (this.valuePresent == null) {
                sb.append("*");
            } else {
                for (int i = 0; i < this.valuePresent.length; i++) {
                    if (this.valuePresent[i]) {
                        sb.append("1");
                    } else {
                        sb.append("0");
                    }
                }
            }
            sb.append(")");
            return sb.toString();
        }
    }

    public OERInputStream(InputStream inputStream) {
        super(inputStream);
        this.debugOutput = null;
        this.maxByteAllocation = 1048576;
        this.debugStream = null;
    }

    public OERInputStream(InputStream inputStream, int i) {
        super(inputStream);
        this.debugOutput = null;
        this.maxByteAllocation = 1048576;
        this.debugStream = null;
        this.maxByteAllocation = i;
    }

    public static ASN1Encodable parse(byte[] bArr, Element element) throws IOException {
        return new OERInputStream(new ByteArrayInputStream(bArr)).parse(element);
    }

    private int countOptionalChildTypes(Element element) {
        int i = 0;
        Iterator<Element> it = element.getChildren().iterator();
        while (it.hasNext()) {
            i += it.next().isExplicit() ? 0 : 1;
        }
        return i;
    }

    /* JADX WARN: Code duplicated, block: B:226:0x0336 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:229:0x0348 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:55:0x025f  */
    /* JADX WARN: Code duplicated, block: B:57:0x027f  */
    /* JADX WARN: Code duplicated, block: B:59:0x0289  */
    /* JADX WARN: Code duplicated, block: B:66:0x02b9  */
    /* JADX WARN: Code duplicated, block: B:67:0x02c8  */
    /* JADX WARN: Code duplicated, block: B:70:0x02d0  */
    /* JADX WARN: Code duplicated, block: B:72:0x02e5  */
    /* JADX WARN: Code duplicated, block: B:75:0x02f6 A[LOOP:3: B:73:0x02ee->B:75:0x02f6, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:77:0x0304  */
    /* JADX WARN: Code duplicated, block: B:82:0x032e  */
    /* JADX WARN: Code duplicated, block: B:86:0x0340  */
    public ASN1Object parse(Element element) throws IOException {
        byte[] bArrAllocateArray;
        BigInteger bigInteger;
        byte[] bArrAllocateArray2;
        int i;
        int length;
        Element element2;
        Element element3;
        int iIntLength;
        Element elementResult;
        switch (element.getBaseType()) {
            case OPAQUE:
                return parse(new Element(element.resolveSupplier().build(), element));
            case Switch:
                throw new IllegalStateException("A switch element should only be found within a sequence.");
            case Supplier:
                return parse(new Element(element.getElementSupplier().build(), element));
            case SEQ_OF:
                byte[] bArrAllocateArray3 = allocateArray(readLength().intLength());
                if (Streams.readFully(this, bArrAllocateArray3) != bArrAllocateArray3.length) {
                    throw new IOException("could not read all of count of seq-of values");
                }
                int iIntValue = BigIntegers.fromUnsignedByteArray(bArrAllocateArray3).intValue();
                debugPrint(element + "(len = " + iIntValue + ")");
                ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
                if (element.getChildren().get(0).getaSwitch() != null) {
                    throw new IllegalStateException("element def for item in SEQ OF has a switch, switches only supported in sequences");
                }
                for (int i2 = 0; i2 < iIntValue; i2++) {
                    aSN1EncodableVector.add(parse(Element.expandDeferredDefinition(element.getChildren().get(0), element)));
                }
                return new DERSequence(aSN1EncodableVector);
            case SEQ:
                Sequence sequence = new Sequence(this.in, element);
                debugPrint(element + sequence.toString());
                ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
                List<Element> children = element.getChildren();
                int i3 = 0;
                while (i3 < children.size()) {
                    Element element4 = children.get(i3);
                    if (element4.getBaseType() != OERDefinition.BaseType.EXTENSION) {
                        if (element4.getBlock() > 0) {
                            if (sequence.extensionFlagSet) {
                                bArrAllocateArray2 = allocateArray(readLength().intLength());
                                if (Streams.readFully(this.in, bArrAllocateArray2) != bArrAllocateArray2.length) {
                                    throw new IOException("did not fully read presence list.");
                                }
                                i = 8;
                                length = (bArrAllocateArray2.length * 8) - bArrAllocateArray2[0];
                                while (true) {
                                    if (i3 >= children.size() || i < length) {
                                        if (i3 < children.size()) {
                                            element2 = children.get(i3);
                                        } else {
                                            element2 = null;
                                        }
                                        element3 = element2;
                                        if (element3 == null) {
                                            if ((bArrAllocateArray2[i / 8] & bitsR[i % 8]) != 0) {
                                                iIntLength = readLength().intLength();
                                                while (true) {
                                                    iIntLength--;
                                                    if (iIntLength >= 0) {
                                                        this.in.read();
                                                    }
                                                }
                                            }
                                        } else if (i >= length && (bArrAllocateArray2[i / 8] & bitsR[i % 8]) != 0) {
                                            aSN1EncodableVector2.add(parseOpenType(element3));
                                        } else {
                                            if (element3.isExplicit()) {
                                                throw new IOException("extension is marked as explicit but is not defined in presence list");
                                            }
                                            aSN1EncodableVector2.add(OEROptional.ABSENT);
                                        }
                                        i++;
                                        i3++;
                                    }
                                }
                            }
                            return new DERSequence(aSN1EncodableVector2);
                        }
                        Element elementExpandDeferredDefinition = Element.expandDeferredDefinition(element4, element);
                        if (elementExpandDeferredDefinition.getaSwitch() != null) {
                            elementResult = elementExpandDeferredDefinition.getaSwitch().result(new SwitchIndexer.Asn1EncodableVectorIndexer(aSN1EncodableVector2));
                            if (elementResult.getParent() != element) {
                                elementResult = new Element(elementResult, element);
                            }
                        } else {
                            elementResult = elementExpandDeferredDefinition;
                        }
                        if (sequence.valuePresent == null) {
                            aSN1EncodableVector2.add(parse(elementResult));
                        } else if (sequence.valuePresent[i3]) {
                            if (elementResult.isExplicit()) {
                                aSN1EncodableVector2.add(parse(elementResult));
                            } else {
                                aSN1EncodableVector2.add(OEROptional.getInstance(parse(elementResult)));
                            }
                        } else if (elementResult.getDefaultValue() != null) {
                            aSN1EncodableVector2.add(elementExpandDeferredDefinition.getDefaultValue());
                        } else {
                            aSN1EncodableVector2.add(absent(elementExpandDeferredDefinition));
                        }
                    }
                    i3++;
                }
                if (sequence.extensionFlagSet) {
                    bArrAllocateArray2 = allocateArray(readLength().intLength());
                    if (Streams.readFully(this.in, bArrAllocateArray2) != bArrAllocateArray2.length) {
                        throw new IOException("did not fully read presence list.");
                    }
                    i = 8;
                    length = (bArrAllocateArray2.length * 8) - bArrAllocateArray2[0];
                    while (true) {
                        if (i3 >= children.size()) {
                        }
                        if (i3 < children.size()) {
                            element2 = children.get(i3);
                        } else {
                            element2 = null;
                        }
                        element3 = element2;
                        if (element3 == null) {
                            if ((bArrAllocateArray2[i / 8] & bitsR[i % 8]) != 0) {
                                iIntLength = readLength().intLength();
                                while (true) {
                                    iIntLength--;
                                    if (iIntLength >= 0) {
                                        this.in.read();
                                    }
                                }
                            }
                        } else if (i >= length) {
                            if (element3.isExplicit()) {
                                throw new IOException("extension is marked as explicit but is not defined in presence list");
                            }
                            aSN1EncodableVector2.add(OEROptional.ABSENT);
                        } else {
                            if (element3.isExplicit()) {
                                throw new IOException("extension is marked as explicit but is not defined in presence list");
                            }
                            aSN1EncodableVector2.add(OEROptional.ABSENT);
                        }
                        i++;
                        i3++;
                    }
                }
                return new DERSequence(aSN1EncodableVector2);
            case CHOICE:
                Choice choice = choice();
                debugPrint(choice.toString() + " " + choice.tag);
                if (choice.isContextSpecific()) {
                    Element elementExpandDeferredDefinition2 = Element.expandDeferredDefinition(element.getChildren().get(choice.getTag()), element);
                    if (elementExpandDeferredDefinition2.getBlock() > 0) {
                        debugPrint("Chosen (Ext): " + elementExpandDeferredDefinition2);
                        return new DERTaggedObject(choice.tag, parseOpenType(elementExpandDeferredDefinition2));
                    }
                    debugPrint("Chosen: " + elementExpandDeferredDefinition2);
                    return new DERTaggedObject(choice.tag, parse(elementExpandDeferredDefinition2));
                }
                if (choice.isApplicationTagClass()) {
                    throw new IllegalStateException("Unimplemented tag type");
                }
                if (choice.isPrivateTagClass()) {
                    throw new IllegalStateException("Unimplemented tag type");
                }
                if (choice.isUniversalTagClass()) {
                    throw new IllegalStateException("Unimplemented tag type");
                }
                throw new IllegalStateException("Unimplemented tag type");
            case ENUM:
                BigInteger bigIntegerEnumeration = enumeration();
                debugPrint(element + "ENUM(" + bigIntegerEnumeration + ") = " + element.getChildren().get(bigIntegerEnumeration.intValue()).getLabel());
                return new ASN1Enumerated(bigIntegerEnumeration);
            case INT:
                int iIntBytesForRange = element.intBytesForRange();
                if (iIntBytesForRange != 0) {
                    bArrAllocateArray = allocateArray(Math.abs(iIntBytesForRange));
                    Streams.readFully(this, bArrAllocateArray);
                    bigInteger = iIntBytesForRange < 0 ? new BigInteger(bArrAllocateArray) : BigIntegers.fromUnsignedByteArray(bArrAllocateArray);
                } else if (element.isLowerRangeZero()) {
                    bArrAllocateArray = allocateArray(readLength().intLength());
                    Streams.readFully(this, bArrAllocateArray);
                    bigInteger = bArrAllocateArray.length == 0 ? BigInteger.ZERO : new BigInteger(1, bArrAllocateArray);
                } else {
                    bArrAllocateArray = allocateArray(readLength().intLength());
                    Streams.readFully(this, bArrAllocateArray);
                    bigInteger = bArrAllocateArray.length == 0 ? BigInteger.ZERO : new BigInteger(bArrAllocateArray);
                }
                if (this.debugOutput != null) {
                    debugPrint(element + "INTEGER byteLen= " + bArrAllocateArray.length + " hex= " + bigInteger.toString(16) + ")");
                }
                return new ASN1Integer(bigInteger);
            case OCTET_STRING:
                int iIntLength2 = (element.getUpperBound() == null || !element.getUpperBound().equals(element.getLowerBound())) ? readLength().intLength() : element.getUpperBound().intValue();
                byte[] bArrAllocateArray4 = allocateArray(iIntLength2);
                if (Streams.readFully(this, bArrAllocateArray4) != iIntLength2) {
                    throw new IOException("did not read all of " + element.getLabel());
                }
                if (this.debugOutput != null) {
                    debugPrint(element + "OCTET STRING (" + bArrAllocateArray4.length + ") = " + Hex.toHexString(bArrAllocateArray4, 0, Math.min(bArrAllocateArray4.length, 32)) + " " + (bArrAllocateArray4.length > 32 ? "..." : ""));
                }
                return new DEROctetString(bArrAllocateArray4);
            case IA5String:
                byte[] bArrAllocateArray5 = element.isFixedLength() ? allocateArray(element.getUpperBound().intValue()) : allocateArray(readLength().intLength());
                if (Streams.readFully(this, bArrAllocateArray5) != bArrAllocateArray5.length) {
                    throw new IOException("could not read all of IA5 string");
                }
                String strFromByteArray = Strings.fromByteArray(bArrAllocateArray5);
                if (this.debugOutput != null) {
                    debugPrint(element.appendLabel("IA5 String (" + bArrAllocateArray5.length + ") = " + strFromByteArray));
                }
                return new DERIA5String(strFromByteArray);
            case UTF8_STRING:
                byte[] bArrAllocateArray6 = allocateArray(readLength().intLength());
                if (Streams.readFully(this, bArrAllocateArray6) != bArrAllocateArray6.length) {
                    throw new IOException("could not read all of utf 8 string");
                }
                String strFromUTF8ByteArray = Strings.fromUTF8ByteArray(bArrAllocateArray6);
                if (this.debugOutput != null) {
                    debugPrint(element + "UTF8 String (" + bArrAllocateArray6.length + ") = " + strFromUTF8ByteArray);
                }
                return new DERUTF8String(strFromUTF8ByteArray);
            case BIT_STRING:
                byte[] bArrAllocateArray7 = element.isFixedLength() ? new byte[element.getLowerBound().intValue() / 8] : BigInteger.ZERO.compareTo(element.getUpperBound()) > 0 ? allocateArray(element.getUpperBound().intValue() / 8) : allocateArray(readLength().intLength() / 8);
                Streams.readFully(this, bArrAllocateArray7);
                if (this.debugOutput != null) {
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("BIT STRING(" + (bArrAllocateArray7.length * 8) + ") = ");
                    for (int i4 = 0; i4 != bArrAllocateArray7.length; i4++) {
                        byte b = bArrAllocateArray7[i4];
                        for (int i5 = 0; i5 < 8; i5++) {
                            stringBuffer.append((b & 128) > 0 ? "1" : "0");
                            b = (byte) (b << 1);
                        }
                    }
                    debugPrint(element + stringBuffer.toString());
                }
                return new DERBitString(bArrAllocateArray7);
            case NULL:
                debugPrint(element + "NULL");
                return DERNull.INSTANCE;
            case EXTENSION:
                LengthInfo length2 = readLength();
                byte[] bArr = new byte[length2.intLength()];
                if (Streams.readFully(this, bArr) != length2.intLength()) {
                    throw new IOException("could not read all of count of open value in choice (...) ");
                }
                debugPrint("ext " + length2.intLength() + " " + Hex.toHexString(bArr));
                return new DEROctetString(bArr);
            case BOOLEAN:
                return read() == 0 ? ASN1Boolean.FALSE : ASN1Boolean.TRUE;
            default:
                throw new IllegalStateException("Unhandled type " + element.getBaseType());
        }
    }

    private ASN1Encodable absent(Element element) {
        debugPrint(element + "Absent");
        return OEROptional.ABSENT;
    }

    private byte[] allocateArray(int i) {
        if (i > this.maxByteAllocation) {
            throw new IllegalArgumentException("required byte array size " + i + " was greater than " + this.maxByteAllocation);
        }
        return new byte[i];
    }

    public BigInteger parseInt(boolean z, int i) throws Exception {
        byte[] bArr = new byte[i];
        if (Streams.readFully(this, bArr) != bArr.length) {
            throw new IllegalStateException("integer not fully read");
        }
        return z ? new BigInteger(1, bArr) : new BigInteger(bArr);
    }

    public BigInteger uint8() throws Exception {
        return parseInt(true, 1);
    }

    public BigInteger uint16() throws Exception {
        return parseInt(true, 2);
    }

    public BigInteger uint32() throws Exception {
        return parseInt(true, 4);
    }

    public BigInteger uint64() throws Exception {
        return parseInt(false, 8);
    }

    public BigInteger int8() throws Exception {
        return parseInt(false, 1);
    }

    public BigInteger int16() throws Exception {
        return parseInt(false, 2);
    }

    public BigInteger int32() throws Exception {
        return parseInt(false, 4);
    }

    public BigInteger int64() throws Exception {
        return parseInt(false, 8);
    }

    public LengthInfo readLength() throws IOException {
        int i = read();
        if (i == -1) {
            throw new EOFException("expecting length");
        }
        if ((i & 128) == 0) {
            debugPrint("Len (Short form): " + (i & 127));
            return new LengthInfo(BigInteger.valueOf(i & 127), true);
        }
        byte[] bArr = new byte[i & 127];
        if (Streams.readFully(this, bArr) != bArr.length) {
            throw new EOFException("did not read all bytes of length definition");
        }
        debugPrint("Len (Long Form): " + (i & 127) + " actual len: " + Hex.toHexString(bArr));
        return new LengthInfo(BigIntegers.fromUnsignedByteArray(bArr), false);
    }

    public BigInteger enumeration() throws IOException {
        int i = read();
        if (i == -1) {
            throw new EOFException("expecting prefix of enumeration");
        }
        if ((i & 128) != 128) {
            return BigInteger.valueOf(i);
        }
        int i2 = i & 127;
        if (i2 == 0) {
            return BigInteger.ZERO;
        }
        byte[] bArr = new byte[i2];
        if (Streams.readFully(this, bArr) != bArr.length) {
            throw new EOFException("unable to fully read integer component of enumeration");
        }
        return new BigInteger(1, bArr);
    }

    protected ASN1Encodable parseOpenType(Element element) throws IOException {
        byte[] bArrAllocateArray = allocateArray(readLength().intLength());
        if (Streams.readFully(this.in, bArrAllocateArray) != bArrAllocateArray.length) {
            throw new IOException("did not fully read open type as raw bytes");
        }
        OERInputStream oERInputStream = null;
        try {
            OERInputStream oERInputStream2 = new OERInputStream(new ByteArrayInputStream(bArrAllocateArray));
            ASN1Object aSN1Object = oERInputStream2.parse(element);
            if (oERInputStream2 != null) {
            }
            return aSN1Object;
        } finally {
            if (oERInputStream != null) {
                oERInputStream.close();
            }
        }
    }

    public Choice choice() throws IOException {
        return new Choice(this);
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
}
