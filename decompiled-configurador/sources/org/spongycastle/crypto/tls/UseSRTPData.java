package org.spongycastle.crypto.tls;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/UseSRTPData.class */
public class UseSRTPData {
    protected int[] protectionProfiles;
    protected byte[] mki;

    public UseSRTPData(int[] protectionProfiles, byte[] mki) {
        if (protectionProfiles == null || protectionProfiles.length < 1 || protectionProfiles.length >= 32768) {
            throw new IllegalArgumentException("'protectionProfiles' must have length from 1 to (2^15 - 1)");
        }
        if (mki == null) {
            mki = TlsUtils.EMPTY_BYTES;
        } else if (mki.length > 255) {
            throw new IllegalArgumentException("'mki' cannot be longer than 255 bytes");
        }
        this.protectionProfiles = protectionProfiles;
        this.mki = mki;
    }

    public int[] getProtectionProfiles() {
        return this.protectionProfiles;
    }

    public byte[] getMki() {
        return this.mki;
    }
}
