package org.apache.pdfbox.pdmodel.fdf;

import java.awt.Color;
import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFAnnotationSquare.class */
public class FDFAnnotationSquare extends FDFAnnotation {
    public static final String SUBTYPE = "Square";

    public FDFAnnotationSquare() {
        this.annot.setName(COSName.SUBTYPE, "Square");
    }

    public FDFAnnotationSquare(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationSquare(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, "Square");
        String color = element.getAttribute("interior-color");
        if (color != null && color.length() == 7 && color.charAt(0) == '#') {
            int colorValue = Integer.parseInt(color.substring(1, 7), 16);
            setInteriorColor(new Color(colorValue));
        }
        initFringe(element);
    }

    private void initFringe(Element element) throws IOException {
        String fringe = element.getAttribute("fringe");
        if (fringe != null && !fringe.isEmpty()) {
            String[] fringeValues = fringe.split(",");
            if (fringeValues.length != 4) {
                throw new IOException("Error: wrong amount of numbers in attribute 'fringe'");
            }
            PDRectangle rect = new PDRectangle();
            rect.setLowerLeftX(Float.parseFloat(fringeValues[0]));
            rect.setLowerLeftY(Float.parseFloat(fringeValues[1]));
            rect.setUpperRightX(Float.parseFloat(fringeValues[2]));
            rect.setUpperRightY(Float.parseFloat(fringeValues[3]));
            setFringe(rect);
        }
    }

    public final void setInteriorColor(Color color) {
        COSArray array = null;
        if (color != null) {
            float[] colors = color.getRGBColorComponents((float[]) null);
            array = new COSArray();
            array.setFloatArray(colors);
        }
        this.annot.setItem(COSName.IC, (COSBase) array);
    }

    public Color getInteriorColor() {
        Color retval = null;
        COSArray array = (COSArray) this.annot.getDictionaryObject(COSName.IC);
        if (array != null) {
            float[] rgb = array.toFloatArray();
            if (rgb.length >= 3) {
                retval = new Color(rgb[0], rgb[1], rgb[2]);
            }
        }
        return retval;
    }

    public final void setFringe(PDRectangle fringe) {
        this.annot.setItem(COSName.RD, fringe);
    }

    public PDRectangle getFringe() {
        COSArray rd = (COSArray) this.annot.getDictionaryObject(COSName.RD);
        if (rd != null) {
            return new PDRectangle(rd);
        }
        return null;
    }
}
