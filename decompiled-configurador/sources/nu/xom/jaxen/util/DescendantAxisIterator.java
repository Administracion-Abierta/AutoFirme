package nu.xom.jaxen.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import nu.xom.jaxen.JaxenRuntimeException;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.UnsupportedAxisException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/util/DescendantAxisIterator.class */
public class DescendantAxisIterator implements Iterator {
    private ArrayList stack;
    private Iterator children;
    private Navigator navigator;

    public DescendantAxisIterator(Object obj, Navigator navigator) throws UnsupportedAxisException {
        this(navigator, navigator.getChildAxisIterator(obj));
    }

    public DescendantAxisIterator(Navigator navigator, Iterator it) {
        this.stack = new ArrayList();
        this.navigator = navigator;
        this.children = it;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        while (!this.children.hasNext()) {
            if (this.stack.isEmpty()) {
                return false;
            }
            this.children = (Iterator) this.stack.remove(this.stack.size() - 1);
        }
        return true;
    }

    @Override // java.util.Iterator
    public Object next() {
        try {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Object next = this.children.next();
            this.stack.add(this.children);
            this.children = this.navigator.getChildAxisIterator(next);
            return next;
        } catch (UnsupportedAxisException e) {
            throw new JaxenRuntimeException(e);
        }
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
