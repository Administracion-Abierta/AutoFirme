package org.apache.pdfbox.util;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/util/Vector.class */
public final class Vector {
    private final float x;
    private final float y;

    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public Vector scale(float sxy) {
        return new Vector(this.x * sxy, this.y * sxy);
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}
