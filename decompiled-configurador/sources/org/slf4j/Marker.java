package org.slf4j;

import java.io.Serializable;
import java.util.Iterator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/slf4j/Marker.class */
public interface Marker extends Serializable {
    public static final String ANY_MARKER = "*";
    public static final String ANY_NON_NULL_MARKER = "+";

    String getName();

    void add(Marker marker);

    boolean remove(Marker marker);

    boolean hasChildren();

    boolean hasReferences();

    Iterator<Marker> iterator();

    boolean contains(Marker marker);

    boolean contains(String str);

    boolean equals(Object obj);

    int hashCode();
}
