package com.aowagie.text.pdf.codec.wmf;

import java.awt.Color;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/wmf/MetaPen.class */
class MetaPen extends MetaObject {
    private static final int PS_SOLID = 0;
    static final int PS_DASH = 1;
    public static final int PS_DOT = 2;
    static final int PS_DASHDOT = 3;
    static final int PS_DASHDOTDOT = 4;
    static final int PS_NULL = 5;
    private int style = 0;
    private int penWidth = 1;
    private Color color = Color.black;

    public MetaPen() {
        this.type = 1;
    }

    public void init(InputMeta in) throws IOException {
        this.style = in.readWord();
        this.penWidth = in.readShort();
        in.readWord();
        this.color = in.readColor();
    }

    public int getStyle() {
        return this.style;
    }

    public int getPenWidth() {
        return this.penWidth;
    }

    public Color getColor() {
        return this.color;
    }
}
