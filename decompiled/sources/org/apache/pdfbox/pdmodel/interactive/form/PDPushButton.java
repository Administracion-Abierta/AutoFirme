package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/form/PDPushButton.class */
public class PDPushButton extends PDButton {
    public PDPushButton(PDAcroForm acroForm) {
        super(acroForm);
        getCOSObject().setFlag(COSName.FF, 65536, true);
    }

    PDPushButton(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDButton
    public List<String> getExportValues() {
        return Collections.emptyList();
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDButton
    public void setExportValues(List<String> values) {
        if (values != null && !values.isEmpty()) {
            throw new IllegalArgumentException("A PDPushButton shall not use the Opt entry in the field dictionary");
        }
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDButton
    public String getValue() {
        return "";
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDButton
    public String getDefaultValue() {
        return "";
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDButton, org.apache.pdfbox.pdmodel.interactive.form.PDField
    public String getValueAsString() {
        return getValue();
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDButton
    public Set<String> getOnValues() {
        return Collections.emptySet();
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.form.PDButton, org.apache.pdfbox.pdmodel.interactive.form.PDTerminalField
    void constructAppearances() throws IOException {
    }
}
