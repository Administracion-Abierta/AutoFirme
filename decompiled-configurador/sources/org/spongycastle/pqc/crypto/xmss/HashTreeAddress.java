package org.spongycastle.pqc.crypto.xmss;

import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/xmss/HashTreeAddress.class */
final class HashTreeAddress extends XMSSAddress {
    private static final int TYPE = 2;
    private static final int PADDING = 0;
    private final int padding;
    private final int treeHeight;
    private final int treeIndex;

    private HashTreeAddress(Builder builder) {
        super(builder);
        this.padding = 0;
        this.treeHeight = builder.treeHeight;
        this.treeIndex = builder.treeIndex;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/xmss/HashTreeAddress$Builder.class */
    protected static class Builder extends XMSSAddress.Builder<Builder> {
        private int treeHeight;
        private int treeIndex;

        protected Builder() {
            super(2);
            this.treeHeight = 0;
            this.treeIndex = 0;
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
            return new HashTreeAddress(this);
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
        Pack.intToBigEndian(this.padding, byteRepresentation, 16);
        Pack.intToBigEndian(this.treeHeight, byteRepresentation, 20);
        Pack.intToBigEndian(this.treeIndex, byteRepresentation, 24);
        return byteRepresentation;
    }

    protected int getPadding() {
        return this.padding;
    }

    protected int getTreeHeight() {
        return this.treeHeight;
    }

    protected int getTreeIndex() {
        return this.treeIndex;
    }
}
