package org.spongycastle.crypto.engines;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.digests.SM3Digest;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECKeyParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.math.ec.ECConstants;
import org.spongycastle.math.ec.ECFieldElement;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/engines/SM2Engine.class */
public class SM2Engine {
    private final Digest digest;
    private boolean forEncryption;
    private ECKeyParameters ecKey;
    private ECDomainParameters ecParams;
    private int curveLength;
    private SecureRandom random;

    public SM2Engine() {
        this(new SM3Digest());
    }

    public SM2Engine(Digest digest) {
        this.digest = digest;
    }

    public void init(boolean forEncryption, CipherParameters param) {
        this.forEncryption = forEncryption;
        if (forEncryption) {
            ParametersWithRandom rParam = (ParametersWithRandom) param;
            this.ecKey = (ECKeyParameters) rParam.getParameters();
            this.ecParams = this.ecKey.getParameters();
            ECPoint s = ((ECPublicKeyParameters) this.ecKey).getQ().multiply(this.ecParams.getH());
            if (s.isInfinity()) {
                throw new IllegalArgumentException("invalid key: [h]Q at infinity");
            }
            this.random = rParam.getRandom();
        } else {
            this.ecKey = (ECKeyParameters) param;
            this.ecParams = this.ecKey.getParameters();
        }
        this.curveLength = (this.ecParams.getCurve().getFieldSize() + 7) / 8;
    }

    public byte[] processBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (this.forEncryption) {
            return encrypt(in, inOff, inLen);
        }
        return decrypt(in, inOff, inLen);
    }

    private byte[] encrypt(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        byte[] c1;
        ECPoint kPB;
        byte[] c2 = new byte[inLen];
        System.arraycopy(in, inOff, c2, 0, c2.length);
        do {
            BigInteger k = nextK();
            ECPoint c1P = this.ecParams.getG().multiply(k).normalize();
            c1 = c1P.getEncoded(false);
            kPB = ((ECPublicKeyParameters) this.ecKey).getQ().multiply(k).normalize();
            kdf(this.digest, kPB, c2);
        } while (notEncrypted(c2, in, inOff));
        byte[] c3 = new byte[this.digest.getDigestSize()];
        addFieldElement(this.digest, kPB.getAffineXCoord());
        this.digest.update(in, inOff, inLen);
        addFieldElement(this.digest, kPB.getAffineYCoord());
        this.digest.doFinal(c3, 0);
        return Arrays.concatenate(c1, c2, c3);
    }

    private byte[] decrypt(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        byte[] c1 = new byte[(this.curveLength * 2) + 1];
        System.arraycopy(in, inOff, c1, 0, c1.length);
        ECPoint c1P = this.ecParams.getCurve().decodePoint(c1);
        ECPoint s = c1P.multiply(this.ecParams.getH());
        if (s.isInfinity()) {
            throw new InvalidCipherTextException("[h]C1 at infinity");
        }
        ECPoint c1P2 = c1P.multiply(((ECPrivateKeyParameters) this.ecKey).getD()).normalize();
        byte[] c2 = new byte[(inLen - c1.length) - this.digest.getDigestSize()];
        System.arraycopy(in, inOff + c1.length, c2, 0, c2.length);
        kdf(this.digest, c1P2, c2);
        byte[] c3 = new byte[this.digest.getDigestSize()];
        addFieldElement(this.digest, c1P2.getAffineXCoord());
        this.digest.update(c2, 0, c2.length);
        addFieldElement(this.digest, c1P2.getAffineYCoord());
        this.digest.doFinal(c3, 0);
        int check = 0;
        for (int i = 0; i != c3.length; i++) {
            check |= c3[i] ^ in[(c1.length + c2.length) + i];
        }
        clearBlock(c1);
        clearBlock(c3);
        if (check != 0) {
            clearBlock(c2);
            throw new InvalidCipherTextException("invalid cipher text");
        }
        return c2;
    }

    private boolean notEncrypted(byte[] encData, byte[] in, int inOff) {
        for (int i = 0; i != encData.length; i++) {
            if (encData[i] != in[inOff]) {
                return false;
            }
        }
        return true;
    }

    private void kdf(Digest digest, ECPoint c1, byte[] encData) {
        int ct = 1;
        int v = digest.getDigestSize();
        byte[] buf = new byte[digest.getDigestSize()];
        int off = 0;
        for (int i = 1; i <= ((encData.length + v) - 1) / v; i++) {
            addFieldElement(digest, c1.getAffineXCoord());
            addFieldElement(digest, c1.getAffineYCoord());
            digest.update((byte) (ct >> 24));
            digest.update((byte) (ct >> 16));
            digest.update((byte) (ct >> 8));
            digest.update((byte) ct);
            digest.doFinal(buf, 0);
            if (off + buf.length < encData.length) {
                xor(encData, buf, off, buf.length);
            } else {
                xor(encData, buf, off, encData.length - off);
            }
            off += buf.length;
            ct++;
        }
    }

    private void xor(byte[] data, byte[] kdfOut, int dOff, int dRemaining) {
        for (int i = 0; i != dRemaining; i++) {
            int i2 = dOff + i;
            data[i2] = (byte) (data[i2] ^ kdfOut[i]);
        }
    }

    private BigInteger nextK() {
        int qBitLength = this.ecParams.getN().bitLength();
        while (true) {
            BigInteger k = new BigInteger(qBitLength, this.random);
            if (!k.equals(ECConstants.ZERO) && k.compareTo(this.ecParams.getN()) < 0) {
                return k;
            }
        }
    }

    private void addFieldElement(Digest digest, ECFieldElement v) {
        byte[] p = BigIntegers.asUnsignedByteArray(this.curveLength, v.toBigInteger());
        digest.update(p, 0, p.length);
    }

    private void clearBlock(byte[] block) {
        for (int i = 0; i != block.length; i++) {
            block[i] = 0;
        }
    }
}
