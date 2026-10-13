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
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.PDRange;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/PDShadingType5.class */
public class PDShadingType5 extends PDTriangleBasedShadingType {
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

    public PDShadingType5(COSDictionary shadingDictionary) {
        super(shadingDictionary);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.PDShading
    public int getShadingType() {
        return 5;
    }

    public int getVerticesPerRow() {
        return getCOSObject().getInt(COSName.VERTICES_PER_ROW, -1);
    }

    public void setVerticesPerRow(int verticesPerRow) {
        getCOSObject().setInt(COSName.VERTICES_PER_ROW, verticesPerRow);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.PDShading
    public Paint toPaint(Matrix matrix) {
        return new Type5ShadingPaint(this, matrix);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.PDTriangleBasedShadingType
    List<ShadedTriangle> collectTriangles(AffineTransform xform, Matrix matrix) throws IOException {
        COSDictionary dict = getCOSObject();
        if (!(dict instanceof COSStream)) {
            return Collections.emptyList();
        }
        PDRange rangeX = getDecodeForParameter(0);
        PDRange rangeY = getDecodeForParameter(1);
        if (Float.compare(rangeX.getMin(), rangeX.getMax()) == 0 || Float.compare(rangeY.getMin(), rangeY.getMax()) == 0) {
            return Collections.emptyList();
        }
        int numPerRow = getVerticesPerRow();
        PDRange[] colRange = new PDRange[getNumberOfColorComponents()];
        for (int i = 0; i < colRange.length; i++) {
            colRange[i] = getDecodeForParameter(2 + i);
        }
        List<Vertex> vlist = new ArrayList<>();
        long maxSrcCoord = ((long) Math.pow(2.0d, getBitsPerCoordinate())) - 1;
        long maxSrcColor = ((long) Math.pow(2.0d, getBitsPerComponent())) - 1;
        COSStream cosStream = (COSStream) dict;
        MemoryCacheImageInputStream memoryCacheImageInputStream = new MemoryCacheImageInputStream(cosStream.createInputStream());
        boolean eof = false;
        while (!eof) {
            try {
                Vertex p = readVertex(memoryCacheImageInputStream, maxSrcCoord, maxSrcColor, rangeX, rangeY, colRange, matrix, xform);
                vlist.add(p);
            } catch (EOFException e) {
                eof = true;
            } catch (Throwable th) {
                memoryCacheImageInputStream.close();
                throw th;
            }
        }
        memoryCacheImageInputStream.close();
        int rowNum = vlist.size() / numPerRow;
        Vertex[][] latticeArray = new Vertex[rowNum][numPerRow];
        List<ShadedTriangle> list = new ArrayList<>();
        if (rowNum < 2) {
            return list;
        }
        for (int i2 = 0; i2 < rowNum; i2++) {
            for (int j = 0; j < numPerRow; j++) {
                latticeArray[i2][j] = vlist.get((i2 * numPerRow) + j);
            }
        }
        Point2D[] ps = new Point2D[3];
        float[][] cs = new float[3][];
        for (int i3 = 0; i3 < rowNum - 1; i3++) {
            for (int j2 = 0; j2 < numPerRow - 1; j2++) {
                ps[0] = latticeArray[i3][j2].point;
                ps[1] = latticeArray[i3][j2 + 1].point;
                ps[2] = latticeArray[i3 + 1][j2].point;
                cs[0] = latticeArray[i3][j2].color;
                cs[1] = latticeArray[i3][j2 + 1].color;
                cs[2] = latticeArray[i3 + 1][j2].color;
                list.add(new ShadedTriangle(ps, cs));
                ps[0] = latticeArray[i3][j2 + 1].point;
                ps[1] = latticeArray[i3 + 1][j2].point;
                ps[2] = latticeArray[i3 + 1][j2 + 1].point;
                cs[0] = latticeArray[i3][j2 + 1].color;
                cs[1] = latticeArray[i3 + 1][j2].color;
                cs[2] = latticeArray[i3 + 1][j2 + 1].color;
                list.add(new ShadedTriangle(ps, cs));
            }
        }
        return list;
    }
}
