package org.spongycastle.math.field;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/math/field/ExtensionField.class */
public interface ExtensionField extends FiniteField {
    FiniteField getSubfield();

    int getDegree();
}
