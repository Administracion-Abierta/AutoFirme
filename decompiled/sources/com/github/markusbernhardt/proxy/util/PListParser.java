package com.github.markusbernhardt.proxy.util;

import com.aowagie.text.xml.XMLConstants;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/util/PListParser.class */
public final class PListParser {
    private static final PListParser PLIST = new PListParser();
    private static final String BASE64_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final char[] BASE64_CHARS = BASE64_STRING.toCharArray();
    private final DateFormat m_dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private final Map<Class<?>, ElementType> m_simpleTypes;

    /* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/util/PListParser$ElementType.class */
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

    /* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/util/PListParser$XmlParseException.class */
    public static class XmlParseException extends Exception {
        private static final long serialVersionUID = 1;

        public XmlParseException() {
        }

        public XmlParseException(String msg) {
            super(msg);
        }

        public XmlParseException(String msg, Exception e) {
            super(msg, e);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/util/PListParser$Dict.class */
    public static class Dict implements Iterable<Map.Entry<String, Object>> {
        private Map<String, Object> children = new HashMap();

        public Object get(String key) {
            return this.children.get(key);
        }

        @Override // java.lang.Iterable
        public Iterator<Map.Entry<String, Object>> iterator() {
            return this.children.entrySet().iterator();
        }

        public int size() {
            return this.children.size();
        }

        public void dump() {
            System.out.println("PList");
            dumpInternal(this, 1);
        }

        private static void dumpInternal(Dict plist, int indent) {
            for (Map.Entry<String, Object> child : plist) {
                if (child.getValue() instanceof Dict) {
                    for (int j = 0; j < indent; j++) {
                        System.out.print("  ");
                    }
                    System.out.println(child.getKey());
                    dumpInternal((Dict) child.getValue(), indent + 1);
                } else {
                    for (int j2 = 0; j2 < indent; j2++) {
                        System.out.print("  ");
                    }
                    System.out.println(child.getKey() + " = " + child.getValue());
                }
            }
        }

        public Object getAtPath(String path) {
            Dict currentNode = this;
            String[] pathSegments = path.trim().split("/");
            for (int i = 0; i < pathSegments.length; i++) {
                String segment = pathSegments[i].trim();
                if (segment.length() != 0) {
                    Object o = currentNode.get(segment);
                    if (i >= pathSegments.length - 1) {
                        return o;
                    }
                    if (o != null && (o instanceof Dict)) {
                        currentNode = (Dict) o;
                    } else {
                        return null;
                    }
                }
            }
            return null;
        }
    }

    static void silentlyClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    private static Dict parse(InputSource input) throws XmlParseException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            if (Boolean.getBoolean(PlatformUtil.XML_FEATURE_SECURE_PROCESSING)) {
                documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            } else {
                documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            }
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setEntityResolver(new EmptyXMLResolver());
            Document doc = documentBuilder.parse(input);
            Element element = doc.getDocumentElement();
            return PLIST.parse(element);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new XmlParseException("Error reading input", e);
        }
    }

    public static Dict load(File file) throws IOException, XmlParseException {
        FileInputStream byteStream = new FileInputStream(file);
        Throwable th = null;
        try {
            InputSource input = new InputSource(byteStream);
            Dict dict = parse(input);
            if (byteStream != null) {
                if (0 != 0) {
                    try {
                        byteStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    byteStream.close();
                }
            }
            return dict;
        } catch (Throwable th3) {
            if (byteStream != null) {
                if (0 != 0) {
                    try {
                        byteStream.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    byteStream.close();
                }
            }
            throw th3;
        }
    }

    PListParser() {
        this.m_dateFormat.setTimeZone(TimeZone.getTimeZone("Z"));
        this.m_simpleTypes = new HashMap();
        this.m_simpleTypes.put(Integer.class, ElementType.INTEGER);
        this.m_simpleTypes.put(Byte.class, ElementType.INTEGER);
        this.m_simpleTypes.put(Short.class, ElementType.INTEGER);
        this.m_simpleTypes.put(Short.class, ElementType.INTEGER);
        this.m_simpleTypes.put(Long.class, ElementType.INTEGER);
        this.m_simpleTypes.put(String.class, ElementType.STRING);
        this.m_simpleTypes.put(Float.class, ElementType.REAL);
        this.m_simpleTypes.put(Double.class, ElementType.REAL);
        this.m_simpleTypes.put(byte[].class, ElementType.DATA);
        this.m_simpleTypes.put(Boolean.class, ElementType.TRUE);
        this.m_simpleTypes.put(Date.class, ElementType.DATE);
    }

    Dict parse(Element element) throws XmlParseException {
        Node n;
        if (!"plist".equalsIgnoreCase(element.getNodeName())) {
            throw new XmlParseException("Expected plist top element, was: " + element.getNodeName());
        }
        Node firstChild = element.getFirstChild();
        while (true) {
            n = firstChild;
            if (n == null || n.getNodeName().equals("dict")) {
                break;
            }
            firstChild = n.getNextSibling();
        }
        Dict result = (Dict) parseElement(n);
        return result;
    }

    private Object parseElement(Node element) throws XmlParseException {
        try {
            return parseElementRaw(element);
        } catch (Exception e) {
            throw new XmlParseException("Failed to parse: " + element.getNodeName(), e);
        }
    }

    private Object parseElementRaw(Node element) throws ParseException {
        ElementType type = ElementType.valueOf(element.getNodeName().toUpperCase());
        switch (type) {
            case INTEGER:
                return parseInt(getValue(element));
            case REAL:
                return Double.valueOf(getValue(element));
            case STRING:
                return getValue(element);
            case DATE:
                return this.m_dateFormat.parse(getValue(element));
            case DATA:
                return base64decode(getValue(element));
            case ARRAY:
                return parseArray(element.getChildNodes());
            case TRUE:
                return Boolean.TRUE;
            case FALSE:
                return Boolean.FALSE;
            case DICT:
                return parseDict(element.getChildNodes());
            default:
                throw new RuntimeException("Unexpected type: " + element.getNodeName());
        }
    }

    private String getValue(Node n) {
        StringBuilder sb = new StringBuilder();
        Node firstChild = n.getFirstChild();
        while (true) {
            Node c = firstChild;
            if (c != null) {
                if (c.getNodeType() == 3) {
                    sb.append(c.getNodeValue());
                }
                firstChild = c.getNextSibling();
            } else {
                return sb.toString();
            }
        }
    }

    private Number parseInt(String value) {
        Long l = Long.valueOf(value);
        if (l.intValue() == l.longValue()) {
            return Integer.valueOf(l.intValue());
        }
        return l;
    }

    private Dict parseDict(NodeList elements) throws ParseException {
        Node value;
        Dict dict = new Dict();
        int i = 0;
        while (i < elements.getLength()) {
            Node key = elements.item(i);
            if (key.getNodeType() == 1) {
                if (!"key".equals(key.getNodeName())) {
                    throw new ParseException("Expected key but was " + key.getNodeName(), -1);
                }
                i++;
                Node nodeItem = elements.item(i);
                while (true) {
                    value = nodeItem;
                    if (value.getNodeType() == 1) {
                        break;
                    }
                    i++;
                    nodeItem = elements.item(i);
                }
                Object o = parseElementRaw(value);
                String dictName = getValue(key);
                dict.children.put(dictName, o);
            }
            i++;
        }
        return dict;
    }

    private List<Object> parseArray(NodeList elements) throws ParseException {
        ArrayList<Object> list = new ArrayList<>();
        for (int i = 0; i < elements.getLength(); i++) {
            Node o = elements.item(i);
            if (o.getNodeType() == 1) {
                list.add(parseElementRaw(o));
            }
        }
        return list;
    }

    static String base64encode(byte[] bytes) {
        StringBuilder builder = new StringBuilder(((bytes.length + 2) / 3) * 4);
        int i = 0;
        while (i < bytes.length) {
            byte b0 = bytes[i];
            byte b1 = i < bytes.length - 1 ? bytes[i + 1] : (byte) 0;
            byte b2 = i < bytes.length - 2 ? bytes[i + 2] : (byte) 0;
            builder.append(BASE64_CHARS[(b0 & 255) >> 2]);
            builder.append(BASE64_CHARS[((b0 & 3) << 4) | ((b1 & 240) >> 4)]);
            builder.append(i < bytes.length - 1 ? Character.valueOf(BASE64_CHARS[((b1 & 15) << 2) | ((b2 & 192) >> 6)]) : "=");
            builder.append(i < bytes.length - 2 ? Character.valueOf(BASE64_CHARS[b2 & 63]) : "=");
            i += 3;
        }
        return builder.toString();
    }

    static byte[] base64decode(String base64) {
        String base65 = base64.trim();
        int endTrim = base65.endsWith("==") ? 2 : base65.endsWith("=") ? 1 : 0;
        int length = ((base65.length() / 4) * 3) - endTrim;
        String base66 = base65.replace('=', 'A');
        byte[] result = new byte[length];
        int stringLength = base66.length();
        int index = 0;
        for (int i = 0; i < stringLength; i += 4) {
            int i0 = BASE64_STRING.indexOf(base66.charAt(i));
            int i1 = BASE64_STRING.indexOf(base66.charAt(i + 1));
            int i2 = BASE64_STRING.indexOf(base66.charAt(i + 2));
            int i3 = BASE64_STRING.indexOf(base66.charAt(i + 3));
            byte b0 = (byte) ((i0 << 2) | (i1 >> 4));
            byte b1 = (byte) ((i1 << 4) | (i2 >> 2));
            byte b2 = (byte) ((i2 << 6) | i3);
            int i4 = index;
            index++;
            result[i4] = b0;
            if (index < length) {
                index++;
                result[index] = b1;
                if (index < length) {
                    index++;
                    result[index] = b2;
                }
            }
        }
        return result;
    }
}
