package org.codehaus.stax2.ri.typed;

import org.codehaus.stax2.typed.Base64Variant;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/typed/ValueEncoderFactory.class */
public final class ValueEncoderFactory {
    static final byte BYTE_SPACE = 32;
    protected TokenEncoder _tokenEncoder = null;
    protected IntEncoder _intEncoder = null;
    protected LongEncoder _longEncoder = null;
    protected FloatEncoder _floatEncoder = null;
    protected DoubleEncoder _doubleEncoder = null;

    public ScalarEncoder getScalarEncoder(String value) {
        if (value.length() > 64) {
            if (this._tokenEncoder == null) {
                this._tokenEncoder = new TokenEncoder();
            }
            this._tokenEncoder.reset(value);
            return this._tokenEncoder;
        }
        return new StringEncoder(value);
    }

    public ScalarEncoder getEncoder(boolean value) {
        return getScalarEncoder(value ? "true" : "false");
    }

    public IntEncoder getEncoder(int value) {
        if (this._intEncoder == null) {
            this._intEncoder = new IntEncoder();
        }
        this._intEncoder.reset(value);
        return this._intEncoder;
    }

    public LongEncoder getEncoder(long value) {
        if (this._longEncoder == null) {
            this._longEncoder = new LongEncoder();
        }
        this._longEncoder.reset(value);
        return this._longEncoder;
    }

    public FloatEncoder getEncoder(float value) {
        if (this._floatEncoder == null) {
            this._floatEncoder = new FloatEncoder();
        }
        this._floatEncoder.reset(value);
        return this._floatEncoder;
    }

    public DoubleEncoder getEncoder(double value) {
        if (this._doubleEncoder == null) {
            this._doubleEncoder = new DoubleEncoder();
        }
        this._doubleEncoder.reset(value);
        return this._doubleEncoder;
    }

    public IntArrayEncoder getEncoder(int[] values, int from, int length) {
        return new IntArrayEncoder(values, from, from + length);
    }

    public LongArrayEncoder getEncoder(long[] values, int from, int length) {
        return new LongArrayEncoder(values, from, from + length);
    }

    public FloatArrayEncoder getEncoder(float[] values, int from, int length) {
        return new FloatArrayEncoder(values, from, from + length);
    }

    public DoubleArrayEncoder getEncoder(double[] values, int from, int length) {
        return new DoubleArrayEncoder(values, from, from + length);
    }

    public Base64Encoder getEncoder(Base64Variant v, byte[] data, int from, int length) {
        return new Base64Encoder(v, data, from, from + length);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/typed/ValueEncoderFactory$ScalarEncoder.class */
    public static abstract class ScalarEncoder extends AsciiValueEncoder {
        protected ScalarEncoder() {
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/typed/ValueEncoderFactory$TokenEncoder.class */
    static final class TokenEncoder extends ScalarEncoder {
        String _value;

        protected TokenEncoder() {
        }

        protected void reset(String value) {
            this._value = value;
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public boolean isCompleted() {
            return this._value == null;
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(char[] buffer, int ptr, int end) {
            String str = this._value;
            this._value = null;
            int len = str.length();
            str.getChars(0, len, buffer, ptr);
            return ptr + len;
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(byte[] buffer, int ptr, int end) {
            String str = this._value;
            this._value = null;
            int len = str.length();
            for (int i = 0; i < len; i++) {
                int i2 = ptr;
                ptr++;
                buffer[i2] = (byte) str.charAt(i);
            }
            return ptr;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/typed/ValueEncoderFactory$StringEncoder.class */
    static final class StringEncoder extends ScalarEncoder {
        String _value;
        int _offset;

        protected StringEncoder(String value) {
            this._value = value;
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public boolean isCompleted() {
            return this._value == null;
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(char[] buffer, int ptr, int end) {
            int left = this._value.length() - this._offset;
            int free = end - ptr;
            if (free >= left) {
                this._value.getChars(this._offset, left, buffer, ptr);
                this._value = null;
                return ptr + left;
            }
            this._value.getChars(this._offset, free, buffer, ptr);
            this._offset += free;
            return end;
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(byte[] buffer, int ptr, int end) {
            int left = this._value.length() - this._offset;
            int free = end - ptr;
            if (free >= left) {
                String str = this._value;
                this._value = null;
                int last = str.length();
                for (int offset = this._offset; offset < last; offset++) {
                    int i = ptr;
                    ptr++;
                    buffer[i] = (byte) str.charAt(offset);
                }
                return ptr;
            }
            while (ptr < end) {
                String str2 = this._value;
                int i2 = this._offset;
                this._offset = i2 + 1;
                buffer[ptr] = (byte) str2.charAt(i2);
                ptr++;
            }
            return ptr;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/typed/ValueEncoderFactory$TypedScalarEncoder.class */
    static abstract class TypedScalarEncoder extends ScalarEncoder {
        protected TypedScalarEncoder() {
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public final boolean isCompleted() {
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/typed/ValueEncoderFactory$IntEncoder.class */
    public static final class IntEncoder extends TypedScalarEncoder {
        int _value;

        protected IntEncoder() {
        }

        protected void reset(int value) {
            this._value = value;
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(char[] buffer, int ptr, int end) {
            return NumberUtil.writeInt(this._value, buffer, ptr);
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(byte[] buffer, int ptr, int end) {
            return NumberUtil.writeInt(this._value, buffer, ptr);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/typed/ValueEncoderFactory$LongEncoder.class */
    public static final class LongEncoder extends TypedScalarEncoder {
        long _value;

        protected LongEncoder() {
        }

        protected void reset(long value) {
            this._value = value;
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(char[] buffer, int ptr, int end) {
            return NumberUtil.writeLong(this._value, buffer, ptr);
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(byte[] buffer, int ptr, int end) {
            return NumberUtil.writeLong(this._value, buffer, ptr);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/typed/ValueEncoderFactory$FloatEncoder.class */
    public static final class FloatEncoder extends TypedScalarEncoder {
        float _value;

        protected FloatEncoder() {
        }

        protected void reset(float value) {
            this._value = value;
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(char[] buffer, int ptr, int end) {
            return NumberUtil.writeFloat(this._value, buffer, ptr);
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(byte[] buffer, int ptr, int end) {
            return NumberUtil.writeFloat(this._value, buffer, ptr);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/typed/ValueEncoderFactory$DoubleEncoder.class */
    public static final class DoubleEncoder extends TypedScalarEncoder {
        double _value;

        protected DoubleEncoder() {
        }

        protected void reset(double value) {
            this._value = value;
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(char[] buffer, int ptr, int end) {
            return NumberUtil.writeDouble(this._value, buffer, ptr);
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(byte[] buffer, int ptr, int end) {
            return NumberUtil.writeDouble(this._value, buffer, ptr);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/typed/ValueEncoderFactory$ArrayEncoder.class */
    static abstract class ArrayEncoder extends AsciiValueEncoder {
        int _ptr;
        final int _end;

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public abstract int encodeMore(char[] cArr, int i, int i2);

        protected ArrayEncoder(int ptr, int end) {
            this._ptr = ptr;
            this._end = end;
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public final boolean isCompleted() {
            return this._ptr >= this._end;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/typed/ValueEncoderFactory$IntArrayEncoder.class */
    public static final class IntArrayEncoder extends ArrayEncoder {
        final int[] _values;

        protected IntArrayEncoder(int[] values, int from, int length) {
            super(from, length);
            this._values = values;
        }

        @Override // org.codehaus.stax2.ri.typed.ValueEncoderFactory.ArrayEncoder, org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(char[] buffer, int ptr, int end) {
            int lastOk = end - 12;
            while (ptr <= lastOk && this._ptr < this._end) {
                buffer[ptr] = ' ';
                int[] iArr = this._values;
                int i = this._ptr;
                this._ptr = i + 1;
                ptr = NumberUtil.writeInt(iArr[i], buffer, ptr + 1);
            }
            return ptr;
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(byte[] buffer, int ptr, int end) {
            int lastOk = end - 12;
            while (ptr <= lastOk && this._ptr < this._end) {
                buffer[ptr] = 32;
                int[] iArr = this._values;
                int i = this._ptr;
                this._ptr = i + 1;
                ptr = NumberUtil.writeInt(iArr[i], buffer, ptr + 1);
            }
            return ptr;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/typed/ValueEncoderFactory$LongArrayEncoder.class */
    public static final class LongArrayEncoder extends ArrayEncoder {
        final long[] _values;

        protected LongArrayEncoder(long[] values, int from, int length) {
            super(from, length);
            this._values = values;
        }

        @Override // org.codehaus.stax2.ri.typed.ValueEncoderFactory.ArrayEncoder, org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(char[] buffer, int ptr, int end) {
            int lastOk = end - 22;
            while (ptr <= lastOk && this._ptr < this._end) {
                buffer[ptr] = ' ';
                long[] jArr = this._values;
                int i = this._ptr;
                this._ptr = i + 1;
                ptr = NumberUtil.writeLong(jArr[i], buffer, ptr + 1);
            }
            return ptr;
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(byte[] buffer, int ptr, int end) {
            int lastOk = end - 22;
            while (ptr <= lastOk && this._ptr < this._end) {
                buffer[ptr] = 32;
                long[] jArr = this._values;
                int i = this._ptr;
                this._ptr = i + 1;
                ptr = NumberUtil.writeLong(jArr[i], buffer, ptr + 1);
            }
            return ptr;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/typed/ValueEncoderFactory$FloatArrayEncoder.class */
    public static final class FloatArrayEncoder extends ArrayEncoder {
        final float[] _values;

        protected FloatArrayEncoder(float[] values, int from, int length) {
            super(from, length);
            this._values = values;
        }

        @Override // org.codehaus.stax2.ri.typed.ValueEncoderFactory.ArrayEncoder, org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(char[] buffer, int ptr, int end) {
            int lastOk = end - 33;
            while (ptr <= lastOk && this._ptr < this._end) {
                buffer[ptr] = ' ';
                float[] fArr = this._values;
                int i = this._ptr;
                this._ptr = i + 1;
                ptr = NumberUtil.writeFloat(fArr[i], buffer, ptr + 1);
            }
            return ptr;
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(byte[] buffer, int ptr, int end) {
            int lastOk = end - 33;
            while (ptr <= lastOk && this._ptr < this._end) {
                buffer[ptr] = 32;
                float[] fArr = this._values;
                int i = this._ptr;
                this._ptr = i + 1;
                ptr = NumberUtil.writeFloat(fArr[i], buffer, ptr + 1);
            }
            return ptr;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/typed/ValueEncoderFactory$DoubleArrayEncoder.class */
    public static final class DoubleArrayEncoder extends ArrayEncoder {
        final double[] _values;

        protected DoubleArrayEncoder(double[] values, int from, int length) {
            super(from, length);
            this._values = values;
        }

        @Override // org.codehaus.stax2.ri.typed.ValueEncoderFactory.ArrayEncoder, org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(char[] buffer, int ptr, int end) {
            int lastOk = end - 33;
            while (ptr <= lastOk && this._ptr < this._end) {
                buffer[ptr] = ' ';
                double[] dArr = this._values;
                int i = this._ptr;
                this._ptr = i + 1;
                ptr = NumberUtil.writeDouble(dArr[i], buffer, ptr + 1);
            }
            return ptr;
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(byte[] buffer, int ptr, int end) {
            int lastOk = end - 33;
            while (ptr <= lastOk && this._ptr < this._end) {
                buffer[ptr] = 32;
                double[] dArr = this._values;
                int i = this._ptr;
                this._ptr = i + 1;
                ptr = NumberUtil.writeDouble(dArr[i], buffer, ptr + 1);
            }
            return ptr;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/typed/ValueEncoderFactory$Base64Encoder.class */
    public static final class Base64Encoder extends AsciiValueEncoder {
        static final char PAD_CHAR = '=';
        static final byte PAD_BYTE = 61;
        static final byte LF_CHAR = 10;
        static final byte LF_BYTE = 10;
        final Base64Variant _variant;
        final byte[] _input;
        int _inputPtr;
        final int _inputEnd;
        int _chunksBeforeLf;

        protected Base64Encoder(Base64Variant v, byte[] values, int from, int end) {
            this._variant = v;
            this._input = values;
            this._inputPtr = from;
            this._inputEnd = end;
            this._chunksBeforeLf = this._variant.getMaxLineLength() >> 2;
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public boolean isCompleted() {
            return this._inputPtr >= this._inputEnd;
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(char[] buffer, int outPtr, int outEnd) {
            int inEnd = this._inputEnd - 3;
            int outEnd2 = outEnd - 5;
            while (this._inputPtr <= inEnd) {
                if (outPtr > outEnd2) {
                    return outPtr;
                }
                byte[] bArr = this._input;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                int b24 = bArr[i] << 8;
                byte[] bArr2 = this._input;
                int i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                int i3 = (b24 | (bArr2[i2] & 255)) << 8;
                byte[] bArr3 = this._input;
                int i4 = this._inputPtr;
                this._inputPtr = i4 + 1;
                outPtr = this._variant.encodeBase64Chunk(i3 | (bArr3[i4] & 255), buffer, outPtr);
                int i5 = this._chunksBeforeLf - 1;
                this._chunksBeforeLf = i5;
                if (i5 <= 0) {
                    outPtr++;
                    buffer[outPtr] = '\n';
                    this._chunksBeforeLf = this._variant.getMaxLineLength() >> 2;
                }
            }
            int inputLeft = this._inputEnd - this._inputPtr;
            if (inputLeft > 0 && outPtr <= outEnd2) {
                byte[] bArr4 = this._input;
                int i6 = this._inputPtr;
                this._inputPtr = i6 + 1;
                int b25 = bArr4[i6] << 16;
                if (inputLeft == 2) {
                    byte[] bArr5 = this._input;
                    int i7 = this._inputPtr;
                    this._inputPtr = i7 + 1;
                    b25 |= (bArr5[i7] & 255) << 8;
                }
                outPtr = this._variant.encodeBase64Partial(b25, inputLeft, buffer, outPtr);
            }
            return outPtr;
        }

        @Override // org.codehaus.stax2.ri.typed.AsciiValueEncoder
        public int encodeMore(byte[] buffer, int outPtr, int outEnd) {
            int inEnd = this._inputEnd - 3;
            int outEnd2 = outEnd - 5;
            while (this._inputPtr <= inEnd) {
                if (outPtr > outEnd2) {
                    return outPtr;
                }
                byte[] bArr = this._input;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                int b24 = bArr[i] << 8;
                byte[] bArr2 = this._input;
                int i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                int i3 = (b24 | (bArr2[i2] & 255)) << 8;
                byte[] bArr3 = this._input;
                int i4 = this._inputPtr;
                this._inputPtr = i4 + 1;
                outPtr = this._variant.encodeBase64Chunk(i3 | (bArr3[i4] & 255), buffer, outPtr);
                int i5 = this._chunksBeforeLf - 1;
                this._chunksBeforeLf = i5;
                if (i5 <= 0) {
                    outPtr++;
                    buffer[outPtr] = 10;
                    this._chunksBeforeLf = this._variant.getMaxLineLength() >> 2;
                }
            }
            int inputLeft = this._inputEnd - this._inputPtr;
            if (inputLeft > 0 && outPtr <= outEnd2) {
                byte[] bArr4 = this._input;
                int i6 = this._inputPtr;
                this._inputPtr = i6 + 1;
                int b25 = bArr4[i6] << 16;
                if (inputLeft == 2) {
                    byte[] bArr5 = this._input;
                    int i7 = this._inputPtr;
                    this._inputPtr = i7 + 1;
                    b25 |= (bArr5[i7] & 255) << 8;
                }
                outPtr = this._variant.encodeBase64Partial(b25, inputLeft, buffer, outPtr);
            }
            return outPtr;
        }
    }
}
