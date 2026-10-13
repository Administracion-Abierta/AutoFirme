package com.twelvemonkeys.util;

import java.util.Enumeration;
import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/TokenIterator.class */
public interface TokenIterator extends Iterator<String>, Enumeration<String> {
    boolean hasMoreTokens();

    String nextToken();

    void reset();
}
