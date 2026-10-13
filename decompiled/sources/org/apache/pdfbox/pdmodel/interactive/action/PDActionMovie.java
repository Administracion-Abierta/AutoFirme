package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/action/PDActionMovie.class */
public class PDActionMovie extends PDAction {
    public static final String SUB_TYPE = "Movie";

    public PDActionMovie() {
        setSubType(SUB_TYPE);
    }

    public PDActionMovie(COSDictionary a) {
        super(a);
    }

    @Deprecated
    public String getS() {
        return this.action.getNameAsString(COSName.S);
    }

    @Deprecated
    public void setS(String s) {
        this.action.setName(COSName.S, s);
    }
}
