package com.aowagie.text.xml;

import com.aowagie.text.DocListener;
import java.util.HashMap;
import java.util.Properties;
import org.xml.sax.Attributes;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/xml/SAXmyHandler.class */
class SAXmyHandler extends SAXiTextHandler {
    public SAXmyHandler(DocListener document, HashMap myTags) {
        super(document, myTags);
    }

    @Override // com.aowagie.text.xml.SAXiTextHandler, org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startElement(String uri, String lname, String name, Attributes attrs) {
        if (this.myTags.containsKey(name)) {
            XmlPeer peer = (XmlPeer) this.myTags.get(name);
            handleStartingTags(peer.getTag(), peer.getAttributes(attrs));
            return;
        }
        Properties attributes = new Properties();
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); i++) {
                String attribute = attrs.getQName(i);
                attributes.setProperty(attribute, attrs.getValue(i));
            }
        }
        handleStartingTags(name, attributes);
    }

    @Override // com.aowagie.text.xml.SAXiTextHandler, org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endElement(String uri, String lname, String name) {
        if (this.myTags.containsKey(name)) {
            XmlPeer peer = (XmlPeer) this.myTags.get(name);
            handleEndingTags(peer.getTag());
        } else {
            handleEndingTags(name);
        }
    }
}
