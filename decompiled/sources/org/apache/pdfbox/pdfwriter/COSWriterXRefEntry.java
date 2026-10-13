package org.apache.pdfbox.pdfwriter;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSObjectKey;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfwriter/COSWriterXRefEntry.class */
public class COSWriterXRefEntry implements Comparable<COSWriterXRefEntry> {
    private long offset;
    private COSBase object;
    private COSObjectKey key;
    private boolean free = false;
    private static final COSWriterXRefEntry NULLENTRY = new COSWriterXRefEntry(0, null, new COSObjectKey(0, 65535));

    static {
        NULLENTRY.setFree(true);
    }

    public COSWriterXRefEntry(long start, COSBase obj, COSObjectKey keyValue) {
        setOffset(start);
        setObject(obj);
        setKey(keyValue);
    }

    @Override // java.lang.Comparable
    public int compareTo(COSWriterXRefEntry obj) {
        if (obj == null || getKey().getNumber() < obj.getKey().getNumber()) {
            return -1;
        }
        if (getKey().getNumber() > obj.getKey().getNumber()) {
            return 1;
        }
        return 0;
    }

    public static COSWriterXRefEntry getNullEntry() {
        return NULLENTRY;
    }

    public COSObjectKey getKey() {
        return this.key;
    }

    public long getOffset() {
        return this.offset;
    }

    public boolean isFree() {
        return this.free;
    }

    public void setFree(boolean newFree) {
        this.free = newFree;
    }

    private void setKey(COSObjectKey newKey) {
        this.key = newKey;
    }

    public final void setOffset(long newOffset) {
        this.offset = newOffset;
    }

    public COSBase getObject() {
        return this.object;
    }

    private void setObject(COSBase newObject) {
        this.object = newObject;
    }
}
