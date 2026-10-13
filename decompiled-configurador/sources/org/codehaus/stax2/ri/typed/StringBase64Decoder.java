package org.codehaus.stax2.ri.typed;

import org.codehaus.stax2.ri.Stax2Util;
import org.codehaus.stax2.typed.Base64Variant;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/typed/StringBase64Decoder.class */
public final class StringBase64Decoder extends Base64DecoderBase {
    String _currSegment;
    int _currSegmentPtr;
    int _currSegmentEnd;

    @Override // org.codehaus.stax2.ri.typed.Base64DecoderBase
    public /* bridge */ /* synthetic */ Stax2Util.ByteAggregator getByteAggregator() {
        return super.getByteAggregator();
    }

    @Override // org.codehaus.stax2.ri.typed.Base64DecoderBase
    public /* bridge */ /* synthetic */ byte[] decodeCompletely() {
        return super.decodeCompletely();
    }

    public void init(Base64Variant variant, boolean firstChunk, String segment) {
        this._variant = variant;
        if (firstChunk) {
            this._state = 0;
        }
        this._currSegment = segment;
        this._currSegmentPtr = 0;
        this._currSegmentEnd = segment.length();
    }

    /* JADX WARN: Code duplicated, block: B:18:0x0096  */
    /* JADX WARN: Code duplicated, block: B:22:0x00c2  */
    /* JADX WARN: Code duplicated, block: B:26:0x00e3  */
    /* JADX WARN: Code duplicated, block: B:32:0x0116  */
    /* JADX WARN: Code duplicated, block: B:37:0x0140  */
    /* JADX WARN: Code duplicated, block: B:43:0x0173  */
    /* JADX WARN: Code duplicated, block: B:48:0x01a1  */
    /* JADX WARN: Code duplicated, block: B:52:0x01bd  */
    /* JADX WARN: Code duplicated, block: B:56:0x01da  */
    /* JADX WARN: Code duplicated, block: B:77:0x008e A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:78:0x00ba A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:79:0x00db A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:80:0x011f A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:81:0x010e A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:82:0x0107 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:83:0x0138 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:84:0x0185 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:85:0x016b A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:86:0x0164 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:87:0x0199 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:88:0x01b5 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:89:0x01d1 A[SYNTHETIC] */
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
                        if (this._currSegmentPtr < this._currSegmentEnd) {
                            String str = this._currSegment;
                            int i = this._currSegmentPtr;
                            this._currSegmentPtr = i + 1;
                            ch3 = str.charAt(i);
                        }
                        return resultOffset - resultOffset;
                    } while (ch3 <= ' ');
                    int bits4 = this._variant.decodeBase64Char(ch3);
                    if (bits4 < 0) {
                        throw reportInvalidChar(ch3, 0);
                    }
                    this._decodedData = bits4;
                    if (this._currSegmentPtr >= this._currSegmentEnd) {
                        this._state = 1;
                    } else {
                        String str2 = this._currSegment;
                        int i2 = this._currSegmentPtr;
                        this._currSegmentPtr = i2 + 1;
                        ch2 = str2.charAt(i2);
                        bits2 = this._variant.decodeBase64Char(ch2);
                        if (bits2 < 0) {
                            throw reportInvalidChar(ch2, 1);
                        }
                        this._decodedData = (this._decodedData << 6) | bits2;
                        if (this._currSegmentPtr >= this._currSegmentEnd) {
                            this._state = 2;
                        } else {
                            String str3 = this._currSegment;
                            int i3 = this._currSegmentPtr;
                            this._currSegmentPtr = i3 + 1;
                            ch = str3.charAt(i3);
                            bits = this._variant.decodeBase64Char(ch);
                            if (bits < 0) {
                                if (bits != -2) {
                                    throw reportInvalidChar(ch, 2);
                                }
                                this._state = 7;
                            } else {
                                this._decodedData = (this._decodedData << 6) | bits;
                                if (this._currSegmentPtr >= this._currSegmentEnd) {
                                    this._state = 3;
                                } else {
                                    String str4 = this._currSegment;
                                    int i4 = this._currSegmentPtr;
                                    this._currSegmentPtr = i4 + 1;
                                    ch4 = str4.charAt(i4);
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
                    if (this._currSegmentPtr >= this._currSegmentEnd) {
                        this._state = 1;
                    } else {
                        String str5 = this._currSegment;
                        int i8 = this._currSegmentPtr;
                        this._currSegmentPtr = i8 + 1;
                        ch2 = str5.charAt(i8);
                        bits2 = this._variant.decodeBase64Char(ch2);
                        if (bits2 < 0) {
                            throw reportInvalidChar(ch2, 1);
                        }
                        this._decodedData = (this._decodedData << 6) | bits2;
                        if (this._currSegmentPtr >= this._currSegmentEnd) {
                            this._state = 2;
                        } else {
                            String str6 = this._currSegment;
                            int i9 = this._currSegmentPtr;
                            this._currSegmentPtr = i9 + 1;
                            ch = str6.charAt(i9);
                            bits = this._variant.decodeBase64Char(ch);
                            if (bits < 0) {
                                if (bits != -2) {
                                    throw reportInvalidChar(ch, 2);
                                }
                                this._state = 7;
                            } else {
                                this._decodedData = (this._decodedData << 6) | bits;
                                if (this._currSegmentPtr >= this._currSegmentEnd) {
                                    this._state = 3;
                                } else {
                                    String str7 = this._currSegment;
                                    int i10 = this._currSegmentPtr;
                                    this._currSegmentPtr = i10 + 1;
                                    ch4 = str7.charAt(i10);
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
                                    }
                                }
                            }
                        }
                        break;
                    }
                    return resultOffset - resultOffset;
                case 2:
                    if (this._currSegmentPtr >= this._currSegmentEnd) {
                        this._state = 2;
                    } else {
                        String str8 = this._currSegment;
                        int i14 = this._currSegmentPtr;
                        this._currSegmentPtr = i14 + 1;
                        ch = str8.charAt(i14);
                        bits = this._variant.decodeBase64Char(ch);
                        if (bits < 0) {
                            if (bits != -2) {
                                throw reportInvalidChar(ch, 2);
                            }
                            this._state = 7;
                        } else {
                            this._decodedData = (this._decodedData << 6) | bits;
                            if (this._currSegmentPtr >= this._currSegmentEnd) {
                                this._state = 3;
                            } else {
                                String str9 = this._currSegment;
                                int i15 = this._currSegmentPtr;
                                this._currSegmentPtr = i15 + 1;
                                ch4 = str9.charAt(i15);
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
                                }
                            }
                        }
                        break;
                    }
                    return resultOffset - resultOffset;
                case 3:
                    if (this._currSegmentPtr >= this._currSegmentEnd) {
                        this._state = 3;
                    } else {
                        String str10 = this._currSegment;
                        int i19 = this._currSegmentPtr;
                        this._currSegmentPtr = i19 + 1;
                        ch4 = str10.charAt(i19);
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
                        }
                        break;
                    }
                    return resultOffset - resultOffset;
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
                    if (this._currSegmentPtr >= this._currSegmentEnd) {
                        return resultOffset - resultOffset;
                    }
                    String str11 = this._currSegment;
                    int i20 = this._currSegmentPtr;
                    this._currSegmentPtr = i20 + 1;
                    char ch5 = str11.charAt(i20);
                    if (!this._variant.usesPaddingChar(ch5)) {
                        throw reportInvalidChar(ch5, 3, "expected padding character '='");
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
}
