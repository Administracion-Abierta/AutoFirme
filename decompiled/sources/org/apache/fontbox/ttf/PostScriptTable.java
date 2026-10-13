package org.apache.fontbox.ttf;

import com.aowagie.text.pdf.BaseFont;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/PostScriptTable.class */
public class PostScriptTable extends TTFTable {
    private static final Log LOG = LogFactory.getLog(PostScriptTable.class);
    private float formatType;
    private float italicAngle;
    private short underlinePosition;
    private short underlineThickness;
    private long isFixedPitch;
    private long minMemType42;
    private long maxMemType42;
    private long mimMemType1;
    private long maxMemType1;
    private String[] glyphNames;
    public static final String TAG = "post";

    PostScriptTable(TrueTypeFont font) {
        super(font);
        this.glyphNames = null;
    }

    @Override // org.apache.fontbox.ttf.TTFTable
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        this.formatType = data.read32Fixed();
        this.italicAngle = data.read32Fixed();
        this.underlinePosition = data.readSignedShort();
        this.underlineThickness = data.readSignedShort();
        this.isFixedPitch = data.readUnsignedInt();
        this.minMemType42 = data.readUnsignedInt();
        this.maxMemType42 = data.readUnsignedInt();
        this.mimMemType1 = data.readUnsignedInt();
        this.maxMemType1 = data.readUnsignedInt();
        if (this.formatType == 1.0f) {
            this.glyphNames = new String[258];
            System.arraycopy(WGL4Names.MAC_GLYPH_NAMES, 0, this.glyphNames, 0, 258);
        } else if (this.formatType == 2.0f) {
            int numGlyphs = data.readUnsignedShort();
            int[] glyphNameIndex = new int[numGlyphs];
            this.glyphNames = new String[numGlyphs];
            int maxIndex = Integer.MIN_VALUE;
            for (int i = 0; i < numGlyphs; i++) {
                int index = data.readUnsignedShort();
                glyphNameIndex[i] = index;
                if (index <= 32767) {
                    maxIndex = Math.max(maxIndex, index);
                }
            }
            String[] nameArray = null;
            if (maxIndex >= 258) {
                nameArray = new String[(maxIndex - 258) + 1];
                for (int i2 = 0; i2 < nameArray.length; i2++) {
                    int numberOfChars = data.readUnsignedByte();
                    try {
                        nameArray[i2] = data.readString(numberOfChars);
                    } catch (IOException ex) {
                        LOG.warn("Error reading names in PostScript table at entry " + i2 + " of " + nameArray.length + ", setting remaining entries to .notdef", ex);
                        for (int j = i2; j < nameArray.length; j++) {
                            nameArray[j] = BaseFont.notdef;
                        }
                    }
                }
            }
            for (int i3 = 0; i3 < numGlyphs; i3++) {
                int index2 = glyphNameIndex[i3];
                if (index2 >= 0 && index2 < 258) {
                    this.glyphNames[i3] = WGL4Names.MAC_GLYPH_NAMES[index2];
                } else if (index2 >= 258 && index2 <= 32767) {
                    this.glyphNames[i3] = nameArray[index2 - 258];
                } else {
                    this.glyphNames[i3] = ".undefined";
                }
            }
        } else if (this.formatType == 2.5f) {
            int[] glyphNameIndex2 = new int[ttf.getNumberOfGlyphs()];
            for (int i4 = 0; i4 < glyphNameIndex2.length; i4++) {
                int offset = data.readSignedByte();
                glyphNameIndex2[i4] = i4 + 1 + offset;
            }
            this.glyphNames = new String[glyphNameIndex2.length];
            for (int i5 = 0; i5 < this.glyphNames.length; i5++) {
                int index3 = glyphNameIndex2[i5];
                if (index3 >= 0 && index3 < 258) {
                    String name = WGL4Names.MAC_GLYPH_NAMES[index3];
                    if (name != null) {
                        this.glyphNames[i5] = name;
                    }
                } else {
                    LOG.debug("incorrect glyph name index " + index3 + ", valid numbers 0..258");
                }
            }
        } else if (this.formatType == 3.0f) {
            LOG.debug("No PostScript name information is provided for the font " + this.font.getName());
        }
        this.initialized = true;
    }

    public float getFormatType() {
        return this.formatType;
    }

    public void setFormatType(float formatTypeValue) {
        this.formatType = formatTypeValue;
    }

    public long getIsFixedPitch() {
        return this.isFixedPitch;
    }

    public void setIsFixedPitch(long isFixedPitchValue) {
        this.isFixedPitch = isFixedPitchValue;
    }

    public float getItalicAngle() {
        return this.italicAngle;
    }

    public void setItalicAngle(float italicAngleValue) {
        this.italicAngle = italicAngleValue;
    }

    public long getMaxMemType1() {
        return this.maxMemType1;
    }

    public void setMaxMemType1(long maxMemType1Value) {
        this.maxMemType1 = maxMemType1Value;
    }

    public long getMaxMemType42() {
        return this.maxMemType42;
    }

    public void setMaxMemType42(long maxMemType42Value) {
        this.maxMemType42 = maxMemType42Value;
    }

    public long getMinMemType1() {
        return this.mimMemType1;
    }

    public void setMimMemType1(long mimMemType1Value) {
        this.mimMemType1 = mimMemType1Value;
    }

    public long getMinMemType42() {
        return this.minMemType42;
    }

    public void setMinMemType42(long minMemType42Value) {
        this.minMemType42 = minMemType42Value;
    }

    public short getUnderlinePosition() {
        return this.underlinePosition;
    }

    public void setUnderlinePosition(short underlinePositionValue) {
        this.underlinePosition = underlinePositionValue;
    }

    public short getUnderlineThickness() {
        return this.underlineThickness;
    }

    public void setUnderlineThickness(short underlineThicknessValue) {
        this.underlineThickness = underlineThicknessValue;
    }

    public String[] getGlyphNames() {
        return this.glyphNames;
    }

    public void setGlyphNames(String[] glyphNamesValue) {
        this.glyphNames = glyphNamesValue;
    }

    public String getName(int gid) {
        if (gid < 0 || this.glyphNames == null || gid >= this.glyphNames.length) {
            return null;
        }
        return this.glyphNames[gid];
    }
}
