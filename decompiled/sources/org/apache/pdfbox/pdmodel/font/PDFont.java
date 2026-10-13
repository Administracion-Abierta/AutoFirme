package org.apache.pdfbox.pdmodel.font;

import com.aowagie.text.pdf.ColumnText;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.afm.FontMetrics;
import org.apache.fontbox.cmap.CMap;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.font.encoding.GlyphList;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDFont.class */
public abstract class PDFont implements COSObjectable, PDFontLike {
    private static final Log LOG = LogFactory.getLog(PDFont.class);
    protected static final Matrix DEFAULT_FONT_MATRIX = new Matrix(0.001f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, 0.001f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    protected final COSDictionary dict;
    private final CMap toUnicodeCMap;
    private final FontMetrics afmStandard14;
    private PDFontDescriptor fontDescriptor;
    private List<Float> widths;
    private float avgFontWidth;
    private float fontWidthOfSpace;
    private final Map<Integer, Float> codeToWidthMap;

    protected abstract float getStandard14Width(int i);

    protected abstract byte[] encode(int i) throws IOException;

    public abstract int readCode(InputStream inputStream) throws IOException;

    public abstract boolean isVertical();

    public abstract void addToSubset(int i);

    public abstract void subset() throws IOException;

    public abstract boolean willBeSubset();

    PDFont() {
        this.fontWidthOfSpace = -1.0f;
        this.dict = new COSDictionary();
        this.dict.setItem(COSName.TYPE, (COSBase) COSName.FONT);
        this.toUnicodeCMap = null;
        this.fontDescriptor = null;
        this.afmStandard14 = null;
        this.codeToWidthMap = new HashMap();
    }

    PDFont(String baseFont) {
        this.fontWidthOfSpace = -1.0f;
        this.dict = new COSDictionary();
        this.dict.setItem(COSName.TYPE, (COSBase) COSName.FONT);
        this.toUnicodeCMap = null;
        this.afmStandard14 = Standard14Fonts.getAFM(baseFont);
        if (this.afmStandard14 == null) {
            throw new IllegalArgumentException("No AFM for font " + baseFont);
        }
        this.fontDescriptor = PDType1FontEmbedder.buildFontDescriptor(this.afmStandard14);
        this.codeToWidthMap = new ConcurrentHashMap();
    }

    protected PDFont(COSDictionary fontDictionary) throws IOException {
        this.fontWidthOfSpace = -1.0f;
        this.dict = fontDictionary;
        this.codeToWidthMap = new HashMap();
        this.afmStandard14 = Standard14Fonts.getAFM(getName());
        this.fontDescriptor = loadFontDescriptor();
        this.toUnicodeCMap = loadUnicodeCmap();
    }

    private PDFontDescriptor loadFontDescriptor() {
        COSDictionary fd = this.dict.getCOSDictionary(COSName.FONT_DESC);
        if (fd != null) {
            return new PDFontDescriptor(fd);
        }
        if (this.afmStandard14 != null) {
            return PDType1FontEmbedder.buildFontDescriptor(this.afmStandard14);
        }
        return null;
    }

    private CMap loadUnicodeCmap() {
        COSBase toUnicode = this.dict.getDictionaryObject(COSName.TO_UNICODE);
        if (toUnicode == null) {
            return null;
        }
        CMap cmap = null;
        try {
            cmap = readCMap(toUnicode);
            if (cmap != null && !cmap.hasUnicodeMappings()) {
                LOG.warn("Invalid ToUnicode CMap in font " + getName());
                String cmapName = cmap.getName() != null ? cmap.getName() : "";
                String ordering = cmap.getOrdering() != null ? cmap.getOrdering() : "";
                COSBase encoding = this.dict.getDictionaryObject(COSName.ENCODING);
                if (cmapName.contains("Identity") || ordering.contains("Identity") || COSName.IDENTITY_H.equals(encoding) || COSName.IDENTITY_V.equals(encoding)) {
                    cmap = CMapManager.getPredefinedCMap(COSName.IDENTITY_H.getName());
                    LOG.warn("Using predefined identity CMap instead");
                }
            }
        } catch (IOException ex) {
            LOG.error("Could not read ToUnicode CMap in font " + getName(), ex);
        }
        return cmap;
    }

    protected final FontMetrics getStandard14AFM() {
        return this.afmStandard14;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public PDFontDescriptor getFontDescriptor() {
        return this.fontDescriptor;
    }

    protected final void setFontDescriptor(PDFontDescriptor fontDescriptor) {
        this.fontDescriptor = fontDescriptor;
    }

    protected final CMap readCMap(COSBase base) throws IOException {
        if (base instanceof COSName) {
            String name = ((COSName) base).getName();
            return CMapManager.getPredefinedCMap(name);
        }
        if (base instanceof COSStream) {
            InputStream input = null;
            try {
                InputStream input2 = ((COSStream) base).createInputStream();
                return CMapManager.parseCMap(input2);
            } finally {
                IOUtils.closeQuietly(input);
            }
        }
        throw new IOException("Expected Name or Stream");
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dict;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public Vector getPositionVector(int code) {
        throw new UnsupportedOperationException("Horizontal fonts have no position vector");
    }

    public Vector getDisplacement(int code) throws IOException {
        return new Vector(getWidth(code) / 1000.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getWidth(int code) throws IOException {
        Float width = this.codeToWidthMap.get(Integer.valueOf(code));
        if (width != null) {
            return width.floatValue();
        }
        if (this.dict.getDictionaryObject(COSName.WIDTHS) != null || this.dict.containsKey(COSName.MISSING_WIDTH)) {
            int firstChar = this.dict.getInt(COSName.FIRST_CHAR, -1);
            int lastChar = this.dict.getInt(COSName.LAST_CHAR, -1);
            int siz = getWidths().size();
            int idx = code - firstChar;
            if (siz > 0 && code >= firstChar && code <= lastChar && idx < siz) {
                Float width2 = getWidths().get(idx);
                if (width2 == null) {
                    width2 = Float.valueOf(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                }
                this.codeToWidthMap.put(Integer.valueOf(code), width2);
                return width2.floatValue();
            }
            PDFontDescriptor fd = getFontDescriptor();
            if (fd != null) {
                Float width3 = Float.valueOf(fd.getMissingWidth());
                this.codeToWidthMap.put(Integer.valueOf(code), width3);
                return width3.floatValue();
            }
        }
        if (isStandard14()) {
            Float width4 = Float.valueOf(getStandard14Width(code));
            this.codeToWidthMap.put(Integer.valueOf(code), width4);
            return width4.floatValue();
        }
        Float width5 = Float.valueOf(getWidthFromFont(code));
        this.codeToWidthMap.put(Integer.valueOf(code), width5);
        return width5.floatValue();
    }

    public final byte[] encode(String text) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int iCharCount = 0;
        while (true) {
            int offset = iCharCount;
            if (offset < text.length()) {
                int codePoint = text.codePointAt(offset);
                byte[] bytes = encode(codePoint);
                out.write(bytes);
                iCharCount = offset + Character.charCount(codePoint);
            } else {
                return out.toByteArray();
            }
        }
    }

    public float getStringWidth(String text) throws IOException {
        byte[] bytes = encode(text);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        float width = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        while (true) {
            float width2 = width;
            if (in.available() > 0) {
                int code = readCode(in);
                width = width2 + getWidth(code);
            } else {
                return width2;
            }
        }
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public float getAverageFontWidth() {
        float average;
        if (this.avgFontWidth != ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            average = this.avgFontWidth;
        } else {
            float totalWidth = 0.0f;
            float characterCount = 0.0f;
            COSArray widths = this.dict.getCOSArray(COSName.WIDTHS);
            if (widths != null) {
                for (int i = 0; i < widths.size(); i++) {
                    COSNumber fontWidth = (COSNumber) widths.getObject(i);
                    if (fontWidth.floatValue() > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                        totalWidth += fontWidth.floatValue();
                        characterCount += 1.0f;
                    }
                }
            }
            if (totalWidth > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                average = totalWidth / characterCount;
            } else {
                average = 0.0f;
            }
            this.avgFontWidth = average;
        }
        return average;
    }

    public String toUnicode(int code, GlyphList customGlyphList) throws IOException {
        return toUnicode(code);
    }

    public String toUnicode(int code) throws IOException {
        if (this.toUnicodeCMap != null) {
            if (this.toUnicodeCMap.getName() != null && this.toUnicodeCMap.getName().startsWith("Identity-") && ((this.dict.getDictionaryObject(COSName.TO_UNICODE) instanceof COSName) || !this.toUnicodeCMap.hasUnicodeMappings())) {
                return new String(new char[]{(char) code});
            }
            return this.toUnicodeCMap.toUnicode(code);
        }
        return null;
    }

    public String getType() {
        return this.dict.getNameAsString(COSName.TYPE);
    }

    public String getSubType() {
        return this.dict.getNameAsString(COSName.SUBTYPE);
    }

    protected final List<Float> getWidths() {
        if (this.widths == null) {
            COSArray array = this.dict.getCOSArray(COSName.WIDTHS);
            if (array != null) {
                this.widths = COSArrayList.convertFloatCOSArrayToList(array);
            } else {
                this.widths = Collections.emptyList();
            }
        }
        return this.widths;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public Matrix getFontMatrix() {
        return DEFAULT_FONT_MATRIX;
    }

    /* JADX WARN: Code duplicated, block: B:13:0x0039 A[Catch: Exception -> 0x006a, TryCatch #0 {Exception -> 0x006a, blocks: (B:7:0x0019, B:9:0x0020, B:11:0x002d, B:14:0x0043, B:16:0x004c, B:17:0x0056, B:19:0x005f, B:13:0x0039), top: B:25:0x0019 }] */
    /* JADX WARN: Code duplicated, block: B:16:0x004c A[Catch: Exception -> 0x006a, TryCatch #0 {Exception -> 0x006a, blocks: (B:7:0x0019, B:9:0x0020, B:11:0x002d, B:14:0x0043, B:16:0x004c, B:17:0x0056, B:19:0x005f, B:13:0x0039), top: B:25:0x0019 }] */
    /* JADX WARN: Code duplicated, block: B:19:0x005f A[Catch: Exception -> 0x006a, TryCatch #0 {Exception -> 0x006a, blocks: (B:7:0x0019, B:9:0x0020, B:11:0x002d, B:14:0x0043, B:16:0x004c, B:17:0x0056, B:19:0x005f, B:13:0x0039), top: B:25:0x0019 }] */
    public float getSpaceWidth() {
        if (this.fontWidthOfSpace == -1.0f) {
            COSBase toUnicode = this.dict.getDictionaryObject(COSName.TO_UNICODE);
            if (toUnicode == null) {
                this.fontWidthOfSpace = getWidth(32);
                if (this.fontWidthOfSpace <= ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                    this.fontWidthOfSpace = getWidthFromFont(32);
                }
                if (this.fontWidthOfSpace <= ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                    this.fontWidthOfSpace = getAverageFontWidth();
                }
            } else {
                try {
                    if (this.toUnicodeCMap != null) {
                        int spaceMapping = this.toUnicodeCMap.getSpaceMapping();
                        if (spaceMapping > -1) {
                            this.fontWidthOfSpace = getWidth(spaceMapping);
                        }
                    } else {
                        this.fontWidthOfSpace = getWidth(32);
                    }
                    if (this.fontWidthOfSpace <= ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                        this.fontWidthOfSpace = getWidthFromFont(32);
                    }
                    if (this.fontWidthOfSpace <= ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                        this.fontWidthOfSpace = getAverageFontWidth();
                    }
                } catch (Exception e) {
                    LOG.error("Can't determine the width of the space character, assuming 250", e);
                    this.fontWidthOfSpace = 250.0f;
                }
            }
        }
        return this.fontWidthOfSpace;
    }

    public boolean isStandard14() {
        if (isEmbedded()) {
            return false;
        }
        return Standard14Fonts.containsName(getName());
    }

    public boolean equals(Object other) {
        return (other instanceof PDFont) && ((PDFont) other).getCOSObject() == getCOSObject();
    }

    public int hashCode() {
        return getCOSObject().hashCode();
    }

    public String toString() {
        return getClass().getSimpleName() + " " + getName();
    }

    protected CMap getToUnicodeCMap() {
        return this.toUnicodeCMap;
    }
}
