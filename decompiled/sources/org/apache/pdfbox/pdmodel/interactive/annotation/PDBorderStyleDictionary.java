package org.apache.pdfbox.pdmodel.interactive.annotation;

import com.aowagie.text.pdf.ColumnText;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.PDLineDashPattern;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/PDBorderStyleDictionary.class */
public class PDBorderStyleDictionary implements COSObjectable {
    public static final String STYLE_SOLID = "S";
    public static final String STYLE_DASHED = "D";
    public static final String STYLE_BEVELED = "B";
    public static final String STYLE_INSET = "I";
    public static final String STYLE_UNDERLINE = "U";
    private final COSDictionary dictionary;

    public PDBorderStyleDictionary() {
        this.dictionary = new COSDictionary();
    }

    public PDBorderStyleDictionary(COSDictionary dict) {
        this.dictionary = dict;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public void setWidth(float w) {
        if (w == ((int) w)) {
            getCOSObject().setInt(COSName.W, (int) w);
        } else {
            getCOSObject().setFloat(COSName.W, w);
        }
    }

    public float getWidth() {
        if (getCOSObject().getDictionaryObject(COSName.W) instanceof COSName) {
            return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        return getCOSObject().getFloat(COSName.W, 1.0f);
    }

    public void setStyle(String s) {
        getCOSObject().setName(COSName.S, s);
    }

    public String getStyle() {
        return getCOSObject().getNameAsString(COSName.S, "S");
    }

    public void setDashStyle(COSArray dashArray) {
        getCOSObject().setItem(COSName.D, (COSBase) dashArray);
    }

    public PDLineDashPattern getDashStyle() {
        COSArray d = (COSArray) getCOSObject().getDictionaryObject(COSName.D);
        if (d == null) {
            d = new COSArray();
            d.add((COSBase) COSInteger.THREE);
            getCOSObject().setItem(COSName.D, (COSBase) d);
        }
        return new PDLineDashPattern(d, 0);
    }
}
