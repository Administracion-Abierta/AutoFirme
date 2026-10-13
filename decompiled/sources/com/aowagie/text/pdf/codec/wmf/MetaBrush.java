package com.aowagie.text.pdf.codec.wmf;

import java.awt.Color;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/wmf/MetaBrush.class */
class MetaBrush extends MetaObject {
    static final int BS_SOLID = 0;
    static final int BS_HATCHED = 2;
    private int hatch;
    private int style = 0;
    private Color color = Color.white;

    public MetaBrush() {
        this.type = 2;
    }

    public void init(InputMeta in) throws IOException {
        this.style = in.readWord();
        this.color = in.readColor();
        this.hatch = in.readWord();
    }

    public int getStyle() {
        return this.style;
    }

    public int getHatch() {
        return this.hatch;
    }

    public Color getColor() {
        return this.color;
    }
}
