package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.fontbox.ttf.HorizontalMetricsTable;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.GlyphList;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDTrueTypeFontEmbedder.class */
final class PDTrueTypeFontEmbedder extends TrueTypeEmbedder {
    private final Encoding fontEncoding;

    PDTrueTypeFontEmbedder(PDDocument document, COSDictionary dict, TrueTypeFont ttf, Encoding encoding) throws IOException {
        super(document, dict, ttf, false);
        dict.setItem(COSName.SUBTYPE, (COSBase) COSName.TRUE_TYPE);
        GlyphList glyphList = GlyphList.getAdobeGlyphList();
        this.fontEncoding = encoding;
        dict.setItem(COSName.ENCODING, encoding.getCOSObject());
        this.fontDescriptor.setSymbolic(false);
        this.fontDescriptor.setNonSymbolic(true);
        dict.setItem(COSName.FONT_DESC, this.fontDescriptor);
        setWidths(dict, glyphList);
    }

    private void setWidths(COSDictionary font, GlyphList glyphList) throws IOException {
        float scaling = 1000.0f / this.ttf.getHeader().getUnitsPerEm();
        HorizontalMetricsTable hmtx = this.ttf.getHorizontalMetrics();
        Map<Integer, String> codeToName = getFontEncoding().getCodeToNameMap();
        int firstChar = ((Integer) Collections.min(codeToName.keySet())).intValue();
        int lastChar = ((Integer) Collections.max(codeToName.keySet())).intValue();
        List<Integer> widths = new ArrayList<>((lastChar - firstChar) + 1);
        for (int i = 0; i < (lastChar - firstChar) + 1; i++) {
            widths.add(0);
        }
        for (Map.Entry<Integer, String> entry : codeToName.entrySet()) {
            int code = entry.getKey().intValue();
            String name = entry.getValue();
            if (code >= firstChar && code <= lastChar) {
                String uni = glyphList.toUnicode(name);
                int charCode = uni.codePointAt(0);
                int gid = this.cmapLookup.getGlyphId(charCode);
                widths.set(entry.getKey().intValue() - firstChar, Integer.valueOf(Math.round(hmtx.getAdvanceWidth(gid) * scaling)));
            }
        }
        font.setInt(COSName.FIRST_CHAR, firstChar);
        font.setInt(COSName.LAST_CHAR, lastChar);
        font.setItem(COSName.WIDTHS, (COSBase) COSArrayList.converterToCOSArray(widths));
    }

    public Encoding getFontEncoding() {
        return this.fontEncoding;
    }

    @Override // org.apache.pdfbox.pdmodel.font.TrueTypeEmbedder
    protected void buildSubset(InputStream ttfSubset, String tag, Map<Integer, Integer> gidToCid) throws IOException {
        throw new UnsupportedOperationException();
    }
}
