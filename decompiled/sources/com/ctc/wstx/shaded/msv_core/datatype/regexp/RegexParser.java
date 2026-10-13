package com.ctc.wstx.shaded.msv_core.datatype.regexp;

import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/RegexParser.class */
class RegexParser {
    static final int T_CHAR = 0;
    static final int T_EOF = 1;
    static final int T_OR = 2;
    static final int T_STAR = 3;
    static final int T_PLUS = 4;
    static final int T_QUESTION = 5;
    static final int T_LPAREN = 6;
    static final int T_RPAREN = 7;
    static final int T_DOT = 8;
    static final int T_LBRACKET = 9;
    static final int T_BACKSOLIDUS = 10;
    static final int T_CARET = 11;
    static final int T_DOLLAR = 12;
    static final int T_LPAREN2 = 13;
    static final int T_LOOKAHEAD = 14;
    static final int T_NEGATIVELOOKAHEAD = 15;
    static final int T_LOOKBEHIND = 16;
    static final int T_NEGATIVELOOKBEHIND = 17;
    static final int T_INDEPENDENT = 18;
    static final int T_SET_OPERATIONS = 19;
    static final int T_POSIX_CHARCLASS_START = 20;
    static final int T_COMMENT = 21;
    static final int T_MODIFIERS = 22;
    static final int T_CONDITION = 23;
    static final int T_XMLSCHEMA_CC_SUBTRACTION = 24;
    int offset;
    String regex;
    int regexlen;
    int options;
    ResourceBundle resources;
    int chardata;
    int nexttoken;
    protected static final int S_NORMAL = 0;
    protected static final int S_INBRACKETS = 1;
    protected static final int S_INXBRACKETS = 2;
    boolean hasBackReferences;
    int context = 0;
    int parennumber = 1;
    Vector references = null;

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/RegexParser$ReferencePosition.class */
    static class ReferencePosition {
        int refNumber;
        int position;

        ReferencePosition(int n, int pos) {
            this.refNumber = n;
            this.position = pos;
        }
    }

    public RegexParser() {
        setLocale(Locale.getDefault());
    }

    public RegexParser(Locale locale) {
        setLocale(locale);
    }

    public void setLocale(Locale locale) {
        try {
            this.resources = ResourceBundle.getBundle(getClass().getPackage().getName() + ".message", locale);
        } catch (MissingResourceException mre) {
            throw new RuntimeException("Installation Problem???  Couldn't load messages: " + mre.getMessage());
        }
    }

    final ParseException ex(String key, int loc) {
        return new ParseException(this.resources.getString(key), loc);
    }

    private final boolean isSet(int flag) {
        return (this.options & flag) == flag;
    }

    synchronized Token parse(String regex, int options) throws ParseException {
        this.options = options;
        this.offset = 0;
        setContext(0);
        this.parennumber = 1;
        this.hasBackReferences = false;
        this.regex = regex;
        if (isSet(16)) {
            this.regex = REUtil.stripExtendedComment(this.regex);
        }
        this.regexlen = this.regex.length();
        next();
        Token ret = parseRegex();
        if (this.offset != this.regexlen) {
            throw ex("parser.parse.1", this.offset);
        }
        if (this.references != null) {
            for (int i = 0; i < this.references.size(); i++) {
                ReferencePosition position = (ReferencePosition) this.references.elementAt(i);
                if (this.parennumber <= position.refNumber) {
                    throw ex("parser.parse.2", position.position);
                }
            }
            this.references.removeAllElements();
        }
        return ret;
    }

    protected final void setContext(int con) {
        this.context = con;
    }

    final int read() {
        return this.nexttoken;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:30:0x00fc  */
    /* JADX WARN: Code duplicated, block: B:36:0x0121  */
    /* JADX WARN: Code duplicated, block: B:86:0x02fd  */
    /* JADX WARN: Code duplicated, block: B:88:0x030b  */
    final void next() {
        int ret;
        int ret2;
        int low;
        if (this.offset >= this.regexlen) {
            this.chardata = -1;
            this.nexttoken = 1;
            return;
        }
        String str = this.regex;
        int i = this.offset;
        this.offset = i + 1;
        int ch = str.charAt(i);
        this.chardata = ch;
        if (this.context == 1) {
            switch (ch) {
                case 45:
                    if (isSet(512) && this.offset < this.regexlen && this.regex.charAt(this.offset) == '[') {
                        this.offset++;
                        ret2 = 24;
                    } else {
                        ret2 = 0;
                    }
                    this.nexttoken = ret2;
                    return;
                case 91:
                    if (!isSet(512) && this.offset < this.regexlen && this.regex.charAt(this.offset) == ':') {
                        this.offset++;
                        ret2 = 20;
                    } else {
                        if (REUtil.isHighSurrogate(ch) && this.offset < this.regexlen) {
                            low = this.regex.charAt(this.offset);
                            if (REUtil.isLowSurrogate(low)) {
                                this.chardata = REUtil.composeFromSurrogates(ch, low);
                                this.offset++;
                            }
                        }
                        ret2 = 0;
                    }
                    this.nexttoken = ret2;
                    return;
                case 92:
                    ret2 = 10;
                    if (this.offset >= this.regexlen) {
                        throw ex("parser.next.1", this.offset - 1);
                    }
                    String str2 = this.regex;
                    int i2 = this.offset;
                    this.offset = i2 + 1;
                    this.chardata = str2.charAt(i2);
                    this.nexttoken = ret2;
                    return;
                default:
                    if (REUtil.isHighSurrogate(ch)) {
                        low = this.regex.charAt(this.offset);
                        if (REUtil.isLowSurrogate(low)) {
                            this.chardata = REUtil.composeFromSurrogates(ch, low);
                            this.offset++;
                        }
                    }
                    ret2 = 0;
                    this.nexttoken = ret2;
                    return;
            }
        }
        switch (ch) {
            case 40:
                ret = 6;
                if (this.offset < this.regexlen && this.regex.charAt(this.offset) == '?') {
                    int i3 = this.offset + 1;
                    this.offset = i3;
                    if (i3 >= this.regexlen) {
                        throw ex("parser.next.2", this.offset - 1);
                    }
                    String str3 = this.regex;
                    int i4 = this.offset;
                    this.offset = i4 + 1;
                    int ch2 = str3.charAt(i4);
                    switch (ch2) {
                        case 33:
                            ret = 15;
                            break;
                        case 35:
                            while (this.offset < this.regexlen) {
                                String str4 = this.regex;
                                int i5 = this.offset;
                                this.offset = i5 + 1;
                                ch2 = str4.charAt(i5);
                                if (ch2 == 41) {
                                    if (ch2 != 41) {
                                        throw ex("parser.next.4", this.offset - 1);
                                    }
                                    ret = 21;
                                    break;
                                }
                            }
                            if (ch2 != 41) {
                                throw ex("parser.next.4", this.offset - 1);
                            }
                            ret = 21;
                            break;
                            break;
                        case 58:
                            ret = 13;
                            break;
                        case 60:
                            if (this.offset >= this.regexlen) {
                                throw ex("parser.next.2", this.offset - 3);
                            }
                            String str5 = this.regex;
                            int i6 = this.offset;
                            this.offset = i6 + 1;
                            int ch3 = str5.charAt(i6);
                            if (ch3 == 61) {
                                ret = 16;
                            } else if (ch3 == 33) {
                                ret = 17;
                            } else {
                                throw ex("parser.next.3", this.offset - 3);
                            }
                            break;
                            break;
                        case 61:
                            ret = 14;
                            break;
                        case 62:
                            ret = 18;
                            break;
                        case 91:
                            ret = 19;
                            break;
                        default:
                            if (ch2 == 45 || ((97 <= ch2 && ch2 <= 122) || (65 <= ch2 && ch2 <= 90))) {
                                this.offset--;
                                ret = 22;
                            } else if (ch2 == 40) {
                                ret = 23;
                            } else {
                                throw ex("parser.next.2", this.offset - 2);
                            }
                            break;
                    }
                }
                break;
            case 41:
                ret = 7;
                break;
            case 42:
                ret = 3;
                break;
            case 43:
                ret = 4;
                break;
            case 46:
                ret = 8;
                break;
            case 63:
                ret = 5;
                break;
            case 91:
                ret = 9;
                break;
            case 92:
                ret = 10;
                if (this.offset >= this.regexlen) {
                    throw ex("parser.next.1", this.offset - 1);
                }
                String str6 = this.regex;
                int i7 = this.offset;
                this.offset = i7 + 1;
                this.chardata = str6.charAt(i7);
                break;
                break;
            case 94:
                ret = 11;
                break;
            case 124:
                ret = 2;
                break;
            default:
                ret = 0;
                break;
        }
        this.nexttoken = ret;
    }

    Token parseRegex() throws ParseException {
        Token tok = parseTerm();
        Token parent = null;
        while (read() == 2) {
            next();
            if (parent == null) {
                parent = Token.createUnion();
                parent.addChild(tok);
                tok = parent;
            }
            tok.addChild(parseTerm());
        }
        return tok;
    }

    Token parseTerm() throws ParseException {
        int ch = read();
        if (ch == 2 || ch == 7 || ch == 1) {
            return Token.createEmpty();
        }
        Token tok = parseFactor();
        Token concat = null;
        while (true) {
            int ch2 = read();
            if (ch2 == 2 || ch2 == 7 || ch2 == 1) {
                break;
            }
            if (concat == null) {
                concat = Token.createConcat();
                concat.addChild(tok);
                tok = concat;
            }
            concat.addChild(parseFactor());
        }
        return tok;
    }

    Token processCaret() throws ParseException {
        next();
        return Token.token_linebeginning;
    }

    Token processDollar() throws ParseException {
        next();
        return Token.token_lineend;
    }

    Token processLookahead() throws ParseException {
        next();
        Token tok = Token.createLook(20, parseRegex());
        if (read() != 7) {
            throw ex("parser.factor.1", this.offset - 1);
        }
        next();
        return tok;
    }

    Token processNegativelookahead() throws ParseException {
        next();
        Token tok = Token.createLook(21, parseRegex());
        if (read() != 7) {
            throw ex("parser.factor.1", this.offset - 1);
        }
        next();
        return tok;
    }

    Token processLookbehind() throws ParseException {
        next();
        Token tok = Token.createLook(22, parseRegex());
        if (read() != 7) {
            throw ex("parser.factor.1", this.offset - 1);
        }
        next();
        return tok;
    }

    Token processNegativelookbehind() throws ParseException {
        next();
        Token tok = Token.createLook(23, parseRegex());
        if (read() != 7) {
            throw ex("parser.factor.1", this.offset - 1);
        }
        next();
        return tok;
    }

    Token processBacksolidus_A() throws ParseException {
        next();
        return Token.token_stringbeginning;
    }

    Token processBacksolidus_Z() throws ParseException {
        next();
        return Token.token_stringend2;
    }

    Token processBacksolidus_z() throws ParseException {
        next();
        return Token.token_stringend;
    }

    Token processBacksolidus_b() throws ParseException {
        next();
        return Token.token_wordedge;
    }

    Token processBacksolidus_B() throws ParseException {
        next();
        return Token.token_not_wordedge;
    }

    Token processBacksolidus_lt() throws ParseException {
        next();
        return Token.token_wordbeginning;
    }

    Token processBacksolidus_gt() throws ParseException {
        next();
        return Token.token_wordend;
    }

    Token processStar(Token tok) throws ParseException {
        next();
        if (read() == 5) {
            next();
            return Token.createNGClosure(tok);
        }
        return Token.createClosure(tok);
    }

    Token processPlus(Token tok) throws ParseException {
        next();
        if (read() == 5) {
            next();
            return Token.createConcat(tok, Token.createNGClosure(tok));
        }
        return Token.createConcat(tok, Token.createClosure(tok));
    }

    Token processQuestion(Token tok) throws ParseException {
        next();
        Token par = Token.createUnion();
        if (read() == 5) {
            next();
            par.addChild(Token.createEmpty());
            par.addChild(tok);
        } else {
            par.addChild(tok);
            par.addChild(Token.createEmpty());
        }
        return par;
    }

    boolean checkQuestion(int off) {
        return off < this.regexlen && this.regex.charAt(off) == '?';
    }

    Token processParen() throws ParseException {
        next();
        int p = this.parennumber;
        this.parennumber = p + 1;
        Token tok = Token.createParen(parseRegex(), p);
        if (read() != 7) {
            throw ex("parser.factor.1", this.offset - 1);
        }
        next();
        return tok;
    }

    Token processParen2() throws ParseException {
        next();
        Token tok = Token.createParen(parseRegex(), 0);
        if (read() != 7) {
            throw ex("parser.factor.1", this.offset - 1);
        }
        next();
        return tok;
    }

    Token processCondition() throws ParseException {
        if (this.offset + 1 >= this.regexlen) {
            throw ex("parser.factor.4", this.offset);
        }
        int refno = -1;
        Token condition = null;
        int ch = this.regex.charAt(this.offset);
        if (49 <= ch && ch <= 57) {
            refno = ch - 48;
            this.hasBackReferences = true;
            if (this.references == null) {
                this.references = new Vector();
            }
            this.references.addElement(new ReferencePosition(refno, this.offset));
            this.offset++;
            if (this.regex.charAt(this.offset) != ')') {
                throw ex("parser.factor.1", this.offset);
            }
            this.offset++;
        } else {
            if (ch == 63) {
                this.offset--;
            }
            next();
            condition = parseFactor();
            switch (condition.type) {
                case 8:
                    if (read() != 7) {
                        throw ex("parser.factor.1", this.offset - 1);
                    }
                    break;
                case 20:
                case 21:
                case 22:
                case 23:
                    break;
                default:
                    throw ex("parser.factor.5", this.offset);
            }
        }
        next();
        Token yesPattern = parseRegex();
        Token noPattern = null;
        if (yesPattern.type == 2) {
            if (yesPattern.size() != 2) {
                throw ex("parser.factor.6", this.offset);
            }
            noPattern = yesPattern.getChild(1);
            yesPattern = yesPattern.getChild(0);
        }
        if (read() != 7) {
            throw ex("parser.factor.1", this.offset - 1);
        }
        next();
        return Token.createCondition(refno, condition, yesPattern, noPattern);
    }

    Token processModifiers() throws ParseException {
        Token tok;
        int add = 0;
        int mask = 0;
        int ch = -1;
        while (this.offset < this.regexlen) {
            ch = this.regex.charAt(this.offset);
            int v = REUtil.getOptionValue(ch);
            if (v == 0) {
                break;
            }
            add |= v;
            this.offset++;
        }
        if (this.offset >= this.regexlen) {
            throw ex("parser.factor.2", this.offset - 1);
        }
        if (ch == 45) {
            this.offset++;
            while (this.offset < this.regexlen) {
                ch = this.regex.charAt(this.offset);
                int v2 = REUtil.getOptionValue(ch);
                if (v2 == 0) {
                    break;
                }
                mask |= v2;
                this.offset++;
            }
            if (this.offset >= this.regexlen) {
                throw ex("parser.factor.2", this.offset - 1);
            }
        }
        if (ch == 58) {
            this.offset++;
            next();
            tok = Token.createModifierGroup(parseRegex(), add, mask);
            if (read() != 7) {
                throw ex("parser.factor.1", this.offset - 1);
            }
            next();
        } else if (ch == 41) {
            this.offset++;
            next();
            tok = Token.createModifierGroup(parseRegex(), add, mask);
        } else {
            throw ex("parser.factor.3", this.offset);
        }
        return tok;
    }

    Token processIndependent() throws ParseException {
        next();
        Token tok = Token.createLook(24, parseRegex());
        if (read() != 7) {
            throw ex("parser.factor.1", this.offset - 1);
        }
        next();
        return tok;
    }

    Token processBacksolidus_c() throws ParseException {
        if (this.offset < this.regexlen) {
            String str = this.regex;
            int i = this.offset;
            this.offset = i + 1;
            int ch2 = str.charAt(i);
            if ((ch2 & JPEG.APP0) == 64) {
                next();
                return Token.createChar(ch2 - 64);
            }
        }
        throw ex("parser.atom.1", this.offset - 1);
    }

    Token processBacksolidus_C() throws ParseException {
        throw ex("parser.process.1", this.offset);
    }

    Token processBacksolidus_i() throws ParseException {
        Token tok = Token.createChar(105);
        next();
        return tok;
    }

    Token processBacksolidus_I() throws ParseException {
        throw ex("parser.process.1", this.offset);
    }

    Token processBacksolidus_g() throws ParseException {
        next();
        return Token.getGraphemePattern();
    }

    Token processBacksolidus_X() throws ParseException {
        next();
        return Token.getCombiningCharacterSequence();
    }

    Token processBackreference() throws ParseException {
        int refnum = this.chardata - 48;
        Token tok = Token.createBackReference(refnum);
        this.hasBackReferences = true;
        if (this.references == null) {
            this.references = new Vector();
        }
        this.references.addElement(new ReferencePosition(refnum, this.offset - 2));
        next();
        return tok;
    }

    /* JADX WARN: Code duplicated, block: B:65:0x01a6  */
    /* JADX WARN: Code duplicated, block: B:67:0x01ae  */
    /* JADX WARN: Code duplicated, block: B:69:0x01ba  */
    /* JADX WARN: Code duplicated, block: B:86:0x021c  */
    /* JADX WARN: Code duplicated, block: B:88:0x0228  */
    /* JADX WARN: Code duplicated, block: B:91:0x0231  */
    /* JADX WARN: Code duplicated, block: B:93:0x023d  */
    /* JADX WARN: Code duplicated, block: B:95:0x0245  */
    /* JADX WARN: Code duplicated, block: B:96:0x0254  */
    Token parseFactor() throws ParseException {
        int max;
        int iCharAt;
        int ch = read();
        switch (ch) {
            case 10:
                switch (this.chardata) {
                    case 60:
                        return processBacksolidus_lt();
                    case 62:
                        return processBacksolidus_gt();
                    case 65:
                        return processBacksolidus_A();
                    case 66:
                        return processBacksolidus_B();
                    case 90:
                        return processBacksolidus_Z();
                    case 98:
                        return processBacksolidus_b();
                    case 122:
                        return processBacksolidus_z();
                }
            case 11:
                return processCaret();
            case 12:
                return processDollar();
            case 14:
                return processLookahead();
            case 15:
                return processNegativelookahead();
            case 16:
                return processLookbehind();
            case 17:
                return processNegativelookbehind();
            case 21:
                next();
                return Token.createEmpty();
        }
        Token tok = parseAtom();
        int ch2 = read();
        switch (ch2) {
            case 0:
                if (this.chardata == 123 && this.offset < this.regexlen) {
                    int off = this.offset;
                    int off2 = off + 1;
                    int iCharAt2 = this.regex.charAt(off);
                    int ch3 = iCharAt2;
                    if (iCharAt2 >= 48 && ch3 <= 57) {
                        int min = ch3 - 48;
                        while (off2 < this.regexlen) {
                            int i = off2;
                            off2++;
                            int iCharAt3 = this.regex.charAt(i);
                            ch3 = iCharAt3;
                            if (iCharAt3 >= 48 && ch3 <= 57) {
                                min = ((min * 10) + ch3) - 48;
                                if (min < 0) {
                                    throw ex("parser.quantifier.5", this.offset);
                                }
                            } else {
                                max = min;
                                if (ch3 == 44) {
                                    if (off2 >= this.regexlen) {
                                        throw ex("parser.quantifier.3", this.offset);
                                    }
                                    int i2 = off2;
                                    off2++;
                                    iCharAt = this.regex.charAt(i2);
                                    ch3 = iCharAt;
                                    if (iCharAt < 48 && ch3 <= 57) {
                                        max = ch3 - 48;
                                        while (off2 < this.regexlen) {
                                            int i3 = off2;
                                            off2++;
                                            int iCharAt4 = this.regex.charAt(i3);
                                            ch3 = iCharAt4;
                                            if (iCharAt4 >= 48 && ch3 <= 57) {
                                                max = ((max * 10) + ch3) - 48;
                                                if (max < 0) {
                                                    throw ex("parser.quantifier.5", this.offset);
                                                }
                                            } else if (min > max) {
                                                throw ex("parser.quantifier.4", this.offset);
                                            }
                                        }
                                        if (min > max) {
                                            throw ex("parser.quantifier.4", this.offset);
                                        }
                                    } else {
                                        max = -1;
                                    }
                                }
                                if (ch3 != 125) {
                                    throw ex("parser.quantifier.2", this.offset);
                                }
                                if (checkQuestion(off2)) {
                                    tok = Token.createNGClosure(tok);
                                    this.offset = off2 + 1;
                                } else {
                                    tok = Token.createClosure(tok);
                                    this.offset = off2;
                                }
                                tok.setMin(min);
                                tok.setMax(max);
                                next();
                            }
                            break;
                        }
                        max = min;
                        if (ch3 == 44) {
                            if (off2 >= this.regexlen) {
                                throw ex("parser.quantifier.3", this.offset);
                            }
                            int i4 = off2;
                            off2++;
                            iCharAt = this.regex.charAt(i4);
                            ch3 = iCharAt;
                            if (iCharAt < 48) {
                                max = -1;
                            } else {
                                max = -1;
                            }
                        }
                        if (ch3 != 125) {
                            throw ex("parser.quantifier.2", this.offset);
                        }
                        if (checkQuestion(off2)) {
                            tok = Token.createNGClosure(tok);
                            this.offset = off2 + 1;
                        } else {
                            tok = Token.createClosure(tok);
                            this.offset = off2;
                        }
                        tok.setMin(min);
                        tok.setMax(max);
                        next();
                    } else {
                        throw ex("parser.quantifier.1", this.offset);
                    }
                }
                break;
            case 3:
                return processStar(tok);
            case 4:
                return processPlus(tok);
            case 5:
                return processQuestion(tok);
        }
        return tok;
    }

    Token parseAtom() throws ParseException {
        Token tok;
        int ch = read();
        switch (ch) {
            case 0:
                if (this.chardata == 93 || this.chardata == 123 || this.chardata == 125) {
                    throw ex("parser.atom.4", this.offset - 1);
                }
                tok = Token.createChar(this.chardata);
                int high = this.chardata;
                next();
                if (REUtil.isHighSurrogate(high) && read() == 0 && REUtil.isLowSurrogate(this.chardata)) {
                    char[] sur = {(char) high, (char) this.chardata};
                    tok = Token.createParen(Token.createString(new String(sur)), 0);
                    next();
                }
                break;
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 7:
            case 11:
            case 12:
            case 14:
            case 15:
            case 16:
            case 17:
            case 20:
            case 21:
            default:
                throw ex("parser.atom.4", this.offset - 1);
            case 6:
                return processParen();
            case 8:
                next();
                tok = Token.token_dot;
                break;
            case 9:
                return parseCharacterClass(true);
            case 10:
                switch (this.chardata) {
                    case 49:
                    case 50:
                    case 51:
                    case 52:
                    case 53:
                    case 54:
                    case 55:
                    case 56:
                    case 57:
                        return processBackreference();
                    case 58:
                    case 59:
                    case 60:
                    case 61:
                    case 62:
                    case 63:
                    case 64:
                    case 65:
                    case 66:
                    case 69:
                    case 70:
                    case 71:
                    case 72:
                    case 74:
                    case 75:
                    case 76:
                    case 77:
                    case 78:
                    case 79:
                    case 81:
                    case 82:
                    case 84:
                    case 85:
                    case 86:
                    case 89:
                    case 90:
                    case 91:
                    case 92:
                    case 93:
                    case 94:
                    case 95:
                    case 96:
                    case 97:
                    case 98:
                    case 104:
                    case 106:
                    case 107:
                    case 108:
                    case 109:
                    case 111:
                    case 113:
                    default:
                        tok = Token.createChar(this.chardata);
                        break;
                    case 67:
                        return processBacksolidus_C();
                    case 68:
                    case 83:
                    case 87:
                    case 100:
                    case 115:
                    case 119:
                        Token tok2 = getTokenForShorthand(this.chardata);
                        next();
                        return tok2;
                    case 73:
                        return processBacksolidus_I();
                    case 80:
                    case 112:
                        int pstart = this.offset;
                        tok = processBacksolidus_pP(this.chardata);
                        if (tok == null) {
                            throw ex("parser.atom.5", pstart);
                        }
                        break;
                    case 88:
                        return processBacksolidus_X();
                    case 99:
                        return processBacksolidus_c();
                    case 101:
                    case 102:
                    case 110:
                    case 114:
                    case 116:
                    case 117:
                    case 118:
                    case 120:
                        int ch2 = decodeEscaped();
                        if (ch2 < 65536) {
                            tok = Token.createChar(ch2);
                        } else {
                            tok = Token.createString(REUtil.decomposeToSurrogates(ch2));
                        }
                        break;
                    case 103:
                        return processBacksolidus_g();
                    case 105:
                        return processBacksolidus_i();
                }
                next();
                break;
            case 13:
                return processParen2();
            case 18:
                return processIndependent();
            case 19:
                return parseSetOperations();
            case 22:
                return processModifiers();
            case 23:
                return processCondition();
        }
        return tok;
    }

    protected RangeToken processBacksolidus_pP(int c) throws ParseException {
        next();
        if (read() != 0 || this.chardata != 123) {
            throw ex("parser.atom.2", this.offset - 1);
        }
        boolean positive = c == 112;
        int namestart = this.offset;
        int nameend = this.regex.indexOf(125, namestart);
        if (nameend < 0) {
            throw ex("parser.atom.3", this.offset);
        }
        String pname = this.regex.substring(namestart, nameend);
        this.offset = nameend + 1;
        return Token.getRange(pname, positive, isSet(512));
    }

    int processCIinCharacterClass(RangeToken tok, int c) {
        return decodeEscaped();
    }

    protected RangeToken parseCharacterClass(boolean useNrange) throws ParseException {
        RangeToken tok;
        setContext(1);
        next();
        boolean nrange = false;
        RangeToken base = null;
        if (read() == 0 && this.chardata == 94) {
            nrange = true;
            next();
            if (useNrange) {
                tok = Token.createNRange();
            } else {
                base = Token.createRange();
                base.addRange(0, 1114111);
                tok = Token.createRange();
            }
        } else {
            tok = Token.createRange();
        }
        boolean firstloop = true;
        while (true) {
            int type = read();
            if (type != 1 && (type != 0 || this.chardata != 93 || firstloop)) {
                firstloop = false;
                int c = this.chardata;
                boolean end = false;
                if (type == 10) {
                    switch (c) {
                        case 67:
                        case 73:
                        case 99:
                        case 105:
                            c = processCIinCharacterClass(tok, c);
                            if (c < 0) {
                                end = true;
                            }
                            break;
                        case 68:
                        case 83:
                        case 87:
                        case 100:
                        case 115:
                        case 119:
                            tok.mergeRanges(getTokenForShorthand(c));
                            end = true;
                            break;
                        case 80:
                        case 112:
                            int pstart = this.offset;
                            RangeToken tok2 = processBacksolidus_pP(c);
                            if (tok2 == null) {
                                throw ex("parser.atom.5", pstart);
                            }
                            tok.mergeRanges(tok2);
                            end = true;
                            break;
                        default:
                            c = decodeEscaped();
                            break;
                    }
                } else if (type == 20) {
                    int nameend = this.regex.indexOf(58, this.offset);
                    if (nameend < 0) {
                        throw ex("parser.cc.1", this.offset);
                    }
                    boolean positive = true;
                    if (this.regex.charAt(this.offset) == '^') {
                        this.offset++;
                        positive = false;
                    }
                    String name = this.regex.substring(this.offset, nameend);
                    RangeToken range = Token.getRange(name, positive, isSet(512));
                    if (range == null) {
                        throw ex("parser.cc.3", this.offset);
                    }
                    tok.mergeRanges(range);
                    end = true;
                    if (nameend + 1 >= this.regexlen || this.regex.charAt(nameend + 1) != ']') {
                        throw ex("parser.cc.1", nameend);
                    }
                    this.offset = nameend + 2;
                }
                next();
                if (!end) {
                    if (read() != 0 || this.chardata != 45) {
                        tok.addRange(c, c);
                    } else {
                        next();
                        int type2 = read();
                        if (type2 == 1) {
                            throw ex("parser.cc.2", this.offset);
                        }
                        if (type2 == 0 && this.chardata == 93) {
                            tok.addRange(c, c);
                            tok.addRange(45, 45);
                        } else {
                            int rangeend = this.chardata;
                            if (type2 == 10) {
                                rangeend = decodeEscaped();
                            }
                            next();
                            tok.addRange(c, rangeend);
                        }
                    }
                }
                if (isSet(1024) && read() == 0 && this.chardata == 44) {
                    next();
                }
            }
        }
        if (read() == 1) {
            throw ex("parser.cc.2", this.offset);
        }
        if (!useNrange && nrange) {
            base.subtractRanges(tok);
            tok = base;
        }
        tok.sortRanges();
        tok.compactRanges();
        setContext(0);
        next();
        return tok;
    }

    protected RangeToken parseSetOperations() throws ParseException {
        RangeToken tok = parseCharacterClass(false);
        while (true) {
            int type = read();
            if (type != 7) {
                int ch = this.chardata;
                if ((type == 0 && (ch == 45 || ch == 38)) || type == 4) {
                    next();
                    if (read() != 9) {
                        throw ex("parser.ope.1", this.offset - 1);
                    }
                    RangeToken t2 = parseCharacterClass(false);
                    if (type == 4) {
                        tok.mergeRanges(t2);
                    } else if (ch == 45) {
                        tok.subtractRanges(t2);
                    } else if (ch == 38) {
                        tok.intersectRanges(t2);
                    } else {
                        throw new RuntimeException("ASSERT");
                    }
                } else {
                    throw ex("parser.ope.2", this.offset - 1);
                }
            } else {
                next();
                return tok;
            }
        }
    }

    Token getTokenForShorthand(int ch) {
        Token tok;
        switch (ch) {
            case 68:
                tok = isSet(32) ? Token.getRange("Nd", false) : Token.token_not_0to9;
                break;
            case 83:
                tok = isSet(32) ? Token.getRange("IsSpace", false) : Token.token_not_spaces;
                break;
            case 87:
                tok = isSet(32) ? Token.getRange("IsWord", false) : Token.token_not_wordchars;
                break;
            case 100:
                tok = isSet(32) ? Token.getRange("Nd", true) : Token.token_0to9;
                break;
            case 115:
                tok = isSet(32) ? Token.getRange("IsSpace", true) : Token.token_spaces;
                break;
            case 119:
                tok = isSet(32) ? Token.getRange("IsWord", true) : Token.token_wordchars;
                break;
            default:
                throw new RuntimeException("Internal Error: shorthands: \\u" + Integer.toString(ch, 16));
        }
        return tok;
    }

    int decodeEscaped() throws ParseException {
        int v1;
        int v2;
        int v3;
        int v4;
        int v5;
        int v6;
        int v7;
        int v8;
        int v9;
        int v10;
        int v11;
        int v12;
        if (read() != 10) {
            throw ex("parser.next.1", this.offset - 1);
        }
        int c = this.chardata;
        switch (c) {
            case 65:
            case 90:
            case 122:
                throw ex("parser.descape.5", this.offset - 2);
            case 101:
                c = 27;
                break;
            case 102:
                c = 12;
                break;
            case 110:
                c = 10;
                break;
            case 114:
                c = 13;
                break;
            case 116:
                c = 9;
                break;
            case 117:
                next();
                if (read() != 0 || (v7 = hexChar(this.chardata)) < 0) {
                    throw ex("parser.descape.1", this.offset - 1);
                }
                next();
                if (read() != 0 || (v8 = hexChar(this.chardata)) < 0) {
                    throw ex("parser.descape.1", this.offset - 1);
                }
                int uv = (v7 * 16) + v8;
                next();
                if (read() != 0 || (v9 = hexChar(this.chardata)) < 0) {
                    throw ex("parser.descape.1", this.offset - 1);
                }
                int uv2 = (uv * 16) + v9;
                next();
                if (read() != 0 || (v10 = hexChar(this.chardata)) < 0) {
                    throw ex("parser.descape.1", this.offset - 1);
                }
                c = (uv2 * 16) + v10;
                break;
                break;
            case 118:
                next();
                if (read() != 0 || (v1 = hexChar(this.chardata)) < 0) {
                    throw ex("parser.descape.1", this.offset - 1);
                }
                next();
                if (read() != 0 || (v2 = hexChar(this.chardata)) < 0) {
                    throw ex("parser.descape.1", this.offset - 1);
                }
                int uv3 = (v1 * 16) + v2;
                next();
                if (read() != 0 || (v3 = hexChar(this.chardata)) < 0) {
                    throw ex("parser.descape.1", this.offset - 1);
                }
                int uv4 = (uv3 * 16) + v3;
                next();
                if (read() != 0 || (v4 = hexChar(this.chardata)) < 0) {
                    throw ex("parser.descape.1", this.offset - 1);
                }
                int uv5 = (uv4 * 16) + v4;
                next();
                if (read() != 0 || (v5 = hexChar(this.chardata)) < 0) {
                    throw ex("parser.descape.1", this.offset - 1);
                }
                int uv6 = (uv5 * 16) + v5;
                next();
                if (read() != 0 || (v6 = hexChar(this.chardata)) < 0) {
                    throw ex("parser.descape.1", this.offset - 1);
                }
                int uv7 = (uv6 * 16) + v6;
                if (uv7 > 1114111) {
                    throw ex("parser.descappe.4", this.offset - 1);
                }
                c = uv7;
                break;
                break;
            case 120:
                next();
                if (read() != 0) {
                    throw ex("parser.descape.1", this.offset - 1);
                }
                if (this.chardata == 123) {
                    int i = 0;
                    while (true) {
                        int uv8 = i;
                        next();
                        if (read() != 0) {
                            throw ex("parser.descape.1", this.offset - 1);
                        }
                        int v13 = hexChar(this.chardata);
                        if (v13 >= 0) {
                            if (uv8 > uv8 * 16) {
                                throw ex("parser.descape.2", this.offset - 1);
                            }
                            i = (uv8 * 16) + v13;
                        } else {
                            if (this.chardata != 125) {
                                throw ex("parser.descape.3", this.offset - 1);
                            }
                            if (uv8 > 1114111) {
                                throw ex("parser.descape.4", this.offset - 1);
                            }
                            c = uv8;
                        }
                    }
                } else {
                    if (read() != 0 || (v11 = hexChar(this.chardata)) < 0) {
                        throw ex("parser.descape.1", this.offset - 1);
                    }
                    next();
                    if (read() != 0 || (v12 = hexChar(this.chardata)) < 0) {
                        throw ex("parser.descape.1", this.offset - 1);
                    }
                    int uv9 = (v11 * 16) + v12;
                    c = uv9;
                }
                break;
        }
        return c;
    }

    private static final int hexChar(int ch) {
        if (ch < 48 || ch > 102) {
            return -1;
        }
        if (ch <= 57) {
            return ch - 48;
        }
        if (ch < 65) {
            return -1;
        }
        if (ch <= 70) {
            return (ch - 65) + 10;
        }
        if (ch < 97) {
            return -1;
        }
        return (ch - 97) + 10;
    }
}
