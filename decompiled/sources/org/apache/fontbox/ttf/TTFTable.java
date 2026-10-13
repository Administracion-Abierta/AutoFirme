package org.apache.fontbox.ttf;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/TTFTable.class */
public class TTFTable {
    private String tag;
    private long checkSum;
    private long offset;
    private long length;
    protected volatile boolean initialized;
    protected final TrueTypeFont font;

    TTFTable(TrueTypeFont font) {
        this.font = font;
    }

    public long getCheckSum() {
        return this.checkSum;
    }

    void setCheckSum(long checkSumValue) {
        this.checkSum = checkSumValue;
    }

    public long getLength() {
        return this.length;
    }

    void setLength(long lengthValue) {
        this.length = lengthValue;
    }

    public long getOffset() {
        return this.offset;
    }

    void setOffset(long offsetValue) {
        this.offset = offsetValue;
    }

    public String getTag() {
        return this.tag;
    }

    void setTag(String tagValue) {
        this.tag = tagValue;
    }

    public boolean getInitialized() {
        return this.initialized;
    }

    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
    }
}
