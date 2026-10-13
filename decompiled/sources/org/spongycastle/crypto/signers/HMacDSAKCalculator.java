package org.spongycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/signers/HMacDSAKCalculator.class */
public class HMacDSAKCalculator implements DSAKCalculator {
    private static final BigInteger ZERO = BigInteger.valueOf(0);
    private final HMac hMac;
    private final byte[] K;
    private final byte[] V;
    private BigInteger n;

    public HMacDSAKCalculator(Digest digest) {
        this.hMac = new HMac(digest);
        this.V = new byte[this.hMac.getMacSize()];
        this.K = new byte[this.hMac.getMacSize()];
    }

    @Override // org.spongycastle.crypto.signers.DSAKCalculator
    public boolean isDeterministic() {
        return true;
    }

    @Override // org.spongycastle.crypto.signers.DSAKCalculator
    public void init(BigInteger n, SecureRandom random) {
        throw new IllegalStateException("Operation not supported");
    }

    @Override // org.spongycastle.crypto.signers.DSAKCalculator
    public void init(BigInteger n, BigInteger d, byte[] message) {
        this.n = n;
        Arrays.fill(this.V, (byte) 1);
        Arrays.fill(this.K, (byte) 0);
        byte[] x = new byte[(n.bitLength() + 7) / 8];
        byte[] dVal = BigIntegers.asUnsignedByteArray(d);
        System.arraycopy(dVal, 0, x, x.length - dVal.length, dVal.length);
        byte[] m = new byte[(n.bitLength() + 7) / 8];
        BigInteger mInt = bitsToInt(message);
        if (mInt.compareTo(n) >= 0) {
            mInt = mInt.subtract(n);
        }
        byte[] mVal = BigIntegers.asUnsignedByteArray(mInt);
        System.arraycopy(mVal, 0, m, m.length - mVal.length, mVal.length);
        this.hMac.init(new KeyParameter(this.K));
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.update((byte) 0);
        this.hMac.update(x, 0, x.length);
        this.hMac.update(m, 0, m.length);
        this.hMac.doFinal(this.K, 0);
        this.hMac.init(new KeyParameter(this.K));
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.doFinal(this.V, 0);
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.update((byte) 1);
        this.hMac.update(x, 0, x.length);
        this.hMac.update(m, 0, m.length);
        this.hMac.doFinal(this.K, 0);
        this.hMac.init(new KeyParameter(this.K));
        this.hMac.update(this.V, 0, this.V.length);
        this.hMac.doFinal(this.V, 0);
    }

    @Override // org.spongycastle.crypto.signers.DSAKCalculator
    public BigInteger nextK() {
        byte[] t = new byte[(this.n.bitLength() + 7) / 8];
        while (true) {
            int i = 0;
            while (true) {
                int tOff = i;
                if (tOff >= t.length) {
                    break;
                }
                this.hMac.update(this.V, 0, this.V.length);
                this.hMac.doFinal(this.V, 0);
                int len = Math.min(t.length - tOff, this.V.length);
                System.arraycopy(this.V, 0, t, tOff, len);
                i = tOff + len;
            }
            BigInteger k = bitsToInt(t);
            if (k.compareTo(ZERO) > 0 && k.compareTo(this.n) < 0) {
                return k;
            }
            this.hMac.update(this.V, 0, this.V.length);
            this.hMac.update((byte) 0);
            this.hMac.doFinal(this.K, 0);
            this.hMac.init(new KeyParameter(this.K));
            this.hMac.update(this.V, 0, this.V.length);
            this.hMac.doFinal(this.V, 0);
        }
    }

    private BigInteger bitsToInt(byte[] t) {
        BigInteger v = new BigInteger(1, t);
        if (t.length * 8 > this.n.bitLength()) {
            v = v.shiftRight((t.length * 8) - this.n.bitLength());
        }
        return v;
    }
}
