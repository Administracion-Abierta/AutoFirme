package org.apache.pdfbox.pdmodel.font;

import com.aowagie.text.pdf.ColumnText;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.ResourceCache;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.encoding.DictionaryEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.GlyphList;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDType3Font.class */
public class PDType3Font extends PDSimpleFont {
    private static final Log LOG = LogFactory.getLog(PDType3Font.class);
    private PDResources resources;
    private COSDictionary charProcs;
    private Matrix fontMatrix;
    private BoundingBox fontBBox;
    private final ResourceCache resourceCache;

    public PDType3Font(COSDictionary fontDictionary) throws IOException {
        this(fontDictionary, null);
    }

    public PDType3Font(COSDictionary fontDictionary, ResourceCache resourceCache) throws IOException {
        super(fontDictionary);
        this.resourceCache = resourceCache;
        readEncoding();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public String getName() {
        return this.dict.getNameAsString(COSName.NAME);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDSimpleFont
    protected final void readEncoding() throws IOException {
        COSBase encodingBase = this.dict.getDictionaryObject(COSName.ENCODING);
        if (encodingBase instanceof COSName) {
            COSName encodingName = (COSName) encodingBase;
            this.encoding = Encoding.getInstance(encodingName);
            if (this.encoding == null) {
                LOG.warn("Unknown encoding: " + encodingName.getName());
            }
        } else if (encodingBase instanceof COSDictionary) {
            this.encoding = new DictionaryEncoding((COSDictionary) encodingBase);
        }
        this.glyphList = GlyphList.getAdobeGlyphList();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDSimpleFont
    protected Encoding readEncodingFromFont() throws IOException {
        throw new UnsupportedOperationException("not supported for Type 3 fonts");
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDSimpleFont
    protected Boolean isFontSymbolic() {
        return false;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDSimpleFont
    public GeneralPath getPath(String name) throws IOException {
        throw new UnsupportedOperationException("not supported for Type 3 fonts");
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDSimpleFont
    public boolean hasGlyph(String name) throws IOException {
        COSBase base = getCharProcs().getDictionaryObject(COSName.getPDFName(name));
        return base instanceof COSStream;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDSimpleFont
    public FontBoxFont getFontBoxFont() {
        throw new UnsupportedOperationException("not supported for Type 3 fonts");
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public Vector getDisplacement(int code) throws IOException {
        return getFontMatrix().transform(new Vector(getWidth(code), ColumnText.GLOBAL_SPACE_CHAR_RATIO));
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont, org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getWidth(int code) throws IOException {
        Float w;
        int firstChar = this.dict.getInt(COSName.FIRST_CHAR, -1);
        int lastChar = this.dict.getInt(COSName.LAST_CHAR, -1);
        List<Float> widths = getWidths();
        if (!widths.isEmpty() && code >= firstChar && code <= lastChar) {
            return (code - firstChar < widths.size() && (w = widths.get(code - firstChar)) != null) ? w.floatValue() : ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        PDFontDescriptor fd = getFontDescriptor();
        if (fd != null) {
            return fd.getMissingWidth();
        }
        return getWidthFromFont(code);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getWidthFromFont(int code) throws IOException {
        PDType3CharProc charProc = getCharProc(code);
        if (charProc == null || charProc.getContentStream().getLength() == 0) {
            return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        return charProc.getWidth();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public boolean isEmbedded() {
        return true;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getHeight(int code) throws IOException {
        PDFontDescriptor desc = getFontDescriptor();
        if (desc != null) {
            PDRectangle bbox = desc.getFontBoundingBox();
            float retval = 0.0f;
            if (bbox != null) {
                retval = bbox.getHeight() / 2.0f;
            }
            if (retval == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                retval = desc.getCapHeight();
            }
            if (retval == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                retval = desc.getAscent();
            }
            if (retval == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                retval = desc.getXHeight();
                if (retval > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                    retval -= desc.getDescent();
                }
            }
            return retval;
        }
        return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    protected byte[] encode(int unicode) throws IOException {
        throw new UnsupportedOperationException("Not implemented: Type3");
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public int readCode(InputStream in) throws IOException {
        return in.read();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont, org.apache.pdfbox.pdmodel.font.PDFontLike
    public Matrix getFontMatrix() {
        if (this.fontMatrix == null) {
            COSBase base = this.dict.getDictionaryObject(COSName.FONT_MATRIX);
            if (base instanceof COSArray) {
                this.fontMatrix = new Matrix((COSArray) base);
            } else {
                return super.getFontMatrix();
            }
        }
        return this.fontMatrix;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public boolean isDamaged() {
        return false;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDSimpleFont, org.apache.pdfbox.pdmodel.font.PDFont
    public boolean isStandard14() {
        return false;
    }

    public PDResources getResources() {
        if (this.resources == null) {
            COSBase base = this.dict.getDictionaryObject(COSName.RESOURCES);
            if (base instanceof COSDictionary) {
                this.resources = new PDResources((COSDictionary) base, this.resourceCache);
            }
        }
        return this.resources;
    }

    public PDRectangle getFontBBox() {
        COSBase base = this.dict.getDictionaryObject(COSName.FONT_BBOX);
        PDRectangle retval = null;
        if (base instanceof COSArray) {
            retval = new PDRectangle((COSArray) base);
        }
        return retval;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public BoundingBox getBoundingBox() {
        if (this.fontBBox == null) {
            this.fontBBox = generateBoundingBox();
        }
        return this.fontBBox;
    }

    private BoundingBox generateBoundingBox() {
        PDRectangle rect = getFontBBox();
        if (rect.getLowerLeftX() == ColumnText.GLOBAL_SPACE_CHAR_RATIO && rect.getLowerLeftY() == ColumnText.GLOBAL_SPACE_CHAR_RATIO && rect.getUpperRightX() == ColumnText.GLOBAL_SPACE_CHAR_RATIO && rect.getUpperRightY() == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            COSDictionary cp = getCharProcs();
            for (COSName name : cp.keySet()) {
                COSBase base = cp.getDictionaryObject(name);
                if (base instanceof COSStream) {
                    PDType3CharProc charProc = new PDType3CharProc(this, (COSStream) base);
                    try {
                        PDRectangle glyphBBox = charProc.getGlyphBBox();
                        if (glyphBBox != null) {
                            rect.setLowerLeftX(Math.min(rect.getLowerLeftX(), glyphBBox.getLowerLeftX()));
                            rect.setLowerLeftY(Math.min(rect.getLowerLeftY(), glyphBBox.getLowerLeftY()));
                            rect.setUpperRightX(Math.max(rect.getUpperRightX(), glyphBBox.getUpperRightX()));
                            rect.setUpperRightY(Math.max(rect.getUpperRightY(), glyphBBox.getUpperRightY()));
                        }
                    } catch (IOException e) {
                    }
                }
            }
        }
        return new BoundingBox(rect.getLowerLeftX(), rect.getLowerLeftY(), rect.getUpperRightX(), rect.getUpperRightY());
    }

    public COSDictionary getCharProcs() {
        if (this.charProcs == null) {
            this.charProcs = this.dict.getCOSDictionary(COSName.CHAR_PROCS);
        }
        return this.charProcs;
    }

    public PDType3CharProc getCharProc(int code) {
        COSStream stream;
        String name = getEncoding().getName(code);
        if (getCharProcs() != null && (stream = getCharProcs().getCOSStream(COSName.getPDFName(name))) != null) {
            return new PDType3CharProc(this, stream);
        }
        return null;
    }
}
