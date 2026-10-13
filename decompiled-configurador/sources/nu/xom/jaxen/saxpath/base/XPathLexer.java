package nu.xom.jaxen.saxpath.base;

import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.crypto.tls.AlertDescription;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.crypto.tls.ExtensionType;
import org.spongycastle.pqc.math.linearalgebra.Matrix;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/saxpath/base/XPathLexer.class */
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
                case EACTags.APPLICATION_EFFECTIVE_DATE /* 37 */:
                case EACTags.CARD_EFFECTIVE_DATE /* 38 */:
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
                case EACTags.CERTIFICATION_AUTHORITY_PUBLIC_KEY /* 74 */:
                case EACTags.DEPRECATED /* 75 */:
                case 'L':
                case EACTags.INTEGRATED_CIRCUIT_MANUFACTURER_ID /* 77 */:
                case 'N':
                case 'O':
                case 'P':
                case EACTags.ANSWER_TO_RESET /* 81 */:
                case 'R':
                case 'S':
                case 'T':
                case Matrix.MATRIX_TYPE_RANDOM_UT /* 85 */:
                case AlertDescription.inappropriate_fallback /* 86 */:
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
                case AlertDescription.no_renegotiation /* 100 */:
                case ExtensionType.negotiated_ff_dhe_groups /* 101 */:
                case 'f':
                case 'g':
                case 'h':
                case CipherSuite.TLS_DH_RSA_WITH_AES_256_CBC_SHA256 /* 105 */:
                case CipherSuite.TLS_DHE_DSS_WITH_AES_256_CBC_SHA256 /* 106 */:
                case CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA256 /* 107 */:
                case CipherSuite.TLS_DH_anon_WITH_AES_128_CBC_SHA256 /* 108 */:
                case CipherSuite.TLS_DH_anon_WITH_AES_256_CBC_SHA256 /* 109 */:
                case AlertDescription.unsupported_extension /* 110 */:
                case AlertDescription.certificate_unobtainable /* 111 */:
                case AlertDescription.unrecognized_name /* 112 */:
                case AlertDescription.bad_certificate_status_response /* 113 */:
                case AlertDescription.bad_certificate_hash_value /* 114 */:
                case AlertDescription.unknown_psk_identity /* 115 */:
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
                case EACTags.INTERCHANGE_CONTROL /* 39 */:
                    tokenIdentifierOrOperatorName = literal();
                    break;
                case EACTags.APPLICATION_EXPIRATION_DATE /* 36 */:
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
                case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA256 /* 62 */:
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
            case AlertDescription.no_renegotiation /* 100 */:
                tokenDiv = div();
                break;
            case CipherSuite.TLS_DH_anon_WITH_AES_256_CBC_SHA256 /* 109 */:
                tokenDiv = mod();
                break;
            case AlertDescription.certificate_unobtainable /* 111 */:
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
            case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA256 /* 62 */:
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
