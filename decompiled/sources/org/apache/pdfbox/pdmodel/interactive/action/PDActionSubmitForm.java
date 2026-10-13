package org.apache.pdfbox.pdmodel.interactive.action;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/action/PDActionSubmitForm.class */
public class PDActionSubmitForm extends PDAction {
    public static final String SUB_TYPE = "SubmitForm";

    public PDActionSubmitForm() {
        setSubType(SUB_TYPE);
    }

    public PDActionSubmitForm(COSDictionary a) {
        super(a);
    }

    public PDFileSpecification getFile() throws IOException {
        return PDFileSpecification.createFS(this.action.getDictionaryObject(COSName.F));
    }

    public void setFile(PDFileSpecification fs) {
        this.action.setItem(COSName.F, fs);
    }

    public COSArray getFields() {
        COSBase retval = this.action.getDictionaryObject(COSName.FIELDS);
        if (retval instanceof COSArray) {
            return (COSArray) retval;
        }
        return null;
    }

    public void setFields(COSArray array) {
        this.action.setItem(COSName.FIELDS, (COSBase) array);
    }

    public int getFlags() {
        return this.action.getInt(COSName.FLAGS, 0);
    }

    public void setFlags(int flags) {
        this.action.setInt(COSName.FLAGS, flags);
    }
}
