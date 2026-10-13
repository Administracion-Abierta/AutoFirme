package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/form/PDComboBox.class */
public final class PDComboBox extends PDChoice {
    private static final int FLAG_EDIT = 262144;

    public PDComboBox(PDAcroForm acroForm) {
        super(acroForm);
        setCombo(true);
    }

    PDComboBox(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    public boolean isEdit() {
        return getCOSObject().getFlag(COSName.FF, 262144);
    }

    public void setEdit(boolean edit) {
        getCOSObject().setFlag(COSName.FF, 262144, edit);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDChoice, org.apache.pdfbox.pdmodel.interactive.form.PDTerminalField
    void constructAppearances() throws IOException {
        AppearanceGeneratorHelper apHelper = new AppearanceGeneratorHelper(this);
        List<String> values = getValue();
        if (!values.isEmpty()) {
            apHelper.setAppearanceValue(values.get(0));
        } else {
            apHelper.setAppearanceValue("");
        }
    }
}
