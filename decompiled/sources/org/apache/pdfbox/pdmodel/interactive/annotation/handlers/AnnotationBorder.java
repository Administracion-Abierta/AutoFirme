package org.apache.pdfbox.pdmodel.interactive.annotation.handlers;

import com.aowagie.text.pdf.ColumnText;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/handlers/AnnotationBorder.class */
class AnnotationBorder {
    float[] dashArray = null;
    boolean underline = false;
    float width = ColumnText.GLOBAL_SPACE_CHAR_RATIO;

    AnnotationBorder() {
    }

    static AnnotationBorder getAnnotationBorder(PDAnnotation annotation, PDBorderStyleDictionary borderStyle) {
        AnnotationBorder ab = new AnnotationBorder();
        if (borderStyle == null) {
            COSArray border = annotation.getBorder();
            if (border.size() >= 3 && (border.getObject(2) instanceof COSNumber)) {
                ab.width = ((COSNumber) border.getObject(2)).floatValue();
            }
            if (border.size() > 3) {
                COSBase base3 = border.getObject(3);
                if (base3 instanceof COSArray) {
                    ab.dashArray = ((COSArray) base3).toFloatArray();
                }
            }
        } else {
            ab.width = borderStyle.getWidth();
            if (borderStyle.getStyle().equals("D")) {
                ab.dashArray = borderStyle.getDashStyle().getDashArray();
            }
            if (borderStyle.getStyle().equals(PDBorderStyleDictionary.STYLE_UNDERLINE)) {
                ab.underline = true;
            }
        }
        if (ab.dashArray != null) {
            boolean allZero = true;
            for (float f : ab.dashArray) {
                if (Float.compare(f, ColumnText.GLOBAL_SPACE_CHAR_RATIO) != 0) {
                    allZero = false;
                    break;
                }
            }
            if (allZero) {
                ab.dashArray = null;
            }
        }
        return ab;
    }
}
