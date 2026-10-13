package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfIndirectReference.class */
public class PdfIndirectReference extends PdfObject {
    protected int number;
    protected int generation;

    protected PdfIndirectReference() {
        super(0);
        this.generation = 0;
    }

    PdfIndirectReference(int type, int number, int generation) {
        super(0, new StringBuffer().append(number).append(" ").append(generation).append(" R").toString());
        this.generation = 0;
        this.number = number;
        this.generation = generation;
    }

    PdfIndirectReference(int type, int number) {
        this(type, number, 0);
    }

    public int getNumber() {
        return this.number;
    }

    public int getGeneration() {
        return this.generation;
    }

    @Override // com.aowagie.text.pdf.PdfObject
    public String toString() {
        return new StringBuffer().append(this.number).append(" ").append(this.generation).append(" R").toString();
    }
}
