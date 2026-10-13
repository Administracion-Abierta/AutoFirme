package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/Patch.class */
abstract class Patch {
    protected Point2D[][] controlPoints;
    protected float[][] cornerColor;
    protected int[] level;
    protected List<ShadedTriangle> listOfTriangles;

    protected abstract Point2D[] getFlag1Edge();

    protected abstract Point2D[] getFlag2Edge();

    protected abstract Point2D[] getFlag3Edge();

    Patch(float[][] color) {
        this.cornerColor = (float[][]) color.clone();
    }

    protected float[][] getFlag1Color() {
        int numberOfColorComponents = this.cornerColor[0].length;
        float[][] implicitCornerColor = new float[2][numberOfColorComponents];
        for (int i = 0; i < numberOfColorComponents; i++) {
            implicitCornerColor[0][i] = this.cornerColor[1][i];
            implicitCornerColor[1][i] = this.cornerColor[2][i];
        }
        return implicitCornerColor;
    }

    protected float[][] getFlag2Color() {
        int numberOfColorComponents = this.cornerColor[0].length;
        float[][] implicitCornerColor = new float[2][numberOfColorComponents];
        for (int i = 0; i < numberOfColorComponents; i++) {
            implicitCornerColor[0][i] = this.cornerColor[2][i];
            implicitCornerColor[1][i] = this.cornerColor[3][i];
        }
        return implicitCornerColor;
    }

    protected float[][] getFlag3Color() {
        int numberOfColorComponents = this.cornerColor[0].length;
        float[][] implicitCornerColor = new float[2][numberOfColorComponents];
        for (int i = 0; i < numberOfColorComponents; i++) {
            implicitCornerColor[0][i] = this.cornerColor[3][i];
            implicitCornerColor[1][i] = this.cornerColor[0][i];
        }
        return implicitCornerColor;
    }

    protected double getLen(Point2D ps, Point2D pe) {
        double x = pe.getX() - ps.getX();
        double y = pe.getY() - ps.getY();
        return Math.sqrt((x * x) + (y * y));
    }

    protected boolean isEdgeALine(Point2D[] ctl) {
        double ctl1 = Math.abs(edgeEquationValue(ctl[1], ctl[0], ctl[3]));
        double ctl2 = Math.abs(edgeEquationValue(ctl[2], ctl[0], ctl[3]));
        double x = Math.abs(ctl[0].getX() - ctl[3].getX());
        double y = Math.abs(ctl[0].getY() - ctl[3].getY());
        return (ctl1 <= x && ctl2 <= x) || (ctl1 <= y && ctl2 <= y);
    }

    protected double edgeEquationValue(Point2D p, Point2D p1, Point2D p2) {
        return ((p2.getY() - p1.getY()) * (p.getX() - p1.getX())) - ((p2.getX() - p1.getX()) * (p.getY() - p1.getY()));
    }

    protected List<ShadedTriangle> getShadedTriangles(CoordinateColorPair[][] patchCC) {
        List<ShadedTriangle> list = new ArrayList<>();
        int szV = patchCC.length;
        int szU = patchCC[0].length;
        for (int i = 1; i < szV; i++) {
            for (int j = 1; j < szU; j++) {
                Point2D p0 = patchCC[i - 1][j - 1].coordinate;
                Point2D p1 = patchCC[i - 1][j].coordinate;
                Point2D p2 = patchCC[i][j].coordinate;
                Point2D p3 = patchCC[i][j - 1].coordinate;
                boolean ll = true;
                if (overlaps(p0, p1) || overlaps(p0, p3)) {
                    ll = false;
                } else {
                    Point2D[] llCorner = {p0, p1, p3};
                    float[][] llColor = {patchCC[i - 1][j - 1].color, patchCC[i - 1][j].color, patchCC[i][j - 1].color};
                    ShadedTriangle tmpll = new ShadedTriangle(llCorner, llColor);
                    list.add(tmpll);
                }
                if (!ll || (!overlaps(p2, p1) && !overlaps(p2, p3))) {
                    Point2D[] urCorner = {p3, p1, p2};
                    float[][] urColor = {patchCC[i][j - 1].color, patchCC[i - 1][j].color, patchCC[i][j].color};
                    ShadedTriangle tmpur = new ShadedTriangle(urCorner, urColor);
                    list.add(tmpur);
                }
            }
        }
        return list;
    }

    private boolean overlaps(Point2D p0, Point2D p1) {
        return Math.abs(p0.getX() - p1.getX()) < 0.001d && Math.abs(p0.getY() - p1.getY()) < 0.001d;
    }
}
