package org.apache.xml.security.utils;

import com.aowagie.text.xml.XMLConstants;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/utils/JDKXPathAPI.class */
class JDKXPathAPI implements XPathAPI {
    private javax.xml.xpath.XPathFactory xpf;
    private String xpathStr;
    private XPathExpression xpathExpression;

    JDKXPathAPI() {
    }

    @Override // org.apache.xml.security.utils.XPathAPI
    public NodeList selectNodeList(Node contextNode, Node xpathnode, String str, Node namespaceNode) throws TransformerException {
        if (!str.equals(this.xpathStr) || this.xpathExpression == null) {
            if (this.xpf == null) {
                this.xpf = javax.xml.xpath.XPathFactory.newInstance();
                try {
                    this.xpf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE.booleanValue());
                } catch (XPathFactoryConfigurationException ex) {
                    throw new TransformerException(ex);
                }
            }
            XPath xpath = this.xpf.newXPath();
            xpath.setNamespaceContext(new DOMNamespaceContext(namespaceNode));
            this.xpathStr = str;
            try {
                this.xpathExpression = xpath.compile(this.xpathStr);
            } catch (XPathExpressionException ex2) {
                throw new TransformerException(ex2);
            }
        }
        try {
            return (NodeList) this.xpathExpression.evaluate(contextNode, XPathConstants.NODESET);
        } catch (XPathExpressionException ex3) {
            throw new TransformerException(ex3);
        }
    }

    @Override // org.apache.xml.security.utils.XPathAPI
    public boolean evaluate(Node contextNode, Node xpathnode, String str, Node namespaceNode) throws TransformerException {
        if (!str.equals(this.xpathStr) || this.xpathExpression == null) {
            if (this.xpf == null) {
                this.xpf = javax.xml.xpath.XPathFactory.newInstance();
                try {
                    this.xpf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE.booleanValue());
                } catch (XPathFactoryConfigurationException ex) {
                    throw new TransformerException(ex);
                }
            }
            XPath xpath = this.xpf.newXPath();
            xpath.setNamespaceContext(new DOMNamespaceContext(namespaceNode));
            this.xpathStr = str;
            try {
                this.xpathExpression = xpath.compile(this.xpathStr);
            } catch (XPathExpressionException ex2) {
                throw new TransformerException(ex2);
            }
        }
        try {
            return ((Boolean) this.xpathExpression.evaluate(contextNode, XPathConstants.BOOLEAN)).booleanValue();
        } catch (XPathExpressionException ex3) {
            throw new TransformerException(ex3);
        }
    }

    @Override // org.apache.xml.security.utils.XPathAPI
    public void clear() {
        this.xpathStr = null;
        this.xpathExpression = null;
        this.xpf = null;
    }
}
