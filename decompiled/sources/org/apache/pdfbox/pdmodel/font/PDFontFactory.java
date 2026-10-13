package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.ResourceCache;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDFontFactory.class */
public final class PDFontFactory {
    private static final Log LOG = LogFactory.getLog(PDFontFactory.class);

    private PDFontFactory() {
    }

    public static PDFont createFont(COSDictionary dictionary) throws IOException {
        return createFont(dictionary, null);
    }

    public static PDFont createFont(COSDictionary dictionary, ResourceCache resourceCache) throws IOException {
        COSName type = dictionary.getCOSName(COSName.TYPE, COSName.FONT);
        if (!COSName.FONT.equals(type)) {
            LOG.error("Expected 'Font' dictionary but found '" + type.getName() + OperatorName.SHOW_TEXT_LINE);
        }
        COSName subType = dictionary.getCOSName(COSName.SUBTYPE);
        if (COSName.TYPE1.equals(subType)) {
            COSBase fd = dictionary.getDictionaryObject(COSName.FONT_DESC);
            if ((fd instanceof COSDictionary) && ((COSDictionary) fd).containsKey(COSName.FONT_FILE3)) {
                return new PDType1CFont(dictionary);
            }
            return new PDType1Font(dictionary);
        }
        if (COSName.MM_TYPE1.equals(subType)) {
            COSBase fd2 = dictionary.getDictionaryObject(COSName.FONT_DESC);
            if ((fd2 instanceof COSDictionary) && ((COSDictionary) fd2).containsKey(COSName.FONT_FILE3)) {
                return new PDType1CFont(dictionary);
            }
            return new PDMMType1Font(dictionary);
        }
        if (COSName.TRUE_TYPE.equals(subType)) {
            return new PDTrueTypeFont(dictionary);
        }
        if (COSName.TYPE3.equals(subType)) {
            return new PDType3Font(dictionary, resourceCache);
        }
        if (COSName.TYPE0.equals(subType)) {
            return new PDType0Font(dictionary);
        }
        if (COSName.CID_FONT_TYPE0.equals(subType)) {
            throw new IllegalArgumentException("Type 0 descendant font not allowed");
        }
        if (COSName.CID_FONT_TYPE2.equals(subType)) {
            throw new IllegalArgumentException("Type 2 descendant font not allowed");
        }
        LOG.warn("Invalid font subtype '" + subType + OperatorName.SHOW_TEXT_LINE);
        return new PDType1Font(dictionary);
    }

    static PDCIDFont createDescendantFont(COSDictionary dictionary, PDType0Font parent) throws IOException {
        COSName type = dictionary.getCOSName(COSName.TYPE, COSName.FONT);
        if (!COSName.FONT.equals(type)) {
            throw new IllegalArgumentException("Expected 'Font' dictionary but found '" + type.getName() + OperatorName.SHOW_TEXT_LINE);
        }
        COSName subType = dictionary.getCOSName(COSName.SUBTYPE);
        if (COSName.CID_FONT_TYPE0.equals(subType)) {
            return new PDCIDFontType0(dictionary, parent);
        }
        if (COSName.CID_FONT_TYPE2.equals(subType)) {
            return new PDCIDFontType2(dictionary, parent);
        }
        throw new IOException("Invalid font type: " + type);
    }

    @Deprecated
    public static PDFont createDefaultFont() throws IOException {
        return PDType1Font.HELVETICA;
    }
}
