package com.twelvemonkeys.imageio.metadata;

import com.twelvemonkeys.lang.Validate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/AbstractDirectory.class */
public abstract class AbstractDirectory implements Directory {
    private final List<Entry> entries = new ArrayList();
    private final List<Entry> unmodifiable = Collections.unmodifiableList(this.entries);

    protected AbstractDirectory(Collection<? extends Entry> collection) {
        if (collection != null) {
            this.entries.addAll(Validate.noNullElements(collection));
        }
    }

    @Override // com.twelvemonkeys.imageio.metadata.Directory
    public Entry getEntryById(Object obj) {
        for (Entry entry : this) {
            if (entry.getIdentifier().equals(obj)) {
                return entry;
            }
        }
        return null;
    }

    @Override // com.twelvemonkeys.imageio.metadata.Directory
    public Entry getEntryByFieldName(String str) {
        for (Entry entry : this) {
            if (entry.getFieldName() != null && entry.getFieldName().equals(str)) {
                return entry;
            }
        }
        return null;
    }

    @Override // java.lang.Iterable
    public Iterator<Entry> iterator() {
        return isReadOnly() ? this.unmodifiable.iterator() : this.entries.iterator();
    }

    protected final void assertMutable() {
        if (isReadOnly()) {
            throw new UnsupportedOperationException("Directory is read-only");
        }
    }

    @Override // com.twelvemonkeys.imageio.metadata.Directory
    public boolean add(Entry entry) {
        assertMutable();
        return this.entries.add(entry);
    }

    @Override // com.twelvemonkeys.imageio.metadata.Directory
    public boolean remove(Object obj) {
        assertMutable();
        return this.entries.remove(obj);
    }

    @Override // com.twelvemonkeys.imageio.metadata.Directory
    public int size() {
        return this.entries.size();
    }

    @Override // com.twelvemonkeys.imageio.metadata.Directory
    public boolean isReadOnly() {
        return true;
    }

    public int hashCode() {
        return this.entries.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return this.entries.equals(((AbstractDirectory) obj).entries);
    }

    public String toString() {
        return String.format("%s%s", getClass().getSimpleName(), this.entries.toString());
    }
}
