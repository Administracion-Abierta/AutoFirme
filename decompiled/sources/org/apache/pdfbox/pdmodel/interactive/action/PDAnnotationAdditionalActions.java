package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/action/PDAnnotationAdditionalActions.class */
public class PDAnnotationAdditionalActions implements COSObjectable {
    private final COSDictionary actions;

    public PDAnnotationAdditionalActions() {
        this.actions = new COSDictionary();
    }

    public PDAnnotationAdditionalActions(COSDictionary a) {
        this.actions = a;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.actions;
    }

    public PDAction getE() {
        COSDictionary e = (COSDictionary) this.actions.getDictionaryObject("E");
        PDAction retval = null;
        if (e != null) {
            retval = PDActionFactory.createAction(e);
        }
        return retval;
    }

    public void setE(PDAction e) {
        this.actions.setItem("E", e);
    }

    public PDAction getX() {
        COSDictionary x = (COSDictionary) this.actions.getDictionaryObject("X");
        PDAction retval = null;
        if (x != null) {
            retval = PDActionFactory.createAction(x);
        }
        return retval;
    }

    public void setX(PDAction x) {
        this.actions.setItem("X", x);
    }

    public PDAction getD() {
        COSDictionary d = (COSDictionary) this.actions.getDictionaryObject(COSName.D);
        PDAction retval = null;
        if (d != null) {
            retval = PDActionFactory.createAction(d);
        }
        return retval;
    }

    public void setD(PDAction d) {
        this.actions.setItem(COSName.D, d);
    }

    public PDAction getU() {
        COSDictionary u = (COSDictionary) this.actions.getDictionaryObject(PDBorderStyleDictionary.STYLE_UNDERLINE);
        PDAction retval = null;
        if (u != null) {
            retval = PDActionFactory.createAction(u);
        }
        return retval;
    }

    public void setU(PDAction u) {
        this.actions.setItem(PDBorderStyleDictionary.STYLE_UNDERLINE, u);
    }

    public PDAction getFo() {
        COSDictionary fo = (COSDictionary) this.actions.getDictionaryObject("Fo");
        PDAction retval = null;
        if (fo != null) {
            retval = PDActionFactory.createAction(fo);
        }
        return retval;
    }

    public void setFo(PDAction fo) {
        this.actions.setItem("Fo", fo);
    }

    public PDAction getBl() {
        COSDictionary bl = (COSDictionary) this.actions.getDictionaryObject("Bl");
        PDAction retval = null;
        if (bl != null) {
            retval = PDActionFactory.createAction(bl);
        }
        return retval;
    }

    public void setBl(PDAction bl) {
        this.actions.setItem("Bl", bl);
    }

    public PDAction getPO() {
        COSDictionary po = (COSDictionary) this.actions.getDictionaryObject("PO");
        PDAction retval = null;
        if (po != null) {
            retval = PDActionFactory.createAction(po);
        }
        return retval;
    }

    public void setPO(PDAction po) {
        this.actions.setItem("PO", po);
    }

    public PDAction getPC() {
        COSDictionary pc = (COSDictionary) this.actions.getDictionaryObject("PC");
        PDAction retval = null;
        if (pc != null) {
            retval = PDActionFactory.createAction(pc);
        }
        return retval;
    }

    public void setPC(PDAction pc) {
        this.actions.setItem("PC", pc);
    }

    public PDAction getPV() {
        COSDictionary pv = (COSDictionary) this.actions.getDictionaryObject("PV");
        PDAction retval = null;
        if (pv != null) {
            retval = PDActionFactory.createAction(pv);
        }
        return retval;
    }

    public void setPV(PDAction pv) {
        this.actions.setItem("PV", pv);
    }

    public PDAction getPI() {
        COSDictionary pi = (COSDictionary) this.actions.getDictionaryObject("PI");
        PDAction retval = null;
        if (pi != null) {
            retval = PDActionFactory.createAction(pi);
        }
        return retval;
    }

    public void setPI(PDAction pi) {
        this.actions.setItem("PI", pi);
    }
}
