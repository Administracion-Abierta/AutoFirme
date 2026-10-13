package org.apache.commons.codec.language;

import java.util.Arrays;
import java.util.Locale;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/language/ColognePhonetic.class */
public class ColognePhonetic implements StringEncoder {
    private static final char[] AEIJOUY = {'A', 'E', 'I', 'J', 'O', 'U', 'Y'};
    private static final char[] CSZ = {'C', 'S', 'Z'};
    private static final char[] FPVW = {'F', 'P', 'V', 'W'};
    private static final char[] GKQ = {'G', 'K', 'Q'};
    private static final char[] CKQ = {'C', 'K', 'Q'};
    private static final char[] AHKLOQRUX = {'A', 'H', 'K', 'L', 'O', 'Q', 'R', 'U', 'X'};
    private static final char[] SZ = {'S', 'Z'};
    private static final char[] AHKOQUX = {'A', 'H', 'K', 'O', 'Q', 'U', 'X'};
    private static final char[] DTX = {'D', 'T', 'X'};
    private static final char CHAR_IGNORE = '-';

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/language/ColognePhonetic$CologneBuffer.class */
    static abstract class CologneBuffer {
        protected final char[] data;
        protected int length;

        protected abstract char[] copyData(int i, int i2);

        public CologneBuffer(char[] data) {
            this.data = data;
            this.length = data.length;
        }

        public CologneBuffer(int buffSize) {
            this.data = new char[buffSize];
            this.length = 0;
        }

        public boolean isEmpty() {
            return length() == 0;
        }

        public int length() {
            return this.length;
        }

        public String toString() {
            return new String(copyData(0, this.length));
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/language/ColognePhonetic$CologneInputBuffer.class */
    private final class CologneInputBuffer extends CologneBuffer {
        public CologneInputBuffer(char[] data) {
            super(data);
        }

        @Override // org.apache.commons.codec.language.ColognePhonetic.CologneBuffer
        protected char[] copyData(int start, int length) {
            char[] newData = new char[length];
            System.arraycopy(this.data, (this.data.length - this.length) + start, newData, 0, length);
            return newData;
        }

        public char getNextChar() {
            return this.data[getNextPos()];
        }

        protected int getNextPos() {
            return this.data.length - this.length;
        }

        public char removeNext() {
            char ch = getNextChar();
            this.length--;
            return ch;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/language/ColognePhonetic$CologneOutputBuffer.class */
    private final class CologneOutputBuffer extends CologneBuffer {
        private char lastCode;

        public CologneOutputBuffer(int buffSize) {
            super(buffSize);
            this.lastCode = '/';
        }

        @Override // org.apache.commons.codec.language.ColognePhonetic.CologneBuffer
        protected char[] copyData(int start, int length) {
            return Arrays.copyOfRange(this.data, start, length);
        }

        public void put(char code) {
            if (code != '-' && this.lastCode != code && (code != '0' || this.length == 0)) {
                this.data[this.length] = code;
                this.length++;
            }
            this.lastCode = code;
        }
    }

    private static boolean arrayContains(char[] arr, char key) {
        for (char element : arr) {
            if (element == key) {
                return true;
            }
        }
        return false;
    }

    public String colognePhonetic(String text) {
        char nextChar;
        if (text == null) {
            return null;
        }
        CologneInputBuffer input = new CologneInputBuffer(preprocess(text));
        CologneOutputBuffer output = new CologneOutputBuffer(input.length() * 2);
        char lastChar = '-';
        while (!input.isEmpty()) {
            char chr = input.removeNext();
            if (!input.isEmpty()) {
                nextChar = input.getNextChar();
            } else {
                nextChar = '-';
            }
            if (chr >= 'A' && chr <= 'Z') {
                if (arrayContains(AEIJOUY, chr)) {
                    output.put('0');
                } else if (chr == 'B' || (chr == 'P' && nextChar != 'H')) {
                    output.put('1');
                } else if ((chr == 'D' || chr == 'T') && !arrayContains(CSZ, nextChar)) {
                    output.put('2');
                } else if (arrayContains(FPVW, chr)) {
                    output.put('3');
                } else if (arrayContains(GKQ, chr)) {
                    output.put('4');
                } else if (chr == 'X' && !arrayContains(CKQ, lastChar)) {
                    output.put('4');
                    output.put('8');
                } else if (chr == 'S' || chr == 'Z') {
                    output.put('8');
                } else if (chr == 'C') {
                    if (output.isEmpty()) {
                        if (arrayContains(AHKLOQRUX, nextChar)) {
                            output.put('4');
                        } else {
                            output.put('8');
                        }
                    } else if (arrayContains(SZ, lastChar) || !arrayContains(AHKOQUX, nextChar)) {
                        output.put('8');
                    } else {
                        output.put('4');
                    }
                } else if (arrayContains(DTX, chr)) {
                    output.put('8');
                } else {
                    switch (chr) {
                        case 'H':
                            output.put('-');
                            break;
                        case 'L':
                            output.put('5');
                            break;
                        case EACTags.INTEGRATED_CIRCUIT_MANUFACTURER_ID /* 77 */:
                        case 'N':
                            output.put('6');
                            break;
                        case 'R':
                            output.put('7');
                            break;
                    }
                }
                lastChar = chr;
            }
        }
        return output.toString();
    }

    @Override // org.apache.commons.codec.Encoder
    public Object encode(Object object) throws EncoderException {
        if (!(object instanceof String)) {
            throw new EncoderException("This method's parameter was expected to be of the type " + String.class.getName() + ". But actually it was of the type " + object.getClass().getName() + ".");
        }
        return encode((String) object);
    }

    @Override // org.apache.commons.codec.StringEncoder
    public String encode(String text) {
        return colognePhonetic(text);
    }

    public boolean isEncodeEqual(String text1, String text2) {
        return colognePhonetic(text1).equals(colognePhonetic(text2));
    }

    private char[] preprocess(String text) {
        char[] chrs = text.toUpperCase(Locale.GERMAN).toCharArray();
        for (int index = 0; index < chrs.length; index++) {
            switch (chrs[index]) {
                case CipherSuite.TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA256 /* 196 */:
                    chrs[index] = 'A';
                    break;
                case 214:
                    chrs[index] = 'O';
                    break;
                case 220:
                    chrs[index] = 'U';
                    break;
            }
        }
        return chrs;
    }
}
