package org.apache.pdfbox.pdmodel.font;

import com.aowagie.text.pdf.BaseFont;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.FontBoxFont;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.font.encoding.DictionaryEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.GlyphList;
import org.apache.pdfbox.pdmodel.font.encoding.MacRomanEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.StandardEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDSimpleFont.class */
public abstract class PDSimpleFont extends PDFont {
    private static final Log LOG = LogFactory.getLog(PDSimpleFont.class);
    protected Encoding encoding;
    protected GlyphList glyphList;
    private Boolean isSymbolic;
    private final Set<Integer> noUnicode;

    protected abstract Encoding readEncodingFromFont() throws IOException;

    public abstract GeneralPath getPath(String str) throws IOException;

    public abstract boolean hasGlyph(String str) throws IOException;

    public abstract FontBoxFont getFontBoxFont();

    PDSimpleFont() {
        this.noUnicode = new HashSet();
    }

    PDSimpleFont(String baseFont) {
        super(baseFont);
        this.noUnicode = new HashSet();
        assignGlyphList(baseFont);
    }

    PDSimpleFont(COSDictionary fontDictionary) throws IOException {
        super(fontDictionary);
        this.noUnicode = new HashSet();
    }

    protected void readEncoding() throws IOException {
        COSBase encodingBase = this.dict.getDictionaryObject(COSName.ENCODING);
        if (encodingBase instanceof COSName) {
            COSName encodingName = (COSName) encodingBase;
            this.encoding = Encoding.getInstance(encodingName);
            if (this.encoding == null) {
                LOG.warn("Unknown encoding: " + encodingName.getName());
                this.encoding = readEncodingFromFont();
            }
        } else if (encodingBase instanceof COSDictionary) {
            COSDictionary encodingDict = (COSDictionary) encodingBase;
            Encoding builtIn = null;
            Boolean symbolic = getSymbolicFlag();
            COSName baseEncoding = encodingDict.getCOSName(COSName.BASE_ENCODING);
            boolean hasValidBaseEncoding = (baseEncoding == null || Encoding.getInstance(baseEncoding) == null) ? false : true;
            if (!hasValidBaseEncoding && Boolean.TRUE.equals(symbolic)) {
                builtIn = readEncodingFromFont();
            }
            if (symbolic == null) {
                symbolic = false;
            }
            this.encoding = new DictionaryEncoding(encodingDict, !symbolic.booleanValue(), builtIn);
        } else if (encodingBase == null) {
            this.encoding = readEncodingFromFont();
        }
        String standard14Name = Standard14Fonts.getMappedFontName(getName());
        assignGlyphList(standard14Name);
    }

    public Encoding getEncoding() {
        return this.encoding;
    }

    public GlyphList getGlyphList() {
        return this.glyphList;
    }

    public final boolean isSymbolic() {
        if (this.isSymbolic == null) {
            Boolean result = isFontSymbolic();
            if (result != null) {
                this.isSymbolic = result;
            } else {
                this.isSymbolic = true;
            }
        }
        return this.isSymbolic.booleanValue();
    }

    protected Boolean isFontSymbolic() {
        Boolean result = getSymbolicFlag();
        if (result != null) {
            return result;
        }
        if (isStandard14()) {
            String mappedName = Standard14Fonts.getMappedFontName(getName());
            return Boolean.valueOf(mappedName.equals("Symbol") || mappedName.equals("ZapfDingbats"));
        }
        if (this.encoding == null) {
            if (!(this instanceof PDTrueTypeFont)) {
                throw new IllegalStateException("PDFBox bug: encoding should not be null!");
            }
            return true;
        }
        if ((this.encoding instanceof WinAnsiEncoding) || (this.encoding instanceof MacRomanEncoding) || (this.encoding instanceof StandardEncoding)) {
            return false;
        }
        if (this.encoding instanceof DictionaryEncoding) {
            for (String name : ((DictionaryEncoding) this.encoding).getDifferences().values()) {
                if (!BaseFont.notdef.equals(name) && (!WinAnsiEncoding.INSTANCE.contains(name) || !MacRomanEncoding.INSTANCE.contains(name) || !StandardEncoding.INSTANCE.contains(name))) {
                    return true;
                }
            }
            return false;
        }
        return null;
    }

    protected final Boolean getSymbolicFlag() {
        if (getFontDescriptor() != null) {
            return Boolean.valueOf(getFontDescriptor().isSymbolic());
        }
        return null;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public String toUnicode(int code) throws IOException {
        return toUnicode(code, GlyphList.getAdobeGlyphList());
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public String toUnicode(int code, GlyphList customGlyphList) throws IOException {
        GlyphList unicodeGlyphList;
        if (this.glyphList == GlyphList.getAdobeGlyphList()) {
            unicodeGlyphList = customGlyphList;
        } else {
            unicodeGlyphList = this.glyphList;
        }
        String unicode = super.toUnicode(code);
        if (unicode != null) {
            return unicode;
        }
        String name = null;
        if (this.encoding != null) {
            name = this.encoding.getName(code);
            String unicode2 = unicodeGlyphList.toUnicode(name);
            if (unicode2 != null) {
                return unicode2;
            }
        }
        if (LOG.isWarnEnabled() && !this.noUnicode.contains(Integer.valueOf(code))) {
            this.noUnicode.add(Integer.valueOf(code));
            if (name != null) {
                LOG.warn("No Unicode mapping for " + name + " (" + code + ") in font " + getName());
                return null;
            }
            LOG.warn("No Unicode mapping for character code " + code + " in font " + getName());
            return null;
        }
        return null;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public boolean isVertical() {
        return false;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    protected final float getStandard14Width(int code) {
        if (getStandard14AFM() != null) {
            String nameInAFM = getEncoding().getName(code);
            if (BaseFont.notdef.equals(nameInAFM)) {
                return 250.0f;
            }
            if ("nbspace".equals(nameInAFM)) {
                nameInAFM = "space";
            } else if ("sfthyphen".equals(nameInAFM)) {
                nameInAFM = "hyphen";
            }
            return getStandard14AFM().getCharacterWidth(nameInAFM);
        }
        throw new IllegalStateException("No AFM");
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public boolean isStandard14() {
        if (getEncoding() instanceof DictionaryEncoding) {
            DictionaryEncoding dictionary = (DictionaryEncoding) getEncoding();
            if (dictionary.getDifferences().size() > 0) {
                Encoding baseEncoding = dictionary.getBaseEncoding();
                for (Map.Entry<Integer, String> entry : dictionary.getDifferences().entrySet()) {
                    if (!entry.getValue().equals(baseEncoding.getName(entry.getKey().intValue()))) {
                        return false;
                    }
                }
            }
        }
        return super.isStandard14();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public void addToSubset(int codePoint) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public void subset() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFont
    public boolean willBeSubset() {
        return false;
    }

    @Override // org.apache.pdfbox.pdmodel.font.PDFontLike
    public boolean hasExplicitWidth(int code) throws IOException {
        int firstChar;
        if (this.dict.containsKey(COSName.WIDTHS) && code >= (firstChar = this.dict.getInt(COSName.FIRST_CHAR, -1)) && code - firstChar < getWidths().size()) {
            return true;
        }
        return false;
    }

    private void assignGlyphList(String baseFont) {
        if ("ZapfDingbats".equals(baseFont)) {
            this.glyphList = GlyphList.getZapfDingbats();
        } else {
            this.glyphList = GlyphList.getAdobeGlyphList();
        }
    }
}
