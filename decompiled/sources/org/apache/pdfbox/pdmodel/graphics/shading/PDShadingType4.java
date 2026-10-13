package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.PDRange;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/PDShadingType4.class */
public class PDShadingType4 extends PDTriangleBasedShadingType {
    private static final Log LOG = LogFactory.getLog(PDShadingType4.class);

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.PDTriangleBasedShadingType, org.apache.pdfbox.pdmodel.graphics.shading.PDShading
    public /* bridge */ /* synthetic */ Rectangle2D getBounds(AffineTransform affineTransform, Matrix matrix) throws IOException {
        return super.getBounds(affineTransform, matrix);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.PDTriangleBasedShadingType
    public /* bridge */ /* synthetic */ PDRange getDecodeForParameter(int i) {
        return super.getDecodeForParameter(i);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.PDTriangleBasedShadingType
    public /* bridge */ /* synthetic */ void setDecodeValues(COSArray cOSArray) {
        super.setDecodeValues(cOSArray);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.PDTriangleBasedShadingType
    public /* bridge */ /* synthetic */ int getNumberOfColorComponents() throws IOException {
        return super.getNumberOfColorComponents();
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.PDTriangleBasedShadingType
    public /* bridge */ /* synthetic */ void setBitsPerCoordinate(int i) {
        super.setBitsPerCoordinate(i);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.PDTriangleBasedShadingType
    public /* bridge */ /* synthetic */ int getBitsPerCoordinate() {
        return super.getBitsPerCoordinate();
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.PDTriangleBasedShadingType
    public /* bridge */ /* synthetic */ void setBitsPerComponent(int i) {
        super.setBitsPerComponent(i);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.PDTriangleBasedShadingType
    public /* bridge */ /* synthetic */ int getBitsPerComponent() {
        return super.getBitsPerComponent();
    }

    public PDShadingType4(COSDictionary shadingDictionary) {
        super(shadingDictionary);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.PDShading
    public int getShadingType() {
        return 4;
    }

    public int getBitsPerFlag() {
        return getCOSObject().getInt(COSName.BITS_PER_FLAG, -1);
    }

    public void setBitsPerFlag(int bitsPerFlag) {
        getCOSObject().setInt(COSName.BITS_PER_FLAG, bitsPerFlag);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.PDShading
    public Paint toPaint(Matrix matrix) {
        return new Type4ShadingPaint(this, matrix);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.PDTriangleBasedShadingType
    List<ShadedTriangle> collectTriangles(AffineTransform xform, Matrix matrix) throws IOException {
        int bitsPerFlag = getBitsPerFlag();
        COSDictionary dict = getCOSObject();
        if (!(dict instanceof COSStream)) {
            return Collections.emptyList();
        }
        PDRange rangeX = getDecodeForParameter(0);
        PDRange rangeY = getDecodeForParameter(1);
        if (Float.compare(rangeX.getMin(), rangeX.getMax()) == 0 || Float.compare(rangeY.getMin(), rangeY.getMax()) == 0) {
            return Collections.emptyList();
        }
        PDRange[] colRange = new PDRange[getNumberOfColorComponents()];
        for (int i = 0; i < colRange.length; i++) {
            colRange[i] = getDecodeForParameter(2 + i);
        }
        List<ShadedTriangle> list = new ArrayList<>();
        long maxSrcCoord = ((long) Math.pow(2.0d, getBitsPerCoordinate())) - 1;
        long maxSrcColor = ((long) Math.pow(2.0d, getBitsPerComponent())) - 1;
        COSStream stream = (COSStream) dict;
        MemoryCacheImageInputStream memoryCacheImageInputStream = new MemoryCacheImageInputStream(stream.createInputStream());
        byte flag = 0;
        try {
            try {
                flag = (byte) (memoryCacheImageInputStream.readBits(bitsPerFlag) & 3);
            } catch (EOFException ex) {
                LOG.error(ex);
            }
            boolean eof = false;
            while (!eof) {
                try {
                    switch (flag) {
                        case 0:
                            Vertex p0 = readVertex(memoryCacheImageInputStream, maxSrcCoord, maxSrcColor, rangeX, rangeY, colRange, matrix, xform);
                            byte flag2 = (byte) (memoryCacheImageInputStream.readBits(bitsPerFlag) & 3);
                            if (flag2 != 0) {
                                LOG.error("bad triangle: " + ((int) flag2));
                            }
                            Vertex p1 = readVertex(memoryCacheImageInputStream, maxSrcCoord, maxSrcColor, rangeX, rangeY, colRange, matrix, xform);
                            memoryCacheImageInputStream.readBits(bitsPerFlag);
                            if (flag2 != 0) {
                                LOG.error("bad triangle: " + ((int) flag2));
                            }
                            Vertex p2 = readVertex(memoryCacheImageInputStream, maxSrcCoord, maxSrcColor, rangeX, rangeY, colRange, matrix, xform);
                            list.add(new ShadedTriangle(new Point2D[]{p0.point, p1.point, p2.point}, new float[][]{p0.color, p1.color, p2.color}));
                            flag = (byte) (memoryCacheImageInputStream.readBits(bitsPerFlag) & 3);
                            break;
                        case 1:
                        case 2:
                            int lastIndex = list.size() - 1;
                            if (lastIndex < 0) {
                                LOG.error("broken data stream: " + list.size());
                            } else {
                                ShadedTriangle preTri = list.get(lastIndex);
                                Vertex p3 = readVertex(memoryCacheImageInputStream, maxSrcCoord, maxSrcColor, rangeX, rangeY, colRange, matrix, xform);
                                Point2D[] ps = new Point2D[3];
                                ps[0] = flag == 1 ? preTri.corner[1] : preTri.corner[0];
                                ps[1] = preTri.corner[2];
                                ps[2] = p3.point;
                                float[][] cs = new float[3][];
                                cs[0] = flag == 1 ? preTri.color[1] : preTri.color[0];
                                cs[1] = preTri.color[2];
                                cs[2] = p3.color;
                                list.add(new ShadedTriangle(ps, cs));
                                flag = (byte) (memoryCacheImageInputStream.readBits(bitsPerFlag) & 3);
                            }
                            break;
                        default:
                            LOG.warn("bad flag: " + ((int) flag));
                            break;
                    }
                } catch (EOFException e) {
                    eof = true;
                }
            }
            return list;
        } finally {
            memoryCacheImageInputStream.close();
        }
    }
}
