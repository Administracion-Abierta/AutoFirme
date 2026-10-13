package com.aowagie.text.xml;

import com.aowagie.text.ExceptionConverter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/xml/TagMap.class */
class TagMap extends HashMap {
    private static final long serialVersionUID = -6809383366554350820L;

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/xml/TagMap$AttributeHandler.class */
    private class AttributeHandler extends DefaultHandler {
        private static final String TAG = "tag";
        private static final String ATTRIBUTE = "attribute";
        private static final String NAME = "name";
        private static final String ALIAS = "alias";
        private static final String VALUE = "value";
        private static final String CONTENT = "content";
        private final HashMap tagMap;
        private XmlPeer currentPeer;

        private AttributeHandler(HashMap tagMap) {
            this.tagMap = tagMap;
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void startElement(String uri, String lname, String tag, Attributes attrs) {
            String name = attrs.getValue("name");
            String alias = attrs.getValue(ALIAS);
            String value = attrs.getValue("value");
            if (name != null) {
                if (TAG.equals(tag)) {
                    this.currentPeer = new XmlPeer(name, alias);
                } else if (ATTRIBUTE.equals(tag)) {
                    if (alias != null) {
                        this.currentPeer.addAlias(name, alias);
                    }
                    if (value != null) {
                        this.currentPeer.addValue(name, value);
                    }
                }
            }
            String value2 = attrs.getValue("content");
            if (value2 != null) {
                this.currentPeer.setContent(value2);
            }
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void ignorableWhitespace(char[] ch, int start, int length) {
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void characters(char[] ch, int start, int length) {
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void endElement(String uri, String lname, String tag) {
            if (TAG.equals(tag)) {
                this.tagMap.put(this.currentPeer.getAlias(), this.currentPeer);
            }
        }
    }

    public TagMap(String tagfile) {
        try {
            init(TagMap.class.getClassLoader().getResourceAsStream(tagfile));
        } catch (Exception e) {
            try {
                init(new FileInputStream(tagfile));
            } catch (FileNotFoundException fnfe) {
                throw new ExceptionConverter(fnfe);
            }
        }
    }

    public TagMap(InputStream in) {
        init(in);
    }

    private void init(InputStream in) {
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(new InputSource(in), new AttributeHandler(this));
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }
}
