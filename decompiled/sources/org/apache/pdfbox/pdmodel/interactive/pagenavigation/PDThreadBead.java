package org.apache.pdfbox.pdmodel.interactive.pagenavigation;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.measurement.PDNumberFormatDictionary;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/pagenavigation/PDThreadBead.class */
public class PDThreadBead implements COSObjectable {
    private final COSDictionary bead;

    public PDThreadBead(COSDictionary b) {
        this.bead = b;
    }

    public PDThreadBead() {
        this.bead = new COSDictionary();
        this.bead.setName("Type", "Bead");
        setNextBead(this);
        setPreviousBead(this);
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.bead;
    }

    public PDThread getThread() {
        PDThread retval = null;
        COSDictionary dic = (COSDictionary) this.bead.getDictionaryObject(PDNumberFormatDictionary.FRACTIONAL_DISPLAY_TRUNCATE);
        if (dic != null) {
            retval = new PDThread(dic);
        }
        return retval;
    }

    public void setThread(PDThread thread) {
        this.bead.setItem(PDNumberFormatDictionary.FRACTIONAL_DISPLAY_TRUNCATE, thread);
    }

    public PDThreadBead getNextBead() {
        return new PDThreadBead((COSDictionary) this.bead.getDictionaryObject("N"));
    }

    protected final void setNextBead(PDThreadBead next) {
        this.bead.setItem("N", next);
    }

    public PDThreadBead getPreviousBead() {
        return new PDThreadBead((COSDictionary) this.bead.getDictionaryObject("V"));
    }

    protected final void setPreviousBead(PDThreadBead previous) {
        this.bead.setItem("V", previous);
    }

    public void appendBead(PDThreadBead append) {
        PDThreadBead nextBead = getNextBead();
        nextBead.setPreviousBead(append);
        append.setNextBead(nextBead);
        setNextBead(append);
        append.setPreviousBead(this);
    }

    public PDPage getPage() {
        PDPage page = null;
        COSDictionary dic = (COSDictionary) this.bead.getDictionaryObject("P");
        if (dic != null) {
            page = new PDPage(dic);
        }
        return page;
    }

    public void setPage(PDPage page) {
        this.bead.setItem("P", page);
    }

    public PDRectangle getRectangle() {
        PDRectangle rect = null;
        COSArray array = (COSArray) this.bead.getDictionaryObject(COSName.R);
        if (array != null) {
            rect = new PDRectangle(array);
        }
        return rect;
    }

    public void setRectangle(PDRectangle rect) {
        this.bead.setItem(COSName.R, rect);
    }
}
