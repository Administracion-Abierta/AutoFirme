package es.gob.afirma.core.signers.der;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Vector;
import org.spongycastle.asn1.eac.CertificateBody;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/signers/der/DerInputStream.class */
public class DerInputStream {
    DerInputBuffer buffer;
    public byte tag;

    public DerInputStream(byte[] bArr) throws IOException {
        init(bArr, 0, bArr.length, true);
    }

    public DerInputStream(byte[] bArr, int i, int i2, boolean z) throws IOException {
        init(bArr, i, i2, z);
    }

    public DerInputStream(byte[] bArr, int i, int i2) throws IOException {
        init(bArr, i, i2, true);
    }

    private void init(byte[] bArr, int i, int i2, boolean z) throws IOException {
        if (i + 2 > bArr.length || i + i2 > bArr.length) {
            throw new IOException("Encoding bytes too short");
        }
        if (DerIndefLenConverter.isIndefinite(bArr[i + 1])) {
            if (!z) {
                throw new IOException("Indefinite length BER encoding found");
            }
            byte[] bArr2 = new byte[i2];
            System.arraycopy(bArr, i, bArr2, 0, i2);
            this.buffer = new DerInputBuffer(new DerIndefLenConverter().convert(bArr2), z);
        } else {
            this.buffer = new DerInputBuffer(bArr, i, i2, z);
        }
        this.buffer.mark(Integer.MAX_VALUE);
    }

    DerInputStream(DerInputBuffer derInputBuffer) {
        this.buffer = derInputBuffer;
        this.buffer.mark(Integer.MAX_VALUE);
    }

    public DerInputStream subStream(int i, boolean z) throws IOException {
        DerInputBuffer derInputBufferDup = this.buffer.dup();
        derInputBufferDup.truncate(i);
        if (z) {
            this.buffer.skip(i);
        }
        return new DerInputStream(derInputBufferDup);
    }

    public byte[] toByteArray() {
        return this.buffer.toByteArray();
    }

    public int getInteger() throws IOException {
        if (this.buffer.read() != 2) {
            throw new IOException("DER input, Integer tag error");
        }
        return this.buffer.getInteger(getLength(this.buffer));
    }

    public BigInteger getBigInteger() throws IOException {
        if (this.buffer.read() != 2) {
            throw new IOException("DER input, Integer tag error");
        }
        return this.buffer.getBigInteger(getLength(this.buffer), false);
    }

    public BigInteger getPositiveBigInteger() throws IOException {
        if (this.buffer.read() != 2) {
            throw new IOException("DER input, Integer tag error");
        }
        return this.buffer.getBigInteger(getLength(this.buffer), true);
    }

    public int getEnumerated() throws IOException {
        if (this.buffer.read() != 10) {
            throw new IOException("DER input, Enumerated tag error");
        }
        return this.buffer.getInteger(getLength(this.buffer));
    }

    public byte[] getBitString() throws IOException {
        if (this.buffer.read() != 3) {
            throw new IOException("DER input not an bit string");
        }
        return this.buffer.getBitString(getLength(this.buffer));
    }

    public BitArray getUnalignedBitString() throws IOException {
        if (this.buffer.read() != 3) {
            throw new IOException("DER input not a bit string");
        }
        int length = getLength(this.buffer) - 1;
        int i = this.buffer.read();
        if (i < 0) {
            throw new IOException("Unused bits of bit string invalid");
        }
        int i2 = (length * 8) - i;
        if (i2 < 0) {
            throw new IOException("Valid bits of bit string invalid");
        }
        byte[] bArr = new byte[length];
        if (length != 0 && this.buffer.read(bArr) != length) {
            throw new IOException("Short read of DER bit string");
        }
        return new BitArray(i2, bArr);
    }

    public byte[] getOctetString() throws IOException {
        if (this.buffer.read() != 4) {
            throw new IOException("DER input not an octet string");
        }
        int length = getLength(this.buffer);
        byte[] bArr = new byte[length];
        if (length != 0 && this.buffer.read(bArr) != length) {
            throw new IOException("Short read of DER octet string");
        }
        return bArr;
    }

    public void getBytes(byte[] bArr) throws IOException {
        if (bArr.length != 0 && this.buffer.read(bArr) != bArr.length) {
            throw new IOException("Short read of DER octet string");
        }
    }

    public void getNull() throws IOException {
        if (this.buffer.read() != 5 || this.buffer.read() != 0) {
            throw new IOException("getNull, bad data");
        }
    }

    public ObjectIdentifier getOID() throws IOException {
        return new ObjectIdentifier(this);
    }

    public DerValue[] getSequence(int i) throws IOException {
        this.tag = (byte) this.buffer.read();
        if (this.tag != 48) {
            throw new IOException("Sequence tag error");
        }
        return readVector(i);
    }

    public DerValue[] getSet(int i) throws IOException {
        this.tag = (byte) this.buffer.read();
        if (this.tag != 49) {
            throw new IOException("Set tag error");
        }
        return readVector(i);
    }

    public DerValue[] getSet(int i, boolean z) throws IOException {
        this.tag = (byte) this.buffer.read();
        if (!z && this.tag != 49) {
            throw new IOException("Set tag error");
        }
        return readVector(i);
    }

    protected DerValue[] readVector(int i) throws IOException {
        DerInputStream derInputStreamSubStream;
        byte b = (byte) this.buffer.read();
        int length = getLength(b, this.buffer);
        if (length == -1) {
            int iAvailable = this.buffer.available();
            byte[] bArr = new byte[iAvailable + 2];
            bArr[0] = this.tag;
            bArr[1] = b;
            DataInputStream dataInputStream = new DataInputStream(this.buffer);
            dataInputStream.readFully(bArr, 2, iAvailable);
            dataInputStream.close();
            this.buffer = new DerInputBuffer(new DerIndefLenConverter().convert(bArr), this.buffer.allowBER);
            if (this.tag != this.buffer.read()) {
                throw new IOException("Indefinite length encoding not supported");
            }
            length = getLength(this.buffer);
        }
        if (length == 0) {
            return new DerValue[0];
        }
        if (this.buffer.available() == length) {
            derInputStreamSubStream = this;
        } else {
            derInputStreamSubStream = subStream(length, true);
        }
        Vector vector = new Vector(i);
        do {
            vector.addElement(new DerValue(derInputStreamSubStream.buffer, this.buffer.allowBER));
        } while (derInputStreamSubStream.available() > 0);
        if (derInputStreamSubStream.available() != 0) {
            throw new IOException("Extra data at end of vector");
        }
        int size = vector.size();
        DerValue[] derValueArr = new DerValue[size];
        for (int i2 = 0; i2 < size; i2++) {
            derValueArr[i2] = (DerValue) vector.elementAt(i2);
        }
        return derValueArr;
    }

    public DerValue getDerValue() throws IOException {
        return new DerValue(this.buffer);
    }

    public String getUTF8String() throws IOException {
        return readString((byte) 12, "UTF-8", "UTF8");
    }

    public String getPrintableString() throws IOException {
        return readString((byte) 19, "Printable", "ASCII");
    }

    public String getT61String() throws IOException {
        return readString((byte) 20, "T61", "ISO-8859-1");
    }

    public String getIA5String() throws IOException {
        return readString((byte) 22, "IA5", "ASCII");
    }

    public String getBMPString() throws IOException {
        return readString((byte) 30, "BMP", "UnicodeBigUnmarked");
    }

    public String getGeneralString() throws IOException {
        return readString((byte) 27, "General", "ASCII");
    }

    private String readString(byte b, String str, String str2) throws IOException {
        if (this.buffer.read() != b) {
            throw new IOException("DER input not a " + str + " string");
        }
        int length = getLength(this.buffer);
        byte[] bArr = new byte[length];
        if (length != 0 && this.buffer.read(bArr) != length) {
            throw new IOException("Short read of DER " + str + " string");
        }
        return new String(bArr, str2);
    }

    int getByte() throws IOException {
        return 255 & this.buffer.read();
    }

    public int peekByte() throws IOException {
        return this.buffer.peek();
    }

    int getLength() throws IOException {
        return getLength(this.buffer);
    }

    static int getLength(InputStream inputStream) throws IOException {
        return getLength(inputStream.read(), inputStream);
    }

    static int getLength(int i, InputStream inputStream) throws IOException {
        int i2;
        if (i == -1) {
            throw new IOException("Short read of DER length");
        }
        if ((i & 128) == 0) {
            i2 = i;
        } else {
            int i3 = i & CertificateBody.profileType;
            if (i3 == 0) {
                return -1;
            }
            if (i3 < 0 || i3 > 4) {
                throw new IOException("DerInputStream.getLength(): lengthTag=" + i3 + ", " + (i3 < 0 ? "incorrect DER encoding." : "too big."));
            }
            i2 = 255 & inputStream.read();
            int i4 = i3 - 1;
            if (i2 == 0) {
                throw new IOException("DerInputStream.getLength(): Redundant length bytes found");
            }
            while (true) {
                int i5 = i4;
                i4--;
                if (i5 <= 0) {
                    break;
                }
                i2 = (i2 << 8) + (255 & inputStream.read());
            }
            if (i2 < 0) {
                throw new IOException("DerInputStream.getLength(): Invalid length bytes");
            }
            if (i2 <= 127) {
                throw new IOException("DerInputStream.getLength(): Should use short form for length");
            }
        }
        return i2;
    }

    public void mark(int i) {
        this.buffer.mark(i);
    }

    public void reset() {
        this.buffer.reset();
    }

    public int available() {
        return this.buffer.available();
    }
}
