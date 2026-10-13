package org.apache.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilterParameterSpec;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/jcp/xml/dsig/internal/dom/DOMXPathTransform.class */
public final class DOMXPathTransform extends ApacheTransform {
    public void init(TransformParameterSpec params) throws InvalidAlgorithmParameterException {
        if (params == null) {
            throw new InvalidAlgorithmParameterException("params are required");
        }
        if (!(params instanceof XPathFilterParameterSpec)) {
            throw new InvalidAlgorithmParameterException("params must be of type XPathFilterParameterSpec");
        }
        this.params = params;
    }

    @Override // org.apache.jcp.xml.dsig.internal.dom.ApacheTransform
    public void init(XMLStructure parent, XMLCryptoContext context) throws InvalidAlgorithmParameterException {
        super.init(parent, context);
        unmarshalParams(DOMUtils.getFirstChildElement(this.transformElem));
    }

    private void unmarshalParams(Element paramsElem) {
        String xPath = paramsElem.getFirstChild().getNodeValue();
        NamedNodeMap attributes = paramsElem.getAttributes();
        if (attributes != null) {
            int length = attributes.getLength();
            Map<String, String> namespaceMap = new HashMap<>((int) Math.ceil(((double) length) / 0.75d));
            for (int i = 0; i < length; i++) {
                Attr attr = (Attr) attributes.item(i);
                String prefix = attr.getPrefix();
                if (prefix != null && CanonicalizerBase.XMLNS.equals(prefix)) {
                    namespaceMap.put(attr.getLocalName(), attr.getValue());
                }
            }
            this.params = new XPathFilterParameterSpec(xPath, namespaceMap);
            return;
        }
        this.params = new XPathFilterParameterSpec(xPath);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.ApacheTransform
    public void marshalParams(XMLStructure parent, XMLCryptoContext context) throws MarshalException {
        super.marshalParams(parent, context);
        XPathFilterParameterSpec xp = getParameterSpec();
        Element xpathElem = DOMUtils.createElement(this.ownerDoc, "XPath", "http://www.w3.org/2000/09/xmldsig#", DOMUtils.getSignaturePrefix(context));
        xpathElem.appendChild(this.ownerDoc.createTextNode(xp.getXPath()));
        Set<Map.Entry<String, String>> entries = xp.getNamespaceMap().entrySet();
        for (Map.Entry<String, String> entry : entries) {
            xpathElem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + entry.getKey(), entry.getValue());
        }
        this.transformElem.appendChild(xpathElem);
    }
}
