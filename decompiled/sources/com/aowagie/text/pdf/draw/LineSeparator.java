package com.aowagie.text.pdf.draw;

import com.aowagie.text.pdf.ColumnText;
import com.aowagie.text.pdf.PdfContentByte;
import java.awt.Color;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/draw/LineSeparator.class */
public class LineSeparator extends VerticalPositionMark {
    protected float lineWidth;
    private float percentage;
    private Color lineColor;
    private int alignment;

    public LineSeparator(float lineWidth, float percentage, Color lineColor, int align, float offset) {
        this.lineWidth = 1.0f;
        this.percentage = 100.0f;
        this.alignment = 1;
        this.lineWidth = lineWidth;
        this.percentage = percentage;
        this.lineColor = lineColor;
        this.alignment = align;
        this.offset = offset;
    }

    public LineSeparator() {
        this.lineWidth = 1.0f;
        this.percentage = 100.0f;
        this.alignment = 1;
    }

    @Override // com.aowagie.text.pdf.draw.VerticalPositionMark, com.aowagie.text.pdf.draw.DrawInterface
    public void draw(PdfContentByte canvas, float llx, float lly, float urx, float ury, float y) {
        canvas.saveState();
        drawLine(canvas, llx, urx, y);
        canvas.restoreState();
    }

    void drawLine(PdfContentByte canvas, float leftX, float rightX, float y) {
        float w;
        float s;
        if (getPercentage() < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            w = -getPercentage();
        } else {
            w = ((rightX - leftX) * getPercentage()) / 100.0f;
        }
        switch (getAlignment()) {
            case 0:
                s = 0.0f;
                break;
            case 2:
                s = (rightX - leftX) - w;
                break;
            default:
                s = ((rightX - leftX) - w) / 2.0f;
                break;
        }
        canvas.setLineWidth(getLineWidth());
        if (getLineColor() != null) {
            canvas.setColorStroke(getLineColor());
        }
        canvas.moveTo(s + leftX, y + this.offset);
        canvas.lineTo(s + w + leftX, y + this.offset);
        canvas.stroke();
    }

    public float getLineWidth() {
        return this.lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public float getPercentage() {
        return this.percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public Color getLineColor() {
        return this.lineColor;
    }

    public void setLineColor(Color color) {
        this.lineColor = color;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public void setAlignment(int align) {
        this.alignment = align;
    }
}
