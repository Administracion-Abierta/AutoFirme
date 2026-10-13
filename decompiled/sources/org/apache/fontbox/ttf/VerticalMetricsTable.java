package org.apache.fontbox.ttf;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/VerticalMetricsTable.class */
public class VerticalMetricsTable extends TTFTable {
    public static final String TAG = "vmtx";
    private int[] advanceHeight;
    private short[] topSideBearing;
    private short[] additionalTopSideBearing;
    private int numVMetrics;

    VerticalMetricsTable(TrueTypeFont font) {
        super(font);
    }

    @Override // org.apache.fontbox.ttf.TTFTable
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        VerticalHeaderTable vHeader = ttf.getVerticalHeader();
        if (vHeader == null) {
            throw new IOException("Could not get vhea table");
        }
        this.numVMetrics = vHeader.getNumberOfVMetrics();
        int numGlyphs = ttf.getNumberOfGlyphs();
        int bytesRead = 0;
        this.advanceHeight = new int[this.numVMetrics];
        this.topSideBearing = new short[this.numVMetrics];
        for (int i = 0; i < this.numVMetrics; i++) {
            this.advanceHeight[i] = data.readUnsignedShort();
            this.topSideBearing[i] = data.readSignedShort();
            bytesRead += 4;
        }
        if (bytesRead < getLength()) {
            int numberNonVertical = numGlyphs - this.numVMetrics;
            if (numberNonVertical < 0) {
                numberNonVertical = numGlyphs;
            }
            this.additionalTopSideBearing = new short[numberNonVertical];
            for (int i2 = 0; i2 < numberNonVertical; i2++) {
                if (bytesRead < getLength()) {
                    this.additionalTopSideBearing[i2] = data.readSignedShort();
                    bytesRead += 2;
                }
            }
        }
        this.initialized = true;
    }

    public int getTopSideBearing(int gid) {
        if (gid < this.numVMetrics) {
            return this.topSideBearing[gid];
        }
        return this.additionalTopSideBearing[gid - this.numVMetrics];
    }

    public int getAdvanceHeight(int gid) {
        if (gid < this.numVMetrics) {
            return this.advanceHeight[gid];
        }
        return this.advanceHeight[this.advanceHeight.length - 1];
    }
}
