package org.codehaus.stax2.ri;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/EmptyIterator.class */
public final class EmptyIterator implements Iterator<Object> {
    static final Iterator<?> sInstance = new EmptyIterator();

    private EmptyIterator() {
    }

    public static <T> Iterator<T> getInstance() {
        return (Iterator<T>) sInstance;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return false;
    }

    @Override // java.util.Iterator
    public Object next() {
        throw new NoSuchElementException();
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new IllegalStateException();
    }
}
