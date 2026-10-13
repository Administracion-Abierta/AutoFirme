package org.apache.pdfbox.contentstream;

import com.aowagie.text.pdf.ColumnText;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.MissingOperandException;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.contentstream.operator.state.EmptyGraphicsStackException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.filter.MissingImageReaderException;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.MissingResourceException;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType3CharProc;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.pdfbox.pdmodel.graphics.PDLineDashPattern;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.PDTextState;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/PDFStreamEngine.class */
public abstract class PDFStreamEngine {
    private static final Log LOG = LogFactory.getLog(PDFStreamEngine.class);
    private Matrix textMatrix;
    private Matrix textLineMatrix;
    private PDResources resources;
    private PDPage currentPage;
    private boolean isProcessingPage;
    private Matrix initialMatrix;
    private final Map<String, OperatorProcessor> operators = new HashMap(80);
    private Deque<PDGraphicsState> graphicsStack = new ArrayDeque();
    private int level = 0;

    protected PDFStreamEngine() {
    }

    @Deprecated
    public void registerOperatorProcessor(String operator, OperatorProcessor op) {
        op.setContext(this);
        this.operators.put(operator, op);
    }

    public final void addOperator(OperatorProcessor op) {
        op.setContext(this);
        this.operators.put(op.getName(), op);
    }

    private void initPage(PDPage page) {
        if (page == null) {
            throw new IllegalArgumentException("Page cannot be null");
        }
        this.currentPage = page;
        this.graphicsStack.clear();
        this.graphicsStack.push(new PDGraphicsState(page.getCropBox()));
        this.textMatrix = null;
        this.textLineMatrix = null;
        this.resources = null;
        this.initialMatrix = page.getMatrix();
    }

    public void processPage(PDPage page) throws IOException {
        initPage(page);
        if (page.hasContents()) {
            this.isProcessingPage = true;
            processStream(page);
            this.isProcessingPage = false;
        }
    }

    public void showTransparencyGroup(PDTransparencyGroup form) throws IOException {
        processTransparencyGroup(form);
    }

    public void showForm(PDFormXObject form) throws IOException {
        if (this.currentPage == null) {
            throw new IllegalStateException("No current page, call #processChildStream(PDContentStream, PDPage) instead");
        }
        if (form.getCOSObject().getLength() > 0) {
            processStream(form);
        }
    }

    protected void processSoftMask(PDTransparencyGroup group) throws IOException {
        saveGraphicsState();
        Matrix softMaskCTM = getGraphicsState().getSoftMask().getInitialTransformationMatrix();
        getGraphicsState().setCurrentTransformationMatrix(softMaskCTM);
        processTransparencyGroup(group);
        restoreGraphicsState();
    }

    protected void processTransparencyGroup(PDTransparencyGroup group) throws IOException {
        if (this.currentPage == null) {
            throw new IllegalStateException("No current page, call #processChildStream(PDContentStream, PDPage) instead");
        }
        PDResources parent = pushResources(group);
        Deque<PDGraphicsState> savedStack = saveGraphicsStack();
        Matrix parentMatrix = this.initialMatrix;
        PDGraphicsState graphicsState = getGraphicsState();
        this.initialMatrix = graphicsState.getCurrentTransformationMatrix().m1288clone();
        graphicsState.getCurrentTransformationMatrix().concatenate(group.getMatrix());
        graphicsState.setBlendMode(BlendMode.NORMAL);
        graphicsState.setAlphaConstant(1.0d);
        graphicsState.setNonStrokeAlphaConstant(1.0d);
        graphicsState.setSoftMask(null);
        clipToRect(group.getBBox());
        processStreamOperators(group);
        this.initialMatrix = parentMatrix;
        restoreGraphicsStack(savedStack);
        popResources(parent);
    }

    protected void processType3Stream(PDType3CharProc charProc, Matrix textRenderingMatrix) throws IOException {
        if (this.currentPage == null) {
            throw new IllegalStateException("No current page, call #processChildStream(PDContentStream, PDPage) instead");
        }
        PDResources parent = pushResources(charProc);
        Deque<PDGraphicsState> savedStack = saveGraphicsStack();
        getGraphicsState().setCurrentTransformationMatrix(textRenderingMatrix);
        getGraphicsState().getCurrentTransformationMatrix().concatenate(charProc.getMatrix());
        Matrix textMatrixOld = this.textMatrix;
        this.textMatrix = new Matrix();
        Matrix textLineMatrixOld = this.textLineMatrix;
        this.textLineMatrix = new Matrix();
        processStreamOperators(charProc);
        this.textMatrix = textMatrixOld;
        this.textLineMatrix = textLineMatrixOld;
        restoreGraphicsStack(savedStack);
        popResources(parent);
    }

    protected void processAnnotation(PDAnnotation annotation, PDAppearanceStream appearance) throws IOException {
        PDResources parent = pushResources(appearance);
        Deque<PDGraphicsState> savedStack = saveGraphicsStack();
        PDRectangle bbox = appearance.getBBox();
        PDRectangle rect = annotation.getRectangle();
        if (rect != null && rect.getWidth() > ColumnText.GLOBAL_SPACE_CHAR_RATIO && rect.getHeight() > ColumnText.GLOBAL_SPACE_CHAR_RATIO && bbox != null && bbox.getWidth() > ColumnText.GLOBAL_SPACE_CHAR_RATIO && bbox.getHeight() > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            Matrix matrix = appearance.getMatrix();
            Rectangle2D transformedBox = bbox.transform(matrix).getBounds2D();
            Matrix a = Matrix.getTranslateInstance(rect.getLowerLeftX(), rect.getLowerLeftY());
            a.concatenate(Matrix.getScaleInstance((float) (((double) rect.getWidth()) / transformedBox.getWidth()), (float) (((double) rect.getHeight()) / transformedBox.getHeight())));
            a.concatenate(Matrix.getTranslateInstance((float) (-transformedBox.getX()), (float) (-transformedBox.getY())));
            Matrix aa = Matrix.concatenate(a, matrix);
            getGraphicsState().setCurrentTransformationMatrix(aa);
            clipToRect(bbox);
            this.initialMatrix = aa.m1288clone();
            processStreamOperators(appearance);
        }
        restoreGraphicsStack(savedStack);
        popResources(parent);
    }

    protected final void processTilingPattern(PDTilingPattern tilingPattern, PDColor color, PDColorSpace colorSpace) throws IOException {
        processTilingPattern(tilingPattern, color, colorSpace, tilingPattern.getMatrix());
    }

    protected final void processTilingPattern(PDTilingPattern tilingPattern, PDColor color, PDColorSpace colorSpace, Matrix patternMatrix) throws IOException {
        PDResources parent = pushResources(tilingPattern);
        Matrix parentMatrix = this.initialMatrix;
        this.initialMatrix = Matrix.concatenate(this.initialMatrix, patternMatrix);
        Deque<PDGraphicsState> savedStack = saveGraphicsStack();
        Rectangle2D bbox = tilingPattern.getBBox().transform(patternMatrix).getBounds2D();
        PDRectangle rect = new PDRectangle((float) bbox.getX(), (float) bbox.getY(), (float) bbox.getWidth(), (float) bbox.getHeight());
        this.graphicsStack.push(new PDGraphicsState(rect));
        PDGraphicsState graphicsState = getGraphicsState();
        if (colorSpace != null) {
            PDColor color2 = new PDColor(color.getComponents(), colorSpace);
            graphicsState.setNonStrokingColorSpace(colorSpace);
            graphicsState.setNonStrokingColor(color2);
            graphicsState.setStrokingColorSpace(colorSpace);
            graphicsState.setStrokingColor(color2);
        }
        graphicsState.getCurrentTransformationMatrix().concatenate(patternMatrix);
        clipToRect(tilingPattern.getBBox());
        Matrix textMatrixSave = this.textMatrix;
        Matrix textLineMatrixSave = this.textLineMatrix;
        processStreamOperators(tilingPattern);
        this.textMatrix = textMatrixSave;
        this.textLineMatrix = textLineMatrixSave;
        this.initialMatrix = parentMatrix;
        restoreGraphicsStack(savedStack);
        popResources(parent);
    }

    public void showAnnotation(PDAnnotation annotation) throws IOException {
        PDAppearanceStream appearanceStream = getAppearance(annotation);
        if (appearanceStream != null) {
            processAnnotation(annotation, appearanceStream);
        }
    }

    public PDAppearanceStream getAppearance(PDAnnotation annotation) {
        return annotation.getNormalAppearanceStream();
    }

    protected void processChildStream(PDContentStream contentStream, PDPage page) throws IOException {
        if (this.isProcessingPage) {
            throw new IllegalStateException("Current page has already been set via  #processPage(PDPage) call #processChildStream(PDContentStream) instead");
        }
        initPage(page);
        processStream(contentStream);
        this.currentPage = null;
    }

    private void processStream(PDContentStream contentStream) throws IOException {
        PDResources parent = pushResources(contentStream);
        Deque<PDGraphicsState> savedStack = saveGraphicsStack();
        Matrix parentMatrix = this.initialMatrix;
        PDGraphicsState graphicsState = getGraphicsState();
        graphicsState.getCurrentTransformationMatrix().concatenate(contentStream.getMatrix());
        this.initialMatrix = graphicsState.getCurrentTransformationMatrix().m1288clone();
        PDRectangle bbox = contentStream.getBBox();
        clipToRect(bbox);
        processStreamOperators(contentStream);
        this.initialMatrix = parentMatrix;
        restoreGraphicsStack(savedStack);
        popResources(parent);
    }

    private void processStreamOperators(PDContentStream contentStream) throws IOException {
        List<COSBase> arguments = new ArrayList<>();
        PDFStreamParser parser = new PDFStreamParser(contentStream);
        Object nextToken = parser.parseNextToken();
        while (true) {
            Object token = nextToken;
            if (token != null) {
                if (token instanceof Operator) {
                    processOperator((Operator) token, arguments);
                    arguments.clear();
                } else {
                    arguments.add((COSBase) token);
                }
                nextToken = parser.parseNextToken();
            } else {
                return;
            }
        }
    }

    private PDResources pushResources(PDContentStream contentStream) {
        PDResources parentResources = this.resources;
        PDResources streamResources = contentStream.getResources();
        if (streamResources != null) {
            this.resources = streamResources;
        } else if (this.resources == null) {
            this.resources = this.currentPage.getResources();
            if (this.resources == null) {
                this.resources = new PDResources();
            }
        }
        return parentResources;
    }

    private void popResources(PDResources parentResources) {
        this.resources = parentResources;
    }

    private void clipToRect(PDRectangle rectangle) {
        if (rectangle != null) {
            PDGraphicsState graphicsState = getGraphicsState();
            GeneralPath clip = rectangle.transform(graphicsState.getCurrentTransformationMatrix());
            graphicsState.intersectClippingPath(clip);
        }
    }

    public void beginText() throws IOException {
    }

    public void endText() throws IOException {
    }

    public void showTextString(byte[] string) throws IOException {
        showText(string);
    }

    public void showTextStrings(COSArray array) throws IOException {
        float tx;
        float f;
        PDTextState textState = getGraphicsState().getTextState();
        float fontSize = textState.getFontSize();
        float horizontalScaling = textState.getHorizontalScaling() / 100.0f;
        PDFont font = textState.getFont();
        boolean isVertical = false;
        if (font != null) {
            isVertical = font.isVertical();
        }
        for (COSBase obj : array) {
            if (obj instanceof COSNumber) {
                float tj = ((COSNumber) obj).floatValue();
                if (isVertical) {
                    tx = 0.0f;
                    f = ((-tj) / 1000.0f) * fontSize;
                } else {
                    tx = ((-tj) / 1000.0f) * fontSize * horizontalScaling;
                    f = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
                }
                float ty = f;
                applyTextAdjustment(tx, ty);
            } else if (obj instanceof COSString) {
                byte[] string = ((COSString) obj).getBytes();
                showText(string);
            } else if (obj instanceof COSArray) {
                LOG.error("Nested arrays are not allowed in an array for TJ operation:" + obj);
            } else {
                throw new IOException("Unknown type " + obj.getClass().getSimpleName() + " in array for TJ operation:" + obj);
            }
        }
    }

    protected void applyTextAdjustment(float tx, float ty) throws IOException {
        this.textMatrix.concatenate(Matrix.getTranslateInstance(tx, ty));
    }

    protected void showText(byte[] string) throws IOException {
        float tx;
        float y;
        PDGraphicsState state = getGraphicsState();
        PDTextState textState = state.getTextState();
        PDFont font = textState.getFont();
        if (font == null) {
            LOG.warn("No current font, will use default");
            font = PDType1Font.HELVETICA;
        }
        float fontSize = textState.getFontSize();
        float horizontalScaling = textState.getHorizontalScaling() / 100.0f;
        float charSpacing = textState.getCharacterSpacing();
        Matrix parameters = new Matrix(fontSize * horizontalScaling, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, fontSize, ColumnText.GLOBAL_SPACE_CHAR_RATIO, textState.getRise());
        InputStream in = new ByteArrayInputStream(string);
        while (in.available() > 0) {
            int before = in.available();
            int code = font.readCode(in);
            int codeLength = before - in.available();
            float wordSpacing = 0.0f;
            if (codeLength == 1 && code == 32) {
                wordSpacing = ColumnText.GLOBAL_SPACE_CHAR_RATIO + textState.getWordSpacing();
            }
            Matrix ctm = state.getCurrentTransformationMatrix();
            Matrix textRenderingMatrix = parameters.multiply(this.textMatrix).multiply(ctm);
            if (font.isVertical()) {
                Vector v = font.getPositionVector(code);
                textRenderingMatrix.translate(v);
            }
            Vector w = font.getDisplacement(code);
            showGlyph(textRenderingMatrix, font, code, w);
            if (font.isVertical()) {
                tx = 0.0f;
                y = (w.getY() * fontSize) + charSpacing + wordSpacing;
            } else {
                tx = ((w.getX() * fontSize) + charSpacing + wordSpacing) * horizontalScaling;
                y = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            }
            float ty = y;
            this.textMatrix.concatenate(Matrix.getTranslateInstance(tx, ty));
        }
    }

    protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode, Vector displacement) throws IOException {
        if (font instanceof PDType3Font) {
            showType3Glyph(textRenderingMatrix, (PDType3Font) font, code, displacement);
        } else {
            showFontGlyph(textRenderingMatrix, font, code, displacement);
        }
    }

    protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, Vector displacement) throws IOException {
        showGlyph(textRenderingMatrix, font, code, font.toUnicode(code), displacement);
    }

    protected void showFontGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode, Vector displacement) throws IOException {
    }

    protected void showFontGlyph(Matrix textRenderingMatrix, PDFont font, int code, Vector displacement) throws IOException {
        showFontGlyph(textRenderingMatrix, font, code, font.toUnicode(code), displacement);
    }

    protected void showType3Glyph(Matrix textRenderingMatrix, PDType3Font font, int code, String unicode, Vector displacement) throws IOException {
        PDType3CharProc charProc = font.getCharProc(code);
        if (charProc != null) {
            processType3Stream(charProc, textRenderingMatrix);
        }
    }

    protected void showType3Glyph(Matrix textRenderingMatrix, PDType3Font font, int code, Vector displacement) throws IOException {
        showType3Glyph(textRenderingMatrix, font, code, font.toUnicode(code), displacement);
    }

    public void beginMarkedContentSequence(COSName tag, COSDictionary properties) {
    }

    public void endMarkedContentSequence() {
    }

    public void processOperator(String operation, List<COSBase> arguments) throws IOException {
        Operator operator = Operator.getOperator(operation);
        processOperator(operator, arguments);
    }

    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
        String name = operator.getName();
        OperatorProcessor processor = this.operators.get(name);
        if (processor != null) {
            processor.setContext(this);
            try {
                processor.process(operator, operands);
                return;
            } catch (IOException e) {
                operatorException(operator, operands, e);
                return;
            }
        }
        unsupportedOperator(operator, operands);
    }

    protected void unsupportedOperator(Operator operator, List<COSBase> operands) throws IOException {
    }

    protected void operatorException(Operator operator, List<COSBase> operands, IOException e) throws IOException {
        if ((e instanceof MissingOperandException) || (e instanceof MissingResourceException) || (e instanceof MissingImageReaderException)) {
            LOG.error(e.getMessage());
        } else if (e instanceof EmptyGraphicsStackException) {
            LOG.warn(e.getMessage());
        } else {
            if (operator.getName().equals(OperatorName.DRAW_OBJECT)) {
                LOG.warn(e.getMessage());
                return;
            }
            throw e;
        }
    }

    public void saveGraphicsState() {
        this.graphicsStack.push(this.graphicsStack.peek().m1220clone());
    }

    public void restoreGraphicsState() {
        this.graphicsStack.pop();
    }

    protected final Deque<PDGraphicsState> saveGraphicsStack() {
        Deque<PDGraphicsState> savedStack = this.graphicsStack;
        this.graphicsStack = new ArrayDeque();
        this.graphicsStack.add(savedStack.peek().m1220clone());
        return savedStack;
    }

    protected final void restoreGraphicsStack(Deque<PDGraphicsState> snapshot) {
        this.graphicsStack = snapshot;
    }

    public int getGraphicsStackSize() {
        return this.graphicsStack.size();
    }

    public PDGraphicsState getGraphicsState() {
        return this.graphicsStack.peek();
    }

    public Matrix getTextLineMatrix() {
        return this.textLineMatrix;
    }

    public void setTextLineMatrix(Matrix value) {
        this.textLineMatrix = value;
    }

    public Matrix getTextMatrix() {
        return this.textMatrix;
    }

    public void setTextMatrix(Matrix value) {
        this.textMatrix = value;
    }

    public void setLineDashPattern(COSArray array, int phase) {
        if (phase < 0) {
            LOG.warn("Dash phase has negative value " + phase + ", set to 0");
            phase = 0;
        }
        PDLineDashPattern lineDash = new PDLineDashPattern(array, phase);
        getGraphicsState().setLineDashPattern(lineDash);
    }

    public PDResources getResources() {
        return this.resources;
    }

    public PDPage getCurrentPage() {
        return this.currentPage;
    }

    public Matrix getInitialMatrix() {
        return this.initialMatrix;
    }

    public Point2D.Float transformedPoint(float x, float y) {
        float[] position = {x, y};
        getGraphicsState().getCurrentTransformationMatrix().createAffineTransform().transform(position, 0, position, 0, 1);
        return new Point2D.Float(position[0], position[1]);
    }

    protected float transformWidth(float width) {
        Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
        float x = ctm.getScaleX() + ctm.getShearX();
        float y = ctm.getScaleY() + ctm.getShearY();
        return width * ((float) Math.sqrt(((double) ((x * x) + (y * y))) * 0.5d));
    }

    public int getLevel() {
        return this.level;
    }

    public void increaseLevel() {
        this.level++;
    }

    public void decreaseLevel() {
        this.level--;
        if (this.level < 0) {
            LOG.error("level is " + this.level);
        }
    }
}
