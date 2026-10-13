package org.apache.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilter2ParameterSpec;
import javax.xml.crypto.dsig.spec.XPathType;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.apache.xml.security.transforms.params.XPath2FilterContainer;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/jcp/xml/dsig/internal/dom/DOMXPathFilter2Transform.class */
public final class DOMXPathFilter2Transform extends ApacheTransform {
    public void init(TransformParameterSpec params) throws InvalidAlgorithmParameterException {
        if (params == null) {
            throw new InvalidAlgorithmParameterException("params are required");
        }
        if (!(params instanceof XPathFilter2ParameterSpec)) {
            throw new InvalidAlgorithmParameterException("params must be of type XPathFilter2ParameterSpec");
        }
        this.params = params;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.ApacheTransform
    public void init(XMLStructure parent, XMLCryptoContext context) throws InvalidAlgorithmParameterException {
        super.init(parent, context);
        try {
            unmarshalParams(DOMUtils.getFirstChildElement(this.transformElem));
        } catch (MarshalException me) {
            throw new InvalidAlgorithmParameterException((Throwable) me);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    private void unmarshalParams(Element curXPathElem) throws MarshalException {
        XPathType.Filter filter;
        List<XPathType> list = new ArrayList<>();
        Element nextSiblingElement = curXPathElem;
        while (true) {
            Element currentElement = nextSiblingElement;
            if (currentElement != null) {
                String xPath = currentElement.getFirstChild().getNodeValue();
                String filterVal = DOMUtils.getAttributeValue(currentElement, "Filter");
                if (filterVal == null) {
                    throw new MarshalException("filter cannot be null");
                }
                if (XPath2FilterContainer.INTERSECT.equals(filterVal)) {
                    filter = XPathType.Filter.INTERSECT;
                } else if (XPath2FilterContainer.SUBTRACT.equals(filterVal)) {
                    filter = XPathType.Filter.SUBTRACT;
                } else if (XPath2FilterContainer.UNION.equals(filterVal)) {
                    filter = XPathType.Filter.UNION;
                } else {
                    throw new MarshalException("Unknown XPathType filter type" + filterVal);
                }
                NamedNodeMap attributes = currentElement.getAttributes();
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
                    list.add(new XPathType(xPath, filter, namespaceMap));
                } else {
                    list.add(new XPathType(xPath, filter));
                }
                nextSiblingElement = DOMUtils.getNextSiblingElement(currentElement);
            } else {
                this.params = new XPathFilter2ParameterSpec(list);
                return;
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.ApacheTransform
    public void marshalParams(XMLStructure parent, XMLCryptoContext context) throws MarshalException {
        super.marshalParams(parent, context);
        XPathFilter2ParameterSpec xp = getParameterSpec();
        String prefix = DOMUtils.getNSPrefix(context, "http://www.w3.org/2002/06/xmldsig-filter2");
        String qname = (prefix == null || prefix.length() == 0) ? CanonicalizerBase.XMLNS : "xmlns:" + prefix;
        List<XPathType> xpathList = xp.getXPathList();
        for (XPathType xpathType : xpathList) {
            Element elem = DOMUtils.createElement(this.ownerDoc, "XPath", "http://www.w3.org/2002/06/xmldsig-filter2", prefix);
            elem.appendChild(this.ownerDoc.createTextNode(xpathType.getExpression()));
            DOMUtils.setAttribute(elem, "Filter", xpathType.getFilter().toString());
            elem.setAttributeNS("http://www.w3.org/2000/xmlns/", qname, "http://www.w3.org/2002/06/xmldsig-filter2");
            Set<Map.Entry<String, String>> entries = xpathType.getNamespaceMap().entrySet();
            for (Map.Entry<String, String> entry : entries) {
                elem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + entry.getKey(), entry.getValue());
            }
            this.transformElem.appendChild(elem);
        }
    }
}
