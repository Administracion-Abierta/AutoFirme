package org.apache.pdfbox.pdmodel.graphics.state;

import com.aowagie.text.pdf.ColumnText;
import org.apache.pdfbox.pdmodel.font.PDFont;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/state/PDTextState.class */
public class PDTextState implements Cloneable {
    private PDFont font;
    private float fontSize;
    private float characterSpacing = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    private float wordSpacing = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    private float horizontalScaling = 100.0f;
    private float leading = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    private RenderingMode renderingMode = RenderingMode.FILL;
    private float rise = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    private boolean knockout = true;

    public float getCharacterSpacing() {
        return this.characterSpacing;
    }

    public void setCharacterSpacing(float value) {
        this.characterSpacing = value;
    }

    public float getWordSpacing() {
        return this.wordSpacing;
    }

    public void setWordSpacing(float value) {
        this.wordSpacing = value;
    }

    public float getHorizontalScaling() {
        return this.horizontalScaling;
    }

    public void setHorizontalScaling(float value) {
        this.horizontalScaling = value;
    }

    public float getLeading() {
        return this.leading;
    }

    public void setLeading(float value) {
        this.leading = value;
    }

    public PDFont getFont() {
        return this.font;
    }

    public void setFont(PDFont value) {
        this.font = value;
    }

    public float getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(float value) {
        this.fontSize = value;
    }

    public RenderingMode getRenderingMode() {
        return this.renderingMode;
    }

    public void setRenderingMode(RenderingMode renderingMode) {
        this.renderingMode = renderingMode;
    }

    public float getRise() {
        return this.rise;
    }

    public void setRise(float value) {
        this.rise = value;
    }

    public boolean getKnockoutFlag() {
        return this.knockout;
    }

    public void setKnockoutFlag(boolean value) {
        this.knockout = value;
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public PDTextState m1222clone() {
        try {
            return (PDTextState) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
