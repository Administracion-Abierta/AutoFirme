package org.spongycastle.est;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/est/HttpUtil.class */
class HttpUtil {
    HttpUtil() {
    }

    static String mergeCSL(String prefix, Map<String, String> kv) {
        StringWriter sw = new StringWriter();
        sw.write(prefix);
        sw.write(32);
        boolean comma = false;
        for (Map.Entry<String, String> ent : kv.entrySet()) {
            if (!comma) {
                comma = true;
            } else {
                sw.write(44);
            }
            sw.write(ent.getKey());
            sw.write("=\"");
            sw.write(ent.getValue());
            sw.write(34);
        }
        return sw.toString();
    }

    static Map<String, String> splitCSL(String skip, String src) {
        String src2 = src.trim();
        if (src2.startsWith(skip)) {
            src2 = src2.substring(skip.length());
        }
        return new PartLexer(src2).Parse();
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/est/HttpUtil$PartLexer.class */
    static class PartLexer {
        private final String src;
        int last = 0;
        int p = 0;

        PartLexer(String src) {
            this.src = src;
        }

        Map<String, String> Parse() {
            Map<String, String> out = new HashMap<>();
            while (this.p < this.src.length()) {
                skipWhiteSpace();
                String key = consumeAlpha();
                if (key.length() == 0) {
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
                String value = consumeUntil('\"');
                discard(1);
                out.put(key, value);
                skipWhiteSpace();
                if (!consumeIf(',')) {
                    break;
                }
                discard();
            }
            return out;
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
            String s = this.src.substring(this.last, this.p);
            this.last = this.p;
            return s;
        }

        private void skipWhiteSpace() {
            while (this.p < this.src.length() && this.src.charAt(this.p) < '!') {
                this.p++;
            }
            this.last = this.p;
        }

        private boolean consumeIf(char c) {
            if (this.p < this.src.length() && this.src.charAt(this.p) == c) {
                this.p++;
                return true;
            }
            return false;
        }

        private String consumeUntil(char c) {
            while (this.p < this.src.length() && this.src.charAt(this.p) != c) {
                this.p++;
            }
            String s = this.src.substring(this.last, this.p);
            this.last = this.p;
            return s;
        }

        private void discard() {
            this.last = this.p;
        }

        private void discard(int i) {
            this.p += i;
            this.last = this.p;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/est/HttpUtil$Headers.class */
    static class Headers extends HashMap<String, String[]> {
        public String getFirstValue(String key) {
            String[] j = getValues(key);
            if (j != null && j.length > 0) {
                return j[0];
            }
            return null;
        }

        public String[] getValues(String key) {
            String key2 = actualKey(key);
            if (key2 == null) {
                return null;
            }
            return get(key2);
        }

        private String actualKey(String header) {
            if (containsKey(header)) {
                return header;
            }
            for (String k : keySet()) {
                if (header.equalsIgnoreCase(k)) {
                    return k;
                }
            }
            return null;
        }

        private boolean hasHeader(String header) {
            return actualKey(header) != null;
        }

        public void set(String key, String value) {
            put(key, new String[]{value});
        }

        public void add(String key, String value) {
            put(key, HttpUtil.append(get(key), value));
        }

        public void ensureHeader(String key, String value) {
            if (!containsKey(key)) {
                set(key, value);
            }
        }

        @Override // java.util.HashMap, java.util.AbstractMap
        public Object clone() {
            Headers n = new Headers();
            for (Map.Entry<String, String[]> entry : entrySet()) {
                n.put(entry.getKey(), copy(entry.getValue()));
            }
            return n;
        }

        private String[] copy(String[] vs) {
            String[] rv = new String[vs.length];
            System.arraycopy(vs, 0, rv, 0, rv.length);
            return rv;
        }
    }

    public static String[] append(String[] a, String b) {
        if (a == null) {
            return new String[]{b};
        }
        int length = a.length;
        String[] result = new String[length + 1];
        System.arraycopy(a, 0, result, 0, length);
        result[length] = b;
        return result;
    }
}
