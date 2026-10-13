package org.bouncycastle.oer;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/DeferredElementSupplier.class */
public class DeferredElementSupplier implements ElementSupplier {
    private final OERDefinition.Builder src;
    private Element buildProduct;

    public DeferredElementSupplier(OERDefinition.Builder builder) {
        this.src = builder;
    }

    @Override // org.bouncycastle.oer.ElementSupplier
    public Element build() {
        Element element;
        synchronized (this) {
            if (this.buildProduct == null) {
                this.buildProduct = this.src.build();
            }
            element = this.buildProduct;
        }
        return element;
    }
}
