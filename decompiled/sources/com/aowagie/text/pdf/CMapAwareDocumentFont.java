package com.aowagie.text.pdf;

import com.aowagie.text.pdf.fonts.cmaps.CMap;
import com.aowagie.text.pdf.fonts.cmaps.CMapParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CMapAwareDocumentFont.class */
public class CMapAwareDocumentFont extends DocumentFont {
    private final PdfDictionary fontDic;
    private int spaceWidth;
    private CMap toUnicodeCmap;
    private char[] cidbyte2uni;

    public CMapAwareDocumentFont(PRIndirectReference refFont) {
        super(refFont);
        this.fontDic = (PdfDictionary) PdfReader.getPdfObjectRelease(refFont);
        processToUnicode();
        if (this.toUnicodeCmap == null) {
            processUni2Byte();
        }
        this.spaceWidth = super.getWidth(32);
        if (this.spaceWidth == 0) {
            this.spaceWidth = computeAverageWidth();
        }
    }

    private void processToUnicode() {
        PdfObject toUni = this.fontDic.get(PdfName.TOUNICODE);
        if (toUni != null) {
            try {
                byte[] touni = PdfReader.getStreamBytes((PRStream) PdfReader.getPdfObjectRelease(toUni));
                CMapParser cmapParser = new CMapParser();
                this.toUnicodeCmap = cmapParser.parse(new ByteArrayInputStream(touni));
            } catch (IOException e) {
                throw new Error("Unable to process ToUnicode map - " + e.getMessage(), e);
            }
        }
    }

    private void processUni2Byte() {
        IntHashtable uni2byte = getUni2Byte();
        int[] e = uni2byte.toOrderedKeys();
        this.cidbyte2uni = new char[256];
        for (int element : e) {
            int n = uni2byte.get(element);
            if (this.cidbyte2uni[n] == 0) {
                this.cidbyte2uni[n] = (char) element;
            }
        }
    }

    private int computeAverageWidth() {
        int count = 0;
        int total = 0;
        for (int i = 0; i < this.widths.length; i++) {
            if (this.widths[i] != 0) {
                total += this.widths[i];
                count++;
            }
        }
        if (count != 0) {
            return total / count;
        }
        return 0;
    }

    @Override // com.aowagie.text.pdf.DocumentFont, com.aowagie.text.pdf.BaseFont
    public int getWidth(int char1) {
        if (char1 == 32) {
            return this.spaceWidth;
        }
        return super.getWidth(char1);
    }

    private String decodeSingleCID(byte[] bytes, int offset, int len) {
        if (this.toUnicodeCmap != null) {
            if (offset + len > bytes.length) {
                throw new ArrayIndexOutOfBoundsException("Invalid index: " + offset + len);
            }
            return this.toUnicodeCmap.lookup(bytes, offset, len);
        }
        if (len == 1) {
            return new String(this.cidbyte2uni, 255 & bytes[offset], 1);
        }
        throw new Error("Multi-byte glyphs not implemented yet");
    }

    public String decode(byte[] cidbytes, int offset, int len) {
        StringBuffer sb = new StringBuffer();
        int i = offset;
        while (i < offset + len) {
            String rslt = decodeSingleCID(cidbytes, i, 1);
            if (rslt == null) {
                rslt = decodeSingleCID(cidbytes, i, 2);
                i++;
            }
            sb.append(rslt);
            i++;
        }
        return sb.toString();
    }
}
