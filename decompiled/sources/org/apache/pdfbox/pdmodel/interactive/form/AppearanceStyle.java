package org.apache.pdfbox.pdmodel.interactive.form;

import org.apache.pdfbox.pdmodel.font.PDFont;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/form/AppearanceStyle.class */
class AppearanceStyle {
    private PDFont font;
    private float fontSize = 12.0f;
    private float leading = 14.4f;

    AppearanceStyle() {
    }

    PDFont getFont() {
        return this.font;
    }

    void setFont(PDFont font) {
        this.font = font;
    }

    float getFontSize() {
        return this.fontSize;
    }

    void setFontSize(float fontSize) {
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
