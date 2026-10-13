package com.aowagie.text.pdf.codec.wmf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/wmf/MetaObject.class */
class MetaObject {
    private static final int META_NOT_SUPPORTED = 0;
    static final int META_PEN = 1;
    static final int META_BRUSH = 2;
    static final int META_FONT = 3;
    int type = 0;

    public int getType() {
        return this.type;
    }
}
