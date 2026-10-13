package com.aowagie.text.pdf;

import com.aowagie.text.DocumentException;
import com.sun.jna.platform.win32.WinError;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/BaseFont.class */
public abstract class BaseFont {
    public static final String COURIER = "Courier";
    public static final String COURIER_BOLD = "Courier-Bold";
    public static final String COURIER_OBLIQUE = "Courier-Oblique";
    public static final String COURIER_BOLDOBLIQUE = "Courier-BoldOblique";
    public static final String HELVETICA = "Helvetica";
    public static final String HELVETICA_BOLD = "Helvetica-Bold";
    public static final String HELVETICA_OBLIQUE = "Helvetica-Oblique";
    public static final String HELVETICA_BOLDOBLIQUE = "Helvetica-BoldOblique";
    public static final String SYMBOL = "Symbol";
    public static final String TIMES_ROMAN = "Times-Roman";
    public static final String TIMES_BOLD = "Times-Bold";
    public static final String TIMES_ITALIC = "Times-Italic";
    public static final String TIMES_BOLDITALIC = "Times-BoldItalic";
    public static final String ZAPFDINGBATS = "ZapfDingbats";
    public static final int ASCENT = 1;
    public static final int CAPHEIGHT = 2;
    public static final int DESCENT = 3;
    public static final int ITALICANGLE = 4;
    public static final int BBOXLLX = 5;
    public static final int BBOXLLY = 6;
    public static final int BBOXURX = 7;
    public static final int BBOXURY = 8;
    public static final int AWT_ASCENT = 9;
    public static final int AWT_DESCENT = 10;
    public static final int AWT_LEADING = 11;
    public static final int AWT_MAXADVANCE = 12;
    public static final int UNDERLINE_POSITION = 13;
    public static final int UNDERLINE_THICKNESS = 14;
    public static final int STRIKETHROUGH_POSITION = 15;
    public static final int STRIKETHROUGH_THICKNESS = 16;
    public static final int SUBSCRIPT_SIZE = 17;
    public static final int SUBSCRIPT_OFFSET = 18;
    public static final int SUPERSCRIPT_SIZE = 19;
    public static final int SUPERSCRIPT_OFFSET = 20;
    public static final int FONT_TYPE_T1 = 0;
    public static final int FONT_TYPE_TT = 1;
    public static final int FONT_TYPE_CJK = 2;
    public static final int FONT_TYPE_TTUNI = 3;
    public static final int FONT_TYPE_DOCUMENT = 4;
    public static final int FONT_TYPE_T3 = 5;
    public static final String IDENTITY_H = "Identity-H";
    public static final String IDENTITY_V = "Identity-V";
    public static final String CP1250 = "Cp1250";
    public static final String CP1252 = "Cp1252";
    public static final String CP1257 = "Cp1257";
    public static final String WINANSI = "Cp1252";
    public static final String MACROMAN = "MacRoman";
    public static final boolean EMBEDDED = true;
    public static final boolean NOT_EMBEDDED = false;
    public static final boolean CACHED = true;
    public static final boolean NOT_CACHED = false;
    public static final String RESOURCE_PATH = "com/lowagie/text/pdf/fonts/";
    public static final char CID_NEWLINE = 32767;
    protected ArrayList subsetRanges;
    int fontType;
    public static final String notdef = ".notdef";
    protected String encoding;
    protected boolean embedded;
    protected IntHashtable specialMap;
    public static final int[] CHAR_RANGE_LATIN = {0, 383, 8192, WinError.ERROR_DS_ATT_NOT_DEF_IN_SCHEMA, WinError.ERROR_DS_NOT_AN_OBJECT, WinError.ERROR_DS_ATT_SCHEMA_REQ_ID, 64256, 64262};
    public static final int[] CHAR_RANGE_ARABIC = {0, 127, 1536, 1663, WinError.ERROR_DS_NOT_AN_OBJECT, WinError.ERROR_DS_ATT_SCHEMA_REQ_ID, 64336, 64511, 65136, 65279};
    public static final int[] CHAR_RANGE_HEBREW = {0, 127, WinError.ERROR_INVALID_EDIT_HEIGHT, 1535, WinError.ERROR_DS_NOT_AN_OBJECT, WinError.ERROR_DS_ATT_SCHEMA_REQ_ID, 64285, 64335};
    public static final int[] CHAR_RANGE_CYRILLIC = {0, 127, 1024, WinError.ERROR_ACCOUNT_RESTRICTION, 8192, WinError.ERROR_DS_ATT_NOT_DEF_IN_SCHEMA, WinError.ERROR_DS_NOT_AN_OBJECT, WinError.ERROR_DS_ATT_SCHEMA_REQ_ID};
    protected static HashMap fontCache = new HashMap();
    protected static final HashMap BuiltinFonts14 = new HashMap();
    protected int[] widths = new int[256];
    protected String[] differences = new String[256];
    protected char[] unicodeDifferences = new char[256];
    protected int[][] charBBoxes = new int[256][];
    protected int compressionLevel = -1;
    protected boolean fontSpecific = true;
    protected boolean forceWidthsOutput = false;
    protected boolean directTextToByte = false;
    protected boolean subset = true;
    protected boolean fastWinansi = false;

    abstract int getRawWidth(int i, String str);

    public abstract int getKerning(int i, int i2);

    public abstract boolean setKerning(int i, int i2, int i3);

    abstract void writeFont(PdfWriter pdfWriter, PdfIndirectReference pdfIndirectReference, Object[] objArr) throws IOException, DocumentException;

    abstract PdfStream getFullFontStream() throws IOException, DocumentException;

    public abstract float getFontDescriptor(int i, float f);

    public abstract String getPostscriptFontName();

    public abstract void setPostscriptFontName(String str);

    public abstract String[][] getFullFontName();

    public abstract String[][] getAllNameEntries();

    public abstract String[][] getFamilyFontName();

    public abstract boolean hasKernPairs();

    protected abstract int[] getRawCharBBox(int i, String str);

    static {
        BuiltinFonts14.put("Courier", PdfName.COURIER);
        BuiltinFonts14.put("Courier-Bold", PdfName.COURIER_BOLD);
        BuiltinFonts14.put("Courier-BoldOblique", PdfName.COURIER_BOLDOBLIQUE);
        BuiltinFonts14.put("Courier-Oblique", PdfName.COURIER_OBLIQUE);
        BuiltinFonts14.put("Helvetica", PdfName.HELVETICA);
        BuiltinFonts14.put("Helvetica-Bold", PdfName.HELVETICA_BOLD);
        BuiltinFonts14.put("Helvetica-BoldOblique", PdfName.HELVETICA_BOLDOBLIQUE);
        BuiltinFonts14.put("Helvetica-Oblique", PdfName.HELVETICA_OBLIQUE);
        BuiltinFonts14.put("Symbol", PdfName.SYMBOL);
        BuiltinFonts14.put("Times-Roman", PdfName.TIMES_ROMAN);
        BuiltinFonts14.put("Times-Bold", PdfName.TIMES_BOLD);
        BuiltinFonts14.put("Times-BoldItalic", PdfName.TIMES_BOLDITALIC);
        BuiltinFonts14.put("Times-Italic", PdfName.TIMES_ITALIC);
        BuiltinFonts14.put("ZapfDingbats", PdfName.ZAPFDINGBATS);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/BaseFont$StreamFont.class */
    static class StreamFont extends PdfStream {
        public StreamFont(byte[] contents, int[] lengths, int compressionLevel) throws DocumentException {
            try {
                this.bytes = contents;
                put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
                for (int k = 0; k < lengths.length; k++) {
                    put(new PdfName("Length" + (k + 1)), new PdfNumber(lengths[k]));
                }
                flateCompress(compressionLevel);
            } catch (Exception e) {
                throw new DocumentException(e);
            }
        }

        public StreamFont(byte[] contents, String subType, int compressionLevel) throws DocumentException {
            try {
                this.bytes = contents;
                put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
                if (subType != null) {
                    put(PdfName.SUBTYPE, new PdfName(subType));
                }
                flateCompress(compressionLevel);
            } catch (Exception e) {
                throw new DocumentException(e);
            }
        }
    }

    protected BaseFont() {
    }

    public static BaseFont createFont() throws IOException, DocumentException {
        return createFont("Helvetica", "Cp1252", false);
    }

    public static BaseFont createFont(String name, String encoding, boolean embedded) throws IOException, DocumentException {
        return createFont(name, encoding, embedded, true, null, null, false);
    }

    public static BaseFont createFont(String name, String encoding, boolean embedded, boolean forceRead) throws IOException, DocumentException {
        return createFont(name, encoding, embedded, true, null, null, forceRead);
    }

    public static BaseFont createFont(String name, String encoding, boolean embedded, boolean cached, byte[] ttfAfm, byte[] pfb) throws IOException, DocumentException {
        return createFont(name, encoding, embedded, cached, ttfAfm, pfb, false);
    }

    public static BaseFont createFont(String name, String encoding, boolean embedded, boolean cached, byte[] ttfAfm, byte[] pfb, boolean noThrow) throws IOException, DocumentException {
        return createFont(name, encoding, embedded, cached, ttfAfm, pfb, false, false);
    }

    public static BaseFont createFont(String name, String encoding, boolean embedded, boolean cached, byte[] ttfAfm, byte[] pfb, boolean noThrow, boolean forceRead) throws IOException, DocumentException {
        BaseFont fontBuilt;
        BaseFont fontFound;
        String nameBase = getBaseName(name);
        String encoding2 = normalizeEncoding(encoding);
        boolean isBuiltinFonts14 = BuiltinFonts14.containsKey(name);
        boolean isCJKFont = isBuiltinFonts14 ? false : CJKFont.isCJKFont(nameBase, encoding2);
        if (isBuiltinFonts14 || isCJKFont) {
            embedded = false;
        } else if (encoding2.equals(IDENTITY_H) || encoding2.equals(IDENTITY_V)) {
            embedded = true;
        }
        String key = name + "\n" + encoding2 + "\n" + embedded;
        if (cached) {
            synchronized (fontCache) {
                fontFound = (BaseFont) fontCache.get(key);
            }
            if (fontFound != null) {
                return fontFound;
            }
        }
        if (isBuiltinFonts14 || name.toLowerCase().endsWith(".afm") || name.toLowerCase().endsWith(".pfm")) {
            fontBuilt = new Type1Font(name, encoding2, embedded, ttfAfm, pfb, forceRead);
            fontBuilt.fastWinansi = encoding2.equals("Cp1252");
        } else if (nameBase.toLowerCase().endsWith(".ttf") || nameBase.toLowerCase().endsWith(".otf") || nameBase.toLowerCase().indexOf(".ttc,") > 0) {
            if (encoding2.equals(IDENTITY_H) || encoding2.equals(IDENTITY_V)) {
                fontBuilt = new TrueTypeFontUnicode(name, encoding2, embedded, ttfAfm, forceRead);
            } else {
                fontBuilt = new TrueTypeFont(name, encoding2, embedded, ttfAfm, false, forceRead);
                fontBuilt.fastWinansi = encoding2.equals("Cp1252");
            }
        } else if (isCJKFont) {
            fontBuilt = new CJKFont(name, encoding2, embedded);
        } else {
            if (noThrow) {
                return null;
            }
            throw new DocumentException("Font '" + name + "' with '" + encoding2 + "' is not recognized.");
        }
        if (cached) {
            synchronized (fontCache) {
                BaseFont fontFound2 = (BaseFont) fontCache.get(key);
                if (fontFound2 != null) {
                    return fontFound2;
                }
                fontCache.put(key, fontBuilt);
            }
        }
        return fontBuilt;
    }

    public static BaseFont createFont(PRIndirectReference fontRef) {
        return new DocumentFont(fontRef);
    }

    protected static String getBaseName(String name) {
        if (name.endsWith(",Bold")) {
            return name.substring(0, name.length() - 5);
        }
        if (name.endsWith(",Italic")) {
            return name.substring(0, name.length() - 7);
        }
        if (name.endsWith(",BoldItalic")) {
            return name.substring(0, name.length() - 11);
        }
        return name;
    }

    protected static String normalizeEncoding(String enc) {
        if (enc.equals("winansi") || enc.equals("")) {
            return "Cp1252";
        }
        if (enc.equals("macroman")) {
            return MACROMAN;
        }
        return enc;
    }

    protected void createEncoding() {
        char c;
        int orderK;
        if (this.encoding.startsWith("#")) {
            this.specialMap = new IntHashtable();
            StringTokenizer tok = new StringTokenizer(this.encoding.substring(1), " ,\t\n\r\f");
            if (tok.nextToken().equals("full")) {
                while (tok.hasMoreTokens()) {
                    String order = tok.nextToken();
                    String name = tok.nextToken();
                    char uni = (char) Integer.parseInt(tok.nextToken(), 16);
                    if (order.startsWith(OperatorName.SHOW_TEXT_LINE)) {
                        orderK = order.charAt(1);
                    } else {
                        orderK = Integer.parseInt(order);
                    }
                    int orderK2 = orderK % 256;
                    this.specialMap.put(uni, orderK2);
                    this.differences[orderK2] = name;
                    this.unicodeDifferences[orderK2] = uni;
                    this.widths[orderK2] = getRawWidth(uni, name);
                    this.charBBoxes[orderK2] = getRawCharBBox(uni, name);
                }
            } else {
                int k = 0;
                if (tok.hasMoreTokens()) {
                    k = Integer.parseInt(tok.nextToken());
                }
                while (tok.hasMoreTokens() && k < 256) {
                    String hex = tok.nextToken();
                    int uni2 = Integer.parseInt(hex, 16) % 65536;
                    String name2 = GlyphList.unicodeToName(uni2);
                    if (name2 != null) {
                        this.specialMap.put(uni2, k);
                        this.differences[k] = name2;
                        this.unicodeDifferences[k] = (char) uni2;
                        this.widths[k] = getRawWidth(uni2, name2);
                        this.charBBoxes[k] = getRawCharBBox(uni2, name2);
                        k++;
                    }
                }
            }
            for (int k2 = 0; k2 < 256; k2++) {
                if (this.differences[k2] == null) {
                    this.differences[k2] = notdef;
                }
            }
            return;
        }
        if (this.fontSpecific) {
            for (int k3 = 0; k3 < 256; k3++) {
                this.widths[k3] = getRawWidth(k3, null);
                this.charBBoxes[k3] = getRawCharBBox(k3, null);
            }
            return;
        }
        byte[] b = new byte[1];
        for (int k4 = 0; k4 < 256; k4++) {
            b[0] = (byte) k4;
            String s = PdfEncodings.convertToString(b, this.encoding);
            if (s.length() > 0) {
                c = s.charAt(0);
            } else {
                c = '?';
            }
            String name3 = GlyphList.unicodeToName(c);
            if (name3 == null) {
                name3 = notdef;
            }
            this.differences[k4] = name3;
            this.unicodeDifferences[k4] = c;
            this.widths[k4] = getRawWidth(c, name3);
            this.charBBoxes[k4] = getRawCharBBox(c, name3);
        }
    }

    public int getWidth(int char1) {
        if (this.fastWinansi) {
            if (char1 < 128 || (char1 >= 160 && char1 <= 255)) {
                return this.widths[char1];
            }
            return this.widths[PdfEncodings.winansi.get(char1)];
        }
        int total = 0;
        byte[] mbytes = convertToBytes((char) char1);
        for (byte mbyte : mbytes) {
            total += this.widths[255 & mbyte];
        }
        return total;
    }

    public int getWidth(String text) {
        int total = 0;
        if (this.fastWinansi) {
            int len = text.length();
            for (int k = 0; k < len; k++) {
                char char1 = text.charAt(k);
                total += (char1 < 128 || (char1 >= 160 && char1 <= 255)) ? this.widths[char1] : this.widths[PdfEncodings.winansi.get(char1)];
            }
            return total;
        }
        byte[] mbytes = convertToBytes(text);
        for (byte mbyte : mbytes) {
            total += this.widths[255 & mbyte];
        }
        return total;
    }

    public int getDescent(String text) {
        int min = 0;
        char[] chars = text.toCharArray();
        for (char c : chars) {
            int[] bbox = getCharBBox(c);
            if (bbox != null && bbox[1] < min) {
                min = bbox[1];
            }
        }
        return min;
    }

    public int getAscent(String text) {
        int max = 0;
        char[] chars = text.toCharArray();
        for (char c : chars) {
            int[] bbox = getCharBBox(c);
            if (bbox != null && bbox[3] > max) {
                max = bbox[3];
            }
        }
        return max;
    }

    public float getDescentPoint(String text, float fontSize) {
        return getDescent(text) * 0.001f * fontSize;
    }

    public float getAscentPoint(String text, float fontSize) {
        return getAscent(text) * 0.001f * fontSize;
    }

    public float getWidthPointKerned(String text, float fontSize) {
        float size = getWidth(text) * 0.001f * fontSize;
        if (!hasKernPairs()) {
            return size;
        }
        int len = text.length() - 1;
        int kern = 0;
        char[] c = text.toCharArray();
        for (int k = 0; k < len; k++) {
            kern += getKerning(c[k], c[k + 1]);
        }
        return size + (kern * 0.001f * fontSize);
    }

    public float getWidthPoint(String text, float fontSize) {
        return getWidth(text) * 0.001f * fontSize;
    }

    public float getWidthPoint(int char1, float fontSize) {
        return getWidth(char1) * 0.001f * fontSize;
    }

    byte[] convertToBytes(String text) {
        if (this.directTextToByte) {
            return PdfEncodings.convertToBytes(text, (String) null);
        }
        if (this.specialMap != null) {
            byte[] b = new byte[text.length()];
            int ptr = 0;
            int length = text.length();
            for (int k = 0; k < length; k++) {
                char c = text.charAt(k);
                if (this.specialMap.containsKey(c)) {
                    int i = ptr;
                    ptr++;
                    b[i] = (byte) this.specialMap.get(c);
                }
            }
            if (ptr < length) {
                byte[] b2 = new byte[ptr];
                System.arraycopy(b, 0, b2, 0, ptr);
                return b2;
            }
            return b;
        }
        return PdfEncodings.convertToBytes(text, this.encoding);
    }

    byte[] convertToBytes(int char1) {
        if (this.directTextToByte) {
            return PdfEncodings.convertToBytes((char) char1, (String) null);
        }
        if (this.specialMap != null) {
            if (this.specialMap.containsKey(char1)) {
                return new byte[]{(byte) this.specialMap.get(char1)};
            }
            return new byte[0];
        }
        return PdfEncodings.convertToBytes((char) char1, this.encoding);
    }

    public String getEncoding() {
        return this.encoding;
    }

    public int getFontType() {
        return this.fontType;
    }

    public boolean isEmbedded() {
        return this.embedded;
    }

    public boolean isFontSpecific() {
        return this.fontSpecific;
    }

    public static String createSubsetPrefix() {
        String s = "";
        for (int k = 0; k < 6; k++) {
            s = s + ((char) ((Math.random() * 26.0d) + 65.0d));
        }
        return s + Marker.ANY_NON_NULL_MARKER;
    }

    char getUnicodeDifferences(int index) {
        return this.unicodeDifferences[index];
    }

    public static String[][] getFullFontName(String name, String encoding, byte[] ttfAfm) throws IOException, DocumentException {
        BaseFont fontBuilt;
        String nameBase = getBaseName(name);
        if (nameBase.toLowerCase().endsWith(".ttf") || nameBase.toLowerCase().endsWith(".otf") || nameBase.toLowerCase().indexOf(".ttc,") > 0) {
            fontBuilt = new TrueTypeFont(name, "Cp1252", false, ttfAfm, true, false);
        } else {
            fontBuilt = createFont(name, encoding, false, false, ttfAfm, null);
        }
        return fontBuilt.getFullFontName();
    }

    public static Object[] getAllFontNames(String name, String encoding, byte[] ttfAfm) throws IOException, DocumentException {
        BaseFont fontBuilt;
        String nameBase = getBaseName(name);
        if (nameBase.toLowerCase().endsWith(".ttf") || nameBase.toLowerCase().endsWith(".otf") || nameBase.toLowerCase().indexOf(".ttc,") > 0) {
            fontBuilt = new TrueTypeFont(name, "Cp1252", false, ttfAfm, true, false);
        } else {
            fontBuilt = createFont(name, encoding, false, false, ttfAfm, null);
        }
        return new Object[]{fontBuilt.getPostscriptFontName(), fontBuilt.getFamilyFontName(), fontBuilt.getFullFontName()};
    }

    public static String[][] getAllNameEntries(String name, String encoding, byte[] ttfAfm) throws IOException, DocumentException {
        BaseFont fontBuilt;
        String nameBase = getBaseName(name);
        if (nameBase.toLowerCase().endsWith(".ttf") || nameBase.toLowerCase().endsWith(".otf") || nameBase.toLowerCase().indexOf(".ttc,") > 0) {
            fontBuilt = new TrueTypeFont(name, "Cp1252", false, ttfAfm, true, false);
        } else {
            fontBuilt = createFont(name, encoding, false, false, ttfAfm, null);
        }
        return fontBuilt.getAllNameEntries();
    }

    public String[] getCodePagesSupported() {
        return new String[0];
    }

    public static String[] enumerateTTCNames(String ttcFile) throws IOException, DocumentException {
        return new EnumerateTTC(ttcFile).getNames();
    }

    public static String[] enumerateTTCNames(byte[] ttcArray) throws IOException, DocumentException {
        return new EnumerateTTC(ttcArray).getNames();
    }

    public int[] getWidths() {
        return this.widths;
    }

    public String[] getDifferences() {
        return this.differences;
    }

    public char[] getUnicodeDifferences() {
        return this.unicodeDifferences;
    }

    public boolean isForceWidthsOutput() {
        return this.forceWidthsOutput;
    }

    public void setForceWidthsOutput(boolean forceWidthsOutput) {
        this.forceWidthsOutput = forceWidthsOutput;
    }

    public boolean isDirectTextToByte() {
        return this.directTextToByte;
    }

    public void setDirectTextToByte(boolean directTextToByte) {
        this.directTextToByte = directTextToByte;
    }

    public boolean isSubset() {
        return this.subset;
    }

    public void setSubset(boolean subset) {
        this.subset = subset;
    }

    public static InputStream getResourceStream(String key) {
        return getResourceStream(key, null);
    }

    public static InputStream getResourceStream(String key, ClassLoader loader) {
        if (key.startsWith("/")) {
            key = key.substring(1);
        }
        InputStream is = null;
        if (loader != null) {
            is = loader.getResourceAsStream(key);
            if (is != null) {
                return is;
            }
        }
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                is = contextClassLoader.getResourceAsStream(key);
            }
        } catch (Throwable th) {
        }
        if (is == null) {
            is = BaseFont.class.getResourceAsStream("/" + key);
        }
        if (is == null) {
            is = ClassLoader.getSystemResourceAsStream(key);
        }
        return is;
    }

    public int getUnicodeEquivalent(int c) {
        return c;
    }

    public int getCidCode(int c) {
        return c;
    }

    public boolean charExists(int c) {
        byte[] b = convertToBytes(c);
        return b.length > 0;
    }

    public boolean setCharAdvance(int c, int advance) {
        byte[] b = convertToBytes(c);
        if (b.length == 0) {
            return false;
        }
        this.widths[255 & b[0]] = advance;
        return true;
    }

    private static void addFont(PRIndirectReference fontRef, IntHashtable hits, ArrayList fonts) {
        PdfObject obj = PdfReader.getPdfObject(fontRef);
        if (obj == null || !obj.isDictionary()) {
            return;
        }
        PdfDictionary font = (PdfDictionary) obj;
        PdfName subtype = font.getAsName(PdfName.SUBTYPE);
        if (!PdfName.TYPE1.equals(subtype) && !PdfName.TRUETYPE.equals(subtype)) {
            return;
        }
        PdfName name = font.getAsName(PdfName.BASEFONT);
        fonts.add(new Object[]{PdfName.decodeName(name.toString()), fontRef});
        hits.put(fontRef.getNumber(), 1);
    }

    private static void recourseFonts(PdfDictionary page, IntHashtable hits, ArrayList fonts, int level) {
        PdfDictionary resources;
        int level2 = level + 1;
        if (level2 > 50 || (resources = page.getAsDict(PdfName.RESOURCES)) == null) {
            return;
        }
        PdfDictionary font = resources.getAsDict(PdfName.FONT);
        if (font != null) {
            for (Object element : font.getKeys()) {
                PdfObject ft = font.get((PdfName) element);
                if (ft != null && ft.isIndirect()) {
                    int hit = ((PRIndirectReference) ft).getNumber();
                    if (!hits.containsKey(hit)) {
                        addFont((PRIndirectReference) ft, hits, fonts);
                    }
                }
            }
        }
        PdfDictionary xobj = resources.getAsDict(PdfName.XOBJECT);
        if (xobj != null) {
            for (Object element2 : xobj.getKeys()) {
                recourseFonts(xobj.getAsDict((PdfName) element2), hits, fonts, level2);
            }
        }
    }

    public static ArrayList getDocumentFonts(PdfReader reader) {
        IntHashtable hits = new IntHashtable();
        ArrayList fonts = new ArrayList();
        int npages = reader.getNumberOfPages();
        for (int k = 1; k <= npages; k++) {
            recourseFonts(reader.getPageN(k), hits, fonts, 1);
        }
        return fonts;
    }

    public static ArrayList getDocumentFonts(PdfReader reader, int page) {
        IntHashtable hits = new IntHashtable();
        ArrayList fonts = new ArrayList();
        recourseFonts(reader.getPageN(page), hits, fonts, 1);
        return fonts;
    }

    public int[] getCharBBox(int c) {
        byte[] b = convertToBytes(c);
        if (b.length == 0) {
            return null;
        }
        return this.charBBoxes[b[0] & 255];
    }

    public void correctArabicAdvance() {
        char c = 1611;
        while (true) {
            char c2 = c;
            if (c2 > 1624) {
                break;
            }
            setCharAdvance(c2, 0);
            c = (char) (c2 + 1);
        }
        setCharAdvance(WinError.ERROR_PATCH_NO_SEQUENCE, 0);
        char c3 = 1750;
        while (true) {
            char c4 = c3;
            if (c4 > 1756) {
                break;
            }
            setCharAdvance(c4, 0);
            c3 = (char) (c4 + 1);
        }
        char c5 = 1759;
        while (true) {
            char c6 = c5;
            if (c6 > 1764) {
                break;
            }
            setCharAdvance(c6, 0);
            c5 = (char) (c6 + 1);
        }
        char c7 = 1767;
        while (true) {
            char c8 = c7;
            if (c8 > 1768) {
                break;
            }
            setCharAdvance(c8, 0);
            c7 = (char) (c8 + 1);
        }
        char c9 = 1770;
        while (true) {
            char c10 = c9;
            if (c10 <= 1773) {
                setCharAdvance(c10, 0);
                c9 = (char) (c10 + 1);
            } else {
                return;
            }
        }
    }

    public void addSubsetRange(int[] range) {
        if (this.subsetRanges == null) {
            this.subsetRanges = new ArrayList();
        }
        this.subsetRanges.add(range);
    }

    public int getCompressionLevel() {
        return this.compressionLevel;
    }

    public void setCompressionLevel(int compressionLevel) {
        if (compressionLevel < 0 || compressionLevel > 9) {
            this.compressionLevel = -1;
        } else {
            this.compressionLevel = compressionLevel;
        }
    }
}
