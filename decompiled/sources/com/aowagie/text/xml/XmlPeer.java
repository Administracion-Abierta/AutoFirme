package com.aowagie.text.xml;

import com.aowagie.text.ElementTags;
import java.util.Properties;
import org.xml.sax.Attributes;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/xml/XmlPeer.class */
public class XmlPeer {
    private final String tagname;
    private final String customTagname;
    protected Properties attributeAliases = new Properties();
    protected Properties attributeValues = new Properties();
    protected String defaultContent = null;

    public XmlPeer(String name, String alias) {
        this.tagname = name;
        this.customTagname = alias;
    }

    public String getTag() {
        return this.tagname;
    }

    public String getAlias() {
        return this.customTagname;
    }

    public Properties getAttributes(Attributes attrs) {
        Properties attributes = new Properties();
        attributes.putAll(this.attributeValues);
        if (this.defaultContent != null) {
            attributes.put(ElementTags.ITEXT, this.defaultContent);
        }
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); i++) {
                String attribute = getName(attrs.getQName(i));
                attributes.setProperty(attribute, attrs.getValue(i));
            }
        }
        return attributes;
    }

    public void addAlias(String name, String alias) {
        this.attributeAliases.put(alias, name);
    }

    public void addValue(String name, String value) {
        this.attributeValues.put(name, value);
    }

    public void setContent(String content) {
        this.defaultContent = content;
    }

    public String getName(String name) {
        String value = this.attributeAliases.getProperty(name);
        if (value != null) {
            return value;
        }
        return name;
    }

    public Properties getDefaultValues() {
        return this.attributeValues;
    }
}
