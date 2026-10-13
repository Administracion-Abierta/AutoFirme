package org.spongycastle.crypto.prng.drbg;

import java.math.BigInteger;
import org.spongycastle.asn1.nist.NISTNamedCurves;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.prng.EntropySource;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECMultiplier;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.math.ec.FixedPointCombMultiplier;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/prng/drbg/DualECSP800DRBG.class */
public class DualECSP800DRBG implements SP80090DRBG {
    private static final BigInteger p256_Px = new BigInteger("6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296", 16);
    private static final BigInteger p256_Py = new BigInteger("4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5", 16);
    private static final BigInteger p256_Qx = new BigInteger("c97445f45cdef9f0d3e05e1e585fc297235b82b5be8ff3efca67c59852018192", 16);
    private static final BigInteger p256_Qy = new BigInteger("b28ef557ba31dfcbdd21ac46e2a91e3c304f44cb87058ada2cb815151e610046", 16);
    private static final BigInteger p384_Px = new BigInteger("aa87ca22be8b05378eb1c71ef320ad746e1d3b628ba79b9859f741e082542a385502f25dbf55296c3a545e3872760ab7", 16);
    private static final BigInteger p384_Py = new BigInteger("3617de4a96262c6f5d9e98bf9292dc29f8f41dbd289a147ce9da3113b5f0b8c00a60b1ce1d7e819d7a431d7c90ea0e5f", 16);
    private static final BigInteger p384_Qx = new BigInteger("8e722de3125bddb05580164bfe20b8b432216a62926c57502ceede31c47816edd1e89769124179d0b695106428815065", 16);
    private static final BigInteger p384_Qy = new BigInteger("023b1660dd701d0839fd45eec36f9ee7b32e13b315dc02610aa1b636e346df671f790f84c5e09b05674dbb7e45c803dd", 16);
    private static final BigInteger p521_Px = new BigInteger("c6858e06b70404e9cd9e3ecb662395b4429c648139053fb521f828af606b4d3dbaa14b5e77efe75928fe1dc127a2ffa8de3348b3c1856a429bf97e7e31c2e5bd66", 16);
    private static final BigInteger p521_Py = new BigInteger("11839296a789a3bc0045c8a5fb42c7d1bd998f54449579b446817afbd17273e662c97ee72995ef42640c550b9013fad0761353c7086a272c24088be94769fd16650", 16);
    private static final BigInteger p521_Qx = new BigInteger("1b9fa3e518d683c6b65763694ac8efbaec6fab44f2276171a42726507dd08add4c3b3f4c1ebc5b1222ddba077f722943b24c3edfa0f85fe24d0c8c01591f0be6f63", 16);
    private static final BigInteger p521_Qy = new BigInteger("1f3bdba585295d9a1110d1df1f9430ef8442c5018976ff3437ef91b81dc0b8132c8d5c39c32d0e004a3092b7d327c0e7a4d26d2c7b69b58f9066652911e457779de", 16);
    private static final DualECPoints[] nistPoints = new DualECPoints[3];
    private static final long RESEED_MAX = 2147483648L;
    private static final int MAX_ADDITIONAL_INPUT = 4096;
    private static final int MAX_ENTROPY_LENGTH = 4096;
    private static final int MAX_PERSONALIZATION_STRING = 4096;
    private Digest _digest;
    private long _reseedCounter;
    private EntropySource _entropySource;
    private int _securityStrength;
    private int _seedlen;
    private int _outlen;
    private ECCurve.Fp _curve;
    private ECPoint _P;
    private ECPoint _Q;
    private byte[] _s;
    private int _sLength;
    private ECMultiplier _fixedPointMultiplier;

    static {
        ECCurve.Fp curve = (ECCurve.Fp) NISTNamedCurves.getByName("P-256").getCurve();
        nistPoints[0] = new DualECPoints(128, curve.createPoint(p256_Px, p256_Py), curve.createPoint(p256_Qx, p256_Qy), 1);
        ECCurve.Fp curve2 = (ECCurve.Fp) NISTNamedCurves.getByName("P-384").getCurve();
        nistPoints[1] = new DualECPoints(192, curve2.createPoint(p384_Px, p384_Py), curve2.createPoint(p384_Qx, p384_Qy), 1);
        ECCurve.Fp curve3 = (ECCurve.Fp) NISTNamedCurves.getByName("P-521").getCurve();
        nistPoints[2] = new DualECPoints(256, curve3.createPoint(p521_Px, p521_Py), curve3.createPoint(p521_Qx, p521_Qy), 1);
    }

    public DualECSP800DRBG(Digest digest, int securityStrength, EntropySource entropySource, byte[] personalizationString, byte[] nonce) {
        this(nistPoints, digest, securityStrength, entropySource, personalizationString, nonce);
    }

    public DualECSP800DRBG(DualECPoints[] pointSet, Digest digest, int securityStrength, EntropySource entropySource, byte[] personalizationString, byte[] nonce) {
        this._fixedPointMultiplier = new FixedPointCombMultiplier();
        this._digest = digest;
        this._entropySource = entropySource;
        this._securityStrength = securityStrength;
        if (Utils.isTooLarge(personalizationString, 512)) {
            throw new IllegalArgumentException("Personalization string too large");
        }
        if (entropySource.entropySize() < securityStrength || entropySource.entropySize() > 4096) {
            throw new IllegalArgumentException("EntropySource must provide between " + securityStrength + " and 4096 bits");
        }
        byte[] entropy = getEntropy();
        byte[] seedMaterial = Arrays.concatenate(entropy, nonce, personalizationString);
        for (int i = 0; i != pointSet.length; i++) {
            if (securityStrength <= pointSet[i].getSecurityStrength()) {
                if (Utils.getMaxSecurityStrength(digest) < pointSet[i].getSecurityStrength()) {
                    throw new IllegalArgumentException("Requested security strength is not supported by digest");
                }
                this._seedlen = pointSet[i].getSeedLen();
                this._outlen = pointSet[i].getMaxOutlen() / 8;
                this._P = pointSet[i].getP();
                this._Q = pointSet[i].getQ();
                break;
            }
        }
        if (this._P == null) {
            throw new IllegalArgumentException("security strength cannot be greater than 256 bits");
        }
        this._s = Utils.hash_df(this._digest, seedMaterial, this._seedlen);
        this._sLength = this._s.length;
        this._reseedCounter = 0L;
    }

    @Override // org.spongycastle.crypto.prng.drbg.SP80090DRBG
    public int getBlockSize() {
        return this._outlen * 8;
    }

    @Override // org.spongycastle.crypto.prng.drbg.SP80090DRBG
    public int generate(byte[] output, byte[] additionalInput, boolean predictionResistant) {
        BigInteger s;
        int numberOfBits = output.length * 8;
        int m = output.length / this._outlen;
        if (Utils.isTooLarge(additionalInput, 512)) {
            throw new IllegalArgumentException("Additional input too large");
        }
        if (this._reseedCounter + ((long) m) > RESEED_MAX) {
            return -1;
        }
        if (predictionResistant) {
            reseed(additionalInput);
            additionalInput = null;
        }
        if (additionalInput != null) {
            s = new BigInteger(1, xor(this._s, Utils.hash_df(this._digest, additionalInput, this._seedlen)));
        } else {
            s = new BigInteger(1, this._s);
        }
        Arrays.fill(output, (byte) 0);
        int outOffset = 0;
        for (int i = 0; i < m; i++) {
            s = getScalarMultipleXCoord(this._P, s);
            byte[] r = getScalarMultipleXCoord(this._Q, s).toByteArray();
            if (r.length > this._outlen) {
                System.arraycopy(r, r.length - this._outlen, output, outOffset, this._outlen);
            } else {
                System.arraycopy(r, 0, output, outOffset + (this._outlen - r.length), r.length);
            }
            outOffset += this._outlen;
            this._reseedCounter++;
        }
        if (outOffset < output.length) {
            s = getScalarMultipleXCoord(this._P, s);
            byte[] r2 = getScalarMultipleXCoord(this._Q, s).toByteArray();
            int required = output.length - outOffset;
            if (r2.length > this._outlen) {
                System.arraycopy(r2, r2.length - this._outlen, output, outOffset, required);
            } else {
                System.arraycopy(r2, 0, output, outOffset + (this._outlen - r2.length), required);
            }
            this._reseedCounter++;
        }
        this._s = BigIntegers.asUnsignedByteArray(this._sLength, getScalarMultipleXCoord(this._P, s));
        return numberOfBits;
    }

    @Override // org.spongycastle.crypto.prng.drbg.SP80090DRBG
    public void reseed(byte[] additionalInput) {
        if (Utils.isTooLarge(additionalInput, 512)) {
            throw new IllegalArgumentException("Additional input string too large");
        }
        byte[] entropy = getEntropy();
        byte[] seedMaterial = Arrays.concatenate(pad8(this._s, this._seedlen), entropy, additionalInput);
        this._s = Utils.hash_df(this._digest, seedMaterial, this._seedlen);
        this._reseedCounter = 0L;
    }

    private byte[] getEntropy() {
        byte[] entropy = this._entropySource.getEntropy();
        if (entropy.length < (this._securityStrength + 7) / 8) {
            throw new IllegalStateException("Insufficient entropy provided by entropy source");
        }
        return entropy;
    }

    private byte[] xor(byte[] a, byte[] b) {
        if (b == null) {
            return a;
        }
        byte[] rv = new byte[a.length];
        for (int i = 0; i != rv.length; i++) {
            rv[i] = (byte) (a[i] ^ b[i]);
        }
        return rv;
    }

    private byte[] pad8(byte[] s, int seedlen) {
        if (seedlen % 8 == 0) {
            return s;
        }
        int shift = 8 - (seedlen % 8);
        int carry = 0;
        for (int i = s.length - 1; i >= 0; i--) {
            int b = s[i] & 255;
            s[i] = (byte) ((b << shift) | (carry >> (8 - shift)));
            carry = b;
        }
        return s;
    }

    private BigInteger getScalarMultipleXCoord(ECPoint p, BigInteger s) {
        return this._fixedPointMultiplier.multiply(p, s).normalize().getAffineXCoord().toBigInteger();
    }
}
