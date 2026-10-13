package com.aowagie.text.pdf;

import com.aowagie.text.DocumentException;
import com.aowagie.text.Image;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/Type3Glyph.class */
final class Type3Glyph extends PdfContentByte {
    private PageResources pageResources;
    private boolean colorized;

    private Type3Glyph() {
        super(null);
    }

    Type3Glyph(PdfWriter writer, PageResources pageResources, float wx, float llx, float lly, float urx, float ury, boolean colorized) {
        super(writer);
        this.pageResources = pageResources;
        this.colorized = colorized;
        if (colorized) {
            this.content.append(wx).append(" 0 d0\n");
        } else {
            this.content.append(wx).append(" 0 ").append(llx).append(' ').append(lly).append(' ').append(urx).append(' ').append(ury).append(" d1\n");
        }
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    PageResources getPageResources() {
        return this.pageResources;
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void addImage(Image image, float a, float b, float c, float d, float e, float f, boolean inlineImage) throws DocumentException {
        if (!this.colorized && (!image.isMask() || (image.getBpc() != 1 && image.getBpc() <= 255))) {
            throw new DocumentException("Not colorized Typed3 fonts only accept mask images.");
        }
        super.addImage(image, a, b, c, d, e, f, inlineImage);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public PdfContentByte getDuplicate() {
        Type3Glyph dup = new Type3Glyph();
        dup.writer = this.writer;
        dup.pdf = this.pdf;
        dup.pageResources = this.pageResources;
        dup.colorized = this.colorized;
        return dup;
    }
}
