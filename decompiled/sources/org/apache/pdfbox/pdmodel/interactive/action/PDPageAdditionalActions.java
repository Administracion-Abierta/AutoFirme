package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/action/PDPageAdditionalActions.class */
public class PDPageAdditionalActions implements COSObjectable {
    private final COSDictionary actions;

    public PDPageAdditionalActions() {
        this.actions = new COSDictionary();
    }

    public PDPageAdditionalActions(COSDictionary a) {
        this.actions = a;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.actions;
    }

    public PDAction getO() {
        COSDictionary o = (COSDictionary) this.actions.getDictionaryObject(COSName.O);
        PDAction retval = null;
        if (o != null) {
            retval = PDActionFactory.createAction(o);
        }
        return retval;
    }

    public void setO(PDAction o) {
        this.actions.setItem(COSName.O, o);
    }

    public PDAction getC() {
        COSDictionary c = (COSDictionary) this.actions.getDictionaryObject("C");
        PDAction retval = null;
        if (c != null) {
            retval = PDActionFactory.createAction(c);
        }
        return retval;
    }

    public void setC(PDAction c) {
        this.actions.setItem("C", c);
    }
}
