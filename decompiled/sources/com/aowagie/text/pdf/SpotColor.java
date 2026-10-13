package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/SpotColor.class */
public class SpotColor extends ExtendedColor {
    private static final long serialVersionUID = -6257004582113248079L;
    private final PdfSpotColor spot;
    private final float tint;

    private SpotColor(PdfSpotColor spot, float tint) {
        super(3, (((spot.getAlternativeCS().getRed() / 255.0f) - 1.0f) * tint) + 1.0f, (((spot.getAlternativeCS().getGreen() / 255.0f) - 1.0f) * tint) + 1.0f, (((spot.getAlternativeCS().getBlue() / 255.0f) - 1.0f) * tint) + 1.0f);
        this.spot = spot;
        this.tint = tint;
    }

    public PdfSpotColor getPdfSpotColor() {
        return this.spot;
    }

    public float getTint() {
        return this.tint;
    }

    public boolean equals(Object obj) {
        return this == obj;
    }

    public int hashCode() {
        return this.spot.hashCode() ^ Float.floatToIntBits(this.tint);
    }
}
