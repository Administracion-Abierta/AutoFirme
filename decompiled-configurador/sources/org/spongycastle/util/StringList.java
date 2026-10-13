package org.spongycastle.util;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/util/StringList.class */
public interface StringList extends Iterable<String> {
    boolean add(String str);

    String get(int i);

    int size();

    String[] toStringArray();

    String[] toStringArray(int i, int i2);
}
