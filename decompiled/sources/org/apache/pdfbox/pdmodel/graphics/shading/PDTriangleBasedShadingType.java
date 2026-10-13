package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDRange;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/PDTriangleBasedShadingType.class */
abstract class PDTriangleBasedShadingType extends PDShading {
    private COSArray decode;
    private static final Log LOG = LogFactory.getLog(PDTriangleBasedShadingType.class);
    private int bitsPerCoordinate;
    private int bitsPerColorComponent;
    private int numberOfColorComponents;

    abstract List<ShadedTriangle> collectTriangles(AffineTransform affineTransform, Matrix matrix) throws IOException;

    PDTriangleBasedShadingType(COSDictionary shadingDictionary) {
        super(shadingDictionary);
        this.decode = null;
        this.bitsPerCoordinate = -1;
        this.bitsPerColorComponent = -1;
        this.numberOfColorComponents = -1;
    }

    public int getBitsPerComponent() {
        if (this.bitsPerColorComponent == -1) {
            this.bitsPerColorComponent = getCOSObject().getInt(COSName.BITS_PER_COMPONENT, -1);
            LOG.debug("bitsPerColorComponent: " + this.bitsPerColorComponent);
        }
        return this.bitsPerColorComponent;
    }

    public void setBitsPerComponent(int bitsPerComponent) {
        getCOSObject().setInt(COSName.BITS_PER_COMPONENT, bitsPerComponent);
        this.bitsPerColorComponent = bitsPerComponent;
    }

    public int getBitsPerCoordinate() {
        if (this.bitsPerCoordinate == -1) {
            this.bitsPerCoordinate = getCOSObject().getInt(COSName.BITS_PER_COORDINATE, -1);
            LOG.debug("bitsPerCoordinate: " + (Math.pow(2.0d, this.bitsPerCoordinate) - 1.0d));
        }
        return this.bitsPerCoordinate;
    }

    public void setBitsPerCoordinate(int bitsPerCoordinate) {
        getCOSObject().setInt(COSName.BITS_PER_COORDINATE, bitsPerCoordinate);
        this.bitsPerCoordinate = bitsPerCoordinate;
    }

    public int getNumberOfColorComponents() throws IOException {
        if (this.numberOfColorComponents == -1) {
            this.numberOfColorComponents = getFunction() != null ? 1 : getColorSpace().getNumberOfComponents();
            LOG.debug("numberOfColorComponents: " + this.numberOfColorComponents);
        }
        return this.numberOfColorComponents;
    }

    private COSArray getDecodeValues() {
        if (this.decode == null) {
            this.decode = (COSArray) getCOSObject().getDictionaryObject(COSName.DECODE);
        }
        return this.decode;
    }

    public void setDecodeValues(COSArray decodeValues) {
        this.decode = decodeValues;
        getCOSObject().setItem(COSName.DECODE, (COSBase) decodeValues);
    }

    public PDRange getDecodeForParameter(int paramNum) {
        PDRange retval = null;
        COSArray decodeValues = getDecodeValues();
        if (decodeValues != null && decodeValues.size() >= (paramNum * 2) + 1) {
            retval = new PDRange(decodeValues, paramNum);
        }
        return retval;
    }

    protected float interpolate(float src, long srcMax, float dstMin, float dstMax) {
        return dstMin + ((src * (dstMax - dstMin)) / srcMax);
    }

    protected Vertex readVertex(ImageInputStream input, long maxSrcCoord, long maxSrcColor, PDRange rangeX, PDRange rangeY, PDRange[] colRangeTab, Matrix matrix, AffineTransform xform) throws IOException {
        float[] colorComponentTab = new float[this.numberOfColorComponents];
        long x = input.readBits(this.bitsPerCoordinate);
        long y = input.readBits(this.bitsPerCoordinate);
        float dstX = interpolate(x, maxSrcCoord, rangeX.getMin(), rangeX.getMax());
        float dstY = interpolate(y, maxSrcCoord, rangeY.getMin(), rangeY.getMax());
        LOG.debug("coord: " + String.format("[%06X,%06X] -> [%f,%f]", Long.valueOf(x), Long.valueOf(y), Float.valueOf(dstX), Float.valueOf(dstY)));
        Point2D.Float floatTransformPoint = matrix.transformPoint(dstX, dstY);
        xform.transform(floatTransformPoint, floatTransformPoint);
        for (int n = 0; n < this.numberOfColorComponents; n++) {
            int color = (int) input.readBits(this.bitsPerColorComponent);
            colorComponentTab[n] = interpolate(color, maxSrcColor, colRangeTab[n].getMin(), colRangeTab[n].getMax());
            LOG.debug("color[" + n + "]: " + color + "/" + String.format("%02x", Integer.valueOf(color)) + "-> color[" + n + "]: " + colorComponentTab[n]);
        }
        int bitOffset = input.getBitOffset();
        if (bitOffset != 0) {
            input.readBits(8 - bitOffset);
        }
        return new Vertex(floatTransformPoint, colorComponentTab);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.PDShading
    public Rectangle2D getBounds(AffineTransform xform, Matrix matrix) throws IOException {
        Rectangle2D.Double r14 = null;
        for (ShadedTriangle shadedTriangle : collectTriangles(xform, matrix)) {
            if (r14 == null) {
                r14 = new Rectangle2D.Double(shadedTriangle.corner[0].getX(), shadedTriangle.corner[0].getY(), 0.0d, 0.0d);
            }
            r14.add(shadedTriangle.corner[0]);
            r14.add(shadedTriangle.corner[1]);
            r14.add(shadedTriangle.corner[2]);
        }
        if (r14 == null) {
            return new Rectangle2D.Float();
        }
        return r14;
    }
}
