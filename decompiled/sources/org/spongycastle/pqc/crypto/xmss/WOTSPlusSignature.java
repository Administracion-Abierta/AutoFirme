package org.spongycastle.pqc.crypto.xmss;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/xmss/WOTSPlusSignature.class */
final class WOTSPlusSignature {
    private byte[][] signature;

    protected WOTSPlusSignature(WOTSPlusParameters params, byte[][] signature) {
        if (params == null) {
            throw new NullPointerException("params == null");
        }
        if (signature == null) {
            throw new NullPointerException("signature == null");
        }
        if (XMSSUtil.hasNullPointer(signature)) {
            throw new NullPointerException("signature byte array == null");
        }
        if (signature.length != params.getLen()) {
            throw new IllegalArgumentException("wrong signature size");
        }
        for (byte[] bArr : signature) {
            if (bArr.length != params.getDigestSize()) {
                throw new IllegalArgumentException("wrong signature format");
            }
        }
        this.signature = XMSSUtil.cloneArray(signature);
    }

    public byte[][] toByteArray() {
        return XMSSUtil.cloneArray(this.signature);
    }
}
