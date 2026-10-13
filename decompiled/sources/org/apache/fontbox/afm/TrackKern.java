package org.apache.fontbox.afm;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/afm/TrackKern.class */
public class TrackKern {
    private int degree;
    private float minPointSize;
    private float minKern;
    private float maxPointSize;
    private float maxKern;

    public int getDegree() {
        return this.degree;
    }

    public void setDegree(int degreeValue) {
        this.degree = degreeValue;
    }

    public float getMaxKern() {
        return this.maxKern;
    }

    public void setMaxKern(float maxKernValue) {
        this.maxKern = maxKernValue;
    }

    public float getMaxPointSize() {
        return this.maxPointSize;
    }

    public void setMaxPointSize(float maxPointSizeValue) {
        this.maxPointSize = maxPointSizeValue;
    }

    public float getMinKern() {
        return this.minKern;
    }

    public void setMinKern(float minKernValue) {
        this.minKern = minKernValue;
    }

    public float getMinPointSize() {
        return this.minPointSize;
    }

    public void setMinPointSize(float minPointSizeValue) {
        this.minPointSize = minPointSizeValue;
    }
}
