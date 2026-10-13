package nu.xom.jaxen.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import nu.xom.jaxen.JaxenConstants;
import nu.xom.jaxen.JaxenRuntimeException;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.UnsupportedAxisException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/util/FollowingAxisIterator.class */
public class FollowingAxisIterator implements Iterator {
    private Object contextNode;
    private Navigator navigator;
    private Iterator siblings;
    private Iterator currentSibling = JaxenConstants.EMPTY_ITERATOR;

    public FollowingAxisIterator(Object obj, Navigator navigator) throws UnsupportedAxisException {
        this.contextNode = obj;
        this.navigator = navigator;
        this.siblings = navigator.getFollowingSiblingAxisIterator(obj);
    }

    private boolean goForward() {
        while (!this.siblings.hasNext()) {
            if (!goUp()) {
                return false;
            }
        }
        this.currentSibling = new DescendantOrSelfAxisIterator(this.siblings.next(), this.navigator);
        return true;
    }

    private boolean goUp() {
        if (this.contextNode == null || this.navigator.isDocument(this.contextNode)) {
            return false;
        }
        try {
            this.contextNode = this.navigator.getParentNode(this.contextNode);
            if (this.contextNode == null || this.navigator.isDocument(this.contextNode)) {
                return false;
            }
            this.siblings = this.navigator.getFollowingSiblingAxisIterator(this.contextNode);
            return true;
        } catch (UnsupportedAxisException e) {
            throw new JaxenRuntimeException(e);
        }
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        while (!this.currentSibling.hasNext()) {
            if (!goForward()) {
                return false;
            }
        }
        return true;
    }

    @Override // java.util.Iterator
    public Object next() throws NoSuchElementException {
        if (hasNext()) {
            return this.currentSibling.next();
        }
        throw new NoSuchElementException();
    }

    @Override // java.util.Iterator
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
