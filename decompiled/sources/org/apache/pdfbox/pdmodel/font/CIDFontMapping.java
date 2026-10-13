package org.apache.pdfbox.pdmodel.font;

import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.ttf.OpenTypeFont;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/CIDFontMapping.class */
public final class CIDFontMapping extends FontMapping<OpenTypeFont> {
    private final FontBoxFont ttf;

    public CIDFontMapping(OpenTypeFont font, FontBoxFont fontBoxFont, boolean isFallback) {
        super(font, isFallback);
        this.ttf = fontBoxFont;
    }

    public FontBoxFont getTrueTypeFont() {
        return this.ttf;
    }

    public boolean isCIDFont() {
        return getFont() != null;
    }
}
