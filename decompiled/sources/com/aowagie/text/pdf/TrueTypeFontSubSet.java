package com.aowagie.text.pdf;

import com.aowagie.text.DocumentException;
import com.aowagie.text.ExceptionConverter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.apache.fontbox.ttf.CmapTable;
import org.apache.fontbox.ttf.GlyphTable;
import org.apache.fontbox.ttf.HeaderTable;
import org.apache.fontbox.ttf.HorizontalHeaderTable;
import org.apache.fontbox.ttf.HorizontalMetricsTable;
import org.apache.fontbox.ttf.IndexToLocationTable;
import org.apache.fontbox.ttf.MaximumProfileTable;
import org.apache.fontbox.ttf.OS2WindowsMetricsTable;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/TrueTypeFontSubSet.class */
class TrueTypeFontSubSet {
    private static final String[] tableNamesSimple = {"cvt ", "fpgm", GlyphTable.TAG, HeaderTable.TAG, HorizontalHeaderTable.TAG, HorizontalMetricsTable.TAG, IndexToLocationTable.TAG, MaximumProfileTable.TAG, "prep"};
    private static final String[] tableNamesCmap = {CmapTable.TAG, "cvt ", "fpgm", GlyphTable.TAG, HeaderTable.TAG, HorizontalHeaderTable.TAG, HorizontalMetricsTable.TAG, IndexToLocationTable.TAG, MaximumProfileTable.TAG, "prep"};
    private static final String[] tableNamesExtra = {OS2WindowsMetricsTable.TAG, CmapTable.TAG, "cvt ", "fpgm", GlyphTable.TAG, HeaderTable.TAG, HorizontalHeaderTable.TAG, HorizontalMetricsTable.TAG, IndexToLocationTable.TAG, MaximumProfileTable.TAG, "name, prep"};
    private static final int[] entrySelectors = {0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4};
    private static final int TABLE_CHECKSUM = 0;
    private static final int TABLE_OFFSET = 1;
    private static final int TABLE_LENGTH = 2;
    static final int HEAD_LOCA_FORMAT_OFFSET = 51;
    private static final int ARG_1_AND_2_ARE_WORDS = 1;
    private static final int WE_HAVE_A_SCALE = 8;
    private static final int MORE_COMPONENTS = 32;
    private static final int WE_HAVE_AN_X_AND_Y_SCALE = 64;
    private static final int WE_HAVE_A_TWO_BY_TWO = 128;
    private HashMap tableDirectory;
    private final RandomAccessFileOrArray rf;
    private final String fileName;
    private final boolean includeCmap;
    private final boolean includeExtras;
    private boolean locaShortTable;
    private int[] locaTable;
    private final HashMap glyphsUsed;
    private final ArrayList glyphsInList;
    private int tableGlyphOffset;
    private int[] newLocaTable;
    private byte[] newLocaTableOut;
    private byte[] newGlyfTable;
    private int glyfTableRealSize;
    private int locaTableRealSize;
    private byte[] outFont;
    private int fontPtr;
    private final int directoryOffset;

    TrueTypeFontSubSet(String fileName, RandomAccessFileOrArray rf, HashMap glyphsUsed, int directoryOffset, boolean includeCmap, boolean includeExtras) {
        this.fileName = fileName;
        this.rf = rf;
        this.glyphsUsed = glyphsUsed;
        this.includeCmap = includeCmap;
        this.includeExtras = includeExtras;
        this.directoryOffset = directoryOffset;
        this.glyphsInList = new ArrayList(glyphsUsed.keySet());
    }

    byte[] process() throws IOException, DocumentException {
        try {
            this.rf.reOpen();
            createTableDirectory();
            readLoca();
            flatGlyphs();
            createNewGlyphTables();
            locaTobytes();
            assembleFont();
            return this.outFont;
        } finally {
            try {
                this.rf.close();
            } catch (Exception e) {
            }
        }
    }

    private void assembleFont() throws IOException {
        String[] tableNames;
        int len;
        int[] tableLocation;
        int fullFontSize = 0;
        if (this.includeExtras) {
            tableNames = tableNamesExtra;
        } else if (this.includeCmap) {
            tableNames = tableNamesCmap;
        } else {
            tableNames = tableNamesSimple;
        }
        int tablesUsed = 2;
        for (String name : tableNames) {
            if (!name.equals(GlyphTable.TAG) && !name.equals(IndexToLocationTable.TAG) && (tableLocation = (int[]) this.tableDirectory.get(name)) != null) {
                tablesUsed++;
                fullFontSize += (tableLocation[2] + 3) & (-4);
            }
        }
        int ref = (16 * tablesUsed) + 12;
        this.outFont = new byte[fullFontSize + this.newLocaTableOut.length + this.newGlyfTable.length + ref];
        this.fontPtr = 0;
        writeFontInt(65536);
        writeFontShort(tablesUsed);
        int selector = entrySelectors[tablesUsed];
        writeFontShort((1 << selector) * 16);
        writeFontShort(selector);
        writeFontShort((tablesUsed - (1 << selector)) * 16);
        for (String name2 : tableNames) {
            int[] tableLocation2 = (int[]) this.tableDirectory.get(name2);
            if (tableLocation2 != null) {
                writeFontString(name2);
                if (name2.equals(GlyphTable.TAG)) {
                    writeFontInt(calculateChecksum(this.newGlyfTable));
                    len = this.glyfTableRealSize;
                } else if (name2.equals(IndexToLocationTable.TAG)) {
                    writeFontInt(calculateChecksum(this.newLocaTableOut));
                    len = this.locaTableRealSize;
                } else {
                    writeFontInt(tableLocation2[0]);
                    len = tableLocation2[2];
                }
                writeFontInt(ref);
                writeFontInt(len);
                ref += (len + 3) & (-4);
            }
        }
        for (String name3 : tableNames) {
            int[] tableLocation3 = (int[]) this.tableDirectory.get(name3);
            if (tableLocation3 != null) {
                if (name3.equals(GlyphTable.TAG)) {
                    System.arraycopy(this.newGlyfTable, 0, this.outFont, this.fontPtr, this.newGlyfTable.length);
                    this.fontPtr += this.newGlyfTable.length;
                    this.newGlyfTable = null;
                } else if (name3.equals(IndexToLocationTable.TAG)) {
                    System.arraycopy(this.newLocaTableOut, 0, this.outFont, this.fontPtr, this.newLocaTableOut.length);
                    this.fontPtr += this.newLocaTableOut.length;
                    this.newLocaTableOut = null;
                } else {
                    this.rf.seek(tableLocation3[1]);
                    this.rf.readFully(this.outFont, this.fontPtr, tableLocation3[2]);
                    this.fontPtr += (tableLocation3[2] + 3) & (-4);
                }
            }
        }
    }

    private void createTableDirectory() throws IOException, DocumentException {
        this.tableDirectory = new HashMap();
        this.rf.seek(this.directoryOffset);
        int id = this.rf.readInt();
        if (id != 65536) {
            throw new DocumentException(this.fileName + " is not a true type file.");
        }
        int num_tables = this.rf.readUnsignedShort();
        this.rf.skipBytes(6);
        for (int k = 0; k < num_tables; k++) {
            String tag = readStandardString(4);
            int[] tableLocation = {this.rf.readInt(), this.rf.readInt(), this.rf.readInt()};
            this.tableDirectory.put(tag, tableLocation);
        }
    }

    private void readLoca() throws IOException, DocumentException {
        int[] tableLocation = (int[]) this.tableDirectory.get(HeaderTable.TAG);
        if (tableLocation == null) {
            throw new DocumentException("Table 'head' does not exist in " + this.fileName);
        }
        this.rf.seek(tableLocation[1] + 51);
        this.locaShortTable = this.rf.readUnsignedShort() == 0;
        int[] tableLocation2 = (int[]) this.tableDirectory.get(IndexToLocationTable.TAG);
        if (tableLocation2 == null) {
            throw new DocumentException("Table 'loca' does not exist in " + this.fileName);
        }
        this.rf.seek(tableLocation2[1]);
        if (this.locaShortTable) {
            int entries = tableLocation2[2] / 2;
            this.locaTable = new int[entries];
            for (int k = 0; k < entries; k++) {
                this.locaTable[k] = this.rf.readUnsignedShort() * 2;
            }
            return;
        }
        int entries2 = tableLocation2[2] / 4;
        this.locaTable = new int[entries2];
        for (int k2 = 0; k2 < entries2; k2++) {
            this.locaTable[k2] = this.rf.readInt();
        }
    }

    private void createNewGlyphTables() throws IOException {
        this.newLocaTable = new int[this.locaTable.length];
        int[] activeGlyphs = new int[this.glyphsInList.size()];
        for (int k = 0; k < activeGlyphs.length; k++) {
            activeGlyphs[k] = ((Integer) this.glyphsInList.get(k)).intValue();
        }
        Arrays.sort(activeGlyphs);
        int glyfSize = 0;
        for (int glyph : activeGlyphs) {
            glyfSize += this.locaTable[glyph + 1] - this.locaTable[glyph];
        }
        this.glyfTableRealSize = glyfSize;
        this.newGlyfTable = new byte[(glyfSize + 3) & (-4)];
        int glyfPtr = 0;
        int listGlyf = 0;
        for (int k2 = 0; k2 < this.newLocaTable.length; k2++) {
            this.newLocaTable[k2] = glyfPtr;
            if (listGlyf < activeGlyphs.length && activeGlyphs[listGlyf] == k2) {
                listGlyf++;
                this.newLocaTable[k2] = glyfPtr;
                int start = this.locaTable[k2];
                int len = this.locaTable[k2 + 1] - start;
                if (len > 0) {
                    this.rf.seek(this.tableGlyphOffset + start);
                    this.rf.readFully(this.newGlyfTable, glyfPtr, len);
                    glyfPtr += len;
                }
            }
        }
    }

    private void locaTobytes() {
        if (this.locaShortTable) {
            this.locaTableRealSize = this.newLocaTable.length * 2;
        } else {
            this.locaTableRealSize = this.newLocaTable.length * 4;
        }
        this.newLocaTableOut = new byte[(this.locaTableRealSize + 3) & (-4)];
        this.outFont = this.newLocaTableOut;
        this.fontPtr = 0;
        for (int element : this.newLocaTable) {
            if (this.locaShortTable) {
                writeFontShort(element / 2);
            } else {
                writeFontInt(element);
            }
        }
    }

    private void flatGlyphs() throws IOException, DocumentException {
        int[] tableLocation = (int[]) this.tableDirectory.get(GlyphTable.TAG);
        if (tableLocation == null) {
            throw new DocumentException("Table 'glyf' does not exist in " + this.fileName);
        }
        if (!this.glyphsUsed.containsKey(0)) {
            this.glyphsUsed.put(0, null);
            this.glyphsInList.add(0);
        }
        this.tableGlyphOffset = tableLocation[1];
        for (int k = 0; k < this.glyphsInList.size(); k++) {
            int glyph = ((Integer) this.glyphsInList.get(k)).intValue();
            checkGlyphComposite(glyph);
        }
    }

    private void checkGlyphComposite(int glyph) throws IOException {
        int skip;
        int start = this.locaTable[glyph];
        if (start == this.locaTable[glyph + 1]) {
            return;
        }
        this.rf.seek(this.tableGlyphOffset + start);
        int numContours = this.rf.readShort();
        if (numContours >= 0) {
            return;
        }
        this.rf.skipBytes(8);
        while (true) {
            int flags = this.rf.readUnsignedShort();
            Integer cGlyph = Integer.valueOf(this.rf.readUnsignedShort());
            if (!this.glyphsUsed.containsKey(cGlyph)) {
                this.glyphsUsed.put(cGlyph, null);
                this.glyphsInList.add(cGlyph);
            }
            if ((flags & 32) == 0) {
                return;
            }
            if ((flags & 1) != 0) {
                skip = 4;
            } else {
                skip = 2;
            }
            if ((flags & 8) != 0) {
                skip += 2;
            } else if ((flags & 64) != 0) {
                skip += 4;
            }
            if ((flags & 128) != 0) {
                skip += 8;
            }
            this.rf.skipBytes(skip);
        }
    }

    private String readStandardString(int length) throws IOException {
        byte[] buf = new byte[length];
        this.rf.readFully(buf);
        try {
            return new String(buf, "Cp1252");
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    private void writeFontShort(int n) {
        byte[] bArr = this.outFont;
        int i = this.fontPtr;
        this.fontPtr = i + 1;
        bArr[i] = (byte) (n >> 8);
        byte[] bArr2 = this.outFont;
        int i2 = this.fontPtr;
        this.fontPtr = i2 + 1;
        bArr2[i2] = (byte) n;
    }

    private void writeFontInt(int n) {
        byte[] bArr = this.outFont;
        int i = this.fontPtr;
        this.fontPtr = i + 1;
        bArr[i] = (byte) (n >> 24);
        byte[] bArr2 = this.outFont;
        int i2 = this.fontPtr;
        this.fontPtr = i2 + 1;
        bArr2[i2] = (byte) (n >> 16);
        byte[] bArr3 = this.outFont;
        int i3 = this.fontPtr;
        this.fontPtr = i3 + 1;
        bArr3[i3] = (byte) (n >> 8);
        byte[] bArr4 = this.outFont;
        int i4 = this.fontPtr;
        this.fontPtr = i4 + 1;
        bArr4[i4] = (byte) n;
    }

    private void writeFontString(String s) {
        byte[] b = PdfEncodings.convertToBytes(s, "Cp1252");
        System.arraycopy(b, 0, this.outFont, this.fontPtr, b.length);
        this.fontPtr += b.length;
    }

    private int calculateChecksum(byte[] b) {
        int len = b.length / 4;
        int v0 = 0;
        int v1 = 0;
        int v2 = 0;
        int v3 = 0;
        int ptr = 0;
        for (int k = 0; k < len; k++) {
            int i = ptr;
            int ptr2 = ptr + 1;
            v3 += b[i] & 255;
            int ptr3 = ptr2 + 1;
            v2 += b[ptr2] & 255;
            int ptr4 = ptr3 + 1;
            v1 += b[ptr3] & 255;
            ptr = ptr4 + 1;
            v0 += b[ptr4] & 255;
        }
        return v0 + (v1 << 8) + (v2 << 16) + (v3 << 24);
    }
}
