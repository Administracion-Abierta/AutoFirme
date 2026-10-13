package org.apache.pdfbox.rendering;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/rendering/GroupGraphics.class */
class GroupGraphics extends Graphics2D {
    private final BufferedImage groupImage;
    private final BufferedImage groupAlphaImage;
    private final Graphics2D groupG2D;
    private final Graphics2D alphaG2D;

    GroupGraphics(BufferedImage groupImage, Graphics2D groupGraphics) {
        this.groupImage = groupImage;
        this.groupG2D = groupGraphics;
        this.groupAlphaImage = new BufferedImage(groupImage.getWidth(), groupImage.getHeight(), 2);
        this.alphaG2D = this.groupAlphaImage.createGraphics();
    }

    private GroupGraphics(BufferedImage groupImage, Graphics2D groupGraphics, BufferedImage groupAlphaImage, Graphics2D alphaGraphics) {
        this.groupImage = groupImage;
        this.groupG2D = groupGraphics;
        this.groupAlphaImage = groupAlphaImage;
        this.alphaG2D = alphaGraphics;
    }

    public void clearRect(int x, int y, int width, int height) {
        this.groupG2D.clearRect(x, y, width, height);
        this.alphaG2D.clearRect(x, y, width, height);
    }

    public void clipRect(int x, int y, int width, int height) {
        this.groupG2D.clipRect(x, y, width, height);
        this.alphaG2D.clipRect(x, y, width, height);
    }

    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        this.groupG2D.copyArea(x, y, width, height, dx, dy);
        this.alphaG2D.copyArea(x, y, width, height, dx, dy);
    }

    public Graphics create() {
        Graphics2D graphics2DCreate = this.groupG2D.create();
        Graphics2D graphics2DCreate2 = this.alphaG2D.create();
        if ((graphics2DCreate instanceof Graphics2D) && (graphics2DCreate2 instanceof Graphics2D)) {
            return new GroupGraphics(this.groupImage, graphics2DCreate, this.groupAlphaImage, graphics2DCreate2);
        }
        graphics2DCreate.dispose();
        graphics2DCreate2.dispose();
        throw new UnsupportedOperationException("Only Graphics2D supported by this method");
    }

    public void dispose() {
        this.groupG2D.dispose();
        this.alphaG2D.dispose();
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        this.groupG2D.drawArc(x, y, width, height, startAngle, arcAngle);
        this.alphaG2D.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        this.groupG2D.drawImage(img, x, y, bgcolor, observer);
        return this.alphaG2D.drawImage(img, x, y, bgcolor, observer);
    }

    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        this.groupG2D.drawImage(img, x, y, observer);
        return this.alphaG2D.drawImage(img, x, y, observer);
    }

    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        this.groupG2D.drawImage(img, x, y, width, height, bgcolor, observer);
        return this.alphaG2D.drawImage(img, x, y, width, height, bgcolor, observer);
    }

    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        this.groupG2D.drawImage(img, x, y, width, height, observer);
        return this.alphaG2D.drawImage(img, x, y, width, height, observer);
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        this.groupG2D.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
        return this.alphaG2D.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        this.groupG2D.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
        return this.alphaG2D.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        this.groupG2D.drawLine(x1, y1, x2, y2);
        this.alphaG2D.drawLine(x1, y1, x2, y2);
    }

    public void drawOval(int x, int y, int width, int height) {
        this.groupG2D.drawOval(x, y, width, height);
        this.alphaG2D.drawOval(x, y, width, height);
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        this.groupG2D.drawPolygon(xPoints, yPoints, nPoints);
        this.alphaG2D.drawPolygon(xPoints, yPoints, nPoints);
    }

    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        this.groupG2D.drawPolyline(xPoints, yPoints, nPoints);
        this.alphaG2D.drawPolyline(xPoints, yPoints, nPoints);
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        this.groupG2D.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
        this.alphaG2D.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        this.groupG2D.drawString(iterator, x, y);
        this.alphaG2D.drawString(iterator, x, y);
    }

    public void drawString(String str, int x, int y) {
        this.groupG2D.drawString(str, x, y);
        this.alphaG2D.drawString(str, x, y);
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        this.groupG2D.fillArc(x, y, width, height, startAngle, arcAngle);
        this.alphaG2D.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    public void fillOval(int x, int y, int width, int height) {
        this.groupG2D.fillOval(x, y, width, height);
        this.alphaG2D.fillOval(x, y, width, height);
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        this.groupG2D.fillPolygon(xPoints, yPoints, nPoints);
        this.alphaG2D.fillPolygon(xPoints, yPoints, nPoints);
    }

    public void fillRect(int x, int y, int width, int height) {
        this.groupG2D.fillRect(x, y, width, height);
        this.alphaG2D.fillRect(x, y, width, height);
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        this.groupG2D.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
        this.alphaG2D.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public Shape getClip() {
        return this.groupG2D.getClip();
    }

    public Rectangle getClipBounds() {
        return this.groupG2D.getClipBounds();
    }

    public Color getColor() {
        return this.groupG2D.getColor();
    }

    public Font getFont() {
        return this.groupG2D.getFont();
    }

    public FontMetrics getFontMetrics(Font f) {
        return this.groupG2D.getFontMetrics(f);
    }

    public void setClip(int x, int y, int width, int height) {
        this.groupG2D.setClip(x, y, width, height);
        this.alphaG2D.setClip(x, y, width, height);
    }

    public void setClip(Shape clip) {
        this.groupG2D.setClip(clip);
        this.alphaG2D.setClip(clip);
    }

    public void setColor(Color c) {
        this.groupG2D.setColor(c);
        this.alphaG2D.setColor(c);
    }

    public void setFont(Font font) {
        this.groupG2D.setFont(font);
        this.alphaG2D.setFont(font);
    }

    public void setPaintMode() {
        this.groupG2D.setPaintMode();
        this.alphaG2D.setPaintMode();
    }

    public void setXORMode(Color c1) {
        this.groupG2D.setXORMode(c1);
        this.alphaG2D.setXORMode(c1);
    }

    public void translate(int x, int y) {
        this.groupG2D.translate(x, y);
        this.alphaG2D.translate(x, y);
    }

    public void addRenderingHints(Map<?, ?> hints) {
        this.groupG2D.addRenderingHints(hints);
        this.alphaG2D.addRenderingHints(hints);
    }

    public void clip(Shape s) {
        this.groupG2D.clip(s);
        this.alphaG2D.clip(s);
    }

    public void draw(Shape s) {
        this.groupG2D.draw(s);
        this.alphaG2D.draw(s);
    }

    public void drawGlyphVector(GlyphVector g, float x, float y) {
        this.groupG2D.drawGlyphVector(g, x, y);
        this.alphaG2D.drawGlyphVector(g, x, y);
    }

    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        this.groupG2D.drawImage(img, op, x, y);
        this.alphaG2D.drawImage(img, op, x, y);
    }

    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        this.groupG2D.drawImage(img, xform, obs);
        return this.alphaG2D.drawImage(img, xform, obs);
    }

    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        this.groupG2D.drawRenderableImage(img, xform);
        this.alphaG2D.drawRenderableImage(img, xform);
    }

    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        this.groupG2D.drawRenderedImage(img, xform);
        this.alphaG2D.drawRenderedImage(img, xform);
    }

    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        this.groupG2D.drawString(iterator, x, y);
        this.alphaG2D.drawString(iterator, x, y);
    }

    public void drawString(String str, float x, float y) {
        this.groupG2D.drawString(str, x, y);
        this.alphaG2D.drawString(str, x, y);
    }

    public void fill(Shape s) {
        this.groupG2D.fill(s);
        this.alphaG2D.fill(s);
    }

    public Color getBackground() {
        return this.groupG2D.getBackground();
    }

    public Composite getComposite() {
        return this.groupG2D.getComposite();
    }

    public GraphicsConfiguration getDeviceConfiguration() {
        return this.groupG2D.getDeviceConfiguration();
    }

    public FontRenderContext getFontRenderContext() {
        return this.groupG2D.getFontRenderContext();
    }

    public Paint getPaint() {
        return this.groupG2D.getPaint();
    }

    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return this.groupG2D.getRenderingHint(hintKey);
    }

    public RenderingHints getRenderingHints() {
        return this.groupG2D.getRenderingHints();
    }

    public Stroke getStroke() {
        return this.groupG2D.getStroke();
    }

    public AffineTransform getTransform() {
        return this.groupG2D.getTransform();
    }

    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return this.groupG2D.hit(rect, s, onStroke);
    }

    public void rotate(double theta) {
        this.groupG2D.rotate(theta);
        this.alphaG2D.rotate(theta);
    }

    public void rotate(double theta, double x, double y) {
        this.groupG2D.rotate(theta, x, y);
        this.alphaG2D.rotate(theta, x, y);
    }

    public void scale(double sx, double sy) {
        this.groupG2D.scale(sx, sy);
        this.alphaG2D.scale(sx, sy);
    }

    public void setBackground(Color color) {
        this.groupG2D.setBackground(color);
        this.alphaG2D.setBackground(color);
    }

    public void setComposite(Composite comp) {
        this.groupG2D.setComposite(comp);
        this.alphaG2D.setComposite(comp);
    }

    public void setPaint(Paint paint) {
        this.groupG2D.setPaint(paint);
        this.alphaG2D.setPaint(paint);
    }

    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        this.groupG2D.setRenderingHint(hintKey, hintValue);
        this.alphaG2D.setRenderingHint(hintKey, hintValue);
    }

    public void setRenderingHints(Map<?, ?> hints) {
        this.groupG2D.setRenderingHints(hints);
        this.alphaG2D.setRenderingHints(hints);
    }

    public void setStroke(Stroke s) {
        this.groupG2D.setStroke(s);
        this.alphaG2D.setStroke(s);
    }

    public void setTransform(AffineTransform tx) {
        this.groupG2D.setTransform(tx);
        this.alphaG2D.setTransform(tx);
    }

    public void shear(double shx, double shy) {
        this.groupG2D.shear(shx, shy);
        this.alphaG2D.shear(shx, shy);
    }

    public void transform(AffineTransform tx) {
        this.groupG2D.transform(tx);
        this.alphaG2D.transform(tx);
    }

    public void translate(double tx, double ty) {
        this.groupG2D.translate(tx, ty);
        this.alphaG2D.translate(tx, ty);
    }

    void removeBackdrop(BufferedImage backdrop, int offsetX, int offsetY) {
        int backdropRGB;
        float alpha0;
        int backdropRGB2;
        float alpha1;
        int groupWidth = this.groupImage.getWidth();
        int groupHeight = this.groupImage.getHeight();
        int backdropWidth = backdrop.getWidth();
        int backdropHeight = backdrop.getHeight();
        int groupType = this.groupImage.getType();
        int groupAlphaType = this.groupAlphaImage.getType();
        int backdropType = backdrop.getType();
        DataBufferInt dataBuffer = this.groupImage.getRaster().getDataBuffer();
        DataBufferInt dataBuffer2 = this.groupAlphaImage.getRaster().getDataBuffer();
        DataBufferInt dataBuffer3 = backdrop.getRaster().getDataBuffer();
        if (groupType == 2 && groupAlphaType == 2 && ((backdropType == 2 || backdropType == 1) && (dataBuffer instanceof DataBufferInt) && (dataBuffer2 instanceof DataBufferInt) && (dataBuffer3 instanceof DataBufferInt))) {
            int[] groupData = dataBuffer.getData();
            int[] groupAlphaData = dataBuffer2.getData();
            int[] backdropData = dataBuffer3.getData();
            boolean backdropHasAlpha = backdropType == 2;
            for (int y = 0; y < groupHeight; y++) {
                for (int x = 0; x < groupWidth; x++) {
                    int index = x + (y * groupWidth);
                    int alphagn = (groupAlphaData[index] >> 24) & 255;
                    if (alphagn == 0) {
                        groupData[index] = 0;
                    } else {
                        int backdropX = x + offsetX;
                        int backdropY = y + offsetY;
                        if (backdropX >= 0 && backdropX < backdropWidth && backdropY >= 0 && backdropY < backdropHeight) {
                            backdropRGB2 = backdropData[backdropX + (backdropY * backdropWidth)];
                            alpha1 = backdropHasAlpha ? (backdropRGB2 >> 24) & 255 : 255.0f;
                        } else {
                            backdropRGB2 = 0;
                            alpha1 = 0.0f;
                        }
                        float alphaFactor = (alpha1 / alphagn) - (alpha1 / 255.0f);
                        int groupRGB = groupData[index];
                        int r = backdropRemoval(groupRGB, backdropRGB2, 16, alphaFactor);
                        int g = backdropRemoval(groupRGB, backdropRGB2, 8, alphaFactor);
                        int b = backdropRemoval(groupRGB, backdropRGB2, 0, alphaFactor);
                        groupData[index] = (alphagn << 24) | (r << 16) | (g << 8) | b;
                    }
                }
            }
            return;
        }
        for (int y2 = 0; y2 < groupHeight; y2++) {
            for (int x2 = 0; x2 < groupWidth; x2++) {
                int alphagn2 = (this.groupAlphaImage.getRGB(x2, y2) >> 24) & 255;
                if (alphagn2 == 0) {
                    this.groupImage.setRGB(x2, y2, 0);
                } else {
                    int backdropX2 = x2 + offsetX;
                    int backdropY2 = y2 + offsetY;
                    if (backdropX2 >= 0 && backdropX2 < backdropWidth && backdropY2 >= 0 && backdropY2 < backdropHeight) {
                        backdropRGB = backdrop.getRGB(backdropX2, backdropY2);
                        alpha0 = (backdropRGB >> 24) & 255;
                    } else {
                        backdropRGB = 0;
                        alpha0 = 0.0f;
                    }
                    int groupRGB2 = this.groupImage.getRGB(x2, y2);
                    float alphaFactor2 = (alpha0 / alphagn2) - (alpha0 / 255.0f);
                    int r2 = backdropRemoval(groupRGB2, backdropRGB, 16, alphaFactor2);
                    int g2 = backdropRemoval(groupRGB2, backdropRGB, 8, alphaFactor2);
                    int b2 = backdropRemoval(groupRGB2, backdropRGB, 0, alphaFactor2);
                    this.groupImage.setRGB(x2, y2, (alphagn2 << 24) | (r2 << 16) | (g2 << 8) | b2);
                }
            }
        }
    }

    private int backdropRemoval(int groupRGB, int backdropRGB, int shift, float alphaFactor) {
        float cn = (groupRGB >> shift) & 255;
        float c0 = (backdropRGB >> shift) & 255;
        int c = Math.round(cn + ((cn - c0) * alphaFactor));
        if (c < 0) {
            return 0;
        }
        if (c > 255) {
            return 255;
        }
        return c;
    }
}
