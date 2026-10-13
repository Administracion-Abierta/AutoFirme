package org.apache.pdfbox.pdmodel.font;

import com.aowagie.text.pdf.ColumnText;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.fontbox.afm.FontMetrics;
import org.apache.fontbox.encoding.BuiltInEncoding;
import org.apache.fontbox.pfb.PfbParser;
import org.apache.fontbox.type1.Type1Font;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.GlyphList;
import org.apache.pdfbox.pdmodel.font.encoding.Type1Encoding;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDType1FontEmbedder.class */
class PDType1FontEmbedder {
    private final Encoding fontEncoding;
    private final Type1Font type1;

    PDType1FontEmbedder(PDDocument doc, COSDictionary dict, InputStream pfbStream, Encoding encoding) throws IOException {
        dict.setItem(COSName.SUBTYPE, COSName.TYPE1);
        byte[] pfbBytes = IOUtils.toByteArray(pfbStream);
        PfbParser pfbParser = new PfbParser(pfbBytes);
        this.type1 = Type1Font.createWithPFB(pfbBytes);
        if (encoding == null) {
            this.fontEncoding = Type1Encoding.fromFontBox(this.type1.getEncoding());
        } else {
            this.fontEncoding = encoding;
        }
        PDFontDescriptor fd = buildFontDescriptor(this.type1);
        PDStream fontStream = new PDStream(doc, pfbParser.getInputStream(), COSName.FLATE_DECODE);
        fontStream.getCOSObject().setInt("Length", pfbParser.size());
        for (int i = 0; i < pfbParser.getLengths().length; i++) {
            fontStream.getCOSObject().setInt("Length" + (i + 1), pfbParser.getLengths()[i]);
        }
        fd.setFontFile(fontStream);
        dict.setItem(COSName.FONT_DESC, fd);
        dict.setName(COSName.BASE_FONT, this.type1.getName());
        List<Integer> widths = new ArrayList<>(256);
        for (int code = 0; code <= 255; code++) {
            String name = this.fontEncoding.getName(code);
            int width = Math.round(this.type1.getWidth(name));
            widths.add(Integer.valueOf(width));
        }
        dict.setInt(COSName.FIRST_CHAR, 0);
        dict.setInt(COSName.LAST_CHAR, 255);
        dict.setItem(COSName.WIDTHS, COSArrayList.converterToCOSArray(widths));
        dict.setItem(COSName.ENCODING, encoding);
    }

    static PDFontDescriptor buildFontDescriptor(Type1Font type1) {
        boolean isSymbolic = type1.getEncoding() instanceof BuiltInEncoding;
        BoundingBox bbox = type1.getFontBBox();
        PDFontDescriptor fd = new PDFontDescriptor();
        fd.setFontName(type1.getName());
        fd.setFontFamily(type1.getFamilyName());
        fd.setNonSymbolic(!isSymbolic);
        fd.setSymbolic(isSymbolic);
        fd.setFontBoundingBox(new PDRectangle(bbox));
        fd.setItalicAngle(type1.getItalicAngle());
        fd.setAscent(bbox.getUpperRightY());
        fd.setDescent(bbox.getLowerLeftY());
        fd.setCapHeight(type1.getBlueValues().get(2).floatValue());
        fd.setStemV(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        return fd;
    }

    static PDFontDescriptor buildFontDescriptor(FontMetrics metrics) {
        boolean isSymbolic = metrics.getEncodingScheme().equals("FontSpecific");
        PDFontDescriptor fd = new PDFontDescriptor();
        fd.setFontName(metrics.getFontName());
        fd.setFontFamily(metrics.getFamilyName());
        fd.setNonSymbolic(!isSymbolic);
        fd.setSymbolic(isSymbolic);
        fd.setFontBoundingBox(new PDRectangle(metrics.getFontBBox()));
        fd.setItalicAngle(metrics.getItalicAngle());
        fd.setAscent(metrics.getAscender());
        fd.setDescent(metrics.getDescender());
        fd.setCapHeight(metrics.getCapHeight());
        fd.setXHeight(metrics.getXHeight());
        fd.setAverageWidth(metrics.getAverageCharacterWidth());
        fd.setCharacterSet(metrics.getCharacterSet());
        fd.setStemV(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        return fd;
    }

    public Encoding getFontEncoding() {
        return this.fontEncoding;
    }

    public GlyphList getGlyphList() {
        return GlyphList.getAdobeGlyphList();
    }

    public Type1Font getType1Font() {
        return this.type1;
    }
}
