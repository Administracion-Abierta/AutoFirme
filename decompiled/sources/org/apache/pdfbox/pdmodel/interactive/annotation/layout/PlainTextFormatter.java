package org.apache.pdfbox.pdmodel.interactive.annotation.layout;

import com.aowagie.text.pdf.ColumnText;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/layout/PlainTextFormatter.class */
public class PlainTextFormatter {
    private static final int FONTSCALE = 1000;
    private final AppearanceStyle appearanceStyle;
    private final boolean wrapLines;
    private final float width;
    private final PDAppearanceContentStream contents;
    private final PlainText textContent;
    private final TextAlign textAlignment;
    private float horizontalOffset;
    private float verticalOffset;

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/layout/PlainTextFormatter$TextAlign.class */
    enum TextAlign {
        LEFT(0),
        CENTER(1),
        RIGHT(2),
        JUSTIFY(4);

        private final int alignment;

        TextAlign(int alignment) {
            this.alignment = alignment;
        }

        int getTextAlign() {
            return this.alignment;
        }

        public static TextAlign valueOf(int alignment) {
            for (TextAlign textAlignment : values()) {
                if (textAlignment.getTextAlign() == alignment) {
                    return textAlignment;
                }
            }
            return LEFT;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/layout/PlainTextFormatter$Builder.class */
    public static class Builder {
        private PDAppearanceContentStream contents;
        private AppearanceStyle appearanceStyle;
        private PlainText textContent;
        private boolean wrapLines = false;
        private float width = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        private TextAlign textAlignment = TextAlign.LEFT;
        private float horizontalOffset = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        private float verticalOffset = ColumnText.GLOBAL_SPACE_CHAR_RATIO;

        public Builder(PDAppearanceContentStream contents) {
            this.contents = contents;
        }

        public Builder style(AppearanceStyle appearanceStyle) {
            this.appearanceStyle = appearanceStyle;
            return this;
        }

        public Builder wrapLines(boolean wrapLines) {
            this.wrapLines = wrapLines;
            return this;
        }

        public Builder width(float width) {
            this.width = width;
            return this;
        }

        public Builder textAlign(int alignment) {
            this.textAlignment = TextAlign.valueOf(alignment);
            return this;
        }

        public Builder textAlign(TextAlign alignment) {
            this.textAlignment = alignment;
            return this;
        }

        public Builder text(PlainText textContent) {
            this.textContent = textContent;
            return this;
        }

        public Builder initialOffset(float horizontalOffset, float verticalOffset) {
            this.horizontalOffset = horizontalOffset;
            this.verticalOffset = verticalOffset;
            return this;
        }

        public PlainTextFormatter build() {
            return new PlainTextFormatter(this);
        }
    }

    private PlainTextFormatter(Builder builder) {
        this.appearanceStyle = builder.appearanceStyle;
        this.wrapLines = builder.wrapLines;
        this.width = builder.width;
        this.contents = builder.contents;
        this.textContent = builder.textContent;
        this.textAlignment = builder.textAlignment;
        this.horizontalOffset = builder.horizontalOffset;
        this.verticalOffset = builder.verticalOffset;
    }

    public void format() throws IOException {
        if (this.textContent != null && !this.textContent.getParagraphs().isEmpty()) {
            boolean isFirstParagraph = true;
            for (PlainText.Paragraph paragraph : this.textContent.getParagraphs()) {
                if (this.wrapLines) {
                    List<PlainText.Line> lines = paragraph.getLines(this.appearanceStyle.getFont(), this.appearanceStyle.getFontSize(), this.width);
                    processLines(lines, isFirstParagraph);
                    isFirstParagraph = false;
                } else {
                    float startOffset = 0.0f;
                    float lineWidth = (this.appearanceStyle.getFont().getStringWidth(paragraph.getText()) * this.appearanceStyle.getFontSize()) / 1000.0f;
                    if (lineWidth < this.width) {
                        switch (this.textAlignment) {
                            case CENTER:
                                startOffset = (this.width - lineWidth) / 2.0f;
                                break;
                            case RIGHT:
                                startOffset = this.width - lineWidth;
                                break;
                            case JUSTIFY:
                            default:
                                startOffset = 0.0f;
                                break;
                        }
                    }
                    this.contents.newLineAtOffset(this.horizontalOffset + startOffset, this.verticalOffset);
                    this.contents.showText(paragraph.getText());
                }
            }
        }
    }

    private void processLines(List<PlainText.Line> lines, boolean isFirstParagraph) throws IOException {
        float lastPos = 0.0f;
        float startOffset = 0.0f;
        float interWordSpacing = 0.0f;
        for (PlainText.Line line : lines) {
            switch (this.textAlignment) {
                case CENTER:
                    startOffset = (this.width - line.getWidth()) / 2.0f;
                    break;
                case RIGHT:
                    startOffset = this.width - line.getWidth();
                    break;
                case JUSTIFY:
                    if (lines.indexOf(line) != lines.size() - 1) {
                        interWordSpacing = line.getInterWordSpacing(this.width);
                    }
                    break;
                default:
                    startOffset = 0.0f;
                    break;
            }
            float offset = (-lastPos) + startOffset + this.horizontalOffset;
            if (lines.indexOf(line) == 0 && isFirstParagraph) {
                this.contents.newLineAtOffset(offset, this.verticalOffset);
            } else {
                this.verticalOffset -= this.appearanceStyle.getLeading();
                this.contents.newLineAtOffset(offset, -this.appearanceStyle.getLeading());
            }
            lastPos += offset;
            List<PlainText.Word> words = line.getWords();
            int wordIndex = 0;
            for (PlainText.Word word : words) {
                this.contents.showText(word.getText());
                float wordWidth = ((Float) word.getAttributes().getIterator().getAttribute(PlainText.TextAttribute.WIDTH)).floatValue();
                if (wordIndex != words.size() - 1) {
                    this.contents.newLineAtOffset(wordWidth + interWordSpacing, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                    lastPos = lastPos + wordWidth + interWordSpacing;
                }
                wordIndex++;
            }
        }
        this.horizontalOffset -= lastPos;
    }
}
