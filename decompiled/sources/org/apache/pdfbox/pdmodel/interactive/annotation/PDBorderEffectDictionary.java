package org.apache.pdfbox.pdmodel.interactive.annotation;

import com.aowagie.text.pdf.ColumnText;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/PDBorderEffectDictionary.class */
public class PDBorderEffectDictionary implements COSObjectable {
    public static final String STYLE_SOLID = "S";
    public static final String STYLE_CLOUDY = "C";
    private final COSDictionary dictionary;

    public PDBorderEffectDictionary() {
        this.dictionary = new COSDictionary();
    }

    public PDBorderEffectDictionary(COSDictionary dict) {
        this.dictionary = dict;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public void setIntensity(float i) {
        getCOSObject().setFloat("I", i);
    }

    public float getIntensity() {
        return getCOSObject().getFloat("I", ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public void setStyle(String s) {
        getCOSObject().setName("S", s);
    }

    public String getStyle() {
        return getCOSObject().getNameAsString("S", "S");
    }
}
