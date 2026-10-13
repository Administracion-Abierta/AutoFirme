package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/ShadingColor.class */
public class ShadingColor extends ExtendedColor {
    private static final long serialVersionUID = 4817929454941328671L;
    private final PdfShadingPattern shadingPattern;

    public ShadingColor(PdfShadingPattern shadingPattern) {
        super(5, 0.5f, 0.5f, 0.5f);
        this.shadingPattern = shadingPattern;
    }

    public PdfShadingPattern getPdfShadingPattern() {
        return this.shadingPattern;
    }

    public boolean equals(Object obj) {
        return this == obj;
    }

    public int hashCode() {
        return this.shadingPattern.hashCode();
    }
}
