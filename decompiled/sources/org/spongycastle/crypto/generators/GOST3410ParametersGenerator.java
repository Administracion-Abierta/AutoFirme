package org.spongycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.params.GOST3410Parameters;
import org.spongycastle.crypto.params.GOST3410ValidationParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/generators/GOST3410ParametersGenerator.class */
public class GOST3410ParametersGenerator {
    private int size;
    private int typeproc;
    private SecureRandom init_random;
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);

    public void init(int size, int typeproc, SecureRandom random) {
        this.size = size;
        this.typeproc = typeproc;
        this.init_random = random;
    }

    private int procedure_A(int x0, int c, BigInteger[] pq, int size) {
        while (true) {
            if (x0 >= 0 && x0 <= 65536) {
                break;
            }
            x0 = this.init_random.nextInt() / 32768;
        }
        while (true) {
            if (c >= 0 && c <= 65536 && c / 2 != 0) {
                break;
            }
            c = (this.init_random.nextInt() / 32768) + 1;
        }
        BigInteger C = new BigInteger(Integer.toString(c));
        BigInteger constA16 = new BigInteger("19381");
        BigInteger[] y = {new BigInteger(Integer.toString(x0))};
        int[] t = {size};
        int s = 0;
        for (int i = 0; t[i] >= 17; i++) {
            int[] tmp_t = new int[t.length + 1];
            System.arraycopy(t, 0, tmp_t, 0, t.length);
            t = new int[tmp_t.length];
            System.arraycopy(tmp_t, 0, t, 0, tmp_t.length);
            t[i + 1] = t[i] / 2;
            s = i + 1;
        }
        BigInteger[] p = new BigInteger[s + 1];
        p[s] = new BigInteger("8003", 16);
        int m = s - 1;
        for (int i2 = 0; i2 < s; i2++) {
            int rm = t[m] / 16;
            while (true) {
                BigInteger[] tmp_y = new BigInteger[y.length];
                System.arraycopy(y, 0, tmp_y, 0, y.length);
                y = new BigInteger[rm + 1];
                System.arraycopy(tmp_y, 0, y, 0, tmp_y.length);
                for (int j = 0; j < rm; j++) {
                    y[j + 1] = y[j].multiply(constA16).add(C).mod(TWO.pow(16));
                }
                BigInteger Ym = new BigInteger("0");
                for (int j2 = 0; j2 < rm; j2++) {
                    Ym = Ym.add(y[j2].multiply(TWO.pow(16 * j2)));
                }
                y[0] = y[rm];
                BigInteger N = TWO.pow(t[m] - 1).divide(p[m + 1]).add(TWO.pow(t[m] - 1).multiply(Ym).divide(p[m + 1].multiply(TWO.pow(16 * rm))));
                if (N.mod(TWO).compareTo(ONE) == 0) {
                    N = N.add(ONE);
                }
                int k = 0;
                while (true) {
                    p[m] = p[m + 1].multiply(N.add(BigInteger.valueOf(k))).add(ONE);
                    if (p[m].compareTo(TWO.pow(t[m])) != 1) {
                        if (TWO.modPow(p[m + 1].multiply(N.add(BigInteger.valueOf(k))), p[m]).compareTo(ONE) == 0 && TWO.modPow(N.add(BigInteger.valueOf(k)), p[m]).compareTo(ONE) != 0) {
                            break;
                        }
                        k += 2;
                    }
                }
            }
            m--;
            if (m < 0) {
                pq[0] = p[0];
                pq[1] = p[1];
                return y[0].intValue();
            }
        }
        return y[0].intValue();
    }

    private long procedure_Aa(long x0, long c, BigInteger[] pq, int size) {
        while (true) {
            if (x0 >= 0 && x0 <= 4294967296L) {
                break;
            }
            x0 = this.init_random.nextInt() * 2;
        }
        while (true) {
            if (c >= 0 && c <= 4294967296L && c / 2 != 0) {
                break;
            }
            c = (this.init_random.nextInt() * 2) + 1;
        }
        BigInteger C = new BigInteger(Long.toString(c));
        BigInteger constA32 = new BigInteger("97781173");
        BigInteger[] y = {new BigInteger(Long.toString(x0))};
        int[] t = {size};
        int s = 0;
        for (int i = 0; t[i] >= 33; i++) {
            int[] tmp_t = new int[t.length + 1];
            System.arraycopy(t, 0, tmp_t, 0, t.length);
            t = new int[tmp_t.length];
            System.arraycopy(tmp_t, 0, t, 0, tmp_t.length);
            t[i + 1] = t[i] / 2;
            s = i + 1;
        }
        BigInteger[] p = new BigInteger[s + 1];
        p[s] = new BigInteger("8000000B", 16);
        int m = s - 1;
        for (int i2 = 0; i2 < s; i2++) {
            int rm = t[m] / 32;
            while (true) {
                BigInteger[] tmp_y = new BigInteger[y.length];
                System.arraycopy(y, 0, tmp_y, 0, y.length);
                y = new BigInteger[rm + 1];
                System.arraycopy(tmp_y, 0, y, 0, tmp_y.length);
                for (int j = 0; j < rm; j++) {
                    y[j + 1] = y[j].multiply(constA32).add(C).mod(TWO.pow(32));
                }
                BigInteger Ym = new BigInteger("0");
                for (int j2 = 0; j2 < rm; j2++) {
                    Ym = Ym.add(y[j2].multiply(TWO.pow(32 * j2)));
                }
                y[0] = y[rm];
                BigInteger N = TWO.pow(t[m] - 1).divide(p[m + 1]).add(TWO.pow(t[m] - 1).multiply(Ym).divide(p[m + 1].multiply(TWO.pow(32 * rm))));
                if (N.mod(TWO).compareTo(ONE) == 0) {
                    N = N.add(ONE);
                }
                int k = 0;
                while (true) {
                    p[m] = p[m + 1].multiply(N.add(BigInteger.valueOf(k))).add(ONE);
                    if (p[m].compareTo(TWO.pow(t[m])) != 1) {
                        if (TWO.modPow(p[m + 1].multiply(N.add(BigInteger.valueOf(k))), p[m]).compareTo(ONE) == 0 && TWO.modPow(N.add(BigInteger.valueOf(k)), p[m]).compareTo(ONE) != 0) {
                            break;
                        }
                        k += 2;
                    }
                }
            }
            m--;
            if (m < 0) {
                pq[0] = p[0];
                pq[1] = p[1];
                return y[0].longValue();
            }
        }
        return y[0].longValue();
    }

    private void procedure_B(int x0, int c, BigInteger[] pq) {
        while (true) {
            if (x0 >= 0 && x0 <= 65536) {
                break;
            } else {
                x0 = this.init_random.nextInt() / 32768;
            }
        }
        while (true) {
            if (c >= 0 && c <= 65536 && c / 2 != 0) {
                break;
            } else {
                c = (this.init_random.nextInt() / 32768) + 1;
            }
        }
        BigInteger[] qp = new BigInteger[2];
        BigInteger C = new BigInteger(Integer.toString(c));
        BigInteger constA16 = new BigInteger("19381");
        int x1 = procedure_A(x0, c, qp, 256);
        BigInteger q = qp[0];
        int x2 = procedure_A(x1, c, qp, 512);
        BigInteger Q = qp[0];
        BigInteger[] y = new BigInteger[65];
        y[0] = new BigInteger(Integer.toString(x2));
        while (true) {
            for (int j = 0; j < 64; j++) {
                y[j + 1] = y[j].multiply(constA16).add(C).mod(TWO.pow(16));
            }
            BigInteger Y = new BigInteger("0");
            for (int j2 = 0; j2 < 64; j2++) {
                Y = Y.add(y[j2].multiply(TWO.pow(16 * j2)));
            }
            y[0] = y[64];
            BigInteger N = TWO.pow(1024 - 1).divide(q.multiply(Q)).add(TWO.pow(1024 - 1).multiply(Y).divide(q.multiply(Q).multiply(TWO.pow(1024))));
            if (N.mod(TWO).compareTo(ONE) == 0) {
                N = N.add(ONE);
            }
            int k = 0;
            while (true) {
                BigInteger p = q.multiply(Q).multiply(N.add(BigInteger.valueOf(k))).add(ONE);
                if (p.compareTo(TWO.pow(1024)) == 1) {
                    break;
                }
                if (TWO.modPow(q.multiply(Q).multiply(N.add(BigInteger.valueOf(k))), p).compareTo(ONE) == 0 && TWO.modPow(q.multiply(N.add(BigInteger.valueOf(k))), p).compareTo(ONE) != 0) {
                    pq[0] = p;
                    pq[1] = q;
                    return;
                }
                k += 2;
            }
        }
    }

    private void procedure_Bb(long x0, long c, BigInteger[] pq) {
        while (true) {
            if (x0 >= 0 && x0 <= 4294967296L) {
                break;
            } else {
                x0 = this.init_random.nextInt() * 2;
            }
        }
        while (true) {
            if (c >= 0 && c <= 4294967296L && c / 2 != 0) {
                break;
            } else {
                c = (this.init_random.nextInt() * 2) + 1;
            }
        }
        BigInteger[] qp = new BigInteger[2];
        BigInteger C = new BigInteger(Long.toString(c));
        BigInteger constA32 = new BigInteger("97781173");
        long x1 = procedure_Aa(x0, c, qp, 256);
        BigInteger q = qp[0];
        long x2 = procedure_Aa(x1, c, qp, 512);
        BigInteger Q = qp[0];
        BigInteger[] y = new BigInteger[33];
        y[0] = new BigInteger(Long.toString(x2));
        while (true) {
            for (int j = 0; j < 32; j++) {
                y[j + 1] = y[j].multiply(constA32).add(C).mod(TWO.pow(32));
            }
            BigInteger Y = new BigInteger("0");
            for (int j2 = 0; j2 < 32; j2++) {
                Y = Y.add(y[j2].multiply(TWO.pow(32 * j2)));
            }
            y[0] = y[32];
            BigInteger N = TWO.pow(1024 - 1).divide(q.multiply(Q)).add(TWO.pow(1024 - 1).multiply(Y).divide(q.multiply(Q).multiply(TWO.pow(1024))));
            if (N.mod(TWO).compareTo(ONE) == 0) {
                N = N.add(ONE);
            }
            int k = 0;
            while (true) {
                BigInteger p = q.multiply(Q).multiply(N.add(BigInteger.valueOf(k))).add(ONE);
                if (p.compareTo(TWO.pow(1024)) == 1) {
                    break;
                }
                if (TWO.modPow(q.multiply(Q).multiply(N.add(BigInteger.valueOf(k))), p).compareTo(ONE) == 0 && TWO.modPow(q.multiply(N.add(BigInteger.valueOf(k))), p).compareTo(ONE) != 0) {
                    pq[0] = p;
                    pq[1] = q;
                    return;
                }
                k += 2;
            }
        }
    }

    private BigInteger procedure_C(BigInteger p, BigInteger q) {
        BigInteger pSub1 = p.subtract(ONE);
        BigInteger pSub1DivQ = pSub1.divide(q);
        int length = p.bitLength();
        while (true) {
            BigInteger d = new BigInteger(length, this.init_random);
            if (d.compareTo(ONE) > 0 && d.compareTo(pSub1) < 0) {
                BigInteger a = d.modPow(pSub1DivQ, p);
                if (a.compareTo(ONE) != 0) {
                    return a;
                }
            }
        }
    }

    public GOST3410Parameters generateParameters() {
        BigInteger[] pq = new BigInteger[2];
        if (this.typeproc == 1) {
            int x0 = this.init_random.nextInt();
            int c = this.init_random.nextInt();
            switch (this.size) {
                case 512:
                    procedure_A(x0, c, pq, 512);
                    break;
                case 1024:
                    procedure_B(x0, c, pq);
                    break;
                default:
                    throw new IllegalArgumentException("Ooops! key size 512 or 1024 bit.");
            }
            BigInteger p = pq[0];
            BigInteger q = pq[1];
            BigInteger a = procedure_C(p, q);
            return new GOST3410Parameters(p, q, a, new GOST3410ValidationParameters(x0, c));
        }
        long x0L = this.init_random.nextLong();
        long cL = this.init_random.nextLong();
        switch (this.size) {
            case 512:
                procedure_Aa(x0L, cL, pq, 512);
                break;
            case 1024:
                procedure_Bb(x0L, cL, pq);
                break;
            default:
                throw new IllegalStateException("Ooops! key size 512 or 1024 bit.");
        }
        BigInteger p2 = pq[0];
        BigInteger q2 = pq[1];
        BigInteger a2 = procedure_C(p2, q2);
        return new GOST3410Parameters(p2, q2, a2, new GOST3410ValidationParameters(x0L, cL));
    }
}
