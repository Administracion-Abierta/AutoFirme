package com.ctc.wstx.shaded.msv_core.datatype.regexp;

import com.sun.jna.platform.win32.WinNT;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.commons.codec.language.bm.Rule;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.pdmodel.interactive.action.PDWindowsLaunchParams;
import org.ini4j.Config;
import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/Token.class */
class Token implements Serializable {
    static final boolean COUNTTOKENS = true;
    static final int CHAR = 0;
    static final int DOT = 11;
    static final int CONCAT = 1;
    static final int UNION = 2;
    static final int CLOSURE = 3;
    static final int RANGE = 4;
    static final int NRANGE = 5;
    static final int PAREN = 6;
    static final int EMPTY = 7;
    static final int ANCHOR = 8;
    static final int NONGREEDYCLOSURE = 9;
    static final int STRING = 10;
    static final int BACKREFERENCE = 12;
    static final int LOOKAHEAD = 20;
    static final int NEGATIVELOOKAHEAD = 21;
    static final int LOOKBEHIND = 22;
    static final int NEGATIVELOOKBEHIND = 23;
    static final int INDEPENDENT = 24;
    static final int MODIFIERGROUP = 25;
    static final int CONDITION = 26;
    static final int UTF16_MAX = 1114111;
    int type;
    static Token token_wordchars;
    static Token token_not_0to9;
    static Token token_not_wordchars;
    static Token token_spaces;
    static Token token_not_spaces;
    static final int FC_CONTINUE = 0;
    static final int FC_TERMINAL = 1;
    static final int FC_ANY = 2;
    private static final Hashtable categories;
    private static final Hashtable categories2;
    private static final String[] categoryNames;
    static final int CHAR_INIT_QUOTE = 29;
    static final int CHAR_FINAL_QUOTE = 30;
    static final int CHAR_LETTER = 31;
    static final int CHAR_MARK = 32;
    static final int CHAR_NUMBER = 33;
    static final int CHAR_SEPARATOR = 34;
    static final int CHAR_OTHER = 35;
    static final int CHAR_PUNCTUATION = 36;
    static final int CHAR_SYMBOL = 37;
    private static final String[] blockNames;
    static final String blockRanges = "\u0000\u007f\u0080ÿĀſƀɏɐʯʰ˿̀ͯͰϿЀӿ\u0530֏\u0590\u05ff\u0600ۿ܀ݏހ\u07bfऀॿঀ\u09ff\u0a00\u0a7f\u0a80૿\u0b00\u0b7f\u0b80\u0bffఀ౿ಀ\u0cffഀൿ\u0d80\u0dff\u0e00\u0e7f\u0e80\u0effༀ\u0fffက႟Ⴀჿᄀᇿሀ\u137fᎠ\u13ff᐀ᙿ\u1680\u169fᚠ\u16ffក\u17ff᠀\u18afḀỿἀ\u1fff\u2000\u206f⁰\u209f₠\u20cf⃐\u20ff℀⅏⅐\u218f←⇿∀⋿⌀⏿␀\u243f⑀\u245f①⓿─╿▀▟■◿☀⛿✀➿⠀⣿⺀\u2eff⼀\u2fdf⿰⿿\u3000〿\u3040ゟ゠ヿ\u3100ㄯ\u3130\u318f㆐㆟ㆠㆿ㈀㋿㌀㏿㐀䶵一鿿ꀀ\ua48f꒐\ua4cf가힣\ue000\uf8ff豈\ufaffﬀﭏﭐ﷿︠︯︰﹏﹐\ufe6fﹰ\ufefe\ufeff\ufeff\uff00\uffef";
    static final int[] nonBMPBlockRanges;
    private static final int NONBMP_BLOCK_START = 84;
    static Hashtable nonxs;
    static final String viramaString = "्্੍્୍்్್്ฺ྄";
    private static Token token_grapheme;
    private static Token token_ccs;
    static int tokens = 0;
    static Token token_empty = new Token(7);
    static Token token_linebeginning = createAnchor(94);
    static Token token_linebeginning2 = createAnchor(64);
    static Token token_lineend = createAnchor(36);
    static Token token_stringbeginning = createAnchor(65);
    static Token token_stringend = createAnchor(122);
    static Token token_stringend2 = createAnchor(90);
    static Token token_wordedge = createAnchor(98);
    static Token token_not_wordedge = createAnchor(66);
    static Token token_wordbeginning = createAnchor(60);
    static Token token_wordend = createAnchor(62);
    static Token token_dot = new Token(11);
    static Token token_0to9 = createRange();

    static {
        token_0to9.addRange(48, 57);
        token_wordchars = createRange();
        token_wordchars.addRange(48, 57);
        token_wordchars.addRange(65, 90);
        token_wordchars.addRange(95, 95);
        token_wordchars.addRange(97, 122);
        token_spaces = createRange();
        token_spaces.addRange(9, 9);
        token_spaces.addRange(10, 10);
        token_spaces.addRange(12, 12);
        token_spaces.addRange(13, 13);
        token_spaces.addRange(32, 32);
        token_not_0to9 = complementRanges(token_0to9);
        token_not_wordchars = complementRanges(token_wordchars);
        token_not_spaces = complementRanges(token_spaces);
        categories = new Hashtable();
        categories2 = new Hashtable();
        categoryNames = new String[]{"Cn", "Lu", "Ll", "Lt", "Lm", "Lo", "Mn", "Me", "Mc", "Nd", "Nl", "No", "Zs", "Zl", "Zp", "Cc", "Cf", null, "Co", "Cs", "Pd", "Ps", "Pe", "Pc", "Po", "Sm", "Sc", "Sk", "So", "Pi", "Pf", "L", OperatorName.SET_LINE_MITERLIMIT, "N", "Z", "C", "P", "S"};
        blockNames = new String[]{"Basic Latin", "Latin-1 Supplement", "Latin Extended-A", "Latin Extended-B", "IPA Extensions", "Spacing Modifier Letters", "Combining Diacritical Marks", "Greek", "Cyrillic", "Armenian", "Hebrew", "Arabic", "Syriac", "Thaana", "Devanagari", "Bengali", "Gurmukhi", "Gujarati", "Oriya", "Tamil", "Telugu", "Kannada", "Malayalam", "Sinhala", "Thai", "Lao", "Tibetan", "Myanmar", "Georgian", "Hangul Jamo", "Ethiopic", "Cherokee", "Unified Canadian Aboriginal Syllabics", "Ogham", "Runic", "Khmer", "Mongolian", "Latin Extended Additional", "Greek Extended", "General Punctuation", "Superscripts and Subscripts", "Currency Symbols", "Combining Marks for Symbols", "Letterlike Symbols", "Number Forms", "Arrows", "Mathematical Operators", "Miscellaneous Technical", "Control Pictures", "Optical Character Recognition", "Enclosed Alphanumerics", "Box Drawing", "Block Elements", "Geometric Shapes", "Miscellaneous Symbols", "Dingbats", "Braille Patterns", "CJK Radicals Supplement", "Kangxi Radicals", "Ideographic Description Characters", "CJK Symbols and Punctuation", "Hiragana", "Katakana", "Bopomofo", "Hangul Compatibility Jamo", "Kanbun", "Bopomofo Extended", "Enclosed CJK Letters and Months", "CJK Compatibility", "CJK Unified Ideographs Extension A", "CJK Unified Ideographs", "Yi Syllables", "Yi Radicals", "Hangul Syllables", "Private Use", "CJK Compatibility Ideographs", "Alphabetic Presentation Forms", "Arabic Presentation Forms-A", "Combining Half Marks", "CJK Compatibility Forms", "Small Form Variants", "Arabic Presentation Forms-B", "Specials", "Halfwidth and Fullwidth Forms", "Old Italic", "Gothic", "Deseret", "Byzantine Musical Symbols", "Musical Symbols", "Mathematical Alphanumeric Symbols", "CJK Unified Ideographs Extension B", "CJK Compatibility Ideographs Supplement", "Tags"};
        nonBMPBlockRanges = new int[]{66304, 66351, 66352, 66383, 66560, 66639, 118784, 119039, 119040, 119295, 119808, 120831, 131072, 173782, 194560, 195103, 917504, 917631};
        nonxs = null;
        token_grapheme = null;
        token_ccs = null;
    }

    static ParenToken createLook(int type, Token child) {
        tokens++;
        return new ParenToken(type, child, 0);
    }

    static ParenToken createParen(Token child, int pnumber) {
        tokens++;
        return new ParenToken(6, child, pnumber);
    }

    static ClosureToken createClosure(Token tok) {
        tokens++;
        return new ClosureToken(3, tok);
    }

    static ClosureToken createNGClosure(Token tok) {
        tokens++;
        return new ClosureToken(9, tok);
    }

    static ConcatToken createConcat(Token tok1, Token tok2) {
        tokens++;
        return new ConcatToken(tok1, tok2);
    }

    static UnionToken createConcat() {
        tokens++;
        return new UnionToken(1);
    }

    static UnionToken createUnion() {
        tokens++;
        return new UnionToken(2);
    }

    static Token createEmpty() {
        return token_empty;
    }

    static RangeToken createRange() {
        tokens++;
        return new RangeToken(4);
    }

    static RangeToken createNRange() {
        tokens++;
        return new RangeToken(5);
    }

    static CharToken createChar(int ch) {
        tokens++;
        return new CharToken(0, ch);
    }

    private static CharToken createAnchor(int ch) {
        tokens++;
        return new CharToken(8, ch);
    }

    static StringToken createBackReference(int refno) {
        tokens++;
        return new StringToken(12, null, refno);
    }

    static StringToken createString(String str) {
        tokens++;
        return new StringToken(10, str, 0);
    }

    static ModifierToken createModifierGroup(Token child, int add, int mask) {
        tokens++;
        return new ModifierToken(child, add, mask);
    }

    static ConditionToken createCondition(int refno, Token condition, Token yespat, Token nopat) {
        tokens++;
        return new ConditionToken(refno, condition, yespat, nopat);
    }

    protected Token(int type) {
        this.type = type;
    }

    int size() {
        return 0;
    }

    Token getChild(int index) {
        return null;
    }

    void addChild(Token tok) {
        throw new RuntimeException("Not supported.");
    }

    protected void addRange(int start, int end) {
        throw new RuntimeException("Not supported.");
    }

    protected void sortRanges() {
        throw new RuntimeException("Not supported.");
    }

    protected void compactRanges() {
        throw new RuntimeException("Not supported.");
    }

    protected void mergeRanges(Token tok) {
        throw new RuntimeException("Not supported.");
    }

    protected void subtractRanges(Token tok) {
        throw new RuntimeException("Not supported.");
    }

    protected void intersectRanges(Token tok) {
        throw new RuntimeException("Not supported.");
    }

    static Token complementRanges(Token tok) {
        return RangeToken.complementRanges(tok);
    }

    void setMin(int min) {
    }

    void setMax(int max) {
    }

    int getMin() {
        return -1;
    }

    int getMax() {
        return -1;
    }

    int getReferenceNumber() {
        return 0;
    }

    String getString() {
        return null;
    }

    int getParenNumber() {
        return 0;
    }

    int getChar() {
        return -1;
    }

    public String toString() {
        return toString(0);
    }

    public String toString(int options) {
        return this.type == 11 ? "." : "";
    }

    final int getMinLength() {
        switch (this.type) {
            case 0:
            case 4:
            case 5:
            case 11:
                return 1;
            case 1:
                int sum = 0;
                for (int i = 0; i < size(); i++) {
                    sum += getChild(i).getMinLength();
                }
                return sum;
            case 2:
            case 26:
                if (size() == 0) {
                    return 0;
                }
                int ret = getChild(0).getMinLength();
                for (int i2 = 1; i2 < size(); i2++) {
                    int min = getChild(i2).getMinLength();
                    if (min < ret) {
                        ret = min;
                    }
                }
                return ret;
            case 3:
            case 9:
                if (getMin() >= 0) {
                    return getMin() * getChild(0).getMinLength();
                }
                return 0;
            case 6:
            case 24:
            case 25:
                return getChild(0).getMinLength();
            case 7:
            case 8:
                return 0;
            case 10:
                return getString().length();
            case 12:
                return 0;
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            default:
                throw new RuntimeException("Token#getMinLength(): Invalid Type: " + this.type);
            case 20:
            case 21:
            case 22:
            case 23:
                return 0;
        }
    }

    final int getMaxLength() {
        switch (this.type) {
            case 0:
                return 1;
            case 1:
                int sum = 0;
                for (int i = 0; i < size(); i++) {
                    int d = getChild(i).getMaxLength();
                    if (d < 0) {
                        return -1;
                    }
                    sum += d;
                }
                return sum;
            case 2:
            case 26:
                if (size() == 0) {
                    return 0;
                }
                int ret = getChild(0).getMaxLength();
                for (int i2 = 1; ret >= 0 && i2 < size(); i2++) {
                    int max = getChild(i2).getMaxLength();
                    if (max < 0) {
                        ret = -1;
                        return ret;
                    }
                    if (max > ret) {
                        ret = max;
                    }
                }
                return ret;
            case 3:
            case 9:
                if (getMax() >= 0) {
                    return getMax() * getChild(0).getMaxLength();
                }
                return -1;
            case 4:
            case 5:
            case 11:
                return 2;
            case 6:
            case 24:
            case 25:
                return getChild(0).getMaxLength();
            case 7:
            case 8:
                return 0;
            case 10:
                return getString().length();
            case 12:
                return -1;
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            default:
                throw new RuntimeException("Token#getMaxLength(): Invalid Type: " + this.type);
            case 20:
            case 21:
            case 22:
            case 23:
                return 0;
        }
    }

    private static final boolean isSet(int options, int flag) {
        return (options & flag) == flag;
    }

    /* JADX WARN: Code duplicated, block: B:29:0x00ed A[RETURN, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:30:0x00f1  */
    final int analyzeFirstCharacter(RangeToken result, int options) {
        switch (this.type) {
            case 0:
                int ch = getChar();
                result.addRange(ch, ch);
                if (ch < 65536 && isSet(options, 2)) {
                    int ch2 = Character.toUpperCase((char) ch);
                    result.addRange(ch2, ch2);
                    int ch3 = Character.toLowerCase((char) ch2);
                    result.addRange(ch3, ch3);
                    return 1;
                }
                return 1;
            case 1:
                int ret = 0;
                for (int i = 0; i < size(); i++) {
                    int iAnalyzeFirstCharacter = getChild(i).analyzeFirstCharacter(result, options);
                    ret = iAnalyzeFirstCharacter;
                    if (iAnalyzeFirstCharacter != 0) {
                        return ret;
                    }
                }
                return ret;
            case 2:
                if (size() == 0) {
                    return 0;
                }
                int ret2 = 0;
                boolean hasEmpty = false;
                for (int i2 = 0; i2 < size(); i2++) {
                    ret2 = getChild(i2).analyzeFirstCharacter(result, options);
                    if (ret2 != 2) {
                        if (ret2 == 0) {
                            hasEmpty = true;
                        }
                    } else {
                        if (hasEmpty) {
                            return 0;
                        }
                        return ret2;
                    }
                }
                if (hasEmpty) {
                    return 0;
                }
                return ret2;
            case 3:
            case 9:
                getChild(0).analyzeFirstCharacter(result, options);
                return 0;
            case 4:
                if (isSet(options, 2)) {
                    result.mergeRanges(((RangeToken) this).getCaseInsensitiveToken());
                    return 1;
                }
                result.mergeRanges(this);
                return 1;
            case 5:
                if (isSet(options, 2)) {
                    result.mergeRanges(complementRanges(((RangeToken) this).getCaseInsensitiveToken()));
                    return 1;
                }
                result.mergeRanges(complementRanges(this));
                return 1;
            case 6:
            case 24:
                return getChild(0).analyzeFirstCharacter(result, options);
            case 7:
            case 8:
                return 0;
            case 10:
                int cha = getString().charAt(0);
                if (REUtil.isHighSurrogate(cha) && getString().length() >= 2) {
                    int ch4 = getString().charAt(1);
                    if (REUtil.isLowSurrogate(ch4)) {
                        cha = REUtil.composeFromSurrogates(cha, ch4);
                    }
                }
                result.addRange(cha, cha);
                if (cha < 65536 && isSet(options, 2)) {
                    int cha2 = Character.toUpperCase((char) cha);
                    result.addRange(cha2, cha2);
                    int cha3 = Character.toLowerCase((char) cha2);
                    result.addRange(cha3, cha3);
                    return 1;
                }
                return 1;
            case 11:
                if (isSet(options, 4)) {
                    return 0;
                }
                return 0;
            case 12:
                result.addRange(0, 1114111);
                return 2;
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            default:
                throw new RuntimeException("Token#analyzeHeadCharacter(): Invalid Type: " + this.type);
            case 20:
            case 21:
            case 22:
            case 23:
                return 0;
            case 25:
                return getChild(0).analyzeFirstCharacter(result, (options | ((ModifierToken) this).getOptions()) & (((ModifierToken) this).getOptionsMask() ^ (-1)));
            case 26:
                int ret3 = getChild(0).analyzeFirstCharacter(result, options);
                if (size() == 1) {
                    return 0;
                }
                if (ret3 == 2) {
                    return ret3;
                }
                int ret4 = getChild(1).analyzeFirstCharacter(result, options);
                if (ret4 == 2) {
                    return ret4;
                }
                return (ret3 == 0 || ret4 == 0) ? 0 : 1;
        }
    }

    private final boolean isShorterThan(Token tok) {
        if (tok == null) {
            return false;
        }
        if (this.type != 10) {
            throw new RuntimeException("Internal Error: Illegal type: " + this.type);
        }
        int mylength = getString().length();
        if (tok.type != 10) {
            throw new RuntimeException("Internal Error: Illegal type: " + tok.type);
        }
        int otherlength = tok.getString().length();
        return mylength < otherlength;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/Token$FixedStringContainer.class */
    static class FixedStringContainer {
        Token token = null;
        int options = 0;

        FixedStringContainer() {
        }
    }

    final void findFixedString(FixedStringContainer container, int options) {
        switch (this.type) {
            case 0:
                container.token = null;
                return;
            case 1:
                Token prevToken = null;
                int prevOptions = 0;
                for (int i = 0; i < size(); i++) {
                    getChild(i).findFixedString(container, options);
                    if (prevToken == null || prevToken.isShorterThan(container.token)) {
                        prevToken = container.token;
                        prevOptions = container.options;
                    }
                }
                container.token = prevToken;
                container.options = prevOptions;
                return;
            case 2:
            case 3:
            case 4:
            case 5:
            case 7:
            case 8:
            case 9:
            case 11:
            case 12:
            case 20:
            case 21:
            case 22:
            case 23:
            case 26:
                container.token = null;
                return;
            case 6:
            case 24:
                getChild(0).findFixedString(container, options);
                return;
            case 10:
                container.token = this;
                container.options = options;
                return;
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            default:
                throw new RuntimeException("Token#findFixedString(): Invalid Type: " + this.type);
            case 25:
                getChild(0).findFixedString(container, (options | ((ModifierToken) this).getOptions()) & (((ModifierToken) this).getOptionsMask() ^ (-1)));
                return;
        }
    }

    boolean match(int ch) {
        throw new RuntimeException("NFAArrow#match(): Internal error: " + this.type);
    }

    protected static RangeToken getRange(String name, boolean positive) {
        int type;
        if (categories.size() == 0) {
            synchronized (categories) {
                Token[] ranges = new Token[categoryNames.length];
                for (int i = 0; i < ranges.length; i++) {
                    ranges[i] = createRange();
                }
                for (int i2 = 0; i2 < 65536; i2++) {
                    int type2 = Character.getType((char) i2);
                    if (type2 == 21 || type2 == 22) {
                        if (i2 == 171 || i2 == 8216 || i2 == 8219 || i2 == 8220 || i2 == 8223 || i2 == 8249) {
                            type2 = 29;
                        }
                        if (i2 == 187 || i2 == 8217 || i2 == 8221 || i2 == 8250) {
                            type2 = 30;
                        }
                    }
                    ranges[type2].addRange(i2, i2);
                    switch (type2) {
                        case 0:
                        case 15:
                        case 16:
                        case 18:
                        case 19:
                            type = 35;
                            break;
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                            type = 31;
                            break;
                        case 6:
                        case 7:
                        case 8:
                            type = 32;
                            break;
                        case 9:
                        case 10:
                        case 11:
                            type = 33;
                            break;
                        case 12:
                        case 13:
                        case 14:
                            type = 34;
                            break;
                        case 17:
                        default:
                            throw new RuntimeException("org.apache.xerces.utils.regex.Token#getRange(): Unknown Unicode category: " + type2);
                        case 20:
                        case 21:
                        case 22:
                        case 23:
                        case 24:
                        case 29:
                        case 30:
                            type = 36;
                            break;
                        case 25:
                        case 26:
                        case 27:
                        case 28:
                            type = 37;
                            break;
                    }
                    ranges[type].addRange(i2, i2);
                }
                ranges[0].addRange(65536, 1114111);
                for (int i3 = 0; i3 < ranges.length; i3++) {
                    if (categoryNames[i3] != null) {
                        if (i3 == 0) {
                            ranges[i3].addRange(65536, 1114111);
                        }
                        categories.put(categoryNames[i3], ranges[i3]);
                        categories2.put(categoryNames[i3], complementRanges(ranges[i3]));
                    }
                }
                StringBuffer buffer = new StringBuffer(50);
                for (int i4 = 0; i4 < blockNames.length; i4++) {
                    RangeToken rangeTokenCreateRange = createRange();
                    if (i4 < 84) {
                        int location = i4 * 2;
                        int rstart = blockRanges.charAt(location);
                        int rend = blockRanges.charAt(location + 1);
                        rangeTokenCreateRange.addRange(rstart, rend);
                    } else {
                        int location2 = (i4 - 84) * 2;
                        rangeTokenCreateRange.addRange(nonBMPBlockRanges[location2], nonBMPBlockRanges[location2 + 1]);
                    }
                    String n = blockNames[i4];
                    if (n.equals("Specials")) {
                        rangeTokenCreateRange.addRange(65520, 65533);
                    }
                    if (n.equals("Private Use")) {
                        rangeTokenCreateRange.addRange(WinNT.STANDARD_RIGHTS_REQUIRED, 1048573);
                        rangeTokenCreateRange.addRange(1048576, 1114109);
                    }
                    categories.put(n, rangeTokenCreateRange);
                    categories2.put(n, complementRanges(rangeTokenCreateRange));
                    buffer.setLength(0);
                    buffer.append("Is");
                    if (n.indexOf(32) >= 0) {
                        for (int ci = 0; ci < n.length(); ci++) {
                            if (n.charAt(ci) != ' ') {
                                buffer.append(n.charAt(ci));
                            }
                        }
                    } else {
                        buffer.append(n);
                    }
                    setAlias(buffer.toString(), n, true);
                }
                setAlias("ASSIGNED", "Cn", false);
                setAlias("UNASSIGNED", "Cn", true);
                RangeToken rangeTokenCreateRange2 = createRange();
                rangeTokenCreateRange2.addRange(0, 1114111);
                categories.put(Rule.ALL, rangeTokenCreateRange2);
                categories2.put(Rule.ALL, complementRanges(rangeTokenCreateRange2));
                registerNonXS("ASSIGNED");
                registerNonXS("UNASSIGNED");
                registerNonXS(Rule.ALL);
                RangeToken rangeTokenCreateRange3 = createRange();
                rangeTokenCreateRange3.mergeRanges(ranges[1]);
                rangeTokenCreateRange3.mergeRanges(ranges[2]);
                rangeTokenCreateRange3.mergeRanges(ranges[5]);
                categories.put("IsAlpha", rangeTokenCreateRange3);
                categories2.put("IsAlpha", complementRanges(rangeTokenCreateRange3));
                registerNonXS("IsAlpha");
                RangeToken rangeTokenCreateRange4 = createRange();
                rangeTokenCreateRange4.mergeRanges(rangeTokenCreateRange3);
                rangeTokenCreateRange4.mergeRanges(ranges[9]);
                categories.put("IsAlnum", rangeTokenCreateRange4);
                categories2.put("IsAlnum", complementRanges(rangeTokenCreateRange4));
                registerNonXS("IsAlnum");
                RangeToken rangeTokenCreateRange5 = createRange();
                rangeTokenCreateRange5.mergeRanges(token_spaces);
                rangeTokenCreateRange5.mergeRanges(ranges[34]);
                categories.put("IsSpace", rangeTokenCreateRange5);
                categories2.put("IsSpace", complementRanges(rangeTokenCreateRange5));
                registerNonXS("IsSpace");
                RangeToken rangeTokenCreateRange6 = createRange();
                rangeTokenCreateRange6.mergeRanges(rangeTokenCreateRange4);
                rangeTokenCreateRange6.addRange(95, 95);
                categories.put("IsWord", rangeTokenCreateRange6);
                categories2.put("IsWord", complementRanges(rangeTokenCreateRange6));
                registerNonXS("IsWord");
                RangeToken rangeTokenCreateRange7 = createRange();
                rangeTokenCreateRange7.addRange(0, 127);
                categories.put("IsASCII", rangeTokenCreateRange7);
                categories2.put("IsASCII", complementRanges(rangeTokenCreateRange7));
                registerNonXS("IsASCII");
                RangeToken rangeTokenCreateRange8 = createRange();
                rangeTokenCreateRange8.mergeRanges(ranges[35]);
                rangeTokenCreateRange8.addRange(32, 32);
                categories.put("IsGraph", complementRanges(rangeTokenCreateRange8));
                categories2.put("IsGraph", rangeTokenCreateRange8);
                registerNonXS("IsGraph");
                RangeToken rangeTokenCreateRange9 = createRange();
                rangeTokenCreateRange9.addRange(48, 57);
                rangeTokenCreateRange9.addRange(65, 70);
                rangeTokenCreateRange9.addRange(97, 102);
                categories.put("IsXDigit", complementRanges(rangeTokenCreateRange9));
                categories2.put("IsXDigit", rangeTokenCreateRange9);
                registerNonXS("IsXDigit");
                setAlias("IsDigit", "Nd", true);
                setAlias("IsUpper", "Lu", true);
                setAlias("IsLower", "Ll", true);
                setAlias("IsCntrl", "C", true);
                setAlias("IsPrint", "C", false);
                setAlias("IsPunct", "P", true);
                registerNonXS("IsDigit");
                registerNonXS("IsUpper");
                registerNonXS("IsLower");
                registerNonXS("IsCntrl");
                registerNonXS("IsPrint");
                registerNonXS("IsPunct");
                setAlias("alpha", "IsAlpha", true);
                setAlias("alnum", "IsAlnum", true);
                setAlias("ascii", "IsASCII", true);
                setAlias("cntrl", "IsCntrl", true);
                setAlias("digit", "IsDigit", true);
                setAlias("graph", "IsGraph", true);
                setAlias("lower", "IsLower", true);
                setAlias(PDWindowsLaunchParams.OPERATION_PRINT, "IsPrint", true);
                setAlias("punct", "IsPunct", true);
                setAlias("space", "IsSpace", true);
                setAlias("upper", "IsUpper", true);
                setAlias("word", "IsWord", true);
                setAlias("xdigit", "IsXDigit", true);
                registerNonXS("alpha");
                registerNonXS("alnum");
                registerNonXS("ascii");
                registerNonXS("cntrl");
                registerNonXS("digit");
                registerNonXS("graph");
                registerNonXS("lower");
                registerNonXS(PDWindowsLaunchParams.OPERATION_PRINT);
                registerNonXS("punct");
                registerNonXS("space");
                registerNonXS("upper");
                registerNonXS("word");
                registerNonXS("xdigit");
            }
        }
        RangeToken tok = positive ? (RangeToken) categories.get(name) : (RangeToken) categories2.get(name);
        return tok;
    }

    protected static RangeToken getRange(String name, boolean positive, boolean xs) {
        RangeToken range = getRange(name, positive);
        if (xs && range != null && isRegisterNonXS(name)) {
            range = null;
        }
        return range;
    }

    protected static void registerNonXS(String name) {
        if (nonxs == null) {
            nonxs = new Hashtable();
        }
        nonxs.put(name, name);
    }

    protected static boolean isRegisterNonXS(String name) {
        if (nonxs == null) {
            return false;
        }
        return nonxs.containsKey(name);
    }

    private static void setAlias(String newName, String name, boolean positive) {
        Token t1 = (Token) categories.get(name);
        Token t2 = (Token) categories2.get(name);
        if (positive) {
            categories.put(newName, t1);
            categories2.put(newName, t2);
        } else {
            categories2.put(newName, t1);
            categories.put(newName, t2);
        }
    }

    static synchronized Token getGraphemePattern() {
        if (token_grapheme != null) {
            return token_grapheme;
        }
        Token base_char = createRange();
        base_char.mergeRanges(getRange("ASSIGNED", true));
        base_char.subtractRanges(getRange(OperatorName.SET_LINE_MITERLIMIT, true));
        base_char.subtractRanges(getRange("C", true));
        Token virama = createRange();
        for (int i = 0; i < viramaString.length(); i++) {
            viramaString.charAt(i);
            virama.addRange(i, i);
        }
        Token combiner_wo_virama = createRange();
        combiner_wo_virama.mergeRanges(getRange(OperatorName.SET_LINE_MITERLIMIT, true));
        combiner_wo_virama.addRange(4448, 4607);
        combiner_wo_virama.addRange(65438, 65439);
        Token left = createUnion();
        left.addChild(base_char);
        left.addChild(token_empty);
        Token foo = createUnion();
        foo.addChild(createConcat(virama, getRange("L", true)));
        foo.addChild(combiner_wo_virama);
        token_grapheme = createConcat(left, createClosure(foo));
        return token_grapheme;
    }

    static synchronized Token getCombiningCharacterSequence() {
        if (token_ccs != null) {
            return token_ccs;
        }
        Token foo = createClosure(getRange(OperatorName.SET_LINE_MITERLIMIT, true));
        token_ccs = createConcat(getRange(OperatorName.SET_LINE_MITERLIMIT, false), foo);
        return token_ccs;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/Token$StringToken.class */
    static class StringToken extends Token implements Serializable {
        String string;
        int refNumber;

        StringToken(int type, String str, int n) {
            super(type);
            this.string = str;
            this.refNumber = n;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        int getReferenceNumber() {
            return this.refNumber;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        String getString() {
            return this.string;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        public String toString(int options) {
            if (this.type == 12) {
                return "\\" + this.refNumber;
            }
            return REUtil.quoteMeta(this.string);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/Token$ConcatToken.class */
    static class ConcatToken extends Token implements Serializable {
        Token child;
        Token child2;

        ConcatToken(Token t1, Token t2) {
            super(1);
            this.child = t1;
            this.child2 = t2;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        int size() {
            return 2;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        Token getChild(int index) {
            return index == 0 ? this.child : this.child2;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        public String toString(int options) {
            String ret;
            if (this.child2.type == 3 && this.child2.getChild(0) == this.child) {
                ret = this.child.toString(options) + Marker.ANY_NON_NULL_MARKER;
            } else if (this.child2.type == 9 && this.child2.getChild(0) == this.child) {
                ret = this.child.toString(options) + "+?";
            } else {
                ret = this.child.toString(options) + this.child2.toString(options);
            }
            return ret;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/Token$CharToken.class */
    static class CharToken extends Token implements Serializable {
        int chardata;

        CharToken(int type, int ch) {
            super(type);
            this.chardata = ch;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        int getChar() {
            return this.chardata;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        public String toString(int options) {
            String ret;
            switch (this.type) {
                case 0:
                    switch (this.chardata) {
                        case 9:
                            ret = "\\t";
                            break;
                        case 10:
                            ret = "\\n";
                            break;
                        case 12:
                            ret = "\\f";
                            break;
                        case 13:
                            ret = "\\r";
                            break;
                        case 27:
                            ret = "\\e";
                            break;
                        case 40:
                        case 41:
                        case 42:
                        case 43:
                        case 46:
                        case 63:
                        case 91:
                        case 92:
                        case 123:
                        case 124:
                            ret = "\\" + ((char) this.chardata);
                            break;
                        default:
                            if (this.chardata >= 65536) {
                                String pre = "0" + Integer.toHexString(this.chardata);
                                ret = "\\v" + pre.substring(pre.length() - 6, pre.length());
                            } else {
                                ret = "" + ((char) this.chardata);
                            }
                            break;
                    }
                    break;
                case 8:
                    if (this == Token.token_linebeginning || this == Token.token_lineend) {
                        ret = "" + ((char) this.chardata);
                    } else {
                        ret = "\\" + ((char) this.chardata);
                    }
                    break;
                default:
                    ret = null;
                    break;
            }
            return ret;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        boolean match(int ch) {
            if (this.type == 0) {
                return ch == this.chardata;
            }
            throw new RuntimeException("NFAArrow#match(): Internal error: " + this.type);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/Token$ClosureToken.class */
    static class ClosureToken extends Token implements Serializable {
        int min;
        int max;
        Token child;

        ClosureToken(int type, Token tok) {
            super(type);
            this.child = tok;
            setMin(-1);
            setMax(-1);
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        int size() {
            return 1;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        Token getChild(int index) {
            return this.child;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        final void setMin(int min) {
            this.min = min;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        final void setMax(int max) {
            this.max = max;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        final int getMin() {
            return this.min;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        final int getMax() {
            return this.max;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        public String toString(int options) {
            String ret;
            if (this.type == 3) {
                if (getMin() < 0 && getMax() < 0) {
                    ret = this.child.toString(options) + "*";
                } else if (getMin() == getMax()) {
                    ret = this.child.toString(options) + "{" + getMin() + "}";
                } else if (getMin() >= 0 && getMax() >= 0) {
                    ret = this.child.toString(options) + "{" + getMin() + "," + getMax() + "}";
                } else if (getMin() >= 0 && getMax() < 0) {
                    ret = this.child.toString(options) + "{" + getMin() + ",}";
                } else {
                    throw new RuntimeException("Token#toString(): CLOSURE " + getMin() + ", " + getMax());
                }
            } else if (getMin() < 0 && getMax() < 0) {
                ret = this.child.toString(options) + "*?";
            } else if (getMin() == getMax()) {
                ret = this.child.toString(options) + "{" + getMin() + "}?";
            } else if (getMin() >= 0 && getMax() >= 0) {
                ret = this.child.toString(options) + "{" + getMin() + "," + getMax() + "}?";
            } else if (getMin() >= 0 && getMax() < 0) {
                ret = this.child.toString(options) + "{" + getMin() + ",}?";
            } else {
                throw new RuntimeException("Token#toString(): NONGREEDYCLOSURE " + getMin() + ", " + getMax());
            }
            return ret;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/Token$ParenToken.class */
    static class ParenToken extends Token implements Serializable {
        Token child;
        int parennumber;

        ParenToken(int type, Token tok, int paren) {
            super(type);
            this.child = tok;
            this.parennumber = paren;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        int size() {
            return 1;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        Token getChild(int index) {
            return this.child;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        int getParenNumber() {
            return this.parennumber;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        public String toString(int options) {
            String ret = null;
            switch (this.type) {
                case 6:
                    if (this.parennumber == 0) {
                        ret = "(?:" + this.child.toString(options) + ")";
                    } else {
                        ret = "(" + this.child.toString(options) + ")";
                    }
                    break;
                case 20:
                    ret = "(?=" + this.child.toString(options) + ")";
                    break;
                case 21:
                    ret = "(?!" + this.child.toString(options) + ")";
                    break;
                case 22:
                    ret = "(?<=" + this.child.toString(options) + ")";
                    break;
                case 23:
                    ret = "(?<!" + this.child.toString(options) + ")";
                    break;
                case 24:
                    ret = "(?>" + this.child.toString(options) + ")";
                    break;
            }
            return ret;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/Token$ConditionToken.class */
    static class ConditionToken extends Token implements Serializable {
        int refNumber;
        Token condition;
        Token yes;
        Token no;

        ConditionToken(int refno, Token cond, Token yespat, Token nopat) {
            super(26);
            this.refNumber = refno;
            this.condition = cond;
            this.yes = yespat;
            this.no = nopat;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        int size() {
            return this.no == null ? 1 : 2;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        Token getChild(int index) {
            if (index == 0) {
                return this.yes;
            }
            if (index == 1) {
                return this.no;
            }
            throw new RuntimeException("Internal Error: " + index);
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        public String toString(int options) {
            String ret;
            String ret2;
            if (this.refNumber > 0) {
                ret = "(?(" + this.refNumber + ")";
            } else if (this.condition.type == 8) {
                ret = "(?(" + this.condition + ")";
            } else {
                ret = "(?" + this.condition;
            }
            if (this.no == null) {
                ret2 = ret + this.yes + ")";
            } else {
                ret2 = ret + this.yes + "|" + this.no + ")";
            }
            return ret2;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/Token$ModifierToken.class */
    static class ModifierToken extends Token implements Serializable {
        Token child;
        int add;
        int mask;

        ModifierToken(Token tok, int add, int mask) {
            super(25);
            this.child = tok;
            this.add = add;
            this.mask = mask;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        int size() {
            return 1;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        Token getChild(int index) {
            return this.child;
        }

        int getOptions() {
            return this.add;
        }

        int getOptionsMask() {
            return this.mask;
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        public String toString(int options) {
            return "(?" + (this.add == 0 ? "" : REUtil.createOptionString(this.add)) + (this.mask == 0 ? "" : REUtil.createOptionString(this.mask)) + ":" + this.child.toString(options) + ")";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/Token$UnionToken.class */
    static class UnionToken extends Token implements Serializable {
        Vector children;

        UnionToken(int type) {
            super(type);
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        void addChild(Token tok) {
            StringBuffer buffer;
            if (tok == null) {
                return;
            }
            if (this.children == null) {
                this.children = new Vector();
            }
            if (this.type == 2) {
                this.children.addElement(tok);
                return;
            }
            if (tok.type == 1) {
                for (int i = 0; i < tok.size(); i++) {
                    addChild(tok.getChild(i));
                }
                return;
            }
            int size = this.children.size();
            if (size == 0) {
                this.children.addElement(tok);
                return;
            }
            Token previous = (Token) this.children.elementAt(size - 1);
            if ((previous.type != 0 && previous.type != 10) || (tok.type != 0 && tok.type != 10)) {
                this.children.addElement(tok);
                return;
            }
            int nextMaxLength = tok.type == 0 ? 2 : tok.getString().length();
            if (previous.type == 0) {
                buffer = new StringBuffer(2 + nextMaxLength);
                int ch = previous.getChar();
                if (ch >= 65536) {
                    buffer.append(REUtil.decomposeToSurrogates(ch));
                } else {
                    buffer.append((char) ch);
                }
                previous = Token.createString(null);
                this.children.setElementAt(previous, size - 1);
            } else {
                buffer = new StringBuffer(previous.getString().length() + nextMaxLength);
                buffer.append(previous.getString());
            }
            if (tok.type == 0) {
                int ch2 = tok.getChar();
                if (ch2 >= 65536) {
                    buffer.append(REUtil.decomposeToSurrogates(ch2));
                } else {
                    buffer.append((char) ch2);
                }
            } else {
                buffer.append(tok.getString());
            }
            ((StringToken) previous).string = new String(buffer);
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        int size() {
            if (this.children == null) {
                return 0;
            }
            return this.children.size();
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        Token getChild(int index) {
            return (Token) this.children.elementAt(index);
        }

        @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.Token
        public String toString(int options) {
            String ret;
            String ret2;
            if (this.type == 1) {
                if (this.children.size() == 2) {
                    Token ch = getChild(0);
                    Token ch2 = getChild(1);
                    if (ch2.type == 3 && ch2.getChild(0) == ch) {
                        ret2 = ch.toString(options) + Marker.ANY_NON_NULL_MARKER;
                    } else if (ch2.type == 9 && ch2.getChild(0) == ch) {
                        ret2 = ch.toString(options) + "+?";
                    } else {
                        ret2 = ch.toString(options) + ch2.toString(options);
                    }
                } else {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < this.children.size(); i++) {
                        sb.append(((Token) this.children.elementAt(i)).toString(options));
                    }
                    ret2 = new String(sb);
                }
                return ret2;
            }
            if (this.children.size() == 2 && getChild(1).type == 7) {
                ret = getChild(0).toString(options) + Config.DEFAULT_GLOBAL_SECTION_NAME;
            } else if (this.children.size() == 2 && getChild(0).type == 7) {
                ret = getChild(1).toString(options) + "??";
            } else {
                StringBuffer sb2 = new StringBuffer();
                sb2.append(((Token) this.children.elementAt(0)).toString(options));
                for (int i2 = 1; i2 < this.children.size(); i2++) {
                    sb2.append('|');
                    sb2.append(((Token) this.children.elementAt(i2)).toString(options));
                }
                ret = new String(sb2);
            }
            return ret;
        }
    }
}
