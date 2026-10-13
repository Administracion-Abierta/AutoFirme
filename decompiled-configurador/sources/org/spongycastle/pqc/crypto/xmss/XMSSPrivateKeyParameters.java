package org.spongycastle.pqc.crypto.xmss;

import java.io.IOException;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/xmss/XMSSPrivateKeyParameters.class */
public final class XMSSPrivateKeyParameters extends AsymmetricKeyParameter implements XMSSStoreableObjectInterface {
    private final XMSSParameters params;
    private final byte[] secretKeySeed;
    private final byte[] secretKeyPRF;
    private final byte[] publicSeed;
    private final byte[] root;
    private final BDS bdsState;

    private XMSSPrivateKeyParameters(Builder builder) {
        super(true);
        this.params = builder.params;
        if (this.params == null) {
            throw new NullPointerException("params == null");
        }
        int n = this.params.getDigestSize();
        byte[] privateKey = builder.privateKey;
        if (privateKey != null) {
            if (builder.xmss == null) {
                throw new NullPointerException("xmss == null");
            }
            int height = this.params.getHeight();
            int index = Pack.bigEndianToInt(privateKey, 0);
            if (!XMSSUtil.isIndexValid(height, index)) {
                throw new IllegalArgumentException("index out of bounds");
            }
            int position = 0 + 4;
            this.secretKeySeed = XMSSUtil.extractBytesAtOffset(privateKey, position, n);
            int position2 = position + n;
            this.secretKeyPRF = XMSSUtil.extractBytesAtOffset(privateKey, position2, n);
            int position3 = position2 + n;
            this.publicSeed = XMSSUtil.extractBytesAtOffset(privateKey, position3, n);
            int position4 = position3 + n;
            this.root = XMSSUtil.extractBytesAtOffset(privateKey, position4, n);
            int position5 = position4 + n;
            byte[] bdsStateBinary = XMSSUtil.extractBytesAtOffset(privateKey, position5, privateKey.length - position5);
            BDS bdsImport = null;
            try {
                bdsImport = (BDS) XMSSUtil.deserialize(bdsStateBinary);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e2) {
                e2.printStackTrace();
            }
            bdsImport.setXMSS(builder.xmss);
            bdsImport.validate();
            if (bdsImport.getIndex() != index) {
                throw new IllegalStateException("serialized BDS has wrong index");
            }
            this.bdsState = bdsImport;
            return;
        }
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
        BDS tmpBDSState = builder.bdsState;
        if (tmpBDSState != null) {
            this.bdsState = tmpBDSState;
        } else if (builder.index < (1 << this.params.getHeight()) - 2 && tmpPublicSeed != null && tmpSecretKeySeed != null) {
            this.bdsState = new BDS(this.params, tmpPublicSeed, tmpSecretKeySeed, (OTSHashAddress) new OTSHashAddress.Builder().build(), builder.index);
        } else {
            this.bdsState = new BDS(this.params, builder.index);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/xmss/XMSSPrivateKeyParameters$Builder.class */
    public static class Builder {
        private final XMSSParameters params;
        private int index = 0;
        private byte[] secretKeySeed = null;
        private byte[] secretKeyPRF = null;
        private byte[] publicSeed = null;
        private byte[] root = null;
        private BDS bdsState = null;
        private byte[] privateKey = null;
        private XMSSParameters xmss = null;

        public Builder(XMSSParameters params) {
            this.params = params;
        }

        public Builder withIndex(int val) {
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

        public Builder withBDSState(BDS valBDS) {
            this.bdsState = valBDS;
            return this;
        }

        public Builder withPrivateKey(byte[] privateKeyVal, XMSSParameters xmssParameters) {
            this.privateKey = XMSSUtil.cloneArray(privateKeyVal);
            this.xmss = xmssParameters;
            return this;
        }

        public XMSSPrivateKeyParameters build() {
            return new XMSSPrivateKeyParameters(this);
        }
    }

    @Override // org.spongycastle.pqc.crypto.xmss.XMSSStoreableObjectInterface
    public byte[] toByteArray() {
        int n = this.params.getDigestSize();
        int totalSize = 4 + n + n + n + n;
        byte[] out = new byte[totalSize];
        Pack.intToBigEndian(this.bdsState.getIndex(), out, 0);
        int position = 0 + 4;
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
            throw new RuntimeException("error serializing bds state: " + e.getMessage());
        }
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

    public XMSSPrivateKeyParameters getNextKey() {
        int treeHeight = this.params.getHeight();
        if (getIndex() < (1 << treeHeight) - 1) {
            return new Builder(this.params).withSecretKeySeed(this.secretKeySeed).withSecretKeyPRF(this.secretKeyPRF).withPublicSeed(this.publicSeed).withRoot(this.root).withBDSState(this.bdsState.getNextState(this.publicSeed, this.secretKeySeed, (OTSHashAddress) new OTSHashAddress.Builder().build())).build();
        }
        return new Builder(this.params).withSecretKeySeed(this.secretKeySeed).withSecretKeyPRF(this.secretKeyPRF).withPublicSeed(this.publicSeed).withRoot(this.root).withBDSState(new BDS(this.params, getIndex() + 1)).build();
    }
}
