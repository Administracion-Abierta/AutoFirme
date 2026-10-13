package com.aowagie.text.pdf.internal;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/internal/PolylineShapeIterator.class */
public class PolylineShapeIterator implements PathIterator {
    protected PolylineShape poly;
    protected AffineTransform affine;
    protected int index;

    PolylineShapeIterator(PolylineShape l, AffineTransform at) {
        this.poly = l;
        this.affine = at;
    }

    public int currentSegment(double[] coords) {
        if (isDone()) {
            throw new NoSuchElementException("line iterator out of bounds");
        }
        int type = this.index == 0 ? 0 : 1;
        coords[0] = this.poly.x[this.index];
        coords[1] = this.poly.y[this.index];
        if (this.affine != null) {
            this.affine.transform(coords, 0, coords, 0, 1);
        }
        return type;
    }

    public int currentSegment(float[] coords) {
        if (isDone()) {
            throw new NoSuchElementException("line iterator out of bounds");
        }
        int type = this.index == 0 ? 0 : 1;
        coords[0] = this.poly.x[this.index];
        coords[1] = this.poly.y[this.index];
        if (this.affine != null) {
            this.affine.transform(coords, 0, coords, 0, 1);
        }
        return type;
    }

    public int getWindingRule() {
        return 1;
    }

    public boolean isDone() {
        return this.index >= this.poly.np;
    }

    public void next() {
        this.index++;
    }
}
