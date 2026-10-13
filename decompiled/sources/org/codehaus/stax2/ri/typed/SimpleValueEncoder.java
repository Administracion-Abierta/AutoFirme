package org.codehaus.stax2.ri.typed;

import org.codehaus.stax2.typed.Base64Variant;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/typed/SimpleValueEncoder.class */
public class SimpleValueEncoder {
    protected final char[] mBuffer = new char[500];
    protected final ValueEncoderFactory mEncoderFactory = new ValueEncoderFactory();

    public String encodeAsString(int[] value, int from, int length) {
        return encode(this.mEncoderFactory.getEncoder(value, from, length));
    }

    public String encodeAsString(long[] value, int from, int length) {
        return encode(this.mEncoderFactory.getEncoder(value, from, length));
    }

    public String encodeAsString(float[] value, int from, int length) {
        return encode(this.mEncoderFactory.getEncoder(value, from, length));
    }

    public String encodeAsString(double[] value, int from, int length) {
        return encode(this.mEncoderFactory.getEncoder(value, from, length));
    }

    public String encodeAsString(Base64Variant v, byte[] value, int from, int length) {
        return encode(this.mEncoderFactory.getEncoder(v, value, from, length));
    }

    protected String encode(AsciiValueEncoder enc) {
        int last = enc.encodeMore(this.mBuffer, 0, this.mBuffer.length);
        if (enc.isCompleted()) {
            return new String(this.mBuffer, 0, last);
        }
        StringBuffer sb = new StringBuffer(this.mBuffer.length << 1);
        sb.append(this.mBuffer, 0, last);
        do {
            sb.append(this.mBuffer, 0, enc.encodeMore(this.mBuffer, 0, this.mBuffer.length));
        } while (!enc.isCompleted());
        return sb.toString();
    }
}
