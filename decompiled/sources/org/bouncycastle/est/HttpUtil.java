package org.bouncycastle.est;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/HttpUtil.class */
class HttpUtil {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/HttpUtil$Headers.class */
    static class Headers extends HashMap<String, String[]> {
        private static final String EMPTY = "";

        public String getFirstValue(String str) {
            String[] values = getValues(str);
            if (values == null || values.length <= 0) {
                return null;
            }
            return values[0];
        }

        public String getFirstValueOrEmpty(String str) {
            String[] values = getValues(str);
            return (values == null || values.length <= 0) ? "" : values[0];
        }

        public String[] getValues(String str) {
            String strActualKey = actualKey(str);
            if (strActualKey == null) {
                return null;
            }
            return get(strActualKey);
        }

        private String actualKey(String str) {
            if (containsKey(str)) {
                return str;
            }
            for (String str2 : keySet()) {
                if (str.equalsIgnoreCase(str2)) {
                    return str2;
                }
            }
            return null;
        }

        private boolean hasHeader(String str) {
            return actualKey(str) != null;
        }

        public void set(String str, String str2) {
            put(str, new String[]{str2});
        }

        public void add(String str, String str2) {
            put(str, HttpUtil.append(get(str), str2));
        }

        public void ensureHeader(String str, String str2) {
            if (containsKey(str)) {
                return;
            }
            set(str, str2);
        }

        @Override // java.util.HashMap, java.util.AbstractMap
        public Object clone() {
            Headers headers = new Headers();
            for (Map.Entry<String, String[]> entry : entrySet()) {
                headers.put(entry.getKey(), copy(entry.getValue()));
            }
            return headers;
        }

        private String[] copy(String[] strArr) {
            String[] strArr2 = new String[strArr.length];
            System.arraycopy(strArr, 0, strArr2, 0, strArr2.length);
            return strArr2;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/HttpUtil$PartLexer.class */
    static class PartLexer {
        private final String src;
        int last = 0;
        int p = 0;

        PartLexer(String str) {
            this.src = str;
        }

        Map<String, String> Parse() {
            HashMap map = new HashMap();
            while (this.p < this.src.length()) {
                skipWhiteSpace();
                String strConsumeAlpha = consumeAlpha();
                if (strConsumeAlpha.length() == 0) {
                    throw new IllegalArgumentException("Expecting alpha label.");
                }
                skipWhiteSpace();
                if (!consumeIf('=')) {
                    throw new IllegalArgumentException("Expecting assign: '='");
                }
                skipWhiteSpace();
                if (!consumeIf('\"')) {
                    throw new IllegalArgumentException("Expecting start quote: '\"'");
                }
                discard();
                String strConsumeUntil = consumeUntil('\"');
                discard(1);
                map.put(strConsumeAlpha, strConsumeUntil);
                skipWhiteSpace();
                if (!consumeIf(',')) {
                    break;
                }
                discard();
            }
            return map;
        }

        private String consumeAlpha() {
            char cCharAt = this.src.charAt(this.p);
            while (true) {
                char c = cCharAt;
                if (this.p >= this.src.length() || ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z'))) {
                    break;
                }
                this.p++;
                cCharAt = this.src.charAt(this.p);
            }
            String strSubstring = this.src.substring(this.last, this.p);
            this.last = this.p;
            return strSubstring;
        }

        private void skipWhiteSpace() {
            while (this.p < this.src.length() && this.src.charAt(this.p) < '!') {
                this.p++;
            }
            this.last = this.p;
        }

        private boolean consumeIf(char c) {
            if (this.p >= this.src.length() || this.src.charAt(this.p) != c) {
                return false;
            }
            this.p++;
            return true;
        }

        private String consumeUntil(char c) {
            while (this.p < this.src.length() && this.src.charAt(this.p) != c) {
                this.p++;
            }
            String strSubstring = this.src.substring(this.last, this.p);
            this.last = this.p;
            return strSubstring;
        }

        private void discard() {
            this.last = this.p;
        }

        private void discard(int i) {
            this.p += i;
            this.last = this.p;
        }
    }

    HttpUtil() {
    }

    static String mergeCSL(String str, Map<String, String> map) {
        StringWriter stringWriter = new StringWriter();
        stringWriter.write(str);
        stringWriter.write(32);
        boolean z = false;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (z) {
                stringWriter.write(44);
            } else {
                z = true;
            }
            stringWriter.write(entry.getKey());
            stringWriter.write("=\"");
            stringWriter.write(entry.getValue());
            stringWriter.write(34);
        }
        return stringWriter.toString();
    }

    static Map<String, String> splitCSL(String str, String str2) {
        String strTrim = str2.trim();
        if (strTrim.startsWith(str)) {
            strTrim = strTrim.substring(str.length());
        }
        return new PartLexer(strTrim).Parse();
    }

    public static String[] append(String[] strArr, String str) {
        if (strArr == null) {
            return new String[]{str};
        }
        int length = strArr.length;
        String[] strArr2 = new String[length + 1];
        System.arraycopy(strArr, 0, strArr2, 0, length);
        strArr2[length] = str;
        return strArr2;
    }
}
