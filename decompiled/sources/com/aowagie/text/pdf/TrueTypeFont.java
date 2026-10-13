package com.aowagie.text.pdf;

import com.aowagie.text.Document;
import com.aowagie.text.DocumentException;
import com.aowagie.text.ExceptionConverter;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEG;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.fontbox.ttf.CFFTable;
import org.apache.fontbox.ttf.CmapTable;
import org.apache.fontbox.ttf.GlyphTable;
import org.apache.fontbox.ttf.HeaderTable;
import org.apache.fontbox.ttf.HorizontalHeaderTable;
import org.apache.fontbox.ttf.HorizontalMetricsTable;
import org.apache.fontbox.ttf.IndexToLocationTable;
import org.apache.fontbox.ttf.KerningTable;
import org.apache.fontbox.ttf.OS2WindowsMetricsTable;
import org.apache.fontbox.ttf.PostScriptTable;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/TrueTypeFont.class */
class TrueTypeFont extends BaseFont {
    static final String[] codePages = {"1252 Latin 1", "1250 Latin 2: Eastern Europe", "1251 Cyrillic", "1253 Greek", "1254 Turkish", "1255 Hebrew", "1256 Arabic", "1257 Windows Baltic", "1258 Vietnamese", null, null, null, null, null, null, null, "874 Thai", "932 JIS/Japan", "936 Chinese: Simplified chars--PRC and Singapore", "949 Korean Wansung", "950 Chinese: Traditional chars--Taiwan and Hong Kong", "1361 Korean Johab", null, null, null, null, null, null, null, "Macintosh Character Set (US Roman)", "OEM Character Set", "Symbol Character Set", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "869 IBM Greek", "866 MS-DOS Russian", "865 MS-DOS Nordic", "864 Arabic", "863 MS-DOS Canadian French", "862 Hebrew", "861 MS-DOS Icelandic", "860 MS-DOS Portuguese", "857 IBM Turkish", "855 IBM Cyrillic; primarily Russian", "852 Latin 2", "775 MS-DOS Baltic", "737 Greek; former 437 G", "708 Arabic; ASMO 708", "850 WE/Latin 1", "437 US"};
    protected boolean justNames;
    protected HashMap tables;
    protected RandomAccessFileOrArray rf;
    protected String fileName;
    protected boolean cff;
    protected int cffOffset;
    protected int cffLength;
    protected int directoryOffset;
    protected String ttcIndex;
    protected String style;
    protected FontHeader head;
    protected HorizontalHeader hhea;
    protected WindowsMetrics os_2;
    protected int[] GlyphWidths;
    protected int[][] bboxes;
    protected HashMap cmap10;
    protected HashMap cmap31;
    protected HashMap cmapExt;
    protected IntHashtable kerning;
    protected String fontName;
    protected String[][] fullName;
    protected String[][] allNameEntries;
    protected String[][] familyName;
    protected double italicAngle;
    protected boolean isFixedPitch;
    protected int underlinePosition;
    protected int underlineThickness;

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/TrueTypeFont$FontHeader.class */
    protected static class FontHeader {
        int flags;
        int unitsPerEm;
        short xMin;
        short yMin;
        short xMax;
        short yMax;
        int macStyle;

        protected FontHeader() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/TrueTypeFont$HorizontalHeader.class */
    protected static class HorizontalHeader {
        short Ascender;
        short Descender;
        short LineGap;
        int advanceWidthMax;
        short minLeftSideBearing;
        short minRightSideBearing;
        short xMaxExtent;
        short caretSlopeRise;
        short caretSlopeRun;
        int numberOfHMetrics;

        protected HorizontalHeader() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/TrueTypeFont$WindowsMetrics.class */
    protected static class WindowsMetrics {
        short xAvgCharWidth;
        int usWeightClass;
        int usWidthClass;
        short fsType;
        short ySubscriptXSize;
        short ySubscriptYSize;
        short ySubscriptXOffset;
        short ySubscriptYOffset;
        short ySuperscriptXSize;
        short ySuperscriptYSize;
        short ySuperscriptXOffset;
        short ySuperscriptYOffset;
        short yStrikeoutSize;
        short yStrikeoutPosition;
        short sFamilyClass;
        byte[] panose = new byte[10];
        byte[] achVendID = new byte[4];
        int fsSelection;
        int usFirstCharIndex;
        int usLastCharIndex;
        short sTypoAscender;
        short sTypoDescender;
        short sTypoLineGap;
        int usWinAscent;
        int usWinDescent;
        int ulCodePageRange1;
        int ulCodePageRange2;
        int sCapHeight;

        protected WindowsMetrics() {
        }
    }

    protected TrueTypeFont() {
        this.justNames = false;
        this.cff = false;
        this.style = "";
        this.head = new FontHeader();
        this.hhea = new HorizontalHeader();
        this.os_2 = new WindowsMetrics();
        this.kerning = new IntHashtable();
        this.isFixedPitch = false;
    }

    TrueTypeFont(String ttFile, String enc, boolean emb, byte[] ttfAfm, boolean justNames, boolean forceRead) throws IOException, DocumentException {
        this.justNames = false;
        this.cff = false;
        this.style = "";
        this.head = new FontHeader();
        this.hhea = new HorizontalHeader();
        this.os_2 = new WindowsMetrics();
        this.kerning = new IntHashtable();
        this.isFixedPitch = false;
        this.justNames = justNames;
        String nameBase = getBaseName(ttFile);
        String ttcName = getTTCName(nameBase);
        if (nameBase.length() < ttFile.length()) {
            this.style = ttFile.substring(nameBase.length());
        }
        this.encoding = enc;
        this.embedded = emb;
        this.fileName = ttcName;
        this.fontType = 1;
        this.ttcIndex = "";
        if (ttcName.length() < nameBase.length()) {
            this.ttcIndex = nameBase.substring(ttcName.length() + 1);
        }
        if (this.fileName.toLowerCase().endsWith(".ttf") || this.fileName.toLowerCase().endsWith(".otf") || this.fileName.toLowerCase().endsWith(".ttc")) {
            process(ttfAfm, forceRead);
            if (!justNames && this.embedded && this.os_2.fsType == 2) {
                throw new DocumentException(this.fileName + this.style + " cannot be embedded due to licensing restrictions.");
            }
            if (!this.encoding.startsWith("#")) {
                PdfEncodings.convertToBytes(" ", enc);
            }
            createEncoding();
            return;
        }
        throw new DocumentException(this.fileName + this.style + " is not a TTF, OTF or TTC font file.");
    }

    protected static String getTTCName(String name) {
        int idx = name.toLowerCase().indexOf(".ttc,");
        if (idx < 0) {
            return name;
        }
        return name.substring(0, idx + 4);
    }

    void fillTables() throws IOException, DocumentException {
        int[] table_location = (int[]) this.tables.get(HeaderTable.TAG);
        if (table_location == null) {
            throw new DocumentException("Table 'head' does not exist in " + this.fileName + this.style);
        }
        this.rf.seek(table_location[0] + 16);
        this.head.flags = this.rf.readUnsignedShort();
        this.head.unitsPerEm = this.rf.readUnsignedShort();
        this.rf.skipBytes(16);
        this.head.xMin = this.rf.readShort();
        this.head.yMin = this.rf.readShort();
        this.head.xMax = this.rf.readShort();
        this.head.yMax = this.rf.readShort();
        this.head.macStyle = this.rf.readUnsignedShort();
        int[] table_location2 = (int[]) this.tables.get(HorizontalHeaderTable.TAG);
        if (table_location2 == null) {
            throw new DocumentException("Table 'hhea' does not exist " + this.fileName + this.style);
        }
        this.rf.seek(table_location2[0] + 4);
        this.hhea.Ascender = this.rf.readShort();
        this.hhea.Descender = this.rf.readShort();
        this.hhea.LineGap = this.rf.readShort();
        this.hhea.advanceWidthMax = this.rf.readUnsignedShort();
        this.hhea.minLeftSideBearing = this.rf.readShort();
        this.hhea.minRightSideBearing = this.rf.readShort();
        this.hhea.xMaxExtent = this.rf.readShort();
        this.hhea.caretSlopeRise = this.rf.readShort();
        this.hhea.caretSlopeRun = this.rf.readShort();
        this.rf.skipBytes(12);
        this.hhea.numberOfHMetrics = this.rf.readUnsignedShort();
        int[] table_location3 = (int[]) this.tables.get(OS2WindowsMetricsTable.TAG);
        if (table_location3 == null) {
            throw new DocumentException("Table 'OS/2' does not exist in " + this.fileName + this.style);
        }
        this.rf.seek(table_location3[0]);
        int version = this.rf.readUnsignedShort();
        this.os_2.xAvgCharWidth = this.rf.readShort();
        this.os_2.usWeightClass = this.rf.readUnsignedShort();
        this.os_2.usWidthClass = this.rf.readUnsignedShort();
        this.os_2.fsType = this.rf.readShort();
        this.os_2.ySubscriptXSize = this.rf.readShort();
        this.os_2.ySubscriptYSize = this.rf.readShort();
        this.os_2.ySubscriptXOffset = this.rf.readShort();
        this.os_2.ySubscriptYOffset = this.rf.readShort();
        this.os_2.ySuperscriptXSize = this.rf.readShort();
        this.os_2.ySuperscriptYSize = this.rf.readShort();
        this.os_2.ySuperscriptXOffset = this.rf.readShort();
        this.os_2.ySuperscriptYOffset = this.rf.readShort();
        this.os_2.yStrikeoutSize = this.rf.readShort();
        this.os_2.yStrikeoutPosition = this.rf.readShort();
        this.os_2.sFamilyClass = this.rf.readShort();
        this.rf.readFully(this.os_2.panose);
        this.rf.skipBytes(16);
        this.rf.readFully(this.os_2.achVendID);
        this.os_2.fsSelection = this.rf.readUnsignedShort();
        this.os_2.usFirstCharIndex = this.rf.readUnsignedShort();
        this.os_2.usLastCharIndex = this.rf.readUnsignedShort();
        this.os_2.sTypoAscender = this.rf.readShort();
        this.os_2.sTypoDescender = this.rf.readShort();
        if (this.os_2.sTypoDescender > 0) {
            this.os_2.sTypoDescender = (short) (-this.os_2.sTypoDescender);
        }
        this.os_2.sTypoLineGap = this.rf.readShort();
        this.os_2.usWinAscent = this.rf.readUnsignedShort();
        this.os_2.usWinDescent = this.rf.readUnsignedShort();
        this.os_2.ulCodePageRange1 = 0;
        this.os_2.ulCodePageRange2 = 0;
        if (version > 0) {
            this.os_2.ulCodePageRange1 = this.rf.readInt();
            this.os_2.ulCodePageRange2 = this.rf.readInt();
        }
        if (version > 1) {
            this.rf.skipBytes(2);
            this.os_2.sCapHeight = this.rf.readShort();
        } else {
            this.os_2.sCapHeight = (int) (0.7d * ((double) this.head.unitsPerEm));
        }
        int[] table_location4 = (int[]) this.tables.get(PostScriptTable.TAG);
        if (table_location4 == null) {
            this.italicAngle = ((-Math.atan2(this.hhea.caretSlopeRun, this.hhea.caretSlopeRise)) * 180.0d) / 3.141592653589793d;
            return;
        }
        this.rf.seek(table_location4[0] + 4);
        short mantissa = this.rf.readShort();
        int fraction = this.rf.readUnsignedShort();
        this.italicAngle = ((double) mantissa) + (((double) fraction) / 16384.0d);
        this.underlinePosition = this.rf.readShort();
        this.underlineThickness = this.rf.readShort();
        this.isFixedPitch = this.rf.readInt() != 0;
    }

    String getBaseFont() throws IOException, DocumentException {
        int[] table_location = (int[]) this.tables.get("name");
        if (table_location == null) {
            throw new DocumentException("Table 'name' does not exist in " + this.fileName + this.style);
        }
        this.rf.seek(table_location[0] + 2);
        int numRecords = this.rf.readUnsignedShort();
        int startOfStorage = this.rf.readUnsignedShort();
        for (int k = 0; k < numRecords; k++) {
            int platformID = this.rf.readUnsignedShort();
            this.rf.readUnsignedShort();
            this.rf.readUnsignedShort();
            int nameID = this.rf.readUnsignedShort();
            int length = this.rf.readUnsignedShort();
            int offset = this.rf.readUnsignedShort();
            if (nameID == 6) {
                this.rf.seek(table_location[0] + startOfStorage + offset);
                if (platformID == 0 || platformID == 3) {
                    return readUnicodeString(length);
                }
                return readStandardString(length);
            }
        }
        File file = new File(this.fileName);
        return file.getName().replace(' ', '-');
    }

    String[][] getNames(int id) throws IOException, DocumentException {
        String name;
        int[] table_location = (int[]) this.tables.get("name");
        if (table_location == null) {
            throw new DocumentException("Table 'name' does not exist in " + this.fileName + this.style);
        }
        this.rf.seek(table_location[0] + 2);
        int numRecords = this.rf.readUnsignedShort();
        int startOfStorage = this.rf.readUnsignedShort();
        ArrayList names = new ArrayList();
        for (int k = 0; k < numRecords; k++) {
            int platformID = this.rf.readUnsignedShort();
            int platformEncodingID = this.rf.readUnsignedShort();
            int languageID = this.rf.readUnsignedShort();
            int nameID = this.rf.readUnsignedShort();
            int length = this.rf.readUnsignedShort();
            int offset = this.rf.readUnsignedShort();
            if (nameID == id) {
                int pos = this.rf.getFilePointer();
                this.rf.seek(table_location[0] + startOfStorage + offset);
                if (platformID == 0 || platformID == 3 || (platformID == 2 && platformEncodingID == 1)) {
                    name = readUnicodeString(length);
                } else {
                    name = readStandardString(length);
                }
                names.add(new String[]{String.valueOf(platformID), String.valueOf(platformEncodingID), String.valueOf(languageID), name});
                this.rf.seek(pos);
            }
        }
        String[][] thisName = new String[names.size()][];
        for (int k2 = 0; k2 < names.size(); k2++) {
            thisName[k2] = (String[]) names.get(k2);
        }
        return thisName;
    }

    String[][] getAllNames() throws IOException, DocumentException {
        String unicodeString;
        int[] table_location = (int[]) this.tables.get("name");
        if (table_location == null) {
            throw new DocumentException("Table 'name' does not exist in " + this.fileName + this.style);
        }
        this.rf.seek(table_location[0] + 2);
        int numRecords = this.rf.readUnsignedShort();
        int startOfStorage = this.rf.readUnsignedShort();
        ArrayList names = new ArrayList();
        for (int k = 0; k < numRecords; k++) {
            int platformID = this.rf.readUnsignedShort();
            int platformEncodingID = this.rf.readUnsignedShort();
            int languageID = this.rf.readUnsignedShort();
            int nameID = this.rf.readUnsignedShort();
            int length = this.rf.readUnsignedShort();
            int offset = this.rf.readUnsignedShort();
            int pos = this.rf.getFilePointer();
            this.rf.seek(table_location[0] + startOfStorage + offset);
            if (platformID == 0 || platformID == 3 || (platformID == 2 && platformEncodingID == 1)) {
                unicodeString = readUnicodeString(length);
            } else {
                unicodeString = readStandardString(length);
            }
            String name = unicodeString;
            names.add(new String[]{String.valueOf(nameID), String.valueOf(platformID), String.valueOf(platformEncodingID), String.valueOf(languageID), name});
            this.rf.seek(pos);
        }
        String[][] thisName = new String[names.size()][];
        for (int k2 = 0; k2 < names.size(); k2++) {
            thisName[k2] = (String[]) names.get(k2);
        }
        return thisName;
    }

    void checkCff() {
        int[] table_location = (int[]) this.tables.get(CFFTable.TAG);
        if (table_location != null) {
            this.cff = true;
            this.cffOffset = table_location[0];
            this.cffLength = table_location[1];
        }
    }

    void process(byte[] ttfAfm, boolean preload) throws IOException, DocumentException {
        this.tables = new HashMap();
        try {
            if (ttfAfm == null) {
                this.rf = new RandomAccessFileOrArray(this.fileName, preload, Document.plainRandomAccess);
            } else {
                this.rf = new RandomAccessFileOrArray(ttfAfm);
            }
            if (this.ttcIndex.length() > 0) {
                int dirIdx = Integer.parseInt(this.ttcIndex);
                if (dirIdx < 0) {
                    throw new DocumentException("The font index for " + this.fileName + " must be positive.");
                }
                String mainTag = readStandardString(4);
                if (!mainTag.equals("ttcf")) {
                    throw new DocumentException(this.fileName + " is not a valid TTC file.");
                }
                this.rf.skipBytes(4);
                int dirCount = this.rf.readInt();
                if (dirIdx >= dirCount) {
                    throw new DocumentException("The font index for " + this.fileName + " must be between 0 and " + (dirCount - 1) + ". It was " + dirIdx + ".");
                }
                this.rf.skipBytes(dirIdx * 4);
                this.directoryOffset = this.rf.readInt();
            }
            this.rf.seek(this.directoryOffset);
            int ttId = this.rf.readInt();
            if (ttId != 65536 && ttId != 1330926671) {
                throw new DocumentException(this.fileName + " is not a valid TTF or OTF file.");
            }
            int num_tables = this.rf.readUnsignedShort();
            this.rf.skipBytes(6);
            for (int k = 0; k < num_tables; k++) {
                String tag = readStandardString(4);
                this.rf.skipBytes(4);
                int[] table_location = {this.rf.readInt(), this.rf.readInt()};
                this.tables.put(tag, table_location);
            }
            checkCff();
            this.fontName = getBaseFont();
            this.fullName = getNames(4);
            this.familyName = getNames(1);
            this.allNameEntries = getAllNames();
            if (!this.justNames) {
                fillTables();
                readGlyphWidths();
                readCMaps();
                readKerning();
                readBbox();
                this.GlyphWidths = null;
            }
            if (this.rf != null) {
                this.rf.close();
                if (!this.embedded) {
                    this.rf = null;
                }
            }
        } catch (Throwable th) {
            if (this.rf != null) {
                this.rf.close();
                if (!this.embedded) {
                    this.rf = null;
                }
            }
            throw th;
        }
    }

    protected String readStandardString(int length) throws IOException {
        byte[] buf = new byte[length];
        this.rf.readFully(buf);
        try {
            return new String(buf, "Cp1252");
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    protected String readUnicodeString(int length) throws IOException {
        StringBuffer buf = new StringBuffer();
        int length2 = length / 2;
        for (int k = 0; k < length2; k++) {
            buf.append(this.rf.readChar());
        }
        return buf.toString();
    }

    protected void readGlyphWidths() throws IOException, DocumentException {
        int[] table_location = (int[]) this.tables.get(HorizontalMetricsTable.TAG);
        if (table_location == null) {
            throw new DocumentException("Table 'hmtx' does not exist in " + this.fileName + this.style);
        }
        this.rf.seek(table_location[0]);
        this.GlyphWidths = new int[this.hhea.numberOfHMetrics];
        for (int k = 0; k < this.hhea.numberOfHMetrics; k++) {
            this.GlyphWidths[k] = (this.rf.readUnsignedShort() * 1000) / this.head.unitsPerEm;
            this.rf.readUnsignedShort();
        }
    }

    protected int getGlyphWidth(int glyph) {
        if (glyph >= this.GlyphWidths.length) {
            glyph = this.GlyphWidths.length - 1;
        }
        return this.GlyphWidths[glyph];
    }

    private void readBbox() throws IOException, DocumentException {
        int[] locaTable;
        int[] tableLocation = (int[]) this.tables.get(HeaderTable.TAG);
        if (tableLocation == null) {
            throw new DocumentException("Table 'head' does not exist in " + this.fileName + this.style);
        }
        this.rf.seek(tableLocation[0] + 51);
        boolean locaShortTable = this.rf.readUnsignedShort() == 0;
        int[] tableLocation2 = (int[]) this.tables.get(IndexToLocationTable.TAG);
        if (tableLocation2 == null) {
            return;
        }
        this.rf.seek(tableLocation2[0]);
        if (locaShortTable) {
            int entries = tableLocation2[1] / 2;
            locaTable = new int[entries];
            for (int k = 0; k < entries; k++) {
                locaTable[k] = this.rf.readUnsignedShort() * 2;
            }
        } else {
            int entries2 = tableLocation2[1] / 4;
            locaTable = new int[entries2];
            for (int k2 = 0; k2 < entries2; k2++) {
                locaTable[k2] = this.rf.readInt();
            }
        }
        int[] tableLocation3 = (int[]) this.tables.get(GlyphTable.TAG);
        if (tableLocation3 == null) {
            throw new DocumentException("Table 'glyf' does not exist in " + this.fileName + this.style);
        }
        int tableGlyphOffset = tableLocation3[0];
        this.bboxes = new int[locaTable.length - 1][];
        for (int glyph = 0; glyph < locaTable.length - 1; glyph++) {
            int start = locaTable[glyph];
            if (start != locaTable[glyph + 1]) {
                this.rf.seek(tableGlyphOffset + start + 2);
                this.bboxes[glyph] = new int[]{(this.rf.readShort() * 1000) / this.head.unitsPerEm, (this.rf.readShort() * 1000) / this.head.unitsPerEm, (this.rf.readShort() * 1000) / this.head.unitsPerEm, (this.rf.readShort() * 1000) / this.head.unitsPerEm};
            }
        }
    }

    void readCMaps() throws IOException, DocumentException {
        int[] table_location = (int[]) this.tables.get(CmapTable.TAG);
        if (table_location == null) {
            throw new DocumentException("Table 'cmap' does not exist in " + this.fileName + this.style);
        }
        this.rf.seek(table_location[0]);
        this.rf.skipBytes(2);
        int num_tables = this.rf.readUnsignedShort();
        this.fontSpecific = false;
        int map10 = 0;
        int map31 = 0;
        int map30 = 0;
        int mapExt = 0;
        for (int k = 0; k < num_tables; k++) {
            int platId = this.rf.readUnsignedShort();
            int platSpecId = this.rf.readUnsignedShort();
            int offset = this.rf.readInt();
            if (platId == 3 && platSpecId == 0) {
                this.fontSpecific = true;
                map30 = offset;
            } else if (platId == 3 && platSpecId == 1) {
                map31 = offset;
            } else if (platId == 3 && platSpecId == 10) {
                mapExt = offset;
            }
            if (platId == 1 && platSpecId == 0) {
                map10 = offset;
            }
        }
        if (map10 > 0) {
            this.rf.seek(table_location[0] + map10);
            int format = this.rf.readUnsignedShort();
            switch (format) {
                case 0:
                    this.cmap10 = readFormat0();
                    break;
                case 4:
                    this.cmap10 = readFormat4();
                    break;
                case 6:
                    this.cmap10 = readFormat6();
                    break;
            }
        }
        if (map31 > 0) {
            this.rf.seek(table_location[0] + map31);
            int format2 = this.rf.readUnsignedShort();
            if (format2 == 4) {
                this.cmap31 = readFormat4();
            }
        }
        if (map30 > 0) {
            this.rf.seek(table_location[0] + map30);
            int format3 = this.rf.readUnsignedShort();
            if (format3 == 4) {
                this.cmap10 = readFormat4();
            }
        }
        if (mapExt > 0) {
            this.rf.seek(table_location[0] + mapExt);
            int format4 = this.rf.readUnsignedShort();
            switch (format4) {
                case 0:
                    this.cmapExt = readFormat0();
                    return;
                case 4:
                    this.cmapExt = readFormat4();
                    return;
                case 6:
                    this.cmapExt = readFormat6();
                    return;
                case 12:
                    this.cmapExt = readFormat12();
                    return;
                default:
                    return;
            }
        }
    }

    HashMap readFormat12() throws IOException {
        HashMap h = new HashMap();
        this.rf.skipBytes(2);
        this.rf.readInt();
        this.rf.skipBytes(4);
        int nGroups = this.rf.readInt();
        for (int k = 0; k < nGroups; k++) {
            int startCharCode = this.rf.readInt();
            int endCharCode = this.rf.readInt();
            int startGlyphID = this.rf.readInt();
            for (int i = startCharCode; i <= endCharCode; i++) {
                int[] r = new int[2];
                r[0] = startGlyphID;
                r[1] = getGlyphWidth(r[0]);
                h.put(new Integer(i), r);
                startGlyphID++;
            }
        }
        return h;
    }

    HashMap readFormat0() throws IOException {
        HashMap h = new HashMap();
        this.rf.skipBytes(4);
        for (int k = 0; k < 256; k++) {
            int[] r = new int[2];
            r[0] = this.rf.readUnsignedByte();
            r[1] = getGlyphWidth(r[0]);
            h.put(new Integer(k), r);
        }
        return h;
    }

    HashMap readFormat4() throws IOException {
        int glyph;
        HashMap h = new HashMap();
        int table_lenght = this.rf.readUnsignedShort();
        this.rf.skipBytes(2);
        int segCount = this.rf.readUnsignedShort() / 2;
        this.rf.skipBytes(6);
        int[] endCount = new int[segCount];
        for (int k = 0; k < segCount; k++) {
            endCount[k] = this.rf.readUnsignedShort();
        }
        this.rf.skipBytes(2);
        int[] startCount = new int[segCount];
        for (int k2 = 0; k2 < segCount; k2++) {
            startCount[k2] = this.rf.readUnsignedShort();
        }
        int[] idDelta = new int[segCount];
        for (int k3 = 0; k3 < segCount; k3++) {
            idDelta[k3] = this.rf.readUnsignedShort();
        }
        int[] idRO = new int[segCount];
        for (int k4 = 0; k4 < segCount; k4++) {
            idRO[k4] = this.rf.readUnsignedShort();
        }
        int[] glyphId = new int[((table_lenght / 2) - 8) - (segCount * 4)];
        for (int k5 = 0; k5 < glyphId.length; k5++) {
            glyphId[k5] = this.rf.readUnsignedShort();
        }
        for (int k6 = 0; k6 < segCount; k6++) {
            int j = startCount[k6];
            while (j <= endCount[k6] && j != 65535) {
                if (idRO[k6] == 0) {
                    glyph = (j + idDelta[k6]) & 65535;
                } else {
                    int idx = (((k6 + (idRO[k6] / 2)) - segCount) + j) - startCount[k6];
                    if (idx < glyphId.length) {
                        glyph = (glyphId[idx] + idDelta[k6]) & 65535;
                    }
                    j++;
                }
                int[] r = new int[2];
                r[0] = glyph;
                r[1] = getGlyphWidth(r[0]);
                int i = (this.fontSpecific && (j & 65280) == 61440) ? j & 255 : j;
                h.put(new Integer(i), r);
                j++;
            }
        }
        return h;
    }

    HashMap readFormat6() throws IOException {
        HashMap h = new HashMap();
        this.rf.skipBytes(4);
        int start_code = this.rf.readUnsignedShort();
        int code_count = this.rf.readUnsignedShort();
        for (int k = 0; k < code_count; k++) {
            int[] r = new int[2];
            r[0] = this.rf.readUnsignedShort();
            r[1] = getGlyphWidth(r[0]);
            h.put(new Integer(k + start_code), r);
        }
        return h;
    }

    void readKerning() throws IOException {
        int[] table_location = (int[]) this.tables.get(KerningTable.TAG);
        if (table_location == null) {
            return;
        }
        this.rf.seek(table_location[0] + 2);
        int nTables = this.rf.readUnsignedShort();
        int checkpoint = table_location[0] + 4;
        int length = 0;
        for (int k = 0; k < nTables; k++) {
            checkpoint += length;
            this.rf.seek(checkpoint);
            this.rf.skipBytes(2);
            length = this.rf.readUnsignedShort();
            int coverage = this.rf.readUnsignedShort();
            if ((coverage & JPEG.SOF55) == 1) {
                int nPairs = this.rf.readUnsignedShort();
                this.rf.skipBytes(6);
                for (int j = 0; j < nPairs; j++) {
                    int pair = this.rf.readInt();
                    int value = (this.rf.readShort() * 1000) / this.head.unitsPerEm;
                    this.kerning.put(pair, value);
                }
            }
        }
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public int getKerning(int char1, int char2) {
        int[] metrics = getMetricsTT(char1);
        if (metrics == null) {
            return 0;
        }
        int c1 = metrics[0];
        int[] metrics2 = getMetricsTT(char2);
        if (metrics2 == null) {
            return 0;
        }
        int c2 = metrics2[0];
        return this.kerning.get((c1 << 16) + c2);
    }

    @Override // com.aowagie.text.pdf.BaseFont
    int getRawWidth(int c, String name) {
        int[] metric = getMetricsTT(c);
        if (metric == null) {
            return 0;
        }
        return metric[1];
    }

    protected PdfDictionary getFontDescriptor(PdfIndirectReference fontStream, String subsetPrefix, PdfIndirectReference cidset) {
        PdfDictionary dic = new PdfDictionary(PdfName.FONTDESCRIPTOR);
        dic.put(PdfName.ASCENT, new PdfNumber((this.os_2.sTypoAscender * 1000) / this.head.unitsPerEm));
        dic.put(PdfName.CAPHEIGHT, new PdfNumber((this.os_2.sCapHeight * 1000) / this.head.unitsPerEm));
        dic.put(PdfName.DESCENT, new PdfNumber((this.os_2.sTypoDescender * 1000) / this.head.unitsPerEm));
        dic.put(PdfName.FONTBBOX, new PdfRectangle((this.head.xMin * 1000) / this.head.unitsPerEm, (this.head.yMin * 1000) / this.head.unitsPerEm, (this.head.xMax * 1000) / this.head.unitsPerEm, (this.head.yMax * 1000) / this.head.unitsPerEm));
        if (cidset != null) {
            dic.put(PdfName.CIDSET, cidset);
        }
        if (this.cff && this.encoding.startsWith("Identity-")) {
            dic.put(PdfName.FONTNAME, new PdfName(subsetPrefix + this.fontName + "-" + this.encoding));
        } else {
            dic.put(PdfName.FONTNAME, new PdfName(subsetPrefix + this.fontName + this.style));
        }
        dic.put(PdfName.ITALICANGLE, new PdfNumber(this.italicAngle));
        dic.put(PdfName.STEMV, new PdfNumber(80));
        if (fontStream != null) {
            if (this.cff) {
                dic.put(PdfName.FONTFILE3, fontStream);
            } else {
                dic.put(PdfName.FONTFILE2, fontStream);
            }
        }
        int flags = 0;
        if (this.isFixedPitch) {
            flags = 0 | 1;
        }
        int flags2 = flags | (this.fontSpecific ? 4 : 32);
        if ((this.head.macStyle & 2) != 0) {
            flags2 |= 64;
        }
        if ((this.head.macStyle & 1) != 0) {
            flags2 |= 262144;
        }
        dic.put(PdfName.FLAGS, new PdfNumber(flags2));
        return dic;
    }

    protected PdfDictionary getFontBaseType(PdfIndirectReference fontDescriptor, String subsetPrefix, int firstChar, int lastChar, byte[] shortTag) {
        PdfDictionary pdfDictionary = new PdfDictionary(PdfName.FONT);
        if (this.cff) {
            pdfDictionary.put(PdfName.SUBTYPE, PdfName.TYPE1);
            pdfDictionary.put(PdfName.BASEFONT, new PdfName(this.fontName + this.style));
        } else {
            pdfDictionary.put(PdfName.SUBTYPE, PdfName.TRUETYPE);
            pdfDictionary.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName + this.style));
        }
        pdfDictionary.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName + this.style));
        if (!this.fontSpecific) {
            for (int k = firstChar; k <= lastChar; k++) {
                if (!this.differences[k].equals(BaseFont.notdef)) {
                    firstChar = k;
                    break;
                }
            }
            if (this.encoding.equals("Cp1252") || this.encoding.equals(BaseFont.MACROMAN)) {
                pdfDictionary.put(PdfName.ENCODING, this.encoding.equals("Cp1252") ? PdfName.WIN_ANSI_ENCODING : PdfName.MAC_ROMAN_ENCODING);
            } else {
                PdfDictionary enc = new PdfDictionary(PdfName.ENCODING);
                PdfArray dif = new PdfArray();
                boolean gap = true;
                for (int k2 = firstChar; k2 <= lastChar; k2++) {
                    if (shortTag[k2] != 0) {
                        if (gap) {
                            dif.add(new PdfNumber(k2));
                            gap = false;
                        }
                        dif.add(new PdfName(this.differences[k2]));
                    } else {
                        gap = true;
                    }
                }
                enc.put(PdfName.DIFFERENCES, dif);
                pdfDictionary.put(PdfName.ENCODING, enc);
            }
        }
        pdfDictionary.put(PdfName.FIRSTCHAR, new PdfNumber(firstChar));
        pdfDictionary.put(PdfName.LASTCHAR, new PdfNumber(lastChar));
        PdfArray wd = new PdfArray();
        for (int k3 = firstChar; k3 <= lastChar; k3++) {
            if (shortTag[k3] == 0) {
                wd.add(new PdfNumber(0));
            } else {
                wd.add(new PdfNumber(this.widths[k3]));
            }
        }
        pdfDictionary.put(PdfName.WIDTHS, wd);
        if (fontDescriptor != null) {
            pdfDictionary.put(PdfName.FONTDESCRIPTOR, fontDescriptor);
        }
        return pdfDictionary;
    }

    protected byte[] getFullFont() throws IOException {
        RandomAccessFileOrArray rf2 = null;
        try {
            RandomAccessFileOrArray rf3 = new RandomAccessFileOrArray(this.rf);
            rf3.reOpen();
            byte[] b = new byte[rf3.length()];
            rf3.readFully(b);
            return b;
        } finally {
            if (rf2 != null) {
                try {
                    rf2.close();
                } catch (Exception e) {
                }
            }
        }
    }

    protected static int[] compactRanges(ArrayList ranges) {
        ArrayList simp = new ArrayList();
        for (int k = 0; k < ranges.size(); k++) {
            int[] r = (int[]) ranges.get(k);
            for (int j = 0; j < r.length; j += 2) {
                simp.add(new int[]{Math.max(0, Math.min(r[j], r[j + 1])), Math.min(65535, Math.max(r[j], r[j + 1]))});
            }
        }
        for (int k1 = 0; k1 < simp.size() - 1; k1++) {
            int k2 = k1 + 1;
            while (k2 < simp.size()) {
                int[] r1 = (int[]) simp.get(k1);
                int[] r2 = (int[]) simp.get(k2);
                if ((r1[0] >= r2[0] && r1[0] <= r2[1]) || (r1[1] >= r2[0] && r1[0] <= r2[1])) {
                    r1[0] = Math.min(r1[0], r2[0]);
                    r1[1] = Math.max(r1[1], r2[1]);
                    simp.remove(k2);
                    k2--;
                }
                k2++;
            }
        }
        int[] s = new int[simp.size() * 2];
        for (int k3 = 0; k3 < simp.size(); k3++) {
            int[] r3 = (int[]) simp.get(k3);
            s[k3 * 2] = r3[0];
            s[(k3 * 2) + 1] = r3[1];
        }
        return s;
    }

    protected void addRangeUni(HashMap longTag, boolean includeMetrics, boolean subsetp) {
        HashMap usemap;
        if (subsetp) {
            return;
        }
        if (this.subsetRanges != null || this.directoryOffset > 0) {
            int[] rg = (this.subsetRanges != null || this.directoryOffset <= 0) ? compactRanges(this.subsetRanges) : new int[]{0, 65535};
            if (!this.fontSpecific && this.cmap31 != null) {
                usemap = this.cmap31;
            } else if ((!this.fontSpecific || this.cmap10 == null) && this.cmap31 != null) {
                usemap = this.cmap31;
            } else {
                usemap = this.cmap10;
            }
            for (Map.Entry e : usemap.entrySet()) {
                int[] v = (int[]) e.getValue();
                Integer gi = new Integer(v[0]);
                if (!longTag.containsKey(gi)) {
                    int c = ((Integer) e.getKey()).intValue();
                    boolean skip = true;
                    for (int k = 0; k < rg.length; k += 2) {
                        if (c >= rg[k] && c <= rg[k + 1]) {
                            skip = false;
                            break;
                        }
                    }
                    if (!skip) {
                        longTag.put(gi, includeMetrics ? new int[]{v[0], v[1], c} : null);
                    }
                }
            }
        }
    }

    @Override // com.aowagie.text.pdf.BaseFont
    void writeFont(PdfWriter writer, PdfIndirectReference ref, Object[] params) throws IOException, DocumentException {
        byte[] b;
        int firstChar = ((Integer) params[0]).intValue();
        int lastChar = ((Integer) params[1]).intValue();
        byte[] shortTag = (byte[]) params[2];
        boolean subsetp = ((Boolean) params[3]).booleanValue() && this.subset;
        if (!subsetp) {
            firstChar = 0;
            lastChar = shortTag.length - 1;
            for (int k = 0; k < shortTag.length; k++) {
                shortTag[k] = 1;
            }
        }
        PdfIndirectReference ind_font = null;
        String subsetPrefix = "";
        if (this.embedded) {
            if (this.cff) {
                PdfIndirectObject obj = writer.addToBody(new BaseFont.StreamFont(readCffFont(), "Type1C", this.compressionLevel));
                ind_font = obj.getIndirectReference();
            } else {
                if (subsetp) {
                    subsetPrefix = createSubsetPrefix();
                }
                HashMap glyphs = new HashMap();
                for (int k2 = firstChar; k2 <= lastChar; k2++) {
                    if (shortTag[k2] != 0) {
                        int[] metrics = null;
                        if (this.specialMap != null) {
                            int[] cd = GlyphList.nameToUnicode(this.differences[k2]);
                            if (cd != null) {
                                metrics = getMetricsTT(cd[0]);
                            }
                        } else if (this.fontSpecific) {
                            metrics = getMetricsTT(k2);
                        } else {
                            metrics = getMetricsTT(this.unicodeDifferences[k2]);
                        }
                        if (metrics != null) {
                            glyphs.put(new Integer(metrics[0]), null);
                        }
                    }
                }
                addRangeUni(glyphs, false, subsetp);
                if (subsetp || this.directoryOffset != 0 || this.subsetRanges != null) {
                    TrueTypeFontSubSet sb = new TrueTypeFontSubSet(this.fileName, new RandomAccessFileOrArray(this.rf), glyphs, this.directoryOffset, true, !subsetp);
                    b = sb.process();
                } else {
                    b = getFullFont();
                }
                int[] lengths = {b.length};
                PdfIndirectObject obj2 = writer.addToBody(new BaseFont.StreamFont(b, lengths, this.compressionLevel));
                ind_font = obj2.getIndirectReference();
            }
        }
        PdfObject pobj = getFontDescriptor(ind_font, subsetPrefix, null);
        if (pobj != null) {
            PdfIndirectObject obj3 = writer.addToBody(pobj);
            ind_font = obj3.getIndirectReference();
        }
        writer.addToBody(getFontBaseType(ind_font, subsetPrefix, firstChar, lastChar, shortTag), ref);
    }

    protected byte[] readCffFont() throws IOException {
        RandomAccessFileOrArray rf2 = new RandomAccessFileOrArray(this.rf);
        byte[] b = new byte[this.cffLength];
        try {
            rf2.reOpen();
            rf2.seek(this.cffOffset);
            rf2.readFully(b);
            return b;
        } finally {
            try {
                rf2.close();
            } catch (Exception e) {
            }
        }
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public PdfStream getFullFontStream() throws IOException, DocumentException {
        if (this.cff) {
            return new BaseFont.StreamFont(readCffFont(), "Type1C", this.compressionLevel);
        }
        byte[] b = getFullFont();
        int[] lengths = {b.length};
        return new BaseFont.StreamFont(b, lengths, this.compressionLevel);
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public float getFontDescriptor(int key, float fontSize) {
        switch (key) {
            case 1:
                return (this.os_2.sTypoAscender * fontSize) / this.head.unitsPerEm;
            case 2:
                return (this.os_2.sCapHeight * fontSize) / this.head.unitsPerEm;
            case 3:
                return (this.os_2.sTypoDescender * fontSize) / this.head.unitsPerEm;
            case 4:
                return (float) this.italicAngle;
            case 5:
                return (fontSize * this.head.xMin) / this.head.unitsPerEm;
            case 6:
                return (fontSize * this.head.yMin) / this.head.unitsPerEm;
            case 7:
                return (fontSize * this.head.xMax) / this.head.unitsPerEm;
            case 8:
                return (fontSize * this.head.yMax) / this.head.unitsPerEm;
            case 9:
                return (fontSize * this.hhea.Ascender) / this.head.unitsPerEm;
            case 10:
                return (fontSize * this.hhea.Descender) / this.head.unitsPerEm;
            case 11:
                return (fontSize * this.hhea.LineGap) / this.head.unitsPerEm;
            case 12:
                return (fontSize * this.hhea.advanceWidthMax) / this.head.unitsPerEm;
            case 13:
                return ((this.underlinePosition - (this.underlineThickness / 2)) * fontSize) / this.head.unitsPerEm;
            case 14:
                return (this.underlineThickness * fontSize) / this.head.unitsPerEm;
            case 15:
                return (this.os_2.yStrikeoutPosition * fontSize) / this.head.unitsPerEm;
            case 16:
                return (this.os_2.yStrikeoutSize * fontSize) / this.head.unitsPerEm;
            case 17:
                return (this.os_2.ySubscriptYSize * fontSize) / this.head.unitsPerEm;
            case 18:
                return ((-this.os_2.ySubscriptYOffset) * fontSize) / this.head.unitsPerEm;
            case 19:
                return (this.os_2.ySuperscriptYSize * fontSize) / this.head.unitsPerEm;
            case 20:
                return (this.os_2.ySuperscriptYOffset * fontSize) / this.head.unitsPerEm;
            default:
                return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
    }

    public int[] getMetricsTT(int c) {
        if (this.cmapExt != null) {
            return (int[]) this.cmapExt.get(new Integer(c));
        }
        if (!this.fontSpecific && this.cmap31 != null) {
            return (int[]) this.cmap31.get(new Integer(c));
        }
        if (this.fontSpecific && this.cmap10 != null) {
            return (int[]) this.cmap10.get(new Integer(c));
        }
        if (this.cmap31 != null) {
            return (int[]) this.cmap31.get(new Integer(c));
        }
        if (this.cmap10 != null) {
            return (int[]) this.cmap10.get(new Integer(c));
        }
        return null;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public String getPostscriptFontName() {
        return this.fontName;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public String[] getCodePagesSupported() {
        long cp = (((long) this.os_2.ulCodePageRange2) << 32) + (((long) this.os_2.ulCodePageRange1) & 4294967295L);
        int count = 0;
        long bit = 1;
        for (int k = 0; k < 64; k++) {
            if ((cp & bit) != 0 && codePages[k] != null) {
                count++;
            }
            bit <<= 1;
        }
        String[] ret = new String[count];
        int count2 = 0;
        long bit2 = 1;
        for (int k2 = 0; k2 < 64; k2++) {
            if ((cp & bit2) != 0 && codePages[k2] != null) {
                int i = count2;
                count2++;
                ret[i] = codePages[k2];
            }
            bit2 <<= 1;
        }
        return ret;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public String[][] getFullFontName() {
        return this.fullName;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public String[][] getAllNameEntries() {
        return this.allNameEntries;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public String[][] getFamilyFontName() {
        return this.familyName;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public boolean hasKernPairs() {
        return this.kerning.size() > 0;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public void setPostscriptFontName(String name) {
        this.fontName = name;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public boolean setKerning(int char1, int char2, int kern) {
        int[] metrics = getMetricsTT(char1);
        if (metrics == null) {
            return false;
        }
        int c1 = metrics[0];
        int[] metrics2 = getMetricsTT(char2);
        if (metrics2 == null) {
            return false;
        }
        int c2 = metrics2[0];
        this.kerning.put((c1 << 16) + c2, kern);
        return true;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    protected int[] getRawCharBBox(int c, String name) {
        HashMap map;
        int[] metric;
        if (name == null || this.cmap31 == null) {
            map = this.cmap10;
        } else {
            map = this.cmap31;
        }
        if (map == null || (metric = (int[]) map.get(new Integer(c))) == null || this.bboxes == null) {
            return null;
        }
        return this.bboxes[metric[0]];
    }
}
