package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/form/PDRadioButton.class */
public final class PDRadioButton extends PDButton {
    private static final int FLAG_NO_TOGGLE_TO_OFF = 16384;

    public PDRadioButton(PDAcroForm acroForm) {
        super(acroForm);
        getCOSObject().setFlag(COSName.FF, 32768, true);
    }

    PDRadioButton(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    public void setRadiosInUnison(boolean radiosInUnison) {
        getCOSObject().setFlag(COSName.FF, 33554432, radiosInUnison);
    }

    public boolean isRadiosInUnison() {
        return getCOSObject().getFlag(COSName.FF, 33554432);
    }

    public int getSelectedIndex() {
        int idx = 0;
        for (PDAnnotationWidget widget : getWidgets()) {
            if (!COSName.Off.equals(widget.getAppearanceState())) {
                return idx;
            }
            idx++;
        }
        return -1;
    }

    public List<String> getSelectedExportValues() throws IOException {
        Set<String> onValues = getOnValues();
        List<String> exportValues = getExportValues();
        List<String> selectedExportValues = new ArrayList<>();
        if (exportValues.isEmpty()) {
            selectedExportValues.add(getValue());
            return selectedExportValues;
        }
        String fieldValue = getValue();
        int idx = 0;
        for (String onValue : onValues) {
            if (onValue.compareTo(fieldValue) == 0) {
                selectedExportValues.add(exportValues.get(idx));
            }
            idx++;
        }
        return selectedExportValues;
    }
}
