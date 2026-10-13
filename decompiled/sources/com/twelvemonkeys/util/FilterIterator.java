package com.twelvemonkeys.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/FilterIterator.class */
public class FilterIterator<E> implements Iterator<E> {
    protected final Filter<E> filter;
    protected final Iterator<E> iterator;
    private E next = null;
    private E current = null;

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/FilterIterator$Filter.class */
    public interface Filter<E> {
        boolean accept(E e);
    }

    public FilterIterator(Iterator<E> it, Filter<E> filter) {
        if (it == null) {
            throw new IllegalArgumentException("iterator == null");
        }
        if (filter == null) {
            throw new IllegalArgumentException("filter == null");
        }
        this.iterator = it;
        this.filter = filter;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        while (this.next == null && this.iterator.hasNext()) {
            E next = this.iterator.next();
            if (this.filter.accept(next)) {
                this.next = next;
                break;
            }
        }
        return this.next != null;
    }

    @Override // java.util.Iterator
    public E next() {
        if (hasNext()) {
            this.current = this.next;
            this.next = null;
            return this.current;
        }
        throw new NoSuchElementException("Iteration has no more elements.");
    }

    @Override // java.util.Iterator
    public void remove() {
        if (this.current != null) {
            this.iterator.remove();
            return;
        }
        throw new IllegalStateException("Iteration has no current element.");
    }
}
