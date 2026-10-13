package org.apache.pdfbox.pdmodel.interactive.annotation;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionFactory;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDLinkAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/PDAnnotationLink.class */
public class PDAnnotationLink extends PDAnnotation {
    private PDAppearanceHandler customAppearanceHandler;
    public static final String HIGHLIGHT_MODE_NONE = "N";
    public static final String HIGHLIGHT_MODE_INVERT = "I";
    public static final String HIGHLIGHT_MODE_OUTLINE = "O";
    public static final String HIGHLIGHT_MODE_PUSH = "P";
    public static final String SUB_TYPE = "Link";

    public PDAnnotationLink() {
        getCOSObject().setName(COSName.SUBTYPE, "Link");
    }

    public PDAnnotationLink(COSDictionary field) {
        super(field);
    }

    public PDAction getAction() {
        COSBase base = getCOSObject().getDictionaryObject(COSName.A);
        if (base instanceof COSDictionary) {
            return PDActionFactory.createAction((COSDictionary) base);
        }
        return null;
    }

    public void setAction(PDAction action) {
        getCOSObject().setItem(COSName.A, action);
    }

    public void setBorderStyle(PDBorderStyleDictionary bs) {
        getCOSObject().setItem(COSName.BS, bs);
    }

    public PDBorderStyleDictionary getBorderStyle() {
        COSBase bs = getCOSObject().getDictionaryObject(COSName.BS);
        if (bs instanceof COSDictionary) {
            return new PDBorderStyleDictionary((COSDictionary) bs);
        }
        return null;
    }

    public PDDestination getDestination() throws IOException {
        COSBase base = getCOSObject().getDictionaryObject(COSName.DEST);
        return PDDestination.create(base);
    }

    public void setDestination(PDDestination dest) {
        getCOSObject().setItem(COSName.DEST, dest);
    }

    public String getHighlightMode() {
        return getCOSObject().getNameAsString(COSName.H, "I");
    }

    public void setHighlightMode(String mode) {
        getCOSObject().setName(COSName.H, mode);
    }

    public void setPreviousURI(PDActionURI pa) {
        getCOSObject().setItem("PA", pa);
    }

    public PDActionURI getPreviousURI() {
        COSBase base = getCOSObject().getDictionaryObject("PA");
        if (base instanceof COSDictionary) {
            return new PDActionURI((COSDictionary) base);
        }
        return null;
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

    public void setCustomAppearanceHandler(PDAppearanceHandler appearanceHandler) {
        this.customAppearanceHandler = appearanceHandler;
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation
    public void constructAppearances() {
        constructAppearances(null);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation
    public void constructAppearances(PDDocument document) {
        if (this.customAppearanceHandler == null) {
            PDLinkAppearanceHandler appearanceHandler = new PDLinkAppearanceHandler(this, document);
            appearanceHandler.generateAppearanceStreams();
        } else {
            this.customAppearanceHandler.generateAppearanceStreams();
        }
    }
}
