package org.apache.xml.security.transforms.params;

import org.apache.xml.security.transforms.TransformParam;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/transforms/params/XPathContainer.class */
public class XPathContainer extends SignatureElementProxy implements TransformParam {
    public XPathContainer(Document doc) {
        super(doc);
    }

    public void setXPath(String xpath) {
        Node childNode = getElement().getFirstChild();
        while (childNode != null) {
            Node nodeToBeRemoved = childNode;
            childNode = childNode.getNextSibling();
            getElement().removeChild(nodeToBeRemoved);
        }
        Text xpathText = createText(xpath);
        appendSelf(xpathText);
    }

    public String getXPath() {
        return getTextFromTextChild();
    }

    @Override // org.apache.xml.security.utils.ElementProxy
    public String getBaseLocalName() {
        return "XPath";
    }
}
