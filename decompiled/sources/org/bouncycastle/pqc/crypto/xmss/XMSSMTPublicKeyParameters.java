package org.bouncycastle.pqc.crypto.xmss;

import java.io.IOException;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/XMSSMTPublicKeyParameters.class */
public final class XMSSMTPublicKeyParameters extends XMSSMTKeyParameters implements XMSSStoreableObjectInterface, Encodable {
    private final XMSSMTParameters params;
    private final int oid;
    private final byte[] root;
    private final byte[] publicSeed;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/XMSSMTPublicKeyParameters$Builder.class */
    public static class Builder {
        private final XMSSMTParameters params;
        private byte[] root = null;
        private byte[] publicSeed = null;
        private byte[] publicKey = null;

        public Builder(XMSSMTParameters xMSSMTParameters) {
            this.params = xMSSMTParameters;
        }

        public Builder withRoot(byte[] bArr) {
            this.root = XMSSUtil.cloneArray(bArr);
            return this;
        }

        public Builder withPublicSeed(byte[] bArr) {
            this.publicSeed = XMSSUtil.cloneArray(bArr);
            return this;
        }

        public Builder withPublicKey(byte[] bArr) {
            this.publicKey = XMSSUtil.cloneArray(bArr);
            return this;
        }

        public XMSSMTPublicKeyParameters build() {
            return new XMSSMTPublicKeyParameters(this);
        }
    }

    private XMSSMTPublicKeyParameters(Builder builder) {
        super(false, builder.params.getTreeDigest());
        this.params = builder.params;
        if (this.params == null) {
            throw new NullPointerException("params == null");
        }
        int treeDigestSize = this.params.getTreeDigestSize();
        byte[] bArr = builder.publicKey;
        if (bArr != null) {
            if (bArr.length == treeDigestSize + treeDigestSize) {
                this.oid = 0;
                this.root = XMSSUtil.extractBytesAtOffset(bArr, 0, treeDigestSize);
                this.publicSeed = XMSSUtil.extractBytesAtOffset(bArr, 0 + treeDigestSize, treeDigestSize);
                return;
            } else {
                if (bArr.length != 4 + treeDigestSize + treeDigestSize) {
                    throw new IllegalArgumentException("public key has wrong size");
                }
                this.oid = Pack.bigEndianToInt(bArr, 0);
                int i = 0 + 4;
                this.root = XMSSUtil.extractBytesAtOffset(bArr, i, treeDigestSize);
                this.publicSeed = XMSSUtil.extractBytesAtOffset(bArr, i + treeDigestSize, treeDigestSize);
                return;
            }
        }
        if (this.params.getOid() != null) {
            this.oid = this.params.getOid().getOid();
        } else {
            this.oid = 0;
        }
        byte[] bArr2 = builder.root;
        if (bArr2 == null) {
            this.root = new byte[treeDigestSize];
        } else {
            if (bArr2.length != treeDigestSize) {
                throw new IllegalArgumentException("length of root must be equal to length of digest");
            }
            this.root = bArr2;
        }
        byte[] bArr3 = builder.publicSeed;
        if (bArr3 == null) {
            this.publicSeed = new byte[treeDigestSize];
        } else {
            if (bArr3.length != treeDigestSize) {
                throw new IllegalArgumentException("length of publicSeed must be equal to length of digest");
            }
            this.publicSeed = bArr3;
        }
    }

    @Override // org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return toByteArray();
    }

    @Override // org.bouncycastle.pqc.crypto.xmss.XMSSStoreableObjectInterface
    public byte[] toByteArray() {
        byte[] bArr;
        int treeDigestSize = this.params.getTreeDigestSize();
        int i = 0;
        if (this.oid != 0) {
            bArr = new byte[4 + treeDigestSize + treeDigestSize];
            Pack.intToBigEndian(this.oid, bArr, 0);
            i = 0 + 4;
        } else {
            bArr = new byte[treeDigestSize + treeDigestSize];
        }
        XMSSUtil.copyBytesAtOffset(bArr, this.root, i);
        XMSSUtil.copyBytesAtOffset(bArr, this.publicSeed, i + treeDigestSize);
        return bArr;
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
