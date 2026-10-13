package com.aowagie.text.pdf.fonts.cmaps;

import com.ctc.wstx.io.CharsetNames;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/fonts/cmaps/CMapParser.class */
public class CMapParser {
    private static final String BEGIN_CODESPACE_RANGE = "begincodespacerange";
    private static final String BEGIN_BASE_FONT_CHAR = "beginbfchar";
    private static final String BEGIN_BASE_FONT_RANGE = "beginbfrange";
    private static final String MARK_END_OF_DICTIONARY = ">>";
    private static final String MARK_END_OF_ARRAY = "]";
    private final byte[] tokenParserByteBuffer = new byte[512];

    public CMap parse(InputStream input) throws IOException {
        byte[] tokenBytes;
        PushbackInputStream cmapStream = new PushbackInputStream(input);
        CMap result = new CMap();
        Object previousToken = null;
        while (true) {
            Object token = parseNextToken(cmapStream);
            if (token != null) {
                if (token instanceof Operator) {
                    Operator op = (Operator) token;
                    if (op.op.equals(BEGIN_CODESPACE_RANGE)) {
                        Number cosCount = (Number) previousToken;
                        for (int j = 0; j < cosCount.intValue(); j++) {
                            byte[] startRange = (byte[]) parseNextToken(cmapStream);
                            byte[] endRange = (byte[]) parseNextToken(cmapStream);
                            CodespaceRange range = new CodespaceRange();
                            range.setStart(startRange);
                            range.setEnd(endRange);
                            result.addCodespaceRange(range);
                        }
                    } else if (op.op.equals(BEGIN_BASE_FONT_CHAR)) {
                        Number cosCount2 = (Number) previousToken;
                        for (int j2 = 0; j2 < cosCount2.intValue(); j2++) {
                            byte[] inputCode = (byte[]) parseNextToken(cmapStream);
                            Object nextToken = parseNextToken(cmapStream);
                            if (nextToken instanceof byte[]) {
                                byte[] bytes = (byte[]) nextToken;
                                String value = createStringFromBytes(bytes);
                                result.addMapping(inputCode, value);
                            } else if (nextToken instanceof LiteralName) {
                                result.addMapping(inputCode, ((LiteralName) nextToken).name);
                            } else {
                                throw new IOException("Error parsing CMap beginbfchar, expected{COSString or COSName} and not " + nextToken);
                            }
                        }
                    } else if (op.op.equals(BEGIN_BASE_FONT_RANGE)) {
                        Number cosCount3 = (Number) previousToken;
                        for (int j3 = 0; j3 < cosCount3.intValue(); j3++) {
                            byte[] startCode = (byte[]) parseNextToken(cmapStream);
                            byte[] endCode = (byte[]) parseNextToken(cmapStream);
                            Object nextToken2 = parseNextToken(cmapStream);
                            List array = null;
                            if (nextToken2 instanceof List) {
                                array = (List) nextToken2;
                                tokenBytes = (byte[]) array.get(0);
                            } else {
                                tokenBytes = (byte[]) nextToken2;
                            }
                            int arrayIndex = 0;
                            boolean done = false;
                            while (!done) {
                                if (compare(startCode, endCode) >= 0) {
                                    done = true;
                                }
                                String value2 = createStringFromBytes(tokenBytes);
                                result.addMapping(startCode, value2);
                                increment(startCode);
                                if (array == null) {
                                    increment(tokenBytes);
                                } else {
                                    arrayIndex++;
                                    if (arrayIndex < array.size()) {
                                        tokenBytes = (byte[]) array.get(arrayIndex);
                                    }
                                }
                            }
                        }
                    }
                }
                previousToken = token;
            } else {
                return result;
            }
        }
    }

    private Object parseNextToken(PushbackInputStream is) throws IOException {
        int nextByte;
        int nextByte2;
        int intValue;
        int i;
        Object retval = null;
        int i2 = is.read();
        while (true) {
            nextByte = i2;
            if (nextByte != 9 && nextByte != 32 && nextByte != 13 && nextByte != 10) {
                break;
            }
            i2 = is.read();
        }
        switch (nextByte) {
            case -1:
                break;
            case 37:
                StringBuffer buffer = new StringBuffer();
                buffer.append((char) nextByte);
                readUntilEndOfLine(is, buffer);
                retval = buffer.toString();
                break;
            case 40:
                StringBuffer buffer2 = new StringBuffer();
                int i3 = is.read();
                while (true) {
                    int stringByte = i3;
                    if (stringByte != -1 && stringByte != 41) {
                        buffer2.append((char) stringByte);
                        i3 = is.read();
                    }
                }
                retval = buffer2.toString();
                break;
            case 47:
                StringBuffer buffer3 = new StringBuffer();
                int i4 = is.read();
                while (true) {
                    int stringByte2 = i4;
                    if (!isWhitespaceOrEOF(stringByte2)) {
                        buffer3.append((char) stringByte2);
                        i4 = is.read();
                    } else {
                        retval = new LiteralName(buffer3.toString());
                    }
                    break;
                }
                break;
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
                StringBuffer buffer4 = new StringBuffer();
                buffer4.append((char) nextByte);
                int i5 = is.read();
                while (true) {
                    nextByte2 = i5;
                    if (!isWhitespaceOrEOF(nextByte2) && (Character.isDigit((char) nextByte2) || nextByte2 == 46)) {
                        buffer4.append((char) nextByte2);
                        i5 = is.read();
                    }
                }
                is.unread(nextByte2);
                String value = buffer4.toString();
                if (value.indexOf(46) >= 0) {
                    retval = Double.valueOf(value);
                } else {
                    retval = Integer.valueOf(buffer4.toString());
                }
                break;
            case 60:
                int theNextByte = is.read();
                if (theNextByte == 60) {
                    Map result = new LinkedHashMap();
                    Object nextToken = parseNextToken(is);
                    while (true) {
                        Object key = nextToken;
                        if ((key instanceof LiteralName) && key != MARK_END_OF_DICTIONARY) {
                            result.put(((LiteralName) key).name, parseNextToken(is));
                            nextToken = parseNextToken(is);
                        }
                    }
                    retval = result;
                } else {
                    int multiplyer = 16;
                    int bufferIndex = -1;
                    while (theNextByte != -1 && theNextByte != 62) {
                        if (theNextByte >= 48 && theNextByte <= 57) {
                            intValue = theNextByte - 48;
                        } else if (theNextByte >= 65 && theNextByte <= 70) {
                            intValue = (10 + theNextByte) - 65;
                        } else if (theNextByte >= 97 && theNextByte <= 102) {
                            intValue = (10 + theNextByte) - 97;
                        } else {
                            throw new IOException("Error: expected hex character and not " + ((char) theNextByte) + ":" + theNextByte);
                        }
                        int intValue2 = intValue * multiplyer;
                        if (multiplyer == 16) {
                            bufferIndex++;
                            this.tokenParserByteBuffer[bufferIndex] = 0;
                            i = 1;
                        } else {
                            i = 16;
                        }
                        multiplyer = i;
                        byte[] bArr = this.tokenParserByteBuffer;
                        int i6 = bufferIndex;
                        bArr[i6] = (byte) (bArr[i6] + intValue2);
                        theNextByte = is.read();
                    }
                    byte[] finalResult = new byte[bufferIndex + 1];
                    System.arraycopy(this.tokenParserByteBuffer, 0, finalResult, 0, bufferIndex + 1);
                    retval = finalResult;
                }
                break;
            case 62:
                int secondCloseBrace = is.read();
                if (secondCloseBrace == 62) {
                    retval = MARK_END_OF_DICTIONARY;
                } else {
                    throw new IOException("Error: expected the end of a dictionary.");
                }
                break;
            case 91:
                List list = new ArrayList();
                Object nextToken2 = parseNextToken(is);
                while (true) {
                    Object nextToken3 = nextToken2;
                    if (nextToken3 != MARK_END_OF_ARRAY) {
                        list.add(nextToken3);
                        nextToken2 = parseNextToken(is);
                    } else {
                        retval = list;
                    }
                    break;
                }
                break;
            case 93:
                retval = MARK_END_OF_ARRAY;
                break;
            default:
                StringBuffer buffer5 = new StringBuffer();
                buffer5.append((char) nextByte);
                int i7 = is.read();
                while (true) {
                    int nextByte3 = i7;
                    if (!isWhitespaceOrEOF(nextByte3)) {
                        buffer5.append((char) nextByte3);
                        i7 = is.read();
                    } else {
                        retval = new Operator(buffer5.toString());
                    }
                    break;
                }
                break;
        }
        return retval;
    }

    private void readUntilEndOfLine(InputStream is, StringBuffer buf) throws IOException {
        int i = is.read();
        while (true) {
            int nextByte = i;
            if (nextByte != -1 && nextByte != 13 && nextByte != 10) {
                buf.append((char) nextByte);
                i = is.read();
            } else {
                return;
            }
        }
    }

    private boolean isWhitespaceOrEOF(int aByte) {
        return aByte == -1 || aByte == 32 || aByte == 13 || aByte == 10;
    }

    private void increment(byte[] data) {
        increment(data, data.length - 1);
    }

    private void increment(byte[] data, int position) {
        if (position > 0 && (data[position] + 256) % 256 == 255) {
            data[position] = 0;
            increment(data, position - 1);
        } else {
            data[position] = (byte) (data[position] + 1);
        }
    }

    private String createStringFromBytes(byte[] bytes) throws IOException {
        String retval;
        if (bytes.length == 1) {
            retval = new String(bytes);
        } else {
            retval = new String(bytes, CharsetNames.CS_UTF16BE);
        }
        return retval;
    }

    private int compare(byte[] first, byte[] second) {
        int retval = 1;
        boolean done = false;
        for (int i = 0; i < first.length && !done; i++) {
            if (first[i] != second[i]) {
                if ((first[i] + 256) % 256 < (second[i] + 256) % 256) {
                    done = true;
                    retval = -1;
                } else {
                    done = true;
                    retval = 1;
                }
            }
        }
        return retval;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/fonts/cmaps/CMapParser$LiteralName.class */
    private class LiteralName {
        private final String name;

        private LiteralName(String theName) {
            this.name = theName;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/fonts/cmaps/CMapParser$Operator.class */
    private class Operator {
        private final String op;

        private Operator(String theOp) {
            this.op = theOp;
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("usage: java org.pdfbox.cmapparser.CMapParser <CMAP File>");
            System.exit(-1);
        }
        CMapParser parser = new CMapParser();
        CMap result = parser.parse(new FileInputStream(args[0]));
        System.out.println("Result:" + result);
    }
}
