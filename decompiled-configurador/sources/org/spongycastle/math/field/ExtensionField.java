package org.spongycastle.math.field;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/field/ExtensionField.class */
public interface ExtensionField extends FiniteField {
    FiniteField getSubfield();

    int getDegree();
}
