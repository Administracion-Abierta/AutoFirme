package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.geom.Point2D;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/shading/CoordinateColorPair.class */
class CoordinateColorPair {
    final Point2D coordinate;
    final float[] color;

    CoordinateColorPair(Point2D p, float[] c) {
        this.coordinate = p;
        this.color = (float[]) c.clone();
    }
}
