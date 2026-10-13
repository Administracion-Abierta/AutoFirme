package nu.xom.xslt;

import javax.xml.transform.sax.SAXSource;
import nu.xom.Document;
import nu.xom.Nodes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/xslt/XOMSource.class */
class XOMSource extends SAXSource {
    private final Nodes source;

    XOMSource(Document document) {
        this.source = new Nodes(document);
    }

    public XOMSource(Nodes nodes) {
        this.source = nodes;
    }

    @Override // javax.xml.transform.sax.SAXSource
    public InputSource getInputSource() {
        return new XOMInputSource(this.source);
    }

    @Override // javax.xml.transform.sax.SAXSource
    public XMLReader getXMLReader() {
        return new XOMReader();
    }

    @Override // javax.xml.transform.sax.SAXSource, javax.xml.transform.Source
    public String getSystemId() {
        if (this.source.size() == 0) {
            return null;
        }
        return this.source.get(0).getBaseURI();
    }
}
