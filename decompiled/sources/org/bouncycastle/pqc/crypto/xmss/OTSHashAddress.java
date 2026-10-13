package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/OTSHashAddress.class */
final class OTSHashAddress extends XMSSAddress {
    private static final int TYPE = 0;
    private final int otsAddress;
    private final int chainAddress;
    private final int hashAddress;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/OTSHashAddress$Builder.class */
    protected static class Builder extends XMSSAddress.Builder<Builder> {
        private int otsAddress;
        private int chainAddress;
        private int hashAddress;

        protected Builder() {
            super(0);
            this.otsAddress = 0;
            this.chainAddress = 0;
            this.hashAddress = 0;
        }

        protected Builder withOTSAddress(int i) {
            this.otsAddress = i;
            return this;
        }

        protected Builder withChainAddress(int i) {
            this.chainAddress = i;
            return this;
        }

        protected Builder withHashAddress(int i) {
            this.hashAddress = i;
            return this;
        }

        @Override // org.bouncycastle.pqc.crypto.xmss.XMSSAddress.Builder
        protected XMSSAddress build() {
            return new OTSHashAddress(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.bouncycastle.pqc.crypto.xmss.XMSSAddress.Builder
        public Builder getThis() {
            return this;
        }
    }

    private OTSHashAddress(Builder builder) {
        super(builder);
        this.otsAddress = builder.otsAddress;
        this.chainAddress = builder.chainAddress;
        this.hashAddress = builder.hashAddress;
    }

    @Override // org.bouncycastle.pqc.crypto.xmss.XMSSAddress
    protected byte[] toByteArray() {
        byte[] byteArray = super.toByteArray();
        Pack.intToBigEndian(this.otsAddress, byteArray, 16);
        Pack.intToBigEndian(this.chainAddress, byteArray, 20);
        Pack.intToBigEndian(this.hashAddress, byteArray, 24);
        return byteArray;
    }

    protected int getOTSAddress() {
        return this.otsAddress;
    }

    protected int getChainAddress() {
        return this.chainAddress;
    }

    protected int getHashAddress() {
        return this.hashAddress;
    }
}
