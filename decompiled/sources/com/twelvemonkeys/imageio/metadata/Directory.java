package com.twelvemonkeys.imageio.metadata;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/Directory.class */
public interface Directory extends Iterable<Entry> {
    Entry getEntryById(Object obj);

    Entry getEntryByFieldName(String str);

    boolean add(Entry entry);

    boolean remove(Object obj);

    int size();

    boolean isReadOnly();
}
