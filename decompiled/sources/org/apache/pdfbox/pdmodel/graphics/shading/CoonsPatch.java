package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.geom.Point2D;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/CoonsPatch.class */
class CoonsPatch extends Patch {
    protected CoonsPatch(Point2D[] points, float[][] color) {
        super(color);
        this.controlPoints = reshapeControlPoints(points);
        this.level = calcLevel();
        this.listOfTriangles = getTriangles();
    }

    private Point2D[][] reshapeControlPoints(Point2D[] points) {
        Point2D[] point2DArr = {points[0], points[1], points[2], points[3]};
        Point2D[][] fourRows = {new Point2D[]{points[0], points[11], points[10], points[9]}, new Point2D[]{points[3], points[4], points[5], points[6]}, point2DArr, new Point2D[]{points[9], points[8], points[7], points[6]}};
        return fourRows;
    }

    private int[] calcLevel() {
        int[] l = {4, 4};
        if (isEdgeALine(this.controlPoints[0]) && isEdgeALine(this.controlPoints[1])) {
            double lc1 = getLen(this.controlPoints[0][0], this.controlPoints[0][3]);
            double lc2 = getLen(this.controlPoints[1][0], this.controlPoints[1][3]);
            if (lc1 <= 800.0d && lc2 <= 800.0d) {
                if (lc1 > 400.0d || lc2 > 400.0d) {
                    l[0] = 3;
                } else if (lc1 > 200.0d || lc2 > 200.0d) {
                    l[0] = 2;
                } else {
                    l[0] = 1;
                }
            }
        }
        if (isEdgeALine(this.controlPoints[2]) && isEdgeALine(this.controlPoints[3])) {
            double ld1 = getLen(this.controlPoints[2][0], this.controlPoints[2][3]);
            double ld2 = getLen(this.controlPoints[3][0], this.controlPoints[3][3]);
            if (ld1 <= 800.0d && ld2 <= 800.0d) {
                if (ld1 > 400.0d || ld2 > 400.0d) {
                    l[1] = 3;
                } else if (ld1 > 200.0d || ld2 > 200.0d) {
                    l[1] = 2;
                } else {
                    l[1] = 1;
                }
            }
        }
        return l;
    }

    private List<ShadedTriangle> getTriangles() {
        CubicBezierCurve eC1 = new CubicBezierCurve(this.controlPoints[0], this.level[0]);
        CubicBezierCurve eC2 = new CubicBezierCurve(this.controlPoints[1], this.level[0]);
        CubicBezierCurve eD1 = new CubicBezierCurve(this.controlPoints[2], this.level[1]);
        CubicBezierCurve eD2 = new CubicBezierCurve(this.controlPoints[3], this.level[1]);
        CoordinateColorPair[][] patchCC = getPatchCoordinatesColor(eC1, eC2, eD1, eD2);
        return getShadedTriangles(patchCC);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.Patch
    protected Point2D[] getFlag1Edge() {
        return (Point2D[]) this.controlPoints[1].clone();
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.Patch
    protected Point2D[] getFlag2Edge() {
        Point2D[] implicitEdge = {this.controlPoints[3][3], this.controlPoints[3][2], this.controlPoints[3][1], this.controlPoints[3][0]};
        return implicitEdge;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.Patch
    protected Point2D[] getFlag3Edge() {
        Point2D[] implicitEdge = {this.controlPoints[0][3], this.controlPoints[0][2], this.controlPoints[0][1], this.controlPoints[0][0]};
        return implicitEdge;
    }

    private CoordinateColorPair[][] getPatchCoordinatesColor(CubicBezierCurve c1, CubicBezierCurve c2, CubicBezierCurve d1, CubicBezierCurve d2) {
        Point2D[] curveC1 = c1.getCubicBezierCurve();
        Point2D[] curveC2 = c2.getCubicBezierCurve();
        Point2D[] curveD1 = d1.getCubicBezierCurve();
        Point2D[] curveD2 = d2.getCubicBezierCurve();
        int numberOfColorComponents = this.cornerColor[0].length;
        int szV = curveD1.length;
        int szU = curveC1.length;
        CoordinateColorPair[][] patchCC = new CoordinateColorPair[szV][szU];
        double stepV = 1.0d / ((double) (szV - 1));
        double stepU = 1.0d / ((double) (szU - 1));
        double v = -stepV;
        for (int i = 0; i < szV; i++) {
            v += stepV;
            double u = -stepU;
            for (int j = 0; j < szU; j++) {
                u += stepU;
                double scx = ((1.0d - v) * curveC1[j].getX()) + (v * curveC2[j].getX());
                double scy = ((1.0d - v) * curveC1[j].getY()) + (v * curveC2[j].getY());
                double sdx = ((1.0d - u) * curveD1[i].getX()) + (u * curveD2[i].getX());
                double sdy = ((1.0d - u) * curveD1[i].getY()) + (u * curveD2[i].getY());
                double sbx = ((1.0d - v) * (((1.0d - u) * this.controlPoints[0][0].getX()) + (u * this.controlPoints[0][3].getX()))) + (v * (((1.0d - u) * this.controlPoints[1][0].getX()) + (u * this.controlPoints[1][3].getX())));
                double sby = ((1.0d - v) * (((1.0d - u) * this.controlPoints[0][0].getY()) + (u * this.controlPoints[0][3].getY()))) + (v * (((1.0d - u) * this.controlPoints[1][0].getY()) + (u * this.controlPoints[1][3].getY())));
                double sx = (scx + sdx) - sbx;
                double sy = (scy + sdy) - sby;
                Point2D.Double r0 = new Point2D.Double(sx, sy);
                float[] paramSC = new float[numberOfColorComponents];
                for (int ci = 0; ci < numberOfColorComponents; ci++) {
                    paramSC[ci] = (float) (((1.0d - v) * (((1.0d - u) * ((double) this.cornerColor[0][ci])) + (u * ((double) this.cornerColor[3][ci])))) + (v * (((1.0d - u) * ((double) this.cornerColor[1][ci])) + (u * ((double) this.cornerColor[2][ci])))));
                }
                patchCC[i][j] = new CoordinateColorPair(r0, paramSC);
            }
        }
        return patchCC;
    }
}
