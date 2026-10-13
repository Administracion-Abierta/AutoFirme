package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/CMYKColor.class */
class CMYKColor extends ExtendedColor {
    private static final long serialVersionUID = 5940378778276468452L;
    private final float cyan;
    private final float magenta;
    private final float yellow;
    private final float black;

    CMYKColor(float floatCyan, float floatMagenta, float floatYellow, float floatBlack) {
        super(2, (1.0f - floatCyan) - floatBlack, (1.0f - floatMagenta) - floatBlack, (1.0f - floatYellow) - floatBlack);
        this.cyan = normalize(floatCyan);
        this.magenta = normalize(floatMagenta);
        this.yellow = normalize(floatYellow);
        this.black = normalize(floatBlack);
    }

    public float getCyan() {
        return this.cyan;
    }

    public float getMagenta() {
        return this.magenta;
    }

    public float getYellow() {
        return this.yellow;
    }

    public float getBlack() {
        return this.black;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof CMYKColor)) {
            return false;
        }
        CMYKColor c2 = (CMYKColor) obj;
        return this.cyan == c2.cyan && this.magenta == c2.magenta && this.yellow == c2.yellow && this.black == c2.black;
    }

    public int hashCode() {
        return ((Float.floatToIntBits(this.cyan) ^ Float.floatToIntBits(this.magenta)) ^ Float.floatToIntBits(this.yellow)) ^ Float.floatToIntBits(this.black);
    }
}
