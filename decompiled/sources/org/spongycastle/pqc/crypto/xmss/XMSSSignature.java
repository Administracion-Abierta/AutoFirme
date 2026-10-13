package org.spongycastle.pqc.crypto.xmss;

import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/xmss/XMSSSignature.class */
public final class XMSSSignature extends XMSSReducedSignature implements XMSSStoreableObjectInterface {
    private final int index;
    private final byte[] random;

    private XMSSSignature(Builder builder) {
        super(builder);
        this.index = builder.index;
        int n = getParams().getDigestSize();
        byte[] tmpRandom = builder.random;
        if (tmpRandom != null) {
            if (tmpRandom.length != n) {
                throw new IllegalArgumentException("size of random needs to be equal to size of digest");
            }
            this.random = tmpRandom;
            return;
        }
        this.random = new byte[n];
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/xmss/XMSSSignature$Builder.class */
    public static class Builder extends XMSSReducedSignature.Builder {
        private final XMSSParameters params;
        private int index;
        private byte[] random;

        public Builder(XMSSParameters params) {
            super(params);
            this.index = 0;
            this.random = null;
            this.params = params;
        }

        public Builder withIndex(int val) {
            this.index = val;
            return this;
        }

        public Builder withRandom(byte[] val) {
            this.random = XMSSUtil.cloneArray(val);
            return this;
        }

        public Builder withSignature(byte[] val) {
            if (val == null) {
                throw new NullPointerException("signature == null");
            }
            int n = this.params.getDigestSize();
            int len = this.params.getWOTSPlus().getParams().getLen();
            int height = this.params.getHeight();
            int signatureSize = len * n;
            int authPathSize = height * n;
            this.index = Pack.bigEndianToInt(val, 0);
            int position = 0 + 4;
            this.random = XMSSUtil.extractBytesAtOffset(val, position, n);
            withReducedSignature(XMSSUtil.extractBytesAtOffset(val, position + n, signatureSize + authPathSize));
            return this;
        }

        @Override // org.spongycastle.pqc.crypto.xmss.XMSSReducedSignature.Builder
        public XMSSSignature build() {
            return new XMSSSignature(this);
        }
    }

    @Override // org.spongycastle.pqc.crypto.xmss.XMSSReducedSignature, org.spongycastle.pqc.crypto.xmss.XMSSStoreableObjectInterface
    public byte[] toByteArray() {
        int n = getParams().getDigestSize();
        int signatureSize = getParams().getWOTSPlus().getParams().getLen() * n;
        int authPathSize = getParams().getHeight() * n;
        int totalSize = 4 + n + signatureSize + authPathSize;
        byte[] out = new byte[totalSize];
        Pack.intToBigEndian(this.index, out, 0);
        int position = 0 + 4;
        XMSSUtil.copyBytesAtOffset(out, this.random, position);
        int position2 = position + n;
        byte[][] signature = getWOTSPlusSignature().toByteArray();
        for (byte[] bArr : signature) {
            XMSSUtil.copyBytesAtOffset(out, bArr, position2);
            position2 += n;
        }
        for (int i = 0; i < getAuthPath().size(); i++) {
            byte[] value = getAuthPath().get(i).getValue();
            XMSSUtil.copyBytesAtOffset(out, value, position2);
            position2 += n;
        }
        return out;
    }

    public int getIndex() {
        return this.index;
    }

    public byte[] getRandom() {
        return XMSSUtil.cloneArray(this.random);
    }
}
