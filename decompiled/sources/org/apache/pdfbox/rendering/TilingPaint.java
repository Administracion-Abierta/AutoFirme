package org.apache.pdfbox.rendering;

import com.aowagie.text.pdf.ColumnText;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/rendering/TilingPaint.class */
class TilingPaint implements Paint {
    private static final Log LOG = LogFactory.getLog(TilingPaint.class);
    private final Paint paint;
    private final Matrix patternMatrix;
    private static final int MAXEDGE;
    private static final String DEFAULTMAXEDGE = "3000";

    static {
        int val;
        String s = System.getProperty("pdfbox.rendering.tilingpaint.maxedge", DEFAULTMAXEDGE);
        try {
            val = Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            LOG.error("Default will be used", ex);
            val = Integer.parseInt(DEFAULTMAXEDGE);
        }
        MAXEDGE = val;
    }

    TilingPaint(PageDrawer drawer, PDTilingPattern pattern, AffineTransform xform) throws IOException {
        this(drawer, pattern, null, null, xform);
    }

    TilingPaint(PageDrawer drawer, PDTilingPattern pattern, PDColorSpace colorSpace, PDColor color, AffineTransform xform) throws IOException {
        this.patternMatrix = Matrix.concatenate(drawer.getInitialMatrix(), pattern.getMatrix());
        Rectangle2D anchorRect = getAnchorRect(pattern);
        this.paint = new TexturePaint(getImage(drawer, pattern, colorSpace, color, xform, anchorRect), anchorRect);
    }

    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        AffineTransform xformPattern = (AffineTransform) xform.clone();
        AffineTransform patternNoScale = this.patternMatrix.createAffineTransform();
        patternNoScale.scale(1.0f / this.patternMatrix.getScalingFactorX(), 1.0f / this.patternMatrix.getScalingFactorY());
        xformPattern.concatenate(patternNoScale);
        return this.paint.createContext(cm, deviceBounds, userBounds, xformPattern, hints);
    }

    private BufferedImage getImage(PageDrawer drawer, PDTilingPattern pattern, PDColorSpace colorSpace, PDColor color, AffineTransform xform, Rectangle2D anchorRect) throws IOException {
        float width = (float) Math.abs(anchorRect.getWidth());
        float height = (float) Math.abs(anchorRect.getHeight());
        Matrix xformMatrix = new Matrix(xform);
        float xScale = Math.abs(xformMatrix.getScalingFactorX());
        float yScale = Math.abs(xformMatrix.getScalingFactorY());
        float width2 = width * xScale;
        float height2 = height * yScale;
        int rasterWidth = Math.max(1, ceiling(width2));
        int rasterHeight = Math.max(1, ceiling(height2));
        BufferedImage image = new BufferedImage(rasterWidth, rasterHeight, 2);
        Graphics2D graphics = image.createGraphics();
        if (pattern.getYStep() < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            graphics.translate(0, rasterHeight);
            graphics.scale(1.0d, -1.0d);
        }
        if (pattern.getXStep() < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            graphics.translate(rasterWidth, 0);
            graphics.scale(-1.0d, 1.0d);
        }
        graphics.scale(xScale, yScale);
        Matrix newPatternMatrix = Matrix.getScaleInstance(Math.abs(this.patternMatrix.getScalingFactorX()), Math.abs(this.patternMatrix.getScalingFactorY()));
        PDRectangle bbox = pattern.getBBox();
        newPatternMatrix.concatenate(Matrix.getTranslateInstance(-bbox.getLowerLeftX(), -bbox.getLowerLeftY()));
        drawer.drawTilingPattern(graphics, pattern, colorSpace, color, newPatternMatrix);
        graphics.dispose();
        return image;
    }

    private static int ceiling(double num) {
        BigDecimal decimal = BigDecimal.valueOf(num);
        return decimal.setScale(5, RoundingMode.CEILING).intValue();
    }

    public int getTransparency() {
        return 3;
    }

    private Rectangle2D getAnchorRect(PDTilingPattern pattern) throws IOException {
        PDRectangle bbox = pattern.getBBox();
        if (bbox == null) {
            throw new IOException("Pattern /BBox is missing");
        }
        float xStep = pattern.getXStep();
        if (xStep == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            LOG.warn("/XStep is 0, using pattern /BBox width");
            xStep = bbox.getWidth();
        }
        float yStep = pattern.getYStep();
        if (yStep == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            LOG.warn("/YStep is 0, using pattern /BBox height");
            yStep = bbox.getHeight();
        }
        float xScale = this.patternMatrix.getScalingFactorX();
        float yScale = this.patternMatrix.getScalingFactorY();
        float width = xStep * xScale;
        float height = yStep * yScale;
        if (Math.abs(width * height) > MAXEDGE * MAXEDGE) {
            LOG.info("Pattern surface is too large, will be clipped");
            LOG.info("width: " + width + ", height: " + height);
            LOG.info("XStep: " + xStep + ", YStep: " + yStep);
            LOG.info("bbox: " + bbox);
            LOG.info("pattern matrix: " + pattern.getMatrix());
            LOG.info("concatenated matrix: " + this.patternMatrix);
            width = Math.min(MAXEDGE, Math.abs(width)) * Math.signum(width);
            height = Math.min(MAXEDGE, Math.abs(height)) * Math.signum(height);
        }
        return new Rectangle2D.Float(bbox.getLowerLeftX() * xScale, bbox.getLowerLeftY() * yScale, width, height);
    }
}
