package org.spongycastle.pqc.math.ntru.polynomial;

import com.sun.jna.platform.win32.GL;
import com.sun.jna.platform.win32.WinError;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import org.spongycastle.pqc.math.ntru.euclid.BigIntEuclidean;
import org.spongycastle.pqc.math.ntru.util.ArrayEncoder;
import org.spongycastle.pqc.math.ntru.util.Util;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/ntru/polynomial/IntegerPolynomial.class */
public class IntegerPolynomial implements Polynomial {
    private static final int NUM_EQUAL_RESULTANTS = 3;
    private static final int[] PRIMES = {4507, 4513, 4517, 4519, 4523, 4547, 4549, 4561, 4567, 4583, 4591, 4597, 4603, 4621, 4637, 4639, 4643, 4649, 4651, 4657, 4663, 4673, 4679, 4691, 4703, 4721, 4723, 4729, 4733, 4751, 4759, 4783, 4787, 4789, 4793, 4799, 4801, 4813, 4817, 4831, 4861, 4871, 4877, 4889, 4903, 4909, 4919, 4931, 4933, 4937, 4943, 4951, 4957, 4967, 4969, 4973, 4987, 4993, 4999, WinError.ERROR_DEPENDENCY_ALREADY_EXISTS, WinError.ERROR_CANT_EVICT_ACTIVE_NODE, WinError.ERROR_OBJECT_IN_LIST, WinError.ERROR_NOT_QUORUM_CAPABLE, WinError.ERROR_INVALID_STATE, WinError.ERROR_CLUSTER_INVALID_NODE, WinError.ERROR_CLUSTER_NODE_UNREACHABLE, WinError.ERROR_CLUSTER_NO_SECURITY_CONTEXT, WinError.ERROR_CLUSTER_SYSTEM_CONFIG_CHANGED, WinError.ERROR_CLUSTER_NO_RPC_PACKAGES_REGISTERED, WinError.ERROR_DATABASE_BACKUP_CORRUPT, 5099, 5101, 5107, 5113, 5119, 5147, 5153, 5167, 5171, 5179, 5189, 5197, 5209, 5227, 5231, 5233, 5237, 5261, 5273, 5279, 5281, 5297, 5303, 5309, 5323, 5333, 5347, 5351, 5381, 5387, 5393, 5399, 5407, 5413, 5417, 5419, 5431, 5437, 5441, 5443, 5449, 5471, 5477, 5479, 5483, 5501, 5503, 5507, 5519, 5521, 5527, 5531, 5557, 5563, 5569, 5573, 5581, 5591, 5623, 5639, 5641, 5647, 5651, 5653, 5657, 5659, 5669, 5683, 5689, 5693, 5701, 5711, 5717, 5737, 5741, 5743, 5749, 5779, 5783, 5791, 5801, 5807, 5813, 5821, 5827, 5839, 5843, 5849, 5851, 5857, 5861, 5867, 5869, 5879, 5881, WinError.ERROR_CLUSTER_PARAMETER_MISMATCH, WinError.ERROR_CLUSCFG_SYSTEM_DISK_DRIVE_LETTER_CONFLICT, WinError.ERROR_CLUSTER_CANT_DESERIALIZE_DATA, WinError.ERROR_CLUSTER_INVALID_IPV6_TUNNEL_NETWORK, 5939, 5953, 5981, 5987, WinError.ERROR_FILE_NOT_ENCRYPTED, WinError.ERROR_EFS_SERVER_NOT_TRUSTED, 6029, 6037, 6043, 6047, 6053, 6067, 6073, 6079, 6089, 6091, 6101, 6113, 6121, 6131, 6133, 6143, 6151, 6163, 6173, 6197, 6199, 6203, 6211, 6217, 6221, 6229, 6247, 6257, 6263, 6269, 6271, 6277, 6287, 6299, 6301, 6311, 6317, 6323, 6329, 6337, 6343, 6353, 6359, 6361, 6367, 6373, 6379, 6389, 6397, 6421, 6427, 6449, 6451, 6469, 6473, 6481, 6491, 6521, 6529, 6547, 6551, 6553, 6563, 6569, 6571, 6577, 6581, 6599, WinError.ERROR_LOG_RESTART_INVALID, WinError.ERROR_LOG_POLICY_ALREADY_INSTALLED, WinError.ERROR_LOG_CLIENT_NOT_REGISTERED, 6653, 6659, 6661, 6673, 6679, 6689, 6691, WinError.ERROR_TRANSACTION_NOT_ACTIVE, WinError.ERROR_TRANSACTION_NOT_REQUESTED, WinError.ERROR_TRANSACTION_SUPERIOR_EXISTS, WinError.ERROR_TRANSACTIONMANAGER_NOT_ONLINE, 6733, 6737, 6761, 6763, 6779, 6781, 6791, 6793, WinError.ERROR_DIRECTORY_NOT_RM, WinError.ERROR_FILE_IDENTITY_NOT_PERSISTENT, WinError.ERROR_INDOUBT_TRANSACTIONS_EXIST, WinError.ERROR_ROLLBACK_TIMER_EXPIRED, WinError.ERROR_LOG_GROWTH_FAILED, WinError.ERROR_NOT_SNAPSHOT_VOLUME, 6857, 6863, 6869, 6871, 6883, 6899, 6907, 6911, 6917, 6947, 6949, 6959, 6961, 6967, 6971, 6977, 6983, 6991, 6997, WinError.ERROR_CTX_WINSTATION_NAME_INVALID, WinError.ERROR_CTX_MODEM_RESPONSE_NO_CARRIER, 7019, 7027, 7039, 7043, WinError.ERROR_CTX_SHADOW_NOT_RUNNING, WinError.ERROR_TS_INCOMPATIBLE_SESSIONS, 7079, 7103, 7109, 7121, 7127, 7129, 7151, 7159, 7177, 7187, 7193, 7207, 7211, 7213, 7219, 7229, 7237, 7243, 7247, 7253, 7283, 7297, 7307, 7309, 7321, 7331, 7333, 7349, 7351, 7369, 7393, 7411, 7417, 7433, 7451, 7457, 7459, 7477, 7481, 7487, 7489, 7499, 7507, 7517, 7523, 7529, 7537, 7541, 7547, 7549, 7559, 7561, 7573, 7577, 7583, 7589, 7591, 7603, 7607, 7621, 7639, 7643, 7649, 7669, 7673, 7681, 7687, 7691, 7699, 7703, 7717, 7723, 7727, 7741, 7753, 7757, 7759, 7789, 7793, 7817, 7823, 7829, 7841, 7853, 7867, 7873, 7877, 7879, 7883, 7901, 7907, 7919, 7927, 7933, GL.GL_RENDERER, 7949, 7951, 7963, 7993, WinError.FRS_ERR_PARENT_INSUFFICIENT_PRIV, WinError.FRS_ERR_CHILD_TO_PARENT_COMM, WinError.FRS_ERR_INVALID_SERVICE_PARAMETER, 8039, 8053, 8059, 8069, 8081, 8087, 8089, 8093, 8101, 8111, 8117, 8123, 8147, 8161, 8167, 8171, 8179, 8191, WinError.ERROR_DS_NO_MORE_RIDS, WinError.ERROR_POLICY_OBJECT_NOT_FOUND, WinError.ERROR_PROMOTION_ACTIVE, WinError.ERROR_DS_AUTH_METHOD_NOT_SUPPORTED, WinError.ERROR_DS_INAPPROPRIATE_AUTH, WinError.ERROR_DS_CONFIDENTIALITY_REQUIRED, WinError.ERROR_DS_IS_LEAF, 8263, 8269, 8273, 8287, 8291, 8293, 8297, WinError.ERROR_DS_ILLEGAL_MOD_OPERATION, WinError.ERROR_DS_ATT_NOT_DEF_FOR_CLASS, WinError.ERROR_DS_NO_PARENT_OBJECT, WinError.ERROR_DS_SEC_DESC_TOO_SHORT, WinError.ERROR_DS_NO_CROSSREF_FOR_NC, WinError.ERROR_DS_CANT_MOD_SYSTEM_ONLY, WinError.ERROR_DS_NOTIFY_FILTER_TOO_COMPLEX, WinError.ERROR_DS_NONEXISTENT_MAY_HAVE, WinError.ERROR_DS_AUX_CLS_TEST_FAIL, WinError.ERROR_DS_CANT_FIND_DSA_OBJ, WinError.ERROR_DS_SECURITY_ILLEGAL_MODIFY, WinError.ERROR_DS_HIERARCHY_TABLE_MALLOC_FAILED, WinError.ERROR_DS_UNKNOWN_ERROR, WinError.ERROR_DS_DRA_INCONSISTENT_DIT, WinError.ERROR_DS_DRA_MAIL_PROBLEM, WinError.ERROR_DS_DRA_PREEMPTED, WinError.ERROR_DS_INSTALL_SCHEMA_MISMATCH, WinError.ERROR_DS_NO_TREE_DELETE_ABOVE_NC, WinError.ERROR_DS_INVALID_GROUP_TYPE, WinError.ERROR_DS_HAVE_PRIMARY_MEMBERS, WinError.ERROR_DS_KEY_NOT_UNIQUE, WinError.ERROR_DS_CANT_FIND_DC_FOR_SRC_DOMAIN, WinError.ERROR_DS_SRC_SID_EXISTS_IN_FOREST, WinError.ERROR_DS_DRA_SCHEMA_CONFLICT, WinError.ERROR_DS_FOREST_VERSION_TOO_HIGH, WinError.ERROR_DS_OUT_OF_VERSION_STORE, WinError.ERROR_DS_MODIFYDN_DISALLOWED_BY_FLAG, WinError.ERROR_DS_DUP_MSDS_INTID, WinError.ERROR_DS_AUTHORIZATION_FAILED, WinError.ERROR_DS_CANT_MOVE_APP_QUERY_GROUP, WinError.ERROR_DS_NO_NTDSA_OBJECT, WinError.ERROR_DS_INVALID_SEARCH_FLAG_TUPLE, 8629, 8641, 8647, 8663, 8669, 8677, 8681, 8689, 8693, 8699, 8707, 8713, 8719, 8731, 8737, 8741, 8747, 8753, 8761, 8779, 8783, 8803, 8807, 8819, 8821, 8831, 8837, 8839, 8849, 8861, 8863, 8867, 8887, 8893, 8923, 8929, 8933, 8941, 8951, 8963, 8969, 8971, 8999, WinError.DNS_ERROR_RCODE_FORMAT_ERROR, WinError.DNS_ERROR_RCODE_YXRRSET, 9011, 9013, 9029, 9041, 9043, 9049, 9059, 9067, 9091, 9103, 9109, 9127, 9133, 9137, 9151, 9157, 9161, 9173, 9181, 9187, 9199, 9203, 9209, 9221, 9227, 9239, 9241, 9257, 9277, 9281, 9283, 9293, 9311, 9319, 9323, 9337, 9341, 9343, 9349, 9371, 9377, 9391, 9397, 9403, 9413, 9419, 9421, 9431, 9433, 9437, 9439, 9461, 9463, 9467, 9473, 9479, 9491, 9497, 9511, 9521, 9533, 9539, 9547, WinError.DNS_ERROR_INVALID_TYPE, 9587, WinError.DNS_ERROR_ZONE_DOES_NOT_EXIST, WinError.DNS_ERROR_ZONE_NOT_SECONDARY, WinError.DNS_ERROR_FORWARDER_ALREADY_EXISTS, 9623, 9629, 9631, 9643, 9649, 9661, 9677, 9679, 9689, 9697, WinError.DNS_ERROR_NO_BOOTFILE_IF_DS_ZONE, WinError.DNS_ERROR_DNAME_COLLISION, 9733, 9739, 9743, 9749, 9767, 9769, 9781, 9787, 9791, 9803, 9811, 9817, 9829, 9833, 9839, WinError.DNS_ERROR_NO_TCPIP, 9857, 9859, 9871, 9883, 9887, WinError.DNS_ERROR_DP_DOES_NOT_EXIST, 9907, 9923, 9929, 9931, 9941, 9949, 9967, 9973};
    private static final List BIGINT_PRIMES = new ArrayList();
    public int[] coeffs;

    static {
        for (int i = 0; i != PRIMES.length; i++) {
            BIGINT_PRIMES.add(BigInteger.valueOf(PRIMES[i]));
        }
    }

    public IntegerPolynomial(int N) {
        this.coeffs = new int[N];
    }

    public IntegerPolynomial(int[] coeffs) {
        this.coeffs = coeffs;
    }

    public IntegerPolynomial(BigIntPolynomial p) {
        this.coeffs = new int[p.coeffs.length];
        for (int i = 0; i < p.coeffs.length; i++) {
            this.coeffs[i] = p.coeffs[i].intValue();
        }
    }

    public static IntegerPolynomial fromBinary3Sves(byte[] data, int N) {
        return new IntegerPolynomial(ArrayEncoder.decodeMod3Sves(data, N));
    }

    public static IntegerPolynomial fromBinary3Tight(byte[] b, int N) {
        return new IntegerPolynomial(ArrayEncoder.decodeMod3Tight(b, N));
    }

    public static IntegerPolynomial fromBinary3Tight(InputStream is, int N) throws IOException {
        return new IntegerPolynomial(ArrayEncoder.decodeMod3Tight(is, N));
    }

    public static IntegerPolynomial fromBinary(byte[] data, int N, int q) {
        return new IntegerPolynomial(ArrayEncoder.decodeModQ(data, N, q));
    }

    public static IntegerPolynomial fromBinary(InputStream is, int N, int q) throws IOException {
        return new IntegerPolynomial(ArrayEncoder.decodeModQ(is, N, q));
    }

    public byte[] toBinary3Sves() {
        return ArrayEncoder.encodeMod3Sves(this.coeffs);
    }

    public byte[] toBinary3Tight() {
        BigInteger sum = Constants.BIGINT_ZERO;
        for (int i = this.coeffs.length - 1; i >= 0; i--) {
            sum = sum.multiply(BigInteger.valueOf(3L)).add(BigInteger.valueOf(this.coeffs[i] + 1));
        }
        int size = (BigInteger.valueOf(3L).pow(this.coeffs.length).bitLength() + 7) / 8;
        byte[] arr = sum.toByteArray();
        if (arr.length < size) {
            byte[] arr2 = new byte[size];
            System.arraycopy(arr, 0, arr2, size - arr.length, arr.length);
            return arr2;
        }
        if (arr.length > size) {
            arr = Arrays.copyOfRange(arr, 1, arr.length);
        }
        return arr;
    }

    public byte[] toBinary(int q) {
        return ArrayEncoder.encodeModQ(this.coeffs, q);
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.Polynomial
    public IntegerPolynomial mult(IntegerPolynomial poly2, int modulus) {
        IntegerPolynomial c = mult(poly2);
        c.mod(modulus);
        return c;
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.Polynomial
    public IntegerPolynomial mult(IntegerPolynomial poly2) {
        int N = this.coeffs.length;
        if (poly2.coeffs.length != N) {
            throw new IllegalArgumentException("Number of coefficients must be the same");
        }
        IntegerPolynomial c = multRecursive(poly2);
        if (c.coeffs.length > N) {
            for (int k = N; k < c.coeffs.length; k++) {
                int[] iArr = c.coeffs;
                int i = k - N;
                iArr[i] = iArr[i] + c.coeffs[k];
            }
            c.coeffs = Arrays.copyOf(c.coeffs, N);
        }
        return c;
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.Polynomial
    public BigIntPolynomial mult(BigIntPolynomial poly2) {
        return new BigIntPolynomial(this).mult(poly2);
    }

    private IntegerPolynomial multRecursive(IntegerPolynomial poly2) {
        int[] a = this.coeffs;
        int[] b = poly2.coeffs;
        int n = poly2.coeffs.length;
        if (n <= 32) {
            int cn = (2 * n) - 1;
            IntegerPolynomial c = new IntegerPolynomial(new int[cn]);
            for (int k = 0; k < cn; k++) {
                for (int i = Math.max(0, (k - n) + 1); i <= Math.min(k, n - 1); i++) {
                    int[] iArr = c.coeffs;
                    int i2 = k;
                    iArr[i2] = iArr[i2] + (b[i] * a[k - i]);
                }
            }
            return c;
        }
        int n1 = n / 2;
        IntegerPolynomial a1 = new IntegerPolynomial(Arrays.copyOf(a, n1));
        IntegerPolynomial a2 = new IntegerPolynomial(Arrays.copyOfRange(a, n1, n));
        IntegerPolynomial b1 = new IntegerPolynomial(Arrays.copyOf(b, n1));
        IntegerPolynomial b2 = new IntegerPolynomial(Arrays.copyOfRange(b, n1, n));
        IntegerPolynomial A = (IntegerPolynomial) a1.clone();
        A.add(a2);
        IntegerPolynomial B = (IntegerPolynomial) b1.clone();
        B.add(b2);
        IntegerPolynomial c1 = a1.multRecursive(b1);
        IntegerPolynomial c2 = a2.multRecursive(b2);
        IntegerPolynomial c3 = A.multRecursive(B);
        c3.sub(c1);
        c3.sub(c2);
        IntegerPolynomial c4 = new IntegerPolynomial((2 * n) - 1);
        for (int i3 = 0; i3 < c1.coeffs.length; i3++) {
            c4.coeffs[i3] = c1.coeffs[i3];
        }
        for (int i4 = 0; i4 < c3.coeffs.length; i4++) {
            int[] iArr2 = c4.coeffs;
            int i5 = n1 + i4;
            iArr2[i5] = iArr2[i5] + c3.coeffs[i4];
        }
        for (int i6 = 0; i6 < c2.coeffs.length; i6++) {
            int[] iArr3 = c4.coeffs;
            int i7 = (2 * n1) + i6;
            iArr3[i7] = iArr3[i7] + c2.coeffs[i6];
        }
        return c4;
    }

    public IntegerPolynomial invertFq(int q) {
        int N = this.coeffs.length;
        int k = 0;
        IntegerPolynomial b = new IntegerPolynomial(N + 1);
        b.coeffs[0] = 1;
        IntegerPolynomial c = new IntegerPolynomial(N + 1);
        IntegerPolynomial f = new IntegerPolynomial(N + 1);
        f.coeffs = Arrays.copyOf(this.coeffs, N + 1);
        f.modPositive(2);
        IntegerPolynomial g = new IntegerPolynomial(N + 1);
        g.coeffs[0] = 1;
        g.coeffs[N] = 1;
        while (true) {
            if (f.coeffs[0] == 0) {
                for (int i = 1; i <= N; i++) {
                    f.coeffs[i - 1] = f.coeffs[i];
                    c.coeffs[(N + 1) - i] = c.coeffs[N - i];
                }
                f.coeffs[N] = 0;
                c.coeffs[0] = 0;
                k++;
                if (f.equalsZero()) {
                    return null;
                }
            } else if (!f.equalsOne()) {
                if (f.degree() < g.degree()) {
                    IntegerPolynomial temp = f;
                    f = g;
                    g = temp;
                    IntegerPolynomial temp2 = b;
                    b = c;
                    c = temp2;
                }
                f.add(g, 2);
                b.add(c, 2);
            } else {
                if (b.coeffs[N] != 0) {
                    return null;
                }
                IntegerPolynomial Fq = new IntegerPolynomial(N);
                int k2 = k % N;
                for (int i2 = N - 1; i2 >= 0; i2--) {
                    int j = i2 - k2;
                    if (j < 0) {
                        j += N;
                    }
                    Fq.coeffs[j] = b.coeffs[i2];
                }
                return mod2ToModq(Fq, q);
            }
        }
    }

    private IntegerPolynomial mod2ToModq(IntegerPolynomial Fq, int q) {
        if (Util.is64BitJVM() && q == 2048) {
            LongPolynomial2 thisLong = new LongPolynomial2(this);
            LongPolynomial2 FqLong = new LongPolynomial2(Fq);
            int v = 2;
            while (v < q) {
                v *= 2;
                LongPolynomial2 temp = (LongPolynomial2) FqLong.clone();
                temp.mult2And(v - 1);
                temp.subAnd(thisLong.mult(FqLong).mult(FqLong), v - 1);
                FqLong = temp;
            }
            return FqLong.toIntegerPolynomial();
        }
        int v2 = 2;
        while (v2 < q) {
            v2 *= 2;
            IntegerPolynomial temp2 = new IntegerPolynomial(Arrays.copyOf(Fq.coeffs, Fq.coeffs.length));
            temp2.mult2(v2);
            temp2.sub(mult(Fq, v2).mult(Fq, v2), v2);
            Fq = temp2;
        }
        return Fq;
    }

    public IntegerPolynomial invertF3() {
        int N = this.coeffs.length;
        int k = 0;
        IntegerPolynomial b = new IntegerPolynomial(N + 1);
        b.coeffs[0] = 1;
        IntegerPolynomial c = new IntegerPolynomial(N + 1);
        IntegerPolynomial f = new IntegerPolynomial(N + 1);
        f.coeffs = Arrays.copyOf(this.coeffs, N + 1);
        f.modPositive(3);
        IntegerPolynomial g = new IntegerPolynomial(N + 1);
        g.coeffs[0] = -1;
        g.coeffs[N] = 1;
        while (true) {
            if (f.coeffs[0] == 0) {
                for (int i = 1; i <= N; i++) {
                    f.coeffs[i - 1] = f.coeffs[i];
                    c.coeffs[(N + 1) - i] = c.coeffs[N - i];
                }
                f.coeffs[N] = 0;
                c.coeffs[0] = 0;
                k++;
                if (f.equalsZero()) {
                    return null;
                }
            } else if (!f.equalsAbsOne()) {
                if (f.degree() < g.degree()) {
                    IntegerPolynomial temp = f;
                    f = g;
                    g = temp;
                    IntegerPolynomial temp2 = b;
                    b = c;
                    c = temp2;
                }
                if (f.coeffs[0] == g.coeffs[0]) {
                    f.sub(g, 3);
                    b.sub(c, 3);
                } else {
                    f.add(g, 3);
                    b.add(c, 3);
                }
            } else {
                if (b.coeffs[N] != 0) {
                    return null;
                }
                IntegerPolynomial Fp = new IntegerPolynomial(N);
                int k2 = k % N;
                for (int i2 = N - 1; i2 >= 0; i2--) {
                    int j = i2 - k2;
                    if (j < 0) {
                        j += N;
                    }
                    Fp.coeffs[j] = f.coeffs[0] * b.coeffs[i2];
                }
                Fp.ensurePositive(3);
                return Fp;
            }
        }
    }

    public Resultant resultant() {
        int N = this.coeffs.length;
        LinkedList<ModularResultant> modResultants = new LinkedList<>();
        BigInteger pProd = Constants.BIGINT_ONE;
        BigInteger res = Constants.BIGINT_ONE;
        int numEqual = 1;
        PrimeGenerator primes = new PrimeGenerator();
        while (true) {
            BigInteger prime = primes.nextPrime();
            ModularResultant crr = resultant(prime.intValue());
            modResultants.add(crr);
            BigInteger temp = pProd.multiply(prime);
            BigIntEuclidean er = BigIntEuclidean.calculate(prime, pProd);
            BigInteger resPrev = res;
            BigInteger res2 = res.multiply(er.x.multiply(prime));
            BigInteger res3 = crr.res.multiply(er.y.multiply(pProd));
            res = res2.add(res3).mod(temp);
            pProd = temp;
            BigInteger pProd2 = pProd.divide(BigInteger.valueOf(2L));
            BigInteger pProd2n = pProd2.negate();
            if (res.compareTo(pProd2) > 0) {
                res = res.subtract(pProd);
            } else if (res.compareTo(pProd2n) < 0) {
                res = res.add(pProd);
            }
            if (res.equals(resPrev)) {
                numEqual++;
                if (numEqual >= 3) {
                    break;
                }
            } else {
                numEqual = 1;
            }
        }
        while (modResultants.size() > 1) {
            ModularResultant modRes1 = modResultants.removeFirst();
            ModularResultant modRes2 = modResultants.removeFirst();
            ModularResultant modRes3 = ModularResultant.combineRho(modRes1, modRes2);
            modResultants.addLast(modRes3);
        }
        BigIntPolynomial rhoP = modResultants.getFirst().rho;
        BigInteger pProd3 = pProd.divide(BigInteger.valueOf(2L));
        BigInteger pProd2n2 = pProd3.negate();
        if (res.compareTo(pProd3) > 0) {
            res = res.subtract(pProd);
        }
        if (res.compareTo(pProd2n2) < 0) {
            res = res.add(pProd);
        }
        for (int i = 0; i < N; i++) {
            BigInteger c = rhoP.coeffs[i];
            if (c.compareTo(pProd3) > 0) {
                rhoP.coeffs[i] = c.subtract(pProd);
            }
            if (c.compareTo(pProd2n2) < 0) {
                rhoP.coeffs[i] = c.add(pProd);
            }
        }
        return new Resultant(rhoP, res);
    }

    public Resultant resultantMultiThread() {
        BigInteger bigIntegerNextProbablePrime;
        int N = this.coeffs.length;
        BigInteger max = squareSum().pow((N + 1) / 2);
        BigInteger max2 = max.multiply(BigInteger.valueOf(2L).pow((degree() + 1) / 2)).multiply(BigInteger.valueOf(2L));
        BigInteger prime = BigInteger.valueOf(10000L);
        BigInteger pProd = Constants.BIGINT_ONE;
        LinkedBlockingQueue<Future<ModularResultant>> resultantTasks = new LinkedBlockingQueue<>();
        Iterator<BigInteger> primes = BIGINT_PRIMES.iterator();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        while (pProd.compareTo(max2) < 0) {
            if (primes.hasNext()) {
                bigIntegerNextProbablePrime = primes.next();
            } else {
                bigIntegerNextProbablePrime = prime.nextProbablePrime();
            }
            prime = bigIntegerNextProbablePrime;
            Future<ModularResultant> task = executor.submit(new ModResultantTask(prime.intValue()));
            resultantTasks.add(task);
            pProd = pProd.multiply(prime);
        }
        ModularResultant overallResultant = null;
        while (!resultantTasks.isEmpty()) {
            try {
                Future<ModularResultant> modRes1 = resultantTasks.take();
                Future<ModularResultant> modRes2 = resultantTasks.poll();
                if (modRes2 == null) {
                    overallResultant = modRes1.get();
                    break;
                }
                Future<ModularResultant> newTask = executor.submit(new CombineTask(modRes1.get(), modRes2.get()));
                resultantTasks.add(newTask);
            } catch (Exception e) {
                throw new IllegalStateException(e.toString());
            }
        }
        executor.shutdown();
        BigInteger res = overallResultant.res;
        BigIntPolynomial rhoP = overallResultant.rho;
        BigInteger pProd2 = pProd.divide(BigInteger.valueOf(2L));
        BigInteger pProd2n = pProd2.negate();
        if (res.compareTo(pProd2) > 0) {
            res = res.subtract(pProd);
        }
        if (res.compareTo(pProd2n) < 0) {
            res = res.add(pProd);
        }
        for (int i = 0; i < N; i++) {
            BigInteger c = rhoP.coeffs[i];
            if (c.compareTo(pProd2) > 0) {
                rhoP.coeffs[i] = c.subtract(pProd);
            }
            if (c.compareTo(pProd2n) < 0) {
                rhoP.coeffs[i] = c.add(pProd);
            }
        }
        return new Resultant(rhoP, res);
    }

    public ModularResultant resultant(int p) {
        int[] fcoeffs = Arrays.copyOf(this.coeffs, this.coeffs.length + 1);
        IntegerPolynomial f = new IntegerPolynomial(fcoeffs);
        int N = fcoeffs.length;
        IntegerPolynomial a = new IntegerPolynomial(N);
        a.coeffs[0] = -1;
        a.coeffs[N - 1] = 1;
        IntegerPolynomial b = new IntegerPolynomial(f.coeffs);
        IntegerPolynomial v1 = new IntegerPolynomial(N);
        IntegerPolynomial v2 = new IntegerPolynomial(N);
        v2.coeffs[0] = 1;
        int da = N - 1;
        int db = b.degree();
        int ta = da;
        int r = 1;
        while (db > 0) {
            int c = Util.invert(b.coeffs[db], p);
            int c2 = (c * a.coeffs[da]) % p;
            a.multShiftSub(b, c2, da - db, p);
            v1.multShiftSub(v2, c2, da - db, p);
            da = a.degree();
            if (da < db) {
                r = (r * Util.pow(b.coeffs[db], ta - da, p)) % p;
                if (ta % 2 == 1 && db % 2 == 1) {
                    r = (-r) % p;
                }
                IntegerPolynomial temp = a;
                a = b;
                b = temp;
                da = db;
                IntegerPolynomial temp2 = v1;
                v1 = v2;
                v2 = temp2;
                ta = db;
                db = da;
            }
        }
        int r2 = (r * Util.pow(b.coeffs[0], da, p)) % p;
        int c3 = Util.invert(b.coeffs[0], p);
        v2.mult(c3);
        v2.mod(p);
        v2.mult(r2);
        v2.mod(p);
        v2.coeffs = Arrays.copyOf(v2.coeffs, v2.coeffs.length - 1);
        return new ModularResultant(new BigIntPolynomial(v2), BigInteger.valueOf(r2), BigInteger.valueOf(p));
    }

    private void multShiftSub(IntegerPolynomial b, int c, int k, int p) {
        int N = this.coeffs.length;
        for (int i = k; i < N; i++) {
            this.coeffs[i] = (this.coeffs[i] - (b.coeffs[i - k] * c)) % p;
        }
    }

    private BigInteger squareSum() {
        BigInteger sum = Constants.BIGINT_ZERO;
        for (int i = 0; i < this.coeffs.length; i++) {
            sum = sum.add(BigInteger.valueOf(this.coeffs[i] * this.coeffs[i]));
        }
        return sum;
    }

    int degree() {
        int degree = this.coeffs.length - 1;
        while (degree > 0 && this.coeffs[degree] == 0) {
            degree--;
        }
        return degree;
    }

    public void add(IntegerPolynomial b, int modulus) {
        add(b);
        mod(modulus);
    }

    public void add(IntegerPolynomial b) {
        if (b.coeffs.length > this.coeffs.length) {
            this.coeffs = Arrays.copyOf(this.coeffs, b.coeffs.length);
        }
        for (int i = 0; i < b.coeffs.length; i++) {
            int[] iArr = this.coeffs;
            int i2 = i;
            iArr[i2] = iArr[i2] + b.coeffs[i];
        }
    }

    public void sub(IntegerPolynomial b, int modulus) {
        sub(b);
        mod(modulus);
    }

    public void sub(IntegerPolynomial b) {
        if (b.coeffs.length > this.coeffs.length) {
            this.coeffs = Arrays.copyOf(this.coeffs, b.coeffs.length);
        }
        for (int i = 0; i < b.coeffs.length; i++) {
            int[] iArr = this.coeffs;
            int i2 = i;
            iArr[i2] = iArr[i2] - b.coeffs[i];
        }
    }

    void sub(int b) {
        for (int i = 0; i < this.coeffs.length; i++) {
            int[] iArr = this.coeffs;
            int i2 = i;
            iArr[i2] = iArr[i2] - b;
        }
    }

    public void mult(int factor) {
        for (int i = 0; i < this.coeffs.length; i++) {
            int[] iArr = this.coeffs;
            int i2 = i;
            iArr[i2] = iArr[i2] * factor;
        }
    }

    private void mult2(int modulus) {
        for (int i = 0; i < this.coeffs.length; i++) {
            int[] iArr = this.coeffs;
            int i2 = i;
            iArr[i2] = iArr[i2] * 2;
            int[] iArr2 = this.coeffs;
            int i3 = i;
            iArr2[i3] = iArr2[i3] % modulus;
        }
    }

    public void mult3(int modulus) {
        for (int i = 0; i < this.coeffs.length; i++) {
            int[] iArr = this.coeffs;
            int i2 = i;
            iArr[i2] = iArr[i2] * 3;
            int[] iArr2 = this.coeffs;
            int i3 = i;
            iArr2[i3] = iArr2[i3] % modulus;
        }
    }

    public void div(int k) {
        int k2 = (k + 1) / 2;
        for (int i = 0; i < this.coeffs.length; i++) {
            int[] iArr = this.coeffs;
            int i2 = i;
            iArr[i2] = iArr[i2] + (this.coeffs[i] > 0 ? k2 : -k2);
            int[] iArr2 = this.coeffs;
            int i3 = i;
            iArr2[i3] = iArr2[i3] / k;
        }
    }

    public void mod3() {
        for (int i = 0; i < this.coeffs.length; i++) {
            int[] iArr = this.coeffs;
            int i2 = i;
            iArr[i2] = iArr[i2] % 3;
            if (this.coeffs[i] > 1) {
                int[] iArr2 = this.coeffs;
                int i3 = i;
                iArr2[i3] = iArr2[i3] - 3;
            }
            if (this.coeffs[i] < -1) {
                int[] iArr3 = this.coeffs;
                int i4 = i;
                iArr3[i4] = iArr3[i4] + 3;
            }
        }
    }

    public void modPositive(int modulus) {
        mod(modulus);
        ensurePositive(modulus);
    }

    void modCenter(int modulus) {
        mod(modulus);
        for (int j = 0; j < this.coeffs.length; j++) {
            while (this.coeffs[j] < modulus / 2) {
                int[] iArr = this.coeffs;
                int i = j;
                iArr[i] = iArr[i] + modulus;
            }
            while (this.coeffs[j] >= modulus / 2) {
                int[] iArr2 = this.coeffs;
                int i2 = j;
                iArr2[i2] = iArr2[i2] - modulus;
            }
        }
    }

    public void mod(int modulus) {
        for (int i = 0; i < this.coeffs.length; i++) {
            int[] iArr = this.coeffs;
            int i2 = i;
            iArr[i2] = iArr[i2] % modulus;
        }
    }

    public void ensurePositive(int modulus) {
        for (int i = 0; i < this.coeffs.length; i++) {
            while (this.coeffs[i] < 0) {
                int[] iArr = this.coeffs;
                int i2 = i;
                iArr[i2] = iArr[i2] + modulus;
            }
        }
    }

    public long centeredNormSq(int q) {
        int N = this.coeffs.length;
        IntegerPolynomial p = (IntegerPolynomial) clone();
        p.shiftGap(q);
        long sum = 0;
        long sqSum = 0;
        for (int i = 0; i != p.coeffs.length; i++) {
            int c = p.coeffs[i];
            sum += (long) c;
            sqSum += (long) (c * c);
        }
        long centeredNormSq = sqSum - ((sum * sum) / ((long) N));
        return centeredNormSq;
    }

    void shiftGap(int q) {
        int shift;
        modCenter(q);
        int[] sorted = Arrays.clone(this.coeffs);
        sort(sorted);
        int maxrange = 0;
        int maxrangeStart = 0;
        for (int i = 0; i < sorted.length - 1; i++) {
            int range = sorted[i + 1] - sorted[i];
            if (range > maxrange) {
                maxrange = range;
                maxrangeStart = sorted[i];
            }
        }
        int pmin = sorted[0];
        int pmax = sorted[sorted.length - 1];
        int j = (q - pmax) + pmin;
        if (j > maxrange) {
            shift = (pmax + pmin) / 2;
        } else {
            shift = maxrangeStart + (maxrange / 2) + (q / 2);
        }
        sub(shift);
    }

    private void sort(int[] ints) {
        boolean swap = true;
        while (swap) {
            swap = false;
            for (int i = 0; i != ints.length - 1; i++) {
                if (ints[i] > ints[i + 1]) {
                    int tmp = ints[i];
                    ints[i] = ints[i + 1];
                    ints[i + 1] = tmp;
                    swap = true;
                }
            }
        }
    }

    public void center0(int q) {
        for (int i = 0; i < this.coeffs.length; i++) {
            while (this.coeffs[i] < (-q) / 2) {
                int[] iArr = this.coeffs;
                int i2 = i;
                iArr[i2] = iArr[i2] + q;
            }
            while (this.coeffs[i] > q / 2) {
                int[] iArr2 = this.coeffs;
                int i3 = i;
                iArr2[i3] = iArr2[i3] - q;
            }
        }
    }

    public int sumCoeffs() {
        int sum = 0;
        for (int i = 0; i < this.coeffs.length; i++) {
            sum += this.coeffs[i];
        }
        return sum;
    }

    private boolean equalsZero() {
        for (int i = 0; i < this.coeffs.length; i++) {
            if (this.coeffs[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public boolean equalsOne() {
        for (int i = 1; i < this.coeffs.length; i++) {
            if (this.coeffs[i] != 0) {
                return false;
            }
        }
        return this.coeffs[0] == 1;
    }

    private boolean equalsAbsOne() {
        for (int i = 1; i < this.coeffs.length; i++) {
            if (this.coeffs[i] != 0) {
                return false;
            }
        }
        return Math.abs(this.coeffs[0]) == 1;
    }

    public int count(int value) {
        int count = 0;
        for (int i = 0; i != this.coeffs.length; i++) {
            if (this.coeffs[i] == value) {
                count++;
            }
        }
        return count;
    }

    public void rotate1() {
        int clast = this.coeffs[this.coeffs.length - 1];
        for (int i = this.coeffs.length - 1; i > 0; i--) {
            this.coeffs[i] = this.coeffs[i - 1];
        }
        this.coeffs[0] = clast;
    }

    public void clear() {
        for (int i = 0; i < this.coeffs.length; i++) {
            this.coeffs[i] = 0;
        }
    }

    @Override // org.spongycastle.pqc.math.ntru.polynomial.Polynomial
    public IntegerPolynomial toIntegerPolynomial() {
        return (IntegerPolynomial) clone();
    }

    public Object clone() {
        return new IntegerPolynomial((int[]) this.coeffs.clone());
    }

    public boolean equals(Object obj) {
        if (obj instanceof IntegerPolynomial) {
            return Arrays.areEqual(this.coeffs, ((IntegerPolynomial) obj).coeffs);
        }
        return false;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/ntru/polynomial/IntegerPolynomial$ModResultantTask.class */
    private class ModResultantTask implements Callable<ModularResultant> {
        private int modulus;

        private ModResultantTask(int modulus) {
            this.modulus = modulus;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Callable
        public ModularResultant call() {
            return IntegerPolynomial.this.resultant(this.modulus);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/ntru/polynomial/IntegerPolynomial$CombineTask.class */
    private class CombineTask implements Callable<ModularResultant> {
        private ModularResultant modRes1;
        private ModularResultant modRes2;

        private CombineTask(ModularResultant modRes1, ModularResultant modRes2) {
            this.modRes1 = modRes1;
            this.modRes2 = modRes2;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Callable
        public ModularResultant call() {
            return ModularResultant.combineRho(this.modRes1, this.modRes2);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/math/ntru/polynomial/IntegerPolynomial$PrimeGenerator.class */
    private class PrimeGenerator {
        private int index;
        private BigInteger prime;

        private PrimeGenerator() {
            this.index = 0;
        }

        public BigInteger nextPrime() {
            if (this.index < IntegerPolynomial.BIGINT_PRIMES.size()) {
                List list = IntegerPolynomial.BIGINT_PRIMES;
                int i = this.index;
                this.index = i + 1;
                this.prime = (BigInteger) list.get(i);
            } else {
                this.prime = this.prime.nextProbablePrime();
            }
            return this.prime;
        }
    }
}
