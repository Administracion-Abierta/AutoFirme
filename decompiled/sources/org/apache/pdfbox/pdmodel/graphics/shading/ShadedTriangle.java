package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/ShadedTriangle.class */
class ShadedTriangle {
    protected final Point2D[] corner;
    protected final float[][] color;
    private final double area;
    private final int degree;
    private final Line line;
    private final double v0;
    private final double v1;
    private final double v2;

    ShadedTriangle(Point2D[] p, float[][] c) {
        this.corner = (Point2D[]) p.clone();
        this.color = (float[][]) c.clone();
        this.area = getArea(p[0], p[1], p[2]);
        this.degree = calcDeg(p);
        if (this.degree == 2) {
            if (overlaps(this.corner[1], this.corner[2]) && !overlaps(this.corner[0], this.corner[2])) {
                Point p0 = new Point((int) Math.round(this.corner[0].getX()), (int) Math.round(this.corner[0].getY()));
                Point p1 = new Point((int) Math.round(this.corner[2].getX()), (int) Math.round(this.corner[2].getY()));
                this.line = new Line(p0, p1, this.color[0], this.color[2]);
            } else {
                Point p2 = new Point((int) Math.round(this.corner[1].getX()), (int) Math.round(this.corner[1].getY()));
                Point p3 = new Point((int) Math.round(this.corner[2].getX()), (int) Math.round(this.corner[2].getY()));
                this.line = new Line(p2, p3, this.color[1], this.color[2]);
            }
        } else {
            this.line = null;
        }
        this.v0 = edgeEquationValue(p[0], p[1], p[2]);
        this.v1 = edgeEquationValue(p[1], p[2], p[0]);
        this.v2 = edgeEquationValue(p[2], p[0], p[1]);
    }

    private int calcDeg(Point2D[] p) {
        Set<Point> set = new HashSet<>();
        for (Point2D itp : p) {
            Point np = new Point((int) Math.round(itp.getX() * 1000.0d), (int) Math.round(itp.getY() * 1000.0d));
            set.add(np);
        }
        return set.size();
    }

    public int getDeg() {
        return this.degree;
    }

    public int[] getBoundary() {
        int x0 = (int) Math.round(this.corner[0].getX());
        int x1 = (int) Math.round(this.corner[1].getX());
        int x2 = (int) Math.round(this.corner[2].getX());
        int y0 = (int) Math.round(this.corner[0].getY());
        int y1 = (int) Math.round(this.corner[1].getY());
        int y2 = (int) Math.round(this.corner[2].getY());
        int[] boundary = {Math.min(Math.min(x0, x1), x2), Math.max(Math.max(x0, x1), x2), Math.min(Math.min(y0, y1), y2), Math.max(Math.max(y0, y1), y2)};
        return boundary;
    }

    public Line getLine() {
        return this.line;
    }

    public boolean contains(Point2D p) {
        if (this.degree == 1) {
            return overlaps(this.corner[0], p) || overlaps(this.corner[1], p) || overlaps(this.corner[2], p);
        }
        if (this.degree == 2) {
            Point tp = new Point((int) Math.round(p.getX()), (int) Math.round(p.getY()));
            return this.line.linePoints.contains(tp);
        }
        double pv0 = edgeEquationValue(p, this.corner[1], this.corner[2]);
        if (pv0 * this.v0 < 0.0d) {
            return false;
        }
        double pv1 = edgeEquationValue(p, this.corner[2], this.corner[0]);
        if (pv1 * this.v1 < 0.0d) {
            return false;
        }
        double pv2 = edgeEquationValue(p, this.corner[0], this.corner[1]);
        return pv2 * this.v2 >= 0.0d;
    }

    private boolean overlaps(Point2D p0, Point2D p1) {
        return Math.abs(p0.getX() - p1.getX()) < 0.001d && Math.abs(p0.getY() - p1.getY()) < 0.001d;
    }

    private double edgeEquationValue(Point2D p, Point2D p1, Point2D p2) {
        return ((p2.getY() - p1.getY()) * (p.getX() - p1.getX())) - ((p2.getX() - p1.getX()) * (p.getY() - p1.getY()));
    }

    private double getArea(Point2D a, Point2D b, Point2D c) {
        return Math.abs(((c.getX() - b.getX()) * (c.getY() - a.getY())) - ((c.getX() - a.getX()) * (c.getY() - b.getY()))) / 2.0d;
    }

    public float[] calcColor(Point2D p) {
        int numberOfColorComponents = this.color[0].length;
        float[] pCol = new float[numberOfColorComponents];
        switch (this.degree) {
            case 1:
                for (int i = 0; i < numberOfColorComponents; i++) {
                    pCol[i] = ((this.color[0][i] + this.color[1][i]) + this.color[2][i]) / 3.0f;
                }
                break;
            case 2:
                Point tp = new Point((int) Math.round(p.getX()), (int) Math.round(p.getY()));
                return this.line.calcColor(tp);
            default:
                float aw = (float) (getArea(p, this.corner[1], this.corner[2]) / this.area);
                float bw = (float) (getArea(p, this.corner[2], this.corner[0]) / this.area);
                float cw = (float) (getArea(p, this.corner[0], this.corner[1]) / this.area);
                for (int i2 = 0; i2 < numberOfColorComponents; i2++) {
                    pCol[i2] = (this.color[0][i2] * aw) + (this.color[1][i2] * bw) + (this.color[2][i2] * cw);
                }
                break;
        }
        return pCol;
    }

    public String toString() {
        return this.corner[0] + " " + this.corner[1] + " " + this.corner[2];
    }
}
