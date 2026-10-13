package com.aowagie.text.pdf;

import com.aowagie.text.DocumentException;
import com.aowagie.text.ExceptionConverter;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import com.sun.jna.platform.win32.WinError;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.io.IOException;
import java.util.HashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/DocumentFont.class */
public class DocumentFont extends BaseFont {
    private String fontName;
    private PRIndirectReference refFont;
    private PdfDictionary font;
    private IntHashtable diffmap;
    private boolean isType0;
    private BaseFont cjkMirror;
    private static String[] cjkNames = {"HeiseiMin-W3", "HeiseiKakuGo-W5", "STSong-Light", "MHei-Medium", "MSung-Light", "HYGoThic-Medium", "HYSMyeongJo-Medium", "MSungStd-Light", "STSongStd-Light", "HYSMyeongJoStd-Medium", "KozMinPro-Regular"};
    private static String[] cjkEncs = {"UniJIS-UCS2-H", "UniJIS-UCS2-H", "UniGB-UCS2-H", "UniCNS-UCS2-H", "UniCNS-UCS2-H", "UniKS-UCS2-H", "UniKS-UCS2-H", "UniCNS-UCS2-H", "UniGB-UCS2-H", "UniKS-UCS2-H", "UniJIS-UCS2-H"};
    private static String[] cjkNames2 = {"MSungStd-Light", "STSongStd-Light", "HYSMyeongJoStd-Medium", "KozMinPro-Regular"};
    private static String[] cjkEncs2 = {"UniCNS-UCS2-H", "UniGB-UCS2-H", "UniKS-UCS2-H", "UniJIS-UCS2-H", "UniCNS-UTF16-H", "UniGB-UTF16-H", "UniKS-UTF16-H", "UniJIS-UTF16-H"};
    private static final int[] stdEnc = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 33, 34, 35, 36, 37, 38, WinError.ERROR_DS_GC_NOT_AVAILABLE, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, WinError.ERROR_DS_CROSS_DOM_MOVE_ERROR, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 161, 162, 163, WinError.ERROR_DS_REFERRAL_LIMIT_EXCEEDED, 165, WinError.ERROR_PROCESS_MODE_ALREADY_BACKGROUND, 167, 164, 39, WinError.ERROR_POLICY_ONLY_IN_DS, 171, WinError.ERROR_DS_AFFECTS_MULTIPLE_DSAS, WinError.ERROR_DS_SERVER_DOWN, 64257, 64258, 0, WinError.ERROR_DS_RIDMGR_INIT_ERROR, WinError.ERROR_DS_OPERATIONS_ERROR, WinError.ERROR_DS_PROTOCOL_ERROR, 183, 0, 182, 8226, WinError.ERROR_SHARED_POLICY, WinError.ERROR_NO_PROMOTION_ACTIVE, WinError.ERROR_PROMOTION_ACTIVE, 187, WinError.ERROR_DS_COMPARE_TRUE, WinError.ERROR_DS_NO_SUCH_OBJECT, 0, 191, 0, 96, 180, WinError.ERROR_EVENT_DONE, WinError.ERROR_WAIT_2, 175, WinError.ERROR_FIRMWARE_UPDATED, WinError.ERROR_DRIVERS_LEAKING_LOCKED_PAGES, 168, 0, WinError.ERROR_WAKE_SYSTEM, 184, 0, WinError.ERROR_WAIT_3, WinError.ERROR_WAIT_1, 711, WinError.ERROR_DS_OBJ_CLASS_VIOLATION, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 198, 0, 170, 0, 0, 0, 0, 321, WinError.ERROR_EXE_MACHINE_TYPE_MISMATCH, TIFF.TAG_EXTRA_SAMPLES, 186, 0, 0, 0, 0, 0, WinError.ERROR_BAD_PIPE, 0, 0, 0, TIFF.TAG_SOFTWARE, 0, 0, TIFF.TAG_TILE_WIDTH, 248, TIFF.TAG_SAMPLE_FORMAT, WinError.ERROR_FILE_TOO_LARGE, 0, 0, 0, 0};
    private final HashMap metrics = new HashMap();
    private final IntHashtable uni2byte = new IntHashtable();
    private float Ascender = 800.0f;
    private float CapHeight = 700.0f;
    private float Descender = -200.0f;
    private float ItalicAngle = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    private float llx = -50.0f;
    private float lly = -200.0f;
    private float urx = 100.0f;
    private float ury = 900.0f;

    DocumentFont(PRIndirectReference refFont) {
        this.isType0 = false;
        this.encoding = "";
        this.fontSpecific = false;
        this.refFont = refFont;
        this.fontType = 4;
        this.font = (PdfDictionary) PdfReader.getPdfObject(refFont);
        this.fontName = PdfName.decodeName(this.font.getAsName(PdfName.BASEFONT).toString());
        PdfName subType = this.font.getAsName(PdfName.SUBTYPE);
        if (PdfName.TYPE1.equals(subType) || PdfName.TRUETYPE.equals(subType)) {
            doType1TT();
            return;
        }
        for (int k = 0; k < cjkNames.length; k++) {
            if (this.fontName.startsWith(cjkNames[k])) {
                this.fontName = cjkNames[k];
                try {
                    this.cjkMirror = BaseFont.createFont(this.fontName, cjkEncs[k], false);
                    return;
                } catch (Exception e) {
                    throw new ExceptionConverter(e);
                }
            }
        }
        String enc = PdfName.decodeName(this.font.getAsName(PdfName.ENCODING).toString());
        int k2 = 0;
        while (k2 < cjkEncs2.length) {
            if (!enc.startsWith(cjkEncs2[k2])) {
                k2++;
            } else {
                if (k2 > 3) {
                    try {
                        k2 -= 4;
                    } catch (Exception e2) {
                        throw new ExceptionConverter(e2);
                    }
                }
                this.cjkMirror = BaseFont.createFont(cjkNames2[k2], cjkEncs2[k2], false);
                return;
            }
        }
        if (PdfName.TYPE0.equals(subType) && enc.equals(BaseFont.IDENTITY_H)) {
            processType0(this.font);
            this.isType0 = true;
        }
    }

    private void processType0(PdfDictionary font) {
        try {
            PdfObject toUniObject = PdfReader.getPdfObjectRelease(font.get(PdfName.TOUNICODE));
            PdfArray df = (PdfArray) PdfReader.getPdfObjectRelease(font.get(PdfName.DESCENDANTFONTS));
            PdfDictionary cidft = (PdfDictionary) PdfReader.getPdfObjectRelease(df.getPdfObject(0));
            PdfNumber dwo = (PdfNumber) PdfReader.getPdfObjectRelease(cidft.get(PdfName.DW));
            int dw = 1000;
            if (dwo != null) {
                dw = dwo.intValue();
            }
            IntHashtable widths = readWidths((PdfArray) PdfReader.getPdfObjectRelease(cidft.get(PdfName.W)));
            PdfDictionary fontDesc = (PdfDictionary) PdfReader.getPdfObjectRelease(cidft.get(PdfName.FONTDESCRIPTOR));
            fillFontDesc(fontDesc);
            if (toUniObject != null) {
                fillMetrics(PdfReader.getStreamBytes((PRStream) toUniObject), widths, dw);
            }
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    private IntHashtable readWidths(PdfArray ws) {
        IntHashtable hh = new IntHashtable();
        if (ws == null) {
            return hh;
        }
        int k = 0;
        while (k < ws.size()) {
            int c1 = ((PdfNumber) PdfReader.getPdfObjectRelease(ws.getPdfObject(k))).intValue();
            int k2 = k + 1;
            PdfObject obj = PdfReader.getPdfObjectRelease(ws.getPdfObject(k2));
            if (obj.isArray()) {
                PdfArray a2 = (PdfArray) obj;
                for (int j = 0; j < a2.size(); j++) {
                    int c2 = ((PdfNumber) PdfReader.getPdfObjectRelease(a2.getPdfObject(j))).intValue();
                    int i = c1;
                    c1++;
                    hh.put(i, c2);
                }
            } else {
                int c3 = ((PdfNumber) obj).intValue();
                k2++;
                int w = ((PdfNumber) PdfReader.getPdfObjectRelease(ws.getPdfObject(k2))).intValue();
                while (c1 <= c3) {
                    hh.put(c1, w);
                    c1++;
                }
            }
            k = k2 + 1;
        }
        return hh;
    }

    private String decodeString(PdfString ps) {
        if (ps.isHexWriting()) {
            return PdfEncodings.convertToString(ps.getBytes(), "UnicodeBigUnmarked");
        }
        return ps.toUnicodeString();
    }

    private void fillMetrics(byte[] touni, IntHashtable widths, int dw) {
        try {
            PdfContentParser ps = new PdfContentParser(new PRTokeniser(touni));
            PdfObject last = null;
            while (true) {
                PdfObject ob = ps.readPRObject();
                if (ob != null) {
                    if (ob.type() == 200) {
                        if (ob.toString().equals("beginbfchar")) {
                            int n = ((PdfNumber) last).intValue();
                            for (int k = 0; k < n; k++) {
                                String cid = decodeString((PdfString) ps.readPRObject());
                                String uni = decodeString((PdfString) ps.readPRObject());
                                if (uni.length() == 1) {
                                    int cidc = cid.charAt(0);
                                    int unic = uni.charAt(uni.length() - 1);
                                    int w = dw;
                                    if (widths.containsKey(cidc)) {
                                        w = widths.get(cidc);
                                    }
                                    this.metrics.put(Integer.valueOf(unic), new int[]{cidc, w});
                                }
                            }
                        } else if (ob.toString().equals("beginbfrange")) {
                            int n2 = ((PdfNumber) last).intValue();
                            for (int k2 = 0; k2 < n2; k2++) {
                                String cid1 = decodeString((PdfString) ps.readPRObject());
                                String cid2 = decodeString((PdfString) ps.readPRObject());
                                int cid1c = cid1.charAt(0);
                                int cid2c = cid2.charAt(0);
                                PdfObject ob2 = ps.readPRObject();
                                if (ob2.isString()) {
                                    String uni2 = decodeString((PdfString) ob2);
                                    if (uni2.length() == 1) {
                                        int unic2 = uni2.charAt(uni2.length() - 1);
                                        while (cid1c <= cid2c) {
                                            int w2 = dw;
                                            if (widths.containsKey(cid1c)) {
                                                w2 = widths.get(cid1c);
                                            }
                                            this.metrics.put(Integer.valueOf(unic2), new int[]{cid1c, w2});
                                            cid1c++;
                                            unic2++;
                                        }
                                    }
                                } else {
                                    PdfArray a = (PdfArray) ob2;
                                    int j = 0;
                                    while (j < a.size()) {
                                        String uni3 = decodeString(a.getAsString(j));
                                        if (uni3.length() == 1) {
                                            int unic3 = uni3.charAt(uni3.length() - 1);
                                            int w3 = dw;
                                            if (widths.containsKey(cid1c)) {
                                                w3 = widths.get(cid1c);
                                            }
                                            this.metrics.put(Integer.valueOf(unic3), new int[]{cid1c, w3});
                                        }
                                        j++;
                                        cid1c++;
                                    }
                                }
                            }
                        }
                    } else {
                        last = ob;
                    }
                } else {
                    return;
                }
            }
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    private void doType1TT() {
        PdfObject enc = PdfReader.getPdfObject(this.font.get(PdfName.ENCODING));
        if (enc == null) {
            fillEncoding(null);
        } else if (enc.isName()) {
            fillEncoding((PdfName) enc);
        } else {
            PdfDictionary encDic = (PdfDictionary) enc;
            PdfObject enc2 = PdfReader.getPdfObject(encDic.get(PdfName.BASEENCODING));
            if (enc2 == null) {
                fillEncoding(null);
            } else {
                fillEncoding((PdfName) enc2);
            }
            PdfArray diffs = encDic.getAsArray(PdfName.DIFFERENCES);
            if (diffs != null) {
                this.diffmap = new IntHashtable();
                int currentNumber = 0;
                for (int k = 0; k < diffs.size(); k++) {
                    PdfObject obj = diffs.getPdfObject(k);
                    if (obj.isNumber()) {
                        currentNumber = ((PdfNumber) obj).intValue();
                    } else {
                        int[] c = GlyphList.nameToUnicode(PdfName.decodeName(((PdfName) obj).toString()));
                        if (c != null && c.length > 0) {
                            this.uni2byte.put(c[0], currentNumber);
                            this.diffmap.put(c[0], currentNumber);
                        }
                        currentNumber++;
                    }
                }
            }
        }
        PdfArray newWidths = this.font.getAsArray(PdfName.WIDTHS);
        PdfNumber first = this.font.getAsNumber(PdfName.FIRSTCHAR);
        PdfNumber last = this.font.getAsNumber(PdfName.LASTCHAR);
        if (BuiltinFonts14.containsKey(this.fontName)) {
            try {
                BaseFont bf = BaseFont.createFont(this.fontName, "Cp1252", false);
                int[] e = this.uni2byte.toOrderedKeys();
                for (int k2 = 0; k2 < e.length; k2++) {
                    int n = this.uni2byte.get(e[k2]);
                    this.widths[n] = bf.getRawWidth(n, GlyphList.unicodeToName(e[k2]));
                }
                if (this.diffmap != null) {
                    int[] e2 = this.diffmap.toOrderedKeys();
                    for (int k3 = 0; k3 < e2.length; k3++) {
                        int n2 = this.diffmap.get(e2[k3]);
                        this.widths[n2] = bf.getRawWidth(n2, GlyphList.unicodeToName(e2[k3]));
                    }
                    this.diffmap = null;
                }
                this.Ascender = bf.getFontDescriptor(1, 1000.0f);
                this.CapHeight = bf.getFontDescriptor(2, 1000.0f);
                this.Descender = bf.getFontDescriptor(3, 1000.0f);
                this.ItalicAngle = bf.getFontDescriptor(4, 1000.0f);
                this.llx = bf.getFontDescriptor(5, 1000.0f);
                this.lly = bf.getFontDescriptor(6, 1000.0f);
                this.urx = bf.getFontDescriptor(7, 1000.0f);
                this.ury = bf.getFontDescriptor(8, 1000.0f);
            } catch (Exception e3) {
                throw new ExceptionConverter(e3);
            }
        }
        if (first != null && last != null && newWidths != null) {
            int f = first.intValue();
            for (int k4 = 0; k4 < newWidths.size(); k4++) {
                this.widths[f + k4] = newWidths.getAsNumber(k4).intValue();
            }
        }
        fillFontDesc(this.font.getAsDict(PdfName.FONTDESCRIPTOR));
    }

    private void fillFontDesc(PdfDictionary fontDesc) {
        if (fontDesc == null) {
            return;
        }
        PdfNumber v = fontDesc.getAsNumber(PdfName.ASCENT);
        if (v != null) {
            this.Ascender = v.floatValue();
        }
        PdfNumber v2 = fontDesc.getAsNumber(PdfName.CAPHEIGHT);
        if (v2 != null) {
            this.CapHeight = v2.floatValue();
        }
        PdfNumber v3 = fontDesc.getAsNumber(PdfName.DESCENT);
        if (v3 != null) {
            this.Descender = v3.floatValue();
        }
        PdfNumber v4 = fontDesc.getAsNumber(PdfName.ITALICANGLE);
        if (v4 != null) {
            this.ItalicAngle = v4.floatValue();
        }
        PdfArray bbox = fontDesc.getAsArray(PdfName.FONTBBOX);
        if (bbox != null) {
            this.llx = bbox.getAsNumber(0).floatValue();
            this.lly = bbox.getAsNumber(1).floatValue();
            this.urx = bbox.getAsNumber(2).floatValue();
            this.ury = bbox.getAsNumber(3).floatValue();
            if (this.llx > this.urx) {
                float t = this.llx;
                this.llx = this.urx;
                this.urx = t;
            }
            if (this.lly > this.ury) {
                float t2 = this.lly;
                this.lly = this.ury;
                this.ury = t2;
            }
        }
    }

    private void fillEncoding(PdfName encoding) {
        if (PdfName.MAC_ROMAN_ENCODING.equals(encoding) || PdfName.WIN_ANSI_ENCODING.equals(encoding)) {
            byte[] b = new byte[256];
            for (int k = 0; k < 256; k++) {
                b[k] = (byte) k;
            }
            String enc = "Cp1252";
            if (PdfName.MAC_ROMAN_ENCODING.equals(encoding)) {
                enc = BaseFont.MACROMAN;
            }
            String cv = PdfEncodings.convertToString(b, enc);
            char[] arr = cv.toCharArray();
            for (int k2 = 0; k2 < 256; k2++) {
                this.uni2byte.put(arr[k2], k2);
            }
            return;
        }
        for (int k3 = 0; k3 < 256; k3++) {
            this.uni2byte.put(stdEnc[k3], k3);
        }
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public String[][] getFamilyFontName() {
        return getFullFontName();
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public float getFontDescriptor(int key, float fontSize) {
        if (this.cjkMirror != null) {
            return this.cjkMirror.getFontDescriptor(key, fontSize);
        }
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
            default:
                return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public String[][] getFullFontName() {
        return new String[][]{new String[]{"", "", "", this.fontName}};
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public String[][] getAllNameEntries() {
        return new String[][]{new String[]{TlbConst.TYPELIB_MINOR_VERSION_WORD, "", "", "", this.fontName}};
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public int getKerning(int char1, int char2) {
        return 0;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public String getPostscriptFontName() {
        return this.fontName;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    int getRawWidth(int c, String name) {
        return 0;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public boolean hasKernPairs() {
        return false;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    void writeFont(PdfWriter writer, PdfIndirectReference ref, Object[] params) throws IOException, DocumentException {
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public PdfStream getFullFontStream() {
        return null;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public int getWidth(int char1) {
        if (this.cjkMirror != null) {
            return this.cjkMirror.getWidth(char1);
        }
        if (this.isType0) {
            int[] ws = (int[]) this.metrics.get(Integer.valueOf(char1));
            if (ws != null) {
                return ws[1];
            }
            return 0;
        }
        return super.getWidth(char1);
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public int getWidth(String text) {
        if (this.cjkMirror != null) {
            return this.cjkMirror.getWidth(text);
        }
        if (this.isType0) {
            char[] chars = text.toCharArray();
            int total = 0;
            for (char c : chars) {
                int[] ws = (int[]) this.metrics.get(Integer.valueOf(c));
                if (ws != null) {
                    total += ws[1];
                }
            }
            return total;
        }
        return super.getWidth(text);
    }

    @Override // com.aowagie.text.pdf.BaseFont
    byte[] convertToBytes(String text) {
        if (this.cjkMirror != null) {
            return PdfEncodings.convertToBytes(text, "UnicodeBigUnmarked");
        }
        if (this.isType0) {
            char[] chars = text.toCharArray();
            int len = chars.length;
            byte[] b = new byte[len * 2];
            int bptr = 0;
            for (char c : chars) {
                int[] ws = (int[]) this.metrics.get(Integer.valueOf(c));
                if (ws != null) {
                    int g = ws[0];
                    int i = bptr;
                    int bptr2 = bptr + 1;
                    b[i] = (byte) (g / 256);
                    bptr = bptr2 + 1;
                    b[bptr2] = (byte) g;
                }
            }
            if (bptr == b.length) {
                return b;
            }
            byte[] nb = new byte[bptr];
            System.arraycopy(b, 0, nb, 0, bptr);
            return nb;
        }
        char[] cc = text.toCharArray();
        byte[] b2 = new byte[cc.length];
        int ptr = 0;
        for (int k = 0; k < cc.length; k++) {
            if (this.uni2byte.containsKey(cc[k])) {
                int i2 = ptr;
                ptr++;
                b2[i2] = (byte) this.uni2byte.get(cc[k]);
            }
        }
        if (ptr == b2.length) {
            return b2;
        }
        byte[] b3 = new byte[ptr];
        System.arraycopy(b2, 0, b3, 0, ptr);
        return b3;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    byte[] convertToBytes(int char1) {
        if (this.cjkMirror != null) {
            return PdfEncodings.convertToBytes((char) char1, "UnicodeBigUnmarked");
        }
        if (this.isType0) {
            int[] ws = (int[]) this.metrics.get(Integer.valueOf(char1));
            if (ws != null) {
                int g = ws[0];
                return new byte[]{(byte) (g / 256), (byte) g};
            }
            return new byte[0];
        }
        if (this.uni2byte.containsKey(char1)) {
            return new byte[]{(byte) this.uni2byte.get(char1)};
        }
        return new byte[0];
    }

    PdfIndirectReference getIndirectReference() {
        return this.refFont;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public boolean charExists(int c) {
        if (this.cjkMirror != null) {
            return this.cjkMirror.charExists(c);
        }
        if (this.isType0) {
            return this.metrics.containsKey(Integer.valueOf(c));
        }
        return super.charExists(c);
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public void setPostscriptFontName(String name) {
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public boolean setKerning(int char1, int char2, int kern) {
        return false;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public int[] getCharBBox(int c) {
        return null;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    protected int[] getRawCharBBox(int c, String name) {
        return null;
    }

    IntHashtable getUni2Byte() {
        return this.uni2byte;
    }
}
