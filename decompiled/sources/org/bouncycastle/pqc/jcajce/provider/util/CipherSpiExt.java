package org.bouncycastle.pqc.jcajce.provider.util;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/util/CipherSpiExt.class */
public abstract class CipherSpiExt extends CipherSpi {
    public static final int ENCRYPT_MODE = 1;
    public static final int DECRYPT_MODE = 2;
    protected int opMode;

    @Override // javax.crypto.CipherSpi
    protected final void engineInit(int i, Key key, SecureRandom secureRandom) throws InvalidKeyException {
        try {
            engineInit(i, key, (AlgorithmParameterSpec) null, secureRandom);
        } catch (InvalidAlgorithmParameterException e) {
            throw new InvalidParameterException(e.getMessage());
        }
    }

    @Override // javax.crypto.CipherSpi
    protected final void engineInit(int i, Key key, AlgorithmParameters algorithmParameters, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameters == null) {
            engineInit(i, key, secureRandom);
        } else {
            engineInit(i, key, (AlgorithmParameterSpec) null, secureRandom);
        }
    }

    @Override // javax.crypto.CipherSpi
    protected void engineInit(int i, Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameterSpec != null && !(algorithmParameterSpec instanceof AlgorithmParameterSpec)) {
            throw new InvalidAlgorithmParameterException();
        }
        if (key == null || !(key instanceof Key)) {
            throw new InvalidKeyException();
        }
        this.opMode = i;
        if (i == 1) {
            initEncrypt(key, algorithmParameterSpec, secureRandom);
        } else if (i == 2) {
            initDecrypt(key, algorithmParameterSpec);
        }
    }

    @Override // javax.crypto.CipherSpi
    protected final byte[] engineDoFinal(byte[] bArr, int i, int i2) throws BadPaddingException, IllegalBlockSizeException {
        return doFinal(bArr, i, i2);
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineDoFinal(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        return doFinal(bArr, i, i2, bArr2, i3);
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineGetBlockSize() {
        return getBlockSize();
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineGetKeySize(Key key) throws InvalidKeyException {
        if (key instanceof Key) {
            return getKeySize(key);
        }
        throw new InvalidKeyException("Unsupported key.");
    }

    @Override // javax.crypto.CipherSpi
    protected final byte[] engineGetIV() {
        return getIV();
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineGetOutputSize(int i) {
        return getOutputSize(i);
    }

    @Override // javax.crypto.CipherSpi
    protected final AlgorithmParameters engineGetParameters() {
        return null;
    }

    @Override // javax.crypto.CipherSpi
    protected final void engineSetMode(String str) throws NoSuchAlgorithmException {
        setMode(str);
    }

    @Override // javax.crypto.CipherSpi
    protected final void engineSetPadding(String str) throws NoSuchPaddingException {
        setPadding(str);
    }

    @Override // javax.crypto.CipherSpi
    protected final byte[] engineUpdate(byte[] bArr, int i, int i2) {
        return update(bArr, i, i2);
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineUpdate(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws ShortBufferException {
        return update(bArr, i, i2, bArr2, i3);
    }

    public abstract void initEncrypt(Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException;

    public abstract void initDecrypt(Key key, AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException;

    public abstract String getName();

    public abstract int getBlockSize();

    public abstract int getOutputSize(int i);

    public abstract int getKeySize(Key key) throws InvalidKeyException;

    public abstract AlgorithmParameterSpec getParameters();

    public abstract byte[] getIV();

    protected abstract void setMode(String str) throws NoSuchAlgorithmException;

    protected abstract void setPadding(String str) throws NoSuchPaddingException;

    public final byte[] update(byte[] bArr) {
        return update(bArr, 0, bArr.length);
    }

    public abstract byte[] update(byte[] bArr, int i, int i2);

    public abstract int update(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws ShortBufferException;

    public final byte[] doFinal() throws BadPaddingException, IllegalBlockSizeException {
        return doFinal(null, 0, 0);
    }

    public final byte[] doFinal(byte[] bArr) throws BadPaddingException, IllegalBlockSizeException {
        return doFinal(bArr, 0, bArr.length);
    }

    public abstract byte[] doFinal(byte[] bArr, int i, int i2) throws BadPaddingException, IllegalBlockSizeException;

    public abstract int doFinal(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws BadPaddingException, IllegalBlockSizeException, ShortBufferException;
}
