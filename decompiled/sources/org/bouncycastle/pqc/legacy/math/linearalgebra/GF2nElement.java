package org.bouncycastle.pqc.legacy.math.linearalgebra;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/linearalgebra/GF2nElement.class */
public abstract class GF2nElement implements GFElement {
    protected GF2nField mField;
    protected int mDegree;

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.GFElement
    public abstract Object clone();

    abstract void assignZero();

    abstract void assignOne();

    public abstract boolean testRightmostBit();

    abstract boolean testBit(int i);

    public final GF2nField getField() {
        return this.mField;
    }

    public abstract GF2nElement increase();

    public abstract void increaseThis();

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.GFElement
    public final GFElement subtract(GFElement gFElement) {
        return add(gFElement);
    }

    @Override // org.bouncycastle.pqc.legacy.math.linearalgebra.GFElement
    public final void subtractFromThis(GFElement gFElement) {
        addToThis(gFElement);
    }

    public abstract GF2nElement square();

    public abstract void squareThis();

    public abstract GF2nElement squareRoot();

    public abstract void squareRootThis();

    public final GF2nElement convert(GF2nField gF2nField) {
        return this.mField.convert(this, gF2nField);
    }

    public abstract int trace();

    public abstract GF2nElement solveQuadraticEquation() throws RuntimeException;
}
