package org.apache.pdfbox.pdmodel.fdf;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFAnnotationTextMarkup.class */
public abstract class FDFAnnotationTextMarkup extends FDFAnnotation {
    public FDFAnnotationTextMarkup() {
    }

    public FDFAnnotationTextMarkup(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationTextMarkup(Element element) throws IOException {
        super(element);
        String coords = element.getAttribute("coords");
        if (coords == null || coords.isEmpty()) {
            throw new IOException("Error: missing attribute 'coords'");
        }
        String[] coordsValues = coords.split(",");
        if (coordsValues.length < 8) {
            throw new IOException("Error: too little numbers in attribute 'coords'");
        }
        float[] values = new float[coordsValues.length];
        for (int i = 0; i < coordsValues.length; i++) {
            values[i] = Float.parseFloat(coordsValues[i]);
        }
        setCoords(values);
    }

    public void setCoords(float[] coords) {
        COSArray newQuadPoints = new COSArray();
        newQuadPoints.setFloatArray(coords);
        this.annot.setItem(COSName.QUADPOINTS, (COSBase) newQuadPoints);
    }

    public float[] getCoords() {
        COSArray quadPoints = (COSArray) this.annot.getItem(COSName.QUADPOINTS);
        if (quadPoints != null) {
            return quadPoints.toFloatArray();
        }
        return null;
    }
}
