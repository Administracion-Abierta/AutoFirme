package org.apache.pdfbox.pdmodel.fdf;

import com.aowagie.text.ElementTags;
import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.mozilla.javascript.NativeSymbol;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFAnnotationCaret.class */
public class FDFAnnotationCaret extends FDFAnnotation {
    public static final String SUBTYPE = "Caret";

    public FDFAnnotationCaret() {
        this.annot.setName(COSName.SUBTYPE, "Caret");
    }

    public FDFAnnotationCaret(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationCaret(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, "Caret");
        initFringe(element);
        String symbol = element.getAttribute(NativeSymbol.TYPE_NAME);
        if (symbol != null && !symbol.isEmpty()) {
            setSymbol(element.getAttribute(NativeSymbol.TYPE_NAME));
        }
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

    public final void setSymbol(String symbol) {
        String newSymbol = "None";
        if (ElementTags.PARAGRAPH.equals(symbol)) {
            newSymbol = "P";
        }
        this.annot.setString(COSName.SY, newSymbol);
    }

    public String getSymbol() {
        return this.annot.getString(COSName.SY);
    }
}
