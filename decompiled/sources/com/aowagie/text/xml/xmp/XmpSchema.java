package com.aowagie.text.xml.xmp;

import java.util.Enumeration;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/xml/xmp/XmpSchema.class */
public abstract class XmpSchema extends Properties {
    private static final long serialVersionUID = -176374295948945272L;
    private final String xmlns;

    protected XmpSchema(String xmlns) {
        this.xmlns = xmlns;
    }

    @Override // java.util.Hashtable
    public String toString() {
        StringBuffer buf = new StringBuffer();
        Enumeration<?> enumerationPropertyNames = propertyNames();
        while (enumerationPropertyNames.hasMoreElements()) {
            process(buf, enumerationPropertyNames.nextElement());
        }
        return buf.toString();
    }

    private void process(StringBuffer buf, Object p) {
        buf.append('<');
        buf.append(p);
        buf.append('>');
        buf.append(get(p));
        buf.append("</");
        buf.append(p);
        buf.append('>');
    }

    public String getXmlns() {
        return this.xmlns;
    }

    @Override // java.util.Properties
    public Object setProperty(String key, String value) {
        return super.setProperty(key, escape(value));
    }

    protected Object setProperty(String key, XmpArray value) {
        return super.setProperty(key, value.toString());
    }

    static String escape(String content) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < content.length(); i++) {
            switch (content.charAt(i)) {
                case '\"':
                    buf.append("&quot;");
                    break;
                case '&':
                    buf.append("&amp;");
                    break;
                case '\'':
                    buf.append("&apos;");
                    break;
                case '<':
                    buf.append("&lt;");
                    break;
                case '>':
                    buf.append("&gt;");
                    break;
                default:
                    buf.append(content.charAt(i));
                    break;
            }
        }
        return buf.toString();
    }
}
