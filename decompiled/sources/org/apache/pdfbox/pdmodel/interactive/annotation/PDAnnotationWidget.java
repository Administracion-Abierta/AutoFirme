package org.apache.pdfbox.pdmodel.interactive.annotation;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionFactory;
import org.apache.pdfbox.pdmodel.interactive.action.PDAnnotationAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.form.PDTerminalField;
import org.apache.pdfbox.pdmodel.interactive.measurement.PDNumberFormatDictionary;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/PDAnnotationWidget.class */
public class PDAnnotationWidget extends PDAnnotation {
    public static final String SUB_TYPE = "Widget";

    public PDAnnotationWidget() {
        getCOSObject().setName(COSName.SUBTYPE, SUB_TYPE);
    }

    public PDAnnotationWidget(COSDictionary field) {
        super(field);
        getCOSObject().setName(COSName.SUBTYPE, SUB_TYPE);
    }

    public String getHighlightingMode() {
        return getCOSObject().getNameAsString(COSName.H, "I");
    }

    public void setHighlightingMode(String highlightingMode) {
        if (highlightingMode == null || "N".equals(highlightingMode) || "I".equals(highlightingMode) || PDAnnotationLink.HIGHLIGHT_MODE_OUTLINE.equals(highlightingMode) || "P".equals(highlightingMode) || PDNumberFormatDictionary.FRACTIONAL_DISPLAY_TRUNCATE.equals(highlightingMode)) {
            getCOSObject().setName(COSName.H, highlightingMode);
            return;
        }
        throw new IllegalArgumentException("Valid values for highlighting mode are 'N', 'N', 'O', 'P' or 'T'");
    }

    public PDAppearanceCharacteristicsDictionary getAppearanceCharacteristics() {
        COSBase mk = getCOSObject().getDictionaryObject(COSName.MK);
        if (mk instanceof COSDictionary) {
            return new PDAppearanceCharacteristicsDictionary((COSDictionary) mk);
        }
        return null;
    }

    public void setAppearanceCharacteristics(PDAppearanceCharacteristicsDictionary appearanceCharacteristics) {
        getCOSObject().setItem(COSName.MK, appearanceCharacteristics);
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

    public PDAnnotationAdditionalActions getActions() {
        COSBase base = getCOSObject().getDictionaryObject(COSName.AA);
        if (base instanceof COSDictionary) {
            return new PDAnnotationAdditionalActions((COSDictionary) base);
        }
        return null;
    }

    public void setActions(PDAnnotationAdditionalActions actions) {
        getCOSObject().setItem(COSName.AA, actions);
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

    public void setParent(PDTerminalField field) {
        if (getCOSObject().equals(field.getCOSObject())) {
            throw new IllegalArgumentException("setParent() is not to be called for a field that shares a dictionary with its only widget");
        }
        getCOSObject().setItem(COSName.PARENT, field);
    }
}
