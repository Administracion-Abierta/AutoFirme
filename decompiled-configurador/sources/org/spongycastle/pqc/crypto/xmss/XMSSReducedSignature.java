package org.spongycastle.pqc.crypto.xmss;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/xmss/XMSSReducedSignature.class */
public class XMSSReducedSignature implements XMSSStoreableObjectInterface {
    private final XMSSParameters params;
    private final WOTSPlusSignature wotsPlusSignature;
    private final List<XMSSNode> authPath;

    protected XMSSReducedSignature(Builder builder) {
        this.params = builder.params;
        if (this.params == null) {
            throw new NullPointerException("params == null");
        }
        int n = this.params.getDigestSize();
        int len = this.params.getWOTSPlus().getParams().getLen();
        int height = this.params.getHeight();
        byte[] reducedSignature = builder.reducedSignature;
        if (reducedSignature != null) {
            int signatureSize = len * n;
            int authPathSize = height * n;
            int totalSize = signatureSize + authPathSize;
            if (reducedSignature.length != totalSize) {
                throw new IllegalArgumentException("signature has wrong size");
            }
            int position = 0;
            byte[][] wotsPlusSignature = new byte[len][];
            for (int i = 0; i < wotsPlusSignature.length; i++) {
                wotsPlusSignature[i] = XMSSUtil.extractBytesAtOffset(reducedSignature, position, n);
                position += n;
            }
            this.wotsPlusSignature = new WOTSPlusSignature(this.params.getWOTSPlus().getParams(), wotsPlusSignature);
            List<XMSSNode> nodeList = new ArrayList<>();
            for (int i2 = 0; i2 < height; i2++) {
                nodeList.add(new XMSSNode(i2, XMSSUtil.extractBytesAtOffset(reducedSignature, position, n)));
                position += n;
            }
            this.authPath = nodeList;
            return;
        }
        WOTSPlusSignature tmpSignature = builder.wotsPlusSignature;
        if (tmpSignature != null) {
            this.wotsPlusSignature = tmpSignature;
        } else {
            this.wotsPlusSignature = new WOTSPlusSignature(this.params.getWOTSPlus().getParams(), new byte[len][n]);
        }
        List<XMSSNode> tmpAuthPath = builder.authPath;
        if (tmpAuthPath != null) {
            if (tmpAuthPath.size() != height) {
                throw new IllegalArgumentException("size of authPath needs to be equal to height of tree");
            }
            this.authPath = tmpAuthPath;
            return;
        }
        this.authPath = new ArrayList();
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/xmss/XMSSReducedSignature$Builder.class */
    public static class Builder {
        private final XMSSParameters params;
        private WOTSPlusSignature wotsPlusSignature = null;
        private List<XMSSNode> authPath = null;
        private byte[] reducedSignature = null;

        public Builder(XMSSParameters params) {
            this.params = params;
        }

        public Builder withWOTSPlusSignature(WOTSPlusSignature val) {
            this.wotsPlusSignature = val;
            return this;
        }

        public Builder withAuthPath(List<XMSSNode> val) {
            this.authPath = val;
            return this;
        }

        public Builder withReducedSignature(byte[] val) {
            this.reducedSignature = XMSSUtil.cloneArray(val);
            return this;
        }

        public XMSSReducedSignature build() {
            return new XMSSReducedSignature(this);
        }
    }

    @Override // org.spongycastle.pqc.crypto.xmss.XMSSStoreableObjectInterface
    public byte[] toByteArray() {
        int n = this.params.getDigestSize();
        int signatureSize = this.params.getWOTSPlus().getParams().getLen() * n;
        int authPathSize = this.params.getHeight() * n;
        int totalSize = signatureSize + authPathSize;
        byte[] out = new byte[totalSize];
        int position = 0;
        byte[][] signature = this.wotsPlusSignature.toByteArray();
        for (byte[] bArr : signature) {
            XMSSUtil.copyBytesAtOffset(out, bArr, position);
            position += n;
        }
        for (int i = 0; i < this.authPath.size(); i++) {
            byte[] value = this.authPath.get(i).getValue();
            XMSSUtil.copyBytesAtOffset(out, value, position);
            position += n;
        }
        return out;
    }

    public XMSSParameters getParams() {
        return this.params;
    }

    public WOTSPlusSignature getWOTSPlusSignature() {
        return this.wotsPlusSignature;
    }

    public List<XMSSNode> getAuthPath() {
        return this.authPath;
    }
}
