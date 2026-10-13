package nu.xom.jaxen;

import java.util.Iterator;
import nu.xom.jaxen.util.AncestorAxisIterator;
import nu.xom.jaxen.util.AncestorOrSelfAxisIterator;
import nu.xom.jaxen.util.DescendantAxisIterator;
import nu.xom.jaxen.util.DescendantOrSelfAxisIterator;
import nu.xom.jaxen.util.FollowingAxisIterator;
import nu.xom.jaxen.util.FollowingSiblingAxisIterator;
import nu.xom.jaxen.util.PrecedingAxisIterator;
import nu.xom.jaxen.util.PrecedingSiblingAxisIterator;
import nu.xom.jaxen.util.SelfAxisIterator;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/DefaultNavigator.class */
public abstract class DefaultNavigator implements Navigator {
    @Override // nu.xom.jaxen.Navigator
    public Iterator getChildAxisIterator(Object obj) throws UnsupportedAxisException {
        throw new UnsupportedAxisException("child");
    }

    @Override // nu.xom.jaxen.Navigator
    public Iterator getDescendantAxisIterator(Object obj) throws UnsupportedAxisException {
        return new DescendantAxisIterator(obj, this);
    }

    @Override // nu.xom.jaxen.Navigator
    public Iterator getParentAxisIterator(Object obj) throws UnsupportedAxisException {
        throw new UnsupportedAxisException("parent");
    }

    @Override // nu.xom.jaxen.Navigator
    public Iterator getAncestorAxisIterator(Object obj) throws UnsupportedAxisException {
        return new AncestorAxisIterator(obj, this);
    }

    @Override // nu.xom.jaxen.Navigator
    public Iterator getFollowingSiblingAxisIterator(Object obj) throws UnsupportedAxisException {
        return new FollowingSiblingAxisIterator(obj, this);
    }

    @Override // nu.xom.jaxen.Navigator
    public Iterator getPrecedingSiblingAxisIterator(Object obj) throws UnsupportedAxisException {
        return new PrecedingSiblingAxisIterator(obj, this);
    }

    @Override // nu.xom.jaxen.Navigator
    public Iterator getFollowingAxisIterator(Object obj) throws UnsupportedAxisException {
        return new FollowingAxisIterator(obj, this);
    }

    @Override // nu.xom.jaxen.Navigator
    public Iterator getPrecedingAxisIterator(Object obj) throws UnsupportedAxisException {
        return new PrecedingAxisIterator(obj, this);
    }

    @Override // nu.xom.jaxen.Navigator
    public Iterator getAttributeAxisIterator(Object obj) throws UnsupportedAxisException {
        throw new UnsupportedAxisException("attribute");
    }

    @Override // nu.xom.jaxen.Navigator
    public Iterator getNamespaceAxisIterator(Object obj) throws UnsupportedAxisException {
        throw new UnsupportedAxisException("namespace");
    }

    @Override // nu.xom.jaxen.Navigator
    public Iterator getSelfAxisIterator(Object obj) throws UnsupportedAxisException {
        return new SelfAxisIterator(obj);
    }

    @Override // nu.xom.jaxen.Navigator
    public Iterator getDescendantOrSelfAxisIterator(Object obj) throws UnsupportedAxisException {
        return new DescendantOrSelfAxisIterator(obj, this);
    }

    @Override // nu.xom.jaxen.Navigator
    public Iterator getAncestorOrSelfAxisIterator(Object obj) throws UnsupportedAxisException {
        return new AncestorOrSelfAxisIterator(obj, this);
    }

    @Override // nu.xom.jaxen.Navigator
    public Object getDocumentNode(Object obj) {
        return null;
    }

    @Override // nu.xom.jaxen.Navigator
    public String translateNamespacePrefixToUri(String str, Object obj) {
        return null;
    }

    @Override // nu.xom.jaxen.Navigator
    public String getProcessingInstructionTarget(Object obj) {
        return null;
    }

    @Override // nu.xom.jaxen.Navigator
    public String getProcessingInstructionData(Object obj) {
        return null;
    }

    @Override // nu.xom.jaxen.Navigator
    public short getNodeType(Object obj) {
        if (isElement(obj)) {
            return (short) 1;
        }
        if (isAttribute(obj)) {
            return (short) 2;
        }
        if (isText(obj)) {
            return (short) 3;
        }
        if (isComment(obj)) {
            return (short) 8;
        }
        if (isDocument(obj)) {
            return (short) 9;
        }
        if (isProcessingInstruction(obj)) {
            return (short) 7;
        }
        return isNamespace(obj) ? (short) 13 : (short) 14;
    }

    @Override // nu.xom.jaxen.Navigator
    public Object getParentNode(Object obj) throws UnsupportedAxisException {
        Iterator parentAxisIterator = getParentAxisIterator(obj);
        if (parentAxisIterator == null || !parentAxisIterator.hasNext()) {
            return null;
        }
        return parentAxisIterator.next();
    }

    @Override // nu.xom.jaxen.Navigator
    public Object getDocument(String str) throws FunctionCallException {
        return null;
    }

    @Override // nu.xom.jaxen.Navigator
    public Object getElementById(Object obj, String str) {
        return null;
    }
}
