package es.gob.afirma.core.ciphers;

import java.security.NoSuchAlgorithmException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/ciphers/AOCipherConfig.class */
public final class AOCipherConfig {
    private CipherConstants.AOCipherAlgorithm algo;
    private CipherConstants.AOCipherBlockMode mode;
    private CipherConstants.AOCipherPadding padding;

    public AOCipherConfig(CipherConstants.AOCipherAlgorithm aOCipherAlgorithm, CipherConstants.AOCipherBlockMode aOCipherBlockMode, CipherConstants.AOCipherPadding aOCipherPadding) {
        CipherConstants.AOCipherAlgorithm aOCipherAlgorithm2 = aOCipherAlgorithm != null ? aOCipherAlgorithm : CipherConstants.AOCipherAlgorithm.getDefault();
        CipherConstants.AOCipherBlockMode aOCipherBlockMode2 = aOCipherBlockMode;
        if (aOCipherBlockMode2 == null) {
            if (aOCipherAlgorithm2.equals(CipherConstants.AOCipherAlgorithm.PBEWITHMD5ANDDES) || aOCipherAlgorithm2.equals(CipherConstants.AOCipherAlgorithm.PBEWITHSHA1ANDDESEDE) || aOCipherAlgorithm2.equals(CipherConstants.AOCipherAlgorithm.PBEWITHSHA1ANDRC2_40)) {
                aOCipherBlockMode2 = CipherConstants.AOCipherBlockMode.CBC;
            } else {
                aOCipherBlockMode2 = CipherConstants.AOCipherBlockMode.ECB;
            }
        }
        CipherConstants.AOCipherPadding aOCipherPadding2 = aOCipherPadding;
        aOCipherPadding2 = aOCipherPadding2 == null ? CipherConstants.AOCipherPadding.PKCS5PADDING : aOCipherPadding2;
        this.algo = aOCipherAlgorithm2;
        this.mode = aOCipherBlockMode2;
        this.padding = aOCipherPadding2;
    }

    public static AOCipherConfig parse(String str) throws NoSuchAlgorithmException {
        CipherConstants.AOCipherBlockMode valueOf = null;
        CipherConstants.AOCipherPadding valueOf2 = null;
        String[] strArrSplit = str.split("/");
        CipherConstants.AOCipherAlgorithm valueOf3 = CipherConstants.AOCipherAlgorithm.getValueOf(strArrSplit[0]);
        if (valueOf3 == null) {
            throw new NoSuchAlgorithmException();
        }
        if (strArrSplit.length == 3) {
            valueOf = CipherConstants.AOCipherBlockMode.getValueOf(strArrSplit[1]);
            valueOf2 = CipherConstants.AOCipherPadding.getValueOf(strArrSplit[2]);
        }
        return new AOCipherConfig(valueOf3, valueOf, valueOf2);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.algo.getName());
        if (this.mode != null && this.padding != null) {
            sb.append("/").append(this.mode.getName()).append("/").append(this.padding.getName());
        }
        return sb.toString();
    }

    public boolean equals(Object obj) {
        return (obj instanceof AOCipherConfig) && ((AOCipherConfig) obj).algo.equals(this.algo) && ((AOCipherConfig) obj).mode.equals(this.mode) && ((AOCipherConfig) obj).padding.equals(this.padding);
    }

    public int hashCode() {
        return this.algo.hashCode() + this.mode.hashCode() + this.padding.hashCode();
    }

    public CipherConstants.AOCipherAlgorithm getAlgorithm() {
        return this.algo;
    }

    public CipherConstants.AOCipherBlockMode getBlockMode() {
        return this.mode;
    }

    public CipherConstants.AOCipherPadding getPadding() {
        return this.padding;
    }

    public void setAlgorithm(CipherConstants.AOCipherAlgorithm aOCipherAlgorithm) {
        this.algo = aOCipherAlgorithm;
    }

    public void setBlockMode(CipherConstants.AOCipherBlockMode aOCipherBlockMode) {
        this.mode = aOCipherBlockMode;
    }

    public void setPadding(CipherConstants.AOCipherPadding aOCipherPadding) {
        this.padding = aOCipherPadding;
    }
}
