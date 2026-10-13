package com.twelvemonkeys.imageio.metadata;

import com.twelvemonkeys.lang.Validate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/AbstractCompoundDirectory.class */
public abstract class AbstractCompoundDirectory extends AbstractDirectory implements CompoundDirectory {
    private final List<Directory> directories;

    protected AbstractCompoundDirectory(Collection<? extends Directory> collection) {
        super(null);
        this.directories = new ArrayList();
        if (collection != null) {
            this.directories.addAll(Validate.noNullElements(collection));
        }
    }

    @Override // com.twelvemonkeys.imageio.metadata.CompoundDirectory
    public Directory getDirectory(int i) {
        return this.directories.get(i);
    }

    @Override // com.twelvemonkeys.imageio.metadata.CompoundDirectory
    public int directoryCount() {
        return this.directories.size();
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractDirectory, com.twelvemonkeys.imageio.metadata.Directory
    public Entry getEntryById(Object obj) {
        Iterator<Directory> it = this.directories.iterator();
        while (it.hasNext()) {
            Entry entryById = it.next().getEntryById(obj);
            if (entryById != null) {
                return entryById;
            }
        }
        return null;
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractDirectory, com.twelvemonkeys.imageio.metadata.Directory
    public Entry getEntryByFieldName(String str) {
        Iterator<Directory> it = this.directories.iterator();
        while (it.hasNext()) {
            Entry entryByFieldName = it.next().getEntryByFieldName(str);
            if (entryByFieldName != null) {
                return entryByFieldName;
            }
        }
        return null;
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractDirectory, java.lang.Iterable
    public Iterator<Entry> iterator() {
        return new Iterator<Entry>() { // from class: com.twelvemonkeys.imageio.metadata.AbstractCompoundDirectory.1
            Iterator<Directory> directoryIterator;
            Iterator<Entry> current;

            {
                this.directoryIterator = AbstractCompoundDirectory.this.directories.iterator();
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                if (this.current == null || !this.current.hasNext()) {
                    if (this.directoryIterator.hasNext()) {
                        Iterator<Entry> it = this.directoryIterator.next().iterator();
                        this.current = it;
                        if (it.hasNext()) {
                        }
                    }
                    return false;
                }
                return true;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.Iterator
            public Entry next() {
                hasNext();
                return this.current.next();
            }

            @Override // java.util.Iterator
            public void remove() {
                this.current.remove();
            }
        };
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractDirectory, com.twelvemonkeys.imageio.metadata.Directory
    public boolean add(Entry entry) {
        throw new UnsupportedOperationException("Directory is read-only");
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractDirectory, com.twelvemonkeys.imageio.metadata.Directory
    public boolean remove(Object obj) {
        throw new UnsupportedOperationException("Directory is read-only");
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractDirectory, com.twelvemonkeys.imageio.metadata.Directory
    public boolean isReadOnly() {
        return true;
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractDirectory, com.twelvemonkeys.imageio.metadata.Directory
    public int size() {
        int size = 0;
        Iterator<Directory> it = this.directories.iterator();
        while (it.hasNext()) {
            size += it.next().size();
        }
        return size;
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractDirectory
    public String toString() {
        return String.format("%s%s", getClass().getSimpleName(), this.directories.toString());
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractDirectory
    public int hashCode() {
        int iHashCode = 0;
        Iterator<Directory> it = this.directories.iterator();
        while (it.hasNext()) {
            iHashCode ^= it.next().hashCode();
        }
        return iHashCode;
    }

    @Override // com.twelvemonkeys.imageio.metadata.AbstractDirectory
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        CompoundDirectory compoundDirectory = (CompoundDirectory) obj;
        if (directoryCount() != compoundDirectory.directoryCount()) {
            return false;
        }
        for (int i = 0; i < directoryCount(); i++) {
            if (!getDirectory(i).equals(compoundDirectory.getDirectory(i))) {
                return false;
            }
        }
        return true;
    }
}
