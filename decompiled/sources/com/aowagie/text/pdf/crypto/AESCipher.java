package com.aowagie.text.pdf.crypto;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.engines.AESFastEngine;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/crypto/AESCipher.class */
public class AESCipher {
    private final PaddedBufferedBlockCipher bp;

    public AESCipher(boolean forEncryption, byte[] key, byte[] iv) {
        BlockCipher aes = new AESFastEngine();
        BlockCipher cbc = new CBCBlockCipher(aes);
        this.bp = new PaddedBufferedBlockCipher(cbc);
        KeyParameter kp = new KeyParameter(key);
        ParametersWithIV piv = new ParametersWithIV(kp, iv);
        this.bp.init(forEncryption, piv);
    }

    public byte[] update(byte[] inp, int inpOff, int inpLen) {
        int neededLen = this.bp.getUpdateOutputSize(inpLen);
        byte[] outp = null;
        if (neededLen > 0) {
            outp = new byte[neededLen];
        }
        this.bp.processBytes(inp, inpOff, inpLen, outp, 0);
        return outp;
    }

    public byte[] doFinal() {
        int neededLen = this.bp.getOutputSize(0);
        byte[] outp = new byte[neededLen];
        try {
            int n = this.bp.doFinal(outp, 0);
            if (n != outp.length) {
                byte[] outp2 = new byte[n];
                System.arraycopy(outp, 0, outp2, 0, n);
                return outp2;
            }
            return outp;
        } catch (Exception e) {
            return outp;
        }
    }
}
