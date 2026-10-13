package org.apache.xml.security.stax.config;

import com.aowagie.text.xml.XMLConstants;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/config/XIncludeHandler.class */
public class XIncludeHandler extends DefaultHandler {
    private static final transient Logger LOG = LoggerFactory.getLogger((Class<?>) XIncludeHandler.class);
    private static final String xIncludeNS = "http://www.w3.org/2001/XInclude";
    private static final String xIncludeLN = "include";
    private final ContentHandler contentHandler;
    private URL systemId;
    private boolean skipEvents;
    private final Map<URI, Document> uriDocMap;

    public XIncludeHandler(ContentHandler contentHandler) {
        this(contentHandler, new HashMap());
    }

    private XIncludeHandler(ContentHandler contentHandler, Map<URI, Document> uriDocMap) {
        this.skipEvents = false;
        this.contentHandler = contentHandler;
        this.uriDocMap = uriDocMap;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void setDocumentLocator(Locator locator) {
        if (locator.getSystemId() == null && this.systemId == null) {
            throw new UnsupportedOperationException("Please specify a correct systemId to the sax.parse() method!");
        }
        try {
            if (locator.getSystemId() != null) {
                this.systemId = new URL(locator.getSystemId());
            }
            this.contentHandler.setDocumentLocator(locator);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startDocument() throws SAXException {
        if (!this.skipEvents) {
            this.contentHandler.startDocument();
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endDocument() throws SAXException {
        if (!this.skipEvents) {
            this.contentHandler.endDocument();
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (!this.skipEvents) {
            this.contentHandler.startPrefixMapping(prefix, uri);
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endPrefixMapping(String prefix) throws SAXException {
        if (!this.skipEvents) {
            this.contentHandler.endPrefixMapping(prefix);
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if ("http://www.w3.org/2001/XInclude".equals(uri) && "include".equals(localName)) {
            String href = atts.getValue("href");
            if (href == null) {
                throw new SAXException("XInclude href attribute is missing");
            }
            String parse = atts.getValue("parse");
            if (parse != null && !"xml".equals(parse)) {
                throw new UnsupportedOperationException("Only parse=\"xml\" is currently supported");
            }
            String xpointer = atts.getValue("xpointer");
            URL url = ClassLoaderUtils.getResource(href, XIncludeHandler.class);
            if (url == null) {
                throw new SAXException("XML file not found: " + href);
            }
            try {
                Document document = this.uriDocMap.get(url.toURI());
                if (document == null) {
                    DOMResult domResult = new DOMResult();
                    try {
                        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
                        SAXTransformerFactory saxTransformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
                        saxTransformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE.booleanValue());
                        try {
                            saxTransformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                            saxTransformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
                        } catch (IllegalArgumentException e) {
                        }
                        TransformerHandler transformerHandler = saxTransformerFactory.newTransformerHandler();
                        transformerHandler.setResult(domResult);
                        xmlReader.setContentHandler(new XIncludeHandler(transformerHandler, this.uriDocMap));
                        xmlReader.parse(url.toExternalForm());
                        document = (Document) domResult.getNode();
                        document.setDocumentURI(url.toExternalForm());
                        try {
                            this.uriDocMap.put(url.toURI(), document);
                        } catch (URISyntaxException e2) {
                            throw new SAXException(e2);
                        }
                    } catch (IOException e3) {
                        throw new SAXException(e3);
                    } catch (TransformerConfigurationException e4) {
                        throw new SAXException(e4);
                    }
                }
                SAXResult saxResult = new SAXResult(this);
                this.skipEvents = true;
                try {
                    try {
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE.booleanValue());
                        try {
                            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
                        } catch (IllegalArgumentException e5) {
                        }
                        Transformer transformer = transformerFactory.newTransformer();
                        if (xpointer == null) {
                            transformer.transform(new DOMSource(document, document.getDocumentURI()), saxResult);
                        } else {
                            NodeList nodeList = evaluateXPointer(xpointer, document);
                            int length = nodeList.getLength();
                            for (int i = 0; i < length; i++) {
                                Node node = nodeList.item(i);
                                transformer.transform(new DOMSource(node, document.getDocumentURI()), saxResult);
                            }
                        }
                        this.skipEvents = false;
                        return;
                    } catch (Throwable th) {
                        this.skipEvents = false;
                        throw th;
                    }
                } catch (TransformerConfigurationException e6) {
                    throw new SAXException(e6);
                } catch (TransformerException e7) {
                    throw new SAXException(e7);
                }
            } catch (URISyntaxException ex) {
                throw new SAXException(ex);
            }
        }
        this.contentHandler.startElement(uri, localName, qName, atts);
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (!"http://www.w3.org/2001/XInclude".equals(uri) || !"include".equals(localName)) {
            this.contentHandler.endElement(uri, localName, qName);
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.contentHandler.characters(ch, start, length);
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.contentHandler.ignorableWhitespace(ch, start, length);
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void processingInstruction(String target, String data) throws SAXException {
        this.contentHandler.processingInstruction(target, data);
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void skippedEntity(String name) throws SAXException {
        this.contentHandler.skippedEntity(name);
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ErrorHandler
    public void warning(SAXParseException e) throws SAXException {
        LOG.warn(e.getMessage(), (Throwable) e);
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ErrorHandler
    public void error(SAXParseException e) throws SAXException {
        LOG.error(e.getMessage(), (Throwable) e);
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ErrorHandler
    public void fatalError(SAXParseException e) throws SAXException {
        LOG.error(e.getMessage(), (Throwable) e);
    }

    private NodeList evaluateXPointer(String xpointer, Node node) throws SAXException {
        int xPointerSchemeIndex = xpointer.indexOf("xpointer(");
        if (xPointerSchemeIndex < 0) {
            throw new SAXException("Only xpointer scheme is supported ATM");
        }
        int xPointerSchemeIndex2 = xPointerSchemeIndex + "xpointer(".length();
        int xPointerSchemeEndIndex = findBalancedEndIndex(xpointer, xPointerSchemeIndex2, '(', ')');
        XPathFactory xPathFactory = XPathFactory.newInstance();
        try {
            xPathFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE.booleanValue());
            XPath xPath = xPathFactory.newXPath();
            int xmlnsSchemeIndex = xpointer.indexOf("xmlns(");
            if (xmlnsSchemeIndex >= 0) {
                int xmlnsSchemeIndex2 = xmlnsSchemeIndex + "xmlns(".length();
                int xmlnsSchemeEndIndex = findBalancedEndIndex(xpointer, xmlnsSchemeIndex2, '(', ')');
                String namespaceScheme = xpointer.substring(xmlnsSchemeIndex2, xmlnsSchemeEndIndex);
                final String[] namespaceSplit = namespaceScheme.split("=");
                xPath.setNamespaceContext(new NamespaceContext() { // from class: org.apache.xml.security.stax.config.XIncludeHandler.1
                    @Override // javax.xml.namespace.NamespaceContext
                    public String getNamespaceURI(String prefix) {
                        if (prefix.equals(namespaceSplit[0])) {
                            return namespaceSplit[1];
                        }
                        return null;
                    }

                    @Override // javax.xml.namespace.NamespaceContext
                    public String getPrefix(String namespaceURI) {
                        if (namespaceURI.equals(namespaceSplit[1])) {
                            return namespaceSplit[0];
                        }
                        return null;
                    }

                    @Override // javax.xml.namespace.NamespaceContext
                    public Iterator<String> getPrefixes(String namespaceURI) {
                        return null;
                    }
                });
            }
            try {
                return (NodeList) xPath.evaluate(xpointer.substring(xPointerSchemeIndex2, xPointerSchemeEndIndex), node, XPathConstants.NODESET);
            } catch (XPathExpressionException e) {
                throw new SAXException(e);
            }
        } catch (XPathFactoryConfigurationException ex) {
            throw new SAXException(ex);
        }
    }

    private int findBalancedEndIndex(String string, int startIndex, char opening, char ending) {
        int endIndex = -1;
        int openPar = 1;
        int length = string.length();
        for (int i = startIndex; i < length; i++) {
            char curChar = string.charAt(i);
            if (curChar == opening) {
                openPar++;
            } else if (curChar == ending) {
                openPar--;
            }
            if (openPar == 0) {
                endIndex = i;
                break;
            }
        }
        return endIndex;
    }
}
