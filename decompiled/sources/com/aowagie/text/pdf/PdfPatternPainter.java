package com.aowagie.text.pdf;

import com.aowagie.text.DocumentException;
import com.aowagie.text.Image;
import com.aowagie.text.Rectangle;
import java.awt.Color;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfPatternPainter.class */
public final class PdfPatternPainter extends PdfTemplate {
    private float xstep;
    private float ystep;
    private boolean stencil;
    private Color defaultColor;

    private PdfPatternPainter() {
        this.stencil = false;
        this.type = 3;
    }

    PdfPatternPainter(PdfWriter wr) {
        super(wr);
        this.stencil = false;
        this.type = 3;
    }

    PdfPatternPainter(PdfWriter wr, Color defaultColor) {
        this(wr);
        this.stencil = true;
        if (defaultColor == null) {
            this.defaultColor = Color.gray;
        } else {
            this.defaultColor = defaultColor;
        }
    }

    public void setXStep(float xstep) {
        this.xstep = xstep;
    }

    public void setYStep(float ystep) {
        this.ystep = ystep;
    }

    public float getXStep() {
        return this.xstep;
    }

    public float getYStep() {
        return this.ystep;
    }

    public boolean isStencil() {
        return this.stencil;
    }

    void setPatternMatrix(float a, float b, float c, float d, float e, float f) {
        setMatrix(a, b, c, d, e, f);
    }

    PdfPattern getPattern(int compressionLevel) {
        return new PdfPattern(this, compressionLevel);
    }

    @Override // com.aowagie.text.pdf.PdfTemplate, com.aowagie.text.pdf.PdfContentByte
    public PdfContentByte getDuplicate() {
        PdfPatternPainter tpl = new PdfPatternPainter();
        tpl.writer = this.writer;
        tpl.pdf = this.pdf;
        tpl.thisReference = this.thisReference;
        tpl.pageResources = this.pageResources;
        tpl.bBox = new Rectangle(this.bBox);
        tpl.xstep = this.xstep;
        tpl.ystep = this.ystep;
        tpl.matrix = this.matrix;
        tpl.stencil = this.stencil;
        tpl.defaultColor = this.defaultColor;
        return tpl;
    }

    public Color getDefaultColor() {
        return this.defaultColor;
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setGrayFill(float gray) {
        checkNoColor();
        super.setGrayFill(gray);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void resetGrayFill() {
        checkNoColor();
        super.resetGrayFill();
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setGrayStroke(float gray) {
        checkNoColor();
        super.setGrayStroke(gray);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void resetGrayStroke() {
        checkNoColor();
        super.resetGrayStroke();
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setRGBColorFillF(float red, float green, float blue) {
        checkNoColor();
        super.setRGBColorFillF(red, green, blue);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void resetRGBColorFill() {
        checkNoColor();
        super.resetRGBColorFill();
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setRGBColorStrokeF(float red, float green, float blue) {
        checkNoColor();
        super.setRGBColorStrokeF(red, green, blue);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void resetRGBColorStroke() {
        checkNoColor();
        super.resetRGBColorStroke();
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setCMYKColorFillF(float cyan, float magenta, float yellow, float black) {
        checkNoColor();
        super.setCMYKColorFillF(cyan, magenta, yellow, black);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void resetCMYKColorFill() {
        checkNoColor();
        super.resetCMYKColorFill();
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setCMYKColorStrokeF(float cyan, float magenta, float yellow, float black) {
        checkNoColor();
        super.setCMYKColorStrokeF(cyan, magenta, yellow, black);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void resetCMYKColorStroke() {
        checkNoColor();
        super.resetCMYKColorStroke();
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void addImage(Image image, float a, float b, float c, float d, float e, float f) throws DocumentException {
        if (this.stencil && !image.isMask()) {
            checkNoColor();
        }
        super.addImage(image, a, b, c, d, e, f);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setCMYKColorFill(int cyan, int magenta, int yellow, int black) {
        checkNoColor();
        super.setCMYKColorFill(cyan, magenta, yellow, black);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setCMYKColorStroke(int cyan, int magenta, int yellow, int black) {
        checkNoColor();
        super.setCMYKColorStroke(cyan, magenta, yellow, black);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setRGBColorFill(int red, int green, int blue) {
        checkNoColor();
        super.setRGBColorFill(red, green, blue);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setRGBColorStroke(int red, int green, int blue) {
        checkNoColor();
        super.setRGBColorStroke(red, green, blue);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setColorStroke(Color color) {
        checkNoColor();
        super.setColorStroke(color);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setColorFill(Color color) {
        checkNoColor();
        super.setColorFill(color);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setColorFill(PdfSpotColor sp, float tint) {
        checkNoColor();
        super.setColorFill(sp, tint);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setColorStroke(PdfSpotColor sp, float tint) {
        checkNoColor();
        super.setColorStroke(sp, tint);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setPatternFill(PdfPatternPainter p) {
        checkNoColor();
        super.setPatternFill(p);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setPatternFill(PdfPatternPainter p, Color color, float tint) {
        checkNoColor();
        super.setPatternFill(p, color, tint);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setPatternStroke(PdfPatternPainter p, Color color, float tint) {
        checkNoColor();
        super.setPatternStroke(p, color, tint);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setPatternStroke(PdfPatternPainter p) {
        checkNoColor();
        super.setPatternStroke(p);
    }

    private void checkNoColor() {
        if (this.stencil) {
            throw new RuntimeException("Colors are not allowed in uncolored tile patterns.");
        }
    }
}
