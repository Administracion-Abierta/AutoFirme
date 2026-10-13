package es.gob.jmulticard.card.icao.vdsned;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/icao/vdsned/C40Decoder.class */
final class C40Decoder {
    private static final char[] C40_BASIC_SET_CHARS = {'*', '*', '*', '<', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final char[] C40_SHIFT2_SET_CHARS = {'!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_'};

    private C40Decoder() {
    }

    static String decode(byte[] c40encoded) throws IOException {
        int firstByte;
        StringBuilder result = new StringBuilder();
        ByteArrayInputStream bits = new ByteArrayInputStream(c40encoded);
        boolean upperShift = false;
        while (bits.available() != 1 && (firstByte = bits.read()) != 254) {
            int[] cValues = parseTwoBytes(firstByte, bits.read());
            int shift = 0;
            for (int i = 0; i < 3; i++) {
                int cValue = cValues[i];
                switch (shift) {
                    case 0:
                        if (cValue < 3) {
                            shift = cValue + 1;
                        } else if (upperShift) {
                            result.append((char) (C40_BASIC_SET_CHARS[cValue] + 128));
                            upperShift = false;
                        } else {
                            result.append(C40_BASIC_SET_CHARS[cValue]);
                        }
                        break;
                    case 1:
                        if (upperShift) {
                            result.append((char) (cValue + 128));
                            upperShift = false;
                        } else {
                            result.append(cValue);
                        }
                        shift = 0;
                        break;
                    case 2:
                        if (cValue < 27) {
                            if (upperShift) {
                                result.append((char) (C40_SHIFT2_SET_CHARS[cValue] + 128));
                                upperShift = false;
                            } else {
                                result.append(C40_SHIFT2_SET_CHARS[cValue]);
                            }
                        } else {
                            if (cValue == 27 || cValue != 30) {
                                throw new IOException();
                            }
                            upperShift = true;
                        }
                        shift = 0;
                        break;
                    case 3:
                        if (upperShift) {
                            result.append((char) (cValue + 224));
                            upperShift = false;
                        } else {
                            result.append((char) (cValue + 96));
                        }
                        shift = 0;
                        break;
                    default:
                        throw new IOException();
                }
            }
            if (bits.available() <= 0) {
                return result.toString();
            }
        }
        return result.toString();
    }

    private static int[] parseTwoBytes(int firstByte, int secondByte) {
        int fullBitValue = ((firstByte << 8) + secondByte) - 1;
        int temp = fullBitValue / 1600;
        int fullBitValue2 = fullBitValue - (temp * 1600);
        int temp2 = fullBitValue2 / 40;
        int[] result = {temp, temp2, fullBitValue2 - (temp2 * 40)};
        return result;
    }
}
