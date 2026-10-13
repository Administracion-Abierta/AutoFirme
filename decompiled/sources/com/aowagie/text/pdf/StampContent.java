package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/StampContent.class */
class StampContent extends PdfContentByte {
    private final PdfStamperImp.PageStamp ps;
    private final PageResources pageResources;

    StampContent(PdfStamperImp stamper, PdfStamperImp.PageStamp ps) {
        super(stamper);
        this.ps = ps;
        this.pageResources = ps.pageResources;
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public void setAction(PdfAction action, float llx, float lly, float urx, float ury) {
        ((PdfStamperImp) this.writer).addAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, action), this.ps.pageN);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    public PdfContentByte getDuplicate() {
        return new StampContent((PdfStamperImp) this.writer, this.ps);
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    PageResources getPageResources() {
        return this.pageResources;
    }

    @Override // com.aowagie.text.pdf.PdfContentByte
    void addAnnotation(PdfAnnotation annot) {
        ((PdfStamperImp) this.writer).addAnnotation(annot, this.ps.pageN);
    }
}
