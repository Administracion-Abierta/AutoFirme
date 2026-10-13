package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSDictionary;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/action/PDActionNamed.class */
public class PDActionNamed extends PDAction {
    public static final String SUB_TYPE = "Named";

    public PDActionNamed() {
        setSubType(SUB_TYPE);
    }

    public PDActionNamed(COSDictionary a) {
        super(a);
    }

    public String getN() {
        return this.action.getNameAsString("N");
    }

    public void setN(String name) {
        this.action.setName("N", name);
    }
}
