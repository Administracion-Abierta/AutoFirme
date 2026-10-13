package com.aowagie.text;

import com.aowagie.text.pdf.ColumnText;
import java.awt.Color;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/RectangleReadOnly.class */
class RectangleReadOnly extends Rectangle {
    public RectangleReadOnly(float urx, float ury) {
        super(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, urx, ury);
    }

    private void throwReadOnlyError() {
        throw new UnsupportedOperationException("RectangleReadOnly: this Rectangle is read only.");
    }

    @Override // com.aowagie.text.Rectangle
    public void setLeft(float llx) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void setRight(float urx) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void setTop(float ury) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void setBottom(float lly) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void normalize() {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void setBackgroundColor(Color value) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void setGrayFill(float value) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void setBorder(int border) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void setUseVariableBorders(boolean useVariableBorders) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void enableBorderSide(int side) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void disableBorderSide(int side) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void setBorderWidth(float borderWidth) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void setBorderWidthLeft(float borderWidthLeft) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void setBorderWidthRight(float borderWidthRight) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void setBorderWidthTop(float borderWidthTop) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void setBorderWidthBottom(float borderWidthBottom) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void setBorderColor(Color borderColor) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void setBorderColorLeft(Color borderColorLeft) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void setBorderColorRight(Color borderColorRight) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void setBorderColorTop(Color borderColorTop) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void setBorderColorBottom(Color borderColorBottom) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void cloneNonPositionParameters(Rectangle rect) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle
    public void softCloneNonPositionParameters(Rectangle rect) {
        throwReadOnlyError();
    }

    @Override // com.aowagie.text.Rectangle, com.aowagie.text.Element
    public String toString() {
        StringBuffer buf = new StringBuffer("RectangleReadOnly: ");
        buf.append(getWidth());
        buf.append('x');
        buf.append(getHeight());
        buf.append(" (rot: ");
        buf.append(this.rotation);
        buf.append(" degrees)");
        return buf.toString();
    }
}
