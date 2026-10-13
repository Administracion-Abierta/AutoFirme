package org.spongycastle.pqc.jcajce.provider.util;

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

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/util/CipherSpiExt.class */
public abstract class CipherSpiExt extends CipherSpi {
    public static final int ENCRYPT_MODE = 1;
    public static final int DECRYPT_MODE = 2;
    protected int opMode;

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

    public abstract byte[] update(byte[] bArr, int i, int i2);

    public abstract int update(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws ShortBufferException;

    public abstract byte[] doFinal(byte[] bArr, int i, int i2) throws BadPaddingException, IllegalBlockSizeException;

    public abstract int doFinal(byte[] bArr, int i, int i2, byte[] bArr2, int i3) throws BadPaddingException, IllegalBlockSizeException, ShortBufferException;

    @Override // javax.crypto.CipherSpi
    protected final void engineInit(int opMode, Key key, SecureRandom random) throws InvalidKeyException {
        try {
            engineInit(opMode, key, (AlgorithmParameterSpec) null, random);
        } catch (InvalidAlgorithmParameterException e) {
            throw new InvalidParameterException(e.getMessage());
        }
    }

    @Override // javax.crypto.CipherSpi
    protected final void engineInit(int opMode, Key key, AlgorithmParameters algParams, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algParams == null) {
            engineInit(opMode, key, random);
        } else {
            engineInit(opMode, key, (AlgorithmParameterSpec) null, random);
        }
    }

    @Override // javax.crypto.CipherSpi
    protected void engineInit(int opMode, Key key, AlgorithmParameterSpec params, SecureRandom javaRand) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (params != null && !(params instanceof AlgorithmParameterSpec)) {
            throw new InvalidAlgorithmParameterException();
        }
        if (key == null || !(key instanceof Key)) {
            throw new InvalidKeyException();
        }
        this.opMode = opMode;
        if (opMode == 1) {
            initEncrypt(key, params, javaRand);
        } else if (opMode == 2) {
            initDecrypt(key, params);
        }
    }

    @Override // javax.crypto.CipherSpi
    protected final byte[] engineDoFinal(byte[] input, int inOff, int inLen) throws BadPaddingException, IllegalBlockSizeException {
        return doFinal(input, inOff, inLen);
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineDoFinal(byte[] input, int inOff, int inLen, byte[] output, int outOff) throws BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        return doFinal(input, inOff, inLen, output, outOff);
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineGetBlockSize() {
        return getBlockSize();
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineGetKeySize(Key key) throws InvalidKeyException {
        if (!(key instanceof Key)) {
            throw new InvalidKeyException("Unsupported key.");
        }
        return getKeySize(key);
    }

    @Override // javax.crypto.CipherSpi
    protected final byte[] engineGetIV() {
        return getIV();
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineGetOutputSize(int inLen) {
        return getOutputSize(inLen);
    }

    @Override // javax.crypto.CipherSpi
    protected final AlgorithmParameters engineGetParameters() {
        return null;
    }

    @Override // javax.crypto.CipherSpi
    protected final void engineSetMode(String modeName) throws NoSuchAlgorithmException {
        setMode(modeName);
    }

    @Override // javax.crypto.CipherSpi
    protected final void engineSetPadding(String paddingName) throws NoSuchPaddingException {
        setPadding(paddingName);
    }

    @Override // javax.crypto.CipherSpi
    protected final byte[] engineUpdate(byte[] input, int inOff, int inLen) {
        return update(input, inOff, inLen);
    }

    @Override // javax.crypto.CipherSpi
    protected final int engineUpdate(byte[] input, int inOff, int inLen, byte[] output, int outOff) throws ShortBufferException {
        return update(input, inOff, inLen, output, outOff);
    }

    public final byte[] update(byte[] input) {
        return update(input, 0, input.length);
    }

    public final byte[] doFinal() throws BadPaddingException, IllegalBlockSizeException {
        return doFinal(null, 0, 0);
    }

    public final byte[] doFinal(byte[] input) throws BadPaddingException, IllegalBlockSizeException {
        return doFinal(input, 0, input.length);
    }
}
