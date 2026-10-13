package org.apache.fontbox.ttf;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyfSimpleDescript.class */
public class GlyfSimpleDescript extends GlyfDescript {
    private static final Log LOG = LogFactory.getLog(GlyfSimpleDescript.class);
    private int[] endPtsOfContours;
    private byte[] flags;
    private short[] xCoordinates;
    private short[] yCoordinates;
    private final int pointCount;

    GlyfSimpleDescript() throws IOException {
        super((short) 0, null);
        this.pointCount = 0;
    }

    GlyfSimpleDescript(short numberOfContours, TTFDataStream bais, short x0) throws IOException {
        super(numberOfContours, bais);
        if (numberOfContours == 0) {
            this.pointCount = 0;
            return;
        }
        this.endPtsOfContours = bais.readUnsignedShortArray(numberOfContours);
        int lastEndPt = this.endPtsOfContours[numberOfContours - 1];
        if (numberOfContours == 1 && lastEndPt == 65535) {
            this.pointCount = 0;
            return;
        }
        this.pointCount = lastEndPt + 1;
        this.flags = new byte[this.pointCount];
        this.xCoordinates = new short[this.pointCount];
        this.yCoordinates = new short[this.pointCount];
        int instructionCount = bais.readUnsignedShort();
        readInstructions(bais, instructionCount);
        readFlags(this.pointCount, bais);
        readCoords(this.pointCount, bais, x0);
    }

    @Override // org.apache.fontbox.ttf.GlyphDescription
    public int getEndPtOfContours(int i) {
        return this.endPtsOfContours[i];
    }

    @Override // org.apache.fontbox.ttf.GlyphDescription
    public byte getFlags(int i) {
        return this.flags[i];
    }

    @Override // org.apache.fontbox.ttf.GlyphDescription
    public short getXCoordinate(int i) {
        return this.xCoordinates[i];
    }

    @Override // org.apache.fontbox.ttf.GlyphDescription
    public short getYCoordinate(int i) {
        return this.yCoordinates[i];
    }

    @Override // org.apache.fontbox.ttf.GlyphDescription
    public boolean isComposite() {
        return false;
    }

    @Override // org.apache.fontbox.ttf.GlyphDescription
    public int getPointCount() {
        return this.pointCount;
    }

    private void readCoords(int count, TTFDataStream bais, short x0) throws IOException {
        short x = x0;
        short y = 0;
        for (int i = 0; i < count; i++) {
            if ((this.flags[i] & 16) != 0) {
                if ((this.flags[i] & 2) != 0) {
                    x = (short) (x + ((short) bais.readUnsignedByte()));
                }
            } else if ((this.flags[i] & 2) != 0) {
                x = (short) (x - ((short) bais.readUnsignedByte()));
            } else {
                x = (short) (x + bais.readSignedShort());
            }
            this.xCoordinates[i] = x;
        }
        for (int i2 = 0; i2 < count; i2++) {
            if ((this.flags[i2] & 32) != 0) {
                if ((this.flags[i2] & 4) != 0) {
                    y = (short) (y + ((short) bais.readUnsignedByte()));
                }
            } else if ((this.flags[i2] & 4) != 0) {
                y = (short) (y - ((short) bais.readUnsignedByte()));
            } else {
                y = (short) (y + bais.readSignedShort());
            }
            this.yCoordinates[i2] = y;
        }
    }

    private void readFlags(int flagCount, TTFDataStream bais) throws IOException {
        int index = 0;
        while (index < flagCount) {
            this.flags[index] = (byte) bais.readUnsignedByte();
            if ((this.flags[index] & 8) != 0) {
                int repeats = bais.readUnsignedByte();
                for (int i = 1; i <= repeats; i++) {
                    if (index + i >= this.flags.length) {
                        LOG.error("repeat count (" + repeats + ") higher than remaining space");
                        return;
                    }
                    this.flags[index + i] = this.flags[index];
                }
                index += repeats;
            }
            index++;
        }
    }
}
