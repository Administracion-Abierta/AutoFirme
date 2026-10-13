package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.Set;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceEntry;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/form/PDCheckBox.class */
public final class PDCheckBox extends PDButton {
    public PDCheckBox(PDAcroForm acroForm) {
        super(acroForm);
    }

    PDCheckBox(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    public boolean isChecked() {
        return getValue().compareTo(getOnValue()) == 0;
    }

    public void check() throws IOException {
        setValue(getOnValue());
    }

    public void unCheck() throws IOException {
        setValue(COSName.Off.getName());
    }

    public String getOnValue() {
        PDAppearanceEntry normalAppearance;
        PDAnnotationWidget widget = getWidgets().get(0);
        PDAppearanceDictionary apDictionary = widget.getAppearance();
        if (apDictionary != null && (normalAppearance = apDictionary.getNormalAppearance()) != null) {
            Set<COSName> entries = normalAppearance.getSubDictionary().keySet();
            for (COSName entry : entries) {
                if (COSName.Off.compareTo(entry) != 0) {
                    return entry.getName();
                }
            }
            return "";
        }
        return "";
    }
}
