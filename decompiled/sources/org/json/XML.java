package org.json;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:org/json/XML.class */
public class XML {
    public static final Character AMP = '&';
    public static final Character APOS = '\'';
    public static final Character BANG = '!';
    public static final Character EQ = '=';
    public static final Character GT = '>';
    public static final Character LT = '<';
    public static final Character QUEST = '?';
    public static final Character QUOT = '\"';
    public static final Character SLASH = '/';
    public static final String NULL_ATTR = "xsi:nil";
    public static final String TYPE_ATTR = "xsi:type";

    private static Iterable<Integer> codePointIterator(final String string) {
        return new Iterable<Integer>() { // from class: org.json.XML.1
            @Override // java.lang.Iterable
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() { // from class: org.json.XML.1.1
                    private int nextIndex = 0;
                    private int length;

                    {
                        this.length = string.length();
                    }

                    @Override // java.util.Iterator
                    public boolean hasNext() {
                        return this.nextIndex < this.length;
                    }

                    /* JADX WARN: Can't rename method to resolve collision */
                    @Override // java.util.Iterator
                    public Integer next() {
                        int result = string.codePointAt(this.nextIndex);
                        this.nextIndex += Character.charCount(result);
                        return Integer.valueOf(result);
                    }

                    @Override // java.util.Iterator
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static String escape(String string) {
        StringBuilder sb = new StringBuilder(string.length());
        Iterator<Integer> it = codePointIterator(string).iterator();
        while (it.hasNext()) {
            int cp = it.next().intValue();
            switch (cp) {
                case 34:
                    sb.append("&quot;");
                    break;
                case 38:
                    sb.append("&amp;");
                    break;
                case 39:
                    sb.append("&apos;");
                    break;
                case 60:
                    sb.append("&lt;");
                    break;
                case 62:
                    sb.append("&gt;");
                    break;
                default:
                    if (mustEscape(cp)) {
                        sb.append("&#x");
                        sb.append(Integer.toHexString(cp));
                        sb.append(';');
                    } else {
                        sb.appendCodePoint(cp);
                    }
                    break;
            }
        }
        return sb.toString();
    }

    private static boolean mustEscape(int cp) {
        return !(!Character.isISOControl(cp) || cp == 9 || cp == 10 || cp == 13) || ((cp < 32 || cp > 55295) && ((cp < 57344 || cp > 65533) && (cp < 65536 || cp > 1114111)));
    }

    public static String unescape(String string) {
        StringBuilder sb = new StringBuilder(string.length());
        int i = 0;
        int length = string.length();
        while (i < length) {
            char c = string.charAt(i);
            if (c == '&') {
                int semic = string.indexOf(59, i);
                if (semic > i) {
                    String entity = string.substring(i + 1, semic);
                    sb.append(XMLTokener.unescapeEntity(entity));
                    i += entity.length() + 1;
                } else {
                    sb.append(c);
                }
            } else {
                sb.append(c);
            }
            i++;
        }
        return sb.toString();
    }

    public static void noSpace(String string) throws JSONException {
        int length = string.length();
        if (length == 0) {
            throw new JSONException("Empty string.");
        }
        for (int i = 0; i < length; i++) {
            if (Character.isWhitespace(string.charAt(i))) {
                throw new JSONException(OperatorName.SHOW_TEXT_LINE + string + "' contains a space character.");
            }
        }
    }

    private static boolean parse(XMLTokener x, JSONObject jSONObject, String name, XMLParserConfiguration config, int currentNestingDepth) throws JSONException {
        Object token = x.nextToken();
        if (token == BANG) {
            char c = x.next();
            if (c == '-') {
                if (x.next() == '-') {
                    x.skipPast("-->");
                    return false;
                }
                x.back();
            } else if (c == '[') {
                if ("CDATA".equals(x.nextToken()) && x.next() == '[') {
                    String string = x.nextCDATA();
                    if (string.length() > 0) {
                        jSONObject.accumulate(config.getcDataTagName(), string);
                        return false;
                    }
                    return false;
                }
                throw x.syntaxError("Expected 'CDATA['");
            }
            int i = 1;
            do {
                Object token2 = x.nextMeta();
                if (token2 == null) {
                    throw x.syntaxError("Missing '>' after '<!'.");
                }
                if (token2 == LT) {
                    i++;
                } else if (token2 == GT) {
                    i--;
                }
            } while (i > 0);
            return false;
        }
        if (token == QUEST) {
            x.skipPast("?>");
            return false;
        }
        if (token == SLASH) {
            Object token3 = x.nextToken();
            if (name == null) {
                throw x.syntaxError("Mismatched close tag " + token3);
            }
            if (!token3.equals(name)) {
                throw x.syntaxError("Mismatched " + name + " and " + token3);
            }
            if (x.nextToken() != GT) {
                throw x.syntaxError("Misshaped close tag");
            }
            return true;
        }
        if (token instanceof Character) {
            throw x.syntaxError("Misshaped tag");
        }
        String tagName = (String) token;
        Object token4 = null;
        JSONObject jsonObject = new JSONObject();
        boolean nilAttributeFound = false;
        XMLXsiTypeConverter<?> xmlXsiTypeConverter = null;
        while (true) {
            if (token4 == null) {
                token4 = x.nextToken();
            }
            if (token4 instanceof String) {
                String string2 = (String) token4;
                token4 = x.nextToken();
                if (token4 == EQ) {
                    Object token5 = x.nextToken();
                    if (!(token5 instanceof String)) {
                        throw x.syntaxError("Missing value");
                    }
                    if (config.isConvertNilAttributeToNull() && NULL_ATTR.equals(string2) && Boolean.parseBoolean((String) token5)) {
                        nilAttributeFound = true;
                    } else if (config.getXsiTypeMap() != null && !config.getXsiTypeMap().isEmpty() && TYPE_ATTR.equals(string2)) {
                        xmlXsiTypeConverter = config.getXsiTypeMap().get(token5);
                    } else if (!nilAttributeFound) {
                        jsonObject.accumulate(string2, config.isKeepStrings() ? (String) token5 : stringToValue((String) token5));
                    }
                    token4 = null;
                } else {
                    jsonObject.accumulate(string2, "");
                }
            } else {
                if (token4 == SLASH) {
                    if (x.nextToken() != GT) {
                        throw x.syntaxError("Misshaped tag");
                    }
                    if (config.getForceList().contains(tagName)) {
                        if (nilAttributeFound) {
                            jSONObject.append(tagName, JSONObject.NULL);
                            return false;
                        }
                        if (jsonObject.length() > 0) {
                            jSONObject.append(tagName, jsonObject);
                            return false;
                        }
                        jSONObject.put(tagName, new JSONArray());
                        return false;
                    }
                    if (nilAttributeFound) {
                        jSONObject.accumulate(tagName, JSONObject.NULL);
                        return false;
                    }
                    if (jsonObject.length() > 0) {
                        jSONObject.accumulate(tagName, jsonObject);
                        return false;
                    }
                    jSONObject.accumulate(tagName, "");
                    return false;
                }
                if (token4 != GT) {
                    throw x.syntaxError("Misshaped tag");
                }
                while (true) {
                    Object token6 = x.nextContent();
                    if (token6 == null) {
                        if (tagName != null) {
                            throw x.syntaxError("Unclosed tag " + tagName);
                        }
                        return false;
                    }
                    if (token6 instanceof String) {
                        String string3 = (String) token6;
                        if (string3.length() > 0) {
                            if (xmlXsiTypeConverter != null) {
                                jsonObject.accumulate(config.getcDataTagName(), stringToValue(string3, xmlXsiTypeConverter));
                            } else {
                                jsonObject.accumulate(config.getcDataTagName(), config.isKeepStrings() ? string3 : stringToValue(string3));
                            }
                        }
                    } else if (token6 != LT) {
                        continue;
                    } else {
                        if (currentNestingDepth == config.getMaxNestingDepth()) {
                            throw x.syntaxError("Maximum nesting depth of " + config.getMaxNestingDepth() + " reached");
                        }
                        if (parse(x, jsonObject, tagName, config, currentNestingDepth + 1)) {
                            if (config.getForceList().contains(tagName)) {
                                if (jsonObject.length() == 0) {
                                    jSONObject.put(tagName, new JSONArray());
                                    return false;
                                }
                                if (jsonObject.length() == 1 && jsonObject.opt(config.getcDataTagName()) != null) {
                                    jSONObject.append(tagName, jsonObject.opt(config.getcDataTagName()));
                                    return false;
                                }
                                jSONObject.append(tagName, jsonObject);
                                return false;
                            }
                            if (jsonObject.length() == 0) {
                                jSONObject.accumulate(tagName, "");
                                return false;
                            }
                            if (jsonObject.length() == 1 && jsonObject.opt(config.getcDataTagName()) != null) {
                                jSONObject.accumulate(tagName, jsonObject.opt(config.getcDataTagName()));
                                return false;
                            }
                            if (!config.shouldTrimWhiteSpace()) {
                                removeEmpty(jsonObject, config);
                            }
                            jSONObject.accumulate(tagName, jsonObject);
                            return false;
                        }
                    }
                }
            }
        }
    }

    private static void removeEmpty(JSONObject jsonObject, XMLParserConfiguration config) {
        if (jsonObject.has(config.getcDataTagName())) {
            Object s = jsonObject.get(config.getcDataTagName());
            if (s instanceof String) {
                if (isStringAllWhiteSpace(s.toString())) {
                    jsonObject.remove(config.getcDataTagName());
                }
            } else if (s instanceof JSONArray) {
                JSONArray sArray = (JSONArray) s;
                for (int k = sArray.length() - 1; k >= 0; k--) {
                    Object eachString = sArray.get(k);
                    if (eachString instanceof String) {
                        String s1 = (String) eachString;
                        if (isStringAllWhiteSpace(s1)) {
                            sArray.remove(k);
                        }
                    }
                }
                if (sArray.isEmpty()) {
                    jsonObject.remove(config.getcDataTagName());
                }
            }
        }
    }

    private static boolean isStringAllWhiteSpace(String s) {
        for (int k = 0; k < s.length(); k++) {
            char eachChar = s.charAt(k);
            if (!Character.isWhitespace(eachChar)) {
                return false;
            }
        }
        return true;
    }

    private static Number stringToNumber(String val) throws NumberFormatException {
        char initial = val.charAt(0);
        if ((initial >= '0' && initial <= '9') || initial == '-') {
            if (isDecimalNotation(val)) {
                try {
                    BigDecimal bd = new BigDecimal(val);
                    if (initial == '-' && BigDecimal.ZERO.compareTo(bd) == 0) {
                        return Double.valueOf(-0.0d);
                    }
                    return bd;
                } catch (NumberFormatException e) {
                    try {
                        Double d = Double.valueOf(val);
                        if (d.isNaN() || d.isInfinite()) {
                            throw new NumberFormatException("val [" + val + "] is not a valid number.");
                        }
                        return d;
                    } catch (NumberFormatException e2) {
                        throw new NumberFormatException("val [" + val + "] is not a valid number.");
                    }
                }
            }
            if (initial == '0' && val.length() > 1) {
                char at1 = val.charAt(1);
                if (at1 >= '0' && at1 <= '9') {
                    throw new NumberFormatException("val [" + val + "] is not a valid number.");
                }
            } else if (initial == '-' && val.length() > 2) {
                char at2 = val.charAt(1);
                char at3 = val.charAt(2);
                if (at2 == '0' && at3 >= '0' && at3 <= '9') {
                    throw new NumberFormatException("val [" + val + "] is not a valid number.");
                }
            }
            BigInteger bi = new BigInteger(val);
            if (bi.bitLength() <= 31) {
                return Integer.valueOf(bi.intValue());
            }
            if (bi.bitLength() <= 63) {
                return Long.valueOf(bi.longValue());
            }
            return bi;
        }
        throw new NumberFormatException("val [" + val + "] is not a valid number.");
    }

    private static boolean isDecimalNotation(String val) {
        return val.indexOf(46) > -1 || val.indexOf(101) > -1 || val.indexOf(69) > -1 || "-0".equals(val);
    }

    public static Object stringToValue(String string, XMLXsiTypeConverter<?> typeConverter) {
        if (typeConverter != null) {
            return typeConverter.convert(string);
        }
        return stringToValue(string);
    }

    public static Object stringToValue(String string) {
        if ("".equals(string)) {
            return string;
        }
        if ("true".equalsIgnoreCase(string)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(string)) {
            return Boolean.FALSE;
        }
        if ("null".equalsIgnoreCase(string)) {
            return JSONObject.NULL;
        }
        char initial = string.charAt(0);
        if ((initial >= '0' && initial <= '9') || initial == '-') {
            try {
                return stringToNumber(string);
            } catch (Exception e) {
            }
        }
        return string;
    }

    public static JSONObject toJSONObject(String string) throws JSONException {
        return toJSONObject(string, XMLParserConfiguration.ORIGINAL);
    }

    public static JSONObject toJSONObject(Reader reader) throws JSONException {
        return toJSONObject(reader, XMLParserConfiguration.ORIGINAL);
    }

    public static JSONObject toJSONObject(Reader reader, boolean keepStrings) throws JSONException {
        if (keepStrings) {
            return toJSONObject(reader, XMLParserConfiguration.KEEP_STRINGS);
        }
        return toJSONObject(reader, XMLParserConfiguration.ORIGINAL);
    }

    public static JSONObject toJSONObject(Reader reader, XMLParserConfiguration config) throws JSONException {
        JSONObject jo = new JSONObject();
        XMLTokener x = new XMLTokener(reader, config);
        while (x.more()) {
            x.skipPast("<");
            if (x.more()) {
                parse(x, jo, null, config, 0);
            }
        }
        return jo;
    }

    public static JSONObject toJSONObject(String string, boolean keepStrings) throws JSONException {
        return toJSONObject(new StringReader(string), keepStrings);
    }

    public static JSONObject toJSONObject(String string, XMLParserConfiguration config) throws JSONException {
        return toJSONObject(new StringReader(string), config);
    }

    public static String toString(Object object) throws JSONException {
        return toString(object, (String) null, XMLParserConfiguration.ORIGINAL);
    }

    public static String toString(Object object, String tagName) {
        return toString(object, tagName, XMLParserConfiguration.ORIGINAL);
    }

    public static String toString(Object object, String tagName, XMLParserConfiguration config) throws JSONException {
        return toString(object, tagName, config, 0, 0);
    }

    private static String toString(Object object, String tagName, XMLParserConfiguration config, int indentFactor, int indent) throws JSONException {
        JSONArray ja;
        StringBuilder sb = new StringBuilder();
        if (object instanceof JSONObject) {
            if (tagName != null) {
                sb.append(indent(indent));
                sb.append('<');
                sb.append(tagName);
                sb.append('>');
                if (indentFactor > 0) {
                    sb.append("\n");
                    indent += indentFactor;
                }
            }
            JSONObject jo = (JSONObject) object;
            for (String key : jo.keySet()) {
                Object value = jo.opt(key);
                if (value == null) {
                    value = "";
                } else if (value.getClass().isArray()) {
                    value = new JSONArray(value);
                }
                if (key.equals(config.getcDataTagName())) {
                    if (value instanceof JSONArray) {
                        JSONArray ja2 = (JSONArray) value;
                        int jaLength = ja2.length();
                        for (int i = 0; i < jaLength; i++) {
                            if (i > 0) {
                                sb.append('\n');
                            }
                            Object val = ja2.opt(i);
                            sb.append(escape(val.toString()));
                        }
                    } else {
                        sb.append(escape(value.toString()));
                    }
                } else if (value instanceof JSONArray) {
                    JSONArray ja3 = (JSONArray) value;
                    int jaLength2 = ja3.length();
                    for (int i2 = 0; i2 < jaLength2; i2++) {
                        Object val2 = ja3.opt(i2);
                        if (val2 instanceof JSONArray) {
                            sb.append('<');
                            sb.append(key);
                            sb.append('>');
                            sb.append(toString(val2, null, config, indentFactor, indent));
                            sb.append("</");
                            sb.append(key);
                            sb.append('>');
                        } else {
                            sb.append(toString(val2, key, config, indentFactor, indent));
                        }
                    }
                } else if ("".equals(value)) {
                    if (config.isCloseEmptyTag()) {
                        sb.append(indent(indent));
                        sb.append('<');
                        sb.append(key);
                        sb.append(">");
                        sb.append("</");
                        sb.append(key);
                        sb.append(">");
                        if (indentFactor > 0) {
                            sb.append("\n");
                        }
                    } else {
                        sb.append(indent(indent));
                        sb.append('<');
                        sb.append(key);
                        sb.append("/>");
                        if (indentFactor > 0) {
                            sb.append("\n");
                        }
                    }
                } else {
                    sb.append(toString(value, key, config, indentFactor, indent));
                }
            }
            if (tagName != null) {
                sb.append(indent(indent - indentFactor));
                sb.append("</");
                sb.append(tagName);
                sb.append('>');
                if (indentFactor > 0) {
                    sb.append("\n");
                }
            }
            return sb.toString();
        }
        if (object != null && ((object instanceof JSONArray) || object.getClass().isArray())) {
            if (object.getClass().isArray()) {
                ja = new JSONArray(object);
            } else {
                ja = (JSONArray) object;
            }
            int jaLength3 = ja.length();
            for (int i3 = 0; i3 < jaLength3; i3++) {
                Object val3 = ja.opt(i3);
                sb.append(toString(val3, tagName == null ? "array" : tagName, config, indentFactor, indent));
            }
            return sb.toString();
        }
        String string = object == null ? "null" : escape(object.toString());
        String indentationSuffix = indentFactor > 0 ? "\n" : "";
        if (tagName == null) {
            return indent(indent) + OperatorName.SHOW_TEXT_LINE_AND_SPACE + string + OperatorName.SHOW_TEXT_LINE_AND_SPACE + indentationSuffix;
        }
        if (string.length() == 0) {
            return indent(indent) + "<" + tagName + "/>" + indentationSuffix;
        }
        return indent(indent) + "<" + tagName + ">" + string + "</" + tagName + ">" + indentationSuffix;
    }

    public static String toString(Object object, int indentFactor) {
        return toString(object, null, XMLParserConfiguration.ORIGINAL, indentFactor);
    }

    public static String toString(Object object, String tagName, int indentFactor) {
        return toString(object, tagName, XMLParserConfiguration.ORIGINAL, indentFactor);
    }

    public static String toString(Object object, String tagName, XMLParserConfiguration config, int indentFactor) throws JSONException {
        return toString(object, tagName, config, indentFactor, 0);
    }

    private static final String indent(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }
}
