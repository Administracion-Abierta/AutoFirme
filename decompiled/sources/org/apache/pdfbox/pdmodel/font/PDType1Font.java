package org.apache.pdfbox.pdmodel.font;

import com.aowagie.text.pdf.BaseFont;
import com.aowagie.text.pdf.ColumnText;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.EncodedFont;
import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.type1.DamagedFontException;
import org.apache.fontbox.type1.Type1Font;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.StandardEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.SymbolEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.Type1Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.ZapfDingbatsEncoding;
import org.apache.pdfbox.util.Matrix;
import org.ini4j.Config;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDType1Font.class */
public class PDType1Font extends PDSimpleFont {
    private static final Log LOG = LogFactory.getLog(PDType1Font.class);
    private static final Map<String, String> ALT_NAMES = new HashMap();
    private static final int PFB_START_MARKER = 128;
    public static final PDType1Font TIMES_ROMAN;
    public static final PDType1Font TIMES_BOLD;
    public static final PDType1Font TIMES_ITALIC;
    public static final PDType1Font TIMES_BOLD_ITALIC;
    public static final PDType1Font HELVETICA;
    public static final PDType1Font HELVETICA_BOLD;
    public static final PDType1Font HELVETICA_OBLIQUE;
    public static final PDType1Font HELVETICA_BOLD_OBLIQUE;
    public static final PDType1Font COURIER;
    public static final PDType1Font COURIER_BOLD;
    public static final PDType1Font COURIER_OBLIQUE;
    public static final PDType1Font COURIER_BOLD_OBLIQUE;
    public static final PDType1Font SYMBOL;
    public static final PDType1Font ZAPF_DINGBATS;
    private final Type1Font type1font;
    private final FontBoxFont genericFont;
    private final boolean isEmbedded;
    private final boolean isDamaged;
    private Matrix fontMatrix;
    private final AffineTransform fontMatrixTransform;
    private BoundingBox fontBBox;
    private final Map<Integer, byte[]> codeToBytesMap;

    static {
        ALT_NAMES.put("ff", "f_f");
        ALT_NAMES.put("ffi", "f_f_i");
        ALT_NAMES.put("ffl", "f_f_l");
        ALT_NAMES.put("fi", "f_i");
        ALT_NAMES.put("fl", "f_l");
        ALT_NAMES.put("st", "s_t");
        ALT_NAMES.put("IJ", "I_J");
        ALT_NAMES.put("ij", "i_j");
        ALT_NAMES.put("ellipsis", "elipsis");
        TIMES_ROMAN = new PDType1Font("Times-Roman");
        TIMES_BOLD = new PDType1Font("Times-Bold");
        TIMES_ITALIC = new PDType1Font("Times-Italic");
        TIMES_BOLD_ITALIC = new PDType1Font("Times-BoldItalic");
        HELVETICA = new PDType1Font("Helvetica");
        HELVETICA_BOLD = new PDType1Font("Helvetica-Bold");
        HELVETICA_OBLIQUE = new PDType1Font("Helvetica-Oblique");
        HELVETICA_BOLD_OBLIQUE = new PDType1Font("Helvetica-BoldOblique");
        COURIER = new PDType1Font("Courier");
        COURIER_BOLD = new PDType1Font("Courier-Bold");
        COURIER_OBLIQUE = new PDType1Font("Courier-Oblique");
        COURIER_BOLD_OBLIQUE = new PDType1Font("Courier-BoldOblique");
        SYMBOL = new PDType1Font("Symbol");
        ZAPF_DINGBATS = new PDType1Font("ZapfDingbats");
    }

    private PDType1Font(String baseFont) {
        String fontName;
        super(baseFont);
        this.dict.setItem(COSName.SUBTYPE, (COSBase) COSName.TYPE1);
        this.dict.setName(COSName.BASE_FONT, baseFont);
        if ("ZapfDingbats".equals(baseFont)) {
            this.encoding = ZapfDingbatsEncoding.INSTANCE;
        } else if ("Symbol".equals(baseFont)) {
            this.encoding = SymbolEncoding.INSTANCE;
        } else {
            this.encoding = WinAnsiEncoding.INSTANCE;
            this.dict.setItem(COSName.ENCODING, (COSBase) COSName.WIN_ANSI_ENCODING);
        }
        this.codeToBytesMap = new ConcurrentHashMap();
        this.type1font = null;
        FontMapping<FontBoxFont> mapping = FontMappers.instance().getFontBoxFont(getBaseFont(), getFontDescriptor());
        this.genericFont = mapping.getFont();
        if (mapping.isFallback()) {
            try {
                fontName = this.genericFont.getName();
            } catch (IOException e) {
                fontName = Config.DEFAULT_GLOBAL_SECTION_NAME;
            }
            LOG.warn("Using fallback font " + fontName + " for base font " + getBaseFont());
        }
        this.isEmbedded = false;
        this.isDamaged = false;
        this.fontMatrixTransform = new AffineTransform();
    }

    public PDType1Font(PDDocument doc, InputStream pfbIn) throws IOException {
        this(doc, pfbIn, null);
    }

    public PDType1Font(PDDocument doc, InputStream pfbIn, Encoding encoding) throws IOException {
        PDType1FontEmbedder embedder = new PDType1FontEmbedder(doc, this.dict, pfbIn, encoding);
        this.encoding = encoding == null ? embedder.getFontEncoding() : encoding;
        this.glyphList = embedder.getGlyphList();
        this.type1font = embedder.getType1Font();
        this.genericFont = embedder.getType1Font();
        this.isEmbedded = true;
        this.isDamaged = false;
        this.fontMatrixTransform = new AffineTransform();
        this.codeToBytesMap = new HashMap();
    }

    public PDType1Font(COSDictionary fontDictionary) throws IOException {
        super(fontDictionary);
        this.codeToBytesMap = new HashMap();
        PDFontDescriptor fd = getFontDescriptor();
        Type1Font t1 = null;
        boolean fontIsDamaged = false;
        if (fd != null) {
            PDStream fontFile3 = fd.getFontFile3();
            if (fontFile3 != null) {
                throw new IllegalArgumentException("Use PDType1CFont for FontFile3");
            }
            PDStream fontFile = fd.getFontFile();
            if (fontFile != null) {
                try {
                    COSStream stream = fontFile.getCOSObject();
                    int length1 = stream.getInt(COSName.LENGTH1);
                    int length2 = stream.getInt(COSName.LENGTH2);
                    byte[] bytes = fontFile.toByteArray();
                    if (bytes.length == 0) {
                        throw new IOException("Font data unavailable");
                    }
                    int length3 = repairLength1(bytes, length1);
                    int length4 = repairLength2(bytes, length3, length2);
                    if ((bytes[0] & 255) == 128) {
                        t1 = Type1Font.createWithPFB(bytes);
                    } else {
                        if (length3 < 0 || length3 > length3 + length4) {
                            throw new IOException("Invalid length data, actual length: " + bytes.length + ", /Length1: " + length3 + ", /Length2: " + length4);
                        }
                        byte[] segment1 = Arrays.copyOfRange(bytes, 0, length3);
                        byte[] segment2 = Arrays.copyOfRange(bytes, length3, length3 + length4);
                        if (length3 > 0 && length4 > 0) {
                            t1 = Type1Font.createWithSegments(segment1, segment2);
                        }
                    }
                } catch (DamagedFontException e) {
                    LOG.warn("Can't read damaged embedded Type1 font " + fd.getFontName());
                    fontIsDamaged = true;
                } catch (IOException e2) {
                    LOG.error("Can't read the embedded Type1 font " + fd.getFontName(), e2);
                    fontIsDamaged = true;
                }
            }
        }
        this.isEmbedded = t1 != null;
        this.isDamaged = fontIsDamaged;
        this.type1font = t1;
        if (this.type1font != null) {
            this.genericFont = this.type1font;
        } else {
            FontMapping<FontBoxFont> mapping = FontMappers.instance().getFontBoxFont(getBaseFont(), fd);
            this.genericFont = mapping.getFont();
            if (mapping.isFallback()) {
                LOG.warn("Using fallback font " + this.genericFont.getName() + " for " + getBaseFont());
            }
        }
        readEncoding();
        this.fontMatrixTransform = getFontMatrix().createAffineTransform();
        this.fontMatrixTransform.scale(1000.0d, 1000.0d);
    }

    private int repairLength1(byte[] bytes, int length1) {
        int offset = Math.max(0, length1 - 4);
        if (offset <= 0 || offset > bytes.length - 4) {
            offset = bytes.length - 4;
        }
        int offset2 = findBinaryOffsetAfterExec(bytes, offset);
        if (offset2 == 0 && length1 > 0) {
            offset2 = findBinaryOffsetAfterExec(bytes, bytes.length - 4);
        }
        if (length1 - offset2 != 0 && offset2 > 0) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Ignored invalid Length1 " + length1 + " for Type 1 font " + getName());
            }
            return offset2;
        }
        return length1;
    }

    private static int findBinaryOffsetAfterExec(byte[] bytes, int startOffset) {
        int offset = startOffset;
        while (offset > 0) {
            if (bytes[offset + 0] == 101 && bytes[offset + 1] == 120 && bytes[offset + 2] == 101 && bytes[offset + 3] == 99) {
                offset += 4;
                while (offset < bytes.length && (bytes[offset] == 13 || bytes[offset] == 10 || bytes[offset] == 32 || bytes[offset] == 9)) {
                    offset++;
                }
                break;
            }
            offset--;
        }
        return offset;
    }

    private int repairLength2(byte[] bytes, int length1, int length2) {
        if (length2 < 0 || length2 > bytes.length - length1) {
            LOG.warn("Ignored invalid Length2 " + length2 + " for Type 1 font " + getName());
            return bytes.length - length1;
        }
        return length2;
    }

    public final String getBaseFont() {
        return this.dict.getNameAsString(COSName.BASE_FONT);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getHeight(int code) throws IOException {
        if (getStandard14AFM() != null) {
            String afmName = getEncoding().getName(code);
            return getStandard14AFM().getCharacterHeight(afmName);
        }
        String name = codeToName(code);
        return (float) this.genericFont.getPath(name).getBounds().getHeight();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    protected byte[] encode(int unicode) throws IOException {
        byte[] bytes = this.codeToBytesMap.get(Integer.valueOf(unicode));
        if (bytes != null) {
            return bytes;
        }
        String name = getGlyphList().codePointToName(unicode);
        if (isStandard14()) {
            if (!this.encoding.contains(name)) {
                throw new IllegalArgumentException(String.format("U+%04X ('%s') is not available in the font %s, encoding: %s", Integer.valueOf(unicode), name, getName(), this.encoding.getEncodingName()));
            }
            if (BaseFont.notdef.equals(name)) {
                throw new IllegalArgumentException(String.format("No glyph for U+%04X in the font %s", Integer.valueOf(unicode), getName()));
            }
        } else {
            if (!this.encoding.contains(name)) {
                throw new IllegalArgumentException(String.format("U+%04X ('%s') is not available in the font %s (generic: %s), encoding: %s", Integer.valueOf(unicode), name, getName(), this.genericFont.getName(), this.encoding.getEncodingName()));
            }
            String nameInFont = getNameInFont(name);
            if (nameInFont.equals(BaseFont.notdef) || !this.genericFont.hasGlyph(nameInFont)) {
                throw new IllegalArgumentException(String.format("No glyph for U+%04X in the font %s (generic: %s)", Integer.valueOf(unicode), getName(), this.genericFont.getName()));
            }
        }
        Map<String, Integer> inverted = this.encoding.getNameToCodeMap();
        int code = inverted.get(name).intValue();
        if (code < 0) {
            throw new IllegalArgumentException(String.format("U+%04X ('%s') is not available in the font %s (generic: %s), encoding: %s", Integer.valueOf(unicode), name, getName(), this.genericFont.getName(), this.encoding.getEncodingName()));
        }
        byte[] bytes2 = {(byte) code};
        this.codeToBytesMap.put(Integer.valueOf(unicode), bytes2);
        return bytes2;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getWidthFromFont(int code) throws IOException {
        String name = codeToName(code);
        if (!this.isEmbedded && BaseFont.notdef.equals(name)) {
            return 250.0f;
        }
        float width = this.genericFont.getWidth(name);
        Point2D.Float r0 = new Point2D.Float(width, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        this.fontMatrixTransform.transform(r0, r0);
        return (float) r0.getX();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public boolean isEmbedded() {
        return this.isEmbedded;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont, org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getAverageFontWidth() {
        if (getStandard14AFM() != null) {
            return getStandard14AFM().getAverageCharacterWidth();
        }
        return super.getAverageFontWidth();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public int readCode(InputStream in) throws IOException {
        return in.read();
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

    public Type1Font getType1Font() {
        return this.type1font;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDSimpleFont
    public FontBoxFont getFontBoxFont() {
        return this.genericFont;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public String getName() {
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

    public String codeToName(int code) throws IOException {
        String name = getEncoding().getName(code);
        return getNameInFont(name);
    }

    private String getNameInFont(String name) throws IOException {
        Integer code;
        if (isEmbedded() || this.genericFont.hasGlyph(name)) {
            return name;
        }
        String altName = ALT_NAMES.get(name);
        if (altName != null && !name.equals(BaseFont.notdef) && this.genericFont.hasGlyph(altName)) {
            return altName;
        }
        String unicodes = getGlyphList().toUnicode(name);
        if (unicodes != null && unicodes.length() == 1) {
            String uniName = UniUtil.getUniNameOfCodePoint(unicodes.codePointAt(0));
            if (this.genericFont.hasGlyph(uniName)) {
                return uniName;
            }
            if ("SymbolMT".equals(this.genericFont.getName()) && (code = SymbolEncoding.INSTANCE.getNameToCodeMap().get(name)) != null) {
                String uniName2 = UniUtil.getUniNameOfCodePoint(code.intValue() + 61440);
                if (this.genericFont.hasGlyph(uniName2)) {
                    return uniName2;
                }
                return BaseFont.notdef;
            }
            return BaseFont.notdef;
        }
        return BaseFont.notdef;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDSimpleFont
    public GeneralPath getPath(String name) throws IOException {
        if (name.equals(BaseFont.notdef) && !this.isEmbedded) {
            return new GeneralPath();
        }
        return this.genericFont.getPath(getNameInFont(name));
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDSimpleFont
    public boolean hasGlyph(String name) throws IOException {
        return this.genericFont.hasGlyph(getNameInFont(name));
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
}
