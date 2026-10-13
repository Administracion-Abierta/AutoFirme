package com.twelvemonkeys.util;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/AbstractTokenIterator.class */
public abstract class AbstractTokenIterator implements TokenIterator {
    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    @Override // com.twelvemonkeys.util.TokenIterator
    public final boolean hasMoreTokens() {
        return hasNext();
    }

    @Override // com.twelvemonkeys.util.TokenIterator
    public final String nextToken() {
        return (String) next();
    }

    @Override // java.util.Enumeration
    public final boolean hasMoreElements() {
        return hasNext();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Enumeration
    public final String nextElement() {
        return (String) next();
    }
}
