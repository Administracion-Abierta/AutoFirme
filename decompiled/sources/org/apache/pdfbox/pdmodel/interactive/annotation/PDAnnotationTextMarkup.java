package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDHighlightAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDSquigglyAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDStrikeoutAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDUnderlineAppearanceHandler;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/PDAnnotationTextMarkup.class */
public class PDAnnotationTextMarkup extends PDAnnotationMarkup {
    private PDAppearanceHandler customAppearanceHandler;
    public static final String SUB_TYPE_HIGHLIGHT = "Highlight";
    public static final String SUB_TYPE_UNDERLINE = "Underline";
    public static final String SUB_TYPE_SQUIGGLY = "Squiggly";
    public static final String SUB_TYPE_STRIKEOUT = "StrikeOut";

    private PDAnnotationTextMarkup() {
    }

    public PDAnnotationTextMarkup(String subType) {
        setSubtype(subType);
        setQuadPoints(new float[0]);
    }

    public PDAnnotationTextMarkup(COSDictionary field) {
        super(field);
    }

    public void setQuadPoints(float[] quadPoints) {
        COSArray newQuadPoints = new COSArray();
        newQuadPoints.setFloatArray(quadPoints);
        getCOSObject().setItem(COSName.QUADPOINTS, (COSBase) newQuadPoints);
    }

    public float[] getQuadPoints() {
        COSBase base = getCOSObject().getDictionaryObject(COSName.QUADPOINTS);
        if (base instanceof COSArray) {
            return ((COSArray) base).toFloatArray();
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
            PDAppearanceHandler appearanceHandler = null;
            if ("Highlight".equals(getSubtype())) {
                appearanceHandler = new PDHighlightAppearanceHandler(this, document);
            } else if ("Squiggly".equals(getSubtype())) {
                appearanceHandler = new PDSquigglyAppearanceHandler(this, document);
            } else if ("StrikeOut".equals(getSubtype())) {
                appearanceHandler = new PDStrikeoutAppearanceHandler(this, document);
            } else if ("Underline".equals(getSubtype())) {
                appearanceHandler = new PDUnderlineAppearanceHandler(this, document);
            }
            if (appearanceHandler != null) {
                appearanceHandler.generateAppearanceStreams();
                return;
            }
            return;
        }
        this.customAppearanceHandler.generateAppearanceStreams();
    }
}
