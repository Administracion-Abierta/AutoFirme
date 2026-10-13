package org.apache.pdfbox.contentstream.operator.color;

import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/color/SetNonStrokingColor.class */
public class SetNonStrokingColor extends SetColor {
    @Override // org.apache.pdfbox.contentstream.operator.color.SetColor
    protected PDColor getColor() {
        return this.context.getGraphicsState().getNonStrokingColor();
    }

    @Override // org.apache.pdfbox.contentstream.operator.color.SetColor
    protected void setColor(PDColor color) {
        this.context.getGraphicsState().setNonStrokingColor(color);
    }

    @Override // org.apache.pdfbox.contentstream.operator.color.SetColor
    protected PDColorSpace getColorSpace() {
        return this.context.getGraphicsState().getNonStrokingColorSpace();
    }

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return OperatorName.NON_STROKING_COLOR;
    }
}
