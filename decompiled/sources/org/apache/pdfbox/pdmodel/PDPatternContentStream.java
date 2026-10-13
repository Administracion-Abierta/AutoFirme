package org.apache.pdfbox.pdmodel;

import java.awt.Color;
import java.io.IOException;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDInlineImage;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/PDPatternContentStream.class */
public final class PDPatternContentStream extends PDAbstractContentStream {
    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setTextRise(float f) throws IOException {
        super.setTextRise(f);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setRenderingMode(RenderingMode renderingMode) throws IOException {
        super.setRenderingMode(renderingMode);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setHorizontalScaling(float f) throws IOException {
        super.setHorizontalScaling(f);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setWordSpacing(float f) throws IOException {
        super.setWordSpacing(f);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setCharacterSpacing(float f) throws IOException {
        super.setCharacterSpacing(f);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream, java.io.Closeable, java.lang.AutoCloseable
    public /* bridge */ /* synthetic */ void close() throws IOException {
        super.close();
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void addComment(String str) throws IOException {
        super.addComment(str);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setGraphicsStateParameters(PDExtendedGraphicsState pDExtendedGraphicsState) throws IOException {
        super.setGraphicsStateParameters(pDExtendedGraphicsState);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void endMarkedContent() throws IOException {
        super.endMarkedContent();
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void beginMarkedContent(COSName cOSName, PDPropertyList pDPropertyList) throws IOException {
        super.beginMarkedContent(cOSName, pDPropertyList);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void beginMarkedContent(COSName cOSName) throws IOException {
        super.beginMarkedContent(cOSName);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setMiterLimit(float f) throws IOException {
        super.setMiterLimit(f);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setLineDashPattern(float[] fArr, float f) throws IOException {
        super.setLineDashPattern(fArr, f);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setLineCapStyle(int i) throws IOException {
        super.setLineCapStyle(i);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setLineJoinStyle(int i) throws IOException {
        super.setLineJoinStyle(i);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setLineWidth(float f) throws IOException {
        super.setLineWidth(f);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void clipEvenOdd() throws IOException {
        super.clipEvenOdd();
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void clip() throws IOException {
        super.clip();
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void closePath() throws IOException {
        super.closePath();
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void shadingFill(PDShading pDShading) throws IOException {
        super.shadingFill(pDShading);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void closeAndFillAndStrokeEvenOdd() throws IOException {
        super.closeAndFillAndStrokeEvenOdd();
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void closeAndFillAndStroke() throws IOException {
        super.closeAndFillAndStroke();
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void fillAndStrokeEvenOdd() throws IOException {
        super.fillAndStrokeEvenOdd();
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void fillAndStroke() throws IOException {
        super.fillAndStroke();
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void fillEvenOdd() throws IOException {
        super.fillEvenOdd();
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void fill() throws IOException {
        super.fill();
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void closeAndStroke() throws IOException {
        super.closeAndStroke();
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void stroke() throws IOException {
        super.stroke();
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void lineTo(float f, float f2) throws IOException {
        super.lineTo(f, f2);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void moveTo(float f, float f2) throws IOException {
        super.moveTo(f, f2);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void curveTo1(float f, float f2, float f3, float f4) throws IOException {
        super.curveTo1(f, f2, f3, f4);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void curveTo2(float f, float f2, float f3, float f4) throws IOException {
        super.curveTo2(f, f2, f3, f4);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void curveTo(float f, float f2, float f3, float f4, float f5, float f6) throws IOException {
        super.curveTo(f, f2, f3, f4, f5, f6);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void addRect(float f, float f2, float f3, float f4) throws IOException {
        super.addRect(f, f2, f3, f4);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setNonStrokingColor(float f) throws IOException {
        super.setNonStrokingColor(f);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setNonStrokingColor(int i) throws IOException {
        super.setNonStrokingColor(i);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setNonStrokingColor(float f, float f2, float f3, float f4) throws IOException {
        super.setNonStrokingColor(f, f2, f3, f4);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setNonStrokingColor(int i, int i2, int i3, int i4) throws IOException {
        super.setNonStrokingColor(i, i2, i3, i4);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    @Deprecated
    public /* bridge */ /* synthetic */ void setNonStrokingColor(int i, int i2, int i3) throws IOException {
        super.setNonStrokingColor(i, i2, i3);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setNonStrokingColor(float f, float f2, float f3) throws IOException {
        super.setNonStrokingColor(f, f2, f3);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setNonStrokingColor(Color color) throws IOException {
        super.setNonStrokingColor(color);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setNonStrokingColor(PDColor pDColor) throws IOException {
        super.setNonStrokingColor(pDColor);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setStrokingColor(float f) throws IOException {
        super.setStrokingColor(f);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setStrokingColor(float f, float f2, float f3, float f4) throws IOException {
        super.setStrokingColor(f, f2, f3, f4);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    @Deprecated
    public /* bridge */ /* synthetic */ void setStrokingColor(int i, int i2, int i3) throws IOException {
        super.setStrokingColor(i, i2, i3);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setStrokingColor(float f, float f2, float f3) throws IOException {
        super.setStrokingColor(f, f2, f3);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setStrokingColor(Color color) throws IOException {
        super.setStrokingColor(color);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setStrokingColor(PDColor pDColor) throws IOException {
        super.setStrokingColor(pDColor);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void restoreGraphicsState() throws IOException {
        super.restoreGraphicsState();
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void saveGraphicsState() throws IOException {
        super.saveGraphicsState();
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void transform(Matrix matrix) throws IOException {
        super.transform(matrix);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void drawForm(PDFormXObject pDFormXObject) throws IOException {
        super.drawForm(pDFormXObject);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void drawImage(PDInlineImage pDInlineImage, float f, float f2, float f3, float f4) throws IOException {
        super.drawImage(pDInlineImage, f, f2, f3, f4);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void drawImage(PDInlineImage pDInlineImage, float f, float f2) throws IOException {
        super.drawImage(pDInlineImage, f, f2);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void drawImage(PDImageXObject pDImageXObject, Matrix matrix) throws IOException {
        super.drawImage(pDImageXObject, matrix);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void drawImage(PDImageXObject pDImageXObject, float f, float f2, float f3, float f4) throws IOException {
        super.drawImage(pDImageXObject, f, f2, f3, f4);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void drawImage(PDImageXObject pDImageXObject, float f, float f2) throws IOException {
        super.drawImage(pDImageXObject, f, f2);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setTextMatrix(Matrix matrix) throws IOException {
        super.setTextMatrix(matrix);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void newLineAtOffset(float f, float f2) throws IOException {
        super.newLineAtOffset(f, f2);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void newLine() throws IOException {
        super.newLine();
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setLeading(float f) throws IOException {
        super.setLeading(f);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void showText(String str) throws IOException {
        super.showText(str);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void showTextWithPositioning(Object[] objArr) throws IOException {
        super.showTextWithPositioning(objArr);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void setFont(PDFont pDFont, float f) throws IOException {
        super.setFont(pDFont, f);
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void endText() throws IOException {
        super.endText();
    }

    @Override // org.apache.pdfbox.pdmodel.PDAbstractContentStream
    public /* bridge */ /* synthetic */ void beginText() throws IOException {
        super.beginText();
    }

    public PDPatternContentStream(PDTilingPattern pattern) throws IOException {
        super(null, pattern.getContentStream().createOutputStream(), pattern.getResources());
    }
}
