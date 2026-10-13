package org.spongycastle.crypto.engines;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.spongycastle.crypto.BasicAgreement;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DerivationFunction;
import org.spongycastle.crypto.EphemeralKeyPair;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.KeyParser;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.generators.EphemeralKeyPairGenerator;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.IESParameters;
import org.spongycastle.crypto.params.IESWithCipherParameters;
import org.spongycastle.crypto.params.KDFParameters;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.BigIntegers;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/IESEngine.class */
public class IESEngine {
    BasicAgreement agree;
    DerivationFunction kdf;
    Mac mac;
    BufferedBlockCipher cipher;
    byte[] macBuf;
    boolean forEncryption;
    CipherParameters privParam;
    CipherParameters pubParam;
    IESParameters param;
    byte[] V;
    private EphemeralKeyPairGenerator keyPairGenerator;
    private KeyParser keyParser;
    private byte[] IV;

    public IESEngine(BasicAgreement agree, DerivationFunction kdf, Mac mac) {
        this.agree = agree;
        this.kdf = kdf;
        this.mac = mac;
        this.macBuf = new byte[mac.getMacSize()];
        this.cipher = null;
    }

    public IESEngine(BasicAgreement agree, DerivationFunction kdf, Mac mac, BufferedBlockCipher cipher) {
        this.agree = agree;
        this.kdf = kdf;
        this.mac = mac;
        this.macBuf = new byte[mac.getMacSize()];
        this.cipher = cipher;
    }

    public void init(boolean forEncryption, CipherParameters privParam, CipherParameters pubParam, CipherParameters params) {
        this.forEncryption = forEncryption;
        this.privParam = privParam;
        this.pubParam = pubParam;
        this.V = new byte[0];
        extractParams(params);
    }

    public void init(AsymmetricKeyParameter publicKey, CipherParameters params, EphemeralKeyPairGenerator ephemeralKeyPairGenerator) {
        this.forEncryption = true;
        this.pubParam = publicKey;
        this.keyPairGenerator = ephemeralKeyPairGenerator;
        extractParams(params);
    }

    public void init(AsymmetricKeyParameter privateKey, CipherParameters params, KeyParser publicKeyParser) {
        this.forEncryption = false;
        this.privParam = privateKey;
        this.keyParser = publicKeyParser;
        extractParams(params);
    }

    private void extractParams(CipherParameters params) {
        if (params instanceof ParametersWithIV) {
            this.IV = ((ParametersWithIV) params).getIV();
            this.param = (IESParameters) ((ParametersWithIV) params).getParameters();
        } else {
            this.IV = null;
            this.param = (IESParameters) params;
        }
    }

    public BufferedBlockCipher getCipher() {
        return this.cipher;
    }

    public Mac getMac() {
        return this.mac;
    }

    private byte[] encryptBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        byte[] K2;
        byte[] C;
        int len;
        if (this.cipher == null) {
            byte[] K1 = new byte[inLen];
            K2 = new byte[this.param.getMacKeySize() / 8];
            byte[] K = new byte[K1.length + K2.length];
            this.kdf.generateBytes(K, 0, K.length);
            if (this.V.length != 0) {
                System.arraycopy(K, 0, K2, 0, K2.length);
                System.arraycopy(K, K2.length, K1, 0, K1.length);
            } else {
                System.arraycopy(K, 0, K1, 0, K1.length);
                System.arraycopy(K, inLen, K2, 0, K2.length);
            }
            C = new byte[inLen];
            for (int i = 0; i != inLen; i++) {
                C[i] = (byte) (in[inOff + i] ^ K1[i]);
            }
            len = inLen;
        } else {
            byte[] K3 = new byte[((IESWithCipherParameters) this.param).getCipherKeySize() / 8];
            K2 = new byte[this.param.getMacKeySize() / 8];
            byte[] K4 = new byte[K3.length + K2.length];
            this.kdf.generateBytes(K4, 0, K4.length);
            System.arraycopy(K4, 0, K3, 0, K3.length);
            System.arraycopy(K4, K3.length, K2, 0, K2.length);
            if (this.IV != null) {
                this.cipher.init(true, new ParametersWithIV(new KeyParameter(K3), this.IV));
            } else {
                this.cipher.init(true, new KeyParameter(K3));
            }
            C = new byte[this.cipher.getOutputSize(inLen)];
            int len2 = this.cipher.processBytes(in, inOff, inLen, C, 0);
            len = len2 + this.cipher.doFinal(C, len2);
        }
        byte[] P2 = this.param.getEncodingV();
        byte[] L2 = null;
        if (this.V.length != 0) {
            L2 = getLengthTag(P2);
        }
        byte[] T = new byte[this.mac.getMacSize()];
        this.mac.init(new KeyParameter(K2));
        this.mac.update(C, 0, C.length);
        if (P2 != null) {
            this.mac.update(P2, 0, P2.length);
        }
        if (this.V.length != 0) {
            this.mac.update(L2, 0, L2.length);
        }
        this.mac.doFinal(T, 0);
        byte[] Output = new byte[this.V.length + len + T.length];
        System.arraycopy(this.V, 0, Output, 0, this.V.length);
        System.arraycopy(C, 0, Output, this.V.length, len);
        System.arraycopy(T, 0, Output, this.V.length + len, T.length);
        return Output;
    }

    private byte[] decryptBlock(byte[] in_enc, int inOff, int inLen) throws InvalidCipherTextException {
        byte[] K2;
        byte[] M;
        int len = 0;
        if (inLen < this.V.length + this.mac.getMacSize()) {
            throw new InvalidCipherTextException("Length of input must be greater than the MAC and V combined");
        }
        if (this.cipher == null) {
            byte[] K1 = new byte[(inLen - this.V.length) - this.mac.getMacSize()];
            K2 = new byte[this.param.getMacKeySize() / 8];
            byte[] K = new byte[K1.length + K2.length];
            this.kdf.generateBytes(K, 0, K.length);
            if (this.V.length != 0) {
                System.arraycopy(K, 0, K2, 0, K2.length);
                System.arraycopy(K, K2.length, K1, 0, K1.length);
            } else {
                System.arraycopy(K, 0, K1, 0, K1.length);
                System.arraycopy(K, K1.length, K2, 0, K2.length);
            }
            M = new byte[K1.length];
            for (int i = 0; i != K1.length; i++) {
                M[i] = (byte) (in_enc[(inOff + this.V.length) + i] ^ K1[i]);
            }
        } else {
            byte[] K3 = new byte[((IESWithCipherParameters) this.param).getCipherKeySize() / 8];
            K2 = new byte[this.param.getMacKeySize() / 8];
            byte[] K4 = new byte[K3.length + K2.length];
            this.kdf.generateBytes(K4, 0, K4.length);
            System.arraycopy(K4, 0, K3, 0, K3.length);
            System.arraycopy(K4, K3.length, K2, 0, K2.length);
            CipherParameters cp = new KeyParameter(K3);
            if (this.IV != null) {
                cp = new ParametersWithIV(cp, this.IV);
            }
            this.cipher.init(false, cp);
            M = new byte[this.cipher.getOutputSize((inLen - this.V.length) - this.mac.getMacSize())];
            len = this.cipher.processBytes(in_enc, inOff + this.V.length, (inLen - this.V.length) - this.mac.getMacSize(), M, 0);
        }
        byte[] P2 = this.param.getEncodingV();
        byte[] L2 = null;
        if (this.V.length != 0) {
            L2 = getLengthTag(P2);
        }
        int end = inOff + inLen;
        byte[] T1 = Arrays.copyOfRange(in_enc, end - this.mac.getMacSize(), end);
        byte[] T2 = new byte[T1.length];
        this.mac.init(new KeyParameter(K2));
        this.mac.update(in_enc, inOff + this.V.length, (inLen - this.V.length) - T2.length);
        if (P2 != null) {
            this.mac.update(P2, 0, P2.length);
        }
        if (this.V.length != 0) {
            this.mac.update(L2, 0, L2.length);
        }
        this.mac.doFinal(T2, 0);
        if (!Arrays.constantTimeAreEqual(T1, T2)) {
            throw new InvalidCipherTextException("invalid MAC");
        }
        if (this.cipher == null) {
            return M;
        }
        return Arrays.copyOfRange(M, 0, len + this.cipher.doFinal(M, len));
    }

    public byte[] processBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        byte[] bArrDecryptBlock;
        if (this.forEncryption) {
            if (this.keyPairGenerator != null) {
                EphemeralKeyPair ephKeyPair = this.keyPairGenerator.generate();
                this.privParam = ephKeyPair.getKeyPair().getPrivate();
                this.V = ephKeyPair.getEncodedPublicKey();
            }
        } else if (this.keyParser != null) {
            ByteArrayInputStream bIn = new ByteArrayInputStream(in, inOff, inLen);
            try {
                this.pubParam = this.keyParser.readKey(bIn);
                int encLength = inLen - bIn.available();
                this.V = Arrays.copyOfRange(in, inOff, inOff + encLength);
            } catch (IOException e) {
                throw new InvalidCipherTextException("unable to recover ephemeral public key: " + e.getMessage(), e);
            } catch (IllegalArgumentException e2) {
                throw new InvalidCipherTextException("unable to recover ephemeral public key: " + e2.getMessage(), e2);
            }
        }
        this.agree.init(this.privParam);
        BigInteger z = this.agree.calculateAgreement(this.pubParam);
        byte[] Z = BigIntegers.asUnsignedByteArray(this.agree.getFieldSize(), z);
        if (this.V.length != 0) {
            byte[] VZ = Arrays.concatenate(this.V, Z);
            Arrays.fill(Z, (byte) 0);
            Z = VZ;
        }
        try {
            KDFParameters kdfParam = new KDFParameters(Z, this.param.getDerivationV());
            this.kdf.init(kdfParam);
            if (this.forEncryption) {
                bArrDecryptBlock = encryptBlock(in, inOff, inLen);
            } else {
                bArrDecryptBlock = decryptBlock(in, inOff, inLen);
            }
            byte[] bArr = bArrDecryptBlock;
            return bArr;
        } finally {
            Arrays.fill(Z, (byte) 0);
        }
    }

    protected byte[] getLengthTag(byte[] p2) {
        byte[] L2 = new byte[8];
        if (p2 != null) {
            Pack.longToBigEndian(((long) p2.length) * 8, L2, 0);
        }
        return L2;
    }
}
