package org.apache.pdfbox.pdfparser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdmodel.common.PDStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfparser/PDFStreamParser.class */
public class PDFStreamParser extends BaseParser {
    private static final Log LOG = LogFactory.getLog(PDFStreamParser.class);
    private final List<Object> streamObjects;
    private static final int MAX_BIN_CHAR_TEST_LENGTH = 10;
    private final byte[] binCharTestArr;

    @Deprecated
    public PDFStreamParser(PDStream stream) throws IOException {
        super(new InputStreamSource(stream.createInputStream()));
        this.streamObjects = new ArrayList(100);
        this.binCharTestArr = new byte[10];
    }

    @Deprecated
    public PDFStreamParser(COSStream stream) throws IOException {
        super(new InputStreamSource(stream.createInputStream()));
        this.streamObjects = new ArrayList(100);
        this.binCharTestArr = new byte[10];
    }

    public PDFStreamParser(PDContentStream contentStream) throws IOException {
        super(new InputStreamSource(contentStream.getContents()));
        this.streamObjects = new ArrayList(100);
        this.binCharTestArr = new byte[10];
    }

    public PDFStreamParser(byte[] bytes) throws IOException {
        super(new RandomAccessSource(new RandomAccessBuffer(bytes)));
        this.streamObjects = new ArrayList(100);
        this.binCharTestArr = new byte[10];
    }

    public void parse() throws IOException {
        while (true) {
            Object token = parseNextToken();
            if (token != null) {
                this.streamObjects.add(token);
            } else {
                return;
            }
        }
    }

    public List<Object> getTokens() {
        return this.streamObjects;
    }

    public Object parseNextToken() throws IOException {
        Object nextToken;
        skipSpaces();
        if (this.seqSource.isEOF()) {
            return null;
        }
        char c = (char) this.seqSource.peek();
        switch (c) {
            case '(':
                return parseCOSString();
            case ')':
            case '*':
            case ',':
            case ':':
            case ';':
            case '=':
            case '>':
            case '?':
            case '@':
            case 'A':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '\\':
            case '^':
            case '_':
            case '`':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            default:
                String operator = readOperator().trim();
                if (operator.length() > 0) {
                    return Operator.getOperator(operator);
                }
                return null;
            case '+':
            case '-':
            case '.':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                StringBuilder buf = new StringBuilder();
                buf.append(c);
                this.seqSource.read();
                if (c == '-' && this.seqSource.peek() == c) {
                    this.seqSource.read();
                }
                boolean dotNotRead = c != '.';
                while (true) {
                    char c2 = (char) this.seqSource.peek();
                    if (Character.isDigit(c2) || ((dotNotRead && c2 == '.') || c2 == '-')) {
                        if (c2 != '-') {
                            buf.append(c2);
                        }
                        this.seqSource.read();
                        if (dotNotRead && c2 == '.') {
                            dotNotRead = false;
                        }
                    } else {
                        return COSNumber.get(buf.toString());
                    }
                }
                break;
            case '/':
                return parseCOSName();
            case '<':
                int leftBracket = this.seqSource.read();
                char c3 = (char) this.seqSource.peek();
                this.seqSource.unread(leftBracket);
                if (c3 == '<') {
                    return parseCOSDictionary();
                }
                return parseCOSString();
            case 'B':
                String nextOperator = readString();
                Operator beginImageOP = Operator.getOperator(nextOperator);
                if (nextOperator.equals(OperatorName.BEGIN_INLINE_IMAGE)) {
                    COSDictionary imageParams = new COSDictionary();
                    beginImageOP.setImageParameters(imageParams);
                    while (true) {
                        nextToken = parseNextToken();
                        if (nextToken instanceof COSName) {
                            Object value = parseNextToken();
                            if (!(value instanceof COSBase)) {
                                LOG.warn("Unexpected token in inline image dictionary at offset " + this.seqSource.getPosition());
                            } else {
                                imageParams.setItem((COSName) nextToken, (COSBase) value);
                            }
                        }
                    }
                    if (nextToken instanceof Operator) {
                        Operator imageData = (Operator) nextToken;
                        if (imageData.getImageData() == null || imageData.getImageData().length == 0) {
                            LOG.warn("empty inline image at stream offset " + this.seqSource.getPosition());
                        }
                        beginImageOP.setImageData(imageData.getImageData());
                    }
                }
                return beginImageOP;
            case 'I':
                String id = Character.toString((char) this.seqSource.read()) + ((char) this.seqSource.read());
                if (!id.equals("ID")) {
                    throw new IOException("Error: Expected operator 'ID' actual='" + id + "' at stream offset " + this.seqSource.getPosition());
                }
                ByteArrayOutputStream imageData2 = new ByteArrayOutputStream();
                if (isWhitespace()) {
                    this.seqSource.read();
                }
                int lastByte = this.seqSource.read();
                int i = this.seqSource.read();
                while (true) {
                    int currentByte = i;
                    if ((lastByte != 69 || currentByte != 73 || !hasNextSpaceOrReturn() || !hasNoFollowingBinData(this.seqSource)) && !this.seqSource.isEOF()) {
                        imageData2.write(lastByte);
                        lastByte = currentByte;
                        i = this.seqSource.read();
                    }
                }
                Operator beginImageDataOP = Operator.getOperator("ID");
                beginImageDataOP.setImageData(imageData2.toByteArray());
                return beginImageDataOP;
            case '[':
                return parseCOSArray();
            case ']':
                this.seqSource.read();
                return COSNull.NULL;
            case 'f':
            case 't':
                String next = readString();
                if (next.equals("true")) {
                    return COSBoolean.TRUE;
                }
                if (next.equals("false")) {
                    return COSBoolean.FALSE;
                }
                return Operator.getOperator(next);
            case 'n':
                String nullString = readString();
                if (nullString.equals("null")) {
                    return COSNull.NULL;
                }
                return Operator.getOperator(nullString);
        }
    }

    private boolean hasNoFollowingBinData(SequentialSource pdfSource) throws IOException {
        int readBytes = pdfSource.read(this.binCharTestArr, 0, 10);
        boolean noBinData = true;
        int startOpIdx = -1;
        int endOpIdx = -1;
        if (readBytes > 0) {
            for (int bIdx = 0; bIdx < readBytes; bIdx++) {
                byte b = this.binCharTestArr[bIdx];
                if ((b != 0 && b < 9) || (b > 10 && b < 32 && b != 13)) {
                    noBinData = false;
                    break;
                }
                if (startOpIdx == -1 && b != 0 && b != 9 && b != 32 && b != 10 && b != 13) {
                    startOpIdx = bIdx;
                } else if (startOpIdx != -1 && endOpIdx == -1 && (b == 0 || b == 9 || b == 32 || b == 10 || b == 13)) {
                    endOpIdx = bIdx;
                }
            }
            if (endOpIdx != -1 && startOpIdx != -1) {
                String s = new String(this.binCharTestArr, startOpIdx, endOpIdx - startOpIdx);
                if (!"Q".equals(s) && !OperatorName.END_MARKED_CONTENT.equals(s) && !"S".equals(s)) {
                    noBinData = false;
                }
            }
            if (readBytes == 10) {
                if (startOpIdx != -1 && endOpIdx == -1) {
                    endOpIdx = 10;
                }
                if (endOpIdx != -1 && startOpIdx != -1 && endOpIdx - startOpIdx > 3) {
                    noBinData = false;
                }
            }
            pdfSource.unread(this.binCharTestArr, 0, readBytes);
        }
        if (!noBinData) {
            LOG.warn("ignoring 'EI' assumed to be in the middle of inline image at stream offset " + pdfSource.getPosition());
        }
        return noBinData;
    }

    protected String readOperator() throws IOException {
        skipSpaces();
        StringBuilder buffer = new StringBuilder(4);
        int nextChar = this.seqSource.peek();
        while (nextChar != -1 && !isWhitespace(nextChar) && !isClosing(nextChar) && nextChar != 91 && nextChar != 60 && nextChar != 40 && nextChar != 47 && (nextChar < 48 || nextChar > 57)) {
            char currentChar = (char) this.seqSource.read();
            nextChar = this.seqSource.peek();
            buffer.append(currentChar);
            if (currentChar == 'd' && (nextChar == 48 || nextChar == 49)) {
                buffer.append((char) this.seqSource.read());
                nextChar = this.seqSource.peek();
            }
        }
        return buffer.toString();
    }

    private boolean isSpaceOrReturn(int c) {
        return c == 10 || c == 13 || c == 32;
    }

    private boolean hasNextSpaceOrReturn() throws IOException {
        return isSpaceOrReturn(this.seqSource.peek());
    }
}
