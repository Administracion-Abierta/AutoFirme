package org.codehaus.stax2.ri;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/SingletonIterator.class */
public class SingletonIterator<T> implements Iterator<T> {
    private final T _value;
    private boolean _done = false;

    @Deprecated
    public SingletonIterator(T value) {
        this._value = value;
    }

    public static <T> SingletonIterator<T> create(T value) {
        return new SingletonIterator<>(value);
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return !this._done;
    }

    @Override // java.util.Iterator
    public T next() {
        if (this._done) {
            throw new NoSuchElementException();
        }
        this._done = true;
        return this._value;
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("Can not remove item from SingletonIterator.");
    }
}
