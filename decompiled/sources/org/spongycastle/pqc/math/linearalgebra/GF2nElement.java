package org.spongycastle.pqc.math.linearalgebra;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/linearalgebra/GF2nElement.class */
public abstract class GF2nElement implements GFElement {
    protected GF2nField mField;
    protected int mDegree;

    @Override // org.spongycastle.pqc.math.linearalgebra.GFElement
    public abstract Object clone();

    abstract void assignZero();

    abstract void assignOne();

    public abstract boolean testRightmostBit();

    abstract boolean testBit(int i);

    public abstract GF2nElement increase();

    public abstract void increaseThis();

    public abstract GF2nElement square();

    public abstract void squareThis();

    public abstract GF2nElement squareRoot();

    public abstract void squareRootThis();

    public abstract int trace();

    public abstract GF2nElement solveQuadraticEquation() throws RuntimeException;

    public final GF2nField getField() {
        return this.mField;
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.GFElement
    public final GFElement subtract(GFElement minuend) throws RuntimeException {
        return add(minuend);
    }

    @Override // org.spongycastle.pqc.math.linearalgebra.GFElement
    public final void subtractFromThis(GFElement minuend) {
        addToThis(minuend);
    }

    public final GF2nElement convert(GF2nField basis) throws RuntimeException {
        return this.mField.convert(this, basis);
    }
}
