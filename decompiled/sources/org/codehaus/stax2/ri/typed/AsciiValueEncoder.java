package org.codehaus.stax2.ri.typed;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/typed/AsciiValueEncoder.class */
public abstract class AsciiValueEncoder {
    protected static final int MIN_CHARS_WITHOUT_FLUSH = 64;

    public abstract boolean isCompleted();

    public abstract int encodeMore(char[] cArr, int i, int i2);

    public abstract int encodeMore(byte[] bArr, int i, int i2);

    protected AsciiValueEncoder() {
    }

    public final boolean bufferNeedsFlush(int freeChars) {
        return freeChars < 64;
    }
}
