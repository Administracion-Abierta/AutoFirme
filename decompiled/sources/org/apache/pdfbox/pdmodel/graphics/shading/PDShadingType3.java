package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Paint;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/PDShadingType3.class */
public class PDShadingType3 extends PDShadingType2 {
    public PDShadingType3(COSDictionary shadingDictionary) {
        super(shadingDictionary);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType2, org.apache.pdfbox.pdmodel.graphics.shading.PDShading
    public int getShadingType() {
        return 3;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType2, org.apache.pdfbox.pdmodel.graphics.shading.PDShading
    public Paint toPaint(Matrix matrix) {
        return new RadialShadingPaint(this, matrix);
    }
}
