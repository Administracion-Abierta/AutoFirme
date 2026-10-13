package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/form/PDListBox.class */
public final class PDListBox extends PDChoice {
    public PDListBox(PDAcroForm acroForm) {
        super(acroForm);
    }

    PDListBox(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    public int getTopIndex() {
        return getCOSObject().getInt(COSName.TI, 0);
    }

    public void setTopIndex(Integer topIndex) {
        if (topIndex != null) {
            getCOSObject().setInt(COSName.TI, topIndex.intValue());
        } else {
            getCOSObject().removeItem(COSName.TI);
        }
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDChoice, org.apache.pdfbox.pdmodel.interactive.form.PDTerminalField
    void constructAppearances() throws IOException {
        AppearanceGeneratorHelper apHelper = new AppearanceGeneratorHelper(this);
        apHelper.setAppearanceValue("");
    }
}
