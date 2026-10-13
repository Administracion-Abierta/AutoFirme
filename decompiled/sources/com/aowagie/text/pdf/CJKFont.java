package com.aowagie.text.pdf;

import com.aowagie.text.DocumentException;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.fontbox.afm.AFMParser;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CJKFont.class */
class CJKFont extends BaseFont {
    static final String CJK_ENCODING = "UnicodeBigUnmarked";
    private static final int FIRST = 0;
    private static final int BRACKET = 1;
    private static final int SERIAL = 2;
    private static final int V1Y = 880;
    private static Properties cjkFonts = new Properties();
    private static Properties cjkEncodings = new Properties();
    private static Hashtable allCMaps = new Hashtable();
    private static Hashtable allFonts = new Hashtable();
    private static boolean propertiesLoaded = false;
    private String fontName;
    private String style;
    private final String CMap;
    private boolean cidDirect;
    private char[] translationMap;
    private final IntHashtable vMetrics;
    private final IntHashtable hMetrics;
    private HashMap fontDesc;
    private boolean vertical;

    private static void loadProperties() {
        if (propertiesLoaded) {
            return;
        }
        synchronized (allFonts) {
            if (propertiesLoaded) {
                return;
            }
            try {
                InputStream is = getResourceStream("com/lowagie/text/pdf/fonts/cjkfonts.properties");
                cjkFonts.load(is);
                is.close();
                InputStream is2 = getResourceStream("com/lowagie/text/pdf/fonts/cjkencodings.properties");
                cjkEncodings.load(is2);
                is2.close();
            } catch (Exception e) {
                cjkFonts = new Properties();
                cjkEncodings = new Properties();
            }
            propertiesLoaded = true;
        }
    }

    CJKFont(String fontName, String enc, boolean emb) throws DocumentException {
        this.style = "";
        this.cidDirect = false;
        this.vertical = false;
        loadProperties();
        this.fontType = 2;
        String nameBase = getBaseName(fontName);
        if (!isCJKFont(nameBase, enc)) {
            throw new DocumentException("Font '" + fontName + "' with '" + enc + "' encoding is not a CJK font.");
        }
        if (nameBase.length() < fontName.length()) {
            this.style = fontName.substring(nameBase.length());
            fontName = nameBase;
        }
        this.fontName = fontName;
        this.encoding = CJK_ENCODING;
        this.vertical = enc.endsWith("V");
        this.CMap = enc;
        if (enc.startsWith("Identity-")) {
            this.cidDirect = true;
            String s = cjkFonts.getProperty(fontName);
            String s2 = s.substring(0, s.indexOf(95));
            char[] c = (char[]) allCMaps.get(s2);
            if (c == null) {
                c = readCMap(s2);
                if (c == null) {
                    throw new DocumentException("The cmap " + s2 + " does not exist as a resource.");
                }
                c[32767] = '\n';
                allCMaps.put(s2, c);
            }
            this.translationMap = c;
        } else {
            char[] c2 = (char[]) allCMaps.get(enc);
            if (c2 == null) {
                String s3 = cjkEncodings.getProperty(enc);
                if (s3 == null) {
                    throw new DocumentException("The resource cjkencodings.properties does not contain the encoding " + enc);
                }
                StringTokenizer tk = new StringTokenizer(s3);
                String nt = tk.nextToken();
                c2 = (char[]) allCMaps.get(nt);
                if (c2 == null) {
                    c2 = readCMap(nt);
                    allCMaps.put(nt, c2);
                }
                if (tk.hasMoreTokens()) {
                    String nt2 = tk.nextToken();
                    char[] m2 = readCMap(nt2);
                    for (int k = 0; k < 65536; k++) {
                        if (m2[k] == 0) {
                            m2[k] = c2[k];
                        }
                    }
                    allCMaps.put(enc, m2);
                    c2 = m2;
                }
            }
            this.translationMap = c2;
        }
        this.fontDesc = (HashMap) allFonts.get(fontName);
        if (this.fontDesc == null) {
            this.fontDesc = readFontProperties(fontName);
            allFonts.put(fontName, this.fontDesc);
        }
        this.hMetrics = (IntHashtable) this.fontDesc.get("W");
        this.vMetrics = (IntHashtable) this.fontDesc.get("W2");
    }

    public static boolean isCJKFont(String fontName, String enc) {
        loadProperties();
        String encodings = cjkFonts.getProperty(fontName);
        return encodings != null && (enc.equals(BaseFont.IDENTITY_H) || enc.equals(BaseFont.IDENTITY_V) || encodings.indexOf(new StringBuilder().append("_").append(enc).append("_").toString()) >= 0);
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public int getWidth(int char1) {
        int v;
        int c = char1;
        if (!this.cidDirect) {
            c = this.translationMap[c];
        }
        if (this.vertical) {
            v = this.vMetrics.get(c);
        } else {
            v = this.hMetrics.get(c);
        }
        if (v > 0) {
            return v;
        }
        return 1000;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public int getWidth(String text) {
        int v;
        int total = 0;
        for (int k = 0; k < text.length(); k++) {
            int c = text.charAt(k);
            if (!this.cidDirect) {
                c = this.translationMap[c];
            }
            if (this.vertical) {
                v = this.vMetrics.get(c);
            } else {
                v = this.hMetrics.get(c);
            }
            if (v > 0) {
                total += v;
            } else {
                total += 1000;
            }
        }
        return total;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    int getRawWidth(int c, String name) {
        return 0;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public int getKerning(int char1, int char2) {
        return 0;
    }

    private PdfDictionary getFontDescriptor() {
        PdfDictionary dic = new PdfDictionary(PdfName.FONTDESCRIPTOR);
        dic.put(PdfName.ASCENT, new PdfLiteral((String) this.fontDesc.get("Ascent")));
        dic.put(PdfName.CAPHEIGHT, new PdfLiteral((String) this.fontDesc.get(AFMParser.CAP_HEIGHT)));
        dic.put(PdfName.DESCENT, new PdfLiteral((String) this.fontDesc.get("Descent")));
        dic.put(PdfName.FLAGS, new PdfLiteral((String) this.fontDesc.get("Flags")));
        dic.put(PdfName.FONTBBOX, new PdfLiteral((String) this.fontDesc.get(AFMParser.FONT_BBOX)));
        dic.put(PdfName.FONTNAME, new PdfName(this.fontName + this.style));
        dic.put(PdfName.ITALICANGLE, new PdfLiteral((String) this.fontDesc.get(AFMParser.ITALIC_ANGLE)));
        dic.put(PdfName.STEMV, new PdfLiteral((String) this.fontDesc.get("StemV")));
        PdfDictionary pdic = new PdfDictionary();
        pdic.put(PdfName.PANOSE, new PdfString((String) this.fontDesc.get("Panose"), null));
        dic.put(PdfName.STYLE, pdic);
        return dic;
    }

    private PdfDictionary getCIDFont(PdfIndirectReference fontDescriptor, IntHashtable cjkTag) {
        PdfDictionary dic = new PdfDictionary(PdfName.FONT);
        dic.put(PdfName.SUBTYPE, PdfName.CIDFONTTYPE0);
        dic.put(PdfName.BASEFONT, new PdfName(this.fontName + this.style));
        dic.put(PdfName.FONTDESCRIPTOR, fontDescriptor);
        int[] keys = cjkTag.toOrderedKeys();
        String w = convertToHCIDMetrics(keys, this.hMetrics);
        if (w != null) {
            dic.put(PdfName.W, new PdfLiteral(w));
        }
        if (this.vertical) {
            String w2 = convertToVCIDMetrics(keys, this.vMetrics, this.hMetrics);
            if (w2 != null) {
                dic.put(PdfName.W2, new PdfLiteral(w2));
            }
        } else {
            dic.put(PdfName.DW, new PdfNumber(1000));
        }
        PdfDictionary cdic = new PdfDictionary();
        cdic.put(PdfName.REGISTRY, new PdfString((String) this.fontDesc.get("Registry"), null));
        cdic.put(PdfName.ORDERING, new PdfString((String) this.fontDesc.get("Ordering"), null));
        cdic.put(PdfName.SUPPLEMENT, new PdfLiteral((String) this.fontDesc.get("Supplement")));
        dic.put(PdfName.CIDSYSTEMINFO, cdic);
        return dic;
    }

    private PdfDictionary getFontBaseType(PdfIndirectReference CIDFont) {
        PdfDictionary dic = new PdfDictionary(PdfName.FONT);
        dic.put(PdfName.SUBTYPE, PdfName.TYPE0);
        String name = this.fontName;
        if (this.style.length() > 0) {
            name = name + "-" + this.style.substring(1);
        }
        dic.put(PdfName.BASEFONT, new PdfName(name + "-" + this.CMap));
        dic.put(PdfName.ENCODING, new PdfName(this.CMap));
        dic.put(PdfName.DESCENDANTFONTS, new PdfArray(CIDFont));
        return dic;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    void writeFont(PdfWriter writer, PdfIndirectReference ref, Object[] params) throws IOException, DocumentException {
        IntHashtable cjkTag = (IntHashtable) params[0];
        PdfIndirectReference ind_font = null;
        PdfObject pobj = getFontDescriptor();
        if (pobj != null) {
            PdfIndirectObject obj = writer.addToBody(pobj);
            ind_font = obj.getIndirectReference();
        }
        PdfObject pobj2 = getCIDFont(ind_font, cjkTag);
        if (pobj2 != null) {
            PdfIndirectObject obj2 = writer.addToBody(pobj2);
            ind_font = obj2.getIndirectReference();
        }
        writer.addToBody(getFontBaseType(ind_font), ref);
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public PdfStream getFullFontStream() {
        return null;
    }

    private float getDescNumber(String name) {
        return Integer.parseInt((String) this.fontDesc.get(name));
    }

    private float getBBox(int idx) {
        String s = (String) this.fontDesc.get(AFMParser.FONT_BBOX);
        StringTokenizer tk = new StringTokenizer(s, " []\r\n\t\f");
        String ret = tk.nextToken();
        for (int k = 0; k < idx; k++) {
            ret = tk.nextToken();
        }
        return Integer.parseInt(ret);
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public float getFontDescriptor(int key, float fontSize) {
        switch (key) {
            case 1:
            case 9:
                return (getDescNumber("Ascent") * fontSize) / 1000.0f;
            case 2:
                return (getDescNumber(AFMParser.CAP_HEIGHT) * fontSize) / 1000.0f;
            case 3:
            case 10:
                return (getDescNumber("Descent") * fontSize) / 1000.0f;
            case 4:
                return getDescNumber(AFMParser.ITALIC_ANGLE);
            case 5:
                return (fontSize * getBBox(0)) / 1000.0f;
            case 6:
                return (fontSize * getBBox(1)) / 1000.0f;
            case 7:
                return (fontSize * getBBox(2)) / 1000.0f;
            case 8:
                return (fontSize * getBBox(3)) / 1000.0f;
            case 11:
                return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            case 12:
                return (fontSize * (getBBox(2) - getBBox(0))) / 1000.0f;
            default:
                return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public String getPostscriptFontName() {
        return this.fontName;
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
    public String[][] getFamilyFontName() {
        return getFullFontName();
    }

    private static char[] readCMap(String name) {
        try {
            InputStream is = getResourceStream(BaseFont.RESOURCE_PATH + (name + ".cmap"));
            char[] c = new char[65536];
            for (int k = 0; k < 65536; k++) {
                c[k] = (char) ((is.read() << 8) + is.read());
            }
            is.close();
            return c;
        } catch (Exception e) {
            return null;
        }
    }

    private static IntHashtable createMetric(String s) {
        IntHashtable h = new IntHashtable();
        StringTokenizer tk = new StringTokenizer(s);
        while (tk.hasMoreTokens()) {
            int n1 = Integer.parseInt(tk.nextToken());
            h.put(n1, Integer.parseInt(tk.nextToken()));
        }
        return h;
    }

    private static String convertToHCIDMetrics(int[] keys, IntHashtable h) {
        if (keys.length == 0) {
            return null;
        }
        int lastCid = 0;
        int lastValue = 0;
        int start = 0;
        while (start < keys.length) {
            lastCid = keys[start];
            lastValue = h.get(lastCid);
            if (lastValue == 0) {
                start++;
            } else {
                start++;
                break;
            }
        }
        if (lastValue == 0) {
            return null;
        }
        StringBuffer buf = new StringBuffer();
        buf.append('[');
        buf.append(lastCid);
        int state = 0;
        for (int k = start; k < keys.length; k++) {
            int cid = keys[k];
            int value = h.get(cid);
            if (value != 0) {
                switch (state) {
                    case 0:
                        if (cid == lastCid + 1 && value == lastValue) {
                            state = 2;
                        } else if (cid == lastCid + 1) {
                            state = 1;
                            buf.append('[').append(lastValue);
                        } else {
                            buf.append('[').append(lastValue).append(']').append(cid);
                        }
                        break;
                    case 1:
                        if (cid == lastCid + 1 && value == lastValue) {
                            state = 2;
                            buf.append(']').append(lastCid);
                        } else if (cid == lastCid + 1) {
                            buf.append(' ').append(lastValue);
                        } else {
                            state = 0;
                            buf.append(' ').append(lastValue).append(']').append(cid);
                        }
                        break;
                    case 2:
                        if (cid != lastCid + 1 || value != lastValue) {
                            buf.append(' ').append(lastCid).append(' ').append(lastValue).append(' ').append(cid);
                            state = 0;
                        }
                        break;
                }
                lastValue = value;
                lastCid = cid;
            }
        }
        switch (state) {
            case 0:
                buf.append('[').append(lastValue).append("]]");
                break;
            case 1:
                buf.append(' ').append(lastValue).append("]]");
                break;
            case 2:
                buf.append(' ').append(lastCid).append(' ').append(lastValue).append(']');
                break;
        }
        return buf.toString();
    }

    private static String convertToVCIDMetrics(int[] keys, IntHashtable v, IntHashtable h) {
        if (keys.length == 0) {
            return null;
        }
        int lastCid = 0;
        int lastValue = 0;
        int lastHValue = 0;
        int start = 0;
        while (start < keys.length) {
            lastCid = keys[start];
            lastValue = v.get(lastCid);
            if (lastValue != 0) {
                start++;
                break;
            }
            lastHValue = h.get(lastCid);
            start++;
        }
        if (lastValue == 0) {
            return null;
        }
        if (lastHValue == 0) {
            lastHValue = 1000;
        }
        StringBuffer buf = new StringBuffer();
        buf.append('[');
        buf.append(lastCid);
        int state = 0;
        for (int k = start; k < keys.length; k++) {
            int cid = keys[k];
            int value = v.get(cid);
            if (value != 0) {
                int hValue = h.get(lastCid);
                if (hValue == 0) {
                    hValue = 1000;
                }
                switch (state) {
                    case 0:
                        if (cid == lastCid + 1 && value == lastValue && hValue == lastHValue) {
                            state = 2;
                        } else {
                            buf.append(' ').append(lastCid).append(' ').append(-lastValue).append(' ').append(lastHValue / 2).append(' ').append(V1Y).append(' ').append(cid);
                        }
                        break;
                    case 2:
                        if (cid != lastCid + 1 || value != lastValue || hValue != lastHValue) {
                            buf.append(' ').append(lastCid).append(' ').append(-lastValue).append(' ').append(lastHValue / 2).append(' ').append(V1Y).append(' ').append(cid);
                            state = 0;
                        }
                        break;
                }
                lastValue = value;
                lastCid = cid;
                lastHValue = hValue;
            }
        }
        buf.append(' ').append(lastCid).append(' ').append(-lastValue).append(' ').append(lastHValue / 2).append(' ').append(V1Y).append(" ]");
        return buf.toString();
    }

    private static HashMap readFontProperties(String name) {
        try {
            InputStream is = getResourceStream(BaseFont.RESOURCE_PATH + (name + ".properties"));
            Properties p = new Properties();
            p.load(is);
            is.close();
            IntHashtable W = createMetric(p.getProperty("W"));
            p.remove("W");
            IntHashtable W2 = createMetric(p.getProperty("W2"));
            p.remove("W2");
            HashMap map = new HashMap();
            Enumeration e = p.keys();
            while (e.hasMoreElements()) {
                Object obj = e.nextElement();
                map.put(obj, p.getProperty((String) obj));
            }
            map.put("W", W);
            map.put("W2", W2);
            return map;
        } catch (Exception e2) {
            return null;
        }
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public int getUnicodeEquivalent(int c) {
        if (this.cidDirect) {
            return this.translationMap[c];
        }
        return c;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public int getCidCode(int c) {
        if (this.cidDirect) {
            return c;
        }
        return this.translationMap[c];
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public boolean hasKernPairs() {
        return false;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public boolean charExists(int c) {
        return this.translationMap[c] != 0;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public boolean setCharAdvance(int c, int advance) {
        return false;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public void setPostscriptFontName(String name) {
        this.fontName = name;
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
}
