package org.apache.fontbox.ttf;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/VerticalHeaderTable.class */
public class VerticalHeaderTable extends TTFTable {
    public static final String TAG = "vhea";
    private float version;
    private short ascender;
    private short descender;
    private short lineGap;
    private int advanceHeightMax;
    private short minTopSideBearing;
    private short minBottomSideBearing;
    private short yMaxExtent;
    private short caretSlopeRise;
    private short caretSlopeRun;
    private short caretOffset;
    private short reserved1;
    private short reserved2;
    private short reserved3;
    private short reserved4;
    private short metricDataFormat;
    private int numberOfVMetrics;

    VerticalHeaderTable(TrueTypeFont font) {
        super(font);
    }

    @Override // org.apache.fontbox.ttf.TTFTable
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        this.version = data.read32Fixed();
        this.ascender = data.readSignedShort();
        this.descender = data.readSignedShort();
        this.lineGap = data.readSignedShort();
        this.advanceHeightMax = data.readUnsignedShort();
        this.minTopSideBearing = data.readSignedShort();
        this.minBottomSideBearing = data.readSignedShort();
        this.yMaxExtent = data.readSignedShort();
        this.caretSlopeRise = data.readSignedShort();
        this.caretSlopeRun = data.readSignedShort();
        this.caretOffset = data.readSignedShort();
        this.reserved1 = data.readSignedShort();
        this.reserved2 = data.readSignedShort();
        this.reserved3 = data.readSignedShort();
        this.reserved4 = data.readSignedShort();
        this.metricDataFormat = data.readSignedShort();
        this.numberOfVMetrics = data.readUnsignedShort();
        this.initialized = true;
    }

    public int getAdvanceHeightMax() {
        return this.advanceHeightMax;
    }

    public short getAscender() {
        return this.ascender;
    }

    public short getCaretSlopeRise() {
        return this.caretSlopeRise;
    }

    public short getCaretSlopeRun() {
        return this.caretSlopeRun;
    }

    public short getCaretOffset() {
        return this.caretOffset;
    }

    public short getDescender() {
        return this.descender;
    }

    public short getLineGap() {
        return this.lineGap;
    }

    public short getMetricDataFormat() {
        return this.metricDataFormat;
    }

    public short getMinTopSideBearing() {
        return this.minTopSideBearing;
    }

    public short getMinBottomSideBearing() {
        return this.minBottomSideBearing;
    }

    public int getNumberOfVMetrics() {
        return this.numberOfVMetrics;
    }

    public short getReserved1() {
        return this.reserved1;
    }

    public short getReserved2() {
        return this.reserved2;
    }

    public short getReserved3() {
        return this.reserved3;
    }

    public short getReserved4() {
        return this.reserved4;
    }

    public float getVersion() {
        return this.version;
    }

    public short getYMaxExtent() {
        return this.yMaxExtent;
    }
}
