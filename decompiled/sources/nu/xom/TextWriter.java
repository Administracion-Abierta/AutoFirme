package nu.xom;

import java.io.IOException;
import java.io.Writer;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/TextWriter.class */
abstract class TextWriter {
    protected final Writer out;
    protected final String encoding;
    private char highSurrogate;
    private static final String _128_SPACES = "                                                                                                                                ";
    private static final int _128 = 128;
    private String lineSeparator = "\r\n";
    boolean lineSeparatorSet = false;
    private boolean inDocType = false;
    private int maxLength = 0;
    private int indent = 0;
    private String indentString = "";
    protected int column = 0;
    private boolean preserveSpace = false;
    protected boolean normalize = false;
    protected boolean lastCharacterWasSpace = false;
    protected boolean skipFollowingLinefeed = false;
    protected boolean justBroke = false;
    private int fakeIndents = 0;

    protected TextWriter(Writer writer, String str) {
        this.out = writer;
        this.encoding = str;
    }

    void reset() {
        this.column = 0;
        this.fakeIndents = 0;
        this.lastCharacterWasSpace = false;
        this.skipFollowingLinefeed = false;
    }

    private boolean isHighSurrogate(int i) {
        return i >= 55296 && i <= 56319;
    }

    private boolean isLowSurrogate(int i) {
        return i >= 56320 && i <= 57343;
    }

    final void writePCDATA(char c) throws IOException {
        switch (c) {
            case '\r':
                if (adjustingWhiteSpace() || this.lineSeparatorSet) {
                    breakLine();
                    this.lastCharacterWasSpace = true;
                } else {
                    this.out.write("&#x0D;");
                    this.column += 6;
                    this.justBroke = false;
                }
                this.skipFollowingLinefeed = true;
                return;
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
                throw new XMLException("Bad character snuck into document");
            case ' ':
                write(c);
                return;
            case '!':
                write(c);
                return;
            case '\"':
                write(c);
                return;
            case '#':
                write(c);
                return;
            case '$':
                write(c);
                return;
            case '%':
                write(c);
                return;
            case '&':
                this.out.write("&amp;");
                this.column += 5;
                this.lastCharacterWasSpace = false;
                this.skipFollowingLinefeed = false;
                this.justBroke = false;
                return;
            case '\'':
                write(c);
                return;
            case '(':
                write(c);
                return;
            case ')':
                write(c);
                return;
            case '*':
                write(c);
                return;
            case '+':
                write(c);
                return;
            case ',':
                write(c);
                return;
            case '-':
                write(c);
                return;
            case '.':
                write(c);
                return;
            case '/':
                write(c);
                return;
            case '0':
                write(c);
                return;
            case '1':
                write(c);
                return;
            case '2':
                write(c);
                return;
            case '3':
                write(c);
                return;
            case '4':
                write(c);
                return;
            case '5':
                write(c);
                return;
            case '6':
                write(c);
                return;
            case '7':
                write(c);
                return;
            case '8':
                write(c);
                return;
            case '9':
                write(c);
                return;
            case ':':
                write(c);
                return;
            case ';':
                write(c);
                return;
            case '<':
                this.out.write("&lt;");
                this.column += 4;
                this.lastCharacterWasSpace = false;
                this.skipFollowingLinefeed = false;
                this.justBroke = false;
                return;
            case '=':
                write(c);
                return;
            case '>':
                this.out.write("&gt;");
                this.column += 4;
                this.lastCharacterWasSpace = false;
                this.skipFollowingLinefeed = false;
                this.justBroke = false;
                return;
            default:
                if (needsEscaping(c)) {
                    writeEscapedChar(c);
                    return;
                } else {
                    write(c);
                    return;
                }
        }
    }

    private void writeEscapedChar(char c) throws IOException {
        if (isHighSurrogate(c)) {
            this.highSurrogate = c;
            return;
        }
        if (!isLowSurrogate(c)) {
            String string = new StringBuffer().append("&#x").append(Integer.toHexString(c).toUpperCase()).append(';').toString();
            this.out.write(string);
            this.column += string.length();
            this.lastCharacterWasSpace = false;
            this.skipFollowingLinefeed = false;
            this.justBroke = false;
            return;
        }
        String string2 = new StringBuffer().append("&#x").append(Integer.toHexString(UnicodeUtil.combineSurrogatePair(this.highSurrogate, c)).toUpperCase()).append(';').toString();
        this.out.write(string2);
        this.column += string2.length();
        this.lastCharacterWasSpace = false;
        this.skipFollowingLinefeed = false;
        this.justBroke = false;
    }

    private boolean adjustingWhiteSpace() {
        return this.maxLength > 0 || this.indent > 0;
    }

    final void writeAttributeValue(char c) throws IOException {
        switch (c) {
            case '\t':
                if (adjustingWhiteSpace()) {
                    write(' ');
                    return;
                }
                this.out.write("&#x09;");
                this.column += 6;
                this.lastCharacterWasSpace = true;
                this.skipFollowingLinefeed = false;
                this.justBroke = false;
                return;
            case '\n':
                if (this.skipFollowingLinefeed) {
                    this.skipFollowingLinefeed = false;
                    return;
                }
                if (adjustingWhiteSpace()) {
                    this.out.write(" ");
                    this.lastCharacterWasSpace = true;
                    this.justBroke = false;
                    return;
                } else {
                    if (this.lineSeparatorSet) {
                        escapeBreakLine();
                    } else {
                        this.out.write("&#x0A;");
                        this.column += 6;
                        this.justBroke = false;
                    }
                    this.lastCharacterWasSpace = true;
                    return;
                }
            case 11:
            case '\f':
                throw new XMLException("Bad character snuck into document");
            case '\r':
                if (adjustingWhiteSpace()) {
                    this.out.write(" ");
                    this.lastCharacterWasSpace = true;
                    this.skipFollowingLinefeed = true;
                    this.justBroke = false;
                    return;
                }
                if (this.lineSeparatorSet) {
                    escapeBreakLine();
                    this.skipFollowingLinefeed = true;
                    return;
                } else {
                    this.out.write("&#x0D;");
                    this.column += 6;
                    this.justBroke = false;
                    return;
                }
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
                throw new XMLException("Bad character snuck into document");
            case ' ':
                write(c);
                return;
            case '!':
                write(c);
                return;
            case '\"':
                this.out.write("&quot;");
                this.column += 6;
                this.lastCharacterWasSpace = false;
                this.skipFollowingLinefeed = false;
                this.justBroke = false;
                return;
            case '#':
                write(c);
                return;
            case '$':
                write(c);
                return;
            case '%':
                write(c);
                return;
            case '&':
                this.out.write("&amp;");
                this.column += 5;
                this.lastCharacterWasSpace = false;
                this.skipFollowingLinefeed = false;
                this.justBroke = false;
                return;
            case '\'':
                write(c);
                return;
            case '(':
                write(c);
                return;
            case ')':
                write(c);
                return;
            case '*':
                write(c);
                return;
            case '+':
                write(c);
                return;
            case ',':
                write(c);
                return;
            case '-':
                write(c);
                return;
            case '.':
                write(c);
                return;
            case '/':
                write(c);
                return;
            case '0':
                write(c);
                return;
            case '1':
                write(c);
                return;
            case '2':
                write(c);
                return;
            case '3':
                write(c);
                return;
            case '4':
                write(c);
                return;
            case '5':
                write(c);
                return;
            case '6':
                write(c);
                return;
            case '7':
                write(c);
                return;
            case '8':
                write(c);
                return;
            case '9':
                write(c);
                return;
            case ':':
                write(c);
                return;
            case ';':
                write(c);
                return;
            case '<':
                this.out.write("&lt;");
                this.column += 4;
                this.lastCharacterWasSpace = false;
                this.skipFollowingLinefeed = false;
                this.justBroke = false;
                return;
            case '=':
                write(c);
                return;
            case '>':
                this.out.write("&gt;");
                this.column += 4;
                this.lastCharacterWasSpace = false;
                this.skipFollowingLinefeed = false;
                this.justBroke = false;
                return;
            default:
                if (needsEscaping(c)) {
                    writeEscapedChar(c);
                    return;
                } else {
                    write(c);
                    return;
                }
        }
    }

    void write(char c) throws IOException {
        if (c != ' ' && c != '\n' && c != '\t') {
            this.out.write(c);
            if (c < 55296 || c > 56319) {
                this.column++;
            }
            this.lastCharacterWasSpace = false;
            this.skipFollowingLinefeed = false;
            this.justBroke = false;
            return;
        }
        if (needsBreak()) {
            breakLine();
            this.skipFollowingLinefeed = false;
        } else if (this.preserveSpace || (this.indent <= 0 && this.maxLength <= 0)) {
            if (c == ' ' || c == '\t') {
                this.out.write(c);
                this.skipFollowingLinefeed = false;
                this.column++;
                this.justBroke = false;
            } else {
                if (!this.lineSeparatorSet || !this.skipFollowingLinefeed) {
                    writeLineSeparator(c);
                }
                this.skipFollowingLinefeed = false;
                this.column = 0;
            }
        } else if (!this.lastCharacterWasSpace) {
            this.out.write(32);
            this.column++;
            this.skipFollowingLinefeed = false;
            this.justBroke = false;
        }
        this.lastCharacterWasSpace = true;
    }

    private void writeLineSeparator(char c) throws IOException {
        if (!this.inDocType && (!this.lineSeparatorSet || this.preserveSpace)) {
            this.out.write(c);
            return;
        }
        if (this.lineSeparator.equals("\r\n")) {
            this.out.write("\r\n");
        } else if (this.lineSeparator.equals("\n")) {
            this.out.write(10);
        } else {
            this.out.write(13);
        }
    }

    private boolean needsBreak() {
        return this.maxLength > 0 && !this.preserveSpace && this.column >= this.maxLength - 10;
    }

    boolean justBroke() {
        return this.justBroke;
    }

    final void breakLine() throws IOException {
        this.out.write(this.lineSeparator);
        this.out.write(this.indentString);
        this.column = this.indentString.length();
        this.lastCharacterWasSpace = true;
        this.justBroke = true;
    }

    private final void escapeBreakLine() throws IOException {
        if ("\n".equals(this.lineSeparator)) {
            this.out.write("&#x0A;");
            this.column += 6;
        } else if ("\r\n".equals(this.lineSeparator)) {
            this.out.write("&#x0D;&#x0A;");
            this.column += 12;
        } else {
            this.out.write("&#x0D;");
            this.column += 6;
        }
        this.lastCharacterWasSpace = true;
    }

    final void writeMarkup(char c) throws IOException {
        if (needsEscaping(c)) {
            throw new UnavailableCharacterException(c, this.encoding);
        }
        write(c);
    }

    void writePCDATA(String str) throws IOException {
        String strNormalize = normalize(str);
        int length = strNormalize.length();
        for (int i = 0; i < length; i++) {
            writePCDATA(strNormalize.charAt(i));
        }
    }

    void writeAttributeValue(String str) throws IOException {
        String strNormalize = normalize(str);
        int length = strNormalize.length();
        for (int i = 0; i < length; i++) {
            writeAttributeValue(strNormalize.charAt(i));
        }
    }

    void writeMarkup(String str) throws IOException {
        String strNormalize = normalize(str);
        int length = strNormalize.length();
        for (int i = 0; i < length; i++) {
            writeMarkup(strNormalize.charAt(i));
        }
    }

    void writeUncheckedMarkup(String str) throws IOException {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            write(str.charAt(i));
        }
    }

    protected String normalize(String str) {
        return this.normalize ? UnicodeUtil.normalize(str) : str;
    }

    boolean isIndenting() {
        return this.indentString.length() > 0;
    }

    void incrementIndent() {
        String string;
        if (this.indent == 0) {
            return;
        }
        int length = this.indentString.length() + this.indent;
        if (this.indentString.length() + this.indent < 128) {
            string = _128_SPACES.substring(0, length);
        } else {
            StringBuffer stringBuffer = new StringBuffer(length);
            stringBuffer.append(_128_SPACES);
            for (int i = 128; i < length; i++) {
                stringBuffer.append(' ');
            }
            string = stringBuffer.toString();
        }
        if (this.maxLength <= 0 || string.length() <= this.maxLength / 2) {
            this.indentString = string;
        } else {
            this.fakeIndents++;
        }
    }

    void decrementIndent() {
        if (this.indent == 0) {
            return;
        }
        if (this.fakeIndents > 0) {
            this.fakeIndents--;
        } else {
            this.indentString = this.indentString.substring(0, this.indentString.length() - this.indent);
        }
    }

    String getEncoding() {
        return this.encoding;
    }

    String getLineSeparator() {
        return this.lineSeparator;
    }

    void setLineSeparator(String str) {
        if (!str.equals("\n") && !str.equals("\r") && !str.equals("\r\n")) {
            throw new IllegalArgumentException("Illegal Line Separator");
        }
        this.lineSeparator = str;
        this.lineSeparatorSet = true;
    }

    void setInDocType(boolean z) {
        this.inDocType = z;
    }

    int getIndent() {
        return this.indent;
    }

    int getMaxLength() {
        return this.maxLength;
    }

    void setMaxLength(int i) {
        if (i < 0) {
            i = 0;
        }
        this.maxLength = i;
    }

    void setIndent(int i) {
        this.indent = i;
    }

    void flush() throws IOException {
        this.out.flush();
    }

    abstract boolean needsEscaping(char c);

    boolean isPreserveSpace() {
        return this.preserveSpace;
    }

    void setPreserveSpace(boolean z) {
        this.preserveSpace = z;
    }

    int getColumnNumber() {
        return this.column;
    }

    void setNFC(boolean z) {
        this.normalize = z;
    }

    boolean getNFC() {
        return this.normalize;
    }

    void writeName(String str) throws IOException {
        writeMarkup(str);
    }
}
