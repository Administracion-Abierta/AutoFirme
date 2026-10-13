package com.aowagie.text.pdf.draw;

import com.aowagie.text.Chunk;
import com.aowagie.text.DocumentException;
import com.aowagie.text.Element;
import com.aowagie.text.ElementListener;
import com.aowagie.text.pdf.ColumnText;
import com.aowagie.text.pdf.PdfContentByte;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/draw/VerticalPositionMark.class */
public class VerticalPositionMark implements DrawInterface, Element {
    protected DrawInterface drawInterface;
    protected float offset;

    public VerticalPositionMark() {
        this.drawInterface = null;
        this.offset = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    }

    public VerticalPositionMark(DrawInterface drawInterface, float offset) {
        this.drawInterface = null;
        this.offset = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.drawInterface = drawInterface;
        this.offset = offset;
    }

    @Override // com.aowagie.text.pdf.draw.DrawInterface
    public void draw(PdfContentByte canvas, float llx, float lly, float urx, float ury, float y) {
        if (this.drawInterface != null) {
            this.drawInterface.draw(canvas, llx, lly, urx, ury, y + this.offset);
        }
    }

    @Override // com.aowagie.text.Element
    public boolean process(ElementListener listener) {
        try {
            return listener.add(this);
        } catch (DocumentException e) {
            return false;
        }
    }

    @Override // com.aowagie.text.Element
    public int type() {
        return 55;
    }

    @Override // com.aowagie.text.Element
    public boolean isContent() {
        return true;
    }

    @Override // com.aowagie.text.Element
    public boolean isNestable() {
        return false;
    }

    @Override // com.aowagie.text.Element
    public ArrayList getChunks() {
        ArrayList list = new ArrayList();
        list.add(new Chunk((DrawInterface) this, true));
        return list;
    }

    public DrawInterface getDrawInterface() {
        return this.drawInterface;
    }

    public void setDrawInterface(DrawInterface drawInterface) {
        this.drawInterface = drawInterface;
    }

    public float getOffset() {
        return this.offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }
}
