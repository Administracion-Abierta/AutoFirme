package com.ctc.wstx.shaded.msv_core.datatype.regexp;

import java.io.Serializable;
import java.text.CharacterIterator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/RegularExpression.class */
class RegularExpression implements Serializable {
    static final boolean DEBUG = false;
    String regex;
    int options;
    int nofparen;
    Token tokentree;
    boolean hasBackReferences;
    transient int minlength;
    transient Op operations;
    transient int numberOfClosures;
    transient Context context;
    transient RangeToken firstChar;
    transient String fixedString;
    transient int fixedStringOptions;
    transient BMPattern fixedStringTable;
    transient boolean fixedStringOnly;
    static final int IGNORE_CASE = 2;
    static final int SINGLE_LINE = 4;
    static final int MULTIPLE_LINES = 8;
    static final int EXTENDED_COMMENT = 16;
    static final int USE_UNICODE_CATEGORY = 32;
    static final int UNICODE_WORD_BOUNDARY = 64;
    static final int PROHIBIT_HEAD_CHARACTER_OPTIMIZATION = 128;
    static final int PROHIBIT_FIXED_STRING_OPTIMIZATION = 256;
    static final int XMLSCHEMA_MODE = 512;
    static final int SPECIAL_COMMA = 1024;
    private static final int WT_IGNORE = 0;
    private static final int WT_LETTER = 1;
    private static final int WT_OTHER = 2;
    static final int LINE_FEED = 10;
    static final int CARRIAGE_RETURN = 13;
    static final int LINE_SEPARATOR = 8232;
    static final int PARAGRAPH_SEPARATOR = 8233;

    private synchronized void compile(Token tok) {
        if (this.operations != null) {
            return;
        }
        this.numberOfClosures = 0;
        this.operations = compile(tok, null, false);
    }

    private Op compile(Token tok, Op next, boolean reverse) {
        Op ret;
        Op.ChildOp op;
        switch (tok.type) {
            case 0:
                ret = Op.createChar(tok.getChar());
                ret.next = next;
                break;
            case 1:
                ret = next;
                if (!reverse) {
                    for (int i = tok.size() - 1; i >= 0; i--) {
                        ret = compile(tok.getChild(i), ret, false);
                    }
                } else {
                    for (int i2 = 0; i2 < tok.size(); i2++) {
                        ret = compile(tok.getChild(i2), ret, true);
                    }
                }
                break;
            case 2:
                Op.UnionOp uni = Op.createUnion(tok.size());
                for (int i3 = 0; i3 < tok.size(); i3++) {
                    uni.addElement(compile(tok.getChild(i3), next, reverse));
                }
                ret = uni;
                break;
            case 3:
            case 9:
                Token child = tok.getChild(0);
                int min = tok.getMin();
                int max = tok.getMax();
                if (min >= 0 && min == max) {
                    ret = next;
                    for (int i4 = 0; i4 < min; i4++) {
                        ret = compile(child, ret, reverse);
                    }
                } else {
                    if (min > 0 && max > 0) {
                        max -= min;
                    }
                    if (max > 0) {
                        ret = next;
                        for (int i5 = 0; i5 < max; i5++) {
                            Op.ChildOp q = Op.createQuestion(tok.type == 9);
                            q.next = next;
                            q.setChild(compile(child, ret, reverse));
                            ret = q;
                        }
                    } else {
                        if (tok.type == 9) {
                            op = Op.createNonGreedyClosure();
                        } else if (child.getMinLength() == 0) {
                            int i6 = this.numberOfClosures;
                            this.numberOfClosures = i6 + 1;
                            op = Op.createClosure(i6);
                        } else {
                            op = Op.createClosure(-1);
                        }
                        op.next = next;
                        op.setChild(compile(child, op, reverse));
                        ret = op;
                    }
                    if (min > 0) {
                        for (int i7 = 0; i7 < min; i7++) {
                            ret = compile(child, ret, reverse);
                        }
                    }
                }
                break;
            case 4:
            case 5:
                ret = Op.createRange(tok);
                ret.next = next;
                break;
            case 6:
                if (tok.getParenNumber() == 0) {
                    ret = compile(tok.getChild(0), next, reverse);
                } else if (reverse) {
                    ret = Op.createCapture(-tok.getParenNumber(), compile(tok.getChild(0), Op.createCapture(tok.getParenNumber(), next), reverse));
                } else {
                    ret = Op.createCapture(tok.getParenNumber(), compile(tok.getChild(0), Op.createCapture(-tok.getParenNumber(), next), reverse));
                }
                break;
            case 7:
                ret = next;
                break;
            case 8:
                ret = Op.createAnchor(tok.getChar());
                ret.next = next;
                break;
            case 10:
                ret = Op.createString(tok.getString());
                ret.next = next;
                break;
            case 11:
                ret = Op.createDot();
                ret.next = next;
                break;
            case 12:
                ret = Op.createBackReference(tok.getReferenceNumber());
                ret.next = next;
                break;
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            default:
                throw new RuntimeException("Unknown token type: " + tok.type);
            case 20:
                ret = Op.createLook(20, next, compile(tok.getChild(0), null, false));
                break;
            case 21:
                ret = Op.createLook(21, next, compile(tok.getChild(0), null, false));
                break;
            case 22:
                ret = Op.createLook(22, next, compile(tok.getChild(0), null, true));
                break;
            case 23:
                ret = Op.createLook(23, next, compile(tok.getChild(0), null, true));
                break;
            case 24:
                ret = Op.createIndependent(next, compile(tok.getChild(0), null, reverse));
                break;
            case 25:
                ret = Op.createModifier(next, compile(tok.getChild(0), null, reverse), ((Token.ModifierToken) tok).getOptions(), ((Token.ModifierToken) tok).getOptionsMask());
                break;
            case 26:
                Token.ConditionToken ctok = (Token.ConditionToken) tok;
                int ref = ctok.refNumber;
                Op condition = ctok.condition == null ? null : compile(ctok.condition, null, reverse);
                Op yes = compile(ctok.yes, next, reverse);
                Op no = ctok.no == null ? null : compile(ctok.no, next, reverse);
                ret = Op.createCondition(next, ref, condition, yes, no);
                break;
        }
        return ret;
    }

    public boolean matches(char[] target) {
        return matches(target, 0, target.length, (Match) null);
    }

    public boolean matches(char[] target, int start, int end) {
        return matches(target, start, end, (Match) null);
    }

    public boolean matches(char[] target, Match match) {
        return matches(target, 0, target.length, match);
    }

    /* JADX WARN: Code duplicated, block: B:110:0x02b6  */
    /* JADX WARN: Code duplicated, block: B:154:0x036c A[EDGE_INSN: B:154:0x036c->B:137:0x036c BREAK  A[LOOP:1: B:94:0x024f->B:113:0x02d1], SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:156:0x02d1 A[SYNTHETIC] */
    public boolean matches(char[] target, int start, int end, Match match) {
        Context con;
        int matchStart;
        int iMatchCharArray;
        boolean z;
        synchronized (this) {
            if (this.operations == null) {
                prepare();
            }
            if (this.context == null) {
                this.context = new Context();
            }
        }
        synchronized (this.context) {
            con = this.context.inuse ? new Context() : this.context;
            con.reset(target, start, end, this.numberOfClosures);
        }
        if (match != null) {
            match.setNumberOfGroups(this.nofparen);
            match.setSource(target);
        } else if (this.hasBackReferences) {
            match = new Match();
            match.setNumberOfGroups(this.nofparen);
        }
        con.match = match;
        if (isSet(this.options, 512)) {
            int matchEnd = matchCharArray(con, this.operations, con.start, 1, this.options);
            if (matchEnd == con.limit) {
                if (con.match != null) {
                    con.match.setBeginning(0, con.start);
                    con.match.setEnd(0, matchEnd);
                }
                con.inuse = false;
                return true;
            }
            return false;
        }
        if (this.fixedStringOnly) {
            int o = this.fixedStringTable.matches(target, con.start, con.limit);
            if (o >= 0) {
                if (con.match != null) {
                    con.match.setBeginning(0, o);
                    con.match.setEnd(0, o + this.fixedString.length());
                }
                con.inuse = false;
                return true;
            }
            con.inuse = false;
            return false;
        }
        if (this.fixedString != null && this.fixedStringTable.matches(target, con.start, con.limit) < 0) {
            con.inuse = false;
            return false;
        }
        int limit = con.limit - this.minlength;
        int matchEnd2 = -1;
        if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
            if (isSet(this.options, 4)) {
                matchStart = con.start;
                matchEnd2 = matchCharArray(con, this.operations, con.start, 1, this.options);
            } else {
                boolean previousIsEOL = true;
                matchStart = con.start;
                while (matchStart <= limit) {
                    if (isEOLChar(target[matchStart])) {
                        z = true;
                    } else {
                        if (previousIsEOL) {
                            int iMatchCharArray2 = matchCharArray(con, this.operations, matchStart, 1, this.options);
                            matchEnd2 = iMatchCharArray2;
                            if (0 <= iMatchCharArray2) {
                                break;
                            }
                        }
                        z = false;
                    }
                    previousIsEOL = z;
                    matchStart++;
                }
            }
        } else if (this.firstChar != null) {
            RangeToken range = this.firstChar;
            if (isSet(this.options, 2)) {
                RangeToken range2 = this.firstChar.getCaseInsensitiveToken();
                matchStart = con.start;
                while (matchStart <= limit) {
                    char c = target[matchStart];
                    if (REUtil.isHighSurrogate(c) && matchStart + 1 < con.limit) {
                        if (range2.match(REUtil.composeFromSurrogates(c, target[matchStart + 1]))) {
                            iMatchCharArray = matchCharArray(con, this.operations, matchStart, 1, this.options);
                            matchEnd2 = iMatchCharArray;
                            if (0 <= iMatchCharArray) {
                                break;
                                break;
                            }
                        } else {
                            continue;
                        }
                    } else if (!range2.match(c)) {
                        char ch1 = Character.toUpperCase(c);
                        if (range2.match(ch1) || range2.match(Character.toLowerCase(ch1))) {
                            iMatchCharArray = matchCharArray(con, this.operations, matchStart, 1, this.options);
                            matchEnd2 = iMatchCharArray;
                            if (0 <= iMatchCharArray) {
                                break;
                            }
                        }
                    } else {
                        iMatchCharArray = matchCharArray(con, this.operations, matchStart, 1, this.options);
                        matchEnd2 = iMatchCharArray;
                        if (0 <= iMatchCharArray) {
                            break;
                            break;
                        }
                    }
                    matchStart++;
                }
            } else {
                matchStart = con.start;
                while (matchStart <= limit) {
                    int ch = target[matchStart];
                    if (REUtil.isHighSurrogate(ch) && matchStart + 1 < con.limit) {
                        ch = REUtil.composeFromSurrogates(ch, target[matchStart + 1]);
                    }
                    if (range.match(ch)) {
                        int iMatchCharArray3 = matchCharArray(con, this.operations, matchStart, 1, this.options);
                        matchEnd2 = iMatchCharArray3;
                        if (0 <= iMatchCharArray3) {
                            break;
                        }
                    }
                    matchStart++;
                }
            }
        } else {
            matchStart = con.start;
            while (matchStart <= limit) {
                int iMatchCharArray4 = matchCharArray(con, this.operations, matchStart, 1, this.options);
                matchEnd2 = iMatchCharArray4;
                if (0 <= iMatchCharArray4) {
                    break;
                }
                matchStart++;
            }
        }
        if (matchEnd2 >= 0) {
            if (con.match != null) {
                con.match.setBeginning(0, matchStart);
                con.match.setEnd(0, matchEnd2);
            }
            con.inuse = false;
            return true;
        }
        con.inuse = false;
        return false;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private int matchCharArray(Context con, Op op, int offset, int dx, int opts) {
        boolean matchp;
        int i;
        int i2;
        boolean go;
        int after;
        char[] target = con.charTarget;
        while (op != null) {
            if (offset > con.limit || offset < con.start) {
                return -1;
            }
            switch (op.type) {
                case 0:
                    if (dx > 0) {
                        if (offset >= con.limit) {
                            return -1;
                        }
                        int ch = target[offset];
                        if (isSet(opts, 4)) {
                            if (REUtil.isHighSurrogate(ch) && offset + 1 < con.limit) {
                                offset++;
                            }
                        } else {
                            if (REUtil.isHighSurrogate(ch) && offset + 1 < con.limit) {
                                offset++;
                                ch = REUtil.composeFromSurrogates(ch, target[offset]);
                            }
                            if (isEOLChar(ch)) {
                                return -1;
                            }
                        }
                        offset++;
                    } else {
                        int o1 = offset - 1;
                        if (o1 >= con.limit || o1 < 0) {
                            return -1;
                        }
                        int ch2 = target[o1];
                        if (isSet(opts, 4)) {
                            if (REUtil.isLowSurrogate(ch2) && o1 - 1 >= 0) {
                                o1--;
                            }
                        } else {
                            if (REUtil.isLowSurrogate(ch2) && o1 - 1 >= 0) {
                                o1--;
                                ch2 = REUtil.composeFromSurrogates(target[o1], ch2);
                            }
                            if (!isEOLChar(ch2)) {
                                return -1;
                            }
                        }
                        offset = o1;
                    }
                    op = op.next;
                    break;
                case 1:
                    if (isSet(opts, 2)) {
                        int ch3 = op.getData();
                        if (dx > 0) {
                            if (offset >= con.limit || !matchIgnoreCase(ch3, target[offset])) {
                                return -1;
                            }
                            offset++;
                        } else {
                            int o2 = offset - 1;
                            if (o2 >= con.limit || o2 < 0 || !matchIgnoreCase(ch3, target[o2])) {
                                return -1;
                            }
                            offset = o2;
                        }
                    } else {
                        int ch4 = op.getData();
                        if (dx > 0) {
                            if (offset >= con.limit || ch4 != target[offset]) {
                                return -1;
                            }
                            offset++;
                        } else {
                            int o3 = offset - 1;
                            if (o3 >= con.limit || o3 < 0 || ch4 != target[o3]) {
                                return -1;
                            }
                            offset = o3;
                        }
                    }
                    op = op.next;
                    break;
                case 2:
                case 12:
                case 13:
                case 14:
                case 17:
                case 18:
                case 19:
                default:
                    throw new RuntimeException("Unknown operation type: " + op.type);
                case 3:
                case 4:
                    if (dx > 0) {
                        if (offset >= con.limit) {
                            return -1;
                        }
                        int ch5 = target[offset];
                        if (REUtil.isHighSurrogate(ch5) && offset + 1 < con.limit) {
                            offset++;
                            ch5 = REUtil.composeFromSurrogates(ch5, target[offset]);
                        }
                        RangeToken tok = op.getToken();
                        if (isSet(opts, 2)) {
                            RangeToken tok2 = tok.getCaseInsensitiveToken();
                            if (!tok2.match(ch5)) {
                                if (ch5 >= 65536) {
                                    return -1;
                                }
                                char uch = Character.toUpperCase((char) ch5);
                                if (!tok2.match(uch) && !tok2.match(Character.toLowerCase(uch))) {
                                    return -1;
                                }
                            }
                        } else if (!tok.match(ch5)) {
                            return -1;
                        }
                        offset++;
                    } else {
                        int o4 = offset - 1;
                        if (o4 >= con.limit || o4 < 0) {
                            return -1;
                        }
                        int ch6 = target[o4];
                        if (REUtil.isLowSurrogate(ch6) && o4 - 1 >= 0) {
                            o4--;
                            ch6 = REUtil.composeFromSurrogates(target[o4], ch6);
                        }
                        RangeToken tok3 = op.getToken();
                        if (isSet(opts, 2)) {
                            RangeToken tok4 = tok3.getCaseInsensitiveToken();
                            if (!tok4.match(ch6)) {
                                if (ch6 >= 65536) {
                                    return -1;
                                }
                                char uch2 = Character.toUpperCase((char) ch6);
                                if (!tok4.match(uch2) && !tok4.match(Character.toLowerCase(uch2))) {
                                    return -1;
                                }
                            }
                        } else if (!tok3.match(ch6)) {
                            return -1;
                        }
                        offset = o4;
                    }
                    op = op.next;
                    break;
                case 5:
                    switch (op.getData()) {
                        case 36:
                            if (isSet(opts, 8)) {
                                if (offset != con.limit && (offset >= con.limit || !isEOLChar(target[offset]))) {
                                    return -1;
                                }
                            } else if (offset != con.limit && ((offset + 1 != con.limit || !isEOLChar(target[offset])) && (offset + 2 != con.limit || target[offset] != '\r' || target[offset + 1] != '\n'))) {
                                return -1;
                            }
                            op = op.next;
                        case 60:
                            if (con.length == 0 || offset == con.limit || getWordType(target, con.start, con.limit, offset, opts) != 1 || getPreviousWordType(target, con.start, con.limit, offset, opts) != 2) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 62:
                            if (con.length == 0 || offset == con.start || getWordType(target, con.start, con.limit, offset, opts) != 2 || getPreviousWordType(target, con.start, con.limit, offset, opts) != 1) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 64:
                            if (offset != con.start && (offset <= con.start || !isEOLChar(target[offset - 1]))) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 65:
                            if (offset != con.start) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 66:
                            if (con.length == 0) {
                                go = true;
                            } else {
                                int after2 = getWordType(target, con.start, con.limit, offset, opts);
                                go = after2 == 0 || after2 == getPreviousWordType(target, con.start, con.limit, offset, opts);
                            }
                            if (!go) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 90:
                            if (offset != con.limit && ((offset + 1 != con.limit || !isEOLChar(target[offset])) && (offset + 2 != con.limit || target[offset] != '\r' || target[offset + 1] != '\n'))) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 94:
                            if (isSet(opts, 8)) {
                                if (offset != con.start && (offset <= con.start || !isEOLChar(target[offset - 1]))) {
                                    return -1;
                                }
                            } else if (offset != con.start) {
                                return -1;
                            }
                            op = op.next;
                        case 98:
                            if (con.length == 0 || (after = getWordType(target, con.start, con.limit, offset, opts)) == 0) {
                                return -1;
                            }
                            int before = getPreviousWordType(target, con.start, con.limit, offset, opts);
                            if (after == before) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 122:
                            if (offset != con.limit) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        default:
                            op = op.next;
                            break;
                    }
                    break;
                case 6:
                    String literal = op.getString();
                    int literallen = literal.length();
                    if (!isSet(opts, 2)) {
                        if (dx > 0) {
                            if (!regionMatches(target, offset, con.limit, literal, literallen)) {
                                return -1;
                            }
                            i = offset + literallen;
                        } else {
                            if (!regionMatches(target, offset - literallen, con.limit, literal, literallen)) {
                                return -1;
                            }
                            i = offset - literallen;
                        }
                    } else if (dx > 0) {
                        if (!regionMatchesIgnoreCase(target, offset, con.limit, literal, literallen)) {
                            return -1;
                        }
                        i = offset + literallen;
                    } else {
                        if (!regionMatchesIgnoreCase(target, offset - literallen, con.limit, literal, literallen)) {
                            return -1;
                        }
                        i = offset - literallen;
                    }
                    offset = i;
                    op = op.next;
                    break;
                case 7:
                    int id = op.getData();
                    if (id >= 0) {
                        int previousOffset = con.offsets[id];
                        if (previousOffset < 0 || previousOffset != offset) {
                            con.offsets[id] = offset;
                        } else {
                            con.offsets[id] = -1;
                            op = op.next;
                        }
                        break;
                    }
                    int ret = matchCharArray(con, op.getChild(), offset, dx, opts);
                    if (id >= 0) {
                        con.offsets[id] = -1;
                    }
                    if (ret >= 0) {
                        return ret;
                    }
                    op = op.next;
                    break;
                    break;
                case 8:
                case 10:
                    int ret2 = matchCharArray(con, op.next, offset, dx, opts);
                    if (ret2 >= 0) {
                        return ret2;
                    }
                    op = op.getChild();
                    break;
                    break;
                case 9:
                    int ret3 = matchCharArray(con, op.getChild(), offset, dx, opts);
                    if (ret3 >= 0) {
                        return ret3;
                    }
                    op = op.next;
                    break;
                    break;
                case 11:
                    for (int i3 = 0; i3 < op.size(); i3++) {
                        int ret4 = matchCharArray(con, op.elementAt(i3), offset, dx, opts);
                        if (ret4 >= 0) {
                            return ret4;
                        }
                    }
                    return -1;
                case 15:
                    int refno = op.getData();
                    if (con.match != null && refno > 0) {
                        int save = con.match.getBeginning(refno);
                        con.match.setBeginning(refno, offset);
                        int ret5 = matchCharArray(con, op.next, offset, dx, opts);
                        if (ret5 < 0) {
                            con.match.setBeginning(refno, save);
                        }
                        return ret5;
                    }
                    if (con.match != null && refno < 0) {
                        int index = -refno;
                        int save2 = con.match.getEnd(index);
                        con.match.setEnd(index, offset);
                        int ret6 = matchCharArray(con, op.next, offset, dx, opts);
                        if (ret6 < 0) {
                            con.match.setEnd(index, save2);
                        }
                        return ret6;
                    }
                    op = op.next;
                    break;
                    break;
                case 16:
                    int refno2 = op.getData();
                    if (refno2 <= 0 || refno2 >= this.nofparen) {
                        throw new RuntimeException("Internal Error: Reference number must be more than zero: " + refno2);
                    }
                    if (con.match.getBeginning(refno2) < 0 || con.match.getEnd(refno2) < 0) {
                        return -1;
                    }
                    int o5 = con.match.getBeginning(refno2);
                    int literallen2 = con.match.getEnd(refno2) - o5;
                    if (!isSet(opts, 2)) {
                        if (dx > 0) {
                            if (!regionMatches(target, offset, con.limit, o5, literallen2)) {
                                return -1;
                            }
                            i2 = offset + literallen2;
                        } else {
                            if (!regionMatches(target, offset - literallen2, con.limit, o5, literallen2)) {
                                return -1;
                            }
                            i2 = offset - literallen2;
                        }
                    } else if (dx > 0) {
                        if (!regionMatchesIgnoreCase(target, offset, con.limit, o5, literallen2)) {
                            return -1;
                        }
                        i2 = offset + literallen2;
                    } else {
                        if (!regionMatchesIgnoreCase(target, offset - literallen2, con.limit, o5, literallen2)) {
                            return -1;
                        }
                        i2 = offset - literallen2;
                    }
                    offset = i2;
                    op = op.next;
                    break;
                    break;
                case 20:
                    if (0 > matchCharArray(con, op.getChild(), offset, 1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    break;
                    break;
                case 21:
                    if (0 <= matchCharArray(con, op.getChild(), offset, 1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    break;
                    break;
                case 22:
                    if (0 > matchCharArray(con, op.getChild(), offset, -1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    break;
                    break;
                case 23:
                    if (0 <= matchCharArray(con, op.getChild(), offset, -1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    break;
                    break;
                case 24:
                    int ret7 = matchCharArray(con, op.getChild(), offset, dx, opts);
                    if (ret7 < 0) {
                        return ret7;
                    }
                    offset = ret7;
                    op = op.next;
                    break;
                    break;
                case 25:
                    int localopts = opts | op.getData();
                    int ret8 = matchCharArray(con, op.getChild(), offset, dx, localopts & (op.getData2() ^ (-1)));
                    if (ret8 < 0) {
                        return ret8;
                    }
                    offset = ret8;
                    op = op.next;
                    break;
                    break;
                case 26:
                    Op.ConditionOp cop = (Op.ConditionOp) op;
                    if (cop.refNumber > 0) {
                        if (cop.refNumber >= this.nofparen) {
                            throw new RuntimeException("Internal Error: Reference number must be more than zero: " + cop.refNumber);
                        }
                        matchp = con.match.getBeginning(cop.refNumber) >= 0 && con.match.getEnd(cop.refNumber) >= 0;
                    } else {
                        matchp = 0 <= matchCharArray(con, cop.condition, offset, dx, opts);
                    }
                    if (matchp) {
                        op = cop.yes;
                    } else if (cop.no != null) {
                        op = cop.no;
                    } else {
                        op = cop.next;
                    }
                    break;
            }
        }
        if (!isSet(opts, 512) || offset == con.limit) {
            return offset;
        }
        return -1;
    }

    private static final int getPreviousWordType(char[] target, int begin, int end, int offset, int opts) {
        int offset2 = offset - 1;
        int wordType = getWordType(target, begin, end, offset2, opts);
        while (true) {
            int ret = wordType;
            if (ret == 0) {
                offset2--;
                wordType = getWordType(target, begin, end, offset2, opts);
            } else {
                return ret;
            }
        }
    }

    private static final int getWordType(char[] target, int begin, int end, int offset, int opts) {
        if (offset < begin || offset >= end) {
            return 2;
        }
        return getWordType0(target[offset], opts);
    }

    private static final boolean regionMatches(char[] target, int offset, int limit, String part, int partlen) {
        int i;
        int i2;
        if (offset < 0 || limit - offset < partlen) {
            return false;
        }
        int i3 = 0;
        do {
            int i4 = partlen;
            partlen--;
            if (i4 <= 0) {
                return true;
            }
            i = offset;
            offset++;
            i2 = i3;
            i3++;
        } while (target[i] == part.charAt(i2));
        return false;
    }

    private static final boolean regionMatches(char[] target, int offset, int limit, int offset2, int partlen) {
        int i;
        int i2;
        if (offset < 0 || limit - offset < partlen) {
            return false;
        }
        int i3 = offset2;
        do {
            int i4 = partlen;
            partlen--;
            if (i4 <= 0) {
                return true;
            }
            i = offset;
            offset++;
            i2 = i3;
            i3++;
        } while (target[i] == target[i2]);
        return false;
    }

    private static final boolean regionMatchesIgnoreCase(char[] target, int offset, int limit, String part, int partlen) {
        char uch1;
        char uch2;
        if (offset < 0 || limit - offset < partlen) {
            return false;
        }
        int i = 0;
        while (true) {
            int i2 = partlen;
            partlen--;
            if (i2 > 0) {
                int i3 = offset;
                offset++;
                char ch1 = target[i3];
                int i4 = i;
                i++;
                char ch2 = part.charAt(i4);
                if (ch1 != ch2 && (uch1 = Character.toUpperCase(ch1)) != (uch2 = Character.toUpperCase(ch2)) && Character.toLowerCase(uch1) != Character.toLowerCase(uch2)) {
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    private static final boolean regionMatchesIgnoreCase(char[] target, int offset, int limit, int offset2, int partlen) {
        char uch1;
        char uch2;
        if (offset < 0 || limit - offset < partlen) {
            return false;
        }
        int i = offset2;
        while (true) {
            int i2 = partlen;
            partlen--;
            if (i2 > 0) {
                int i3 = offset;
                offset++;
                char ch1 = target[i3];
                int i4 = i;
                i++;
                char ch2 = target[i4];
                if (ch1 != ch2 && (uch1 = Character.toUpperCase(ch1)) != (uch2 = Character.toUpperCase(ch2)) && Character.toLowerCase(uch1) != Character.toLowerCase(uch2)) {
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    public boolean matches(String target) {
        return matches(target, 0, target.length(), (Match) null);
    }

    public boolean matches(String target, int start, int end) {
        return matches(target, start, end, (Match) null);
    }

    public boolean matches(String target, Match match) {
        return matches(target, 0, target.length(), match);
    }

    /* JADX WARN: Code duplicated, block: B:110:0x02bc  */
    /* JADX WARN: Code duplicated, block: B:154:0x0376 A[EDGE_INSN: B:154:0x0376->B:137:0x0376 BREAK  A[LOOP:1: B:94:0x0251->B:113:0x02d7], SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:156:0x02d7 A[SYNTHETIC] */
    public boolean matches(String target, int start, int end, Match match) {
        Context con;
        int matchStart;
        int iMatchString;
        boolean z;
        synchronized (this) {
            if (this.operations == null) {
                prepare();
            }
            if (this.context == null) {
                this.context = new Context();
            }
        }
        synchronized (this.context) {
            con = this.context.inuse ? new Context() : this.context;
            con.reset(target, start, end, this.numberOfClosures);
        }
        if (match != null) {
            match.setNumberOfGroups(this.nofparen);
            match.setSource(target);
        } else if (this.hasBackReferences) {
            match = new Match();
            match.setNumberOfGroups(this.nofparen);
        }
        con.match = match;
        if (isSet(this.options, 512)) {
            int matchEnd = matchString(con, this.operations, con.start, 1, this.options);
            if (matchEnd == con.limit) {
                if (con.match != null) {
                    con.match.setBeginning(0, con.start);
                    con.match.setEnd(0, matchEnd);
                }
                con.inuse = false;
                return true;
            }
            return false;
        }
        if (this.fixedStringOnly) {
            int o = this.fixedStringTable.matches(target, con.start, con.limit);
            if (o >= 0) {
                if (con.match != null) {
                    con.match.setBeginning(0, o);
                    con.match.setEnd(0, o + this.fixedString.length());
                }
                con.inuse = false;
                return true;
            }
            con.inuse = false;
            return false;
        }
        if (this.fixedString != null && this.fixedStringTable.matches(target, con.start, con.limit) < 0) {
            con.inuse = false;
            return false;
        }
        int limit = con.limit - this.minlength;
        int matchEnd2 = -1;
        if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
            if (isSet(this.options, 4)) {
                matchStart = con.start;
                matchEnd2 = matchString(con, this.operations, con.start, 1, this.options);
            } else {
                boolean previousIsEOL = true;
                matchStart = con.start;
                while (matchStart <= limit) {
                    if (isEOLChar(target.charAt(matchStart))) {
                        z = true;
                    } else {
                        if (previousIsEOL) {
                            int iMatchString2 = matchString(con, this.operations, matchStart, 1, this.options);
                            matchEnd2 = iMatchString2;
                            if (0 <= iMatchString2) {
                                break;
                            }
                        }
                        z = false;
                    }
                    previousIsEOL = z;
                    matchStart++;
                }
            }
        } else if (this.firstChar != null) {
            RangeToken range = this.firstChar;
            if (isSet(this.options, 2)) {
                RangeToken range2 = this.firstChar.getCaseInsensitiveToken();
                matchStart = con.start;
                while (matchStart <= limit) {
                    int ch = target.charAt(matchStart);
                    if (REUtil.isHighSurrogate(ch) && matchStart + 1 < con.limit) {
                        if (range2.match(REUtil.composeFromSurrogates(ch, target.charAt(matchStart + 1)))) {
                            iMatchString = matchString(con, this.operations, matchStart, 1, this.options);
                            matchEnd2 = iMatchString;
                            if (0 <= iMatchString) {
                                break;
                                break;
                            }
                        } else {
                            continue;
                        }
                    } else if (!range2.match(ch)) {
                        char ch1 = Character.toUpperCase((char) ch);
                        if (range2.match(ch1) || range2.match(Character.toLowerCase(ch1))) {
                            iMatchString = matchString(con, this.operations, matchStart, 1, this.options);
                            matchEnd2 = iMatchString;
                            if (0 <= iMatchString) {
                                break;
                            }
                        }
                    } else {
                        iMatchString = matchString(con, this.operations, matchStart, 1, this.options);
                        matchEnd2 = iMatchString;
                        if (0 <= iMatchString) {
                            break;
                            break;
                        }
                    }
                    matchStart++;
                }
            } else {
                matchStart = con.start;
                while (matchStart <= limit) {
                    int ch2 = target.charAt(matchStart);
                    if (REUtil.isHighSurrogate(ch2) && matchStart + 1 < con.limit) {
                        ch2 = REUtil.composeFromSurrogates(ch2, target.charAt(matchStart + 1));
                    }
                    if (range.match(ch2)) {
                        int iMatchString3 = matchString(con, this.operations, matchStart, 1, this.options);
                        matchEnd2 = iMatchString3;
                        if (0 <= iMatchString3) {
                            break;
                        }
                    }
                    matchStart++;
                }
            }
        } else {
            matchStart = con.start;
            while (matchStart <= limit) {
                int iMatchString4 = matchString(con, this.operations, matchStart, 1, this.options);
                matchEnd2 = iMatchString4;
                if (0 <= iMatchString4) {
                    break;
                }
                matchStart++;
            }
        }
        if (matchEnd2 >= 0) {
            if (con.match != null) {
                con.match.setBeginning(0, matchStart);
                con.match.setEnd(0, matchEnd2);
            }
            con.inuse = false;
            return true;
        }
        con.inuse = false;
        return false;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private int matchString(Context con, Op op, int offset, int dx, int opts) {
        boolean matchp;
        int i;
        int i2;
        boolean go;
        int after;
        String target = con.strTarget;
        while (op != null) {
            if (offset > con.limit || offset < con.start) {
                return -1;
            }
            switch (op.type) {
                case 0:
                    if (dx > 0) {
                        if (offset >= con.limit) {
                            return -1;
                        }
                        int ch = target.charAt(offset);
                        if (isSet(opts, 4)) {
                            if (REUtil.isHighSurrogate(ch) && offset + 1 < con.limit) {
                                offset++;
                            }
                        } else {
                            if (REUtil.isHighSurrogate(ch) && offset + 1 < con.limit) {
                                offset++;
                                ch = REUtil.composeFromSurrogates(ch, target.charAt(offset));
                            }
                            if (isEOLChar(ch)) {
                                return -1;
                            }
                        }
                        offset++;
                    } else {
                        int o1 = offset - 1;
                        if (o1 >= con.limit || o1 < 0) {
                            return -1;
                        }
                        int ch2 = target.charAt(o1);
                        if (isSet(opts, 4)) {
                            if (REUtil.isLowSurrogate(ch2) && o1 - 1 >= 0) {
                                o1--;
                            }
                        } else {
                            if (REUtil.isLowSurrogate(ch2) && o1 - 1 >= 0) {
                                o1--;
                                ch2 = REUtil.composeFromSurrogates(target.charAt(o1), ch2);
                            }
                            if (!isEOLChar(ch2)) {
                                return -1;
                            }
                        }
                        offset = o1;
                    }
                    op = op.next;
                    break;
                case 1:
                    if (isSet(opts, 2)) {
                        int ch3 = op.getData();
                        if (dx > 0) {
                            if (offset >= con.limit || !matchIgnoreCase(ch3, target.charAt(offset))) {
                                return -1;
                            }
                            offset++;
                        } else {
                            int o2 = offset - 1;
                            if (o2 >= con.limit || o2 < 0 || !matchIgnoreCase(ch3, target.charAt(o2))) {
                                return -1;
                            }
                            offset = o2;
                        }
                    } else {
                        int ch4 = op.getData();
                        if (dx > 0) {
                            if (offset >= con.limit || ch4 != target.charAt(offset)) {
                                return -1;
                            }
                            offset++;
                        } else {
                            int o3 = offset - 1;
                            if (o3 >= con.limit || o3 < 0 || ch4 != target.charAt(o3)) {
                                return -1;
                            }
                            offset = o3;
                        }
                    }
                    op = op.next;
                    break;
                case 2:
                case 12:
                case 13:
                case 14:
                case 17:
                case 18:
                case 19:
                default:
                    throw new RuntimeException("Unknown operation type: " + op.type);
                case 3:
                case 4:
                    if (dx > 0) {
                        if (offset >= con.limit) {
                            return -1;
                        }
                        int ch5 = target.charAt(offset);
                        if (REUtil.isHighSurrogate(ch5) && offset + 1 < con.limit) {
                            offset++;
                            ch5 = REUtil.composeFromSurrogates(ch5, target.charAt(offset));
                        }
                        RangeToken tok = op.getToken();
                        if (isSet(opts, 2)) {
                            RangeToken tok2 = tok.getCaseInsensitiveToken();
                            if (!tok2.match(ch5)) {
                                if (ch5 >= 65536) {
                                    return -1;
                                }
                                char uch = Character.toUpperCase((char) ch5);
                                if (!tok2.match(uch) && !tok2.match(Character.toLowerCase(uch))) {
                                    return -1;
                                }
                            }
                        } else if (!tok.match(ch5)) {
                            return -1;
                        }
                        offset++;
                    } else {
                        int o4 = offset - 1;
                        if (o4 >= con.limit || o4 < 0) {
                            return -1;
                        }
                        int ch6 = target.charAt(o4);
                        if (REUtil.isLowSurrogate(ch6) && o4 - 1 >= 0) {
                            o4--;
                            ch6 = REUtil.composeFromSurrogates(target.charAt(o4), ch6);
                        }
                        RangeToken tok3 = op.getToken();
                        if (isSet(opts, 2)) {
                            RangeToken tok4 = tok3.getCaseInsensitiveToken();
                            if (!tok4.match(ch6)) {
                                if (ch6 >= 65536) {
                                    return -1;
                                }
                                char uch2 = Character.toUpperCase((char) ch6);
                                if (!tok4.match(uch2) && !tok4.match(Character.toLowerCase(uch2))) {
                                    return -1;
                                }
                            }
                        } else if (!tok3.match(ch6)) {
                            return -1;
                        }
                        offset = o4;
                    }
                    op = op.next;
                    break;
                case 5:
                    switch (op.getData()) {
                        case 36:
                            if (isSet(opts, 8)) {
                                if (offset != con.limit && (offset >= con.limit || !isEOLChar(target.charAt(offset)))) {
                                    return -1;
                                }
                            } else if (offset != con.limit && ((offset + 1 != con.limit || !isEOLChar(target.charAt(offset))) && (offset + 2 != con.limit || target.charAt(offset) != '\r' || target.charAt(offset + 1) != '\n'))) {
                                return -1;
                            }
                            op = op.next;
                        case 60:
                            if (con.length == 0 || offset == con.limit || getWordType(target, con.start, con.limit, offset, opts) != 1 || getPreviousWordType(target, con.start, con.limit, offset, opts) != 2) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 62:
                            if (con.length == 0 || offset == con.start || getWordType(target, con.start, con.limit, offset, opts) != 2 || getPreviousWordType(target, con.start, con.limit, offset, opts) != 1) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 64:
                            if (offset != con.start && (offset <= con.start || !isEOLChar(target.charAt(offset - 1)))) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 65:
                            if (offset != con.start) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 66:
                            if (con.length == 0) {
                                go = true;
                            } else {
                                int after2 = getWordType(target, con.start, con.limit, offset, opts);
                                go = after2 == 0 || after2 == getPreviousWordType(target, con.start, con.limit, offset, opts);
                            }
                            if (!go) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 90:
                            if (offset != con.limit && ((offset + 1 != con.limit || !isEOLChar(target.charAt(offset))) && (offset + 2 != con.limit || target.charAt(offset) != '\r' || target.charAt(offset + 1) != '\n'))) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 94:
                            if (isSet(opts, 8)) {
                                if (offset != con.start && (offset <= con.start || !isEOLChar(target.charAt(offset - 1)))) {
                                    return -1;
                                }
                            } else if (offset != con.start) {
                                return -1;
                            }
                            op = op.next;
                        case 98:
                            if (con.length == 0 || (after = getWordType(target, con.start, con.limit, offset, opts)) == 0) {
                                return -1;
                            }
                            int before = getPreviousWordType(target, con.start, con.limit, offset, opts);
                            if (after == before) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 122:
                            if (offset != con.limit) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        default:
                            op = op.next;
                            break;
                    }
                    break;
                case 6:
                    String literal = op.getString();
                    int literallen = literal.length();
                    if (!isSet(opts, 2)) {
                        if (dx > 0) {
                            if (!regionMatches(target, offset, con.limit, literal, literallen)) {
                                return -1;
                            }
                            i = offset + literallen;
                        } else {
                            if (!regionMatches(target, offset - literallen, con.limit, literal, literallen)) {
                                return -1;
                            }
                            i = offset - literallen;
                        }
                    } else if (dx > 0) {
                        if (!regionMatchesIgnoreCase(target, offset, con.limit, literal, literallen)) {
                            return -1;
                        }
                        i = offset + literallen;
                    } else {
                        if (!regionMatchesIgnoreCase(target, offset - literallen, con.limit, literal, literallen)) {
                            return -1;
                        }
                        i = offset - literallen;
                    }
                    offset = i;
                    op = op.next;
                    break;
                case 7:
                    int id = op.getData();
                    if (id >= 0) {
                        int previousOffset = con.offsets[id];
                        if (previousOffset < 0 || previousOffset != offset) {
                            con.offsets[id] = offset;
                        } else {
                            con.offsets[id] = -1;
                            op = op.next;
                        }
                        break;
                    }
                    int ret = matchString(con, op.getChild(), offset, dx, opts);
                    if (id >= 0) {
                        con.offsets[id] = -1;
                    }
                    if (ret >= 0) {
                        return ret;
                    }
                    op = op.next;
                    break;
                    break;
                case 8:
                case 10:
                    int ret2 = matchString(con, op.next, offset, dx, opts);
                    if (ret2 >= 0) {
                        return ret2;
                    }
                    op = op.getChild();
                    break;
                    break;
                case 9:
                    int ret3 = matchString(con, op.getChild(), offset, dx, opts);
                    if (ret3 >= 0) {
                        return ret3;
                    }
                    op = op.next;
                    break;
                    break;
                case 11:
                    for (int i3 = 0; i3 < op.size(); i3++) {
                        int ret4 = matchString(con, op.elementAt(i3), offset, dx, opts);
                        if (ret4 >= 0) {
                            return ret4;
                        }
                    }
                    return -1;
                case 15:
                    int refno = op.getData();
                    if (con.match != null && refno > 0) {
                        int save = con.match.getBeginning(refno);
                        con.match.setBeginning(refno, offset);
                        int ret5 = matchString(con, op.next, offset, dx, opts);
                        if (ret5 < 0) {
                            con.match.setBeginning(refno, save);
                        }
                        return ret5;
                    }
                    if (con.match != null && refno < 0) {
                        int index = -refno;
                        int save2 = con.match.getEnd(index);
                        con.match.setEnd(index, offset);
                        int ret6 = matchString(con, op.next, offset, dx, opts);
                        if (ret6 < 0) {
                            con.match.setEnd(index, save2);
                        }
                        return ret6;
                    }
                    op = op.next;
                    break;
                    break;
                case 16:
                    int refno2 = op.getData();
                    if (refno2 <= 0 || refno2 >= this.nofparen) {
                        throw new RuntimeException("Internal Error: Reference number must be more than zero: " + refno2);
                    }
                    if (con.match.getBeginning(refno2) < 0 || con.match.getEnd(refno2) < 0) {
                        return -1;
                    }
                    int o5 = con.match.getBeginning(refno2);
                    int literallen2 = con.match.getEnd(refno2) - o5;
                    if (!isSet(opts, 2)) {
                        if (dx > 0) {
                            if (!regionMatches(target, offset, con.limit, o5, literallen2)) {
                                return -1;
                            }
                            i2 = offset + literallen2;
                        } else {
                            if (!regionMatches(target, offset - literallen2, con.limit, o5, literallen2)) {
                                return -1;
                            }
                            i2 = offset - literallen2;
                        }
                    } else if (dx > 0) {
                        if (!regionMatchesIgnoreCase(target, offset, con.limit, o5, literallen2)) {
                            return -1;
                        }
                        i2 = offset + literallen2;
                    } else {
                        if (!regionMatchesIgnoreCase(target, offset - literallen2, con.limit, o5, literallen2)) {
                            return -1;
                        }
                        i2 = offset - literallen2;
                    }
                    offset = i2;
                    op = op.next;
                    break;
                    break;
                case 20:
                    if (0 > matchString(con, op.getChild(), offset, 1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    break;
                    break;
                case 21:
                    if (0 <= matchString(con, op.getChild(), offset, 1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    break;
                    break;
                case 22:
                    if (0 > matchString(con, op.getChild(), offset, -1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    break;
                    break;
                case 23:
                    if (0 <= matchString(con, op.getChild(), offset, -1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    break;
                    break;
                case 24:
                    int ret7 = matchString(con, op.getChild(), offset, dx, opts);
                    if (ret7 < 0) {
                        return ret7;
                    }
                    offset = ret7;
                    op = op.next;
                    break;
                    break;
                case 25:
                    int localopts = opts | op.getData();
                    int ret8 = matchString(con, op.getChild(), offset, dx, localopts & (op.getData2() ^ (-1)));
                    if (ret8 < 0) {
                        return ret8;
                    }
                    offset = ret8;
                    op = op.next;
                    break;
                    break;
                case 26:
                    Op.ConditionOp cop = (Op.ConditionOp) op;
                    if (cop.refNumber > 0) {
                        if (cop.refNumber >= this.nofparen) {
                            throw new RuntimeException("Internal Error: Reference number must be more than zero: " + cop.refNumber);
                        }
                        matchp = con.match.getBeginning(cop.refNumber) >= 0 && con.match.getEnd(cop.refNumber) >= 0;
                    } else {
                        matchp = 0 <= matchString(con, cop.condition, offset, dx, opts);
                    }
                    if (matchp) {
                        op = cop.yes;
                    } else if (cop.no != null) {
                        op = cop.no;
                    } else {
                        op = cop.next;
                    }
                    break;
            }
        }
        if (!isSet(opts, 512) || offset == con.limit) {
            return offset;
        }
        return -1;
    }

    private static final int getPreviousWordType(String target, int begin, int end, int offset, int opts) {
        int offset2 = offset - 1;
        int wordType = getWordType(target, begin, end, offset2, opts);
        while (true) {
            int ret = wordType;
            if (ret == 0) {
                offset2--;
                wordType = getWordType(target, begin, end, offset2, opts);
            } else {
                return ret;
            }
        }
    }

    private static final int getWordType(String target, int begin, int end, int offset, int opts) {
        if (offset < begin || offset >= end) {
            return 2;
        }
        return getWordType0(target.charAt(offset), opts);
    }

    private static final boolean regionMatches(String text, int offset, int limit, String part, int partlen) {
        if (limit - offset < partlen) {
            return false;
        }
        return text.regionMatches(offset, part, 0, partlen);
    }

    private static final boolean regionMatches(String text, int offset, int limit, int offset2, int partlen) {
        if (limit - offset < partlen) {
            return false;
        }
        return text.regionMatches(offset, text, offset2, partlen);
    }

    private static final boolean regionMatchesIgnoreCase(String text, int offset, int limit, String part, int partlen) {
        return text.regionMatches(true, offset, part, 0, partlen);
    }

    private static final boolean regionMatchesIgnoreCase(String text, int offset, int limit, int offset2, int partlen) {
        if (limit - offset < partlen) {
            return false;
        }
        return text.regionMatches(true, offset, text, offset2, partlen);
    }

    public boolean matches(CharacterIterator target) {
        return matches(target, (Match) null);
    }

    /* JADX WARN: Code duplicated, block: B:110:0x02cc  */
    /* JADX WARN: Code duplicated, block: B:154:0x038a A[EDGE_INSN: B:154:0x038a->B:137:0x038a BREAK  A[LOOP:1: B:94:0x025d->B:113:0x02e7], SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:156:0x02e7 A[SYNTHETIC] */
    public boolean matches(CharacterIterator target, Match match) {
        Context con;
        int matchStart;
        int iMatchCharacterIterator;
        boolean z;
        int start = target.getBeginIndex();
        int end = target.getEndIndex();
        synchronized (this) {
            if (this.operations == null) {
                prepare();
            }
            if (this.context == null) {
                this.context = new Context();
            }
        }
        synchronized (this.context) {
            con = this.context.inuse ? new Context() : this.context;
            con.reset(target, start, end, this.numberOfClosures);
        }
        if (match != null) {
            match.setNumberOfGroups(this.nofparen);
            match.setSource(target);
        } else if (this.hasBackReferences) {
            match = new Match();
            match.setNumberOfGroups(this.nofparen);
        }
        con.match = match;
        if (isSet(this.options, 512)) {
            int matchEnd = matchCharacterIterator(con, this.operations, con.start, 1, this.options);
            if (matchEnd == con.limit) {
                if (con.match != null) {
                    con.match.setBeginning(0, con.start);
                    con.match.setEnd(0, matchEnd);
                }
                con.inuse = false;
                return true;
            }
            return false;
        }
        if (this.fixedStringOnly) {
            int o = this.fixedStringTable.matches(target, con.start, con.limit);
            if (o >= 0) {
                if (con.match != null) {
                    con.match.setBeginning(0, o);
                    con.match.setEnd(0, o + this.fixedString.length());
                }
                con.inuse = false;
                return true;
            }
            con.inuse = false;
            return false;
        }
        if (this.fixedString != null && this.fixedStringTable.matches(target, con.start, con.limit) < 0) {
            con.inuse = false;
            return false;
        }
        int limit = con.limit - this.minlength;
        int matchEnd2 = -1;
        if (this.operations != null && this.operations.type == 7 && this.operations.getChild().type == 0) {
            if (isSet(this.options, 4)) {
                matchStart = con.start;
                matchEnd2 = matchCharacterIterator(con, this.operations, con.start, 1, this.options);
            } else {
                boolean previousIsEOL = true;
                matchStart = con.start;
                while (matchStart <= limit) {
                    if (isEOLChar(target.setIndex(matchStart))) {
                        z = true;
                    } else {
                        if (previousIsEOL) {
                            int iMatchCharacterIterator2 = matchCharacterIterator(con, this.operations, matchStart, 1, this.options);
                            matchEnd2 = iMatchCharacterIterator2;
                            if (0 <= iMatchCharacterIterator2) {
                                break;
                            }
                        }
                        z = false;
                    }
                    previousIsEOL = z;
                    matchStart++;
                }
            }
        } else if (this.firstChar != null) {
            RangeToken range = this.firstChar;
            if (isSet(this.options, 2)) {
                RangeToken range2 = this.firstChar.getCaseInsensitiveToken();
                matchStart = con.start;
                while (matchStart <= limit) {
                    int ch = target.setIndex(matchStart);
                    if (REUtil.isHighSurrogate(ch) && matchStart + 1 < con.limit) {
                        if (range2.match(REUtil.composeFromSurrogates(ch, target.setIndex(matchStart + 1)))) {
                            iMatchCharacterIterator = matchCharacterIterator(con, this.operations, matchStart, 1, this.options);
                            matchEnd2 = iMatchCharacterIterator;
                            if (0 <= iMatchCharacterIterator) {
                                break;
                                break;
                            }
                        } else {
                            continue;
                        }
                    } else if (!range2.match(ch)) {
                        char ch1 = Character.toUpperCase((char) ch);
                        if (range2.match(ch1) || range2.match(Character.toLowerCase(ch1))) {
                            iMatchCharacterIterator = matchCharacterIterator(con, this.operations, matchStart, 1, this.options);
                            matchEnd2 = iMatchCharacterIterator;
                            if (0 <= iMatchCharacterIterator) {
                                break;
                            }
                        }
                    } else {
                        iMatchCharacterIterator = matchCharacterIterator(con, this.operations, matchStart, 1, this.options);
                        matchEnd2 = iMatchCharacterIterator;
                        if (0 <= iMatchCharacterIterator) {
                            break;
                            break;
                        }
                    }
                    matchStart++;
                }
            } else {
                matchStart = con.start;
                while (matchStart <= limit) {
                    int ch2 = target.setIndex(matchStart);
                    if (REUtil.isHighSurrogate(ch2) && matchStart + 1 < con.limit) {
                        ch2 = REUtil.composeFromSurrogates(ch2, target.setIndex(matchStart + 1));
                    }
                    if (range.match(ch2)) {
                        int iMatchCharacterIterator3 = matchCharacterIterator(con, this.operations, matchStart, 1, this.options);
                        matchEnd2 = iMatchCharacterIterator3;
                        if (0 <= iMatchCharacterIterator3) {
                            break;
                        }
                    }
                    matchStart++;
                }
            }
        } else {
            matchStart = con.start;
            while (matchStart <= limit) {
                int iMatchCharacterIterator4 = matchCharacterIterator(con, this.operations, matchStart, 1, this.options);
                matchEnd2 = iMatchCharacterIterator4;
                if (0 <= iMatchCharacterIterator4) {
                    break;
                }
                matchStart++;
            }
        }
        if (matchEnd2 >= 0) {
            if (con.match != null) {
                con.match.setBeginning(0, matchStart);
                con.match.setEnd(0, matchEnd2);
            }
            con.inuse = false;
            return true;
        }
        con.inuse = false;
        return false;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private int matchCharacterIterator(Context con, Op op, int offset, int dx, int opts) {
        boolean matchp;
        int i;
        int i2;
        boolean go;
        int after;
        CharacterIterator target = con.ciTarget;
        while (op != null) {
            if (offset > con.limit || offset < con.start) {
                return -1;
            }
            switch (op.type) {
                case 0:
                    if (dx > 0) {
                        if (offset >= con.limit) {
                            return -1;
                        }
                        int ch = target.setIndex(offset);
                        if (isSet(opts, 4)) {
                            if (REUtil.isHighSurrogate(ch) && offset + 1 < con.limit) {
                                offset++;
                            }
                        } else {
                            if (REUtil.isHighSurrogate(ch) && offset + 1 < con.limit) {
                                offset++;
                                ch = REUtil.composeFromSurrogates(ch, target.setIndex(offset));
                            }
                            if (isEOLChar(ch)) {
                                return -1;
                            }
                        }
                        offset++;
                    } else {
                        int o1 = offset - 1;
                        if (o1 >= con.limit || o1 < 0) {
                            return -1;
                        }
                        int ch2 = target.setIndex(o1);
                        if (isSet(opts, 4)) {
                            if (REUtil.isLowSurrogate(ch2) && o1 - 1 >= 0) {
                                o1--;
                            }
                        } else {
                            if (REUtil.isLowSurrogate(ch2) && o1 - 1 >= 0) {
                                o1--;
                                ch2 = REUtil.composeFromSurrogates(target.setIndex(o1), ch2);
                            }
                            if (!isEOLChar(ch2)) {
                                return -1;
                            }
                        }
                        offset = o1;
                    }
                    op = op.next;
                    break;
                case 1:
                    if (isSet(opts, 2)) {
                        int ch3 = op.getData();
                        if (dx > 0) {
                            if (offset >= con.limit || !matchIgnoreCase(ch3, target.setIndex(offset))) {
                                return -1;
                            }
                            offset++;
                        } else {
                            int o2 = offset - 1;
                            if (o2 >= con.limit || o2 < 0 || !matchIgnoreCase(ch3, target.setIndex(o2))) {
                                return -1;
                            }
                            offset = o2;
                        }
                    } else {
                        int ch4 = op.getData();
                        if (dx > 0) {
                            if (offset >= con.limit || ch4 != target.setIndex(offset)) {
                                return -1;
                            }
                            offset++;
                        } else {
                            int o3 = offset - 1;
                            if (o3 >= con.limit || o3 < 0 || ch4 != target.setIndex(o3)) {
                                return -1;
                            }
                            offset = o3;
                        }
                    }
                    op = op.next;
                    break;
                case 2:
                case 12:
                case 13:
                case 14:
                case 17:
                case 18:
                case 19:
                default:
                    throw new RuntimeException("Unknown operation type: " + op.type);
                case 3:
                case 4:
                    if (dx > 0) {
                        if (offset >= con.limit) {
                            return -1;
                        }
                        int ch5 = target.setIndex(offset);
                        if (REUtil.isHighSurrogate(ch5) && offset + 1 < con.limit) {
                            offset++;
                            ch5 = REUtil.composeFromSurrogates(ch5, target.setIndex(offset));
                        }
                        RangeToken tok = op.getToken();
                        if (isSet(opts, 2)) {
                            RangeToken tok2 = tok.getCaseInsensitiveToken();
                            if (!tok2.match(ch5)) {
                                if (ch5 >= 65536) {
                                    return -1;
                                }
                                char uch = Character.toUpperCase((char) ch5);
                                if (!tok2.match(uch) && !tok2.match(Character.toLowerCase(uch))) {
                                    return -1;
                                }
                            }
                        } else if (!tok.match(ch5)) {
                            return -1;
                        }
                        offset++;
                    } else {
                        int o4 = offset - 1;
                        if (o4 >= con.limit || o4 < 0) {
                            return -1;
                        }
                        int ch6 = target.setIndex(o4);
                        if (REUtil.isLowSurrogate(ch6) && o4 - 1 >= 0) {
                            o4--;
                            ch6 = REUtil.composeFromSurrogates(target.setIndex(o4), ch6);
                        }
                        RangeToken tok3 = op.getToken();
                        if (isSet(opts, 2)) {
                            RangeToken tok4 = tok3.getCaseInsensitiveToken();
                            if (!tok4.match(ch6)) {
                                if (ch6 >= 65536) {
                                    return -1;
                                }
                                char uch2 = Character.toUpperCase((char) ch6);
                                if (!tok4.match(uch2) && !tok4.match(Character.toLowerCase(uch2))) {
                                    return -1;
                                }
                            }
                        } else if (!tok3.match(ch6)) {
                            return -1;
                        }
                        offset = o4;
                    }
                    op = op.next;
                    break;
                case 5:
                    switch (op.getData()) {
                        case 36:
                            if (isSet(opts, 8)) {
                                if (offset != con.limit && (offset >= con.limit || !isEOLChar(target.setIndex(offset)))) {
                                    return -1;
                                }
                            } else if (offset != con.limit && ((offset + 1 != con.limit || !isEOLChar(target.setIndex(offset))) && (offset + 2 != con.limit || target.setIndex(offset) != '\r' || target.setIndex(offset + 1) != '\n'))) {
                                return -1;
                            }
                            op = op.next;
                        case 60:
                            if (con.length == 0 || offset == con.limit || getWordType(target, con.start, con.limit, offset, opts) != 1 || getPreviousWordType(target, con.start, con.limit, offset, opts) != 2) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 62:
                            if (con.length == 0 || offset == con.start || getWordType(target, con.start, con.limit, offset, opts) != 2 || getPreviousWordType(target, con.start, con.limit, offset, opts) != 1) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 64:
                            if (offset != con.start && (offset <= con.start || !isEOLChar(target.setIndex(offset - 1)))) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 65:
                            if (offset != con.start) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 66:
                            if (con.length == 0) {
                                go = true;
                            } else {
                                int after2 = getWordType(target, con.start, con.limit, offset, opts);
                                go = after2 == 0 || after2 == getPreviousWordType(target, con.start, con.limit, offset, opts);
                            }
                            if (!go) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 90:
                            if (offset != con.limit && ((offset + 1 != con.limit || !isEOLChar(target.setIndex(offset))) && (offset + 2 != con.limit || target.setIndex(offset) != '\r' || target.setIndex(offset + 1) != '\n'))) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 94:
                            if (isSet(opts, 8)) {
                                if (offset != con.start && (offset <= con.start || !isEOLChar(target.setIndex(offset - 1)))) {
                                    return -1;
                                }
                            } else if (offset != con.start) {
                                return -1;
                            }
                            op = op.next;
                        case 98:
                            if (con.length == 0 || (after = getWordType(target, con.start, con.limit, offset, opts)) == 0) {
                                return -1;
                            }
                            int before = getPreviousWordType(target, con.start, con.limit, offset, opts);
                            if (after == before) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        case 122:
                            if (offset != con.limit) {
                                return -1;
                            }
                            op = op.next;
                            break;
                        default:
                            op = op.next;
                            break;
                    }
                    break;
                case 6:
                    String literal = op.getString();
                    int literallen = literal.length();
                    if (!isSet(opts, 2)) {
                        if (dx > 0) {
                            if (!regionMatches(target, offset, con.limit, literal, literallen)) {
                                return -1;
                            }
                            i = offset + literallen;
                        } else {
                            if (!regionMatches(target, offset - literallen, con.limit, literal, literallen)) {
                                return -1;
                            }
                            i = offset - literallen;
                        }
                    } else if (dx > 0) {
                        if (!regionMatchesIgnoreCase(target, offset, con.limit, literal, literallen)) {
                            return -1;
                        }
                        i = offset + literallen;
                    } else {
                        if (!regionMatchesIgnoreCase(target, offset - literallen, con.limit, literal, literallen)) {
                            return -1;
                        }
                        i = offset - literallen;
                    }
                    offset = i;
                    op = op.next;
                    break;
                case 7:
                    int id = op.getData();
                    if (id >= 0) {
                        int previousOffset = con.offsets[id];
                        if (previousOffset < 0 || previousOffset != offset) {
                            con.offsets[id] = offset;
                        } else {
                            con.offsets[id] = -1;
                            op = op.next;
                        }
                        break;
                    }
                    int ret = matchCharacterIterator(con, op.getChild(), offset, dx, opts);
                    if (id >= 0) {
                        con.offsets[id] = -1;
                    }
                    if (ret >= 0) {
                        return ret;
                    }
                    op = op.next;
                    break;
                    break;
                case 8:
                case 10:
                    int ret2 = matchCharacterIterator(con, op.next, offset, dx, opts);
                    if (ret2 >= 0) {
                        return ret2;
                    }
                    op = op.getChild();
                    break;
                    break;
                case 9:
                    int ret3 = matchCharacterIterator(con, op.getChild(), offset, dx, opts);
                    if (ret3 >= 0) {
                        return ret3;
                    }
                    op = op.next;
                    break;
                    break;
                case 11:
                    for (int i3 = 0; i3 < op.size(); i3++) {
                        int ret4 = matchCharacterIterator(con, op.elementAt(i3), offset, dx, opts);
                        if (ret4 >= 0) {
                            return ret4;
                        }
                    }
                    return -1;
                case 15:
                    int refno = op.getData();
                    if (con.match != null && refno > 0) {
                        int save = con.match.getBeginning(refno);
                        con.match.setBeginning(refno, offset);
                        int ret5 = matchCharacterIterator(con, op.next, offset, dx, opts);
                        if (ret5 < 0) {
                            con.match.setBeginning(refno, save);
                        }
                        return ret5;
                    }
                    if (con.match != null && refno < 0) {
                        int index = -refno;
                        int save2 = con.match.getEnd(index);
                        con.match.setEnd(index, offset);
                        int ret6 = matchCharacterIterator(con, op.next, offset, dx, opts);
                        if (ret6 < 0) {
                            con.match.setEnd(index, save2);
                        }
                        return ret6;
                    }
                    op = op.next;
                    break;
                    break;
                case 16:
                    int refno2 = op.getData();
                    if (refno2 <= 0 || refno2 >= this.nofparen) {
                        throw new RuntimeException("Internal Error: Reference number must be more than zero: " + refno2);
                    }
                    if (con.match.getBeginning(refno2) < 0 || con.match.getEnd(refno2) < 0) {
                        return -1;
                    }
                    int o5 = con.match.getBeginning(refno2);
                    int literallen2 = con.match.getEnd(refno2) - o5;
                    if (!isSet(opts, 2)) {
                        if (dx > 0) {
                            if (!regionMatches(target, offset, con.limit, o5, literallen2)) {
                                return -1;
                            }
                            i2 = offset + literallen2;
                        } else {
                            if (!regionMatches(target, offset - literallen2, con.limit, o5, literallen2)) {
                                return -1;
                            }
                            i2 = offset - literallen2;
                        }
                    } else if (dx > 0) {
                        if (!regionMatchesIgnoreCase(target, offset, con.limit, o5, literallen2)) {
                            return -1;
                        }
                        i2 = offset + literallen2;
                    } else {
                        if (!regionMatchesIgnoreCase(target, offset - literallen2, con.limit, o5, literallen2)) {
                            return -1;
                        }
                        i2 = offset - literallen2;
                    }
                    offset = i2;
                    op = op.next;
                    break;
                    break;
                case 20:
                    if (0 > matchCharacterIterator(con, op.getChild(), offset, 1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    break;
                    break;
                case 21:
                    if (0 <= matchCharacterIterator(con, op.getChild(), offset, 1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    break;
                    break;
                case 22:
                    if (0 > matchCharacterIterator(con, op.getChild(), offset, -1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    break;
                    break;
                case 23:
                    if (0 <= matchCharacterIterator(con, op.getChild(), offset, -1, opts)) {
                        return -1;
                    }
                    op = op.next;
                    break;
                    break;
                case 24:
                    int ret7 = matchCharacterIterator(con, op.getChild(), offset, dx, opts);
                    if (ret7 < 0) {
                        return ret7;
                    }
                    offset = ret7;
                    op = op.next;
                    break;
                    break;
                case 25:
                    int localopts = opts | op.getData();
                    int ret8 = matchCharacterIterator(con, op.getChild(), offset, dx, localopts & (op.getData2() ^ (-1)));
                    if (ret8 < 0) {
                        return ret8;
                    }
                    offset = ret8;
                    op = op.next;
                    break;
                    break;
                case 26:
                    Op.ConditionOp cop = (Op.ConditionOp) op;
                    if (cop.refNumber > 0) {
                        if (cop.refNumber >= this.nofparen) {
                            throw new RuntimeException("Internal Error: Reference number must be more than zero: " + cop.refNumber);
                        }
                        matchp = con.match.getBeginning(cop.refNumber) >= 0 && con.match.getEnd(cop.refNumber) >= 0;
                    } else {
                        matchp = 0 <= matchCharacterIterator(con, cop.condition, offset, dx, opts);
                    }
                    if (matchp) {
                        op = cop.yes;
                    } else if (cop.no != null) {
                        op = cop.no;
                    } else {
                        op = cop.next;
                    }
                    break;
            }
        }
        if (!isSet(opts, 512) || offset == con.limit) {
            return offset;
        }
        return -1;
    }

    private static final int getPreviousWordType(CharacterIterator target, int begin, int end, int offset, int opts) {
        int offset2 = offset - 1;
        int wordType = getWordType(target, begin, end, offset2, opts);
        while (true) {
            int ret = wordType;
            if (ret == 0) {
                offset2--;
                wordType = getWordType(target, begin, end, offset2, opts);
            } else {
                return ret;
            }
        }
    }

    private static final int getWordType(CharacterIterator target, int begin, int end, int offset, int opts) {
        if (offset < begin || offset >= end) {
            return 2;
        }
        return getWordType0(target.setIndex(offset), opts);
    }

    private static final boolean regionMatches(CharacterIterator target, int offset, int limit, String part, int partlen) {
        int i;
        int i2;
        if (offset < 0 || limit - offset < partlen) {
            return false;
        }
        int i3 = 0;
        do {
            int i4 = partlen;
            partlen--;
            if (i4 <= 0) {
                return true;
            }
            i = offset;
            offset++;
            i2 = i3;
            i3++;
        } while (target.setIndex(i) == part.charAt(i2));
        return false;
    }

    private static final boolean regionMatches(CharacterIterator target, int offset, int limit, int offset2, int partlen) {
        int i;
        int i2;
        if (offset < 0 || limit - offset < partlen) {
            return false;
        }
        int i3 = offset2;
        do {
            int i4 = partlen;
            partlen--;
            if (i4 <= 0) {
                return true;
            }
            i = offset;
            offset++;
            i2 = i3;
            i3++;
        } while (target.setIndex(i) == target.setIndex(i2));
        return false;
    }

    private static final boolean regionMatchesIgnoreCase(CharacterIterator target, int offset, int limit, String part, int partlen) {
        char uch1;
        char uch2;
        if (offset < 0 || limit - offset < partlen) {
            return false;
        }
        int i = 0;
        while (true) {
            int i2 = partlen;
            partlen--;
            if (i2 > 0) {
                int i3 = offset;
                offset++;
                char ch1 = target.setIndex(i3);
                int i4 = i;
                i++;
                char ch2 = part.charAt(i4);
                if (ch1 != ch2 && (uch1 = Character.toUpperCase(ch1)) != (uch2 = Character.toUpperCase(ch2)) && Character.toLowerCase(uch1) != Character.toLowerCase(uch2)) {
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    private static final boolean regionMatchesIgnoreCase(CharacterIterator target, int offset, int limit, int offset2, int partlen) {
        char uch1;
        char uch2;
        if (offset < 0 || limit - offset < partlen) {
            return false;
        }
        int i = offset2;
        while (true) {
            int i2 = partlen;
            partlen--;
            if (i2 > 0) {
                int i3 = offset;
                offset++;
                char ch1 = target.setIndex(i3);
                int i4 = i;
                i++;
                char ch2 = target.setIndex(i4);
                if (ch1 != ch2 && (uch1 = Character.toUpperCase(ch1)) != (uch2 = Character.toUpperCase(ch2)) && Character.toLowerCase(uch1) != Character.toLowerCase(uch2)) {
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/RegularExpression$Context.class */
    static final class Context {
        CharacterIterator ciTarget;
        String strTarget;
        char[] charTarget;
        int start;
        int limit;
        int length;
        Match match;
        boolean inuse = false;
        int[] offsets;

        Context() {
        }

        private void resetCommon(int nofclosures) {
            this.length = this.limit - this.start;
            this.inuse = true;
            this.match = null;
            if (this.offsets == null || this.offsets.length != nofclosures) {
                this.offsets = new int[nofclosures];
            }
            for (int i = 0; i < nofclosures; i++) {
                this.offsets[i] = -1;
            }
        }

        void reset(CharacterIterator target, int start, int limit, int nofclosures) {
            this.ciTarget = target;
            this.start = start;
            this.limit = limit;
            resetCommon(nofclosures);
        }

        void reset(String target, int start, int limit, int nofclosures) {
            this.strTarget = target;
            this.start = start;
            this.limit = limit;
            resetCommon(nofclosures);
        }

        void reset(char[] target, int start, int limit, int nofclosures) {
            this.charTarget = target;
            this.start = start;
            this.limit = limit;
            resetCommon(nofclosures);
        }
    }

    void prepare() {
        compile(this.tokentree);
        this.minlength = this.tokentree.getMinLength();
        this.firstChar = null;
        if (!isSet(this.options, 128) && !isSet(this.options, 512)) {
            RangeToken firstChar = Token.createRange();
            int fresult = this.tokentree.analyzeFirstCharacter(firstChar, this.options);
            if (fresult == 1) {
                firstChar.compactRanges();
                this.firstChar = firstChar;
            }
        }
        if (this.operations != null && ((this.operations.type == 6 || this.operations.type == 1) && this.operations.next == null)) {
            this.fixedStringOnly = true;
            if (this.operations.type == 6) {
                this.fixedString = this.operations.getString();
            } else if (this.operations.getData() >= 65536) {
                this.fixedString = REUtil.decomposeToSurrogates(this.operations.getData());
            } else {
                char[] ac = {(char) this.operations.getData()};
                this.fixedString = new String(ac);
            }
            this.fixedStringOptions = this.options;
            this.fixedStringTable = new BMPattern(this.fixedString, 256, isSet(this.fixedStringOptions, 2));
            return;
        }
        if (!isSet(this.options, 256) && !isSet(this.options, 512)) {
            Token.FixedStringContainer container = new Token.FixedStringContainer();
            this.tokentree.findFixedString(container, this.options);
            this.fixedString = container.token == null ? null : container.token.getString();
            this.fixedStringOptions = container.options;
            if (this.fixedString != null && this.fixedString.length() < 2) {
                this.fixedString = null;
            }
            if (this.fixedString != null) {
                this.fixedStringTable = new BMPattern(this.fixedString, 256, isSet(this.fixedStringOptions, 2));
            }
        }
    }

    private static final boolean isSet(int options, int flag) {
        return (options & flag) == flag;
    }

    public RegularExpression(String regex) throws ParseException {
        this.hasBackReferences = false;
        this.operations = null;
        this.context = null;
        this.firstChar = null;
        this.fixedString = null;
        this.fixedStringTable = null;
        this.fixedStringOnly = false;
        setPattern(regex, (String) null);
    }

    public RegularExpression(String regex, String options) throws ParseException {
        this.hasBackReferences = false;
        this.operations = null;
        this.context = null;
        this.firstChar = null;
        this.fixedString = null;
        this.fixedStringTable = null;
        this.fixedStringOnly = false;
        setPattern(regex, options);
    }

    RegularExpression(String regex, Token tok, int parens, boolean hasBackReferences, int options) {
        this.hasBackReferences = false;
        this.operations = null;
        this.context = null;
        this.firstChar = null;
        this.fixedString = null;
        this.fixedStringTable = null;
        this.fixedStringOnly = false;
        this.regex = regex;
        this.tokentree = tok;
        this.nofparen = parens;
        this.options = options;
        this.hasBackReferences = hasBackReferences;
    }

    public void setPattern(String newPattern) throws ParseException {
        setPattern(newPattern, this.options);
    }

    private void setPattern(String newPattern, int options) throws ParseException {
        this.regex = newPattern;
        this.options = options;
        RegexParser rp = isSet(this.options, 512) ? new ParserForXMLSchema() : new RegexParser();
        this.tokentree = rp.parse(this.regex, this.options);
        this.nofparen = rp.parennumber;
        this.hasBackReferences = rp.hasBackReferences;
        this.operations = null;
        this.context = null;
    }

    public void setPattern(String newPattern, String options) throws ParseException {
        setPattern(newPattern, REUtil.parseOptions(options));
    }

    public String getPattern() {
        return this.regex;
    }

    public String toString() {
        return this.tokentree.toString(this.options);
    }

    public String getOptions() {
        return REUtil.createOptionString(this.options);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof RegularExpression)) {
            return false;
        }
        RegularExpression r = (RegularExpression) obj;
        return this.regex.equals(r.regex) && this.options == r.options;
    }

    boolean equals(String pattern, int options) {
        return this.regex.equals(pattern) && this.options == options;
    }

    public int hashCode() {
        return (this.regex + "/" + getOptions()).hashCode();
    }

    public int getNumberOfGroups() {
        return this.nofparen;
    }

    private static final int getWordType0(char ch, int opts) {
        if (!isSet(opts, 64)) {
            if (isSet(opts, 32)) {
                return Token.getRange("IsWord", true).match(ch) ? 1 : 2;
            }
            return isWordChar(ch) ? 1 : 2;
        }
        switch (Character.getType(ch)) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 8:
            case 9:
            case 10:
            case 11:
                return 1;
            case 6:
            case 7:
            case 16:
                return 0;
            case 12:
            case 13:
            case 14:
            default:
                return 2;
            case 15:
                switch (ch) {
                    case '\t':
                    case '\n':
                    case 11:
                    case '\f':
                    case '\r':
                        return 2;
                    default:
                        return 0;
                }
        }
    }

    private static final boolean isEOLChar(int ch) {
        return ch == 10 || ch == 13 || ch == 8232 || ch == 8233;
    }

    private static final boolean isWordChar(int ch) {
        if (ch == 95) {
            return true;
        }
        if (ch < 48 || ch > 122) {
            return false;
        }
        if (ch <= 57) {
            return true;
        }
        if (ch < 65) {
            return false;
        }
        return ch <= 90 || ch >= 97;
    }

    private static final boolean matchIgnoreCase(int chardata, int ch) {
        if (chardata == ch) {
            return true;
        }
        if (chardata > 65535 || ch > 65535) {
            return false;
        }
        char uch1 = Character.toUpperCase((char) chardata);
        char uch2 = Character.toUpperCase((char) ch);
        return uch1 == uch2 || Character.toLowerCase(uch1) == Character.toLowerCase(uch2);
    }
}
