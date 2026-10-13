package org.apache.fontbox.ttf;

import java.io.EOFException;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/OS2WindowsMetricsTable.class */
public class OS2WindowsMetricsTable extends TTFTable {
    private static final Log LOG = LogFactory.getLog(OS2WindowsMetricsTable.class);
    public static final int WEIGHT_CLASS_THIN = 100;
    public static final int WEIGHT_CLASS_ULTRA_LIGHT = 200;
    public static final int WEIGHT_CLASS_LIGHT = 300;
    public static final int WEIGHT_CLASS_NORMAL = 400;
    public static final int WEIGHT_CLASS_MEDIUM = 500;
    public static final int WEIGHT_CLASS_SEMI_BOLD = 600;
    public static final int WEIGHT_CLASS_BOLD = 700;
    public static final int WEIGHT_CLASS_EXTRA_BOLD = 800;
    public static final int WEIGHT_CLASS_BLACK = 900;
    public static final int WIDTH_CLASS_ULTRA_CONDENSED = 1;
    public static final int WIDTH_CLASS_EXTRA_CONDENSED = 2;
    public static final int WIDTH_CLASS_CONDENSED = 3;
    public static final int WIDTH_CLASS_SEMI_CONDENSED = 4;
    public static final int WIDTH_CLASS_MEDIUM = 5;
    public static final int WIDTH_CLASS_SEMI_EXPANDED = 6;
    public static final int WIDTH_CLASS_EXPANDED = 7;
    public static final int WIDTH_CLASS_EXTRA_EXPANDED = 8;
    public static final int WIDTH_CLASS_ULTRA_EXPANDED = 9;
    public static final int FAMILY_CLASS_NO_CLASSIFICATION = 0;
    public static final int FAMILY_CLASS_OLDSTYLE_SERIFS = 1;
    public static final int FAMILY_CLASS_TRANSITIONAL_SERIFS = 2;
    public static final int FAMILY_CLASS_MODERN_SERIFS = 3;
    public static final int FAMILY_CLASS_CLAREDON_SERIFS = 4;
    public static final int FAMILY_CLASS_SLAB_SERIFS = 5;
    public static final int FAMILY_CLASS_FREEFORM_SERIFS = 7;
    public static final int FAMILY_CLASS_SANS_SERIF = 8;
    public static final int FAMILY_CLASS_ORNAMENTALS = 9;
    public static final int FAMILY_CLASS_SCRIPTS = 10;
    public static final int FAMILY_CLASS_SYMBOLIC = 12;
    public static final short FSTYPE_RESTRICTED = 2;
    public static final short FSTYPE_PREVIEW_AND_PRINT = 4;
    public static final short FSTYPE_EDITIBLE = 8;
    public static final short FSTYPE_NO_SUBSETTING = 256;
    public static final short FSTYPE_BITMAP_ONLY = 512;
    private int version;
    private short averageCharWidth;
    private int weightClass;
    private int widthClass;
    private short fsType;
    private short subscriptXSize;
    private short subscriptYSize;
    private short subscriptXOffset;
    private short subscriptYOffset;
    private short superscriptXSize;
    private short superscriptYSize;
    private short superscriptXOffset;
    private short superscriptYOffset;
    private short strikeoutSize;
    private short strikeoutPosition;
    private int familyClass;
    private byte[] panose;
    private long unicodeRange1;
    private long unicodeRange2;
    private long unicodeRange3;
    private long unicodeRange4;
    private String achVendId;
    private int fsSelection;
    private int firstCharIndex;
    private int lastCharIndex;
    private int typoAscender;
    private int typoDescender;
    private int typoLineGap;
    private int winAscent;
    private int winDescent;
    private long codePageRange1;
    private long codePageRange2;
    private int sxHeight;
    private int sCapHeight;
    private int usDefaultChar;
    private int usBreakChar;
    private int usMaxContext;
    public static final String TAG = "OS/2";

    OS2WindowsMetricsTable(TrueTypeFont font) {
        super(font);
        this.panose = new byte[10];
        this.achVendId = "XXXX";
        this.codePageRange1 = 0L;
        this.codePageRange2 = 0L;
    }

    public String getAchVendId() {
        return this.achVendId;
    }

    public void setAchVendId(String achVendIdValue) {
        this.achVendId = achVendIdValue;
    }

    public short getAverageCharWidth() {
        return this.averageCharWidth;
    }

    public void setAverageCharWidth(short averageCharWidthValue) {
        this.averageCharWidth = averageCharWidthValue;
    }

    public long getCodePageRange1() {
        return this.codePageRange1;
    }

    public void setCodePageRange1(long codePageRange1Value) {
        this.codePageRange1 = codePageRange1Value;
    }

    public long getCodePageRange2() {
        return this.codePageRange2;
    }

    public void setCodePageRange2(long codePageRange2Value) {
        this.codePageRange2 = codePageRange2Value;
    }

    public int getFamilyClass() {
        return this.familyClass;
    }

    public void setFamilyClass(int familyClassValue) {
        this.familyClass = familyClassValue;
    }

    public int getFirstCharIndex() {
        return this.firstCharIndex;
    }

    public void setFirstCharIndex(int firstCharIndexValue) {
        this.firstCharIndex = firstCharIndexValue;
    }

    public int getFsSelection() {
        return this.fsSelection;
    }

    public void setFsSelection(int fsSelectionValue) {
        this.fsSelection = fsSelectionValue;
    }

    public short getFsType() {
        return this.fsType;
    }

    public void setFsType(short fsTypeValue) {
        this.fsType = fsTypeValue;
    }

    public int getLastCharIndex() {
        return this.lastCharIndex;
    }

    public void setLastCharIndex(int lastCharIndexValue) {
        this.lastCharIndex = lastCharIndexValue;
    }

    public byte[] getPanose() {
        return this.panose;
    }

    public void setPanose(byte[] panoseValue) {
        this.panose = panoseValue;
    }

    public short getStrikeoutPosition() {
        return this.strikeoutPosition;
    }

    public void setStrikeoutPosition(short strikeoutPositionValue) {
        this.strikeoutPosition = strikeoutPositionValue;
    }

    public short getStrikeoutSize() {
        return this.strikeoutSize;
    }

    public void setStrikeoutSize(short strikeoutSizeValue) {
        this.strikeoutSize = strikeoutSizeValue;
    }

    public short getSubscriptXOffset() {
        return this.subscriptXOffset;
    }

    public void setSubscriptXOffset(short subscriptXOffsetValue) {
        this.subscriptXOffset = subscriptXOffsetValue;
    }

    public short getSubscriptXSize() {
        return this.subscriptXSize;
    }

    public void setSubscriptXSize(short subscriptXSizeValue) {
        this.subscriptXSize = subscriptXSizeValue;
    }

    public short getSubscriptYOffset() {
        return this.subscriptYOffset;
    }

    public void setSubscriptYOffset(short subscriptYOffsetValue) {
        this.subscriptYOffset = subscriptYOffsetValue;
    }

    public short getSubscriptYSize() {
        return this.subscriptYSize;
    }

    public void setSubscriptYSize(short subscriptYSizeValue) {
        this.subscriptYSize = subscriptYSizeValue;
    }

    public short getSuperscriptXOffset() {
        return this.superscriptXOffset;
    }

    public void setSuperscriptXOffset(short superscriptXOffsetValue) {
        this.superscriptXOffset = superscriptXOffsetValue;
    }

    public short getSuperscriptXSize() {
        return this.superscriptXSize;
    }

    public void setSuperscriptXSize(short superscriptXSizeValue) {
        this.superscriptXSize = superscriptXSizeValue;
    }

    public short getSuperscriptYOffset() {
        return this.superscriptYOffset;
    }

    public void setSuperscriptYOffset(short superscriptYOffsetValue) {
        this.superscriptYOffset = superscriptYOffsetValue;
    }

    public short getSuperscriptYSize() {
        return this.superscriptYSize;
    }

    public void setSuperscriptYSize(short superscriptYSizeValue) {
        this.superscriptYSize = superscriptYSizeValue;
    }

    public int getTypoLineGap() {
        return this.typoLineGap;
    }

    public void setTypoLineGap(int typeLineGapValue) {
        this.typoLineGap = typeLineGapValue;
    }

    public int getTypoAscender() {
        return this.typoAscender;
    }

    public void setTypoAscender(int typoAscenderValue) {
        this.typoAscender = typoAscenderValue;
    }

    public int getTypoDescender() {
        return this.typoDescender;
    }

    public void setTypoDescender(int typoDescenderValue) {
        this.typoDescender = typoDescenderValue;
    }

    public long getUnicodeRange1() {
        return this.unicodeRange1;
    }

    public void setUnicodeRange1(long unicodeRange1Value) {
        this.unicodeRange1 = unicodeRange1Value;
    }

    public long getUnicodeRange2() {
        return this.unicodeRange2;
    }

    public void setUnicodeRange2(long unicodeRange2Value) {
        this.unicodeRange2 = unicodeRange2Value;
    }

    public long getUnicodeRange3() {
        return this.unicodeRange3;
    }

    public void setUnicodeRange3(long unicodeRange3Value) {
        this.unicodeRange3 = unicodeRange3Value;
    }

    public long getUnicodeRange4() {
        return this.unicodeRange4;
    }

    public void setUnicodeRange4(long unicodeRange4Value) {
        this.unicodeRange4 = unicodeRange4Value;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int versionValue) {
        this.version = versionValue;
    }

    public int getWeightClass() {
        return this.weightClass;
    }

    public void setWeightClass(int weightClassValue) {
        this.weightClass = weightClassValue;
    }

    public int getWidthClass() {
        return this.widthClass;
    }

    public void setWidthClass(int widthClassValue) {
        this.widthClass = widthClassValue;
    }

    public int getWinAscent() {
        return this.winAscent;
    }

    public void setWinAscent(int winAscentValue) {
        this.winAscent = winAscentValue;
    }

    public int getWinDescent() {
        return this.winDescent;
    }

    public void setWinDescent(int winDescentValue) {
        this.winDescent = winDescentValue;
    }

    public int getHeight() {
        return this.sxHeight;
    }

    public int getCapHeight() {
        return this.sCapHeight;
    }

    public int getDefaultChar() {
        return this.usDefaultChar;
    }

    public int getBreakChar() {
        return this.usBreakChar;
    }

    public int getMaxContext() {
        return this.usMaxContext;
    }

    @Override // org.apache.fontbox.ttf.TTFTable
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        this.version = data.readUnsignedShort();
        this.averageCharWidth = data.readSignedShort();
        this.weightClass = data.readUnsignedShort();
        this.widthClass = data.readUnsignedShort();
        this.fsType = data.readSignedShort();
        this.subscriptXSize = data.readSignedShort();
        this.subscriptYSize = data.readSignedShort();
        this.subscriptXOffset = data.readSignedShort();
        this.subscriptYOffset = data.readSignedShort();
        this.superscriptXSize = data.readSignedShort();
        this.superscriptYSize = data.readSignedShort();
        this.superscriptXOffset = data.readSignedShort();
        this.superscriptYOffset = data.readSignedShort();
        this.strikeoutSize = data.readSignedShort();
        this.strikeoutPosition = data.readSignedShort();
        this.familyClass = data.readSignedShort();
        this.panose = data.read(10);
        this.unicodeRange1 = data.readUnsignedInt();
        this.unicodeRange2 = data.readUnsignedInt();
        this.unicodeRange3 = data.readUnsignedInt();
        this.unicodeRange4 = data.readUnsignedInt();
        this.achVendId = data.readString(4);
        this.fsSelection = data.readUnsignedShort();
        this.firstCharIndex = data.readUnsignedShort();
        this.lastCharIndex = data.readUnsignedShort();
        try {
            this.typoAscender = data.readSignedShort();
            this.typoDescender = data.readSignedShort();
            this.typoLineGap = data.readSignedShort();
            this.winAscent = data.readUnsignedShort();
            this.winDescent = data.readUnsignedShort();
            if (this.version >= 1) {
                try {
                    this.codePageRange1 = data.readUnsignedInt();
                    this.codePageRange2 = data.readUnsignedInt();
                } catch (EOFException ex) {
                    this.version = 0;
                    LOG.warn("Could not read all expected parts of version >= 1, setting version to 0", ex);
                    this.initialized = true;
                    return;
                }
            }
            if (this.version >= 2) {
                try {
                    this.sxHeight = data.readSignedShort();
                    this.sCapHeight = data.readSignedShort();
                    this.usDefaultChar = data.readUnsignedShort();
                    this.usBreakChar = data.readUnsignedShort();
                    this.usMaxContext = data.readUnsignedShort();
                } catch (EOFException ex2) {
                    this.version = 1;
                    LOG.warn("Could not read all expected parts of version >= 2, setting version to 1", ex2);
                    this.initialized = true;
                    return;
                }
            }
            this.initialized = true;
        } catch (EOFException e) {
            LOG.debug("EOF, probably some legacy TrueType font");
            this.initialized = true;
        }
    }
}
