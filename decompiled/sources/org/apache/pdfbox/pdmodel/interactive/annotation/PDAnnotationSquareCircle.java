package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDCircleAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDSquareAppearanceHandler;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/PDAnnotationSquareCircle.class */
public class PDAnnotationSquareCircle extends PDAnnotationMarkup {
    public static final String SUB_TYPE_SQUARE = "Square";
    public static final String SUB_TYPE_CIRCLE = "Circle";
    private PDAppearanceHandler customAppearanceHandler;

    public PDAnnotationSquareCircle(String subType) {
        setSubtype(subType);
    }

    public PDAnnotationSquareCircle(COSDictionary field) {
        super(field);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup
    public void setInteriorColor(PDColor ic) {
        getCOSObject().setItem(COSName.IC, (COSBase) ic.toCOSArray());
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup
    public PDColor getInteriorColor() {
        return getColor(COSName.IC);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup
    public void setBorderEffect(PDBorderEffectDictionary be) {
        getCOSObject().setItem(COSName.BE, be);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup
    public PDBorderEffectDictionary getBorderEffect() {
        COSDictionary be = (COSDictionary) getCOSObject().getDictionaryObject(COSName.BE);
        if (be != null) {
            return new PDBorderEffectDictionary(be);
        }
        return null;
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup
    public void setRectDifference(PDRectangle rd) {
        getCOSObject().setItem(COSName.RD, rd);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup
    public PDRectangle getRectDifference() {
        COSArray rd = (COSArray) getCOSObject().getDictionaryObject(COSName.RD);
        if (rd != null) {
            return new PDRectangle(rd);
        }
        return null;
    }

    public void setSubtype(String subType) {
        getCOSObject().setName(COSName.SUBTYPE, subType);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation
    public String getSubtype() {
        return getCOSObject().getNameAsString(COSName.SUBTYPE);
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

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup
    public void setRectDifferences(float difference) {
        setRectDifferences(difference, difference, difference, difference);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup
    public void setRectDifferences(float differenceLeft, float differenceTop, float differenceRight, float differenceBottom) {
        COSArray margins = new COSArray();
        margins.add((COSBase) new COSFloat(differenceLeft));
        margins.add((COSBase) new COSFloat(differenceTop));
        margins.add((COSBase) new COSFloat(differenceRight));
        margins.add((COSBase) new COSFloat(differenceBottom));
        getCOSObject().setItem(COSName.RD, (COSBase) margins);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup
    public float[] getRectDifferences() {
        COSBase margin = getCOSObject().getItem(COSName.RD);
        if (margin instanceof COSArray) {
            return ((COSArray) margin).toFloatArray();
        }
        return new float[0];
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
            if ("Circle".equals(getSubtype())) {
                PDCircleAppearanceHandler appearanceHandler = new PDCircleAppearanceHandler(this, document);
                appearanceHandler.generateAppearanceStreams();
                return;
            } else {
                if ("Square".equals(getSubtype())) {
                    PDSquareAppearanceHandler appearanceHandler2 = new PDSquareAppearanceHandler(this, document);
                    appearanceHandler2.generateAppearanceStreams();
                    return;
                }
                return;
            }
        }
        this.customAppearanceHandler.generateAppearanceStreams();
    }
}
