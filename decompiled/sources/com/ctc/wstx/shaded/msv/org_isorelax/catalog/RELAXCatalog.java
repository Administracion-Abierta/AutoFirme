package com.ctc.wstx.shaded.msv.org_isorelax.catalog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/org_isorelax/catalog/RELAXCatalog.class */
public class RELAXCatalog {
    private Map grammars_;

    public RELAXCatalog() throws ParserConfigurationException, SAXException, IOException {
        this("http://www.iso-relax.org/catalog");
    }

    public RELAXCatalog(String rootURI) throws ParserConfigurationException, SAXException, IOException {
        this.grammars_ = new HashMap();
        String catalogFile = rootURI + "/catalog.xml";
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(catalogFile, new CatalogHandler());
    }

    public InputSource getGrammar(String uri) {
        String location = (String) this.grammars_.get(uri);
        if (location == null) {
            return null;
        }
        return new InputSource(location);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/org_isorelax/catalog/RELAXCatalog$CatalogHandler.class */
    class CatalogHandler extends DefaultHandler {
        CatalogHandler() {
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
            String uri = atts.getValue("uri");
            String grammar = atts.getValue("grammar");
            RELAXCatalog.this.grammars_.put(uri, grammar);
        }
    }
}
