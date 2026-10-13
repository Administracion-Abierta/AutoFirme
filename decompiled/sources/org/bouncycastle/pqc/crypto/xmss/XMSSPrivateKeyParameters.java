package org.bouncycastle.pqc.crypto.xmss;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/XMSSPrivateKeyParameters.class */
public final class XMSSPrivateKeyParameters extends XMSSKeyParameters implements XMSSStoreableObjectInterface, Encodable {
    private final XMSSParameters params;
    private final byte[] secretKeySeed;
    private final byte[] secretKeyPRF;
    private final byte[] publicSeed;
    private final byte[] root;
    private volatile BDS bdsState;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/XMSSPrivateKeyParameters$Builder.class */
    public static class Builder {
        private final XMSSParameters params;
        private int index = 0;
        private int maxIndex = -1;
        private byte[] secretKeySeed = null;
        private byte[] secretKeyPRF = null;
        private byte[] publicSeed = null;
        private byte[] root = null;
        private BDS bdsState = null;
        private byte[] privateKey = null;

        public Builder(XMSSParameters xMSSParameters) {
            this.params = xMSSParameters;
        }

        public Builder withIndex(int i) {
            this.index = i;
            return this;
        }

        public Builder withMaxIndex(int i) {
            this.maxIndex = i;
            return this;
        }

        public Builder withSecretKeySeed(byte[] bArr) {
            this.secretKeySeed = XMSSUtil.cloneArray(bArr);
            return this;
        }

        public Builder withSecretKeyPRF(byte[] bArr) {
            this.secretKeyPRF = XMSSUtil.cloneArray(bArr);
            return this;
        }

        public Builder withPublicSeed(byte[] bArr) {
            this.publicSeed = XMSSUtil.cloneArray(bArr);
            return this;
        }

        public Builder withRoot(byte[] bArr) {
            this.root = XMSSUtil.cloneArray(bArr);
            return this;
        }

        public Builder withBDSState(BDS bds) {
            this.bdsState = bds;
            return this;
        }

        public Builder withPrivateKey(byte[] bArr) {
            this.privateKey = XMSSUtil.cloneArray(bArr);
            return this;
        }

        public XMSSPrivateKeyParameters build() {
            return new XMSSPrivateKeyParameters(this);
        }
    }

    private XMSSPrivateKeyParameters(Builder builder) {
        super(true, builder.params.getTreeDigest());
        this.params = builder.params;
        if (this.params == null) {
            throw new NullPointerException("params == null");
        }
        int treeDigestSize = this.params.getTreeDigestSize();
        byte[] bArr = builder.privateKey;
        if (bArr != null) {
            int height = this.params.getHeight();
            int iBigEndianToInt = Pack.bigEndianToInt(bArr, 0);
            if (!XMSSUtil.isIndexValid(height, iBigEndianToInt)) {
                throw new IllegalArgumentException("index out of bounds");
            }
            int i = 0 + 4;
            this.secretKeySeed = XMSSUtil.extractBytesAtOffset(bArr, i, treeDigestSize);
            int i2 = i + treeDigestSize;
            this.secretKeyPRF = XMSSUtil.extractBytesAtOffset(bArr, i2, treeDigestSize);
            int i3 = i2 + treeDigestSize;
            this.publicSeed = XMSSUtil.extractBytesAtOffset(bArr, i3, treeDigestSize);
            int i4 = i3 + treeDigestSize;
            this.root = XMSSUtil.extractBytesAtOffset(bArr, i4, treeDigestSize);
            int i5 = i4 + treeDigestSize;
            try {
                BDS bds = (BDS) XMSSUtil.deserialize(XMSSUtil.extractBytesAtOffset(bArr, i5, bArr.length - i5), BDS.class);
                if (bds.getIndex() != iBigEndianToInt) {
                    throw new IllegalStateException("serialized BDS has wrong index");
                }
                this.bdsState = bds.withWOTSDigest(builder.params.getTreeDigestOID());
                return;
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            } catch (ClassNotFoundException e2) {
                throw new IllegalArgumentException(e2.getMessage(), e2);
            }
        }
        byte[] bArr2 = builder.secretKeySeed;
        if (bArr2 == null) {
            this.secretKeySeed = new byte[treeDigestSize];
        } else {
            if (bArr2.length != treeDigestSize) {
                throw new IllegalArgumentException("size of secretKeySeed needs to be equal size of digest");
            }
            this.secretKeySeed = bArr2;
        }
        byte[] bArr3 = builder.secretKeyPRF;
        if (bArr3 == null) {
            this.secretKeyPRF = new byte[treeDigestSize];
        } else {
            if (bArr3.length != treeDigestSize) {
                throw new IllegalArgumentException("size of secretKeyPRF needs to be equal size of digest");
            }
            this.secretKeyPRF = bArr3;
        }
        byte[] bArr4 = builder.publicSeed;
        if (bArr4 == null) {
            this.publicSeed = new byte[treeDigestSize];
        } else {
            if (bArr4.length != treeDigestSize) {
                throw new IllegalArgumentException("size of publicSeed needs to be equal size of digest");
            }
            this.publicSeed = bArr4;
        }
        byte[] bArr5 = builder.root;
        if (bArr5 == null) {
            this.root = new byte[treeDigestSize];
        } else {
            if (bArr5.length != treeDigestSize) {
                throw new IllegalArgumentException("size of root needs to be equal size of digest");
            }
            this.root = bArr5;
        }
        BDS bds2 = builder.bdsState;
        if (bds2 != null) {
            this.bdsState = bds2;
        } else if (builder.index >= (1 << this.params.getHeight()) - 2 || bArr4 == null || bArr2 == null) {
            this.bdsState = new BDS(this.params, (1 << this.params.getHeight()) - 1, builder.index);
        } else {
            this.bdsState = new BDS(this.params, bArr4, bArr2, (OTSHashAddress) new OTSHashAddress.Builder().build(), builder.index);
        }
        if (builder.maxIndex >= 0 && builder.maxIndex != this.bdsState.getMaxIndex()) {
            throw new IllegalArgumentException("maxIndex set but not reflected in state");
        }
    }

    public long getUsagesRemaining() {
        long maxIndex;
        synchronized (this) {
            maxIndex = (this.bdsState.getMaxIndex() - getIndex()) + 1;
        }
        return maxIndex;
    }

    @Override // org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        byte[] byteArray;
        synchronized (this) {
            byteArray = toByteArray();
        }
        return byteArray;
    }

    XMSSPrivateKeyParameters rollKey() {
        synchronized (this) {
            if (this.bdsState.getIndex() < this.bdsState.getMaxIndex()) {
                this.bdsState = this.bdsState.getNextState(this.publicSeed, this.secretKeySeed, (OTSHashAddress) new OTSHashAddress.Builder().build());
            } else {
                this.bdsState = new BDS(this.params, this.bdsState.getMaxIndex(), this.bdsState.getMaxIndex() + 1);
            }
        }
        return this;
    }

    public XMSSPrivateKeyParameters getNextKey() {
        XMSSPrivateKeyParameters xMSSPrivateKeyParametersExtractKeyShard;
        synchronized (this) {
            xMSSPrivateKeyParametersExtractKeyShard = extractKeyShard(1);
        }
        return xMSSPrivateKeyParametersExtractKeyShard;
    }

    public XMSSPrivateKeyParameters extractKeyShard(int i) {
        XMSSPrivateKeyParameters xMSSPrivateKeyParametersBuild;
        if (i < 1) {
            throw new IllegalArgumentException("cannot ask for a shard with 0 keys");
        }
        synchronized (this) {
            if (i > getUsagesRemaining()) {
                throw new IllegalArgumentException("usageCount exceeds usages remaining");
            }
            xMSSPrivateKeyParametersBuild = new Builder(this.params).withSecretKeySeed(this.secretKeySeed).withSecretKeyPRF(this.secretKeyPRF).withPublicSeed(this.publicSeed).withRoot(this.root).withIndex(getIndex()).withBDSState(this.bdsState.withMaxIndex((this.bdsState.getIndex() + i) - 1, this.params.getTreeDigestOID())).build();
            if (i == getUsagesRemaining()) {
                this.bdsState = new BDS(this.params, this.bdsState.getMaxIndex(), getIndex() + i);
            } else {
                OTSHashAddress oTSHashAddress = (OTSHashAddress) new OTSHashAddress.Builder().build();
                for (int i2 = 0; i2 != i; i2++) {
                    this.bdsState = this.bdsState.getNextState(this.publicSeed, this.secretKeySeed, oTSHashAddress);
                }
            }
        }
        return xMSSPrivateKeyParametersBuild;
    }

    @Override // org.bouncycastle.pqc.crypto.xmss.XMSSStoreableObjectInterface
    public byte[] toByteArray() {
        byte[] bArrConcatenate;
        synchronized (this) {
            int treeDigestSize = this.params.getTreeDigestSize();
            byte[] bArr = new byte[4 + treeDigestSize + treeDigestSize + treeDigestSize + treeDigestSize];
            Pack.intToBigEndian(this.bdsState.getIndex(), bArr, 0);
            int i = 0 + 4;
            XMSSUtil.copyBytesAtOffset(bArr, this.secretKeySeed, i);
            int i2 = i + treeDigestSize;
            XMSSUtil.copyBytesAtOffset(bArr, this.secretKeyPRF, i2);
            int i3 = i2 + treeDigestSize;
            XMSSUtil.copyBytesAtOffset(bArr, this.publicSeed, i3);
            XMSSUtil.copyBytesAtOffset(bArr, this.root, i3 + treeDigestSize);
            try {
                bArrConcatenate = Arrays.concatenate(bArr, XMSSUtil.serialize(this.bdsState));
            } catch (IOException e) {
                throw new RuntimeException("error serializing bds state: " + e.getMessage());
            }
        }
        return bArrConcatenate;
    }

    public int getIndex() {
        return this.bdsState.getIndex();
    }

    public byte[] getSecretKeySeed() {
        return XMSSUtil.cloneArray(this.secretKeySeed);
    }

    public byte[] getSecretKeyPRF() {
        return XMSSUtil.cloneArray(this.secretKeyPRF);
    }

    public byte[] getPublicSeed() {
        return XMSSUtil.cloneArray(this.publicSeed);
    }

    public byte[] getRoot() {
        return XMSSUtil.cloneArray(this.root);
    }

    BDS getBDSState() {
        return this.bdsState;
    }

    public XMSSParameters getParameters() {
        return this.params;
    }
}
