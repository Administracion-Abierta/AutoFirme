package nu.xom.jaxen.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import nu.xom.jaxen.JaxenConstants;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.UnsupportedAxisException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/util/PrecedingSiblingAxisIterator.class */
public class PrecedingSiblingAxisIterator implements Iterator {
    private Object contextNode;
    private Navigator navigator;
    private Iterator siblingIter;
    private Object nextObj;

    public PrecedingSiblingAxisIterator(Object obj, Navigator navigator) throws UnsupportedAxisException {
        this.contextNode = obj;
        this.navigator = navigator;
        init();
        if (this.siblingIter.hasNext()) {
            this.nextObj = this.siblingIter.next();
        }
    }

    private void init() throws UnsupportedAxisException {
        Object parentNode = this.navigator.getParentNode(this.contextNode);
        if (parentNode == null) {
            this.siblingIter = JaxenConstants.EMPTY_ITERATOR;
            return;
        }
        Iterator childAxisIterator = this.navigator.getChildAxisIterator(parentNode);
        LinkedList linkedList = new LinkedList();
        while (childAxisIterator.hasNext()) {
            Object next = childAxisIterator.next();
            if (next.equals(this.contextNode)) {
                break;
            } else {
                linkedList.addFirst(next);
            }
        }
        this.siblingIter = linkedList.iterator();
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.nextObj != null;
    }

    @Override // java.util.Iterator
    public Object next() throws NoSuchElementException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Object obj = this.nextObj;
        if (this.siblingIter.hasNext()) {
            this.nextObj = this.siblingIter.next();
        } else {
            this.nextObj = null;
        }
        return obj;
    }

    @Override // java.util.Iterator
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
