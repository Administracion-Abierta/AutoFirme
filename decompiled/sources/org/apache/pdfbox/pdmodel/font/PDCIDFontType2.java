package org.apache.pdfbox.pdmodel.font;

import com.aowagie.text.pdf.ColumnText;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.cff.Type2CharString;
import org.apache.fontbox.cmap.CMap;
import org.apache.fontbox.ttf.CmapLookup;
import org.apache.fontbox.ttf.GlyphData;
import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDCIDFontType2.class */
public class PDCIDFontType2 extends PDCIDFont {
    private static final Log LOG = LogFactory.getLog(PDCIDFontType2.class);
    private final TrueTypeFont ttf;
    private final int[] cid2gid;
    private final boolean isEmbedded;
    private final boolean isDamaged;
    private final CmapLookup cmap;
    private Matrix fontMatrix;
    private BoundingBox fontBBox;
    private final Set<Integer> noMapping;

    public PDCIDFontType2(COSDictionary fontDictionary, PDType0Font parent) throws IOException {
        this(fontDictionary, parent, null);
    }

    public PDCIDFontType2(COSDictionary fontDictionary, PDType0Font parent, TrueTypeFont trueTypeFont) throws IOException {
        super(fontDictionary, parent);
        this.noMapping = new HashSet();
        PDFontDescriptor fd = getFontDescriptor();
        if (trueTypeFont != null) {
            this.ttf = trueTypeFont;
            this.isEmbedded = true;
            this.isDamaged = false;
        } else {
            boolean fontIsDamaged = false;
            TrueTypeFont ttfFont = null;
            PDStream stream = null;
            if (fd != null) {
                stream = fd.getFontFile2();
                stream = stream == null ? fd.getFontFile3() : stream;
                if (stream == null) {
                    stream = fd.getFontFile();
                }
            }
            if (stream != null) {
                try {
                    OTFParser otfParser = new OTFParser(true);
                    OpenTypeFont otf = otfParser.parse((InputStream) stream.createInputStream());
                    ttfFont = otf;
                    if (otf.isPostScript()) {
                        fontIsDamaged = true;
                        LOG.warn("Found CFF/OTF but expected embedded TTF font " + fd.getFontName());
                    }
                } catch (IOException e) {
                    fontIsDamaged = true;
                    LOG.warn("Could not read embedded OTF for font " + getBaseFont(), e);
                }
            }
            this.isEmbedded = ttfFont != null;
            this.isDamaged = fontIsDamaged;
            this.ttf = ttfFont == null ? findFontOrSubstitute() : ttfFont;
        }
        this.cmap = this.ttf.getUnicodeCmapLookup(false);
        this.cid2gid = readCIDToGIDMap();
    }

    private TrueTypeFont findFontOrSubstitute() throws IOException {
        TrueTypeFont ttfFont;
        CIDFontMapping mapping = FontMappers.instance().getCIDFont(getBaseFont(), getFontDescriptor(), getCIDSystemInfo());
        if (mapping.isCIDFont()) {
            ttfFont = mapping.getFont();
        } else {
            ttfFont = (TrueTypeFont) mapping.getTrueTypeFont();
        }
        if (mapping.isFallback()) {
            LOG.warn("Using fallback font " + ttfFont.getName() + " for CID-keyed TrueType font " + getBaseFont());
        }
        return ttfFont;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public Matrix getFontMatrix() {
        if (this.fontMatrix == null) {
            this.fontMatrix = new Matrix(0.001f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, 0.001f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        }
        return this.fontMatrix;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public BoundingBox getBoundingBox() throws IOException {
        if (this.fontBBox == null) {
            this.fontBBox = generateBoundingBox();
        }
        return this.fontBBox;
    }

    private BoundingBox generateBoundingBox() throws IOException {
        PDRectangle bbox;
        if (getFontDescriptor() != null && (bbox = getFontDescriptor().getFontBoundingBox()) != null && (Float.compare(bbox.getLowerLeftX(), ColumnText.GLOBAL_SPACE_CHAR_RATIO) != 0 || Float.compare(bbox.getLowerLeftY(), ColumnText.GLOBAL_SPACE_CHAR_RATIO) != 0 || Float.compare(bbox.getUpperRightX(), ColumnText.GLOBAL_SPACE_CHAR_RATIO) != 0 || Float.compare(bbox.getUpperRightY(), ColumnText.GLOBAL_SPACE_CHAR_RATIO) != 0)) {
            return new BoundingBox(bbox.getLowerLeftX(), bbox.getLowerLeftY(), bbox.getUpperRightX(), bbox.getUpperRightY());
        }
        return this.ttf.getFontBBox();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDCIDFont
    public int codeToCID(int code) {
        String unicode;
        CMap cMap = this.parent.getCMap();
        if (!cMap.hasCIDMappings() && cMap.hasUnicodeMappings() && (unicode = cMap.toUnicode(code)) != null) {
            return unicode.codePointAt(0);
        }
        return cMap.toCID(code);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDCIDFont
    public int codeToGID(int code) throws IOException {
        if (!this.isEmbedded) {
            if (this.cid2gid != null && !this.isDamaged) {
                LOG.warn("Using non-embedded GIDs in font " + getName());
                int cid = codeToCID(code);
                if (cid < this.cid2gid.length) {
                    return this.cid2gid[cid];
                }
                return 0;
            }
            String unicode = this.parent.toUnicode(code);
            if (unicode == null) {
                if (!this.noMapping.contains(Integer.valueOf(code))) {
                    this.noMapping.add(Integer.valueOf(code));
                    LOG.warn("Failed to find a character mapping for " + code + " in " + getName());
                }
                return codeToCID(code);
            }
            if (unicode.length() > 1) {
                LOG.warn("Trying to map multi-byte character using 'cmap', result will be poor");
            }
            return this.cmap.getGlyphId(unicode.codePointAt(0));
        }
        int cid2 = codeToCID(code);
        if (this.cid2gid != null) {
            if (cid2 < this.cid2gid.length) {
                return this.cid2gid[cid2];
            }
            return 0;
        }
        if (cid2 < this.ttf.getNumberOfGlyphs()) {
            return cid2;
        }
        return 0;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getHeight(int code) throws IOException {
        return (this.ttf.getHorizontalHeader().getAscender() + (-this.ttf.getHorizontalHeader().getDescender())) / this.ttf.getUnitsPerEm();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getWidthFromFont(int code) throws IOException {
        int gid = codeToGID(code);
        int width = this.ttf.getAdvanceWidth(gid);
        int unitsPerEM = this.ttf.getUnitsPerEm();
        if (unitsPerEM != 1000) {
            width = (int) (width * (1000.0f / unitsPerEM));
        }
        return width;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDCIDFont
    public byte[] encode(int unicode) {
        int cid = -1;
        if (this.isEmbedded) {
            if (this.parent.getCMap().getName().startsWith("Identity-")) {
                if (this.cmap != null) {
                    cid = this.cmap.getGlyphId(unicode);
                }
            } else if (this.parent.getCMapUCS2() != null) {
                cid = this.parent.getCMapUCS2().toCID(unicode);
            }
            if (cid == -1) {
                CMap toUnicodeCMap = this.parent.getToUnicodeCMap();
                byte[] codes = toUnicodeCMap.getCodesFromUnicode(Character.toString((char) unicode));
                if (codes != null) {
                    return codes;
                }
                cid = 0;
            }
        } else {
            cid = this.cmap.getGlyphId(unicode);
        }
        if (cid == 0) {
            throw new IllegalArgumentException(String.format("No glyph for U+%04X (%c) in font %s", Integer.valueOf(unicode), Character.valueOf((char) unicode), getName()));
        }
        return new byte[]{(byte) ((cid >> 8) & 255), (byte) (cid & 255)};
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public boolean isEmbedded() {
        return this.isEmbedded;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public boolean isDamaged() {
        return this.isDamaged;
    }

    public TrueTypeFont getTrueTypeFont() {
        return this.ttf;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDVectorFont
    public GeneralPath getPath(int code) throws IOException {
        if ((this.ttf instanceof OpenTypeFont) && ((OpenTypeFont) this.ttf).isPostScript()) {
            int cid = codeToGID(code);
            Type2CharString charstring = ((OpenTypeFont) this.ttf).getCFF().getFont().getType2CharString(cid);
            return charstring.getPath();
        }
        int gid = codeToGID(code);
        GlyphData glyph = this.ttf.getGlyph().getGlyph(gid);
        if (glyph != null) {
            return glyph.getPath();
        }
        return new GeneralPath();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDVectorFont
    public boolean hasGlyph(int code) throws IOException {
        return codeToGID(code) != 0;
    }
}
