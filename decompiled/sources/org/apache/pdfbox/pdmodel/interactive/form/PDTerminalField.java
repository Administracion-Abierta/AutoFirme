package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.fdf.FDFField;
import org.apache.pdfbox.pdmodel.interactive.action.PDFormFieldAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/form/PDTerminalField.class */
public abstract class PDTerminalField extends PDField {
    abstract void constructAppearances() throws IOException;

    protected PDTerminalField(PDAcroForm acroForm) {
        super(acroForm);
    }

    PDTerminalField(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    public void setActions(PDFormFieldAdditionalActions actions) {
        getCOSObject().setItem(COSName.AA, actions);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDField
    public int getFieldFlags() {
        int retval = 0;
        COSInteger ff = (COSInteger) getCOSObject().getDictionaryObject(COSName.FF);
        if (ff != null) {
            retval = ff.intValue();
        } else if (getParent() != null) {
            retval = getParent().getFieldFlags();
        }
        return retval;
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDField
    public String getFieldType() {
        String fieldType = getCOSObject().getNameAsString(COSName.FT);
        if (fieldType == null && getParent() != null) {
            fieldType = getParent().getFieldType();
        }
        return fieldType;
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDField
    public void importFDF(FDFField fdfField) throws IOException {
        super.importFDF(fdfField);
        PDAnnotationWidget widget = getWidgets().get(0);
        if (widget != null) {
            int annotFlags = widget.getAnnotationFlags();
            Integer f = fdfField.getWidgetFieldFlags();
            if (f != null) {
                widget.setAnnotationFlags(f.intValue());
                return;
            }
            Integer setF = fdfField.getSetWidgetFieldFlags();
            if (setF != null) {
                annotFlags |= setF.intValue();
                widget.setAnnotationFlags(annotFlags);
            }
            Integer clrF = fdfField.getClearWidgetFieldFlags();
            if (clrF != null) {
                int clrFValue = clrF.intValue();
                widget.setAnnotationFlags(annotFlags & ((int) (((long) clrFValue) ^ 4294967295L)));
            }
        }
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDField
    FDFField exportFDF() throws IOException {
        FDFField fdfField = new FDFField();
        fdfField.setPartialFieldName(getPartialName());
        fdfField.setValue(getCOSObject().getDictionaryObject(COSName.V));
        return fdfField;
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDField
    public List<PDAnnotationWidget> getWidgets() {
        List<PDAnnotationWidget> widgets = new ArrayList<>();
        COSArray kids = (COSArray) getCOSObject().getDictionaryObject(COSName.KIDS);
        if (kids == null) {
            widgets.add(new PDAnnotationWidget(getCOSObject()));
        } else if (kids.size() > 0) {
            for (int i = 0; i < kids.size(); i++) {
                COSBase kid = kids.getObject(i);
                if (kid instanceof COSDictionary) {
                    widgets.add(new PDAnnotationWidget((COSDictionary) kid));
                }
            }
        }
        return widgets;
    }

    public void setWidgets(List<PDAnnotationWidget> children) {
        COSArray kidsArray = COSArrayList.converterToCOSArray(children);
        getCOSObject().setItem(COSName.KIDS, (COSBase) kidsArray);
        for (PDAnnotationWidget widget : children) {
            widget.getCOSObject().setItem(COSName.PARENT, this);
        }
    }

    @Deprecated
    public PDAnnotationWidget getWidget() {
        return getWidgets().get(0);
    }

    protected final void applyChange() throws IOException {
        if (!getAcroForm().getNeedAppearances()) {
            constructAppearances();
        }
    }
}
