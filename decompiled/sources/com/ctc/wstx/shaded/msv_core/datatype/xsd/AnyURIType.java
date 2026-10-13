package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExp;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExpFactory;
import java.text.ParseException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/AnyURIType.class */
public class AnyURIType extends BuiltinAtomicType implements Discrete {
    public static final AnyURIType theInstance = new AnyURIType();
    private static final boolean[] isUric = createUricMap();
    static final RegExp regexp = createRegExp();
    private static final long serialVersionUID = 1;

    private AnyURIType() {
        super("anyURI");
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected boolean checkFormat(String content, ValidationContext context) {
        return regexp.matches(escape(content));
    }

    private static void appendHex(StringBuffer buf, int hex) {
        if (hex < 10) {
            buf.append((char) (hex + 48));
        } else {
            buf.append((char) ((hex - 10) + 65));
        }
    }

    private static void appendByte(StringBuffer buf, int ch) {
        buf.append('%');
        appendHex(buf, ch / 16);
        appendHex(buf, ch % 16);
    }

    private static void appendEscaped(StringBuffer buf, char ch) {
        if (ch < 127) {
            appendByte(buf, ch);
            return;
        }
        if (ch < 2047) {
            appendByte(buf, 192 + (ch >> 6));
            appendByte(buf, 128 + (ch % '@'));
        } else if (ch < 65535) {
            appendByte(buf, 224 + (ch >> '\f'));
            appendByte(buf, 128 + ((ch >> 6) % 64));
            appendByte(buf, 128 + (ch % '@'));
        }
    }

    private static void appendEscaped(StringBuffer buf, char ch1, char ch2) {
        int ucs = ((ch1 & 1023) << 10) + (ch2 & 1023);
        appendByte(buf, 240 + (ucs >> 18));
        appendByte(buf, 128 + ((ucs >> 12) % 64));
        appendByte(buf, 128 + ((ucs >> 6) % 64));
        appendByte(buf, 128 + (ucs % 64));
    }

    private static boolean[] createUricMap() {
        boolean[] r = new boolean[128];
        for (int i = 97; i <= 122; i++) {
            r[i] = true;
        }
        for (int i2 = 65; i2 <= 90; i2++) {
            r[i2] = true;
        }
        for (int i3 = 48; i3 <= 57; i3++) {
            r[i3] = true;
        }
        char[] mark = {'-', '_', '.', '!', '~', '*', '\'', '(', ')', '#', '%', '[', ']'};
        for (char c : mark) {
            r[c] = true;
        }
        char[] reserved = {';', '/', '?', ':', '@', '&', '=', '+', '$', ','};
        for (char c2 : reserved) {
            r[c2] = true;
        }
        return r;
    }

    public static String escape(String content) {
        StringBuffer escaped = new StringBuffer(content.length());
        int i = 0;
        while (i < content.length()) {
            char ch = content.charAt(i);
            if (ch < 128 && isUric[ch]) {
                escaped.append(ch);
            } else if (55296 <= ch && ch < 56320) {
                i++;
                appendEscaped(escaped, ch, content.charAt(i));
            } else {
                appendEscaped(escaped, ch);
            }
            i++;
        }
        return new String(escaped);
    }

    static RegExp createRegExp() {
        String escaped = "%[0-9a-fA-F]{2}";
        String unreserved = "([0-9a-zA-Z]|[\\-_\\.!~\\*'\\(\\)])";
        String uric = "([;/\\?:@&=\\+$,\\[\\]]|" + unreserved + "|" + escaped + ")";
        String fragment = uric + "*";
        String query = uric + "*";
        String pchar = "(" + unreserved + "|" + escaped + "|[:@&=\\+$,])";
        String param = pchar + "*";
        String segment = "(" + param + "(;" + param + ")*)";
        String pathSegments = "(" + segment + "(/" + segment + ")*)";
        String IPv4address = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";
        String hex4 = "[0-9a-fA-F]{1,4}";
        String hexseq = hex4 + "(:" + hex4 + ")*";
        String hexpart = "((" + hexseq + "(::(" + hexseq + ")?)?)|(::(" + hexseq + ")?))";
        String IPv6address = "((" + hexpart + "(:" + IPv4address + ")?)|(::" + IPv4address + "))";
        String IPv6reference = "\\[" + IPv6address + "\\]";
        String domainlabel = "[0-9a-zA-Z]([0-9A-Za-z\\-]*[0-9a-zA-Z])?";
        String toplabel = "[a-zA-Z]([0-9A-Za-z\\-]*[0-9a-zA-Z])?";
        String hostname = "(" + domainlabel + "\\.)*" + toplabel + "(\\.)?";
        String host = "((" + hostname + ")|(" + IPv4address + ")|(" + IPv6reference + "))";
        String hostport = host + "(:[0-9]*)?";
        String userinfo = "(" + unreserved + "|" + escaped + "|[;:&=\\+$,])*";
        String server = "((" + userinfo + "@)?" + hostport + ")?";
        String regName = "(" + unreserved + "|" + escaped + "|[$,;:@&=\\+])+";
        String authority = "((" + server + ")|(" + regName + "))";
        String scheme = "[a-zA-Z][A-Za-z0-9\\+\\-\\.]*";
        String relSegment = "(" + unreserved + "|" + escaped + "|[;@&=\\+$,])+";
        String absPath = "/" + pathSegments;
        String relPath = relSegment + "(" + absPath + ")?";
        String netPath = "//" + authority + "(" + absPath + ")?";
        String uricNoSlash = "(" + unreserved + "|" + escaped + "|[;\\?:@&=\\+$,])";
        String opaquePart = uricNoSlash + "(" + uric + ")*";
        String hierPart = "((" + netPath + ")|(" + absPath + "))(\\?" + query + ")?";
        String relativeURI = "((" + netPath + ")|(" + absPath + ")|(" + relPath + "))(\\?" + query + ")?";
        String absoluteURI = scheme + ":((" + hierPart + ")|(" + opaquePart + "))";
        String uriRef = "(" + absoluteURI + "|" + relativeURI + ")?(#" + fragment + ")?";
        try {
            return RegExpFactory.createFactory().compile(uriRef);
        } catch (ParseException e) {
            throw new Error();
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String content, ValidationContext context) {
        if (!regexp.matches(escape(content))) {
            return null;
        }
        return content;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public String convertToLexicalValue(Object value, SerializationContext context) {
        if (value instanceof String) {
            return (String) value;
        }
        throw new IllegalArgumentException();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final int isFacetApplicable(String facetName) {
        if (facetName.equals(XSDatatype.FACET_LENGTH) || facetName.equals(XSDatatype.FACET_MINLENGTH) || facetName.equals(XSDatatype.FACET_MAXLENGTH) || facetName.equals(XSDatatype.FACET_PATTERN) || facetName.equals(XSDatatype.FACET_WHITESPACE) || facetName.equals(XSDatatype.FACET_ENUMERATION)) {
            return 0;
        }
        return -2;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.Discrete
    public final int countLength(Object value) {
        return UnicodeUtil.countLength((String) value);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public Class getJavaObjectType() {
        return String.class;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }
}
