package org.spongycastle.pqc.jcajce.provider.mceliece;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.X509ObjectIdentifiers;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.pqc.crypto.mceliece.McElieceCipher;
import org.spongycastle.pqc.crypto.mceliece.McElieceKeyParameters;
import org.spongycastle.pqc.jcajce.provider.util.AsymmetricBlockCipher;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/mceliece/McEliecePKCSCipherSpi.class */
public class McEliecePKCSCipherSpi extends AsymmetricBlockCipher implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
    private McElieceCipher cipher;

    public McEliecePKCSCipherSpi(McElieceCipher cipher) {
        this.cipher = cipher;
    }

    @Override // org.spongycastle.pqc.jcajce.provider.util.AsymmetricBlockCipher
    protected void initCipherEncrypt(Key key, AlgorithmParameterSpec params, SecureRandom sr) throws InvalidKeyException, InvalidAlgorithmParameterException {
        CipherParameters param = McElieceKeysToParams.generatePublicKeyParameter((PublicKey) key);
        this.cipher.init(true, new ParametersWithRandom(param, sr));
        this.maxPlainTextSize = this.cipher.maxPlainTextSize;
        this.cipherTextSize = this.cipher.cipherTextSize;
    }

    @Override // org.spongycastle.pqc.jcajce.provider.util.AsymmetricBlockCipher
    protected void initCipherDecrypt(Key key, AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
        CipherParameters param = McElieceKeysToParams.generatePrivateKeyParameter((PrivateKey) key);
        this.cipher.init(false, param);
        this.maxPlainTextSize = this.cipher.maxPlainTextSize;
        this.cipherTextSize = this.cipher.cipherTextSize;
    }

    @Override // org.spongycastle.pqc.jcajce.provider.util.AsymmetricBlockCipher
    protected byte[] messageEncrypt(byte[] input) throws BadPaddingException, IllegalBlockSizeException {
        byte[] output = null;
        try {
            output = this.cipher.messageEncrypt(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    @Override // org.spongycastle.pqc.jcajce.provider.util.AsymmetricBlockCipher
    protected byte[] messageDecrypt(byte[] input) throws BadPaddingException, IllegalBlockSizeException {
        byte[] output = null;
        try {
            output = this.cipher.messageDecrypt(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    @Override // org.spongycastle.pqc.jcajce.provider.util.CipherSpiExt
    public String getName() {
        return "McEliecePKCS";
    }

    @Override // org.spongycastle.pqc.jcajce.provider.util.CipherSpiExt
    public int getKeySize(Key key) throws InvalidKeyException {
        McElieceKeyParameters mcElieceKeyParameters;
        if (key instanceof PublicKey) {
            mcElieceKeyParameters = (McElieceKeyParameters) McElieceKeysToParams.generatePublicKeyParameter((PublicKey) key);
        } else {
            mcElieceKeyParameters = (McElieceKeyParameters) McElieceKeysToParams.generatePrivateKeyParameter((PrivateKey) key);
        }
        return this.cipher.getKeySize(mcElieceKeyParameters);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/provider/mceliece/McEliecePKCSCipherSpi$McEliecePKCS.class */
    public static class McEliecePKCS extends McEliecePKCSCipherSpi {
        public McEliecePKCS() {
            super(new McElieceCipher());
        }
    }
}
