package org.spongycastle.pqc.crypto.xmss;

import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/xmss/OTSHashAddress.class */
final class OTSHashAddress extends XMSSAddress {
    private static final int TYPE = 0;
    private final int otsAddress;
    private final int chainAddress;
    private final int hashAddress;

    private OTSHashAddress(Builder builder) {
        super(builder);
        this.otsAddress = builder.otsAddress;
        this.chainAddress = builder.chainAddress;
        this.hashAddress = builder.hashAddress;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/xmss/OTSHashAddress$Builder.class */
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

        protected Builder withOTSAddress(int val) {
            this.otsAddress = val;
            return this;
        }

        protected Builder withChainAddress(int val) {
            this.chainAddress = val;
            return this;
        }

        protected Builder withHashAddress(int val) {
            this.hashAddress = val;
            return this;
        }

        @Override // org.spongycastle.pqc.crypto.xmss.XMSSAddress.Builder
        protected XMSSAddress build() {
            return new OTSHashAddress(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.spongycastle.pqc.crypto.xmss.XMSSAddress.Builder
        public Builder getThis() {
            return this;
        }
    }

    @Override // org.spongycastle.pqc.crypto.xmss.XMSSAddress
    protected byte[] toByteArray() {
        byte[] byteRepresentation = super.toByteArray();
        Pack.intToBigEndian(this.otsAddress, byteRepresentation, 16);
        Pack.intToBigEndian(this.chainAddress, byteRepresentation, 20);
        Pack.intToBigEndian(this.hashAddress, byteRepresentation, 24);
        return byteRepresentation;
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
