package es.gob.afirma.signers.xades;

import es.uji.crypto.xades.jxades.security.xml.XAdES.DataObjectFormat;
import es.uji.crypto.xades.jxades.security.xml.XAdES.DataObjectFormatImpl;
import es.uji.crypto.xades.jxades.security.xml.XAdES.ObjectIdentifier;
import es.uji.crypto.xades.jxades.security.xml.XAdES.ObjectIdentifierImpl;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xades/DataObjectFormatParser.class */
public class DataObjectFormatParser {
    private static final String OBJECT_REFERENCE_TAG = "ObjectReference";
    private static final String DESCRIPTION_TAG = "Description";
    private static final String MIMETYPE_TAG = "MimeType";
    private static final String ENCODING_TAG = "Encoding";
    private static final String OBJECT_IDENTIFIER_TAG = "ObjectIdentifier";
    private static final String IDENTIFIER_TAG = "Identifier";
    private static final String QUALIFIER_ATTRIBUTE = "Qualifier";
    private static final String DOCUMENTATION_REFERENCES_TAG = "DocumentationReferences";
    private static final String DOCUMENTATION_REFERENCE_TAG = "DocumentationReference";

    private DataObjectFormatParser() {
    }

    static DataObjectFormat parseDataObjectFormat(Element element) {
        DataObjectFormatImpl dataObjectFormatImpl = new DataObjectFormatImpl(null, null, null, null, element.getAttribute(OBJECT_REFERENCE_TAG));
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node nodeItem = childNodes.item(i);
            if (nodeItem.getNodeType() == 1) {
                switch (nodeItem.getLocalName()) {
                    case "Description":
                        String textContent = nodeItem.getTextContent();
                        if (textContent != null && !textContent.isEmpty()) {
                            dataObjectFormatImpl.setDescription(textContent.trim());
                            break;
                        } else {
                            break;
                        }
                        break;
                    case "MimeType":
                        String textContent2 = nodeItem.getTextContent();
                        if (textContent2 != null && !textContent2.isEmpty()) {
                            dataObjectFormatImpl.setMimeType(textContent2);
                            break;
                        } else {
                            break;
                        }
                        break;
                    case "Encoding":
                        String textContent3 = nodeItem.getTextContent();
                        if (textContent3 != null && !textContent3.isEmpty()) {
                            dataObjectFormatImpl.setEncoding(textContent3);
                            break;
                        } else {
                            break;
                        }
                        break;
                    case "ObjectIdentifier":
                        dataObjectFormatImpl.setObjectIdentifier(parseObjectIdentifier(nodeItem));
                        break;
                }
            }
        }
        return dataObjectFormatImpl;
    }

    private static ObjectIdentifier parseObjectIdentifier(Node node) {
        String textContent;
        String attribute = null;
        String textContent2 = null;
        String textContent3 = null;
        ArrayList arrayList = new ArrayList(0);
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node nodeItem = childNodes.item(i);
            if (nodeItem.getNodeType() == 1) {
                switch (nodeItem.getLocalName()) {
                    case "Identifier":
                        attribute = ((Element) nodeItem).getAttribute(QUALIFIER_ATTRIBUTE);
                        textContent2 = nodeItem.getTextContent();
                        break;
                    case "Description":
                        textContent3 = nodeItem.getTextContent();
                        break;
                    case "DocumentationReferences":
                        NodeList childNodes2 = nodeItem.getChildNodes();
                        for (int i2 = 0; i2 < childNodes2.getLength(); i2++) {
                            Node nodeItem2 = childNodes2.item(i2);
                            if (nodeItem2.getNodeType() == 1 && DOCUMENTATION_REFERENCE_TAG.equals(nodeItem2.getLocalName()) && (textContent = nodeItem2.getTextContent()) != null) {
                                arrayList.add(textContent);
                            }
                        }
                        break;
                }
            }
        }
        return new ObjectIdentifierImpl(attribute, textContent2, textContent3, arrayList);
    }
}
