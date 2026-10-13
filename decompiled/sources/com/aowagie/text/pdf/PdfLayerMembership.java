package com.aowagie.text.pdf;

import java.util.Collection;
import java.util.HashSet;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfLayerMembership.class */
class PdfLayerMembership extends PdfDictionary implements PdfOCG {
    private final PdfIndirectReference ref;
    private final PdfArray members;
    private final HashSet layers;

    public PdfLayerMembership(PdfWriter writer) {
        super(PdfName.OCMD);
        this.members = new PdfArray();
        this.layers = new HashSet();
        put(PdfName.OCGS, this.members);
        this.ref = writer.getPdfIndirectReference();
    }

    @Override // com.aowagie.text.pdf.PdfOCG
    public PdfIndirectReference getRef() {
        return this.ref;
    }

    public Collection getLayers() {
        return this.layers;
    }

    public void setVisibilityPolicy(PdfName type) {
        put(PdfName.P, type);
    }

    @Override // com.aowagie.text.pdf.PdfOCG
    public PdfObject getPdfObject() {
        return this;
    }
}
