package com.ctc.wstx.shaded.msv_core.datatype.regexp;

import java.util.Hashtable;
import java.util.Locale;
import org.spongycastle.crypto.tls.AlertDescription;
import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/ParserForXMLSchema.class */
class ParserForXMLSchema extends RegexParser {
    private static Hashtable ranges = null;
    private static Hashtable ranges2 = null;
    private static final String SPACES = "\t\n\r\r  ";
    private static final String NAMECHARS = "-.0:AZ__az··ÀÖØöøıĴľŁňŊžƀǃǍǰǴǵǺȗɐʨʻˁːˑ̀͠͡ͅΆΊΌΌΎΡΣώϐϖϚϚϜϜϞϞϠϠϢϳЁЌЎяёќўҁ҃҆ҐӄӇӈӋӌӐӫӮӵӸӹԱՖՙՙաֆֹֻֽֿֿׁׂ֑֣֡ׄׄאתװײءغـْ٠٩ٰڷںھۀێېۓە۪ۭۨ۰۹ँःअह़्॑॔क़ॣ०९ঁঃঅঌএঐওনপরললশহ়়াৄেৈো্ৗৗড়ঢ়য়ৣ০ৱਂਂਅਊਏਐਓਨਪਰਲਲ਼ਵਸ਼ਸਹ਼਼ਾੂੇੈੋ੍ਖ਼ੜਫ਼ਫ਼੦ੴઁઃઅઋઍઍએઑઓનપરલળવહ઼ૅેૉો્ૠૠ૦૯ଁଃଅଌଏଐଓନପରଲଳଶହ଼ୃେୈୋ୍ୖୗଡ଼ଢ଼ୟୡ୦୯ஂஃஅஊஎஐஒகஙசஜஜஞடணதநபமவஷஹாூெைொ்ௗௗ௧௯ఁఃఅఌఎఐఒనపళవహాౄెైొ్ౕౖౠౡ౦౯ಂಃಅಌಎಐಒನಪಳವಹಾೄೆೈೊ್ೕೖೞೞೠೡ೦೯ംഃഅഌഎഐഒനപഹാൃെൈൊ്ൗൗൠൡ൦൯กฮะฺเ๎๐๙ກຂຄຄງຈຊຊຍຍດທນຟມຣລລວວສຫອຮະູົຽເໄໆໆ່ໍ໐໙༘༙༠༩༹༹༵༵༷༷༾ཇཉཀྵ྄ཱ྆ྋྐྕྗྗྙྭྱྷྐྵྐྵႠჅაჶᄀᄀᄂᄃᄅᄇᄉᄉᄋᄌᄎᄒᄼᄼᄾᄾᅀᅀᅌᅌᅎᅎᅐᅐᅔᅕᅙᅙᅟᅡᅣᅣᅥᅥᅧᅧᅩᅩᅭᅮᅲᅳᅵᅵᆞᆞᆨᆨᆫᆫᆮᆯᆷᆸᆺᆺᆼᇂᇫᇫᇰᇰᇹᇹḀẛẠỹἀἕἘἝἠὅὈὍὐὗὙὙὛὛὝὝὟώᾀᾴᾶᾼιιῂῄῆῌῐΐῖΊῠῬῲῴῶῼ⃐⃜⃡⃡ΩΩKÅ℮℮ↀↂ々々〇〇〡〯〱〵ぁゔ゙゚ゝゞァヺーヾㄅㄬ一龥가힣";
    private static final String LETTERS = "AZazÀÖØöøıĴľŁňŊžƀǃǍǰǴǵǺȗɐʨʻˁΆΆΈΊΌΌΎΡΣώϐϖϚϚϜϜϞϞϠϠϢϳЁЌЎяёќўҁҐӄӇӈӋӌӐӫӮӵӸӹԱՖՙՙաֆאתװײءغفيٱڷںھۀێېۓەەۥۦअहऽऽक़ॡঅঌএঐওনপরললশহড়ঢ়য়ৡৰৱਅਊਏਐਓਨਪਰਲਲ਼ਵਸ਼ਸਹਖ਼ੜਫ਼ਫ਼ੲੴઅઋઍઍએઑઓનપરલળવહઽઽૠૠଅଌଏଐଓନପରଲଳଶହଽଽଡ଼ଢ଼ୟୡஅஊஎஐஒகஙசஜஜஞடணதநபமவஷஹఅఌఎఐఒనపళవహౠౡಅಌಎಐಒನಪಳವಹೞೞೠೡഅഌഎഐഒനപഹൠൡกฮะะาำเๅກຂຄຄງຈຊຊຍຍດທນຟມຣລລວວສຫອຮະະາຳຽຽເໄཀཇཉཀྵႠჅაჶᄀᄀᄂᄃᄅᄇᄉᄉᄋᄌᄎᄒᄼᄼᄾᄾᅀᅀᅌᅌᅎᅎᅐᅐᅔᅕᅙᅙᅟᅡᅣᅣᅥᅥᅧᅧᅩᅩᅭᅮᅲᅳᅵᅵᆞᆞᆨᆨᆫᆫᆮᆯᆷᆸᆺᆺᆼᇂᇫᇫᇰᇰᇹᇹḀẛẠỹἀἕἘἝἠὅὈὍὐὗὙὙὛὛὝὝὟώᾀᾴᾶᾼιιῂῄῆῌῐΐῖΊῠῬῲῴῶῼΩΩKÅ℮℮ↀↂ〇〇〡〩ぁゔァヺㄅㄬ一龥가힣";
    private static final String DIGITS = "09٠٩۰۹०९০৯੦੯૦૯୦୯௧௯౦౯೦೯൦൯๐๙໐໙༠༩";

    public ParserForXMLSchema() {
    }

    public ParserForXMLSchema(Locale locale) {
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processCaret() throws ParseException {
        next();
        return Token.createChar(94);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processDollar() throws ParseException {
        next();
        return Token.createChar(36);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processLookahead() throws ParseException {
        throw ex("parser.process.1", this.offset);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processNegativelookahead() throws ParseException {
        throw ex("parser.process.1", this.offset);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processLookbehind() throws ParseException {
        throw ex("parser.process.1", this.offset);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processNegativelookbehind() throws ParseException {
        throw ex("parser.process.1", this.offset);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processBacksolidus_A() throws ParseException {
        throw ex("parser.process.1", this.offset);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processBacksolidus_Z() throws ParseException {
        throw ex("parser.process.1", this.offset);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processBacksolidus_z() throws ParseException {
        throw ex("parser.process.1", this.offset);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processBacksolidus_b() throws ParseException {
        throw ex("parser.process.1", this.offset);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processBacksolidus_B() throws ParseException {
        throw ex("parser.process.1", this.offset);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processBacksolidus_lt() throws ParseException {
        throw ex("parser.process.1", this.offset);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processBacksolidus_gt() throws ParseException {
        throw ex("parser.process.1", this.offset);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processStar(Token tok) throws ParseException {
        next();
        return Token.createClosure(tok);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processPlus(Token tok) throws ParseException {
        next();
        return Token.createConcat(tok, Token.createClosure(tok));
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processQuestion(Token tok) throws ParseException {
        next();
        Token par = Token.createUnion();
        par.addChild(tok);
        par.addChild(Token.createEmpty());
        return par;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    boolean checkQuestion(int off) {
        return false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processParen() throws ParseException {
        next();
        Token tok = Token.createParen(parseRegex(), 0);
        if (read() != 7) {
            throw ex("parser.factor.1", this.offset - 1);
        }
        next();
        return tok;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processParen2() throws ParseException {
        throw ex("parser.process.1", this.offset);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processCondition() throws ParseException {
        throw ex("parser.process.1", this.offset);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processModifiers() throws ParseException {
        throw ex("parser.process.1", this.offset);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processIndependent() throws ParseException {
        throw ex("parser.process.1", this.offset);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processBacksolidus_c() throws ParseException {
        next();
        return getTokenForShorthand(99);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processBacksolidus_C() throws ParseException {
        next();
        return getTokenForShorthand(67);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processBacksolidus_i() throws ParseException {
        next();
        return getTokenForShorthand(CipherSuite.TLS_DH_RSA_WITH_AES_256_CBC_SHA256);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processBacksolidus_I() throws ParseException {
        next();
        return getTokenForShorthand(73);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processBacksolidus_g() throws ParseException {
        throw ex("parser.process.1", this.offset - 2);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processBacksolidus_X() throws ParseException {
        throw ex("parser.process.1", this.offset - 2);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token processBackreference() throws ParseException {
        throw ex("parser.process.1", this.offset - 4);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    int processCIinCharacterClass(RangeToken tok, int c) {
        tok.mergeRanges(getTokenForShorthand(c));
        return -1;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    protected RangeToken parseCharacterClass(boolean useNrange) throws ParseException {
        RangeToken tok;
        setContext(1);
        next();
        boolean nrange = false;
        RangeToken base = null;
        if (read() == 0 && this.chardata == 94) {
            nrange = true;
            next();
            base = Token.createRange();
            base.addRange(0, 1114111);
            tok = Token.createRange();
        } else {
            tok = Token.createRange();
        }
        boolean z = true;
        while (true) {
            boolean firstloop = z;
            int type = read();
            if (type != 1) {
                if (type == 0 && this.chardata == 93 && !firstloop) {
                    if (nrange) {
                        base.subtractRanges(tok);
                        tok = base;
                    }
                } else {
                    int c = this.chardata;
                    boolean end = false;
                    if (type == 10) {
                        switch (c) {
                            case 67:
                            case 73:
                            case 99:
                            case CipherSuite.TLS_DH_RSA_WITH_AES_256_CBC_SHA256 /* 105 */:
                                c = processCIinCharacterClass(tok, c);
                                if (c < 0) {
                                    end = true;
                                }
                                break;
                            case 68:
                            case 83:
                            case 87:
                            case AlertDescription.no_renegotiation /* 100 */:
                            case AlertDescription.unknown_psk_identity /* 115 */:
                            case 119:
                                tok.mergeRanges(getTokenForShorthand(c));
                                end = true;
                                break;
                            case 80:
                            case AlertDescription.unrecognized_name /* 112 */:
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
                    } else if (type == 24 && !firstloop) {
                        if (nrange) {
                            base.subtractRanges(tok);
                            tok = base;
                        }
                        RangeToken range2 = parseCharacterClass(false);
                        tok.subtractRanges(range2);
                        if (read() != 0 || this.chardata != 93) {
                            throw ex("parser.cc.5", this.offset);
                        }
                    }
                    next();
                    if (!end) {
                        if (type == 0) {
                            if (c == 91) {
                                throw ex("parser.cc.6", this.offset - 2);
                            }
                            if (c == 93) {
                                throw ex("parser.cc.7", this.offset - 2);
                            }
                            if (c == 45) {
                                throw ex("parser.cc.8", this.offset - 2);
                            }
                        }
                        if (read() != 0 || this.chardata != 45) {
                            tok.addRange(c, c);
                        } else {
                            next();
                            int type2 = read();
                            if (type2 == 1) {
                                throw ex("parser.cc.2", this.offset);
                            }
                            if ((type2 == 0 && this.chardata == 93) || type2 == 24) {
                                throw ex("parser.cc.8", this.offset - 1);
                            }
                            int rangeend = this.chardata;
                            if (type2 == 0) {
                                if (rangeend == 91) {
                                    throw ex("parser.cc.6", this.offset - 1);
                                }
                                if (rangeend == 93) {
                                    throw ex("parser.cc.7", this.offset - 1);
                                }
                                if (rangeend == 45) {
                                    throw ex("parser.cc.8", this.offset - 2);
                                }
                            } else if (type2 == 10) {
                                rangeend = decodeEscaped();
                            }
                            next();
                            if (c > rangeend) {
                                throw ex("parser.ope.3", this.offset - 1);
                            }
                            tok.addRange(c, rangeend);
                        }
                    }
                    z = false;
                }
            }
        }
        if (read() == 1) {
            throw ex("parser.cc.2", this.offset);
        }
        tok.sortRanges();
        tok.compactRanges();
        setContext(0);
        next();
        return tok;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    protected RangeToken parseSetOperations() throws ParseException {
        throw ex("parser.process.1", this.offset);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    Token getTokenForShorthand(int ch) {
        switch (ch) {
            case 67:
                return getRange("xml:isNameChar", false);
            case 68:
                return getRange("xml:isDigit", false);
            case 73:
                return getRange("xml:isInitialNameChar", false);
            case 83:
                return getRange("xml:isSpace", false);
            case 87:
                return getRange("xml:isWord", false);
            case 99:
                return getRange("xml:isNameChar", true);
            case AlertDescription.no_renegotiation /* 100 */:
                return getRange("xml:isDigit", true);
            case CipherSuite.TLS_DH_RSA_WITH_AES_256_CBC_SHA256 /* 105 */:
                return getRange("xml:isInitialNameChar", true);
            case AlertDescription.unknown_psk_identity /* 115 */:
                return getRange("xml:isSpace", true);
            case 119:
                return getRange("xml:isWord", true);
            default:
                throw new RuntimeException("Internal Error: shorthands: \\u" + Integer.toString(ch, 16));
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.regexp.RegexParser
    int decodeEscaped() throws ParseException {
        if (read() != 10) {
            throw ex("parser.next.1", this.offset - 1);
        }
        int c = this.chardata;
        switch (c) {
            case 40:
            case 41:
            case 42:
            case 43:
            case 45:
            case 46:
            case 63:
            case 91:
            case 92:
            case 93:
            case 94:
            case 123:
            case 124:
            case 125:
                break;
            case AlertDescription.unsupported_extension /* 110 */:
                c = 10;
                break;
            case AlertDescription.bad_certificate_hash_value /* 114 */:
                c = 13;
                break;
            case 116:
                c = 9;
                break;
            default:
                throw ex("parser.process.1", this.offset - 2);
        }
        return c;
    }

    protected static synchronized RangeToken getRange(String name, boolean positive) {
        if (ranges == null) {
            ranges = new Hashtable();
            ranges2 = new Hashtable();
            RangeToken rangeTokenCreateRange = Token.createRange();
            setupRange(rangeTokenCreateRange, SPACES);
            ranges.put("xml:isSpace", rangeTokenCreateRange);
            ranges2.put("xml:isSpace", Token.complementRanges(rangeTokenCreateRange));
            RangeToken rangeTokenCreateRange2 = Token.createRange();
            setupRange(rangeTokenCreateRange2, DIGITS);
            ranges.put("xml:isDigit", rangeTokenCreateRange2);
            ranges2.put("xml:isDigit", Token.complementRanges(rangeTokenCreateRange2));
            RangeToken rangeTokenCreateRange3 = Token.createRange();
            setupRange(rangeTokenCreateRange3, DIGITS);
            ranges.put("xml:isDigit", rangeTokenCreateRange3);
            ranges2.put("xml:isDigit", Token.complementRanges(rangeTokenCreateRange3));
            RangeToken rangeTokenCreateRange4 = Token.createRange();
            setupRange(rangeTokenCreateRange4, LETTERS);
            rangeTokenCreateRange4.mergeRanges((Token) ranges.get("xml:isDigit"));
            ranges.put("xml:isWord", rangeTokenCreateRange4);
            ranges2.put("xml:isWord", Token.complementRanges(rangeTokenCreateRange4));
            RangeToken rangeTokenCreateRange5 = Token.createRange();
            setupRange(rangeTokenCreateRange5, NAMECHARS);
            ranges.put("xml:isNameChar", rangeTokenCreateRange5);
            ranges2.put("xml:isNameChar", Token.complementRanges(rangeTokenCreateRange5));
            RangeToken rangeTokenCreateRange6 = Token.createRange();
            setupRange(rangeTokenCreateRange6, LETTERS);
            rangeTokenCreateRange6.addRange(95, 95);
            rangeTokenCreateRange6.addRange(58, 58);
            ranges.put("xml:isInitialNameChar", rangeTokenCreateRange6);
            ranges2.put("xml:isInitialNameChar", Token.complementRanges(rangeTokenCreateRange6));
        }
        RangeToken tok = positive ? (RangeToken) ranges.get(name) : (RangeToken) ranges2.get(name);
        return tok;
    }

    static void setupRange(Token range, String src) {
        int len = src.length();
        for (int i = 0; i < len; i += 2) {
            range.addRange(src.charAt(i), src.charAt(i + 1));
        }
    }
}
