package org.bouncycastle.pqc.crypto.xmss;

import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/HashTreeAddress.class */
final class HashTreeAddress extends XMSSAddress {
    private static final int TYPE = 2;
    private static final int PADDING = 0;
    private final int padding;
    private final int treeHeight;
    private final int treeIndex;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/HashTreeAddress$Builder.class */
    protected static class Builder extends XMSSAddress.Builder<Builder> {
        private int treeHeight;
        private int treeIndex;

        protected Builder() {
            super(2);
            this.treeHeight = 0;
            this.treeIndex = 0;
        }

        protected Builder withTreeHeight(int i) {
            this.treeHeight = i;
            return this;
        }

        protected Builder withTreeIndex(int i) {
            this.treeIndex = i;
            return this;
        }

        @Override // org.bouncycastle.pqc.crypto.xmss.XMSSAddress.Builder
        protected XMSSAddress build() {
            return new HashTreeAddress(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.bouncycastle.pqc.crypto.xmss.XMSSAddress.Builder
        public Builder getThis() {
            return this;
        }
    }

    private HashTreeAddress(Builder builder) {
        super(builder);
        this.padding = 0;
        this.treeHeight = builder.treeHeight;
        this.treeIndex = builder.treeIndex;
    }

    @Override // org.bouncycastle.pqc.crypto.xmss.XMSSAddress
    protected byte[] toByteArray() {
        byte[] byteArray = super.toByteArray();
        Pack.intToBigEndian(this.padding, byteArray, 16);
        Pack.intToBigEndian(this.treeHeight, byteArray, 20);
        Pack.intToBigEndian(this.treeIndex, byteArray, 24);
        return byteArray;
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
