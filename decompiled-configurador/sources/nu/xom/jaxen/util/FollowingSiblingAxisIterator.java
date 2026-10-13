package nu.xom.jaxen.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import nu.xom.jaxen.JaxenConstants;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.UnsupportedAxisException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/util/FollowingSiblingAxisIterator.class */
public class FollowingSiblingAxisIterator implements Iterator {
    private Object contextNode;
    private Navigator navigator;
    private Iterator siblingIter;

    public FollowingSiblingAxisIterator(Object obj, Navigator navigator) throws UnsupportedAxisException {
        this.contextNode = obj;
        this.navigator = navigator;
        init();
    }

    private void init() throws UnsupportedAxisException {
        Object parentNode = this.navigator.getParentNode(this.contextNode);
        if (parentNode == null) {
            this.siblingIter = JaxenConstants.EMPTY_ITERATOR;
            return;
        }
        this.siblingIter = this.navigator.getChildAxisIterator(parentNode);
        while (this.siblingIter.hasNext() && !this.siblingIter.next().equals(this.contextNode)) {
        }
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.siblingIter.hasNext();
    }

    @Override // java.util.Iterator
    public Object next() throws NoSuchElementException {
        return this.siblingIter.next();
    }

    @Override // java.util.Iterator
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
