package com.aowagie.text.pdf;

import java.awt.Color;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/ExtendedColor.class */
public abstract class ExtendedColor extends Color {
    private static final long serialVersionUID = 2722660170712380080L;
    public static final int TYPE_RGB = 0;
    public static final int TYPE_GRAY = 1;
    public static final int TYPE_CMYK = 2;
    public static final int TYPE_SEPARATION = 3;
    public static final int TYPE_PATTERN = 4;
    public static final int TYPE_SHADING = 5;
    protected int type;

    ExtendedColor(int type, float red, float green, float blue) {
        super(normalize(red), normalize(green), normalize(blue));
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    static int getType(Color color) {
        if (color instanceof ExtendedColor) {
            return ((ExtendedColor) color).getType();
        }
        return 0;
    }

    static final float normalize(float value) {
        if (value < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        if (value > 1.0f) {
            return 1.0f;
        }
        return value;
    }
}
