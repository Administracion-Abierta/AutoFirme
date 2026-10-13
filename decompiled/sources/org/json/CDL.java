package org.json;

/* JADX INFO: loaded from: autofirma.jar:org/json/CDL.class */
public class CDL {
    private static String getValue(JSONTokener x, char delimiter) throws JSONException {
        char c;
        char nextC;
        while (true) {
            c = x.next();
            if (c != ' ' && c != '\t') {
                break;
            }
        }
        if (c == 0) {
            return null;
        }
        if (c == '\"' || c == '\'') {
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c2 = x.next();
                if (c2 == c && (nextC = x.next()) != '\"') {
                    if (nextC > 0) {
                        x.back();
                    }
                    return sb.toString();
                }
                if (c2 == 0 || c2 == '\n' || c2 == '\r') {
                    throw x.syntaxError("Missing close quote '" + c + "'.");
                }
                sb.append(c2);
            }
        } else {
            if (c == delimiter) {
                x.back();
                return "";
            }
            x.back();
            return x.nextTo(delimiter);
        }
    }

    public static JSONArray rowToJSONArray(JSONTokener x) throws JSONException {
        return rowToJSONArray(x, ',');
    }

    public static JSONArray rowToJSONArray(JSONTokener x, char delimiter) throws JSONException {
        JSONArray ja = new JSONArray();
        while (true) {
            String value = getValue(x, delimiter);
            char c = x.next();
            if (value != null) {
                if (ja.length() == 0 && value.length() == 0 && c != delimiter) {
                    return null;
                }
                ja.put(value);
                while (c != delimiter) {
                    if (c != ' ') {
                        if (c == '\n' || c == '\r' || c == 0) {
                            return ja;
                        }
                        throw x.syntaxError("Bad character '" + c + "' (" + ((int) c) + ").");
                    }
                    c = x.next();
                }
            } else {
                return null;
            }
        }
    }

    public static JSONObject rowToJSONObject(JSONArray names, JSONTokener x) throws JSONException {
        return rowToJSONObject(names, x, ',');
    }

    public static JSONObject rowToJSONObject(JSONArray names, JSONTokener x, char delimiter) throws JSONException {
        JSONArray ja = rowToJSONArray(x, delimiter);
        if (ja != null) {
            return ja.toJSONObject(names);
        }
        return null;
    }

    public static String rowToString(JSONArray ja) {
        return rowToString(ja, ',');
    }

    public static String rowToString(JSONArray ja, char delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ja.length(); i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            Object object = ja.opt(i);
            if (object != null) {
                String string = object.toString();
                if (string.length() > 0 && (string.indexOf(delimiter) >= 0 || string.indexOf(10) >= 0 || string.indexOf(13) >= 0 || string.indexOf(0) >= 0 || string.charAt(0) == '\"')) {
                    sb.append('\"');
                    int length = string.length();
                    for (int j = 0; j < length; j++) {
                        char c = string.charAt(j);
                        if (c >= ' ' && c != '\"') {
                            sb.append(c);
                        }
                    }
                    sb.append('\"');
                } else {
                    sb.append(string);
                }
            }
        }
        sb.append('\n');
        return sb.toString();
    }

    public static JSONArray toJSONArray(String string) throws JSONException {
        return toJSONArray(string, ',');
    }

    public static JSONArray toJSONArray(String string, char delimiter) throws JSONException {
        return toJSONArray(new JSONTokener(string), delimiter);
    }

    public static JSONArray toJSONArray(JSONTokener x) throws JSONException {
        return toJSONArray(x, ',');
    }

    public static JSONArray toJSONArray(JSONTokener x, char delimiter) throws JSONException {
        return toJSONArray(rowToJSONArray(x, delimiter), x, delimiter);
    }

    public static JSONArray toJSONArray(JSONArray names, String string) throws JSONException {
        return toJSONArray(names, string, ',');
    }

    public static JSONArray toJSONArray(JSONArray names, String string, char delimiter) throws JSONException {
        return toJSONArray(names, new JSONTokener(string), delimiter);
    }

    public static JSONArray toJSONArray(JSONArray names, JSONTokener x) throws JSONException {
        return toJSONArray(names, x, ',');
    }

    public static JSONArray toJSONArray(JSONArray names, JSONTokener x, char delimiter) throws JSONException {
        if (names == null || names.length() == 0) {
            return null;
        }
        JSONArray ja = new JSONArray();
        while (true) {
            JSONObject jo = rowToJSONObject(names, x, delimiter);
            if (jo == null) {
                break;
            }
            ja.put(jo);
        }
        if (ja.length() == 0) {
            return null;
        }
        return ja;
    }

    public static String toString(JSONArray ja) throws JSONException {
        return toString(ja, ',');
    }

    public static String toString(JSONArray ja, char delimiter) throws JSONException {
        JSONArray names;
        JSONObject jo = ja.optJSONObject(0);
        if (jo != null && (names = jo.names()) != null) {
            return rowToString(names, delimiter) + toString(names, ja, delimiter);
        }
        return null;
    }

    public static String toString(JSONArray names, JSONArray ja) throws JSONException {
        return toString(names, ja, ',');
    }

    public static String toString(JSONArray names, JSONArray ja, char delimiter) throws JSONException {
        if (names == null || names.length() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ja.length(); i++) {
            JSONObject jo = ja.optJSONObject(i);
            if (jo != null) {
                sb.append(rowToString(jo.toJSONArray(names), delimiter));
            }
        }
        return sb.toString();
    }
}
