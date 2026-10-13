package org.bouncycastle.pkix.util.filter;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkix/util/filter/UntrustedInput.class */
public class UntrustedInput {
    protected Object input;

    public UntrustedInput(Object obj) {
        this.input = obj;
    }

    public Object getInput() {
        return this.input;
    }

    public String getString() {
        return this.input.toString();
    }

    public String toString() {
        return this.input.toString();
    }
}
