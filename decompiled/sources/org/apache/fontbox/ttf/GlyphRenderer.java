package org.apache.fontbox.ttf;

import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphRenderer.class */
class GlyphRenderer {
    private static final Log LOG = LogFactory.getLog(GlyphRenderer.class);
    private GlyphDescription glyphDescription;

    GlyphRenderer(GlyphDescription glyphDescription) {
        this.glyphDescription = glyphDescription;
    }

    public GeneralPath getPath() {
        Point[] points = describe(this.glyphDescription);
        return calculatePath(points);
    }

    private Point[] describe(GlyphDescription gd) {
        int endPtIndex = 0;
        int endPtOfContourIndex = -1;
        Point[] points = new Point[gd.getPointCount()];
        int i = 0;
        while (i < points.length) {
            if (endPtOfContourIndex == -1) {
                endPtOfContourIndex = gd.getEndPtOfContours(endPtIndex);
            }
            boolean endPt = endPtOfContourIndex == i;
            if (endPt) {
                endPtIndex++;
                endPtOfContourIndex = -1;
            }
            points[i] = new Point(gd.getXCoordinate(i), gd.getYCoordinate(i), (gd.getFlags(i) & 1) != 0, endPt);
            i++;
        }
        return points;
    }

    private GeneralPath calculatePath(Point[] points) {
        GeneralPath path = new GeneralPath();
        int start = 0;
        int len = points.length;
        for (int p = 0; p < len; p++) {
            if (points[p].endOfContour) {
                Point firstPoint = points[start];
                Point lastPoint = points[p];
                List<Point> contour = new ArrayList<>();
                for (int q = start; q <= p; q++) {
                    contour.add(points[q]);
                }
                if (points[start].onCurve) {
                    contour.add(firstPoint);
                } else if (points[p].onCurve) {
                    contour.add(0, lastPoint);
                } else {
                    Point pmid = midValue(firstPoint, lastPoint);
                    contour.add(0, pmid);
                    contour.add(pmid);
                }
                moveTo(path, contour.get(0));
                int j = 1;
                int clen = contour.size();
                while (j < clen) {
                    Point pnow = contour.get(j);
                    if (pnow.onCurve) {
                        lineTo(path, pnow);
                    } else if (contour.get(j + 1).onCurve) {
                        quadTo(path, pnow, contour.get(j + 1));
                        j++;
                    } else {
                        quadTo(path, pnow, midValue(pnow, contour.get(j + 1)));
                    }
                    j++;
                }
                path.closePath();
                start = p + 1;
            }
        }
        return path;
    }

    private void moveTo(GeneralPath path, Point point) {
        path.moveTo(point.x, point.y);
        if (LOG.isDebugEnabled()) {
            LOG.trace("moveTo: " + String.format(Locale.US, "%d,%d", Integer.valueOf(point.x), Integer.valueOf(point.y)));
        }
    }

    private void lineTo(GeneralPath path, Point point) {
        path.lineTo(point.x, point.y);
        if (LOG.isDebugEnabled()) {
            LOG.trace("lineTo: " + String.format(Locale.US, "%d,%d", Integer.valueOf(point.x), Integer.valueOf(point.y)));
        }
    }

    private void quadTo(GeneralPath path, Point ctrlPoint, Point point) {
        path.quadTo(ctrlPoint.x, ctrlPoint.y, point.x, point.y);
        if (LOG.isDebugEnabled()) {
            LOG.trace("quadTo: " + String.format(Locale.US, "%d,%d %d,%d", Integer.valueOf(ctrlPoint.x), Integer.valueOf(ctrlPoint.y), Integer.valueOf(point.x), Integer.valueOf(point.y)));
        }
    }

    private int midValue(int a, int b) {
        return a + ((b - a) / 2);
    }

    private Point midValue(Point point1, Point point2) {
        return new Point(midValue(point1.x, point2.x), midValue(point1.y, point2.y));
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphRenderer$Point.class */
    private static class Point {
        private int x;
        private int y;
        private boolean onCurve;
        private boolean endOfContour;

        Point(int xValue, int yValue, boolean onCurveValue, boolean endOfContourValue) {
            this.x = 0;
            this.y = 0;
            this.onCurve = true;
            this.endOfContour = false;
            this.x = xValue;
            this.y = yValue;
            this.onCurve = onCurveValue;
            this.endOfContour = endOfContourValue;
        }

        Point(int xValue, int yValue) {
            this(xValue, yValue, true, false);
        }

        public String toString() {
            Locale locale = Locale.US;
            Object[] objArr = new Object[4];
            objArr[0] = Integer.valueOf(this.x);
            objArr[1] = Integer.valueOf(this.y);
            objArr[2] = this.onCurve ? "onCurve" : "";
            objArr[3] = this.endOfContour ? "endOfContour" : "";
            return String.format(locale, "Point(%d,%d,%s,%s)", objArr);
        }
    }
}
