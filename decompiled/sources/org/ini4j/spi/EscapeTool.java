package org.ini4j.spi;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/spi/EscapeTool.class */
public class EscapeTool {
    private static final String ESCAPE_LETTERS = "\\tnfbr:=";
    private static final String ESCAPEABLE_CHARS = "\\\t\n\f\b\r:=";
    private static final char ESCAPE_CHAR = '\\';
    static final char[] HEX = "0123456789abcdef".toCharArray();
    private static final EscapeTool INSTANCE = (EscapeTool) ServiceFinder.findService(EscapeTool.class);
    private static final char ASCII_MIN = ' ';
    private static final char ASCII_MAX = '~';
    static final int HEX_DIGIT_MASK = 15;
    static final int HEX_DIGIT_3_OFFSET = 4;
    static final int HEX_DIGIT_2_OFFSET = 8;
    static final int HEX_DIGIT_1_OFFSET = 12;
    static final int HEX_RADIX = 16;
    private static final int UNICODE_HEX_DIGITS = 4;
    static final char DOUBLE_QUOTE = '\"';

    public static EscapeTool getInstance() {
        return INSTANCE;
    }

    public String escape(String line) {
        int len = line.length();
        StringBuilder buffer = new StringBuilder(len * 2);
        for (int i = 0; i < len; i++) {
            char c = line.charAt(i);
            int idx = ESCAPEABLE_CHARS.indexOf(c);
            if (idx >= 0) {
                buffer.append('\\');
                buffer.append(ESCAPE_LETTERS.charAt(idx));
            } else if (c < ' ' || c > '~') {
                escapeBinary(buffer, c);
            } else {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }

    public String quote(String value) {
        String ret = value;
        if (value != null && value.length() != 0) {
            StringBuilder buff = new StringBuilder();
            buff.append('\"');
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (c == '\\' || c == '\"') {
                    buff.append('\\');
                }
                buff.append(c);
            }
            buff.append('\"');
            ret = buff.toString();
        }
        return ret;
    }

    public String unescape(String line) {
        int n = line.length();
        StringBuilder buffer = new StringBuilder(n);
        int i = 0;
        while (i < n) {
            int i2 = i;
            i++;
            char c = line.charAt(i2);
            if (c == '\\') {
                i++;
                char c2 = line.charAt(i);
                int next = unescapeBinary(buffer, c2, line, i);
                if (next == i) {
                    int idx = ESCAPE_LETTERS.indexOf(c2);
                    if (idx >= 0) {
                        c2 = ESCAPEABLE_CHARS.charAt(idx);
                    }
                    buffer.append(c2);
                } else {
                    i = next;
                }
            } else {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }

    public String unquote(String value) {
        StringBuilder buff = new StringBuilder();
        boolean escape = false;
        for (int i = 1; i < value.length() - 1; i++) {
            char c = value.charAt(i);
            if (c == '\\') {
                if (!escape) {
                    escape = true;
                } else {
                    escape = false;
                    buff.append(c);
                }
            } else {
                buff.append(c);
            }
        }
        return buff.toString();
    }

    void escapeBinary(StringBuilder buff, char c) {
        buff.append("\\u");
        buff.append(HEX[(c >>> '\f') & 15]);
        buff.append(HEX[(c >>> '\b') & 15]);
        buff.append(HEX[(c >>> 4) & 15]);
        buff.append(HEX[c & 15]);
    }

    int unescapeBinary(StringBuilder buff, char escapeType, String line, int index) {
        int ret = index;
        if (escapeType == 'u') {
            try {
                buff.append((char) Integer.parseInt(line.substring(index, index + 4), 16));
                ret = index + 4;
            } catch (Exception x) {
                throw new IllegalArgumentException("Malformed \\uxxxx encoding.", x);
            }
        }
        return ret;
    }
}
