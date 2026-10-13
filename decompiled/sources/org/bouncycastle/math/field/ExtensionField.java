package org.bouncycastle.math.field;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/math/field/ExtensionField.class */
public interface ExtensionField extends FiniteField {
    FiniteField getSubfield();

    int getDegree();
}
