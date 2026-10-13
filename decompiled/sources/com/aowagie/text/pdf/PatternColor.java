package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PatternColor.class */
public class PatternColor extends ExtendedColor {
    private static final long serialVersionUID = -1185448552860615964L;
    private final PdfPatternPainter painter;

    public PatternColor(PdfPatternPainter painter) {
        super(4, 0.5f, 0.5f, 0.5f);
        this.painter = painter;
    }

    public PdfPatternPainter getPainter() {
        return this.painter;
    }

    public boolean equals(Object obj) {
        return this == obj;
    }

    public int hashCode() {
        return this.painter.hashCode();
    }
}
