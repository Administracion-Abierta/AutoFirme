package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfStructureElement.class */
class PdfStructureElement extends PdfDictionary {
    private final PdfStructureElement parent;
    private final PdfStructureTreeRoot top;
    private PdfIndirectReference reference;

    public PdfStructureElement(PdfStructureElement parent, PdfName structureType) {
        this.top = parent.top;
        init(parent, structureType);
        this.parent = parent;
        put(PdfName.P, parent.reference);
    }

    private void init(PdfDictionary parent, PdfName structureType) {
        PdfArray kids;
        PdfObject kido = parent.get(PdfName.K);
        if (kido != null && !kido.isArray()) {
            throw new IllegalArgumentException("The parent has already another function.");
        }
        if (kido == null) {
            kids = new PdfArray();
            parent.put(PdfName.K, kids);
        } else {
            kids = (PdfArray) kido;
        }
        kids.add(this);
        put(PdfName.S, structureType);
        this.reference = this.top.getWriter().getPdfIndirectReference();
    }

    public PdfDictionary getParent() {
        return this.parent;
    }

    public PdfIndirectReference getReference() {
        return this.reference;
    }
}
