package es.gob.afirma.core.signers.der;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/signers/der/DerValue.class */
public class DerValue {
    public static final byte TAG_UNIVERSAL = 0;
    public static final byte TAG_APPLICATION = 64;
    public static final byte TAG_CONTEXT = -128;
    public static final byte TAG_PRIVATE = -64;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int MAX_BUFFER_SIZE = 2147483639;
    public byte tag;
    protected DerInputBuffer buffer;
    public final DerInputStream data;
    private int length;
    public static final byte tag_Boolean = 1;
    public static final byte tag_Integer = 2;
    public static final byte tag_BitString = 3;
    public static final byte tag_OctetString = 4;
    public static final byte tag_Null = 5;
    public static final byte tag_ObjectId = 6;
    public static final byte tag_Enumerated = 10;
    public static final byte tag_UTF8String = 12;
    public static final byte tag_PrintableString = 19;
    public static final byte tag_T61String = 20;
    public static final byte tag_IA5String = 22;
    public static final byte tag_UtcTime = 23;
    public static final byte tag_GeneralizedTime = 24;
    public static final byte tag_GeneralString = 27;
    public static final byte tag_UniversalString = 28;
    public static final byte tag_BMPString = 30;
    public static final byte tag_Sequence = 48;
    public static final byte tag_SequenceOf = 48;
    public static final byte tag_Set = 49;
    public static final byte tag_SetOf = 49;

    public boolean isUniversal() {
        return (this.tag & 192) == 0;
    }

    public boolean isApplication() {
        return (this.tag & 192) == 64;
    }

    public boolean isContextSpecific() {
        return (this.tag & 192) == 128;
    }

    public boolean isContextSpecific(byte b) {
        return isContextSpecific() && (this.tag & 31) == b;
    }

    boolean isPrivate() {
        return (this.tag & 192) == 192;
    }

    public boolean isConstructed() {
        return (this.tag & 32) == 32;
    }

    public boolean isConstructed(byte b) {
        return isConstructed() && (this.tag & 31) == b;
    }

    public DerValue(String str) throws IOException {
        boolean z = true;
        for (int i = 0; i < str.length(); i++) {
            if (!isPrintableStringChar(str.charAt(i))) {
                z = false;
                break;
            }
        }
        this.data = init(z ? (byte) 19 : (byte) 12, str);
    }

    public DerValue(byte b, String str) throws IOException {
        this.data = init(b, str);
    }

    DerValue(byte b, byte[] bArr, boolean z) {
        this.tag = b;
        this.buffer = new DerInputBuffer((byte[]) bArr.clone(), z);
        this.length = bArr.length;
        this.data = new DerInputStream(this.buffer);
        this.data.mark(Integer.MAX_VALUE);
    }

    public DerValue(byte b, byte[] bArr) {
        this(b, bArr, true);
    }

    DerValue(DerInputBuffer derInputBuffer) throws IOException {
        this.tag = (byte) derInputBuffer.read();
        byte b = (byte) derInputBuffer.read();
        this.length = DerInputStream.getLength(b, derInputBuffer);
        if (this.length == -1) {
            DerInputBuffer derInputBufferDup = derInputBuffer.dup();
            int iAvailable = derInputBufferDup.available();
            byte[] bArr = new byte[iAvailable + 2];
            bArr[0] = this.tag;
            bArr[1] = b;
            DataInputStream dataInputStream = new DataInputStream(derInputBufferDup);
            dataInputStream.readFully(bArr, 2, iAvailable);
            dataInputStream.close();
            DerInputBuffer derInputBuffer2 = new DerInputBuffer(new DerIndefLenConverter().convert(bArr), derInputBuffer.allowBER);
            if (this.tag != derInputBuffer2.read()) {
                throw new IOException("Indefinite length encoding not supported");
            }
            this.length = DerInputStream.getLength(derInputBuffer2);
            this.buffer = derInputBuffer2.dup();
            this.buffer.truncate(this.length);
            this.data = new DerInputStream(this.buffer);
            derInputBuffer.skip(this.length + 2);
            return;
        }
        this.buffer = derInputBuffer.dup();
        this.buffer.truncate(this.length);
        this.data = new DerInputStream(this.buffer);
        derInputBuffer.skip(this.length);
    }

    DerValue(byte[] bArr, boolean z) throws IOException {
        this.data = init(true, new ByteArrayInputStream(bArr), z);
    }

    public DerValue(byte[] bArr) throws IOException {
        this(bArr, true);
    }

    DerValue(byte[] bArr, int i, int i2, boolean z) throws IOException {
        this.data = init(true, new ByteArrayInputStream(bArr, i, i2), z);
    }

    public DerValue(byte[] bArr, int i, int i2) throws IOException {
        this(bArr, i, i2, true);
    }

    DerValue(InputStream inputStream, boolean z) throws IOException {
        this.data = init(false, inputStream, z);
    }

    public DerValue(InputStream inputStream) throws IOException {
        this(inputStream, true);
    }

    private DerInputStream init(byte b, String str) throws IOException {
        String str2;
        this.tag = b;
        switch (b) {
            case 12:
                str2 = "UTF8";
                break;
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 21:
            case 23:
            case 24:
            case 25:
            case 26:
            case 28:
            case 29:
            default:
                throw new IllegalArgumentException("Unsupported DER string type");
            case 19:
            case 22:
            case 27:
                str2 = "ASCII";
                break;
            case 20:
                str2 = "ISO-8859-1";
                break;
            case 30:
                str2 = "UnicodeBigUnmarked";
                break;
        }
        byte[] bytes = str.getBytes(str2);
        this.length = bytes.length;
        this.buffer = new DerInputBuffer(bytes, true);
        DerInputStream derInputStream = new DerInputStream(this.buffer);
        derInputStream.mark(Integer.MAX_VALUE);
        return derInputStream;
    }

    private DerInputStream init(boolean z, InputStream inputStream, boolean z2) throws IOException {
        this.tag = (byte) inputStream.read();
        byte b = (byte) inputStream.read();
        this.length = DerInputStream.getLength(b, inputStream);
        if (this.length == -1) {
            int iAvailable = inputStream.available();
            byte[] bArr = new byte[iAvailable + 2];
            bArr[0] = this.tag;
            bArr[1] = b;
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            dataInputStream.readFully(bArr, 2, iAvailable);
            dataInputStream.close();
            inputStream = new ByteArrayInputStream(new DerIndefLenConverter().convert(bArr));
            if (this.tag != inputStream.read()) {
                throw new IOException("Indefinite length encoding not supported");
            }
            this.length = DerInputStream.getLength(inputStream);
        }
        if (z && inputStream.available() != this.length) {
            throw new IOException("extra data given to DerValue constructor");
        }
        this.buffer = new DerInputBuffer(readExactlyNBytes(inputStream, this.length), z2);
        return new DerInputStream(this.buffer);
    }

    public void encode(DerOutputStream derOutputStream) throws IOException {
        derOutputStream.write(this.tag);
        derOutputStream.putLength(this.length);
        if (this.length > 0) {
            byte[] bArr = new byte[this.length];
            synchronized (this.data) {
                this.buffer.reset();
                if (this.buffer.read(bArr) != this.length) {
                    throw new IOException("short DER value read (encode)");
                }
                derOutputStream.write(bArr);
            }
        }
    }

    public final DerInputStream getData() {
        return this.data;
    }

    public final byte getTag() {
        return this.tag;
    }

    public boolean getBoolean() throws IOException {
        if (this.tag != 1) {
            throw new IOException("DerValue.getBoolean, not a BOOLEAN " + ((int) this.tag));
        }
        if (this.length != 1) {
            throw new IOException("DerValue.getBoolean, invalid length " + this.length);
        }
        if (this.buffer.read() != 0) {
            return true;
        }
        return false;
    }

    public ObjectIdentifier getOID() throws IOException {
        if (this.tag != 6) {
            throw new IOException("DerValue.getOID, not an OID " + ((int) this.tag));
        }
        return new ObjectIdentifier(this.buffer);
    }

    private byte[] append(byte[] bArr, byte[] bArr2) {
        if (bArr == null) {
            return bArr2;
        }
        byte[] bArr3 = new byte[bArr.length + bArr2.length];
        System.arraycopy(bArr, 0, bArr3, 0, bArr.length);
        System.arraycopy(bArr2, 0, bArr3, bArr.length, bArr2.length);
        return bArr3;
    }

    public byte[] getOctetString() throws IOException {
        if (this.tag != 4 && !isConstructed((byte) 4)) {
            throw new IOException("DerValue.getOctetString, not an Octet String: " + ((int) this.tag));
        }
        if (this.length == 0) {
            return new byte[0];
        }
        DerInputBuffer derInputBuffer = this.buffer;
        if (derInputBuffer.available() < this.length) {
            throw new IOException("short read on DerValue buffer");
        }
        byte[] bArr = new byte[this.length];
        derInputBuffer.read(bArr);
        if (isConstructed()) {
            DerInputStream derInputStream = new DerInputStream(bArr, 0, bArr.length, this.buffer.allowBER);
            byte[] bArrAppend = null;
            while (true) {
                bArr = bArrAppend;
                if (derInputStream.available() == 0) {
                    break;
                }
                bArrAppend = append(bArr, derInputStream.getOctetString());
            }
        }
        return bArr;
    }

    public int getInteger() throws IOException {
        if (this.tag != 2) {
            throw new IOException("DerValue.getInteger, not an int " + ((int) this.tag));
        }
        return this.buffer.getInteger(this.data.available());
    }

    public BigInteger getBigInteger() throws IOException {
        if (this.tag != 2) {
            throw new IOException("DerValue.getBigInteger, not an int " + ((int) this.tag));
        }
        return this.buffer.getBigInteger(this.data.available(), false);
    }

    public BigInteger getPositiveBigInteger() throws IOException {
        if (this.tag != 2) {
            throw new IOException("DerValue.getBigInteger, not an int " + ((int) this.tag));
        }
        return this.buffer.getBigInteger(this.data.available(), true);
    }

    public int getEnumerated() throws IOException {
        if (this.tag != 10) {
            throw new IOException("DerValue.getEnumerated, incorrect tag: " + ((int) this.tag));
        }
        return this.buffer.getInteger(this.data.available());
    }

    public byte[] getBitString() throws IOException {
        if (this.tag != 3) {
            throw new IOException("DerValue.getBitString, not a bit string " + ((int) this.tag));
        }
        return this.buffer.getBitString();
    }

    public BitArray getUnalignedBitString() throws IOException {
        if (this.tag != 3) {
            throw new IOException("DerValue.getBitString, not a bit string " + ((int) this.tag));
        }
        return this.buffer.getUnalignedBitString();
    }

    public String getAsString() throws IOException {
        if (this.tag == 12) {
            return getUTF8String();
        }
        if (this.tag == 19) {
            return getPrintableString();
        }
        if (this.tag == 20) {
            return getT61String();
        }
        if (this.tag == 22) {
            return getIA5String();
        }
        if (this.tag == 30) {
            return getBMPString();
        }
        if (this.tag == 27) {
            return getGeneralString();
        }
        return null;
    }

    public byte[] getBitString(boolean z) throws IOException {
        if (!z && this.tag != 3) {
            throw new IOException("DerValue.getBitString, not a bit string " + ((int) this.tag));
        }
        return this.buffer.getBitString();
    }

    public BitArray getUnalignedBitString(boolean z) throws IOException {
        if (!z && this.tag != 3) {
            throw new IOException("DerValue.getBitString, not a bit string " + ((int) this.tag));
        }
        return this.buffer.getUnalignedBitString();
    }

    public byte[] getDataBytes() throws IOException {
        byte[] bArr = new byte[this.length];
        synchronized (this.data) {
            this.data.reset();
            this.data.getBytes(bArr);
        }
        return bArr;
    }

    public String getPrintableString() throws IOException {
        if (this.tag != 19) {
            throw new IOException("DerValue.getPrintableString, not a string " + ((int) this.tag));
        }
        return new String(getDataBytes(), "ASCII");
    }

    public String getT61String() throws IOException {
        if (this.tag != 20) {
            throw new IOException("DerValue.getT61String, not T61 " + ((int) this.tag));
        }
        return new String(getDataBytes(), "ISO-8859-1");
    }

    public String getIA5String() throws IOException {
        if (this.tag != 22) {
            throw new IOException("DerValue.getIA5String, not IA5 " + ((int) this.tag));
        }
        return new String(getDataBytes(), "ASCII");
    }

    public String getBMPString() throws IOException {
        if (this.tag != 30) {
            throw new IOException("DerValue.getBMPString, not BMP " + ((int) this.tag));
        }
        return new String(getDataBytes(), "UnicodeBigUnmarked");
    }

    public String getUTF8String() throws IOException {
        if (this.tag != 12) {
            throw new IOException("DerValue.getUTF8String, not UTF-8 " + ((int) this.tag));
        }
        return new String(getDataBytes(), "UTF8");
    }

    public String getGeneralString() throws IOException {
        if (this.tag != 27) {
            throw new IOException("DerValue.getGeneralString, not GeneralString " + ((int) this.tag));
        }
        return new String(getDataBytes(), "ASCII");
    }

    public boolean equals(Object obj) {
        if (obj instanceof DerValue) {
            return equals((DerValue) obj);
        }
        return false;
    }

    public boolean equals(DerValue derValue) {
        if (this == derValue) {
            return true;
        }
        if (this.tag != derValue.tag) {
            return false;
        }
        if (this.data == derValue.data) {
            return true;
        }
        if (System.identityHashCode(this.data) > System.identityHashCode(derValue.data)) {
            return doEquals(this, derValue);
        }
        return doEquals(derValue, this);
    }

    private static boolean doEquals(DerValue derValue, DerValue derValue2) {
        boolean zEquals;
        synchronized (derValue.data) {
            synchronized (derValue2.data) {
                derValue.data.reset();
                derValue2.data.reset();
                zEquals = derValue.buffer.equals(derValue2.buffer);
            }
        }
        return zEquals;
    }

    public String toString() {
        try {
            String asString = getAsString();
            if (asString != null) {
                return "\"" + asString + "\"";
            }
            if (this.tag == 5) {
                return "[DerValue, null]";
            }
            if (this.tag == 6) {
                return "OID." + getOID();
            }
            return "[DerValue, tag = " + ((int) this.tag) + ", length = " + this.length + "]";
        } catch (IOException e) {
            throw new IllegalArgumentException("misformatted DER value");
        }
    }

    public byte[] toByteArray() throws IOException {
        DerOutputStream derOutputStream = new DerOutputStream();
        encode(derOutputStream);
        this.data.reset();
        return derOutputStream.toByteArray();
    }

    public DerInputStream toDerInputStream() throws IOException {
        if (this.tag == 48 || this.tag == 49) {
            return new DerInputStream(this.buffer);
        }
        throw new IOException("toDerInputStream rejects tag type " + ((int) this.tag));
    }

    public int length() {
        return this.length;
    }

    public static boolean isPrintableStringChar(char c) {
        if (c >= 'a' && c <= 'z') {
            return true;
        }
        if (c >= 'A' && c <= 'Z') {
            return true;
        }
        if (c >= '0' && c <= '9') {
            return true;
        }
        switch (c) {
            case ' ':
            case EACTags.INTERCHANGE_CONTROL /* 39 */:
            case '(':
            case ')':
            case '+':
            case ',':
            case '-':
            case '.':
            case '/':
            case ':':
            case '=':
            case '?':
                return true;
            case '!':
            case '\"':
            case '#':
            case EACTags.APPLICATION_EXPIRATION_DATE /* 36 */:
            case EACTags.APPLICATION_EFFECTIVE_DATE /* 37 */:
            case EACTags.CARD_EFFECTIVE_DATE /* 38 */:
            case '*':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case ';':
            case '<':
            case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA256 /* 62 */:
            default:
                return false;
        }
    }

    public static byte createTag(byte b, boolean z, byte b2) {
        byte b3 = (byte) (b | b2);
        if (z) {
            b3 = (byte) (b3 | 32);
        }
        return b3;
    }

    public void resetTag(byte b) {
        this.tag = b;
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public static byte[] readExactlyNBytes(InputStream inputStream, int i) throws IOException {
        if (i < 0) {
            throw new IOException("length cannot be negative: " + i);
        }
        byte[] nBytes = readNBytes(inputStream, i);
        if (nBytes.length < i) {
            throw new EOFException();
        }
        return nBytes;
    }

    public static byte[] readNBytes(InputStream inputStream, int i) throws IOException {
        int i2;
        if (i < 0) {
            throw new IllegalArgumentException("len < 0");
        }
        ArrayList<byte[]> arrayList = null;
        byte[] bArr = null;
        int i3 = 0;
        int i4 = i;
        do {
            byte[] bArr2 = new byte[Math.min(i4, 8192)];
            int i5 = 0;
            while (true) {
                i2 = inputStream.read(bArr2, i5, Math.min(bArr2.length - i5, i4));
                if (i2 <= 0) {
                    break;
                }
                i5 += i2;
                i4 -= i2;
            }
            if (i5 > 0) {
                if (MAX_BUFFER_SIZE - i3 < i5) {
                    throw new OutOfMemoryError("Required array size too large");
                }
                i3 += i5;
                if (bArr == null) {
                    bArr = bArr2;
                } else {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                        arrayList.add(bArr);
                    }
                    arrayList.add(bArr2);
                }
            }
            if (i2 < 0) {
                break;
            }
        } while (i4 > 0);
        if (arrayList == null) {
            if (bArr == null) {
                return new byte[0];
            }
            return bArr.length == i3 ? bArr : Arrays.copyOf(bArr, i3);
        }
        byte[] bArr3 = new byte[i3];
        int i6 = 0;
        int i7 = i3;
        for (byte[] bArr4 : arrayList) {
            int iMin = Math.min(bArr4.length, i7);
            System.arraycopy(bArr4, 0, bArr3, i6, iMin);
            i6 += iMin;
            i7 -= iMin;
        }
        return bArr3;
    }
}
