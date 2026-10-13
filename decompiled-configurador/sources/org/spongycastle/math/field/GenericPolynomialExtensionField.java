package org.spongycastle.math.field;

import java.math.BigInteger;
import org.spongycastle.util.Integers;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/math/field/GenericPolynomialExtensionField.class */
class GenericPolynomialExtensionField implements PolynomialExtensionField {
    protected final FiniteField subfield;
    protected final Polynomial minimalPolynomial;

    GenericPolynomialExtensionField(FiniteField subfield, Polynomial polynomial) {
        this.subfield = subfield;
        this.minimalPolynomial = polynomial;
    }

    @Override // org.spongycastle.math.field.FiniteField
    public BigInteger getCharacteristic() {
        return this.subfield.getCharacteristic();
    }

    @Override // org.spongycastle.math.field.FiniteField
    public int getDimension() {
        return this.subfield.getDimension() * this.minimalPolynomial.getDegree();
    }

    @Override // org.spongycastle.math.field.ExtensionField
    public FiniteField getSubfield() {
        return this.subfield;
    }

    @Override // org.spongycastle.math.field.ExtensionField
    public int getDegree() {
        return this.minimalPolynomial.getDegree();
    }

    @Override // org.spongycastle.math.field.PolynomialExtensionField
    public Polynomial getMinimalPolynomial() {
        return this.minimalPolynomial;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GenericPolynomialExtensionField)) {
            return false;
        }
        GenericPolynomialExtensionField other = (GenericPolynomialExtensionField) obj;
        return this.subfield.equals(other.subfield) && this.minimalPolynomial.equals(other.minimalPolynomial);
    }

    public int hashCode() {
        return this.subfield.hashCode() ^ Integers.rotateLeft(this.minimalPolynomial.hashCode(), 16);
    }
}
