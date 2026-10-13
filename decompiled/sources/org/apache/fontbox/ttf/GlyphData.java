package org.apache.fontbox.ttf;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import org.apache.fontbox.util.BoundingBox;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphData.class */
public class GlyphData {
    private short xMin;
    private short yMin;
    private short xMax;
    private short yMax;
    private short numberOfContours;
    private BoundingBox boundingBox = null;
    private GlyfDescript glyphDescription = null;

    void initData(GlyphTable glyphTable, TTFDataStream data, int leftSideBearing) throws IOException {
        this.numberOfContours = data.readSignedShort();
        this.xMin = data.readSignedShort();
        this.yMin = data.readSignedShort();
        this.xMax = data.readSignedShort();
        this.yMax = data.readSignedShort();
        this.boundingBox = new BoundingBox(this.xMin, this.yMin, this.xMax, this.yMax);
        if (this.numberOfContours >= 0) {
            short x0 = (short) (leftSideBearing - this.xMin);
            this.glyphDescription = new GlyfSimpleDescript(this.numberOfContours, data, x0);
        } else {
            this.glyphDescription = new GlyfCompositeDescript(data, glyphTable);
        }
    }

    void initEmptyData() throws IOException {
        this.glyphDescription = new GlyfSimpleDescript();
        this.boundingBox = new BoundingBox();
    }

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBoxValue) {
        this.boundingBox = boundingBoxValue;
    }

    public short getNumberOfContours() {
        return this.numberOfContours;
    }

    public void setNumberOfContours(short numberOfContoursValue) {
        this.numberOfContours = numberOfContoursValue;
    }

    public GlyphDescription getDescription() {
        return this.glyphDescription;
    }

    public GeneralPath getPath() {
        return new GlyphRenderer(this.glyphDescription).getPath();
    }

    public short getXMaximum() {
        return this.xMax;
    }

    public short getXMinimum() {
        return this.xMin;
    }

    public short getYMaximum() {
        return this.yMax;
    }

    public short getYMinimum() {
        return this.yMin;
    }
}
