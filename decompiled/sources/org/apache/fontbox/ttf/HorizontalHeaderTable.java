package org.apache.fontbox.ttf;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/HorizontalHeaderTable.class */
public class HorizontalHeaderTable extends TTFTable {
    public static final String TAG = "hhea";
    private float version;
    private short ascender;
    private short descender;
    private short lineGap;
    private int advanceWidthMax;
    private short minLeftSideBearing;
    private short minRightSideBearing;
    private short xMaxExtent;
    private short caretSlopeRise;
    private short caretSlopeRun;
    private short reserved1;
    private short reserved2;
    private short reserved3;
    private short reserved4;
    private short reserved5;
    private short metricDataFormat;
    private int numberOfHMetrics;

    HorizontalHeaderTable(TrueTypeFont font) {
        super(font);
    }

    @Override // org.apache.fontbox.ttf.TTFTable
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        this.version = data.read32Fixed();
        this.ascender = data.readSignedShort();
        this.descender = data.readSignedShort();
        this.lineGap = data.readSignedShort();
        this.advanceWidthMax = data.readUnsignedShort();
        this.minLeftSideBearing = data.readSignedShort();
        this.minRightSideBearing = data.readSignedShort();
        this.xMaxExtent = data.readSignedShort();
        this.caretSlopeRise = data.readSignedShort();
        this.caretSlopeRun = data.readSignedShort();
        this.reserved1 = data.readSignedShort();
        this.reserved2 = data.readSignedShort();
        this.reserved3 = data.readSignedShort();
        this.reserved4 = data.readSignedShort();
        this.reserved5 = data.readSignedShort();
        this.metricDataFormat = data.readSignedShort();
        this.numberOfHMetrics = data.readUnsignedShort();
        this.initialized = true;
    }

    public int getAdvanceWidthMax() {
        return this.advanceWidthMax;
    }

    public void setAdvanceWidthMax(int advanceWidthMaxValue) {
        this.advanceWidthMax = advanceWidthMaxValue;
    }

    public short getAscender() {
        return this.ascender;
    }

    public void setAscender(short ascenderValue) {
        this.ascender = ascenderValue;
    }

    public short getCaretSlopeRise() {
        return this.caretSlopeRise;
    }

    public void setCaretSlopeRise(short caretSlopeRiseValue) {
        this.caretSlopeRise = caretSlopeRiseValue;
    }

    public short getCaretSlopeRun() {
        return this.caretSlopeRun;
    }

    public void setCaretSlopeRun(short caretSlopeRunValue) {
        this.caretSlopeRun = caretSlopeRunValue;
    }

    public short getDescender() {
        return this.descender;
    }

    public void setDescender(short descenderValue) {
        this.descender = descenderValue;
    }

    public short getLineGap() {
        return this.lineGap;
    }

    public void setLineGap(short lineGapValue) {
        this.lineGap = lineGapValue;
    }

    public short getMetricDataFormat() {
        return this.metricDataFormat;
    }

    public void setMetricDataFormat(short metricDataFormatValue) {
        this.metricDataFormat = metricDataFormatValue;
    }

    public short getMinLeftSideBearing() {
        return this.minLeftSideBearing;
    }

    public void setMinLeftSideBearing(short minLeftSideBearingValue) {
        this.minLeftSideBearing = minLeftSideBearingValue;
    }

    public short getMinRightSideBearing() {
        return this.minRightSideBearing;
    }

    public void setMinRightSideBearing(short minRightSideBearingValue) {
        this.minRightSideBearing = minRightSideBearingValue;
    }

    public int getNumberOfHMetrics() {
        return this.numberOfHMetrics;
    }

    public void setNumberOfHMetrics(int numberOfHMetricsValue) {
        this.numberOfHMetrics = numberOfHMetricsValue;
    }

    public short getReserved1() {
        return this.reserved1;
    }

    public void setReserved1(short reserved1Value) {
        this.reserved1 = reserved1Value;
    }

    public short getReserved2() {
        return this.reserved2;
    }

    public void setReserved2(short reserved2Value) {
        this.reserved2 = reserved2Value;
    }

    public short getReserved3() {
        return this.reserved3;
    }

    public void setReserved3(short reserved3Value) {
        this.reserved3 = reserved3Value;
    }

    public short getReserved4() {
        return this.reserved4;
    }

    public void setReserved4(short reserved4Value) {
        this.reserved4 = reserved4Value;
    }

    public short getReserved5() {
        return this.reserved5;
    }

    public void setReserved5(short reserved5Value) {
        this.reserved5 = reserved5Value;
    }

    public float getVersion() {
        return this.version;
    }

    public void setVersion(float versionValue) {
        this.version = versionValue;
    }

    public short getXMaxExtent() {
        return this.xMaxExtent;
    }

    public void setXMaxExtent(short maxExtentValue) {
        this.xMaxExtent = maxExtentValue;
    }
}
