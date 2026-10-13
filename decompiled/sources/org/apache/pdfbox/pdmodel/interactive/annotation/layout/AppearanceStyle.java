package org.apache.pdfbox.pdmodel.interactive.annotation.layout;

import org.apache.pdfbox.pdmodel.font.PDFont;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/layout/AppearanceStyle.class */
public class AppearanceStyle {
    private PDFont font;
    private float fontSize = 12.0f;
    private float leading = 14.4f;

    PDFont getFont() {
        return this.font;
    }

    public void setFont(PDFont font) {
        this.font = font;
    }

    float getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
        this.leading = fontSize * 1.2f;
    }

    float getLeading() {
        return this.leading;
    }

    void setLeading(float leading) {
        this.leading = leading;
    }
}
