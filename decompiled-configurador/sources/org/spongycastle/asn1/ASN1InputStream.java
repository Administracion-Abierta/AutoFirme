package org.spongycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.spongycastle.asn1.eac.CertificateBody;
import org.spongycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/ASN1InputStream.class */
public class ASN1InputStream extends FilterInputStream implements BERTags {
    private final int limit;
    private final boolean lazyEvaluate;
    private final byte[][] tmpBuffers;

    public ASN1InputStream(InputStream is) {
        this(is, StreamUtil.findLimit(is));
    }

    public ASN1InputStream(byte[] input) {
        this(new ByteArrayInputStream(input), input.length);
    }

    public ASN1InputStream(byte[] input, boolean lazyEvaluate) {
        this(new ByteArrayInputStream(input), input.length, lazyEvaluate);
    }

    public ASN1InputStream(InputStream input, int limit) {
        this(input, limit, false);
    }

    public ASN1InputStream(InputStream input, boolean lazyEvaluate) {
        this(input, StreamUtil.findLimit(input), lazyEvaluate);
    }

    public ASN1InputStream(InputStream input, int limit, boolean lazyEvaluate) {
        super(input);
        this.limit = limit;
        this.lazyEvaluate = lazyEvaluate;
        this.tmpBuffers = new byte[11][];
    }

    int getLimit() {
        return this.limit;
    }

    protected int readLength() throws IOException {
        return readLength(this, this.limit);
    }

    protected void readFully(byte[] bytes) throws IOException {
        if (Streams.readFully(this, bytes) != bytes.length) {
            throw new EOFException("EOF encountered in middle of object");
        }
    }

    protected ASN1Primitive buildObject(int tag, int tagNo, int length) throws IOException {
        boolean isConstructed = (tag & 32) != 0;
        DefiniteLengthInputStream defIn = new DefiniteLengthInputStream(this, length);
        if ((tag & 64) != 0) {
            return new DERApplicationSpecific(isConstructed, tagNo, defIn.toByteArray());
        }
        if ((tag & 128) != 0) {
            return new ASN1StreamParser(defIn).readTaggedObject(isConstructed, tagNo);
        }
        if (isConstructed) {
            switch (tagNo) {
                case 4:
                    ASN1EncodableVector v = buildDEREncodableVector(defIn);
                    ASN1OctetString[] strings = new ASN1OctetString[v.size()];
                    for (int i = 0; i != strings.length; i++) {
                        strings[i] = (ASN1OctetString) v.get(i);
                    }
                    return new BEROctetString(strings);
                case 8:
                    return new DERExternal(buildDEREncodableVector(defIn));
                case 16:
                    if (this.lazyEvaluate) {
                        return new LazyEncodedSequence(defIn.toByteArray());
                    }
                    return DERFactory.createSequence(buildDEREncodableVector(defIn));
                case 17:
                    return DERFactory.createSet(buildDEREncodableVector(defIn));
                default:
                    throw new IOException("unknown tag " + tagNo + " encountered");
            }
        }
        return createPrimitiveDERObject(tagNo, defIn, this.tmpBuffers);
    }

    ASN1EncodableVector buildEncodableVector() throws IOException {
        ASN1EncodableVector v = new ASN1EncodableVector();
        while (true) {
            ASN1Primitive o = readObject();
            if (o != null) {
                v.add(o);
            } else {
                return v;
            }
        }
    }

    ASN1EncodableVector buildDEREncodableVector(DefiniteLengthInputStream dIn) throws IOException {
        return new ASN1InputStream(dIn).buildEncodableVector();
    }

    public ASN1Primitive readObject() throws IOException {
        int tag = read();
        if (tag <= 0) {
            if (tag == 0) {
                throw new IOException("unexpected end-of-contents marker");
            }
            return null;
        }
        int tagNo = readTagNumber(this, tag);
        boolean isConstructed = (tag & 32) != 0;
        int length = readLength();
        if (length < 0) {
            if (!isConstructed) {
                throw new IOException("indefinite-length primitive encoding encountered");
            }
            IndefiniteLengthInputStream indIn = new IndefiniteLengthInputStream(this, this.limit);
            ASN1StreamParser sp = new ASN1StreamParser(indIn, this.limit);
            if ((tag & 64) != 0) {
                return new BERApplicationSpecificParser(tagNo, sp).getLoadedObject();
            }
            if ((tag & 128) != 0) {
                return new BERTaggedObjectParser(true, tagNo, sp).getLoadedObject();
            }
            switch (tagNo) {
                case 4:
                    return new BEROctetStringParser(sp).getLoadedObject();
                case 8:
                    return new DERExternalParser(sp).getLoadedObject();
                case 16:
                    return new BERSequenceParser(sp).getLoadedObject();
                case 17:
                    return new BERSetParser(sp).getLoadedObject();
                default:
                    throw new IOException("unknown BER object encountered");
            }
        }
        try {
            return buildObject(tag, tagNo, length);
        } catch (IllegalArgumentException e) {
            throw new ASN1Exception("corrupted stream detected", e);
        }
    }

    static int readTagNumber(InputStream s, int tag) throws IOException {
        int tagNo = tag & 31;
        if (tagNo == 31) {
            int tagNo2 = 0;
            int b = s.read();
            if ((b & CertificateBody.profileType) == 0) {
                throw new IOException("corrupted stream - invalid high tag number found");
            }
            while (b >= 0 && (b & 128) != 0) {
                tagNo2 = (tagNo2 | (b & CertificateBody.profileType)) << 7;
                b = s.read();
            }
            if (b < 0) {
                throw new EOFException("EOF found inside tag value.");
            }
            tagNo = tagNo2 | (b & CertificateBody.profileType);
        }
        return tagNo;
    }

    static int readLength(InputStream s, int limit) throws IOException {
        int length = s.read();
        if (length < 0) {
            throw new EOFException("EOF found when length expected");
        }
        if (length == 128) {
            return -1;
        }
        if (length > 127) {
            int size = length & CertificateBody.profileType;
            if (size > 4) {
                throw new IOException("DER length more than 4 bytes: " + size);
            }
            length = 0;
            for (int i = 0; i < size; i++) {
                int next = s.read();
                if (next < 0) {
                    throw new EOFException("EOF found reading length");
                }
                length = (length << 8) + next;
            }
            if (length < 0) {
                throw new IOException("corrupted stream - negative length found");
            }
            if (length >= limit) {
                throw new IOException("corrupted stream - out of bounds length found");
            }
        }
        return length;
    }

    private static byte[] getBuffer(DefiniteLengthInputStream defIn, byte[][] tmpBuffers) throws IOException {
        int len = defIn.getRemaining();
        if (defIn.getRemaining() < tmpBuffers.length) {
            byte[] buf = tmpBuffers[len];
            if (buf == null) {
                byte[] bArr = new byte[len];
                tmpBuffers[len] = bArr;
                buf = bArr;
            }
            Streams.readFully(defIn, buf);
            return buf;
        }
        return defIn.toByteArray();
    }

    private static char[] getBMPCharBuffer(DefiniteLengthInputStream defIn) throws IOException {
        int ch1;
        int ch2;
        int len = defIn.getRemaining() / 2;
        char[] buf = new char[len];
        int totalRead = 0;
        while (totalRead < len && (ch1 = defIn.read()) >= 0 && (ch2 = defIn.read()) >= 0) {
            int i = totalRead;
            totalRead++;
            buf[i] = (char) ((ch1 << 8) | (ch2 & 255));
        }
        return buf;
    }

    static ASN1Primitive createPrimitiveDERObject(int tagNo, DefiniteLengthInputStream defIn, byte[][] tmpBuffers) throws IOException {
        switch (tagNo) {
            case 1:
                return ASN1Boolean.fromOctetString(getBuffer(defIn, tmpBuffers));
            case 2:
                return new ASN1Integer(defIn.toByteArray(), false);
            case 3:
                return ASN1BitString.fromInputStream(defIn.getRemaining(), defIn);
            case 4:
                return new DEROctetString(defIn.toByteArray());
            case 5:
                return DERNull.INSTANCE;
            case 6:
                return ASN1ObjectIdentifier.fromOctetString(getBuffer(defIn, tmpBuffers));
            case 7:
            case 8:
            case 9:
            case 11:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 29:
            default:
                throw new IOException("unknown tag " + tagNo + " encountered");
            case 10:
                return ASN1Enumerated.fromOctetString(getBuffer(defIn, tmpBuffers));
            case 12:
                return new DERUTF8String(defIn.toByteArray());
            case 18:
                return new DERNumericString(defIn.toByteArray());
            case 19:
                return new DERPrintableString(defIn.toByteArray());
            case 20:
                return new DERT61String(defIn.toByteArray());
            case 21:
                return new DERVideotexString(defIn.toByteArray());
            case 22:
                return new DERIA5String(defIn.toByteArray());
            case 23:
                return new ASN1UTCTime(defIn.toByteArray());
            case 24:
                return new ASN1GeneralizedTime(defIn.toByteArray());
            case 25:
                return new DERGraphicString(defIn.toByteArray());
            case 26:
                return new DERVisibleString(defIn.toByteArray());
            case 27:
                return new DERGeneralString(defIn.toByteArray());
            case 28:
                return new DERUniversalString(defIn.toByteArray());
            case 30:
                return new DERBMPString(getBMPCharBuffer(defIn));
        }
    }
}
