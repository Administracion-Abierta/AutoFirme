package org.apache.commons.codec.language;

import java.util.Locale;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.xml.security.utils.Constants;
import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.crypto.tls.AlertDescription;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.pqc.math.linearalgebra.Matrix;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/language/DoubleMetaphone.class */
public class DoubleMetaphone implements StringEncoder {
    private static final String VOWELS = "AEIOUY";
    private static final String[] SILENT_START = {"GN", "KN", "PN", "WR", "PS"};
    private static final String[] L_R_N_M_B_H_F_V_W_SPACE = {"L", "R", "N", "M", "B", "H", "F", "V", "W", " "};
    private static final String[] ES_EP_EB_EL_EY_IB_IL_IN_IE_EI_ER = {"ES", "EP", "EB", "EL", "EY", "IB", "IL", "IN", "IE", "EI", "ER"};
    private static final String[] L_T_K_S_N_M_B_Z = {"L", "T", "K", "S", "N", "M", "B", "Z"};
    private int maxCodeLen = 4;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/language/DoubleMetaphone$DoubleMetaphoneResult.class */
    public class DoubleMetaphoneResult {
        private final StringBuilder primary;
        private final StringBuilder alternate;
        private final int maxLength;

        public DoubleMetaphoneResult(int maxLength) {
            this.primary = new StringBuilder(DoubleMetaphone.this.getMaxCodeLen());
            this.alternate = new StringBuilder(DoubleMetaphone.this.getMaxCodeLen());
            this.maxLength = maxLength;
        }

        public void append(char value) {
            appendPrimary(value);
            appendAlternate(value);
        }

        public void append(char primary, char alternate) {
            appendPrimary(primary);
            appendAlternate(alternate);
        }

        public void append(String value) {
            appendPrimary(value);
            appendAlternate(value);
        }

        public void append(String primary, String alternate) {
            appendPrimary(primary);
            appendAlternate(alternate);
        }

        public void appendAlternate(char value) {
            if (this.alternate.length() < this.maxLength) {
                this.alternate.append(value);
            }
        }

        public void appendAlternate(String value) {
            int addChars = this.maxLength - this.alternate.length();
            if (value.length() <= addChars) {
                this.alternate.append(value);
            } else {
                this.alternate.append((CharSequence) value, 0, addChars);
            }
        }

        public void appendPrimary(char value) {
            if (this.primary.length() < this.maxLength) {
                this.primary.append(value);
            }
        }

        public void appendPrimary(String value) {
            int addChars = this.maxLength - this.primary.length();
            if (value.length() <= addChars) {
                this.primary.append(value);
            } else {
                this.primary.append((CharSequence) value, 0, addChars);
            }
        }

        public String getAlternate() {
            return this.alternate.toString();
        }

        public String getPrimary() {
            return this.primary.toString();
        }

        public boolean isComplete() {
            return this.primary.length() >= this.maxLength && this.alternate.length() >= this.maxLength;
        }
    }

    protected static boolean contains(String value, int start, int length, String... criteria) {
        boolean result = false;
        if (start >= 0 && start + length <= value.length()) {
            String target = value.substring(start, start + length);
            for (String element : criteria) {
                if (target.equals(element)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    protected char charAt(String value, int index) {
        if (index < 0 || index >= value.length()) {
            return (char) 0;
        }
        return value.charAt(index);
    }

    private String cleanInput(String input) {
        if (input == null) {
            return null;
        }
        String input2 = input.trim();
        if (input2.isEmpty()) {
            return null;
        }
        return input2.toUpperCase(Locale.ENGLISH);
    }

    private boolean conditionC0(String value, int index) {
        if (contains(value, index, 4, "CHIA")) {
            return true;
        }
        if (index <= 1 || isVowel(charAt(value, index - 2)) || !contains(value, index - 1, 3, "ACH")) {
            return false;
        }
        char c = charAt(value, index + 2);
        return !(c == 'I' || c == 'E') || contains(value, index - 2, 6, "BACHER", "MACHER");
    }

    private boolean conditionCH0(String value, int index) {
        if (index != 0) {
            return false;
        }
        return (contains(value, index + 1, 5, "HARAC", "HARIS") || contains(value, index + 1, 3, "HOR", "HYM", "HIA", "HEM")) && !contains(value, 0, 5, "CHORE");
    }

    private boolean conditionCH1(String value, int index) {
        return contains(value, 0, 4, "VAN ", "VON ") || contains(value, 0, 3, "SCH") || contains(value, index - 2, 6, "ORCHES", "ARCHIT", "ORCHID") || contains(value, index + 2, 1, "T", "S") || ((contains(value, index - 1, 1, "A", "O", "U", "E") || index == 0) && (contains(value, index + 2, 1, L_R_N_M_B_H_F_V_W_SPACE) || index + 1 == value.length() - 1));
    }

    private boolean conditionL0(String value, int index) {
        if (index == value.length() - 3 && contains(value, index - 1, 4, "ILLO", "ILLA", "ALLE")) {
            return true;
        }
        return (contains(value, value.length() - 2, 2, "AS", "OS") || contains(value, value.length() - 1, 1, "A", "O")) && contains(value, index - 1, 4, "ALLE");
    }

    private boolean conditionM0(String value, int index) {
        if (charAt(value, index + 1) == 'M') {
            return true;
        }
        return contains(value, index - 1, 3, "UMB") && (index + 1 == value.length() - 1 || contains(value, index + 2, 2, "ER"));
    }

    public String doubleMetaphone(String value) {
        return doubleMetaphone(value, false);
    }

    public String doubleMetaphone(String value, boolean alternate) {
        String value2 = cleanInput(value);
        if (value2 == null) {
            return null;
        }
        boolean slavoGermanic = isSlavoGermanic(value2);
        int index = isSilentStart(value2) ? 1 : 0;
        DoubleMetaphoneResult result = new DoubleMetaphoneResult(getMaxCodeLen());
        while (!result.isComplete() && index <= value2.length() - 1) {
            switch (value2.charAt(index)) {
                case 'A':
                case 'E':
                case 'I':
                case 'O':
                case Matrix.MATRIX_TYPE_RANDOM_UT /* 85 */:
                case 'Y':
                    index = handleAEIOUY(result, index);
                    break;
                case 'B':
                    result.append('P');
                    index += charAt(value2, index + 1) == 'B' ? 2 : 1;
                    break;
                case 'C':
                    index = handleC(value2, result, index);
                    break;
                case 'D':
                    index = handleD(value2, result, index);
                    break;
                case 'F':
                    result.append('F');
                    index += charAt(value2, index + 1) == 'F' ? 2 : 1;
                    break;
                case 'G':
                    index = handleG(value2, result, index, slavoGermanic);
                    break;
                case 'H':
                    index = handleH(value2, result, index);
                    break;
                case EACTags.CERTIFICATION_AUTHORITY_PUBLIC_KEY /* 74 */:
                    index = handleJ(value2, result, index, slavoGermanic);
                    break;
                case EACTags.DEPRECATED /* 75 */:
                    result.append('K');
                    index += charAt(value2, index + 1) == 'K' ? 2 : 1;
                    break;
                case 'L':
                    index = handleL(value2, result, index);
                    break;
                case EACTags.INTEGRATED_CIRCUIT_MANUFACTURER_ID /* 77 */:
                    result.append('M');
                    index += conditionM0(value2, index) ? 2 : 1;
                    break;
                case 'N':
                    result.append('N');
                    index += charAt(value2, index + 1) == 'N' ? 2 : 1;
                    break;
                case 'P':
                    index = handleP(value2, result, index);
                    break;
                case EACTags.ANSWER_TO_RESET /* 81 */:
                    result.append('K');
                    index += charAt(value2, index + 1) == 'Q' ? 2 : 1;
                    break;
                case 'R':
                    index = handleR(value2, result, index, slavoGermanic);
                    break;
                case 'S':
                    index = handleS(value2, result, index, slavoGermanic);
                    break;
                case 'T':
                    index = handleT(value2, result, index);
                    break;
                case AlertDescription.inappropriate_fallback /* 86 */:
                    result.append('F');
                    index += charAt(value2, index + 1) == 'V' ? 2 : 1;
                    break;
                case 'W':
                    index = handleW(value2, result, index);
                    break;
                case 'X':
                    index = handleX(value2, result, index);
                    break;
                case 'Z':
                    index = handleZ(value2, result, index, slavoGermanic);
                    break;
                case 199:
                    result.append('S');
                    index++;
                    break;
                case 209:
                    result.append('N');
                    index++;
                    break;
                default:
                    index++;
                    break;
            }
        }
        return alternate ? result.getAlternate() : result.getPrimary();
    }

    @Override // org.apache.commons.codec.Encoder
    public Object encode(Object obj) throws EncoderException {
        if (!(obj instanceof String)) {
            throw new EncoderException("DoubleMetaphone encode parameter is not of type String");
        }
        return doubleMetaphone((String) obj);
    }

    @Override // org.apache.commons.codec.StringEncoder
    public String encode(String value) {
        return doubleMetaphone(value);
    }

    public int getMaxCodeLen() {
        return this.maxCodeLen;
    }

    private int handleAEIOUY(DoubleMetaphoneResult result, int index) {
        if (index == 0) {
            result.append('A');
        }
        return index + 1;
    }

    private int handleC(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        if (conditionC0(value, index)) {
            result.append('K');
            index2 = index + 2;
        } else if (index == 0 && contains(value, index, 6, "CAESAR")) {
            result.append('S');
            index2 = index + 2;
        } else if (contains(value, index, 2, "CH")) {
            index2 = handleCH(value, result, index);
        } else if (contains(value, index, 2, "CZ") && !contains(value, index - 2, 4, "WICZ")) {
            result.append('S', 'X');
            index2 = index + 2;
        } else if (contains(value, index + 1, 3, "CIA")) {
            result.append('X');
            index2 = index + 3;
        } else {
            if (contains(value, index, 2, "CC") && (index != 1 || charAt(value, 0) != 'M')) {
                return handleCC(value, result, index);
            }
            if (contains(value, index, 2, "CK", "CG", "CQ")) {
                result.append('K');
                index2 = index + 2;
            } else if (contains(value, index, 2, "CI", "CE", "CY")) {
                if (contains(value, index, 3, "CIO", "CIE", "CIA")) {
                    result.append('S', 'X');
                } else {
                    result.append('S');
                }
                index2 = index + 2;
            } else {
                result.append('K');
                if (contains(value, index + 1, 2, " C", " Q", " G")) {
                    index2 = index + 3;
                } else if (contains(value, index + 1, 1, "C", "K", Constants._TAG_Q) && !contains(value, index + 1, 2, "CE", "CI")) {
                    index2 = index + 2;
                } else {
                    index2 = index + 1;
                }
            }
        }
        return index2;
    }

    private int handleCC(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        if (contains(value, index + 2, 1, "I", "E", "H") && !contains(value, index + 2, 2, "HU")) {
            if ((index == 1 && charAt(value, index - 1) == 'A') || contains(value, index - 1, 5, "UCCEE", "UCCES")) {
                result.append("KS");
            } else {
                result.append('X');
            }
            index2 = index + 3;
        } else {
            result.append('K');
            index2 = index + 2;
        }
        return index2;
    }

    private int handleCH(String value, DoubleMetaphoneResult result, int index) {
        if (index > 0 && contains(value, index, 4, "CHAE")) {
            result.append('K', 'X');
            return index + 2;
        }
        if (conditionCH0(value, index)) {
            result.append('K');
            return index + 2;
        }
        if (conditionCH1(value, index)) {
            result.append('K');
            return index + 2;
        }
        if (index > 0) {
            if (contains(value, 0, 2, "MC")) {
                result.append('K');
            } else {
                result.append('X', 'K');
            }
        } else {
            result.append('X');
        }
        return index + 2;
    }

    private int handleD(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        if (contains(value, index, 2, "DG")) {
            if (contains(value, index + 2, 1, "I", "E", Constants._TAG_Y)) {
                result.append('J');
                index2 = index + 3;
            } else {
                result.append("TK");
                index2 = index + 2;
            }
        } else if (contains(value, index, 2, "DT", "DD")) {
            result.append('T');
            index2 = index + 2;
        } else {
            result.append('T');
            index2 = index + 1;
        }
        return index2;
    }

    private int handleG(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
        int index2;
        if (charAt(value, index + 1) == 'H') {
            index2 = handleGH(value, result, index);
        } else if (charAt(value, index + 1) == 'N') {
            if (index == 1 && isVowel(charAt(value, 0)) && !slavoGermanic) {
                result.append("KN", "N");
            } else if (!contains(value, index + 2, 2, "EY") && charAt(value, index + 1) != 'Y' && !slavoGermanic) {
                result.append("N", "KN");
            } else {
                result.append("KN");
            }
            index2 = index + 2;
        } else if (contains(value, index + 1, 2, "LI") && !slavoGermanic) {
            result.append("KL", "L");
            index2 = index + 2;
        } else if (index == 0 && (charAt(value, index + 1) == 'Y' || contains(value, index + 1, 2, ES_EP_EB_EL_EY_IB_IL_IN_IE_EI_ER))) {
            result.append('K', 'J');
            index2 = index + 2;
        } else if ((contains(value, index + 1, 2, "ER") || charAt(value, index + 1) == 'Y') && !contains(value, 0, 6, "DANGER", "RANGER", "MANGER") && !contains(value, index - 1, 1, "E", "I") && !contains(value, index - 1, 3, "RGY", "OGY")) {
            result.append('K', 'J');
            index2 = index + 2;
        } else if (contains(value, index + 1, 1, "E", "I", Constants._TAG_Y) || contains(value, index - 1, 4, "AGGI", "OGGI")) {
            if (contains(value, 0, 4, "VAN ", "VON ") || contains(value, 0, 3, "SCH") || contains(value, index + 1, 2, "ET")) {
                result.append('K');
            } else if (contains(value, index + 1, 3, "IER")) {
                result.append('J');
            } else {
                result.append('J', 'K');
            }
            index2 = index + 2;
        } else {
            if (charAt(value, index + 1) == 'G') {
                index2 = index + 2;
            } else {
                index2 = index + 1;
            }
            result.append('K');
        }
        return index2;
    }

    private int handleGH(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        if (index > 0 && !isVowel(charAt(value, index - 1))) {
            result.append('K');
            index2 = index + 2;
        } else if (index == 0) {
            if (charAt(value, index + 2) == 'I') {
                result.append('J');
            } else {
                result.append('K');
            }
            index2 = index + 2;
        } else if ((index > 1 && contains(value, index - 2, 1, "B", "H", "D")) || ((index > 2 && contains(value, index - 3, 1, "B", "H", "D")) || (index > 3 && contains(value, index - 4, 1, "B", "H")))) {
            index2 = index + 2;
        } else {
            if (index > 2 && charAt(value, index - 1) == 'U' && contains(value, index - 3, 1, "C", Constants._TAG_G, "L", "R", "T")) {
                result.append('F');
            } else if (index > 0 && charAt(value, index - 1) != 'I') {
                result.append('K');
            }
            index2 = index + 2;
        }
        return index2;
    }

    private int handleH(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        if ((index == 0 || isVowel(charAt(value, index - 1))) && isVowel(charAt(value, index + 1))) {
            result.append('H');
            index2 = index + 2;
        } else {
            index2 = index + 1;
        }
        return index2;
    }

    private int handleJ(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
        int index2;
        if (contains(value, index, 4, "JOSE") || contains(value, 0, 4, "SAN ")) {
            if ((index == 0 && charAt(value, index + 4) == ' ') || value.length() == 4 || contains(value, 0, 4, "SAN ")) {
                result.append('H');
            } else {
                result.append('J', 'H');
            }
            index2 = index + 1;
        } else {
            if (index == 0 && !contains(value, index, 4, "JOSE")) {
                result.append('J', 'A');
            } else if (isVowel(charAt(value, index - 1)) && !slavoGermanic && (charAt(value, index + 1) == 'A' || charAt(value, index + 1) == 'O')) {
                result.append('J', 'H');
            } else if (index == value.length() - 1) {
                result.append('J', ' ');
            } else if (!contains(value, index + 1, 1, L_T_K_S_N_M_B_Z) && !contains(value, index - 1, 1, "S", "K", "L")) {
                result.append('J');
            }
            if (charAt(value, index + 1) == 'J') {
                index2 = index + 2;
            } else {
                index2 = index + 1;
            }
        }
        return index2;
    }

    private int handleL(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        if (charAt(value, index + 1) == 'L') {
            if (conditionL0(value, index)) {
                result.appendPrimary('L');
            } else {
                result.append('L');
            }
            index2 = index + 2;
        } else {
            index2 = index + 1;
            result.append('L');
        }
        return index2;
    }

    private int handleP(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        if (charAt(value, index + 1) == 'H') {
            result.append('F');
            index2 = index + 2;
        } else {
            result.append('P');
            index2 = contains(value, index + 1, 1, Constants._TAG_P, "B") ? index + 2 : index + 1;
        }
        return index2;
    }

    private int handleR(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
        if (index == value.length() - 1 && !slavoGermanic && contains(value, index - 2, 2, "IE") && !contains(value, index - 4, 2, "ME", "MA")) {
            result.appendAlternate('R');
        } else {
            result.append('R');
        }
        return charAt(value, index + 1) == 'R' ? index + 2 : index + 1;
    }

    private int handleS(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
        int index2;
        if (contains(value, index - 1, 3, "ISL", "YSL")) {
            index2 = index + 1;
        } else if (index == 0 && contains(value, index, 5, "SUGAR")) {
            result.append('X', 'S');
            index2 = index + 1;
        } else if (contains(value, index, 2, "SH")) {
            if (contains(value, index + 1, 4, "HEIM", "HOEK", "HOLM", "HOLZ")) {
                result.append('S');
            } else {
                result.append('X');
            }
            index2 = index + 2;
        } else if (contains(value, index, 3, "SIO", "SIA") || contains(value, index, 4, "SIAN")) {
            if (slavoGermanic) {
                result.append('S');
            } else {
                result.append('S', 'X');
            }
            index2 = index + 3;
        } else if ((index == 0 && contains(value, index + 1, 1, "M", "N", "L", "W")) || contains(value, index + 1, 1, "Z")) {
            result.append('S', 'X');
            index2 = contains(value, index + 1, 1, "Z") ? index + 2 : index + 1;
        } else if (contains(value, index, 2, BouncyCastleProvider.PROVIDER_NAME)) {
            index2 = handleSC(value, result, index);
        } else {
            if (index == value.length() - 1 && contains(value, index - 2, 2, "AI", "OI")) {
                result.appendAlternate('S');
            } else {
                result.append('S');
            }
            index2 = contains(value, index + 1, 1, "S", "Z") ? index + 2 : index + 1;
        }
        return index2;
    }

    private int handleSC(String value, DoubleMetaphoneResult result, int index) {
        if (charAt(value, index + 2) == 'H') {
            if (contains(value, index + 3, 2, "OO", "ER", "EN", "UY", "ED", "EM")) {
                if (contains(value, index + 3, 2, "ER", "EN")) {
                    result.append("X", "SK");
                } else {
                    result.append("SK");
                }
            } else if (index == 0 && !isVowel(charAt(value, 3)) && charAt(value, 3) != 'W') {
                result.append('X', 'S');
            } else {
                result.append('X');
            }
        } else if (contains(value, index + 2, 1, "I", "E", Constants._TAG_Y)) {
            result.append('S');
        } else {
            result.append("SK");
        }
        return index + 3;
    }

    private int handleT(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        if (contains(value, index, 4, "TION") || contains(value, index, 3, "TIA", "TCH")) {
            result.append('X');
            index2 = index + 3;
        } else if (contains(value, index, 2, "TH") || contains(value, index, 3, "TTH")) {
            if (contains(value, index + 2, 2, "OM", "AM") || contains(value, 0, 4, "VAN ", "VON ") || contains(value, 0, 3, "SCH")) {
                result.append('T');
            } else {
                result.append('0', 'T');
            }
            index2 = index + 2;
        } else {
            result.append('T');
            index2 = contains(value, index + 1, 1, "T", "D") ? index + 2 : index + 1;
        }
        return index2;
    }

    private int handleW(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        if (contains(value, index, 2, "WR")) {
            result.append('R');
            index2 = index + 2;
        } else if (index == 0 && (isVowel(charAt(value, index + 1)) || contains(value, index, 2, "WH"))) {
            if (isVowel(charAt(value, index + 1))) {
                result.append('A', 'F');
            } else {
                result.append('A');
            }
            index2 = index + 1;
        } else if ((index == value.length() - 1 && isVowel(charAt(value, index - 1))) || contains(value, index - 1, 5, "EWSKI", "EWSKY", "OWSKI", "OWSKY") || contains(value, 0, 3, "SCH")) {
            result.appendAlternate('F');
            index2 = index + 1;
        } else if (contains(value, index, 4, "WICZ", "WITZ")) {
            result.append("TS", "FX");
            index2 = index + 4;
        } else {
            index2 = index + 1;
        }
        return index2;
    }

    private int handleX(String value, DoubleMetaphoneResult result, int index) {
        int index2;
        if (index == 0) {
            result.append('S');
            index2 = index + 1;
        } else {
            if (index != value.length() - 1 || (!contains(value, index - 3, 3, "IAU", "EAU") && !contains(value, index - 2, 2, "AU", "OU"))) {
                result.append("KS");
            }
            index2 = contains(value, index + 1, 1, "C", "X") ? index + 2 : index + 1;
        }
        return index2;
    }

    private int handleZ(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic) {
        int index2;
        if (charAt(value, index + 1) == 'H') {
            result.append('J');
            index2 = index + 2;
        } else {
            if (contains(value, index + 1, 2, "ZO", "ZI", "ZA") || (slavoGermanic && index > 0 && charAt(value, index - 1) != 'T')) {
                result.append("S", "TS");
            } else {
                result.append('S');
            }
            index2 = charAt(value, index + 1) == 'Z' ? index + 2 : index + 1;
        }
        return index2;
    }

    public boolean isDoubleMetaphoneEqual(String value1, String value2) {
        return isDoubleMetaphoneEqual(value1, value2, false);
    }

    public boolean isDoubleMetaphoneEqual(String value1, String value2, boolean alternate) {
        return StringUtils.equals(doubleMetaphone(value1, alternate), doubleMetaphone(value2, alternate));
    }

    private boolean isSilentStart(String value) {
        boolean result = false;
        for (String element : SILENT_START) {
            if (value.startsWith(element)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean isSlavoGermanic(String value) {
        return value.indexOf(87) > -1 || value.indexOf(75) > -1 || value.contains("CZ") || value.contains("WITZ");
    }

    private boolean isVowel(char ch) {
        return VOWELS.indexOf(ch) != -1;
    }

    public void setMaxCodeLen(int maxCodeLen) {
        this.maxCodeLen = maxCodeLen;
    }
}
