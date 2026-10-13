package org.apache.pdfbox.pdmodel.fdf;

import com.aowagie.text.ElementTags;
import java.io.IOException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.ttf.HeaderTable;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFAnnotationFreeText.class */
public class FDFAnnotationFreeText extends FDFAnnotation {
    private static final Log LOG = LogFactory.getLog(FDFAnnotationFreeText.class);
    public static final String SUBTYPE = "FreeText";

    public FDFAnnotationFreeText() {
        this.annot.setName(COSName.SUBTYPE, "FreeText");
    }

    public FDFAnnotationFreeText(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationFreeText(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, "FreeText");
        setJustification(element.getAttribute("justification"));
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            setDefaultAppearance(xpath.evaluate("defaultappearance", element));
            setDefaultStyle(xpath.evaluate("defaultstyle", element));
        } catch (XPathExpressionException e) {
            LOG.debug("Error while evaluating XPath expression");
        }
        initCallout(element);
        String rotation = element.getAttribute(ElementTags.ROTATION);
        if (rotation != null && !rotation.isEmpty()) {
            setRotation(Integer.parseInt(rotation));
        }
        initFringe(element);
        String lineEndingStyle = element.getAttribute(HeaderTable.TAG);
        if (lineEndingStyle != null && !lineEndingStyle.isEmpty()) {
            setLineEndingStyle(lineEndingStyle);
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

    private void initCallout(Element element) {
        String callout = element.getAttribute("callout");
        if (callout != null && !callout.isEmpty()) {
            String[] calloutValues = callout.split(",");
            float[] values = new float[calloutValues.length];
            for (int i = 0; i < calloutValues.length; i++) {
                values[i] = Float.parseFloat(calloutValues[i]);
            }
            setCallout(values);
        }
    }

    public void setCallout(float[] callout) {
        COSArray newCallout = new COSArray();
        newCallout.setFloatArray(callout);
        this.annot.setItem(COSName.CL, (COSBase) newCallout);
    }

    public float[] getCallout() {
        COSArray array = (COSArray) this.annot.getDictionaryObject(COSName.CL);
        if (array != null) {
            return array.toFloatArray();
        }
        return null;
    }

    public final void setJustification(String justification) {
        int quadding = 0;
        if ("centered".equals(justification)) {
            quadding = 1;
        } else if (ElementTags.RIGHT.equals(justification)) {
            quadding = 2;
        }
        this.annot.setInt(COSName.Q, quadding);
    }

    public String getJustification() {
        return "" + this.annot.getInt(COSName.Q, 0);
    }

    public final void setRotation(int rotation) {
        this.annot.setInt(COSName.ROTATE, rotation);
    }

    public String getRotation() {
        return this.annot.getString(COSName.ROTATE);
    }

    public final void setDefaultAppearance(String appearance) {
        this.annot.setString(COSName.DA, appearance);
    }

    public String getDefaultAppearance() {
        return this.annot.getString(COSName.DA);
    }

    public final void setDefaultStyle(String style) {
        this.annot.setString(COSName.DS, style);
    }

    public String getDefaultStyle() {
        return this.annot.getString(COSName.DS);
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

    public final void setLineEndingStyle(String style) {
        this.annot.setName(COSName.LE, style);
    }

    public String getLineEndingStyle() {
        return this.annot.getNameAsString(COSName.LE);
    }
}
