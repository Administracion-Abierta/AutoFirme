package org.apache.pdfbox.pdmodel.font;

import com.aowagie.text.pdf.BaseFont;
import com.aowagie.text.pdf.ColumnText;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.cff.CFFCIDFont;
import org.apache.fontbox.cff.CFFFont;
import org.apache.fontbox.cff.CFFParser;
import org.apache.fontbox.cff.CFFType1Font;
import org.apache.fontbox.cff.Type2CharString;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDCIDFontType0.class */
public class PDCIDFontType0 extends PDCIDFont {
    private static final Log LOG = LogFactory.getLog(PDCIDFontType0.class);
    private final CFFCIDFont cidFont;
    private final FontBoxFont t1Font;
    private final Map<Integer, Float> glyphHeights;
    private final boolean isEmbedded;
    private final boolean isDamaged;
    private Float avgWidth;
    private Matrix fontMatrix;
    private final AffineTransform fontMatrixTransform;
    private BoundingBox fontBBox;
    private int[] cid2gid;

    public PDCIDFontType0(COSDictionary fontDictionary, PDType0Font parent) throws IOException {
        FontBoxFont font;
        PDStream ff3Stream;
        super(fontDictionary, parent);
        this.glyphHeights = new HashMap();
        this.avgWidth = null;
        this.cid2gid = null;
        PDFontDescriptor fd = getFontDescriptor();
        byte[] bytes = null;
        if (fd != null && (ff3Stream = fd.getFontFile3()) != null) {
            bytes = ff3Stream.toByteArray();
        }
        boolean fontIsDamaged = false;
        CFFFont cffFont = null;
        if (bytes != null && bytes.length > 0 && (bytes[0] & 255) == 37) {
            LOG.warn("Found PFB but expected embedded CFF font " + fd.getFontName());
            fontIsDamaged = true;
        } else if (bytes != null) {
            CFFParser cffParser = new CFFParser();
            try {
                cffFont = cffParser.parse(bytes, new FF3ByteSource()).get(0);
            } catch (IOException e) {
                LOG.error("Can't read the embedded CFF font " + fd.getFontName(), e);
                fontIsDamaged = true;
            }
        }
        if (cffFont != null) {
            if (cffFont instanceof CFFCIDFont) {
                this.cidFont = (CFFCIDFont) cffFont;
                this.t1Font = null;
            } else {
                this.cidFont = null;
                this.t1Font = cffFont;
            }
            this.cid2gid = readCIDToGIDMap();
            this.isEmbedded = true;
            this.isDamaged = false;
        } else {
            CIDFontMapping mapping = FontMappers.instance().getCIDFont(getBaseFont(), getFontDescriptor(), getCIDSystemInfo());
            if (mapping.isCIDFont()) {
                CFFFont cffFont2 = mapping.getFont().getCFF().getFont();
                if (cffFont2 instanceof CFFCIDFont) {
                    this.cidFont = (CFFCIDFont) cffFont2;
                    this.t1Font = null;
                    font = this.cidFont;
                } else {
                    CFFType1Font f = (CFFType1Font) cffFont2;
                    this.cidFont = null;
                    this.t1Font = f;
                    font = f;
                }
            } else {
                this.cidFont = null;
                this.t1Font = mapping.getTrueTypeFont();
                font = this.t1Font;
            }
            if (mapping.isFallback()) {
                LOG.warn("Using fallback " + font.getName() + " for CID-keyed font " + getBaseFont());
            }
            this.isEmbedded = false;
            this.isDamaged = fontIsDamaged;
        }
        this.fontMatrixTransform = getFontMatrix().createAffineTransform();
        this.fontMatrixTransform.scale(1000.0d, 1000.0d);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public final Matrix getFontMatrix() {
        List<Number> numbers;
        if (this.fontMatrix == null) {
            if (this.cidFont != null) {
                numbers = this.cidFont.getFontMatrix();
            } else {
                try {
                    numbers = this.t1Font.getFontMatrix();
                } catch (IOException e) {
                    return new Matrix(0.001f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, 0.001f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                }
            }
            if (numbers != null && numbers.size() == 6) {
                this.fontMatrix = new Matrix(numbers.get(0).floatValue(), numbers.get(1).floatValue(), numbers.get(2).floatValue(), numbers.get(3).floatValue(), numbers.get(4).floatValue(), numbers.get(5).floatValue());
            } else {
                this.fontMatrix = new Matrix(0.001f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, 0.001f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
            }
        }
        return this.fontMatrix;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDCIDFontType0$FF3ByteSource.class */
    private class FF3ByteSource implements CFFParser.ByteSource {
        private FF3ByteSource() {
        }

        @Override // org.apache.fontbox.cff.CFFParser.ByteSource
        public byte[] getBytes() throws IOException {
            return PDCIDFontType0.this.getFontDescriptor().getFontFile3().toByteArray();
        }
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public BoundingBox getBoundingBox() {
        if (this.fontBBox == null) {
            this.fontBBox = generateBoundingBox();
        }
        return this.fontBBox;
    }

    private BoundingBox generateBoundingBox() {
        if (getFontDescriptor() != null) {
            PDRectangle bbox = getFontDescriptor().getFontBoundingBox();
            if (bbox.getLowerLeftX() != ColumnText.GLOBAL_SPACE_CHAR_RATIO || bbox.getLowerLeftY() != ColumnText.GLOBAL_SPACE_CHAR_RATIO || bbox.getUpperRightX() != ColumnText.GLOBAL_SPACE_CHAR_RATIO || bbox.getUpperRightY() != ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                return new BoundingBox(bbox.getLowerLeftX(), bbox.getLowerLeftY(), bbox.getUpperRightX(), bbox.getUpperRightY());
            }
        }
        if (this.cidFont != null) {
            return this.cidFont.getFontBBox();
        }
        try {
            return this.t1Font.getFontBBox();
        } catch (IOException e) {
            return new BoundingBox();
        }
    }

    public CFFFont getCFFFont() {
        if (this.cidFont != null) {
            return this.cidFont;
        }
        if (this.t1Font instanceof CFFType1Font) {
            return (CFFType1Font) this.t1Font;
        }
        return null;
    }

    public FontBoxFont getFontBoxFont() {
        if (this.cidFont != null) {
            return this.cidFont;
        }
        return this.t1Font;
    }

    public Type2CharString getType2CharString(int cid) throws IOException {
        if (this.cidFont != null) {
            return this.cidFont.getType2CharString(cid);
        }
        if (this.t1Font instanceof CFFType1Font) {
            return ((CFFType1Font) this.t1Font).getType2CharString(cid);
        }
        return null;
    }

    private String getGlyphName(int code) throws IOException {
        String unicodes = this.parent.toUnicode(code);
        if (unicodes == null) {
            return BaseFont.notdef;
        }
        return UniUtil.getUniNameOfCodePoint(unicodes.codePointAt(0));
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDVectorFont
    public GeneralPath getPath(int code) throws IOException {
        int cid = codeToCID(code);
        if (this.cid2gid != null && this.isEmbedded) {
            cid = this.cid2gid[cid];
        }
        Type2CharString charstring = getType2CharString(cid);
        if (charstring != null) {
            return charstring.getPath();
        }
        if (this.isEmbedded && (this.t1Font instanceof CFFType1Font)) {
            return ((CFFType1Font) this.t1Font).getType2CharString(cid).getPath();
        }
        return this.t1Font.getPath(getGlyphName(code));
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDVectorFont
    public boolean hasGlyph(int code) throws IOException {
        int cid = codeToCID(code);
        Type2CharString charstring = getType2CharString(cid);
        if (charstring != null) {
            return charstring.getGID() != 0;
        }
        if (this.isEmbedded && (this.t1Font instanceof CFFType1Font)) {
            return ((CFFType1Font) this.t1Font).getType2CharString(cid).getGID() != 0;
        }
        return this.t1Font.hasGlyph(getGlyphName(code));
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDCIDFont
    public int codeToCID(int code) {
        return this.parent.getCMap().toCID(code);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDCIDFont
    public int codeToGID(int code) {
        int cid = codeToCID(code);
        if (this.cidFont != null) {
            return this.cidFont.getCharset().getGIDForCID(cid);
        }
        return cid;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDCIDFont
    public byte[] encode(int unicode) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getWidthFromFont(int code) throws IOException {
        float width;
        int cid = codeToCID(code);
        if (this.cidFont != null) {
            width = getType2CharString(cid).getWidth();
        } else if (this.isEmbedded && (this.t1Font instanceof CFFType1Font)) {
            width = ((CFFType1Font) this.t1Font).getType2CharString(cid).getWidth();
        } else {
            width = this.t1Font.getWidth(getGlyphName(code));
        }
        Point2D.Float r0 = new Point2D.Float(width, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        this.fontMatrixTransform.transform(r0, r0);
        return (float) r0.getX();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public boolean isEmbedded() {
        return this.isEmbedded;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public boolean isDamaged() {
        return this.isDamaged;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getHeight(int code) throws IOException {
        float height;
        int cid = codeToCID(code);
        if (!this.glyphHeights.containsKey(Integer.valueOf(cid))) {
            height = (float) getType2CharString(cid).getBounds().getHeight();
            this.glyphHeights.put(Integer.valueOf(cid), Float.valueOf(height));
        } else {
            height = this.glyphHeights.get(Integer.valueOf(cid)).floatValue();
        }
        return height;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDCIDFont, org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getAverageFontWidth() {
        if (this.avgWidth == null) {
            this.avgWidth = Float.valueOf(getAverageCharacterWidth());
        }
        return this.avgWidth.floatValue();
    }

    private float getAverageCharacterWidth() {
        return 500.0f;
    }
}
