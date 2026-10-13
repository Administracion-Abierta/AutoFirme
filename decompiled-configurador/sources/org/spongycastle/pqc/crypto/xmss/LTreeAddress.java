package org.spongycastle.pqc.crypto.xmss;

import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/xmss/LTreeAddress.class */
final class LTreeAddress extends XMSSAddress {
    private static final int TYPE = 1;
    private final int lTreeAddress;
    private final int treeHeight;
    private final int treeIndex;

    private LTreeAddress(Builder builder) {
        super(builder);
        this.lTreeAddress = builder.lTreeAddress;
        this.treeHeight = builder.treeHeight;
        this.treeIndex = builder.treeIndex;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/xmss/LTreeAddress$Builder.class */
    protected static class Builder extends XMSSAddress.Builder<Builder> {
        private int lTreeAddress;
        private int treeHeight;
        private int treeIndex;

        protected Builder() {
            super(1);
            this.lTreeAddress = 0;
            this.treeHeight = 0;
            this.treeIndex = 0;
        }

        protected Builder withLTreeAddress(int val) {
            this.lTreeAddress = val;
            return this;
        }

        protected Builder withTreeHeight(int val) {
            this.treeHeight = val;
            return this;
        }

        protected Builder withTreeIndex(int val) {
            this.treeIndex = val;
            return this;
        }

        @Override // org.spongycastle.pqc.crypto.xmss.XMSSAddress.Builder
        protected XMSSAddress build() {
            return new LTreeAddress(this);
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
        Pack.intToBigEndian(this.lTreeAddress, byteRepresentation, 16);
        Pack.intToBigEndian(this.treeHeight, byteRepresentation, 20);
        Pack.intToBigEndian(this.treeIndex, byteRepresentation, 24);
        return byteRepresentation;
    }

    protected int getLTreeAddress() {
        return this.lTreeAddress;
    }

    protected int getTreeHeight() {
        return this.treeHeight;
    }

    protected int getTreeIndex() {
        return this.treeIndex;
    }
}
