package org.spongycastle.pqc.crypto.xmss;

import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/xmss/XMSSAddress.class */
public abstract class XMSSAddress {
    private final int layerAddress;
    private final long treeAddress;
    private final int type;
    private final int keyAndMask;

    protected XMSSAddress(Builder builder) {
        this.layerAddress = builder.layerAddress;
        this.treeAddress = builder.treeAddress;
        this.type = builder.type;
        this.keyAndMask = builder.keyAndMask;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/xmss/XMSSAddress$Builder.class */
    protected static abstract class Builder<T extends Builder> {
        private final int type;
        private int layerAddress = 0;
        private long treeAddress = 0;
        private int keyAndMask = 0;

        protected abstract XMSSAddress build();

        protected abstract T getThis();

        protected Builder(int type) {
            this.type = type;
        }

        protected T withLayerAddress(int i) {
            this.layerAddress = i;
            return (T) getThis();
        }

        protected T withTreeAddress(long j) {
            this.treeAddress = j;
            return (T) getThis();
        }

        protected T withKeyAndMask(int i) {
            this.keyAndMask = i;
            return (T) getThis();
        }
    }

    protected byte[] toByteArray() {
        byte[] byteRepresentation = new byte[32];
        Pack.intToBigEndian(this.layerAddress, byteRepresentation, 0);
        Pack.longToBigEndian(this.treeAddress, byteRepresentation, 4);
        Pack.intToBigEndian(this.type, byteRepresentation, 12);
        Pack.intToBigEndian(this.keyAndMask, byteRepresentation, 28);
        return byteRepresentation;
    }

    protected final int getLayerAddress() {
        return this.layerAddress;
    }

    protected final long getTreeAddress() {
        return this.treeAddress;
    }

    public final int getType() {
        return this.type;
    }

    public final int getKeyAndMask() {
        return this.keyAndMask;
    }
}
