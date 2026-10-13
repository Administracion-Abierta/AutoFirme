package com.aowagie.text.pdf;

import com.aowagie.text.Document;
import com.aowagie.text.DocumentException;
import com.aowagie.text.pdf.fonts.FontsResourceAnchor;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import es.gob.afirma.keystores.filters.PolicyIdFilter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.StringTokenizer;
import org.apache.fontbox.afm.AFMParser;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/Type1Font.class */
class Type1Font extends BaseFont {
    private static FontsResourceAnchor resourceAnchor;
    private byte[] pfb;
    private String FontName;
    private String FullName;
    private String FamilyName;
    private String CharacterSet;
    private String EncodingScheme;
    private int StdHW;
    private final String fileName;
    private boolean builtinFont;
    private static final int[] PFB_TYPES = {1, 2, 1};
    private String Weight = "";
    private float ItalicAngle = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    private boolean IsFixedPitch = false;
    private int llx = -50;
    private int lly = -200;
    private int urx = 1000;
    private int ury = 900;
    private int UnderlinePosition = -100;
    private int UnderlineThickness = 50;
    private int CapHeight = 700;
    private int XHeight = 480;
    private int Ascender = 800;
    private int Descender = -200;
    private int StdVW = 80;
    private final HashMap CharMetrics = new HashMap();
    private final HashMap KernPairs = new HashMap();

    Type1Font(String afmFile, String enc, boolean emb, byte[] ttfAfm, byte[] pfb, boolean forceRead) throws IOException, DocumentException {
        RandomAccessFileOrArray rf;
        this.EncodingScheme = "FontSpecific";
        this.builtinFont = false;
        if (emb && ttfAfm != null && pfb == null) {
            throw new DocumentException("Two byte arrays are needed if the Type1 font is embedded.");
        }
        if (emb && ttfAfm != null) {
            this.pfb = pfb;
        }
        this.encoding = enc;
        this.embedded = emb;
        this.fileName = afmFile;
        this.fontType = 0;
        RandomAccessFileOrArray rf2 = null;
        InputStream is = null;
        if (BuiltinFonts14.containsKey(afmFile)) {
            this.embedded = false;
            this.builtinFont = true;
            byte[] buf = new byte[1024];
            try {
                if (resourceAnchor == null) {
                    resourceAnchor = new FontsResourceAnchor();
                }
                InputStream is2 = getResourceStream(BaseFont.RESOURCE_PATH + afmFile + ".afm", resourceAnchor.getClass().getClassLoader());
                if (is2 == null) {
                    String msg = afmFile + " not found as resource. (The *.afm files must exist as resources in the package com.aowagie.text.pdf.fonts)";
                    System.err.println(msg);
                    throw new DocumentException(msg);
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                while (true) {
                    int size = is2.read(buf);
                    if (size < 0) {
                        break;
                    } else {
                        out.write(buf, 0, size);
                    }
                }
                byte[] buf2 = out.toByteArray();
                if (is2 != null) {
                    try {
                        is2.close();
                    } catch (Exception e) {
                    }
                }
                try {
                    rf2 = new RandomAccessFileOrArray(buf2);
                    process(rf2);
                    if (rf2 != null) {
                        try {
                            rf2.close();
                        } catch (Exception e2) {
                        }
                    }
                } catch (Throwable th) {
                    if (rf2 != null) {
                        try {
                            rf2.close();
                        } catch (Exception e3) {
                        }
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                if (0 != 0) {
                    try {
                        is.close();
                    } catch (Exception e4) {
                    }
                }
                throw th2;
            }
        } else if (afmFile.toLowerCase().endsWith(".afm")) {
            try {
                if (ttfAfm == null) {
                    rf2 = new RandomAccessFileOrArray(afmFile, forceRead, Document.plainRandomAccess);
                } else {
                    rf2 = new RandomAccessFileOrArray(ttfAfm);
                }
                process(rf2);
                if (rf2 != null) {
                    try {
                        rf2.close();
                    } catch (Exception e5) {
                    }
                }
            } catch (Throwable th3) {
                if (rf2 != null) {
                    try {
                        rf2.close();
                    } catch (Exception e6) {
                    }
                }
                throw th3;
            }
        } else if (afmFile.toLowerCase().endsWith(".pfm")) {
            try {
                ByteArrayOutputStream ba = new ByteArrayOutputStream();
                if (ttfAfm == null) {
                    rf = new RandomAccessFileOrArray(afmFile, forceRead, Document.plainRandomAccess);
                } else {
                    rf = new RandomAccessFileOrArray(ttfAfm);
                }
                Pfm2afm.convert(rf, ba);
                rf.close();
                rf2 = new RandomAccessFileOrArray(ba.toByteArray());
                process(rf2);
                if (rf2 != null) {
                    try {
                        rf2.close();
                    } catch (Exception e7) {
                    }
                }
            } catch (Throwable th4) {
                if (rf2 != null) {
                    try {
                        rf2.close();
                    } catch (Exception e8) {
                    }
                }
                throw th4;
            }
        } else {
            throw new DocumentException(afmFile + " is not an AFM or PFM font file.");
        }
        this.EncodingScheme = this.EncodingScheme.trim();
        if (this.EncodingScheme.equals("AdobeStandardEncoding") || this.EncodingScheme.equals("StandardEncoding")) {
            this.fontSpecific = false;
        }
        if (!this.encoding.startsWith("#")) {
            PdfEncodings.convertToBytes(" ", enc);
        }
        createEncoding();
    }

    @Override // com.aowagie.text.pdf.BaseFont
    int getRawWidth(int c, String name) {
        Object[] metrics;
        if (name == null) {
            metrics = (Object[]) this.CharMetrics.get(Integer.valueOf(c));
        } else {
            if (name.equals(BaseFont.notdef)) {
                return 0;
            }
            metrics = (Object[]) this.CharMetrics.get(name);
        }
        if (metrics != null) {
            return ((Integer) metrics[1]).intValue();
        }
        return 0;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public int getKerning(int char1, int char2) {
        String second;
        Object[] obj;
        String first = GlyphList.unicodeToName(char1);
        if (first == null || (second = GlyphList.unicodeToName(char2)) == null || (obj = (Object[]) this.KernPairs.get(first)) == null) {
            return 0;
        }
        for (int k = 0; k < obj.length; k += 2) {
            if (second.equals(obj[k])) {
                return ((Integer) obj[k + 1]).intValue();
            }
        }
        return 0;
    }

    private void process(RandomAccessFileOrArray rf) throws IOException, DocumentException {
        Object[] space;
        boolean isMetrics = false;
        while (true) {
            String line = rf.readLine();
            if (line == null) {
                break;
            }
            StringTokenizer tok = new StringTokenizer(line, " ,\n\r\t\f");
            if (tok.hasMoreTokens()) {
                String ident = tok.nextToken();
                if (ident.equals(AFMParser.FONT_NAME)) {
                    this.FontName = tok.nextToken("ÿ").substring(1);
                } else if (ident.equals(AFMParser.FULL_NAME)) {
                    this.FullName = tok.nextToken("ÿ").substring(1);
                } else if (ident.equals(AFMParser.FAMILY_NAME)) {
                    this.FamilyName = tok.nextToken("ÿ").substring(1);
                } else if (ident.equals(AFMParser.WEIGHT)) {
                    this.Weight = tok.nextToken("ÿ").substring(1);
                } else if (ident.equals(AFMParser.ITALIC_ANGLE)) {
                    this.ItalicAngle = Float.parseFloat(tok.nextToken());
                } else if (ident.equals(AFMParser.IS_FIXED_PITCH)) {
                    this.IsFixedPitch = tok.nextToken().equals("true");
                } else if (ident.equals(AFMParser.CHARACTER_SET)) {
                    this.CharacterSet = tok.nextToken("ÿ").substring(1);
                } else if (ident.equals(AFMParser.FONT_BBOX)) {
                    this.llx = (int) Float.parseFloat(tok.nextToken());
                    this.lly = (int) Float.parseFloat(tok.nextToken());
                    this.urx = (int) Float.parseFloat(tok.nextToken());
                    this.ury = (int) Float.parseFloat(tok.nextToken());
                } else if (ident.equals(AFMParser.UNDERLINE_POSITION)) {
                    this.UnderlinePosition = (int) Float.parseFloat(tok.nextToken());
                } else if (ident.equals(AFMParser.UNDERLINE_THICKNESS)) {
                    this.UnderlineThickness = (int) Float.parseFloat(tok.nextToken());
                } else if (ident.equals(AFMParser.ENCODING_SCHEME)) {
                    this.EncodingScheme = tok.nextToken("ÿ").substring(1);
                } else if (ident.equals(AFMParser.CAP_HEIGHT)) {
                    this.CapHeight = (int) Float.parseFloat(tok.nextToken());
                } else if (ident.equals(AFMParser.X_HEIGHT)) {
                    this.XHeight = (int) Float.parseFloat(tok.nextToken());
                } else if (ident.equals(AFMParser.ASCENDER)) {
                    this.Ascender = (int) Float.parseFloat(tok.nextToken());
                } else if (ident.equals(AFMParser.DESCENDER)) {
                    this.Descender = (int) Float.parseFloat(tok.nextToken());
                } else if (ident.equals(AFMParser.STD_HW)) {
                    this.StdHW = (int) Float.parseFloat(tok.nextToken());
                } else if (ident.equals(AFMParser.STD_VW)) {
                    this.StdVW = (int) Float.parseFloat(tok.nextToken());
                } else if (ident.equals(AFMParser.START_CHAR_METRICS)) {
                    isMetrics = true;
                    break;
                }
            }
        }
        if (!isMetrics) {
            throw new DocumentException("Missing StartCharMetrics in " + this.fileName);
        }
        while (true) {
            String line2 = rf.readLine();
            if (line2 == null) {
                break;
            }
            StringTokenizer tok2 = new StringTokenizer(line2);
            if (tok2.hasMoreTokens()) {
                if (tok2.nextToken().equals(AFMParser.END_CHAR_METRICS)) {
                    isMetrics = false;
                    break;
                }
                Integer C = -1;
                Integer WX = 250;
                String N = "";
                int[] B = null;
                StringTokenizer tok3 = new StringTokenizer(line2, PolicyIdFilter.OID_SEPARATOR);
                while (tok3.hasMoreTokens()) {
                    StringTokenizer tokc = new StringTokenizer(tok3.nextToken());
                    if (tokc.hasMoreTokens()) {
                        String ident2 = tokc.nextToken();
                        if (ident2.equals("C")) {
                            C = Integer.valueOf(tokc.nextToken());
                        } else if (ident2.equals(AFMParser.CHARMETRICS_WX)) {
                            WX = Integer.valueOf((int) Float.parseFloat(tokc.nextToken()));
                        } else if (ident2.equals("N")) {
                            N = tokc.nextToken();
                        } else if (ident2.equals("B")) {
                            B = new int[]{Integer.parseInt(tokc.nextToken()), Integer.parseInt(tokc.nextToken()), Integer.parseInt(tokc.nextToken()), Integer.parseInt(tokc.nextToken())};
                        }
                    }
                }
                Object[] metrics = {C, WX, N, B};
                if (C.intValue() >= 0) {
                    this.CharMetrics.put(C, metrics);
                }
                this.CharMetrics.put(N, metrics);
            }
        }
        if (isMetrics) {
            throw new DocumentException("Missing EndCharMetrics in " + this.fileName);
        }
        if (!this.CharMetrics.containsKey("nonbreakingspace") && (space = (Object[]) this.CharMetrics.get("space")) != null) {
            this.CharMetrics.put("nonbreakingspace", space);
        }
        while (true) {
            String line3 = rf.readLine();
            if (line3 == null) {
                break;
            }
            StringTokenizer tok4 = new StringTokenizer(line3);
            if (tok4.hasMoreTokens()) {
                String ident3 = tok4.nextToken();
                if (ident3.equals(AFMParser.END_FONT_METRICS)) {
                    return;
                }
                if (ident3.equals(AFMParser.START_KERN_PAIRS)) {
                    isMetrics = true;
                    break;
                }
            }
        }
        if (!isMetrics) {
            throw new DocumentException("Missing EndFontMetrics in " + this.fileName);
        }
        while (true) {
            String line4 = rf.readLine();
            if (line4 == null) {
                break;
            }
            StringTokenizer tok5 = new StringTokenizer(line4);
            if (tok5.hasMoreTokens()) {
                String ident4 = tok5.nextToken();
                if (ident4.equals(AFMParser.KERN_PAIR_KPX)) {
                    String first = tok5.nextToken();
                    String second = tok5.nextToken();
                    Integer width = Integer.valueOf((int) Float.parseFloat(tok5.nextToken()));
                    Object[] relates = (Object[]) this.KernPairs.get(first);
                    if (relates == null) {
                        this.KernPairs.put(first, new Object[]{second, width});
                    } else {
                        int n = relates.length;
                        Object[] relates2 = new Object[n + 2];
                        System.arraycopy(relates, 0, relates2, 0, n);
                        relates2[n] = second;
                        relates2[n + 1] = width;
                        this.KernPairs.put(first, relates2);
                    }
                } else if (ident4.equals(AFMParser.END_KERN_PAIRS)) {
                    isMetrics = false;
                    break;
                }
            }
        }
        if (isMetrics) {
            throw new DocumentException("Missing EndKernPairs in " + this.fileName);
        }
        rf.close();
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public PdfStream getFullFontStream() throws DocumentException {
        RandomAccessFileOrArray rf;
        if (this.builtinFont || !this.embedded) {
            return null;
        }
        RandomAccessFileOrArray rf2 = null;
        try {
            try {
                String filePfb = this.fileName.substring(0, this.fileName.length() - 3) + "pfb";
                if (this.pfb == null) {
                    rf = new RandomAccessFileOrArray(filePfb, true, Document.plainRandomAccess);
                } else {
                    rf = new RandomAccessFileOrArray(this.pfb);
                }
                int fileLength = rf.length();
                byte[] st = new byte[fileLength - 18];
                int[] lengths = new int[3];
                int bytePtr = 0;
                for (int k = 0; k < 3; k++) {
                    if (rf.read() != 128) {
                        throw new DocumentException("Start marker missing in " + filePfb);
                    }
                    if (rf.read() != PFB_TYPES[k]) {
                        throw new DocumentException("Incorrect segment type in " + filePfb);
                    }
                    int size = rf.read() + (rf.read() << 8) + (rf.read() << 16) + (rf.read() << 24);
                    lengths[k] = size;
                    while (size != 0) {
                        int got = rf.read(st, bytePtr, size);
                        if (got < 0) {
                            throw new DocumentException("Premature end in " + filePfb);
                        }
                        bytePtr += got;
                        size -= got;
                    }
                }
                BaseFont.StreamFont streamFont = new BaseFont.StreamFont(st, lengths, this.compressionLevel);
                if (rf != null) {
                    try {
                        rf.close();
                    } catch (Exception e) {
                    }
                }
                return streamFont;
            } catch (Exception e2) {
                throw new DocumentException(e2);
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    rf2.close();
                } catch (Exception e3) {
                }
            }
            throw th;
        }
    }

    private PdfDictionary getFontDescriptor(PdfIndirectReference fontStream) {
        if (this.builtinFont) {
            return null;
        }
        PdfDictionary dic = new PdfDictionary(PdfName.FONTDESCRIPTOR);
        dic.put(PdfName.ASCENT, new PdfNumber(this.Ascender));
        dic.put(PdfName.CAPHEIGHT, new PdfNumber(this.CapHeight));
        dic.put(PdfName.DESCENT, new PdfNumber(this.Descender));
        dic.put(PdfName.FONTBBOX, new PdfRectangle(this.llx, this.lly, this.urx, this.ury));
        dic.put(PdfName.FONTNAME, new PdfName(this.FontName));
        dic.put(PdfName.ITALICANGLE, new PdfNumber(this.ItalicAngle));
        dic.put(PdfName.STEMV, new PdfNumber(this.StdVW));
        if (fontStream != null) {
            dic.put(PdfName.FONTFILE, fontStream);
        }
        int flags = 0;
        if (this.IsFixedPitch) {
            flags = 0 | 1;
        }
        int flags2 = flags | (this.fontSpecific ? 4 : 32);
        if (this.ItalicAngle < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            flags2 |= 64;
        }
        if (this.FontName.indexOf("Caps") >= 0 || this.FontName.endsWith("SC")) {
            flags2 |= 131072;
        }
        if (this.Weight.equals("Bold")) {
            flags2 |= 262144;
        }
        dic.put(PdfName.FLAGS, new PdfNumber(flags2));
        return dic;
    }

    private PdfDictionary getFontBaseType(PdfIndirectReference fontDescriptor, int firstChar, int lastChar, byte[] shortTag) {
        PdfDictionary pdfDictionary = new PdfDictionary(PdfName.FONT);
        pdfDictionary.put(PdfName.SUBTYPE, PdfName.TYPE1);
        pdfDictionary.put(PdfName.BASEFONT, new PdfName(this.FontName));
        boolean stdEncoding = this.encoding.equals("Cp1252") || this.encoding.equals(BaseFont.MACROMAN);
        if (!this.fontSpecific || this.specialMap != null) {
            for (int k = firstChar; k <= lastChar; k++) {
                if (!this.differences[k].equals(BaseFont.notdef)) {
                    firstChar = k;
                    break;
                }
            }
            if (stdEncoding) {
                pdfDictionary.put(PdfName.ENCODING, this.encoding.equals("Cp1252") ? PdfName.WIN_ANSI_ENCODING : PdfName.MAC_ROMAN_ENCODING);
            } else {
                PdfDictionary enc = new PdfDictionary(PdfName.ENCODING);
                PdfArray dif = new PdfArray();
                boolean gap = true;
                for (int k2 = firstChar; k2 <= lastChar; k2++) {
                    if (shortTag[k2] != 0) {
                        if (gap) {
                            dif.add(new PdfNumber(k2));
                            gap = false;
                        }
                        dif.add(new PdfName(this.differences[k2]));
                    } else {
                        gap = true;
                    }
                }
                enc.put(PdfName.DIFFERENCES, dif);
                pdfDictionary.put(PdfName.ENCODING, enc);
            }
        }
        if (this.specialMap != null || this.forceWidthsOutput || !this.builtinFont || (!this.fontSpecific && !stdEncoding)) {
            pdfDictionary.put(PdfName.FIRSTCHAR, new PdfNumber(firstChar));
            pdfDictionary.put(PdfName.LASTCHAR, new PdfNumber(lastChar));
            PdfArray wd = new PdfArray();
            for (int k3 = firstChar; k3 <= lastChar; k3++) {
                if (shortTag[k3] == 0) {
                    wd.add(new PdfNumber(0));
                } else {
                    wd.add(new PdfNumber(this.widths[k3]));
                }
            }
            pdfDictionary.put(PdfName.WIDTHS, wd);
        }
        if (!this.builtinFont && fontDescriptor != null) {
            pdfDictionary.put(PdfName.FONTDESCRIPTOR, fontDescriptor);
        }
        return pdfDictionary;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    void writeFont(PdfWriter writer, PdfIndirectReference ref, Object[] params) throws IOException, DocumentException {
        int firstChar = ((Integer) params[0]).intValue();
        int lastChar = ((Integer) params[1]).intValue();
        byte[] shortTag = (byte[]) params[2];
        boolean subsetp = ((Boolean) params[3]).booleanValue() && this.subset;
        if (!subsetp) {
            firstChar = 0;
            lastChar = shortTag.length - 1;
            for (int k = 0; k < shortTag.length; k++) {
                shortTag[k] = 1;
            }
        }
        PdfIndirectReference ind_font = null;
        PdfObject pobj = getFullFontStream();
        if (pobj != null) {
            PdfIndirectObject obj = writer.addToBody(pobj);
            ind_font = obj.getIndirectReference();
        }
        PdfObject pobj2 = getFontDescriptor(ind_font);
        if (pobj2 != null) {
            PdfIndirectObject obj2 = writer.addToBody(pobj2);
            ind_font = obj2.getIndirectReference();
        }
        writer.addToBody(getFontBaseType(ind_font, firstChar, lastChar, shortTag), ref);
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public float getFontDescriptor(int key, float fontSize) {
        switch (key) {
            case 1:
            case 9:
                return (this.Ascender * fontSize) / 1000.0f;
            case 2:
                return (this.CapHeight * fontSize) / 1000.0f;
            case 3:
            case 10:
                return (this.Descender * fontSize) / 1000.0f;
            case 4:
                return this.ItalicAngle;
            case 5:
                return (this.llx * fontSize) / 1000.0f;
            case 6:
                return (this.lly * fontSize) / 1000.0f;
            case 7:
                return (this.urx * fontSize) / 1000.0f;
            case 8:
                return (this.ury * fontSize) / 1000.0f;
            case 11:
                return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            case 12:
                return ((this.urx - this.llx) * fontSize) / 1000.0f;
            case 13:
                return (this.UnderlinePosition * fontSize) / 1000.0f;
            case 14:
                return (this.UnderlineThickness * fontSize) / 1000.0f;
            default:
                return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public String getPostscriptFontName() {
        return this.FontName;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public String[][] getFullFontName() {
        return new String[][]{new String[]{"", "", "", this.FullName}};
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public String[][] getAllNameEntries() {
        return new String[][]{new String[]{TlbConst.TYPELIB_MINOR_VERSION_WORD, "", "", "", this.FullName}};
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public String[][] getFamilyFontName() {
        return new String[][]{new String[]{"", "", "", this.FamilyName}};
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public boolean hasKernPairs() {
        return !this.KernPairs.isEmpty();
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public void setPostscriptFontName(String name) {
        this.FontName = name;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public boolean setKerning(int char1, int char2, int kern) {
        String second;
        String first = GlyphList.unicodeToName(char1);
        if (first == null || (second = GlyphList.unicodeToName(char2)) == null) {
            return false;
        }
        Object[] obj = (Object[]) this.KernPairs.get(first);
        if (obj == null) {
            this.KernPairs.put(first, new Object[]{second, Integer.valueOf(kern)});
            return true;
        }
        for (int k = 0; k < obj.length; k += 2) {
            if (second.equals(obj[k])) {
                obj[k + 1] = Integer.valueOf(kern);
                return true;
            }
        }
        int size = obj.length;
        Object[] obj2 = new Object[size + 2];
        System.arraycopy(obj, 0, obj2, 0, size);
        obj2[size] = second;
        obj2[size + 1] = Integer.valueOf(kern);
        this.KernPairs.put(first, obj2);
        return true;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    protected int[] getRawCharBBox(int c, String name) {
        Object[] metrics;
        if (name == null) {
            metrics = (Object[]) this.CharMetrics.get(Integer.valueOf(c));
        } else {
            if (name.equals(BaseFont.notdef)) {
                return null;
            }
            metrics = (Object[]) this.CharMetrics.get(name);
        }
        if (metrics != null) {
            return (int[]) metrics[3];
        }
        return null;
    }
}
