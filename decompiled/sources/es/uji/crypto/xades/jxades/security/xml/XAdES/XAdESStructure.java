package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.uji.crypto.xades.jxades.util.XMLUtils;
import java.util.List;
import javax.xml.crypto.dom.DOMStructure;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/XAdESStructure.class */
public class XAdESStructure extends DOMStructure {
    public static final String SIGNATURE_ELEMENT_NAME = "Signature";
    public static final String ID_ATTRIBUTE = "Id";
    public static final String TARGET_ATTRIBUTE = "Target";
    private Document baseDocument;
    public String xadesPrefix;
    public String xadesNamespace;
    public String xmlSignaturePrefix;

    public XAdESStructure(Document document, XAdESStructure parent, String elementName, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        this(document, parent.getElement(), elementName, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public XAdESStructure(Document document, Element parentElement, String elementName, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        this(document.createElementNS(xadesNamespace, elementName), xadesPrefix, xadesNamespace, xmlSignaturePrefix);
        this.baseDocument = document;
        this.xadesPrefix = xadesPrefix;
        this.xadesNamespace = xadesNamespace;
        this.xmlSignaturePrefix = xmlSignaturePrefix;
        Element element = getElement();
        element.setPrefix(xadesPrefix);
        parentElement.appendChild(element);
    }

    public XAdESStructure(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node);
        this.xadesPrefix = xadesPrefix;
        this.xadesNamespace = xadesNamespace;
        this.xmlSignaturePrefix = xmlSignaturePrefix;
    }

    public Element getElement() {
        return (Element) getNode();
    }

    public String getId() {
        return getAttribute("Id");
    }

    protected void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        getElement().setAttributeNS(namespaceURI, qualifiedName, value);
        if ("Id".equals(qualifiedName)) {
            getElement().setIdAttributeNS(null, "Id", true);
        }
    }

    protected String getAttribute(String name) {
        return getElement().getAttribute(name);
    }

    protected String getAttributeNS(String namespaceURI, String qualifiedName) {
        return getElement().getAttributeNS(namespaceURI, qualifiedName);
    }

    protected String getTextContent() {
        return getElement().getTextContent();
    }

    protected void setTextContent(String textContent) {
        getElement().setTextContent(textContent);
    }

    protected Element getChildElement(String elementName) {
        return XMLUtils.getChildElementByTagName(getElement(), elementName);
    }

    protected Element getChildElementNS(String elementName) {
        return XMLUtils.getChildElementByTagNameNS(getElement(), elementName, this.xadesNamespace);
    }

    protected Element getChildElementNS(String elementName, String namespace) {
        return XMLUtils.getChildElementByTagNameNS(getElement(), elementName, namespace);
    }

    protected List<Element> getChildElements(String elementName) {
        return XMLUtils.getChildElementsByTagName(getElement(), elementName);
    }

    protected List<Element> getChildElementsNS(String elementName) {
        return XMLUtils.getChildElementsByTagNameNS(getElement(), elementName, this.xadesNamespace);
    }

    protected Document getDocument() {
        return this.baseDocument;
    }

    protected Element createElement(String elementName) {
        Element element = getDocument().createElementNS(this.xadesNamespace, elementName);
        element.setPrefix(this.xadesPrefix);
        return element;
    }

    protected Element createElementNS(String namespace, String prefix, String elementName) {
        Element element = getDocument().createElementNS(namespace, elementName);
        element.setPrefix(prefix);
        return element;
    }

    protected String getChildElementTextContent(String elementName) {
        Element element = getChildElementNS(elementName);
        if (element != null) {
            return element.getTextContent();
        }
        return null;
    }
}
