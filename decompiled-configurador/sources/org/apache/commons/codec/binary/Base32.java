package org.apache.commons.codec.binary;

import java.util.Objects;
import org.apache.commons.codec.CodecPolicy;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/binary/Base32.class */
public class Base32 extends BaseNCodec {
    private static final int BITS_PER_ENCODED_BYTE = 5;
    private static final int BYTES_PER_ENCODED_BLOCK = 8;
    private static final int BYTES_PER_UNENCODED_BLOCK = 5;
    private static final long MASK_4BITS = 15;
    private static final long MASK_3BITS = 7;
    private static final long MASK_2BITS = 3;
    private static final long MASK_1BITS = 1;
    private final byte[] decodeTable;
    private final int encodeSize;
    private final byte[] encodeTable;
    private final byte[] lineSeparator;
    private static final int MASK_5BITS = 31;
    private static final byte[] DECODE_TABLE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, MASK_5BITS, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};
    private static final byte[] ENCODE_TABLE = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 50, 51, 52, 53, 54, 55};
    private static final byte[] HEX_DECODE_TABLE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, MASK_5BITS, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, MASK_5BITS};
    private static final byte[] HEX_ENCODE_TABLE = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86};

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/binary/Base32$Builder.class */
    public static class Builder extends BaseNCodec.AbstractBuilder<Base32, Builder> {
        public Builder() {
            super(Base32.ENCODE_TABLE);
        }

        @Override // java.util.function.Supplier
        public Base32 get() {
            return new Base32(getLineLength(), getLineSeparator(), getEncodeTable(), getPadding(), getDecodingPolicy());
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public Base32() {
        this(false);
    }

    public Base32(boolean useHex) {
        this(0, null, useHex, (byte) 61);
    }

    public Base32(boolean useHex, byte padding) {
        this(0, null, useHex, padding);
    }

    public Base32(byte pad) {
        this(false, pad);
    }

    public Base32(int lineLength) {
        this(lineLength, CHUNK_SEPARATOR);
    }

    public Base32(int lineLength, byte[] lineSeparator) {
        this(lineLength, lineSeparator, false, (byte) 61);
    }

    public Base32(int lineLength, byte[] lineSeparator, boolean useHex) {
        this(lineLength, lineSeparator, useHex, (byte) 61);
    }

    public Base32(int lineLength, byte[] lineSeparator, boolean useHex, byte padding) {
        this(lineLength, lineSeparator, useHex, padding, DECODING_POLICY_DEFAULT);
    }

    public Base32(int lineLength, byte[] lineSeparator, boolean useHex, byte padding, CodecPolicy decodingPolicy) {
        this(lineLength, lineSeparator, useHex ? HEX_ENCODE_TABLE : ENCODE_TABLE, padding, decodingPolicy);
    }

    private Base32(int lineLength, byte[] lineSeparator, byte[] encodeTable, byte padding, CodecPolicy decodingPolicy) {
        super(5, 8, lineLength, toLength(lineSeparator), padding, decodingPolicy);
        Objects.requireNonNull(encodeTable, "encodeTable");
        this.encodeTable = encodeTable;
        this.decodeTable = encodeTable == HEX_ENCODE_TABLE ? HEX_DECODE_TABLE : DECODE_TABLE;
        if (lineLength > 0) {
            if (lineSeparator == null) {
                throw new IllegalArgumentException("lineLength " + lineLength + " > 0, but lineSeparator is null");
            }
            byte[] lineSeparatorCopy = (byte[]) lineSeparator.clone();
            if (containsAlphabetOrPad(lineSeparatorCopy)) {
                String sep = StringUtils.newStringUtf8(lineSeparatorCopy);
                throw new IllegalArgumentException("lineSeparator must not contain Base32 characters: [" + sep + "]");
            }
            this.encodeSize = 8 + lineSeparatorCopy.length;
            this.lineSeparator = lineSeparatorCopy;
        } else {
            this.encodeSize = 8;
            this.lineSeparator = null;
        }
        if (isInAlphabet(padding) || Character.isWhitespace(padding)) {
            throw new IllegalArgumentException("pad must not be in alphabet or whitespace");
        }
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
            int i2 = inPos;
            inPos++;
            byte b2 = input[i2];
            if (b2 == this.pad) {
                context.eof = true;
                break;
            }
            byte[] buffer = ensureBufferSize(decodeSize, context);
            if (b2 >= 0 && b2 < this.decodeTable.length && (b = this.decodeTable[b2]) >= 0) {
                context.modulus = (context.modulus + 1) % 8;
                context.lbitWorkArea = (context.lbitWorkArea << 5) + ((long) b);
                if (context.modulus == 0) {
                    int i3 = context.pos;
                    context.pos = i3 + 1;
                    buffer[i3] = (byte) ((context.lbitWorkArea >> 32) & 255);
                    int i4 = context.pos;
                    context.pos = i4 + 1;
                    buffer[i4] = (byte) ((context.lbitWorkArea >> 24) & 255);
                    int i5 = context.pos;
                    context.pos = i5 + 1;
                    buffer[i5] = (byte) ((context.lbitWorkArea >> 16) & 255);
                    int i6 = context.pos;
                    context.pos = i6 + 1;
                    buffer[i6] = (byte) ((context.lbitWorkArea >> 8) & 255);
                    int i7 = context.pos;
                    context.pos = i7 + 1;
                    buffer[i7] = (byte) (context.lbitWorkArea & 255);
                }
            }
        }
        if (context.eof && context.modulus > 0) {
            byte[] buffer2 = ensureBufferSize(decodeSize, context);
            switch (context.modulus) {
                case 1:
                    validateTrailingCharacters();
                    break;
                case 2:
                    break;
                case 3:
                    validateTrailingCharacters();
                    int i8 = context.pos;
                    context.pos = i8 + 1;
                    buffer2[i8] = (byte) ((context.lbitWorkArea >> MASK_3BITS) & 255);
                    return;
                case 4:
                    validateCharacter(MASK_4BITS, context);
                    context.lbitWorkArea >>= 4;
                    int i9 = context.pos;
                    context.pos = i9 + 1;
                    buffer2[i9] = (byte) ((context.lbitWorkArea >> 8) & 255);
                    int i10 = context.pos;
                    context.pos = i10 + 1;
                    buffer2[i10] = (byte) (context.lbitWorkArea & 255);
                    return;
                case 5:
                    validateCharacter(MASK_1BITS, context);
                    context.lbitWorkArea >>= MASK_1BITS;
                    int i11 = context.pos;
                    context.pos = i11 + 1;
                    buffer2[i11] = (byte) ((context.lbitWorkArea >> 16) & 255);
                    int i12 = context.pos;
                    context.pos = i12 + 1;
                    buffer2[i12] = (byte) ((context.lbitWorkArea >> 8) & 255);
                    int i13 = context.pos;
                    context.pos = i13 + 1;
                    buffer2[i13] = (byte) (context.lbitWorkArea & 255);
                    return;
                case 6:
                    validateTrailingCharacters();
                    context.lbitWorkArea >>= 6;
                    int i14 = context.pos;
                    context.pos = i14 + 1;
                    buffer2[i14] = (byte) ((context.lbitWorkArea >> 16) & 255);
                    int i15 = context.pos;
                    context.pos = i15 + 1;
                    buffer2[i15] = (byte) ((context.lbitWorkArea >> 8) & 255);
                    int i16 = context.pos;
                    context.pos = i16 + 1;
                    buffer2[i16] = (byte) (context.lbitWorkArea & 255);
                    return;
                case 7:
                    validateCharacter(MASK_3BITS, context);
                    context.lbitWorkArea >>= MASK_2BITS;
                    int i17 = context.pos;
                    context.pos = i17 + 1;
                    buffer2[i17] = (byte) ((context.lbitWorkArea >> 24) & 255);
                    int i18 = context.pos;
                    context.pos = i18 + 1;
                    buffer2[i18] = (byte) ((context.lbitWorkArea >> 16) & 255);
                    int i19 = context.pos;
                    context.pos = i19 + 1;
                    buffer2[i19] = (byte) ((context.lbitWorkArea >> 8) & 255);
                    int i20 = context.pos;
                    context.pos = i20 + 1;
                    buffer2[i20] = (byte) (context.lbitWorkArea & 255);
                    return;
                default:
                    throw new IllegalStateException("Impossible modulus " + context.modulus);
            }
            validateCharacter(MASK_2BITS, context);
            int i21 = context.pos;
            context.pos = i21 + 1;
            buffer2[i21] = (byte) ((context.lbitWorkArea >> 2) & 255);
        }
    }

    @Override // org.apache.commons.codec.binary.BaseNCodec
    void encode(byte[] input, int inPos, int inAvail, BaseNCodec.Context context) {
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
                    buffer[i] = this.encodeTable[((int) (context.lbitWorkArea >> MASK_2BITS)) & MASK_5BITS];
                    int i2 = context.pos;
                    context.pos = i2 + 1;
                    buffer[i2] = this.encodeTable[((int) (context.lbitWorkArea << 2)) & MASK_5BITS];
                    int i3 = context.pos;
                    context.pos = i3 + 1;
                    buffer[i3] = this.pad;
                    int i4 = context.pos;
                    context.pos = i4 + 1;
                    buffer[i4] = this.pad;
                    int i5 = context.pos;
                    context.pos = i5 + 1;
                    buffer[i5] = this.pad;
                    int i6 = context.pos;
                    context.pos = i6 + 1;
                    buffer[i6] = this.pad;
                    int i7 = context.pos;
                    context.pos = i7 + 1;
                    buffer[i7] = this.pad;
                    int i8 = context.pos;
                    context.pos = i8 + 1;
                    buffer[i8] = this.pad;
                    break;
                case 2:
                    int i9 = context.pos;
                    context.pos = i9 + 1;
                    buffer[i9] = this.encodeTable[((int) (context.lbitWorkArea >> 11)) & MASK_5BITS];
                    int i10 = context.pos;
                    context.pos = i10 + 1;
                    buffer[i10] = this.encodeTable[((int) (context.lbitWorkArea >> 6)) & MASK_5BITS];
                    int i11 = context.pos;
                    context.pos = i11 + 1;
                    buffer[i11] = this.encodeTable[((int) (context.lbitWorkArea >> MASK_1BITS)) & MASK_5BITS];
                    int i12 = context.pos;
                    context.pos = i12 + 1;
                    buffer[i12] = this.encodeTable[((int) (context.lbitWorkArea << 4)) & MASK_5BITS];
                    int i13 = context.pos;
                    context.pos = i13 + 1;
                    buffer[i13] = this.pad;
                    int i14 = context.pos;
                    context.pos = i14 + 1;
                    buffer[i14] = this.pad;
                    int i15 = context.pos;
                    context.pos = i15 + 1;
                    buffer[i15] = this.pad;
                    int i16 = context.pos;
                    context.pos = i16 + 1;
                    buffer[i16] = this.pad;
                    break;
                case 3:
                    int i17 = context.pos;
                    context.pos = i17 + 1;
                    buffer[i17] = this.encodeTable[((int) (context.lbitWorkArea >> 19)) & MASK_5BITS];
                    int i18 = context.pos;
                    context.pos = i18 + 1;
                    buffer[i18] = this.encodeTable[((int) (context.lbitWorkArea >> 14)) & MASK_5BITS];
                    int i19 = context.pos;
                    context.pos = i19 + 1;
                    buffer[i19] = this.encodeTable[((int) (context.lbitWorkArea >> 9)) & MASK_5BITS];
                    int i20 = context.pos;
                    context.pos = i20 + 1;
                    buffer[i20] = this.encodeTable[((int) (context.lbitWorkArea >> 4)) & MASK_5BITS];
                    int i21 = context.pos;
                    context.pos = i21 + 1;
                    buffer[i21] = this.encodeTable[((int) (context.lbitWorkArea << MASK_1BITS)) & MASK_5BITS];
                    int i22 = context.pos;
                    context.pos = i22 + 1;
                    buffer[i22] = this.pad;
                    int i23 = context.pos;
                    context.pos = i23 + 1;
                    buffer[i23] = this.pad;
                    int i24 = context.pos;
                    context.pos = i24 + 1;
                    buffer[i24] = this.pad;
                    break;
                case 4:
                    int i25 = context.pos;
                    context.pos = i25 + 1;
                    buffer[i25] = this.encodeTable[((int) (context.lbitWorkArea >> 27)) & MASK_5BITS];
                    int i26 = context.pos;
                    context.pos = i26 + 1;
                    buffer[i26] = this.encodeTable[((int) (context.lbitWorkArea >> 22)) & MASK_5BITS];
                    int i27 = context.pos;
                    context.pos = i27 + 1;
                    buffer[i27] = this.encodeTable[((int) (context.lbitWorkArea >> 17)) & MASK_5BITS];
                    int i28 = context.pos;
                    context.pos = i28 + 1;
                    buffer[i28] = this.encodeTable[((int) (context.lbitWorkArea >> 12)) & MASK_5BITS];
                    int i29 = context.pos;
                    context.pos = i29 + 1;
                    buffer[i29] = this.encodeTable[((int) (context.lbitWorkArea >> MASK_3BITS)) & MASK_5BITS];
                    int i30 = context.pos;
                    context.pos = i30 + 1;
                    buffer[i30] = this.encodeTable[((int) (context.lbitWorkArea >> 2)) & MASK_5BITS];
                    int i31 = context.pos;
                    context.pos = i31 + 1;
                    buffer[i31] = this.encodeTable[((int) (context.lbitWorkArea << MASK_2BITS)) & MASK_5BITS];
                    int i32 = context.pos;
                    context.pos = i32 + 1;
                    buffer[i32] = this.pad;
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
        for (int i33 = 0; i33 < inAvail; i33++) {
            byte[] buffer2 = ensureBufferSize(this.encodeSize, context);
            context.modulus = (context.modulus + 1) % 5;
            int i34 = inPos;
            inPos++;
            int b = input[i34];
            if (b < 0) {
                b += 256;
            }
            context.lbitWorkArea = (context.lbitWorkArea << 8) + ((long) b);
            if (0 == context.modulus) {
                int i35 = context.pos;
                context.pos = i35 + 1;
                buffer2[i35] = this.encodeTable[((int) (context.lbitWorkArea >> 35)) & MASK_5BITS];
                int i36 = context.pos;
                context.pos = i36 + 1;
                buffer2[i36] = this.encodeTable[((int) (context.lbitWorkArea >> 30)) & MASK_5BITS];
                int i37 = context.pos;
                context.pos = i37 + 1;
                buffer2[i37] = this.encodeTable[((int) (context.lbitWorkArea >> 25)) & MASK_5BITS];
                int i38 = context.pos;
                context.pos = i38 + 1;
                buffer2[i38] = this.encodeTable[((int) (context.lbitWorkArea >> 20)) & MASK_5BITS];
                int i39 = context.pos;
                context.pos = i39 + 1;
                buffer2[i39] = this.encodeTable[((int) (context.lbitWorkArea >> MASK_4BITS)) & MASK_5BITS];
                int i40 = context.pos;
                context.pos = i40 + 1;
                buffer2[i40] = this.encodeTable[((int) (context.lbitWorkArea >> 10)) & MASK_5BITS];
                int i41 = context.pos;
                context.pos = i41 + 1;
                buffer2[i41] = this.encodeTable[((int) (context.lbitWorkArea >> 5)) & MASK_5BITS];
                int i42 = context.pos;
                context.pos = i42 + 1;
                buffer2[i42] = this.encodeTable[((int) context.lbitWorkArea) & MASK_5BITS];
                context.currentLinePos += 8;
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
    public boolean isInAlphabet(byte octet) {
        return octet >= 0 && octet < this.decodeTable.length && this.decodeTable[octet] != -1;
    }

    private void validateCharacter(long emptyBitsMask, BaseNCodec.Context context) {
        if (isStrictDecoding() && (context.lbitWorkArea & emptyBitsMask) != 0) {
            throw new IllegalArgumentException("Strict decoding: Last encoded character (before the paddings if any) is a valid base 32 alphabet but not a possible encoding. Expected the discarded bits from the character to be zero.");
        }
    }

    private void validateTrailingCharacters() {
        if (isStrictDecoding()) {
            throw new IllegalArgumentException("Strict decoding: Last encoded character(s) (before the paddings if any) are valid base 32 alphabet but not a possible encoding. Decoding requires either 2, 4, 5, or 7 trailing 5-bit characters to create bytes.");
        }
    }
}
