package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/ASN1InputStream.class */
public class ASN1InputStream extends FilterInputStream implements BERTags {
    private final int limit;
    private final boolean lazyEvaluate;
    private final byte[][] tmpBuffers;

    public ASN1InputStream(InputStream inputStream) {
        this(inputStream, StreamUtil.findLimit(inputStream));
    }

    public ASN1InputStream(byte[] bArr) {
        this(new ByteArrayInputStream(bArr), bArr.length);
    }

    public ASN1InputStream(byte[] bArr, boolean z) {
        this(new ByteArrayInputStream(bArr), bArr.length, z);
    }

    public ASN1InputStream(InputStream inputStream, int i) {
        this(inputStream, i, false);
    }

    public ASN1InputStream(InputStream inputStream, boolean z) {
        this(inputStream, StreamUtil.findLimit(inputStream), z);
    }

    public ASN1InputStream(InputStream inputStream, int i, boolean z) {
        this(inputStream, i, z, new byte[11][]);
    }

    private ASN1InputStream(InputStream inputStream, int i, boolean z, byte[][] bArr) {
        super(inputStream);
        this.limit = i;
        this.lazyEvaluate = z;
        this.tmpBuffers = bArr;
    }

    int getLimit() {
        return this.limit;
    }

    protected int readLength() throws IOException {
        return readLength(this, this.limit, false);
    }

    protected void readFully(byte[] bArr) throws IOException {
        if (Streams.readFully(this, bArr, 0, bArr.length) != bArr.length) {
            throw new EOFException("EOF encountered in middle of object");
        }
    }

    protected ASN1Primitive buildObject(int i, int i2, int i3) throws IOException {
        DefiniteLengthInputStream definiteLengthInputStream = new DefiniteLengthInputStream(this, i3, this.limit);
        if (0 == (i & 224)) {
            return createPrimitiveDERObject(i2, definiteLengthInputStream, this.tmpBuffers);
        }
        int i4 = i & 192;
        if (0 != i4) {
            return readTaggedObjectDL(i4, i2, (i & 32) != 0, definiteLengthInputStream);
        }
        switch (i2) {
            case 3:
                return buildConstructedBitString(readVector(definiteLengthInputStream));
            case 4:
                return buildConstructedOctetString(readVector(definiteLengthInputStream));
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            default:
                throw new IOException("unknown tag " + i2 + " encountered");
            case 8:
                return DLFactory.createSequence(readVector(definiteLengthInputStream)).toASN1External();
            case 16:
                if (definiteLengthInputStream.getRemaining() < 1) {
                    return DLFactory.EMPTY_SEQUENCE;
                }
                return this.lazyEvaluate ? new LazyEncodedSequence(definiteLengthInputStream.toByteArray()) : DLFactory.createSequence(readVector(definiteLengthInputStream));
            case 17:
                return DLFactory.createSet(readVector(definiteLengthInputStream));
        }
    }

    public ASN1Primitive readObject() throws IOException {
        int i = read();
        if (i <= 0) {
            if (i == 0) {
                throw new IOException("unexpected end-of-contents marker");
            }
            return null;
        }
        int tagNumber = readTagNumber(this, i);
        int length = readLength();
        if (length >= 0) {
            try {
                return buildObject(i, tagNumber, length);
            } catch (IllegalArgumentException e) {
                throw new ASN1Exception("corrupted stream detected", e);
            }
        }
        if (0 == (i & 32)) {
            throw new IOException("indefinite-length primitive encoding encountered");
        }
        ASN1StreamParser aSN1StreamParser = new ASN1StreamParser(new IndefiniteLengthInputStream(this, this.limit), this.limit, this.tmpBuffers);
        int i2 = i & 192;
        if (0 != i2) {
            return aSN1StreamParser.loadTaggedIL(i2, tagNumber);
        }
        switch (tagNumber) {
            case 3:
                return BERBitStringParser.parse(aSN1StreamParser);
            case 4:
                return BEROctetStringParser.parse(aSN1StreamParser);
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            default:
                throw new IOException("unknown BER object encountered");
            case 8:
                return DERExternalParser.parse(aSN1StreamParser);
            case 16:
                return BERSequenceParser.parse(aSN1StreamParser);
            case 17:
                return BERSetParser.parse(aSN1StreamParser);
        }
    }

    ASN1BitString buildConstructedBitString(ASN1EncodableVector aSN1EncodableVector) throws IOException {
        ASN1BitString[] aSN1BitStringArr = new ASN1BitString[aSN1EncodableVector.size()];
        for (int i = 0; i != aSN1BitStringArr.length; i++) {
            ASN1Encodable aSN1Encodable = aSN1EncodableVector.get(i);
            if (!(aSN1Encodable instanceof ASN1BitString)) {
                throw new ASN1Exception("unknown object encountered in constructed BIT STRING: " + aSN1Encodable.getClass());
            }
            aSN1BitStringArr[i] = (ASN1BitString) aSN1Encodable;
        }
        return new BERBitString(aSN1BitStringArr);
    }

    ASN1OctetString buildConstructedOctetString(ASN1EncodableVector aSN1EncodableVector) throws IOException {
        ASN1OctetString[] aSN1OctetStringArr = new ASN1OctetString[aSN1EncodableVector.size()];
        for (int i = 0; i != aSN1OctetStringArr.length; i++) {
            ASN1Encodable aSN1Encodable = aSN1EncodableVector.get(i);
            if (!(aSN1Encodable instanceof ASN1OctetString)) {
                throw new ASN1Exception("unknown object encountered in constructed OCTET STRING: " + aSN1Encodable.getClass());
            }
            aSN1OctetStringArr[i] = (ASN1OctetString) aSN1Encodable;
        }
        return new BEROctetString(aSN1OctetStringArr);
    }

    ASN1Primitive readTaggedObjectDL(int i, int i2, boolean z, DefiniteLengthInputStream definiteLengthInputStream) throws IOException {
        return !z ? ASN1TaggedObject.createPrimitive(i, i2, definiteLengthInputStream.toByteArray()) : ASN1TaggedObject.createConstructedDL(i, i2, readVector(definiteLengthInputStream));
    }

    ASN1EncodableVector readVector() throws IOException {
        ASN1Primitive object;
        ASN1Primitive object2 = readObject();
        if (null == object2) {
            return new ASN1EncodableVector(0);
        }
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        do {
            aSN1EncodableVector.add(object2);
            object = readObject();
            object2 = object;
        } while (object != null);
        return aSN1EncodableVector;
    }

    ASN1EncodableVector readVector(DefiniteLengthInputStream definiteLengthInputStream) throws IOException {
        int remaining = definiteLengthInputStream.getRemaining();
        return remaining < 1 ? new ASN1EncodableVector(0) : new ASN1InputStream(definiteLengthInputStream, remaining, this.lazyEvaluate, this.tmpBuffers).readVector();
    }

    static int readTagNumber(InputStream inputStream, int i) throws IOException {
        int i2 = i & 31;
        if (i2 == 31) {
            int i3 = inputStream.read();
            if (i3 < 31) {
                if (i3 < 0) {
                    throw new EOFException("EOF found inside tag value.");
                }
                throw new IOException("corrupted stream - high tag number < 31 found");
            }
            i2 = i3 & 127;
            if (0 == i2) {
                throw new IOException("corrupted stream - invalid high tag number found");
            }
            while ((i3 & 128) != 0) {
                if ((i2 >>> 24) != 0) {
                    throw new IOException("Tag number more than 31 bits");
                }
                int i4 = i2 << 7;
                i3 = inputStream.read();
                if (i3 < 0) {
                    throw new EOFException("EOF found inside tag value.");
                }
                i2 = i4 | (i3 & 127);
            }
        }
        return i2;
    }

    static int readLength(InputStream inputStream, int i, boolean z) throws IOException {
        int i2 = inputStream.read();
        if (0 == (i2 >>> 7)) {
            return i2;
        }
        if (128 == i2) {
            return -1;
        }
        if (i2 < 0) {
            throw new EOFException("EOF found when length expected");
        }
        if (255 == i2) {
            throw new IOException("invalid long form definite-length 0xFF");
        }
        int i3 = i2 & 127;
        int i4 = 0;
        int i5 = 0;
        do {
            int i6 = inputStream.read();
            if (i6 < 0) {
                throw new EOFException("EOF found reading length");
            }
            if ((i5 >>> 23) != 0) {
                throw new IOException("long form definite-length more than 31 bits");
            }
            i5 = (i5 << 8) + i6;
            i4++;
        } while (i4 < i3);
        if (i5 < i || z) {
            return i5;
        }
        throw new IOException("corrupted stream - out of bounds length found: " + i5 + " >= " + i);
    }

    private static byte[] getBuffer(DefiniteLengthInputStream definiteLengthInputStream, byte[][] bArr) throws IOException {
        int remaining = definiteLengthInputStream.getRemaining();
        if (remaining >= bArr.length) {
            return definiteLengthInputStream.toByteArray();
        }
        byte[] bArr2 = bArr[remaining];
        if (bArr2 == null) {
            byte[] bArr3 = new byte[remaining];
            bArr[remaining] = bArr3;
            bArr2 = bArr3;
        }
        definiteLengthInputStream.readAllIntoByteArray(bArr2);
        return bArr2;
    }

    private static char[] getBMPCharBuffer(DefiniteLengthInputStream definiteLengthInputStream) throws IOException {
        int remaining = definiteLengthInputStream.getRemaining();
        if (0 != (remaining & 1)) {
            throw new IOException("malformed BMPString encoding encountered");
        }
        char[] cArr = new char[remaining / 2];
        int i = 0;
        byte[] bArr = new byte[8];
        while (remaining >= 8) {
            if (Streams.readFully(definiteLengthInputStream, bArr, 0, 8) != 8) {
                throw new EOFException("EOF encountered in middle of BMPString");
            }
            cArr[i] = (char) ((bArr[0] << 8) | (bArr[1] & 255));
            cArr[i + 1] = (char) ((bArr[2] << 8) | (bArr[3] & 255));
            cArr[i + 2] = (char) ((bArr[4] << 8) | (bArr[5] & 255));
            cArr[i + 3] = (char) ((bArr[6] << 8) | (bArr[7] & 255));
            i += 4;
            remaining -= 8;
        }
        if (remaining > 0) {
            if (Streams.readFully(definiteLengthInputStream, bArr, 0, remaining) != remaining) {
                throw new EOFException("EOF encountered in middle of BMPString");
            }
            int i2 = 0;
            do {
                int i3 = i2;
                int i4 = i2 + 1;
                i2 = i4 + 1;
                int i5 = i;
                i++;
                cArr[i5] = (char) ((bArr[i3] << 8) | (bArr[i4] & 255));
            } while (i2 < remaining);
        }
        if (0 == definiteLengthInputStream.getRemaining() && cArr.length == i) {
            return cArr;
        }
        throw new IllegalStateException();
    }

    static ASN1Primitive createPrimitiveDERObject(int i, DefiniteLengthInputStream definiteLengthInputStream, byte[][] bArr) throws IOException {
        try {
            switch (i) {
                case 1:
                    return ASN1Boolean.createPrimitive(getBuffer(definiteLengthInputStream, bArr));
                case 2:
                    return ASN1Integer.createPrimitive(definiteLengthInputStream.toByteArray());
                case 3:
                    return ASN1BitString.createPrimitive(definiteLengthInputStream.toByteArray());
                case 4:
                    return ASN1OctetString.createPrimitive(definiteLengthInputStream.toByteArray());
                case 5:
                    return ASN1Null.createPrimitive(definiteLengthInputStream.toByteArray());
                case 6:
                    ASN1ObjectIdentifier.checkContentsLength(definiteLengthInputStream.getRemaining());
                    return ASN1ObjectIdentifier.createPrimitive(getBuffer(definiteLengthInputStream, bArr), true);
                case 7:
                    return ASN1ObjectDescriptor.createPrimitive(definiteLengthInputStream.toByteArray());
                case 8:
                case 9:
                case 11:
                case 15:
                case 16:
                case 17:
                case 29:
                default:
                    throw new IOException("unknown tag " + i + " encountered");
                case 10:
                    return ASN1Enumerated.createPrimitive(getBuffer(definiteLengthInputStream, bArr), true);
                case 12:
                    return ASN1UTF8String.createPrimitive(definiteLengthInputStream.toByteArray());
                case 13:
                    ASN1RelativeOID.checkContentsLength(definiteLengthInputStream.getRemaining());
                    return ASN1RelativeOID.createPrimitive(getBuffer(definiteLengthInputStream, bArr), true);
                case 14:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                    throw new IOException("unsupported tag " + i + " encountered");
                case 18:
                    return ASN1NumericString.createPrimitive(definiteLengthInputStream.toByteArray());
                case 19:
                    return ASN1PrintableString.createPrimitive(definiteLengthInputStream.toByteArray());
                case 20:
                    return ASN1T61String.createPrimitive(definiteLengthInputStream.toByteArray());
                case 21:
                    return ASN1VideotexString.createPrimitive(definiteLengthInputStream.toByteArray());
                case 22:
                    return ASN1IA5String.createPrimitive(definiteLengthInputStream.toByteArray());
                case 23:
                    return ASN1UTCTime.createPrimitive(definiteLengthInputStream.toByteArray());
                case 24:
                    return ASN1GeneralizedTime.createPrimitive(definiteLengthInputStream.toByteArray());
                case 25:
                    return ASN1GraphicString.createPrimitive(definiteLengthInputStream.toByteArray());
                case 26:
                    return ASN1VisibleString.createPrimitive(definiteLengthInputStream.toByteArray());
                case 27:
                    return ASN1GeneralString.createPrimitive(definiteLengthInputStream.toByteArray());
                case 28:
                    return ASN1UniversalString.createPrimitive(definiteLengthInputStream.toByteArray());
                case 30:
                    return ASN1BMPString.createPrimitive(getBMPCharBuffer(definiteLengthInputStream));
            }
        } catch (IllegalArgumentException e) {
            throw new ASN1Exception(e.getMessage(), e);
        } catch (IllegalStateException e2) {
            throw new ASN1Exception(e2.getMessage(), e2);
        }
    }
}
