package org.apache.xml.security.transforms.implementations;

import java.io.IOException;
import java.io.OutputStream;
import javax.xml.transform.TransformerException;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.signature.NodeFilter;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformSpi;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.JDKXPathFactory;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.XPathAPI;
import org.apache.xml.security.utils.XPathFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/transforms/implementations/TransformXPath.class */
public class TransformXPath extends TransformSpi {
    @Override // org.apache.xml.security.transforms.TransformSpi
    protected String engineGetURI() {
        return Transforms.TRANSFORM_XPATH;
    }

    @Override // org.apache.xml.security.transforms.TransformSpi
    protected XMLSignatureInput enginePerformTransform(XMLSignatureInput input, OutputStream os, Element transformElement, String baseURI, boolean secureValidation) throws TransformationException {
        try {
            Element xpathElement = XMLUtils.selectDsNode(transformElement.getFirstChild(), "XPath", 0);
            if (xpathElement == null) {
                Object[] exArgs = {"ds:XPath", Constants._TAG_TRANSFORM};
                throw new TransformationException("xml.WrongContent", exArgs);
            }
            Node xpathnode = xpathElement.getFirstChild();
            if (xpathnode == null) {
                throw new DOMException((short) 3, "Text must be in ds:Xpath");
            }
            String str = XMLUtils.getStrFromNode(xpathnode);
            input.setNeedsToBeExpanded(needsCircumvent(str));
            XPathFactory xpathFactory = getXPathFactory();
            XPathAPI xpathAPIInstance = xpathFactory.newXPathAPI();
            input.addNodeFilter(new XPathNodeFilter(xpathElement, xpathnode, str, xpathAPIInstance));
            input.setNodeSet(true);
            return input;
        } catch (IOException | XMLParserException | DOMException ex) {
            throw new TransformationException(ex);
        }
    }

    protected XPathFactory getXPathFactory() {
        return new JDKXPathFactory();
    }

    private boolean needsCircumvent(String str) {
        return (str.indexOf("namespace") == -1 && str.indexOf("name()") == -1) ? false : true;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/transforms/implementations/TransformXPath$XPathNodeFilter.class */
    private static class XPathNodeFilter implements NodeFilter {
        private final XPathAPI xPathAPI;
        private final Node xpathnode;
        private final Element xpathElement;
        private final String str;

        XPathNodeFilter(Element xpathElement, Node xpathnode, String str, XPathAPI xPathAPI) {
            this.xpathnode = xpathnode;
            this.str = str;
            this.xpathElement = xpathElement;
            this.xPathAPI = xPathAPI;
        }

        @Override // org.apache.xml.security.signature.NodeFilter
        public int isNodeInclude(Node currentNode) throws TransformationException {
            try {
                boolean include = this.xPathAPI.evaluate(currentNode, this.xpathnode, this.str, this.xpathElement);
                if (include) {
                    return 1;
                }
                return 0;
            } catch (TransformerException ex) {
                throw new TransformationException(ex);
            }
        }

        @Override // org.apache.xml.security.signature.NodeFilter
        public int isNodeIncludeDO(Node n, int level) throws TransformationException {
            return isNodeInclude(n);
        }
    }
}
