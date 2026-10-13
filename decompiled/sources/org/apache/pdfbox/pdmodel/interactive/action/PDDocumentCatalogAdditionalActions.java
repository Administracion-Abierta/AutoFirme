package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/action/PDDocumentCatalogAdditionalActions.class */
public class PDDocumentCatalogAdditionalActions implements COSObjectable {
    private final COSDictionary actions;

    public PDDocumentCatalogAdditionalActions() {
        this.actions = new COSDictionary();
    }

    public PDDocumentCatalogAdditionalActions(COSDictionary a) {
        this.actions = a;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.actions;
    }

    public PDAction getWC() {
        COSDictionary wc = (COSDictionary) this.actions.getDictionaryObject("WC");
        PDAction retval = null;
        if (wc != null) {
            retval = PDActionFactory.createAction(wc);
        }
        return retval;
    }

    public void setWC(PDAction wc) {
        this.actions.setItem("WC", wc);
    }

    public PDAction getWS() {
        COSDictionary ws = (COSDictionary) this.actions.getDictionaryObject("WS");
        PDAction retval = null;
        if (ws != null) {
            retval = PDActionFactory.createAction(ws);
        }
        return retval;
    }

    public void setWS(PDAction ws) {
        this.actions.setItem("WS", ws);
    }

    public PDAction getDS() {
        COSDictionary ds = (COSDictionary) this.actions.getDictionaryObject("DS");
        PDAction retval = null;
        if (ds != null) {
            retval = PDActionFactory.createAction(ds);
        }
        return retval;
    }

    public void setDS(PDAction ds) {
        this.actions.setItem("DS", ds);
    }

    public PDAction getWP() {
        COSDictionary wp = (COSDictionary) this.actions.getDictionaryObject(StandardStructureTypes.WP);
        PDAction retval = null;
        if (wp != null) {
            retval = PDActionFactory.createAction(wp);
        }
        return retval;
    }

    public void setWP(PDAction wp) {
        this.actions.setItem(StandardStructureTypes.WP, wp);
    }

    public PDAction getDP() {
        COSDictionary dp = (COSDictionary) this.actions.getDictionaryObject(OperatorName.MARKED_CONTENT_POINT_WITH_PROPS);
        PDAction retval = null;
        if (dp != null) {
            retval = PDActionFactory.createAction(dp);
        }
        return retval;
    }

    public void setDP(PDAction dp) {
        this.actions.setItem(OperatorName.MARKED_CONTENT_POINT_WITH_PROPS, dp);
    }
}
