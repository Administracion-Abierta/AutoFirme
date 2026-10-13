package com.twelvemonkeys.imageio.metadata.jpeg;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/jpeg/JPEGSegment.class */
public final class JPEGSegment implements Serializable {
    final int marker;
    final byte[] data;
    private final int length;
    private transient String id;

    JPEGSegment(int i, byte[] bArr, int i2) {
        this.marker = i;
        this.data = bArr;
        this.length = i2;
    }

    public int segmentLength() {
        return this.length;
    }

    public InputStream segmentData() {
        if (this.data != null) {
            return new ByteArrayInputStream(this.data);
        }
        return null;
    }

    public int marker() {
        return this.marker;
    }

    public String identifier() {
        if (this.id == null && isAppSegmentMarker(this.marker)) {
            this.id = JPEGSegmentUtil.asNullTerminatedAsciiString(this.data, 0);
        }
        return this.id;
    }

    static boolean isAppSegmentMarker(int i) {
        return i >= 65504 && i <= 65519;
    }

    public InputStream data() {
        if (this.data != null) {
            return new ByteArrayInputStream(this.data, offset(), length());
        }
        return null;
    }

    public int length() {
        if (this.data != null) {
            return this.data.length - offset();
        }
        return 0;
    }

    int offset() {
        String strIdentifier = identifier();
        if (strIdentifier == null) {
            return 0;
        }
        return strIdentifier.length() + 1;
    }

    public String toString() {
        String strIdentifier = identifier();
        if (strIdentifier != null) {
            return String.format("JPEGSegment[%04x/%s size: %d]", Integer.valueOf(this.marker), strIdentifier, Integer.valueOf(segmentLength()));
        }
        return String.format("JPEGSegment[%04x size: %d]", Integer.valueOf(this.marker), Integer.valueOf(segmentLength()));
    }

    public int hashCode() {
        String strIdentifier = identifier();
        return (marker() << 16) | ((strIdentifier != null ? strIdentifier.hashCode() : 0) & 65535);
    }

    public boolean equals(Object obj) {
        return (obj instanceof JPEGSegment) && ((JPEGSegment) obj).marker == this.marker && Arrays.equals(((JPEGSegment) obj).data, this.data);
    }
}
