package org.bouncycastle.pqc.legacy.math.ntru.polynomial;

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
import org.bouncycastle.pqc.legacy.math.ntru.euclid.BigIntEuclidean;
import org.bouncycastle.pqc.legacy.math.ntru.util.ArrayEncoder;
import org.bouncycastle.pqc.legacy.math.ntru.util.Util;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/ntru/polynomial/IntegerPolynomial.class */
public class IntegerPolynomial implements Polynomial {
    private static final int NUM_EQUAL_RESULTANTS = 3;
    private static final int[] PRIMES = {4507, 4513, 4517, 4519, 4523, 4547, 4549, 4561, 4567, 4583, 4591, 4597, 4603, 4621, 4637, 4639, 4643, 4649, 4651, 4657, 4663, 4673, 4679, 4691, 4703, 4721, 4723, 4729, 4733, 4751, 4759, 4783, 4787, 4789, 4793, 4799, 4801, 4813, 4817, 4831, 4861, 4871, 4877, 4889, 4903, 4909, 4919, 4931, 4933, 4937, 4943, 4951, 4957, 4967, 4969, 4973, 4987, 4993, 4999, WinError.ERROR_DEPENDENCY_ALREADY_EXISTS, WinError.ERROR_CANT_EVICT_ACTIVE_NODE, WinError.ERROR_OBJECT_IN_LIST, WinError.ERROR_NOT_QUORUM_CAPABLE, WinError.ERROR_INVALID_STATE, WinError.ERROR_CLUSTER_INVALID_NODE, WinError.ERROR_CLUSTER_NODE_UNREACHABLE, WinError.ERROR_CLUSTER_NO_SECURITY_CONTEXT, WinError.ERROR_CLUSTER_SYSTEM_CONFIG_CHANGED, WinError.ERROR_CLUSTER_NO_RPC_PACKAGES_REGISTERED, WinError.ERROR_DATABASE_BACKUP_CORRUPT, 5099, 5101, 5107, 5113, 5119, 5147, 5153, 5167, 5171, 5179, 5189, 5197, 5209, 5227, 5231, 5233, 5237, 5261, 5273, 5279, 5281, 5297, 5303, 5309, 5323, 5333, 5347, 5351, 5381, 5387, 5393, 5399, 5407, 5413, 5417, 5419, 5431, 5437, 5441, 5443, 5449, 5471, 5477, 5479, 5483, 5501, 5503, 5507, 5519, 5521, 5527, 5531, 5557, 5563, 5569, 5573, 5581, 5591, 5623, 5639, 5641, 5647, 5651, 5653, 5657, 5659, 5669, 5683, 5689, 5693, 5701, 5711, 5717, 5737, 5741, 5743, 5749, 5779, 5783, 5791, 5801, 5807, 5813, 5821, 5827, 5839, 5843, 5849, 5851, 5857, 5861, 5867, 5869, 5879, 5881, WinError.ERROR_CLUSTER_PARAMETER_MISMATCH, WinError.ERROR_CLUSCFG_SYSTEM_DISK_DRIVE_LETTER_CONFLICT, WinError.ERROR_CLUSTER_CANT_DESERIALIZE_DATA, WinError.ERROR_CLUSTER_INVALID_IPV6_TUNNEL_NETWORK, 5939, 5953, 5981, 5987, WinError.ERROR_FILE_NOT_ENCRYPTED, WinError.ERROR_EFS_SERVER_NOT_TRUSTED, 6029, 6037, 6043, 6047, 6053, 6067, 6073, 6079, 6089, 6091, 6101, 6113, 6121, 6131, 6133, 6143, 6151, 6163, 6173, 6197, 6199, 6203, 6211, 6217, 6221, 6229, 6247, 6257, 6263, 6269, 6271, 6277, 6287, 6299, 6301, 6311, 6317, 6323, 6329, 6337, 6343, 6353, 6359, 6361, 6367, 6373, 6379, 6389, 6397, 6421, 6427, 6449, 6451, 6469, 6473, 6481, 6491, 6521, 6529, 6547, 6551, 6553, 6563, 6569, 6571, 6577, 6581, 6599, WinError.ERROR_LOG_RESTART_INVALID, WinError.ERROR_LOG_POLICY_ALREADY_INSTALLED, WinError.ERROR_LOG_CLIENT_NOT_REGISTERED, 6653, 6659, 6661, 6673, 6679, 6689, 6691, WinError.ERROR_TRANSACTION_NOT_ACTIVE, WinError.ERROR_TRANSACTION_NOT_REQUESTED, WinError.ERROR_TRANSACTION_SUPERIOR_EXISTS, WinError.ERROR_TRANSACTIONMANAGER_NOT_ONLINE, 6733, 6737, 6761, 6763, 6779, 6781, 6791, 6793, WinError.ERROR_DIRECTORY_NOT_RM, WinError.ERROR_FILE_IDENTITY_NOT_PERSISTENT, WinError.ERROR_INDOUBT_TRANSACTIONS_EXIST, WinError.ERROR_ROLLBACK_TIMER_EXPIRED, WinError.ERROR_LOG_GROWTH_FAILED, WinError.ERROR_NOT_SNAPSHOT_VOLUME, 6857, 6863, 6869, 6871, 6883, 6899, 6907, 6911, 6917, 6947, 6949, 6959, 6961, 6967, 6971, 6977, 6983, 6991, 6997, WinError.ERROR_CTX_WINSTATION_NAME_INVALID, WinError.ERROR_CTX_MODEM_RESPONSE_NO_CARRIER, 7019, 7027, 7039, 7043, WinError.ERROR_CTX_SHADOW_NOT_RUNNING, WinError.ERROR_TS_INCOMPATIBLE_SESSIONS, 7079, 7103, 7109, 7121, 7127, 7129, 7151, 7159, 7177, 7187, 7193, 7207, 7211, 7213, 7219, 7229, 7237, 7243, 7247, 7253, 7283, 7297, 7307, 7309, 7321, 7331, 7333, 7349, 7351, 7369, 7393, 7411, 7417, 7433, 7451, 7457, 7459, 7477, 7481, 7487, 7489, 7499, 7507, 7517, 7523, 7529, 7537, 7541, 7547, 7549, 7559, 7561, 7573, 7577, 7583, 7589, 7591, 7603, 7607, 7621, 7639, 7643, 7649, 7669, 7673, 7681, 7687, 7691, 7699, 7703, 7717, 7723, 7727, 7741, 7753, 7757, 7759, 7789, 7793, 7817, 7823, 7829, 7841, 7853, 7867, 7873, 7877, 7879, 7883, 7901, 7907, 7919, 7927, 7933, GL.GL_RENDERER, 7949, 7951, 7963, 7993, WinError.FRS_ERR_PARENT_INSUFFICIENT_PRIV, WinError.FRS_ERR_CHILD_TO_PARENT_COMM, WinError.FRS_ERR_INVALID_SERVICE_PARAMETER, 8039, 8053, 8059, 8069, 8081, 8087, 8089, 8093, 8101, 8111, 8117, 8123, 8147, 8161, 8167, 8171, 8179, 8191, WinError.ERROR_DS_NO_MORE_RIDS, WinError.ERROR_POLICY_OBJECT_NOT_FOUND, WinError.ERROR_PROMOTION_ACTIVE, WinError.ERROR_DS_AUTH_METHOD_NOT_SUPPORTED, WinError.ERROR_DS_INAPPROPRIATE_AUTH, WinError.ERROR_DS_CONFIDENTIALITY_REQUIRED, WinError.ERROR_DS_IS_LEAF, 8263, 8269, 8273, 8287, 8291, 8293, 8297, WinError.ERROR_DS_ILLEGAL_MOD_OPERATION, WinError.ERROR_DS_ATT_NOT_DEF_FOR_CLASS, WinError.ERROR_DS_NO_PARENT_OBJECT, WinError.ERROR_DS_SEC_DESC_TOO_SHORT, WinError.ERROR_DS_NO_CROSSREF_FOR_NC, WinError.ERROR_DS_CANT_MOD_SYSTEM_ONLY, WinError.ERROR_DS_NOTIFY_FILTER_TOO_COMPLEX, WinError.ERROR_DS_NONEXISTENT_MAY_HAVE, WinError.ERROR_DS_AUX_CLS_TEST_FAIL, WinError.ERROR_DS_CANT_FIND_DSA_OBJ, WinError.ERROR_DS_SECURITY_ILLEGAL_MODIFY, WinError.ERROR_DS_HIERARCHY_TABLE_MALLOC_FAILED, WinError.ERROR_DS_UNKNOWN_ERROR, WinError.ERROR_DS_DRA_INCONSISTENT_DIT, WinError.ERROR_DS_DRA_MAIL_PROBLEM, WinError.ERROR_DS_DRA_PREEMPTED, WinError.ERROR_DS_INSTALL_SCHEMA_MISMATCH, WinError.ERROR_DS_NO_TREE_DELETE_ABOVE_NC, WinError.ERROR_DS_INVALID_GROUP_TYPE, WinError.ERROR_DS_HAVE_PRIMARY_MEMBERS, WinError.ERROR_DS_KEY_NOT_UNIQUE, WinError.ERROR_DS_CANT_FIND_DC_FOR_SRC_DOMAIN, WinError.ERROR_DS_SRC_SID_EXISTS_IN_FOREST, WinError.ERROR_DS_DRA_SCHEMA_CONFLICT, WinError.ERROR_DS_FOREST_VERSION_TOO_HIGH, WinError.ERROR_DS_OUT_OF_VERSION_STORE, WinError.ERROR_DS_MODIFYDN_DISALLOWED_BY_FLAG, WinError.ERROR_DS_DUP_MSDS_INTID, WinError.ERROR_DS_AUTHORIZATION_FAILED, WinError.ERROR_DS_CANT_MOVE_APP_QUERY_GROUP, WinError.ERROR_DS_NO_NTDSA_OBJECT, WinError.ERROR_DS_INVALID_SEARCH_FLAG_TUPLE, 8629, 8641, 8647, 8663, 8669, 8677, 8681, 8689, 8693, 8699, 8707, 8713, 8719, 8731, 8737, 8741, 8747, 8753, 8761, 8779, 8783, 8803, 8807, 8819, 8821, 8831, 8837, 8839, 8849, 8861, 8863, 8867, 8887, 8893, 8923, 8929, 8933, 8941, 8951, 8963, 8969, 8971, 8999, WinError.DNS_ERROR_RCODE_FORMAT_ERROR, WinError.DNS_ERROR_RCODE_YXRRSET, 9011, 9013, 9029, 9041, 9043, 9049, 9059, 9067, 9091, 9103, 9109, 9127, 9133, 9137, 9151, 9157, 9161, 9173, 9181, 9187, 9199, 9203, 9209, 9221, 9227, 9239, 9241, 9257, 9277, 9281, 9283, 9293, 9311, 9319, 9323, 9337, 9341, 9343, 9349, 9371, 9377, 9391, 9397, 9403, 9413, 9419, 9421, 9431, 9433, 9437, 9439, 9461, 9463, 9467, 9473, 9479, 9491, 9497, 9511, 9521, 9533, 9539, 9547, WinError.DNS_ERROR_INVALID_TYPE, 9587, WinError.DNS_ERROR_ZONE_DOES_NOT_EXIST, WinError.DNS_ERROR_ZONE_NOT_SECONDARY, WinError.DNS_ERROR_FORWARDER_ALREADY_EXISTS, 9623, 9629, 9631, 9643, 9649, 9661, 9677, 9679, 9689, 9697, WinError.DNS_ERROR_NO_BOOTFILE_IF_DS_ZONE, WinError.DNS_ERROR_DNAME_COLLISION, 9733, 9739, 9743, 9749, 9767, 9769, 9781, 9787, 9791, 9803, 9811, 9817, 9829, 9833, 9839, WinError.DNS_ERROR_NO_TCPIP, 9857, 9859, 9871, 9883, 9887, WinError.DNS_ERROR_DP_DOES_NOT_EXIST, 9907, 9923, 9929, 9931, 9941, 9949, 9967, 9973};
    private static final List BIGINT_PRIMES = new ArrayList();
    public int[] coeffs;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/ntru/polynomial/IntegerPolynomial$CombineTask.class */
    private static class CombineTask implements Callable<ModularResultant> {
        private ModularResultant modRes1;
        private ModularResultant modRes2;

        private CombineTask(ModularResultant modularResultant, ModularResultant modularResultant2) {
            this.modRes1 = modularResultant;
            this.modRes2 = modularResultant2;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Callable
        public ModularResultant call() {
            return ModularResultant.combineRho(this.modRes1, this.modRes2);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/ntru/polynomial/IntegerPolynomial$ModResultantTask.class */
    private class ModResultantTask implements Callable<ModularResultant> {
        private int modulus;

        private ModResultantTask(int i) {
            this.modulus = i;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Callable
        public ModularResultant call() {
            return IntegerPolynomial.this.resultant(this.modulus);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/math/ntru/polynomial/IntegerPolynomial$PrimeGenerator.class */
    private static class PrimeGenerator {
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

    public IntegerPolynomial(int i) {
        this.coeffs = new int[i];
    }

    public IntegerPolynomial(int[] iArr) {
        this.coeffs = iArr;
    }

    public IntegerPolynomial(BigIntPolynomial bigIntPolynomial) {
        this.coeffs = new int[bigIntPolynomial.coeffs.length];
        for (int i = 0; i < bigIntPolynomial.coeffs.length; i++) {
            this.coeffs[i] = bigIntPolynomial.coeffs[i].intValue();
        }
    }

    public static IntegerPolynomial fromBinary3Sves(byte[] bArr, int i) {
        return new IntegerPolynomial(ArrayEncoder.decodeMod3Sves(bArr, i));
    }

    public static IntegerPolynomial fromBinary3Tight(byte[] bArr, int i) {
        return new IntegerPolynomial(ArrayEncoder.decodeMod3Tight(bArr, i));
    }

    public static IntegerPolynomial fromBinary3Tight(InputStream inputStream, int i) throws IOException {
        return new IntegerPolynomial(ArrayEncoder.decodeMod3Tight(inputStream, i));
    }

    public static IntegerPolynomial fromBinary(byte[] bArr, int i, int i2) {
        return new IntegerPolynomial(ArrayEncoder.decodeModQ(bArr, i, i2));
    }

    public static IntegerPolynomial fromBinary(InputStream inputStream, int i, int i2) throws IOException {
        return new IntegerPolynomial(ArrayEncoder.decodeModQ(inputStream, i, i2));
    }

    public byte[] toBinary3Sves() {
        return ArrayEncoder.encodeMod3Sves(this.coeffs);
    }

    public byte[] toBinary3Tight() {
        BigInteger bigIntegerAdd = Constants.BIGINT_ZERO;
        for (int length = this.coeffs.length - 1; length >= 0; length--) {
            bigIntegerAdd = bigIntegerAdd.multiply(BigInteger.valueOf(3L)).add(BigInteger.valueOf(this.coeffs[length] + 1));
        }
        int iBitLength = (BigInteger.valueOf(3L).pow(this.coeffs.length).bitLength() + 7) / 8;
        byte[] byteArray = bigIntegerAdd.toByteArray();
        if (byteArray.length < iBitLength) {
            byte[] bArr = new byte[iBitLength];
            System.arraycopy(byteArray, 0, bArr, iBitLength - byteArray.length, byteArray.length);
            return bArr;
        }
        if (byteArray.length > iBitLength) {
            byteArray = Arrays.copyOfRange(byteArray, 1, byteArray.length);
        }
        return byteArray;
    }

    public byte[] toBinary(int i) {
        return ArrayEncoder.encodeModQ(this.coeffs, i);
    }

    @Override // org.bouncycastle.pqc.legacy.math.ntru.polynomial.Polynomial
    public IntegerPolynomial mult(IntegerPolynomial integerPolynomial, int i) {
        IntegerPolynomial integerPolynomialMult = mult(integerPolynomial);
        integerPolynomialMult.mod(i);
        return integerPolynomialMult;
    }

    @Override // org.bouncycastle.pqc.legacy.math.ntru.polynomial.Polynomial
    public IntegerPolynomial mult(IntegerPolynomial integerPolynomial) {
        int length = this.coeffs.length;
        if (integerPolynomial.coeffs.length != length) {
            throw new IllegalArgumentException("Number of coefficients must be the same");
        }
        IntegerPolynomial integerPolynomialMultRecursive = multRecursive(integerPolynomial);
        if (integerPolynomialMultRecursive.coeffs.length > length) {
            for (int i = length; i < integerPolynomialMultRecursive.coeffs.length; i++) {
                int[] iArr = integerPolynomialMultRecursive.coeffs;
                int i2 = i - length;
                iArr[i2] = iArr[i2] + integerPolynomialMultRecursive.coeffs[i];
            }
            integerPolynomialMultRecursive.coeffs = Arrays.copyOf(integerPolynomialMultRecursive.coeffs, length);
        }
        return integerPolynomialMultRecursive;
    }

    @Override // org.bouncycastle.pqc.legacy.math.ntru.polynomial.Polynomial
    public BigIntPolynomial mult(BigIntPolynomial bigIntPolynomial) {
        return new BigIntPolynomial(this).mult(bigIntPolynomial);
    }

    private IntegerPolynomial multRecursive(IntegerPolynomial integerPolynomial) {
        int[] iArr = this.coeffs;
        int[] iArr2 = integerPolynomial.coeffs;
        int length = integerPolynomial.coeffs.length;
        if (length <= 32) {
            int i = (2 * length) - 1;
            IntegerPolynomial integerPolynomial2 = new IntegerPolynomial(new int[i]);
            for (int i2 = 0; i2 < i; i2++) {
                for (int iMax = Math.max(0, (i2 - length) + 1); iMax <= Math.min(i2, length - 1); iMax++) {
                    int[] iArr3 = integerPolynomial2.coeffs;
                    int i3 = i2;
                    iArr3[i3] = iArr3[i3] + (iArr2[iMax] * iArr[i2 - iMax]);
                }
            }
            return integerPolynomial2;
        }
        int i4 = length / 2;
        IntegerPolynomial integerPolynomial3 = new IntegerPolynomial(Arrays.copyOf(iArr, i4));
        IntegerPolynomial integerPolynomial4 = new IntegerPolynomial(Arrays.copyOfRange(iArr, i4, length));
        IntegerPolynomial integerPolynomial5 = new IntegerPolynomial(Arrays.copyOf(iArr2, i4));
        IntegerPolynomial integerPolynomial6 = new IntegerPolynomial(Arrays.copyOfRange(iArr2, i4, length));
        IntegerPolynomial integerPolynomial7 = (IntegerPolynomial) integerPolynomial3.clone();
        integerPolynomial7.add(integerPolynomial4);
        IntegerPolynomial integerPolynomial8 = (IntegerPolynomial) integerPolynomial5.clone();
        integerPolynomial8.add(integerPolynomial6);
        IntegerPolynomial integerPolynomialMultRecursive = integerPolynomial3.multRecursive(integerPolynomial5);
        IntegerPolynomial integerPolynomialMultRecursive2 = integerPolynomial4.multRecursive(integerPolynomial6);
        IntegerPolynomial integerPolynomialMultRecursive3 = integerPolynomial7.multRecursive(integerPolynomial8);
        integerPolynomialMultRecursive3.sub(integerPolynomialMultRecursive);
        integerPolynomialMultRecursive3.sub(integerPolynomialMultRecursive2);
        IntegerPolynomial integerPolynomial9 = new IntegerPolynomial((2 * length) - 1);
        for (int i5 = 0; i5 < integerPolynomialMultRecursive.coeffs.length; i5++) {
            integerPolynomial9.coeffs[i5] = integerPolynomialMultRecursive.coeffs[i5];
        }
        for (int i6 = 0; i6 < integerPolynomialMultRecursive3.coeffs.length; i6++) {
            int[] iArr4 = integerPolynomial9.coeffs;
            int i7 = i4 + i6;
            iArr4[i7] = iArr4[i7] + integerPolynomialMultRecursive3.coeffs[i6];
        }
        for (int i8 = 0; i8 < integerPolynomialMultRecursive2.coeffs.length; i8++) {
            int[] iArr5 = integerPolynomial9.coeffs;
            int i9 = (2 * i4) + i8;
            iArr5[i9] = iArr5[i9] + integerPolynomialMultRecursive2.coeffs[i8];
        }
        return integerPolynomial9;
    }

    public IntegerPolynomial invertFq(int i) {
        int length = this.coeffs.length;
        int i2 = 0;
        IntegerPolynomial integerPolynomial = new IntegerPolynomial(length + 1);
        integerPolynomial.coeffs[0] = 1;
        IntegerPolynomial integerPolynomial2 = new IntegerPolynomial(length + 1);
        IntegerPolynomial integerPolynomial3 = new IntegerPolynomial(length + 1);
        integerPolynomial3.coeffs = Arrays.copyOf(this.coeffs, length + 1);
        integerPolynomial3.modPositive(2);
        IntegerPolynomial integerPolynomial4 = new IntegerPolynomial(length + 1);
        integerPolynomial4.coeffs[0] = 1;
        integerPolynomial4.coeffs[length] = 1;
        while (true) {
            if (integerPolynomial3.coeffs[0] == 0) {
                for (int i3 = 1; i3 <= length; i3++) {
                    integerPolynomial3.coeffs[i3 - 1] = integerPolynomial3.coeffs[i3];
                    integerPolynomial2.coeffs[(length + 1) - i3] = integerPolynomial2.coeffs[length - i3];
                }
                integerPolynomial3.coeffs[length] = 0;
                integerPolynomial2.coeffs[0] = 0;
                i2++;
                if (integerPolynomial3.equalsZero()) {
                    return null;
                }
            } else {
                if (integerPolynomial3.equalsOne()) {
                    if (integerPolynomial.coeffs[length] != 0) {
                        return null;
                    }
                    IntegerPolynomial integerPolynomial5 = new IntegerPolynomial(length);
                    int i4 = i2 % length;
                    for (int i5 = length - 1; i5 >= 0; i5--) {
                        int i6 = i5 - i4;
                        if (i6 < 0) {
                            i6 += length;
                        }
                        integerPolynomial5.coeffs[i6] = integerPolynomial.coeffs[i5];
                    }
                    return mod2ToModq(integerPolynomial5, i);
                }
                if (integerPolynomial3.degree() < integerPolynomial4.degree()) {
                    IntegerPolynomial integerPolynomial6 = integerPolynomial3;
                    integerPolynomial3 = integerPolynomial4;
                    integerPolynomial4 = integerPolynomial6;
                    IntegerPolynomial integerPolynomial7 = integerPolynomial;
                    integerPolynomial = integerPolynomial2;
                    integerPolynomial2 = integerPolynomial7;
                }
                integerPolynomial3.add(integerPolynomial4, 2);
                integerPolynomial.add(integerPolynomial2, 2);
            }
        }
    }

    private IntegerPolynomial mod2ToModq(IntegerPolynomial integerPolynomial, int i) {
        if (!Util.is64BitJVM() || i != 2048) {
            int i2 = 2;
            while (i2 < i) {
                i2 *= 2;
                IntegerPolynomial integerPolynomial2 = new IntegerPolynomial(Arrays.copyOf(integerPolynomial.coeffs, integerPolynomial.coeffs.length));
                integerPolynomial2.mult2(i2);
                integerPolynomial2.sub(mult(integerPolynomial, i2).mult(integerPolynomial, i2), i2);
                integerPolynomial = integerPolynomial2;
            }
            return integerPolynomial;
        }
        LongPolynomial2 longPolynomial2 = new LongPolynomial2(this);
        LongPolynomial2 longPolynomial3 = new LongPolynomial2(integerPolynomial);
        int i3 = 2;
        while (i3 < i) {
            i3 *= 2;
            LongPolynomial2 longPolynomial4 = (LongPolynomial2) longPolynomial3.clone();
            longPolynomial4.mult2And(i3 - 1);
            longPolynomial4.subAnd(longPolynomial2.mult(longPolynomial3).mult(longPolynomial3), i3 - 1);
            longPolynomial3 = longPolynomial4;
        }
        return longPolynomial3.toIntegerPolynomial();
    }

    public IntegerPolynomial invertF3() {
        int length = this.coeffs.length;
        int i = 0;
        IntegerPolynomial integerPolynomial = new IntegerPolynomial(length + 1);
        integerPolynomial.coeffs[0] = 1;
        IntegerPolynomial integerPolynomial2 = new IntegerPolynomial(length + 1);
        IntegerPolynomial integerPolynomial3 = new IntegerPolynomial(length + 1);
        integerPolynomial3.coeffs = Arrays.copyOf(this.coeffs, length + 1);
        integerPolynomial3.modPositive(3);
        IntegerPolynomial integerPolynomial4 = new IntegerPolynomial(length + 1);
        integerPolynomial4.coeffs[0] = -1;
        integerPolynomial4.coeffs[length] = 1;
        while (true) {
            if (integerPolynomial3.coeffs[0] == 0) {
                for (int i2 = 1; i2 <= length; i2++) {
                    integerPolynomial3.coeffs[i2 - 1] = integerPolynomial3.coeffs[i2];
                    integerPolynomial2.coeffs[(length + 1) - i2] = integerPolynomial2.coeffs[length - i2];
                }
                integerPolynomial3.coeffs[length] = 0;
                integerPolynomial2.coeffs[0] = 0;
                i++;
                if (integerPolynomial3.equalsZero()) {
                    return null;
                }
            } else {
                if (integerPolynomial3.equalsAbsOne()) {
                    if (integerPolynomial.coeffs[length] != 0) {
                        return null;
                    }
                    IntegerPolynomial integerPolynomial5 = new IntegerPolynomial(length);
                    int i3 = i % length;
                    for (int i4 = length - 1; i4 >= 0; i4--) {
                        int i5 = i4 - i3;
                        if (i5 < 0) {
                            i5 += length;
                        }
                        integerPolynomial5.coeffs[i5] = integerPolynomial3.coeffs[0] * integerPolynomial.coeffs[i4];
                    }
                    integerPolynomial5.ensurePositive(3);
                    return integerPolynomial5;
                }
                if (integerPolynomial3.degree() < integerPolynomial4.degree()) {
                    IntegerPolynomial integerPolynomial6 = integerPolynomial3;
                    integerPolynomial3 = integerPolynomial4;
                    integerPolynomial4 = integerPolynomial6;
                    IntegerPolynomial integerPolynomial7 = integerPolynomial;
                    integerPolynomial = integerPolynomial2;
                    integerPolynomial2 = integerPolynomial7;
                }
                if (integerPolynomial3.coeffs[0] == integerPolynomial4.coeffs[0]) {
                    integerPolynomial3.sub(integerPolynomial4, 3);
                    integerPolynomial.sub(integerPolynomial2, 3);
                } else {
                    integerPolynomial3.add(integerPolynomial4, 3);
                    integerPolynomial.add(integerPolynomial2, 3);
                }
            }
        }
    }

    public Resultant resultant() {
        int length = this.coeffs.length;
        LinkedList linkedList = new LinkedList();
        BigInteger bigInteger = Constants.BIGINT_ONE;
        BigInteger bigIntegerMod = Constants.BIGINT_ONE;
        int i = 1;
        PrimeGenerator primeGenerator = new PrimeGenerator();
        while (true) {
            BigInteger bigIntegerNextPrime = primeGenerator.nextPrime();
            ModularResultant modularResultantResultant = resultant(bigIntegerNextPrime.intValue());
            linkedList.add(modularResultantResultant);
            BigInteger bigIntegerMultiply = bigInteger.multiply(bigIntegerNextPrime);
            BigIntEuclidean bigIntEuclideanCalculate = BigIntEuclidean.calculate(bigIntegerNextPrime, bigInteger);
            BigInteger bigInteger2 = bigIntegerMod;
            bigIntegerMod = bigIntegerMod.multiply(bigIntEuclideanCalculate.x.multiply(bigIntegerNextPrime)).add(modularResultantResultant.res.multiply(bigIntEuclideanCalculate.y.multiply(bigInteger))).mod(bigIntegerMultiply);
            bigInteger = bigIntegerMultiply;
            BigInteger bigIntegerDivide = bigInteger.divide(BigInteger.valueOf(2L));
            BigInteger bigIntegerNegate = bigIntegerDivide.negate();
            if (bigIntegerMod.compareTo(bigIntegerDivide) > 0) {
                bigIntegerMod = bigIntegerMod.subtract(bigInteger);
            } else if (bigIntegerMod.compareTo(bigIntegerNegate) < 0) {
                bigIntegerMod = bigIntegerMod.add(bigInteger);
            }
            if (bigIntegerMod.equals(bigInteger2)) {
                i++;
                if (i >= 3) {
                    break;
                }
            } else {
                i = 1;
            }
        }
        while (linkedList.size() > 1) {
            linkedList.addLast(ModularResultant.combineRho((ModularResultant) linkedList.removeFirst(), (ModularResultant) linkedList.removeFirst()));
        }
        BigIntPolynomial bigIntPolynomial = ((ModularResultant) linkedList.getFirst()).rho;
        BigInteger bigIntegerDivide2 = bigInteger.divide(BigInteger.valueOf(2L));
        BigInteger bigIntegerNegate2 = bigIntegerDivide2.negate();
        if (bigIntegerMod.compareTo(bigIntegerDivide2) > 0) {
            bigIntegerMod = bigIntegerMod.subtract(bigInteger);
        }
        if (bigIntegerMod.compareTo(bigIntegerNegate2) < 0) {
            bigIntegerMod = bigIntegerMod.add(bigInteger);
        }
        for (int i2 = 0; i2 < length; i2++) {
            BigInteger bigInteger3 = bigIntPolynomial.coeffs[i2];
            if (bigInteger3.compareTo(bigIntegerDivide2) > 0) {
                bigIntPolynomial.coeffs[i2] = bigInteger3.subtract(bigInteger);
            }
            if (bigInteger3.compareTo(bigIntegerNegate2) < 0) {
                bigIntPolynomial.coeffs[i2] = bigInteger3.add(bigInteger);
            }
        }
        return new Resultant(bigIntPolynomial, bigIntegerMod);
    }

    public Resultant resultantMultiThread() {
        int length = this.coeffs.length;
        BigInteger bigIntegerMultiply = squareSum().pow((length + 1) / 2).multiply(BigInteger.valueOf(2L).pow((degree() + 1) / 2)).multiply(BigInteger.valueOf(2L));
        BigInteger bigIntegerValueOf = BigInteger.valueOf(10000L);
        BigInteger bigIntegerMultiply2 = Constants.BIGINT_ONE;
        LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue();
        Iterator it = BIGINT_PRIMES.iterator();
        ExecutorService executorServiceNewFixedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        while (bigIntegerMultiply2.compareTo(bigIntegerMultiply) < 0) {
            bigIntegerValueOf = it.hasNext() ? (BigInteger) it.next() : bigIntegerValueOf.nextProbablePrime();
            linkedBlockingQueue.add(executorServiceNewFixedThreadPool.submit(new ModResultantTask(bigIntegerValueOf.intValue())));
            bigIntegerMultiply2 = bigIntegerMultiply2.multiply(bigIntegerValueOf);
        }
        ModularResultant modularResultant = null;
        while (!linkedBlockingQueue.isEmpty()) {
            try {
                Future future = (Future) linkedBlockingQueue.take();
                Future future2 = (Future) linkedBlockingQueue.poll();
                if (future2 == null) {
                    modularResultant = (ModularResultant) future.get();
                    break;
                }
                linkedBlockingQueue.add(executorServiceNewFixedThreadPool.submit(new CombineTask((ModularResultant) future.get(), (ModularResultant) future2.get())));
            } catch (Exception e) {
                throw new IllegalStateException(e.toString());
            }
        }
        executorServiceNewFixedThreadPool.shutdown();
        BigInteger bigIntegerAdd = modularResultant.res;
        BigIntPolynomial bigIntPolynomial = modularResultant.rho;
        BigInteger bigIntegerDivide = bigIntegerMultiply2.divide(BigInteger.valueOf(2L));
        BigInteger bigIntegerNegate = bigIntegerDivide.negate();
        if (bigIntegerAdd.compareTo(bigIntegerDivide) > 0) {
            bigIntegerAdd = bigIntegerAdd.subtract(bigIntegerMultiply2);
        }
        if (bigIntegerAdd.compareTo(bigIntegerNegate) < 0) {
            bigIntegerAdd = bigIntegerAdd.add(bigIntegerMultiply2);
        }
        for (int i = 0; i < length; i++) {
            BigInteger bigInteger = bigIntPolynomial.coeffs[i];
            if (bigInteger.compareTo(bigIntegerDivide) > 0) {
                bigIntPolynomial.coeffs[i] = bigInteger.subtract(bigIntegerMultiply2);
            }
            if (bigInteger.compareTo(bigIntegerNegate) < 0) {
                bigIntPolynomial.coeffs[i] = bigInteger.add(bigIntegerMultiply2);
            }
        }
        return new Resultant(bigIntPolynomial, bigIntegerAdd);
    }

    public ModularResultant resultant(int i) {
        int[] iArrCopyOf = Arrays.copyOf(this.coeffs, this.coeffs.length + 1);
        IntegerPolynomial integerPolynomial = new IntegerPolynomial(iArrCopyOf);
        int length = iArrCopyOf.length;
        IntegerPolynomial integerPolynomial2 = new IntegerPolynomial(length);
        integerPolynomial2.coeffs[0] = -1;
        integerPolynomial2.coeffs[length - 1] = 1;
        IntegerPolynomial integerPolynomial3 = new IntegerPolynomial(integerPolynomial.coeffs);
        IntegerPolynomial integerPolynomial4 = new IntegerPolynomial(length);
        IntegerPolynomial integerPolynomial5 = new IntegerPolynomial(length);
        integerPolynomial5.coeffs[0] = 1;
        int iDegree = length - 1;
        int iDegree2 = integerPolynomial3.degree();
        int i2 = iDegree;
        int iPow = 1;
        while (iDegree2 > 0) {
            int iInvert = (Util.invert(integerPolynomial3.coeffs[iDegree2], i) * integerPolynomial2.coeffs[iDegree]) % i;
            integerPolynomial2.multShiftSub(integerPolynomial3, iInvert, iDegree - iDegree2, i);
            integerPolynomial4.multShiftSub(integerPolynomial5, iInvert, iDegree - iDegree2, i);
            iDegree = integerPolynomial2.degree();
            if (iDegree < iDegree2) {
                iPow = (iPow * Util.pow(integerPolynomial3.coeffs[iDegree2], i2 - iDegree, i)) % i;
                if (i2 % 2 == 1 && iDegree2 % 2 == 1) {
                    iPow = (-iPow) % i;
                }
                IntegerPolynomial integerPolynomial6 = integerPolynomial2;
                integerPolynomial2 = integerPolynomial3;
                integerPolynomial3 = integerPolynomial6;
                iDegree = iDegree2;
                IntegerPolynomial integerPolynomial7 = integerPolynomial4;
                integerPolynomial4 = integerPolynomial5;
                integerPolynomial5 = integerPolynomial7;
                i2 = iDegree2;
                iDegree2 = iDegree;
            }
        }
        int iPow2 = (iPow * Util.pow(integerPolynomial3.coeffs[0], iDegree, i)) % i;
        integerPolynomial5.mult(Util.invert(integerPolynomial3.coeffs[0], i));
        integerPolynomial5.mod(i);
        integerPolynomial5.mult(iPow2);
        integerPolynomial5.mod(i);
        integerPolynomial5.coeffs = Arrays.copyOf(integerPolynomial5.coeffs, integerPolynomial5.coeffs.length - 1);
        return new ModularResultant(new BigIntPolynomial(integerPolynomial5), BigInteger.valueOf(iPow2), BigInteger.valueOf(i));
    }

    private void multShiftSub(IntegerPolynomial integerPolynomial, int i, int i2, int i3) {
        int length = this.coeffs.length;
        for (int i4 = i2; i4 < length; i4++) {
            this.coeffs[i4] = (this.coeffs[i4] - (integerPolynomial.coeffs[i4 - i2] * i)) % i3;
        }
    }

    private BigInteger squareSum() {
        BigInteger bigIntegerAdd = Constants.BIGINT_ZERO;
        for (int i = 0; i < this.coeffs.length; i++) {
            bigIntegerAdd = bigIntegerAdd.add(BigInteger.valueOf(this.coeffs[i] * this.coeffs[i]));
        }
        return bigIntegerAdd;
    }

    int degree() {
        int length = this.coeffs.length - 1;
        while (length > 0 && this.coeffs[length] == 0) {
            length--;
        }
        return length;
    }

    public void add(IntegerPolynomial integerPolynomial, int i) {
        add(integerPolynomial);
        mod(i);
    }

    public void add(IntegerPolynomial integerPolynomial) {
        if (integerPolynomial.coeffs.length > this.coeffs.length) {
            this.coeffs = Arrays.copyOf(this.coeffs, integerPolynomial.coeffs.length);
        }
        for (int i = 0; i < integerPolynomial.coeffs.length; i++) {
            int[] iArr = this.coeffs;
            int i2 = i;
            iArr[i2] = iArr[i2] + integerPolynomial.coeffs[i];
        }
    }

    public void sub(IntegerPolynomial integerPolynomial, int i) {
        sub(integerPolynomial);
        mod(i);
    }

    public void sub(IntegerPolynomial integerPolynomial) {
        if (integerPolynomial.coeffs.length > this.coeffs.length) {
            this.coeffs = Arrays.copyOf(this.coeffs, integerPolynomial.coeffs.length);
        }
        for (int i = 0; i < integerPolynomial.coeffs.length; i++) {
            int[] iArr = this.coeffs;
            int i2 = i;
            iArr[i2] = iArr[i2] - integerPolynomial.coeffs[i];
        }
    }

    void sub(int i) {
        for (int i2 = 0; i2 < this.coeffs.length; i2++) {
            int[] iArr = this.coeffs;
            int i3 = i2;
            iArr[i3] = iArr[i3] - i;
        }
    }

    public void mult(int i) {
        for (int i2 = 0; i2 < this.coeffs.length; i2++) {
            int[] iArr = this.coeffs;
            int i3 = i2;
            iArr[i3] = iArr[i3] * i;
        }
    }

    private void mult2(int i) {
        for (int i2 = 0; i2 < this.coeffs.length; i2++) {
            int[] iArr = this.coeffs;
            int i3 = i2;
            iArr[i3] = iArr[i3] * 2;
            int[] iArr2 = this.coeffs;
            int i4 = i2;
            iArr2[i4] = iArr2[i4] % i;
        }
    }

    public void mult3(int i) {
        for (int i2 = 0; i2 < this.coeffs.length; i2++) {
            int[] iArr = this.coeffs;
            int i3 = i2;
            iArr[i3] = iArr[i3] * 3;
            int[] iArr2 = this.coeffs;
            int i4 = i2;
            iArr2[i4] = iArr2[i4] % i;
        }
    }

    public void div(int i) {
        int i2 = (i + 1) / 2;
        for (int i3 = 0; i3 < this.coeffs.length; i3++) {
            int[] iArr = this.coeffs;
            int i4 = i3;
            iArr[i4] = iArr[i4] + (this.coeffs[i3] > 0 ? i2 : -i2);
            int[] iArr2 = this.coeffs;
            int i5 = i3;
            iArr2[i5] = iArr2[i5] / i;
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

    public void modPositive(int i) {
        mod(i);
        ensurePositive(i);
    }

    void modCenter(int i) {
        mod(i);
        for (int i2 = 0; i2 < this.coeffs.length; i2++) {
            while (this.coeffs[i2] < i / 2) {
                int[] iArr = this.coeffs;
                int i3 = i2;
                iArr[i3] = iArr[i3] + i;
            }
            while (this.coeffs[i2] >= i / 2) {
                int[] iArr2 = this.coeffs;
                int i4 = i2;
                iArr2[i4] = iArr2[i4] - i;
            }
        }
    }

    public void mod(int i) {
        for (int i2 = 0; i2 < this.coeffs.length; i2++) {
            int[] iArr = this.coeffs;
            int i3 = i2;
            iArr[i3] = iArr[i3] % i;
        }
    }

    public void ensurePositive(int i) {
        for (int i2 = 0; i2 < this.coeffs.length; i2++) {
            while (this.coeffs[i2] < 0) {
                int[] iArr = this.coeffs;
                int i3 = i2;
                iArr[i3] = iArr[i3] + i;
            }
        }
    }

    public long centeredNormSq(int i) {
        int length = this.coeffs.length;
        IntegerPolynomial integerPolynomial = (IntegerPolynomial) clone();
        integerPolynomial.shiftGap(i);
        long j = 0;
        long j2 = 0;
        for (int i2 = 0; i2 != integerPolynomial.coeffs.length; i2++) {
            int i3 = integerPolynomial.coeffs[i2];
            j += (long) i3;
            j2 += (long) (i3 * i3);
        }
        return j2 - ((j * j) / ((long) length));
    }

    void shiftGap(int i) {
        modCenter(i);
        int[] iArrClone = Arrays.clone(this.coeffs);
        sort(iArrClone);
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < iArrClone.length - 1; i4++) {
            int i5 = iArrClone[i4 + 1] - iArrClone[i4];
            if (i5 > i2) {
                i2 = i5;
                i3 = iArrClone[i4];
            }
        }
        int i6 = iArrClone[0];
        int i7 = iArrClone[iArrClone.length - 1];
        sub((i - i7) + i6 > i2 ? (i7 + i6) / 2 : i3 + (i2 / 2) + (i / 2));
    }

    private void sort(int[] iArr) {
        boolean z = true;
        while (z) {
            z = false;
            for (int i = 0; i != iArr.length - 1; i++) {
                if (iArr[i] > iArr[i + 1]) {
                    int i2 = iArr[i];
                    iArr[i] = iArr[i + 1];
                    iArr[i + 1] = i2;
                    z = true;
                }
            }
        }
    }

    public void center0(int i) {
        for (int i2 = 0; i2 < this.coeffs.length; i2++) {
            while (this.coeffs[i2] < (-i) / 2) {
                int[] iArr = this.coeffs;
                int i3 = i2;
                iArr[i3] = iArr[i3] + i;
            }
            while (this.coeffs[i2] > i / 2) {
                int[] iArr2 = this.coeffs;
                int i4 = i2;
                iArr2[i4] = iArr2[i4] - i;
            }
        }
    }

    public int sumCoeffs() {
        int i = 0;
        for (int i2 = 0; i2 < this.coeffs.length; i2++) {
            i += this.coeffs[i2];
        }
        return i;
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

    public int count(int i) {
        int i2 = 0;
        for (int i3 = 0; i3 != this.coeffs.length; i3++) {
            if (this.coeffs[i3] == i) {
                i2++;
            }
        }
        return i2;
    }

    public void rotate1() {
        int i = this.coeffs[this.coeffs.length - 1];
        for (int length = this.coeffs.length - 1; length > 0; length--) {
            this.coeffs[length] = this.coeffs[length - 1];
        }
        this.coeffs[0] = i;
    }

    public void clear() {
        for (int i = 0; i < this.coeffs.length; i++) {
            this.coeffs[i] = 0;
        }
    }

    @Override // org.bouncycastle.pqc.legacy.math.ntru.polynomial.Polynomial
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

    static {
        for (int i = 0; i != PRIMES.length; i++) {
            BIGINT_PRIMES.add(BigInteger.valueOf(PRIMES[i]));
        }
    }
}
