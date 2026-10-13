package com.ctc.wstx.io;

import java.io.IOException;
import java.io.Writer;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/io/TextEscaper.class */
public final class TextEscaper {
    private TextEscaper() {
    }

    public static void writeEscapedAttrValue(Writer w, String value) throws IOException {
        int i = 0;
        int len = value.length();
        do {
            int start = i;
            char c = 0;
            while (i < len) {
                c = value.charAt(i);
                if (c == '<' || c == '&' || c == '\"') {
                    break;
                } else {
                    i++;
                }
            }
            int outLen = i - start;
            if (outLen > 0) {
                w.write(value, start, outLen);
            }
            if (i < len) {
                if (c == '<') {
                    w.write("&lt;");
                } else if (c == '&') {
                    w.write("&amp;");
                } else if (c == '\"') {
                    w.write("&quot;");
                }
            }
            i++;
        } while (i < len);
    }

    public static void outputDTDText(Writer w, char[] ch, int offset, int len) throws IOException {
        int i = offset;
        int len2 = len + offset;
        do {
            int start = i;
            char c = 0;
            while (i < len2) {
                c = ch[i];
                if (c == '&' || c == '%' || c == '\"') {
                    break;
                } else {
                    i++;
                }
            }
            int outLen = i - start;
            if (outLen > 0) {
                w.write(ch, start, outLen);
            }
            if (i < len2) {
                if (c == '&') {
                    w.write("&amp;");
                } else if (c == '%') {
                    w.write("&#37;");
                } else if (c == '\"') {
                    w.write("&#34;");
                }
            }
            i++;
        } while (i < len2);
    }
}
