package org.spongycastle.crypto.engines;

import java.math.BigInteger;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.params.RSAKeyParameters;
import org.spongycastle.crypto.params.RSAPrivateCrtKeyParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/engines/RSACoreEngine.class */
class RSACoreEngine {
    private RSAKeyParameters key;
    private boolean forEncryption;

    RSACoreEngine() {
    }

    public void init(boolean forEncryption, CipherParameters param) {
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom rParam = (ParametersWithRandom) param;
            this.key = (RSAKeyParameters) rParam.getParameters();
        } else {
            this.key = (RSAKeyParameters) param;
        }
        this.forEncryption = forEncryption;
    }

    public int getInputBlockSize() {
        int bitSize = this.key.getModulus().bitLength();
        if (this.forEncryption) {
            return ((bitSize + 7) / 8) - 1;
        }
        return (bitSize + 7) / 8;
    }

    public int getOutputBlockSize() {
        int bitSize = this.key.getModulus().bitLength();
        if (this.forEncryption) {
            return (bitSize + 7) / 8;
        }
        return ((bitSize + 7) / 8) - 1;
    }

    public BigInteger convertInput(byte[] in, int inOff, int inLen) {
        byte[] block;
        if (inLen > getInputBlockSize() + 1) {
            throw new DataLengthException("input too large for RSA cipher.");
        }
        if (inLen == getInputBlockSize() + 1 && !this.forEncryption) {
            throw new DataLengthException("input too large for RSA cipher.");
        }
        if (inOff != 0 || inLen != in.length) {
            block = new byte[inLen];
            System.arraycopy(in, inOff, block, 0, inLen);
        } else {
            block = in;
        }
        BigInteger res = new BigInteger(1, block);
        if (res.compareTo(this.key.getModulus()) >= 0) {
            throw new DataLengthException("input too large for RSA cipher.");
        }
        return res;
    }

    public byte[] convertOutput(BigInteger result) {
        byte[] output = result.toByteArray();
        if (this.forEncryption) {
            if (output[0] == 0 && output.length > getOutputBlockSize()) {
                byte[] tmp = new byte[output.length - 1];
                System.arraycopy(output, 1, tmp, 0, tmp.length);
                return tmp;
            }
            if (output.length < getOutputBlockSize()) {
                byte[] tmp2 = new byte[getOutputBlockSize()];
                System.arraycopy(output, 0, tmp2, tmp2.length - output.length, output.length);
                return tmp2;
            }
        } else if (output[0] == 0) {
            byte[] tmp3 = new byte[output.length - 1];
            System.arraycopy(output, 1, tmp3, 0, tmp3.length);
            return tmp3;
        }
        return output;
    }

    public BigInteger processBlock(BigInteger input) {
        if (this.key instanceof RSAPrivateCrtKeyParameters) {
            RSAPrivateCrtKeyParameters crtKey = (RSAPrivateCrtKeyParameters) this.key;
            BigInteger p = crtKey.getP();
            BigInteger q = crtKey.getQ();
            BigInteger dP = crtKey.getDP();
            BigInteger dQ = crtKey.getDQ();
            BigInteger qInv = crtKey.getQInv();
            BigInteger mP = input.remainder(p).modPow(dP, p);
            BigInteger mQ = input.remainder(q).modPow(dQ, q);
            BigInteger h = mP.subtract(mQ);
            BigInteger m = h.multiply(qInv).mod(p).multiply(q);
            return m.add(mQ);
        }
        return input.modPow(this.key.getExponent(), this.key.getModulus());
    }
}
