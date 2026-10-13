package org.apache.xml.security.transforms.implementations;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.TransformerException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.params.XPath2FilterContainer;
import org.apache.xml.security.utils.JDKXPathFactory;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.XPathAPI;
import org.apache.xml.security.utils.XPathFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/transforms/implementations/TransformXPath2Filter.class */
public class TransformXPath2Filter extends TransformSpi {
    @Override // org.apache.xml.security.transforms.TransformSpi
    protected String engineGetURI() {
        return "http://www.w3.org/2002/06/xmldsig-filter2";
    }

    @Override // org.apache.xml.security.transforms.TransformSpi
    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input, OutputStream os, Element transformElement, String baseURI, boolean secureValidation) throws TransformationException {
        Document inputDoc;
        try {
            List<NodeList> unionNodes = new ArrayList<>();
            List<NodeList> subtractNodes = new ArrayList<>();
            List<NodeList> intersectNodes = new ArrayList<>();
            Element[] xpathElements = XMLUtils.selectNodes(transformElement.getFirstChild(), "http://www.w3.org/2002/06/xmldsig-filter2", "XPath");
            if (xpathElements.length == 0) {
                Object[] exArgs = {"http://www.w3.org/2002/06/xmldsig-filter2", "XPath"};
                throw new TransformationException("xml.WrongContent", exArgs);
            }
            if (input.getSubNode() != null) {
                inputDoc = XMLUtils.getOwnerDocument(input.getSubNode());
            } else {
                inputDoc = XMLUtils.getOwnerDocument(input.getNodeSet());
            }
            XPathFactory xpathFactory = getXPathFactory();
            for (Element xpathElement : xpathElements) {
                XPath2FilterContainer xpathContainer = XPath2FilterContainer.newInstance(xpathElement, input.getSourceURI());
                String str = XMLUtils.getStrFromNode(xpathContainer.getXPathFilterTextNode());
                XPathAPI xpathAPIInstance = xpathFactory.newXPathAPI();
                NodeList subtreeRoots = xpathAPIInstance.selectNodeList(inputDoc, xpathContainer.getXPathFilterTextNode(), str, xpathContainer.getElement());
                if (xpathContainer.isIntersect()) {
                    intersectNodes.add(subtreeRoots);
                } else if (xpathContainer.isSubtract()) {
                    subtractNodes.add(subtreeRoots);
                } else if (xpathContainer.isUnion()) {
                    unionNodes.add(subtreeRoots);
                }
            }
            input.addNodeFilter(new XPath2NodeFilter(unionNodes, subtractNodes, intersectNodes));
            input.setNodeSet(true);
            return input;
        } catch (IOException | TransformerException | XMLSecurityException | DOMException ex) {
            throw new TransformationException(ex);
        }
    }

    protected XPathFactory getXPathFactory() {
        return new JDKXPathFactory();
    }
}
