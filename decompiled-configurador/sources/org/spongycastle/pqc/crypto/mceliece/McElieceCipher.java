package org.spongycastle.pqc.crypto.mceliece;

import java.security.SecureRandom;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.pqc.crypto.MessageEncryptor;
import org.spongycastle.pqc.math.linearalgebra.GF2Matrix;
import org.spongycastle.pqc.math.linearalgebra.GF2Vector;
import org.spongycastle.pqc.math.linearalgebra.GF2mField;
import org.spongycastle.pqc.math.linearalgebra.GoppaCode;
import org.spongycastle.pqc.math.linearalgebra.Permutation;
import org.spongycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;
import org.spongycastle.pqc.math.linearalgebra.Vector;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/mceliece/McElieceCipher.class */
public class McElieceCipher implements MessageEncryptor {
    public static final String OID = "1.3.6.1.4.1.8301.3.1.3.4.1";
    private SecureRandom sr;
    private int n;
    private int k;
    private int t;
    public int maxPlainTextSize;
    public int cipherTextSize;
    private McElieceKeyParameters key;
    private boolean forEncryption;

    @Override // org.spongycastle.pqc.crypto.MessageEncryptor
    public void init(boolean forEncryption, CipherParameters param) {
        this.forEncryption = forEncryption;
        if (forEncryption) {
            if (param instanceof ParametersWithRandom) {
                ParametersWithRandom rParam = (ParametersWithRandom) param;
                this.sr = rParam.getRandom();
                this.key = (McEliecePublicKeyParameters) rParam.getParameters();
                initCipherEncrypt((McEliecePublicKeyParameters) this.key);
                return;
            }
            this.sr = new SecureRandom();
            this.key = (McEliecePublicKeyParameters) param;
            initCipherEncrypt((McEliecePublicKeyParameters) this.key);
            return;
        }
        this.key = (McEliecePrivateKeyParameters) param;
        initCipherDecrypt((McEliecePrivateKeyParameters) this.key);
    }

    public int getKeySize(McElieceKeyParameters key) {
        if (key instanceof McEliecePublicKeyParameters) {
            return ((McEliecePublicKeyParameters) key).getN();
        }
        if (key instanceof McEliecePrivateKeyParameters) {
            return ((McEliecePrivateKeyParameters) key).getN();
        }
        throw new IllegalArgumentException("unsupported type");
    }

    private void initCipherEncrypt(McEliecePublicKeyParameters pubKey) {
        this.sr = this.sr != null ? this.sr : new SecureRandom();
        this.n = pubKey.getN();
        this.k = pubKey.getK();
        this.t = pubKey.getT();
        this.cipherTextSize = this.n >> 3;
        this.maxPlainTextSize = this.k >> 3;
    }

    private void initCipherDecrypt(McEliecePrivateKeyParameters privKey) {
        this.n = privKey.getN();
        this.k = privKey.getK();
        this.maxPlainTextSize = this.k >> 3;
        this.cipherTextSize = this.n >> 3;
    }

    @Override // org.spongycastle.pqc.crypto.MessageEncryptor
    public byte[] messageEncrypt(byte[] input) {
        if (!this.forEncryption) {
            throw new IllegalStateException("cipher initialised for decryption");
        }
        GF2Vector m = computeMessageRepresentative(input);
        GF2Vector z = new GF2Vector(this.n, this.t, this.sr);
        GF2Matrix g = ((McEliecePublicKeyParameters) this.key).getG();
        Vector mG = g.leftMultiply(m);
        GF2Vector mGZ = (GF2Vector) mG.add(z);
        return mGZ.getEncoded();
    }

    private GF2Vector computeMessageRepresentative(byte[] input) {
        byte[] data = new byte[this.maxPlainTextSize + ((this.k & 7) != 0 ? 1 : 0)];
        System.arraycopy(input, 0, data, 0, input.length);
        data[input.length] = 1;
        return GF2Vector.OS2VP(this.k, data);
    }

    @Override // org.spongycastle.pqc.crypto.MessageEncryptor
    public byte[] messageDecrypt(byte[] input) throws InvalidCipherTextException {
        if (this.forEncryption) {
            throw new IllegalStateException("cipher initialised for decryption");
        }
        GF2Vector vec = GF2Vector.OS2VP(this.n, input);
        McEliecePrivateKeyParameters privKey = (McEliecePrivateKeyParameters) this.key;
        GF2mField field = privKey.getField();
        PolynomialGF2mSmallM gp = privKey.getGoppaPoly();
        GF2Matrix sInv = privKey.getSInv();
        Permutation p1 = privKey.getP1();
        Permutation p2 = privKey.getP2();
        GF2Matrix h = privKey.getH();
        PolynomialGF2mSmallM[] qInv = privKey.getQInv();
        Permutation p = p1.rightMultiply(p2);
        Permutation pInv = p.computeInverse();
        GF2Vector cPInv = (GF2Vector) vec.multiply(pInv);
        GF2Vector syndrome = (GF2Vector) h.rightMultiply(cPInv);
        GF2Vector z = GoppaCode.syndromeDecode(syndrome, field, gp, qInv);
        GF2Vector mSG = (GF2Vector) cPInv.add(z);
        GF2Vector mSG2 = (GF2Vector) mSG.multiply(p1);
        GF2Vector mS = mSG2.extractRightVector(this.k);
        GF2Vector mVec = (GF2Vector) sInv.leftMultiply(mS);
        return computeMessage(mVec);
    }

    private byte[] computeMessage(GF2Vector mr) throws InvalidCipherTextException {
        byte[] mrBytes = mr.getEncoded();
        int index = mrBytes.length - 1;
        while (index >= 0 && mrBytes[index] == 0) {
            index--;
        }
        if (index < 0 || mrBytes[index] != 1) {
            throw new InvalidCipherTextException("Bad Padding: invalid ciphertext");
        }
        byte[] mBytes = new byte[index];
        System.arraycopy(mrBytes, 0, mBytes, 0, index);
        return mBytes;
    }
}
