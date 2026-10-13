package com.aowagie.text.pdf;

import com.aowagie.text.DocumentException;
import com.aowagie.text.Utilities;
import com.ctc.wstx.api.InvalidCharHandler;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/TrueTypeFontUnicode.class */
class TrueTypeFontUnicode extends TrueTypeFont implements Comparator {
    private boolean vertical;
    private static final byte[] rotbits = {-128, 64, 32, 16, 8, 4, 2, 1};

    TrueTypeFontUnicode(String ttFile, String enc, boolean emb, byte[] ttfAfm, boolean forceRead) throws IOException, DocumentException {
        this.vertical = false;
        String nameBase = getBaseName(ttFile);
        String ttcName = getTTCName(nameBase);
        if (nameBase.length() < ttFile.length()) {
            this.style = ttFile.substring(nameBase.length());
        }
        this.encoding = enc;
        this.embedded = emb;
        this.fileName = ttcName;
        this.ttcIndex = "";
        if (ttcName.length() < nameBase.length()) {
            this.ttcIndex = nameBase.substring(ttcName.length() + 1);
        }
        this.fontType = 3;
        if ((this.fileName.toLowerCase().endsWith(".ttf") || this.fileName.toLowerCase().endsWith(".otf") || this.fileName.toLowerCase().endsWith(".ttc")) && ((enc.equals(BaseFont.IDENTITY_H) || enc.equals(BaseFont.IDENTITY_V)) && emb)) {
            process(ttfAfm, forceRead);
            if (this.os_2.fsType == 2) {
                throw new DocumentException(this.fileName + this.style + " cannot be embedded due to licensing restrictions.");
            }
            if ((this.cmap31 == null && !this.fontSpecific) || (this.cmap10 == null && this.fontSpecific)) {
                this.directTextToByte = true;
            }
            if (this.fontSpecific) {
                this.fontSpecific = false;
                String tempEncoding = this.encoding;
                this.encoding = "";
                createEncoding();
                this.encoding = tempEncoding;
                this.fontSpecific = true;
            }
            this.vertical = enc.endsWith("V");
            return;
        }
        throw new DocumentException(this.fileName + " " + this.style + " is not a TTF font file.");
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public int getWidth(int char1) {
        if (this.vertical) {
            return 1000;
        }
        if (this.fontSpecific) {
            if ((char1 & 65280) == 0 || (char1 & 65280) == 61440) {
                return getRawWidth(char1 & 255, null);
            }
            return 0;
        }
        return getRawWidth(char1, this.encoding);
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public int getWidth(String text) {
        if (this.vertical) {
            return text.length() * 1000;
        }
        int total = 0;
        if (this.fontSpecific) {
            char[] cc = text.toCharArray();
            for (char c : cc) {
                if ((c & 65280) == 0 || (c & 65280) == 61440) {
                    total += getRawWidth(c & 255, null);
                }
            }
        } else {
            int len = text.length();
            int k = 0;
            while (k < len) {
                if (Utilities.isSurrogatePair(text, k)) {
                    total += getRawWidth(Utilities.convertToUtf32(text, k), this.encoding);
                    k++;
                } else {
                    total += getRawWidth(text.charAt(k), this.encoding);
                }
                k++;
            }
        }
        return total;
    }

    private PdfStream getToUnicode(Object[] metrics) {
        if (metrics.length == 0) {
            return null;
        }
        StringBuffer buf = new StringBuffer("/CIDInit /ProcSet findresource begin\n12 dict begin\nbegincmap\n/CIDSystemInfo\n<< /Registry (TTX+0)\n/Ordering (T42UV)\n/Supplement 0\n>> def\n/CMapName /TTX+0 def\n/CMapType 2 def\n1 begincodespacerange\n<0000><FFFF>\nendcodespacerange\n");
        int size = 0;
        for (int k = 0; k < metrics.length; k++) {
            if (size == 0) {
                if (k != 0) {
                    buf.append("endbfrange\n");
                }
                size = Math.min(100, metrics.length - k);
                buf.append(size).append(" beginbfrange\n");
            }
            size--;
            int[] metric = (int[]) metrics[k];
            String fromTo = toHex(metric[0]);
            buf.append(fromTo).append(fromTo).append(toHex(metric[2])).append('\n');
        }
        buf.append("endbfrange\nendcmap\nCMapName currentdict /CMap defineresource pop\nend end\n");
        String s = buf.toString();
        PdfStream stream = new PdfStream(PdfEncodings.convertToBytes(s, (String) null));
        stream.flateCompress(this.compressionLevel);
        return stream;
    }

    private static String toHex4(int n) {
        String s = "0000" + Integer.toHexString(n);
        return s.substring(s.length() - 4);
    }

    private static String toHex(int n) {
        if (n < 65536) {
            return "<" + toHex4(n) + ">";
        }
        int n2 = n - 65536;
        int high = (n2 / 1024) + InvalidCharHandler.FailingHandler.SURR1_FIRST;
        int low = (n2 % 1024) + InvalidCharHandler.FailingHandler.SURR2_FIRST;
        return "[<" + toHex4(high) + toHex4(low) + ">]";
    }

    private PdfDictionary getCIDFontType2(PdfIndirectReference fontDescriptor, String subsetPrefix, Object[] metrics) {
        PdfDictionary dic = new PdfDictionary(PdfName.FONT);
        if (this.cff) {
            dic.put(PdfName.SUBTYPE, PdfName.CIDFONTTYPE0);
            dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName + "-" + this.encoding));
        } else {
            dic.put(PdfName.SUBTYPE, PdfName.CIDFONTTYPE2);
            dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName));
        }
        dic.put(PdfName.FONTDESCRIPTOR, fontDescriptor);
        if (!this.cff) {
            dic.put(PdfName.CIDTOGIDMAP, PdfName.IDENTITY);
        }
        PdfDictionary cdic = new PdfDictionary();
        cdic.put(PdfName.REGISTRY, new PdfString("Adobe"));
        cdic.put(PdfName.ORDERING, new PdfString("Identity"));
        cdic.put(PdfName.SUPPLEMENT, new PdfNumber(0));
        dic.put(PdfName.CIDSYSTEMINFO, cdic);
        if (!this.vertical) {
            dic.put(PdfName.DW, new PdfNumber(1000));
            StringBuffer buf = new StringBuffer("[");
            int lastNumber = -10;
            boolean firstTime = true;
            for (Object metric2 : metrics) {
                int[] metric = (int[]) metric2;
                if (metric[1] != 1000) {
                    int m = metric[0];
                    if (m == lastNumber + 1) {
                        buf.append(' ').append(metric[1]);
                    } else {
                        if (!firstTime) {
                            buf.append(']');
                        }
                        firstTime = false;
                        buf.append(m).append('[').append(metric[1]);
                    }
                    lastNumber = m;
                }
            }
            if (buf.length() > 1) {
                buf.append("]]");
                dic.put(PdfName.W, new PdfLiteral(buf.toString()));
            }
        }
        return dic;
    }

    private PdfDictionary getFontBaseType(PdfIndirectReference descendant, String subsetPrefix, PdfIndirectReference toUnicode) {
        PdfDictionary dic = new PdfDictionary(PdfName.FONT);
        dic.put(PdfName.SUBTYPE, PdfName.TYPE0);
        if (this.cff) {
            dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName + "-" + this.encoding));
        } else {
            dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName));
        }
        dic.put(PdfName.ENCODING, new PdfName(this.encoding));
        dic.put(PdfName.DESCENDANTFONTS, new PdfArray(descendant));
        if (toUnicode != null) {
            dic.put(PdfName.TOUNICODE, toUnicode);
        }
        return dic;
    }

    @Override // java.util.Comparator
    public int compare(Object o1, Object o2) {
        int m1 = ((int[]) o1)[0];
        int m2 = ((int[]) o2)[0];
        if (m1 < m2) {
            return -1;
        }
        if (m1 == m2) {
            return 0;
        }
        return 1;
    }

    @Override // com.aowagie.text.pdf.TrueTypeFont, com.aowagie.text.pdf.BaseFont
    void writeFont(PdfWriter writer, PdfIndirectReference ref, Object[] params) throws IOException, DocumentException {
        PdfStream stream;
        byte[] b;
        PdfIndirectReference ind_font;
        HashMap longTag = (HashMap) params[0];
        addRangeUni(longTag, true, this.subset);
        Object[] metrics = longTag.values().toArray();
        Arrays.sort(metrics, this);
        PdfIndirectReference cidset = null;
        if (writer.getPDFXConformance() == 3 || writer.getPDFXConformance() == 4 || writer.getPDFXConformance() == 5 || writer.getPDFXConformance() == 6) {
            if (metrics.length == 0) {
                stream = new PdfStream(new byte[]{-128});
            } else {
                int top = ((int[]) metrics[metrics.length - 1])[0];
                byte[] bt = new byte[(top / 8) + 1];
                for (Object metric : metrics) {
                    int v = ((int[]) metric)[0];
                    int i = v / 8;
                    bt[i] = (byte) (bt[i] | rotbits[v % 8]);
                }
                stream = new PdfStream(bt);
                stream.flateCompress(this.compressionLevel);
            }
            cidset = writer.addToBody(stream).getIndirectReference();
        }
        if (this.cff) {
            byte[] b2 = readCffFont();
            if (this.subset || this.subsetRanges != null) {
                CFFFontSubset cff = new CFFFontSubset(new RandomAccessFileOrArray(b2), longTag);
                b2 = cff.Process(cff.getNames()[0]);
            }
            PdfObject pobj = new BaseFont.StreamFont(b2, "CIDFontType0C", this.compressionLevel);
            PdfIndirectObject obj = writer.addToBody(pobj);
            ind_font = obj.getIndirectReference();
        } else {
            if (this.subset || this.directoryOffset != 0) {
                TrueTypeFontSubSet sb = new TrueTypeFontSubSet(this.fileName, new RandomAccessFileOrArray(this.rf), longTag, this.directoryOffset, false, false);
                b = sb.process();
            } else {
                b = getFullFont();
            }
            int[] lengths = {b.length};
            PdfObject pobj2 = new BaseFont.StreamFont(b, lengths, this.compressionLevel);
            PdfIndirectObject obj2 = writer.addToBody(pobj2);
            ind_font = obj2.getIndirectReference();
        }
        String subsetPrefix = "";
        if (this.subset) {
            subsetPrefix = createSubsetPrefix();
        }
        PdfDictionary dic = getFontDescriptor(ind_font, subsetPrefix, cidset);
        PdfIndirectObject obj3 = writer.addToBody(dic);
        PdfIndirectReference ind_font2 = obj3.getIndirectReference();
        PdfObject pobj3 = getCIDFontType2(ind_font2, subsetPrefix, metrics);
        PdfIndirectObject obj4 = writer.addToBody(pobj3);
        PdfIndirectReference ind_font3 = obj4.getIndirectReference();
        PdfObject pobj4 = getToUnicode(metrics);
        PdfIndirectReference toUnicodeRef = null;
        if (pobj4 != null) {
            PdfIndirectObject obj5 = writer.addToBody(pobj4);
            toUnicodeRef = obj5.getIndirectReference();
        }
        PdfObject pobj5 = getFontBaseType(ind_font3, subsetPrefix, toUnicodeRef);
        writer.addToBody(pobj5, ref);
    }

    @Override // com.aowagie.text.pdf.TrueTypeFont, com.aowagie.text.pdf.BaseFont
    public PdfStream getFullFontStream() throws IOException, DocumentException {
        if (this.cff) {
            return new BaseFont.StreamFont(readCffFont(), "CIDFontType0C", this.compressionLevel);
        }
        return super.getFullFontStream();
    }

    @Override // com.aowagie.text.pdf.BaseFont
    byte[] convertToBytes(String text) {
        return null;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    byte[] convertToBytes(int char1) {
        return null;
    }

    @Override // com.aowagie.text.pdf.TrueTypeFont
    public int[] getMetricsTT(int c) {
        HashMap map;
        if (this.cmapExt != null) {
            return (int[]) this.cmapExt.get(Integer.valueOf(c));
        }
        if (this.fontSpecific) {
            map = this.cmap10;
        } else {
            map = this.cmap31;
        }
        if (map == null) {
            return null;
        }
        if (this.fontSpecific) {
            if ((c & (-256)) == 0 || (c & (-256)) == 61440) {
                return (int[]) map.get(Integer.valueOf(c & 255));
            }
            return null;
        }
        return (int[]) map.get(Integer.valueOf(c));
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public boolean charExists(int c) {
        return getMetricsTT(c) != null;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public boolean setCharAdvance(int c, int advance) {
        int[] m = getMetricsTT(c);
        if (m == null) {
            return false;
        }
        m[1] = advance;
        return true;
    }

    @Override // com.aowagie.text.pdf.BaseFont
    public int[] getCharBBox(int c) {
        int[] m;
        if (this.bboxes == null || (m = getMetricsTT(c)) == null) {
            return null;
        }
        return this.bboxes[m[0]];
    }
}
