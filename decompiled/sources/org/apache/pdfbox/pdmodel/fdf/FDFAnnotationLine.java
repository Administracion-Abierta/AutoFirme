package org.apache.pdfbox.pdmodel.fdf;

import com.aowagie.text.pdf.ColumnText;
import com.ctc.wstx.cfg.XmlConsts;
import java.awt.Color;
import java.io.IOException;
import org.apache.fontbox.ttf.HeaderTable;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFAnnotationLine.class */
public class FDFAnnotationLine extends FDFAnnotation {
    public static final String SUBTYPE = "Line";

    public FDFAnnotationLine() {
        this.annot.setName(COSName.SUBTYPE, "Line");
    }

    public FDFAnnotationLine(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationLine(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, "Line");
        String startCoords = element.getAttribute("start");
        if (startCoords == null || startCoords.isEmpty()) {
            throw new IOException("Error: missing attribute 'start'");
        }
        String endCoords = element.getAttribute("end");
        if (endCoords == null || endCoords.isEmpty()) {
            throw new IOException("Error: missing attribute 'end'");
        }
        String line = startCoords + "," + endCoords;
        String[] lineValues = line.split(",");
        if (lineValues.length != 4) {
            throw new IOException("Error: wrong amount of line coordinates");
        }
        float[] values = new float[4];
        for (int i = 0; i < 4; i++) {
            values[i] = Float.parseFloat(lineValues[i]);
        }
        setLine(values);
        String leaderLine = element.getAttribute("leaderLength");
        if (leaderLine != null && !leaderLine.isEmpty()) {
            setLeaderLength(Float.parseFloat(leaderLine));
        }
        String leaderLineExtension = element.getAttribute("leaderExtend");
        if (leaderLineExtension != null && !leaderLineExtension.isEmpty()) {
            setLeaderExtend(Float.parseFloat(leaderLineExtension));
        }
        String leaderLineOffset = element.getAttribute("leaderOffset");
        if (leaderLineOffset != null && !leaderLineOffset.isEmpty()) {
            setLeaderOffset(Float.parseFloat(leaderLineOffset));
        }
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
        String caption = element.getAttribute("caption");
        if (caption != null && !caption.isEmpty()) {
            setCaption(XmlConsts.XML_SA_YES.equals(caption));
        }
        String captionH = element.getAttribute("caption-offset-h");
        if (captionH != null && !captionH.isEmpty()) {
            setCaptionHorizontalOffset(Float.parseFloat(captionH));
        }
        String captionV = element.getAttribute("caption-offset-v");
        if (captionV != null && !captionV.isEmpty()) {
            setCaptionVerticalOffset(Float.parseFloat(captionV));
        }
        String captionStyle = element.getAttribute("caption-style");
        if (captionStyle != null && !captionStyle.isEmpty()) {
            setCaptionStyle(captionStyle);
        }
    }

    public void setLine(float[] line) {
        COSArray newLine = new COSArray();
        newLine.setFloatArray(line);
        this.annot.setItem(COSName.L, (COSBase) newLine);
    }

    public float[] getLine() {
        COSArray array = (COSArray) this.annot.getDictionaryObject(COSName.L);
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

    public void setCaption(boolean cap) {
        this.annot.setBoolean(COSName.CAP, cap);
    }

    public boolean getCaption() {
        return this.annot.getBoolean(COSName.CAP, false);
    }

    public float getLeaderLength() {
        return this.annot.getFloat(COSName.LL);
    }

    public void setLeaderLength(float leaderLength) {
        this.annot.setFloat(COSName.LL, leaderLength);
    }

    public float getLeaderExtend() {
        return this.annot.getFloat(COSName.LLE);
    }

    public void setLeaderExtend(float leaderExtend) {
        this.annot.setFloat(COSName.LLE, leaderExtend);
    }

    public float getLeaderOffset() {
        return this.annot.getFloat(COSName.LLO);
    }

    public void setLeaderOffset(float leaderOffset) {
        this.annot.setFloat(COSName.LLO, leaderOffset);
    }

    public String getCaptionStyle() {
        return this.annot.getString(COSName.CP);
    }

    public void setCaptionStyle(String captionStyle) {
        this.annot.setString(COSName.CP, captionStyle);
    }

    public void setCaptionHorizontalOffset(float offset) {
        COSArray array = (COSArray) this.annot.getDictionaryObject(COSName.CO);
        if (array == null) {
            COSArray array2 = new COSArray();
            array2.setFloatArray(new float[]{offset, ColumnText.GLOBAL_SPACE_CHAR_RATIO});
            this.annot.setItem(COSName.CO, (COSBase) array2);
            return;
        }
        array.set(0, (COSBase) new COSFloat(offset));
    }

    public float getCaptionHorizontalOffset() {
        float retval = 0.0f;
        COSArray array = (COSArray) this.annot.getDictionaryObject(COSName.CO);
        if (array != null) {
            retval = array.toFloatArray()[0];
        }
        return retval;
    }

    public void setCaptionVerticalOffset(float offset) {
        COSArray array = (COSArray) this.annot.getDictionaryObject(COSName.CO);
        if (array == null) {
            COSArray array2 = new COSArray();
            array2.setFloatArray(new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, offset});
            this.annot.setItem(COSName.CO, (COSBase) array2);
            return;
        }
        array.set(1, (COSBase) new COSFloat(offset));
    }

    public float getCaptionVerticalOffset() {
        float retval = 0.0f;
        COSArray array = (COSArray) this.annot.getDictionaryObject(COSName.CO);
        if (array != null) {
            retval = array.toFloatArray()[1];
        }
        return retval;
    }
}
