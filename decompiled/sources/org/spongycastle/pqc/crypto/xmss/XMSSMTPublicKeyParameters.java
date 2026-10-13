package org.spongycastle.pqc.crypto.xmss;

import org.spongycastle.crypto.params.AsymmetricKeyParameter;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/xmss/XMSSMTPublicKeyParameters.class */
public final class XMSSMTPublicKeyParameters extends AsymmetricKeyParameter implements XMSSStoreableObjectInterface {
    private final XMSSMTParameters params;
    private final byte[] root;
    private final byte[] publicSeed;

    private XMSSMTPublicKeyParameters(Builder builder) {
        super(false);
        this.params = builder.params;
        if (this.params == null) {
            throw new NullPointerException("params == null");
        }
        int n = this.params.getDigestSize();
        byte[] publicKey = builder.publicKey;
        if (publicKey != null) {
            int totalSize = n + n;
            if (publicKey.length != totalSize) {
                throw new IllegalArgumentException("public key has wrong size");
            }
            this.root = XMSSUtil.extractBytesAtOffset(publicKey, 0, n);
            int position = 0 + n;
            this.publicSeed = XMSSUtil.extractBytesAtOffset(publicKey, position, n);
            return;
        }
        byte[] tmpRoot = builder.root;
        if (tmpRoot != null) {
            if (tmpRoot.length != n) {
                throw new IllegalArgumentException("length of root must be equal to length of digest");
            }
            this.root = tmpRoot;
        } else {
            this.root = new byte[n];
        }
        byte[] tmpPublicSeed = builder.publicSeed;
        if (tmpPublicSeed != null) {
            if (tmpPublicSeed.length != n) {
                throw new IllegalArgumentException("length of publicSeed must be equal to length of digest");
            }
            this.publicSeed = tmpPublicSeed;
            return;
        }
        this.publicSeed = new byte[n];
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/xmss/XMSSMTPublicKeyParameters$Builder.class */
    public static class Builder {
        private final XMSSMTParameters params;
        private byte[] root = null;
        private byte[] publicSeed = null;
        private byte[] publicKey = null;

        public Builder(XMSSMTParameters params) {
            this.params = params;
        }

        public Builder withRoot(byte[] val) {
            this.root = XMSSUtil.cloneArray(val);
            return this;
        }

        public Builder withPublicSeed(byte[] val) {
            this.publicSeed = XMSSUtil.cloneArray(val);
            return this;
        }

        public Builder withPublicKey(byte[] val) {
            this.publicKey = XMSSUtil.cloneArray(val);
            return this;
        }

        public XMSSMTPublicKeyParameters build() {
            return new XMSSMTPublicKeyParameters(this);
        }
    }

    @Override // org.spongycastle.pqc.crypto.xmss.XMSSStoreableObjectInterface
    public byte[] toByteArray() {
        int n = this.params.getDigestSize();
        int totalSize = n + n;
        byte[] out = new byte[totalSize];
        XMSSUtil.copyBytesAtOffset(out, this.root, 0);
        int position = 0 + n;
        XMSSUtil.copyBytesAtOffset(out, this.publicSeed, position);
        return out;
    }

    public byte[] getRoot() {
        return XMSSUtil.cloneArray(this.root);
    }

    public byte[] getPublicSeed() {
        return XMSSUtil.cloneArray(this.publicSeed);
    }

    public XMSSMTParameters getParameters() {
        return this.params;
    }
}
