package com.aowagie.text.pdf;

import com.aowagie.text.html.Markup;
import com.aowagie.text.pdf.internal.PolylineShape;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderableImage;
import java.io.ByteArrayOutputStream;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfGraphics2D.class */
public class PdfGraphics2D extends Graphics2D {
    private static final int FILL = 1;
    private static final int STROKE = 2;
    private static final int CLIP = 3;
    private BasicStroke strokeOne;
    private static final AffineTransform IDENTITY = new AffineTransform();
    private Font font;
    private BaseFont baseFont;
    private float fontSize;
    private AffineTransform transform;
    private Paint paint;
    private Color background;
    private float width;
    private float height;
    private Area clip;
    private final RenderingHints rhints;
    private Stroke stroke;
    private Stroke originalStroke;
    private PdfContentByte cb;
    private HashMap baseFonts;
    private boolean disposeCalled;
    private FontMapper fontMapper;
    private ArrayList kids;
    private boolean kid;
    private Graphics2D dg2;
    private boolean onlyShapes;
    private Stroke oldStroke;
    private Paint paintFill;
    private Paint paintStroke;
    private MediaTracker mediaTracker;
    protected boolean underline;
    protected PdfGState[] fillGState;
    protected PdfGState[] strokeGState;
    protected int currentFillGState;
    protected int currentStrokeGState;
    public static final int AFM_DIVISOR = 1000;
    private boolean convertImagesToJPEG;
    private float jpegQuality;
    private float alpha;
    private Composite composite;
    private Paint realPaint;

    private PdfGraphics2D() {
        this.strokeOne = new BasicStroke(1.0f);
        this.rhints = new RenderingHints((Map) null);
        this.disposeCalled = false;
        this.kid = false;
        this.dg2 = new BufferedImage(2, 2, 1).createGraphics();
        this.onlyShapes = false;
        this.fillGState = new PdfGState[256];
        this.strokeGState = new PdfGState[256];
        this.currentFillGState = 255;
        this.currentStrokeGState = 255;
        this.convertImagesToJPEG = false;
        this.jpegQuality = 0.95f;
        this.dg2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        setRenderingHint(HyperLinkKey.KEY_INSTANCE, HyperLinkKey.VALUE_HYPERLINKKEY_OFF);
    }

    PdfGraphics2D(PdfContentByte cb, float width, float height, FontMapper fontMapper, boolean onlyShapes, boolean convertImagesToJPEG, float quality) {
        this.strokeOne = new BasicStroke(1.0f);
        this.rhints = new RenderingHints((Map) null);
        this.disposeCalled = false;
        this.kid = false;
        this.dg2 = new BufferedImage(2, 2, 1).createGraphics();
        this.onlyShapes = false;
        this.fillGState = new PdfGState[256];
        this.strokeGState = new PdfGState[256];
        this.currentFillGState = 255;
        this.currentStrokeGState = 255;
        this.convertImagesToJPEG = false;
        this.jpegQuality = 0.95f;
        this.dg2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        setRenderingHint(HyperLinkKey.KEY_INSTANCE, HyperLinkKey.VALUE_HYPERLINKKEY_OFF);
        this.convertImagesToJPEG = convertImagesToJPEG;
        this.jpegQuality = quality;
        this.onlyShapes = onlyShapes;
        this.transform = new AffineTransform();
        this.baseFonts = new HashMap();
        if (!onlyShapes) {
            this.fontMapper = fontMapper;
            if (this.fontMapper == null) {
                this.fontMapper = new DefaultFontMapper();
            }
        }
        this.paint = Color.black;
        this.background = Color.white;
        setFont(new Font("sanserif", 0, 12));
        this.cb = cb;
        cb.saveState();
        this.width = width;
        this.height = height;
        this.clip = new Area(new Rectangle2D.Float(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, width, height));
        clip(this.clip);
        BasicStroke basicStroke = this.strokeOne;
        this.oldStroke = basicStroke;
        this.stroke = basicStroke;
        this.originalStroke = basicStroke;
        setStrokeDiff(this.stroke, null);
        cb.saveState();
    }

    public void draw(Shape s) {
        followPath(s, 2);
    }

    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        return drawImage(img, (Image) null, xform, (Color) null, obs);
    }

    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        BufferedImage result = img;
        if (op != null) {
            BufferedImage result2 = op.createCompatibleDestImage(img, img.getColorModel());
            result = op.filter(img, result2);
        }
        drawImage((Image) result, x, y, (ImageObserver) null);
    }

    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        BufferedImage image;
        if (img instanceof BufferedImage) {
            image = (BufferedImage) img;
        } else {
            ColorModel cm = img.getColorModel();
            int width = img.getWidth();
            int height = img.getHeight();
            WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
            boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
            Hashtable properties = new Hashtable();
            String[] keys = img.getPropertyNames();
            if (keys != null) {
                for (int i = 0; i < keys.length; i++) {
                    properties.put(keys[i], img.getProperty(keys[i]));
                }
            }
            BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
            img.copyData(raster);
            image = result;
        }
        drawImage(image, xform, null);
    }

    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        drawRenderedImage(img.createDefaultRendering(), xform);
    }

    public void drawString(String s, int x, int y) {
        drawString(s, x, y);
    }

    public static double asPoints(double d, int i) {
        return (d * ((double) i)) / 1000.0d;
    }

    protected void doAttributes(AttributedCharacterIterator iter) {
        this.underline = false;
        for (AttributedCharacterIterator.Attribute attribute : iter.getAttributes().keySet()) {
            if (attribute instanceof TextAttribute) {
                TextAttribute textattribute = (TextAttribute) attribute;
                if (textattribute.equals(TextAttribute.FONT)) {
                    setFont((Font) iter.getAttributes().get(textattribute));
                } else if (textattribute.equals(TextAttribute.UNDERLINE)) {
                    if (iter.getAttributes().get(textattribute) == TextAttribute.UNDERLINE_ON) {
                        this.underline = true;
                    }
                } else if (textattribute.equals(TextAttribute.SIZE)) {
                    Object obj = iter.getAttributes().get(textattribute);
                    if (obj instanceof Integer) {
                        int i = ((Integer) obj).intValue();
                        setFont(getFont().deriveFont(getFont().getStyle(), i));
                    } else if (obj instanceof Float) {
                        float f = ((Float) obj).floatValue();
                        setFont(getFont().deriveFont(getFont().getStyle(), f));
                    }
                } else if (textattribute.equals(TextAttribute.FOREGROUND)) {
                    setColor((Color) iter.getAttributes().get(textattribute));
                } else if (textattribute.equals(TextAttribute.FAMILY)) {
                    Font font = getFont();
                    Map fontAttributes = font.getAttributes();
                    fontAttributes.put(TextAttribute.FAMILY, iter.getAttributes().get(textattribute));
                    setFont(font.deriveFont(fontAttributes));
                } else if (textattribute.equals(TextAttribute.POSTURE)) {
                    Font font2 = getFont();
                    Map fontAttributes2 = font2.getAttributes();
                    fontAttributes2.put(TextAttribute.POSTURE, iter.getAttributes().get(textattribute));
                    setFont(font2.deriveFont(fontAttributes2));
                } else if (textattribute.equals(TextAttribute.WEIGHT)) {
                    Font font3 = getFont();
                    Map fontAttributes3 = font3.getAttributes();
                    fontAttributes3.put(TextAttribute.WEIGHT, iter.getAttributes().get(textattribute));
                    setFont(font3.deriveFont(fontAttributes3));
                }
            }
        }
    }

    public void drawString(String s, float x, float y) {
        float angle2;
        if (s.length() == 0) {
            return;
        }
        setFillPaint();
        if (this.onlyShapes) {
            drawGlyphVector(this.font.layoutGlyphVector(getFontRenderContext(), s.toCharArray(), 0, s.length(), 0), x, y);
            return;
        }
        boolean restoreTextRenderingMode = false;
        AffineTransform at = getTransform();
        AffineTransform at2 = getTransform();
        at2.translate(x, y);
        at2.concatenate(this.font.getTransform());
        setTransform(at2);
        AffineTransform inverse = normalizeMatrix();
        AffineTransform flipper = AffineTransform.getScaleInstance(1.0d, -1.0d);
        inverse.concatenate(flipper);
        double[] mx = new double[6];
        inverse.getMatrix(mx);
        this.cb.beginText();
        this.cb.setFontAndSize(this.baseFont, this.fontSize);
        if (this.font.isItalic() && this.font.getFontName().equals(this.font.getName())) {
            float angle = this.baseFont.getFontDescriptor(4, 1000.0f);
            float angle3 = this.font.getItalicAngle();
            if (angle3 == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                angle2 = 15.0f;
            } else {
                angle2 = -angle3;
            }
            if (angle == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                mx[2] = angle2 / 100.0f;
            }
        }
        this.cb.setTextMatrix((float) mx[0], (float) mx[1], (float) mx[2], (float) mx[3], (float) mx[4], (float) mx[5]);
        Float fontTextAttributeWidth = (Float) this.font.getAttributes().get(TextAttribute.WIDTH);
        Float fontTextAttributeWidth2 = fontTextAttributeWidth == null ? TextAttribute.WIDTH_REGULAR : fontTextAttributeWidth;
        if (!TextAttribute.WIDTH_REGULAR.equals(fontTextAttributeWidth2)) {
            this.cb.setHorizontalScaling(100.0f / fontTextAttributeWidth2.floatValue());
        }
        if (this.baseFont.getPostscriptFontName().toLowerCase().indexOf(Markup.CSS_VALUE_BOLD) < 0) {
            Float weight = (Float) this.font.getAttributes().get(TextAttribute.WEIGHT);
            if (weight == null) {
                weight = this.font.isBold() ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR;
            }
            if ((this.font.isBold() || weight.floatValue() >= TextAttribute.WEIGHT_SEMIBOLD.floatValue()) && this.font.getFontName().equals(this.font.getName())) {
                float strokeWidth = (this.font.getSize2D() * (weight.floatValue() - TextAttribute.WEIGHT_REGULAR.floatValue())) / 30.0f;
                if (strokeWidth != 1.0f && (this.realPaint instanceof Color)) {
                    this.cb.setTextRenderingMode(2);
                    this.cb.setLineWidth(strokeWidth);
                    Color color = this.realPaint;
                    int alpha = color.getAlpha();
                    if (alpha != this.currentStrokeGState) {
                        this.currentStrokeGState = alpha;
                        PdfGState gs = this.strokeGState[alpha];
                        if (gs == null) {
                            gs = new PdfGState();
                            gs.setStrokeOpacity(alpha / 255.0f);
                            this.strokeGState[alpha] = gs;
                        }
                        this.cb.setGState(gs);
                    }
                    this.cb.setColorStroke(color);
                    restoreTextRenderingMode = true;
                }
            }
        }
        double width = 0.0d;
        if (this.font.getSize2D() > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            float scale = 1000.0f / this.font.getSize2D();
            Font derivedFont = this.font.deriveFont(AffineTransform.getScaleInstance(scale, scale));
            width = derivedFont.getStringBounds(s, getFontRenderContext()).getWidth();
            if (derivedFont.isTransformed()) {
                width /= (double) scale;
            }
        }
        Object url = getRenderingHint(HyperLinkKey.KEY_INSTANCE);
        if (url != null && !url.equals(HyperLinkKey.VALUE_HYPERLINKKEY_OFF)) {
            float scale2 = 1000.0f / this.font.getSize2D();
            Font derivedFont2 = this.font.deriveFont(AffineTransform.getScaleInstance(scale2, scale2));
            double height = derivedFont2.getStringBounds(s, getFontRenderContext()).getHeight();
            if (derivedFont2.isTransformed()) {
                height /= (double) scale2;
            }
            double leftX = this.cb.getXTLM();
            double leftY = this.cb.getYTLM();
            PdfAction action = new PdfAction(url.toString());
            this.cb.setAction(action, (float) leftX, (float) leftY, (float) (leftX + width), (float) (leftY + height));
        }
        if (s.length() > 1) {
            float adv = (((float) width) - this.baseFont.getWidthPoint(s, this.fontSize)) / (s.length() - 1);
            this.cb.setCharacterSpacing(adv);
        }
        this.cb.showText(s);
        if (s.length() > 1) {
            this.cb.setCharacterSpacing(ColumnText.GLOBAL_SPACE_CHAR_RATIO);
        }
        if (!TextAttribute.WIDTH_REGULAR.equals(fontTextAttributeWidth2)) {
            this.cb.setHorizontalScaling(100.0f);
        }
        if (restoreTextRenderingMode) {
            this.cb.setTextRenderingMode(0);
        }
        this.cb.endText();
        setTransform(at);
        if (this.underline) {
            double d = asPoints(50.0d, (int) this.fontSize);
            Stroke savedStroke = this.originalStroke;
            setStroke(new BasicStroke((float) d));
            float y2 = (float) (((double) y) + asPoints(50.0d, (int) this.fontSize));
            draw(new Line2D.Double(x, y2, width + ((double) x), y2));
            setStroke(savedStroke);
        }
    }

    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        drawString(iterator, x, y);
    }

    public void drawString(AttributedCharacterIterator iter, float x, float y) {
        StringBuffer stringbuffer = new StringBuffer(iter.getEndIndex());
        char cFirst = iter.first();
        while (true) {
            char c = cFirst;
            if (c != 65535) {
                if (iter.getIndex() == iter.getRunStart()) {
                    if (stringbuffer.length() > 0) {
                        drawString(stringbuffer.toString(), x, y);
                        FontMetrics fontmetrics = getFontMetrics();
                        x = (float) (((double) x) + fontmetrics.getStringBounds(stringbuffer.toString(), this).getWidth());
                        stringbuffer.delete(0, stringbuffer.length());
                    }
                    doAttributes(iter);
                }
                stringbuffer.append(c);
                cFirst = iter.next();
            } else {
                drawString(stringbuffer.toString(), x, y);
                this.underline = false;
                return;
            }
        }
    }

    public void drawGlyphVector(GlyphVector g, float x, float y) {
        Shape s = g.getOutline(x, y);
        fill(s);
    }

    public void fill(Shape s) {
        followPath(s, 1);
    }

    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        if (onStroke) {
            s = this.stroke.createStrokedShape(s);
        }
        Area area = new Area(this.transform.createTransformedShape(s));
        if (this.clip != null) {
            area.intersect(this.clip);
        }
        return area.intersects(rect.x, rect.y, rect.width, rect.height);
    }

    public GraphicsConfiguration getDeviceConfiguration() {
        return this.dg2.getDeviceConfiguration();
    }

    public void setComposite(Composite comp) {
        if (comp instanceof AlphaComposite) {
            AlphaComposite composite = (AlphaComposite) comp;
            if (composite.getRule() == 3) {
                this.alpha = composite.getAlpha();
                this.composite = composite;
                if (this.realPaint != null && (this.realPaint instanceof Color)) {
                    Color c = this.realPaint;
                    this.paint = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (c.getAlpha() * this.alpha));
                    return;
                }
                return;
            }
        }
        this.composite = comp;
        this.alpha = 1.0f;
    }

    public void setPaint(Paint paint) {
        if (paint == null) {
            return;
        }
        this.paint = paint;
        this.realPaint = paint;
        if ((this.composite instanceof AlphaComposite) && (paint instanceof Color)) {
            AlphaComposite co = this.composite;
            if (co.getRule() == 3) {
                Color c = (Color) paint;
                this.paint = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (c.getAlpha() * this.alpha));
                this.realPaint = paint;
            }
        }
    }

    private Stroke transformStroke(Stroke stroke) {
        if (!(stroke instanceof BasicStroke)) {
            return stroke;
        }
        BasicStroke st = (BasicStroke) stroke;
        float scale = (float) Math.sqrt(Math.abs(this.transform.getDeterminant()));
        float[] dash = st.getDashArray();
        if (dash != null) {
            for (int k = 0; k < dash.length; k++) {
                int i = k;
                dash[i] = dash[i] * scale;
            }
        }
        return new BasicStroke(st.getLineWidth() * scale, st.getEndCap(), st.getLineJoin(), st.getMiterLimit(), dash, st.getDashPhase() * scale);
    }

    private void setStrokeDiff(Stroke newStroke, Stroke oldStroke) {
        boolean makeDash;
        if (newStroke == oldStroke || !(newStroke instanceof BasicStroke)) {
            return;
        }
        BasicStroke nStroke = (BasicStroke) newStroke;
        boolean oldOk = oldStroke instanceof BasicStroke;
        BasicStroke oStroke = null;
        if (oldOk) {
            oStroke = (BasicStroke) oldStroke;
        }
        if (!oldOk || nStroke.getLineWidth() != oStroke.getLineWidth()) {
            this.cb.setLineWidth(nStroke.getLineWidth());
        }
        if (!oldOk || nStroke.getEndCap() != oStroke.getEndCap()) {
            switch (nStroke.getEndCap()) {
                case 0:
                    this.cb.setLineCap(0);
                    break;
                case 2:
                    this.cb.setLineCap(2);
                    break;
                default:
                    this.cb.setLineCap(1);
                    break;
            }
        }
        if (!oldOk || nStroke.getLineJoin() != oStroke.getLineJoin()) {
            switch (nStroke.getLineJoin()) {
                case 0:
                    this.cb.setLineJoin(0);
                    break;
                case 2:
                    this.cb.setLineJoin(2);
                    break;
                default:
                    this.cb.setLineJoin(1);
                    break;
            }
        }
        if (!oldOk || nStroke.getMiterLimit() != oStroke.getMiterLimit()) {
            this.cb.setMiterLimit(nStroke.getMiterLimit());
        }
        if (oldOk) {
            if (nStroke.getDashArray() != null) {
                if (nStroke.getDashPhase() != oStroke.getDashPhase() || !Arrays.equals(nStroke.getDashArray(), oStroke.getDashArray())) {
                    makeDash = true;
                } else {
                    makeDash = false;
                }
            } else if (oStroke.getDashArray() != null) {
                makeDash = true;
            } else {
                makeDash = false;
            }
        } else {
            makeDash = true;
        }
        if (makeDash) {
            float[] dash = nStroke.getDashArray();
            if (dash == null) {
                this.cb.setLiteral("[]0 d\n");
                return;
            }
            this.cb.setLiteral('[');
            for (float f : dash) {
                this.cb.setLiteral(f);
                this.cb.setLiteral(' ');
            }
            this.cb.setLiteral(']');
            this.cb.setLiteral(nStroke.getDashPhase());
            this.cb.setLiteral(" d\n");
        }
    }

    public void setStroke(Stroke s) {
        this.originalStroke = s;
        this.stroke = transformStroke(s);
    }

    public void setRenderingHint(RenderingHints.Key key, Object value) {
        if (value != null) {
            this.rhints.put(key, value);
        } else if (key instanceof HyperLinkKey) {
            this.rhints.put(key, HyperLinkKey.VALUE_HYPERLINKKEY_OFF);
        } else {
            this.rhints.remove(key);
        }
    }

    public Object getRenderingHint(RenderingHints.Key arg0) {
        return this.rhints.get(arg0);
    }

    public void setRenderingHints(Map hints) {
        this.rhints.clear();
        this.rhints.putAll(hints);
    }

    public void addRenderingHints(Map hints) {
        this.rhints.putAll(hints);
    }

    public RenderingHints getRenderingHints() {
        return this.rhints;
    }

    public void translate(int x, int y) {
        translate(x, y);
    }

    public void translate(double tx, double ty) {
        this.transform.translate(tx, ty);
    }

    public void rotate(double theta) {
        this.transform.rotate(theta);
    }

    public void rotate(double theta, double x, double y) {
        this.transform.rotate(theta, x, y);
    }

    public void scale(double sx, double sy) {
        this.transform.scale(sx, sy);
        this.stroke = transformStroke(this.originalStroke);
    }

    public void shear(double shx, double shy) {
        this.transform.shear(shx, shy);
    }

    public void transform(AffineTransform tx) {
        this.transform.concatenate(tx);
        this.stroke = transformStroke(this.originalStroke);
    }

    public void setTransform(AffineTransform t) {
        this.transform = new AffineTransform(t);
        this.stroke = transformStroke(this.originalStroke);
    }

    public AffineTransform getTransform() {
        return new AffineTransform(this.transform);
    }

    public Paint getPaint() {
        if (this.realPaint != null) {
            return this.realPaint;
        }
        return this.paint;
    }

    public Composite getComposite() {
        return this.composite;
    }

    public void setBackground(Color color) {
        this.background = color;
    }

    public Color getBackground() {
        return this.background;
    }

    public Stroke getStroke() {
        return this.originalStroke;
    }

    public FontRenderContext getFontRenderContext() {
        boolean antialias = RenderingHints.VALUE_TEXT_ANTIALIAS_ON.equals(getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
        boolean fractions = RenderingHints.VALUE_FRACTIONALMETRICS_ON.equals(getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS));
        return new FontRenderContext(new AffineTransform(), antialias, fractions);
    }

    public Graphics create() {
        PdfGraphics2D g2 = new PdfGraphics2D();
        g2.rhints.putAll(this.rhints);
        g2.onlyShapes = this.onlyShapes;
        g2.transform = new AffineTransform(this.transform);
        g2.baseFonts = this.baseFonts;
        g2.fontMapper = this.fontMapper;
        g2.paint = this.paint;
        g2.fillGState = this.fillGState;
        g2.currentFillGState = this.currentFillGState;
        g2.strokeGState = this.strokeGState;
        g2.background = this.background;
        g2.mediaTracker = this.mediaTracker;
        g2.convertImagesToJPEG = this.convertImagesToJPEG;
        g2.jpegQuality = this.jpegQuality;
        g2.setFont(this.font);
        g2.cb = this.cb.getDuplicate();
        g2.cb.saveState();
        g2.width = this.width;
        g2.height = this.height;
        g2.followPath(new Area(new Rectangle2D.Float(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, this.width, this.height)), 3);
        if (this.clip != null) {
            g2.clip = new Area(this.clip);
        }
        g2.composite = this.composite;
        g2.stroke = this.stroke;
        g2.originalStroke = this.originalStroke;
        g2.strokeOne = g2.transformStroke(g2.strokeOne);
        g2.oldStroke = g2.strokeOne;
        g2.setStrokeDiff(g2.oldStroke, null);
        g2.cb.saveState();
        if (g2.clip != null) {
            g2.followPath(g2.clip, 3);
        }
        g2.kid = true;
        if (this.kids == null) {
            this.kids = new ArrayList();
        }
        this.kids.add(new Integer(this.cb.getInternalBuffer().size()));
        this.kids.add(g2);
        return g2;
    }

    public PdfContentByte getContent() {
        return this.cb;
    }

    public Color getColor() {
        if (this.paint instanceof Color) {
            return this.paint;
        }
        return Color.black;
    }

    public void setColor(Color color) {
        setPaint(color);
    }

    public void setPaintMode() {
    }

    public void setXORMode(Color c1) {
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font f) {
        if (f == null) {
            return;
        }
        if (this.onlyShapes) {
            this.font = f;
        } else {
            if (f == this.font) {
                return;
            }
            this.font = f;
            this.fontSize = f.getSize2D();
            this.baseFont = getCachedBaseFont(f);
        }
    }

    private BaseFont getCachedBaseFont(Font f) {
        BaseFont baseFont;
        synchronized (this.baseFonts) {
            BaseFont bf = (BaseFont) this.baseFonts.get(f.getFontName());
            if (bf == null) {
                bf = this.fontMapper.awtToPdf(f);
                this.baseFonts.put(f.getFontName(), bf);
            }
            baseFont = bf;
        }
        return baseFont;
    }

    public FontMetrics getFontMetrics(Font f) {
        return this.dg2.getFontMetrics(f);
    }

    public Rectangle getClipBounds() {
        if (this.clip == null) {
            return null;
        }
        return getClip().getBounds();
    }

    public void clipRect(int x, int y, int width, int height) {
        clip(new Rectangle2D.Double(x, y, width, height));
    }

    public void setClip(int x, int y, int width, int height) {
        setClip(new Rectangle2D.Double(x, y, width, height));
    }

    public void clip(Shape s) {
        if (s == null) {
            setClip(null);
            return;
        }
        Shape s2 = this.transform.createTransformedShape(s);
        if (this.clip == null) {
            this.clip = new Area(s2);
        } else {
            this.clip.intersect(new Area(s2));
        }
        followPath(s2, 3);
    }

    public Shape getClip() {
        try {
            return this.transform.createInverse().createTransformedShape(this.clip);
        } catch (NoninvertibleTransformException e) {
            return null;
        }
    }

    public void setClip(Shape s) {
        this.cb.restoreState();
        this.cb.saveState();
        if (s != null) {
            s = this.transform.createTransformedShape(s);
        }
        if (s == null) {
            this.clip = null;
        } else {
            this.clip = new Area(s);
            followPath(s, 3);
        }
        this.paintStroke = null;
        this.paintFill = null;
        this.currentStrokeGState = 255;
        this.currentFillGState = 255;
        this.oldStroke = this.strokeOne;
    }

    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        draw(new Line2D.Double(x1, y1, x2, y2));
    }

    public void drawRect(int x, int y, int width, int height) {
        draw(new Rectangle(x, y, width, height));
    }

    public void fillRect(int x, int y, int width, int height) {
        fill(new Rectangle(x, y, width, height));
    }

    public void clearRect(int x, int y, int width, int height) {
        Paint temp = this.paint;
        setPaint(this.background);
        fillRect(x, y, width, height);
        setPaint(temp);
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        draw(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        fill(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
    }

    public void drawOval(int x, int y, int width, int height) {
        draw(new Ellipse2D.Float(x, y, width, height));
    }

    public void fillOval(int x, int y, int width, int height) {
        fill(new Ellipse2D.Float(x, y, width, height));
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        draw(new Arc2D.Double(x, y, width, height, startAngle, arcAngle, 0));
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        fill(new Arc2D.Double(x, y, width, height, startAngle, arcAngle, 2));
    }

    public void drawPolyline(int[] x, int[] y, int nPoints) {
        PolylineShape polyline = new PolylineShape(x, y, nPoints);
        draw(polyline);
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        Polygon poly = new Polygon(xPoints, yPoints, nPoints);
        draw(poly);
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        Polygon poly = new Polygon();
        for (int i = 0; i < nPoints; i++) {
            poly.addPoint(xPoints[i], yPoints[i]);
        }
        fill(poly);
    }

    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return drawImage(img, x, y, (Color) null, observer);
    }

    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        return drawImage(img, x, y, width, height, null, observer);
    }

    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        waitForImage(img);
        return drawImage(img, x, y, img.getWidth(observer), img.getHeight(observer), bgcolor, observer);
    }

    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        waitForImage(img);
        double scalex = ((double) width) / ((double) img.getWidth(observer));
        double scaley = ((double) height) / ((double) img.getHeight(observer));
        AffineTransform tx = AffineTransform.getTranslateInstance(x, y);
        tx.scale(scalex, scaley);
        return drawImage(img, (Image) null, tx, bgcolor, observer);
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null, observer);
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        waitForImage(img);
        double dwidth = ((double) dx2) - ((double) dx1);
        double dheight = ((double) dy2) - ((double) dy1);
        double swidth = ((double) sx2) - ((double) sx1);
        double sheight = ((double) sy2) - ((double) sy1);
        if (dwidth == 0.0d || dheight == 0.0d || swidth == 0.0d || sheight == 0.0d) {
            return true;
        }
        double scalex = dwidth / swidth;
        double scaley = dheight / sheight;
        double transx = ((double) sx1) * scalex;
        double transy = ((double) sy1) * scaley;
        AffineTransform tx = AffineTransform.getTranslateInstance(((double) dx1) - transx, ((double) dy1) - transy);
        tx.scale(scalex, scaley);
        BufferedImage mask = new BufferedImage(img.getWidth(observer), img.getHeight(observer), 12);
        Graphics g = mask.getGraphics();
        g.fillRect(sx1, sy1, (int) swidth, (int) sheight);
        drawImage(img, (Image) mask, tx, (Color) null, observer);
        g.dispose();
        return true;
    }

    public void dispose() {
        if (!this.kid && !this.disposeCalled) {
            this.disposeCalled = true;
            this.cb.restoreState();
            this.cb.restoreState();
            this.dg2.dispose();
            this.dg2 = null;
            if (this.kids != null) {
                ByteBuffer buf = new ByteBuffer();
                internalDispose(buf);
                ByteBuffer buf2 = this.cb.getInternalBuffer();
                buf2.reset();
                buf2.append(buf);
            }
        }
    }

    private void internalDispose(ByteBuffer buf) {
        int last = 0;
        ByteBuffer buf2 = this.cb.getInternalBuffer();
        if (this.kids != null) {
            for (int k = 0; k < this.kids.size(); k += 2) {
                int pos = ((Integer) this.kids.get(k)).intValue();
                PdfGraphics2D g2 = (PdfGraphics2D) this.kids.get(k + 1);
                g2.cb.restoreState();
                g2.cb.restoreState();
                buf.append(buf2.getBuffer(), last, pos - last);
                g2.dg2.dispose();
                g2.dg2 = null;
                g2.internalDispose(buf);
                last = pos;
            }
        }
        buf.append(buf2.getBuffer(), last, buf2.size() - last);
    }

    private void followPath(Shape s, int drawType) {
        PathIterator points;
        if (s == null) {
        }
        if (drawType == 2 && !(this.stroke instanceof BasicStroke)) {
            followPath(this.stroke.createStrokedShape(s), 1);
            return;
        }
        if (drawType == 2) {
            setStrokeDiff(this.stroke, this.oldStroke);
            this.oldStroke = this.stroke;
            setStrokePaint();
        } else if (drawType == 1) {
            setFillPaint();
        }
        int traces = 0;
        if (drawType == 3) {
            points = s.getPathIterator(IDENTITY);
        } else {
            points = s.getPathIterator(this.transform);
        }
        float[] coords = new float[6];
        while (!points.isDone()) {
            traces++;
            int segtype = points.currentSegment(coords);
            normalizeY(coords);
            switch (segtype) {
                case 0:
                    this.cb.moveTo(coords[0], coords[1]);
                    break;
                case 1:
                    this.cb.lineTo(coords[0], coords[1]);
                    break;
                case 2:
                    this.cb.curveTo(coords[0], coords[1], coords[2], coords[3]);
                    break;
                case 3:
                    this.cb.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                    break;
                case 4:
                    this.cb.closePath();
                    break;
            }
            points.next();
        }
        switch (drawType) {
            case 1:
                if (traces > 0) {
                    if (points.getWindingRule() == 0) {
                        this.cb.eoFill();
                    } else {
                        this.cb.fill();
                    }
                }
                break;
            case 2:
                if (traces > 0) {
                    this.cb.stroke();
                }
                break;
            default:
                if (traces == 0) {
                    this.cb.rectangle(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                }
                if (points.getWindingRule() == 0) {
                    this.cb.eoClip();
                } else {
                    this.cb.clip();
                }
                this.cb.newPath();
                break;
        }
    }

    private float normalizeY(float y) {
        return this.height - y;
    }

    private void normalizeY(float[] coords) {
        coords[1] = normalizeY(coords[1]);
        coords[3] = normalizeY(coords[3]);
        coords[5] = normalizeY(coords[5]);
    }

    private AffineTransform normalizeMatrix() {
        double[] mx = new double[6];
        AffineTransform.getTranslateInstance(0.0d, 0.0d).getMatrix(mx);
        mx[3] = -1.0d;
        mx[5] = this.height;
        AffineTransform result = new AffineTransform(mx);
        result.concatenate(this.transform);
        return result;
    }

    private boolean drawImage(Image img, Image mask, AffineTransform xform, Color bgColor, ImageObserver obs) {
        AffineTransform xform2;
        com.aowagie.text.Image image;
        if (xform == null) {
            xform2 = new AffineTransform();
        } else {
            xform2 = new AffineTransform(xform);
        }
        xform2.translate(0.0d, img.getHeight(obs));
        xform2.scale(img.getWidth(obs), img.getHeight(obs));
        AffineTransform inverse = normalizeMatrix();
        AffineTransform flipper = AffineTransform.getScaleInstance(1.0d, -1.0d);
        inverse.concatenate(xform2);
        inverse.concatenate(flipper);
        double[] mx = new double[6];
        inverse.getMatrix(mx);
        if (this.currentFillGState != 255) {
            PdfGState gs = this.fillGState[255];
            if (gs == null) {
                gs = new PdfGState();
                gs.setFillOpacity(1.0f);
                this.fillGState[255] = gs;
            }
            this.cb.setGState(gs);
        }
        try {
            if (!this.convertImagesToJPEG) {
                image = com.aowagie.text.Image.getInstance(img, bgColor);
            } else {
                BufferedImage scaled = new BufferedImage(img.getWidth((ImageObserver) null), img.getHeight((ImageObserver) null), 1);
                Graphics2D g3 = scaled.createGraphics();
                g3.drawImage(img, 0, 0, img.getWidth((ImageObserver) null), img.getHeight((ImageObserver) null), (ImageObserver) null);
                g3.dispose();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                JPEGImageWriteParam jPEGImageWriteParam = new JPEGImageWriteParam(Locale.getDefault());
                jPEGImageWriteParam.setCompressionMode(2);
                jPEGImageWriteParam.setCompressionQuality(this.jpegQuality);
                ImageWriter iw = (ImageWriter) ImageIO.getImageWritersByFormatName("jpg").next();
                ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
                iw.setOutput(ios);
                iw.write((IIOMetadata) null, new IIOImage(scaled, (List) null, (IIOMetadata) null), jPEGImageWriteParam);
                iw.dispose();
                ios.close();
                scaled.flush();
                image = com.aowagie.text.Image.getInstance(baos.toByteArray());
            }
            if (mask != null) {
                com.aowagie.text.Image msk = com.aowagie.text.Image.getInstance(mask, null, true);
                msk.makeMask();
                msk.setInverted(true);
                image.setImageMask(msk);
            }
            this.cb.addImage(image, (float) mx[0], (float) mx[1], (float) mx[2], (float) mx[3], (float) mx[4], (float) mx[5]);
            Object url = getRenderingHint(HyperLinkKey.KEY_INSTANCE);
            if (url != null && !url.equals(HyperLinkKey.VALUE_HYPERLINKKEY_OFF)) {
                PdfAction action = new PdfAction(url.toString());
                this.cb.setAction(action, (float) mx[4], (float) mx[5], (float) (mx[0] + mx[4]), (float) (mx[3] + mx[5]));
            }
            if (this.currentFillGState != 255) {
                this.cb.setGState(this.fillGState[this.currentFillGState]);
                return true;
            }
            return true;
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    private boolean checkNewPaint(Paint oldPaint) {
        if (this.paint == oldPaint) {
            return false;
        }
        return ((this.paint instanceof Color) && this.paint.equals(oldPaint)) ? false : true;
    }

    private void setFillPaint() {
        if (checkNewPaint(this.paintFill)) {
            this.paintFill = this.paint;
            setPaint(false, 0.0d, 0.0d, true);
        }
    }

    private void setStrokePaint() {
        if (checkNewPaint(this.paintStroke)) {
            this.paintStroke = this.paint;
            setPaint(false, 0.0d, 0.0d, false);
        }
    }

    private void setPaint(boolean invert, double xoffset, double yoffset, boolean fill) {
        if (this.paint instanceof Color) {
            Color color = this.paint;
            int alpha = color.getAlpha();
            if (fill) {
                if (alpha != this.currentFillGState) {
                    this.currentFillGState = alpha;
                    PdfGState gs = this.fillGState[alpha];
                    if (gs == null) {
                        gs = new PdfGState();
                        gs.setFillOpacity(alpha / 255.0f);
                        this.fillGState[alpha] = gs;
                    }
                    this.cb.setGState(gs);
                }
                this.cb.setColorFill(color);
                return;
            }
            if (alpha != this.currentStrokeGState) {
                this.currentStrokeGState = alpha;
                PdfGState gs2 = this.strokeGState[alpha];
                if (gs2 == null) {
                    gs2 = new PdfGState();
                    gs2.setStrokeOpacity(alpha / 255.0f);
                    this.strokeGState[alpha] = gs2;
                }
                this.cb.setGState(gs2);
            }
            this.cb.setColorStroke(color);
            return;
        }
        if (this.paint instanceof GradientPaint) {
            GradientPaint gp = this.paint;
            Point2D p1 = gp.getPoint1();
            this.transform.transform(p1, p1);
            Point2D p2 = gp.getPoint2();
            this.transform.transform(p2, p2);
            Color c1 = gp.getColor1();
            Color c2 = gp.getColor2();
            PdfShading shading = PdfShading.simpleAxial(this.cb.getPdfWriter(), (float) p1.getX(), normalizeY((float) p1.getY()), (float) p2.getX(), normalizeY((float) p2.getY()), c1, c2);
            PdfShadingPattern pat = new PdfShadingPattern(shading);
            if (fill) {
                this.cb.setShadingFill(pat);
                return;
            } else {
                this.cb.setShadingStroke(pat);
                return;
            }
        }
        if (this.paint instanceof TexturePaint) {
            try {
                TexturePaint tp = this.paint;
                BufferedImage img = tp.getImage();
                Rectangle2D rect = tp.getAnchorRect();
                com.aowagie.text.Image image = com.aowagie.text.Image.getInstance(img, null);
                PdfPatternPainter pattern = this.cb.createPattern(image.getWidth(), image.getHeight());
                AffineTransform inverse = normalizeMatrix();
                inverse.translate(rect.getX(), rect.getY());
                inverse.scale(rect.getWidth() / ((double) image.getWidth()), (-rect.getHeight()) / ((double) image.getHeight()));
                double[] mx = new double[6];
                inverse.getMatrix(mx);
                pattern.setPatternMatrix((float) mx[0], (float) mx[1], (float) mx[2], (float) mx[3], (float) mx[4], (float) mx[5]);
                image.setAbsolutePosition(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
                pattern.addImage(image);
                if (fill) {
                    this.cb.setPatternFill(pattern);
                } else {
                    this.cb.setPatternStroke(pattern);
                }
                return;
            } catch (Exception e) {
                if (fill) {
                    this.cb.setColorFill(Color.gray);
                    return;
                } else {
                    this.cb.setColorStroke(Color.gray);
                    return;
                }
            }
        }
        try {
            int type = 6;
            if (this.paint.getTransparency() == 1) {
                type = 5;
            }
            BufferedImage img2 = new BufferedImage((int) this.width, (int) this.height, type);
            Graphics2D g = img2.getGraphics();
            g.transform(this.transform);
            AffineTransform inv = this.transform.createInverse();
            Shape fillRect = inv.createTransformedShape(new Rectangle2D.Double(0.0d, 0.0d, img2.getWidth(), img2.getHeight()));
            g.setPaint(this.paint);
            g.fill(fillRect);
            if (invert) {
                AffineTransform tx = new AffineTransform();
                tx.scale(1.0d, -1.0d);
                tx.translate(-xoffset, -yoffset);
                g.drawImage(img2, tx, (ImageObserver) null);
            }
            g.dispose();
            com.aowagie.text.Image image2 = com.aowagie.text.Image.getInstance(img2, null);
            PdfPatternPainter pattern2 = this.cb.createPattern(this.width, this.height);
            image2.setAbsolutePosition(ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
            pattern2.addImage(image2);
            if (fill) {
                this.cb.setPatternFill(pattern2);
            } else {
                this.cb.setPatternStroke(pattern2);
            }
        } catch (Exception e2) {
            if (fill) {
                this.cb.setColorFill(Color.gray);
            } else {
                this.cb.setColorStroke(Color.gray);
            }
        }
    }

    private synchronized void waitForImage(Image image) {
        if (this.mediaTracker == null) {
            this.mediaTracker = new MediaTracker(new FakeComponent());
        }
        this.mediaTracker.addImage(image, 0);
        try {
            this.mediaTracker.waitForID(0);
        } catch (InterruptedException e) {
        }
        this.mediaTracker.removeImage(image);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfGraphics2D$FakeComponent.class */
    private static class FakeComponent extends Component {
        private static final long serialVersionUID = 6450197945596086638L;

        private FakeComponent() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfGraphics2D$HyperLinkKey.class */
    public static class HyperLinkKey extends RenderingHints.Key {
        public static final HyperLinkKey KEY_INSTANCE = new HyperLinkKey(9999);
        public static final Object VALUE_HYPERLINKKEY_OFF = "0";

        protected HyperLinkKey(int arg0) {
            super(arg0);
        }

        public boolean isCompatibleValue(Object val) {
            return true;
        }

        public String toString() {
            return "HyperLinkKey";
        }
    }
}
