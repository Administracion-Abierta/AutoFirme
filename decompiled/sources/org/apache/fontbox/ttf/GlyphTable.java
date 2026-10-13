package org.apache.fontbox.ttf;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphTable.class */
public class GlyphTable extends TTFTable {
    public static final String TAG = "glyf";
    private GlyphData[] glyphs;
    private TTFDataStream data;
    private IndexToLocationTable loca;
    private int numGlyphs;
    private int cached;
    private static final int MAX_CACHE_SIZE = 5000;
    private static final int MAX_CACHED_GLYPHS = 100;

    GlyphTable(TrueTypeFont font) {
        super(font);
        this.cached = 0;
    }

    @Override // org.apache.fontbox.ttf.TTFTable
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        this.loca = ttf.getIndexToLocation();
        this.numGlyphs = ttf.getNumberOfGlyphs();
        if (this.numGlyphs < 5000) {
            this.glyphs = new GlyphData[this.numGlyphs];
        }
        this.data = data;
        this.initialized = true;
    }

    @Deprecated
    public GlyphData[] getGlyphs() throws IOException {
        GlyphData[] glyphDataArr;
        synchronized (this.data) {
            long[] offsets = this.loca.getOffsets();
            long endOfGlyphs = offsets[this.numGlyphs];
            long offset = getOffset();
            if (this.glyphs == null) {
                this.glyphs = new GlyphData[this.numGlyphs];
            }
            for (int gid = 0; gid < this.numGlyphs && (endOfGlyphs == 0 || endOfGlyphs != offsets[gid]); gid++) {
                if (offsets[gid + 1] > offsets[gid] && this.glyphs[gid] == null) {
                    this.data.seek(offset + offsets[gid]);
                    if (this.glyphs[gid] == null) {
                        this.cached++;
                    }
                    this.glyphs[gid] = getGlyphData(gid);
                }
            }
            this.initialized = true;
            glyphDataArr = this.glyphs;
        }
        return glyphDataArr;
    }

    public void setGlyphs(GlyphData[] glyphsValue) {
        this.glyphs = glyphsValue;
    }

    public GlyphData getGlyph(int gid) throws IOException {
        GlyphData glyph;
        GlyphData glyphData;
        if (gid < 0 || gid >= this.numGlyphs) {
            return null;
        }
        if (this.glyphs != null && this.glyphs[gid] != null) {
            return this.glyphs[gid];
        }
        synchronized (this.data) {
            long[] offsets = this.loca.getOffsets();
            if (offsets[gid] == offsets[gid + 1]) {
                glyph = new GlyphData();
                glyph.initEmptyData();
            } else {
                long currentPosition = this.data.getCurrentPosition();
                this.data.seek(getOffset() + offsets[gid]);
                glyph = getGlyphData(gid);
                this.data.seek(currentPosition);
            }
            if (this.glyphs != null && this.glyphs[gid] == null && this.cached < 100) {
                this.glyphs[gid] = glyph;
                this.cached++;
            }
            glyphData = glyph;
        }
        return glyphData;
    }

    private GlyphData getGlyphData(int gid) throws IOException {
        GlyphData glyph = new GlyphData();
        HorizontalMetricsTable hmt = this.font.getHorizontalMetrics();
        int leftSideBearing = hmt == null ? 0 : hmt.getLeftSideBearing(gid);
        glyph.initData(this, this.data, leftSideBearing);
        if (glyph.getDescription().isComposite()) {
            glyph.getDescription().resolve();
        }
        return glyph;
    }
}
