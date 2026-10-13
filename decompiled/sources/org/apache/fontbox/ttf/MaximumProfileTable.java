package org.apache.fontbox.ttf;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/MaximumProfileTable.class */
public class MaximumProfileTable extends TTFTable {
    public static final String TAG = "maxp";
    private float version;
    private int numGlyphs;
    private int maxPoints;
    private int maxContours;
    private int maxCompositePoints;
    private int maxCompositeContours;
    private int maxZones;
    private int maxTwilightPoints;
    private int maxStorage;
    private int maxFunctionDefs;
    private int maxInstructionDefs;
    private int maxStackElements;
    private int maxSizeOfInstructions;
    private int maxComponentElements;
    private int maxComponentDepth;

    MaximumProfileTable(TrueTypeFont font) {
        super(font);
    }

    public int getMaxComponentDepth() {
        return this.maxComponentDepth;
    }

    public void setMaxComponentDepth(int maxComponentDepthValue) {
        this.maxComponentDepth = maxComponentDepthValue;
    }

    public int getMaxComponentElements() {
        return this.maxComponentElements;
    }

    public void setMaxComponentElements(int maxComponentElementsValue) {
        this.maxComponentElements = maxComponentElementsValue;
    }

    public int getMaxCompositeContours() {
        return this.maxCompositeContours;
    }

    public void setMaxCompositeContours(int maxCompositeContoursValue) {
        this.maxCompositeContours = maxCompositeContoursValue;
    }

    public int getMaxCompositePoints() {
        return this.maxCompositePoints;
    }

    public void setMaxCompositePoints(int maxCompositePointsValue) {
        this.maxCompositePoints = maxCompositePointsValue;
    }

    public int getMaxContours() {
        return this.maxContours;
    }

    public void setMaxContours(int maxContoursValue) {
        this.maxContours = maxContoursValue;
    }

    public int getMaxFunctionDefs() {
        return this.maxFunctionDefs;
    }

    public void setMaxFunctionDefs(int maxFunctionDefsValue) {
        this.maxFunctionDefs = maxFunctionDefsValue;
    }

    public int getMaxInstructionDefs() {
        return this.maxInstructionDefs;
    }

    public void setMaxInstructionDefs(int maxInstructionDefsValue) {
        this.maxInstructionDefs = maxInstructionDefsValue;
    }

    public int getMaxPoints() {
        return this.maxPoints;
    }

    public void setMaxPoints(int maxPointsValue) {
        this.maxPoints = maxPointsValue;
    }

    public int getMaxSizeOfInstructions() {
        return this.maxSizeOfInstructions;
    }

    public void setMaxSizeOfInstructions(int maxSizeOfInstructionsValue) {
        this.maxSizeOfInstructions = maxSizeOfInstructionsValue;
    }

    public int getMaxStackElements() {
        return this.maxStackElements;
    }

    public void setMaxStackElements(int maxStackElementsValue) {
        this.maxStackElements = maxStackElementsValue;
    }

    public int getMaxStorage() {
        return this.maxStorage;
    }

    public void setMaxStorage(int maxStorageValue) {
        this.maxStorage = maxStorageValue;
    }

    public int getMaxTwilightPoints() {
        return this.maxTwilightPoints;
    }

    public void setMaxTwilightPoints(int maxTwilightPointsValue) {
        this.maxTwilightPoints = maxTwilightPointsValue;
    }

    public int getMaxZones() {
        return this.maxZones;
    }

    public void setMaxZones(int maxZonesValue) {
        this.maxZones = maxZonesValue;
    }

    public int getNumGlyphs() {
        return this.numGlyphs;
    }

    public void setNumGlyphs(int numGlyphsValue) {
        this.numGlyphs = numGlyphsValue;
    }

    public float getVersion() {
        return this.version;
    }

    public void setVersion(float versionValue) {
        this.version = versionValue;
    }

    @Override // org.apache.fontbox.ttf.TTFTable
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        this.version = data.read32Fixed();
        this.numGlyphs = data.readUnsignedShort();
        this.maxPoints = data.readUnsignedShort();
        this.maxContours = data.readUnsignedShort();
        this.maxCompositePoints = data.readUnsignedShort();
        this.maxCompositeContours = data.readUnsignedShort();
        this.maxZones = data.readUnsignedShort();
        this.maxTwilightPoints = data.readUnsignedShort();
        this.maxStorage = data.readUnsignedShort();
        this.maxFunctionDefs = data.readUnsignedShort();
        this.maxInstructionDefs = data.readUnsignedShort();
        this.maxStackElements = data.readUnsignedShort();
        this.maxSizeOfInstructions = data.readUnsignedShort();
        this.maxComponentElements = data.readUnsignedShort();
        this.maxComponentDepth = data.readUnsignedShort();
        this.initialized = true;
    }
}
