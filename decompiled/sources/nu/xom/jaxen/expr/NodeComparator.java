package nu.xom.jaxen.expr;

import java.util.Comparator;
import java.util.Iterator;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.UnsupportedAxisException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/NodeComparator.class */
class NodeComparator implements Comparator {
    private Navigator navigator;

    NodeComparator(Navigator navigator) {
        this.navigator = navigator;
    }

    @Override // java.util.Comparator
    public int compare(Object obj, Object obj2) {
        if (obj == obj2 || this.navigator == null) {
            return 0;
        }
        if (isNonChild(obj) && isNonChild(obj2)) {
            try {
                Object parentNode = this.navigator.getParentNode(obj);
                Object parentNode2 = this.navigator.getParentNode(obj2);
                if (parentNode == parentNode2) {
                    if (this.navigator.isNamespace(obj) && this.navigator.isAttribute(obj2)) {
                        return -1;
                    }
                    if (this.navigator.isNamespace(obj2) && this.navigator.isAttribute(obj)) {
                        return 1;
                    }
                    if (this.navigator.isNamespace(obj)) {
                        return this.navigator.getNamespacePrefix(obj).compareTo(this.navigator.getNamespacePrefix(obj2));
                    }
                    if (this.navigator.isAttribute(obj)) {
                        return this.navigator.getAttributeQName(obj).compareTo(this.navigator.getAttributeQName(obj2));
                    }
                }
                return compare(parentNode, parentNode2);
            } catch (UnsupportedAxisException e) {
                return 0;
            }
        }
        try {
            int depth = getDepth(obj);
            int depth2 = getDepth(obj2);
            Object parentNode3 = obj;
            Object parentNode4 = obj2;
            while (depth > depth2) {
                parentNode3 = this.navigator.getParentNode(parentNode3);
                depth--;
            }
            if (parentNode3 == obj2) {
                return 1;
            }
            while (depth2 > depth) {
                parentNode4 = this.navigator.getParentNode(parentNode4);
                depth2--;
            }
            if (parentNode4 == obj) {
                return -1;
            }
            while (true) {
                Object parentNode5 = this.navigator.getParentNode(parentNode3);
                Object parentNode6 = this.navigator.getParentNode(parentNode4);
                if (parentNode5 == parentNode6) {
                    return compareSiblings(parentNode3, parentNode4);
                }
                parentNode3 = parentNode5;
                parentNode4 = parentNode6;
            }
        } catch (UnsupportedAxisException e2) {
            return 0;
        }
    }

    private boolean isNonChild(Object obj) {
        return this.navigator.isAttribute(obj) || this.navigator.isNamespace(obj);
    }

    private int compareSiblings(Object obj, Object obj2) throws UnsupportedAxisException {
        if (isNonChild(obj)) {
            return 1;
        }
        if (isNonChild(obj2)) {
            return -1;
        }
        Iterator followingSiblingAxisIterator = this.navigator.getFollowingSiblingAxisIterator(obj);
        while (followingSiblingAxisIterator.hasNext()) {
            if (followingSiblingAxisIterator.next().equals(obj2)) {
                return -1;
            }
        }
        return 1;
    }

    private int getDepth(Object obj) throws UnsupportedAxisException {
        int i = 0;
        Object obj2 = obj;
        while (true) {
            Object parentNode = this.navigator.getParentNode(obj2);
            obj2 = parentNode;
            if (parentNode == null) {
                return i;
            }
            i++;
        }
    }
}
