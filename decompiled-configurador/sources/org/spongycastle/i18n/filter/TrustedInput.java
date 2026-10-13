package org.spongycastle.i18n.filter;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/i18n/filter/TrustedInput.class */
public class TrustedInput {
    protected Object input;

    public TrustedInput(Object input) {
        this.input = input;
    }

    public Object getInput() {
        return this.input;
    }

    public String toString() {
        return this.input.toString();
    }
}
