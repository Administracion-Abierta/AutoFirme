package org.spongycastle.pqc.crypto.ntru;

import com.sun.jna.platform.win32.WinError;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.digests.SHA512Digest;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/ntru/NTRUSigningKeyGenerationParameters.class */
public class NTRUSigningKeyGenerationParameters extends KeyGenerationParameters implements Cloneable {
    public static final int BASIS_TYPE_STANDARD = 0;
    public static final int BASIS_TYPE_TRANSPOSE = 1;
    public static final int KEY_GEN_ALG_RESULTANT = 0;
    public static final int KEY_GEN_ALG_FLOAT = 1;
    public static final NTRUSigningKeyGenerationParameters APR2011_439 = new NTRUSigningKeyGenerationParameters(439, 2048, 146, 1, 1, 0.165d, 490.0d, 280.0d, false, true, 0, new SHA256Digest());
    public static final NTRUSigningKeyGenerationParameters APR2011_439_PROD = new NTRUSigningKeyGenerationParameters(439, 2048, 9, 8, 5, 1, 1, 0.165d, 490.0d, 280.0d, false, true, 0, new SHA256Digest());
    public static final NTRUSigningKeyGenerationParameters APR2011_743 = new NTRUSigningKeyGenerationParameters(WinError.ERROR_VOLUME_MOUNTED, 2048, 248, 1, 1, 0.127d, 560.0d, 360.0d, true, false, 0, new SHA512Digest());
    public static final NTRUSigningKeyGenerationParameters APR2011_743_PROD = new NTRUSigningKeyGenerationParameters(WinError.ERROR_VOLUME_MOUNTED, 2048, 11, 11, 15, 1, 1, 0.127d, 560.0d, 360.0d, true, false, 0, new SHA512Digest());
    public static final NTRUSigningKeyGenerationParameters TEST157 = new NTRUSigningKeyGenerationParameters(157, 256, 29, 1, 1, 0.38d, 200.0d, 80.0d, false, false, 0, new SHA256Digest());
    public static final NTRUSigningKeyGenerationParameters TEST157_PROD = new NTRUSigningKeyGenerationParameters(157, 256, 5, 5, 8, 1, 1, 0.38d, 200.0d, 80.0d, false, false, 0, new SHA256Digest());
    public int N;
    public int q;
    public int d;
    public int d1;
    public int d2;
    public int d3;
    public int B;
    double beta;
    public double betaSq;
    double normBound;
    public double normBoundSq;
    public int signFailTolerance;
    double keyNormBound;
    public double keyNormBoundSq;
    public boolean primeCheck;
    public int basisType;
    int bitsF;
    public boolean sparse;
    public int keyGenAlg;
    public Digest hashAlg;
    public int polyType;

    public NTRUSigningKeyGenerationParameters(int N, int q, int d, int B, int basisType, double beta, double normBound, double keyNormBound, boolean primeCheck, boolean sparse, int keyGenAlg, Digest hashAlg) {
        super(new SecureRandom(), N);
        this.signFailTolerance = 100;
        this.bitsF = 6;
        this.N = N;
        this.q = q;
        this.d = d;
        this.B = B;
        this.basisType = basisType;
        this.beta = beta;
        this.normBound = normBound;
        this.keyNormBound = keyNormBound;
        this.primeCheck = primeCheck;
        this.sparse = sparse;
        this.keyGenAlg = keyGenAlg;
        this.hashAlg = hashAlg;
        this.polyType = 0;
        init();
    }

    public NTRUSigningKeyGenerationParameters(int N, int q, int d1, int d2, int d3, int B, int basisType, double beta, double normBound, double keyNormBound, boolean primeCheck, boolean sparse, int keyGenAlg, Digest hashAlg) {
        super(new SecureRandom(), N);
        this.signFailTolerance = 100;
        this.bitsF = 6;
        this.N = N;
        this.q = q;
        this.d1 = d1;
        this.d2 = d2;
        this.d3 = d3;
        this.B = B;
        this.basisType = basisType;
        this.beta = beta;
        this.normBound = normBound;
        this.keyNormBound = keyNormBound;
        this.primeCheck = primeCheck;
        this.sparse = sparse;
        this.keyGenAlg = keyGenAlg;
        this.hashAlg = hashAlg;
        this.polyType = 1;
        init();
    }

    private void init() {
        this.betaSq = this.beta * this.beta;
        this.normBoundSq = this.normBound * this.normBound;
        this.keyNormBoundSq = this.keyNormBound * this.keyNormBound;
    }

    public NTRUSigningKeyGenerationParameters(InputStream is) throws IOException {
        super(new SecureRandom(), 0);
        this.signFailTolerance = 100;
        this.bitsF = 6;
        DataInputStream dis = new DataInputStream(is);
        this.N = dis.readInt();
        this.q = dis.readInt();
        this.d = dis.readInt();
        this.d1 = dis.readInt();
        this.d2 = dis.readInt();
        this.d3 = dis.readInt();
        this.B = dis.readInt();
        this.basisType = dis.readInt();
        this.beta = dis.readDouble();
        this.normBound = dis.readDouble();
        this.keyNormBound = dis.readDouble();
        this.signFailTolerance = dis.readInt();
        this.primeCheck = dis.readBoolean();
        this.sparse = dis.readBoolean();
        this.bitsF = dis.readInt();
        this.keyGenAlg = dis.read();
        String alg = dis.readUTF();
        if ("SHA-512".equals(alg)) {
            this.hashAlg = new SHA512Digest();
        } else if ("SHA-256".equals(alg)) {
            this.hashAlg = new SHA256Digest();
        }
        this.polyType = dis.read();
        init();
    }

    public void writeTo(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(this.N);
        dos.writeInt(this.q);
        dos.writeInt(this.d);
        dos.writeInt(this.d1);
        dos.writeInt(this.d2);
        dos.writeInt(this.d3);
        dos.writeInt(this.B);
        dos.writeInt(this.basisType);
        dos.writeDouble(this.beta);
        dos.writeDouble(this.normBound);
        dos.writeDouble(this.keyNormBound);
        dos.writeInt(this.signFailTolerance);
        dos.writeBoolean(this.primeCheck);
        dos.writeBoolean(this.sparse);
        dos.writeInt(this.bitsF);
        dos.write(this.keyGenAlg);
        dos.writeUTF(this.hashAlg.getAlgorithmName());
        dos.write(this.polyType);
    }

    public NTRUSigningParameters getSigningParameters() {
        return new NTRUSigningParameters(this.N, this.q, this.d, this.B, this.beta, this.normBound, this.hashAlg);
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public NTRUSigningKeyGenerationParameters m2869clone() {
        if (this.polyType == 0) {
            return new NTRUSigningKeyGenerationParameters(this.N, this.q, this.d, this.B, this.basisType, this.beta, this.normBound, this.keyNormBound, this.primeCheck, this.sparse, this.keyGenAlg, this.hashAlg);
        }
        return new NTRUSigningKeyGenerationParameters(this.N, this.q, this.d1, this.d2, this.d3, this.B, this.basisType, this.beta, this.normBound, this.keyNormBound, this.primeCheck, this.sparse, this.keyGenAlg, this.hashAlg);
    }

    public int hashCode() {
        int result = (31 * 1) + this.B;
        int result2 = (31 * ((31 * result) + this.N)) + this.basisType;
        long temp = Double.doubleToLongBits(this.beta);
        int result3 = (31 * result2) + ((int) (temp ^ (temp >>> 32)));
        long temp2 = Double.doubleToLongBits(this.betaSq);
        int result4 = (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * result3) + ((int) (temp2 ^ (temp2 >>> 32))))) + this.bitsF)) + this.d)) + this.d1)) + this.d2)) + this.d3)) + (this.hashAlg == null ? 0 : this.hashAlg.getAlgorithmName().hashCode()))) + this.keyGenAlg;
        long temp3 = Double.doubleToLongBits(this.keyNormBound);
        int result5 = (31 * result4) + ((int) (temp3 ^ (temp3 >>> 32)));
        long temp4 = Double.doubleToLongBits(this.keyNormBoundSq);
        int result6 = (31 * result5) + ((int) (temp4 ^ (temp4 >>> 32)));
        long temp5 = Double.doubleToLongBits(this.normBound);
        int result7 = (31 * result6) + ((int) (temp5 ^ (temp5 >>> 32)));
        long temp6 = Double.doubleToLongBits(this.normBoundSq);
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * result7) + ((int) (temp6 ^ (temp6 >>> 32))))) + this.polyType)) + (this.primeCheck ? WinError.ERROR_NETWORK_UNREACHABLE : WinError.ERROR_RETRY))) + this.q)) + this.signFailTolerance)) + (this.sparse ? WinError.ERROR_NETWORK_UNREACHABLE : WinError.ERROR_RETRY);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof NTRUSigningKeyGenerationParameters)) {
            return false;
        }
        NTRUSigningKeyGenerationParameters other = (NTRUSigningKeyGenerationParameters) obj;
        if (this.B != other.B || this.N != other.N || this.basisType != other.basisType || Double.doubleToLongBits(this.beta) != Double.doubleToLongBits(other.beta) || Double.doubleToLongBits(this.betaSq) != Double.doubleToLongBits(other.betaSq) || this.bitsF != other.bitsF || this.d != other.d || this.d1 != other.d1 || this.d2 != other.d2 || this.d3 != other.d3) {
            return false;
        }
        if (this.hashAlg == null) {
            if (other.hashAlg != null) {
                return false;
            }
        } else if (!this.hashAlg.getAlgorithmName().equals(other.hashAlg.getAlgorithmName())) {
            return false;
        }
        if (this.keyGenAlg != other.keyGenAlg || Double.doubleToLongBits(this.keyNormBound) != Double.doubleToLongBits(other.keyNormBound) || Double.doubleToLongBits(this.keyNormBoundSq) != Double.doubleToLongBits(other.keyNormBoundSq) || Double.doubleToLongBits(this.normBound) != Double.doubleToLongBits(other.normBound) || Double.doubleToLongBits(this.normBoundSq) != Double.doubleToLongBits(other.normBoundSq) || this.polyType != other.polyType || this.primeCheck != other.primeCheck || this.q != other.q || this.signFailTolerance != other.signFailTolerance || this.sparse != other.sparse) {
            return false;
        }
        return true;
    }

    public String toString() {
        DecimalFormat format = new DecimalFormat("0.00");
        StringBuilder output = new StringBuilder("SignatureParameters(N=" + this.N + " q=" + this.q);
        if (this.polyType == 0) {
            output.append(" polyType=SIMPLE d=" + this.d);
        } else {
            output.append(" polyType=PRODUCT d1=" + this.d1 + " d2=" + this.d2 + " d3=" + this.d3);
        }
        output.append(" B=" + this.B + " basisType=" + this.basisType + " beta=" + format.format(this.beta) + " normBound=" + format.format(this.normBound) + " keyNormBound=" + format.format(this.keyNormBound) + " prime=" + this.primeCheck + " sparse=" + this.sparse + " keyGenAlg=" + this.keyGenAlg + " hashAlg=" + this.hashAlg + ")");
        return output.toString();
    }
}
