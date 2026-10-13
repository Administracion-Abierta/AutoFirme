package com.aowagie.text.pdf;

import com.sun.jna.platform.win32.WinError;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/ArabicLigaturizer.class */
class ArabicLigaturizer {
    private static final char ALEF = 1575;
    private static final char ALEFHAMZA = 1571;
    private static final char ALEFHAMZABELOW = 1573;
    private static final char ALEFMADDA = 1570;
    private static final char LAM = 1604;
    private static final char HAMZA = 1569;
    private static final char TATWEEL = 1600;
    private static final char ZWJ = 8205;
    private static final char HAMZAABOVE = 1620;
    private static final char HAMZABELOW = 1621;
    private static final char WAWHAMZA = 1572;
    private static final char YEHHAMZA = 1574;
    private static final char WAW = 1608;
    private static final char ALEFMAKSURA = 1609;
    private static final char YEH = 1610;
    private static final char FARSIYEH = 1740;
    private static final char SHADDA = 1617;
    private static final char KASRA = 1616;
    private static final char FATHA = 1614;
    private static final char DAMMA = 1615;
    private static final char MADDA = 1619;
    private static final char LAM_ALEF = 65275;
    private static final char LAM_ALEFHAMZA = 65271;
    private static final char LAM_ALEFHAMZABELOW = 65273;
    private static final char LAM_ALEFMADDA = 65269;
    private static final char[][] chartable = {new char[]{1569, 65152}, new char[]{1570, 65153, 65154}, new char[]{1571, 65155, 65156}, new char[]{1572, 65157, 65158}, new char[]{1573, 65159, 65160}, new char[]{1574, 65161, 65162, 65163, 65164}, new char[]{1575, 65165, 65166}, new char[]{1576, 65167, 65168, 65169, 65170}, new char[]{1577, 65171, 65172}, new char[]{1578, 65173, 65174, 65175, 65176}, new char[]{1579, 65177, 65178, 65179, 65180}, new char[]{1580, 65181, 65182, 65183, 65184}, new char[]{1581, 65185, 65186, 65187, 65188}, new char[]{1582, 65189, 65190, 65191, 65192}, new char[]{1583, 65193, 65194}, new char[]{1584, 65195, 65196}, new char[]{1585, 65197, 65198}, new char[]{1586, 65199, 65200}, new char[]{1587, 65201, 65202, 65203, 65204}, new char[]{1588, 65205, 65206, 65207, 65208}, new char[]{1589, 65209, 65210, 65211, 65212}, new char[]{1590, 65213, 65214, 65215, 65216}, new char[]{1591, 65217, 65218, 65219, 65220}, new char[]{1592, 65221, 65222, 65223, 65224}, new char[]{1593, 65225, 65226, 65227, 65228}, new char[]{1594, 65229, 65230, 65231, 65232}, new char[]{1600, 1600, 1600, 1600, 1600}, new char[]{1601, 65233, 65234, 65235, 65236}, new char[]{1602, 65237, 65238, 65239, 65240}, new char[]{1603, 65241, 65242, 65243, 65244}, new char[]{1604, 65245, 65246, 65247, 65248}, new char[]{1605, 65249, 65250, 65251, 65252}, new char[]{1606, 65253, 65254, 65255, 65256}, new char[]{1607, 65257, 65258, 65259, 65260}, new char[]{1608, 65261, 65262}, new char[]{1609, 65263, 65264, 64488, 64489}, new char[]{1610, 65265, 65266, 65267, 65268}, new char[]{1649, 64336, 64337}, new char[]{1657, 64358, 64359, 64360, 64361}, new char[]{1658, 64350, 64351, 64352, 64353}, new char[]{1659, 64338, 64339, 64340, 64341}, new char[]{1662, 64342, 64343, 64344, 64345}, new char[]{1663, 64354, 64355, 64356, 64357}, new char[]{1664, 64346, 64347, 64348, 64349}, new char[]{1667, 64374, 64375, 64376, 64377}, new char[]{1668, 64370, 64371, 64372, 64373}, new char[]{1670, 64378, 64379, 64380, 64381}, new char[]{1671, 64382, 64383, 64384, 64385}, new char[]{1672, 64392, 64393}, new char[]{1676, 64388, 64389}, new char[]{1677, 64386, 64387}, new char[]{1678, 64390, 64391}, new char[]{1681, 64396, 64397}, new char[]{1688, 64394, 64395}, new char[]{1700, 64362, 64363, 64364, 64365}, new char[]{1702, 64366, 64367, 64368, 64369}, new char[]{1705, 64398, 64399, 64400, 64401}, new char[]{1709, 64467, 64468, 64469, 64470}, new char[]{1711, 64402, 64403, 64404, 64405}, new char[]{1713, 64410, 64411, 64412, 64413}, new char[]{1715, 64406, 64407, 64408, 64409}, new char[]{1722, 64414, 64415}, new char[]{1723, 64416, 64417, 64418, 64419}, new char[]{1726, 64426, 64427, 64428, 64429}, new char[]{1728, 64420, 64421}, new char[]{1729, 64422, 64423, 64424, 64425}, new char[]{1733, 64480, 64481}, new char[]{1734, 64473, 64474}, new char[]{1735, 64471, 64472}, new char[]{1736, 64475, 64476}, new char[]{1737, 64482, 64483}, new char[]{1739, 64478, 64479}, new char[]{1740, 64508, 64509, 64510, 64511}, new char[]{1744, 64484, 64485, 64486, 64487}, new char[]{1746, 64430, 64431}, new char[]{1747, 64432, 64433}};
    static final int ar_novowel = 1;
    static final int ar_composedtashkeel = 4;
    static final int ar_lig = 8;
    static final int DIGITS_EN2AN = 32;
    static final int DIGITS_AN2EN = 64;
    static final int DIGITS_EN2AN_INIT_LR = 96;
    static final int DIGITS_EN2AN_INIT_AL = 128;
    private static final int DIGITS_RESERVED = 160;
    private static final int DIGITS_MASK = 224;
    static final int DIGIT_TYPE_AN = 0;
    static final int DIGIT_TYPE_AN_EXTENDED = 256;
    private static final int DIGIT_TYPE_MASK = 256;

    ArabicLigaturizer() {
    }

    private static boolean isVowel(char s) {
        return (s >= 1611 && s <= 1621) || s == 1648;
    }

    private static char charshape(char s, int which) {
        if (s >= HAMZA && s <= 1747) {
            int l = 0;
            int r = chartable.length - 1;
            while (l <= r) {
                int m = (l + r) / 2;
                if (s == chartable[m][0]) {
                    return chartable[m][which + 1];
                }
                if (s < chartable[m][0]) {
                    r = m - 1;
                } else {
                    l = m + 1;
                }
            }
        } else if (s >= LAM_ALEFMADDA && s <= LAM_ALEF) {
            return (char) (s + which);
        }
        return s;
    }

    private static int shapecount(char s) {
        if (s >= HAMZA && s <= 1747 && !isVowel(s)) {
            int l = 0;
            int r = chartable.length - 1;
            while (l <= r) {
                int m = (l + r) / 2;
                if (s == chartable[m][0]) {
                    return chartable[m].length - 1;
                }
                if (s < chartable[m][0]) {
                    r = m - 1;
                } else {
                    l = m + 1;
                }
            }
            return 1;
        }
        if (s == 8205) {
            return 4;
        }
        return 1;
    }

    private static int ligature(char newchar, charstruct oldchar) {
        int retval = 0;
        if (oldchar.basechar == 0) {
            return 0;
        }
        if (isVowel(newchar)) {
            int retval2 = 1;
            if (oldchar.vowel != 0 && newchar != 1617) {
                retval2 = 2;
            }
            switch (newchar) {
                case 1617:
                    if (oldchar.mark1 != 0) {
                        return 0;
                    }
                    oldchar.mark1 = (char) 1617;
                    break;
                    break;
                case WinError.ERROR_INSTALL_ALREADY_RUNNING /* 1618 */:
                default:
                    oldchar.vowel = newchar;
                    break;
                case 1619:
                    switch (oldchar.basechar) {
                        case ALEF /* 1575 */:
                            oldchar.basechar = (char) 1570;
                            retval2 = 2;
                            break;
                    }
                    break;
                case 1620:
                    switch (oldchar.basechar) {
                        case ALEF /* 1575 */:
                            oldchar.basechar = (char) 1571;
                            retval2 = 2;
                            break;
                        case 1608:
                            oldchar.basechar = (char) 1572;
                            retval2 = 2;
                            break;
                        case 1609:
                        case 1610:
                        case 1740:
                            oldchar.basechar = (char) 1574;
                            retval2 = 2;
                            break;
                        case LAM_ALEF /* 65275 */:
                            oldchar.basechar = (char) 65271;
                            retval2 = 2;
                            break;
                        default:
                            oldchar.mark1 = (char) 1620;
                            break;
                    }
                    break;
                case 1621:
                    switch (oldchar.basechar) {
                        case ALEF /* 1575 */:
                            oldchar.basechar = (char) 1573;
                            retval2 = 2;
                            break;
                        case LAM_ALEF /* 65275 */:
                            oldchar.basechar = (char) 65273;
                            retval2 = 2;
                            break;
                        default:
                            oldchar.mark1 = (char) 1621;
                            break;
                    }
                    break;
            }
            if (retval2 == 1) {
                charstruct.access$308(oldchar);
            }
            return retval2;
        }
        if (oldchar.vowel != 0) {
            return 0;
        }
        switch (oldchar.basechar) {
            case 0:
                oldchar.basechar = newchar;
                oldchar.numshapes = shapecount(newchar);
                retval = 1;
                break;
            case 1604:
                switch (newchar) {
                    case ALEFMADDA /* 1570 */:
                        oldchar.basechar = (char) 65269;
                        oldchar.numshapes = 2;
                        retval = 3;
                        break;
                    case ALEFHAMZA /* 1571 */:
                        oldchar.basechar = (char) 65271;
                        oldchar.numshapes = 2;
                        retval = 3;
                        break;
                    case ALEFHAMZABELOW /* 1573 */:
                        oldchar.basechar = (char) 65273;
                        oldchar.numshapes = 2;
                        retval = 3;
                        break;
                    case ALEF /* 1575 */:
                        oldchar.basechar = (char) 65275;
                        oldchar.numshapes = 2;
                        retval = 3;
                        break;
                }
                break;
        }
        return retval;
    }

    private static void copycstostring(StringBuffer string, charstruct s, int level) {
        if (s.basechar == 0) {
            return;
        }
        string.append(s.basechar);
        charstruct.access$310(s);
        if (s.mark1 != 0) {
            if ((level & 1) == 0) {
                string.append(s.mark1);
                charstruct.access$310(s);
            } else {
                charstruct.access$310(s);
            }
        }
        if (s.vowel != 0) {
            if ((level & 1) == 0) {
                string.append(s.vowel);
                charstruct.access$310(s);
            } else {
                charstruct.access$310(s);
            }
        }
    }

    private static void doublelig(StringBuffer string, int level) {
        int olen = string.length();
        int len = olen;
        int j = 0;
        int si = 1;
        while (si < olen) {
            char lapresult = 0;
            if ((level & 4) != 0) {
                switch (string.charAt(j)) {
                    case 1614:
                        if (string.charAt(si) == 1617) {
                            lapresult = 64608;
                        }
                        break;
                    case 1615:
                        if (string.charAt(si) == 1617) {
                            lapresult = 64609;
                        }
                        break;
                    case 1616:
                        if (string.charAt(si) == 1617) {
                            lapresult = 64610;
                        }
                        break;
                    case 1617:
                        switch (string.charAt(si)) {
                            case WinError.ERROR_INSTALL_SOURCE_ABSENT /* 1612 */:
                                lapresult = 64606;
                                break;
                            case WinError.ERROR_INSTALL_PACKAGE_VERSION /* 1613 */:
                                lapresult = 64607;
                                break;
                            case 1614:
                                lapresult = 64608;
                                break;
                            case 1615:
                                lapresult = 64609;
                                break;
                            case 1616:
                                lapresult = 64610;
                                break;
                        }
                        break;
                }
            }
            if ((level & 8) != 0) {
                switch (string.charAt(j)) {
                    case 65169:
                        switch (string.charAt(si)) {
                            case 65184:
                                lapresult = 64668;
                                break;
                            case 65188:
                                lapresult = 64669;
                                break;
                            case 65192:
                                lapresult = 64670;
                                break;
                        }
                        break;
                    case 65175:
                        switch (string.charAt(si)) {
                            case 65184:
                                lapresult = 64673;
                                break;
                            case 65188:
                                lapresult = 64674;
                                break;
                            case 65192:
                                lapresult = 64675;
                                break;
                        }
                        break;
                    case 65235:
                        switch (string.charAt(si)) {
                            case 65266:
                                lapresult = 64562;
                                break;
                        }
                        break;
                    case 65247:
                        switch (string.charAt(si)) {
                            case 65182:
                                lapresult = 64575;
                                break;
                            case 65184:
                                lapresult = 64713;
                                break;
                            case 65186:
                                lapresult = 64576;
                                break;
                            case 65188:
                                lapresult = 64714;
                                break;
                            case 65190:
                                lapresult = 64577;
                                break;
                            case 65192:
                                lapresult = 64715;
                                break;
                            case 65250:
                                lapresult = 64578;
                                break;
                            case 65252:
                                lapresult = 64716;
                                break;
                        }
                        break;
                    case 65251:
                        switch (string.charAt(si)) {
                            case 65184:
                                lapresult = 64718;
                                break;
                            case 65188:
                                lapresult = 64719;
                                break;
                            case 65192:
                                lapresult = 64720;
                                break;
                            case 65252:
                                lapresult = 64721;
                                break;
                        }
                        break;
                    case 65255:
                        switch (string.charAt(si)) {
                            case 65184:
                                lapresult = 64722;
                                break;
                            case 65188:
                                lapresult = 64723;
                                break;
                            case 65192:
                                lapresult = 64724;
                                break;
                        }
                        break;
                    case 65256:
                        switch (string.charAt(si)) {
                            case 65198:
                                lapresult = 64650;
                                break;
                            case 65200:
                                lapresult = 64651;
                                break;
                        }
                        break;
                }
            }
            if (lapresult != 0) {
                string.setCharAt(j, lapresult);
                len--;
                si++;
            } else {
                j++;
                string.setCharAt(j, string.charAt(si));
                si++;
            }
        }
        string.setLength(len);
    }

    private static boolean connects_to_left(charstruct a) {
        return a.numshapes > 2;
    }

    private static void shape(char[] text, StringBuffer string, int level) {
        int which;
        int which2;
        int p = 0;
        charstruct oldchar = new charstruct();
        charstruct curchar = new charstruct();
        while (p < text.length) {
            int i = p;
            p++;
            char nextletter = text[i];
            int join = ligature(nextletter, curchar);
            if (join == 0) {
                int nc = shapecount(nextletter);
                if (nc == 1) {
                    which2 = 0;
                } else {
                    which2 = 2;
                }
                if (connects_to_left(oldchar)) {
                    which2++;
                }
                curchar.basechar = charshape(curchar.basechar, which2 % curchar.numshapes);
                copycstostring(string, oldchar, level);
                oldchar = curchar;
                curchar = new charstruct();
                curchar.basechar = nextletter;
                curchar.numshapes = nc;
                charstruct.access$308(curchar);
            } else if (join == 1) {
            }
        }
        if (connects_to_left(oldchar)) {
            which = 1;
        } else {
            which = 0;
        }
        curchar.basechar = charshape(curchar.basechar, which % curchar.numshapes);
        copycstostring(string, oldchar, level);
        copycstostring(string, curchar, level);
    }

    static int arabic_shape(char[] src, int srcoffset, int srclength, char[] dest, int destoffset, int destlength, int level) {
        char[] str = new char[srclength];
        for (int k = (srclength + srcoffset) - 1; k >= srcoffset; k--) {
            str[k - srcoffset] = src[k];
        }
        StringBuffer string = new StringBuffer(srclength);
        shape(str, string, level);
        if ((level & 12) != 0) {
            doublelig(string, level);
        }
        System.arraycopy(string.toString().toCharArray(), 0, dest, destoffset, string.length());
        return string.length();
    }

    static void processNumbers(char[] text, int offset, int length, int options) {
        int limit = offset + length;
        if ((options & 224) != 0) {
            char digitBase = '0';
            switch (options & 256) {
                case 0:
                    digitBase = 1632;
                    break;
                case 256:
                    digitBase = 1776;
                    break;
            }
            switch (options & 224) {
                case 32:
                    int digitDelta = digitBase - '0';
                    for (int i = offset; i < limit; i++) {
                        char ch = text[i];
                        if (ch <= '9' && ch >= '0') {
                            int i2 = i;
                            text[i2] = (char) (text[i2] + digitDelta);
                        }
                    }
                    break;
                case 64:
                    char digitTop = (char) (digitBase + '\t');
                    int digitDelta2 = '0' - digitBase;
                    for (int i3 = offset; i3 < limit; i3++) {
                        char ch2 = text[i3];
                        if (ch2 <= digitTop && ch2 >= digitBase) {
                            int i4 = i3;
                            text[i4] = (char) (text[i4] + digitDelta2);
                        }
                    }
                    break;
                case 96:
                    shapeToArabicDigitsWithContext(text, 0, length, digitBase, false);
                    break;
                case 128:
                    shapeToArabicDigitsWithContext(text, 0, length, digitBase, true);
                    break;
            }
        }
    }

    private static void shapeToArabicDigitsWithContext(char[] dest, int start, int length, char digitBase, boolean lastStrongWasAL) {
        char digitBase2 = (char) (digitBase - '0');
        int limit = start + length;
        for (int i = start; i < limit; i++) {
            char ch = dest[i];
            switch (BidiOrder.getDirection(ch)) {
                case 0:
                case 3:
                    lastStrongWasAL = false;
                    break;
                case 4:
                    lastStrongWasAL = true;
                    break;
                case 8:
                    if (lastStrongWasAL && ch <= '9') {
                        dest[i] = (char) (ch + digitBase2);
                    }
                    break;
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/ArabicLigaturizer$charstruct.class */
    private static class charstruct {
        private char basechar;
        private char mark1;
        private char vowel;
        private int lignum;
        private int numshapes;

        private charstruct() {
            this.numshapes = 1;
        }

        static /* synthetic */ int access$308(charstruct x0) {
            int i = x0.lignum;
            x0.lignum = i + 1;
            return i;
        }

        static /* synthetic */ int access$310(charstruct x0) {
            int i = x0.lignum;
            x0.lignum = i - 1;
            return i;
        }
    }
}
