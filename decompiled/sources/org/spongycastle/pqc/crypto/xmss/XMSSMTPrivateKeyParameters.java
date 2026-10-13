package org.spongycastle.pqc.crypto.xmss;

import java.io.IOException;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/xmss/XMSSMTPrivateKeyParameters.class */
public final class XMSSMTPrivateKeyParameters extends AsymmetricKeyParameter implements XMSSStoreableObjectInterface {
    private final XMSSMTParameters params;
    private final long index;
    private final byte[] secretKeySeed;
    private final byte[] secretKeyPRF;
    private final byte[] publicSeed;
    private final byte[] root;
    private final BDSStateMap bdsState;

    private XMSSMTPrivateKeyParameters(Builder builder) {
        super(true);
        this.params = builder.params;
        if (this.params == null) {
            throw new NullPointerException("params == null");
        }
        int n = this.params.getDigestSize();
        byte[] privateKey = builder.privateKey;
        if (privateKey == null) {
            this.index = builder.index;
            byte[] tmpSecretKeySeed = builder.secretKeySeed;
            if (tmpSecretKeySeed != null) {
                if (tmpSecretKeySeed.length != n) {
                    throw new IllegalArgumentException("size of secretKeySeed needs to be equal size of digest");
                }
                this.secretKeySeed = tmpSecretKeySeed;
            } else {
                this.secretKeySeed = new byte[n];
            }
            byte[] tmpSecretKeyPRF = builder.secretKeyPRF;
            if (tmpSecretKeyPRF != null) {
                if (tmpSecretKeyPRF.length != n) {
                    throw new IllegalArgumentException("size of secretKeyPRF needs to be equal size of digest");
                }
                this.secretKeyPRF = tmpSecretKeyPRF;
            } else {
                this.secretKeyPRF = new byte[n];
            }
            byte[] tmpPublicSeed = builder.publicSeed;
            if (tmpPublicSeed != null) {
                if (tmpPublicSeed.length != n) {
                    throw new IllegalArgumentException("size of publicSeed needs to be equal size of digest");
                }
                this.publicSeed = tmpPublicSeed;
            } else {
                this.publicSeed = new byte[n];
            }
            byte[] tmpRoot = builder.root;
            if (tmpRoot != null) {
                if (tmpRoot.length != n) {
                    throw new IllegalArgumentException("size of root needs to be equal size of digest");
                }
                this.root = tmpRoot;
            } else {
                this.root = new byte[n];
            }
            BDSStateMap tmpBDSState = builder.bdsState;
            if (tmpBDSState != null) {
                this.bdsState = tmpBDSState;
                return;
            }
            long globalIndex = builder.index;
            if (XMSSUtil.isIndexValid(this.params.getHeight(), globalIndex) && tmpPublicSeed != null && tmpSecretKeySeed != null) {
                this.bdsState = new BDSStateMap(this.params, builder.index, tmpPublicSeed, tmpSecretKeySeed);
                return;
            } else {
                this.bdsState = new BDSStateMap();
                return;
            }
        }
        if (builder.xmss == null) {
            throw new NullPointerException("xmss == null");
        }
        int totalHeight = this.params.getHeight();
        int indexSize = (totalHeight + 7) / 8;
        this.index = XMSSUtil.bytesToXBigEndian(privateKey, 0, indexSize);
        if (!XMSSUtil.isIndexValid(totalHeight, this.index)) {
            throw new IllegalArgumentException("index out of bounds");
        }
        int position = 0 + indexSize;
        this.secretKeySeed = XMSSUtil.extractBytesAtOffset(privateKey, position, n);
        int position2 = position + n;
        this.secretKeyPRF = XMSSUtil.extractBytesAtOffset(privateKey, position2, n);
        int position3 = position2 + n;
        this.publicSeed = XMSSUtil.extractBytesAtOffset(privateKey, position3, n);
        int position4 = position3 + n;
        this.root = XMSSUtil.extractBytesAtOffset(privateKey, position4, n);
        int position5 = position4 + n;
        byte[] bdsStateBinary = XMSSUtil.extractBytesAtOffset(privateKey, position5, privateKey.length - position5);
        BDSStateMap bdsImport = null;
        try {
            bdsImport = (BDSStateMap) XMSSUtil.deserialize(bdsStateBinary);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e2) {
            e2.printStackTrace();
        }
        bdsImport.setXMSS(builder.xmss);
        this.bdsState = bdsImport;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/xmss/XMSSMTPrivateKeyParameters$Builder.class */
    public static class Builder {
        private final XMSSMTParameters params;
        private long index = 0;
        private byte[] secretKeySeed = null;
        private byte[] secretKeyPRF = null;
        private byte[] publicSeed = null;
        private byte[] root = null;
        private BDSStateMap bdsState = null;
        private byte[] privateKey = null;
        private XMSSParameters xmss = null;

        public Builder(XMSSMTParameters params) {
            this.params = params;
        }

        public Builder withIndex(long val) {
            this.index = val;
            return this;
        }

        public Builder withSecretKeySeed(byte[] val) {
            this.secretKeySeed = XMSSUtil.cloneArray(val);
            return this;
        }

        public Builder withSecretKeyPRF(byte[] val) {
            this.secretKeyPRF = XMSSUtil.cloneArray(val);
            return this;
        }

        public Builder withPublicSeed(byte[] val) {
            this.publicSeed = XMSSUtil.cloneArray(val);
            return this;
        }

        public Builder withRoot(byte[] val) {
            this.root = XMSSUtil.cloneArray(val);
            return this;
        }

        public Builder withBDSState(BDSStateMap val) {
            this.bdsState = val;
            return this;
        }

        public Builder withPrivateKey(byte[] privateKeyVal, XMSSParameters xmssVal) {
            this.privateKey = XMSSUtil.cloneArray(privateKeyVal);
            this.xmss = xmssVal;
            return this;
        }

        public XMSSMTPrivateKeyParameters build() {
            return new XMSSMTPrivateKeyParameters(this);
        }
    }

    @Override // org.spongycastle.pqc.crypto.xmss.XMSSStoreableObjectInterface
    public byte[] toByteArray() {
        int n = this.params.getDigestSize();
        int indexSize = (this.params.getHeight() + 7) / 8;
        int totalSize = indexSize + n + n + n + n;
        byte[] out = new byte[totalSize];
        byte[] indexBytes = XMSSUtil.toBytesBigEndian(this.index, indexSize);
        XMSSUtil.copyBytesAtOffset(out, indexBytes, 0);
        int position = 0 + indexSize;
        XMSSUtil.copyBytesAtOffset(out, this.secretKeySeed, position);
        int position2 = position + n;
        XMSSUtil.copyBytesAtOffset(out, this.secretKeyPRF, position2);
        int position3 = position2 + n;
        XMSSUtil.copyBytesAtOffset(out, this.publicSeed, position3);
        XMSSUtil.copyBytesAtOffset(out, this.root, position3 + n);
        try {
            byte[] bdsStateOut = XMSSUtil.serialize(this.bdsState);
            return Arrays.concatenate(out, bdsStateOut);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("error serializing bds state");
        }
    }

    public long getIndex() {
        return this.index;
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

    BDSStateMap getBDSState() {
        return this.bdsState;
    }

    public XMSSMTParameters getParameters() {
        return this.params;
    }

    public XMSSMTPrivateKeyParameters getNextKey() {
        BDSStateMap newState = new BDSStateMap(this.bdsState, this.params, getIndex(), this.publicSeed, this.secretKeySeed);
        return new Builder(this.params).withIndex(this.index + 1).withSecretKeySeed(this.secretKeySeed).withSecretKeyPRF(this.secretKeyPRF).withPublicSeed(this.publicSeed).withRoot(this.root).withBDSState(newState).build();
    }
}
