package org.apache.fontbox.type1;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/type1/Type1Lexer.class */
class Type1Lexer {
    private static final Log LOG = LogFactory.getLog(Type1Lexer.class);
    private final ByteBuffer buffer;
    private int openParens = 0;
    private Token aheadToken = readToken(null);

    Type1Lexer(byte[] bytes) throws IOException {
        this.buffer = ByteBuffer.wrap(bytes);
    }

    public Token nextToken() throws IOException {
        Token curToken = this.aheadToken;
        this.aheadToken = readToken(curToken);
        return curToken;
    }

    public Token peekToken() {
        return this.aheadToken;
    }

    private char getChar() {
        return (char) this.buffer.get();
    }

    private Token readToken(Token prevToken) throws IOException {
        boolean skip;
        do {
            skip = false;
            while (this.buffer.hasRemaining()) {
                char c = getChar();
                if (c == '%') {
                    readComment();
                } else {
                    if (c == '(') {
                        return readString();
                    }
                    if (c == ')') {
                        throw new IOException("unexpected closing parenthesis");
                    }
                    if (c == '[') {
                        return new Token(c, Token.START_ARRAY);
                    }
                    if (c == '{') {
                        return new Token(c, Token.START_PROC);
                    }
                    if (c == ']') {
                        return new Token(c, Token.END_ARRAY);
                    }
                    if (c == '}') {
                        return new Token(c, Token.END_PROC);
                    }
                    if (c == '/') {
                        return new Token(readRegular(), Token.LITERAL);
                    }
                    if (c == '<') {
                        char c2 = getChar();
                        if (c2 == c) {
                            return new Token("<<", Token.START_DICT);
                        }
                        this.buffer.position(this.buffer.position() - 1);
                        return new Token(c, Token.NAME);
                    }
                    if (c == '>') {
                        char c3 = getChar();
                        if (c3 == c) {
                            return new Token(">>", Token.END_DICT);
                        }
                        this.buffer.position(this.buffer.position() - 1);
                        return new Token(c, Token.NAME);
                    }
                    if (Character.isWhitespace(c)) {
                        skip = true;
                    } else if (c == 0) {
                        LOG.warn("NULL byte in font, skipped");
                        skip = true;
                    } else {
                        this.buffer.position(this.buffer.position() - 1);
                        Token number = tryReadNumber();
                        if (number != null) {
                            return number;
                        }
                        String name = readRegular();
                        if (name == null) {
                            throw new DamagedFontException("Could not read token at position " + this.buffer.position());
                        }
                        if (name.equals("RD") || name.equals("-|")) {
                            if (prevToken != null && prevToken.getKind() == Token.INTEGER) {
                                return readCharString(prevToken.intValue());
                            }
                            throw new IOException("expected INTEGER before -| or RD");
                        }
                        return new Token(name, Token.NAME);
                    }
                }
            }
        } while (skip);
        return null;
    }

    private Token tryReadNumber() {
        this.buffer.mark();
        StringBuilder sb = new StringBuilder();
        StringBuilder radix = null;
        char c = getChar();
        boolean hasDigit = false;
        if (c == '+' || c == '-') {
            sb.append(c);
            c = getChar();
        }
        while (Character.isDigit(c)) {
            sb.append(c);
            c = getChar();
            hasDigit = true;
        }
        if (c == '.') {
            sb.append(c);
            c = getChar();
        } else if (c == '#') {
            radix = sb;
            sb = new StringBuilder();
            c = getChar();
        } else {
            if (sb.length() == 0 || !hasDigit) {
                this.buffer.reset();
                return null;
            }
            if (c != 'e' && c != 'E') {
                this.buffer.position(this.buffer.position() - 1);
                return new Token(sb.toString(), Token.INTEGER);
            }
        }
        if (Character.isDigit(c)) {
            sb.append(c);
            c = getChar();
        } else if (c != 'e' && c != 'E') {
            this.buffer.reset();
            return null;
        }
        while (Character.isDigit(c)) {
            sb.append(c);
            c = getChar();
        }
        if (c == 'E' || c == 'e') {
            sb.append(c);
            char c2 = getChar();
            if (c2 == '-') {
                sb.append(c2);
                c2 = getChar();
            }
            if (Character.isDigit(c2)) {
                sb.append(c2);
                char c3 = getChar();
                while (true) {
                    char c4 = c3;
                    if (!Character.isDigit(c4)) {
                        break;
                    }
                    sb.append(c4);
                    c3 = getChar();
                }
            } else {
                this.buffer.reset();
                return null;
            }
        }
        this.buffer.position(this.buffer.position() - 1);
        if (radix != null) {
            Integer val = Integer.valueOf(Integer.parseInt(sb.toString(), Integer.parseInt(radix.toString())));
            return new Token(val.toString(), Token.INTEGER);
        }
        return new Token(sb.toString(), Token.REAL);
    }

    private String readRegular() {
        StringBuilder sb = new StringBuilder();
        while (this.buffer.hasRemaining()) {
            this.buffer.mark();
            char c = getChar();
            if (Character.isWhitespace(c) || c == '(' || c == ')' || c == '<' || c == '>' || c == '[' || c == ']' || c == '{' || c == '}' || c == '/' || c == '%') {
                this.buffer.reset();
                break;
            }
            sb.append(c);
        }
        if (sb.length() == 0) {
            return null;
        }
        return sb.toString();
    }

    private String readComment() {
        char c;
        StringBuilder sb = new StringBuilder();
        while (this.buffer.hasRemaining() && (c = getChar()) != '\r' && c != '\n') {
            sb.append(c);
        }
        return sb.toString();
    }

    private Token readString() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (this.buffer.hasRemaining()) {
            char c = getChar();
            switch (c) {
                case '\n':
                case '\r':
                    sb.append("\n");
                    break;
                case '(':
                    this.openParens++;
                    sb.append('(');
                    break;
                case ')':
                    if (this.openParens == 0) {
                        return new Token(sb.toString(), Token.STRING);
                    }
                    sb.append(')');
                    this.openParens--;
                    break;
                    break;
                case '\\':
                    char c1 = getChar();
                    switch (c1) {
                        case '(':
                            sb.append('(');
                            break;
                        case ')':
                            sb.append(')');
                            break;
                        case '\\':
                            sb.append('\\');
                            break;
                        case 'b':
                            sb.append('\b');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'n':
                        case 'r':
                            sb.append("\n");
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                    }
                    if (!Character.isDigit(c1)) {
                        continue;
                    } else {
                        String num = String.valueOf(new char[]{c1, getChar(), getChar()});
                        try {
                            int code = Integer.parseInt(num, 8);
                            sb.append((char) code);
                        } catch (NumberFormatException ex) {
                            throw new IOException(ex);
                        }
                    }
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return null;
    }

    private Token readCharString(int length) {
        this.buffer.get();
        byte[] data = new byte[length];
        this.buffer.get(data);
        return new Token(data, Token.CHARSTRING);
    }
}
