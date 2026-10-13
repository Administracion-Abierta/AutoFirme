package org.apache.commons.codec.binary;

import com.sun.jna.platform.win32.WinNT;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import org.apache.commons.codec.CodecPolicy;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/binary/Base64.class */
public class Base64 extends BaseNCodec {
    private static final int BITS_PER_ENCODED_BYTE = 6;
    private static final int BYTES_PER_UNENCODED_BLOCK = 3;
    private static final int BYTES_PER_ENCODED_BLOCK = 4;
    private static final int ALPHABET_LENGTH = 64;
    private static final int DECODING_TABLE_LENGTH = 256;
    private static final byte[] STANDARD_ENCODE_TABLE = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    private static final byte[] URL_SAFE_ENCODE_TABLE = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};
    private static final byte[] DECODE_TABLE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51};
    private static final int MASK_6BITS = 63;
    private static final int MASK_4BITS = 15;
    private static final int MASK_2BITS = 3;
    private final byte[] encodeTable;
    private final byte[] decodeTable;
    private final byte[] lineSeparator;
    private final int encodeSize;
    private final boolean isUrlSafe;

    /* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/binary/Base64$Builder.class */
    public static class Builder extends BaseNCodec.AbstractBuilder<Base64, Builder> {
        public Builder() {
            super(Base64.STANDARD_ENCODE_TABLE);
        }

        @Override // java.util.function.Supplier
        public Base64 get() {
            return new Base64(getLineLength(), getLineSeparator(), getPadding(), getEncodeTable(), getDecodingPolicy());
        }

        public Builder setUrlSafe(boolean urlSafe) {
            return setEncodeTable(Base64.toUrlSafeEncodeTable(urlSafe));
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static byte[] decodeBase64(byte[] base64Data) {
        return new Base64().decode(base64Data);
    }

    public static byte[] decodeBase64(String base64String) {
        return new Base64().decode(base64String);
    }

    public static BigInteger decodeInteger(byte[] pArray) {
        return new BigInteger(1, decodeBase64(pArray));
    }

    public static byte[] encodeBase64(byte[] binaryData) {
        return encodeBase64(binaryData, false);
    }

    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked) {
        return encodeBase64(binaryData, isChunked, false);
    }

    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe) {
        return encodeBase64(binaryData, isChunked, urlSafe, WinNT.MAXLONG);
    }

    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe, int maxResultSize) {
        if (BinaryCodec.isEmpty(binaryData)) {
            return binaryData;
        }
        Base64 b64 = isChunked ? new Base64(urlSafe) : new Base64(0, CHUNK_SEPARATOR, urlSafe);
        long len = b64.getEncodedLength(binaryData);
        if (len > maxResultSize) {
            throw new IllegalArgumentException("Input array too big, the output array would be bigger (" + len + ") than the specified maximum size of " + maxResultSize);
        }
        return b64.encode(binaryData);
    }

    public static byte[] encodeBase64Chunked(byte[] binaryData) {
        return encodeBase64(binaryData, true);
    }

    public static String encodeBase64String(byte[] binaryData) {
        return StringUtils.newStringUsAscii(encodeBase64(binaryData, false));
    }

    public static byte[] encodeBase64URLSafe(byte[] binaryData) {
        return encodeBase64(binaryData, false, true);
    }

    public static String encodeBase64URLSafeString(byte[] binaryData) {
        return StringUtils.newStringUsAscii(encodeBase64(binaryData, false, true));
    }

    public static byte[] encodeInteger(BigInteger bigInteger) {
        Objects.requireNonNull(bigInteger, "bigInteger");
        return encodeBase64(toIntegerBytes(bigInteger), false);
    }

    @Deprecated
    public static boolean isArrayByteBase64(byte[] arrayOctet) {
        return isBase64(arrayOctet);
    }

    public static boolean isBase64(byte octet) {
        return octet == 61 || (octet >= 0 && octet < DECODE_TABLE.length && DECODE_TABLE[octet] != -1);
    }

    public static boolean isBase64(byte[] arrayOctet) {
        for (byte element : arrayOctet) {
            if (!isBase64(element) && !Character.isWhitespace(element)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isBase64(String base64) {
        return isBase64(StringUtils.getBytesUtf8(base64));
    }

    static byte[] toIntegerBytes(BigInteger bigInt) {
        int bitlen = ((bigInt.bitLength() + 7) >> 3) << 3;
        byte[] bigBytes = bigInt.toByteArray();
        if (bigInt.bitLength() % 8 != 0 && (bigInt.bitLength() / 8) + 1 == bitlen / 8) {
            return bigBytes;
        }
        int startSrc = 0;
        int len = bigBytes.length;
        if (bigInt.bitLength() % 8 == 0) {
            startSrc = 1;
            len--;
        }
        int startDst = (bitlen / 8) - len;
        byte[] resizedBytes = new byte[bitlen / 8];
        System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, len);
        return resizedBytes;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] toUrlSafeEncodeTable(boolean urlSafe) {
        return urlSafe ? URL_SAFE_ENCODE_TABLE : STANDARD_ENCODE_TABLE;
    }

    public Base64() {
        this(0);
    }

    public Base64(boolean urlSafe) {
        this(76, CHUNK_SEPARATOR, urlSafe);
    }

    public Base64(int lineLength) {
        this(lineLength, CHUNK_SEPARATOR);
    }

    public Base64(int lineLength, byte[] lineSeparator) {
        this(lineLength, lineSeparator, false);
    }

    public Base64(int lineLength, byte[] lineSeparator, boolean urlSafe) {
        this(lineLength, lineSeparator, (byte) 61, toUrlSafeEncodeTable(urlSafe), DECODING_POLICY_DEFAULT);
    }

    public Base64(int lineLength, byte[] lineSeparator, boolean urlSafe, CodecPolicy decodingPolicy) {
        this(lineLength, lineSeparator, (byte) 61, toUrlSafeEncodeTable(urlSafe), decodingPolicy);
    }

    private Base64(int lineLength, byte[] lineSeparator, byte padding, byte[] encodeTable, CodecPolicy decodingPolicy) {
        super(3, 4, lineLength, toLength(lineSeparator), padding, decodingPolicy);
        Objects.requireNonNull(encodeTable, "encodeTable");
        if (encodeTable.length != 64) {
            throw new IllegalArgumentException("encodeTable must have exactly 64 entries.");
        }
        this.isUrlSafe = encodeTable == URL_SAFE_ENCODE_TABLE;
        if (encodeTable == STANDARD_ENCODE_TABLE || this.isUrlSafe) {
            this.decodeTable = DECODE_TABLE;
            this.encodeTable = encodeTable;
        } else {
            this.encodeTable = (byte[]) encodeTable.clone();
            this.decodeTable = calculateDecodeTable(this.encodeTable);
        }
        if (lineSeparator != null) {
            byte[] lineSeparatorCopy = (byte[]) lineSeparator.clone();
            if (containsAlphabetOrPad(lineSeparatorCopy)) {
                String sep = StringUtils.newStringUtf8(lineSeparatorCopy);
                throw new IllegalArgumentException("lineSeparator must not contain base64 characters: [" + sep + "]");
            }
            if (lineLength > 0) {
                this.encodeSize = 4 + lineSeparatorCopy.length;
                this.lineSeparator = lineSeparatorCopy;
                return;
            } else {
                this.encodeSize = 4;
                this.lineSeparator = null;
                return;
            }
        }
        this.encodeSize = 4;
        this.lineSeparator = null;
    }

    private byte[] calculateDecodeTable(byte[] encodeTable) {
        byte[] decodeTable = new byte[256];
        Arrays.fill(decodeTable, (byte) -1);
        for (int i = 0; i < encodeTable.length; i++) {
            decodeTable[encodeTable[i]] = (byte) i;
        }
        return decodeTable;
    }

    @Override // org.apache.commons.codec.binary.BaseNCodec
    void decode(byte[] input, int inPos, int inAvail, BaseNCodec.Context context) {
        byte b;
        if (context.eof) {
            return;
        }
        if (inAvail < 0) {
            context.eof = true;
        }
        int decodeSize = this.encodeSize - 1;
        for (int i = 0; i < inAvail; i++) {
            byte[] buffer = ensureBufferSize(decodeSize, context);
            int i2 = inPos;
            inPos++;
            byte b2 = input[i2];
            if (b2 == this.pad) {
                context.eof = true;
                break;
            }
            if (b2 >= 0 && b2 < this.decodeTable.length && (b = this.decodeTable[b2]) >= 0) {
                context.modulus = (context.modulus + 1) % 4;
                context.ibitWorkArea = (context.ibitWorkArea << 6) + b;
                if (context.modulus == 0) {
                    int i3 = context.pos;
                    context.pos = i3 + 1;
                    buffer[i3] = (byte) ((context.ibitWorkArea >> 16) & 255);
                    int i4 = context.pos;
                    context.pos = i4 + 1;
                    buffer[i4] = (byte) ((context.ibitWorkArea >> 8) & 255);
                    int i5 = context.pos;
                    context.pos = i5 + 1;
                    buffer[i5] = (byte) (context.ibitWorkArea & 255);
                }
            }
        }
        if (context.eof && context.modulus != 0) {
            byte[] buffer2 = ensureBufferSize(decodeSize, context);
            switch (context.modulus) {
                case 1:
                    validateTrailingCharacter();
                    return;
                case 2:
                    validateCharacter(15, context);
                    context.ibitWorkArea >>= 4;
                    int i6 = context.pos;
                    context.pos = i6 + 1;
                    buffer2[i6] = (byte) (context.ibitWorkArea & 255);
                    return;
                case 3:
                    validateCharacter(3, context);
                    context.ibitWorkArea >>= 2;
                    int i7 = context.pos;
                    context.pos = i7 + 1;
                    buffer2[i7] = (byte) ((context.ibitWorkArea >> 8) & 255);
                    int i8 = context.pos;
                    context.pos = i8 + 1;
                    buffer2[i8] = (byte) (context.ibitWorkArea & 255);
                    return;
                default:
                    throw new IllegalStateException("Impossible modulus " + context.modulus);
            }
        }
    }

    @Override // org.apache.commons.codec.binary.BaseNCodec
    void encode(byte[] in, int inPos, int inAvail, BaseNCodec.Context context) {
        if (context.eof) {
            return;
        }
        if (inAvail < 0) {
            context.eof = true;
            if (0 == context.modulus && this.lineLength == 0) {
                return;
            }
            byte[] buffer = ensureBufferSize(this.encodeSize, context);
            int savedPos = context.pos;
            switch (context.modulus) {
                case 0:
                    break;
                case 1:
                    int i = context.pos;
                    context.pos = i + 1;
                    buffer[i] = this.encodeTable[(context.ibitWorkArea >> 2) & 63];
                    int i2 = context.pos;
                    context.pos = i2 + 1;
                    buffer[i2] = this.encodeTable[(context.ibitWorkArea << 4) & 63];
                    if (this.encodeTable == STANDARD_ENCODE_TABLE) {
                        int i3 = context.pos;
                        context.pos = i3 + 1;
                        buffer[i3] = this.pad;
                        int i4 = context.pos;
                        context.pos = i4 + 1;
                        buffer[i4] = this.pad;
                    }
                    break;
                case 2:
                    int i5 = context.pos;
                    context.pos = i5 + 1;
                    buffer[i5] = this.encodeTable[(context.ibitWorkArea >> 10) & 63];
                    int i6 = context.pos;
                    context.pos = i6 + 1;
                    buffer[i6] = this.encodeTable[(context.ibitWorkArea >> 4) & 63];
                    int i7 = context.pos;
                    context.pos = i7 + 1;
                    buffer[i7] = this.encodeTable[(context.ibitWorkArea << 2) & 63];
                    if (this.encodeTable == STANDARD_ENCODE_TABLE) {
                        int i8 = context.pos;
                        context.pos = i8 + 1;
                        buffer[i8] = this.pad;
                    }
                    break;
                default:
                    throw new IllegalStateException("Impossible modulus " + context.modulus);
            }
            context.currentLinePos += context.pos - savedPos;
            if (this.lineLength > 0 && context.currentLinePos > 0) {
                System.arraycopy(this.lineSeparator, 0, buffer, context.pos, this.lineSeparator.length);
                context.pos += this.lineSeparator.length;
                return;
            }
            return;
        }
        for (int i9 = 0; i9 < inAvail; i9++) {
            byte[] buffer2 = ensureBufferSize(this.encodeSize, context);
            context.modulus = (context.modulus + 1) % 3;
            int i10 = inPos;
            inPos++;
            int b = in[i10];
            if (b < 0) {
                b += 256;
            }
            context.ibitWorkArea = (context.ibitWorkArea << 8) + b;
            if (0 == context.modulus) {
                int i11 = context.pos;
                context.pos = i11 + 1;
                buffer2[i11] = this.encodeTable[(context.ibitWorkArea >> 18) & 63];
                int i12 = context.pos;
                context.pos = i12 + 1;
                buffer2[i12] = this.encodeTable[(context.ibitWorkArea >> 12) & 63];
                int i13 = context.pos;
                context.pos = i13 + 1;
                buffer2[i13] = this.encodeTable[(context.ibitWorkArea >> 6) & 63];
                int i14 = context.pos;
                context.pos = i14 + 1;
                buffer2[i14] = this.encodeTable[context.ibitWorkArea & 63];
                context.currentLinePos += 4;
                if (this.lineLength > 0 && this.lineLength <= context.currentLinePos) {
                    System.arraycopy(this.lineSeparator, 0, buffer2, context.pos, this.lineSeparator.length);
                    context.pos += this.lineSeparator.length;
                    context.currentLinePos = 0;
                }
            }
        }
    }

    byte[] getLineSeparator() {
        return this.lineSeparator;
    }

    @Override // org.apache.commons.codec.binary.BaseNCodec
    protected boolean isInAlphabet(byte octet) {
        return octet >= 0 && octet < this.decodeTable.length && this.decodeTable[octet] != -1;
    }

    public boolean isUrlSafe() {
        return this.isUrlSafe;
    }

    private void validateCharacter(int emptyBitsMask, BaseNCodec.Context context) {
        if (isStrictDecoding() && (context.ibitWorkArea & emptyBitsMask) != 0) {
            throw new IllegalArgumentException("Strict decoding: Last encoded character (before the paddings if any) is a valid base 64 alphabet but not a possible encoding. Expected the discarded bits from the character to be zero.");
        }
    }

    private void validateTrailingCharacter() {
        if (isStrictDecoding()) {
            throw new IllegalArgumentException("Strict decoding: Last encoded character (before the paddings if any) is a valid base 64 alphabet but not a possible encoding. Decoding requires at least two trailing 6-bit characters to create bytes.");
        }
    }
}
