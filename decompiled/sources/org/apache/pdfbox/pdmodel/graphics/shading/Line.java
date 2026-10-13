package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/Line.class */
class Line {
    private final Point point0;
    private final Point point1;
    private final float[] color0;
    private final float[] color1;
    protected final Set<Point> linePoints;

    Line(Point p0, Point p1, float[] c0, float[] c1) {
        this.point0 = p0;
        this.point1 = p1;
        this.color0 = (float[]) c0.clone();
        this.color1 = (float[]) c1.clone();
        this.linePoints = calcLine(this.point0.x, this.point0.y, this.point1.x, this.point1.y);
    }

    private Set<Point> calcLine(int x0, int y0, int x1, int y1) {
        Set<Point> points = new HashSet<>(3);
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        while (true) {
            points.add(new IntPoint(x0, y0));
            if (x0 != x1 || y0 != y1) {
                int e2 = 2 * err;
                if (e2 > (-dy)) {
                    err -= dy;
                    x0 += sx;
                }
                if (e2 < dx) {
                    err += dx;
                    y0 += sy;
                }
            } else {
                return points;
            }
        }
    }

    protected float[] calcColor(Point p) {
        if (this.point0.x == this.point1.x && this.point0.y == this.point1.y) {
            return this.color0;
        }
        int numberOfColorComponents = this.color0.length;
        float[] pc = new float[numberOfColorComponents];
        if (this.point0.x == this.point1.x) {
            float l = this.point1.y - this.point0.y;
            for (int i = 0; i < numberOfColorComponents; i++) {
                pc[i] = ((this.color0[i] * (this.point1.y - p.y)) / l) + ((this.color1[i] * (p.y - this.point0.y)) / l);
            }
        } else {
            float l2 = this.point1.x - this.point0.x;
            for (int i2 = 0; i2 < numberOfColorComponents; i2++) {
                pc[i2] = ((this.color0[i2] * (this.point1.x - p.x)) / l2) + ((this.color1[i2] * (p.x - this.point0.x)) / l2);
            }
        }
        return pc;
    }
}
