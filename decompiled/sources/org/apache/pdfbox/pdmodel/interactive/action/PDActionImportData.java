package org.apache.pdfbox.pdmodel.interactive.action;

import java.io.IOException;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/action/PDActionImportData.class */
public class PDActionImportData extends PDAction {
    public static final String SUB_TYPE = "ImportData";

    public PDActionImportData() {
        setSubType(SUB_TYPE);
    }

    public PDActionImportData(COSDictionary a) {
        super(a);
    }

    public PDFileSpecification getFile() throws IOException {
        return PDFileSpecification.createFS(this.action.getDictionaryObject(COSName.F));
    }

    public void setFile(PDFileSpecification fs) {
        this.action.setItem(COSName.F, fs);
    }
}
