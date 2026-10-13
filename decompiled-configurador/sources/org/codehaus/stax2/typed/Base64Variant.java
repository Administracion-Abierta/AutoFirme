package org.codehaus.stax2.typed;

import java.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/typed/Base64Variant.class */
public final class Base64Variant {
    static final char PADDING_CHAR_NONE = 0;
    public static final int BASE64_VALUE_INVALID = -1;
    public static final int BASE64_VALUE_PADDING = -2;
    private final int[] _asciiToBase64;
    private final char[] _base64ToAsciiC;
    private final byte[] _base64ToAsciiB;
    final String _name;
    final boolean _usesPadding;
    final char _paddingChar;
    final int _maxLineLength;

    public Base64Variant(String name, String base64Alphabet, boolean usesPadding, char paddingChar, int maxLineLength) {
        this._asciiToBase64 = new int[128];
        this._base64ToAsciiC = new char[64];
        this._base64ToAsciiB = new byte[64];
        this._name = name;
        this._usesPadding = usesPadding;
        this._paddingChar = paddingChar;
        this._maxLineLength = maxLineLength;
        int alphaLen = base64Alphabet.length();
        if (alphaLen != 64) {
            throw new IllegalArgumentException("Base64Alphabet length must be exactly 64 (was " + alphaLen + ")");
        }
        base64Alphabet.getChars(0, alphaLen, this._base64ToAsciiC, 0);
        Arrays.fill(this._asciiToBase64, -1);
        for (int i = 0; i < alphaLen; i++) {
            char alpha = this._base64ToAsciiC[i];
            this._base64ToAsciiB[i] = (byte) alpha;
            this._asciiToBase64[alpha] = i;
        }
        if (usesPadding) {
            this._asciiToBase64[paddingChar] = -2;
        }
    }

    public Base64Variant(Base64Variant base, String name, int maxLineLength) {
        this(base, name, base._usesPadding, base._paddingChar, maxLineLength);
    }

    public Base64Variant(Base64Variant base, String name, boolean usesPadding, char paddingChar, int maxLineLength) {
        this._asciiToBase64 = new int[128];
        this._base64ToAsciiC = new char[64];
        this._base64ToAsciiB = new byte[64];
        this._name = name;
        byte[] srcB = base._base64ToAsciiB;
        System.arraycopy(srcB, 0, this._base64ToAsciiB, 0, srcB.length);
        char[] srcC = base._base64ToAsciiC;
        System.arraycopy(srcC, 0, this._base64ToAsciiC, 0, srcC.length);
        int[] srcV = base._asciiToBase64;
        System.arraycopy(srcV, 0, this._asciiToBase64, 0, srcV.length);
        this._usesPadding = usesPadding;
        this._paddingChar = paddingChar;
        this._maxLineLength = maxLineLength;
    }

    public String getName() {
        return this._name;
    }

    public boolean usesPadding() {
        return this._usesPadding;
    }

    public boolean usesPaddingChar(char c) {
        return c == this._paddingChar;
    }

    public char getPaddingChar() {
        return this._paddingChar;
    }

    public byte getPaddingByte() {
        return (byte) this._paddingChar;
    }

    public int getMaxLineLength() {
        return this._maxLineLength;
    }

    public int decodeBase64Char(char c) {
        if (c <= 127) {
            return this._asciiToBase64[c];
        }
        return -1;
    }

    public int decodeBase64Byte(byte b) {
        if (b <= 127) {
            return this._asciiToBase64[b];
        }
        return -1;
    }

    public char encodeBase64BitsAsChar(int value) {
        return this._base64ToAsciiC[value];
    }

    public int encodeBase64Chunk(int b24, char[] buffer, int ptr) {
        int ptr2 = ptr + 1;
        buffer[ptr] = this._base64ToAsciiC[(b24 >> 18) & 63];
        int ptr3 = ptr2 + 1;
        buffer[ptr2] = this._base64ToAsciiC[(b24 >> 12) & 63];
        int ptr4 = ptr3 + 1;
        buffer[ptr3] = this._base64ToAsciiC[(b24 >> 6) & 63];
        int ptr5 = ptr4 + 1;
        buffer[ptr4] = this._base64ToAsciiC[b24 & 63];
        return ptr5;
    }

    public int encodeBase64Partial(int bits, int outputBytes, char[] buffer, int outPtr) {
        int outPtr2 = outPtr + 1;
        buffer[outPtr] = this._base64ToAsciiC[(bits >> 18) & 63];
        int outPtr3 = outPtr2 + 1;
        buffer[outPtr2] = this._base64ToAsciiC[(bits >> 12) & 63];
        if (this._usesPadding) {
            int outPtr4 = outPtr3 + 1;
            buffer[outPtr3] = outputBytes == 2 ? this._base64ToAsciiC[(bits >> 6) & 63] : this._paddingChar;
            outPtr3 = outPtr4 + 1;
            buffer[outPtr4] = this._paddingChar;
        } else if (outputBytes == 2) {
            outPtr3++;
            buffer[outPtr3] = this._base64ToAsciiC[(bits >> 6) & 63];
        }
        return outPtr3;
    }

    public byte encodeBase64BitsAsByte(int value) {
        return this._base64ToAsciiB[value];
    }

    public int encodeBase64Chunk(int b24, byte[] buffer, int ptr) {
        int ptr2 = ptr + 1;
        buffer[ptr] = this._base64ToAsciiB[(b24 >> 18) & 63];
        int ptr3 = ptr2 + 1;
        buffer[ptr2] = this._base64ToAsciiB[(b24 >> 12) & 63];
        int ptr4 = ptr3 + 1;
        buffer[ptr3] = this._base64ToAsciiB[(b24 >> 6) & 63];
        int ptr5 = ptr4 + 1;
        buffer[ptr4] = this._base64ToAsciiB[b24 & 63];
        return ptr5;
    }

    public int encodeBase64Partial(int bits, int outputBytes, byte[] buffer, int outPtr) {
        int outPtr2 = outPtr + 1;
        buffer[outPtr] = this._base64ToAsciiB[(bits >> 18) & 63];
        int outPtr3 = outPtr2 + 1;
        buffer[outPtr2] = this._base64ToAsciiB[(bits >> 12) & 63];
        if (this._usesPadding) {
            byte pb = (byte) this._paddingChar;
            int outPtr4 = outPtr3 + 1;
            buffer[outPtr3] = outputBytes == 2 ? this._base64ToAsciiB[(bits >> 6) & 63] : pb;
            outPtr3 = outPtr4 + 1;
            buffer[outPtr4] = pb;
        } else if (outputBytes == 2) {
            outPtr3++;
            buffer[outPtr3] = this._base64ToAsciiB[(bits >> 6) & 63];
        }
        return outPtr3;
    }

    public String toString() {
        return this._name;
    }
}
