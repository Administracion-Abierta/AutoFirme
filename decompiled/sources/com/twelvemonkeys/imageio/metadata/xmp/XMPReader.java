package com.twelvemonkeys.imageio.metadata.xmp;

import com.aowagie.text.xml.XMLConstants;
import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.MetadataReader;
import com.twelvemonkeys.imageio.util.IIOUtil;
import com.twelvemonkeys.lang.Validate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/xmp/XMPReader.class */
public final class XMPReader extends MetadataReader {
    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    @Override // com.twelvemonkeys.imageio.metadata.MetadataReader
    public Directory read(ImageInputStream imageInputStream) throws IOException, IIOException {
        Validate.notNull(imageInputStream, "input");
        try {
            DocumentBuilder documentBuilderNewDocumentBuilder = createDocumentBuilderFactory().newDocumentBuilder();
            documentBuilderNewDocumentBuilder.setErrorHandler(new DefaultHandler());
            Document document = documentBuilderNewDocumentBuilder.parse(new InputSource(IIOUtil.createStreamAdapter(imageInputStream)));
            return parseDirectories(document.getElementsByTagNameNS(XMP.NS_RDF, "RDF").item(0), document.getElementsByTagNameNS(XMP.NS_RDF, "Description"), getToolkit(document));
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e2) {
            throw new IIOException(e2.getMessage(), e2);
        }
    }

    private DocumentBuilderFactory createDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactoryNewInstance = DocumentBuilderFactory.newInstance();
        documentBuilderFactoryNewInstance.setNamespaceAware(true);
        documentBuilderFactoryNewInstance.setXIncludeAware(false);
        documentBuilderFactoryNewInstance.setExpandEntityReferences(false);
        documentBuilderFactoryNewInstance.setAttribute(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        documentBuilderFactoryNewInstance.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        documentBuilderFactoryNewInstance.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        documentBuilderFactoryNewInstance.setFeature("http://xml.org/sax/features/external-general-entities", false);
        documentBuilderFactoryNewInstance.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        documentBuilderFactoryNewInstance.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return documentBuilderFactoryNewInstance;
    }

    private String getToolkit(Document document) {
        Node namedItemNS;
        NodeList elementsByTagNameNS = document.getElementsByTagNameNS(XMP.NS_X, "xmpmeta");
        if (elementsByTagNameNS == null || elementsByTagNameNS.getLength() <= 0 || (namedItemNS = elementsByTagNameNS.item(0).getAttributes().getNamedItemNS(XMP.NS_X, "xmptk")) == null) {
            return null;
        }
        return namedItemNS.getNodeValue();
    }

    private XMPDirectory parseDirectories(Node node, NodeList nodeList, String str) {
        Object childTextValue;
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (Node node2 : asIterable(nodeList)) {
            if (node2.getParentNode() == node) {
                parseAttributesForKnownElements(linkedHashMap, node2);
                for (Node node3 : asIterable(node2.getChildNodes())) {
                    if (node3.getNodeType() == 1) {
                        List<Entry> arrayList = linkedHashMap.get(node3.getNamespaceURI());
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                            linkedHashMap.put(node3.getNamespaceURI(), arrayList);
                        }
                        if (isResourceType(node3)) {
                            childTextValue = parseAsResource(node3);
                        } else {
                            LinkedHashMap linkedHashMap2 = new LinkedHashMap();
                            parseAttributesForKnownElements(linkedHashMap2, node3);
                            if (!linkedHashMap2.isEmpty()) {
                                ArrayList arrayList2 = new ArrayList(linkedHashMap2.size());
                                Iterator<Map.Entry<String, List<Entry>>> it = linkedHashMap2.entrySet().iterator();
                                while (it.hasNext()) {
                                    arrayList2.addAll(it.next().getValue());
                                }
                                childTextValue = new RDFDescription(arrayList2);
                            } else {
                                childTextValue = getChildTextValue(node3);
                            }
                        }
                        arrayList.add(new XMPEntry(node3.getNamespaceURI() + node3.getLocalName(), node3.getLocalName(), childTextValue));
                    }
                }
            }
        }
        ArrayList arrayList3 = new ArrayList(linkedHashMap.size());
        for (Map.Entry<String, List<Entry>> entry : linkedHashMap.entrySet()) {
            arrayList3.add(new RDFDescription(entry.getKey(), entry.getValue()));
        }
        return new XMPDirectory(arrayList3, str);
    }

    private boolean isResourceType(Node node) {
        Node namedItemNS = node.getAttributes().getNamedItemNS(XMP.NS_RDF, "parseType");
        return namedItemNS != null && "Resource".equals(namedItemNS.getNodeValue());
    }

    private RDFDescription parseAsResource(Node node) {
        ArrayList arrayList = new ArrayList();
        for (Node node2 : asIterable(node.getChildNodes())) {
            if (node2.getNodeType() == 1) {
                arrayList.add(new XMPEntry(node2.getNamespaceURI() + node2.getLocalName(), node2.getLocalName(), getChildTextValue(node2)));
            }
        }
        return new RDFDescription(arrayList);
    }

    private void parseAttributesForKnownElements(Map<String, List<Entry>> map, Node node) {
        for (Node node2 : asIterable(node.getAttributes())) {
            if (XMP.ELEMENTS.contains(node2.getNamespaceURI())) {
                List<Entry> arrayList = map.get(node2.getNamespaceURI());
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    map.put(node2.getNamespaceURI(), arrayList);
                }
                arrayList.add(new XMPEntry(node2.getNamespaceURI() + node2.getLocalName(), node2.getLocalName(), node2.getNodeValue()));
            }
        }
    }

    private Object getChildTextValue(Node node) {
        for (Node node2 : asIterable(node.getChildNodes())) {
            if (XMP.NS_RDF.equals(node2.getNamespaceURI()) && "Alt".equals(node2.getLocalName())) {
                LinkedHashMap linkedHashMap = new LinkedHashMap();
                for (Node node3 : asIterable(node2.getChildNodes())) {
                    if (XMP.NS_RDF.equals(node3.getNamespaceURI()) && "li".equals(node3.getLocalName())) {
                        Node namedItem = node3.getAttributes().getNamedItem("xml:lang");
                        linkedHashMap.put(namedItem == null ? null : namedItem.getTextContent(), getChildTextValue(node3));
                    }
                }
                return linkedHashMap;
            }
            if (XMP.NS_RDF.equals(node2.getNamespaceURI()) && ("Seq".equals(node2.getLocalName()) || "Bag".equals(node2.getLocalName()))) {
                ArrayList arrayList = new ArrayList();
                for (Node node4 : asIterable(node2.getChildNodes())) {
                    if (XMP.NS_RDF.equals(node4.getNamespaceURI()) && "li".equals(node4.getLocalName())) {
                        arrayList.add(getChildTextValue(node4));
                    }
                }
                return Collections.unmodifiableList(arrayList);
            }
        }
        if (isResourceType(node)) {
            return parseAsResource(node);
        }
        Node firstChild = node.getFirstChild();
        String nodeValue = firstChild != null ? firstChild.getNodeValue() : null;
        return nodeValue != null ? nodeValue.trim() : "";
    }

    private Iterable<? extends Node> asIterable(final NamedNodeMap namedNodeMap) {
        return new Iterable<Node>() { // from class: com.twelvemonkeys.imageio.metadata.xmp.XMPReader.1
            @Override // java.lang.Iterable
            public Iterator<Node> iterator() {
                return new Iterator<Node>() { // from class: com.twelvemonkeys.imageio.metadata.xmp.XMPReader.1.1
                    private int index;

                    @Override // java.util.Iterator
                    public boolean hasNext() {
                        return namedNodeMap != null && namedNodeMap.getLength() > this.index;
                    }

                    /* JADX WARN: Can't rename method to resolve collision */
                    @Override // java.util.Iterator
                    public Node next() {
                        NamedNodeMap namedNodeMap2 = namedNodeMap;
                        int i = this.index;
                        this.index = i + 1;
                        return namedNodeMap2.item(i);
                    }

                    @Override // java.util.Iterator
                    public void remove() {
                        throw new UnsupportedOperationException("Method remove not supported");
                    }
                };
            }
        };
    }

    private Iterable<? extends Node> asIterable(final NodeList nodeList) {
        return new Iterable<Node>() { // from class: com.twelvemonkeys.imageio.metadata.xmp.XMPReader.2
            @Override // java.lang.Iterable
            public Iterator<Node> iterator() {
                return new Iterator<Node>() { // from class: com.twelvemonkeys.imageio.metadata.xmp.XMPReader.2.1
                    private int index;

                    @Override // java.util.Iterator
                    public boolean hasNext() {
                        return nodeList != null && nodeList.getLength() > this.index;
                    }

                    /* JADX WARN: Can't rename method to resolve collision */
                    @Override // java.util.Iterator
                    public Node next() {
                        NodeList nodeList2 = nodeList;
                        int i = this.index;
                        this.index = i + 1;
                        return nodeList2.item(i);
                    }

                    @Override // java.util.Iterator
                    public void remove() {
                        throw new UnsupportedOperationException("Method remove not supported");
                    }
                };
            }
        };
    }
}
