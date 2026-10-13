package org.apache.pdfbox.pdmodel.font;

import org.apache.fontbox.FontBoxFont;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/FontMapping.class */
public class FontMapping<T extends FontBoxFont> {
    private final T font;
    private final boolean isFallback;

    public FontMapping(T font, boolean isFallback) {
        this.font = font;
        this.isFallback = isFallback;
    }

    public T getFont() {
        return this.font;
    }

    public boolean isFallback() {
        return this.isFallback;
    }
}
