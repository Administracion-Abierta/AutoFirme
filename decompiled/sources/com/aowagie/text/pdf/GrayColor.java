package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/GrayColor.class */
public class GrayColor extends ExtendedColor {
    private static final long serialVersionUID = -6571835680819282746L;
    private final float gray;
    static final GrayColor GRAYBLACK = new GrayColor(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    static final GrayColor GRAYWHITE = new GrayColor(1.0f);

    public GrayColor(float floatGray) {
        super(1, floatGray, floatGray, floatGray);
        this.gray = normalize(floatGray);
    }

    public float getGray() {
        return this.gray;
    }

    public boolean equals(Object obj) {
        return (obj instanceof GrayColor) && ((GrayColor) obj).gray == this.gray;
    }

    public int hashCode() {
        return Float.floatToIntBits(this.gray);
    }
}
