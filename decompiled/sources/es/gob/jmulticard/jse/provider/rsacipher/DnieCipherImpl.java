package es.gob.jmulticard.jse.provider.rsacipher;

import es.gob.jmulticard.card.CryptoCardException;
import es.gob.jmulticard.card.PinException;
import es.gob.jmulticard.connection.LostChannelException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.MGF1ParameterSpec;
import java.util.Locale;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/rsacipher/DnieCipherImpl.class */
public final class DnieCipherImpl extends CipherSpi {
    private static final byte[] B0 = new byte[0];
    private static final int MODE_ENCRYPT = 1;
    private static final int MODE_DECRYPT = 2;
    private static final int MODE_SIGN = 3;
    private static final int MODE_VERIFY = 4;
    private static final String PAD_NONE = "NoPadding";
    private static final String PAD_PKCS1 = "PKCS1Padding";
    private static final String PAD_OAEP_MGF1 = "OAEP";
    private int mode;
    private RSAPadding padding;
    private byte[] buffer;
    private int bufOfs;
    private int outputSize;
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    private static final String OAEP_HASH_ALGORITHM = "SHA-1";
    private SecureRandom random;
    private AlgorithmParameterSpec spec = null;
    private String paddingType = PAD_PKCS1;

    @Override // javax.crypto.CipherSpi
    protected void engineSetMode(String cipherMode) throws NoSuchAlgorithmException {
        if (cipherMode == null || !"ECB".equalsIgnoreCase(cipherMode)) {
            throw new NoSuchAlgorithmException("Modo no soportado: " + cipherMode);
        }
    }

    @Override // javax.crypto.CipherSpi
    protected void engineSetPadding(String paddingName) throws NoSuchPaddingException {
        if (paddingName == null) {
            throw new NoSuchPaddingException("El tipo de relleno no puede ser nulo");
        }
        if (PAD_NONE.equalsIgnoreCase(paddingName)) {
            this.paddingType = PAD_NONE;
        } else if (PAD_PKCS1.equalsIgnoreCase(paddingName)) {
            this.paddingType = PAD_PKCS1;
        } else if ("oaeppadding".equals(paddingName.toLowerCase(Locale.ENGLISH))) {
            this.paddingType = PAD_OAEP_MGF1;
        }
        throw new NoSuchPaddingException("Relleno no soportado: " + paddingName);
    }

    @Override // javax.crypto.CipherSpi
    protected int engineGetBlockSize() {
        return 0;
    }

    @Override // javax.crypto.CipherSpi
    protected int engineGetOutputSize(int inputLen) {
        return this.outputSize;
    }

    @Override // javax.crypto.CipherSpi
    protected byte[] engineGetIV() {
        return null;
    }

    @Override // javax.crypto.CipherSpi
    protected AlgorithmParameters engineGetParameters() {
        if (!(this.spec instanceof OAEPParameterSpec)) {
            return null;
        }
        try {
            AlgorithmParameters params = AlgorithmParameters.getInstance(PAD_OAEP_MGF1);
            params.init(this.spec);
            return params;
        } catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException("No se ha encontrado una implementacion OAEP de 'AlgorithmParameters'", nsae);
        } catch (InvalidParameterSpecException ipse) {
            throw new IllegalStateException("No se soporta OAEPParameterSpec", ipse);
        }
    }

    @Override // javax.crypto.CipherSpi
    protected void engineInit(int opmode, Key key, SecureRandom rnd) throws InvalidKeyException {
        try {
            init(opmode, key, rnd, null);
        } catch (InvalidAlgorithmParameterException iape) {
            throw new InvalidKeyException("Parametros invalidos para la inicializacion", iape);
        }
    }

    @Override // javax.crypto.CipherSpi
    protected void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom rnd) throws InvalidKeyException, InvalidAlgorithmParameterException {
        init(opmode, key, rnd, params);
    }

    @Override // javax.crypto.CipherSpi
    protected void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom rnd) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (params == null) {
            init(opmode, key, rnd, null);
            return;
        }
        try {
            OAEPParameterSpec paramSpec = (OAEPParameterSpec) params.getParameterSpec(OAEPParameterSpec.class);
            init(opmode, key, rnd, paramSpec);
        } catch (InvalidParameterSpecException ipse) {
            throw new InvalidAlgorithmParameterException("Parametros invalidos para la inicializacion", ipse);
        }
    }

    private void init(int opmode, Key key, SecureRandom rnd, AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
        boolean encrypt;
        switch (opmode) {
            case 1:
            case 3:
                encrypt = true;
                break;
            case 2:
            case 4:
                encrypt = false;
                break;
            default:
                throw new InvalidKeyException("Modo no valido: " + opmode);
        }
        RSAKey rsaKey = (RSAKey) key;
        if (key instanceof RSAPublicKey) {
            this.mode = encrypt ? 1 : 4;
            this.publicKey = (RSAPublicKey) key;
            this.privateKey = null;
        } else {
            this.mode = encrypt ? 3 : 2;
            this.privateKey = (RSAPrivateKey) key;
            this.publicKey = null;
        }
        int n = RSACore.getByteLength(rsaKey.getModulus());
        this.outputSize = n;
        this.bufOfs = 0;
        if (PAD_NONE.equals(this.paddingType)) {
            if (params != null) {
                throw new InvalidAlgorithmParameterException("Parametros no soportados para datos sin relleno");
            }
            this.padding = RSAPadding.createInstance(3, n, rnd);
            this.buffer = new byte[n];
            return;
        }
        if (PAD_PKCS1.equals(this.paddingType)) {
            if (params != null) {
                if (!(params instanceof TlsRsaPremasterSecretParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("Parametros no soportados para datos con relleno PKCS#1");
                }
                this.spec = params;
                this.random = rnd;
            }
            int blockType = this.mode <= 2 ? 2 : 1;
            this.padding = RSAPadding.createInstance(blockType, n, rnd);
            if (encrypt) {
                int k = this.padding.getMaxDataSize();
                this.buffer = new byte[k];
                return;
            } else {
                this.buffer = new byte[n];
                return;
            }
        }
        if (this.mode == 3 || this.mode == 4) {
            throw new InvalidKeyException("OAEP no puede usarse para firmar ni para verificar firmas");
        }
        if (params != null) {
            if (!(params instanceof OAEPParameterSpec)) {
                throw new InvalidAlgorithmParameterException("Parametros invalidos para el relleno OAEP");
            }
            this.spec = params;
        } else {
            this.spec = new OAEPParameterSpec("SHA-1", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
        }
        this.padding = RSAPadding.createInstance(4, n, rnd, (OAEPParameterSpec) this.spec);
        if (encrypt) {
            int k2 = this.padding.getMaxDataSize();
            this.buffer = new byte[k2];
        } else {
            this.buffer = new byte[n];
        }
    }

    private void update(byte[] inData, int inOfs, int inLen) {
        if (inLen == 0 || inData == null) {
            return;
        }
        if (this.bufOfs + inLen > this.buffer.length) {
            this.bufOfs = this.buffer.length + 1;
        } else {
            System.arraycopy(inData, inOfs, this.buffer, this.bufOfs, inLen);
            this.bufOfs += inLen;
        }
    }

    private byte[] doFinal() throws BadPaddingException, CryptoCardException, PinException, IllegalBlockSizeException, LostChannelException {
        if (this.bufOfs > this.buffer.length) {
            throw new IllegalBlockSizeException("Los datos no pueden exceder los " + this.buffer.length + " octetos");
        }
        try {
            switch (this.mode) {
                case 1:
                    byte[] data = this.padding.pad(this.buffer, 0, this.bufOfs);
                    byte[] bArrRsa = RSACore.rsa(data, this.publicKey);
                    this.bufOfs = 0;
                    return bArrRsa;
                case 2:
                    byte[] decryptBuffer = RSACore.convert(this.buffer, 0, this.bufOfs);
                    byte[] data2 = RSACore.rsa(decryptBuffer, this.privateKey);
                    byte[] bArrUnpad = this.padding.unpad(data2);
                    this.bufOfs = 0;
                    return bArrUnpad;
                case 3:
                    byte[] data3 = this.padding.pad(this.buffer, 0, this.bufOfs);
                    byte[] bArrRsa2 = RSACore.rsa(data3, this.privateKey);
                    this.bufOfs = 0;
                    return bArrRsa2;
                case 4:
                    byte[] verifyBuffer = RSACore.convert(this.buffer, 0, this.bufOfs);
                    byte[] data4 = RSACore.rsa(verifyBuffer, this.publicKey);
                    byte[] bArrUnpad2 = this.padding.unpad(data4);
                    this.bufOfs = 0;
                    return bArrUnpad2;
                default:
                    throw new AssertionError("Modo no soportado: " + this.mode);
            }
        } catch (Throwable th) {
            this.bufOfs = 0;
            throw th;
        }
    }

    @Override // javax.crypto.CipherSpi
    protected byte[] engineUpdate(byte[] inData, int inOfs, int inLen) {
        update(inData, inOfs, inLen);
        return B0;
    }

    @Override // javax.crypto.CipherSpi
    protected int engineUpdate(byte[] inData, int inOfs, int inLen, byte[] out, int outOfs) {
        update(inData, inOfs, inLen);
        return 0;
    }

    @Override // javax.crypto.CipherSpi
    protected byte[] engineDoFinal(byte[] inData, int inOfs, int inLen) throws BadPaddingException, IllegalBlockSizeException {
        update(inData, inOfs, inLen);
        try {
            return doFinal();
        } catch (CryptoCardException | PinException | LostChannelException e) {
            BadPaddingException bpe = new BadPaddingException("Error en el cifrado RSA con el DNIe");
            bpe.initCause(e);
            throw bpe;
        }
    }

    @Override // javax.crypto.CipherSpi
    protected int engineDoFinal(byte[] inData, int inOfs, int inLen, byte[] out, int outOfs) throws BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        if (this.outputSize > out.length - outOfs) {
            throw new ShortBufferException("Se necesitan al menos " + this.outputSize + " bytes en el buffer de salida");
        }
        update(inData, inOfs, inLen);
        try {
            byte[] result = doFinal();
            int n = result.length;
            System.arraycopy(result, 0, out, outOfs, n);
            return n;
        } catch (CryptoCardException | PinException | LostChannelException e) {
            BadPaddingException bpe = new BadPaddingException("Error en el cifrado RSA con el DNIe");
            bpe.initCause(e);
            throw bpe;
        }
    }

    @Override // javax.crypto.CipherSpi
    protected byte[] engineWrap(Key key) throws IllegalBlockSizeException, InvalidKeyException {
        byte[] encoded = key.getEncoded();
        if (encoded == null || encoded.length == 0) {
            throw new InvalidKeyException("No se ha podido obtener la codificacion de la clave");
        }
        if (encoded.length > this.buffer.length) {
            throw new InvalidKeyException("La cave es demasiado grande como para ser envuelta");
        }
        update(encoded, 0, encoded.length);
        try {
            return doFinal();
        } catch (CryptoCardException | PinException | LostChannelException e) {
            throw new InvalidKeyException("Error en la envoltura para el cifrado RSA con el DNIe", e);
        } catch (BadPaddingException e2) {
            throw new InvalidKeyException("Ha fallado la envoltura por un relleno invalido", e2);
        }
    }

    @Override // javax.crypto.CipherSpi
    protected Key engineUnwrap(byte[] wrappedKey, String algorithm, int type) throws NoSuchAlgorithmException, InvalidKeyException {
        if (wrappedKey.length > this.buffer.length) {
            throw new InvalidKeyException("La clave es demasiado grande para la desenvoltura");
        }
        boolean isTlsRsaPremasterSecret = "TlsRsaPremasterSecret".equals(algorithm);
        Exception failover = null;
        byte[] encoded = null;
        update(wrappedKey, 0, wrappedKey.length);
        try {
            encoded = doFinal();
        } catch (CryptoCardException | PinException | LostChannelException e) {
            throw new InvalidKeyException("Error en la desenvoltura para el descifrado RSA con el DNIe", e);
        } catch (BadPaddingException e2) {
            if (!isTlsRsaPremasterSecret) {
                throw new InvalidKeyException("Ha fallado la desenvoltura", e2);
            }
            failover = e2;
        } catch (IllegalBlockSizeException e3) {
            throw new InvalidKeyException("Ha fallado la desenvoltura por un tamano de bloque invalido", e3);
        }
        if (isTlsRsaPremasterSecret) {
            if (!(this.spec instanceof TlsRsaPremasterSecretParameterSpec)) {
                throw new IllegalStateException("No se ha especificado un TlsRsaPremasterSecretParameterSpec" + (this.spec != null ? ", sino un " + this.spec.getClass().getName() : ""));
            }
            encoded = KeyUtil.checkTlsPreMasterSecretKey(((TlsRsaPremasterSecretParameterSpec) this.spec).getClientVersion(), ((TlsRsaPremasterSecretParameterSpec) this.spec).getServerVersion(), this.random, encoded, failover != null);
        }
        return ConstructKeys.constructKey(encoded, algorithm, type);
    }

    @Override // javax.crypto.CipherSpi
    protected int engineGetKeySize(Key key) throws InvalidKeyException {
        return ((RSAKey) key).getModulus().bitLength();
    }
}
