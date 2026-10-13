package com.twelvemonkeys.imageio.metadata.tiff;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/tiff/Unknown.class */
final class Unknown {
    private final short type;
    private final int count;
    private final long pos;

    public Unknown(short s, int i, long j) {
        this.type = s;
        this.count = i;
        this.pos = j;
    }

    public int hashCode() {
        return ((int) (this.pos ^ (this.pos >>> 32))) + (this.count * 37) + (this.type * 97);
    }

    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
            Unknown unknown = (Unknown) obj;
            return this.pos == unknown.pos && this.type == unknown.type && this.count == unknown.count;
        }
        return false;
    }

    public String toString() {
        if (this.count == 1) {
            return String.format("Unknown(%d)@%08x", Short.valueOf(this.type), Long.valueOf(this.pos));
        }
        return String.format("Unknown(%d)[%d]@%08x", Short.valueOf(this.type), Integer.valueOf(this.count), Long.valueOf(this.pos));
    }
}
