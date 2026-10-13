package es.gob.afirma.standalone.configurator.common.xmlwise;

import java.io.Closeable;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/common/xmlwise/Plist.class */
public final class Plist {
    private static final Plist PLIST = new Plist();
    private static final String BASE64_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final char[] BASE64_CHARS = BASE64_STRING.toCharArray();
    private static DateFormat m_dateFormat;
    private static Map<Class<?>, ElementType> m_simpleTypes;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/common/xmlwise/Plist$ElementType.class */
    private enum ElementType {
        INTEGER,
        STRING,
        REAL,
        DATA,
        DATE,
        DICT,
        ARRAY,
        TRUE,
        FALSE
    }

    static {
        m_dateFormat = null;
        m_simpleTypes = null;
        m_dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        m_dateFormat.setTimeZone(TimeZone.getTimeZone("Z"));
        m_simpleTypes = new HashMap();
        m_simpleTypes.put(Integer.class, ElementType.INTEGER);
        m_simpleTypes.put(Byte.class, ElementType.INTEGER);
        m_simpleTypes.put(Short.class, ElementType.INTEGER);
        m_simpleTypes.put(Short.class, ElementType.INTEGER);
        m_simpleTypes.put(Long.class, ElementType.INTEGER);
        m_simpleTypes.put(String.class, ElementType.STRING);
        m_simpleTypes.put(Float.class, ElementType.REAL);
        m_simpleTypes.put(Double.class, ElementType.REAL);
        m_simpleTypes.put(byte[].class, ElementType.DATA);
        m_simpleTypes.put(Boolean.class, ElementType.TRUE);
        m_simpleTypes.put(Date.class, ElementType.DATE);
    }

    static void silentlyClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    public static Map<String, Object> fromXml(String str) throws XmlParseException {
        return PLIST.parse(Xmlwise.createXml(str));
    }

    public static Map<String, Object> fromXml(byte[] bArr) throws XmlParseException {
        return PLIST.parse(Xmlwise.createXml(bArr));
    }

    public static XmlElement objectToXml(Object obj) {
        ElementType elementType = m_simpleTypes.get(obj.getClass());
        if (elementType != null) {
            switch (elementType) {
                case REAL:
                    return new XmlElement("real", obj.toString());
                case INTEGER:
                    return new XmlElement("integer", obj.toString());
                case TRUE:
                    return new XmlElement(((Boolean) obj).booleanValue() ? "true" : "false");
                case DATE:
                    return new XmlElement("date", m_dateFormat.format((Date) obj));
                case STRING:
                    return new XmlElement("string", (String) obj);
                case DATA:
                    return new XmlElement("data", base64encode((byte[]) obj));
            }
        }
        if (obj instanceof Map) {
            return toXmlDict((Map) obj);
        }
        if (obj instanceof List) {
            return toXmlArray((List) obj);
        }
        throw new RuntimeException("Cannot use " + obj.getClass() + " in plist.");
    }

    private static XmlElement toXmlArray(List<Object> list) {
        XmlElement xmlElement = new XmlElement("array");
        Iterator<Object> it = list.iterator();
        while (it.hasNext()) {
            xmlElement.add(objectToXml(it.next()));
        }
        return xmlElement;
    }

    private static XmlElement toXmlDict(Map<String, Object> map) {
        XmlElement xmlElement = new XmlElement("dict");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            xmlElement.add(new XmlElement("key", entry.getKey()));
            xmlElement.add(objectToXml(entry.getValue()));
        }
        return xmlElement;
    }

    Map<String, Object> parse(XmlElement xmlElement) throws XmlParseException {
        if (!"plist".equalsIgnoreCase(xmlElement.getName())) {
            throw new XmlParseException("Expected plist top element, was: " + xmlElement.getName());
        }
        return (Map) parseElement(xmlElement.get("dict").getFirst());
    }

    private Object parseElement(XmlElement xmlElement) throws XmlParseException {
        try {
            return parseElementRaw(xmlElement);
        } catch (Exception e) {
            throw new XmlParseException("Failed to parse: " + xmlElement.toXml(), e);
        }
    }

    private Object parseElementRaw(XmlElement xmlElement) throws Exception {
        switch (ElementType.valueOf(xmlElement.getName().toUpperCase())) {
            case REAL:
                return Double.valueOf(xmlElement.getValue());
            case INTEGER:
                return parseInt(xmlElement.getValue());
            case TRUE:
                return Boolean.TRUE;
            case DATE:
                return m_dateFormat.parse(xmlElement.getValue());
            case STRING:
                return xmlElement.getValue();
            case DATA:
                return base64decode(xmlElement.getValue());
            case ARRAY:
                return parseArray(xmlElement);
            case FALSE:
                return Boolean.FALSE;
            case DICT:
                return parseDict(xmlElement);
            default:
                throw new RuntimeException("Unexpected type: " + xmlElement.getName());
        }
    }

    private static Number parseInt(String str) {
        Long lValueOf = Long.valueOf(str);
        if (lValueOf.intValue() == lValueOf.longValue()) {
            return Integer.valueOf(lValueOf.intValue());
        }
        return lValueOf;
    }

    private Map<String, Object> parseDict(List<XmlElement> list) throws Exception {
        Iterator<XmlElement> it = list.iterator();
        HashMap map = new HashMap();
        while (it.hasNext()) {
            XmlElement next = it.next();
            if (!"key".equals(next.getName())) {
                throw new Exception("Expected key but was " + next.getName());
            }
            map.put(next.getValue(), parseElementRaw(it.next()));
        }
        return map;
    }

    private List<Object> parseArray(List<XmlElement> list) throws Exception {
        ArrayList arrayList = new ArrayList(list.size());
        Iterator<XmlElement> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(parseElementRaw(it.next()));
        }
        return arrayList;
    }

    static String base64encode(byte[] bArr) {
        StringBuilder sb = new StringBuilder(((bArr.length + 2) / 3) * 4);
        int i = 0;
        while (i < bArr.length) {
            byte b = bArr[i];
            byte b2 = i < bArr.length - 1 ? bArr[i + 1] : (byte) 0;
            byte b3 = i < bArr.length - 2 ? bArr[i + 2] : (byte) 0;
            sb.append(BASE64_CHARS[(b & 255) >> 2]);
            sb.append(BASE64_CHARS[((b & 3) << 4) | ((b2 & 240) >> 4)]);
            sb.append(i < bArr.length - 1 ? Character.valueOf(BASE64_CHARS[((b2 & 15) << 2) | ((b3 & 192) >> 6)]) : "=");
            sb.append(i < bArr.length - 2 ? Character.valueOf(BASE64_CHARS[b3 & 63]) : "=");
            i += 3;
        }
        return sb.toString();
    }

    static byte[] base64decode(String str) {
        String strTrim = str.trim();
        int length = ((strTrim.length() / 4) * 3) - (strTrim.endsWith("==") ? 2 : strTrim.endsWith("=") ? 1 : 0);
        String strReplace = strTrim.replace('=', 'A');
        byte[] bArr = new byte[length];
        int length2 = strReplace.length();
        int i = 0;
        for (int i2 = 0; i2 < length2; i2 += 4) {
            int iIndexOf = BASE64_STRING.indexOf(strReplace.charAt(i2));
            int iIndexOf2 = BASE64_STRING.indexOf(strReplace.charAt(i2 + 1));
            int iIndexOf3 = BASE64_STRING.indexOf(strReplace.charAt(i2 + 2));
            int iIndexOf4 = BASE64_STRING.indexOf(strReplace.charAt(i2 + 3));
            byte b = (byte) ((iIndexOf << 2) | (iIndexOf2 >> 4));
            byte b2 = (byte) ((iIndexOf2 << 4) | (iIndexOf3 >> 2));
            byte b3 = (byte) ((iIndexOf3 << 6) | iIndexOf4);
            int i3 = i;
            i++;
            bArr[i3] = b;
            if (i < length) {
                i++;
                bArr[i] = b2;
                if (i < length) {
                    i++;
                    bArr[i] = b3;
                }
            }
        }
        return bArr;
    }
}
