package com.aowagie.text.pdf;

import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Utilities;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/FontDetails.class */
class FontDetails {
    private final PdfIndirectReference indirectReference;
    private final PdfName fontName;
    private final BaseFont baseFont;
    private TrueTypeFontUnicode ttu;
    private CJKFont cjkFont;
    private byte[] shortTag;
    private HashMap longTag;
    private IntHashtable cjkTag;
    private final int fontType;
    private boolean symbolic;
    private boolean subset = true;

    FontDetails(PdfName fontName, PdfIndirectReference indirectReference, BaseFont baseFont) {
        this.fontName = fontName;
        this.indirectReference = indirectReference;
        this.baseFont = baseFont;
        this.fontType = baseFont.getFontType();
        switch (this.fontType) {
            case 0:
            case 1:
                this.shortTag = new byte[256];
                break;
            case 2:
                this.cjkTag = new IntHashtable();
                this.cjkFont = (CJKFont) baseFont;
                break;
            case 3:
                this.longTag = new HashMap();
                this.ttu = (TrueTypeFontUnicode) baseFont;
                this.symbolic = baseFont.isFontSpecific();
                break;
        }
    }

    PdfIndirectReference getIndirectReference() {
        return this.indirectReference;
    }

    PdfName getFontName() {
        return this.fontName;
    }

    BaseFont getBaseFont() {
        return this.baseFont;
    }

    byte[] convertToBytes(String text) {
        int val;
        byte[] b = null;
        switch (this.fontType) {
            case 0:
            case 1:
                b = this.baseFont.convertToBytes(text);
                for (byte b2 : b) {
                    this.shortTag[b2 & 255] = 1;
                }
                break;
            case 2:
                int len = text.length();
                for (int k = 0; k < len; k++) {
                    this.cjkTag.put(this.cjkFont.getCidCode(text.charAt(k)), 0);
                }
                b = this.baseFont.convertToBytes(text);
                break;
            case 3:
                try {
                    int len2 = text.length();
                    char[] glyph = new char[len2];
                    int i = 0;
                    if (this.symbolic) {
                        byte[] b3 = PdfEncodings.convertToBytes(text, "symboltt");
                        int len3 = b3.length;
                        for (int k2 = 0; k2 < len3; k2++) {
                            int[] metrics = this.ttu.getMetricsTT(b3[k2] & 255);
                            if (metrics != null) {
                                this.longTag.put(Integer.valueOf(metrics[0]), new int[]{metrics[0], metrics[1], this.ttu.getUnicodeDifferences(b3[k2] & 255)});
                                int i2 = i;
                                i++;
                                glyph[i2] = (char) metrics[0];
                            }
                        }
                    } else {
                        int k3 = 0;
                        while (k3 < len2) {
                            if (Utilities.isSurrogatePair(text, k3)) {
                                val = Utilities.convertToUtf32(text, k3);
                                k3++;
                            } else {
                                val = text.charAt(k3);
                            }
                            int[] metrics2 = this.ttu.getMetricsTT(val);
                            if (metrics2 != null) {
                                int m0 = metrics2[0];
                                Integer gl = Integer.valueOf(m0);
                                if (!this.longTag.containsKey(gl)) {
                                    this.longTag.put(gl, new int[]{m0, metrics2[1], val});
                                }
                                int i3 = i;
                                i++;
                                glyph[i3] = (char) m0;
                            }
                            k3++;
                        }
                    }
                    String s = new String(glyph, 0, i);
                    b = s.getBytes("UnicodeBigUnmarked");
                } catch (UnsupportedEncodingException e) {
                    throw new ExceptionConverter(e);
                }
                break;
            case 4:
                b = this.baseFont.convertToBytes(text);
                break;
            case 5:
                return this.baseFont.convertToBytes(text);
        }
        return b;
    }

    void writeFont(PdfWriter writer) {
        try {
            switch (this.fontType) {
                case 0:
                case 1:
                    int firstChar = 0;
                    while (firstChar < 256 && this.shortTag[firstChar] == 0) {
                        firstChar++;
                    }
                    int lastChar = 255;
                    while (lastChar >= firstChar && this.shortTag[lastChar] == 0) {
                        lastChar--;
                    }
                    if (firstChar > 255) {
                        firstChar = 255;
                        lastChar = 255;
                    }
                    this.baseFont.writeFont(writer, this.indirectReference, new Object[]{Integer.valueOf(firstChar), Integer.valueOf(lastChar), this.shortTag, Boolean.valueOf(this.subset)});
                    break;
                case 2:
                    this.baseFont.writeFont(writer, this.indirectReference, new Object[]{this.cjkTag});
                    break;
                case 3:
                    this.baseFont.writeFont(writer, this.indirectReference, new Object[]{this.longTag, Boolean.valueOf(this.subset)});
                    break;
                case 5:
                    this.baseFont.writeFont(writer, this.indirectReference, null);
                    break;
            }
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public boolean isSubset() {
        return this.subset;
    }

    public void setSubset(boolean subset) {
        this.subset = subset;
    }
}
