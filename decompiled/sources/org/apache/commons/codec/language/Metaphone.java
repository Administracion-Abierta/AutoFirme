package org.apache.commons.codec.language;

import java.util.Locale;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/language/Metaphone.class */
public class Metaphone implements StringEncoder {
    private static final String VOWELS = "AEIOU";
    private static final String FRONTV = "EIY";
    private static final String VARSON = "CSPTG";
    private int maxCodeLen = 4;

    @Override // org.apache.commons.codec.Encoder
    public Object encode(Object obj) throws EncoderException {
        if (!(obj instanceof String)) {
            throw new EncoderException("Parameter supplied to Metaphone encode is not of type java.lang.String");
        }
        return metaphone((String) obj);
    }

    @Override // org.apache.commons.codec.StringEncoder
    public String encode(String str) {
        return metaphone(str);
    }

    public int getMaxCodeLen() {
        return this.maxCodeLen;
    }

    private boolean isLastChar(int wdsz, int n) {
        return n + 1 == wdsz;
    }

    public boolean isMetaphoneEqual(String str1, String str2) {
        return metaphone(str1).equals(metaphone(str2));
    }

    private boolean isNextChar(StringBuilder string, int index, char c) {
        boolean matches = false;
        if (index >= 0 && index < string.length() - 1) {
            matches = string.charAt(index + 1) == c;
        }
        return matches;
    }

    private boolean isPreviousChar(StringBuilder string, int index, char c) {
        boolean matches = false;
        if (index > 0 && index < string.length()) {
            matches = string.charAt(index - 1) == c;
        }
        return matches;
    }

    private boolean isVowel(StringBuilder string, int index) {
        return VOWELS.indexOf(string.charAt(index)) >= 0;
    }

    public String metaphone(String txt) {
        int txtLength;
        if (txt == null || (txtLength = txt.length()) == 0) {
            return "";
        }
        if (txtLength == 1) {
            return txt.toUpperCase(Locale.ENGLISH);
        }
        char[] inwd = txt.toUpperCase(Locale.ENGLISH).toCharArray();
        StringBuilder local = new StringBuilder(40);
        StringBuilder code = new StringBuilder(10);
        switch (inwd[0]) {
            case 'A':
                if (inwd[1] == 'E') {
                    local.append(inwd, 1, inwd.length - 1);
                } else {
                    local.append(inwd);
                }
                break;
            case 'G':
            case 'K':
            case 'P':
                if (inwd[1] == 'N') {
                    local.append(inwd, 1, inwd.length - 1);
                } else {
                    local.append(inwd);
                }
                break;
            case 'W':
                if (inwd[1] == 'R') {
                    local.append(inwd, 1, inwd.length - 1);
                } else if (inwd[1] == 'H') {
                    local.append(inwd, 1, inwd.length - 1);
                    local.setCharAt(0, 'W');
                } else {
                    local.append(inwd);
                }
                break;
            case 'X':
                inwd[0] = 'S';
                local.append(inwd);
                break;
            default:
                local.append(inwd);
                break;
        }
        int wdsz = local.length();
        int n = 0;
        while (code.length() < getMaxCodeLen() && n < wdsz) {
            char symb = local.charAt(n);
            if (symb == 'C' || !isPreviousChar(local, n, symb)) {
                switch (symb) {
                    case 'A':
                    case 'E':
                    case 'I':
                    case 'O':
                    case 'U':
                        if (n == 0) {
                            code.append(symb);
                        }
                        break;
                    case 'B':
                        if (!isPreviousChar(local, n, 'M') || !isLastChar(wdsz, n)) {
                            code.append(symb);
                        }
                        break;
                    case 'C':
                        if (!isPreviousChar(local, n, 'S') || isLastChar(wdsz, n) || FRONTV.indexOf(local.charAt(n + 1)) < 0) {
                            if (regionMatch(local, n, "CIA")) {
                                code.append('X');
                            } else if (!isLastChar(wdsz, n) && FRONTV.indexOf(local.charAt(n + 1)) >= 0) {
                                code.append('S');
                            } else if ((isPreviousChar(local, n, 'S') && isNextChar(local, n, 'H')) || !isNextChar(local, n, 'H') || (n == 0 && wdsz >= 3 && isVowel(local, 2))) {
                                code.append('K');
                            } else {
                                code.append('X');
                            }
                        }
                        break;
                    case 'D':
                        if (!isLastChar(wdsz, n + 1) && isNextChar(local, n, 'G') && FRONTV.indexOf(local.charAt(n + 2)) >= 0) {
                            code.append('J');
                            n += 2;
                        } else {
                            code.append('T');
                        }
                        break;
                    case 'F':
                    case 'J':
                    case 'L':
                    case 'M':
                    case 'N':
                    case 'R':
                        code.append(symb);
                        break;
                    case 'G':
                        if ((!isLastChar(wdsz, n + 1) || !isNextChar(local, n, 'H')) && ((isLastChar(wdsz, n + 1) || !isNextChar(local, n, 'H') || isVowel(local, n + 2)) && (n <= 0 || (!regionMatch(local, n, "GN") && !regionMatch(local, n, "GNED"))))) {
                            boolean hard = isPreviousChar(local, n, 'G');
                            if (!isLastChar(wdsz, n) && FRONTV.indexOf(local.charAt(n + 1)) >= 0 && !hard) {
                                code.append('J');
                            } else {
                                code.append('K');
                            }
                        }
                        break;
                    case 'H':
                        if (!isLastChar(wdsz, n) && ((n <= 0 || VARSON.indexOf(local.charAt(n - 1)) < 0) && isVowel(local, n + 1))) {
                            code.append('H');
                        }
                        break;
                    case 'K':
                        if (n <= 0 || !isPreviousChar(local, n, 'C')) {
                            code.append(symb);
                        }
                        break;
                    case 'P':
                        if (isNextChar(local, n, 'H')) {
                            code.append('F');
                        } else {
                            code.append(symb);
                        }
                        break;
                    case 'Q':
                        code.append('K');
                        break;
                    case 'S':
                        if (regionMatch(local, n, "SH") || regionMatch(local, n, "SIO") || regionMatch(local, n, "SIA")) {
                            code.append('X');
                        } else {
                            code.append('S');
                        }
                        break;
                    case 'T':
                        if (regionMatch(local, n, "TIA") || regionMatch(local, n, "TIO")) {
                            code.append('X');
                        } else if (!regionMatch(local, n, "TCH")) {
                            if (regionMatch(local, n, StandardStructureTypes.TH)) {
                                code.append('0');
                            } else {
                                code.append('T');
                            }
                        }
                        break;
                    case 'V':
                        code.append('F');
                        break;
                    case 'W':
                    case 'Y':
                        if (!isLastChar(wdsz, n) && isVowel(local, n + 1)) {
                            code.append(symb);
                        }
                        break;
                    case 'X':
                        code.append('K');
                        code.append('S');
                        break;
                    case 'Z':
                        code.append('S');
                        break;
                }
            }
            n++;
            if (code.length() > getMaxCodeLen()) {
                code.setLength(getMaxCodeLen());
            }
        }
        return code.toString();
    }

    private boolean regionMatch(StringBuilder string, int index, String test) {
        boolean matches = false;
        if (index >= 0 && (index + test.length()) - 1 < string.length()) {
            String substring = string.substring(index, index + test.length());
            matches = substring.equals(test);
        }
        return matches;
    }

    public void setMaxCodeLen(int maxCodeLen) {
        this.maxCodeLen = maxCodeLen;
    }
}
