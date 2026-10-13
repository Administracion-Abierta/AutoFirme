package org.spongycastle.pqc.crypto.ntru;

import java.nio.ByteBuffer;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.pqc.math.ntru.polynomial.IntegerPolynomial;
import org.spongycastle.pqc.math.ntru.polynomial.Polynomial;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/crypto/ntru/NTRUSigner.class */
public class NTRUSigner {
    private NTRUSigningParameters params;
    private Digest hashAlg;
    private NTRUSigningPrivateKeyParameters signingKeyPair;
    private NTRUSigningPublicKeyParameters verificationKey;

    public NTRUSigner(NTRUSigningParameters params) {
        this.params = params;
    }

    public void init(boolean forSigning, CipherParameters params) {
        if (forSigning) {
            this.signingKeyPair = (NTRUSigningPrivateKeyParameters) params;
        } else {
            this.verificationKey = (NTRUSigningPublicKeyParameters) params;
        }
        this.hashAlg = this.params.hashAlg;
        this.hashAlg.reset();
    }

    public void update(byte b) {
        if (this.hashAlg == null) {
            throw new IllegalStateException("Call initSign or initVerify first!");
        }
        this.hashAlg.update(b);
    }

    public void update(byte[] m, int off, int length) {
        if (this.hashAlg == null) {
            throw new IllegalStateException("Call initSign or initVerify first!");
        }
        this.hashAlg.update(m, off, length);
    }

    public byte[] generateSignature() {
        if (this.hashAlg == null || this.signingKeyPair == null) {
            throw new IllegalStateException("Call initSign first!");
        }
        byte[] msgHash = new byte[this.hashAlg.getDigestSize()];
        this.hashAlg.doFinal(msgHash, 0);
        return signHash(msgHash, this.signingKeyPair);
    }

    private byte[] signHash(byte[] msgHash, NTRUSigningPrivateKeyParameters kp) {
        IntegerPolynomial i;
        IntegerPolynomial s;
        int r = 0;
        NTRUSigningPublicKeyParameters kPub = kp.getPublicKey();
        do {
            r++;
            if (r > this.params.signFailTolerance) {
                throw new IllegalStateException("Signing failed: too many retries (max=" + this.params.signFailTolerance + ")");
            }
            i = createMsgRep(msgHash, r);
            s = sign(i, kp);
        } while (!verify(i, s, kPub.h));
        byte[] rawSig = s.toBinary(this.params.q);
        ByteBuffer sbuf = ByteBuffer.allocate(rawSig.length + 4);
        sbuf.put(rawSig);
        sbuf.putInt(r);
        return sbuf.array();
    }

    private IntegerPolynomial sign(IntegerPolynomial i, NTRUSigningPrivateKeyParameters kp) {
        int N = this.params.N;
        int q = this.params.q;
        int perturbationBases = this.params.B;
        NTRUSigningPublicKeyParameters kPub = kp.getPublicKey();
        IntegerPolynomial s = new IntegerPolynomial(N);
        for (int iLoop = perturbationBases; iLoop >= 1; iLoop--) {
            Polynomial f = kp.getBasis(iLoop).f;
            Polynomial fPrime = kp.getBasis(iLoop).fPrime;
            IntegerPolynomial y = f.mult(i);
            y.div(q);
            IntegerPolynomial y2 = fPrime.mult(y);
            IntegerPolynomial x = fPrime.mult(i);
            x.div(q);
            y2.sub(f.mult(x));
            s.add(y2);
            IntegerPolynomial hi = (IntegerPolynomial) kp.getBasis(iLoop).h.clone();
            if (iLoop > 1) {
                hi.sub(kp.getBasis(iLoop - 1).h);
            } else {
                hi.sub(kPub.h);
            }
            i = y2.mult(hi, q);
        }
        Polynomial f2 = kp.getBasis(0).f;
        Polynomial fPrime2 = kp.getBasis(0).fPrime;
        IntegerPolynomial y3 = f2.mult(i);
        y3.div(q);
        IntegerPolynomial y4 = fPrime2.mult(y3);
        IntegerPolynomial x2 = fPrime2.mult(i);
        x2.div(q);
        y4.sub(f2.mult(x2));
        s.add(y4);
        s.modPositive(q);
        return s;
    }

    public boolean verifySignature(byte[] sig) {
        if (this.hashAlg == null || this.verificationKey == null) {
            throw new IllegalStateException("Call initVerify first!");
        }
        byte[] msgHash = new byte[this.hashAlg.getDigestSize()];
        this.hashAlg.doFinal(msgHash, 0);
        return verifyHash(msgHash, sig, this.verificationKey);
    }

    private boolean verifyHash(byte[] msgHash, byte[] sig, NTRUSigningPublicKeyParameters pub) {
        ByteBuffer sbuf = ByteBuffer.wrap(sig);
        byte[] rawSig = new byte[sig.length - 4];
        sbuf.get(rawSig);
        IntegerPolynomial s = IntegerPolynomial.fromBinary(rawSig, this.params.N, this.params.q);
        int r = sbuf.getInt();
        return verify(createMsgRep(msgHash, r), s, pub.h);
    }

    private boolean verify(IntegerPolynomial i, IntegerPolynomial s, IntegerPolynomial h) {
        int q = this.params.q;
        double normBoundSq = this.params.normBoundSq;
        double betaSq = this.params.betaSq;
        IntegerPolynomial t = h.mult(s, q);
        t.sub(i);
        long centeredNormSq = (long) (s.centeredNormSq(q) + (betaSq * t.centeredNormSq(q)));
        return ((double) centeredNormSq) <= normBoundSq;
    }

    protected IntegerPolynomial createMsgRep(byte[] msgHash, int r) {
        int N = this.params.N;
        int q = this.params.q;
        int c = 31 - Integer.numberOfLeadingZeros(q);
        int B = (c + 7) / 8;
        IntegerPolynomial i = new IntegerPolynomial(N);
        ByteBuffer cbuf = ByteBuffer.allocate(msgHash.length + 4);
        cbuf.put(msgHash);
        cbuf.putInt(r);
        NTRUSignerPrng prng = new NTRUSignerPrng(cbuf.array(), this.params.hashAlg);
        for (int t = 0; t < N; t++) {
            byte[] o = prng.nextBytes(B);
            int hi = o[o.length - 1] >> ((8 * B) - c);
            o[o.length - 1] = (byte) (hi << ((8 * B) - c));
            ByteBuffer obuf = ByteBuffer.allocate(4);
            obuf.put(o);
            obuf.rewind();
            i.coeffs[t] = Integer.reverseBytes(obuf.getInt());
        }
        return i;
    }
}
