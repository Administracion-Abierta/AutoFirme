package org.apache.pdfbox.pdmodel.font;

import com.aowagie.text.pdf.BaseFont;
import com.aowagie.text.pdf.ColumnText;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.EncodedFont;
import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.cff.CFFParser;
import org.apache.fontbox.cff.CFFType1Font;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.StandardEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.Type1Encoding;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDType1CFont.class */
public class PDType1CFont extends PDSimpleFont {
    private static final Log LOG = LogFactory.getLog(PDType1CFont.class);
    private final Map<String, Float> glyphHeights;
    private Float avgWidth;
    private Matrix fontMatrix;
    private final AffineTransform fontMatrixTransform;
    private final CFFType1Font cffFont;
    private final FontBoxFont genericFont;
    private final boolean isEmbedded;
    private final boolean isDamaged;
    private BoundingBox fontBBox;

    public PDType1CFont(COSDictionary fontDictionary) throws IOException {
        PDStream ff3Stream;
        super(fontDictionary);
        this.glyphHeights = new HashMap();
        this.avgWidth = null;
        PDFontDescriptor fd = getFontDescriptor();
        byte[] bytes = null;
        if (fd != null && (ff3Stream = fd.getFontFile3()) != null) {
            bytes = ff3Stream.toByteArray();
            if (bytes.length == 0) {
                LOG.error("Invalid data for embedded Type1C font " + getName());
                bytes = null;
            }
        }
        boolean fontIsDamaged = false;
        CFFType1Font cffEmbedded = null;
        if (bytes != null) {
            try {
                CFFParser cffParser = new CFFParser();
                cffEmbedded = (CFFType1Font) cffParser.parse(bytes, new FF3ByteSource()).get(0);
            } catch (IOException e) {
                LOG.error("Can't read the embedded Type1C font " + getName(), e);
                fontIsDamaged = true;
            }
        }
        this.isDamaged = fontIsDamaged;
        this.cffFont = cffEmbedded;
        if (this.cffFont != null) {
            this.genericFont = this.cffFont;
            this.isEmbedded = true;
        } else {
            FontMapping<FontBoxFont> mapping = FontMappers.instance().getFontBoxFont(getBaseFont(), fd);
            this.genericFont = mapping.getFont();
            if (mapping.isFallback()) {
                LOG.warn("Using fallback font " + this.genericFont.getName() + " for " + getBaseFont());
            }
            this.isEmbedded = false;
        }
        readEncoding();
        this.fontMatrixTransform = getFontMatrix().createAffineTransform();
        this.fontMatrixTransform.scale(1000.0d, 1000.0d);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDType1CFont$FF3ByteSource.class */
    private class FF3ByteSource implements CFFParser.ByteSource {
        private FF3ByteSource() {
        }

        @Override // org.apache.fontbox.cff.CFFParser.ByteSource
        public byte[] getBytes() throws IOException {
            return PDType1CFont.this.getFontDescriptor().getFontFile3().toByteArray();
        }
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDSimpleFont
    public FontBoxFont getFontBoxFont() {
        return this.genericFont;
    }

    public final String getBaseFont() {
        return this.dict.getNameAsString(COSName.BASE_FONT);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDSimpleFont
    public GeneralPath getPath(String name) throws IOException {
        if (name.equals(BaseFont.notdef) && !isEmbedded() && !isStandard14()) {
            return new GeneralPath();
        }
        if ("sfthyphen".equals(name)) {
            return this.genericFont.getPath("hyphen");
        }
        if ("nbspace".equals(name)) {
            if (!hasGlyph("space")) {
                return new GeneralPath();
            }
            return this.genericFont.getPath("space");
        }
        return this.genericFont.getPath(name);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDSimpleFont
    public boolean hasGlyph(String name) throws IOException {
        return this.genericFont.hasGlyph(name);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public final String getName() {
        return getBaseFont();
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
        if (getFontDescriptor() != null && (bbox = getFontDescriptor().getFontBoundingBox()) != null && (bbox.getLowerLeftX() != ColumnText.GLOBAL_SPACE_CHAR_RATIO || bbox.getLowerLeftY() != ColumnText.GLOBAL_SPACE_CHAR_RATIO || bbox.getUpperRightX() != ColumnText.GLOBAL_SPACE_CHAR_RATIO || bbox.getUpperRightY() != ColumnText.GLOBAL_SPACE_CHAR_RATIO)) {
            return new BoundingBox(bbox.getLowerLeftX(), bbox.getLowerLeftY(), bbox.getUpperRightX(), bbox.getUpperRightY());
        }
        return this.genericFont.getFontBBox();
    }

    public String codeToName(int code) {
        return getEncoding().getName(code);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDSimpleFont
    protected Encoding readEncodingFromFont() throws IOException {
        if (!isEmbedded() && getStandard14AFM() != null) {
            return new Type1Encoding(getStandard14AFM());
        }
        if (this.genericFont instanceof EncodedFont) {
            return Type1Encoding.fromFontBox(((EncodedFont) this.genericFont).getEncoding());
        }
        return StandardEncoding.INSTANCE;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public int readCode(InputStream in) throws IOException {
        return in.read();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont, org.apache.pdfbox.pdmodel.font.PDFontLike
    public final Matrix getFontMatrix() {
        if (this.fontMatrix == null) {
            List<Number> numbers = null;
            try {
                numbers = this.genericFont.getFontMatrix();
            } catch (IOException e) {
                this.fontMatrix = DEFAULT_FONT_MATRIX;
            }
            if (numbers != null && numbers.size() == 6) {
                this.fontMatrix = new Matrix(numbers.get(0).floatValue(), numbers.get(1).floatValue(), numbers.get(2).floatValue(), numbers.get(3).floatValue(), numbers.get(4).floatValue(), numbers.get(5).floatValue());
            } else {
                return super.getFontMatrix();
            }
        }
        return this.fontMatrix;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public boolean isDamaged() {
        return this.isDamaged;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getWidthFromFont(int code) throws IOException {
        String name = codeToName(code);
        float width = this.genericFont.getWidth(getNameInFont(name));
        Point2D.Float r0 = new Point2D.Float(width, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        this.fontMatrixTransform.transform(r0, r0);
        return (float) r0.getX();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public boolean isEmbedded() {
        return this.isEmbedded;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getHeight(int code) throws IOException {
        float height;
        String name = codeToName(code);
        if (!this.glyphHeights.containsKey(name)) {
            if (this.cffFont == null) {
                LOG.warn("No embedded CFF font, returning 0");
                return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            }
            height = (float) this.cffFont.getType1CharString(name).getBounds().getHeight();
            this.glyphHeights.put(name, Float.valueOf(height));
        } else {
            height = this.glyphHeights.get(name).floatValue();
        }
        return height;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    protected byte[] encode(int unicode) throws IOException {
        String name = getGlyphList().codePointToName(unicode);
        if (!this.encoding.contains(name)) {
            throw new IllegalArgumentException(String.format("U+%04X ('%s') is not available in this font's encoding: %s", Integer.valueOf(unicode), name, this.encoding.getEncodingName()));
        }
        String nameInFont = getNameInFont(name);
        Map<String, Integer> inverted = this.encoding.getNameToCodeMap();
        if (nameInFont.equals(BaseFont.notdef) || !this.genericFont.hasGlyph(nameInFont)) {
            throw new IllegalArgumentException(String.format("No glyph for U+%04X in font %s", Integer.valueOf(unicode), getName()));
        }
        int code = inverted.get(name).intValue();
        return new byte[]{(byte) code};
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public float getStringWidth(String string) throws IOException {
        if (this.cffFont == null) {
            LOG.warn("No embedded CFF font, returning 0");
            return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        float width = 0.0f;
        for (int i = 0; i < string.length(); i++) {
            int codePoint = string.codePointAt(i);
            String name = getGlyphList().codePointToName(codePoint);
            width += this.cffFont.getType1CharString(name).getWidth();
        }
        return width;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont, org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getAverageFontWidth() {
        if (this.avgWidth == null) {
            this.avgWidth = Float.valueOf(getAverageCharacterWidth());
        }
        return this.avgWidth.floatValue();
    }

    public CFFType1Font getCFFType1Font() {
        return this.cffFont;
    }

    private float getAverageCharacterWidth() {
        return 500.0f;
    }

    private String getNameInFont(String name) throws IOException {
        if (isEmbedded() || this.genericFont.hasGlyph(name)) {
            return name;
        }
        String unicodes = getGlyphList().toUnicode(name);
        if (unicodes != null && unicodes.length() == 1) {
            String uniName = UniUtil.getUniNameOfCodePoint(unicodes.codePointAt(0));
            if (this.genericFont.hasGlyph(uniName)) {
                return uniName;
            }
            return BaseFont.notdef;
        }
        return BaseFont.notdef;
    }
}
