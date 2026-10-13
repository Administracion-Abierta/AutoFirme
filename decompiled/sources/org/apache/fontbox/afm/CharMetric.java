package org.apache.fontbox.afm;

import java.util.ArrayList;
import java.util.List;
import org.apache.fontbox.util.BoundingBox;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/afm/CharMetric.class */
public class CharMetric {
    private int characterCode;
    private float wx;
    private float w0x;
    private float w1x;
    private float wy;
    private float w0y;
    private float w1y;
    private float[] w;
    private float[] w0;
    private float[] w1;
    private float[] vv;
    private String name;
    private BoundingBox boundingBox;
    private List<Ligature> ligatures = new ArrayList();

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public void setBoundingBox(BoundingBox bBox) {
        this.boundingBox = bBox;
    }

    public int getCharacterCode() {
        return this.characterCode;
    }

    public void setCharacterCode(int cCode) {
        this.characterCode = cCode;
    }

    public void addLigature(Ligature ligature) {
        this.ligatures.add(ligature);
    }

    public List<Ligature> getLigatures() {
        return this.ligatures;
    }

    public void setLigatures(List<Ligature> lig) {
        this.ligatures = lig;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String n) {
        this.name = n;
    }

    public float[] getVv() {
        return this.vv;
    }

    public void setVv(float[] vvValue) {
        this.vv = vvValue;
    }

    public float[] getW() {
        return this.w;
    }

    public void setW(float[] wValue) {
        this.w = wValue;
    }

    public float[] getW0() {
        return this.w0;
    }

    public void setW0(float[] w0Value) {
        this.w0 = w0Value;
    }

    public float getW0x() {
        return this.w0x;
    }

    public void setW0x(float w0xValue) {
        this.w0x = w0xValue;
    }

    public float getW0y() {
        return this.w0y;
    }

    public void setW0y(float w0yValue) {
        this.w0y = w0yValue;
    }

    public float[] getW1() {
        return this.w1;
    }

    public void setW1(float[] w1Value) {
        this.w1 = w1Value;
    }

    public float getW1x() {
        return this.w1x;
    }

    public void setW1x(float w1xValue) {
        this.w1x = w1xValue;
    }

    public float getW1y() {
        return this.w1y;
    }

    public void setW1y(float w1yValue) {
        this.w1y = w1yValue;
    }

    public float getWx() {
        return this.wx;
    }

    public void setWx(float wxValue) {
        this.wx = wxValue;
    }

    public float getWy() {
        return this.wy;
    }

    public void setWy(float wyValue) {
        this.wy = wyValue;
    }
}
