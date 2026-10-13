package org.codehaus.stax2.ri.typed;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.TypedValueDecoder;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/typed/ValueDecoderFactory.class */
public final class ValueDecoderFactory {
    protected BooleanDecoder mBooleanDecoder = null;
    protected IntDecoder mIntDecoder = null;
    protected LongDecoder mLongDecoder = null;
    protected FloatDecoder mFloatDecoder = null;
    protected DoubleDecoder mDoubleDecoder = null;

    public BooleanDecoder getBooleanDecoder() {
        if (this.mBooleanDecoder == null) {
            this.mBooleanDecoder = new BooleanDecoder();
        }
        return this.mBooleanDecoder;
    }

    public IntDecoder getIntDecoder() {
        if (this.mIntDecoder == null) {
            this.mIntDecoder = new IntDecoder();
        }
        return this.mIntDecoder;
    }

    public LongDecoder getLongDecoder() {
        if (this.mLongDecoder == null) {
            this.mLongDecoder = new LongDecoder();
        }
        return this.mLongDecoder;
    }

    public FloatDecoder getFloatDecoder() {
        if (this.mFloatDecoder == null) {
            this.mFloatDecoder = new FloatDecoder();
        }
        return this.mFloatDecoder;
    }

    public DoubleDecoder getDoubleDecoder() {
        if (this.mDoubleDecoder == null) {
            this.mDoubleDecoder = new DoubleDecoder();
        }
        return this.mDoubleDecoder;
    }

    public IntegerDecoder getIntegerDecoder() {
        return new IntegerDecoder();
    }

    public DecimalDecoder getDecimalDecoder() {
        return new DecimalDecoder();
    }

    public QNameDecoder getQNameDecoder(NamespaceContext nsc) {
        return new QNameDecoder(nsc);
    }

    public IntArrayDecoder getIntArrayDecoder(int[] result, int offset, int len) {
        return new IntArrayDecoder(result, offset, len, getIntDecoder());
    }

    public IntArrayDecoder getIntArrayDecoder() {
        return new IntArrayDecoder(getIntDecoder());
    }

    public LongArrayDecoder getLongArrayDecoder(long[] result, int offset, int len) {
        return new LongArrayDecoder(result, offset, len, getLongDecoder());
    }

    public LongArrayDecoder getLongArrayDecoder() {
        return new LongArrayDecoder(getLongDecoder());
    }

    public FloatArrayDecoder getFloatArrayDecoder(float[] result, int offset, int len) {
        return new FloatArrayDecoder(result, offset, len, getFloatDecoder());
    }

    public FloatArrayDecoder getFloatArrayDecoder() {
        return new FloatArrayDecoder(getFloatDecoder());
    }

    public DoubleArrayDecoder getDoubleArrayDecoder(double[] result, int offset, int len) {
        return new DoubleArrayDecoder(result, offset, len, getDoubleDecoder());
    }

    public DoubleArrayDecoder getDoubleArrayDecoder() {
        return new DoubleArrayDecoder(getDoubleDecoder());
    }

    /* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/typed/ValueDecoderFactory$DecoderBase.class */
    public static abstract class DecoderBase extends TypedValueDecoder {
        static final long L_BILLION = 1000000000;
        static final long L_MAX_INT = 2147483647L;
        static final long L_MIN_INT = -2147483648L;
        static final BigInteger BD_MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
        static final BigInteger BD_MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
        protected int mNextPtr;

        public abstract String getType();

        protected DecoderBase() {
        }

        @Override // org.codehaus.stax2.typed.TypedValueDecoder
        public void handleEmptyValue() {
            throw new IllegalArgumentException("Empty value (all white space) not a valid lexical representation of " + getType());
        }

        protected void verifyDigits(String lexical, int start, int end) {
            while (start < end) {
                char ch = lexical.charAt(start);
                if (ch <= '9' && ch >= '0') {
                    start++;
                } else {
                    throw constructInvalidValue(lexical);
                }
            }
        }

        protected void verifyDigits(char[] lexical, int start, int end, int ptr) {
            while (ptr < end) {
                char ch = lexical[ptr];
                if (ch <= '9' && ch >= '0') {
                    ptr++;
                } else {
                    throw constructInvalidValue(lexical, start, end);
                }
            }
        }

        protected int skipSignAndZeroes(String lexical, char ch, boolean hasSign, int end) {
            int ptr;
            int v2;
            if (hasSign) {
                if (1 < end) {
                    ptr = 1 + 1;
                    ch = lexical.charAt(1);
                } else {
                    throw constructInvalidValue(lexical);
                }
            } else {
                ptr = 1;
            }
            int value = ch - '0';
            if (value < 0 || value > 9) {
                throw constructInvalidValue(lexical);
            }
            while (value == 0 && ptr < end && (v2 = lexical.charAt(ptr) - '0') >= 0 && v2 <= 9) {
                ptr++;
                value = v2;
            }
            this.mNextPtr = ptr;
            return value;
        }

        protected int skipSignAndZeroes(char[] lexical, char ch, boolean hasSign, int start, int end) {
            int v2;
            int ptr = start + 1;
            if (hasSign) {
                if (ptr >= end) {
                    throw constructInvalidValue(lexical, start, end);
                }
                ptr++;
                ch = lexical[ptr];
            }
            int value = ch - '0';
            if (value < 0 || value > 9) {
                throw constructInvalidValue(lexical, start, end);
            }
            while (value == 0 && ptr < end && (v2 = lexical[ptr] - '0') >= 0 && v2 <= 9) {
                ptr++;
                value = v2;
            }
            this.mNextPtr = ptr;
            return value;
        }

        protected static final int parseInt(char[] digitChars, int start, int end) {
            int num = digitChars[start] - '0';
            int start2 = start + 1;
            if (start2 < end) {
                num = (num * 10) + (digitChars[start2] - '0');
                int start3 = start2 + 1;
                if (start3 < end) {
                    num = (num * 10) + (digitChars[start3] - '0');
                    int start4 = start3 + 1;
                    if (start4 < end) {
                        num = (num * 10) + (digitChars[start4] - '0');
                        int start5 = start4 + 1;
                        if (start5 < end) {
                            num = (num * 10) + (digitChars[start5] - '0');
                            int start6 = start5 + 1;
                            if (start6 < end) {
                                num = (num * 10) + (digitChars[start6] - '0');
                                int start7 = start6 + 1;
                                if (start7 < end) {
                                    num = (num * 10) + (digitChars[start7] - '0');
                                    int start8 = start7 + 1;
                                    if (start8 < end) {
                                        num = (num * 10) + (digitChars[start8] - '0');
                                        int start9 = start8 + 1;
                                        if (start9 < end) {
                                            num = (num * 10) + (digitChars[start9] - '0');
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return num;
        }

        protected static final int parseInt(int num, char[] digitChars, int start, int end) {
            int num2 = (num * 10) + (digitChars[start] - '0');
            int start2 = start + 1;
            if (start2 < end) {
                num2 = (num2 * 10) + (digitChars[start2] - '0');
                int start3 = start2 + 1;
                if (start3 < end) {
                    num2 = (num2 * 10) + (digitChars[start3] - '0');
                    int start4 = start3 + 1;
                    if (start4 < end) {
                        num2 = (num2 * 10) + (digitChars[start4] - '0');
                        int start5 = start4 + 1;
                        if (start5 < end) {
                            num2 = (num2 * 10) + (digitChars[start5] - '0');
                            int start6 = start5 + 1;
                            if (start6 < end) {
                                num2 = (num2 * 10) + (digitChars[start6] - '0');
                                int start7 = start6 + 1;
                                if (start7 < end) {
                                    num2 = (num2 * 10) + (digitChars[start7] - '0');
                                    int start8 = start7 + 1;
                                    if (start8 < end) {
                                        num2 = (num2 * 10) + (digitChars[start8] - '0');
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return num2;
        }

        protected static final int parseInt(String digitChars, int start, int end) {
            int num = digitChars.charAt(start) - '0';
            int start2 = start + 1;
            if (start2 < end) {
                num = (num * 10) + (digitChars.charAt(start2) - '0');
                int start3 = start2 + 1;
                if (start3 < end) {
                    num = (num * 10) + (digitChars.charAt(start3) - '0');
                    int start4 = start3 + 1;
                    if (start4 < end) {
                        num = (num * 10) + (digitChars.charAt(start4) - '0');
                        int start5 = start4 + 1;
                        if (start5 < end) {
                            num = (num * 10) + (digitChars.charAt(start5) - '0');
                            int start6 = start5 + 1;
                            if (start6 < end) {
                                num = (num * 10) + (digitChars.charAt(start6) - '0');
                                int start7 = start6 + 1;
                                if (start7 < end) {
                                    num = (num * 10) + (digitChars.charAt(start7) - '0');
                                    int start8 = start7 + 1;
                                    if (start8 < end) {
                                        num = (num * 10) + (digitChars.charAt(start8) - '0');
                                        int start9 = start8 + 1;
                                        if (start9 < end) {
                                            num = (num * 10) + (digitChars.charAt(start9) - '0');
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return num;
        }

        protected static final int parseInt(int num, String digitChars, int start, int end) {
            int num2 = (num * 10) + (digitChars.charAt(start) - '0');
            int start2 = start + 1;
            if (start2 < end) {
                num2 = (num2 * 10) + (digitChars.charAt(start2) - '0');
                int start3 = start2 + 1;
                if (start3 < end) {
                    num2 = (num2 * 10) + (digitChars.charAt(start3) - '0');
                    int start4 = start3 + 1;
                    if (start4 < end) {
                        num2 = (num2 * 10) + (digitChars.charAt(start4) - '0');
                        int start5 = start4 + 1;
                        if (start5 < end) {
                            num2 = (num2 * 10) + (digitChars.charAt(start5) - '0');
                            int start6 = start5 + 1;
                            if (start6 < end) {
                                num2 = (num2 * 10) + (digitChars.charAt(start6) - '0');
                                int start7 = start6 + 1;
                                if (start7 < end) {
                                    num2 = (num2 * 10) + (digitChars.charAt(start7) - '0');
                                    int start8 = start7 + 1;
                                    if (start8 < end) {
                                        num2 = (num2 * 10) + (digitChars.charAt(start8) - '0');
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return num2;
        }

        protected static final long parseLong(char[] digitChars, int start, int end) {
            int start2 = end - 9;
            long val = ((long) parseInt(digitChars, start, start2)) * L_BILLION;
            return val + ((long) parseInt(digitChars, start2, end));
        }

        protected static final long parseLong(String digitChars, int start, int end) {
            int start2 = end - 9;
            long val = ((long) parseInt(digitChars, start, start2)) * L_BILLION;
            return val + ((long) parseInt(digitChars, start2, end));
        }

        protected IllegalArgumentException constructInvalidValue(String lexical) {
            return new IllegalArgumentException("Value \"" + lexical + "\" not a valid lexical representation of " + getType());
        }

        protected IllegalArgumentException constructInvalidValue(char[] lexical, int startOffset, int end) {
            return new IllegalArgumentException("Value \"" + lexicalDesc(lexical, startOffset, end) + "\" not a valid lexical representation of " + getType());
        }

        protected String lexicalDesc(char[] lexical, int startOffset, int end) {
            return _clean(new String(lexical, startOffset, end - startOffset));
        }

        protected String lexicalDesc(String lexical) {
            return _clean(lexical);
        }

        protected String _clean(String str) {
            return str.trim();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/typed/ValueDecoderFactory$BooleanDecoder.class */
    public static final class BooleanDecoder extends DecoderBase {
        protected boolean mValue;

        @Override // org.codehaus.stax2.ri.typed.ValueDecoderFactory.DecoderBase
        public String getType() {
            return "boolean";
        }

        public boolean getValue() {
            return this.mValue;
        }

        @Override // org.codehaus.stax2.typed.TypedValueDecoder
        public void decode(String lexical) throws IllegalArgumentException {
            int len = lexical.length();
            char c = lexical.charAt(0);
            if (c == 't') {
                if (len == 4 && lexical.charAt(1) == 'r' && lexical.charAt(2) == 'u' && lexical.charAt(3) == 'e') {
                    this.mValue = true;
                    return;
                }
            } else if (c == 'f') {
                if (len == 5 && lexical.charAt(1) == 'a' && lexical.charAt(2) == 'l' && lexical.charAt(3) == 's' && lexical.charAt(4) == 'e') {
                    this.mValue = false;
                    return;
                }
            } else if (c == '0') {
                if (len == 1) {
                    this.mValue = false;
                    return;
                }
            } else if (c == '1' && len == 1) {
                this.mValue = true;
                return;
            }
            throw constructInvalidValue(lexical);
        }

        @Override // org.codehaus.stax2.typed.TypedValueDecoder
        public void decode(char[] lexical, int start, int end) throws IllegalArgumentException {
            int len = end - start;
            char c = lexical[start];
            if (c == 't') {
                if (len == 4 && lexical[start + 1] == 'r' && lexical[start + 2] == 'u' && lexical[start + 3] == 'e') {
                    this.mValue = true;
                    return;
                }
            } else if (c == 'f') {
                if (len == 5 && lexical[start + 1] == 'a' && lexical[start + 2] == 'l' && lexical[start + 3] == 's' && lexical[start + 4] == 'e') {
                    this.mValue = false;
                    return;
                }
            } else if (c == '0') {
                if (len == 1) {
                    this.mValue = false;
                    return;
                }
            } else if (c == '1' && len == 1) {
                this.mValue = true;
                return;
            }
            throw constructInvalidValue(lexical, start, end);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/typed/ValueDecoderFactory$IntDecoder.class */
    public static final class IntDecoder extends DecoderBase {
        protected int mValue;

        @Override // org.codehaus.stax2.ri.typed.ValueDecoderFactory.DecoderBase
        public String getType() {
            return "int";
        }

        public int getValue() {
            return this.mValue;
        }

        @Override // org.codehaus.stax2.typed.TypedValueDecoder
        public void decode(String lexical) throws IllegalArgumentException {
            int nr;
            int end = lexical.length();
            char ch = lexical.charAt(0);
            boolean neg = ch == '-';
            if (neg || ch == '+') {
                nr = skipSignAndZeroes(lexical, ch, true, end);
            } else {
                nr = skipSignAndZeroes(lexical, ch, false, end);
            }
            int ptr = this.mNextPtr;
            int charsLeft = end - ptr;
            if (charsLeft == 0) {
                this.mValue = neg ? -nr : nr;
                return;
            }
            verifyDigits(lexical, ptr, end);
            if (charsLeft <= 8) {
                int i = parseInt(nr, lexical, ptr, ptr + charsLeft);
                this.mValue = neg ? -i : i;
                return;
            }
            if (charsLeft == 9 && nr < 3) {
                long base = 1000000000;
                if (nr == 2) {
                    base = 1000000000 + 1000000000;
                }
                long l = base + ((long) parseInt(lexical, ptr, ptr + charsLeft));
                if (neg) {
                    long l2 = -l;
                    if (l2 >= -2147483648L) {
                        this.mValue = (int) l2;
                        return;
                    }
                } else if (l <= 2147483647L) {
                    this.mValue = (int) l;
                    return;
                }
            }
            throw new IllegalArgumentException("value \"" + lexicalDesc(lexical) + "\" not a valid 32-bit integer: overflow.");
        }

        @Override // org.codehaus.stax2.typed.TypedValueDecoder
        public void decode(char[] lexical, int start, int end) throws IllegalArgumentException {
            int nr;
            char ch = lexical[start];
            boolean neg = ch == '-';
            if (neg || ch == '+') {
                nr = skipSignAndZeroes(lexical, ch, true, start, end);
            } else {
                nr = skipSignAndZeroes(lexical, ch, false, start, end);
            }
            int ptr = this.mNextPtr;
            int charsLeft = end - ptr;
            if (charsLeft == 0) {
                this.mValue = neg ? -nr : nr;
                return;
            }
            verifyDigits(lexical, start, end, ptr);
            if (charsLeft <= 8) {
                int i = parseInt(nr, lexical, ptr, ptr + charsLeft);
                this.mValue = neg ? -i : i;
                return;
            }
            if (charsLeft == 9 && nr < 3) {
                long base = 1000000000;
                if (nr == 2) {
                    base = 1000000000 + 1000000000;
                }
                long l = base + ((long) parseInt(lexical, ptr, ptr + charsLeft));
                if (neg) {
                    long l2 = -l;
                    if (l2 >= -2147483648L) {
                        this.mValue = (int) l2;
                        return;
                    }
                } else if (l <= 2147483647L) {
                    this.mValue = (int) l;
                    return;
                }
            }
            throw new IllegalArgumentException("value \"" + lexicalDesc(lexical, start, end) + "\" not a valid 32-bit integer: overflow.");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/typed/ValueDecoderFactory$LongDecoder.class */
    public static final class LongDecoder extends DecoderBase {
        protected long mValue;

        @Override // org.codehaus.stax2.ri.typed.ValueDecoderFactory.DecoderBase
        public String getType() {
            return "long";
        }

        public long getValue() {
            return this.mValue;
        }

        @Override // org.codehaus.stax2.typed.TypedValueDecoder
        public void decode(String lexical) throws IllegalArgumentException {
            int nr;
            int end = lexical.length();
            char ch = lexical.charAt(0);
            boolean neg = ch == '-';
            if (neg || ch == '+') {
                nr = skipSignAndZeroes(lexical, ch, true, end);
            } else {
                nr = skipSignAndZeroes(lexical, ch, false, end);
            }
            int ptr = this.mNextPtr;
            int charsLeft = end - ptr;
            if (charsLeft == 0) {
                this.mValue = neg ? -nr : nr;
                return;
            }
            verifyDigits(lexical, ptr, end);
            if (charsLeft <= 8) {
                int i = parseInt(nr, lexical, ptr, ptr + charsLeft);
                this.mValue = neg ? -i : i;
                return;
            }
            int ptr2 = ptr - 1;
            int charsLeft2 = charsLeft + 1;
            if (charsLeft2 <= 18) {
                long l = parseLong(lexical, ptr2, ptr2 + charsLeft2);
                this.mValue = neg ? -l : l;
            } else {
                this.mValue = parseUsingBD(lexical.substring(ptr2, ptr2 + charsLeft2), neg);
            }
        }

        @Override // org.codehaus.stax2.typed.TypedValueDecoder
        public void decode(char[] lexical, int start, int end) throws IllegalArgumentException {
            int nr;
            char ch = lexical[start];
            boolean neg = ch == '-';
            if (neg || ch == '+') {
                nr = skipSignAndZeroes(lexical, ch, true, start, end);
            } else {
                nr = skipSignAndZeroes(lexical, ch, false, start, end);
            }
            int ptr = this.mNextPtr;
            int charsLeft = end - ptr;
            if (charsLeft == 0) {
                this.mValue = neg ? -nr : nr;
                return;
            }
            verifyDigits(lexical, start, end, ptr);
            if (charsLeft <= 8) {
                int i = parseInt(nr, lexical, ptr, ptr + charsLeft);
                this.mValue = neg ? -i : i;
                return;
            }
            int ptr2 = ptr - 1;
            int charsLeft2 = charsLeft + 1;
            if (charsLeft2 <= 18) {
                long l = parseLong(lexical, ptr2, ptr2 + charsLeft2);
                this.mValue = neg ? -l : l;
            } else {
                this.mValue = parseUsingBD(new String(lexical, ptr2, charsLeft2), neg);
            }
        }

        private long parseUsingBD(String lexical, boolean neg) {
            BigInteger bi = new BigInteger(lexical);
            if (neg) {
                BigInteger bi2 = bi.negate();
                if (bi2.compareTo(BD_MIN_LONG) >= 0) {
                    return bi2.longValue();
                }
            } else if (bi.compareTo(BD_MAX_LONG) <= 0) {
                return bi.longValue();
            }
            throw new IllegalArgumentException("value \"" + lexicalDesc(lexical) + "\" not a valid long: overflow.");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/typed/ValueDecoderFactory$FloatDecoder.class */
    public static final class FloatDecoder extends DecoderBase {
        protected float mValue;

        @Override // org.codehaus.stax2.ri.typed.ValueDecoderFactory.DecoderBase
        public String getType() {
            return "float";
        }

        public float getValue() {
            return this.mValue;
        }

        @Override // org.codehaus.stax2.typed.TypedValueDecoder
        public void decode(String lexical) throws IllegalArgumentException {
            int len = lexical.length();
            if (len == 3) {
                char c = lexical.charAt(0);
                if (c == 'I') {
                    if (lexical.charAt(1) == 'N' && lexical.charAt(2) == 'F') {
                        this.mValue = Float.POSITIVE_INFINITY;
                        return;
                    }
                } else if (c == 'N' && lexical.charAt(1) == 'a' && lexical.charAt(2) == 'N') {
                    this.mValue = Float.NaN;
                    return;
                }
            } else if (len == 4 && lexical.charAt(0) == '-' && lexical.charAt(1) == 'I' && lexical.charAt(2) == 'N' && lexical.charAt(3) == 'F') {
                this.mValue = Float.NEGATIVE_INFINITY;
                return;
            }
            try {
                this.mValue = Float.parseFloat(lexical);
            } catch (NumberFormatException e) {
                throw constructInvalidValue(lexical);
            }
        }

        @Override // org.codehaus.stax2.typed.TypedValueDecoder
        public void decode(char[] lexical, int start, int end) throws IllegalArgumentException {
            int len = end - start;
            if (len == 3) {
                char c = lexical[start];
                if (c == 'I') {
                    if (lexical[start + 1] == 'N' && lexical[start + 2] == 'F') {
                        this.mValue = Float.POSITIVE_INFINITY;
                        return;
                    }
                } else if (c == 'N' && lexical[start + 1] == 'a' && lexical[start + 2] == 'N') {
                    this.mValue = Float.NaN;
                    return;
                }
            } else if (len == 4 && lexical[start] == '-' && lexical[start + 1] == 'I' && lexical[start + 2] == 'N' && lexical[start + 3] == 'F') {
                this.mValue = Float.NEGATIVE_INFINITY;
                return;
            }
            String lexicalStr = new String(lexical, start, len);
            try {
                this.mValue = Float.parseFloat(lexicalStr);
            } catch (NumberFormatException e) {
                throw constructInvalidValue(lexicalStr);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/typed/ValueDecoderFactory$DoubleDecoder.class */
    public static final class DoubleDecoder extends DecoderBase {
        protected double mValue;

        @Override // org.codehaus.stax2.ri.typed.ValueDecoderFactory.DecoderBase
        public String getType() {
            return "double";
        }

        public double getValue() {
            return this.mValue;
        }

        @Override // org.codehaus.stax2.typed.TypedValueDecoder
        public void decode(String lexical) throws IllegalArgumentException {
            int len = lexical.length();
            if (len == 3) {
                char c = lexical.charAt(0);
                if (c == 'I') {
                    if (lexical.charAt(1) == 'N' && lexical.charAt(2) == 'F') {
                        this.mValue = Double.POSITIVE_INFINITY;
                        return;
                    }
                } else if (c == 'N' && lexical.charAt(1) == 'a' && lexical.charAt(2) == 'N') {
                    this.mValue = Double.NaN;
                    return;
                }
            } else if (len == 4 && lexical.charAt(0) == '-' && lexical.charAt(1) == 'I' && lexical.charAt(2) == 'N' && lexical.charAt(3) == 'F') {
                this.mValue = Double.NEGATIVE_INFINITY;
                return;
            }
            try {
                this.mValue = Double.parseDouble(lexical);
            } catch (NumberFormatException e) {
                throw constructInvalidValue(lexical);
            }
        }

        @Override // org.codehaus.stax2.typed.TypedValueDecoder
        public void decode(char[] lexical, int start, int end) throws IllegalArgumentException {
            int len = end - start;
            if (len == 3) {
                char c = lexical[start];
                if (c == 'I') {
                    if (lexical[start + 1] == 'N' && lexical[start + 2] == 'F') {
                        this.mValue = Double.POSITIVE_INFINITY;
                        return;
                    }
                } else if (c == 'N' && lexical[start + 1] == 'a' && lexical[start + 2] == 'N') {
                    this.mValue = Double.NaN;
                    return;
                }
            } else if (len == 4 && lexical[start] == '-' && lexical[start + 1] == 'I' && lexical[start + 2] == 'N' && lexical[start + 3] == 'F') {
                this.mValue = Double.NEGATIVE_INFINITY;
                return;
            }
            String lexicalStr = new String(lexical, start, len);
            try {
                this.mValue = Double.parseDouble(lexicalStr);
            } catch (NumberFormatException e) {
                throw constructInvalidValue(lexicalStr);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/typed/ValueDecoderFactory$IntegerDecoder.class */
    public static final class IntegerDecoder extends DecoderBase {
        protected BigInteger mValue;

        @Override // org.codehaus.stax2.ri.typed.ValueDecoderFactory.DecoderBase
        public String getType() {
            return "integer";
        }

        public BigInteger getValue() {
            return this.mValue;
        }

        @Override // org.codehaus.stax2.typed.TypedValueDecoder
        public void decode(String lexical) throws IllegalArgumentException {
            try {
                this.mValue = new BigInteger(lexical);
            } catch (NumberFormatException e) {
                throw constructInvalidValue(lexical);
            }
        }

        @Override // org.codehaus.stax2.typed.TypedValueDecoder
        public void decode(char[] lexical, int start, int end) throws IllegalArgumentException {
            String lexicalStr = new String(lexical, start, end - start);
            try {
                this.mValue = new BigInteger(lexicalStr);
            } catch (NumberFormatException e) {
                throw constructInvalidValue(lexicalStr);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/typed/ValueDecoderFactory$DecimalDecoder.class */
    public static final class DecimalDecoder extends DecoderBase {
        protected BigDecimal mValue;

        @Override // org.codehaus.stax2.ri.typed.ValueDecoderFactory.DecoderBase
        public String getType() {
            return "decimal";
        }

        public BigDecimal getValue() {
            return this.mValue;
        }

        @Override // org.codehaus.stax2.typed.TypedValueDecoder
        public void decode(String lexical) throws IllegalArgumentException {
            try {
                this.mValue = new BigDecimal(lexical);
            } catch (NumberFormatException e) {
                throw constructInvalidValue(lexical);
            }
        }

        @Override // org.codehaus.stax2.typed.TypedValueDecoder
        public void decode(char[] lexical, int start, int end) throws IllegalArgumentException {
            int len = end - start;
            try {
                this.mValue = new BigDecimal(new String(lexical, start, len));
            } catch (NumberFormatException e) {
                throw constructInvalidValue(new String(lexical, start, len));
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/typed/ValueDecoderFactory$QNameDecoder.class */
    public static final class QNameDecoder extends DecoderBase {
        final NamespaceContext mNsCtxt;
        protected QName mValue;

        public QNameDecoder(NamespaceContext nsc) {
            this.mNsCtxt = nsc;
        }

        @Override // org.codehaus.stax2.ri.typed.ValueDecoderFactory.DecoderBase
        public String getType() {
            return "QName";
        }

        public QName getValue() {
            return this.mValue;
        }

        @Override // org.codehaus.stax2.typed.TypedValueDecoder
        public void decode(String lexical) throws IllegalArgumentException {
            int ix = lexical.indexOf(58);
            if (ix >= 0) {
                this.mValue = resolveQName(lexical.substring(0, ix), lexical.substring(ix + 1));
            } else {
                this.mValue = resolveQName(lexical);
            }
        }

        @Override // org.codehaus.stax2.typed.TypedValueDecoder
        public void decode(char[] lexical, int start, int end) throws IllegalArgumentException {
            for (int i = start; i < end; i++) {
                if (lexical[i] == ':') {
                    this.mValue = resolveQName(new String(lexical, start, i - start), new String(lexical, i + 1, (end - i) - 1));
                    return;
                }
            }
            this.mValue = resolveQName(new String(lexical, start, end - start));
        }

        protected QName resolveQName(String localName) throws IllegalArgumentException {
            String uri = this.mNsCtxt.getNamespaceURI("");
            if (uri == null) {
                uri = "";
            }
            return new QName(uri, localName);
        }

        protected QName resolveQName(String prefix, String localName) throws IllegalArgumentException {
            if (prefix.length() == 0 || localName.length() == 0) {
                throw constructInvalidValue(prefix + ":" + localName);
            }
            String uri = this.mNsCtxt.getNamespaceURI(prefix);
            if (uri == null || uri.length() == 0) {
                throw new IllegalArgumentException("Value \"" + lexicalDesc(prefix + ":" + localName) + "\" not a valid QName: prefix '" + prefix + "' not bound to a namespace");
            }
            return new QName(uri, localName, prefix);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/typed/ValueDecoderFactory$BaseArrayDecoder.class */
    public static abstract class BaseArrayDecoder extends TypedArrayDecoder {
        protected static final int INITIAL_RESULT_BUFFER_SIZE = 40;
        protected static final int SMALL_RESULT_BUFFER_SIZE = 4000;
        protected int mStart;
        protected int mEnd;
        protected int mCount = 0;

        public abstract void expand();

        protected BaseArrayDecoder(int start, int maxCount) {
            this.mStart = start;
            if (maxCount < 1) {
                throw new IllegalArgumentException("Number of elements to read can not be less than 1");
            }
            this.mEnd = maxCount;
        }

        @Override // org.codehaus.stax2.typed.TypedArrayDecoder
        public final int getCount() {
            return this.mCount;
        }

        @Override // org.codehaus.stax2.typed.TypedArrayDecoder
        public final boolean hasRoom() {
            return this.mCount < this.mEnd;
        }

        protected int calcNewSize(int currSize) {
            if (currSize < 4000) {
                return currSize << 2;
            }
            return currSize + currSize;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/typed/ValueDecoderFactory$IntArrayDecoder.class */
    public static final class IntArrayDecoder extends BaseArrayDecoder {
        int[] mResult;
        final IntDecoder mDecoder;

        public IntArrayDecoder(int[] result, int start, int maxCount, IntDecoder intDecoder) {
            super(start, maxCount);
            this.mResult = result;
            this.mDecoder = intDecoder;
        }

        public IntArrayDecoder(IntDecoder intDecoder) {
            super(0, 40);
            this.mResult = new int[40];
            this.mDecoder = intDecoder;
        }

        @Override // org.codehaus.stax2.ri.typed.ValueDecoderFactory.BaseArrayDecoder
        public void expand() {
            int[] old = this.mResult;
            int oldLen = old.length;
            int newSize = calcNewSize(oldLen);
            this.mResult = new int[newSize];
            System.arraycopy(old, this.mStart, this.mResult, 0, oldLen);
            this.mStart = 0;
            this.mEnd = newSize;
        }

        public int[] getValues() {
            int[] result = new int[this.mCount];
            System.arraycopy(this.mResult, this.mStart, result, 0, this.mCount);
            return result;
        }

        @Override // org.codehaus.stax2.typed.TypedArrayDecoder
        public boolean decodeValue(String input) throws IllegalArgumentException {
            this.mDecoder.decode(input);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            int i = this.mCount + 1;
            this.mCount = i;
            return i >= this.mEnd;
        }

        @Override // org.codehaus.stax2.typed.TypedArrayDecoder
        public boolean decodeValue(char[] buffer, int start, int end) throws IllegalArgumentException {
            this.mDecoder.decode(buffer, start, end);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            int i = this.mCount + 1;
            this.mCount = i;
            return i >= this.mEnd;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/typed/ValueDecoderFactory$LongArrayDecoder.class */
    public static final class LongArrayDecoder extends BaseArrayDecoder {
        long[] mResult;
        final LongDecoder mDecoder;

        public LongArrayDecoder(long[] result, int start, int maxCount, LongDecoder longDecoder) {
            super(start, maxCount);
            this.mResult = result;
            this.mDecoder = longDecoder;
        }

        public LongArrayDecoder(LongDecoder longDecoder) {
            super(0, 40);
            this.mResult = new long[40];
            this.mDecoder = longDecoder;
        }

        @Override // org.codehaus.stax2.ri.typed.ValueDecoderFactory.BaseArrayDecoder
        public void expand() {
            long[] old = this.mResult;
            int oldLen = old.length;
            int newSize = calcNewSize(oldLen);
            this.mResult = new long[newSize];
            System.arraycopy(old, this.mStart, this.mResult, 0, oldLen);
            this.mStart = 0;
            this.mEnd = newSize;
        }

        public long[] getValues() {
            long[] result = new long[this.mCount];
            System.arraycopy(this.mResult, this.mStart, result, 0, this.mCount);
            return result;
        }

        @Override // org.codehaus.stax2.typed.TypedArrayDecoder
        public boolean decodeValue(String input) throws IllegalArgumentException {
            this.mDecoder.decode(input);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            int i = this.mCount + 1;
            this.mCount = i;
            return i >= this.mEnd;
        }

        @Override // org.codehaus.stax2.typed.TypedArrayDecoder
        public boolean decodeValue(char[] buffer, int start, int end) throws IllegalArgumentException {
            this.mDecoder.decode(buffer, start, end);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            int i = this.mCount + 1;
            this.mCount = i;
            return i >= this.mEnd;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/typed/ValueDecoderFactory$FloatArrayDecoder.class */
    public static final class FloatArrayDecoder extends BaseArrayDecoder {
        float[] mResult;
        final FloatDecoder mDecoder;

        public FloatArrayDecoder(float[] result, int start, int maxCount, FloatDecoder floatDecoder) {
            super(start, maxCount);
            this.mResult = result;
            this.mDecoder = floatDecoder;
        }

        public FloatArrayDecoder(FloatDecoder floatDecoder) {
            super(0, 40);
            this.mResult = new float[40];
            this.mDecoder = floatDecoder;
        }

        @Override // org.codehaus.stax2.ri.typed.ValueDecoderFactory.BaseArrayDecoder
        public void expand() {
            float[] old = this.mResult;
            int oldLen = old.length;
            int newSize = calcNewSize(oldLen);
            this.mResult = new float[newSize];
            System.arraycopy(old, this.mStart, this.mResult, 0, oldLen);
            this.mStart = 0;
            this.mEnd = newSize;
        }

        public float[] getValues() {
            float[] result = new float[this.mCount];
            System.arraycopy(this.mResult, this.mStart, result, 0, this.mCount);
            return result;
        }

        @Override // org.codehaus.stax2.typed.TypedArrayDecoder
        public boolean decodeValue(String input) throws IllegalArgumentException {
            this.mDecoder.decode(input);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            int i = this.mCount + 1;
            this.mCount = i;
            return i >= this.mEnd;
        }

        @Override // org.codehaus.stax2.typed.TypedArrayDecoder
        public boolean decodeValue(char[] buffer, int start, int end) throws IllegalArgumentException {
            this.mDecoder.decode(buffer, start, end);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            int i = this.mCount + 1;
            this.mCount = i;
            return i >= this.mEnd;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/typed/ValueDecoderFactory$DoubleArrayDecoder.class */
    public static final class DoubleArrayDecoder extends BaseArrayDecoder {
        double[] mResult;
        final DoubleDecoder mDecoder;

        public DoubleArrayDecoder(double[] result, int start, int maxCount, DoubleDecoder doubleDecoder) {
            super(start, maxCount);
            this.mResult = result;
            this.mDecoder = doubleDecoder;
        }

        public DoubleArrayDecoder(DoubleDecoder doubleDecoder) {
            super(0, 40);
            this.mResult = new double[40];
            this.mDecoder = doubleDecoder;
        }

        @Override // org.codehaus.stax2.ri.typed.ValueDecoderFactory.BaseArrayDecoder
        public void expand() {
            double[] old = this.mResult;
            int oldLen = old.length;
            int newSize = calcNewSize(oldLen);
            this.mResult = new double[newSize];
            System.arraycopy(old, this.mStart, this.mResult, 0, oldLen);
            this.mStart = 0;
            this.mEnd = newSize;
        }

        public double[] getValues() {
            double[] result = new double[this.mCount];
            System.arraycopy(this.mResult, this.mStart, result, 0, this.mCount);
            return result;
        }

        @Override // org.codehaus.stax2.typed.TypedArrayDecoder
        public boolean decodeValue(String input) throws IllegalArgumentException {
            this.mDecoder.decode(input);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            int i = this.mCount + 1;
            this.mCount = i;
            return i >= this.mEnd;
        }

        @Override // org.codehaus.stax2.typed.TypedArrayDecoder
        public boolean decodeValue(char[] buffer, int start, int end) throws IllegalArgumentException {
            this.mDecoder.decode(buffer, start, end);
            this.mResult[this.mStart + this.mCount] = this.mDecoder.getValue();
            int i = this.mCount + 1;
            this.mCount = i;
            return i >= this.mEnd;
        }
    }
}
