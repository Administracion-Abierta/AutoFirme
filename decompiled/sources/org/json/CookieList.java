package org.json;

/* JADX INFO: loaded from: autofirma.jar:org/json/CookieList.class */
public class CookieList {
    public static JSONObject toJSONObject(String string) throws JSONException {
        JSONObject jo = new JSONObject();
        JSONTokener x = new JSONTokener(string);
        while (x.more()) {
            String name = Cookie.unescape(x.nextTo('='));
            x.next('=');
            jo.put(name, Cookie.unescape(x.nextTo(';')));
            x.next();
        }
        return jo;
    }

    public static String toString(JSONObject jo) throws JSONException {
        boolean isEndOfPair = false;
        StringBuilder sb = new StringBuilder();
        for (String key : jo.keySet()) {
            Object value = jo.opt(key);
            if (!JSONObject.NULL.equals(value)) {
                if (isEndOfPair) {
                    sb.append(';');
                }
                sb.append(Cookie.escape(key));
                sb.append("=");
                sb.append(Cookie.escape(value.toString()));
                isEndOfPair = true;
            }
        }
        return sb.toString();
    }
}
