package org.apache.pdfbox.pdmodel.graphics.state;

import java.awt.Composite;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.PDLineDashPattern;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendComposite;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/state/PDGraphicsState.class */
public class PDGraphicsState implements Cloneable {
    private boolean isClippingPathDirty;
    private RenderingIntent renderingIntent;
    private PDSoftMask softMask;
    private List<Path2D> clippingPaths = new ArrayList();
    private Map<Path2D, Area> clippingCache = new IdentityHashMap();
    private Matrix currentTransformationMatrix = new Matrix();
    private PDColor strokingColor = PDDeviceGray.INSTANCE.getInitialColor();
    private PDColor nonStrokingColor = PDDeviceGray.INSTANCE.getInitialColor();
    private PDColorSpace strokingColorSpace = PDDeviceGray.INSTANCE;
    private PDColorSpace nonStrokingColorSpace = PDDeviceGray.INSTANCE;
    private PDTextState textState = new PDTextState();
    private float lineWidth = 1.0f;
    private int lineCap = 0;
    private int lineJoin = 0;
    private float miterLimit = 10.0f;
    private PDLineDashPattern lineDashPattern = new PDLineDashPattern();
    private boolean strokeAdjustment = false;
    private BlendMode blendMode = BlendMode.COMPATIBLE;
    private double alphaConstant = 1.0d;
    private double nonStrokingAlphaConstant = 1.0d;
    private boolean alphaSource = false;
    private boolean overprint = false;
    private boolean nonStrokingOverprint = false;
    private double overprintMode = 0.0d;
    private COSBase transfer = null;
    private double flatness = 1.0d;
    private double smoothness = 0.0d;

    public PDGraphicsState(PDRectangle page) {
        this.clippingPaths.add(new Path2D.Double(page.toGeneralPath()));
    }

    public Matrix getCurrentTransformationMatrix() {
        return this.currentTransformationMatrix;
    }

    public void setCurrentTransformationMatrix(Matrix value) {
        this.currentTransformationMatrix = value;
    }

    public float getLineWidth() {
        return this.lineWidth;
    }

    public void setLineWidth(float value) {
        this.lineWidth = value;
    }

    public int getLineCap() {
        return this.lineCap;
    }

    public void setLineCap(int value) {
        this.lineCap = value;
    }

    public int getLineJoin() {
        return this.lineJoin;
    }

    public void setLineJoin(int value) {
        this.lineJoin = value;
    }

    public float getMiterLimit() {
        return this.miterLimit;
    }

    public void setMiterLimit(float value) {
        this.miterLimit = value;
    }

    public boolean isStrokeAdjustment() {
        return this.strokeAdjustment;
    }

    public void setStrokeAdjustment(boolean value) {
        this.strokeAdjustment = value;
    }

    public double getAlphaConstant() {
        return this.alphaConstant;
    }

    public void setAlphaConstant(double value) {
        this.alphaConstant = value;
    }

    @Deprecated
    public double getNonStrokeAlphaConstants() {
        return this.nonStrokingAlphaConstant;
    }

    @Deprecated
    public void setNonStrokeAlphaConstants(double value) {
        this.nonStrokingAlphaConstant = value;
    }

    public double getNonStrokeAlphaConstant() {
        return this.nonStrokingAlphaConstant;
    }

    public void setNonStrokeAlphaConstant(double value) {
        this.nonStrokingAlphaConstant = value;
    }

    public boolean isAlphaSource() {
        return this.alphaSource;
    }

    public void setAlphaSource(boolean value) {
        this.alphaSource = value;
    }

    public PDSoftMask getSoftMask() {
        return this.softMask;
    }

    public void setSoftMask(PDSoftMask softMask) {
        this.softMask = softMask;
    }

    public BlendMode getBlendMode() {
        return this.blendMode;
    }

    public void setBlendMode(BlendMode blendMode) {
        this.blendMode = blendMode;
    }

    public boolean isOverprint() {
        return this.overprint;
    }

    public void setOverprint(boolean value) {
        this.overprint = value;
    }

    public boolean isNonStrokingOverprint() {
        return this.nonStrokingOverprint;
    }

    public void setNonStrokingOverprint(boolean value) {
        this.nonStrokingOverprint = value;
    }

    public double getOverprintMode() {
        return this.overprintMode;
    }

    public void setOverprintMode(double value) {
        this.overprintMode = value;
    }

    public double getFlatness() {
        return this.flatness;
    }

    public void setFlatness(double value) {
        this.flatness = value;
    }

    public double getSmoothness() {
        return this.smoothness;
    }

    public void setSmoothness(double value) {
        this.smoothness = value;
    }

    public PDTextState getTextState() {
        return this.textState;
    }

    public void setTextState(PDTextState value) {
        this.textState = value;
    }

    public PDLineDashPattern getLineDashPattern() {
        return this.lineDashPattern;
    }

    public void setLineDashPattern(PDLineDashPattern value) {
        this.lineDashPattern = value;
    }

    public RenderingIntent getRenderingIntent() {
        return this.renderingIntent;
    }

    public void setRenderingIntent(RenderingIntent value) {
        this.renderingIntent = value;
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public PDGraphicsState m1220clone() {
        try {
            PDGraphicsState clone = (PDGraphicsState) super.clone();
            clone.textState = this.textState.m1222clone();
            clone.currentTransformationMatrix = this.currentTransformationMatrix.m1288clone();
            clone.strokingColor = this.strokingColor;
            clone.nonStrokingColor = this.nonStrokingColor;
            clone.lineDashPattern = this.lineDashPattern;
            clone.clippingPaths = this.clippingPaths;
            clone.clippingCache = this.clippingCache;
            clone.isClippingPathDirty = false;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public PDColor getStrokingColor() {
        return this.strokingColor;
    }

    public void setStrokingColor(PDColor color) {
        this.strokingColor = color;
    }

    public PDColor getNonStrokingColor() {
        return this.nonStrokingColor;
    }

    public void setNonStrokingColor(PDColor color) {
        this.nonStrokingColor = color;
    }

    public PDColorSpace getStrokingColorSpace() {
        return this.strokingColorSpace;
    }

    public void setStrokingColorSpace(PDColorSpace colorSpace) {
        this.strokingColorSpace = colorSpace;
    }

    public PDColorSpace getNonStrokingColorSpace() {
        return this.nonStrokingColorSpace;
    }

    public void setNonStrokingColorSpace(PDColorSpace colorSpace) {
        this.nonStrokingColorSpace = colorSpace;
    }

    public void intersectClippingPath(GeneralPath path) {
        intersectClippingPath(new Path2D.Double(path), true);
    }

    private void intersectClippingPath(Path2D path, boolean clonePath) {
        if (!this.isClippingPathDirty) {
            this.clippingPaths = new ArrayList(this.clippingPaths);
            this.isClippingPathDirty = true;
        }
        this.clippingPaths.add(clonePath ? (Path2D) path.clone() : path);
    }

    public void intersectClippingPath(Area area) {
        intersectClippingPath(new Path2D.Double(area), false);
    }

    public Area getCurrentClippingPath() {
        if (this.clippingPaths.size() == 1) {
            Path2D path = this.clippingPaths.get(0);
            Area area = this.clippingCache.get(path);
            if (area == null) {
                area = new Area(path);
                this.clippingCache.put(path, area);
            }
            return area;
        }
        Area clippingArea = new Area();
        clippingArea.add(new Area(this.clippingPaths.get(0)));
        for (int i = 1; i < this.clippingPaths.size(); i++) {
            clippingArea.intersect(new Area(this.clippingPaths.get(i)));
        }
        Path2D newPath = new Path2D.Double(clippingArea);
        this.clippingPaths = new ArrayList();
        this.clippingPaths.add(newPath);
        this.clippingCache.put(newPath, clippingArea);
        return clippingArea;
    }

    public List<Path2D> getCurrentClippingPaths() {
        return this.clippingPaths;
    }

    public Composite getStrokingJavaComposite() {
        return BlendComposite.getInstance(this.blendMode, (float) this.alphaConstant);
    }

    public Composite getNonStrokingJavaComposite() {
        return BlendComposite.getInstance(this.blendMode, (float) this.nonStrokingAlphaConstant);
    }

    public COSBase getTransfer() {
        return this.transfer;
    }

    public void setTransfer(COSBase transfer) {
        this.transfer = transfer;
    }
}
