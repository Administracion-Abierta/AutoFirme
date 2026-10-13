package com.aowagie.text.pdf;

import com.aowagie.text.Rectangle;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfTemplate.class */
public class PdfTemplate extends PdfContentByte {
    static final int TYPE_TEMPLATE = 1;
    static final int TYPE_IMPORTED = 2;
    public static final int TYPE_PATTERN = 3;
    protected int type;
    protected PdfIndirectReference thisReference;
    protected PageResources pageResources;
    protected Rectangle bBox;
    protected PdfArray matrix;
    protected PdfTransparencyGroup group;
    protected PdfOCG layer;

    protected PdfTemplate() {
        super(null);
        this.bBox = new Rectangle(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        this.type = 1;
    }

    PdfTemplate(PdfWriter wr) {
        super(wr);
        this.bBox = new Rectangle(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        this.type = 1;
        this.pageResources = new PageResources();
        this.pageResources.addDefaultColor(wr.getDefaultColorspace());
        this.thisReference = this.writer.getPdfIndirectReference();
    }

    public static PdfTemplate createTemplate(PdfWriter writer, float width, float height) {
        return createTemplate(writer, width, height, null);
    }

    private static PdfTemplate createTemplate(PdfWriter writer, float width, float height, PdfName forcedName) {
        PdfTemplate template = new PdfTemplate(writer);
        template.setWidth(width);
        template.setHeight(height);
        writer.addDirectTemplateSimple(template, forcedName);
        return template;
    }

    public void setWidth(float width) {
        this.bBox.setLeft(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        this.bBox.setRight(width);
    }

    public void setHeight(float height) {
        this.bBox.setBottom(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        this.bBox.setTop(height);
    }

    public float getWidth() {
        return this.bBox.getWidth();
    }

    public float getHeight() {
        return this.bBox.getHeight();
    }

    public Rectangle getBoundingBox() {
        return this.bBox;
    }

    public void setBoundingBox(Rectangle bBox) {
        this.bBox = bBox;
    }

    public void setLayer(PdfOCG layer) {
        this.layer = layer;
    }

    public PdfOCG getLayer() {
        return this.layer;
    }

    void setMatrix(float a, float b, float c, float d, float e, float f) {
        this.matrix = new PdfArray();
        this.matrix.add(new PdfNumber(a));
        this.matrix.add(new PdfNumber(b));
        this.matrix.add(new PdfNumber(c));
        this.matrix.add(new PdfNumber(d));
        this.matrix.add(new PdfNumber(e));
        this.matrix.add(new PdfNumber(f));
    }

    PdfArray getMatrix() {
        return this.matrix;
    }

    public PdfIndirectReference getIndirectReference() {
        if (this.thisReference == null) {
            this.thisReference = this.writer.getPdfIndirectReference();
        }
        return this.thisReference;
    }

    void beginVariableText() {
        this.content.append("/Tx BMC ");
    }

    void endVariableText() {
        this.content.append("EMC ");
    }

    PdfObject getResources() {
        return getPageResources().getResources();
    }

    PdfStream getFormXObject(int compressionLevel) throws IOException {
        return new PdfFormXObject(this, compressionLevel);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public PdfContentByte getDuplicate() {
        PdfTemplate tpl = new PdfTemplate();
        tpl.writer = this.writer;
        tpl.pdf = this.pdf;
        tpl.thisReference = this.thisReference;
        tpl.pageResources = this.pageResources;
        tpl.bBox = new Rectangle(this.bBox);
        tpl.group = this.group;
        tpl.layer = this.layer;
        if (this.matrix != null) {
            tpl.matrix = new PdfArray(this.matrix);
        }
        tpl.separator = this.separator;
        return tpl;
    }

    public int getType() {
        return this.type;
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    PageResources getPageResources() {
        return this.pageResources;
    }

    public PdfTransparencyGroup getGroup() {
        return this.group;
    }

    public void setGroup(PdfTransparencyGroup group) {
        this.group = group;
    }
}
