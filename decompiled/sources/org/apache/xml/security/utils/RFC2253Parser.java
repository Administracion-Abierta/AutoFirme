package org.apache.xml.security.utils;

import es.gob.afirma.keystores.filters.PolicyIdFilter;
import java.io.IOException;
import java.io.StringReader;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/utils/RFC2253Parser.class */
public class RFC2253Parser {
    public static String rfc2253toXMLdsig(String dn) {
        String normalized = normalize(dn, true);
        return rfctoXML(normalized);
    }

    public static String xmldsigtoRFC2253(String dn) {
        String normalized = normalize(dn, false);
        return xmltoRFC(normalized);
    }

    public static String normalize(String dn) {
        return normalize(dn, true);
    }

    public static String normalize(String dn, boolean toXml) {
        if (dn == null || dn.isEmpty()) {
            return "";
        }
        try {
            String DN = semicolonToComma(dn);
            StringBuilder sb = new StringBuilder();
            int i = 0;
            int l = 0;
            int j = 0;
            while (true) {
                int k = DN.indexOf(44, j);
                if (k >= 0) {
                    l += countQuotes(DN, j, k);
                    if (k > 0 && DN.charAt(k - 1) != '\\' && l % 2 == 0) {
                        sb.append(parseRDN(DN.substring(i, k).trim(), toXml)).append(',');
                        i = k + 1;
                        l = 0;
                    }
                    j = k + 1;
                } else {
                    sb.append(parseRDN(trim(DN.substring(i)), toXml));
                    return sb.toString();
                }
            }
        } catch (IOException e) {
            return dn;
        }
    }

    static String parseRDN(String str, boolean toXml) throws IOException {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int l = 0;
        int i2 = 0;
        while (true) {
            int j = i2;
            int k = str.indexOf(43, j);
            if (k >= 0) {
                l += countQuotes(str, j, k);
                if (k > 0 && str.charAt(k - 1) != '\\' && l % 2 == 0) {
                    sb.append(parseATAV(trim(str.substring(i, k)), toXml)).append('+');
                    i = k + 1;
                    l = 0;
                }
                i2 = k + 1;
            } else {
                sb.append(parseATAV(trim(str.substring(i)), toXml));
                return sb.toString();
            }
        }
    }

    static String parseATAV(String str, boolean toXml) throws Exception {
        String attrValue;
        int i = str.indexOf(61);
        if (i == -1 || (i > 0 && str.charAt(i - 1) == '\\')) {
            return str;
        }
        String attrType = normalizeAT(str.substring(0, i));
        if (attrType.charAt(0) >= '0' && attrType.charAt(0) <= '9') {
            attrValue = str.substring(i + 1);
        } else {
            attrValue = normalizeV(str.substring(i + 1), toXml);
        }
        return attrType + "=" + attrValue;
    }

    static String normalizeAT(String str) {
        String at = str.toUpperCase().trim();
        if (at.startsWith("OID")) {
            at = at.substring(3);
        }
        return at;
    }

    static String normalizeV(String str, boolean toXml) throws Exception {
        String value = trim(str);
        if (value.startsWith(OperatorName.SHOW_TEXT_LINE_AND_SPACE)) {
            StringBuilder sb = new StringBuilder();
            StringReader sr = new StringReader(value.substring(1, value.length() - 1));
            Throwable th = null;
            while (true) {
                try {
                    try {
                        int i = sr.read();
                        if (i <= -1) {
                            break;
                        }
                        char c = (char) i;
                        if (c == ',' || c == '=' || c == '+' || c == '<' || c == '>' || c == '#' || c == ';') {
                            sb.append('\\');
                        }
                        sb.append(c);
                    } catch (Throwable th2) {
                        th = th2;
                        throw th2;
                    }
                } catch (Throwable th3) {
                    $closeResource(th, sr);
                    throw th3;
                }
            }
            $closeResource(null, sr);
            value = trim(sb.toString());
        }
        if (toXml) {
            if (value.length() > 0 && value.charAt(0) == '#') {
                value = '\\' + value;
            }
        } else if (value.startsWith("\\#")) {
            value = value.substring(1);
        }
        return value;
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) throws Exception {
        if (x0 == null) {
            x1.close();
            return;
        }
        try {
            x1.close();
        } catch (Throwable th) {
            x0.addSuppressed(th);
        }
    }

    static String rfctoXML(String string) {
        try {
            String s = changeLess32toXML(string);
            return changeWStoXML(s);
        } catch (Exception e) {
            return string;
        }
    }

    static String xmltoRFC(String string) {
        try {
            String s = changeLess32toRFC(string);
            return changeWStoRFC(s);
        } catch (Exception e) {
            return string;
        }
    }

    static String changeLess32toRFC(String string) throws Exception {
        StringBuilder sb = new StringBuilder();
        StringReader sr = new StringReader(string);
        Throwable th = null;
        while (true) {
            try {
                try {
                    int i = sr.read();
                    if (i > -1) {
                        char c = (char) i;
                        if (c == '\\') {
                            sb.append(c);
                            char c1 = (char) sr.read();
                            char c2 = (char) sr.read();
                            if (((c1 >= '0' && c1 <= '9') || ((c1 >= 'A' && c1 <= 'F') || (c1 >= 'a' && c1 <= 'f'))) && ((c2 >= '0' && c2 <= '9') || ((c2 >= 'A' && c2 <= 'F') || (c2 >= 'a' && c2 <= 'f')))) {
                                try {
                                    char ch = (char) Byte.parseByte("" + c1 + c2, 16);
                                    sb.append(ch);
                                } catch (NumberFormatException ex) {
                                    throw new IOException(ex);
                                }
                            } else {
                                sb.append(c1);
                                sb.append(c2);
                            }
                        } else {
                            sb.append(c);
                        }
                    } else {
                        $closeResource(null, sr);
                        return sb.toString();
                    }
                } catch (Throwable th2) {
                    th = th2;
                    throw th2;
                }
            } catch (Throwable th3) {
                $closeResource(th, sr);
                throw th3;
            }
            $closeResource(th, sr);
            throw th3;
        }
    }

    static String changeLess32toXML(String string) throws Exception {
        StringBuilder sb = new StringBuilder();
        StringReader sr = new StringReader(string);
        Throwable th = null;
        while (true) {
            try {
                try {
                    int i = sr.read();
                    if (i > -1) {
                        if (i < 32) {
                            sb.append('\\');
                            sb.append(Integer.toHexString(i));
                        } else {
                            sb.append((char) i);
                        }
                    } else {
                        $closeResource(null, sr);
                        return sb.toString();
                    }
                } catch (Throwable th2) {
                    th = th2;
                    throw th2;
                }
            } catch (Throwable th3) {
                $closeResource(th, sr);
                throw th3;
            }
        }
    }

    static String changeWStoXML(String string) throws Exception {
        StringBuilder sb = new StringBuilder();
        StringReader sr = new StringReader(string);
        Throwable th = null;
        while (true) {
            try {
                try {
                    int i = sr.read();
                    if (i > -1) {
                        char c = (char) i;
                        if (c == '\\') {
                            char c1 = (char) sr.read();
                            if (c1 == ' ') {
                                sb.append('\\');
                                sb.append("20");
                            } else {
                                sb.append('\\');
                                sb.append(c1);
                            }
                        } else {
                            sb.append(c);
                        }
                    } else {
                        $closeResource(null, sr);
                        return sb.toString();
                    }
                } catch (Throwable th2) {
                    th = th2;
                    throw th2;
                }
            } catch (Throwable th3) {
                $closeResource(th, sr);
                throw th3;
            }
        }
    }

    static String changeWStoRFC(String string) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int i2 = 0;
        while (true) {
            int j = i2;
            int k = string.indexOf("\\20", j);
            if (k >= 0) {
                sb.append(trim(string.substring(i, k))).append("\\ ");
                i = k + 3;
                i2 = k + 3;
            } else {
                sb.append(string.substring(i));
                return sb.toString();
            }
        }
    }

    static String semicolonToComma(String str) {
        return removeWSandReplace(str, PolicyIdFilter.OID_SEPARATOR, ",");
    }

    static String removeWhiteSpace(String str, String symbol) {
        return removeWSandReplace(str, symbol, symbol);
    }

    static String removeWSandReplace(String str, String symbol, String replace) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int l = 0;
        int i2 = 0;
        while (true) {
            int j = i2;
            int k = str.indexOf(symbol, j);
            if (k >= 0) {
                l += countQuotes(str, j, k);
                if (k > 0 && str.charAt(k - 1) != '\\' && l % 2 == 0) {
                    sb.append(trim(str.substring(i, k))).append(replace);
                    i = k + 1;
                    l = 0;
                }
                i2 = k + 1;
            } else {
                sb.append(trim(str.substring(i)));
                return sb.toString();
            }
        }
    }

    private static int countQuotes(String s, int i, int j) {
        int k = 0;
        for (int l = i; l < j; l++) {
            if (s.charAt(l) == '\"') {
                k++;
            }
        }
        return k;
    }

    static String trim(String str) {
        String trimmed = str.trim();
        int i = str.indexOf(trimmed) + trimmed.length();
        if (str.length() > i && trimmed.endsWith("\\") && !trimmed.endsWith("\\\\") && str.charAt(i) == ' ') {
            trimmed = trimmed + " ";
        }
        return trimmed;
    }
}
