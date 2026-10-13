package org.apache.commons.codec.language;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/language/Soundex.class */
public class Soundex implements StringEncoder {
    public static final char SILENT_MARKER = '-';
    public static final String US_ENGLISH_MAPPING_STRING = "01230120022455012623010202";
    private static final char[] US_ENGLISH_MAPPING = US_ENGLISH_MAPPING_STRING.toCharArray();
    public static final Soundex US_ENGLISH = new Soundex();
    public static final Soundex US_ENGLISH_SIMPLIFIED = new Soundex(US_ENGLISH_MAPPING_STRING, false);
    public static final Soundex US_ENGLISH_GENEALOGY = new Soundex("-123-12--22455-12623-1-2-2");

    @Deprecated
    private int maxLength;
    private final char[] soundexMapping;
    private final boolean specialCaseHW;

    public Soundex() {
        this.maxLength = 4;
        this.soundexMapping = US_ENGLISH_MAPPING;
        this.specialCaseHW = true;
    }

    public Soundex(char[] mapping) {
        this.maxLength = 4;
        this.soundexMapping = (char[]) mapping.clone();
        this.specialCaseHW = !hasMarker(this.soundexMapping);
    }

    public Soundex(String mapping) {
        this.maxLength = 4;
        this.soundexMapping = mapping.toCharArray();
        this.specialCaseHW = !hasMarker(this.soundexMapping);
    }

    public Soundex(String mapping, boolean specialCaseHW) {
        this.maxLength = 4;
        this.soundexMapping = mapping.toCharArray();
        this.specialCaseHW = specialCaseHW;
    }

    public int difference(String s1, String s2) throws EncoderException {
        return SoundexUtils.difference(this, s1, s2);
    }

    @Override // org.apache.commons.codec.Encoder
    public Object encode(Object obj) throws EncoderException {
        if (!(obj instanceof String)) {
            throw new EncoderException("Parameter supplied to Soundex encode is not of type java.lang.String");
        }
        return soundex((String) obj);
    }

    @Override // org.apache.commons.codec.StringEncoder
    public String encode(String str) {
        return soundex(str);
    }

    @Deprecated
    public int getMaxLength() {
        return this.maxLength;
    }

    private boolean hasMarker(char[] mapping) {
        for (char ch : mapping) {
            if (ch == '-') {
                return true;
            }
        }
        return false;
    }

    private char map(char ch) {
        int index = ch - 'A';
        if (index < 0 || index >= this.soundexMapping.length) {
            throw new IllegalArgumentException("The character is not mapped: " + ch + " (index=" + index + ")");
        }
        return this.soundexMapping[index];
    }

    @Deprecated
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public String soundex(String str) {
        char digit;
        if (str == null) {
            return null;
        }
        String str2 = SoundexUtils.clean(str);
        if (str2.isEmpty()) {
            return str2;
        }
        char[] out = {'0', '0', '0', '0'};
        char first = str2.charAt(0);
        int count = 0 + 1;
        out[0] = first;
        char lastDigit = map(first);
        for (int i = 1; i < str2.length() && count < out.length; i++) {
            char ch = str2.charAt(i);
            if ((!this.specialCaseHW || (ch != 'H' && ch != 'W')) && (digit = map(ch)) != '-') {
                if (digit != '0' && digit != lastDigit) {
                    int i2 = count;
                    count++;
                    out[i2] = digit;
                }
                lastDigit = digit;
            }
        }
        return new String(out);
    }
}
