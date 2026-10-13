package org.apache.commons.codec.language;

import java.util.Locale;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/language/MatchRatingApproachEncoder.class */
public class MatchRatingApproachEncoder implements StringEncoder {
    private static final String SPACE = " ";
    private static final String EMPTY = "";
    private static final String PLAIN_ASCII = "AaEeIiOoUuAaEeIiOoUuYyAaEeIiOoUuYyAaOoNnAaEeIiOoUuYyAaCcOoUu";
    private static final String UNICODE = "ÀàÈèÌìÒòÙùÁáÉéÍíÓóÚúÝýÂâÊêÎîÔôÛûŶŷÃãÕõÑñÄäËëÏïÖöÜüŸÿÅåÇçŐőŰű";
    private static final String[] DOUBLE_CONSONANT = {"BB", "CC", "DD", "FF", "GG", "HH", "JJ", "KK", "LL", "MM", "NN", "PP", "QQ", "RR", "SS", "TT", "VV", "WW", "XX", "YY", "ZZ"};

    String cleanName(String name) {
        String upperName = name.toUpperCase(Locale.ENGLISH);
        String[] charsToTrim = {"\\-", "[&]", "\\'", "\\.", "[\\,]"};
        for (String str : charsToTrim) {
            upperName = upperName.replaceAll(str, "");
        }
        return removeAccents(upperName).replaceAll("\\s+", "");
    }

    @Override // org.apache.commons.codec.Encoder
    public final Object encode(Object pObject) throws EncoderException {
        if (!(pObject instanceof String)) {
            throw new EncoderException("Parameter supplied to Match Rating Approach encoder is not of type java.lang.String");
        }
        return encode((String) pObject);
    }

    @Override // org.apache.commons.codec.StringEncoder
    public final String encode(String name) {
        if (name == null || "".equalsIgnoreCase(name) || SPACE.equalsIgnoreCase(name) || name.length() == 1) {
            return "";
        }
        String name2 = cleanName(name);
        if (SPACE.equals(name2) || name2.isEmpty()) {
            return "";
        }
        String name3 = removeVowels(name2);
        if (SPACE.equals(name3) || name3.isEmpty()) {
            return "";
        }
        return getFirst3Last3(removeDoubleConsonants(name3));
    }

    String getFirst3Last3(String name) {
        int nameLength = name.length();
        if (nameLength > 6) {
            String firstThree = name.substring(0, 3);
            String lastThree = name.substring(nameLength - 3, nameLength);
            return firstThree + lastThree;
        }
        return name;
    }

    int getMinRating(int sumLength) {
        int minRating;
        if (sumLength <= 4) {
            minRating = 5;
        } else if (sumLength <= 7) {
            minRating = 4;
        } else if (sumLength <= 11) {
            minRating = 3;
        } else if (sumLength == 12) {
            minRating = 2;
        } else {
            minRating = 1;
        }
        return minRating;
    }

    public boolean isEncodeEquals(String name1, String name2) {
        if (name1 == null || "".equalsIgnoreCase(name1) || SPACE.equalsIgnoreCase(name1) || name2 == null || "".equalsIgnoreCase(name2) || SPACE.equalsIgnoreCase(name2) || name1.length() == 1 || name2.length() == 1) {
            return false;
        }
        if (name1.equalsIgnoreCase(name2)) {
            return true;
        }
        String name3 = cleanName(name1);
        String name4 = cleanName(name2);
        String name5 = removeVowels(name3);
        String name6 = removeVowels(name4);
        String name7 = removeDoubleConsonants(name5);
        String name8 = removeDoubleConsonants(name6);
        String name9 = getFirst3Last3(name7);
        String name10 = getFirst3Last3(name8);
        if (Math.abs(name9.length() - name10.length()) >= 3) {
            return false;
        }
        int sumLength = Math.abs(name9.length() + name10.length());
        int minRating = getMinRating(sumLength);
        int count = leftToRightThenRightToLeftProcessing(name9, name10);
        return count >= minRating;
    }

    boolean isVowel(String letter) {
        return letter.equalsIgnoreCase("E") || letter.equalsIgnoreCase("A") || letter.equalsIgnoreCase("O") || letter.equalsIgnoreCase("I") || letter.equalsIgnoreCase("U");
    }

    int leftToRightThenRightToLeftProcessing(String name1, String name2) {
        char[] name1Char = name1.toCharArray();
        char[] name2Char = name2.toCharArray();
        int name1Size = name1.length() - 1;
        int name2Size = name2.length() - 1;
        for (int i = 0; i < name1Char.length && i <= name2Size; i++) {
            String name1LtRStart = name1.substring(i, i + 1);
            String name1LtREnd = name1.substring(name1Size - i, (name1Size - i) + 1);
            String name2RtLStart = name2.substring(i, i + 1);
            String name2RtLEnd = name2.substring(name2Size - i, (name2Size - i) + 1);
            if (name1LtRStart.equals(name2RtLStart)) {
                name1Char[i] = ' ';
                name2Char[i] = ' ';
            }
            if (name1LtREnd.equals(name2RtLEnd)) {
                name1Char[name1Size - i] = ' ';
                name2Char[name2Size - i] = ' ';
            }
        }
        String strA = new String(name1Char).replaceAll("\\s+", "");
        String strB = new String(name2Char).replaceAll("\\s+", "");
        if (strA.length() > strB.length()) {
            return Math.abs(6 - strA.length());
        }
        return Math.abs(6 - strB.length());
    }

    String removeAccents(String accentedWord) {
        if (accentedWord == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int n = accentedWord.length();
        for (int i = 0; i < n; i++) {
            char c = accentedWord.charAt(i);
            int pos = UNICODE.indexOf(c);
            if (pos > -1) {
                sb.append(PLAIN_ASCII.charAt(pos));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    String removeDoubleConsonants(String name) {
        String replacedName = name.toUpperCase(Locale.ENGLISH);
        for (String dc : DOUBLE_CONSONANT) {
            if (replacedName.contains(dc)) {
                String singleLetter = dc.substring(0, 1);
                replacedName = replacedName.replace(dc, singleLetter);
            }
        }
        return replacedName;
    }

    String removeVowels(String name) {
        String firstLetter = name.substring(0, 1);
        String name2 = name.replace("A", "").replace("E", "").replace("I", "").replace("O", "").replace("U", "").replaceAll("\\s{2,}\\b", SPACE);
        if (isVowel(firstLetter)) {
            return firstLetter + name2;
        }
        return name2;
    }
}
