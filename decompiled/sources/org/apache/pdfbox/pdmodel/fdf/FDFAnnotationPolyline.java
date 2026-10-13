package org.apache.pdfbox.pdmodel.fdf;

import java.awt.Color;
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
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFAnnotationPolyline.class */
public class FDFAnnotationPolyline extends FDFAnnotation {
    private static final Log LOG = LogFactory.getLog(FDFAnnotationPolyline.class);
    public static final String SUBTYPE = "Polyline";

    public FDFAnnotationPolyline() {
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
    }

    public FDFAnnotationPolyline(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationPolyline(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
        initVertices(element);
        initStyles(element);
    }

    private void initVertices(Element element) throws IOException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            String vertices = xpath.evaluate("vertices[1]", element);
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
            LOG.debug("Error while evaluating XPath expression for polyline vertices");
        }
    }

    private void initStyles(Element element) {
        String startStyle = element.getAttribute(HeaderTable.TAG);
        if (startStyle != null && !startStyle.isEmpty()) {
            setStartPointEndingStyle(startStyle);
        }
        String endStyle = element.getAttribute("tail");
        if (endStyle != null && !endStyle.isEmpty()) {
            setEndPointEndingStyle(endStyle);
        }
        String color = element.getAttribute("interior-color");
        if (color != null && color.length() == 7 && color.charAt(0) == '#') {
            int colorValue = Integer.parseInt(color.substring(1, 7), 16);
            setInteriorColor(new Color(colorValue));
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

    public void setStartPointEndingStyle(String style) {
        if (style == null) {
            style = "None";
        }
        COSArray array = (COSArray) this.annot.getDictionaryObject(COSName.LE);
        if (array == null) {
            COSArray array2 = new COSArray();
            array2.add((COSBase) COSName.getPDFName(style));
            array2.add((COSBase) COSName.getPDFName("None"));
            this.annot.setItem(COSName.LE, (COSBase) array2);
            return;
        }
        array.setName(0, style);
    }

    public String getStartPointEndingStyle() {
        String retval = "None";
        COSArray array = (COSArray) this.annot.getDictionaryObject(COSName.LE);
        if (array != null) {
            retval = array.getName(0);
        }
        return retval;
    }

    public void setEndPointEndingStyle(String style) {
        if (style == null) {
            style = "None";
        }
        COSArray array = (COSArray) this.annot.getDictionaryObject(COSName.LE);
        if (array == null) {
            COSArray array2 = new COSArray();
            array2.add((COSBase) COSName.getPDFName("None"));
            array2.add((COSBase) COSName.getPDFName(style));
            this.annot.setItem(COSName.LE, (COSBase) array2);
            return;
        }
        array.setName(1, style);
    }

    public String getEndPointEndingStyle() {
        String retval = "None";
        COSArray array = (COSArray) this.annot.getDictionaryObject(COSName.LE);
        if (array != null) {
            retval = array.getName(1);
        }
        return retval;
    }

    public void setInteriorColor(Color color) {
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
