package org.apache.pdfbox.pdmodel.font;

import com.aowagie.text.pdf.ColumnText;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.cmap.CMap;
import org.apache.fontbox.ttf.CmapLookup;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDType0Font.class */
public class PDType0Font extends PDFont implements PDVectorFont {
    private static final Log LOG = LogFactory.getLog(PDType0Font.class);
    private final PDCIDFont descendantFont;
    private CMap cMap;
    private CMap cMapUCS2;
    private boolean isCMapPredefined;
    private boolean isDescendantCJK;
    private PDCIDFontType2Embedder embedder;
    private final Set<Integer> noUnicode;
    private TrueTypeFont ttf;

    public static PDType0Font load(PDDocument doc, File file) throws IOException {
        return new PDType0Font(doc, new TTFParser().parse(file), true, true, false);
    }

    public static PDType0Font load(PDDocument doc, InputStream input) throws IOException {
        return load(doc, input, true);
    }

    public static PDType0Font load(PDDocument doc, InputStream input, boolean embedSubset) throws IOException {
        return new PDType0Font(doc, new TTFParser().parse(input), embedSubset, true, false);
    }

    public static PDType0Font load(PDDocument doc, TrueTypeFont ttf, boolean embedSubset) throws IOException {
        return new PDType0Font(doc, ttf, embedSubset, false, false);
    }

    public static PDType0Font loadVertical(PDDocument doc, File file) throws IOException {
        return new PDType0Font(doc, new TTFParser().parse(file), true, true, true);
    }

    public static PDType0Font loadVertical(PDDocument doc, InputStream input) throws IOException {
        return new PDType0Font(doc, new TTFParser().parse(input), true, true, true);
    }

    public static PDType0Font loadVertical(PDDocument doc, InputStream input, boolean embedSubset) throws IOException {
        return new PDType0Font(doc, new TTFParser().parse(input), embedSubset, true, true);
    }

    public static PDType0Font loadVertical(PDDocument doc, TrueTypeFont ttf, boolean embedSubset) throws IOException {
        return new PDType0Font(doc, ttf, embedSubset, false, true);
    }

    public PDType0Font(COSDictionary fontDictionary) throws IOException {
        super(fontDictionary);
        this.noUnicode = new HashSet();
        COSBase base = this.dict.getDictionaryObject(COSName.DESCENDANT_FONTS);
        if (!(base instanceof COSArray)) {
            throw new IOException("Missing descendant font array");
        }
        COSArray descendantFonts = (COSArray) base;
        if (descendantFonts.size() == 0) {
            throw new IOException("Descendant font array is empty");
        }
        COSBase descendantFontDictBase = descendantFonts.getObject(0);
        if (!(descendantFontDictBase instanceof COSDictionary)) {
            throw new IOException("Missing descendant font dictionary");
        }
        this.descendantFont = PDFontFactory.createDescendantFont((COSDictionary) descendantFontDictBase, this);
        readEncoding();
        fetchCMapUCS2();
    }

    private PDType0Font(PDDocument document, TrueTypeFont ttf, boolean embedSubset, boolean closeTTF, boolean vertical) throws IOException {
        this.noUnicode = new HashSet();
        if (vertical) {
            ttf.enableVerticalSubstitutions();
        }
        this.embedder = new PDCIDFontType2Embedder(document, this.dict, ttf, embedSubset, this, vertical);
        this.descendantFont = this.embedder.getCIDFont();
        readEncoding();
        fetchCMapUCS2();
        if (closeTTF) {
            if (embedSubset) {
                this.ttf = ttf;
                document.registerTrueTypeFontForClosing(ttf);
            } else {
                ttf.close();
            }
        }
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public void addToSubset(int codePoint) {
        if (!willBeSubset()) {
            throw new IllegalStateException("This font was created with subsetting disabled");
        }
        this.embedder.addToSubset(codePoint);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public void subset() throws IOException {
        if (!willBeSubset()) {
            throw new IllegalStateException("This font was created with subsetting disabled");
        }
        this.embedder.subset();
        if (this.ttf != null) {
            this.ttf.close();
            this.ttf = null;
        }
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public boolean willBeSubset() {
        return this.embedder != null && this.embedder.needsSubset();
    }

    private void readEncoding() throws IOException {
        COSBase encoding = this.dict.getDictionaryObject(COSName.ENCODING);
        if (encoding instanceof COSName) {
            COSName encodingName = (COSName) encoding;
            this.cMap = CMapManager.getPredefinedCMap(encodingName.getName());
            if (this.cMap != null) {
                this.isCMapPredefined = true;
            } else {
                throw new IOException("Missing required CMap");
            }
        } else if (encoding != null) {
            this.cMap = readCMap(encoding);
            if (this.cMap == null) {
                throw new IOException("Missing required CMap");
            }
            if (!this.cMap.hasCIDMappings()) {
                LOG.warn("Invalid Encoding CMap in font " + getName());
            }
        }
        PDCIDSystemInfo ros = this.descendantFont.getCIDSystemInfo();
        if (ros != null) {
            this.isDescendantCJK = "Adobe".equals(ros.getRegistry()) && ("GB1".equals(ros.getOrdering()) || "CNS1".equals(ros.getOrdering()) || "Japan1".equals(ros.getOrdering()) || "Korea1".equals(ros.getOrdering()));
        }
    }

    private void fetchCMapUCS2() throws IOException {
        COSName name = this.dict.getCOSName(COSName.ENCODING);
        if ((this.isCMapPredefined && name != COSName.IDENTITY_H && name != COSName.IDENTITY_V) || this.isDescendantCJK) {
            String strName = null;
            if (this.isDescendantCJK) {
                strName = this.descendantFont.getCIDSystemInfo().getRegistry() + "-" + this.descendantFont.getCIDSystemInfo().getOrdering() + "-" + this.descendantFont.getCIDSystemInfo().getSupplement();
            } else if (name != null) {
                strName = name.getName();
            }
            if (strName != null) {
                try {
                    CMap prdCMap = CMapManager.getPredefinedCMap(strName);
                    String ucs2Name = prdCMap.getRegistry() + "-" + prdCMap.getOrdering() + "-UCS2";
                    this.cMapUCS2 = CMapManager.getPredefinedCMap(ucs2Name);
                } catch (IOException ex) {
                    LOG.warn("Could not get " + strName + " UC2 map for font " + getName(), ex);
                }
            }
        }
    }

    public String getBaseFont() {
        return this.dict.getNameAsString(COSName.BASE_FONT);
    }

    public PDCIDFont getDescendantFont() {
        return this.descendantFont;
    }

    public CMap getCMap() {
        return this.cMap;
    }

    public CMap getCMapUCS2() {
        return this.cMapUCS2;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont, org.apache.pdfbox.pdmodel.font.PDFontLike
    public PDFontDescriptor getFontDescriptor() {
        return this.descendantFont.getFontDescriptor();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont, org.apache.pdfbox.pdmodel.font.PDFontLike
    public Matrix getFontMatrix() {
        return this.descendantFont.getFontMatrix();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public boolean isVertical() {
        return this.cMap.getWMode() == 1;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getHeight(int code) throws IOException {
        return this.descendantFont.getHeight(code);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    protected byte[] encode(int unicode) throws IOException {
        return this.descendantFont.encode(unicode);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public boolean hasExplicitWidth(int code) throws IOException {
        return this.descendantFont.hasExplicitWidth(code);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont, org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getAverageFontWidth() {
        return this.descendantFont.getAverageFontWidth();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont, org.apache.pdfbox.pdmodel.font.PDFontLike
    public Vector getPositionVector(int code) {
        return this.descendantFont.getPositionVector(code).scale(-0.001f);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public Vector getDisplacement(int code) throws IOException {
        if (isVertical()) {
            return new Vector(ColumnText.GLOBAL_SPACE_CHAR_RATIO, this.descendantFont.getVerticalDisplacementVectorY(code) / 1000.0f);
        }
        return super.getDisplacement(code);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont, org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getWidth(int code) throws IOException {
        return this.descendantFont.getWidth(code);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    protected float getStandard14Width(int code) {
        throw new UnsupportedOperationException("not supported");
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getWidthFromFont(int code) throws IOException {
        return this.descendantFont.getWidthFromFont(code);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public boolean isEmbedded() {
        return this.descendantFont.isEmbedded();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public String toUnicode(int code) throws IOException {
        TrueTypeFont font;
        int gid;
        String unicode = super.toUnicode(code);
        if (unicode != null) {
            return unicode;
        }
        if ((this.isCMapPredefined || this.isDescendantCJK) && this.cMapUCS2 != null) {
            int cid = codeToCID(code);
            return this.cMapUCS2.toUnicode(cid);
        }
        if ((this.descendantFont instanceof PDCIDFontType2) && (font = ((PDCIDFontType2) this.descendantFont).getTrueTypeFont()) != null) {
            try {
                CmapLookup cmap = font.getUnicodeCmapLookup(false);
                if (cmap != null) {
                    if (this.descendantFont.isEmbedded()) {
                        gid = this.descendantFont.codeToGID(code);
                    } else {
                        gid = this.descendantFont.codeToCID(code);
                    }
                    List<Integer> codes = cmap.getCharCodes(gid);
                    if (codes != null && !codes.isEmpty()) {
                        return Character.toString((char) codes.get(0).intValue());
                    }
                }
            } catch (IOException e) {
                LOG.warn("get unicode from font cmap fail", e);
            }
        }
        if (LOG.isWarnEnabled() && !this.noUnicode.contains(Integer.valueOf(code))) {
            String cid2 = "CID+" + codeToCID(code);
            LOG.warn("No Unicode mapping for " + cid2 + " (" + code + ") in font " + getName());
            this.noUnicode.add(Integer.valueOf(code));
            return null;
        }
        return null;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public String getName() {
        return getBaseFont();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public BoundingBox getBoundingBox() throws IOException {
        return this.descendantFont.getBoundingBox();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public int readCode(InputStream in) throws IOException {
        return this.cMap.readCode(in);
    }

    public int codeToCID(int code) {
        return this.descendantFont.codeToCID(code);
    }

    public int codeToGID(int code) throws IOException {
        return this.descendantFont.codeToGID(code);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public boolean isStandard14() {
        return false;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public boolean isDamaged() {
        return this.descendantFont.isDamaged();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public String toString() {
        String descendant = null;
        if (getDescendantFont() != null) {
            descendant = getDescendantFont().getClass().getSimpleName();
        }
        return getClass().getSimpleName() + "/" + descendant + ", PostScript name: " + getBaseFont();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDVectorFont
    public GeneralPath getPath(int code) throws IOException {
        return this.descendantFont.getPath(code);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDVectorFont
    public boolean hasGlyph(int code) throws IOException {
        return this.descendantFont.hasGlyph(code);
    }
}
