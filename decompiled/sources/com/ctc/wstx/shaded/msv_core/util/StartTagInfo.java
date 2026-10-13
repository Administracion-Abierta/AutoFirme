package com.ctc.wstx.shaded.msv_core.util;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2;
import com.ctc.wstx.shaded.msv_core.grammar.util.IDContextProviderWrapper;
import org.xml.sax.Attributes;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/util/StartTagInfo.class */
public class StartTagInfo {
    public String namespaceURI;
    public String localName;
    public String qName;
    public Attributes attributes;
    public IDContextProvider2 context;

    protected StartTagInfo() {
    }

    public StartTagInfo(String namespaceURI, String localName, String qName, Attributes attributes, IDContextProvider context) {
        reinit(namespaceURI, localName, qName, attributes, context);
    }

    public StartTagInfo(String namespaceURI, String localName, String qName, Attributes attributes, IDContextProvider2 context) {
        reinit(namespaceURI, localName, qName, attributes, context);
    }

    public StartTagInfo(String namespaceURI, String localName, String qName, Attributes attributes) {
        reinit(namespaceURI, localName, qName, attributes, (IDContextProvider2) null);
    }

    public void reinit(String namespaceURI, String localName, String qName, Attributes attributes, IDContextProvider context) {
        reinit(namespaceURI, localName, qName, attributes, IDContextProviderWrapper.create(context));
    }

    public void reinit(String namespaceURI, String localName, String qName, Attributes attributes, IDContextProvider2 context) {
        this.namespaceURI = namespaceURI;
        this.localName = localName;
        this.qName = qName;
        this.attributes = attributes;
        this.context = context;
    }

    public final boolean containsAttribute(String attrName) {
        return containsAttribute("", attrName);
    }

    public final boolean containsAttribute(String namespaceURI, String attrName) {
        return this.attributes.getIndex(namespaceURI, attrName) != -1;
    }

    public final String getAttribute(String attrName) {
        return getAttribute("", attrName);
    }

    public final String getAttribute(String namespaceURI, String attrName) {
        return this.attributes.getValue(namespaceURI, attrName);
    }

    public final String getCollapsedAttribute(String attrName) {
        String s = getAttribute(attrName);
        if (s == null) {
            return null;
        }
        return WhiteSpaceProcessor.collapse(s);
    }

    public final String getDefaultedAttribute(String attrName, String defaultValue) {
        return getDefaultedAttribute("", attrName, defaultValue);
    }

    public final String getDefaultedAttribute(String namespaceURI, String attrName, String defaultValue) {
        String v = getAttribute(namespaceURI, attrName);
        return v != null ? v : defaultValue;
    }
}
