package org.bouncycastle.pqc.jcajce.provider.mceliece;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.jcajce.provider.util.AsymmetricBlockCipher;
import org.bouncycastle.pqc.legacy.crypto.mceliece.McElieceCipher;
import org.bouncycastle.pqc.legacy.crypto.mceliece.McElieceKeyParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/mceliece/McEliecePKCSCipherSpi.class */
public class McEliecePKCSCipherSpi extends AsymmetricBlockCipher implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
    private McElieceCipher cipher;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/mceliece/McEliecePKCSCipherSpi$McEliecePKCS.class */
    public static class McEliecePKCS extends McEliecePKCSCipherSpi {
        public McEliecePKCS() {
            super(new McElieceCipher());
        }
    }

    public McEliecePKCSCipherSpi(McElieceCipher mcElieceCipher) {
        this.cipher = mcElieceCipher;
    }

    @Override // org.bouncycastle.pqc.jcajce.provider.util.AsymmetricBlockCipher
    protected void initCipherEncrypt(Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.cipher.init(true, new ParametersWithRandom(McElieceKeysToParams.generatePublicKeyParameter((PublicKey) key), secureRandom));
        this.maxPlainTextSize = this.cipher.maxPlainTextSize;
        this.cipherTextSize = this.cipher.cipherTextSize;
    }

    @Override // org.bouncycastle.pqc.jcajce.provider.util.AsymmetricBlockCipher
    protected void initCipherDecrypt(Key key, AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.cipher.init(false, McElieceKeysToParams.generatePrivateKeyParameter((PrivateKey) key));
        this.maxPlainTextSize = this.cipher.maxPlainTextSize;
        this.cipherTextSize = this.cipher.cipherTextSize;
    }

    @Override // org.bouncycastle.pqc.jcajce.provider.util.AsymmetricBlockCipher
    protected byte[] messageEncrypt(byte[] bArr) throws BadPaddingException, IllegalBlockSizeException {
        try {
            return this.cipher.messageEncrypt(bArr);
        } catch (Exception e) {
            throw new IllegalBlockSizeException(e.getMessage());
        }
    }

    @Override // org.bouncycastle.pqc.jcajce.provider.util.AsymmetricBlockCipher
    protected byte[] messageDecrypt(byte[] bArr) throws BadPaddingException, IllegalBlockSizeException {
        try {
            return this.cipher.messageDecrypt(bArr);
        } catch (Exception e) {
            throw new IllegalBlockSizeException(e.getMessage());
        }
    }

    @Override // org.bouncycastle.pqc.jcajce.provider.util.CipherSpiExt
    public String getName() {
        return "McEliecePKCS";
    }

    @Override // org.bouncycastle.pqc.jcajce.provider.util.CipherSpiExt
    public int getKeySize(Key key) throws InvalidKeyException {
        return this.cipher.getKeySize(key instanceof PublicKey ? (McElieceKeyParameters) McElieceKeysToParams.generatePublicKeyParameter((PublicKey) key) : (McElieceKeyParameters) McElieceKeysToParams.generatePrivateKeyParameter((PrivateKey) key));
    }
}
