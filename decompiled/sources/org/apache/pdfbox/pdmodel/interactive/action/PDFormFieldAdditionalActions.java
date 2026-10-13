package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/action/PDFormFieldAdditionalActions.class */
public class PDFormFieldAdditionalActions implements COSObjectable {
    private final COSDictionary actions;

    public PDFormFieldAdditionalActions() {
        this.actions = new COSDictionary();
    }

    public PDFormFieldAdditionalActions(COSDictionary a) {
        this.actions = a;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.actions;
    }

    public PDAction getK() {
        COSDictionary k = (COSDictionary) this.actions.getDictionaryObject(COSName.K);
        PDAction retval = null;
        if (k != null) {
            retval = PDActionFactory.createAction(k);
        }
        return retval;
    }

    public void setK(PDAction k) {
        this.actions.setItem(COSName.K, k);
    }

    public PDAction getF() {
        COSDictionary f = (COSDictionary) this.actions.getDictionaryObject(COSName.F);
        PDAction retval = null;
        if (f != null) {
            retval = PDActionFactory.createAction(f);
        }
        return retval;
    }

    public void setF(PDAction f) {
        this.actions.setItem(COSName.F, f);
    }

    public PDAction getV() {
        COSDictionary v = (COSDictionary) this.actions.getDictionaryObject(COSName.V);
        PDAction retval = null;
        if (v != null) {
            retval = PDActionFactory.createAction(v);
        }
        return retval;
    }

    public void setV(PDAction v) {
        this.actions.setItem(COSName.V, v);
    }

    public PDAction getC() {
        COSDictionary c = (COSDictionary) this.actions.getDictionaryObject(COSName.C);
        PDAction retval = null;
        if (c != null) {
            retval = PDActionFactory.createAction(c);
        }
        return retval;
    }

    public void setC(PDAction c) {
        this.actions.setItem(COSName.C, c);
    }
}
