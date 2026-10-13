package org.codehaus.stax2.ri.typed;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.codehaus.stax2.ri.Stax2Util;
import org.codehaus.stax2.typed.Base64Variant;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/typed/CharArrayBase64Decoder.class */
public class CharArrayBase64Decoder extends Base64DecoderBase {
    protected char[] _currSegment;
    protected int _currSegmentPtr;
    protected int _currSegmentEnd;
    protected final ArrayList<char[]> _nextSegments = new ArrayList<>();
    protected int _lastSegmentOffset;
    protected int _lastSegmentEnd;
    protected int _nextSegmentIndex;

    @Override // org.codehaus.stax2.ri.typed.Base64DecoderBase
    public /* bridge */ /* synthetic */ Stax2Util.ByteAggregator getByteAggregator() {
        return super.getByteAggregator();
    }

    @Override // org.codehaus.stax2.ri.typed.Base64DecoderBase
    public /* bridge */ /* synthetic */ byte[] decodeCompletely() {
        return super.decodeCompletely();
    }

    public void init(Base64Variant variant, boolean firstChunk, char[] lastSegment, int lastOffset, int lastLen, List<char[]> segments) {
        this._variant = variant;
        if (firstChunk) {
            this._state = 0;
        }
        this._nextSegments.clear();
        if (segments == null || segments.isEmpty()) {
            this._currSegment = lastSegment;
            this._currSegmentPtr = lastOffset;
            this._currSegmentEnd = lastOffset + lastLen;
        } else {
            if (lastSegment == null) {
                throw new IllegalArgumentException();
            }
            Iterator<char[]> it = segments.iterator();
            this._currSegment = it.next();
            this._currSegmentPtr = 0;
            this._currSegmentEnd = this._currSegment.length;
            while (it.hasNext()) {
                this._nextSegments.add(it.next());
            }
            this._nextSegmentIndex = 0;
            this._nextSegments.add(lastSegment);
            this._lastSegmentOffset = lastOffset;
            this._lastSegmentEnd = lastOffset + lastLen;
        }
    }

    /* JADX WARN: Code duplicated, block: B:26:0x00cc  */
    /* JADX WARN: Code duplicated, block: B:29:0x00e5  */
    /* JADX WARN: Code duplicated, block: B:38:0x0125  */
    /* JADX WARN: Code duplicated, block: B:42:0x0147  */
    /* JADX WARN: Code duplicated, block: B:51:0x0187  */
    /* JADX WARN: Code duplicated, block: B:56:0x01b5  */
    /* JADX WARN: Code duplicated, block: B:60:0x01d1  */
    /* JADX WARN: Code duplicated, block: B:64:0x01ee  */
    /* JADX WARN: Code duplicated, block: B:88:0x00c4 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:90:0x012e A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:91:0x011d A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:92:0x0116 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:94:0x0199 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:95:0x017f A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:96:0x0178 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:97:0x01ad A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:98:0x01c9 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:99:0x01e5 A[SYNTHETIC] */
    @Override // org.codehaus.stax2.ri.typed.Base64DecoderBase
    public int decode(byte[] resultBuffer, int resultOffset, int maxLength) throws IllegalArgumentException {
        char ch;
        int bits;
        char ch2;
        int bits2;
        char ch3;
        char ch4;
        int bits3;
        int resultBufferEnd = resultOffset + maxLength;
        while (true) {
            switch (this._state) {
                case 0:
                    do {
                        if (this._currSegmentPtr < this._currSegmentEnd || nextSegment()) {
                            char[] cArr = this._currSegment;
                            int i = this._currSegmentPtr;
                            this._currSegmentPtr = i + 1;
                            ch3 = cArr[i];
                        }
                        return resultOffset - resultOffset;
                    } while (ch3 <= ' ');
                    int bits4 = this._variant.decodeBase64Char(ch3);
                    if (bits4 < 0) {
                        throw reportInvalidChar(ch3, 0);
                    }
                    this._decodedData = bits4;
                    if (this._currSegmentPtr < this._currSegmentEnd && !nextSegment()) {
                        this._state = 1;
                    } else {
                        char[] cArr2 = this._currSegment;
                        int i2 = this._currSegmentPtr;
                        this._currSegmentPtr = i2 + 1;
                        ch2 = cArr2[i2];
                        bits2 = this._variant.decodeBase64Char(ch2);
                        if (bits2 < 0) {
                            throw reportInvalidChar(ch2, 1);
                        }
                        this._decodedData = (this._decodedData << 6) | bits2;
                        if (this._currSegmentPtr < this._currSegmentEnd && !nextSegment()) {
                            this._state = 2;
                        } else {
                            char[] cArr3 = this._currSegment;
                            int i3 = this._currSegmentPtr;
                            this._currSegmentPtr = i3 + 1;
                            ch = cArr3[i3];
                            bits = this._variant.decodeBase64Char(ch);
                            if (bits < 0) {
                                if (bits != -2) {
                                    throw reportInvalidChar(ch, 2);
                                }
                                this._state = 7;
                            } else {
                                this._decodedData = (this._decodedData << 6) | bits;
                                if (this._currSegmentPtr < this._currSegmentEnd && !nextSegment()) {
                                    this._state = 3;
                                } else {
                                    char[] cArr4 = this._currSegment;
                                    int i4 = this._currSegmentPtr;
                                    this._currSegmentPtr = i4 + 1;
                                    ch4 = cArr4[i4];
                                    bits3 = this._variant.decodeBase64Char(ch4);
                                    if (bits3 < 0) {
                                        if (bits3 != -2) {
                                            throw reportInvalidChar(ch4, 3);
                                        }
                                        this._decodedData >>= 2;
                                        this._state = 5;
                                    } else {
                                        this._decodedData = (this._decodedData << 6) | bits3;
                                        if (resultOffset >= resultBufferEnd) {
                                            this._state = 4;
                                        } else {
                                            int i5 = resultOffset;
                                            resultOffset++;
                                            resultBuffer[i5] = (byte) (this._decodedData >> 16);
                                            if (resultOffset >= resultBufferEnd) {
                                                this._state = 5;
                                            } else {
                                                int i6 = resultOffset;
                                                resultOffset++;
                                                resultBuffer[i6] = (byte) (this._decodedData >> 8);
                                                if (resultOffset >= resultBufferEnd) {
                                                    this._state = 6;
                                                } else {
                                                    int i7 = resultOffset;
                                                    resultOffset++;
                                                    resultBuffer[i7] = (byte) this._decodedData;
                                                    this._state = 0;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    }
                    return resultOffset - resultOffset;
                case 1:
                    if (this._currSegmentPtr < this._currSegmentEnd) {
                    }
                    char[] cArr5 = this._currSegment;
                    int i8 = this._currSegmentPtr;
                    this._currSegmentPtr = i8 + 1;
                    ch2 = cArr5[i8];
                    bits2 = this._variant.decodeBase64Char(ch2);
                    if (bits2 < 0) {
                        throw reportInvalidChar(ch2, 1);
                    }
                    this._decodedData = (this._decodedData << 6) | bits2;
                    if (this._currSegmentPtr < this._currSegmentEnd) {
                    }
                    char[] cArr6 = this._currSegment;
                    int i9 = this._currSegmentPtr;
                    this._currSegmentPtr = i9 + 1;
                    ch = cArr6[i9];
                    bits = this._variant.decodeBase64Char(ch);
                    if (bits < 0) {
                        if (bits != -2) {
                            throw reportInvalidChar(ch, 2);
                        }
                        this._state = 7;
                    } else {
                        this._decodedData = (this._decodedData << 6) | bits;
                        if (this._currSegmentPtr < this._currSegmentEnd) {
                        }
                        char[] cArr7 = this._currSegment;
                        int i10 = this._currSegmentPtr;
                        this._currSegmentPtr = i10 + 1;
                        ch4 = cArr7[i10];
                        bits3 = this._variant.decodeBase64Char(ch4);
                        if (bits3 < 0) {
                            if (bits3 != -2) {
                                throw reportInvalidChar(ch4, 3);
                            }
                            this._decodedData >>= 2;
                            this._state = 5;
                        } else {
                            this._decodedData = (this._decodedData << 6) | bits3;
                            if (resultOffset >= resultBufferEnd) {
                                this._state = 4;
                            } else {
                                int i11 = resultOffset;
                                resultOffset++;
                                resultBuffer[i11] = (byte) (this._decodedData >> 16);
                                if (resultOffset >= resultBufferEnd) {
                                    this._state = 5;
                                } else {
                                    int i12 = resultOffset;
                                    resultOffset++;
                                    resultBuffer[i12] = (byte) (this._decodedData >> 8);
                                    if (resultOffset >= resultBufferEnd) {
                                        this._state = 6;
                                    } else {
                                        int i13 = resultOffset;
                                        resultOffset++;
                                        resultBuffer[i13] = (byte) this._decodedData;
                                        this._state = 0;
                                    }
                                }
                            }
                            return resultOffset - resultOffset;
                        }
                    }
                    break;
                    break;
                case 2:
                    if (this._currSegmentPtr < this._currSegmentEnd) {
                    }
                    char[] cArr8 = this._currSegment;
                    int i14 = this._currSegmentPtr;
                    this._currSegmentPtr = i14 + 1;
                    ch = cArr8[i14];
                    bits = this._variant.decodeBase64Char(ch);
                    if (bits < 0) {
                        if (bits != -2) {
                            throw reportInvalidChar(ch, 2);
                        }
                        this._state = 7;
                    } else {
                        this._decodedData = (this._decodedData << 6) | bits;
                        if (this._currSegmentPtr < this._currSegmentEnd) {
                        }
                        char[] cArr9 = this._currSegment;
                        int i15 = this._currSegmentPtr;
                        this._currSegmentPtr = i15 + 1;
                        ch4 = cArr9[i15];
                        bits3 = this._variant.decodeBase64Char(ch4);
                        if (bits3 < 0) {
                            if (bits3 != -2) {
                                throw reportInvalidChar(ch4, 3);
                            }
                            this._decodedData >>= 2;
                            this._state = 5;
                        } else {
                            this._decodedData = (this._decodedData << 6) | bits3;
                            if (resultOffset >= resultBufferEnd) {
                                this._state = 4;
                            } else {
                                int i16 = resultOffset;
                                resultOffset++;
                                resultBuffer[i16] = (byte) (this._decodedData >> 16);
                                if (resultOffset >= resultBufferEnd) {
                                    this._state = 5;
                                } else {
                                    int i17 = resultOffset;
                                    resultOffset++;
                                    resultBuffer[i17] = (byte) (this._decodedData >> 8);
                                    if (resultOffset >= resultBufferEnd) {
                                        this._state = 6;
                                    } else {
                                        int i18 = resultOffset;
                                        resultOffset++;
                                        resultBuffer[i18] = (byte) this._decodedData;
                                        this._state = 0;
                                    }
                                }
                            }
                            return resultOffset - resultOffset;
                        }
                    }
                    break;
                case 3:
                    if (this._currSegmentPtr < this._currSegmentEnd) {
                    }
                    char[] cArr10 = this._currSegment;
                    int i19 = this._currSegmentPtr;
                    this._currSegmentPtr = i19 + 1;
                    ch4 = cArr10[i19];
                    bits3 = this._variant.decodeBase64Char(ch4);
                    if (bits3 < 0) {
                        if (bits3 != -2) {
                            throw reportInvalidChar(ch4, 3);
                        }
                        this._decodedData >>= 2;
                        this._state = 5;
                    } else {
                        this._decodedData = (this._decodedData << 6) | bits3;
                        if (resultOffset >= resultBufferEnd) {
                            this._state = 4;
                        } else {
                            int i110 = resultOffset;
                            resultOffset++;
                            resultBuffer[i110] = (byte) (this._decodedData >> 16);
                            if (resultOffset >= resultBufferEnd) {
                                this._state = 5;
                            } else {
                                int i111 = resultOffset;
                                resultOffset++;
                                resultBuffer[i111] = (byte) (this._decodedData >> 8);
                                if (resultOffset >= resultBufferEnd) {
                                    this._state = 6;
                                } else {
                                    int i112 = resultOffset;
                                    resultOffset++;
                                    resultBuffer[i112] = (byte) this._decodedData;
                                    this._state = 0;
                                }
                            }
                        }
                        return resultOffset - resultOffset;
                    }
                    break;
                case 4:
                    if (resultOffset >= resultBufferEnd) {
                        this._state = 4;
                    } else {
                        int i113 = resultOffset;
                        resultOffset++;
                        resultBuffer[i113] = (byte) (this._decodedData >> 16);
                        if (resultOffset >= resultBufferEnd) {
                            this._state = 5;
                        } else {
                            int i114 = resultOffset;
                            resultOffset++;
                            resultBuffer[i114] = (byte) (this._decodedData >> 8);
                            if (resultOffset >= resultBufferEnd) {
                                this._state = 6;
                            } else {
                                int i115 = resultOffset;
                                resultOffset++;
                                resultBuffer[i115] = (byte) this._decodedData;
                                this._state = 0;
                            }
                        }
                        break;
                    }
                    return resultOffset - resultOffset;
                case 5:
                    if (resultOffset >= resultBufferEnd) {
                        this._state = 5;
                    } else {
                        int i116 = resultOffset;
                        resultOffset++;
                        resultBuffer[i116] = (byte) (this._decodedData >> 8);
                        if (resultOffset >= resultBufferEnd) {
                            this._state = 6;
                        } else {
                            int i117 = resultOffset;
                            resultOffset++;
                            resultBuffer[i117] = (byte) this._decodedData;
                            this._state = 0;
                        }
                        break;
                    }
                    return resultOffset - resultOffset;
                case 6:
                    if (resultOffset >= resultBufferEnd) {
                        this._state = 6;
                        return resultOffset - resultOffset;
                    }
                    int i118 = resultOffset;
                    resultOffset++;
                    resultBuffer[i118] = (byte) this._decodedData;
                    this._state = 0;
                    break;
                    break;
                case 7:
                    if (this._currSegmentPtr >= this._currSegmentEnd && !nextSegment()) {
                        return resultOffset - resultOffset;
                    }
                    char[] cArr11 = this._currSegment;
                    int i20 = this._currSegmentPtr;
                    this._currSegmentPtr = i20 + 1;
                    char ch5 = cArr11[i20];
                    if (!this._variant.usesPaddingChar(ch5)) {
                        throw reportInvalidChar(ch5, 3, "expected padding character '" + this._variant.getPaddingChar() + "'");
                    }
                    this._state = 6;
                    this._decodedData >>= 4;
                    break;
                    break;
                default:
                    throw new IllegalStateException("Illegal internal state " + this._state);
            }
        }
    }

    private boolean nextSegment() {
        if (this._nextSegmentIndex < this._nextSegments.size()) {
            ArrayList<char[]> arrayList = this._nextSegments;
            int i = this._nextSegmentIndex;
            this._nextSegmentIndex = i + 1;
            this._currSegment = arrayList.get(i);
            if (this._nextSegmentIndex == this._nextSegments.size()) {
                this._currSegmentPtr = this._lastSegmentOffset;
                this._currSegmentEnd = this._lastSegmentEnd;
                return true;
            }
            this._currSegmentPtr = 0;
            this._currSegmentEnd = this._currSegment.length;
            return true;
        }
        return false;
    }
}
