package org.apache.fontbox.ttf;

import java.awt.geom.GeneralPath;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.util.BoundingBox;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/TrueTypeFont.class */
public class TrueTypeFont implements FontBoxFont, Closeable {
    private float version;
    private final TTFDataStream data;
    private Map<String, Integer> postScriptNames;
    private int numberOfGlyphs = -1;
    private int unitsPerEm = -1;
    protected Map<String, TTFTable> tables = new HashMap();
    private final List<String> enabledGsubFeatures = new ArrayList();

    TrueTypeFont(TTFDataStream fontData) {
        this.data = fontData;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.data.close();
    }

    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    public float getVersion() {
        return this.version;
    }

    void setVersion(float versionValue) {
        this.version = versionValue;
    }

    void addTable(TTFTable table) {
        this.tables.put(table.getTag(), table);
    }

    public Collection<TTFTable> getTables() {
        return this.tables.values();
    }

    public Map<String, TTFTable> getTableMap() {
        return this.tables;
    }

    public synchronized byte[] getTableBytes(TTFTable table) throws IOException {
        long currentPosition = this.data.getCurrentPosition();
        this.data.seek(table.getOffset());
        byte[] bytes = this.data.read((int) table.getLength());
        this.data.seek(currentPosition);
        return bytes;
    }

    protected synchronized TTFTable getTable(String tag) throws IOException {
        TTFTable ttfTable = this.tables.get(tag);
        if (ttfTable != null && !ttfTable.getInitialized()) {
            readTable(ttfTable);
        }
        return ttfTable;
    }

    public NamingTable getNaming() throws IOException {
        return (NamingTable) getTable("name");
    }

    public PostScriptTable getPostScript() throws IOException {
        return (PostScriptTable) getTable(PostScriptTable.TAG);
    }

    public OS2WindowsMetricsTable getOS2Windows() throws IOException {
        return (OS2WindowsMetricsTable) getTable(OS2WindowsMetricsTable.TAG);
    }

    public MaximumProfileTable getMaximumProfile() throws IOException {
        return (MaximumProfileTable) getTable(MaximumProfileTable.TAG);
    }

    public HeaderTable getHeader() throws IOException {
        return (HeaderTable) getTable(HeaderTable.TAG);
    }

    public HorizontalHeaderTable getHorizontalHeader() throws IOException {
        return (HorizontalHeaderTable) getTable(HorizontalHeaderTable.TAG);
    }

    public HorizontalMetricsTable getHorizontalMetrics() throws IOException {
        return (HorizontalMetricsTable) getTable(HorizontalMetricsTable.TAG);
    }

    public IndexToLocationTable getIndexToLocation() throws IOException {
        return (IndexToLocationTable) getTable(IndexToLocationTable.TAG);
    }

    public GlyphTable getGlyph() throws IOException {
        return (GlyphTable) getTable(GlyphTable.TAG);
    }

    public CmapTable getCmap() throws IOException {
        return (CmapTable) getTable(CmapTable.TAG);
    }

    public VerticalHeaderTable getVerticalHeader() throws IOException {
        return (VerticalHeaderTable) getTable(VerticalHeaderTable.TAG);
    }

    public VerticalMetricsTable getVerticalMetrics() throws IOException {
        return (VerticalMetricsTable) getTable(VerticalMetricsTable.TAG);
    }

    public VerticalOriginTable getVerticalOrigin() throws IOException {
        return (VerticalOriginTable) getTable(VerticalOriginTable.TAG);
    }

    public KerningTable getKerning() throws IOException {
        return (KerningTable) getTable(KerningTable.TAG);
    }

    public GlyphSubstitutionTable getGsub() throws IOException {
        return (GlyphSubstitutionTable) getTable(GlyphSubstitutionTable.TAG);
    }

    public InputStream getOriginalData() throws IOException {
        return this.data.getOriginalData();
    }

    public long getOriginalDataSize() {
        return this.data.getOriginalDataSize();
    }

    void readTable(TTFTable table) throws IOException {
        synchronized (this.data) {
            long currentPosition = this.data.getCurrentPosition();
            this.data.seek(table.getOffset());
            table.read(this, this.data);
            this.data.seek(currentPosition);
        }
    }

    public int getNumberOfGlyphs() throws IOException {
        if (this.numberOfGlyphs == -1) {
            MaximumProfileTable maximumProfile = getMaximumProfile();
            if (maximumProfile != null) {
                this.numberOfGlyphs = maximumProfile.getNumGlyphs();
            } else {
                this.numberOfGlyphs = 0;
            }
        }
        return this.numberOfGlyphs;
    }

    public int getUnitsPerEm() throws IOException {
        if (this.unitsPerEm == -1) {
            HeaderTable header = getHeader();
            if (header != null) {
                this.unitsPerEm = header.getUnitsPerEm();
            } else {
                this.unitsPerEm = 0;
            }
        }
        return this.unitsPerEm;
    }

    public int getAdvanceWidth(int gid) throws IOException {
        HorizontalMetricsTable hmtx = getHorizontalMetrics();
        if (hmtx != null) {
            return hmtx.getAdvanceWidth(gid);
        }
        return 250;
    }

    public int getAdvanceHeight(int gid) throws IOException {
        VerticalMetricsTable vmtx = getVerticalMetrics();
        if (vmtx != null) {
            return vmtx.getAdvanceHeight(gid);
        }
        return 250;
    }

    @Override // org.apache.fontbox.FontBoxFont
    public String getName() throws IOException {
        NamingTable namingTable = getNaming();
        if (namingTable != null) {
            return namingTable.getPostScriptName();
        }
        return null;
    }

    private synchronized void readPostScriptNames() throws IOException {
        if (this.postScriptNames == null && getPostScript() != null) {
            String[] names = getPostScript().getGlyphNames();
            if (names != null) {
                this.postScriptNames = new HashMap(names.length);
                for (int i = 0; i < names.length; i++) {
                    this.postScriptNames.put(names[i], Integer.valueOf(i));
                }
                return;
            }
            this.postScriptNames = new HashMap();
        }
    }

    @Deprecated
    public CmapSubtable getUnicodeCmap() throws IOException {
        return getUnicodeCmap(true);
    }

    @Deprecated
    public CmapSubtable getUnicodeCmap(boolean isStrict) throws IOException {
        return getUnicodeCmapImpl(isStrict);
    }

    public CmapLookup getUnicodeCmapLookup() throws IOException {
        return getUnicodeCmapLookup(true);
    }

    public CmapLookup getUnicodeCmapLookup(boolean isStrict) throws IOException {
        GlyphSubstitutionTable table;
        CmapSubtable cmap = getUnicodeCmapImpl(isStrict);
        if (!this.enabledGsubFeatures.isEmpty() && (table = getGsub()) != null) {
            return new SubstitutingCmapLookup(cmap, table, Collections.unmodifiableList(this.enabledGsubFeatures));
        }
        return cmap;
    }

    private CmapSubtable getUnicodeCmapImpl(boolean isStrict) throws IOException {
        CmapTable cmapTable = getCmap();
        if (cmapTable == null) {
            if (isStrict) {
                throw new IOException("The TrueType font " + getName() + " does not contain a 'cmap' table");
            }
            return null;
        }
        CmapSubtable cmap = cmapTable.getSubtable(0, 4);
        if (cmap == null) {
            cmap = cmapTable.getSubtable(3, 10);
        }
        if (cmap == null) {
            cmap = cmapTable.getSubtable(0, 3);
        }
        if (cmap == null) {
            cmap = cmapTable.getSubtable(3, 1);
        }
        if (cmap == null) {
            cmap = cmapTable.getSubtable(3, 0);
        }
        if (cmap == null) {
            if (isStrict) {
                throw new IOException("The TrueType font does not contain a Unicode cmap");
            }
            if (cmapTable.getCmaps().length > 0) {
                cmap = cmapTable.getCmaps()[0];
            }
        }
        return cmap;
    }

    public int nameToGID(String name) throws IOException {
        Integer gid;
        readPostScriptNames();
        if (this.postScriptNames != null && (gid = this.postScriptNames.get(name)) != null && gid.intValue() > 0 && gid.intValue() < getMaximumProfile().getNumGlyphs()) {
            return gid.intValue();
        }
        int uni = parseUniName(name);
        if (uni > -1) {
            CmapLookup cmap = getUnicodeCmapLookup(false);
            return cmap.getGlyphId(uni);
        }
        return 0;
    }

    private int parseUniName(String name) {
        if (name.startsWith("uni") && name.length() == 7) {
            int nameLength = name.length();
            StringBuilder uniStr = new StringBuilder();
            for (int chPos = 3; chPos + 4 <= nameLength; chPos += 4) {
                try {
                    int codePoint = Integer.parseInt(name.substring(chPos, chPos + 4), 16);
                    if (codePoint <= 55295 || codePoint >= 57344) {
                        uniStr.append((char) codePoint);
                    }
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
            String unicode = uniStr.toString();
            if (unicode.length() == 0) {
                return -1;
            }
            return unicode.codePointAt(0);
        }
        return -1;
    }

    @Override // org.apache.fontbox.FontBoxFont
    public GeneralPath getPath(String name) throws IOException {
        int gid = nameToGID(name);
        GlyphData glyph = getGlyph().getGlyph(gid);
        if (glyph == null) {
            return new GeneralPath();
        }
        return glyph.getPath();
    }

    @Override // org.apache.fontbox.FontBoxFont
    public float getWidth(String name) throws IOException {
        int gid = nameToGID(name);
        return getAdvanceWidth(gid);
    }

    @Override // org.apache.fontbox.FontBoxFont
    public boolean hasGlyph(String name) throws IOException {
        return nameToGID(name) != 0;
    }

    @Override // org.apache.fontbox.FontBoxFont
    public BoundingBox getFontBBox() throws IOException {
        HeaderTable headerTable = getHeader();
        short xMin = headerTable.getXMin();
        short xMax = headerTable.getXMax();
        short yMin = headerTable.getYMin();
        short yMax = headerTable.getYMax();
        float scale = 1000.0f / getUnitsPerEm();
        return new BoundingBox(xMin * scale, yMin * scale, xMax * scale, yMax * scale);
    }

    @Override // org.apache.fontbox.FontBoxFont
    public List<Number> getFontMatrix() throws IOException {
        float scale = 1000.0f / getUnitsPerEm();
        return Arrays.asList(Float.valueOf(0.001f * scale), 0, 0, Float.valueOf(0.001f * scale), 0, 0);
    }

    public void enableGsubFeature(String featureTag) {
        this.enabledGsubFeatures.add(featureTag);
    }

    public void disableGsubFeature(String featureTag) {
        this.enabledGsubFeatures.remove(featureTag);
    }

    public void enableVerticalSubstitutions() {
        enableGsubFeature("vrt2");
        enableGsubFeature("vert");
    }

    public String toString() {
        try {
            NamingTable namingTable = getNaming();
            if (namingTable != null) {
                return namingTable.getPostScriptName();
            }
            return "(null)";
        } catch (IOException e) {
            return "(null - " + e.getMessage() + ")";
        }
    }
}
