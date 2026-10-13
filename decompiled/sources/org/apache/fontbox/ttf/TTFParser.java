package org.apache.fontbox.ttf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/TTFParser.class */
public class TTFParser {
    private static final Log LOG = LogFactory.getLog(TTFParser.class);
    private boolean isEmbedded;
    private boolean parseOnDemandOnly;

    public TTFParser() {
        this(false);
    }

    public TTFParser(boolean isEmbedded) {
        this(isEmbedded, false);
    }

    public TTFParser(boolean isEmbedded, boolean parseOnDemand) {
        this.isEmbedded = false;
        this.parseOnDemandOnly = false;
        this.isEmbedded = isEmbedded;
        this.parseOnDemandOnly = parseOnDemand;
    }

    public TrueTypeFont parse(String ttfFile) throws IOException {
        return parse(new File(ttfFile));
    }

    public TrueTypeFont parse(File ttfFile) throws IOException {
        RAFDataStream raf = new RAFDataStream(ttfFile, PDPageLabelRange.STYLE_ROMAN_LOWER);
        try {
            return parse(raf);
        } catch (IOException ex) {
            raf.close();
            throw ex;
        }
    }

    public TrueTypeFont parse(InputStream inputStream) throws IOException {
        return parse(new MemoryTTFDataStream(inputStream));
    }

    public TrueTypeFont parseEmbedded(InputStream inputStream) throws IOException {
        this.isEmbedded = true;
        return parse(new MemoryTTFDataStream(inputStream));
    }

    TrueTypeFont parse(TTFDataStream raf) throws IOException {
        TrueTypeFont font = newFont(raf);
        font.setVersion(raf.read32Fixed());
        int numberOfTables = raf.readUnsignedShort();
        raf.readUnsignedShort();
        raf.readUnsignedShort();
        raf.readUnsignedShort();
        for (int i = 0; i < numberOfTables; i++) {
            TTFTable table = readTableDirectory(font, raf);
            if (table != null) {
                if (table.getOffset() + table.getLength() > font.getOriginalDataSize()) {
                    LOG.warn("Skip table '" + table.getTag() + "' which goes past the file size; offset: " + table.getOffset() + ", size: " + table.getLength() + ", font size: " + font.getOriginalDataSize());
                } else {
                    font.addTable(table);
                }
            }
        }
        if (!this.parseOnDemandOnly) {
            parseTables(font);
        }
        return font;
    }

    TrueTypeFont newFont(TTFDataStream raf) {
        return new TrueTypeFont(raf);
    }

    private void parseTables(TrueTypeFont font) throws IOException {
        for (TTFTable table : font.getTables()) {
            if (!table.getInitialized()) {
                font.readTable(table);
            }
        }
        boolean isPostScript = allowCFF() && font.tables.containsKey(CFFTable.TAG);
        HeaderTable head = font.getHeader();
        if (head == null) {
            throw new IOException("head is mandatory");
        }
        HorizontalHeaderTable hh = font.getHorizontalHeader();
        if (hh == null) {
            throw new IOException("hhead is mandatory");
        }
        MaximumProfileTable maxp = font.getMaximumProfile();
        if (maxp == null) {
            throw new IOException("maxp is mandatory");
        }
        PostScriptTable post = font.getPostScript();
        if (post == null && !this.isEmbedded) {
            throw new IOException("post is mandatory");
        }
        if (!isPostScript) {
            IndexToLocationTable loc = font.getIndexToLocation();
            if (loc == null) {
                throw new IOException("loca is mandatory");
            }
            if (font.getGlyph() == null) {
                throw new IOException("glyf is mandatory");
            }
        }
        if (font.getNaming() == null && !this.isEmbedded) {
            throw new IOException("name is mandatory");
        }
        if (font.getHorizontalMetrics() == null) {
            throw new IOException("hmtx is mandatory");
        }
        if (!this.isEmbedded && font.getCmap() == null) {
            throw new IOException("cmap is mandatory");
        }
    }

    protected boolean allowCFF() {
        return false;
    }

    private TTFTable readTableDirectory(TrueTypeFont font, TTFDataStream raf) throws IOException {
        TTFTable table;
        String tag = raf.readString(4);
        if (tag.equals(CmapTable.TAG)) {
            table = new CmapTable(font);
        } else if (tag.equals(GlyphTable.TAG)) {
            table = new GlyphTable(font);
        } else if (tag.equals(HeaderTable.TAG)) {
            table = new HeaderTable(font);
        } else if (tag.equals(HorizontalHeaderTable.TAG)) {
            table = new HorizontalHeaderTable(font);
        } else if (tag.equals(HorizontalMetricsTable.TAG)) {
            table = new HorizontalMetricsTable(font);
        } else if (tag.equals(IndexToLocationTable.TAG)) {
            table = new IndexToLocationTable(font);
        } else if (tag.equals(MaximumProfileTable.TAG)) {
            table = new MaximumProfileTable(font);
        } else if (tag.equals("name")) {
            table = new NamingTable(font);
        } else if (tag.equals(OS2WindowsMetricsTable.TAG)) {
            table = new OS2WindowsMetricsTable(font);
        } else if (tag.equals(PostScriptTable.TAG)) {
            table = new PostScriptTable(font);
        } else if (tag.equals(DigitalSignatureTable.TAG)) {
            table = new DigitalSignatureTable(font);
        } else if (tag.equals(KerningTable.TAG)) {
            table = new KerningTable(font);
        } else if (tag.equals(VerticalHeaderTable.TAG)) {
            table = new VerticalHeaderTable(font);
        } else if (tag.equals(VerticalMetricsTable.TAG)) {
            table = new VerticalMetricsTable(font);
        } else if (tag.equals(VerticalOriginTable.TAG)) {
            table = new VerticalOriginTable(font);
        } else if (tag.equals(GlyphSubstitutionTable.TAG)) {
            table = new GlyphSubstitutionTable(font);
        } else {
            table = readTable(font, tag);
        }
        table.setTag(tag);
        table.setCheckSum(raf.readUnsignedInt());
        table.setOffset(raf.readUnsignedInt());
        table.setLength(raf.readUnsignedInt());
        if (table.getLength() == 0 && !tag.equals(GlyphTable.TAG)) {
            return null;
        }
        return table;
    }

    protected TTFTable readTable(TrueTypeFont font, String tag) {
        return new TTFTable(font);
    }
}
