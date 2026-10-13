package org.apache.pdfbox.pdmodel.interactive.annotation;

import com.aowagie.text.pdf.ColumnText;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDLineAppearanceHandler;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/PDAnnotationLine.class */
public class PDAnnotationLine extends PDAnnotationMarkup {
    private PDAppearanceHandler customAppearanceHandler;
    public static final String IT_LINE_ARROW = "LineArrow";
    public static final String IT_LINE_DIMENSION = "LineDimension";
    public static final String LE_SQUARE = "Square";
    public static final String LE_CIRCLE = "Circle";
    public static final String LE_DIAMOND = "Diamond";
    public static final String LE_OPEN_ARROW = "OpenArrow";
    public static final String LE_CLOSED_ARROW = "ClosedArrow";
    public static final String LE_NONE = "None";
    public static final String LE_BUTT = "Butt";
    public static final String LE_R_OPEN_ARROW = "ROpenArrow";
    public static final String LE_R_CLOSED_ARROW = "RClosedArrow";
    public static final String LE_SLASH = "Slash";
    public static final String SUB_TYPE = "Line";

    public PDAnnotationLine() {
        getCOSObject().setName(COSName.SUBTYPE, "Line");
        setLine(new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO});
    }

    public PDAnnotationLine(COSDictionary field) {
        super(field);
    }

    public void setLine(float[] l) {
        COSArray newL = new COSArray();
        newL.setFloatArray(l);
        getCOSObject().setItem(COSName.L, (COSBase) newL);
    }

    public float[] getLine() {
        COSArray l = (COSArray) getCOSObject().getDictionaryObject(COSName.L);
        return l.toFloatArray();
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup
    public void setStartPointEndingStyle(String style) {
        if (style == null) {
            style = "None";
        }
        COSBase base = getCOSObject().getDictionaryObject(COSName.LE);
        if (!(base instanceof COSArray) || ((COSArray) base).size() == 0) {
            COSArray array = new COSArray();
            array.add((COSBase) COSName.getPDFName(style));
            array.add((COSBase) COSName.getPDFName("None"));
            getCOSObject().setItem(COSName.LE, (COSBase) array);
            return;
        }
        ((COSArray) base).setName(0, style);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup
    public String getStartPointEndingStyle() {
        COSBase base = getCOSObject().getDictionaryObject(COSName.LE);
        if ((base instanceof COSArray) && ((COSArray) base).size() >= 2) {
            return ((COSArray) base).getName(0);
        }
        return "None";
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup
    public void setEndPointEndingStyle(String style) {
        if (style == null) {
            style = "None";
        }
        COSBase base = getCOSObject().getDictionaryObject(COSName.LE);
        if (!(base instanceof COSArray) || ((COSArray) base).size() < 2) {
            COSArray array = new COSArray();
            array.add((COSBase) COSName.getPDFName("None"));
            array.add((COSBase) COSName.getPDFName(style));
            getCOSObject().setItem(COSName.LE, (COSBase) array);
            return;
        }
        ((COSArray) base).setName(1, style);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup
    public String getEndPointEndingStyle() {
        COSBase base = getCOSObject().getDictionaryObject(COSName.LE);
        if ((base instanceof COSArray) && ((COSArray) base).size() >= 2) {
            return ((COSArray) base).getName(1);
        }
        return "None";
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup
    public void setInteriorColor(PDColor ic) {
        getCOSObject().setItem(COSName.IC, (COSBase) ic.toCOSArray());
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup
    public PDColor getInteriorColor() {
        return getColor(COSName.IC);
    }

    public void setCaption(boolean cap) {
        getCOSObject().setBoolean(COSName.CAP, cap);
    }

    public boolean getCaption() {
        return getCOSObject().getBoolean(COSName.CAP, false);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup
    public void setBorderStyle(PDBorderStyleDictionary bs) {
        getCOSObject().setItem(COSName.BS, bs);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup
    public PDBorderStyleDictionary getBorderStyle() {
        COSBase bs = getCOSObject().getDictionaryObject(COSName.BS);
        if (bs instanceof COSDictionary) {
            return new PDBorderStyleDictionary((COSDictionary) bs);
        }
        return null;
    }

    public float getLeaderLineLength() {
        return getCOSObject().getFloat(COSName.LL, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public void setLeaderLineLength(float leaderLineLength) {
        getCOSObject().setFloat(COSName.LL, leaderLineLength);
    }

    public float getLeaderLineExtensionLength() {
        return getCOSObject().getFloat(COSName.LLE, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public void setLeaderLineExtensionLength(float leaderLineExtensionLength) {
        getCOSObject().setFloat(COSName.LLE, leaderLineExtensionLength);
    }

    public float getLeaderLineOffsetLength() {
        return getCOSObject().getFloat(COSName.LLO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public void setLeaderLineOffsetLength(float leaderLineOffsetLength) {
        getCOSObject().setFloat(COSName.LLO, leaderLineOffsetLength);
    }

    public String getCaptionPositioning() {
        return getCOSObject().getNameAsString(COSName.CP);
    }

    public void setCaptionPositioning(String captionPositioning) {
        getCOSObject().setName(COSName.CP, captionPositioning);
    }

    public void setCaptionHorizontalOffset(float offset) {
        COSArray array = (COSArray) getCOSObject().getDictionaryObject(COSName.CO);
        if (array == null) {
            COSArray array2 = new COSArray();
            array2.setFloatArray(new float[]{offset, ColumnText.GLOBAL_SPACE_CHAR_RATIO});
            getCOSObject().setItem(COSName.CO, (COSBase) array2);
            return;
        }
        array.set(0, (COSBase) new COSFloat(offset));
    }

    public float getCaptionHorizontalOffset() {
        float retval = 0.0f;
        COSArray array = (COSArray) getCOSObject().getDictionaryObject(COSName.CO);
        if (array != null) {
            retval = array.toFloatArray()[0];
        }
        return retval;
    }

    public void setCaptionVerticalOffset(float offset) {
        COSArray array = (COSArray) getCOSObject().getDictionaryObject(COSName.CO);
        if (array == null) {
            COSArray array2 = new COSArray();
            array2.setFloatArray(new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, offset});
            getCOSObject().setItem(COSName.CO, (COSBase) array2);
            return;
        }
        array.set(1, (COSBase) new COSFloat(offset));
    }

    public float getCaptionVerticalOffset() {
        float retval = 0.0f;
        COSArray array = (COSArray) getCOSObject().getDictionaryObject(COSName.CO);
        if (array != null) {
            retval = array.toFloatArray()[1];
        }
        return retval;
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup
    public void setCustomAppearanceHandler(PDAppearanceHandler appearanceHandler) {
        this.customAppearanceHandler = appearanceHandler;
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup, org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation
    public void constructAppearances() {
        constructAppearances(null);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup, org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation
    public void constructAppearances(PDDocument document) {
        if (this.customAppearanceHandler == null) {
            PDLineAppearanceHandler appearanceHandler = new PDLineAppearanceHandler(this, document);
            appearanceHandler.generateAppearanceStreams();
        } else {
            this.customAppearanceHandler.generateAppearanceStreams();
        }
    }
}
