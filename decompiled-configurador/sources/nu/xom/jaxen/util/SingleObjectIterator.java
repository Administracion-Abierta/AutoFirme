package nu.xom.jaxen.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/util/SingleObjectIterator.class */
public class SingleObjectIterator implements Iterator {
    private Object object;
    private boolean seen = false;

    public SingleObjectIterator(Object obj) {
        this.object = obj;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return !this.seen;
    }

    @Override // java.util.Iterator
    public Object next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        this.seen = true;
        return this.object;
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
