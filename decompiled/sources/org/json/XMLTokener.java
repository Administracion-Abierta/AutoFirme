package org.json;

import java.io.Reader;
import java.util.HashMap;

/* JADX INFO: loaded from: autofirma.jar:org/json/XMLTokener.class */
public class XMLTokener extends JSONTokener {
    public static final HashMap<String, Character> entity = new HashMap<>(8);
    private XMLParserConfiguration configuration;

    static {
        entity.put("amp", XML.AMP);
        entity.put("apos", XML.APOS);
        entity.put("gt", XML.GT);
        entity.put("lt", XML.LT);
        entity.put("quot", XML.QUOT);
    }

    public XMLTokener(Reader r) {
        super(r);
        this.configuration = XMLParserConfiguration.ORIGINAL;
    }

    public XMLTokener(String s) {
        super(s);
        this.configuration = XMLParserConfiguration.ORIGINAL;
    }

    public XMLTokener(Reader r, XMLParserConfiguration configuration) {
        super(r);
        this.configuration = XMLParserConfiguration.ORIGINAL;
        this.configuration = configuration;
    }

    public String nextCDATA() throws JSONException {
        StringBuilder sb = new StringBuilder();
        while (more()) {
            char c = next();
            sb.append(c);
            int i = sb.length() - 3;
            if (i >= 0 && sb.charAt(i) == ']' && sb.charAt(i + 1) == ']' && sb.charAt(i + 2) == '>') {
                sb.setLength(i);
                return sb.toString();
            }
        }
        throw syntaxError("Unclosed CDATA");
    }

    public Object nextContent() throws JSONException {
        char c;
        do {
            c = next();
            if (!Character.isWhitespace(c)) {
                break;
            }
        } while (this.configuration.shouldTrimWhiteSpace());
        if (c == 0) {
            return null;
        }
        if (c == '<') {
            return XML.LT;
        }
        StringBuilder sb = new StringBuilder();
        while (c != 0) {
            if (c == '<') {
                back();
                if (this.configuration.shouldTrimWhiteSpace()) {
                    return sb.toString().trim();
                }
                return sb.toString();
            }
            if (c == '&') {
                sb.append(nextEntity(c));
            } else {
                sb.append(c);
            }
            c = next();
        }
        return sb.toString().trim();
    }

    public Object nextEntity(char ampersand) throws JSONException {
        char c;
        StringBuilder sb = new StringBuilder();
        while (true) {
            c = next();
            if (!Character.isLetterOrDigit(c) && c != '#') {
                break;
            }
            sb.append(Character.toLowerCase(c));
        }
        if (c != ';') {
            throw syntaxError("Missing ';' in XML entity: &" + ((Object) sb));
        }
        String string = sb.toString();
        return unescapeEntity(string);
    }

    static String unescapeEntity(String e) {
        int cp;
        if (e == null || e.isEmpty()) {
            return "";
        }
        if (e.charAt(0) == '#') {
            if (e.charAt(1) == 'x' || e.charAt(1) == 'X') {
                cp = Integer.parseInt(e.substring(2), 16);
            } else {
                cp = Integer.parseInt(e.substring(1));
            }
            return new String(new int[]{cp}, 0, 1);
        }
        Character knownEntity = entity.get(e);
        if (knownEntity == null) {
            return '&' + e + ';';
        }
        return knownEntity.toString();
    }

    public Object nextMeta() throws JSONException {
        char c;
        char c2;
        do {
            c = next();
        } while (Character.isWhitespace(c));
        switch (c) {
            case 0:
                throw syntaxError("Misshaped meta tag");
            case '!':
                return XML.BANG;
            case '\"':
            case '\'':
                do {
                    c2 = next();
                    if (c2 == 0) {
                        throw syntaxError("Unterminated string");
                    }
                } while (c2 != c);
                return Boolean.TRUE;
            case '/':
                return XML.SLASH;
            case '<':
                return XML.LT;
            case '=':
                return XML.EQ;
            case '>':
                return XML.GT;
            case '?':
                return XML.QUEST;
        }
        while (true) {
            char c3 = next();
            if (Character.isWhitespace(c3)) {
                return Boolean.TRUE;
            }
            switch (c3) {
                case 0:
                    throw syntaxError("Unterminated string");
                case '!':
                case '\"':
                case '\'':
                case '/':
                case '<':
                case '=':
                case '>':
                case '?':
                    back();
                    return Boolean.TRUE;
                default:
                    break;
            }
        }
    }

    public Object nextToken() throws JSONException {
        char c;
        do {
            c = next();
        } while (Character.isWhitespace(c));
        switch (c) {
            case 0:
                throw syntaxError("Misshaped element");
            case '!':
                return XML.BANG;
            case '\"':
            case '\'':
                StringBuilder sb = new StringBuilder();
                while (true) {
                    char c2 = next();
                    if (c2 == 0) {
                        throw syntaxError("Unterminated string");
                    }
                    if (c2 == c) {
                        return sb.toString();
                    }
                    if (c2 == '&') {
                        sb.append(nextEntity(c2));
                    } else {
                        sb.append(c2);
                    }
                }
                break;
            case '/':
                return XML.SLASH;
            case '<':
                throw syntaxError("Misplaced '<'");
            case '=':
                return XML.EQ;
            case '>':
                return XML.GT;
            case '?':
                return XML.QUEST;
            default:
                StringBuilder sb2 = new StringBuilder();
                while (true) {
                    sb2.append(c);
                    c = next();
                    if (Character.isWhitespace(c)) {
                        return sb2.toString();
                    }
                    switch (c) {
                        case 0:
                            return sb2.toString();
                        case '!':
                        case '/':
                        case '=':
                        case '>':
                        case '?':
                        case '[':
                        case ']':
                            back();
                            return sb2.toString();
                        case '\"':
                        case '\'':
                        case '<':
                            throw syntaxError("Bad character in a name");
                        default:
                            break;
                    }
                }
                break;
        }
    }

    public void skipPast(String to) {
        char c;
        int offset = 0;
        int length = to.length();
        char[] circle = new char[length];
        for (int i = 0; i < length; i++) {
            char c2 = next();
            if (c2 == 0) {
                return;
            }
            circle[i] = c2;
        }
        while (true) {
            int j = offset;
            boolean b = true;
            for (int i2 = 0; i2 < length; i2++) {
                if (circle[j] != to.charAt(i2)) {
                    b = false;
                    break;
                }
                j++;
                if (j >= length) {
                    j -= length;
                }
            }
            if (b || (c = next()) == 0) {
                return;
            }
            circle[offset] = c;
            offset++;
            if (offset >= length) {
                offset -= length;
            }
        }
    }
}
