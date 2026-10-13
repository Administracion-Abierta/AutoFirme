package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.geom.Point2D;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/TensorPatch.class */
class TensorPatch extends Patch {
    protected TensorPatch(Point2D[] tcp, float[][] color) {
        super(color);
        this.controlPoints = reshapeControlPoints(tcp);
        this.level = calcLevel();
        this.listOfTriangles = getTriangles();
    }

    private Point2D[][] reshapeControlPoints(Point2D[] tcp) {
        Point2D[][] square = new Point2D[4][4];
        for (int i = 0; i <= 3; i++) {
            square[0][i] = tcp[i];
            square[3][i] = tcp[9 - i];
        }
        for (int i2 = 1; i2 <= 2; i2++) {
            square[i2][0] = tcp[12 - i2];
            square[i2][2] = tcp[12 + i2];
            square[i2][3] = tcp[3 + i2];
        }
        square[1][1] = tcp[12];
        square[2][1] = tcp[15];
        return square;
    }

    private int[] calcLevel() {
        int[] l = {4, 4};
        Point2D[] ctlC1 = new Point2D[4];
        Point2D[] ctlC2 = new Point2D[4];
        for (int j = 0; j < 4; j++) {
            ctlC1[j] = this.controlPoints[j][0];
            ctlC2[j] = this.controlPoints[j][3];
        }
        if (isEdgeALine(ctlC1) && isEdgeALine(ctlC2) && !isOnSameSideCC(this.controlPoints[1][1]) && !isOnSameSideCC(this.controlPoints[1][2]) && !isOnSameSideCC(this.controlPoints[2][1]) && !isOnSameSideCC(this.controlPoints[2][2])) {
            double lc1 = getLen(ctlC1[0], ctlC1[3]);
            double lc2 = getLen(ctlC2[0], ctlC2[3]);
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
        if (isEdgeALine(this.controlPoints[0]) && isEdgeALine(this.controlPoints[3]) && !isOnSameSideDD(this.controlPoints[1][1]) && !isOnSameSideDD(this.controlPoints[1][2]) && !isOnSameSideDD(this.controlPoints[2][1]) && !isOnSameSideDD(this.controlPoints[2][2])) {
            double ld1 = getLen(this.controlPoints[0][0], this.controlPoints[0][3]);
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

    private boolean isOnSameSideCC(Point2D p) {
        double cc = edgeEquationValue(p, this.controlPoints[0][0], this.controlPoints[3][0]) * edgeEquationValue(p, this.controlPoints[0][3], this.controlPoints[3][3]);
        return cc > 0.0d;
    }

    private boolean isOnSameSideDD(Point2D p) {
        double dd = edgeEquationValue(p, this.controlPoints[0][0], this.controlPoints[0][3]) * edgeEquationValue(p, this.controlPoints[3][0], this.controlPoints[3][3]);
        return dd > 0.0d;
    }

    private List<ShadedTriangle> getTriangles() {
        CoordinateColorPair[][] patchCC = getPatchCoordinatesColor();
        return getShadedTriangles(patchCC);
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.Patch
    protected Point2D[] getFlag1Edge() {
        Point2D[] implicitEdge = new Point2D[4];
        for (int i = 0; i < 4; i++) {
            implicitEdge[i] = this.controlPoints[i][3];
        }
        return implicitEdge;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.Patch
    protected Point2D[] getFlag2Edge() {
        Point2D[] implicitEdge = new Point2D[4];
        for (int i = 0; i < 4; i++) {
            implicitEdge[i] = this.controlPoints[3][3 - i];
        }
        return implicitEdge;
    }

    @Override // org.apache.pdfbox.pdmodel.graphics.shading.Patch
    protected Point2D[] getFlag3Edge() {
        Point2D[] implicitEdge = new Point2D[4];
        for (int i = 0; i < 4; i++) {
            implicitEdge[i] = this.controlPoints[3 - i][0];
        }
        return implicitEdge;
    }

    private CoordinateColorPair[][] getPatchCoordinatesColor() {
        int numberOfColorComponents = this.cornerColor[0].length;
        double[][] bernsteinPolyU = getBernsteinPolynomials(this.level[0]);
        int szU = bernsteinPolyU[0].length;
        double[][] bernsteinPolyV = getBernsteinPolynomials(this.level[1]);
        int szV = bernsteinPolyV[0].length;
        CoordinateColorPair[][] patchCC = new CoordinateColorPair[szV][szU];
        double stepU = 1.0d / ((double) (szU - 1));
        double stepV = 1.0d / ((double) (szV - 1));
        double v = -stepV;
        for (int k = 0; k < szV; k++) {
            v += stepV;
            double u = -stepU;
            for (int l = 0; l < szU; l++) {
                double tmpx = 0.0d;
                double tmpy = 0.0d;
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        tmpx += this.controlPoints[i][j].getX() * bernsteinPolyU[i][l] * bernsteinPolyV[j][k];
                        tmpy += this.controlPoints[i][j].getY() * bernsteinPolyU[i][l] * bernsteinPolyV[j][k];
                    }
                }
                Point2D.Double r0 = new Point2D.Double(tmpx, tmpy);
                u += stepU;
                float[] paramSC = new float[numberOfColorComponents];
                for (int ci = 0; ci < numberOfColorComponents; ci++) {
                    paramSC[ci] = (float) (((1.0d - v) * (((1.0d - u) * ((double) this.cornerColor[0][ci])) + (u * ((double) this.cornerColor[3][ci])))) + (v * (((1.0d - u) * ((double) this.cornerColor[1][ci])) + (u * ((double) this.cornerColor[2][ci])))));
                }
                patchCC[k][l] = new CoordinateColorPair(r0, paramSC);
            }
        }
        return patchCC;
    }

    private double[][] getBernsteinPolynomials(int lvl) {
        int sz = (1 << lvl) + 1;
        double[][] poly = new double[4][sz];
        double step = 1.0d / ((double) (sz - 1));
        double t = -step;
        for (int i = 0; i < sz; i++) {
            t += step;
            poly[0][i] = (1.0d - t) * (1.0d - t) * (1.0d - t);
            poly[1][i] = 3.0d * t * (1.0d - t) * (1.0d - t);
            poly[2][i] = 3.0d * t * t * (1.0d - t);
            poly[3][i] = t * t * t;
        }
        return poly;
    }
}
