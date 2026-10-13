package nu.xom.xslt;

import java.io.IOException;
import nu.xom.converters.SAXConverter;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/xslt/XOMReader.class */
class XOMReader implements XMLReader {
    private SAXConverter converter;

    XOMReader() {
    }

    @Override // org.xml.sax.XMLReader
    public boolean getFeature(String str) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/features/namespace-prefixes".equals(str) || "http://xml.org/sax/features/namespaces".equals(str)) {
            return true;
        }
        throw new SAXNotRecognizedException("XOMReader doesn't support features");
    }

    @Override // org.xml.sax.XMLReader
    public void setFeature(String str, boolean z) throws SAXNotRecognizedException, SAXNotSupportedException {
    }

    @Override // org.xml.sax.XMLReader
    public Object getProperty(String str) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/properties/lexical-handler".equals(str)) {
            return this.converter.getLexicalHandler();
        }
        throw new SAXNotRecognizedException("XOMReader doesn't support features");
    }

    @Override // org.xml.sax.XMLReader
    public void setProperty(String str, Object obj) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (!"http://xml.org/sax/properties/lexical-handler".equals(str)) {
            throw new SAXNotRecognizedException(new StringBuffer().append("XOMReader doesn't support ").append(str).toString());
        }
        this.converter.setLexicalHandler((LexicalHandler) obj);
    }

    @Override // org.xml.sax.XMLReader
    public void setEntityResolver(EntityResolver entityResolver) {
        throw new UnsupportedOperationException();
    }

    @Override // org.xml.sax.XMLReader
    public EntityResolver getEntityResolver() {
        return null;
    }

    @Override // org.xml.sax.XMLReader
    public void setDTDHandler(DTDHandler dTDHandler) {
    }

    @Override // org.xml.sax.XMLReader
    public DTDHandler getDTDHandler() {
        return null;
    }

    @Override // org.xml.sax.XMLReader
    public void setContentHandler(ContentHandler contentHandler) {
        this.converter = new SAXConverter(contentHandler);
        this.converter.setContentHandler(new XSLTHandler(null));
    }

    @Override // org.xml.sax.XMLReader
    public ContentHandler getContentHandler() {
        return null;
    }

    @Override // org.xml.sax.XMLReader
    public void setErrorHandler(ErrorHandler errorHandler) {
    }

    @Override // org.xml.sax.XMLReader
    public ErrorHandler getErrorHandler() {
        return null;
    }

    @Override // org.xml.sax.XMLReader
    public void parse(InputSource inputSource) throws SAXException, IOException {
        this.converter.convert(((XOMInputSource) inputSource).getNodes());
    }

    @Override // org.xml.sax.XMLReader
    public void parse(String str) throws SAXException, IOException {
        throw new UnsupportedOperationException();
    }
}
