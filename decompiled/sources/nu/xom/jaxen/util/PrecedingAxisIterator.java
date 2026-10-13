package nu.xom.jaxen.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import nu.xom.jaxen.JaxenConstants;
import nu.xom.jaxen.JaxenRuntimeException;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.UnsupportedAxisException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/util/PrecedingAxisIterator.class */
public class PrecedingAxisIterator implements Iterator {
    private Iterator ancestorOrSelf;
    private Iterator precedingSibling = JaxenConstants.EMPTY_ITERATOR;
    private ListIterator childrenOrSelf = JaxenConstants.EMPTY_LIST_ITERATOR;
    private ArrayList stack = new ArrayList();
    private Navigator navigator;

    public PrecedingAxisIterator(Object obj, Navigator navigator) throws UnsupportedAxisException {
        this.navigator = navigator;
        this.ancestorOrSelf = navigator.getAncestorOrSelfAxisIterator(obj);
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        while (!this.childrenOrSelf.hasPrevious()) {
            try {
                if (this.stack.isEmpty()) {
                    while (!this.precedingSibling.hasNext()) {
                        if (!this.ancestorOrSelf.hasNext()) {
                            return false;
                        }
                        this.precedingSibling = new PrecedingSiblingAxisIterator(this.ancestorOrSelf.next(), this.navigator);
                    }
                    this.childrenOrSelf = childrenOrSelf(this.precedingSibling.next());
                } else {
                    this.childrenOrSelf = (ListIterator) this.stack.remove(this.stack.size() - 1);
                }
            } catch (UnsupportedAxisException e) {
                throw new JaxenRuntimeException(e);
            }
        }
        return true;
    }

    private ListIterator childrenOrSelf(Object obj) {
        try {
            ArrayList arrayList = new ArrayList();
            arrayList.add(obj);
            Iterator childAxisIterator = this.navigator.getChildAxisIterator(obj);
            if (childAxisIterator != null) {
                while (childAxisIterator.hasNext()) {
                    arrayList.add(childAxisIterator.next());
                }
            }
            return arrayList.listIterator(arrayList.size());
        } catch (UnsupportedAxisException e) {
            throw new JaxenRuntimeException(e);
        }
    }

    @Override // java.util.Iterator
    public Object next() throws NoSuchElementException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        while (true) {
            Object objPrevious = this.childrenOrSelf.previous();
            if (!this.childrenOrSelf.hasPrevious()) {
                return objPrevious;
            }
            this.stack.add(this.childrenOrSelf);
            this.childrenOrSelf = childrenOrSelf(objPrevious);
        }
    }

    @Override // java.util.Iterator
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
