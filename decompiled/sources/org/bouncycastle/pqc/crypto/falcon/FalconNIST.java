package org.bouncycastle.pqc.crypto.falcon;

import com.sun.jna.platform.win32.WinError;
import java.security.SecureRandom;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/falcon/FalconNIST.class */
class FalconNIST {
    int NONCELEN;
    int LOGN;
    private int N;
    private SecureRandom rand;
    private int CRYPTO_SECRETKEYBYTES;
    private int CRYPTO_PUBLICKEYBYTES;
    int CRYPTO_BYTES;
    private FalconCodec codec = new FalconCodec();

    FalconNIST(int i, int i2, SecureRandom secureRandom) {
        this.rand = secureRandom;
        this.LOGN = i;
        this.NONCELEN = i2;
        this.N = 1 << i;
        this.CRYPTO_PUBLICKEYBYTES = 1 + ((14 * this.N) / 8);
        if (i == 10) {
            this.CRYPTO_SECRETKEYBYTES = 2305;
            this.CRYPTO_BYTES = WinError.ERROR_PASSWORD_EXPIRED;
            return;
        }
        if (i == 9 || i == 8) {
            this.CRYPTO_SECRETKEYBYTES = 1 + (((6 * this.N) * 2) / 8) + this.N;
            this.CRYPTO_BYTES = WinError.ERROR_DBG_UNABLE_TO_PROVIDE_HANDLE;
        } else if (i == 7 || i == 6) {
            this.CRYPTO_SECRETKEYBYTES = 1 + (((7 * this.N) * 2) / 8) + this.N;
            this.CRYPTO_BYTES = WinError.ERROR_DBG_UNABLE_TO_PROVIDE_HANDLE;
        } else {
            this.CRYPTO_SECRETKEYBYTES = 1 + (this.N * 2) + this.N;
            this.CRYPTO_BYTES = WinError.ERROR_DBG_UNABLE_TO_PROVIDE_HANDLE;
        }
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$ArrayArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    byte[][] crypto_sign_keypair(byte[] bArr, int i, byte[] bArr2, int i2) {
        byte[] bArr3 = new byte[this.N];
        byte[] bArr4 = new byte[this.N];
        byte[] bArr5 = new byte[this.N];
        short[] sArr = new short[this.N];
        byte[] bArr6 = new byte[48];
        SHAKE256 shake256 = new SHAKE256();
        FalconKeyGen falconKeyGen = new FalconKeyGen();
        this.rand.nextBytes(bArr6);
        shake256.inner_shake256_init();
        shake256.inner_shake256_inject(bArr6, 0, bArr6.length);
        shake256.i_shake256_flip();
        falconKeyGen.keygen(shake256, bArr3, 0, bArr4, 0, bArr5, 0, null, 0, sArr, 0, this.LOGN);
        bArr2[i2 + 0] = (byte) (80 + this.LOGN);
        int iTrim_i8_encode = this.codec.trim_i8_encode(bArr2, i2 + 1, this.CRYPTO_SECRETKEYBYTES - 1, bArr3, 0, this.LOGN, this.codec.max_fg_bits[this.LOGN]);
        if (iTrim_i8_encode == 0) {
            throw new IllegalStateException("f encode failed");
        }
        byte[] bArrCopyOfRange = Arrays.copyOfRange(bArr2, i2 + 1, 1 + iTrim_i8_encode);
        int i3 = 1 + iTrim_i8_encode;
        int iTrim_i8_encode2 = this.codec.trim_i8_encode(bArr2, i2 + i3, this.CRYPTO_SECRETKEYBYTES - i3, bArr4, 0, this.LOGN, this.codec.max_fg_bits[this.LOGN]);
        if (iTrim_i8_encode2 == 0) {
            throw new IllegalStateException("g encode failed");
        }
        byte[] bArrCopyOfRange2 = Arrays.copyOfRange(bArr2, i2 + i3, i3 + iTrim_i8_encode2);
        int i4 = i3 + iTrim_i8_encode2;
        int iTrim_i8_encode3 = this.codec.trim_i8_encode(bArr2, i2 + i4, this.CRYPTO_SECRETKEYBYTES - i4, bArr5, 0, this.LOGN, this.codec.max_FG_bits[this.LOGN]);
        if (iTrim_i8_encode3 == 0) {
            throw new IllegalStateException("F encode failed");
        }
        byte[] bArrCopyOfRange3 = Arrays.copyOfRange(bArr2, i2 + i4, i4 + iTrim_i8_encode3);
        if (i4 + iTrim_i8_encode3 != this.CRYPTO_SECRETKEYBYTES) {
            throw new IllegalStateException("secret key encoding failed");
        }
        bArr[i + 0] = (byte) (0 + this.LOGN);
        if (this.codec.modq_encode(bArr, i + 1, this.CRYPTO_PUBLICKEYBYTES - 1, sArr, 0, this.LOGN) != this.CRYPTO_PUBLICKEYBYTES - 1) {
            throw new IllegalStateException("public key encoding failed");
        }
        return new byte[][]{Arrays.copyOfRange(bArr, 1, bArr.length), bArrCopyOfRange, bArrCopyOfRange2, bArrCopyOfRange3};
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$ArrayArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    byte[] crypto_sign(boolean z, byte[] bArr, byte[] bArr2, int i, int i2, byte[] bArr3, int i3) {
        int iComp_encode;
        byte[] bArr4 = new byte[this.N];
        byte[] bArr5 = new byte[this.N];
        byte[] bArr6 = new byte[this.N];
        byte[] bArr7 = new byte[this.N];
        short[] sArr = new short[this.N];
        short[] sArr2 = new short[this.N];
        byte[] bArr8 = new byte[48];
        byte[] bArr9 = new byte[this.NONCELEN];
        SHAKE256 shake256 = new SHAKE256();
        FalconSign falconSign = new FalconSign();
        FalconVrfy falconVrfy = new FalconVrfy();
        FalconCommon falconCommon = new FalconCommon();
        int iTrim_i8_decode = this.codec.trim_i8_decode(bArr4, 0, this.LOGN, this.codec.max_fg_bits[this.LOGN], bArr3, i3 + 0, this.CRYPTO_SECRETKEYBYTES - 0);
        if (iTrim_i8_decode == 0) {
            throw new IllegalStateException("f decode failed");
        }
        int i4 = 0 + iTrim_i8_decode;
        int iTrim_i8_decode2 = this.codec.trim_i8_decode(bArr5, 0, this.LOGN, this.codec.max_fg_bits[this.LOGN], bArr3, i3 + i4, this.CRYPTO_SECRETKEYBYTES - i4);
        if (iTrim_i8_decode2 == 0) {
            throw new IllegalStateException("g decode failed");
        }
        int i5 = i4 + iTrim_i8_decode2;
        int iTrim_i8_decode3 = this.codec.trim_i8_decode(bArr6, 0, this.LOGN, this.codec.max_FG_bits[this.LOGN], bArr3, i3 + i5, this.CRYPTO_SECRETKEYBYTES - i5);
        if (iTrim_i8_decode3 == 0) {
            throw new IllegalArgumentException("F decode failed");
        }
        if (i5 + iTrim_i8_decode3 != this.CRYPTO_SECRETKEYBYTES - 1) {
            throw new IllegalStateException("full key not used");
        }
        if (!falconVrfy.complete_private(bArr7, 0, bArr4, 0, bArr5, 0, bArr6, 0, this.LOGN, new short[2 * this.N], 0)) {
            throw new IllegalStateException("complete_private failed");
        }
        this.rand.nextBytes(bArr9);
        shake256.inner_shake256_init();
        shake256.inner_shake256_inject(bArr9, 0, this.NONCELEN);
        shake256.inner_shake256_inject(bArr2, i, i2);
        shake256.i_shake256_flip();
        falconCommon.hash_to_point_vartime(shake256, sArr2, 0, this.LOGN);
        this.rand.nextBytes(bArr8);
        shake256.inner_shake256_init();
        shake256.inner_shake256_inject(bArr8, 0, bArr8.length);
        shake256.i_shake256_flip();
        falconSign.sign_dyn(sArr, 0, shake256, bArr4, 0, bArr5, 0, bArr6, 0, bArr7, 0, sArr2, 0, this.LOGN, new FalconFPR[10 * this.N], 0);
        byte[] bArr10 = new byte[(this.CRYPTO_BYTES - 2) - this.NONCELEN];
        if (z) {
            bArr10[0] = (byte) (32 + this.LOGN);
            int iComp_encode2 = this.codec.comp_encode(bArr10, 1, bArr10.length - 1, sArr, 0, this.LOGN);
            if (iComp_encode2 == 0) {
                throw new IllegalStateException("signature failed to generate");
            }
            iComp_encode = iComp_encode2 + 1;
        } else {
            iComp_encode = this.codec.comp_encode(bArr10, 0, bArr10.length, sArr, 0, this.LOGN);
            if (iComp_encode == 0) {
                throw new IllegalStateException("signature failed to generate");
            }
        }
        bArr[0] = (byte) (48 + this.LOGN);
        System.arraycopy(bArr9, 0, bArr, 1, this.NONCELEN);
        System.arraycopy(bArr10, 0, bArr, 1 + this.NONCELEN, iComp_encode);
        return Arrays.copyOfRange(bArr, 0, 1 + this.NONCELEN + iComp_encode);
    }

    int crypto_sign_open(boolean z, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, int i) {
        short[] sArr = new short[this.N];
        short[] sArr2 = new short[this.N];
        short[] sArr3 = new short[this.N];
        SHAKE256 shake256 = new SHAKE256();
        FalconVrfy falconVrfy = new FalconVrfy();
        FalconCommon falconCommon = new FalconCommon();
        if (this.codec.modq_decode(sArr, 0, this.LOGN, bArr4, i, this.CRYPTO_PUBLICKEYBYTES - 1) != this.CRYPTO_PUBLICKEYBYTES - 1) {
            return -1;
        }
        falconVrfy.to_ntt_monty(sArr, 0, this.LOGN);
        int length = bArr.length;
        int length2 = bArr3.length;
        if (z) {
            if (length < 1 || bArr[0] != ((byte) (32 + this.LOGN)) || this.codec.comp_decode(sArr3, 0, this.LOGN, bArr, 1, length - 1) != length - 1) {
                return -1;
            }
        } else if (length < 1 || this.codec.comp_decode(sArr3, 0, this.LOGN, bArr, 0, length) != length) {
            return -1;
        }
        shake256.inner_shake256_init();
        shake256.inner_shake256_inject(bArr2, 0, this.NONCELEN);
        shake256.inner_shake256_inject(bArr3, 0, length2);
        shake256.i_shake256_flip();
        falconCommon.hash_to_point_vartime(shake256, sArr2, 0, this.LOGN);
        return falconVrfy.verify_raw(sArr2, 0, sArr3, 0, sArr, 0, this.LOGN, new short[this.N], 0) == 0 ? -1 : 0;
    }
}
