package com.ctc.wstx.util;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/util/TextBuilder.class */
public final class TextBuilder {
    private static final int MIN_LEN = 60;
    private static final int MAX_LEN = 120;
    private char[] mBuffer;
    private int mBufferLen;
    private String mResultString;

    public TextBuilder(int initialSize) {
        int charSize = initialSize << 4;
        if (charSize < 60) {
            charSize = 60;
        } else if (charSize > 120) {
            charSize = 120;
        }
        this.mBuffer = new char[charSize];
    }

    public void reset() {
        this.mBufferLen = 0;
        this.mResultString = null;
    }

    public boolean isEmpty() {
        return this.mBufferLen == 0;
    }

    public String getAllValues() {
        if (this.mResultString == null) {
            this.mResultString = new String(this.mBuffer, 0, this.mBufferLen);
        }
        return this.mResultString;
    }

    public char[] getCharBuffer() {
        return this.mBuffer;
    }

    public int getCharSize() {
        return this.mBufferLen;
    }

    public void append(char c) {
        if (this.mBuffer.length == this.mBufferLen) {
            resize(1);
        }
        char[] cArr = this.mBuffer;
        int i = this.mBufferLen;
        this.mBufferLen = i + 1;
        cArr[i] = c;
    }

    public void append(char[] src, int start, int len) {
        if (len > this.mBuffer.length - this.mBufferLen) {
            resize(len);
        }
        System.arraycopy(src, start, this.mBuffer, this.mBufferLen, len);
        this.mBufferLen += len;
    }

    public void setBufferSize(int newSize) {
        this.mBufferLen = newSize;
    }

    public char[] bufferFull(int needSpaceFor) {
        this.mBufferLen = this.mBuffer.length;
        resize(needSpaceFor);
        return this.mBuffer;
    }

    public String toString() {
        return new String(this.mBuffer, 0, this.mBufferLen);
    }

    private void resize(int needSpaceFor) {
        char[] old = this.mBuffer;
        int oldLen = old.length;
        int addition = oldLen >> 1;
        int needSpaceFor2 = needSpaceFor - (oldLen - this.mBufferLen);
        if (addition < needSpaceFor2) {
            addition = needSpaceFor2;
        }
        this.mBuffer = new char[oldLen + addition];
        System.arraycopy(old, 0, this.mBuffer, 0, this.mBufferLen);
    }
}
