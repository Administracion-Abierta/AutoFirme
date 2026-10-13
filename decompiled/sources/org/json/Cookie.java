package org.json;

import java.util.Locale;
import org.mozilla.javascript.ES6Iterator;

/* JADX INFO: loaded from: autofirma.jar:org/json/Cookie.class */
public class Cookie {
    public static String escape(String string) {
        String s = string.trim();
        int length = s.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (c < ' ' || c == '+' || c == '%' || c == '=' || c == ';') {
                sb.append('%');
                sb.append(Character.forDigit((char) ((c >>> 4) & 15), 16));
                sb.append(Character.forDigit((char) (c & 15), 16));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static JSONObject toJSONObject(String string) {
        Object value;
        JSONObject jo = new JSONObject();
        JSONTokener x = new JSONTokener(string);
        String name = unescape(x.nextTo('=').trim());
        if ("".equals(name)) {
            throw new JSONException("Cookies must have a 'name'");
        }
        jo.put("name", name);
        x.next('=');
        jo.put(ES6Iterator.VALUE_PROPERTY, unescape(x.nextTo(';')).trim());
        x.next();
        while (x.more()) {
            String name2 = unescape(x.nextTo("=;")).trim().toLowerCase(Locale.ROOT);
            if ("name".equalsIgnoreCase(name2)) {
                throw new JSONException("Illegal attribute name: 'name'");
            }
            if (ES6Iterator.VALUE_PROPERTY.equalsIgnoreCase(name2)) {
                throw new JSONException("Illegal attribute name: 'value'");
            }
            if (x.next() != '=') {
                value = Boolean.TRUE;
            } else {
                value = unescape(x.nextTo(';')).trim();
                x.next();
            }
            if (!"".equals(name2) && !"".equals(value)) {
                jo.put(name2, value);
            }
        }
        return jo;
    }

    public static String toString(JSONObject jo) throws JSONException {
        StringBuilder sb = new StringBuilder();
        String name = null;
        Object value = null;
        for (String key : jo.keySet()) {
            if ("name".equalsIgnoreCase(key)) {
                name = jo.getString(key).trim();
            }
            if (ES6Iterator.VALUE_PROPERTY.equalsIgnoreCase(key)) {
                value = jo.getString(key).trim();
            }
            if (name != null && value != null) {
                break;
            }
        }
        if (name == null || "".equals(name.trim())) {
            throw new JSONException("Cookie does not have a name");
        }
        if (value == null) {
            value = "";
        }
        sb.append(escape(name));
        sb.append("=");
        sb.append(escape((String) value));
        for (String key2 : jo.keySet()) {
            if (!"name".equalsIgnoreCase(key2) && !ES6Iterator.VALUE_PROPERTY.equalsIgnoreCase(key2)) {
                Object value2 = jo.opt(key2);
                if (value2 instanceof Boolean) {
                    if (Boolean.TRUE.equals(value2)) {
                        sb.append(';').append(escape(key2));
                    }
                } else {
                    sb.append(';').append(escape(key2)).append('=').append(escape(value2.toString()));
                }
            }
        }
        return sb.toString();
    }

    public static String unescape(String string) {
        int length = string.length();
        StringBuilder sb = new StringBuilder(length);
        int i = 0;
        while (i < length) {
            char c = string.charAt(i);
            if (c == '+') {
                c = ' ';
            } else if (c == '%' && i + 2 < length) {
                int d = JSONTokener.dehexchar(string.charAt(i + 1));
                int e = JSONTokener.dehexchar(string.charAt(i + 2));
                if (d >= 0 && e >= 0) {
                    c = (char) ((d * 16) + e);
                    i += 2;
                }
            }
            sb.append(c);
            i++;
        }
        return sb.toString();
    }
}
