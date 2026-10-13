package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.PaintContext;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/TriangleBasedShadingContext.class */
abstract class TriangleBasedShadingContext extends ShadingContext implements PaintContext {
    private Map<Point, Integer> pixelTable;

    abstract Map<Point, Integer> calcPixelTable(Rectangle rectangle) throws IOException;

    abstract boolean isDataEmpty();

    TriangleBasedShadingContext(PDShading shading, ColorModel cm, AffineTransform xform, Matrix matrix) throws IOException {
        super(shading, cm, xform, matrix);
    }

    protected final void createPixelTable(Rectangle deviceBounds) throws IOException {
        this.pixelTable = calcPixelTable(deviceBounds);
    }

    protected void calcPixelTable(List<ShadedTriangle> triangleList, Map<Point, Integer> map, Rectangle deviceBounds) throws IOException {
        for (ShadedTriangle tri : triangleList) {
            int degree = tri.getDeg();
            if (degree == 2) {
                Line line = tri.getLine();
                for (Point p : line.linePoints) {
                    map.put(p, Integer.valueOf(evalFunctionAndConvertToRGB(line.calcColor(p))));
                }
            } else {
                int[] boundary = tri.getBoundary();
                boundary[0] = Math.max(boundary[0], deviceBounds.x);
                boundary[1] = Math.min(boundary[1], deviceBounds.x + deviceBounds.width);
                boundary[2] = Math.max(boundary[2], deviceBounds.y);
                boundary[3] = Math.min(boundary[3], deviceBounds.y + deviceBounds.height);
                for (int x = boundary[0]; x <= boundary[1]; x++) {
                    for (int y = boundary[2]; y <= boundary[3]; y++) {
                        Point p2 = new IntPoint(x, y);
                        if (tri.contains(p2)) {
                            map.put(p2, Integer.valueOf(evalFunctionAndConvertToRGB(tri.calcColor(p2))));
                        }
                    }
                }
                Point p0 = new IntPoint((int) Math.round(tri.corner[0].getX()), (int) Math.round(tri.corner[0].getY()));
                Point p1 = new IntPoint((int) Math.round(tri.corner[1].getX()), (int) Math.round(tri.corner[1].getY()));
                Point p3 = new IntPoint((int) Math.round(tri.corner[2].getX()), (int) Math.round(tri.corner[2].getY()));
                Line l1 = new Line(p0, p1, tri.color[0], tri.color[1]);
                Line l2 = new Line(p1, p3, tri.color[1], tri.color[2]);
                Line l3 = new Line(p3, p0, tri.color[2], tri.color[0]);
                for (Point p4 : l1.linePoints) {
                    map.put(p4, Integer.valueOf(evalFunctionAndConvertToRGB(l1.calcColor(p4))));
                }
                for (Point p5 : l2.linePoints) {
                    map.put(p5, Integer.valueOf(evalFunctionAndConvertToRGB(l2.calcColor(p5))));
                }
                for (Point p6 : l3.linePoints) {
                    map.put(p6, Integer.valueOf(evalFunctionAndConvertToRGB(l3.calcColor(p6))));
                }
            }
        }
    }

    private int evalFunctionAndConvertToRGB(float[] values) throws IOException {
        if (getShading().getFunction() != null) {
            values = getShading().evalFunction(values);
        }
        return convertToRGB(values);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.ShadingContext
    public final ColorModel getColorModel() {
        return super.getColorModel();
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.ShadingContext
    public void dispose() {
        super.dispose();
    }

    public final Raster getRaster(int x, int y, int w, int h) {
        int value;
        WritableRaster raster = getColorModel().createCompatibleWritableRaster(w, h);
        int[] data = new int[w * h * 4];
        if (!isDataEmpty() || getBackground() != null) {
            for (int row = 0; row < h; row++) {
                for (int col = 0; col < w; col++) {
                    Point p = new IntPoint(x + col, y + row);
                    Integer v = this.pixelTable.get(p);
                    if (v != null) {
                        value = v.intValue();
                    } else {
                        if (getBackground() != null) {
                            value = getRgbBackground();
                        }
                    }
                    int index = ((row * w) + col) * 4;
                    data[index] = value & 255;
                    int value2 = value >> 8;
                    data[index + 1] = value2 & 255;
                    data[index + 2] = (value2 >> 8) & 255;
                    data[index + 3] = 255;
                }
            }
        }
        raster.setPixels(0, 0, w, h, data);
        return raster;
    }
}
