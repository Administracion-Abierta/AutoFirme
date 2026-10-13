package org.spongycastle.pqc.crypto.xmss;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/xmss/XMSSMTSignature.class */
public final class XMSSMTSignature implements XMSSStoreableObjectInterface {
    private final XMSSMTParameters params;
    private final long index;
    private final byte[] random;
    private final List<XMSSReducedSignature> reducedSignatures;

    private XMSSMTSignature(Builder builder) {
        this.params = builder.params;
        if (this.params == null) {
            throw new NullPointerException("params == null");
        }
        int n = this.params.getDigestSize();
        byte[] signature = builder.signature;
        if (signature != null) {
            int len = this.params.getWOTSPlus().getParams().getLen();
            int indexSize = (int) Math.ceil(((double) this.params.getHeight()) / 8.0d);
            int reducedSignatureSizeSingle = ((this.params.getHeight() / this.params.getLayers()) + len) * n;
            int reducedSignaturesSizeTotal = reducedSignatureSizeSingle * this.params.getLayers();
            int totalSize = indexSize + n + reducedSignaturesSizeTotal;
            if (signature.length != totalSize) {
                throw new IllegalArgumentException("signature has wrong size");
            }
            this.index = XMSSUtil.bytesToXBigEndian(signature, 0, indexSize);
            if (!XMSSUtil.isIndexValid(this.params.getHeight(), this.index)) {
                throw new IllegalArgumentException("index out of bounds");
            }
            int position = 0 + indexSize;
            this.random = XMSSUtil.extractBytesAtOffset(signature, position, n);
            this.reducedSignatures = new ArrayList();
            for (int position2 = position + n; position2 < signature.length; position2 += reducedSignatureSizeSingle) {
                XMSSReducedSignature xmssSig = new XMSSReducedSignature.Builder(this.params.getXMSSParameters()).withReducedSignature(XMSSUtil.extractBytesAtOffset(signature, position2, reducedSignatureSizeSingle)).build();
                this.reducedSignatures.add(xmssSig);
            }
            return;
        }
        this.index = builder.index;
        byte[] tmpRandom = builder.random;
        if (tmpRandom != null) {
            if (tmpRandom.length != n) {
                throw new IllegalArgumentException("size of random needs to be equal to size of digest");
            }
            this.random = tmpRandom;
        } else {
            this.random = new byte[n];
        }
        List<XMSSReducedSignature> tmpReducedSignatures = builder.reducedSignatures;
        if (tmpReducedSignatures != null) {
            this.reducedSignatures = tmpReducedSignatures;
        } else {
            this.reducedSignatures = new ArrayList();
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/xmss/XMSSMTSignature$Builder.class */
    public static class Builder {
        private final XMSSMTParameters params;
        private long index = 0;
        private byte[] random = null;
        private List<XMSSReducedSignature> reducedSignatures = null;
        private byte[] signature = null;

        public Builder(XMSSMTParameters params) {
            this.params = params;
        }

        public Builder withIndex(long val) {
            this.index = val;
            return this;
        }

        public Builder withRandom(byte[] val) {
            this.random = XMSSUtil.cloneArray(val);
            return this;
        }

        public Builder withReducedSignatures(List<XMSSReducedSignature> val) {
            this.reducedSignatures = val;
            return this;
        }

        public Builder withSignature(byte[] val) {
            this.signature = val;
            return this;
        }

        public XMSSMTSignature build() {
            return new XMSSMTSignature(this);
        }
    }

    @Override // org.spongycastle.pqc.crypto.xmss.XMSSStoreableObjectInterface
    public byte[] toByteArray() {
        int n = this.params.getDigestSize();
        int len = this.params.getWOTSPlus().getParams().getLen();
        int indexSize = (int) Math.ceil(((double) this.params.getHeight()) / 8.0d);
        int reducedSignatureSizeSingle = ((this.params.getHeight() / this.params.getLayers()) + len) * n;
        int reducedSignaturesSizeTotal = reducedSignatureSizeSingle * this.params.getLayers();
        int totalSize = indexSize + n + reducedSignaturesSizeTotal;
        byte[] out = new byte[totalSize];
        byte[] indexBytes = XMSSUtil.toBytesBigEndian(this.index, indexSize);
        XMSSUtil.copyBytesAtOffset(out, indexBytes, 0);
        int position = 0 + indexSize;
        XMSSUtil.copyBytesAtOffset(out, this.random, position);
        int position2 = position + n;
        for (XMSSReducedSignature reducedSignature : this.reducedSignatures) {
            byte[] signature = reducedSignature.toByteArray();
            XMSSUtil.copyBytesAtOffset(out, signature, position2);
            position2 += reducedSignatureSizeSingle;
        }
        return out;
    }

    public long getIndex() {
        return this.index;
    }

    public byte[] getRandom() {
        return XMSSUtil.cloneArray(this.random);
    }

    public List<XMSSReducedSignature> getReducedSignatures() {
        return this.reducedSignatures;
    }
}
