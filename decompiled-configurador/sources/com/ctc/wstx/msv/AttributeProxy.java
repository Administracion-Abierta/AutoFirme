package com.ctc.wstx.msv;

import org.codehaus.stax2.validation.ValidationContext;
import org.xml.sax.Attributes;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/msv/AttributeProxy.class */
final class AttributeProxy implements Attributes {
    private final ValidationContext mContext;

    public AttributeProxy(ValidationContext ctxt) {
        this.mContext = ctxt;
    }

    @Override // org.xml.sax.Attributes
    public int getIndex(String qName) {
        String p2;
        String prefix;
        int cix = qName.indexOf(58);
        int acount = this.mContext.getAttributeCount();
        if (cix < 0) {
            for (int i = 0; i < acount; i++) {
                if (qName.equals(this.mContext.getAttributeLocalName(i)) && ((prefix = this.mContext.getAttributePrefix(i)) == null || prefix.length() == 0)) {
                    return i;
                }
            }
            return -1;
        }
        String prefix2 = qName.substring(0, cix);
        String ln = qName.substring(cix + 1);
        for (int i2 = 0; i2 < acount; i2++) {
            if (ln.equals(this.mContext.getAttributeLocalName(i2)) && (p2 = this.mContext.getAttributePrefix(i2)) != null && prefix2.equals(p2)) {
                return i2;
            }
        }
        return -1;
    }

    @Override // org.xml.sax.Attributes
    public int getIndex(String uri, String localName) {
        return this.mContext.findAttributeIndex(uri, localName);
    }

    @Override // org.xml.sax.Attributes
    public int getLength() {
        return this.mContext.getAttributeCount();
    }

    @Override // org.xml.sax.Attributes
    public String getLocalName(int index) {
        return this.mContext.getAttributeLocalName(index);
    }

    @Override // org.xml.sax.Attributes
    public String getQName(int index) {
        String prefix = this.mContext.getAttributePrefix(index);
        String ln = this.mContext.getAttributeLocalName(index);
        if (prefix == null || prefix.length() == 0) {
            return ln;
        }
        StringBuilder sb = new StringBuilder(prefix.length() + 1 + ln.length());
        sb.append(prefix);
        sb.append(':');
        sb.append(ln);
        return sb.toString();
    }

    @Override // org.xml.sax.Attributes
    public String getType(int index) {
        return this.mContext.getAttributeType(index);
    }

    @Override // org.xml.sax.Attributes
    public String getType(String qName) {
        return getType(getIndex(qName));
    }

    @Override // org.xml.sax.Attributes
    public String getType(String uri, String localName) {
        return getType(getIndex(uri, localName));
    }

    @Override // org.xml.sax.Attributes
    public String getURI(int index) {
        return this.mContext.getAttributeNamespace(index);
    }

    @Override // org.xml.sax.Attributes
    public String getValue(int index) {
        return this.mContext.getAttributeValue(index);
    }

    @Override // org.xml.sax.Attributes
    public String getValue(String qName) {
        return getValue(getIndex(qName));
    }

    @Override // org.xml.sax.Attributes
    public String getValue(String uri, String localName) {
        return this.mContext.getAttributeValue(uri, localName);
    }
}
