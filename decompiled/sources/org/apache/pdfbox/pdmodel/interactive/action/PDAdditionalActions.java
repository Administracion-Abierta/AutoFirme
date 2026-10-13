package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/action/PDAdditionalActions.class */
public class PDAdditionalActions implements COSObjectable {
    private final COSDictionary actions;

    public PDAdditionalActions() {
        this.actions = new COSDictionary();
    }

    public PDAdditionalActions(COSDictionary a) {
        this.actions = a;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.actions;
    }

    public PDAction getF() {
        return PDActionFactory.createAction((COSDictionary) this.actions.getDictionaryObject(COSName.F));
    }

    public void setF(PDAction action) {
        this.actions.setItem(COSName.F, action);
    }
}
