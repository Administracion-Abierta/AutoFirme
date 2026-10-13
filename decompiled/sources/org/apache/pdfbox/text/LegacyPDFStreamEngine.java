package org.apache.pdfbox.text;

import com.aowagie.text.pdf.ColumnText;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.state.Concatenate;
import org.apache.pdfbox.contentstream.operator.state.Restore;
import org.apache.pdfbox.contentstream.operator.state.Save;
import org.apache.pdfbox.contentstream.operator.state.SetGraphicsStateParameters;
import org.apache.pdfbox.contentstream.operator.state.SetMatrix;
import org.apache.pdfbox.contentstream.operator.text.BeginText;
import org.apache.pdfbox.contentstream.operator.text.EndText;
import org.apache.pdfbox.contentstream.operator.text.MoveText;
import org.apache.pdfbox.contentstream.operator.text.MoveTextSetLeading;
import org.apache.pdfbox.contentstream.operator.text.NextLine;
import org.apache.pdfbox.contentstream.operator.text.SetCharSpacing;
import org.apache.pdfbox.contentstream.operator.text.SetFontAndSize;
import org.apache.pdfbox.contentstream.operator.text.SetTextHorizontalScaling;
import org.apache.pdfbox.contentstream.operator.text.SetTextLeading;
import org.apache.pdfbox.contentstream.operator.text.SetTextRenderingMode;
import org.apache.pdfbox.contentstream.operator.text.SetTextRise;
import org.apache.pdfbox.contentstream.operator.text.SetWordSpacing;
import org.apache.pdfbox.contentstream.operator.text.ShowText;
import org.apache.pdfbox.contentstream.operator.text.ShowTextAdjusted;
import org.apache.pdfbox.contentstream.operator.text.ShowTextLine;
import org.apache.pdfbox.contentstream.operator.text.ShowTextLineAndSpace;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDCIDFont;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType2;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.pdfbox.pdmodel.font.encoding.GlyphList;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/text/LegacyPDFStreamEngine.class */
class LegacyPDFStreamEngine extends PDFStreamEngine {
    private static final Log LOG = LogFactory.getLog(LegacyPDFStreamEngine.class);
    private int pageRotation;
    private PDRectangle pageSize;
    private Matrix translateMatrix;
    private static final GlyphList GLYPHLIST;
    private final Map<COSDictionary, Float> fontHeightMap = new WeakHashMap();

    static {
        InputStream input = GlyphList.class.getResourceAsStream("/org/apache/pdfbox/resources/glyphlist/additional.txt");
        try {
            GLYPHLIST = new GlyphList(GlyphList.getAdobeGlyphList(), input);
            input.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    LegacyPDFStreamEngine() throws IOException {
        addOperator(new BeginText());
        addOperator(new Concatenate());
        addOperator(new DrawObject());
        addOperator(new EndText());
        addOperator(new SetGraphicsStateParameters());
        addOperator(new Save());
        addOperator(new Restore());
        addOperator(new NextLine());
        addOperator(new SetCharSpacing());
        addOperator(new MoveText());
        addOperator(new MoveTextSetLeading());
        addOperator(new SetFontAndSize());
        addOperator(new ShowText());
        addOperator(new ShowTextAdjusted());
        addOperator(new SetTextLeading());
        addOperator(new SetMatrix());
        addOperator(new SetTextRenderingMode());
        addOperator(new SetTextRise());
        addOperator(new SetWordSpacing());
        addOperator(new SetTextHorizontalScaling());
        addOperator(new ShowTextLine());
        addOperator(new ShowTextLineAndSpace());
    }

    @Override // org.apache.pdfbox.contentstream.PDFStreamEngine
    public void processPage(PDPage page) throws IOException {
        this.pageRotation = page.getRotation();
        this.pageSize = page.getCropBox();
        if (this.pageSize.getLowerLeftX() == ColumnText.GLOBAL_SPACE_CHAR_RATIO && this.pageSize.getLowerLeftY() == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            this.translateMatrix = null;
        } else {
            this.translateMatrix = Matrix.getTranslateInstance(-this.pageSize.getLowerLeftX(), -this.pageSize.getLowerLeftY());
        }
        super.processPage(page);
    }

    @Override // org.apache.pdfbox.contentstream.PDFStreamEngine
    protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode, Vector displacement) throws IOException {
        Matrix translatedTextRenderingMatrix;
        PDGraphicsState state = getGraphicsState();
        Matrix ctm = state.getCurrentTransformationMatrix();
        float fontSize = state.getTextState().getFontSize();
        float horizontalScaling = state.getTextState().getHorizontalScaling() / 100.0f;
        Matrix textMatrix = getTextMatrix();
        float displacementX = displacement.getX();
        if (font.isVertical()) {
            displacementX = font.getWidth(code) / 1000.0f;
            TrueTypeFont ttf = null;
            if (font instanceof PDTrueTypeFont) {
                ttf = ((PDTrueTypeFont) font).getTrueTypeFont();
            } else if (font instanceof PDType0Font) {
                PDCIDFont cidFont = ((PDType0Font) font).getDescendantFont();
                if (cidFont instanceof PDCIDFontType2) {
                    ttf = ((PDCIDFontType2) cidFont).getTrueTypeFont();
                }
            }
            if (ttf != null && ttf.getUnitsPerEm() != 1000) {
                displacementX *= 1000.0f / ttf.getUnitsPerEm();
            }
        }
        float tx = displacementX * fontSize * horizontalScaling;
        float ty = displacement.getY() * fontSize;
        Matrix td = Matrix.getTranslateInstance(tx, ty);
        Matrix nextTextRenderingMatrix = td.multiply(textMatrix).multiply(ctm);
        float nextX = nextTextRenderingMatrix.getTranslateX();
        float nextY = nextTextRenderingMatrix.getTranslateY();
        float dxDisplay = nextX - textRenderingMatrix.getTranslateX();
        Float fontHeight = this.fontHeightMap.get(font.getCOSObject());
        if (fontHeight == null) {
            fontHeight = Float.valueOf(computeFontHeight(font));
            this.fontHeightMap.put(font.getCOSObject(), fontHeight);
        }
        float dyDisplay = fontHeight.floatValue() * textRenderingMatrix.getScalingFactorY();
        float glyphSpaceToTextSpaceFactor = 0.001f;
        if (font instanceof PDType3Font) {
            glyphSpaceToTextSpaceFactor = font.getFontMatrix().getScaleX();
        }
        float spaceWidthText = 0.0f;
        try {
            spaceWidthText = font.getSpaceWidth() * glyphSpaceToTextSpaceFactor;
        } catch (Throwable exception) {
            LOG.warn(exception, exception);
        }
        if (spaceWidthText == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            float spaceWidthText2 = font.getAverageFontWidth() * glyphSpaceToTextSpaceFactor;
            spaceWidthText = spaceWidthText2 * 0.8f;
        }
        if (spaceWidthText == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            spaceWidthText = 1.0f;
        }
        float spaceWidthDisplay = spaceWidthText * textRenderingMatrix.getScalingFactorX();
        String unicodeMapping = font.toUnicode(code, GLYPHLIST);
        if (unicodeMapping == null) {
            if (font instanceof PDSimpleFont) {
                char c = (char) code;
                unicodeMapping = new String(new char[]{c});
            } else {
                return;
            }
        }
        if (this.translateMatrix == null) {
            translatedTextRenderingMatrix = textRenderingMatrix;
        } else {
            translatedTextRenderingMatrix = Matrix.concatenate(this.translateMatrix, textRenderingMatrix);
            nextX -= this.pageSize.getLowerLeftX();
            nextY -= this.pageSize.getLowerLeftY();
        }
        processTextPosition(new TextPosition(this.pageRotation, this.pageSize.getWidth(), this.pageSize.getHeight(), translatedTextRenderingMatrix, nextX, nextY, Math.abs(dyDisplay), dxDisplay, Math.abs(spaceWidthDisplay), unicodeMapping, new int[]{code}, font, fontSize, (int) (fontSize * textMatrix.getScalingFactorX())));
    }

    protected float computeFontHeight(PDFont font) throws IOException {
        float height;
        BoundingBox bbox = font.getBoundingBox();
        if (bbox.getLowerLeftY() < -32768.0f) {
            bbox.setLowerLeftY(-(bbox.getLowerLeftY() + 65536.0f));
        }
        float glyphHeight = bbox.getHeight() / 2.0f;
        PDFontDescriptor fontDescriptor = font.getFontDescriptor();
        if (fontDescriptor != null) {
            float capHeight = fontDescriptor.getCapHeight();
            if (Float.compare(capHeight, ColumnText.GLOBAL_SPACE_CHAR_RATIO) != 0 && (capHeight < glyphHeight || Float.compare(glyphHeight, ColumnText.GLOBAL_SPACE_CHAR_RATIO) == 0)) {
                glyphHeight = capHeight;
            }
            float ascent = fontDescriptor.getAscent();
            float descent = fontDescriptor.getDescent();
            if (capHeight > ascent && ascent > ColumnText.GLOBAL_SPACE_CHAR_RATIO && descent < ColumnText.GLOBAL_SPACE_CHAR_RATIO && ((ascent - descent) / 2.0f < glyphHeight || Float.compare(glyphHeight, ColumnText.GLOBAL_SPACE_CHAR_RATIO) == 0)) {
                glyphHeight = (ascent - descent) / 2.0f;
            }
        }
        if (font instanceof PDType3Font) {
            height = font.getFontMatrix().transformPoint(ColumnText.GLOBAL_SPACE_CHAR_RATIO, glyphHeight).y;
        } else {
            height = glyphHeight / 1000.0f;
        }
        return height;
    }

    protected void processTextPosition(TextPosition text) {
    }
}
