package org.spongycastle.pqc.jcajce.provider.util;

import java.io.ByteArrayOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/util/AsymmetricBlockCipher.class */
public abstract class AsymmetricBlockCipher extends CipherSpiExt {
    protected AlgorithmParameterSpec paramSpec;
    protected ByteArrayOutputStream buf = new ByteArrayOutputStream();
    protected int maxPlainTextSize;
    protected int cipherTextSize;

    protected abstract void initCipherEncrypt(Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException;

    protected abstract void initCipherDecrypt(Key key, AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException;

    protected abstract byte[] messageEncrypt(byte[] bArr) throws BadPaddingException, IllegalBlockSizeException;

    protected abstract byte[] messageDecrypt(byte[] bArr) throws BadPaddingException, IllegalBlockSizeException;

    @Override // org.spongycastle.pqc.jcajce.provider.util.CipherSpiExt
    public final int getBlockSize() {
        return this.opMode == 1 ? this.maxPlainTextSize : this.cipherTextSize;
    }

    @Override // org.spongycastle.pqc.jcajce.provider.util.CipherSpiExt
    public final byte[] getIV() {
        return null;
    }

    @Override // org.spongycastle.pqc.jcajce.provider.util.CipherSpiExt
    public final int getOutputSize(int inLen) {
        int totalLen = inLen + this.buf.size();
        int maxLen = getBlockSize();
        if (totalLen > maxLen) {
            return 0;
        }
        return maxLen;
    }

    @Override // org.spongycastle.pqc.jcajce.provider.util.CipherSpiExt
    public final AlgorithmParameterSpec getParameters() {
        return this.paramSpec;
    }

    public final void initEncrypt(Key key) throws InvalidKeyException {
        try {
            initEncrypt(key, null, new SecureRandom());
        } catch (InvalidAlgorithmParameterException e) {
            throw new InvalidParameterException("This cipher needs algorithm parameters for initialization (cannot be null).");
        }
    }

    public final void initEncrypt(Key key, SecureRandom random) throws InvalidKeyException {
        try {
            initEncrypt(key, null, random);
        } catch (InvalidAlgorithmParameterException e) {
            throw new InvalidParameterException("This cipher needs algorithm parameters for initialization (cannot be null).");
        }
    }

    public final void initEncrypt(Key key, AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
        initEncrypt(key, params, new SecureRandom());
    }

    @Override // org.spongycastle.pqc.jcajce.provider.util.CipherSpiExt
    public final void initEncrypt(Key key, AlgorithmParameterSpec params, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.opMode = 1;
        initCipherEncrypt(key, params, secureRandom);
    }

    public final void initDecrypt(Key key) throws InvalidKeyException {
        try {
            initDecrypt(key, null);
        } catch (InvalidAlgorithmParameterException e) {
            throw new InvalidParameterException("This cipher needs algorithm parameters for initialization (cannot be null).");
        }
    }

    @Override // org.spongycastle.pqc.jcajce.provider.util.CipherSpiExt
    public final void initDecrypt(Key key, AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.opMode = 2;
        initCipherDecrypt(key, params);
    }

    @Override // org.spongycastle.pqc.jcajce.provider.util.CipherSpiExt
    public final byte[] update(byte[] input, int inOff, int inLen) {
        if (inLen != 0) {
            this.buf.write(input, inOff, inLen);
        }
        return new byte[0];
    }

    @Override // org.spongycastle.pqc.jcajce.provider.util.CipherSpiExt
    public final int update(byte[] input, int inOff, int inLen, byte[] output, int outOff) {
        update(input, inOff, inLen);
        return 0;
    }

    @Override // org.spongycastle.pqc.jcajce.provider.util.CipherSpiExt
    public final byte[] doFinal(byte[] input, int inOff, int inLen) throws BadPaddingException, IllegalBlockSizeException {
        checkLength(inLen);
        update(input, inOff, inLen);
        byte[] mBytes = this.buf.toByteArray();
        this.buf.reset();
        switch (this.opMode) {
            case 1:
                return messageEncrypt(mBytes);
            case 2:
                return messageDecrypt(mBytes);
            default:
                return null;
        }
    }

    @Override // org.spongycastle.pqc.jcajce.provider.util.CipherSpiExt
    public final int doFinal(byte[] input, int inOff, int inLen, byte[] output, int outOff) throws BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        if (output.length < getOutputSize(inLen)) {
            throw new ShortBufferException("Output buffer too short.");
        }
        byte[] out = doFinal(input, inOff, inLen);
        System.arraycopy(out, 0, output, outOff, out.length);
        return out.length;
    }

    @Override // org.spongycastle.pqc.jcajce.provider.util.CipherSpiExt
    protected final void setMode(String modeName) {
    }

    @Override // org.spongycastle.pqc.jcajce.provider.util.CipherSpiExt
    protected final void setPadding(String paddingName) {
    }

    protected void checkLength(int inLen) throws IllegalBlockSizeException {
        int inLength = inLen + this.buf.size();
        if (this.opMode == 1) {
            if (inLength > this.maxPlainTextSize) {
                throw new IllegalBlockSizeException("The length of the plaintext (" + inLength + " bytes) is not supported by the cipher (max. " + this.maxPlainTextSize + " bytes).");
            }
        } else if (this.opMode == 2 && inLength != this.cipherTextSize) {
            throw new IllegalBlockSizeException("Illegal ciphertext length (expected " + this.cipherTextSize + " bytes, was " + inLength + " bytes).");
        }
    }
}
