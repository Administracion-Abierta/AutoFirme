package com.aowagie.text.xml.xmp;

import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/xml/xmp/XmpArray.class */
public class XmpArray<E> extends ArrayList<E> {
    private static final long serialVersionUID = 5722854116328732742L;
    public static final String UNORDERED = "rdf:Bag";
    public static final String ORDERED = "rdf:Seq";
    static final String ALTERNATIVE = "rdf:Alt";
    private final String type;

    public XmpArray(String type) {
        this.type = type;
    }

    @Override // java.util.AbstractCollection
    public String toString() {
        StringBuffer buf = new StringBuffer("<");
        buf.append(this.type);
        buf.append('>');
        for (Object element : this) {
            String s = (String) element;
            buf.append("<rdf:li>");
            buf.append(XmpSchema.escape(s));
            buf.append("</rdf:li>");
        }
        buf.append("</");
        buf.append(this.type);
        buf.append('>');
        return buf.toString();
    }
}
