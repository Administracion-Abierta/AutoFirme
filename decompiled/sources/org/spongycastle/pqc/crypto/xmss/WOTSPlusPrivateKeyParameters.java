package org.spongycastle.pqc.crypto.xmss;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/xmss/WOTSPlusPrivateKeyParameters.class */
final class WOTSPlusPrivateKeyParameters {
    private final byte[][] privateKey;

    protected WOTSPlusPrivateKeyParameters(WOTSPlusParameters params, byte[][] privateKey) {
        if (params == null) {
            throw new NullPointerException("params == null");
        }
        if (privateKey == null) {
            throw new NullPointerException("privateKey == null");
        }
        if (XMSSUtil.hasNullPointer(privateKey)) {
            throw new NullPointerException("privateKey byte array == null");
        }
        if (privateKey.length != params.getLen()) {
            throw new IllegalArgumentException("wrong privateKey format");
        }
        for (byte[] bArr : privateKey) {
            if (bArr.length != params.getDigestSize()) {
                throw new IllegalArgumentException("wrong privateKey format");
            }
        }
        this.privateKey = XMSSUtil.cloneArray(privateKey);
    }

    protected byte[][] toByteArray() {
        return XMSSUtil.cloneArray(this.privateKey);
    }
}
