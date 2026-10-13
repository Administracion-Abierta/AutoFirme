package org.apache.fontbox.cmap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fontbox.util.Charsets;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cmap/CMapParser.class */
public class CMapParser {
    private static final String MARK_END_OF_DICTIONARY = ">>";
    private static final String MARK_END_OF_ARRAY = "]";
    private final byte[] tokenParserByteBuffer;
    private boolean strictMode;

    public CMapParser() {
        this.tokenParserByteBuffer = new byte[512];
        this.strictMode = false;
    }

    public CMapParser(boolean strictMode) {
        this.tokenParserByteBuffer = new byte[512];
        this.strictMode = false;
        this.strictMode = strictMode;
    }

    public CMap parse(File file) throws IOException {
        FileInputStream input = null;
        try {
            FileInputStream input2 = new FileInputStream(file);
            CMap cMap = parse(input2);
            if (input2 != null) {
            }
            return cMap;
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    public CMap parsePredefined(String name) throws IOException {
        InputStream input = null;
        try {
            InputStream input2 = getExternalCMap(name);
            this.strictMode = false;
            CMap cMap = parse(input2);
            if (input2 != null) {
            }
            return cMap;
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    public CMap parse(InputStream input) throws IOException {
        PushbackInputStream cmapStream = new PushbackInputStream(input);
        CMap result = new CMap();
        Object obj = null;
        while (true) {
            Object previousToken = obj;
            Object token = parseNextToken(cmapStream);
            if (token == null) {
                break;
            }
            if (token instanceof Operator) {
                Operator op = (Operator) token;
                if (op.op.equals("endcmap")) {
                    break;
                }
                if (previousToken != null) {
                    if (op.op.equals("usecmap") && (previousToken instanceof LiteralName)) {
                        parseUsecmap((LiteralName) previousToken, result);
                    } else if (previousToken instanceof Number) {
                        if (op.op.equals("begincodespacerange")) {
                            parseBegincodespacerange((Number) previousToken, cmapStream, result);
                        } else if (op.op.equals("beginbfchar")) {
                            parseBeginbfchar((Number) previousToken, cmapStream, result);
                        } else if (op.op.equals("beginbfrange")) {
                            parseBeginbfrange((Number) previousToken, cmapStream, result);
                        } else if (op.op.equals("begincidchar")) {
                            parseBegincidchar((Number) previousToken, cmapStream, result);
                        } else if (op.op.equals("begincidrange") && (previousToken instanceof Integer)) {
                            parseBegincidrange(((Integer) previousToken).intValue(), cmapStream, result);
                        }
                    }
                }
            } else if (token instanceof LiteralName) {
                parseLiteralName((LiteralName) token, cmapStream, result);
            }
            obj = token;
        }
        return result;
    }

    private void parseUsecmap(LiteralName useCmapName, CMap result) throws IOException {
        InputStream useStream = getExternalCMap(useCmapName.name);
        CMap useCMap = parse(useStream);
        result.useCmap(useCMap);
    }

    private void parseLiteralName(LiteralName literal, PushbackInputStream cmapStream, CMap result) throws IOException {
        if ("WMode".equals(literal.name)) {
            Object next = parseNextToken(cmapStream);
            if (next instanceof Integer) {
                result.setWMode(((Integer) next).intValue());
                return;
            }
            return;
        }
        if ("CMapName".equals(literal.name)) {
            Object next2 = parseNextToken(cmapStream);
            if (next2 instanceof LiteralName) {
                result.setName(((LiteralName) next2).name);
                return;
            }
            return;
        }
        if ("CMapVersion".equals(literal.name)) {
            Object next3 = parseNextToken(cmapStream);
            if (next3 instanceof Number) {
                result.setVersion(next3.toString());
                return;
            } else {
                if (next3 instanceof String) {
                    result.setVersion((String) next3);
                    return;
                }
                return;
            }
        }
        if ("CMapType".equals(literal.name)) {
            Object next4 = parseNextToken(cmapStream);
            if (next4 instanceof Integer) {
                result.setType(((Integer) next4).intValue());
                return;
            }
            return;
        }
        if ("Registry".equals(literal.name)) {
            Object next5 = parseNextToken(cmapStream);
            if (next5 instanceof String) {
                result.setRegistry((String) next5);
                return;
            }
            return;
        }
        if ("Ordering".equals(literal.name)) {
            Object next6 = parseNextToken(cmapStream);
            if (next6 instanceof String) {
                result.setOrdering((String) next6);
                return;
            }
            return;
        }
        if ("Supplement".equals(literal.name)) {
            Object next7 = parseNextToken(cmapStream);
            if (next7 instanceof Integer) {
                result.setSupplement(((Integer) next7).intValue());
            }
        }
    }

    private void checkExpectedOperator(Operator operator, String expectedOperatorName, String rangeName) throws IOException {
        if (!operator.op.equals(expectedOperatorName)) {
            throw new IOException("Error : ~" + rangeName + " contains an unexpected operator : " + operator.op);
        }
    }

    private void parseBegincodespacerange(Number cosCount, PushbackInputStream cmapStream, CMap result) throws IOException {
        for (int j = 0; j < cosCount.intValue(); j++) {
            Object nextToken = parseNextToken(cmapStream);
            if (nextToken instanceof Operator) {
                checkExpectedOperator((Operator) nextToken, "endcodespacerange", "codespacerange");
                return;
            }
            byte[] startRange = (byte[]) nextToken;
            byte[] endRange = (byte[]) parseNextToken(cmapStream);
            try {
                result.addCodespaceRange(new CodespaceRange(startRange, endRange));
            } catch (IllegalArgumentException ex) {
                throw new IOException(ex);
            }
        }
    }

    private void parseBeginbfchar(Number cosCount, PushbackInputStream cmapStream, CMap result) throws IOException {
        for (int j = 0; j < cosCount.intValue(); j++) {
            Object nextToken = parseNextToken(cmapStream);
            if (nextToken instanceof Operator) {
                checkExpectedOperator((Operator) nextToken, "endbfchar", "bfchar");
                return;
            }
            byte[] inputCode = (byte[]) nextToken;
            Object nextToken2 = parseNextToken(cmapStream);
            if (nextToken2 instanceof byte[]) {
                byte[] bytes = (byte[]) nextToken2;
                String value = createStringFromBytes(bytes);
                result.addCharMapping(inputCode, value);
            } else if (nextToken2 instanceof LiteralName) {
                result.addCharMapping(inputCode, ((LiteralName) nextToken2).name);
            } else {
                throw new IOException("Error parsing CMap beginbfchar, expected{COSString or COSName} and not " + nextToken2);
            }
        }
    }

    private void parseBegincidrange(int numberOfLines, PushbackInputStream cmapStream, CMap result) throws IOException {
        for (int n = 0; n < numberOfLines; n++) {
            Object nextToken = parseNextToken(cmapStream);
            if (nextToken instanceof Operator) {
                checkExpectedOperator((Operator) nextToken, "endcidrange", "cidrange");
                return;
            }
            byte[] startCode = (byte[]) nextToken;
            int start = createIntFromBytes(startCode);
            byte[] endCode = (byte[]) parseNextToken(cmapStream);
            int end = createIntFromBytes(endCode);
            int mappedCode = ((Integer) parseNextToken(cmapStream)).intValue();
            if (startCode.length <= 2 && endCode.length <= 2) {
                if (end == start) {
                    result.addCIDMapping(mappedCode, start);
                } else {
                    result.addCIDRange((char) start, (char) end, mappedCode);
                }
            } else {
                int endOfMappings = (mappedCode + end) - start;
                while (mappedCode <= endOfMappings) {
                    int mappedCID = createIntFromBytes(startCode);
                    int i = mappedCode;
                    mappedCode++;
                    result.addCIDMapping(i, mappedCID);
                    increment(startCode, startCode.length - 1, false);
                }
            }
        }
    }

    private void parseBegincidchar(Number cosCount, PushbackInputStream cmapStream, CMap result) throws IOException {
        for (int j = 0; j < cosCount.intValue(); j++) {
            Object nextToken = parseNextToken(cmapStream);
            if (nextToken instanceof Operator) {
                checkExpectedOperator((Operator) nextToken, "endcidchar", "cidchar");
                return;
            }
            byte[] inputCode = (byte[]) nextToken;
            int mappedCode = ((Integer) parseNextToken(cmapStream)).intValue();
            int mappedCID = createIntFromBytes(inputCode);
            result.addCIDMapping(mappedCode, mappedCID);
        }
    }

    private void parseBeginbfrange(Number cosCount, PushbackInputStream cmapStream, CMap result) throws IOException {
        for (int j = 0; j < cosCount.intValue(); j++) {
            Object nextToken = parseNextToken(cmapStream);
            if (nextToken instanceof Operator) {
                checkExpectedOperator((Operator) nextToken, "endbfrange", "bfrange");
                return;
            }
            byte[] startCode = (byte[]) nextToken;
            byte[] endCode = (byte[]) parseNextToken(cmapStream);
            int start = CMap.toInt(startCode, startCode.length);
            int end = CMap.toInt(endCode, endCode.length);
            if (end >= start) {
                Object nextToken2 = parseNextToken(cmapStream);
                if (nextToken2 instanceof List) {
                    List<byte[]> array = (List) nextToken2;
                    if (!array.isEmpty() && array.size() >= end - start) {
                        addMappingFrombfrange(result, startCode, array);
                    }
                } else if (nextToken2 instanceof byte[]) {
                    byte[] tokenBytes = (byte[]) nextToken2;
                    if (tokenBytes.length > 0) {
                        if (tokenBytes.length == 2 && start == 0 && end == 65535 && tokenBytes[0] == 0 && tokenBytes[1] == 0) {
                            for (int i = 0; i < 256; i++) {
                                startCode[0] = (byte) i;
                                startCode[1] = 0;
                                tokenBytes[0] = (byte) i;
                                tokenBytes[1] = 0;
                                addMappingFrombfrange(result, startCode, 256, tokenBytes);
                            }
                        } else {
                            addMappingFrombfrange(result, startCode, (end - start) + 1, tokenBytes);
                        }
                    }
                }
            } else {
                return;
            }
        }
    }

    private void addMappingFrombfrange(CMap cmap, byte[] startCode, List<byte[]> tokenBytesList) {
        for (byte[] tokenBytes : tokenBytesList) {
            String value = createStringFromBytes(tokenBytes);
            cmap.addCharMapping(startCode, value);
            increment(startCode, startCode.length - 1, false);
        }
    }

    private void addMappingFrombfrange(CMap cmap, byte[] startCode, int values, byte[] tokenBytes) {
        for (int i = 0; i < values; i++) {
            String value = createStringFromBytes(tokenBytes);
            cmap.addCharMapping(startCode, value);
            if (increment(tokenBytes, tokenBytes.length - 1, this.strictMode)) {
                increment(startCode, startCode.length - 1, false);
            } else {
                return;
            }
        }
    }

    protected InputStream getExternalCMap(String name) throws IOException {
        InputStream resourceAsStream = getClass().getResourceAsStream(name);
        if (resourceAsStream == null) {
            throw new IOException("Error: Could not find referenced cmap stream " + name);
        }
        return new BufferedInputStream(resourceAsStream);
    }

    private Object parseNextToken(PushbackInputStream is) throws IOException {
        int nextByte;
        int nextByte2;
        int nextByte3;
        int stringByte;
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
                StringBuilder buffer = new StringBuilder();
                buffer.append((char) nextByte);
                readUntilEndOfLine(is, buffer);
                retval = buffer.toString();
                break;
            case 40:
                StringBuilder buffer2 = new StringBuilder();
                int i3 = is.read();
                while (true) {
                    int stringByte2 = i3;
                    if (stringByte2 != -1 && stringByte2 != 41) {
                        buffer2.append((char) stringByte2);
                        i3 = is.read();
                    }
                }
                retval = buffer2.toString();
                break;
            case 47:
                StringBuilder buffer3 = new StringBuilder();
                int i4 = is.read();
                while (true) {
                    stringByte = i4;
                    if (!isWhitespaceOrEOF(stringByte) && !isDelimiter(stringByte)) {
                        buffer3.append((char) stringByte);
                        i4 = is.read();
                    }
                }
                if (isDelimiter(stringByte)) {
                    is.unread(stringByte);
                }
                retval = new LiteralName(buffer3.toString());
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
                StringBuilder buffer4 = new StringBuilder();
                buffer4.append((char) nextByte);
                int i5 = is.read();
                while (true) {
                    nextByte3 = i5;
                    if (!isWhitespaceOrEOF(nextByte3) && (Character.isDigit((char) nextByte3) || nextByte3 == 46)) {
                        buffer4.append((char) nextByte3);
                        i5 = is.read();
                    }
                }
                is.unread(nextByte3);
                String value = buffer4.toString();
                if (value.indexOf(46) >= 0) {
                    retval = Double.valueOf(value);
                } else {
                    retval = Integer.valueOf(value);
                }
                break;
            case 60:
                int theNextByte = is.read();
                if (theNextByte == 60) {
                    Map<String, Object> result = new HashMap<>();
                    Object nextToken = parseNextToken(is);
                    while (true) {
                        Object key = nextToken;
                        if ((key instanceof LiteralName) && !MARK_END_OF_DICTIONARY.equals(key)) {
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
                        } else if (isWhitespaceOrEOF(theNextByte)) {
                            theNextByte = is.read();
                        } else {
                            throw new IOException("Error: expected hex character and not " + ((char) theNextByte) + ":" + theNextByte);
                        }
                        int intValue2 = intValue * multiplyer;
                        if (multiplyer == 16) {
                            bufferIndex++;
                            if (bufferIndex >= this.tokenParserByteBuffer.length) {
                                throw new IOException("cmap token ist larger than buffer size " + this.tokenParserByteBuffer.length);
                            }
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
                List<Object> list = new ArrayList<>();
                Object nextToken2 = parseNextToken(is);
                while (true) {
                    Object nextToken3 = nextToken2;
                    if (nextToken3 != null && !MARK_END_OF_ARRAY.equals(nextToken3)) {
                        list.add(nextToken3);
                        nextToken2 = parseNextToken(is);
                    }
                }
                retval = list;
                break;
            case 93:
                retval = MARK_END_OF_ARRAY;
                break;
            default:
                StringBuilder buffer5 = new StringBuilder();
                buffer5.append((char) nextByte);
                int i7 = is.read();
                while (true) {
                    nextByte2 = i7;
                    if (!isWhitespaceOrEOF(nextByte2) && !isDelimiter(nextByte2) && !Character.isDigit(nextByte2)) {
                        buffer5.append((char) nextByte2);
                        i7 = is.read();
                    }
                }
                if (isDelimiter(nextByte2) || Character.isDigit(nextByte2)) {
                    is.unread(nextByte2);
                }
                retval = new Operator(buffer5.toString());
                break;
        }
        return retval;
    }

    private void readUntilEndOfLine(InputStream is, StringBuilder buf) throws IOException {
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

    private boolean isDelimiter(int aByte) {
        switch (aByte) {
            case 37:
            case 40:
            case 41:
            case 47:
            case 60:
            case 62:
            case 91:
            case 93:
            case 123:
            case 125:
                return true;
            default:
                return false;
        }
    }

    private boolean increment(byte[] data, int position, boolean useStrictMode) {
        if (position > 0 && (data[position] & 255) == 255) {
            if (useStrictMode) {
                return false;
            }
            data[position] = 0;
            increment(data, position - 1, useStrictMode);
            return true;
        }
        data[position] = (byte) (data[position] + 1);
        return true;
    }

    private int createIntFromBytes(byte[] bytes) {
        int intValue = bytes[0] & 255;
        if (bytes.length == 2) {
            intValue = (intValue << 8) + (bytes[1] & 255);
        }
        return intValue;
    }

    private String createStringFromBytes(byte[] bytes) {
        return new String(bytes, bytes.length == 1 ? Charsets.ISO_8859_1 : Charsets.UTF_16BE);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cmap/CMapParser$LiteralName.class */
    private static final class LiteralName {
        private String name;

        private LiteralName(String theName) {
            this.name = theName;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cmap/CMapParser$Operator.class */
    private static final class Operator {
        private String op;

        private Operator(String theOp) {
            this.op = theOp;
        }
    }
}
