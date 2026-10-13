package org.mozilla.javascript.typedarrays;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/typedarrays/NativeTypedArrayIterator.class */
public class NativeTypedArrayIterator<T> implements ListIterator<T> {
    private final NativeTypedArrayView<T> view;
    private int position;
    private int lastPosition = -1;

    NativeTypedArrayIterator(NativeTypedArrayView<T> view, int start) {
        this.view = view;
        this.position = start;
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public boolean hasNext() {
        return this.position < this.view.length;
    }

    @Override // java.util.ListIterator
    public boolean hasPrevious() {
        return this.position > 0;
    }

    @Override // java.util.ListIterator
    public int nextIndex() {
        return this.position;
    }

    @Override // java.util.ListIterator
    public int previousIndex() {
        return this.position - 1;
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public T next() {
        if (hasNext()) {
            T ret = this.view.get(this.position);
            this.lastPosition = this.position;
            this.position++;
            return ret;
        }
        throw new NoSuchElementException();
    }

    @Override // java.util.ListIterator
    public T previous() {
        if (hasPrevious()) {
            this.position--;
            this.lastPosition = this.position;
            return this.view.get(this.position);
        }
        throw new NoSuchElementException();
    }

    @Override // java.util.ListIterator
    public void set(T t) {
        if (this.lastPosition < 0) {
            throw new IllegalStateException();
        }
        this.view.js_set(this.lastPosition, t);
    }

    @Override // java.util.ListIterator, java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.ListIterator
    public void add(T t) {
        throw new UnsupportedOperationException();
    }
}
