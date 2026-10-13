package org.apache.pdfbox.pdmodel;

import com.aowagie.text.pdf.ColumnText;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceCMYK;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceN;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.color.PDICCBased;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;
import org.apache.pdfbox.pdmodel.graphics.color.PDSeparation;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDInlineImage;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.util.Charsets;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.NumberFormatUtil;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/PDAbstractContentStream.class */
abstract class PDAbstractContentStream implements Closeable {
    private static final Log LOG = LogFactory.getLog(PDAbstractContentStream.class);
    protected final PDDocument document;
    protected final OutputStream outputStream;
    protected final PDResources resources;
    protected boolean inTextMode = false;
    protected final Deque<PDFont> fontStack = new ArrayDeque();
    protected final Deque<PDColorSpace> nonStrokingColorSpaceStack = new ArrayDeque();
    protected final Deque<PDColorSpace> strokingColorSpaceStack = new ArrayDeque();
    private final NumberFormat formatDecimal = NumberFormat.getNumberInstance(Locale.US);
    private final byte[] formatBuffer = new byte[32];

    PDAbstractContentStream(PDDocument document, OutputStream outputStream, PDResources resources) {
        this.document = document;
        this.outputStream = outputStream;
        this.resources = resources;
        this.formatDecimal.setMaximumFractionDigits(4);
        this.formatDecimal.setGroupingUsed(false);
    }

    protected void setMaximumFractionDigits(int fractionDigitsNumber) {
        this.formatDecimal.setMaximumFractionDigits(fractionDigitsNumber);
    }

    public void beginText() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: Nested beginText() calls are not allowed.");
        }
        writeOperator(OperatorName.BEGIN_TEXT);
        this.inTextMode = true;
    }

    public void endText() throws IOException {
        if (!this.inTextMode) {
            throw new IllegalStateException("Error: You must call beginText() before calling endText.");
        }
        writeOperator(OperatorName.END_TEXT);
        this.inTextMode = false;
    }

    public void setFont(PDFont font, float fontSize) throws IOException {
        if (this.fontStack.isEmpty()) {
            this.fontStack.add(font);
        } else {
            this.fontStack.pop();
            this.fontStack.push(font);
        }
        if (font.willBeSubset()) {
            if (this.document != null) {
                this.document.getFontsToSubset().add(font);
            } else {
                LOG.warn("Using the subsetted font '" + font.getName() + "' without a PDDocument context; call subset() before saving");
            }
        }
        writeOperand(this.resources.add(font));
        writeOperand(fontSize);
        writeOperator(OperatorName.SET_FONT_AND_SIZE);
    }

    public void showTextWithPositioning(Object[] textWithPositioningArray) throws IOException {
        write("[");
        for (Object obj : textWithPositioningArray) {
            if (obj instanceof String) {
                showTextInternal((String) obj);
            } else if (obj instanceof Float) {
                writeOperand(((Float) obj).floatValue());
            } else {
                throw new IllegalArgumentException("Argument must consist of array of Float and String types");
            }
        }
        write("] ");
        writeOperator(OperatorName.SHOW_TEXT_ADJUSTED);
    }

    public void showText(String text) throws IOException {
        showTextInternal(text);
        write(" ");
        writeOperator(OperatorName.SHOW_TEXT);
    }

    protected void showTextInternal(String text) throws IOException {
        if (!this.inTextMode) {
            throw new IllegalStateException("Must call beginText() before showText()");
        }
        if (this.fontStack.isEmpty()) {
            throw new IllegalStateException("Must call setFont() before showText()");
        }
        PDFont font = this.fontStack.peek();
        byte[] encodedText = null;
        if (0 == 0) {
            encodedText = font.encode(text);
        }
        if (font.willBeSubset()) {
            int iCharCount = 0;
            while (true) {
                int offset = iCharCount;
                if (offset >= text.length()) {
                    break;
                }
                int codePoint = text.codePointAt(offset);
                font.addToSubset(codePoint);
                iCharCount = offset + Character.charCount(codePoint);
            }
        }
        COSWriter.writeString(encodedText, this.outputStream);
    }

    public void setLeading(float leading) throws IOException {
        writeOperand(leading);
        writeOperator(OperatorName.SET_TEXT_LEADING);
    }

    public void newLine() throws IOException {
        if (!this.inTextMode) {
            throw new IllegalStateException("Must call beginText() before newLine()");
        }
        writeOperator(OperatorName.NEXT_LINE);
    }

    public void newLineAtOffset(float tx, float ty) throws IOException {
        if (!this.inTextMode) {
            throw new IllegalStateException("Error: must call beginText() before newLineAtOffset()");
        }
        writeOperand(tx);
        writeOperand(ty);
        writeOperator(OperatorName.MOVE_TEXT);
    }

    public void setTextMatrix(Matrix matrix) throws IOException {
        if (!this.inTextMode) {
            throw new IllegalStateException("Error: must call beginText() before setTextMatrix");
        }
        writeAffineTransform(matrix.createAffineTransform());
        writeOperator(OperatorName.SET_MATRIX);
    }

    public void drawImage(PDImageXObject image, float x, float y) throws IOException {
        drawImage(image, x, y, image.getWidth(), image.getHeight());
    }

    public void drawImage(PDImageXObject image, float x, float y, float width, float height) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: drawImage is not allowed within a text block.");
        }
        saveGraphicsState();
        AffineTransform transform = new AffineTransform(width, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, height, x, y);
        transform(new Matrix(transform));
        writeOperand(this.resources.add(image));
        writeOperator(OperatorName.DRAW_OBJECT);
        restoreGraphicsState();
    }

    public void drawImage(PDImageXObject image, Matrix matrix) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: drawImage is not allowed within a text block.");
        }
        saveGraphicsState();
        AffineTransform transform = matrix.createAffineTransform();
        transform(new Matrix(transform));
        writeOperand(this.resources.add(image));
        writeOperator(OperatorName.DRAW_OBJECT);
        restoreGraphicsState();
    }

    public void drawImage(PDInlineImage inlineImage, float x, float y) throws IOException {
        drawImage(inlineImage, x, y, inlineImage.getWidth(), inlineImage.getHeight());
    }

    public void drawImage(PDInlineImage inlineImage, float x, float y, float width, float height) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: drawImage is not allowed within a text block.");
        }
        saveGraphicsState();
        transform(new Matrix(width, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, height, x, y));
        StringBuilder sb = new StringBuilder();
        sb.append(OperatorName.BEGIN_INLINE_IMAGE);
        sb.append("\n /W ");
        sb.append(inlineImage.getWidth());
        sb.append("\n /H ");
        sb.append(inlineImage.getHeight());
        sb.append("\n /CS ");
        sb.append("/");
        sb.append(inlineImage.getColorSpace().getName());
        COSArray decodeArray = inlineImage.getDecode();
        if (decodeArray != null && decodeArray.size() > 0) {
            sb.append("\n /D ");
            sb.append("[");
            for (COSBase base : decodeArray) {
                sb.append(((COSNumber) base).intValue());
                sb.append(" ");
            }
            sb.append("]");
        }
        if (inlineImage.isStencil()) {
            sb.append("\n /IM true");
        }
        sb.append("\n /BPC ");
        sb.append(inlineImage.getBitsPerComponent());
        write(sb.toString());
        writeLine();
        writeOperator("ID");
        writeBytes(inlineImage.getData());
        writeLine();
        writeOperator(OperatorName.END_INLINE_IMAGE);
        restoreGraphicsState();
    }

    public void drawForm(PDFormXObject form) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: drawForm is not allowed within a text block.");
        }
        writeOperand(this.resources.add(form));
        writeOperator(OperatorName.DRAW_OBJECT);
    }

    public void transform(Matrix matrix) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: Modifying the current transformation matrix is not allowed within text objects.");
        }
        writeAffineTransform(matrix.createAffineTransform());
        writeOperator(OperatorName.CONCAT);
    }

    public void saveGraphicsState() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: Saving the graphics state is not allowed within text objects.");
        }
        if (!this.fontStack.isEmpty()) {
            this.fontStack.push(this.fontStack.peek());
        }
        if (!this.strokingColorSpaceStack.isEmpty()) {
            this.strokingColorSpaceStack.push(this.strokingColorSpaceStack.peek());
        }
        if (!this.nonStrokingColorSpaceStack.isEmpty()) {
            this.nonStrokingColorSpaceStack.push(this.nonStrokingColorSpaceStack.peek());
        }
        writeOperator(OperatorName.SAVE);
    }

    public void restoreGraphicsState() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: Restoring the graphics state is not allowed within text objects.");
        }
        if (!this.fontStack.isEmpty()) {
            this.fontStack.pop();
        }
        if (!this.strokingColorSpaceStack.isEmpty()) {
            this.strokingColorSpaceStack.pop();
        }
        if (!this.nonStrokingColorSpaceStack.isEmpty()) {
            this.nonStrokingColorSpaceStack.pop();
        }
        writeOperator("Q");
    }

    protected COSName getName(PDColorSpace colorSpace) {
        if ((colorSpace instanceof PDDeviceGray) || (colorSpace instanceof PDDeviceRGB) || (colorSpace instanceof PDDeviceCMYK)) {
            return COSName.getPDFName(colorSpace.getName());
        }
        return this.resources.add(colorSpace);
    }

    public void setStrokingColor(PDColor color) throws IOException {
        if (this.strokingColorSpaceStack.isEmpty() || this.strokingColorSpaceStack.peek() != color.getColorSpace()) {
            writeOperand(getName(color.getColorSpace()));
            writeOperator(OperatorName.STROKING_COLORSPACE);
            setStrokingColorSpaceStack(color.getColorSpace());
        }
        for (float value : color.getComponents()) {
            writeOperand(value);
        }
        if (color.getColorSpace() instanceof PDPattern) {
            writeOperand(color.getPatternName());
        }
        if ((color.getColorSpace() instanceof PDPattern) || (color.getColorSpace() instanceof PDSeparation) || (color.getColorSpace() instanceof PDDeviceN) || (color.getColorSpace() instanceof PDICCBased)) {
            writeOperator(OperatorName.STROKING_COLOR_N);
        } else {
            writeOperator("SC");
        }
    }

    public void setStrokingColor(Color color) throws IOException {
        float[] components = {color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f};
        PDColor pdColor = new PDColor(components, PDDeviceRGB.INSTANCE);
        setStrokingColor(pdColor);
    }

    public void setStrokingColor(float r, float g, float b) throws IOException {
        if (isOutsideOneInterval(r) || isOutsideOneInterval(g) || isOutsideOneInterval(b)) {
            throw new IllegalArgumentException("Parameters must be within 0..1, but are " + String.format("(%.2f,%.2f,%.2f)", Float.valueOf(r), Float.valueOf(g), Float.valueOf(b)));
        }
        writeOperand(r);
        writeOperand(g);
        writeOperand(b);
        writeOperator(OperatorName.STROKING_COLOR_RGB);
        setStrokingColorSpaceStack(PDDeviceRGB.INSTANCE);
    }

    @Deprecated
    public void setStrokingColor(int r, int g, int b) throws IOException {
        if (isOutside255Interval(r) || isOutside255Interval(g) || isOutside255Interval(b)) {
            throw new IllegalArgumentException("Parameters must be within 0..255, but are " + String.format("(%d,%d,%d)", Integer.valueOf(r), Integer.valueOf(g), Integer.valueOf(b)));
        }
        setStrokingColor(r / 255.0f, g / 255.0f, b / 255.0f);
    }

    public void setStrokingColor(float c, float m, float y, float k) throws IOException {
        if (isOutsideOneInterval(c) || isOutsideOneInterval(m) || isOutsideOneInterval(y) || isOutsideOneInterval(k)) {
            throw new IllegalArgumentException("Parameters must be within 0..1, but are " + String.format("(%.2f,%.2f,%.2f,%.2f)", Float.valueOf(c), Float.valueOf(m), Float.valueOf(y), Float.valueOf(k)));
        }
        writeOperand(c);
        writeOperand(m);
        writeOperand(y);
        writeOperand(k);
        writeOperator(OperatorName.STROKING_COLOR_CMYK);
        setStrokingColorSpaceStack(PDDeviceCMYK.INSTANCE);
    }

    public void setStrokingColor(float g) throws IOException {
        if (isOutsideOneInterval(g)) {
            throw new IllegalArgumentException("Parameter must be within 0..1, but is " + g);
        }
        writeOperand(g);
        writeOperator("G");
        setStrokingColorSpaceStack(PDDeviceGray.INSTANCE);
    }

    public void setNonStrokingColor(PDColor color) throws IOException {
        if (this.nonStrokingColorSpaceStack.isEmpty() || this.nonStrokingColorSpaceStack.peek() != color.getColorSpace()) {
            writeOperand(getName(color.getColorSpace()));
            writeOperator(OperatorName.NON_STROKING_COLORSPACE);
            setNonStrokingColorSpaceStack(color.getColorSpace());
        }
        for (float value : color.getComponents()) {
            writeOperand(value);
        }
        if (color.getColorSpace() instanceof PDPattern) {
            writeOperand(color.getPatternName());
        }
        if ((color.getColorSpace() instanceof PDPattern) || (color.getColorSpace() instanceof PDSeparation) || (color.getColorSpace() instanceof PDDeviceN) || (color.getColorSpace() instanceof PDICCBased)) {
            writeOperator(OperatorName.NON_STROKING_COLOR_N);
        } else {
            writeOperator(OperatorName.NON_STROKING_COLOR);
        }
    }

    public void setNonStrokingColor(Color color) throws IOException {
        float[] components = {color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f};
        PDColor pdColor = new PDColor(components, PDDeviceRGB.INSTANCE);
        setNonStrokingColor(pdColor);
    }

    public void setNonStrokingColor(float r, float g, float b) throws IOException {
        if (isOutsideOneInterval(r) || isOutsideOneInterval(g) || isOutsideOneInterval(b)) {
            throw new IllegalArgumentException("Parameters must be within 0..1, but are " + String.format("(%.2f,%.2f,%.2f)", Float.valueOf(r), Float.valueOf(g), Float.valueOf(b)));
        }
        writeOperand(r);
        writeOperand(g);
        writeOperand(b);
        writeOperator(OperatorName.NON_STROKING_RGB);
        setNonStrokingColorSpaceStack(PDDeviceRGB.INSTANCE);
    }

    @Deprecated
    public void setNonStrokingColor(int r, int g, int b) throws IOException {
        if (isOutside255Interval(r) || isOutside255Interval(g) || isOutside255Interval(b)) {
            throw new IllegalArgumentException("Parameters must be within 0..255, but are " + String.format("(%d,%d,%d)", Integer.valueOf(r), Integer.valueOf(g), Integer.valueOf(b)));
        }
        setNonStrokingColor(r / 255.0f, g / 255.0f, b / 255.0f);
    }

    public void setNonStrokingColor(int c, int m, int y, int k) throws IOException {
        if (isOutside255Interval(c) || isOutside255Interval(m) || isOutside255Interval(y) || isOutside255Interval(k)) {
            throw new IllegalArgumentException("Parameters must be within 0..255, but are " + String.format("(%d,%d,%d,%d)", Integer.valueOf(c), Integer.valueOf(m), Integer.valueOf(y), Integer.valueOf(k)));
        }
        setNonStrokingColor(c / 255.0f, m / 255.0f, y / 255.0f, k / 255.0f);
    }

    public void setNonStrokingColor(float c, float m, float y, float k) throws IOException {
        if (isOutsideOneInterval(c) || isOutsideOneInterval(m) || isOutsideOneInterval(y) || isOutsideOneInterval(k)) {
            throw new IllegalArgumentException("Parameters must be within 0..1, but are " + String.format("(%.2f,%.2f,%.2f,%.2f)", Float.valueOf(c), Float.valueOf(m), Float.valueOf(y), Float.valueOf(k)));
        }
        writeOperand(c);
        writeOperand(m);
        writeOperand(y);
        writeOperand(k);
        writeOperator(OperatorName.NON_STROKING_CMYK);
        setNonStrokingColorSpaceStack(PDDeviceCMYK.INSTANCE);
    }

    public void setNonStrokingColor(int g) throws IOException {
        if (isOutside255Interval(g)) {
            throw new IllegalArgumentException("Parameter must be within 0..255, but is " + g);
        }
        setNonStrokingColor(g / 255.0f);
    }

    public void setNonStrokingColor(float g) throws IOException {
        if (isOutsideOneInterval(g)) {
            throw new IllegalArgumentException("Parameter must be within 0..1, but is " + g);
        }
        writeOperand(g);
        writeOperator(OperatorName.NON_STROKING_GRAY);
        setNonStrokingColorSpaceStack(PDDeviceGray.INSTANCE);
    }

    public void addRect(float x, float y, float width, float height) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: addRect is not allowed within a text block.");
        }
        writeOperand(x);
        writeOperand(y);
        writeOperand(width);
        writeOperand(height);
        writeOperator(OperatorName.APPEND_RECT);
    }

    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: curveTo is not allowed within a text block.");
        }
        writeOperand(x1);
        writeOperand(y1);
        writeOperand(x2);
        writeOperand(y2);
        writeOperand(x3);
        writeOperand(y3);
        writeOperator("c");
    }

    public void curveTo2(float x2, float y2, float x3, float y3) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: curveTo2 is not allowed within a text block.");
        }
        writeOperand(x2);
        writeOperand(y2);
        writeOperand(x3);
        writeOperand(y3);
        writeOperator(OperatorName.CURVE_TO_REPLICATE_INITIAL_POINT);
    }

    public void curveTo1(float x1, float y1, float x3, float y3) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: curveTo1 is not allowed within a text block.");
        }
        writeOperand(x1);
        writeOperand(y1);
        writeOperand(x3);
        writeOperand(y3);
        writeOperator(OperatorName.CURVE_TO_REPLICATE_FINAL_POINT);
    }

    public void moveTo(float x, float y) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: moveTo is not allowed within a text block.");
        }
        writeOperand(x);
        writeOperand(y);
        writeOperator(OperatorName.MOVE_TO);
    }

    public void lineTo(float x, float y) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: lineTo is not allowed within a text block.");
        }
        writeOperand(x);
        writeOperand(y);
        writeOperator(OperatorName.LINE_TO);
    }

    public void stroke() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: stroke is not allowed within a text block.");
        }
        writeOperator("S");
    }

    public void closeAndStroke() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: closeAndStroke is not allowed within a text block.");
        }
        writeOperator(OperatorName.CLOSE_AND_STROKE);
    }

    public void fill() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: fill is not allowed within a text block.");
        }
        writeOperator(OperatorName.FILL_NON_ZERO);
    }

    public void fillEvenOdd() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: fillEvenOdd is not allowed within a text block.");
        }
        writeOperator(OperatorName.FILL_EVEN_ODD);
    }

    public void fillAndStroke() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: fillAndStroke is not allowed within a text block.");
        }
        writeOperator("B");
    }

    public void fillAndStrokeEvenOdd() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: fillAndStrokeEvenOdd is not allowed within a text block.");
        }
        writeOperator(OperatorName.FILL_EVEN_ODD_AND_STROKE);
    }

    public void closeAndFillAndStroke() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: closeAndFillAndStroke is not allowed within a text block.");
        }
        writeOperator(OperatorName.CLOSE_FILL_NON_ZERO_AND_STROKE);
    }

    public void closeAndFillAndStrokeEvenOdd() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: closeAndFillAndStrokeEvenOdd is not allowed within a text block.");
        }
        writeOperator(OperatorName.CLOSE_FILL_EVEN_ODD_AND_STROKE);
    }

    public void shadingFill(PDShading shading) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: shadingFill is not allowed within a text block.");
        }
        writeOperand(this.resources.add(shading));
        writeOperator(OperatorName.SHADING_FILL);
    }

    public void closePath() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: closePath is not allowed within a text block.");
        }
        writeOperator(OperatorName.CLOSE_PATH);
    }

    public void clip() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: clip is not allowed within a text block.");
        }
        writeOperator("W");
        writeOperator(OperatorName.ENDPATH);
    }

    public void clipEvenOdd() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: clipEvenOdd is not allowed within a text block.");
        }
        writeOperator(OperatorName.CLIP_EVEN_ODD);
        writeOperator(OperatorName.ENDPATH);
    }

    public void setLineWidth(float lineWidth) throws IOException {
        writeOperand(lineWidth);
        writeOperator(OperatorName.SET_LINE_WIDTH);
    }

    public void setLineJoinStyle(int lineJoinStyle) throws IOException {
        if (lineJoinStyle >= 0 && lineJoinStyle <= 2) {
            writeOperand(lineJoinStyle);
            writeOperator(OperatorName.SET_LINE_JOINSTYLE);
            return;
        }
        throw new IllegalArgumentException("Error: unknown value for line join style");
    }

    public void setLineCapStyle(int lineCapStyle) throws IOException {
        if (lineCapStyle >= 0 && lineCapStyle <= 2) {
            writeOperand(lineCapStyle);
            writeOperator("J");
            return;
        }
        throw new IllegalArgumentException("Error: unknown value for line cap style");
    }

    public void setLineDashPattern(float[] pattern, float phase) throws IOException {
        write("[");
        for (float value : pattern) {
            writeOperand(value);
        }
        write("] ");
        writeOperand(phase);
        writeOperator(OperatorName.SET_LINE_DASHPATTERN);
    }

    public void setMiterLimit(float miterLimit) throws IOException {
        if (miterLimit <= 0.0d) {
            throw new IllegalArgumentException("A miter limit <= 0 is invalid and will not render in Acrobat Reader");
        }
        writeOperand(miterLimit);
        writeOperator(OperatorName.SET_LINE_MITERLIMIT);
    }

    public void beginMarkedContent(COSName tag) throws IOException {
        writeOperand(tag);
        writeOperator(OperatorName.BEGIN_MARKED_CONTENT);
    }

    public void beginMarkedContent(COSName tag, PDPropertyList propertyList) throws IOException {
        writeOperand(tag);
        writeOperand(this.resources.add(propertyList));
        writeOperator(OperatorName.BEGIN_MARKED_CONTENT_SEQ);
    }

    public void endMarkedContent() throws IOException {
        writeOperator(OperatorName.END_MARKED_CONTENT);
    }

    public void setGraphicsStateParameters(PDExtendedGraphicsState state) throws IOException {
        writeOperand(this.resources.add(state));
        writeOperator(OperatorName.SET_GRAPHICS_STATE_PARAMS);
    }

    public void addComment(String comment) throws IOException {
        if (comment.indexOf(10) >= 0 || comment.indexOf(13) >= 0) {
            throw new IllegalArgumentException("comment should not include a newline");
        }
        this.outputStream.write(37);
        this.outputStream.write(comment.getBytes(Charsets.US_ASCII));
        this.outputStream.write(10);
    }

    protected void writeOperand(float real) throws IOException {
        if (Float.isInfinite(real) || Float.isNaN(real)) {
            throw new IllegalArgumentException(real + " is not a finite number");
        }
        int byteCount = NumberFormatUtil.formatFloatFast(real, this.formatDecimal.getMaximumFractionDigits(), this.formatBuffer);
        if (byteCount == -1) {
            write(this.formatDecimal.format(real));
        } else {
            this.outputStream.write(this.formatBuffer, 0, byteCount);
        }
        this.outputStream.write(32);
    }

    protected void writeOperand(int integer) throws IOException {
        write(this.formatDecimal.format(integer));
        this.outputStream.write(32);
    }

    protected void writeOperand(COSName name) throws IOException {
        name.writePDF(this.outputStream);
        this.outputStream.write(32);
    }

    protected void writeOperator(String text) throws IOException {
        this.outputStream.write(text.getBytes(Charsets.US_ASCII));
        this.outputStream.write(10);
    }

    protected void write(String text) throws IOException {
        this.outputStream.write(text.getBytes(Charsets.US_ASCII));
    }

    protected void write(byte[] data) throws IOException {
        this.outputStream.write(data);
    }

    protected void writeLine() throws IOException {
        this.outputStream.write(10);
    }

    protected void writeBytes(byte[] data) throws IOException {
        this.outputStream.write(data);
    }

    private void writeAffineTransform(AffineTransform transform) throws IOException {
        double[] values = new double[6];
        transform.getMatrix(values);
        for (double v : values) {
            writeOperand((float) v);
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.inTextMode) {
            LOG.warn("You did not call endText(), some viewers won't display your text");
        }
        this.outputStream.close();
    }

    protected boolean isOutside255Interval(int val) {
        return val < 0 || val > 255;
    }

    private boolean isOutsideOneInterval(double val) {
        return val < 0.0d || val > 1.0d;
    }

    protected void setStrokingColorSpaceStack(PDColorSpace colorSpace) {
        if (this.strokingColorSpaceStack.isEmpty()) {
            this.strokingColorSpaceStack.add(colorSpace);
        } else {
            this.strokingColorSpaceStack.pop();
            this.strokingColorSpaceStack.push(colorSpace);
        }
    }

    protected void setNonStrokingColorSpaceStack(PDColorSpace colorSpace) {
        if (this.nonStrokingColorSpaceStack.isEmpty()) {
            this.nonStrokingColorSpaceStack.add(colorSpace);
        } else {
            this.nonStrokingColorSpaceStack.pop();
            this.nonStrokingColorSpaceStack.push(colorSpace);
        }
    }

    public void setCharacterSpacing(float spacing) throws IOException {
        writeOperand(spacing);
        writeOperator(OperatorName.SET_CHAR_SPACING);
    }

    public void setWordSpacing(float spacing) throws IOException {
        writeOperand(spacing);
        writeOperator(OperatorName.SET_WORD_SPACING);
    }

    public void setHorizontalScaling(float scale) throws IOException {
        writeOperand(scale);
        writeOperator(OperatorName.SET_TEXT_HORIZONTAL_SCALING);
    }

    public void setRenderingMode(RenderingMode rm) throws IOException {
        writeOperand(rm.intValue());
        writeOperator(OperatorName.SET_TEXT_RENDERINGMODE);
    }

    public void setTextRise(float rise) throws IOException {
        writeOperand(rise);
        writeOperator(OperatorName.SET_TEXT_RISE);
    }
}
