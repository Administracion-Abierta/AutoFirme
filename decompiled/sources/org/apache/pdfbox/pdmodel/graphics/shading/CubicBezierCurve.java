package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.geom.Point2D;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/CubicBezierCurve.class */
class CubicBezierCurve {
    private final Point2D[] controlPoints;
    private final int level;
    private final Point2D[] curve;

    CubicBezierCurve(Point2D[] ctrlPnts, int l) {
        this.controlPoints = (Point2D[]) ctrlPnts.clone();
        this.level = l;
        this.curve = getPoints(this.level);
    }

    int getLevel() {
        return this.level;
    }

    private Point2D[] getPoints(int l) {
        if (l < 0) {
            l = 0;
        }
        int sz = (1 << l) + 1;
        Point2D[] res = new Point2D[sz];
        double step = 1.0d / ((double) (sz - 1));
        double t = -step;
        for (int i = 0; i < sz; i++) {
            t += step;
            double tmpX = ((1.0d - t) * (1.0d - t) * (1.0d - t) * this.controlPoints[0].getX()) + (3.0d * t * (1.0d - t) * (1.0d - t) * this.controlPoints[1].getX()) + (3.0d * t * t * (1.0d - t) * this.controlPoints[2].getX()) + (t * t * t * this.controlPoints[3].getX());
            double tmpY = ((1.0d - t) * (1.0d - t) * (1.0d - t) * this.controlPoints[0].getY()) + (3.0d * t * (1.0d - t) * (1.0d - t) * this.controlPoints[1].getY()) + (3.0d * t * t * (1.0d - t) * this.controlPoints[2].getY()) + (t * t * t * this.controlPoints[3].getY());
            res[i] = new Point2D.Double(tmpX, tmpY);
        }
        return res;
    }

    Point2D[] getCubicBezierCurve() {
        return this.curve;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Point2D p : this.controlPoints) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(p);
        }
        return "Cubic Bezier curve{control points p0, p1, p2, p3: " + ((Object) sb) + "}";
    }
}
