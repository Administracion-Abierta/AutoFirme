package org.json;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/json/JSONPointer.class */
public class JSONPointer {
    private static final String ENCODING = "utf-8";
    private final List<String> refTokens;

    /* JADX INFO: loaded from: autofirma.jar:org/json/JSONPointer$Builder.class */
    public static class Builder {
        private final List<String> refTokens = new ArrayList();

        public JSONPointer build() {
            return new JSONPointer(this.refTokens);
        }

        public Builder append(String token) {
            if (token == null) {
                throw new NullPointerException("token cannot be null");
            }
            this.refTokens.add(token);
            return this;
        }

        public Builder append(int arrayIndex) {
            this.refTokens.add(String.valueOf(arrayIndex));
            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public JSONPointer(String pointer) {
        String refs;
        if (pointer == null) {
            throw new NullPointerException("pointer cannot be null");
        }
        if (pointer.isEmpty() || pointer.equals("#")) {
            this.refTokens = Collections.emptyList();
            return;
        }
        if (pointer.startsWith("#/")) {
            String refs2 = pointer.substring(2);
            try {
                refs = URLDecoder.decode(refs2, ENCODING);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else if (pointer.startsWith("/")) {
            refs = pointer.substring(1);
        } else {
            throw new IllegalArgumentException("a JSON pointer should start with '/' or '#/'");
        }
        this.refTokens = new ArrayList();
        int slashIdx = -1;
        do {
            int prevSlashIdx = slashIdx + 1;
            slashIdx = refs.indexOf(47, prevSlashIdx);
            if (prevSlashIdx == slashIdx || prevSlashIdx == refs.length()) {
                this.refTokens.add("");
            } else if (slashIdx >= 0) {
                String token = refs.substring(prevSlashIdx, slashIdx);
                this.refTokens.add(unescape(token));
            } else {
                String token2 = refs.substring(prevSlashIdx);
                this.refTokens.add(unescape(token2));
            }
        } while (slashIdx >= 0);
    }

    public JSONPointer(List<String> refTokens) {
        this.refTokens = new ArrayList(refTokens);
    }

    private static String unescape(String token) {
        return token.replace("~1", "/").replace("~0", "~");
    }

    public Object queryFrom(Object document) throws JSONPointerException {
        if (this.refTokens.isEmpty()) {
            return document;
        }
        Object current = document;
        for (String token : this.refTokens) {
            if (current instanceof JSONObject) {
                current = ((JSONObject) current).opt(unescape(token));
            } else if (current instanceof JSONArray) {
                current = readByIndexToken(current, token);
            } else {
                throw new JSONPointerException(String.format("value [%s] is not an array or object therefore its key %s cannot be resolved", current, token));
            }
        }
        return current;
    }

    private static Object readByIndexToken(Object current, String indexToken) throws JSONPointerException {
        try {
            int index = Integer.parseInt(indexToken);
            JSONArray currentArr = (JSONArray) current;
            if (index >= currentArr.length()) {
                throw new JSONPointerException(String.format("index %s is out of bounds - the array has %d elements", indexToken, Integer.valueOf(currentArr.length())));
            }
            try {
                return currentArr.get(index);
            } catch (JSONException e) {
                throw new JSONPointerException("Error reading value at index position " + index, e);
            }
        } catch (NumberFormatException e2) {
            throw new JSONPointerException(String.format("%s is not an array index", indexToken), e2);
        }
    }

    public String toString() {
        StringBuilder rval = new StringBuilder("");
        for (String token : this.refTokens) {
            rval.append('/').append(escape(token));
        }
        return rval.toString();
    }

    private static String escape(String token) {
        return token.replace("~", "~0").replace("/", "~1");
    }

    public String toURIFragment() {
        try {
            StringBuilder rval = new StringBuilder("#");
            for (String token : this.refTokens) {
                rval.append('/').append(URLEncoder.encode(token, ENCODING));
            }
            return rval.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
