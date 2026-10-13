package com.aowagie.text.pdf.internal;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/internal/PolylineShape.class */
public class PolylineShape implements Shape {
    protected int[] x;
    protected int[] y;
    protected int np;

    public PolylineShape(int[] x, int[] y, int nPoints) {
        this.np = nPoints;
        this.x = new int[this.np];
        this.y = new int[this.np];
        System.arraycopy(x, 0, this.x, 0, this.np);
        System.arraycopy(y, 0, this.y, 0, this.np);
    }

    public Rectangle2D getBounds2D() {
        int[] r = rect();
        if (r == null) {
            return null;
        }
        return new Rectangle2D.Double(r[0], r[1], r[2], r[3]);
    }

    public Rectangle getBounds() {
        return getBounds2D().getBounds();
    }

    private int[] rect() {
        if (this.np == 0) {
            return null;
        }
        int xMin = this.x[0];
        int yMin = this.y[0];
        int xMax = this.x[0];
        int yMax = this.y[0];
        for (int i = 1; i < this.np; i++) {
            if (this.x[i] < xMin) {
                xMin = this.x[i];
            } else if (this.x[i] > xMax) {
                xMax = this.x[i];
            }
            if (this.y[i] < yMin) {
                yMin = this.y[i];
            } else if (this.y[i] > yMax) {
                yMax = this.y[i];
            }
        }
        return new int[]{xMin, yMin, xMax - xMin, yMax - yMin};
    }

    public boolean contains(double x, double y) {
        return false;
    }

    public boolean contains(Point2D p) {
        return false;
    }

    public boolean contains(double x, double y, double w, double h) {
        return false;
    }

    public boolean contains(Rectangle2D r) {
        return false;
    }

    public boolean intersects(double x, double y, double w, double h) {
        return intersects(new Rectangle2D.Double(x, y, w, h));
    }

    public boolean intersects(Rectangle2D r) {
        if (this.np == 0) {
            return false;
        }
        Line2D.Double r0 = new Line2D.Double(this.x[0], this.y[0], this.x[0], this.y[0]);
        for (int i = 1; i < this.np; i++) {
            r0.setLine(this.x[i - 1], this.y[i - 1], this.x[i], this.y[i]);
            if (r0.intersects(r)) {
                return true;
            }
        }
        return false;
    }

    public PathIterator getPathIterator(AffineTransform at) {
        return new PolylineShapeIterator(this, at);
    }

    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return new PolylineShapeIterator(this, at);
    }
}
