package com.aowagie.text.pdf;

import com.aowagie.text.Annotation;
import com.aowagie.text.DocumentException;
import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Image;
import com.aowagie.text.ImgJBIG2;
import com.aowagie.text.Rectangle;
import com.aowagie.text.exceptions.IllegalPdfSyntaxException;
import com.aowagie.text.pdf.internal.PdfAnnotationsImp;
import com.aowagie.text.pdf.internal.PdfXConformanceImp;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfContentByte.class */
public class PdfContentByte {
    static final int ALIGN_CENTER = 1;
    static final int ALIGN_LEFT = 0;
    private static final int ALIGN_RIGHT = 2;
    public static final int LINE_CAP_BUTT = 0;
    private static final int LINE_JOIN_MITER = 0;
    static final int TEXT_RENDER_MODE_FILL = 0;
    static final int TEXT_RENDER_MODE_STROKE = 1;
    static final int TEXT_RENDER_MODE_FILL_STROKE = 2;
    protected PdfWriter writer;
    protected PdfDocument pdf;
    private ArrayList layerDepth;
    private static final float[] unitRect = {ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f, 1.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f, 1.0f};
    private static HashMap abrev = new LinkedHashMap();
    protected ByteBuffer content = new ByteBuffer();
    protected GraphicState state = new GraphicState();
    private final ArrayList stateList = new ArrayList();
    protected int separator = 10;
    private int mcDepth = 0;
    private boolean inText = false;

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfContentByte$GraphicState.class */
    static class GraphicState {
        FontDetails fontDetails;
        private ColorDetails colorDetails;
        float size;
        private float xTLM;
        private float yTLM;
        private float leading;
        private float scale;
        private float charSpace;
        private float wordSpace;

        GraphicState() {
            this.xTLM = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.yTLM = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.leading = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.scale = 100.0f;
            this.charSpace = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.wordSpace = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }

        private GraphicState(GraphicState cp) {
            this.xTLM = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.yTLM = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.leading = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.scale = 100.0f;
            this.charSpace = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.wordSpace = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
            this.fontDetails = cp.fontDetails;
            this.colorDetails = cp.colorDetails;
            this.size = cp.size;
            this.xTLM = cp.xTLM;
            this.yTLM = cp.yTLM;
            this.leading = cp.leading;
            this.scale = cp.scale;
            this.charSpace = cp.charSpace;
            this.wordSpace = cp.wordSpace;
        }
    }

    static {
        abrev.put(PdfName.BITSPERCOMPONENT, "/BPC ");
        abrev.put(PdfName.COLORSPACE, "/CS ");
        abrev.put(PdfName.DECODE, "/D ");
        abrev.put(PdfName.DECODEPARMS, "/DP ");
        abrev.put(PdfName.FILTER, "/F ");
        abrev.put(PdfName.HEIGHT, "/H ");
        abrev.put(PdfName.IMAGEMASK, "/IM ");
        abrev.put(PdfName.INTENT, "/Intent ");
        abrev.put(PdfName.INTERPOLATE, "/I ");
        abrev.put(PdfName.WIDTH, "/W ");
    }

    PdfContentByte(PdfWriter wr) {
        if (wr != null) {
            this.writer = wr;
            this.pdf = this.writer.getPdfDocument();
        }
    }

    public String toString() {
        return this.content.toString();
    }

    public ByteBuffer getInternalBuffer() {
        return this.content;
    }

    byte[] toPdf(PdfWriter writer) {
        sanityCheck();
        return this.content.toByteArray();
    }

    void add(PdfContentByte other) {
        if (other.writer != null && this.writer != other.writer) {
            throw new RuntimeException("Inconsistent writers. Are you mixing two documents?");
        }
        this.content.append(other.content);
    }

    public float getXTLM() {
        return this.state.xTLM;
    }

    public float getYTLM() {
        return this.state.yTLM;
    }

    public float getLeading() {
        return this.state.leading;
    }

    public float getCharacterSpacing() {
        return this.state.charSpace;
    }

    public float getWordSpacing() {
        return this.state.wordSpace;
    }

    public float getHorizontalScaling() {
        return this.state.scale;
    }

    public void setFlatness(float flatness) {
        if (flatness >= ColumnText.GLOBAL_SPACE_CHAR_RATIO && flatness <= 100.0f) {
            this.content.append(flatness).append(" i").append_i(this.separator);
        }
    }

    public void setLineCap(int style) {
        if (style >= 0 && style <= 2) {
            this.content.append(style).append(" J").append_i(this.separator);
        }
    }

    public void setLineDash(float phase) {
        this.content.append("[] ").append(phase).append(" d").append_i(this.separator);
    }

    public void setLineDash(float unitsOn, float phase) {
        this.content.append("[").append(unitsOn).append("] ").append(phase).append(" d").append_i(this.separator);
    }

    public void setLineDash(float unitsOn, float unitsOff, float phase) {
        this.content.append("[").append(unitsOn).append(' ').append(unitsOff).append("] ").append(phase).append(" d").append_i(this.separator);
    }

    public void setLineJoin(int style) {
        if (style >= 0 && style <= 2) {
            this.content.append(style).append(" j").append_i(this.separator);
        }
    }

    public void setLineWidth(float w) {
        this.content.append(w).append(" w").append_i(this.separator);
    }

    public void setMiterLimit(float miterLimit) {
        if (miterLimit > 1.0f) {
            this.content.append(miterLimit).append(" M").append_i(this.separator);
        }
    }

    public void clip() {
        this.content.append("W").append_i(this.separator);
    }

    public void eoClip() {
        this.content.append(OperatorName.CLIP_EVEN_ODD).append_i(this.separator);
    }

    public void setGrayFill(float gray) {
        this.content.append(gray).append(" g").append_i(this.separator);
    }

    public void resetGrayFill() {
        this.content.append("0 g").append_i(this.separator);
    }

    public void setGrayStroke(float gray) {
        this.content.append(gray).append(" G").append_i(this.separator);
    }

    public void resetGrayStroke() {
        this.content.append("0 G").append_i(this.separator);
    }

    private void HelperRGB(float red, float green, float blue) {
        PdfXConformanceImp.checkPDFXConformance(this.writer, 3, null);
        if (red < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            red = 0.0f;
        } else if (red > 1.0f) {
            red = 1.0f;
        }
        if (green < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            green = 0.0f;
        } else if (green > 1.0f) {
            green = 1.0f;
        }
        if (blue < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            blue = 0.0f;
        } else if (blue > 1.0f) {
            blue = 1.0f;
        }
        this.content.append(red).append(' ').append(green).append(' ').append(blue);
    }

    public void setRGBColorFillF(float red, float green, float blue) {
        HelperRGB(red, green, blue);
        this.content.append(" rg").append_i(this.separator);
    }

    public void resetRGBColorFill() {
        this.content.append("0 g").append_i(this.separator);
    }

    public void setRGBColorStrokeF(float red, float green, float blue) {
        HelperRGB(red, green, blue);
        this.content.append(" RG").append_i(this.separator);
    }

    public void resetRGBColorStroke() {
        this.content.append("0 G").append_i(this.separator);
    }

    private void HelperCMYK(float cyan, float magenta, float yellow, float black) {
        if (cyan < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            cyan = 0.0f;
        } else if (cyan > 1.0f) {
            cyan = 1.0f;
        }
        if (magenta < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            magenta = 0.0f;
        } else if (magenta > 1.0f) {
            magenta = 1.0f;
        }
        if (yellow < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            yellow = 0.0f;
        } else if (yellow > 1.0f) {
            yellow = 1.0f;
        }
        if (black < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            black = 0.0f;
        } else if (black > 1.0f) {
            black = 1.0f;
        }
        this.content.append(cyan).append(' ').append(magenta).append(' ').append(yellow).append(' ').append(black);
    }

    public void setCMYKColorFillF(float cyan, float magenta, float yellow, float black) {
        HelperCMYK(cyan, magenta, yellow, black);
        this.content.append(" k").append_i(this.separator);
    }

    public void resetCMYKColorFill() {
        this.content.append("0 0 0 1 k").append_i(this.separator);
    }

    public void setCMYKColorStrokeF(float cyan, float magenta, float yellow, float black) {
        HelperCMYK(cyan, magenta, yellow, black);
        this.content.append(" K").append_i(this.separator);
    }

    public void resetCMYKColorStroke() {
        this.content.append("0 0 0 1 K").append_i(this.separator);
    }

    public void moveTo(float x, float y) {
        this.content.append(x).append(' ').append(y).append(" m").append_i(this.separator);
    }

    public void lineTo(float x, float y) {
        this.content.append(x).append(' ').append(y).append(" l").append_i(this.separator);
    }

    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        this.content.append(x1).append(' ').append(y1).append(' ').append(x2).append(' ').append(y2).append(' ').append(x3).append(' ').append(y3).append(" c").append_i(this.separator);
    }

    void curveTo(float x2, float y2, float x3, float y3) {
        this.content.append(x2).append(' ').append(y2).append(' ').append(x3).append(' ').append(y3).append(" v").append_i(this.separator);
    }

    public void rectangle(float x, float y, float w, float h) {
        this.content.append(x).append(' ').append(y).append(' ').append(w).append(' ').append(h).append(" re").append_i(this.separator);
    }

    private boolean compareColors(Color c1, Color c2) {
        if (c1 == null && c2 == null) {
            return true;
        }
        if (c1 == null || c2 == null) {
            return false;
        }
        if (c1 instanceof ExtendedColor) {
            return c1.equals(c2);
        }
        return c2.equals(c1);
    }

    private void variableRectangle(Rectangle rect) {
        float t = rect.getTop();
        float b = rect.getBottom();
        float r = rect.getRight();
        float l = rect.getLeft();
        float wt = rect.getBorderWidthTop();
        float wb = rect.getBorderWidthBottom();
        float wr = rect.getBorderWidthRight();
        float wl = rect.getBorderWidthLeft();
        Color ct = rect.getBorderColorTop();
        Color cb = rect.getBorderColorBottom();
        Color cr = rect.getBorderColorRight();
        Color cl = rect.getBorderColorLeft();
        saveState();
        setLineCap(0);
        setLineJoin(0);
        float clw = 0.0f;
        boolean cdef = false;
        Color ccol = null;
        boolean cdefi = false;
        Color cfil = null;
        if (wt > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            clw = wt;
            setLineWidth(wt);
            cdef = true;
            if (ct == null) {
                resetRGBColorStroke();
            } else {
                setColorStroke(ct);
            }
            ccol = ct;
            moveTo(l, t - (wt / 2.0f));
            lineTo(r, t - (wt / 2.0f));
            stroke();
        }
        if (wb > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            if (wb != clw) {
                clw = wb;
                setLineWidth(wb);
            }
            if (!cdef || !compareColors(ccol, cb)) {
                cdef = true;
                if (cb == null) {
                    resetRGBColorStroke();
                } else {
                    setColorStroke(cb);
                }
                ccol = cb;
            }
            moveTo(r, b + (wb / 2.0f));
            lineTo(l, b + (wb / 2.0f));
            stroke();
        }
        if (wr > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            if (wr != clw) {
                clw = wr;
                setLineWidth(wr);
            }
            if (!cdef || !compareColors(ccol, cr)) {
                cdef = true;
                if (cr == null) {
                    resetRGBColorStroke();
                } else {
                    setColorStroke(cr);
                }
                ccol = cr;
            }
            boolean bt = compareColors(ct, cr);
            boolean bb = compareColors(cb, cr);
            moveTo(r - (wr / 2.0f), bt ? t : t - wt);
            lineTo(r - (wr / 2.0f), bb ? b : b + wb);
            stroke();
            if (!bt || !bb) {
                cdefi = true;
                if (cr == null) {
                    resetRGBColorFill();
                } else {
                    setColorFill(cr);
                }
                cfil = cr;
                if (!bt) {
                    moveTo(r, t);
                    lineTo(r, t - wt);
                    lineTo(r - wr, t - wt);
                    fill();
                }
                if (!bb) {
                    moveTo(r, b);
                    lineTo(r, b + wb);
                    lineTo(r - wr, b + wb);
                    fill();
                }
            }
        }
        if (wl > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            if (wl != clw) {
                setLineWidth(wl);
            }
            if (!cdef || !compareColors(ccol, cl)) {
                if (cl == null) {
                    resetRGBColorStroke();
                } else {
                    setColorStroke(cl);
                }
            }
            boolean bt2 = compareColors(ct, cl);
            boolean bb2 = compareColors(cb, cl);
            moveTo(l + (wl / 2.0f), bt2 ? t : t - wt);
            lineTo(l + (wl / 2.0f), bb2 ? b : b + wb);
            stroke();
            if (!bt2 || !bb2) {
                if (!cdefi || !compareColors(cfil, cl)) {
                    if (cl == null) {
                        resetRGBColorFill();
                    } else {
                        setColorFill(cl);
                    }
                }
                if (!bt2) {
                    moveTo(l, t);
                    lineTo(l, t - wt);
                    lineTo(l + wl, t - wt);
                    fill();
                }
                if (!bb2) {
                    moveTo(l, b);
                    lineTo(l, b + wb);
                    lineTo(l + wl, b + wb);
                    fill();
                }
            }
        }
        restoreState();
    }

    public void rectangle(Rectangle rectangle) {
        float x1 = rectangle.getLeft();
        float y1 = rectangle.getBottom();
        float x2 = rectangle.getRight();
        float y2 = rectangle.getTop();
        Color background = rectangle.getBackgroundColor();
        if (background != null) {
            setColorFill(background);
            rectangle(x1, y1, x2 - x1, y2 - y1);
            fill();
            resetRGBColorFill();
        }
        if (!rectangle.hasBorders()) {
            return;
        }
        if (rectangle.isUseVariableBorders()) {
            variableRectangle(rectangle);
            return;
        }
        if (rectangle.getBorderWidth() != -1.0f) {
            setLineWidth(rectangle.getBorderWidth());
        }
        Color color = rectangle.getBorderColor();
        if (color != null) {
            setColorStroke(color);
        }
        if (rectangle.hasBorder(15)) {
            rectangle(x1, y1, x2 - x1, y2 - y1);
        } else {
            if (rectangle.hasBorder(8)) {
                moveTo(x2, y1);
                lineTo(x2, y2);
            }
            if (rectangle.hasBorder(4)) {
                moveTo(x1, y1);
                lineTo(x1, y2);
            }
            if (rectangle.hasBorder(2)) {
                moveTo(x1, y1);
                lineTo(x2, y1);
            }
            if (rectangle.hasBorder(1)) {
                moveTo(x1, y2);
                lineTo(x2, y2);
            }
        }
        stroke();
        if (color != null) {
            resetRGBColorStroke();
        }
    }

    public void closePath() {
        this.content.append(OperatorName.CLOSE_PATH).append_i(this.separator);
    }

    public void newPath() {
        this.content.append(OperatorName.ENDPATH).append_i(this.separator);
    }

    public void stroke() {
        this.content.append("S").append_i(this.separator);
    }

    public void closePathStroke() {
        this.content.append(OperatorName.CLOSE_AND_STROKE).append_i(this.separator);
    }

    public void fill() {
        this.content.append(OperatorName.FILL_NON_ZERO).append_i(this.separator);
    }

    public void eoFill() {
        this.content.append(OperatorName.FILL_EVEN_ODD).append_i(this.separator);
    }

    public void closePathFillStroke() {
        this.content.append(OperatorName.CLOSE_FILL_NON_ZERO_AND_STROKE).append_i(this.separator);
    }

    public void closePathEoFillStroke() {
        this.content.append(OperatorName.CLOSE_FILL_EVEN_ODD_AND_STROKE).append_i(this.separator);
    }

    public void addImage(Image image) throws DocumentException {
        addImage(image, false);
    }

    private void addImage(Image image, boolean inlineImage) throws DocumentException {
        if (!image.hasAbsoluteY()) {
            throw new DocumentException("The image must have absolute positioning.");
        }
        float[] matrix = image.matrix();
        matrix[4] = image.getAbsoluteX() - matrix[4];
        matrix[5] = image.getAbsoluteY() - matrix[5];
        addImage(image, matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5], inlineImage);
    }

    public void addImage(Image image, float a, float b, float c, float d, float e, float f) throws DocumentException {
        addImage(image, a, b, c, d, e, f, false);
    }

    public void addImage(Image image, float a, float b, float c, float d, float e, float f, boolean inlineImage) throws DocumentException {
        byte[] globals;
        try {
            if (image.getLayer() != null) {
                beginLayer(image.getLayer());
            }
            if (image.isImgTemplate()) {
                this.writer.addDirectImageSimple(image);
                PdfTemplate template = image.getTemplateData();
                float w = template.getWidth();
                float h = template.getHeight();
                addTemplate(template, a / w, b / w, c / h, d / h, e, f);
            } else {
                this.content.append("q ");
                this.content.append(a).append(' ');
                this.content.append(b).append(' ');
                this.content.append(c).append(' ');
                this.content.append(d).append(' ');
                this.content.append(e).append(' ');
                this.content.append(f).append(" cm");
                if (inlineImage) {
                    this.content.append("\nBI\n");
                    PdfImage pimage = new PdfImage(image, "", null);
                    if ((image instanceof ImgJBIG2) && (globals = ((ImgJBIG2) image).getGlobalBytes()) != null) {
                        PdfDictionary decodeparms = new PdfDictionary();
                        decodeparms.put(PdfName.JBIG2GLOBALS, this.writer.getReferenceJBIG2Globals(globals));
                        pimage.put(PdfName.DECODEPARMS, decodeparms);
                    }
                    for (Object element : pimage.getKeys()) {
                        PdfName key = (PdfName) element;
                        PdfObject value = pimage.get(key);
                        String s = (String) abrev.get(key);
                        if (s != null) {
                            this.content.append(s);
                            boolean check = true;
                            if (key.equals(PdfName.COLORSPACE) && value.isArray()) {
                                PdfArray ar = (PdfArray) value;
                                if (ar.size() == 4 && PdfName.INDEXED.equals(ar.getAsName(0)) && ar.getPdfObject(1).isName() && ar.getPdfObject(2).isNumber() && ar.getPdfObject(3).isString()) {
                                    check = false;
                                }
                            }
                            if (check && key.equals(PdfName.COLORSPACE) && !value.isName()) {
                                PdfName cs = this.writer.getColorspaceName();
                                getPageResources().addColor(cs, this.writer.addToBody(value).getIndirectReference());
                                value = cs;
                            }
                            value.toPdf(null, this.content);
                            this.content.append('\n');
                        }
                    }
                    this.content.append("ID\n");
                    pimage.writeContent(this.content);
                    this.content.append("\nEI\nQ").append_i(this.separator);
                } else {
                    PageResources prs = getPageResources();
                    Image maskImage = image.getImageMask();
                    if (maskImage != null) {
                        PdfName name = this.writer.addDirectImageSimple(maskImage);
                        prs.addXObject(name, this.writer.getImageReference(name));
                    }
                    PdfName name2 = this.writer.addDirectImageSimple(image);
                    this.content.append(' ').append(prs.addXObject(name2, this.writer.getImageReference(name2)).getBytes()).append(" Do Q").append_i(this.separator);
                }
            }
            if (image.hasBorders()) {
                saveState();
                float w2 = image.getWidth();
                float h2 = image.getHeight();
                concatCTM(a / w2, b / w2, c / h2, d / h2, e, f);
                rectangle(image);
                restoreState();
            }
            if (image.getLayer() != null) {
                endLayer();
            }
            Annotation annot = image.getAnnotation();
            if (annot == null) {
                return;
            }
            float[] r = new float[unitRect.length];
            for (int k = 0; k < unitRect.length; k += 2) {
                r[k] = (a * unitRect[k]) + (c * unitRect[k + 1]) + e;
                r[k + 1] = (b * unitRect[k]) + (d * unitRect[k + 1]) + f;
            }
            float llx = r[0];
            float lly = r[1];
            float urx = llx;
            float ury = lly;
            for (int k2 = 2; k2 < r.length; k2 += 2) {
                llx = Math.min(llx, r[k2]);
                lly = Math.min(lly, r[k2 + 1]);
                urx = Math.max(urx, r[k2]);
                ury = Math.max(ury, r[k2 + 1]);
            }
            Annotation annot2 = new Annotation(annot);
            annot2.setDimensions(llx, lly, urx, ury);
            PdfAnnotation an = PdfAnnotationsImp.convertAnnotation(this.writer, annot2, new Rectangle(llx, lly, urx, ury));
            if (an == null) {
                return;
            }
            addAnnotation(an);
        } catch (Exception ee) {
            throw new DocumentException(ee);
        }
    }

    public void reset() {
        reset(true);
    }

    private void reset(boolean validateContent) {
        this.content.reset();
        if (validateContent) {
            sanityCheck();
        }
        this.state = new GraphicState();
    }

    public void beginText() {
        if (this.inText) {
            throw new IllegalPdfSyntaxException("Unbalanced begin/end text operators.");
        }
        this.inText = true;
        this.state.xTLM = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.state.yTLM = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.content.append(OperatorName.BEGIN_TEXT).append_i(this.separator);
    }

    public void endText() {
        if (!this.inText) {
            throw new IllegalPdfSyntaxException("Unbalanced begin/end text operators.");
        }
        this.inText = false;
        this.content.append(OperatorName.END_TEXT).append_i(this.separator);
    }

    public void saveState() {
        this.content.append(OperatorName.SAVE).append_i(this.separator);
        this.stateList.add(new GraphicState(this.state));
    }

    public void restoreState() {
        this.content.append("Q").append_i(this.separator);
        int idx = this.stateList.size() - 1;
        if (idx < 0) {
            throw new IllegalPdfSyntaxException("Unbalanced save/restore state operators.");
        }
        this.state = (GraphicState) this.stateList.get(idx);
        this.stateList.remove(idx);
    }

    public void setCharacterSpacing(float charSpace) {
        this.state.charSpace = charSpace;
        this.content.append(charSpace).append(" Tc").append_i(this.separator);
    }

    public void setWordSpacing(float wordSpace) {
        this.state.wordSpace = wordSpace;
        this.content.append(wordSpace).append(" Tw").append_i(this.separator);
    }

    public void setHorizontalScaling(float scale) {
        this.state.scale = scale;
        this.content.append(scale).append(" Tz").append_i(this.separator);
    }

    public void setLeading(float leading) {
        this.state.leading = leading;
        this.content.append(leading).append(" TL").append_i(this.separator);
    }

    public void setFontAndSize(BaseFont bf, float size) {
        checkWriter();
        if (size < 1.0E-4f && size > -1.0E-4f) {
            throw new IllegalArgumentException("Font size too small: " + size);
        }
        this.state.size = size;
        this.state.fontDetails = this.writer.addSimple(bf);
        PageResources prs = getPageResources();
        PdfName name = this.state.fontDetails.getFontName();
        this.content.append(prs.addFont(name, this.state.fontDetails.getIndirectReference()).getBytes()).append(' ').append(size).append(" Tf").append_i(this.separator);
    }

    public void setTextRenderingMode(int rendering) {
        this.content.append(rendering).append(" Tr").append_i(this.separator);
    }

    public void setTextRise(float rise) {
        this.content.append(rise).append(" Ts").append_i(this.separator);
    }

    private void showText2(String text) {
        if (this.state.fontDetails == null) {
            throw new NullPointerException("Font and size must be set before writing any text");
        }
        byte[] b = this.state.fontDetails.convertToBytes(text);
        escapeString(b, this.content);
    }

    public void showText(String text) {
        showText2(text);
        this.content.append(OperatorName.SHOW_TEXT).append_i(this.separator);
    }

    private static PdfTextArray getKernArray(String text, BaseFont font) {
        PdfTextArray pa = new PdfTextArray();
        StringBuffer acc = new StringBuffer();
        int len = text.length() - 1;
        char[] c = text.toCharArray();
        if (len >= 0) {
            acc.append(c, 0, 1);
        }
        for (int k = 0; k < len; k++) {
            char c2 = c[k + 1];
            int kern = font.getKerning(c[k], c2);
            if (kern == 0) {
                acc.append(c2);
            } else {
                pa.add(acc.toString());
                acc.setLength(0);
                acc.append(c, k + 1, 1);
                pa.add(-kern);
            }
        }
        pa.add(acc.toString());
        return pa;
    }

    private void showTextKerned(String text) {
        if (this.state.fontDetails == null) {
            throw new NullPointerException("Font and size must be set before writing any text");
        }
        BaseFont bf = this.state.fontDetails.getBaseFont();
        if (bf.hasKernPairs()) {
            showText(getKernArray(text, bf));
        } else {
            showText(text);
        }
    }

    void setTextMatrix(float a, float b, float c, float d, float x, float y) {
        this.state.xTLM = x;
        this.state.yTLM = y;
        this.content.append(a).append(' ').append(b).append_i(32).append(c).append_i(32).append(d).append_i(32).append(x).append_i(32).append(y).append(" Tm").append_i(this.separator);
    }

    public void setTextMatrix(float x, float y) {
        setTextMatrix(1.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f, x, y);
    }

    void moveText(float x, float y) {
        this.state.xTLM += x;
        this.state.yTLM += y;
        this.content.append(x).append(' ').append(y).append(" Td").append_i(this.separator);
    }

    int size() {
        return this.content.size();
    }

    static byte[] escapeString(byte[] b) {
        ByteBuffer content = new ByteBuffer();
        escapeString(b, content);
        return content.toByteArray();
    }

    private static void escapeString(byte[] b, ByteBuffer content) {
        content.append_i(40);
        for (byte c : b) {
            switch (c) {
                case 8:
                    content.append("\\b");
                    break;
                case 9:
                    content.append("\\t");
                    break;
                case 10:
                    content.append("\\n");
                    break;
                case 12:
                    content.append("\\f");
                    break;
                case 13:
                    content.append("\\r");
                    break;
                case 40:
                case 41:
                case 92:
                    content.append_i(92).append_i(c);
                    break;
                default:
                    content.append_i(c);
                    break;
            }
        }
        content.append(")");
    }

    public PdfOutline getRootOutline() {
        checkWriter();
        return this.pdf.getRootOutline();
    }

    private float getEffectiveStringWidth(String text, boolean kerned) {
        float w;
        BaseFont bf = this.state.fontDetails.getBaseFont();
        if (kerned) {
            w = bf.getWidthPointKerned(text, this.state.size);
        } else {
            w = bf.getWidthPoint(text, this.state.size);
        }
        if (this.state.charSpace != ColumnText.GLOBAL_SPACE_CHAR_RATIO && text.length() > 1) {
            w += this.state.charSpace * (text.length() - 1);
        }
        int ft = bf.getFontType();
        if (this.state.wordSpace != ColumnText.GLOBAL_SPACE_CHAR_RATIO && (ft == 0 || ft == 1 || ft == 5)) {
            for (int i = 0; i < text.length() - 1; i++) {
                if (text.charAt(i) == ' ') {
                    w += this.state.wordSpace;
                }
            }
        }
        if (this.state.scale != 100.0d) {
            w = (w * this.state.scale) / 100.0f;
        }
        return w;
    }

    void showTextAligned(int alignment, String text, float x, float y, float rotation) {
        showTextAligned(alignment, text, x, y, rotation, false);
    }

    private void showTextAligned(int alignment, String text, float x, float y, float rotation, boolean kerned) {
        if (this.state.fontDetails == null) {
            throw new NullPointerException("Font and size must be set before writing any text");
        }
        if (rotation == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            switch (alignment) {
                case 1:
                    x -= getEffectiveStringWidth(text, kerned) / 2.0f;
                    break;
                case 2:
                    x -= getEffectiveStringWidth(text, kerned);
                    break;
            }
            setTextMatrix(x, y);
            if (kerned) {
                showTextKerned(text);
                return;
            } else {
                showText(text);
                return;
            }
        }
        double alpha = (((double) rotation) * 3.141592653589793d) / 180.0d;
        float cos = (float) Math.cos(alpha);
        float sin = (float) Math.sin(alpha);
        switch (alignment) {
            case 1:
                float len = getEffectiveStringWidth(text, kerned) / 2.0f;
                x -= len * cos;
                y -= len * sin;
                break;
            case 2:
                float len2 = getEffectiveStringWidth(text, kerned);
                x -= len2 * cos;
                y -= len2 * sin;
                break;
        }
        setTextMatrix(cos, sin, -sin, cos, x, y);
        if (kerned) {
            showTextKerned(text);
        } else {
            showText(text);
        }
        setTextMatrix(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public void concatCTM(float a, float b, float c, float d, float e, float f) {
        this.content.append(a).append(' ').append(b).append(' ').append(c).append(' ');
        this.content.append(d).append(' ').append(e).append(' ').append(f).append(" cm").append_i(this.separator);
    }

    public static ArrayList bezierArc(float x1, float y1, float x2, float y2, float startAng, float extent) {
        int Nfrag;
        float fragAngle;
        if (x1 > x2) {
            x1 = x2;
            x2 = x1;
        }
        if (y2 > y1) {
            y1 = y2;
            y2 = y1;
        }
        if (Math.abs(extent) <= 90.0f) {
            fragAngle = extent;
            Nfrag = 1;
        } else {
            Nfrag = (int) Math.ceil(Math.abs(extent) / 90.0f);
            fragAngle = extent / Nfrag;
        }
        float x_cen = (x1 + x2) / 2.0f;
        float y_cen = (y1 + y2) / 2.0f;
        float rx = (x2 - x1) / 2.0f;
        float ry = (y2 - y1) / 2.0f;
        float halfAng = (float) ((((double) fragAngle) * 3.141592653589793d) / 360.0d);
        float kappa = (float) Math.abs((1.3333333333333333d * (1.0d - Math.cos(halfAng))) / Math.sin(halfAng));
        ArrayList pointList = new ArrayList();
        for (int i = 0; i < Nfrag; i++) {
            float theta0 = (float) ((((double) (startAng + (i * fragAngle))) * 3.141592653589793d) / 180.0d);
            float theta1 = (float) ((((double) (startAng + ((i + 1) * fragAngle))) * 3.141592653589793d) / 180.0d);
            float cos0 = (float) Math.cos(theta0);
            float cos1 = (float) Math.cos(theta1);
            float sin0 = (float) Math.sin(theta0);
            float sin1 = (float) Math.sin(theta1);
            if (fragAngle > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                pointList.add(new float[]{x_cen + (rx * cos0), y_cen - (ry * sin0), x_cen + (rx * (cos0 - (kappa * sin0))), y_cen - (ry * (sin0 + (kappa * cos0))), x_cen + (rx * (cos1 + (kappa * sin1))), y_cen - (ry * (sin1 - (kappa * cos1))), x_cen + (rx * cos1), y_cen - (ry * sin1)});
            } else {
                pointList.add(new float[]{x_cen + (rx * cos0), y_cen - (ry * sin0), x_cen + (rx * (cos0 + (kappa * sin0))), y_cen - (ry * (sin0 - (kappa * cos0))), x_cen + (rx * (cos1 - (kappa * sin1))), y_cen - (ry * (sin1 + (kappa * cos1))), x_cen + (rx * cos1), y_cen - (ry * sin1)});
            }
        }
        return pointList;
    }

    public void arc(float x1, float y1, float x2, float y2, float startAng, float extent) {
        ArrayList ar = bezierArc(x1, y1, x2, y2, startAng, extent);
        if (ar.isEmpty()) {
            return;
        }
        float[] pt = (float[]) ar.get(0);
        moveTo(pt[0], pt[1]);
        for (int k = 0; k < ar.size(); k++) {
            float[] pt2 = (float[]) ar.get(k);
            curveTo(pt2[2], pt2[3], pt2[4], pt2[5], pt2[6], pt2[7]);
        }
    }

    private PdfPatternPainter createPattern(float width, float height, float xstep, float ystep) {
        checkWriter();
        if (xstep == ColumnText.GLOBAL_SPACE_CHAR_RATIO || ystep == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            throw new RuntimeException("XStep or YStep can not be ZERO.");
        }
        PdfPatternPainter painter = new PdfPatternPainter(this.writer);
        painter.setWidth(width);
        painter.setHeight(height);
        painter.setXStep(xstep);
        painter.setYStep(ystep);
        this.writer.addSimplePattern(painter);
        return painter;
    }

    PdfPatternPainter createPattern(float width, float height) {
        return createPattern(width, height, width, height);
    }

    private PdfPatternPainter createPattern(float width, float height, float xstep, float ystep, Color color) {
        checkWriter();
        if (xstep == ColumnText.GLOBAL_SPACE_CHAR_RATIO || ystep == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            throw new RuntimeException("XStep or YStep can not be ZERO.");
        }
        PdfPatternPainter painter = new PdfPatternPainter(this.writer, color);
        painter.setWidth(width);
        painter.setHeight(height);
        painter.setXStep(xstep);
        painter.setYStep(ystep);
        this.writer.addSimplePattern(painter);
        return painter;
    }

    public PdfTemplate createTemplate(float width, float height) {
        return createTemplate(width, height, null);
    }

    private PdfTemplate createTemplate(float width, float height, PdfName forcedName) {
        checkWriter();
        PdfTemplate template = new PdfTemplate(this.writer);
        template.setWidth(width);
        template.setHeight(height);
        this.writer.addDirectTemplateSimple(template, forcedName);
        return template;
    }

    private PdfAppearance createAppearance(float width, float height, PdfName forcedName) {
        checkWriter();
        PdfAppearance template = new PdfAppearance(this.writer);
        template.setWidth(width);
        template.setHeight(height);
        this.writer.addDirectTemplateSimple(template, forcedName);
        return template;
    }

    public void addTemplate(PdfTemplate template, float a, float b, float c, float d, float e, float f) {
        checkWriter();
        checkNoPattern(template);
        PdfName name = this.writer.addDirectTemplateSimple(template, null);
        PageResources prs = getPageResources();
        PdfName name2 = prs.addXObject(name, template.getIndirectReference());
        this.content.append("q ");
        this.content.append(a).append(' ');
        this.content.append(b).append(' ');
        this.content.append(c).append(' ');
        this.content.append(d).append(' ');
        this.content.append(e).append(' ');
        this.content.append(f).append(" cm ");
        this.content.append(name2.getBytes()).append(" Do Q").append_i(this.separator);
    }

    void addTemplateReference(PdfIndirectReference template, PdfName name, float a, float b, float c, float d, float e, float f) {
        checkWriter();
        PageResources prs = getPageResources();
        PdfName name2 = prs.addXObject(name, template);
        this.content.append("q ");
        this.content.append(a).append(' ');
        this.content.append(b).append(' ');
        this.content.append(c).append(' ');
        this.content.append(d).append(' ');
        this.content.append(e).append(' ');
        this.content.append(f).append(" cm ");
        this.content.append(name2.getBytes()).append(" Do Q").append_i(this.separator);
    }

    public void addTemplate(PdfTemplate template, float x, float y) {
        addTemplate(template, 1.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f, x, y);
    }

    public void setCMYKColorFill(int cyan, int magenta, int yellow, int black) {
        this.content.append((cyan & 255) / 255.0f);
        this.content.append(' ');
        this.content.append((magenta & 255) / 255.0f);
        this.content.append(' ');
        this.content.append((yellow & 255) / 255.0f);
        this.content.append(' ');
        this.content.append((black & 255) / 255.0f);
        this.content.append(" k").append_i(this.separator);
    }

    public void setCMYKColorStroke(int cyan, int magenta, int yellow, int black) {
        this.content.append((cyan & 255) / 255.0f);
        this.content.append(' ');
        this.content.append((magenta & 255) / 255.0f);
        this.content.append(' ');
        this.content.append((yellow & 255) / 255.0f);
        this.content.append(' ');
        this.content.append((black & 255) / 255.0f);
        this.content.append(" K").append_i(this.separator);
    }

    public void setRGBColorFill(int red, int green, int blue) {
        HelperRGB((red & 255) / 255.0f, (green & 255) / 255.0f, (blue & 255) / 255.0f);
        this.content.append(" rg").append_i(this.separator);
    }

    public void setRGBColorStroke(int red, int green, int blue) {
        HelperRGB((red & 255) / 255.0f, (green & 255) / 255.0f, (blue & 255) / 255.0f);
        this.content.append(" RG").append_i(this.separator);
    }

    public void setColorStroke(Color color) {
        PdfXConformanceImp.checkPDFXConformance(this.writer, 1, color);
        int type = ExtendedColor.getType(color);
        switch (type) {
            case 1:
                setGrayStroke(((GrayColor) color).getGray());
                break;
            case 2:
                CMYKColor cmyk = (CMYKColor) color;
                setCMYKColorStrokeF(cmyk.getCyan(), cmyk.getMagenta(), cmyk.getYellow(), cmyk.getBlack());
                break;
            case 3:
                SpotColor spot = (SpotColor) color;
                setColorStroke(spot.getPdfSpotColor(), spot.getTint());
                break;
            case 4:
                PatternColor pat = (PatternColor) color;
                setPatternStroke(pat.getPainter());
                break;
            case 5:
                ShadingColor shading = (ShadingColor) color;
                setShadingStroke(shading.getPdfShadingPattern());
                break;
            default:
                setRGBColorStroke(color.getRed(), color.getGreen(), color.getBlue());
                break;
        }
    }

    public void setColorFill(Color color) {
        PdfXConformanceImp.checkPDFXConformance(this.writer, 1, color);
        int type = ExtendedColor.getType(color);
        switch (type) {
            case 1:
                setGrayFill(((GrayColor) color).getGray());
                break;
            case 2:
                CMYKColor cmyk = (CMYKColor) color;
                setCMYKColorFillF(cmyk.getCyan(), cmyk.getMagenta(), cmyk.getYellow(), cmyk.getBlack());
                break;
            case 3:
                SpotColor spot = (SpotColor) color;
                setColorFill(spot.getPdfSpotColor(), spot.getTint());
                break;
            case 4:
                PatternColor pat = (PatternColor) color;
                setPatternFill(pat.getPainter());
                break;
            case 5:
                ShadingColor shading = (ShadingColor) color;
                setShadingFill(shading.getPdfShadingPattern());
                break;
            default:
                setRGBColorFill(color.getRed(), color.getGreen(), color.getBlue());
                break;
        }
    }

    public void setColorFill(PdfSpotColor sp, float tint) {
        checkWriter();
        this.state.colorDetails = this.writer.addSimple(sp);
        PageResources prs = getPageResources();
        PdfName name = this.state.colorDetails.getColorName();
        this.content.append(prs.addColor(name, this.state.colorDetails.getIndirectReference()).getBytes()).append(" cs ").append(tint).append(" scn").append_i(this.separator);
    }

    public void setColorStroke(PdfSpotColor sp, float tint) {
        checkWriter();
        this.state.colorDetails = this.writer.addSimple(sp);
        PageResources prs = getPageResources();
        PdfName name = this.state.colorDetails.getColorName();
        this.content.append(prs.addColor(name, this.state.colorDetails.getIndirectReference()).getBytes()).append(" CS ").append(tint).append(" SCN").append_i(this.separator);
    }

    public void setPatternFill(PdfPatternPainter p) {
        if (p.isStencil()) {
            setPatternFill(p, p.getDefaultColor());
            return;
        }
        checkWriter();
        PageResources prs = getPageResources();
        PdfName name = this.writer.addSimplePattern(p);
        this.content.append(PdfName.PATTERN.getBytes()).append(" cs ").append(prs.addPattern(name, p.getIndirectReference()).getBytes()).append(" scn").append_i(this.separator);
    }

    private void outputColorNumbers(Color color, float tint) {
        PdfXConformanceImp.checkPDFXConformance(this.writer, 1, color);
        int type = ExtendedColor.getType(color);
        switch (type) {
            case 0:
                this.content.append(color.getRed() / 255.0f);
                this.content.append(' ');
                this.content.append(color.getGreen() / 255.0f);
                this.content.append(' ');
                this.content.append(color.getBlue() / 255.0f);
                return;
            case 1:
                this.content.append(((GrayColor) color).getGray());
                return;
            case 2:
                CMYKColor cmyk = (CMYKColor) color;
                this.content.append(cmyk.getCyan()).append(' ').append(cmyk.getMagenta());
                this.content.append(' ').append(cmyk.getYellow()).append(' ').append(cmyk.getBlack());
                return;
            case 3:
                this.content.append(tint);
                return;
            default:
                throw new RuntimeException("Invalid color type.");
        }
    }

    private void setPatternFill(PdfPatternPainter p, Color color) {
        if (ExtendedColor.getType(color) == 3) {
            setPatternFill(p, color, ((SpotColor) color).getTint());
        } else {
            setPatternFill(p, color, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        }
    }

    public void setPatternFill(PdfPatternPainter p, Color color, float tint) {
        checkWriter();
        if (!p.isStencil()) {
            throw new RuntimeException("An uncolored pattern was expected.");
        }
        PageResources prs = getPageResources();
        PdfName name = this.writer.addSimplePattern(p);
        PdfName name2 = prs.addPattern(name, p.getIndirectReference());
        ColorDetails csDetail = this.writer.addSimplePatternColorspace(color);
        PdfName cName = prs.addColor(csDetail.getColorName(), csDetail.getIndirectReference());
        this.content.append(cName.getBytes()).append(" cs").append_i(this.separator);
        outputColorNumbers(color, tint);
        this.content.append(' ').append(name2.getBytes()).append(" scn").append_i(this.separator);
    }

    private void setPatternStroke(PdfPatternPainter p, Color color) {
        if (ExtendedColor.getType(color) == 3) {
            setPatternStroke(p, color, ((SpotColor) color).getTint());
        } else {
            setPatternStroke(p, color, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        }
    }

    public void setPatternStroke(PdfPatternPainter p, Color color, float tint) {
        checkWriter();
        if (!p.isStencil()) {
            throw new RuntimeException("An uncolored pattern was expected.");
        }
        PageResources prs = getPageResources();
        PdfName name = this.writer.addSimplePattern(p);
        PdfName name2 = prs.addPattern(name, p.getIndirectReference());
        ColorDetails csDetail = this.writer.addSimplePatternColorspace(color);
        PdfName cName = prs.addColor(csDetail.getColorName(), csDetail.getIndirectReference());
        this.content.append(cName.getBytes()).append(" CS").append_i(this.separator);
        outputColorNumbers(color, tint);
        this.content.append(' ').append(name2.getBytes()).append(" SCN").append_i(this.separator);
    }

    public void setPatternStroke(PdfPatternPainter p) {
        if (p.isStencil()) {
            setPatternStroke(p, p.getDefaultColor());
            return;
        }
        checkWriter();
        PageResources prs = getPageResources();
        PdfName name = this.writer.addSimplePattern(p);
        this.content.append(PdfName.PATTERN.getBytes()).append(" CS ").append(prs.addPattern(name, p.getIndirectReference()).getBytes()).append(" SCN").append_i(this.separator);
    }

    private void paintShading(PdfShading shading) {
        this.writer.addSimpleShading(shading);
        PageResources prs = getPageResources();
        PdfName name = prs.addShading(shading.getShadingName(), shading.getShadingReference());
        this.content.append(name.getBytes()).append(" sh").append_i(this.separator);
        ColorDetails details = shading.getColorDetails();
        if (details != null) {
            prs.addColor(details.getColorName(), details.getIndirectReference());
        }
    }

    public void setShadingFill(PdfShadingPattern shading) {
        this.writer.addSimpleShadingPattern(shading);
        PageResources prs = getPageResources();
        PdfName name = prs.addPattern(shading.getPatternName(), shading.getPatternReference());
        this.content.append(PdfName.PATTERN.getBytes()).append(" cs ").append(name.getBytes()).append(" scn").append_i(this.separator);
        ColorDetails details = shading.getColorDetails();
        if (details != null) {
            prs.addColor(details.getColorName(), details.getIndirectReference());
        }
    }

    public void setShadingStroke(PdfShadingPattern shading) {
        this.writer.addSimpleShadingPattern(shading);
        PageResources prs = getPageResources();
        PdfName name = prs.addPattern(shading.getPatternName(), shading.getPatternReference());
        this.content.append(PdfName.PATTERN.getBytes()).append(" CS ").append(name.getBytes()).append(" SCN").append_i(this.separator);
        ColorDetails details = shading.getColorDetails();
        if (details != null) {
            prs.addColor(details.getColorName(), details.getIndirectReference());
        }
    }

    protected void checkWriter() {
        if (this.writer == null) {
            throw new NullPointerException("The writer in PdfContentByte is null.");
        }
    }

    void showText(PdfTextArray text) {
        if (this.state.fontDetails == null) {
            throw new NullPointerException("Font and size must be set before writing any text");
        }
        this.content.append("[");
        ArrayList arrayList = text.getArrayList();
        boolean lastWasNumber = false;
        for (int k = 0; k < arrayList.size(); k++) {
            Object obj = arrayList.get(k);
            if (obj instanceof String) {
                showText2((String) obj);
                lastWasNumber = false;
            } else {
                if (lastWasNumber) {
                    this.content.append(' ');
                } else {
                    lastWasNumber = true;
                }
                this.content.append(((Float) obj).floatValue());
            }
        }
        this.content.append("]TJ").append_i(this.separator);
    }

    public PdfWriter getPdfWriter() {
        return this.writer;
    }

    public PdfDocument getPdfDocument() {
        return this.pdf;
    }

    public PdfContentByte getDuplicate() {
        return new PdfContentByte(this.writer);
    }

    public void roundRectangle(float x, float y, float w, float h, float r) {
        if (w < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            x += w;
            w = -w;
        }
        if (h < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            y += h;
            h = -h;
        }
        if (r < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            r = -r;
        }
        moveTo(x + r, y);
        lineTo((x + w) - r, y);
        curveTo((x + w) - (r * 0.4477f), y, x + w, y + (r * 0.4477f), x + w, y + r);
        lineTo(x + w, (y + h) - r);
        curveTo(x + w, (y + h) - (r * 0.4477f), (x + w) - (r * 0.4477f), y + h, (x + w) - r, y + h);
        lineTo(x + r, y + h);
        curveTo(x + (r * 0.4477f), y + h, x, (y + h) - (r * 0.4477f), x, (y + h) - r);
        lineTo(x, y + r);
        curveTo(x, y + (r * 0.4477f), x + (r * 0.4477f), y, x + r, y);
    }

    public void setAction(PdfAction action, float llx, float lly, float urx, float ury) {
        this.pdf.setAction(action, llx, lly, urx, ury);
    }

    public void setLiteral(String s) {
        this.content.append(s);
    }

    public void setLiteral(char c) {
        this.content.append(c);
    }

    public void setLiteral(float n) {
        this.content.append(n);
    }

    private void checkNoPattern(PdfTemplate t) {
        if (t.getType() == 3) {
            throw new RuntimeException("Invalid use of a pattern. A template was expected.");
        }
    }

    void drawRadioField(float llx, float lly, float urx, float ury, boolean on) {
        if (llx > urx) {
            llx = urx;
            urx = llx;
        }
        if (lly > ury) {
            lly = ury;
            ury = lly;
        }
        setLineWidth(1.0f);
        setLineCap(1);
        setColorStroke(new Color(192, 192, 192));
        arc(llx + 1.0f, lly + 1.0f, urx - 1.0f, ury - 1.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, 360.0f);
        stroke();
        setLineWidth(1.0f);
        setLineCap(1);
        setColorStroke(new Color(160, 160, 160));
        arc(llx + 0.5f, lly + 0.5f, urx - 0.5f, ury - 0.5f, 45.0f, 180.0f);
        stroke();
        setLineWidth(1.0f);
        setLineCap(1);
        setColorStroke(new Color(0, 0, 0));
        arc(llx + 1.5f, lly + 1.5f, urx - 1.5f, ury - 1.5f, 45.0f, 180.0f);
        stroke();
        if (on) {
            setLineWidth(1.0f);
            setLineCap(1);
            setColorFill(new Color(0, 0, 0));
            arc(llx + 4.0f, lly + 4.0f, urx - 4.0f, ury - 4.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, 360.0f);
            fill();
        }
    }

    void drawTextField(float llx, float lly, float urx, float ury) {
        if (llx > urx) {
            llx = urx;
            urx = llx;
        }
        if (lly > ury) {
            lly = ury;
            ury = lly;
        }
        setColorStroke(new Color(192, 192, 192));
        setLineWidth(1.0f);
        setLineCap(0);
        rectangle(llx, lly, urx - llx, ury - lly);
        stroke();
        setLineWidth(1.0f);
        setLineCap(0);
        setColorFill(new Color(255, 255, 255));
        rectangle(llx + 0.5f, lly + 0.5f, (urx - llx) - 1.0f, (ury - lly) - 1.0f);
        fill();
        setColorStroke(new Color(192, 192, 192));
        setLineWidth(1.0f);
        setLineCap(0);
        moveTo(llx + 1.0f, lly + 1.5f);
        lineTo(urx - 1.5f, lly + 1.5f);
        lineTo(urx - 1.5f, ury - 1.0f);
        stroke();
        setColorStroke(new Color(160, 160, 160));
        setLineWidth(1.0f);
        setLineCap(0);
        moveTo(llx + 1.0f, lly + 1.0f);
        lineTo(llx + 1.0f, ury - 1.0f);
        lineTo(urx - 1.0f, ury - 1.0f);
        stroke();
        setColorStroke(new Color(0, 0, 0));
        setLineWidth(1.0f);
        setLineCap(0);
        moveTo(llx + 2.0f, lly + 2.0f);
        lineTo(llx + 2.0f, ury - 2.0f);
        lineTo(urx - 2.0f, ury - 2.0f);
        stroke();
    }

    void drawButton(float llx, float lly, float urx, float ury, String text, BaseFont bf, float size) {
        if (llx > urx) {
            llx = urx;
            urx = llx;
        }
        if (lly > ury) {
            lly = ury;
            ury = lly;
        }
        setColorStroke(new Color(0, 0, 0));
        setLineWidth(1.0f);
        setLineCap(0);
        rectangle(llx, lly, urx - llx, ury - lly);
        stroke();
        setLineWidth(1.0f);
        setLineCap(0);
        setColorFill(new Color(192, 192, 192));
        rectangle(llx + 0.5f, lly + 0.5f, (urx - llx) - 1.0f, (ury - lly) - 1.0f);
        fill();
        setColorStroke(new Color(255, 255, 255));
        setLineWidth(1.0f);
        setLineCap(0);
        moveTo(llx + 1.0f, lly + 1.0f);
        lineTo(llx + 1.0f, ury - 1.0f);
        lineTo(urx - 1.0f, ury - 1.0f);
        stroke();
        setColorStroke(new Color(160, 160, 160));
        setLineWidth(1.0f);
        setLineCap(0);
        moveTo(llx + 1.0f, lly + 1.0f);
        lineTo(urx - 1.0f, lly + 1.0f);
        lineTo(urx - 1.0f, ury - 1.0f);
        stroke();
        resetRGBColorFill();
        beginText();
        setFontAndSize(bf, size);
        showTextAligned(1, text, llx + ((urx - llx) / 2.0f), lly + (((ury - lly) - size) / 2.0f), ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        endText();
    }

    public Graphics2D createGraphics(float width, float height, FontMapper fontMapper) {
        return new PdfGraphics2D(this, width, height, fontMapper, false, false, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    PageResources getPageResources() {
        return this.pdf.getPageResources();
    }

    public void setGState(PdfGState gstate) {
        PdfObject[] obj = this.writer.addSimpleExtGState(gstate);
        PageResources prs = getPageResources();
        PdfName name = prs.addExtGState((PdfName) obj[0], (PdfIndirectReference) obj[1]);
        this.content.append(name.getBytes()).append(" gs").append_i(this.separator);
    }

    private void beginLayer(PdfOCG layer) {
        if ((layer instanceof PdfLayer) && ((PdfLayer) layer).getTitle() != null) {
            throw new IllegalArgumentException("A title is not a layer");
        }
        if (this.layerDepth == null) {
            this.layerDepth = new ArrayList();
        }
        if (layer instanceof PdfLayerMembership) {
            this.layerDepth.add(1);
            beginLayer2(layer);
            return;
        }
        int n = 0;
        PdfLayer parent = (PdfLayer) layer;
        while (true) {
            PdfLayer la = parent;
            if (la != null) {
                if (la.getTitle() == null) {
                    beginLayer2(la);
                    n++;
                }
                parent = la.getParent();
            } else {
                this.layerDepth.add(Integer.valueOf(n));
                return;
            }
        }
    }

    private void beginLayer2(PdfOCG layer) {
        PdfName name = (PdfName) this.writer.addSimpleProperty(layer, layer.getRef())[0];
        PageResources prs = getPageResources();
        this.content.append("/OC ").append(prs.addProperty(name, layer.getRef()).getBytes()).append(" BDC").append_i(this.separator);
    }

    private void endLayer() {
        if (this.layerDepth != null && !this.layerDepth.isEmpty()) {
            int n = ((Integer) this.layerDepth.get(this.layerDepth.size() - 1)).intValue();
            this.layerDepth.remove(this.layerDepth.size() - 1);
            while (true) {
                int i = n;
                n--;
                if (i > 0) {
                    this.content.append(OperatorName.END_MARKED_CONTENT).append_i(this.separator);
                } else {
                    return;
                }
            }
        } else {
            throw new IllegalPdfSyntaxException("Unbalanced layer operators.");
        }
    }

    void addAnnotation(PdfAnnotation annot) {
        this.writer.addAnnotation(annot);
    }

    private void beginMarkedContentSequence(PdfName tag, PdfDictionary property, boolean inline) {
        PdfObject[] objs;
        if (property == null) {
            this.content.append(tag.getBytes()).append(" BMC").append_i(this.separator);
            return;
        }
        this.content.append(tag.getBytes()).append(' ');
        if (inline) {
            try {
                property.toPdf(this.writer, this.content);
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        } else {
            if (this.writer.propertyExists(property)) {
                objs = this.writer.addSimpleProperty(property, null);
            } else {
                objs = this.writer.addSimpleProperty(property, this.writer.getPdfIndirectReference());
            }
            PdfName name = (PdfName) objs[0];
            PageResources prs = getPageResources();
            this.content.append(prs.addProperty(name, (PdfIndirectReference) objs[1]).getBytes());
        }
        this.content.append(" BDC").append_i(this.separator);
        this.mcDepth++;
    }

    public void sanityCheck() {
        if (this.mcDepth != 0) {
            throw new IllegalPdfSyntaxException("Unbalanced marked content operators.");
        }
        if (this.inText) {
            throw new IllegalPdfSyntaxException("Unbalanced begin/end text operators.");
        }
        if (this.layerDepth != null && !this.layerDepth.isEmpty()) {
            throw new IllegalPdfSyntaxException("Unbalanced layer operators.");
        }
        if (!this.stateList.isEmpty()) {
            throw new IllegalPdfSyntaxException("Unbalanced save/restore state operators.");
        }
    }
}
