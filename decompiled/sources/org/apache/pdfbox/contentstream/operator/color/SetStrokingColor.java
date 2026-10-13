package org.apache.pdfbox.contentstream.operator.color;

import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/color/SetStrokingColor.class */
public class SetStrokingColor extends SetColor {
    @Override // org.apache.pdfbox.contentstream.operator.color.SetColor
    protected PDColor getColor() {
        return this.context.getGraphicsState().getStrokingColor();
    }

    @Override // org.apache.pdfbox.contentstream.operator.color.SetColor
    protected void setColor(PDColor color) {
        this.context.getGraphicsState().setStrokingColor(color);
    }

    @Override // org.apache.pdfbox.contentstream.operator.color.SetColor
    protected PDColorSpace getColorSpace() {
        return this.context.getGraphicsState().getStrokingColorSpace();
    }

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return "SC";
    }
}
