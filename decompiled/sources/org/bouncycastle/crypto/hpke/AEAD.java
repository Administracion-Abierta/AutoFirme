package org.bouncycastle.crypto.hpke;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.bouncycastle.crypto.modes.ChaCha20Poly1305;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/hpke/AEAD.class */
public class AEAD {
    private final short aeadId;
    private final byte[] key;
    private final byte[] baseNonce;
    private long seq;
    private AEADCipher cipher;

    public AEAD(short s, byte[] bArr, byte[] bArr2) {
        this.seq = 0L;
        this.key = bArr;
        this.baseNonce = bArr2;
        this.aeadId = s;
        this.seq = 0L;
        switch (s) {
            case 1:
            case 2:
                this.cipher = new GCMBlockCipher(new AESEngine());
                break;
            case 3:
                this.cipher = new ChaCha20Poly1305();
                break;
        }
    }

    public byte[] seal(byte[] bArr, byte[] bArr2, int i, int i2) throws InvalidCipherTextException {
        if (i < 0 || i > bArr2.length) {
            throw new IndexOutOfBoundsException("Invalid offset");
        }
        if (i + i2 > bArr2.length) {
            throw new IndexOutOfBoundsException("Invalid length");
        }
        switch (this.aeadId) {
            case -1:
            case 0:
            default:
                throw new IllegalStateException("Export only mode, cannot be used to seal/open");
            case 1:
            case 2:
            case 3:
                this.cipher.init(true, new ParametersWithIV(new KeyParameter(this.key), ComputeNonce()));
                this.cipher.processAADBytes(bArr, 0, bArr.length);
                byte[] bArr3 = new byte[this.cipher.getOutputSize(i2)];
                this.cipher.doFinal(bArr3, this.cipher.processBytes(bArr2, i, i2, bArr3, 0));
                this.seq++;
                return bArr3;
        }
    }

    public byte[] seal(byte[] bArr, byte[] bArr2) throws InvalidCipherTextException {
        return seal(bArr, bArr2, 0, bArr2.length);
    }

    public byte[] open(byte[] bArr, byte[] bArr2, int i, int i2) throws InvalidCipherTextException {
        if (i < 0 || i > bArr2.length) {
            throw new IndexOutOfBoundsException("Invalid offset");
        }
        if (i + i2 > bArr2.length) {
            throw new IndexOutOfBoundsException("Invalid length");
        }
        switch (this.aeadId) {
            case -1:
            case 0:
            default:
                throw new IllegalStateException("Export only mode, cannot be used to seal/open");
            case 1:
            case 2:
            case 3:
                this.cipher.init(false, new ParametersWithIV(new KeyParameter(this.key), ComputeNonce()));
                this.cipher.processAADBytes(bArr, 0, bArr.length);
                byte[] bArr3 = new byte[this.cipher.getOutputSize(i2)];
                int iProcessBytes = this.cipher.processBytes(bArr2, i, i2, bArr3, 0);
                int iDoFinal = iProcessBytes + this.cipher.doFinal(bArr3, iProcessBytes);
                this.seq++;
                return bArr3;
        }
    }

    public byte[] open(byte[] bArr, byte[] bArr2) throws InvalidCipherTextException {
        return open(bArr, bArr2, 0, bArr2.length);
    }

    private byte[] ComputeNonce() {
        byte[] bArrLongToBigEndian = Pack.longToBigEndian(this.seq);
        int length = this.baseNonce.length;
        byte[] bArrClone = Arrays.clone(this.baseNonce);
        for (int i = 0; i < 8; i++) {
            int i2 = (length - 8) + i;
            bArrClone[i2] = (byte) (bArrClone[i2] ^ bArrLongToBigEndian[i]);
        }
        return bArrClone;
    }
}
