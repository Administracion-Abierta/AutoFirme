package org.apache.oro.util;

import java.io.Serializable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/oro/util/GenericCacheEntry.class */
final class GenericCacheEntry implements Serializable {
    private static final long serialVersionUID = 8359756019468404576L;
    int _index;
    Object _value = null;
    Object _key = null;

    GenericCacheEntry(int index) {
        this._index = index;
    }
}
