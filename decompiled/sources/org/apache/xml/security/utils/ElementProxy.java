package org.apache.xml.security.utils;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/utils/ElementProxy.class */
public abstract class ElementProxy {
    private Element wrappedElement;
    protected String baseURI;
    private Document wrappedDoc;
    protected static final Logger LOG = LoggerFactory.getLogger((Class<?>) ElementProxy.class);
    private static Map<String, String> prefixMappings = new ConcurrentHashMap();

    public abstract String getBaseNamespace();

    public abstract String getBaseLocalName();

    public ElementProxy() {
    }

    public ElementProxy(Document doc) {
        if (doc == null) {
            throw new RuntimeException("Document is null");
        }
        this.wrappedDoc = doc;
        this.wrappedElement = createElementForFamilyLocal(getBaseNamespace(), getBaseLocalName());
    }

    public ElementProxy(Element element, String baseURI) throws XMLSecurityException {
        if (element == null) {
            throw new XMLSecurityException("ElementProxy.nullElement");
        }
        LOG.debug("setElement(\"{}\", \"{}\")", element.getTagName(), baseURI);
        setElement(element);
        this.baseURI = baseURI;
        guaranteeThatElementInCorrectSpace();
    }

    protected Element createElementForFamilyLocal(String namespace, String localName) {
        Element result;
        Document doc = getDocument();
        if (namespace == null) {
            result = doc.createElementNS(null, localName);
        } else {
            String baseName = getBaseNamespace();
            String prefix = getDefaultPrefix(baseName);
            if (prefix == null || prefix.length() == 0) {
                result = doc.createElementNS(namespace, localName);
                result.setAttributeNS("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS, namespace);
            } else {
                result = doc.createElementNS(namespace, prefix + ":" + localName);
                result.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, namespace);
            }
        }
        return result;
    }

    public static Element createElementForFamily(Document doc, String namespace, String localName) {
        Element result;
        String prefix = getDefaultPrefix(namespace);
        if (namespace == null) {
            result = doc.createElementNS(null, localName);
        } else if (prefix == null || prefix.length() == 0) {
            result = doc.createElementNS(namespace, localName);
            result.setAttributeNS("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS, namespace);
        } else {
            result = doc.createElementNS(namespace, prefix + ":" + localName);
            result.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, namespace);
        }
        return result;
    }

    public void setElement(Element element, String baseURI) throws XMLSecurityException {
        if (element == null) {
            throw new XMLSecurityException("ElementProxy.nullElement");
        }
        LOG.debug("setElement({}, \"{}\")", element.getTagName(), baseURI);
        setElement(element);
        this.baseURI = baseURI;
    }

    public final Element getElement() {
        return this.wrappedElement;
    }

    public final NodeList getElementPlusReturns() {
        HelperNodeList nl = new HelperNodeList();
        nl.appendChild(createText("\n"));
        nl.appendChild(getElement());
        nl.appendChild(createText("\n"));
        return nl;
    }

    protected Text createText(String text) {
        return getDocument().createTextNode(text);
    }

    public Document getDocument() {
        if (this.wrappedDoc == null) {
            this.wrappedDoc = XMLUtils.getOwnerDocument(this.wrappedElement);
        }
        return this.wrappedDoc;
    }

    public String getBaseURI() {
        return this.baseURI;
    }

    void guaranteeThatElementInCorrectSpace() throws XMLSecurityException {
        String expectedLocalName = getBaseLocalName();
        String expectedNamespaceUri = getBaseNamespace();
        String actualLocalName = getElement().getLocalName();
        String actualNamespaceUri = getElement().getNamespaceURI();
        if (!expectedNamespaceUri.equals(actualNamespaceUri) && !expectedLocalName.equals(actualLocalName)) {
            Object[] exArgs = {actualNamespaceUri + ":" + actualLocalName, expectedNamespaceUri + ":" + expectedLocalName};
            throw new XMLSecurityException("xml.WrongElement", exArgs);
        }
    }

    public void addBigIntegerElement(BigInteger bi, String localname) {
        if (bi != null) {
            Element e = XMLUtils.createElementInSignatureSpace(getDocument(), localname);
            byte[] bytes = XMLUtils.getBytes(bi, bi.bitLength());
            String encodedInt = XMLUtils.encodeToString(bytes);
            Document doc = e.getOwnerDocument();
            Text text = doc.createTextNode(encodedInt);
            e.appendChild(text);
            appendSelf(e);
            addReturnToSelf();
        }
    }

    protected void addReturnToSelf() {
        XMLUtils.addReturnToElement(getElement());
    }

    public void addBase64Element(byte[] bytes, String localname) {
        if (bytes != null) {
            addTextElement(XMLUtils.encodeToString(bytes), localname);
        }
    }

    public void addTextElement(String text, String localname) {
        Element e = XMLUtils.createElementInSignatureSpace(getDocument(), localname);
        Text t = createText(text);
        appendOther(e, t);
        appendSelf(e);
        addReturnToSelf();
    }

    public void addBase64Text(byte[] bytes) {
        Text textCreateText;
        if (bytes != null) {
            if (XMLUtils.ignoreLineBreaks()) {
                textCreateText = createText(XMLUtils.encodeToString(bytes));
            } else {
                textCreateText = createText("\n" + XMLUtils.encodeToString(bytes) + "\n");
            }
            Text t = textCreateText;
            appendSelf(t);
        }
    }

    protected void appendSelf(ElementProxy toAppend) {
        getElement().appendChild(toAppend.getElement());
    }

    protected void appendSelf(Node toAppend) {
        getElement().appendChild(toAppend);
    }

    protected void appendOther(Element parent, Node toAppend) {
        parent.appendChild(toAppend);
    }

    public void addText(String text) {
        if (text != null) {
            Text t = createText(text);
            appendSelf(t);
        }
    }

    public BigInteger getBigIntegerFromChildElement(String localname, String namespace) {
        Node n = XMLUtils.selectNode(getFirstChild(), namespace, localname, 0);
        if (n != null) {
            return new BigInteger(1, XMLUtils.decode(XMLUtils.getFullTextChildrenFromNode(n)));
        }
        return null;
    }

    public String getTextFromChildElement(String localname, String namespace) {
        return XMLUtils.selectNode(getFirstChild(), namespace, localname, 0).getTextContent();
    }

    public byte[] getBytesFromTextChild() throws XMLSecurityException {
        return XMLUtils.decode(getTextFromTextChild());
    }

    public String getTextFromTextChild() {
        return XMLUtils.getFullTextChildrenFromNode(getElement());
    }

    public int length(String namespace, String localname) {
        int number = 0;
        Node firstChild = getFirstChild();
        while (true) {
            Node sibling = firstChild;
            if (sibling != null) {
                if (localname.equals(sibling.getLocalName()) && namespace.equals(sibling.getNamespaceURI())) {
                    number++;
                }
                firstChild = sibling.getNextSibling();
            } else {
                return number;
            }
        }
    }

    public void setXPathNamespaceContext(String prefix, String uri) throws XMLSecurityException {
        String ns;
        if (prefix == null || prefix.length() == 0) {
            throw new XMLSecurityException("defaultNamespaceCannotBeSetHere");
        }
        if (CanonicalizerBase.XMLNS.equals(prefix)) {
            throw new XMLSecurityException("defaultNamespaceCannotBeSetHere");
        }
        if (prefix.startsWith("xmlns:")) {
            ns = prefix;
        } else {
            ns = "xmlns:" + prefix;
        }
        Attr a = getElement().getAttributeNodeNS("http://www.w3.org/2000/xmlns/", ns);
        if (a != null) {
            if (!a.getNodeValue().equals(uri)) {
                Object[] exArgs = {ns, getElement().getAttributeNS(null, ns)};
                throw new XMLSecurityException("namespacePrefixAlreadyUsedByOtherURI", exArgs);
            }
            return;
        }
        getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", ns, uri);
    }

    public static void setDefaultPrefix(String namespace, String prefix) throws XMLSecurityException {
        JavaUtils.checkRegisterPermission();
        setNamespacePrefix(namespace, prefix);
    }

    private static void setNamespacePrefix(String namespace, String prefix) throws XMLSecurityException {
        if (prefixMappings.containsValue(prefix)) {
            String storedPrefix = prefixMappings.get(namespace);
            if (!storedPrefix.equals(prefix)) {
                Object[] exArgs = {prefix, namespace, storedPrefix};
                throw new XMLSecurityException("prefix.AlreadyAssigned", exArgs);
            }
        }
        if ("http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
            XMLUtils.setDsPrefix(prefix);
        } else if ("http://www.w3.org/2009/xmldsig11#".equals(namespace)) {
            XMLUtils.setDs11Prefix(prefix);
        } else if ("http://www.w3.org/2001/04/xmlenc#".equals(namespace)) {
            XMLUtils.setXencPrefix(prefix);
        }
        prefixMappings.put(namespace, prefix);
    }

    public static void registerDefaultPrefixes() throws XMLSecurityException {
        setNamespacePrefix("http://www.w3.org/2000/09/xmldsig#", "ds");
        setNamespacePrefix("http://www.w3.org/2001/04/xmlenc#", XMLSecurityConstants.PREFIX_XENC);
        setNamespacePrefix("http://www.w3.org/2009/xmlenc11#", XMLSecurityConstants.PREFIX_XENC11);
        setNamespacePrefix("http://www.xmlsecurity.org/experimental#", "experimental");
        setNamespacePrefix("http://www.w3.org/2002/04/xmldsig-filter2", "dsig-xpath-old");
        setNamespacePrefix("http://www.w3.org/2002/06/xmldsig-filter2", "dsig-xpath");
        setNamespacePrefix("http://www.w3.org/2001/10/xml-exc-c14n#", "ec");
        setNamespacePrefix("http://www.nue.et-inf.uni-siegen.de/~geuer-pollmann/#xpathFilter", "xx");
        setNamespacePrefix("http://www.w3.org/2009/xmldsig11#", XMLSecurityConstants.PREFIX_DSIG11);
        setNamespacePrefix("http://www.w3.org/2001/04/xmldsig-more", "rfc4051");
        setNamespacePrefix("http://www.w3.org/2007/05/xmldsig-more#", "rfc6931");
        setNamespacePrefix(Constants.XML_DSIG_NS_MORE_21_04, "rfc9231");
    }

    public static String getDefaultPrefix(String namespace) {
        return prefixMappings.get(namespace);
    }

    protected void setElement(Element elem) {
        this.wrappedElement = elem;
    }

    protected void setDocument(Document doc) {
        this.wrappedDoc = doc;
    }

    protected String getLocalAttribute(String attrName) {
        return getElement().getAttributeNS(null, attrName);
    }

    protected void setLocalAttribute(String attrName, String value) {
        getElement().setAttributeNS(null, attrName, value);
    }

    protected void setLocalIdAttribute(String attrName, String value) {
        if (value != null) {
            Attr attr = getDocument().createAttributeNS(null, attrName);
            attr.setValue(value);
            getElement().setAttributeNodeNS(attr);
            getElement().setIdAttributeNode(attr, true);
            return;
        }
        getElement().removeAttributeNS(null, attrName);
    }

    protected Node getFirstChild() {
        return getElement().getFirstChild();
    }
}
