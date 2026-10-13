package com.aowagie.text.pdf;

import com.aowagie.text.pdf.crypto.AESCipher;
import com.aowagie.text.pdf.crypto.ARCFOUREncryption;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/StandardDecryption.class */
class StandardDecryption {
    private ARCFOUREncryption arcfour;
    private AESCipher cipher;
    private byte[] key;
    private static final int AES_128 = 4;
    private static final int AES_256_V3 = 6;
    private final boolean aes;
    private boolean initiated;
    private final byte[] iv = new byte[16];
    private int ivptr;

    StandardDecryption(byte[] key, int off, int len, int revision) {
        this.aes = revision == 4 || revision == 6;
        if (this.aes) {
            this.key = new byte[len];
            System.arraycopy(key, off, this.key, 0, len);
        } else {
            this.arcfour = new ARCFOUREncryption();
            this.arcfour.prepareARCFOURKey(key, off, len);
        }
    }

    byte[] update(byte[] b, int off, int len) {
        if (this.aes) {
            if (this.initiated) {
                return this.cipher.update(b, off, len);
            }
            int left = Math.min(this.iv.length - this.ivptr, len);
            System.arraycopy(b, off, this.iv, this.ivptr, left);
            int off2 = off + left;
            int len2 = len - left;
            this.ivptr += left;
            if (this.ivptr == this.iv.length) {
                this.cipher = new AESCipher(false, this.key, this.iv);
                this.initiated = true;
                if (len2 > 0) {
                    return this.cipher.update(b, off2, len2);
                }
                return null;
            }
            return null;
        }
        byte[] b2 = new byte[len];
        this.arcfour.encryptARCFOUR(b, off, len, b2, 0);
        return b2;
    }

    byte[] finish() {
        if (this.aes) {
            return this.cipher.doFinal();
        }
        return null;
    }
}
