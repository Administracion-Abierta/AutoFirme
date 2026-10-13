package com.aowagie.text.pdf.parser;

import com.aowagie.text.pdf.CMapAwareDocumentFont;
import com.aowagie.text.pdf.ColumnText;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/parser/GraphicsState.class */
class GraphicsState {
    Matrix ctm;
    float characterSpacing;
    float wordSpacing;
    float horizontalScaling;
    float leading;
    CMapAwareDocumentFont font;
    float fontSize;
    int renderMode;
    float rise;
    private final boolean knockout;

    public GraphicsState() {
        this.ctm = new Matrix();
        this.characterSpacing = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.wordSpacing = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.horizontalScaling = 1.0f;
        this.leading = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.font = null;
        this.fontSize = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.renderMode = 0;
        this.rise = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.knockout = true;
    }

    public GraphicsState(GraphicsState source) {
        this.ctm = source.ctm;
        this.characterSpacing = source.characterSpacing;
        this.wordSpacing = source.wordSpacing;
        this.horizontalScaling = source.horizontalScaling;
        this.leading = source.leading;
        this.font = source.font;
        this.fontSize = source.fontSize;
        this.renderMode = source.renderMode;
        this.rise = source.rise;
        this.knockout = source.knockout;
    }
}
