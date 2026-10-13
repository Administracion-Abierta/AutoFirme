package es.gob.afirma.core.signers.der;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/signers/der/DerOutputStream.class */
public class DerOutputStream extends ByteArrayOutputStream implements DerEncoder {
    private static ByteArrayLexOrder lexOrder = new ByteArrayLexOrder();
    private static ByteArrayTagOrder tagOrder = new ByteArrayTagOrder();

    public DerOutputStream(int i) {
        super(i);
    }

    public DerOutputStream() {
    }

    public void write(byte b, byte[] bArr) throws IOException {
        write(b);
        putLength(bArr.length);
        write(bArr, 0, bArr.length);
    }

    public void write(byte b, DerOutputStream derOutputStream) throws IOException {
        write(b);
        putLength(derOutputStream.count);
        write(derOutputStream.buf, 0, derOutputStream.count);
    }

    public void writeImplicit(byte b, DerOutputStream derOutputStream) throws IOException {
        write(b);
        write(derOutputStream.buf, 1, derOutputStream.count - 1);
    }

    public void putDerValue(DerValue derValue) throws IOException {
        derValue.encode(this);
    }

    public void putBoolean(boolean z) throws IOException {
        write(1);
        putLength(1);
        if (z) {
            write(255);
        } else {
            write(0);
        }
    }

    public void putEnumerated(int i) throws IOException {
        write(10);
        putIntegerContents(i);
    }

    public void putInteger(BigInteger bigInteger) throws IOException {
        write(2);
        byte[] byteArray = bigInteger.toByteArray();
        putLength(byteArray.length);
        write(byteArray, 0, byteArray.length);
    }

    public void putInteger(Integer num) throws IOException {
        putInteger(num.intValue());
    }

    public void putInteger(int i) throws IOException {
        write(2);
        putIntegerContents(i);
    }

    private void putIntegerContents(int i) throws IOException {
        int i2 = 0;
        byte[] bArr = {(byte) ((i & (-16777216)) >>> 24), (byte) ((i & 16711680) >>> 16), (byte) ((i & CipherSuite.DRAFT_TLS_DHE_RSA_WITH_AES_128_OCB) >>> 8), (byte) (i & 255)};
        if (bArr[0] == -1) {
            for (int i3 = 0; i3 < 3 && bArr[i3] == -1 && (bArr[i3 + 1] & 128) == 128; i3++) {
                i2++;
            }
        } else if (bArr[0] == 0) {
            for (int i4 = 0; i4 < 3 && bArr[i4] == 0 && (bArr[i4 + 1] & 128) == 0; i4++) {
                i2++;
            }
        }
        putLength(4 - i2);
        for (int i5 = i2; i5 < 4; i5++) {
            write(bArr[i5]);
        }
    }

    public void putBitString(byte[] bArr) throws IOException {
        write(3);
        putLength(bArr.length + 1);
        write(0);
        write(bArr);
    }

    public void putUnalignedBitString(BitArray bitArray) throws IOException {
        byte[] byteArray = bitArray.toByteArray();
        write(3);
        putLength(byteArray.length + 1);
        write((byteArray.length * 8) - bitArray.length());
        write(byteArray);
    }

    public void putTruncatedUnalignedBitString(BitArray bitArray) throws IOException {
        putUnalignedBitString(bitArray.truncate());
    }

    public void putOctetString(byte[] bArr) throws IOException {
        write((byte) 4, bArr);
    }

    public void putNull() throws IOException {
        write(5);
        putLength(0);
    }

    public void putOID(ObjectIdentifier objectIdentifier) throws IOException {
        objectIdentifier.encode(this);
    }

    public void putSequence(DerValue[] derValueArr) throws IOException {
        DerOutputStream derOutputStream = new DerOutputStream();
        for (DerValue derValue : derValueArr) {
            derValue.encode(derOutputStream);
        }
        write((byte) 48, derOutputStream);
    }

    public void putSet(DerValue[] derValueArr) throws IOException {
        DerOutputStream derOutputStream = new DerOutputStream();
        for (DerValue derValue : derValueArr) {
            derValue.encode(derOutputStream);
        }
        write((byte) 49, derOutputStream);
    }

    public void putOrderedSetOf(byte b, DerEncoder[] derEncoderArr) throws IOException {
        putOrderedSet(b, derEncoderArr, lexOrder);
    }

    public void putOrderedSet(byte b, DerEncoder[] derEncoderArr) throws IOException {
        putOrderedSet(b, derEncoderArr, tagOrder);
    }

    private void putOrderedSet(byte b, DerEncoder[] derEncoderArr, Comparator<byte[]> comparator) throws IOException {
        DerOutputStream[] derOutputStreamArr = new DerOutputStream[derEncoderArr.length];
        for (int i = 0; i < derEncoderArr.length; i++) {
            derOutputStreamArr[i] = new DerOutputStream();
            derEncoderArr[i].derEncode(derOutputStreamArr[i]);
        }
        byte[][] bArr = new byte[derOutputStreamArr.length][];
        for (int i2 = 0; i2 < derOutputStreamArr.length; i2++) {
            bArr[i2] = derOutputStreamArr[i2].toByteArray();
        }
        Arrays.sort(bArr, comparator);
        DerOutputStream derOutputStream = new DerOutputStream();
        for (int i3 = 0; i3 < derOutputStreamArr.length; i3++) {
            derOutputStream.write(bArr[i3]);
        }
        write(b, derOutputStream);
    }

    public void putUTF8String(String str) throws IOException {
        writeString(str, (byte) 12, "UTF8");
    }

    public void putPrintableString(String str) throws IOException {
        writeString(str, (byte) 19, "ASCII");
    }

    public void putT61String(String str) throws IOException {
        writeString(str, (byte) 20, "ISO-8859-1");
    }

    public void putIA5String(String str) throws IOException {
        writeString(str, (byte) 22, "ASCII");
    }

    public void putBMPString(String str) throws IOException {
        writeString(str, (byte) 30, "UnicodeBigUnmarked");
    }

    public void putGeneralString(String str) throws IOException {
        writeString(str, (byte) 27, "ASCII");
    }

    private void writeString(String str, byte b, String str2) throws IOException {
        byte[] bytes = str.getBytes(str2);
        write(b);
        putLength(bytes.length);
        write(bytes);
    }

    public void putUTCTime(Date date) throws IOException {
        putTime(date, (byte) 23);
    }

    public void putGeneralizedTime(Date date) throws IOException {
        putTime(date, (byte) 24);
    }

    private void putTime(Date date, byte b) throws IOException {
        String str;
        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        if (b == 23) {
            str = "yyMMddHHmmss'Z'";
        } else {
            b = 24;
            str = "yyyyMMddHHmmss'Z'";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str, Locale.US);
        simpleDateFormat.setTimeZone(timeZone);
        byte[] bytes = simpleDateFormat.format(date).getBytes("ISO-8859-1");
        write(b);
        putLength(bytes.length);
        write(bytes);
    }

    public void putLength(int i) throws IOException {
        if (i < 128) {
            write((byte) i);
            return;
        }
        if (i < 256) {
            write(-127);
            write((byte) i);
            return;
        }
        if (i < 65536) {
            write(-126);
            write((byte) (i >> 8));
            write((byte) i);
        } else {
            if (i < 16777216) {
                write(-125);
                write((byte) (i >> 16));
                write((byte) (i >> 8));
                write((byte) i);
                return;
            }
            write(-124);
            write((byte) (i >> 24));
            write((byte) (i >> 16));
            write((byte) (i >> 8));
            write((byte) i);
        }
    }

    public void putTag(byte b, boolean z, byte b2) {
        byte b3 = (byte) (b | b2);
        if (z) {
            b3 = (byte) (b3 | 32);
        }
        write(b3);
    }

    @Override // es.gob.afirma.core.signers.der.DerEncoder
    public void derEncode(OutputStream outputStream) throws IOException {
        outputStream.write(toByteArray());
    }
}
