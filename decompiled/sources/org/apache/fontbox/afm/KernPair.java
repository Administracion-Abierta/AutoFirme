package org.apache.fontbox.afm;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/afm/KernPair.class */
public class KernPair {
    private String firstKernCharacter;
    private String secondKernCharacter;
    private float x;
    private float y;

    public String getFirstKernCharacter() {
        return this.firstKernCharacter;
    }

    public void setFirstKernCharacter(String firstKernCharacterValue) {
        this.firstKernCharacter = firstKernCharacterValue;
    }

    public String getSecondKernCharacter() {
        return this.secondKernCharacter;
    }

    public void setSecondKernCharacter(String secondKernCharacterValue) {
        this.secondKernCharacter = secondKernCharacterValue;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float xValue) {
        this.x = xValue;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float yValue) {
        this.y = yValue;
    }
}
