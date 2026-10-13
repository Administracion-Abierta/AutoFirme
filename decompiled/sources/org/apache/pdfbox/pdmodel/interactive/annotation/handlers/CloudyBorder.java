package org.apache.pdfbox.pdmodel.interactive.annotation.handlers;

import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/handlers/CloudyBorder.class */
class CloudyBorder {
    private static final double ANGLE_180_DEG = 3.141592653589793d;
    private static final double ANGLE_90_DEG = 1.5707963267948966d;
    private static final double ANGLE_34_DEG = Math.toRadians(34.0d);
    private static final double ANGLE_30_DEG = Math.toRadians(30.0d);
    private static final double ANGLE_12_DEG = Math.toRadians(12.0d);
    private final PDAppearanceContentStream output;
    private final PDRectangle annotRect;
    private final double intensity;
    private final double lineWidth;
    private PDRectangle rectWithDiff;
    private boolean outputStarted = false;
    private double bboxMinX;
    private double bboxMinY;
    private double bboxMaxX;
    private double bboxMaxY;

    CloudyBorder(PDAppearanceContentStream stream, double intensity, double lineWidth, PDRectangle rect) {
        this.output = stream;
        this.intensity = intensity;
        this.lineWidth = lineWidth;
        this.annotRect = rect;
    }

    void createCloudyRectangle(PDRectangle rd) throws IOException {
        this.rectWithDiff = applyRectDiff(rd, this.lineWidth / 2.0d);
        double left = this.rectWithDiff.getLowerLeftX();
        double bottom = this.rectWithDiff.getLowerLeftY();
        double right = this.rectWithDiff.getUpperRightX();
        double top = this.rectWithDiff.getUpperRightY();
        cloudyRectangleImpl(left, bottom, right, top, false);
        finish();
    }

    void createCloudyPolygon(float[][] path) throws IOException {
        int n = path.length;
        Point2D.Double[] polygon = new Point2D.Double[n];
        for (int i = 0; i < n; i++) {
            float[] array = path[i];
            if (array.length == 2) {
                polygon[i] = new Point2D.Double(array[0], array[1]);
            } else if (array.length == 6) {
                polygon[i] = new Point2D.Double(array[4], array[5]);
            }
        }
        cloudyPolygonImpl(polygon, false);
        finish();
    }

    void createCloudyEllipse(PDRectangle rd) throws IOException {
        this.rectWithDiff = applyRectDiff(rd, 0.0d);
        double left = this.rectWithDiff.getLowerLeftX();
        double bottom = this.rectWithDiff.getLowerLeftY();
        double right = this.rectWithDiff.getUpperRightX();
        double top = this.rectWithDiff.getUpperRightY();
        cloudyEllipseImpl(left, bottom, right, top);
        finish();
    }

    PDRectangle getBBox() {
        return getRectangle();
    }

    PDRectangle getRectangle() {
        return new PDRectangle((float) this.bboxMinX, (float) this.bboxMinY, (float) (this.bboxMaxX - this.bboxMinX), (float) (this.bboxMaxY - this.bboxMinY));
    }

    AffineTransform getMatrix() {
        return AffineTransform.getTranslateInstance(-this.bboxMinX, -this.bboxMinY);
    }

    PDRectangle getRectDifference() {
        if (this.annotRect == null) {
            float d = ((float) this.lineWidth) / 2.0f;
            return new PDRectangle(d, d, (float) this.lineWidth, (float) this.lineWidth);
        }
        PDRectangle re = this.rectWithDiff != null ? this.rectWithDiff : this.annotRect;
        float left = re.getLowerLeftX() - ((float) this.bboxMinX);
        float bottom = re.getLowerLeftY() - ((float) this.bboxMinY);
        float right = ((float) this.bboxMaxX) - re.getUpperRightX();
        float top = ((float) this.bboxMaxY) - re.getUpperRightY();
        return new PDRectangle(left, bottom, right - left, top - bottom);
    }

    private static double cosine(double dx, double hypot) {
        if (Double.compare(hypot, 0.0d) == 0) {
            return 0.0d;
        }
        return dx / hypot;
    }

    private static double sine(double dy, double hypot) {
        if (Double.compare(hypot, 0.0d) == 0) {
            return 0.0d;
        }
        return dy / hypot;
    }

    private void cloudyRectangleImpl(double left, double bottom, double right, double top, boolean isEllipse) throws IOException {
        Point2D.Double[] polygon;
        double w = right - left;
        double h = top - bottom;
        if (this.intensity <= 0.0d) {
            this.output.addRect((float) left, (float) bottom, (float) w, (float) h);
            this.bboxMinX = left;
            this.bboxMinY = bottom;
            this.bboxMaxX = right;
            this.bboxMaxY = top;
            return;
        }
        if (w < 1.0d) {
            polygon = new Point2D.Double[]{new Point2D.Double(left, bottom), new Point2D.Double(left, top), new Point2D.Double(left, bottom)};
        } else if (h < 1.0d) {
            polygon = new Point2D.Double[]{new Point2D.Double(left, bottom), new Point2D.Double(right, bottom), new Point2D.Double(left, bottom)};
        } else {
            polygon = new Point2D.Double[]{new Point2D.Double(left, bottom), new Point2D.Double(right, bottom), new Point2D.Double(right, top), new Point2D.Double(left, top), new Point2D.Double(left, bottom)};
        }
        cloudyPolygonImpl(polygon, isEllipse);
    }

    private void cloudyPolygonImpl(Point2D.Double[] vertices, boolean isEllipse) throws IOException {
        Point2D.Double[] polygon = removeZeroLengthSegments(vertices);
        getPositivePolygon(polygon);
        int numPoints = polygon.length;
        if (numPoints < 2) {
            return;
        }
        if (this.intensity <= 0.0d) {
            moveTo(polygon[0]);
            for (int i = 1; i < numPoints; i++) {
                lineTo(polygon[i]);
            }
            return;
        }
        double cloudRadius = isEllipse ? getEllipseCloudRadius() : getPolygonCloudRadius();
        if (cloudRadius < 0.5d) {
            cloudRadius = 0.5d;
        }
        double k = Math.cos(ANGLE_34_DEG);
        double advIntermDefault = 2.0d * k * cloudRadius;
        double advCornerDefault = k * cloudRadius;
        double[] array = new double[2];
        double anglePrev = 0.0d;
        int n0 = computeParamsPolygon(advIntermDefault, advCornerDefault, k, cloudRadius, polygon[numPoints - 2].distance(polygon[0]), array);
        double alphaPrev = n0 == 0 ? array[0] : ANGLE_34_DEG;
        for (int j = 0; j + 1 < numPoints; j++) {
            Point2D.Double pt = polygon[j];
            Point2D.Double ptNext = polygon[j + 1];
            double length = pt.distance(ptNext);
            if (Double.compare(length, 0.0d) == 0) {
                alphaPrev = ANGLE_34_DEG;
            } else {
                int n = computeParamsPolygon(advIntermDefault, advCornerDefault, k, cloudRadius, length, array);
                if (n < 0) {
                    if (!this.outputStarted) {
                        moveTo(pt);
                    }
                } else {
                    double alpha = array[0];
                    double dx = array[1];
                    double angleCur = Math.atan2(ptNext.y - pt.y, ptNext.x - pt.x);
                    if (j == 0) {
                        Point2D.Double ptPrev = polygon[numPoints - 2];
                        anglePrev = Math.atan2(pt.y - ptPrev.y, pt.x - ptPrev.x);
                    }
                    double cos = cosine(ptNext.x - pt.x, length);
                    double sin = sine(ptNext.y - pt.y, length);
                    double x = pt.x;
                    double y = pt.y;
                    addCornerCurl(anglePrev, angleCur, cloudRadius, pt.x, pt.y, alpha, alphaPrev, !this.outputStarted);
                    double adv = (2.0d * k * cloudRadius) + (2.0d * dx);
                    double x2 = x + (adv * cos);
                    double y2 = y + (adv * sin);
                    int numInterm = n;
                    if (n >= 1) {
                        addFirstIntermediateCurl(angleCur, cloudRadius, alpha, x2, y2);
                        x2 += advIntermDefault * cos;
                        y2 += advIntermDefault * sin;
                        numInterm = n - 1;
                    }
                    Point2D.Double[] template = getIntermediateCurlTemplate(angleCur, cloudRadius);
                    for (int i2 = 0; i2 < numInterm; i2++) {
                        outputCurlTemplate(template, x2, y2);
                        x2 += advIntermDefault * cos;
                        y2 += advIntermDefault * sin;
                    }
                    anglePrev = angleCur;
                    alphaPrev = n == 0 ? alpha : ANGLE_34_DEG;
                }
            }
        }
    }

    private int computeParamsPolygon(double advInterm, double advCorner, double k, double r, double length, double[] array) {
        if (Double.compare(length, 0.0d) == 0) {
            array[0] = ANGLE_34_DEG;
            array[1] = 0.0d;
            return -1;
        }
        int n = (int) Math.ceil((length - (2.0d * advCorner)) / advInterm);
        double e = length - ((2.0d * advCorner) + (((double) n) * advInterm));
        double dx = e / 2.0d;
        double arg = ((k * r) + dx) / r;
        double alpha = (arg < -1.0d || arg > 1.0d) ? 0.0d : Math.acos(arg);
        array[0] = alpha;
        array[1] = dx;
        return n;
    }

    private void addCornerCurl(double anglePrev, double angleCur, double radius, double cx, double cy, double alpha, double alphaPrev, boolean addMoveTo) throws IOException {
        double a = anglePrev + ANGLE_180_DEG + alphaPrev;
        double b = ((anglePrev + ANGLE_180_DEG) + alphaPrev) - Math.toRadians(22.0d);
        getArcSegment(a, b, cx, cy, radius, radius, null, addMoveTo);
        getArc(b, angleCur - alpha, radius, radius, cx, cy, null, false);
    }

    private void addFirstIntermediateCurl(double angleCur, double r, double alpha, double cx, double cy) throws IOException {
        double a = angleCur + ANGLE_180_DEG;
        getArcSegment(a + alpha, (a + alpha) - ANGLE_30_DEG, cx, cy, r, r, null, false);
        getArcSegment((a + alpha) - ANGLE_30_DEG, a + ANGLE_90_DEG, cx, cy, r, r, null, false);
        getArcSegment(a + ANGLE_90_DEG, (a + ANGLE_180_DEG) - ANGLE_34_DEG, cx, cy, r, r, null, false);
    }

    private Point2D.Double[] getIntermediateCurlTemplate(double angleCur, double r) throws IOException {
        ArrayList<Point2D.Double> points = new ArrayList<>();
        double a = angleCur + ANGLE_180_DEG;
        getArcSegment(a + ANGLE_34_DEG, a + ANGLE_12_DEG, 0.0d, 0.0d, r, r, points, false);
        getArcSegment(a + ANGLE_12_DEG, a + ANGLE_90_DEG, 0.0d, 0.0d, r, r, points, false);
        getArcSegment(a + ANGLE_90_DEG, (a + ANGLE_180_DEG) - ANGLE_34_DEG, 0.0d, 0.0d, r, r, points, false);
        return (Point2D.Double[]) points.toArray(new Point2D.Double[points.size()]);
    }

    private void outputCurlTemplate(Point2D.Double[] template, double x, double y) throws IOException {
        int n = template.length;
        int i = 0;
        if (n % 3 == 1) {
            Point2D.Double a = template[0];
            moveTo(a.x + x, a.y + y);
            i = 0 + 1;
        }
        while (i + 2 < n) {
            Point2D.Double a2 = template[i];
            Point2D.Double b = template[i + 1];
            Point2D.Double c = template[i + 2];
            curveTo(a2.x + x, a2.y + y, b.x + x, b.y + y, c.x + x, c.y + y);
            i += 3;
        }
    }

    private PDRectangle applyRectDiff(PDRectangle rd, double min) {
        double rdLeft;
        double rdBottom;
        double rdRight;
        double rdTop;
        float rectLeft = this.annotRect.getLowerLeftX();
        float rectBottom = this.annotRect.getLowerLeftY();
        float rectRight = this.annotRect.getUpperRightX();
        float rectTop = this.annotRect.getUpperRightY();
        float rectLeft2 = Math.min(rectLeft, rectRight);
        float rectBottom2 = Math.min(rectBottom, rectTop);
        float rectRight2 = Math.max(rectLeft2, rectRight);
        float rectTop2 = Math.max(rectBottom2, rectTop);
        if (rd != null) {
            rdLeft = Math.max(rd.getLowerLeftX(), min);
            rdBottom = Math.max(rd.getLowerLeftY(), min);
            rdRight = Math.max(rd.getUpperRightX(), min);
            rdTop = Math.max(rd.getUpperRightY(), min);
        } else {
            rdLeft = min;
            rdBottom = min;
            rdRight = min;
            rdTop = min;
        }
        float rectLeft3 = (float) (((double) rectLeft2) + rdLeft);
        float rectBottom3 = (float) (((double) rectBottom2) + rdBottom);
        return new PDRectangle(rectLeft3, rectBottom3, ((float) (((double) rectRight2) - rdRight)) - rectLeft3, ((float) (((double) rectTop2) - rdTop)) - rectBottom3);
    }

    private void reversePolygon(Point2D.Double[] points) {
        int len = points.length;
        int n = len / 2;
        for (int i = 0; i < n; i++) {
            int j = (len - i) - 1;
            Point2D.Double pi = points[i];
            Point2D.Double pj = points[j];
            points[i] = pj;
            points[j] = pi;
        }
    }

    private void getPositivePolygon(Point2D.Double[] points) {
        if (getPolygonDirection(points) < 0.0d) {
            reversePolygon(points);
        }
    }

    private double getPolygonDirection(Point2D.Double[] points) {
        double a = 0.0d;
        int len = points.length;
        for (int i = 0; i < len; i++) {
            int j = (i + 1) % len;
            a += (points[i].x * points[j].y) - (points[i].y * points[j].x);
        }
        return a;
    }

    private void getArc(double startAng, double endAng, double rx, double ry, double cx, double cy, ArrayList<Point2D.Double> out, boolean addMoveTo) throws IOException {
        double angleTodo;
        double startx = (rx * Math.cos(startAng)) + cx;
        double starty = (ry * Math.sin(startAng)) + cy;
        double d = endAng - startAng;
        while (true) {
            angleTodo = d;
            if (angleTodo >= 0.0d) {
                break;
            } else {
                d = angleTodo + 6.283185307179586d;
            }
        }
        double angleDone = 0.0d;
        if (addMoveTo) {
            if (out != null) {
                out.add(new Point2D.Double(startx, starty));
            } else {
                moveTo(startx, starty);
            }
        }
        while (angleTodo > ANGLE_90_DEG) {
            getArcSegment(startAng + angleDone, startAng + angleDone + ANGLE_90_DEG, cx, cy, rx, ry, out, false);
            angleDone += ANGLE_90_DEG;
            angleTodo -= ANGLE_90_DEG;
        }
        if (angleTodo > 0.0d) {
            getArcSegment(startAng + angleDone, startAng + angleTodo, cx, cy, rx, ry, out, false);
        }
    }

    private void getArcSegment(double startAng, double endAng, double cx, double cy, double rx, double ry, ArrayList<Point2D.Double> out, boolean addMoveTo) throws IOException {
        double cosA = Math.cos(startAng);
        double sinA = Math.sin(startAng);
        double cosB = Math.cos(endAng);
        double sinB = Math.sin(endAng);
        double denom = Math.sin((endAng - startAng) / 2.0d);
        if (Double.compare(denom, 0.0d) == 0) {
            if (addMoveTo) {
                double xs = cx + (rx * cosA);
                double ys = cy + (ry * sinA);
                if (out != null) {
                    out.add(new Point2D.Double(xs, ys));
                    return;
                } else {
                    moveTo(xs, ys);
                    return;
                }
            }
            return;
        }
        double bcp = (1.333333333d * (1.0d - Math.cos((endAng - startAng) / 2.0d))) / denom;
        double p1x = cx + (rx * (cosA - (bcp * sinA)));
        double p1y = cy + (ry * (sinA + (bcp * cosA)));
        double p2x = cx + (rx * (cosB + (bcp * sinB)));
        double p2y = cy + (ry * (sinB - (bcp * cosB)));
        double p3x = cx + (rx * cosB);
        double p3y = cy + (ry * sinB);
        if (addMoveTo) {
            double xs2 = cx + (rx * cosA);
            double ys2 = cy + (ry * sinA);
            if (out != null) {
                out.add(new Point2D.Double(xs2, ys2));
            } else {
                moveTo(xs2, ys2);
            }
        }
        if (out != null) {
            out.add(new Point2D.Double(p1x, p1y));
            out.add(new Point2D.Double(p2x, p2y));
            out.add(new Point2D.Double(p3x, p3y));
            return;
        }
        curveTo(p1x, p1y, p2x, p2y, p3x, p3y);
    }

    private static Point2D.Double[] flattenEllipse(double left, double bottom, double right, double top) {
        Ellipse2D.Double ellipse = new Ellipse2D.Double(left, bottom, right - left, top - bottom);
        PathIterator iterator = ellipse.getPathIterator((AffineTransform) null, 0.5d);
        double[] coords = new double[6];
        ArrayList arrayList = new ArrayList();
        while (!iterator.isDone()) {
            switch (iterator.currentSegment(coords)) {
                case 0:
                case 1:
                    arrayList.add(new Point2D.Double(coords[0], coords[1]));
                    break;
            }
            iterator.next();
        }
        int size = arrayList.size();
        if (size >= 2 && ((Point2D.Double) arrayList.get(size - 1)).distance((Point2D) arrayList.get(0)) > 0.05d) {
            arrayList.add(arrayList.get(arrayList.size() - 1));
        }
        return (Point2D.Double[]) arrayList.toArray(new Point2D.Double[arrayList.size()]);
    }

    private void cloudyEllipseImpl(double leftOrig, double bottomOrig, double rightOrig, double topOrig) throws IOException {
        double left;
        double right;
        double top;
        double bottom;
        if (this.intensity <= 0.0d) {
            drawBasicEllipse(leftOrig, bottomOrig, rightOrig, topOrig);
            return;
        }
        double width = rightOrig - leftOrig;
        double height = topOrig - bottomOrig;
        double cloudRadius = getEllipseCloudRadius();
        double threshold1 = 0.5d * cloudRadius;
        if (width < threshold1 && height < threshold1) {
            drawBasicEllipse(leftOrig, bottomOrig, rightOrig, topOrig);
            return;
        }
        if ((width < 5.0d && height > 20.0d) || (width > 20.0d && height < 5.0d)) {
            cloudyRectangleImpl(leftOrig, bottomOrig, rightOrig, topOrig, true);
            return;
        }
        double radiusAdj = (Math.sin(ANGLE_12_DEG) * cloudRadius) - 1.5d;
        if (width > 2.0d * radiusAdj) {
            left = leftOrig + radiusAdj;
            right = rightOrig - radiusAdj;
        } else {
            double mid = (leftOrig + rightOrig) / 2.0d;
            left = mid - 0.1d;
            right = mid + 0.1d;
        }
        if (height > 2.0d * radiusAdj) {
            top = topOrig - radiusAdj;
            bottom = bottomOrig + radiusAdj;
        } else {
            double mid2 = (topOrig + bottomOrig) / 2.0d;
            top = mid2 + 0.1d;
            bottom = mid2 - 0.1d;
        }
        Point2D[] point2DArrFlattenEllipse = flattenEllipse(left, bottom, right, top);
        int numPoints = point2DArrFlattenEllipse.length;
        if (numPoints < 2) {
            return;
        }
        double totLen = 0.0d;
        for (int i = 1; i < numPoints; i++) {
            totLen += point2DArrFlattenEllipse[i - 1].distance(point2DArrFlattenEllipse[i]);
        }
        double k = Math.cos(ANGLE_34_DEG);
        int n = (int) Math.ceil(totLen / ((2.0d * k) * cloudRadius));
        if (n < 2) {
            drawBasicEllipse(leftOrig, bottomOrig, rightOrig, topOrig);
            return;
        }
        double curlAdvance = totLen / ((double) n);
        double cloudRadius2 = curlAdvance / (2.0d * k);
        if (cloudRadius2 < 0.5d) {
            cloudRadius2 = 0.5d;
            curlAdvance = 2.0d * k * 0.5d;
        } else if (cloudRadius2 < 3.0d) {
            drawBasicEllipse(leftOrig, bottomOrig, rightOrig, topOrig);
            return;
        }
        Point2D.Double[] centerPoints = new Point2D.Double[n];
        int centerPointsIndex = 0;
        double lengthRemain = 0.0d;
        double comparisonToler = this.lineWidth * 0.1d;
        for (int i2 = 0; i2 + 1 < numPoints; i2++) {
            Point2D point2D = point2DArrFlattenEllipse[i2];
            Point2D point2D2 = point2DArrFlattenEllipse[i2 + 1];
            double dx = ((Point2D.Double) point2D2).x - ((Point2D.Double) point2D).x;
            double dy = ((Point2D.Double) point2D2).y - ((Point2D.Double) point2D).y;
            double length = point2D.distance(point2D2);
            if (Double.compare(length, 0.0d) != 0) {
                double lengthTodo = length + lengthRemain;
                if (lengthTodo >= curlAdvance - comparisonToler || i2 == numPoints - 2) {
                    double cos = cosine(dx, length);
                    double sin = sine(dy, length);
                    double d = curlAdvance - lengthRemain;
                    do {
                        double x = ((Point2D.Double) point2D).x + (d * cos);
                        double y = ((Point2D.Double) point2D).y + (d * sin);
                        if (centerPointsIndex < n) {
                            int i3 = centerPointsIndex;
                            centerPointsIndex++;
                            centerPoints[i3] = new Point2D.Double(x, y);
                        }
                        lengthTodo -= curlAdvance;
                        d += curlAdvance;
                    } while (lengthTodo >= curlAdvance - comparisonToler);
                    lengthRemain = lengthTodo;
                    if (lengthRemain < 0.0d) {
                        lengthRemain = 0.0d;
                    }
                } else {
                    lengthRemain += length;
                }
            }
        }
        int numPoints2 = centerPointsIndex;
        double anglePrev = 0.0d;
        double alphaPrev = 0.0d;
        for (int i4 = 0; i4 < numPoints2; i4++) {
            int idxNext = i4 + 1;
            if (i4 + 1 >= numPoints2) {
                idxNext = 0;
            }
            Point2D.Double pt = centerPoints[i4];
            Point2D.Double ptNext = centerPoints[idxNext];
            if (i4 == 0) {
                Point2D.Double ptPrev = centerPoints[numPoints2 - 1];
                anglePrev = Math.atan2(pt.y - ptPrev.y, pt.x - ptPrev.x);
                alphaPrev = computeParamsEllipse(ptPrev, pt, cloudRadius2, curlAdvance);
            }
            double angleCur = Math.atan2(ptNext.y - pt.y, ptNext.x - pt.x);
            double alpha = computeParamsEllipse(pt, ptNext, cloudRadius2, curlAdvance);
            addCornerCurl(anglePrev, angleCur, cloudRadius2, pt.x, pt.y, alpha, alphaPrev, !this.outputStarted);
            anglePrev = angleCur;
            alphaPrev = alpha;
        }
    }

    private double computeParamsEllipse(Point2D.Double pt, Point2D.Double ptNext, double r, double curlAdv) {
        double length = pt.distance(ptNext);
        if (Double.compare(length, 0.0d) == 0) {
            return ANGLE_34_DEG;
        }
        double e = length - curlAdv;
        double arg = ((curlAdv / 2.0d) + (e / 2.0d)) / r;
        if (arg < -1.0d || arg > 1.0d) {
            return 0.0d;
        }
        return Math.acos(arg);
    }

    private Point2D.Double[] removeZeroLengthSegments(Point2D.Double[] polygon) {
        int np = polygon.length;
        if (np <= 2) {
            return polygon;
        }
        int npNew = np;
        Point2D.Double ptPrev = polygon[0];
        for (int i = 1; i < np; i++) {
            Point2D.Double pt = polygon[i];
            if (Math.abs(pt.x - ptPrev.x) < 0.5d && Math.abs(pt.y - ptPrev.y) < 0.5d) {
                polygon[i] = null;
                npNew--;
            }
            ptPrev = pt;
        }
        if (npNew == np) {
            return polygon;
        }
        Point2D.Double[] polygonNew = new Point2D.Double[npNew];
        int j = 0;
        for (Point2D.Double pt2 : polygon) {
            if (pt2 != null) {
                int i2 = j;
                j++;
                polygonNew[i2] = pt2;
            }
        }
        return polygonNew;
    }

    private void drawBasicEllipse(double left, double bottom, double right, double top) throws IOException {
        double rx = Math.abs(right - left) / 2.0d;
        double ry = Math.abs(top - bottom) / 2.0d;
        double cx = (left + right) / 2.0d;
        double cy = (bottom + top) / 2.0d;
        getArc(0.0d, 6.283185307179586d, rx, ry, cx, cy, null, true);
    }

    private void beginOutput(double x, double y) throws IOException {
        this.bboxMinX = x;
        this.bboxMinY = y;
        this.bboxMaxX = x;
        this.bboxMaxY = y;
        this.outputStarted = true;
        this.output.setLineJoinStyle(2);
    }

    private void updateBBox(double x, double y) {
        this.bboxMinX = Math.min(this.bboxMinX, x);
        this.bboxMinY = Math.min(this.bboxMinY, y);
        this.bboxMaxX = Math.max(this.bboxMaxX, x);
        this.bboxMaxY = Math.max(this.bboxMaxY, y);
    }

    private void moveTo(Point2D.Double p) throws IOException {
        moveTo(p.x, p.y);
    }

    private void moveTo(double x, double y) throws IOException {
        if (this.outputStarted) {
            updateBBox(x, y);
        } else {
            beginOutput(x, y);
        }
        this.output.moveTo((float) x, (float) y);
    }

    private void lineTo(Point2D.Double p) throws IOException {
        lineTo(p.x, p.y);
    }

    private void lineTo(double x, double y) throws IOException {
        if (this.outputStarted) {
            updateBBox(x, y);
        } else {
            beginOutput(x, y);
        }
        this.output.lineTo((float) x, (float) y);
    }

    private void curveTo(double ax, double ay, double bx, double by, double cx, double cy) throws IOException {
        updateBBox(ax, ay);
        updateBBox(bx, by);
        updateBBox(cx, cy);
        this.output.curveTo((float) ax, (float) ay, (float) bx, (float) by, (float) cx, (float) cy);
    }

    private void finish() throws IOException {
        if (this.outputStarted) {
            this.output.closePath();
        }
        if (this.lineWidth > 0.0d) {
            double d = this.lineWidth / 2.0d;
            this.bboxMinX -= d;
            this.bboxMinY -= d;
            this.bboxMaxX += d;
            this.bboxMaxY += d;
        }
    }

    private double getEllipseCloudRadius() {
        return (4.75d * this.intensity) + (0.5d * this.lineWidth);
    }

    private double getPolygonCloudRadius() {
        return (4.0d * this.intensity) + (0.5d * this.lineWidth);
    }
}
