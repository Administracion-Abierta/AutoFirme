package org.bouncycastle.pqc.crypto.xmss;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Encodable;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/XMSSMTSignature.class */
public final class XMSSMTSignature implements XMSSStoreableObjectInterface, Encodable {
    private final XMSSMTParameters params;
    private final long index;
    private final byte[] random;
    private final List<XMSSReducedSignature> reducedSignatures;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xmss/XMSSMTSignature$Builder.class */
    public static class Builder {
        private final XMSSMTParameters params;
        private long index = 0;
        private byte[] random = null;
        private List<XMSSReducedSignature> reducedSignatures = null;
        private byte[] signature = null;

        public Builder(XMSSMTParameters xMSSMTParameters) {
            this.params = xMSSMTParameters;
        }

        public Builder withIndex(long j) {
            this.index = j;
            return this;
        }

        public Builder withRandom(byte[] bArr) {
            this.random = XMSSUtil.cloneArray(bArr);
            return this;
        }

        public Builder withReducedSignatures(List<XMSSReducedSignature> list) {
            this.reducedSignatures = list;
            return this;
        }

        public Builder withSignature(byte[] bArr) {
            this.signature = Arrays.clone(bArr);
            return this;
        }

        public XMSSMTSignature build() {
            return new XMSSMTSignature(this);
        }
    }

    private XMSSMTSignature(Builder builder) {
        this.params = builder.params;
        if (this.params == null) {
            throw new NullPointerException("params == null");
        }
        int treeDigestSize = this.params.getTreeDigestSize();
        byte[] bArr = builder.signature;
        if (bArr == null) {
            this.index = builder.index;
            byte[] bArr2 = builder.random;
            if (bArr2 == null) {
                this.random = new byte[treeDigestSize];
            } else {
                if (bArr2.length != treeDigestSize) {
                    throw new IllegalArgumentException("size of random needs to be equal to size of digest");
                }
                this.random = bArr2;
            }
            List<XMSSReducedSignature> list = builder.reducedSignatures;
            if (list != null) {
                this.reducedSignatures = list;
                return;
            } else {
                this.reducedSignatures = new ArrayList();
                return;
            }
        }
        int len = this.params.getWOTSPlus().getParams().getLen();
        int iCeil = (int) Math.ceil(((double) this.params.getHeight()) / 8.0d);
        int height = ((this.params.getHeight() / this.params.getLayers()) + len) * treeDigestSize;
        if (bArr.length != iCeil + treeDigestSize + (height * this.params.getLayers())) {
            throw new IllegalArgumentException("signature has wrong size");
        }
        this.index = XMSSUtil.bytesToXBigEndian(bArr, 0, iCeil);
        if (!XMSSUtil.isIndexValid(this.params.getHeight(), this.index)) {
            throw new IllegalArgumentException("index out of bounds");
        }
        int i = 0 + iCeil;
        this.random = XMSSUtil.extractBytesAtOffset(bArr, i, treeDigestSize);
        this.reducedSignatures = new ArrayList();
        for (int i2 = i + treeDigestSize; i2 < bArr.length; i2 += height) {
            this.reducedSignatures.add(new XMSSReducedSignature.Builder(this.params.getXMSSParameters()).withReducedSignature(XMSSUtil.extractBytesAtOffset(bArr, i2, height)).build());
        }
    }

    @Override // org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return toByteArray();
    }

    @Override // org.bouncycastle.pqc.crypto.xmss.XMSSStoreableObjectInterface
    public byte[] toByteArray() {
        int treeDigestSize = this.params.getTreeDigestSize();
        int len = this.params.getWOTSPlus().getParams().getLen();
        int iCeil = (int) Math.ceil(((double) this.params.getHeight()) / 8.0d);
        int height = ((this.params.getHeight() / this.params.getLayers()) + len) * treeDigestSize;
        byte[] bArr = new byte[iCeil + treeDigestSize + (height * this.params.getLayers())];
        XMSSUtil.copyBytesAtOffset(bArr, XMSSUtil.toBytesBigEndian(this.index, iCeil), 0);
        int i = 0 + iCeil;
        XMSSUtil.copyBytesAtOffset(bArr, this.random, i);
        int i2 = i + treeDigestSize;
        Iterator<XMSSReducedSignature> it = this.reducedSignatures.iterator();
        while (it.hasNext()) {
            XMSSUtil.copyBytesAtOffset(bArr, it.next().toByteArray(), i2);
            i2 += height;
        }
        return bArr;
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
