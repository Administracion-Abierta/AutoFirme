package nu.xom.jaxen.saxpath.base;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/saxpath/base/XPathLexer.class */
class XPathLexer {
    private String xpath;
    private int currentPosition;
    private int endPosition;
    private boolean expectOperator = false;

    XPathLexer(String str) {
        setXPath(str);
    }

    private void setXPath(String str) {
        this.xpath = str;
        this.currentPosition = 0;
        this.endPosition = str.length();
    }

    String getXPath() {
        return this.xpath;
    }

    Token nextToken() {
        Token tokenIdentifierOrOperatorName;
        do {
            tokenIdentifierOrOperatorName = null;
            switch (LA(1)) {
                case '\t':
                case '\n':
                case '\r':
                case ' ':
                    tokenIdentifierOrOperatorName = whitespace();
                    break;
                case 11:
                case '\f':
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
                case '#':
                case '%':
                case '&':
                case ';':
                case '?':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
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
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                case '{':
                default:
                    if (Verifier.isXMLNCNameStartCharacter(LA(1))) {
                        tokenIdentifierOrOperatorName = identifierOrOperatorName();
                    }
                    break;
                case '!':
                    if (LA(2) == '=') {
                        tokenIdentifierOrOperatorName = notEquals();
                    }
                    break;
                case '\"':
                case '\'':
                    tokenIdentifierOrOperatorName = literal();
                    break;
                case '$':
                    tokenIdentifierOrOperatorName = dollar();
                    break;
                case '(':
                    tokenIdentifierOrOperatorName = leftParen();
                    break;
                case ')':
                    tokenIdentifierOrOperatorName = rightParen();
                    break;
                case '*':
                    tokenIdentifierOrOperatorName = star();
                    break;
                case '+':
                    tokenIdentifierOrOperatorName = plus();
                    break;
                case ',':
                    tokenIdentifierOrOperatorName = comma();
                    break;
                case '-':
                    tokenIdentifierOrOperatorName = minus();
                    break;
                case '.':
                    switch (LA(2)) {
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
                            tokenIdentifierOrOperatorName = number();
                            break;
                        default:
                            tokenIdentifierOrOperatorName = dots();
                            break;
                    }
                    break;
                case '/':
                    tokenIdentifierOrOperatorName = slashes();
                    break;
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
                    tokenIdentifierOrOperatorName = number();
                    break;
                case ':':
                    tokenIdentifierOrOperatorName = LA(2) != ':' ? colon() : doubleColon();
                    break;
                case '<':
                case '>':
                    tokenIdentifierOrOperatorName = relationalOperator();
                    break;
                case '=':
                    tokenIdentifierOrOperatorName = equals();
                    break;
                case '@':
                    tokenIdentifierOrOperatorName = at();
                    break;
                case '[':
                    tokenIdentifierOrOperatorName = leftBracket();
                    break;
                case ']':
                    tokenIdentifierOrOperatorName = rightBracket();
                    break;
                case '|':
                    tokenIdentifierOrOperatorName = pipe();
                    break;
            }
            if (tokenIdentifierOrOperatorName == null) {
                tokenIdentifierOrOperatorName = !hasMoreChars() ? new Token(-1, getXPath(), this.currentPosition, this.endPosition) : new Token(-3, getXPath(), this.currentPosition, this.endPosition);
            }
        } while (tokenIdentifierOrOperatorName.getTokenType() == -2);
        switch (tokenIdentifierOrOperatorName.getTokenType()) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 10:
            case 11:
            case 12:
            case 13:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 23:
            case 25:
            case 27:
            case 28:
            case 30:
            case 31:
                this.expectOperator = false;
                break;
            case 9:
            case 14:
            case 15:
            case 16:
            case 22:
            case 24:
            case 26:
            case 29:
            default:
                this.expectOperator = true;
                break;
        }
        return tokenIdentifierOrOperatorName;
    }

    private Token identifierOrOperatorName() {
        return this.expectOperator ? operatorName() : identifier();
    }

    private Token identifier() {
        int i = this.currentPosition;
        while (hasMoreChars() && Verifier.isXMLNCNameCharacter(LA(1))) {
            consume();
        }
        return new Token(16, getXPath(), i, this.currentPosition);
    }

    private Token operatorName() {
        Token tokenDiv = null;
        switch (LA(1)) {
            case 'a':
                tokenDiv = and();
                break;
            case 'd':
                tokenDiv = div();
                break;
            case 'm':
                tokenDiv = mod();
                break;
            case 'o':
                tokenDiv = or();
                break;
        }
        return tokenDiv;
    }

    private Token mod() {
        Token token = null;
        if (LA(1) == 'm' && LA(2) == 'o' && LA(3) == 'd') {
            token = new Token(10, getXPath(), this.currentPosition, this.currentPosition + 3);
            consume();
            consume();
            consume();
        }
        return token;
    }

    private Token div() {
        Token token = null;
        if (LA(1) == 'd' && LA(2) == 'i' && LA(3) == 'v') {
            token = new Token(11, getXPath(), this.currentPosition, this.currentPosition + 3);
            consume();
            consume();
            consume();
        }
        return token;
    }

    private Token and() {
        Token token = null;
        if (LA(1) == 'a' && LA(2) == 'n' && LA(3) == 'd') {
            token = new Token(27, getXPath(), this.currentPosition, this.currentPosition + 3);
            consume();
            consume();
            consume();
        }
        return token;
    }

    private Token or() {
        Token token = null;
        if (LA(1) == 'o' && LA(2) == 'r') {
            token = new Token(28, getXPath(), this.currentPosition, this.currentPosition + 2);
            consume();
            consume();
        }
        return token;
    }

    private Token number() {
        int i = this.currentPosition;
        boolean z = true;
        while (true) {
            switch (LA(1)) {
                case '.':
                    if (z) {
                        z = false;
                        consume();
                    }
                    break;
                case '/':
                default:
                    break;
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
                    consume();
                    break;
            }
        }
        return new Token(29, getXPath(), i, this.currentPosition);
    }

    private Token whitespace() {
        consume();
        while (hasMoreChars()) {
            switch (LA(1)) {
                case '\t':
                case '\n':
                case '\r':
                case ' ':
                    consume();
                    break;
            }
            return new Token(-2, getXPath(), 0, 0);
        }
        return new Token(-2, getXPath(), 0, 0);
    }

    private Token comma() {
        Token token = new Token(30, getXPath(), this.currentPosition, this.currentPosition + 1);
        consume();
        return token;
    }

    private Token equals() {
        Token token = new Token(1, getXPath(), this.currentPosition, this.currentPosition + 1);
        consume();
        return token;
    }

    private Token minus() {
        Token token = new Token(8, getXPath(), this.currentPosition, this.currentPosition + 1);
        consume();
        return token;
    }

    private Token plus() {
        Token token = new Token(7, getXPath(), this.currentPosition, this.currentPosition + 1);
        consume();
        return token;
    }

    private Token dollar() {
        Token token = new Token(25, getXPath(), this.currentPosition, this.currentPosition + 1);
        consume();
        return token;
    }

    private Token pipe() {
        Token token = new Token(18, getXPath(), this.currentPosition, this.currentPosition + 1);
        consume();
        return token;
    }

    private Token at() {
        Token token = new Token(17, getXPath(), this.currentPosition, this.currentPosition + 1);
        consume();
        return token;
    }

    private Token colon() {
        Token token = new Token(19, getXPath(), this.currentPosition, this.currentPosition + 1);
        consume();
        return token;
    }

    private Token doubleColon() {
        Token token = new Token(20, getXPath(), this.currentPosition, this.currentPosition + 2);
        consume();
        consume();
        return token;
    }

    private Token notEquals() {
        Token token = new Token(2, getXPath(), this.currentPosition, this.currentPosition + 2);
        consume();
        consume();
        return token;
    }

    private Token relationalOperator() {
        Token token = null;
        switch (LA(1)) {
            case '<':
                if (LA(2) == '=') {
                    token = new Token(4, getXPath(), this.currentPosition, this.currentPosition + 2);
                    consume();
                } else {
                    token = new Token(3, getXPath(), this.currentPosition, this.currentPosition + 1);
                }
                consume();
                break;
            case '>':
                if (LA(2) == '=') {
                    token = new Token(6, getXPath(), this.currentPosition, this.currentPosition + 2);
                    consume();
                } else {
                    token = new Token(5, getXPath(), this.currentPosition, this.currentPosition + 1);
                }
                consume();
                break;
        }
        return token;
    }

    private Token star() {
        Token token = new Token(this.expectOperator ? 31 : 9, getXPath(), this.currentPosition, this.currentPosition + 1);
        consume();
        return token;
    }

    private Token literal() {
        Token token = null;
        char cLA = LA(1);
        consume();
        int i = this.currentPosition;
        while (token == null && hasMoreChars()) {
            if (LA(1) == cLA) {
                token = new Token(26, getXPath(), i, this.currentPosition);
            }
            consume();
        }
        return token;
    }

    private Token dots() {
        Token token;
        switch (LA(2)) {
            case '.':
                token = new Token(15, getXPath(), this.currentPosition, this.currentPosition + 2);
                consume();
                consume();
                break;
            default:
                token = new Token(14, getXPath(), this.currentPosition, this.currentPosition + 1);
                consume();
                break;
        }
        return token;
    }

    private Token leftBracket() {
        Token token = new Token(21, getXPath(), this.currentPosition, this.currentPosition + 1);
        consume();
        return token;
    }

    private Token rightBracket() {
        Token token = new Token(22, getXPath(), this.currentPosition, this.currentPosition + 1);
        consume();
        return token;
    }

    private Token leftParen() {
        Token token = new Token(23, getXPath(), this.currentPosition, this.currentPosition + 1);
        consume();
        return token;
    }

    private Token rightParen() {
        Token token = new Token(24, getXPath(), this.currentPosition, this.currentPosition + 1);
        consume();
        return token;
    }

    private Token slashes() {
        Token token;
        switch (LA(2)) {
            case '/':
                token = new Token(13, getXPath(), this.currentPosition, this.currentPosition + 2);
                consume();
                consume();
                break;
            default:
                token = new Token(12, getXPath(), this.currentPosition, this.currentPosition + 1);
                consume();
                break;
        }
        return token;
    }

    private char LA(int i) {
        if (this.currentPosition + (i - 1) >= this.endPosition) {
            return (char) 65535;
        }
        return getXPath().charAt(this.currentPosition + (i - 1));
    }

    private void consume() {
        this.currentPosition++;
    }

    private boolean hasMoreChars() {
        return this.currentPosition < this.endPosition;
    }
}
