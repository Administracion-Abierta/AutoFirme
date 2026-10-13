package org.apache.fontbox.ttf;

import java.io.IOException;
import java.util.Calendar;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/HeaderTable.class */
public class HeaderTable extends TTFTable {
    public static final String TAG = "head";
    public static final int MAC_STYLE_BOLD = 1;
    public static final int MAC_STYLE_ITALIC = 2;
    private float version;
    private float fontRevision;
    private long checkSumAdjustment;
    private long magicNumber;
    private int flags;
    private int unitsPerEm;
    private Calendar created;
    private Calendar modified;
    private short xMin;
    private short yMin;
    private short xMax;
    private short yMax;
    private int macStyle;
    private int lowestRecPPEM;
    private short fontDirectionHint;
    private short indexToLocFormat;
    private short glyphDataFormat;

    HeaderTable(TrueTypeFont font) {
        super(font);
    }

    @Override // org.apache.fontbox.ttf.TTFTable
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        this.version = data.read32Fixed();
        this.fontRevision = data.read32Fixed();
        this.checkSumAdjustment = data.readUnsignedInt();
        this.magicNumber = data.readUnsignedInt();
        this.flags = data.readUnsignedShort();
        this.unitsPerEm = data.readUnsignedShort();
        this.created = data.readInternationalDate();
        this.modified = data.readInternationalDate();
        this.xMin = data.readSignedShort();
        this.yMin = data.readSignedShort();
        this.xMax = data.readSignedShort();
        this.yMax = data.readSignedShort();
        this.macStyle = data.readUnsignedShort();
        this.lowestRecPPEM = data.readUnsignedShort();
        this.fontDirectionHint = data.readSignedShort();
        this.indexToLocFormat = data.readSignedShort();
        this.glyphDataFormat = data.readSignedShort();
        this.initialized = true;
    }

    public long getCheckSumAdjustment() {
        return this.checkSumAdjustment;
    }

    public void setCheckSumAdjustment(long checkSumAdjustmentValue) {
        this.checkSumAdjustment = checkSumAdjustmentValue;
    }

    public Calendar getCreated() {
        return this.created;
    }

    public void setCreated(Calendar createdValue) {
        this.created = createdValue;
    }

    public int getFlags() {
        return this.flags;
    }

    public void setFlags(int flagsValue) {
        this.flags = flagsValue;
    }

    public short getFontDirectionHint() {
        return this.fontDirectionHint;
    }

    public void setFontDirectionHint(short fontDirectionHintValue) {
        this.fontDirectionHint = fontDirectionHintValue;
    }

    public float getFontRevision() {
        return this.fontRevision;
    }

    public void setFontRevision(float fontRevisionValue) {
        this.fontRevision = fontRevisionValue;
    }

    public short getGlyphDataFormat() {
        return this.glyphDataFormat;
    }

    public void setGlyphDataFormat(short glyphDataFormatValue) {
        this.glyphDataFormat = glyphDataFormatValue;
    }

    public short getIndexToLocFormat() {
        return this.indexToLocFormat;
    }

    public void setIndexToLocFormat(short indexToLocFormatValue) {
        this.indexToLocFormat = indexToLocFormatValue;
    }

    public int getLowestRecPPEM() {
        return this.lowestRecPPEM;
    }

    public void setLowestRecPPEM(int lowestRecPPEMValue) {
        this.lowestRecPPEM = lowestRecPPEMValue;
    }

    public int getMacStyle() {
        return this.macStyle;
    }

    public void setMacStyle(int macStyleValue) {
        this.macStyle = macStyleValue;
    }

    public long getMagicNumber() {
        return this.magicNumber;
    }

    public void setMagicNumber(long magicNumberValue) {
        this.magicNumber = magicNumberValue;
    }

    public Calendar getModified() {
        return this.modified;
    }

    public void setModified(Calendar modifiedValue) {
        this.modified = modifiedValue;
    }

    public int getUnitsPerEm() {
        return this.unitsPerEm;
    }

    public void setUnitsPerEm(int unitsPerEmValue) {
        this.unitsPerEm = unitsPerEmValue;
    }

    public float getVersion() {
        return this.version;
    }

    public void setVersion(float versionValue) {
        this.version = versionValue;
    }

    public short getXMax() {
        return this.xMax;
    }

    public void setXMax(short maxValue) {
        this.xMax = maxValue;
    }

    public short getXMin() {
        return this.xMin;
    }

    public void setXMin(short minValue) {
        this.xMin = minValue;
    }

    public short getYMax() {
        return this.yMax;
    }

    public void setYMax(short maxValue) {
        this.yMax = maxValue;
    }

    public short getYMin() {
        return this.yMin;
    }

    public void setYMin(short minValue) {
        this.yMin = minValue;
    }
}
