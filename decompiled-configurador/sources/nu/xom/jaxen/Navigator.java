package nu.xom.jaxen;

import java.io.Serializable;
import java.util.Iterator;
import nu.xom.jaxen.saxpath.SAXPathException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/Navigator.class */
public interface Navigator extends Serializable {
    Iterator getChildAxisIterator(Object obj) throws UnsupportedAxisException;

    Iterator getDescendantAxisIterator(Object obj) throws UnsupportedAxisException;

    Iterator getParentAxisIterator(Object obj) throws UnsupportedAxisException;

    Iterator getAncestorAxisIterator(Object obj) throws UnsupportedAxisException;

    Iterator getFollowingSiblingAxisIterator(Object obj) throws UnsupportedAxisException;

    Iterator getPrecedingSiblingAxisIterator(Object obj) throws UnsupportedAxisException;

    Iterator getFollowingAxisIterator(Object obj) throws UnsupportedAxisException;

    Iterator getPrecedingAxisIterator(Object obj) throws UnsupportedAxisException;

    Iterator getAttributeAxisIterator(Object obj) throws UnsupportedAxisException;

    Iterator getNamespaceAxisIterator(Object obj) throws UnsupportedAxisException;

    Iterator getSelfAxisIterator(Object obj) throws UnsupportedAxisException;

    Iterator getDescendantOrSelfAxisIterator(Object obj) throws UnsupportedAxisException;

    Iterator getAncestorOrSelfAxisIterator(Object obj) throws UnsupportedAxisException;

    Object getDocument(String str) throws FunctionCallException;

    Object getDocumentNode(Object obj);

    Object getParentNode(Object obj) throws UnsupportedAxisException;

    String getElementNamespaceUri(Object obj);

    String getElementName(Object obj);

    String getElementQName(Object obj);

    String getAttributeNamespaceUri(Object obj);

    String getAttributeName(Object obj);

    String getAttributeQName(Object obj);

    String getProcessingInstructionTarget(Object obj);

    String getProcessingInstructionData(Object obj);

    boolean isDocument(Object obj);

    boolean isElement(Object obj);

    boolean isAttribute(Object obj);

    boolean isNamespace(Object obj);

    boolean isComment(Object obj);

    boolean isText(Object obj);

    boolean isProcessingInstruction(Object obj);

    String getCommentStringValue(Object obj);

    String getElementStringValue(Object obj);

    String getAttributeStringValue(Object obj);

    String getNamespaceStringValue(Object obj);

    String getTextStringValue(Object obj);

    String getNamespacePrefix(Object obj);

    String translateNamespacePrefixToUri(String str, Object obj);

    XPath parseXPath(String str) throws SAXPathException;

    Object getElementById(Object obj, String str);

    short getNodeType(Object obj);
}
