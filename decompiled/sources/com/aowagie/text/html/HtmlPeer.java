package com.aowagie.text.html;

import com.aowagie.text.ElementTags;
import com.aowagie.text.xml.XmlPeer;
import java.util.Properties;
import org.xml.sax.Attributes;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/html/HtmlPeer.class */
class HtmlPeer extends XmlPeer {
    HtmlPeer(String name, String alias) {
        super(name, alias.toLowerCase());
    }

    @Override // com.aowagie.text.xml.XmlPeer
    public void addAlias(String name, String alias) {
        this.attributeAliases.put(alias.toLowerCase(), name);
    }

    @Override // com.aowagie.text.xml.XmlPeer
    public Properties getAttributes(Attributes attrs) {
        Properties attributes = new Properties();
        attributes.putAll(this.attributeValues);
        if (this.defaultContent != null) {
            attributes.put(ElementTags.ITEXT, this.defaultContent);
        }
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); i++) {
                String attribute = getName(attrs.getQName(i).toLowerCase());
                String value = attrs.getValue(i);
                attributes.setProperty(attribute, value);
            }
        }
        return attributes;
    }
}
