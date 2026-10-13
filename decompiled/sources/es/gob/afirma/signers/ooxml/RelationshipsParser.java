package es.gob.afirma.signers.ooxml;

import es.gob.afirma.core.misc.SecureXmlBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/ooxml/RelationshipsParser.class */
final class RelationshipsParser {
    private static final String RELATIONSHIPS_SCHEMA = "http://schemas.openxmlformats.org/package/2006/relationships";
    private Relationship[] relations;

    RelationshipsParser(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        this.relations = null;
        this.relations = getRelationships(inputStream);
    }

    Relationship[] getRelationships() {
        if (this.relations != null) {
            return (Relationship[]) this.relations.clone();
        }
        return null;
    }

    private static Relationship[] getRelationships(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        Element documentElement = SecureXmlBuilder.getSecureDocumentBuilder().parse(inputStream).getDocumentElement();
        if (!documentElement.getNodeName().equals("Relationships") || documentElement.getAttributeNode(CanonicalizerBase.XMLNS) == null || !documentElement.getAttribute(CanonicalizerBase.XMLNS).equals(RELATIONSHIPS_SCHEMA)) {
            throw new IOException("El nodo principal no es una etiqueta Relationships");
        }
        NodeList childNodes = documentElement.getChildNodes();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < childNodes.getLength(); i++) {
            arrayList.add(getRelationship(childNodes.item(i)));
        }
        return (Relationship[]) arrayList.toArray(new Relationship[0]);
    }

    private static Relationship getRelationship(Node node) throws IOException {
        if (!node.getNodeName().equals("Relationship")) {
            throw new IOException("Se ha encontrado un nodo que es de relacion: " + node.getNodeName());
        }
        NamedNodeMap attributes = node.getAttributes();
        if (attributes.getNamedItem("Id") == null || attributes.getNamedItem("Type") == null || attributes.getNamedItem("Target") == null) {
            throw new IOException("Se ha encontrado un nodo de relacion que no disponia de todos sus atributos");
        }
        return new Relationship(attributes.getNamedItem("Id").getNodeValue(), attributes.getNamedItem("Type").getNodeValue(), attributes.getNamedItem("Target").getNodeValue());
    }
}
