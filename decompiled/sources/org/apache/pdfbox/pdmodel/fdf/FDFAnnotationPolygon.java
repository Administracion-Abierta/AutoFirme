package org.apache.pdfbox.pdmodel.fdf;

import java.awt.Color;
import java.io.IOException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFAnnotationPolygon.class */
public class FDFAnnotationPolygon extends FDFAnnotation {
    private static final Log LOG = LogFactory.getLog(FDFAnnotationPolygon.class);
    public static final String SUBTYPE = "Polygon";

    public FDFAnnotationPolygon() {
        this.annot.setName(COSName.SUBTYPE, "Polygon");
    }

    public FDFAnnotationPolygon(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationPolygon(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, "Polygon");
        initVertices(element);
        String color = element.getAttribute("interior-color");
        if (color != null && color.length() == 7 && color.charAt(0) == '#') {
            int colorValue = Integer.parseInt(color.substring(1, 7), 16);
            setInteriorColor(new Color(colorValue));
        }
    }

    private void initVertices(Element element) throws IOException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            String vertices = xpath.evaluate("vertices", element);
            if (vertices == null || vertices.isEmpty()) {
                throw new IOException("Error: missing element 'vertices'");
            }
            String[] verticesValues = vertices.split(",|;");
            float[] values = new float[verticesValues.length];
            for (int i = 0; i < verticesValues.length; i++) {
                values[i] = Float.parseFloat(verticesValues[i]);
            }
            setVertices(values);
        } catch (XPathExpressionException e) {
            LOG.debug("Error while evaluating XPath expression for polygon vertices");
        }
    }

    public void setVertices(float[] vertices) {
        COSArray newVertices = new COSArray();
        newVertices.setFloatArray(vertices);
        this.annot.setItem(COSName.VERTICES, (COSBase) newVertices);
    }

    public float[] getVertices() {
        COSArray array = (COSArray) this.annot.getDictionaryObject(COSName.VERTICES);
        if (array != null) {
            return array.toFloatArray();
        }
        return null;
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
}
