package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/action/PDActionJavaScript.class */
public class PDActionJavaScript extends PDAction {
    public static final String SUB_TYPE = "JavaScript";

    public PDActionJavaScript() {
        setSubType(SUB_TYPE);
    }

    public PDActionJavaScript(String js) {
        this();
        setAction(js);
    }

    public PDActionJavaScript(COSDictionary a) {
        super(a);
    }

    public final void setAction(String sAction) {
        this.action.setString(COSName.JS, sAction);
    }

    public String getAction() {
        COSBase base = this.action.getDictionaryObject(COSName.JS);
        if (base instanceof COSString) {
            return ((COSString) base).getString();
        }
        if (base instanceof COSStream) {
            return ((COSStream) base).toTextString();
        }
        return null;
    }
}
