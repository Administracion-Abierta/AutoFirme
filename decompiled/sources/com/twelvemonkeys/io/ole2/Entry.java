package com.twelvemonkeys.io.ole2;

import com.ctc.wstx.io.CharsetNames;
import com.twelvemonkeys.io.SeekableInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/ole2/Entry.class */
public final class Entry implements Comparable<Entry> {
    String name;
    byte type;
    byte nodeColor;
    int prevDId;
    int nextDId;
    int rootNodeDId;
    long createdTimestamp;
    long modifiedTimestamp;
    int startSId;
    int streamSize;
    CompoundDocument document;
    Entry parent;
    SortedSet<Entry> children;
    public static final int LENGTH = 128;
    static final int EMPTY = 0;
    static final int USER_STORAGE = 1;
    static final int USER_STREAM = 2;
    static final int LOCK_BYTES = 3;
    static final int PROPERTY = 4;
    static final int ROOT_STORAGE = 5;
    private static final SortedSet<Entry> NO_CHILDREN = Collections.unmodifiableSortedSet(new TreeSet());

    private Entry() {
    }

    static Entry readEntry(DataInput dataInput) throws IOException {
        Entry entry = new Entry();
        entry.read(dataInput);
        return entry;
    }

    private void read(DataInput dataInput) throws IOException {
        byte[] bArr = new byte[64];
        dataInput.readFully(bArr);
        this.name = new String(bArr, 0, dataInput.readShort() - 2, Charset.forName(CharsetNames.CS_UTF16LE));
        this.type = dataInput.readByte();
        this.nodeColor = dataInput.readByte();
        this.prevDId = dataInput.readInt();
        this.nextDId = dataInput.readInt();
        this.rootNodeDId = dataInput.readInt();
        if (dataInput.skipBytes(20) != 20) {
            throw new CorruptDocumentException();
        }
        this.createdTimestamp = CompoundDocument.toJavaTimeInMillis(dataInput.readLong());
        this.modifiedTimestamp = CompoundDocument.toJavaTimeInMillis(dataInput.readLong());
        this.startSId = dataInput.readInt();
        this.streamSize = dataInput.readInt();
        dataInput.readInt();
    }

    public boolean isRoot() {
        return this.type == 5;
    }

    public boolean isDirectory() {
        return this.type == 1;
    }

    public boolean isFile() {
        return this.type == 2;
    }

    public String getName() {
        return this.name;
    }

    public SeekableInputStream getInputStream() throws IOException {
        if (!isFile()) {
            return null;
        }
        return this.document.getInputStreamForSId(this.startSId, this.streamSize);
    }

    public long length() {
        if (!isFile()) {
            return 0L;
        }
        return this.streamSize;
    }

    public long created() {
        return this.createdTimestamp;
    }

    public long lastModified() {
        return this.modifiedTimestamp;
    }

    public Entry getParentEntry() {
        return this.parent;
    }

    public Entry getChildEntry(String str) throws IOException {
        if (isFile() || this.rootNodeDId == -1) {
            return null;
        }
        Entry entry = new Entry();
        entry.name = str;
        entry.parent = this;
        return getChildEntries().tailSet(entry).first();
    }

    public SortedSet<Entry> getChildEntries() throws IOException {
        if (this.children == null) {
            if (isFile() || this.rootNodeDId == -1) {
                this.children = NO_CHILDREN;
            } else {
                this.children = Collections.unmodifiableSortedSet(this.document.getEntries(this.rootNodeDId, this));
            }
        }
        return this.children;
    }

    public String toString() {
        String str;
        String str2;
        StringBuilder sbAppend = new StringBuilder().append(OperatorName.SHOW_TEXT_LINE_AND_SPACE).append(this.name).append("\" (");
        if (isFile()) {
            str = StandardStructureTypes.DOCUMENT;
        } else {
            str = isDirectory() ? "Directory" : "Root";
        }
        StringBuilder sbAppend2 = sbAppend.append(str).append(this.parent != null ? ", parent: \"" + this.parent.getName() + OperatorName.SHOW_TEXT_LINE_AND_SPACE : "");
        if (isFile()) {
            str2 = "";
        } else {
            str2 = ", children: " + (this.children != null ? String.valueOf(this.children.size()) : "(unknown)");
        }
        return sbAppend2.append(str2).append(", SId=").append(this.startSId).append(", length=").append(this.streamSize).append(")").toString();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Entry)) {
            return false;
        }
        Entry entry = (Entry) obj;
        return this.name.equals(entry.name) && (this.parent == entry.parent || (this.parent != null && this.parent.equals(entry.parent)));
    }

    public int hashCode() {
        return this.name.hashCode() ^ this.startSId;
    }

    @Override // java.lang.Comparable
    public int compareTo(Entry entry) {
        if (this == entry) {
            return 0;
        }
        int length = this.name.length() - entry.name.length();
        if (length != 0) {
            return length;
        }
        return this.name.compareTo(entry.name);
    }
}
