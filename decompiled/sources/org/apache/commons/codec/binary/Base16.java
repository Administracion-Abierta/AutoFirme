package org.apache.commons.codec.binary;

import java.util.Objects;
import org.apache.commons.codec.CodecPolicy;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/binary/Base16.class */
public class Base16 extends BaseNCodec {
    private static final int BITS_PER_ENCODED_BYTE = 4;
    private static final int BYTES_PER_ENCODED_BLOCK = 2;
    private static final int BYTES_PER_UNENCODED_BLOCK = 1;
    private static final byte[] UPPER_CASE_DECODE_TABLE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15};
    private static final byte[] UPPER_CASE_ENCODE_TABLE = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};
    private static final byte[] LOWER_CASE_DECODE_TABLE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15};
    private static final byte[] LOWER_CASE_ENCODE_TABLE = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
    private static final int MASK_4BITS = 15;
    private final byte[] decodeTable;
    private final byte[] encodeTable;

    public Base16() {
        this(false);
    }

    public Base16(boolean lowerCase) {
        this(lowerCase, DECODING_POLICY_DEFAULT);
    }

    private Base16(boolean lowerCase, byte[] encodeTable, CodecPolicy decodingPolicy) {
        super(1, 2, 0, 0, (byte) 61, decodingPolicy);
        Objects.requireNonNull(encodeTable, "encodeTable");
        this.encodeTable = encodeTable;
        this.decodeTable = encodeTable == LOWER_CASE_ENCODE_TABLE ? LOWER_CASE_DECODE_TABLE : UPPER_CASE_DECODE_TABLE;
    }

    public Base16(boolean lowerCase, CodecPolicy decodingPolicy) {
        this(lowerCase, lowerCase ? LOWER_CASE_ENCODE_TABLE : UPPER_CASE_ENCODE_TABLE, decodingPolicy);
    }

    @Override // org.apache.commons.codec.binary.BaseNCodec
    void decode(byte[] data, int offset, int length, BaseNCodec.Context context) {
        if (context.eof || length < 0) {
            context.eof = true;
            if (context.ibitWorkArea != 0) {
                validateTrailingCharacter();
                return;
            }
            return;
        }
        int dataLen = Math.min(data.length - offset, length);
        int availableChars = (context.ibitWorkArea != 0 ? 1 : 0) + dataLen;
        if (availableChars == 1 && availableChars == dataLen) {
            context.ibitWorkArea = decodeOctet(data[offset]) + 1;
            return;
        }
        int charsToProcess = availableChars % 2 == 0 ? availableChars : availableChars - 1;
        int end = offset + dataLen;
        byte[] buffer = ensureBufferSize(charsToProcess / 2, context);
        if (dataLen < availableChars) {
            int result = (context.ibitWorkArea - 1) << 4;
            offset++;
            int result2 = result | decodeOctet(data[offset]);
            int i = context.pos;
            context.pos = i + 1;
            buffer[i] = (byte) result2;
            context.ibitWorkArea = 0;
        }
        int loopEnd = end - 1;
        while (offset < loopEnd) {
            int i2 = offset;
            int offset2 = offset + 1;
            int result3 = decodeOctet(data[i2]) << 4;
            offset = offset2 + 1;
            int result4 = result3 | decodeOctet(data[offset2]);
            int i3 = context.pos;
            context.pos = i3 + 1;
            buffer[i3] = (byte) result4;
        }
        if (offset < end) {
            context.ibitWorkArea = decodeOctet(data[offset]) + 1;
        }
    }

    private int decodeOctet(byte octet) {
        int decoded = -1;
        if ((octet & 255) < this.decodeTable.length) {
            decoded = this.decodeTable[octet];
        }
        if (decoded == -1) {
            throw new IllegalArgumentException("Invalid octet in encoded value: " + ((int) octet));
        }
        return decoded;
    }

    @Override // org.apache.commons.codec.binary.BaseNCodec
    void encode(byte[] data, int offset, int length, BaseNCodec.Context context) {
        if (context.eof) {
            return;
        }
        if (length < 0) {
            context.eof = true;
            return;
        }
        int size = length * 2;
        if (size < 0) {
            throw new IllegalArgumentException("Input length exceeds maximum size for encoded data: " + length);
        }
        byte[] buffer = ensureBufferSize(size, context);
        int end = offset + length;
        for (int i = offset; i < end; i++) {
            byte b = data[i];
            int high = (b >> 4) & 15;
            int low = b & 15;
            int i2 = context.pos;
            context.pos = i2 + 1;
            buffer[i2] = this.encodeTable[high];
            int i3 = context.pos;
            context.pos = i3 + 1;
            buffer[i3] = this.encodeTable[low];
        }
    }

    @Override // org.apache.commons.codec.binary.BaseNCodec
    public boolean isInAlphabet(byte octet) {
        return (octet & 255) < this.decodeTable.length && this.decodeTable[octet] != -1;
    }

    private void validateTrailingCharacter() {
        if (isStrictDecoding()) {
            throw new IllegalArgumentException("Strict decoding: Last encoded character is a valid base 16 alphabet character but not a possible encoding. Decoding requires at least two characters to create one byte.");
        }
    }
}
