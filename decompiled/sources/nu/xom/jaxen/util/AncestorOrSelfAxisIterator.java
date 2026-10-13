package nu.xom.jaxen.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import nu.xom.jaxen.JaxenRuntimeException;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.UnsupportedAxisException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/util/AncestorOrSelfAxisIterator.class */
public class AncestorOrSelfAxisIterator implements Iterator {
    private Object contextNode;
    private Navigator navigator;

    public AncestorOrSelfAxisIterator(Object obj, Navigator navigator) {
        this.contextNode = obj;
        this.navigator = navigator;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.contextNode != null;
    }

    @Override // java.util.Iterator
    public Object next() {
        try {
            if (!hasNext()) {
                throw new NoSuchElementException("Exhausted ancestor-or-self axis");
            }
            Object obj = this.contextNode;
            this.contextNode = this.navigator.getParentNode(this.contextNode);
            return obj;
        } catch (UnsupportedAxisException e) {
            throw new JaxenRuntimeException(e);
        }
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
