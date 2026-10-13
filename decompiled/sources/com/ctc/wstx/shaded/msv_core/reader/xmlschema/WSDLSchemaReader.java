package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaGrammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/WSDLSchemaReader.class */
public final class WSDLSchemaReader {

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/WSDLSchemaReader$SimpleNamespaceContext.class */
    private static final class SimpleNamespaceContext implements NamespaceContext {
        private SimpleNamespaceContext() {
        }

        @Override // javax.xml.namespace.NamespaceContext
        public String getNamespaceURI(String prefix) {
            if ("xs".equals(prefix)) {
                return "http://www.w3.org/2001/XMLSchema";
            }
            return null;
        }

        @Override // javax.xml.namespace.NamespaceContext
        public String getPrefix(String namespaceURI) {
            if ("http://www.w3.org/2001/XMLSchema".equals(namespaceURI)) {
                return "xs";
            }
            return null;
        }

        @Override // javax.xml.namespace.NamespaceContext
        public Iterator getPrefixes(String namespaceURI) {
            List<String> prefixes = new ArrayList<>();
            if ("http://www.w3.org/2001/XMLSchema".equals(namespaceURI)) {
                prefixes.add("xs");
            }
            return prefixes.iterator();
        }
    }

    private WSDLSchemaReader() {
    }

    public static XMLSchemaGrammar read(Source wsdlSource, SAXParserFactory factory, GrammarReaderController2 controller) throws XPathExpressionException, TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        DOMResult wsdlDom = new DOMResult();
        transformerFactory.newTransformer().transform(wsdlSource, wsdlDom);
        Node wsdl = wsdlDom.getNode();
        Map<String, String> wsdlNamespaceMappings = new HashMap<>();
        Document wsdlDoc = (Document) wsdl;
        NamedNodeMap attrMap = wsdlDoc.getDocumentElement().getAttributes();
        if (attrMap != null) {
            for (int x = 0; x < attrMap.getLength(); x++) {
                Attr attr = (Attr) attrMap.item(x);
                String ns = attr.getNamespaceURI();
                if ("http://www.w3.org/2000/xmlns/".equals(ns)) {
                    String localName = attr.getLocalName();
                    String uri = attr.getValue();
                    wsdlNamespaceMappings.put(localName, uri);
                }
            }
        }
        String wsdlSystemId = wsdlSource.getSystemId();
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new SimpleNamespaceContext());
        Map<String, EmbeddedSchema> schemas = new HashMap<>();
        NodeList schemaNodes = (NodeList) xpath.evaluate("//xs:schema", wsdl, XPathConstants.NODESET);
        for (int x2 = 0; x2 < schemaNodes.getLength(); x2++) {
            Element schema = (Element) schemaNodes.item(x2);
            String targetNamespace = schema.getAttribute("targetNamespace");
            String systemId = wsdlSystemId + "#" + x2;
            EmbeddedSchema embeddedWSDLSchema = new EmbeddedSchema(systemId, schema);
            schemas.put(targetNamespace, embeddedWSDLSchema);
        }
        WSDLGrammarReaderController wsdlController = new WSDLGrammarReaderController(controller, wsdlSystemId, schemas);
        XMLSchemaReader reader = new XMLSchemaReader(wsdlController);
        reader.setAdditionalNamespaceMap(wsdlNamespaceMappings);
        MultiSchemaReader multiSchemaReader = new MultiSchemaReader(reader);
        for (EmbeddedSchema schema2 : schemas.values()) {
            DOMSource source = new DOMSource(schema2.getSchemaElement());
            source.setSystemId(schema2.getSystemId());
            multiSchemaReader.parse(source);
        }
        return multiSchemaReader.getResult();
    }
}
