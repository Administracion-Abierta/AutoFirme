package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/action/PDActionResetForm.class */
public class PDActionResetForm extends PDAction {
    public static final String SUB_TYPE = "ResetForm";

    public PDActionResetForm() {
        setSubType(SUB_TYPE);
    }

    public PDActionResetForm(COSDictionary a) {
        super(a);
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
