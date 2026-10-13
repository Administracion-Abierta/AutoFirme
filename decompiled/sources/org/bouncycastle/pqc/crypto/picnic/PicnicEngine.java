package org.bouncycastle.pqc.crypto.picnic;

import com.sun.jna.platform.win32.WinError;
import java.security.SecureRandom;
import java.util.logging.Logger;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.math.raw.Bits;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/picnic/PicnicEngine.class */
class PicnicEngine {
    private static final Logger LOG = Logger.getLogger(PicnicEngine.class.getName());
    protected static final int saltSizeBytes = 32;
    private static final int MAX_DIGEST_SIZE = 64;
    private static final int WORD_SIZE_BITS = 32;
    private static final int LOWMC_MAX_STATE_SIZE = 64;
    protected static final int LOWMC_MAX_WORDS = 16;
    protected static final int LOWMC_MAX_KEY_BITS = 256;
    protected static final int LOWMC_MAX_AND_GATES = 1144;
    private static final int MAX_AUX_BYTES = 176;
    private static final int PICNIC_MAX_LOWMC_BLOCK_SIZE = 32;
    private static final int TRANSFORM_FS = 0;
    private static final int TRANSFORM_UR = 1;
    private static final int TRANSFORM_INVALID = 255;
    private final int CRYPTO_SECRETKEYBYTES;
    private final int CRYPTO_PUBLICKEYBYTES;
    private final int CRYPTO_BYTES;
    protected final int numRounds;
    protected final int numSboxes;
    protected final int stateSizeBits;
    protected final int stateSizeBytes;
    protected final int stateSizeWords;
    protected final int andSizeBytes;
    protected final int UnruhGWithoutInputBytes;
    protected final int UnruhGWithInputBytes;
    protected final int numMPCRounds;
    protected final int numOpenedRounds;
    protected final int numMPCParties;
    protected final int seedSizeBytes;
    protected final int digestSizeBytes;
    protected final int pqSecurityLevel;
    protected final Xof digest;
    private final int transform;
    private final int parameters;
    private int signatureLength;
    protected final LowmcConstants lowmcConstants;

    public int getSecretKeySize() {
        return this.CRYPTO_SECRETKEYBYTES;
    }

    public int getPublicKeySize() {
        return this.CRYPTO_PUBLICKEYBYTES;
    }

    public int getSignatureSize(int i) {
        return this.CRYPTO_BYTES + i;
    }

    public int getTrueSignatureSize() {
        return this.signatureLength;
    }

    PicnicEngine(int i, LowmcConstants lowmcConstants) {
        this.lowmcConstants = lowmcConstants;
        this.parameters = i;
        switch (this.parameters) {
            case 1:
            case 2:
                this.pqSecurityLevel = 64;
                this.stateSizeBits = 128;
                this.numMPCRounds = 219;
                this.numMPCParties = 3;
                this.numSboxes = 10;
                this.numRounds = 20;
                this.digestSizeBytes = 32;
                this.numOpenedRounds = 0;
                break;
            case 3:
            case 4:
                this.pqSecurityLevel = 96;
                this.stateSizeBits = 192;
                this.numMPCRounds = 329;
                this.numMPCParties = 3;
                this.numSboxes = 10;
                this.numRounds = 30;
                this.digestSizeBytes = 48;
                this.numOpenedRounds = 0;
                break;
            case 5:
            case 6:
                this.pqSecurityLevel = 128;
                this.stateSizeBits = 256;
                this.numMPCRounds = 438;
                this.numMPCParties = 3;
                this.numSboxes = 10;
                this.numRounds = 38;
                this.digestSizeBytes = 64;
                this.numOpenedRounds = 0;
                break;
            case 7:
                this.pqSecurityLevel = 64;
                this.stateSizeBits = 129;
                this.numMPCRounds = 250;
                this.numOpenedRounds = 36;
                this.numMPCParties = 16;
                this.numSboxes = 43;
                this.numRounds = 4;
                this.digestSizeBytes = 32;
                break;
            case 8:
                this.pqSecurityLevel = 96;
                this.stateSizeBits = 192;
                this.numMPCRounds = 419;
                this.numOpenedRounds = 52;
                this.numMPCParties = 16;
                this.numSboxes = 64;
                this.numRounds = 4;
                this.digestSizeBytes = 48;
                break;
            case 9:
                this.pqSecurityLevel = 128;
                this.stateSizeBits = 255;
                this.numMPCRounds = WinError.ERROR_FOUND_OUT_OF_SCOPE;
                this.numOpenedRounds = 68;
                this.numMPCParties = 16;
                this.numSboxes = 85;
                this.numRounds = 4;
                this.digestSizeBytes = 64;
                break;
            case 10:
                this.pqSecurityLevel = 64;
                this.stateSizeBits = 129;
                this.numMPCRounds = 219;
                this.numMPCParties = 3;
                this.numSboxes = 43;
                this.numRounds = 4;
                this.digestSizeBytes = 32;
                this.numOpenedRounds = 0;
                break;
            case 11:
                this.pqSecurityLevel = 96;
                this.stateSizeBits = 192;
                this.numMPCRounds = 329;
                this.numMPCParties = 3;
                this.numSboxes = 64;
                this.numRounds = 4;
                this.digestSizeBytes = 48;
                this.numOpenedRounds = 0;
                break;
            case 12:
                this.pqSecurityLevel = 128;
                this.stateSizeBits = 255;
                this.numMPCRounds = 438;
                this.numMPCParties = 3;
                this.numSboxes = 85;
                this.numRounds = 4;
                this.digestSizeBytes = 64;
                this.numOpenedRounds = 0;
                break;
            default:
                throw new IllegalArgumentException("unknown parameter set " + this.parameters);
        }
        switch (this.parameters) {
            case 1:
                this.CRYPTO_SECRETKEYBYTES = 49;
                this.CRYPTO_PUBLICKEYBYTES = 33;
                this.CRYPTO_BYTES = 34036;
                break;
            case 2:
                this.CRYPTO_SECRETKEYBYTES = 49;
                this.CRYPTO_PUBLICKEYBYTES = 33;
                this.CRYPTO_BYTES = 53965;
                break;
            case 3:
                this.CRYPTO_SECRETKEYBYTES = 73;
                this.CRYPTO_PUBLICKEYBYTES = 49;
                this.CRYPTO_BYTES = 76784;
                break;
            case 4:
                this.CRYPTO_SECRETKEYBYTES = 73;
                this.CRYPTO_PUBLICKEYBYTES = 49;
                this.CRYPTO_BYTES = 121857;
                break;
            case 5:
                this.CRYPTO_SECRETKEYBYTES = 97;
                this.CRYPTO_PUBLICKEYBYTES = 65;
                this.CRYPTO_BYTES = 132876;
                break;
            case 6:
                this.CRYPTO_SECRETKEYBYTES = 97;
                this.CRYPTO_PUBLICKEYBYTES = 65;
                this.CRYPTO_BYTES = 209526;
                break;
            case 7:
                this.CRYPTO_SECRETKEYBYTES = 52;
                this.CRYPTO_PUBLICKEYBYTES = 35;
                this.CRYPTO_BYTES = 14612;
                break;
            case 8:
                this.CRYPTO_SECRETKEYBYTES = 73;
                this.CRYPTO_PUBLICKEYBYTES = 49;
                this.CRYPTO_BYTES = 35028;
                break;
            case 9:
                this.CRYPTO_SECRETKEYBYTES = 97;
                this.CRYPTO_PUBLICKEYBYTES = 65;
                this.CRYPTO_BYTES = 61028;
                break;
            case 10:
                this.CRYPTO_SECRETKEYBYTES = 52;
                this.CRYPTO_PUBLICKEYBYTES = 35;
                this.CRYPTO_BYTES = 32061;
                break;
            case 11:
                this.CRYPTO_SECRETKEYBYTES = 73;
                this.CRYPTO_PUBLICKEYBYTES = 49;
                this.CRYPTO_BYTES = 71179;
                break;
            case 12:
                this.CRYPTO_SECRETKEYBYTES = 97;
                this.CRYPTO_PUBLICKEYBYTES = 65;
                this.CRYPTO_BYTES = 126286;
                break;
            default:
                this.CRYPTO_SECRETKEYBYTES = -1;
                this.CRYPTO_PUBLICKEYBYTES = -1;
                this.CRYPTO_BYTES = -1;
                break;
        }
        this.andSizeBytes = Utils.numBytes(this.numSboxes * 3 * this.numRounds);
        this.stateSizeBytes = Utils.numBytes(this.stateSizeBits);
        this.seedSizeBytes = Utils.numBytes(2 * this.pqSecurityLevel);
        this.stateSizeWords = ((this.stateSizeBits + 32) - 1) / 32;
        switch (this.parameters) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                this.transform = 0;
                break;
            case 2:
            case 4:
            case 6:
                this.transform = 1;
                break;
            default:
                this.transform = 255;
                break;
        }
        if (this.transform == 1) {
            this.UnruhGWithoutInputBytes = this.seedSizeBytes + this.andSizeBytes;
            this.UnruhGWithInputBytes = this.UnruhGWithoutInputBytes + this.stateSizeBytes;
        } else {
            this.UnruhGWithoutInputBytes = 0;
            this.UnruhGWithInputBytes = 0;
        }
        if (this.stateSizeBits == 128 || this.stateSizeBits == 129) {
            this.digest = new SHAKEDigest(128);
        } else {
            this.digest = new SHAKEDigest(256);
        }
    }

    public boolean crypto_sign_open(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        if (picnic_verify(bArr3, Arrays.copyOfRange(bArr2, 4, 4 + bArr.length), bArr2, Pack.littleEndianToInt(bArr2, 0)) == -1) {
            return false;
        }
        System.arraycopy(bArr2, 4, bArr, 0, bArr.length);
        return true;
    }

    private int picnic_verify(byte[] bArr, byte[] bArr2, byte[] bArr3, int i) {
        int[] iArr = new int[this.stateSizeWords];
        int[] iArr2 = new int[this.stateSizeWords];
        picnic_read_public_key(iArr, iArr2, bArr);
        if (is_picnic3(this.parameters)) {
            Signature2 signature2 = new Signature2(this);
            if (deserializeSignature2(signature2, bArr3, i, bArr2.length + 4) == 0) {
                return verify_picnic3(signature2, iArr, iArr2, bArr2);
            }
            LOG.fine("Error couldn't deserialize signature (2)!");
            return -1;
        }
        Signature signature = new Signature(this);
        if (deserializeSignature(signature, bArr3, i, bArr2.length + 4) == 0) {
            return verify(signature, iArr, iArr2, bArr2);
        }
        LOG.fine("Error couldn't deserialize signature!");
        return -1;
    }

    private int verify(Signature signature, int[] iArr, int[] iArr2, byte[] bArr) {
        byte[][][] bArr2 = new byte[this.numMPCRounds][this.numMPCParties][this.digestSizeBytes];
        byte[][][] bArr3 = new byte[this.numMPCRounds][3][this.UnruhGWithInputBytes];
        int[][][] iArr3 = new int[this.numMPCRounds][3][this.stateSizeBytes];
        Signature.Proof[] proofArr = signature.proofs;
        byte[] bArr4 = signature.challengeBits;
        int i = 0;
        byte[] bArr5 = new byte[Math.max(6 * this.stateSizeBytes, this.stateSizeBytes + this.andSizeBytes)];
        Tape tape = new Tape(this);
        View[] viewArr = new View[this.numMPCRounds];
        View[] viewArr2 = new View[this.numMPCRounds];
        for (int i2 = 0; i2 < this.numMPCRounds; i2++) {
            viewArr[i2] = new View(this);
            viewArr2[i2] = new View(this);
            if (!verifyProof(proofArr[i2], viewArr[i2], viewArr2[i2], getChallenge(bArr4, i2), signature.salt, i2, bArr5, iArr2, tape)) {
                LOG.fine("Invalid signature. Did not verify");
                return -1;
            }
            int challenge = getChallenge(bArr4, i2);
            Commit(proofArr[i2].seed1, 0, viewArr[i2], bArr2[i2][challenge]);
            Commit(proofArr[i2].seed2, 0, viewArr2[i2], bArr2[i2][(challenge + 1) % 3]);
            System.arraycopy(proofArr[i2].view3Commitment, 0, bArr2[i2][(challenge + 2) % 3], 0, this.digestSizeBytes);
            if (this.transform == 1) {
                G(challenge, proofArr[i2].seed1, 0, viewArr[i2], bArr3[i2][challenge]);
                G((challenge + 1) % 3, proofArr[i2].seed2, 0, viewArr2[i2], bArr3[i2][(challenge + 1) % 3]);
                System.arraycopy(proofArr[i2].view3UnruhG, 0, bArr3[i2][(challenge + 2) % 3], 0, challenge == 0 ? this.UnruhGWithInputBytes : this.UnruhGWithoutInputBytes);
            }
            iArr3[i2][challenge] = viewArr[i2].outputShare;
            iArr3[i2][(challenge + 1) % 3] = viewArr2[i2].outputShare;
            int[] iArr4 = new int[this.stateSizeWords];
            xor_three(iArr4, viewArr[i2].outputShare, viewArr2[i2].outputShare, iArr);
            iArr3[i2][(challenge + 2) % 3] = iArr4;
        }
        byte[] bArr6 = new byte[Utils.numBytes(2 * this.numMPCRounds)];
        H3(iArr, iArr2, iArr3, bArr2, bArr6, signature.salt, bArr, bArr3);
        if (!subarrayEquals(bArr4, bArr6, Utils.numBytes(2 * this.numMPCRounds))) {
            LOG.fine("Invalid signature. Did not verify");
            i = -1;
        }
        return i;
    }

    boolean verifyProof(Signature.Proof proof, View view, View view2, int i, byte[] bArr, int i2, byte[] bArr2, int[] iArr, Tape tape) {
        System.arraycopy(proof.communicatedBits, 0, view2.communicatedBits, 0, this.andSizeBytes);
        tape.pos = 0;
        boolean z = false;
        switch (i) {
            case 0:
                boolean zCreateRandomTape = createRandomTape(proof.seed1, 0, bArr, i2, 0, bArr2, this.stateSizeBytes + this.andSizeBytes);
                Pack.littleEndianToInt(bArr2, 0, view.inputShare);
                System.arraycopy(bArr2, this.stateSizeBytes, tape.tapes[0], 0, this.andSizeBytes);
                z = zCreateRandomTape && createRandomTape(proof.seed2, 0, bArr, i2, 1, bArr2, this.stateSizeBytes + this.andSizeBytes);
                if (z) {
                    Pack.littleEndianToInt(bArr2, 0, view2.inputShare);
                    System.arraycopy(bArr2, this.stateSizeBytes, tape.tapes[1], 0, this.andSizeBytes);
                }
                break;
            case 1:
                boolean zCreateRandomTape2 = createRandomTape(proof.seed1, 0, bArr, i2, 1, bArr2, this.stateSizeBytes + this.andSizeBytes);
                Pack.littleEndianToInt(bArr2, 0, view.inputShare);
                System.arraycopy(bArr2, this.stateSizeBytes, tape.tapes[0], 0, this.andSizeBytes);
                z = zCreateRandomTape2 && createRandomTape(proof.seed2, 0, bArr, i2, 2, tape.tapes[1], this.andSizeBytes);
                if (z) {
                    System.arraycopy(proof.inputShare, 0, view2.inputShare, 0, this.stateSizeWords);
                }
                break;
            case 2:
                boolean zCreateRandomTape3 = createRandomTape(proof.seed1, 0, bArr, i2, 2, tape.tapes[0], this.andSizeBytes);
                System.arraycopy(proof.inputShare, 0, view.inputShare, 0, this.stateSizeWords);
                z = zCreateRandomTape3 && createRandomTape(proof.seed2, 0, bArr, i2, 0, bArr2, this.stateSizeBytes + this.andSizeBytes);
                if (z) {
                    Pack.littleEndianToInt(bArr2, 0, view2.inputShare);
                    System.arraycopy(bArr2, this.stateSizeBytes, tape.tapes[1], 0, this.andSizeBytes);
                }
                break;
            default:
                LOG.fine("Invalid Challenge!");
                break;
        }
        if (!z) {
            LOG.fine("Failed to generate random tapes, signature verification will fail (but signature may actually be valid)");
            return false;
        }
        Utils.zeroTrailingBits(view.inputShare, this.stateSizeBits);
        Utils.zeroTrailingBits(view2.inputShare, this.stateSizeBits);
        mpc_LowMC_verify(view, view2, tape, Pack.littleEndianToInt(bArr2, 0, bArr2.length / 4), iArr, i);
        return true;
    }

    void mpc_LowMC_verify(View view, View view2, Tape tape, int[] iArr, int[] iArr2, int i) {
        Arrays.fill(iArr, 0, iArr.length, 0);
        mpc_xor_constant_verify(iArr, iArr2, 0, this.stateSizeWords, i);
        KMatricesWithPointer kMatricesWithPointerKMatrix = this.lowmcConstants.KMatrix(this, 0);
        matrix_mul_offset(iArr, 0, view.inputShare, 0, kMatricesWithPointerKMatrix.getData(), kMatricesWithPointerKMatrix.getMatrixPointer());
        matrix_mul_offset(iArr, this.stateSizeWords, view2.inputShare, 0, kMatricesWithPointerKMatrix.getData(), kMatricesWithPointerKMatrix.getMatrixPointer());
        mpc_xor(iArr, iArr, 2);
        for (int i2 = 1; i2 <= this.numRounds; i2++) {
            KMatricesWithPointer kMatricesWithPointerKMatrix2 = this.lowmcConstants.KMatrix(this, i2);
            matrix_mul_offset(iArr, 0, view.inputShare, 0, kMatricesWithPointerKMatrix2.getData(), kMatricesWithPointerKMatrix2.getMatrixPointer());
            matrix_mul_offset(iArr, this.stateSizeWords, view2.inputShare, 0, kMatricesWithPointerKMatrix2.getData(), kMatricesWithPointerKMatrix2.getMatrixPointer());
            mpc_substitution_verify(iArr, tape, view, view2);
            KMatricesWithPointer kMatricesWithPointerLMatrix = this.lowmcConstants.LMatrix(this, i2 - 1);
            mpc_matrix_mul(iArr, 2 * this.stateSizeWords, iArr, 2 * this.stateSizeWords, kMatricesWithPointerLMatrix.getData(), kMatricesWithPointerLMatrix.getMatrixPointer(), 2);
            KMatricesWithPointer kMatricesWithPointerRConstant = this.lowmcConstants.RConstant(this, i2 - 1);
            mpc_xor_constant_verify(iArr, kMatricesWithPointerRConstant.getData(), kMatricesWithPointerRConstant.getMatrixPointer(), this.stateSizeWords, i);
            mpc_xor(iArr, iArr, 2);
        }
        System.arraycopy(iArr, 2 * this.stateSizeWords, view.outputShare, 0, this.stateSizeWords);
        System.arraycopy(iArr, 3 * this.stateSizeWords, view2.outputShare, 0, this.stateSizeWords);
    }

    void mpc_substitution_verify(int[] iArr, Tape tape, View view, View view2) {
        int[] iArr2 = new int[2];
        int[] iArr3 = new int[2];
        int[] iArr4 = new int[2];
        int[] iArr5 = new int[2];
        int[] iArr6 = new int[2];
        int[] iArr7 = new int[2];
        for (int i = 0; i < this.numSboxes * 3; i += 3) {
            for (int i2 = 0; i2 < 2; i2++) {
                int i3 = (2 + i2) * this.stateSizeWords * 32;
                iArr2[i2] = Utils.getBitFromWordArray(iArr, i3 + i + 2);
                iArr3[i2] = Utils.getBitFromWordArray(iArr, i3 + i + 1);
                iArr4[i2] = Utils.getBitFromWordArray(iArr, i3 + i);
            }
            mpc_AND_verify(iArr2, iArr3, iArr5, tape, view, view2);
            mpc_AND_verify(iArr3, iArr4, iArr6, tape, view, view2);
            mpc_AND_verify(iArr4, iArr2, iArr7, tape, view, view2);
            for (int i4 = 0; i4 < 2; i4++) {
                int i5 = (2 + i4) * this.stateSizeWords * 32;
                Utils.setBitInWordArray(iArr, i5 + i + 2, iArr2[i4] ^ iArr6[i4]);
                Utils.setBitInWordArray(iArr, i5 + i + 1, (iArr2[i4] ^ iArr3[i4]) ^ iArr7[i4]);
                Utils.setBitInWordArray(iArr, i5 + i, ((iArr2[i4] ^ iArr3[i4]) ^ iArr4[i4]) ^ iArr5[i4]);
            }
        }
    }

    void mpc_AND_verify(int[] iArr, int[] iArr2, int[] iArr3, Tape tape, View view, View view2) {
        byte bit = Utils.getBit(tape.tapes[0], tape.pos);
        byte bit2 = Utils.getBit(tape.tapes[1], tape.pos);
        int i = iArr[0];
        int i2 = iArr[1];
        int i3 = iArr2[0];
        iArr3[0] = ((((i & iArr2[1]) ^ (i2 & i3)) ^ (i & i3)) ^ bit) ^ bit2;
        Utils.setBit(view.communicatedBits, tape.pos, (byte) iArr3[0]);
        iArr3[1] = Utils.getBit(view2.communicatedBits, tape.pos);
        tape.pos++;
    }

    private void mpc_xor_constant_verify(int[] iArr, int[] iArr2, int i, int i2, int i3) {
        int i4;
        if (i3 == 0) {
            i4 = 2 * this.stateSizeWords;
        } else if (i3 != 2) {
            return;
        } else {
            i4 = 3 * this.stateSizeWords;
        }
        for (int i5 = 0; i5 < i2; i5++) {
            int i6 = i5 + i4;
            iArr[i6] = iArr[i6] ^ iArr2[i5 + i];
        }
    }

    private int deserializeSignature(Signature signature, byte[] bArr, int i, int i2) {
        int iCountNonZeroChallenges;
        Signature.Proof[] proofArr = signature.proofs;
        byte[] bArr2 = signature.challengeBits;
        int iNumBytes = Utils.numBytes(2 * this.numMPCRounds);
        if (i < iNumBytes || (iCountNonZeroChallenges = countNonZeroChallenges(bArr, i2)) < 0) {
            return -1;
        }
        int i3 = iNumBytes + 32 + (this.numMPCRounds * ((2 * this.seedSizeBytes) + this.andSizeBytes + this.digestSizeBytes)) + (iCountNonZeroChallenges * this.stateSizeBytes);
        if (this.transform == 1) {
            i3 = i3 + (this.UnruhGWithInputBytes * (this.numMPCRounds - iCountNonZeroChallenges)) + (this.UnruhGWithoutInputBytes * iCountNonZeroChallenges);
        }
        if (i != i3) {
            LOG.fine("sigBytesLen = " + i + ", expected bytesRequired = " + i3);
            return -1;
        }
        System.arraycopy(bArr, i2, bArr2, 0, iNumBytes);
        int i4 = i2 + iNumBytes;
        System.arraycopy(bArr, i4, signature.salt, 0, 32);
        int i5 = i4 + 32;
        for (int i6 = 0; i6 < this.numMPCRounds; i6++) {
            int challenge = getChallenge(bArr2, i6);
            System.arraycopy(bArr, i5, proofArr[i6].view3Commitment, 0, this.digestSizeBytes);
            int i7 = i5 + this.digestSizeBytes;
            if (this.transform == 1) {
                int i8 = challenge == 0 ? this.UnruhGWithInputBytes : this.UnruhGWithoutInputBytes;
                System.arraycopy(bArr, i7, proofArr[i6].view3UnruhG, 0, i8);
                i7 += i8;
            }
            System.arraycopy(bArr, i7, proofArr[i6].communicatedBits, 0, this.andSizeBytes);
            int i9 = i7 + this.andSizeBytes;
            System.arraycopy(bArr, i9, proofArr[i6].seed1, 0, this.seedSizeBytes);
            int i10 = i9 + this.seedSizeBytes;
            System.arraycopy(bArr, i10, proofArr[i6].seed2, 0, this.seedSizeBytes);
            i5 = i10 + this.seedSizeBytes;
            if (challenge == 1 || challenge == 2) {
                Pack.littleEndianToInt(bArr, i5, proofArr[i6].inputShare, 0, this.stateSizeBytes / 4);
                if (this.stateSizeBits == 129) {
                    proofArr[i6].inputShare[this.stateSizeWords - 1] = bArr[(i5 + this.stateSizeBytes) - 1] & 255;
                }
                i5 += this.stateSizeBytes;
                if (!arePaddingBitsZero(proofArr[i6].inputShare, this.stateSizeBits)) {
                    return -1;
                }
            }
        }
        return 0;
    }

    private int countNonZeroChallenges(byte[] bArr, int i) {
        int iBitCount = 0;
        int i2 = 0;
        int i3 = 0;
        while (i3 + 16 <= this.numMPCRounds) {
            int iLittleEndianToInt = Pack.littleEndianToInt(bArr, i + (i3 >>> 2));
            i2 |= iLittleEndianToInt & (iLittleEndianToInt >>> 1);
            iBitCount += Integers.bitCount((iLittleEndianToInt ^ (iLittleEndianToInt >>> 1)) & 1431655765);
            i3 += 16;
        }
        int i4 = (this.numMPCRounds - i3) * 2;
        if (i4 > 0) {
            int iLittleEndianToInt_Low = Pack.littleEndianToInt_Low(bArr, i + (i3 >>> 2), (i4 + 7) / 8) & Utils.getTrailingBitsMask(i4);
            i2 |= iLittleEndianToInt_Low & (iLittleEndianToInt_Low >>> 1);
            iBitCount += Integers.bitCount((iLittleEndianToInt_Low ^ (iLittleEndianToInt_Low >>> 1)) & 1431655765);
        }
        if ((i2 & 1431655765) == 0) {
            return iBitCount;
        }
        return -1;
    }

    private void picnic_read_public_key(int[] iArr, int[] iArr2, byte[] bArr) {
        int i = 1 + this.stateSizeBytes;
        int i2 = this.stateSizeBytes / 4;
        Pack.littleEndianToInt(bArr, 1, iArr, 0, i2);
        Pack.littleEndianToInt(bArr, i, iArr2, 0, i2);
        if (i2 < this.stateSizeWords) {
            int i3 = i2 * 4;
            int i4 = this.stateSizeBytes - i3;
            iArr[i2] = Pack.littleEndianToInt_Low(bArr, 1 + i3, i4);
            iArr2[i2] = Pack.littleEndianToInt_Low(bArr, i + i3, i4);
        }
    }

    private int verify_picnic3(Signature2 signature2, int[] iArr, int[] iArr2, byte[] bArr) {
        int iVerifyMerkleTree;
        byte[][][] bArr2 = new byte[this.numMPCRounds][this.numMPCParties][this.digestSizeBytes];
        byte[][] bArr3 = new byte[this.numMPCRounds][this.digestSizeBytes];
        byte[][] bArr4 = new byte[this.numMPCRounds][this.digestSizeBytes];
        Msg[] msgArr = new Msg[this.numMPCRounds];
        Tree tree = new Tree(this, this.numMPCRounds, this.digestSizeBytes);
        byte[] bArr5 = new byte[64];
        Tree[] treeArr = new Tree[this.numMPCRounds];
        Tape[] tapeArr = new Tape[this.numMPCRounds];
        Tree tree2 = new Tree(this, this.numMPCRounds, this.seedSizeBytes);
        if (tree2.reconstructSeeds(signature2.challengeC, this.numOpenedRounds, signature2.iSeedInfo, signature2.iSeedInfoLen, signature2.salt, 0) != 0) {
            return -1;
        }
        for (int i = 0; i < this.numMPCRounds; i++) {
            if (contains(signature2.challengeC, this.numOpenedRounds, i)) {
                treeArr[i] = new Tree(this, this.numMPCParties, this.seedSizeBytes);
                if (treeArr[i].reconstructSeeds(new int[]{signature2.challengeP[indexOf(signature2.challengeC, this.numOpenedRounds, i)]}, 1, signature2.proofs[i].seedInfo, signature2.proofs[i].seedInfoLen, signature2.salt, i) != 0) {
                    LOG.fine("Failed to reconstruct seeds for round " + i);
                    return -1;
                }
            } else {
                treeArr[i] = new Tree(this, this.numMPCParties, this.seedSizeBytes);
                treeArr[i].generateSeeds(tree2.getLeaf(i), signature2.salt, i);
            }
        }
        int i2 = this.numMPCParties - 1;
        byte[] bArr6 = new byte[176];
        for (int i3 = 0; i3 < this.numMPCRounds; i3++) {
            tapeArr[i3] = new Tape(this);
            createRandomTapes(tapeArr[i3], treeArr[i3].getLeaves(), treeArr[i3].getLeavesOffset(), signature2.salt, i3);
            if (contains(signature2.challengeC, this.numOpenedRounds, i3)) {
                int i4 = signature2.challengeP[indexOf(signature2.challengeC, this.numOpenedRounds, i3)];
                for (int i5 = 0; i5 < i2; i5++) {
                    if (i5 != i4) {
                        commit(bArr2[i3][i5], treeArr[i3].getLeaf(i5), null, signature2.salt, i3, i5);
                    }
                }
                if (i2 != i4) {
                    commit(bArr2[i3][i2], treeArr[i3].getLeaf(i2), signature2.proofs[i3].aux, signature2.salt, i3, i2);
                }
                System.arraycopy(signature2.proofs[i3].C, 0, bArr2[i3][i4], 0, this.digestSizeBytes);
            } else {
                tapeArr[i3].computeAuxTape(null);
                for (int i6 = 0; i6 < i2; i6++) {
                    commit(bArr2[i3][i6], treeArr[i3].getLeaf(i6), null, signature2.salt, i3, i6);
                }
                getAuxBits(bArr6, tapeArr[i3]);
                commit(bArr2[i3][i2], treeArr[i3].getLeaf(i2), bArr6, signature2.salt, i3, i2);
            }
        }
        for (int i7 = 0; i7 < this.numMPCRounds; i7++) {
            commit_h(bArr3[i7], bArr2[i7]);
        }
        int[] iArr3 = new int[this.stateSizeBits];
        for (int i8 = 0; i8 < this.numMPCRounds; i8++) {
            msgArr[i8] = new Msg(this);
            if (contains(signature2.challengeC, this.numOpenedRounds, i8)) {
                int i9 = signature2.challengeP[indexOf(signature2.challengeC, this.numOpenedRounds, i8)];
                if (i9 != i2) {
                    tapeArr[i8].setAuxBits(signature2.proofs[i8].aux);
                }
                System.arraycopy(signature2.proofs[i8].msgs, 0, msgArr[i8].msgs[i9], 0, this.andSizeBytes);
                Arrays.fill(tapeArr[i8].tapes[i9], (byte) 0);
                msgArr[i8].unopened = i9;
                byte[] bArr7 = new byte[this.stateSizeWords * 4];
                System.arraycopy(signature2.proofs[i8].input, 0, bArr7, 0, signature2.proofs[i8].input.length);
                int[] iArr4 = new int[this.stateSizeWords];
                Pack.littleEndianToInt(bArr7, 0, iArr4, 0, this.stateSizeWords);
                if (simulateOnline(iArr4, tapeArr[i8], iArr3, msgArr[i8], iArr2, iArr) != 0) {
                    LOG.fine("MPC simulation failed for round " + i8 + ", signature invalid");
                    return -1;
                }
                commit_v(bArr4[i8], signature2.proofs[i8].input, msgArr[i8]);
            } else {
                bArr4[i8] = null;
            }
        }
        if (tree.addMerkleNodes(getMissingLeavesList(signature2.challengeC), this.numMPCRounds - this.numOpenedRounds, signature2.cvInfo, signature2.cvInfoLen) != 0 || (iVerifyMerkleTree = tree.verifyMerkleTree(bArr4, signature2.salt)) != 0) {
            return -1;
        }
        HCP(bArr5, null, null, bArr3, tree.nodes[0], signature2.salt, iArr, iArr2, bArr);
        if (subarrayEquals(signature2.challengeHash, bArr5, this.digestSizeBytes)) {
            return iVerifyMerkleTree;
        }
        LOG.fine("Challenge does not match, signature invalid");
        return -1;
    }

    private int deserializeSignature2(Signature2 signature2, byte[] bArr, int i, int i2) {
        int i3 = this.digestSizeBytes + 32;
        if (bArr.length < i3) {
            return -1;
        }
        System.arraycopy(bArr, i2, signature2.challengeHash, 0, this.digestSizeBytes);
        int i4 = i2 + this.digestSizeBytes;
        System.arraycopy(bArr, i4, signature2.salt, 0, 32);
        int i5 = i4 + 32;
        expandChallengeHash(signature2.challengeHash, signature2.challengeC, signature2.challengeP);
        signature2.iSeedInfoLen = new Tree(this, this.numMPCRounds, this.seedSizeBytes).revealSeedsSize(signature2.challengeC, this.numOpenedRounds);
        int i6 = i3 + signature2.iSeedInfoLen;
        signature2.cvInfoLen = new Tree(this, this.numMPCRounds, this.digestSizeBytes).openMerkleTreeSize(getMissingLeavesList(signature2.challengeC), this.numMPCRounds - this.numOpenedRounds);
        int i7 = i6 + signature2.cvInfoLen;
        int iRevealSeedsSize = new Tree(this, this.numMPCParties, this.seedSizeBytes).revealSeedsSize(new int[1], 1);
        for (int i8 = 0; i8 < this.numMPCRounds; i8++) {
            if (contains(signature2.challengeC, this.numOpenedRounds, i8)) {
                if (signature2.challengeP[indexOf(signature2.challengeC, this.numOpenedRounds, i8)] != this.numMPCParties - 1) {
                    i7 += this.andSizeBytes;
                }
                i7 = i7 + iRevealSeedsSize + this.stateSizeBytes + this.andSizeBytes + this.digestSizeBytes;
            }
        }
        if (i != i7) {
            LOG.fine("sigLen = " + i + ", expected bytesRequired = " + i7);
            return -1;
        }
        signature2.iSeedInfo = new byte[signature2.iSeedInfoLen];
        System.arraycopy(bArr, i5, signature2.iSeedInfo, 0, signature2.iSeedInfoLen);
        int i9 = i5 + signature2.iSeedInfoLen;
        signature2.cvInfo = new byte[signature2.cvInfoLen];
        System.arraycopy(bArr, i9, signature2.cvInfo, 0, signature2.cvInfoLen);
        int i10 = i9 + signature2.cvInfoLen;
        for (int i11 = 0; i11 < this.numMPCRounds; i11++) {
            if (contains(signature2.challengeC, this.numOpenedRounds, i11)) {
                signature2.proofs[i11] = new Signature2.Proof2(this);
                signature2.proofs[i11].seedInfoLen = iRevealSeedsSize;
                signature2.proofs[i11].seedInfo = new byte[signature2.proofs[i11].seedInfoLen];
                System.arraycopy(bArr, i10, signature2.proofs[i11].seedInfo, 0, signature2.proofs[i11].seedInfoLen);
                int i12 = i10 + signature2.proofs[i11].seedInfoLen;
                if (signature2.challengeP[indexOf(signature2.challengeC, this.numOpenedRounds, i11)] != this.numMPCParties - 1) {
                    System.arraycopy(bArr, i12, signature2.proofs[i11].aux, 0, this.andSizeBytes);
                    i12 += this.andSizeBytes;
                    if (!arePaddingBitsZero(signature2.proofs[i11].aux, 3 * this.numRounds * this.numSboxes)) {
                        LOG.fine("failed while deserializing aux bits");
                        return -1;
                    }
                }
                System.arraycopy(bArr, i12, signature2.proofs[i11].input, 0, this.stateSizeBytes);
                int i13 = i12 + this.stateSizeBytes;
                int i14 = this.andSizeBytes;
                System.arraycopy(bArr, i13, signature2.proofs[i11].msgs, 0, i14);
                int i15 = i13 + i14;
                if (!arePaddingBitsZero(signature2.proofs[i11].msgs, 3 * this.numRounds * this.numSboxes)) {
                    LOG.fine("failed while deserializing msgs bits");
                    return -1;
                }
                System.arraycopy(bArr, i15, signature2.proofs[i11].C, 0, this.digestSizeBytes);
                i10 = i15 + this.digestSizeBytes;
            }
        }
        return 0;
    }

    private boolean arePaddingBitsZero(byte[] bArr, int i) {
        int iNumBytes = Utils.numBytes(i);
        for (int i2 = i; i2 < iNumBytes * 8; i2++) {
            if (Utils.getBit(bArr, i2) != 0) {
                return false;
            }
        }
        return true;
    }

    private boolean arePaddingBitsZero(int[] iArr, int i) {
        if ((i & 31) == 0) {
            return true;
        }
        return (iArr[i >>> 5] & (Utils.getTrailingBitsMask(i) ^ (-1))) == 0;
    }

    public void crypto_sign(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        if (picnic_sign(bArr3, bArr2, bArr)) {
            System.arraycopy(bArr2, 0, bArr, 4, bArr2.length);
        }
    }

    private boolean picnic_sign(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        int[] iArr = new int[this.stateSizeWords];
        int[] iArr2 = new int[this.stateSizeWords];
        int[] iArr3 = new int[this.stateSizeWords];
        int i = 1 + this.stateSizeBytes;
        int i2 = 1 + (2 * this.stateSizeBytes);
        int i3 = this.stateSizeBytes / 4;
        Pack.littleEndianToInt(bArr, 1, iArr, 0, i3);
        Pack.littleEndianToInt(bArr, i, iArr2, 0, i3);
        Pack.littleEndianToInt(bArr, i2, iArr3, 0, i3);
        if (i3 < this.stateSizeWords) {
            int i4 = i3 * 4;
            int i5 = this.stateSizeBytes - i4;
            iArr[i3] = Pack.littleEndianToInt_Low(bArr, 1 + i4, i5);
            iArr2[i3] = Pack.littleEndianToInt_Low(bArr, i + i4, i5);
            iArr3[i3] = Pack.littleEndianToInt_Low(bArr, i2 + i4, i5);
        }
        if (is_picnic3(this.parameters)) {
            Signature2 signature2 = new Signature2(this);
            if (!sign_picnic3(iArr, iArr2, iArr3, bArr2, signature2)) {
                LOG.fine("Failed to create signature");
                return false;
            }
            int iSerializeSignature2 = serializeSignature2(signature2, bArr3, bArr2.length + 4);
            if (iSerializeSignature2 < 0) {
                LOG.fine("Failed to serialize signature");
                return false;
            }
            this.signatureLength = iSerializeSignature2;
            Pack.intToLittleEndian(iSerializeSignature2, bArr3, 0);
            return true;
        }
        Signature signature = new Signature(this);
        if (sign_picnic1(iArr, iArr2, iArr3, bArr2, signature) != 0) {
            LOG.fine("Failed to create signature");
            return false;
        }
        int iSerializeSignature = serializeSignature(signature, bArr3, bArr2.length + 4);
        if (iSerializeSignature < 0) {
            LOG.fine("Failed to serialize signature");
            return false;
        }
        this.signatureLength = iSerializeSignature;
        Pack.intToLittleEndian(iSerializeSignature, bArr3, 0);
        return true;
    }

    int serializeSignature(Signature signature, byte[] bArr, int i) {
        Signature.Proof[] proofArr = signature.proofs;
        byte[] bArr2 = signature.challengeBits;
        int iNumBytes = Utils.numBytes(2 * this.numMPCRounds) + 32 + (this.numMPCRounds * ((2 * this.seedSizeBytes) + this.stateSizeBytes + this.andSizeBytes + this.digestSizeBytes));
        if (this.transform == 1) {
            iNumBytes += this.UnruhGWithoutInputBytes * this.numMPCRounds;
        }
        if (this.CRYPTO_BYTES < iNumBytes) {
            return -1;
        }
        System.arraycopy(bArr2, 0, bArr, i, Utils.numBytes(2 * this.numMPCRounds));
        int iNumBytes2 = i + Utils.numBytes(2 * this.numMPCRounds);
        System.arraycopy(signature.salt, 0, bArr, iNumBytes2, 32);
        int i2 = iNumBytes2 + 32;
        for (int i3 = 0; i3 < this.numMPCRounds; i3++) {
            int challenge = getChallenge(bArr2, i3);
            System.arraycopy(proofArr[i3].view3Commitment, 0, bArr, i2, this.digestSizeBytes);
            int i4 = i2 + this.digestSizeBytes;
            if (this.transform == 1) {
                int i5 = challenge == 0 ? this.UnruhGWithInputBytes : this.UnruhGWithoutInputBytes;
                System.arraycopy(proofArr[i3].view3UnruhG, 0, bArr, i4, i5);
                i4 += i5;
            }
            System.arraycopy(proofArr[i3].communicatedBits, 0, bArr, i4, this.andSizeBytes);
            int i6 = i4 + this.andSizeBytes;
            System.arraycopy(proofArr[i3].seed1, 0, bArr, i6, this.seedSizeBytes);
            int i7 = i6 + this.seedSizeBytes;
            System.arraycopy(proofArr[i3].seed2, 0, bArr, i7, this.seedSizeBytes);
            i2 = i7 + this.seedSizeBytes;
            if (challenge == 1 || challenge == 2) {
                Pack.intToLittleEndian(proofArr[i3].inputShare, 0, this.stateSizeWords, bArr, i2);
                i2 += this.stateSizeBytes;
            }
        }
        return i2 - i;
    }

    int getChallenge(byte[] bArr, int i) {
        return Utils.getCrumbAligned(bArr, i);
    }

    private int serializeSignature2(Signature2 signature2, byte[] bArr, int i) {
        int i2 = this.digestSizeBytes + 32 + signature2.iSeedInfoLen + signature2.cvInfoLen;
        for (int i3 = 0; i3 < this.numMPCRounds; i3++) {
            if (contains(signature2.challengeC, this.numOpenedRounds, i3)) {
                int i4 = signature2.challengeP[indexOf(signature2.challengeC, this.numOpenedRounds, i3)];
                int i5 = i2 + signature2.proofs[i3].seedInfoLen;
                if (i4 != this.numMPCParties - 1) {
                    i5 += this.andSizeBytes;
                }
                i2 = i5 + this.stateSizeBytes + this.andSizeBytes + this.digestSizeBytes;
            }
        }
        if (bArr.length < i2) {
            return -1;
        }
        System.arraycopy(signature2.challengeHash, 0, bArr, i, this.digestSizeBytes);
        int i6 = i + this.digestSizeBytes;
        System.arraycopy(signature2.salt, 0, bArr, i6, 32);
        int i7 = i6 + 32;
        System.arraycopy(signature2.iSeedInfo, 0, bArr, i7, signature2.iSeedInfoLen);
        int i8 = i7 + signature2.iSeedInfoLen;
        System.arraycopy(signature2.cvInfo, 0, bArr, i8, signature2.cvInfoLen);
        int i9 = i8 + signature2.cvInfoLen;
        for (int i10 = 0; i10 < this.numMPCRounds; i10++) {
            if (contains(signature2.challengeC, this.numOpenedRounds, i10)) {
                System.arraycopy(signature2.proofs[i10].seedInfo, 0, bArr, i9, signature2.proofs[i10].seedInfoLen);
                int i11 = i9 + signature2.proofs[i10].seedInfoLen;
                if (signature2.challengeP[indexOf(signature2.challengeC, this.numOpenedRounds, i10)] != this.numMPCParties - 1) {
                    System.arraycopy(signature2.proofs[i10].aux, 0, bArr, i11, this.andSizeBytes);
                    i11 += this.andSizeBytes;
                }
                System.arraycopy(signature2.proofs[i10].input, 0, bArr, i11, this.stateSizeBytes);
                int i12 = i11 + this.stateSizeBytes;
                System.arraycopy(signature2.proofs[i10].msgs, 0, bArr, i12, this.andSizeBytes);
                int i13 = i12 + this.andSizeBytes;
                System.arraycopy(signature2.proofs[i10].C, 0, bArr, i13, this.digestSizeBytes);
                i9 = i13 + this.digestSizeBytes;
            }
        }
        return i9 - i;
    }

    private int sign_picnic1(int[] iArr, int[] iArr2, int[] iArr3, byte[] bArr, Signature signature) {
        View[][] viewArr = new View[this.numMPCRounds][3];
        byte[][][] bArr2 = new byte[this.numMPCRounds][this.numMPCParties][this.digestSizeBytes];
        byte[][][] bArr3 = new byte[this.numMPCRounds][3][this.UnruhGWithInputBytes];
        byte[] bArrComputeSeeds = computeSeeds(iArr, iArr2, iArr3, bArr);
        int i = this.numMPCParties * this.seedSizeBytes;
        System.arraycopy(bArrComputeSeeds, i * this.numMPCRounds, signature.salt, 0, 32);
        Tape tape = new Tape(this);
        byte[] bArr4 = new byte[Math.max(9 * this.stateSizeBytes, this.stateSizeBytes + this.andSizeBytes)];
        for (int i2 = 0; i2 < this.numMPCRounds; i2++) {
            viewArr[i2][0] = new View(this);
            viewArr[i2][1] = new View(this);
            viewArr[i2][2] = new View(this);
            for (int i3 = 0; i3 < 2; i3++) {
                if (!createRandomTape(bArrComputeSeeds, (i * i2) + (i3 * this.seedSizeBytes), signature.salt, i2, i3, bArr4, this.stateSizeBytes + this.andSizeBytes)) {
                    LOG.fine("createRandomTape failed");
                    return -1;
                }
                int[] iArr4 = viewArr[i2][i3].inputShare;
                Pack.littleEndianToInt(bArr4, 0, iArr4);
                Utils.zeroTrailingBits(iArr4, this.stateSizeBits);
                System.arraycopy(bArr4, this.stateSizeBytes, tape.tapes[i3], 0, this.andSizeBytes);
            }
            if (!createRandomTape(bArrComputeSeeds, (i * i2) + (2 * this.seedSizeBytes), signature.salt, i2, 2, tape.tapes[2], this.andSizeBytes)) {
                LOG.fine("createRandomTape failed");
                return -1;
            }
            xor_three(viewArr[i2][2].inputShare, iArr, viewArr[i2][0].inputShare, viewArr[i2][1].inputShare);
            tape.pos = 0;
            int[] iArrLittleEndianToInt = Pack.littleEndianToInt(bArr4, 0, bArr4.length / 4);
            mpc_LowMC(tape, viewArr[i2], iArr3, iArrLittleEndianToInt);
            Pack.intToLittleEndian(iArrLittleEndianToInt, bArr4, 0);
            int[] iArr5 = new int[16];
            xor_three(iArr5, viewArr[i2][0].outputShare, viewArr[i2][1].outputShare, viewArr[i2][2].outputShare);
            if (!subarrayEquals(iArr5, iArr2, this.stateSizeWords)) {
                LOG.fine("Simulation failed; output does not match public key (round = " + i2 + ")");
                return -1;
            }
            Commit(bArrComputeSeeds, (i * i2) + (0 * this.seedSizeBytes), viewArr[i2][0], bArr2[i2][0]);
            Commit(bArrComputeSeeds, (i * i2) + (1 * this.seedSizeBytes), viewArr[i2][1], bArr2[i2][1]);
            Commit(bArrComputeSeeds, (i * i2) + (2 * this.seedSizeBytes), viewArr[i2][2], bArr2[i2][2]);
            if (this.transform == 1) {
                G(0, bArrComputeSeeds, (i * i2) + (0 * this.seedSizeBytes), viewArr[i2][0], bArr3[i2][0]);
                G(1, bArrComputeSeeds, (i * i2) + (1 * this.seedSizeBytes), viewArr[i2][1], bArr3[i2][1]);
                G(2, bArrComputeSeeds, (i * i2) + (2 * this.seedSizeBytes), viewArr[i2][2], bArr3[i2][2]);
            }
        }
        H3(iArr2, iArr3, viewArr, bArr2, signature.challengeBits, signature.salt, bArr, bArr3);
        for (int i4 = 0; i4 < this.numMPCRounds; i4++) {
            prove(signature.proofs[i4], getChallenge(signature.challengeBits, i4), bArrComputeSeeds, i * i4, viewArr[i4], bArr2[i4], this.transform != 1 ? null : bArr3[i4]);
        }
        return 0;
    }

    void prove(Signature.Proof proof, int i, byte[] bArr, int i2, View[] viewArr, byte[][] bArr2, byte[][] bArr3) {
        if (i == 0) {
            System.arraycopy(bArr, i2 + (0 * this.seedSizeBytes), proof.seed1, 0, this.seedSizeBytes);
            System.arraycopy(bArr, i2 + (1 * this.seedSizeBytes), proof.seed2, 0, this.seedSizeBytes);
        } else if (i == 1) {
            System.arraycopy(bArr, i2 + (1 * this.seedSizeBytes), proof.seed1, 0, this.seedSizeBytes);
            System.arraycopy(bArr, i2 + (2 * this.seedSizeBytes), proof.seed2, 0, this.seedSizeBytes);
        } else {
            if (i != 2) {
                LOG.fine("Invalid challenge");
                throw new IllegalArgumentException("challenge");
            }
            System.arraycopy(bArr, i2 + (2 * this.seedSizeBytes), proof.seed1, 0, this.seedSizeBytes);
            System.arraycopy(bArr, i2 + (0 * this.seedSizeBytes), proof.seed2, 0, this.seedSizeBytes);
        }
        if (i == 1 || i == 2) {
            System.arraycopy(viewArr[2].inputShare, 0, proof.inputShare, 0, this.stateSizeWords);
        }
        System.arraycopy(viewArr[(i + 1) % 3].communicatedBits, 0, proof.communicatedBits, 0, this.andSizeBytes);
        System.arraycopy(bArr2[(i + 2) % 3], 0, proof.view3Commitment, 0, this.digestSizeBytes);
        if (this.transform == 1) {
            System.arraycopy(bArr3[(i + 2) % 3], 0, proof.view3UnruhG, 0, i == 0 ? this.UnruhGWithInputBytes : this.UnruhGWithoutInputBytes);
        }
    }

    private void H3(int[] iArr, int[] iArr2, View[][] viewArr, byte[][][] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[][][] bArr5) {
        this.digest.update((byte) 1);
        byte[] bArr6 = new byte[this.stateSizeWords * 4];
        for (int i = 0; i < this.numMPCRounds; i++) {
            for (int i2 = 0; i2 < 3; i2++) {
                Pack.intToLittleEndian(viewArr[i][i2].outputShare, bArr6, 0);
                this.digest.update(bArr6, 0, this.stateSizeBytes);
            }
        }
        implH3(iArr, iArr2, bArr, bArr2, bArr3, bArr4, bArr5);
    }

    private void H3(int[] iArr, int[] iArr2, int[][][] iArr3, byte[][][] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[][][] bArr5) {
        this.digest.update((byte) 1);
        byte[] bArr6 = new byte[this.stateSizeWords * 4];
        for (int i = 0; i < this.numMPCRounds; i++) {
            for (int i2 = 0; i2 < 3; i2++) {
                Pack.intToLittleEndian(iArr3[i][i2], bArr6, 0);
                this.digest.update(bArr6, 0, this.stateSizeBytes);
            }
        }
        implH3(iArr, iArr2, bArr, bArr2, bArr3, bArr4, bArr5);
    }

    private void implH3(int[] iArr, int[] iArr2, byte[][][] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[][][] bArr5) {
        byte[] bArr6 = new byte[this.digestSizeBytes];
        bArr2[Utils.numBytes(this.numMPCRounds * 2) - 1] = 0;
        for (int i = 0; i < this.numMPCRounds; i++) {
            for (int i2 = 0; i2 < 3; i2++) {
                this.digest.update(bArr[i][i2], 0, this.digestSizeBytes);
            }
        }
        if (this.transform == 1) {
            for (int i3 = 0; i3 < this.numMPCRounds; i3++) {
                int i4 = 0;
                while (i4 < 3) {
                    this.digest.update(bArr5[i3][i4], 0, i4 == 2 ? this.UnruhGWithInputBytes : this.UnruhGWithoutInputBytes);
                    i4++;
                }
            }
        }
        this.digest.update(Pack.intToLittleEndian(iArr), 0, this.stateSizeBytes);
        this.digest.update(Pack.intToLittleEndian(iArr2), 0, this.stateSizeBytes);
        this.digest.update(bArr3, 0, 32);
        this.digest.update(bArr4, 0, bArr4.length);
        this.digest.doFinal(bArr6, 0, this.digestSizeBytes);
        int i5 = 0;
        boolean z = true;
        while (z) {
            for (int i6 = 0; i6 < this.digestSizeBytes; i6++) {
                byte b = bArr6[i6];
                for (int i7 = 0; i7 < 8; i7 += 2) {
                    int i8 = (b >>> (6 - i7)) & 3;
                    if (i8 < 3) {
                        setChallenge(bArr2, i5, i8);
                        i5++;
                        if (i5 == this.numMPCRounds) {
                            z = false;
                            break;
                        }
                    }
                }
                if (!z) {
                    break;
                }
            }
            if (!z) {
                return;
            }
            this.digest.update((byte) 1);
            this.digest.update(bArr6, 0, this.digestSizeBytes);
            this.digest.doFinal(bArr6, 0, this.digestSizeBytes);
        }
    }

    private void setChallenge(byte[] bArr, int i, int i2) {
        Utils.setBit(bArr, 2 * i, (byte) (i2 & 1));
        Utils.setBit(bArr, (2 * i) + 1, (byte) ((i2 >>> 1) & 1));
    }

    private void G(int i, byte[] bArr, int i2, View view, byte[] bArr2) {
        int i3 = this.seedSizeBytes + this.andSizeBytes;
        this.digest.update((byte) 5);
        this.digest.update(bArr, i2, this.seedSizeBytes);
        this.digest.doFinal(bArr2, 0, this.digestSizeBytes);
        this.digest.update(bArr2, 0, this.digestSizeBytes);
        if (i == 2) {
            this.digest.update(Pack.intToLittleEndian(view.inputShare), 0, this.stateSizeBytes);
            i3 += this.stateSizeBytes;
        }
        this.digest.update(view.communicatedBits, 0, this.andSizeBytes);
        this.digest.update(Pack.intToLittleEndian(i3), 0, 2);
        this.digest.doFinal(bArr2, 0, i3);
    }

    private void mpc_LowMC(Tape tape, View[] viewArr, int[] iArr, int[] iArr2) {
        Arrays.fill(iArr2, 0, iArr2.length, 0);
        mpc_xor_constant(iArr2, 3 * this.stateSizeWords, iArr, 0, this.stateSizeWords);
        KMatricesWithPointer kMatricesWithPointerKMatrix = this.lowmcConstants.KMatrix(this, 0);
        for (int i = 0; i < 3; i++) {
            matrix_mul_offset(iArr2, i * this.stateSizeWords, viewArr[i].inputShare, 0, kMatricesWithPointerKMatrix.getData(), kMatricesWithPointerKMatrix.getMatrixPointer());
        }
        mpc_xor(iArr2, iArr2, 3);
        for (int i2 = 1; i2 <= this.numRounds; i2++) {
            KMatricesWithPointer kMatricesWithPointerKMatrix2 = this.lowmcConstants.KMatrix(this, i2);
            for (int i3 = 0; i3 < 3; i3++) {
                matrix_mul_offset(iArr2, i3 * this.stateSizeWords, viewArr[i3].inputShare, 0, kMatricesWithPointerKMatrix2.getData(), kMatricesWithPointerKMatrix2.getMatrixPointer());
            }
            mpc_substitution(iArr2, tape, viewArr);
            KMatricesWithPointer kMatricesWithPointerLMatrix = this.lowmcConstants.LMatrix(this, i2 - 1);
            mpc_matrix_mul(iArr2, 3 * this.stateSizeWords, iArr2, 3 * this.stateSizeWords, kMatricesWithPointerLMatrix.getData(), kMatricesWithPointerLMatrix.getMatrixPointer(), 3);
            KMatricesWithPointer kMatricesWithPointerRConstant = this.lowmcConstants.RConstant(this, i2 - 1);
            mpc_xor_constant(iArr2, 3 * this.stateSizeWords, kMatricesWithPointerRConstant.getData(), kMatricesWithPointerRConstant.getMatrixPointer(), this.stateSizeWords);
            mpc_xor(iArr2, iArr2, 3);
        }
        for (int i4 = 0; i4 < 3; i4++) {
            System.arraycopy(iArr2, (3 + i4) * this.stateSizeWords, viewArr[i4].outputShare, 0, this.stateSizeWords);
        }
    }

    private void Commit(byte[] bArr, int i, View view, byte[] bArr2) {
        this.digest.update((byte) 4);
        this.digest.update(bArr, i, this.seedSizeBytes);
        this.digest.doFinal(bArr2, 0, this.digestSizeBytes);
        this.digest.update((byte) 0);
        this.digest.update(bArr2, 0, this.digestSizeBytes);
        this.digest.update(Pack.intToLittleEndian(view.inputShare), 0, this.stateSizeBytes);
        this.digest.update(view.communicatedBits, 0, this.andSizeBytes);
        this.digest.update(Pack.intToLittleEndian(view.outputShare), 0, this.stateSizeBytes);
        this.digest.doFinal(bArr2, 0, this.digestSizeBytes);
    }

    private void mpc_substitution(int[] iArr, Tape tape, View[] viewArr) {
        int[] iArr2 = new int[3];
        int[] iArr3 = new int[3];
        int[] iArr4 = new int[3];
        int[] iArr5 = new int[3];
        int[] iArr6 = new int[3];
        int[] iArr7 = new int[3];
        for (int i = 0; i < this.numSboxes * 3; i += 3) {
            for (int i2 = 0; i2 < 3; i2++) {
                int i3 = (3 + i2) * this.stateSizeWords * 32;
                iArr2[i2] = Utils.getBitFromWordArray(iArr, i3 + i + 2);
                iArr3[i2] = Utils.getBitFromWordArray(iArr, i3 + i + 1);
                iArr4[i2] = Utils.getBitFromWordArray(iArr, i3 + i);
            }
            mpc_AND(iArr2, iArr3, iArr5, tape, viewArr);
            mpc_AND(iArr3, iArr4, iArr6, tape, viewArr);
            mpc_AND(iArr4, iArr2, iArr7, tape, viewArr);
            for (int i4 = 0; i4 < 3; i4++) {
                int i5 = (3 + i4) * this.stateSizeWords * 32;
                Utils.setBitInWordArray(iArr, i5 + i + 2, iArr2[i4] ^ iArr6[i4]);
                Utils.setBitInWordArray(iArr, i5 + i + 1, (iArr2[i4] ^ iArr3[i4]) ^ iArr7[i4]);
                Utils.setBitInWordArray(iArr, i5 + i, ((iArr2[i4] ^ iArr3[i4]) ^ iArr4[i4]) ^ iArr5[i4]);
            }
        }
    }

    private void mpc_AND(int[] iArr, int[] iArr2, int[] iArr3, Tape tape, View[] viewArr) {
        byte bit = Utils.getBit(tape.tapes[0], tape.pos);
        byte bit2 = Utils.getBit(tape.tapes[1], tape.pos);
        byte bit3 = Utils.getBit(tape.tapes[2], tape.pos);
        iArr3[0] = ((((iArr[0] & iArr2[1]) ^ (iArr[1] & iArr2[0])) ^ (iArr[0] & iArr2[0])) ^ bit) ^ bit2;
        iArr3[1] = ((((iArr[1] & iArr2[2]) ^ (iArr[2] & iArr2[1])) ^ (iArr[1] & iArr2[1])) ^ bit2) ^ bit3;
        iArr3[2] = ((((iArr[2] & iArr2[0]) ^ (iArr[0] & iArr2[2])) ^ (iArr[2] & iArr2[2])) ^ bit3) ^ bit;
        Utils.setBit(viewArr[0].communicatedBits, tape.pos, (byte) iArr3[0]);
        Utils.setBit(viewArr[1].communicatedBits, tape.pos, (byte) iArr3[1]);
        Utils.setBit(viewArr[2].communicatedBits, tape.pos, (byte) iArr3[2]);
        tape.pos++;
    }

    private void mpc_xor(int[] iArr, int[] iArr2, int i) {
        int i2 = this.stateSizeWords * i;
        for (int i3 = 0; i3 < i2; i3++) {
            int i4 = (i * this.stateSizeWords) + i3;
            iArr[i4] = iArr[i4] ^ iArr2[i3];
        }
    }

    private void mpc_matrix_mul(int[] iArr, int i, int[] iArr2, int i2, int[] iArr3, int i3, int i4) {
        for (int i5 = 0; i5 < i4; i5++) {
            matrix_mul_offset(iArr, i + (i5 * this.stateSizeWords), iArr2, i2 + (i5 * this.stateSizeWords), iArr3, i3);
        }
    }

    private void mpc_xor_constant(int[] iArr, int i, int[] iArr2, int i2, int i3) {
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = i4 + i;
            iArr[i5] = iArr[i5] ^ iArr2[i4 + i2];
        }
    }

    private boolean createRandomTape(byte[] bArr, int i, byte[] bArr2, int i2, int i3, byte[] bArr3, int i4) {
        if (i4 < this.digestSizeBytes) {
            return false;
        }
        this.digest.update((byte) 2);
        this.digest.update(bArr, i, this.seedSizeBytes);
        this.digest.doFinal(bArr3, 0, this.digestSizeBytes);
        this.digest.update(bArr3, 0, this.digestSizeBytes);
        this.digest.update(bArr2, 0, 32);
        this.digest.update(Pack.intToLittleEndian(i2), 0, 2);
        this.digest.update(Pack.intToLittleEndian(i3), 0, 2);
        this.digest.update(Pack.intToLittleEndian(i4), 0, 2);
        this.digest.doFinal(bArr3, 0, i4);
        return true;
    }

    private byte[] computeSeeds(int[] iArr, int[] iArr2, int[] iArr3, byte[] bArr) {
        byte[] bArr2 = new byte[(this.seedSizeBytes * this.numMPCParties * this.numMPCRounds) + 32];
        byte[] bArr3 = new byte[32];
        updateDigest(iArr, bArr3);
        this.digest.update(bArr, 0, bArr.length);
        updateDigest(iArr2, bArr3);
        updateDigest(iArr3, bArr3);
        this.digest.update(Pack.intToLittleEndian(this.stateSizeBits), 0, 2);
        this.digest.doFinal(bArr2, 0, (this.seedSizeBytes * this.numMPCParties * this.numMPCRounds) + 32);
        return bArr2;
    }

    private boolean sign_picnic3(int[] iArr, int[] iArr2, int[] iArr3, byte[] bArr, Signature2 signature2) {
        byte[] bArr2 = new byte[32 + this.seedSizeBytes];
        computeSaltAndRootSeed(bArr2, iArr, iArr2, iArr3, bArr);
        byte[] bArrCopyOfRange = Arrays.copyOfRange(bArr2, 32, bArr2.length);
        signature2.salt = Arrays.copyOfRange(bArr2, 0, 32);
        Tree tree = new Tree(this, this.numMPCRounds, this.seedSizeBytes);
        tree.generateSeeds(bArrCopyOfRange, signature2.salt, 0);
        byte[][] leaves = tree.getLeaves();
        int leavesOffset = tree.getLeavesOffset();
        Tape[] tapeArr = new Tape[this.numMPCRounds];
        Tree[] treeArr = new Tree[this.numMPCRounds];
        for (int i = 0; i < this.numMPCRounds; i++) {
            tapeArr[i] = new Tape(this);
            treeArr[i] = new Tree(this, this.numMPCParties, this.seedSizeBytes);
            treeArr[i].generateSeeds(leaves[i + leavesOffset], signature2.salt, i);
            createRandomTapes(tapeArr[i], treeArr[i].getLeaves(), treeArr[i].getLeavesOffset(), signature2.salt, i);
        }
        byte[][] bArr3 = new byte[this.numMPCRounds][this.stateSizeWords * 4];
        byte[] bArr4 = new byte[176];
        for (int i2 = 0; i2 < this.numMPCRounds; i2++) {
            tapeArr[i2].computeAuxTape(bArr3[i2]);
        }
        byte[][][] bArr5 = new byte[this.numMPCRounds][this.numMPCParties][this.digestSizeBytes];
        for (int i3 = 0; i3 < this.numMPCRounds; i3++) {
            for (int i4 = 0; i4 < this.numMPCParties - 1; i4++) {
                commit(bArr5[i3][i4], treeArr[i3].getLeaf(i4), null, signature2.salt, i3, i4);
            }
            int i5 = this.numMPCParties - 1;
            getAuxBits(bArr4, tapeArr[i3]);
            commit(bArr5[i3][i5], treeArr[i3].getLeaf(i5), bArr4, signature2.salt, i3, i5);
        }
        Msg[] msgArr = new Msg[this.numMPCRounds];
        int[] iArr4 = new int[this.stateSizeBits];
        for (int i6 = 0; i6 < this.numMPCRounds; i6++) {
            msgArr[i6] = new Msg(this);
            int[] iArrLittleEndianToInt = Pack.littleEndianToInt(bArr3[i6], 0, this.stateSizeWords);
            xor_array(iArrLittleEndianToInt, iArrLittleEndianToInt, iArr, 0);
            if (simulateOnline(iArrLittleEndianToInt, tapeArr[i6], iArr4, msgArr[i6], iArr3, iArr2) != 0) {
                LOG.fine("MPC simulation failed, aborting signature");
                return false;
            }
            Pack.intToLittleEndian(iArrLittleEndianToInt, bArr3[i6], 0);
        }
        byte[][] bArr6 = new byte[this.numMPCRounds][this.digestSizeBytes];
        byte[][] bArr7 = new byte[this.numMPCRounds][this.digestSizeBytes];
        for (int i7 = 0; i7 < this.numMPCRounds; i7++) {
            commit_h(bArr6[i7], bArr5[i7]);
            commit_v(bArr7[i7], bArr3[i7], msgArr[i7]);
        }
        Tree tree2 = new Tree(this, this.numMPCRounds, this.digestSizeBytes);
        tree2.buildMerkleTree(bArr7, signature2.salt);
        signature2.challengeC = new int[this.numOpenedRounds];
        signature2.challengeP = new int[this.numOpenedRounds];
        signature2.challengeHash = new byte[this.digestSizeBytes];
        HCP(signature2.challengeHash, signature2.challengeC, signature2.challengeP, bArr6, tree2.nodes[0], signature2.salt, iArr2, iArr3, bArr);
        int[] iArr5 = new int[1];
        signature2.cvInfo = tree2.openMerkleTree(getMissingLeavesList(signature2.challengeC), this.numMPCRounds - this.numOpenedRounds, iArr5);
        signature2.cvInfoLen = iArr5[0];
        signature2.iSeedInfo = new byte[this.numMPCRounds * this.seedSizeBytes];
        signature2.iSeedInfoLen = tree.revealSeeds(signature2.challengeC, this.numOpenedRounds, signature2.iSeedInfo, this.numMPCRounds * this.seedSizeBytes);
        signature2.proofs = new Signature2.Proof2[this.numMPCRounds];
        for (int i8 = 0; i8 < this.numMPCRounds; i8++) {
            if (contains(signature2.challengeC, this.numOpenedRounds, i8)) {
                signature2.proofs[i8] = new Signature2.Proof2(this);
                int iIndexOf = indexOf(signature2.challengeC, this.numOpenedRounds, i8);
                int[] iArr6 = {signature2.challengeP[iIndexOf]};
                signature2.proofs[i8].seedInfo = new byte[this.numMPCParties * this.seedSizeBytes];
                signature2.proofs[i8].seedInfoLen = treeArr[i8].revealSeeds(iArr6, 1, signature2.proofs[i8].seedInfo, this.numMPCParties * this.seedSizeBytes);
                if (signature2.challengeP[iIndexOf] != this.numMPCParties - 1) {
                    getAuxBits(signature2.proofs[i8].aux, tapeArr[i8]);
                }
                System.arraycopy(bArr3[i8], 0, signature2.proofs[i8].input, 0, this.stateSizeBytes);
                System.arraycopy(msgArr[i8].msgs[signature2.challengeP[iIndexOf]], 0, signature2.proofs[i8].msgs, 0, this.andSizeBytes);
                System.arraycopy(bArr5[i8][signature2.challengeP[iIndexOf]], 0, signature2.proofs[i8].C, 0, this.digestSizeBytes);
            }
        }
        return true;
    }

    static int indexOf(int[] iArr, int i, int i2) {
        for (int i3 = 0; i3 < i; i3++) {
            if (iArr[i3] == i2) {
                return i3;
            }
        }
        return -1;
    }

    private int[] getMissingLeavesList(int[] iArr) {
        int[] iArr2 = new int[this.numMPCRounds - this.numOpenedRounds];
        int i = 0;
        for (int i2 = 0; i2 < this.numMPCRounds; i2++) {
            if (!contains(iArr, this.numOpenedRounds, i2)) {
                iArr2[i] = i2;
                i++;
            }
        }
        return iArr2;
    }

    private void HCP(byte[] bArr, int[] iArr, int[] iArr2, byte[][] bArr2, byte[] bArr3, byte[] bArr4, int[] iArr3, int[] iArr4, byte[] bArr5) {
        for (int i = 0; i < this.numMPCRounds; i++) {
            this.digest.update(bArr2[i], 0, this.digestSizeBytes);
        }
        byte[] bArr6 = new byte[32];
        this.digest.update(bArr3, 0, this.digestSizeBytes);
        this.digest.update(bArr4, 0, 32);
        updateDigest(iArr3, bArr6);
        updateDigest(iArr4, bArr6);
        this.digest.update(bArr5, 0, bArr5.length);
        this.digest.doFinal(bArr, 0, this.digestSizeBytes);
        if (iArr == null || iArr2 == null) {
            return;
        }
        expandChallengeHash(bArr, iArr, iArr2);
    }

    static int bitsToChunks(int i, byte[] bArr, int i2, int[] iArr) {
        if (i > i2 * 8) {
            return 0;
        }
        int i3 = (i2 * 8) / i;
        for (int i4 = 0; i4 < i3; i4++) {
            iArr[i4] = 0;
            for (int i5 = 0; i5 < i; i5++) {
                int i6 = i4;
                iArr[i6] = iArr[i6] + (Utils.getBit(bArr, (i4 * i) + i5) << i5);
            }
        }
        return i3;
    }

    static int appendUnique(int[] iArr, int i, int i2) {
        if (i2 == 0) {
            iArr[i2] = i;
            return i2 + 1;
        }
        for (int i3 = 0; i3 < i2; i3++) {
            if (iArr[i3] == i) {
                return i2;
            }
        }
        iArr[i2] = i;
        return i2 + 1;
    }

    private void expandChallengeHash(byte[] bArr, int[] iArr, int[] iArr2) {
        int iCeil_log2 = Utils.ceil_log2(this.numMPCRounds);
        int iCeil_log3 = Utils.ceil_log2(this.numMPCParties);
        int[] iArr3 = new int[(this.digestSizeBytes * 8) / Math.min(iCeil_log2, iCeil_log3)];
        byte[] bArr2 = new byte[64];
        System.arraycopy(bArr, 0, bArr2, 0, this.digestSizeBytes);
        int iAppendUnique = 0;
        while (iAppendUnique < this.numOpenedRounds) {
            int iBitsToChunks = bitsToChunks(iCeil_log2, bArr2, this.digestSizeBytes, iArr3);
            for (int i = 0; i < iBitsToChunks; i++) {
                if (iArr3[i] < this.numMPCRounds) {
                    iAppendUnique = appendUnique(iArr, iArr3[i], iAppendUnique);
                }
                if (iAppendUnique == this.numOpenedRounds) {
                    break;
                }
            }
            this.digest.update((byte) 1);
            this.digest.update(bArr2, 0, this.digestSizeBytes);
            this.digest.doFinal(bArr2, 0, this.digestSizeBytes);
        }
        int i2 = 0;
        while (i2 < this.numOpenedRounds) {
            int iBitsToChunks2 = bitsToChunks(iCeil_log3, bArr2, this.digestSizeBytes, iArr3);
            for (int i3 = 0; i3 < iBitsToChunks2; i3++) {
                if (iArr3[i3] < this.numMPCParties) {
                    iArr2[i2] = iArr3[i3];
                    i2++;
                }
                if (i2 == this.numOpenedRounds) {
                    break;
                }
            }
            this.digest.update((byte) 1);
            this.digest.update(bArr2, 0, this.digestSizeBytes);
            this.digest.doFinal(bArr2, 0, this.digestSizeBytes);
        }
    }

    private void commit_h(byte[] bArr, byte[][] bArr2) {
        for (int i = 0; i < this.numMPCParties; i++) {
            this.digest.update(bArr2[i], 0, this.digestSizeBytes);
        }
        this.digest.doFinal(bArr, 0, this.digestSizeBytes);
    }

    private void commit_v(byte[] bArr, byte[] bArr2, Msg msg) {
        this.digest.update(bArr2, 0, this.stateSizeBytes);
        for (int i = 0; i < this.numMPCParties; i++) {
            this.digest.update(msg.msgs[i], 0, Utils.numBytes(msg.pos));
        }
        this.digest.doFinal(bArr, 0, this.digestSizeBytes);
    }

    private int simulateOnline(int[] iArr, Tape tape, int[] iArr2, Msg msg, int[] iArr3, int[] iArr4) {
        int[] iArr5 = new int[16];
        int[] iArr6 = new int[16];
        KMatricesWithPointer kMatricesWithPointerKMatrix = this.lowmcConstants.KMatrix(this, 0);
        matrix_mul(iArr5, iArr, kMatricesWithPointerKMatrix.getData(), kMatricesWithPointerKMatrix.getMatrixPointer());
        xor_array(iArr6, iArr5, iArr3, 0);
        for (int i = 1; i <= this.numRounds; i++) {
            tapesToWords(iArr2, tape);
            mpc_sbox(iArr6, iArr2, tape, msg);
            KMatricesWithPointer kMatricesWithPointerLMatrix = this.lowmcConstants.LMatrix(this, i - 1);
            matrix_mul(iArr6, iArr6, kMatricesWithPointerLMatrix.getData(), kMatricesWithPointerLMatrix.getMatrixPointer());
            KMatricesWithPointer kMatricesWithPointerRConstant = this.lowmcConstants.RConstant(this, i - 1);
            xor_array(iArr6, iArr6, kMatricesWithPointerRConstant.getData(), kMatricesWithPointerRConstant.getMatrixPointer());
            KMatricesWithPointer kMatricesWithPointerKMatrix2 = this.lowmcConstants.KMatrix(this, i);
            matrix_mul(iArr5, iArr, kMatricesWithPointerKMatrix2.getData(), kMatricesWithPointerKMatrix2.getMatrixPointer());
            xor_array(iArr6, iArr5, iArr6, 0);
        }
        return subarrayEquals(iArr6, iArr4, this.stateSizeWords) ? 0 : -1;
    }

    private void createRandomTapes(Tape tape, byte[][] bArr, int i, byte[] bArr2, int i2) {
        int i3 = 2 * this.andSizeBytes;
        for (int i4 = 0; i4 < this.numMPCParties; i4++) {
            this.digest.update(bArr[i4 + i], 0, this.seedSizeBytes);
            this.digest.update(bArr2, 0, 32);
            this.digest.update(Pack.intToLittleEndian(i2), 0, 2);
            this.digest.update(Pack.intToLittleEndian(i4), 0, 2);
            this.digest.doFinal(tape.tapes[i4], 0, i3);
        }
    }

    private static boolean subarrayEquals(byte[] bArr, byte[] bArr2, int i) {
        if (bArr.length < i || bArr2.length < i) {
            return false;
        }
        for (int i2 = 0; i2 < i; i2++) {
            if (bArr[i2] != bArr2[i2]) {
                return false;
            }
        }
        return true;
    }

    private static boolean subarrayEquals(int[] iArr, int[] iArr2, int i) {
        if (iArr.length < i || iArr2.length < i) {
            return false;
        }
        for (int i2 = 0; i2 < i; i2++) {
            if (iArr[i2] != iArr2[i2]) {
                return false;
            }
        }
        return true;
    }

    static int extend(int i) {
        return (i - 1) ^ (-1);
    }

    private void wordToMsgs(int i, Msg msg) {
        for (int i2 = 0; i2 < this.numMPCParties; i2++) {
            Utils.setBit(msg.msgs[i2], msg.pos, (byte) Utils.getBit(i, i2));
        }
        msg.pos++;
    }

    private int mpc_AND(int i, int i2, int i3, int i4, Tape tape, Msg msg) {
        int iExtend = ((extend(i) & i4) ^ (extend(i2) & i3)) ^ tape.tapesToWord();
        if (msg.unopened >= 0) {
            iExtend = Utils.setBit(iExtend, msg.unopened, Utils.getBit(msg.msgs[msg.unopened], msg.pos));
        }
        wordToMsgs(iExtend, msg);
        return Utils.parity16(iExtend) ^ (i & i2);
    }

    private void mpc_sbox(int[] iArr, int[] iArr2, Tape tape, Msg msg) {
        for (int i = 0; i < this.numSboxes * 3; i += 3) {
            int bitFromWordArray = Utils.getBitFromWordArray(iArr, i + 2);
            int i2 = iArr2[i + 2];
            int bitFromWordArray2 = Utils.getBitFromWordArray(iArr, i + 1);
            int i3 = iArr2[i + 1];
            int bitFromWordArray3 = Utils.getBitFromWordArray(iArr, i);
            int i4 = iArr2[i];
            int iMpc_AND = mpc_AND(bitFromWordArray, bitFromWordArray2, i2, i3, tape, msg);
            int iMpc_AND2 = mpc_AND(bitFromWordArray2, bitFromWordArray3, i3, i4, tape, msg);
            int iMpc_AND3 = mpc_AND(bitFromWordArray3, bitFromWordArray, i4, i2, tape, msg);
            Utils.setBitInWordArray(iArr, i + 2, bitFromWordArray ^ iMpc_AND2);
            Utils.setBitInWordArray(iArr, i + 1, (bitFromWordArray ^ bitFromWordArray2) ^ iMpc_AND3);
            Utils.setBitInWordArray(iArr, i, ((bitFromWordArray ^ bitFromWordArray2) ^ bitFromWordArray3) ^ iMpc_AND);
        }
    }

    protected void aux_mpc_sbox(int[] iArr, int[] iArr2, Tape tape) {
        for (int i = 0; i < this.numSboxes * 3; i += 3) {
            int bitFromWordArray = Utils.getBitFromWordArray(iArr, i + 2);
            int bitFromWordArray2 = Utils.getBitFromWordArray(iArr, i + 1);
            int bitFromWordArray3 = Utils.getBitFromWordArray(iArr, i);
            int bitFromWordArray4 = Utils.getBitFromWordArray(iArr2, i + 2);
            int bitFromWordArray5 = Utils.getBitFromWordArray(iArr2, i + 1);
            aux_mpc_AND(bitFromWordArray, bitFromWordArray2, ((Utils.getBitFromWordArray(iArr2, i) ^ bitFromWordArray) ^ bitFromWordArray2) ^ bitFromWordArray3, tape);
            aux_mpc_AND(bitFromWordArray2, bitFromWordArray3, bitFromWordArray4 ^ bitFromWordArray, tape);
            aux_mpc_AND(bitFromWordArray3, bitFromWordArray, (bitFromWordArray5 ^ bitFromWordArray) ^ bitFromWordArray2, tape);
        }
    }

    private void aux_mpc_AND(int i, int i2, int i3, Tape tape) {
        int i4 = this.numMPCParties - 1;
        Utils.setBit(tape.tapes[i4], tape.pos - 1, (byte) ((((i & i2) ^ (Utils.parity16(tape.tapesToWord()) ^ Utils.getBit(tape.tapes[i4], tape.pos - 1))) ^ i3) & 255));
    }

    private boolean contains(int[] iArr, int i, int i2) {
        for (int i3 = 0; i3 < i; i3++) {
            if (iArr[i3] == i2) {
                return true;
            }
        }
        return false;
    }

    private void tapesToWords(int[] iArr, Tape tape) {
        for (int i = 0; i < this.stateSizeBits; i++) {
            iArr[i] = tape.tapesToWord();
        }
    }

    private void getAuxBits(byte[] bArr, Tape tape) {
        byte[] bArr2 = tape.tapes[this.numMPCParties - 1];
        int i = this.stateSizeBits;
        int i2 = 0;
        int i3 = 0;
        for (int i4 = 0; i4 < this.numRounds; i4++) {
            i3 += i;
            for (int i5 = 0; i5 < i; i5++) {
                int i6 = i2;
                i2++;
                int i7 = i3;
                i3++;
                Utils.setBit(bArr, i6, Utils.getBit(bArr2, i7));
            }
        }
    }

    private void commit(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, int i, int i2) {
        this.digest.update(bArr2, 0, this.seedSizeBytes);
        if (bArr3 != null) {
            this.digest.update(bArr3, 0, this.andSizeBytes);
        }
        this.digest.update(bArr4, 0, 32);
        this.digest.update(Pack.intToLittleEndian(i), 0, 2);
        this.digest.update(Pack.intToLittleEndian(i2), 0, 2);
        this.digest.doFinal(bArr, 0, this.digestSizeBytes);
    }

    private void computeSaltAndRootSeed(byte[] bArr, int[] iArr, int[] iArr2, int[] iArr3, byte[] bArr2) {
        byte[] bArr3 = new byte[32];
        updateDigest(iArr, bArr3);
        this.digest.update(bArr2, 0, bArr2.length);
        updateDigest(iArr2, bArr3);
        updateDigest(iArr3, bArr3);
        Pack.shortToLittleEndian((short) this.stateSizeBits, bArr3, 0);
        this.digest.update(bArr3, 0, 2);
        this.digest.doFinal(bArr, 0, bArr.length);
    }

    private void updateDigest(int[] iArr, byte[] bArr) {
        Pack.intToLittleEndian(iArr, bArr, 0);
        this.digest.update(bArr, 0, this.stateSizeBytes);
    }

    static boolean is_picnic3(int i) {
        return i == 7 || i == 8 || i == 9;
    }

    public void crypto_sign_keypair(byte[] bArr, byte[] bArr2, SecureRandom secureRandom) {
        byte[] bArr3 = new byte[this.stateSizeWords * 4];
        byte[] bArr4 = new byte[this.stateSizeWords * 4];
        byte[] bArr5 = new byte[this.stateSizeWords * 4];
        picnic_keygen(bArr3, bArr4, bArr5, secureRandom);
        picnic_write_public_key(bArr4, bArr3, bArr);
        picnic_write_private_key(bArr5, bArr4, bArr3, bArr2);
    }

    private int picnic_write_private_key(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        int i = 1 + (3 * this.stateSizeBytes);
        if (bArr4.length < i) {
            LOG.fine("Failed writing private key!");
            return -1;
        }
        bArr4[0] = (byte) this.parameters;
        System.arraycopy(bArr, 0, bArr4, 1, this.stateSizeBytes);
        System.arraycopy(bArr2, 0, bArr4, 1 + this.stateSizeBytes, this.stateSizeBytes);
        System.arraycopy(bArr3, 0, bArr4, 1 + (2 * this.stateSizeBytes), this.stateSizeBytes);
        return i;
    }

    private int picnic_write_public_key(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        int i = 1 + (2 * this.stateSizeBytes);
        if (bArr3.length < i) {
            LOG.fine("Failed writing public key!");
            return -1;
        }
        bArr3[0] = (byte) this.parameters;
        System.arraycopy(bArr, 0, bArr3, 1, this.stateSizeBytes);
        System.arraycopy(bArr2, 0, bArr3, 1 + this.stateSizeBytes, this.stateSizeBytes);
        return i;
    }

    private void picnic_keygen(byte[] bArr, byte[] bArr2, byte[] bArr3, SecureRandom secureRandom) {
        int[] iArr = new int[bArr3.length / 4];
        int[] iArr2 = new int[bArr.length / 4];
        int[] iArr3 = new int[bArr2.length / 4];
        secureRandom.nextBytes(bArr3);
        Pack.littleEndianToInt(bArr3, 0, iArr);
        Utils.zeroTrailingBits(iArr, this.stateSizeBits);
        secureRandom.nextBytes(bArr);
        Pack.littleEndianToInt(bArr, 0, iArr2);
        Utils.zeroTrailingBits(iArr2, this.stateSizeBits);
        LowMCEnc(iArr2, iArr3, iArr);
        Pack.intToLittleEndian(iArr, bArr3, 0);
        Pack.intToLittleEndian(iArr2, bArr, 0);
        Pack.intToLittleEndian(iArr3, bArr2, 0);
    }

    private void LowMCEnc(int[] iArr, int[] iArr2, int[] iArr3) {
        int[] iArr4 = new int[16];
        if (iArr != iArr2) {
            System.arraycopy(iArr, 0, iArr2, 0, this.stateSizeWords);
        }
        KMatricesWithPointer kMatricesWithPointerKMatrix = this.lowmcConstants.KMatrix(this, 0);
        matrix_mul(iArr4, iArr3, kMatricesWithPointerKMatrix.getData(), kMatricesWithPointerKMatrix.getMatrixPointer());
        xor_array(iArr2, iArr2, iArr4, 0);
        for (int i = 1; i <= this.numRounds; i++) {
            KMatricesWithPointer kMatricesWithPointerKMatrix2 = this.lowmcConstants.KMatrix(this, i);
            matrix_mul(iArr4, iArr3, kMatricesWithPointerKMatrix2.getData(), kMatricesWithPointerKMatrix2.getMatrixPointer());
            substitution(iArr2);
            KMatricesWithPointer kMatricesWithPointerLMatrix = this.lowmcConstants.LMatrix(this, i - 1);
            matrix_mul(iArr2, iArr2, kMatricesWithPointerLMatrix.getData(), kMatricesWithPointerLMatrix.getMatrixPointer());
            KMatricesWithPointer kMatricesWithPointerRConstant = this.lowmcConstants.RConstant(this, i - 1);
            xor_array(iArr2, iArr2, kMatricesWithPointerRConstant.getData(), kMatricesWithPointerRConstant.getMatrixPointer());
            xor_array(iArr2, iArr2, iArr4, 0);
        }
    }

    private void substitution(int[] iArr) {
        for (int i = 0; i < this.numSboxes * 3; i += 3) {
            int bitFromWordArray = Utils.getBitFromWordArray(iArr, i + 2);
            int bitFromWordArray2 = Utils.getBitFromWordArray(iArr, i + 1);
            int bitFromWordArray3 = Utils.getBitFromWordArray(iArr, i);
            Utils.setBitInWordArray(iArr, i + 2, bitFromWordArray ^ (bitFromWordArray2 & bitFromWordArray3));
            Utils.setBitInWordArray(iArr, i + 1, (bitFromWordArray ^ bitFromWordArray2) ^ (bitFromWordArray & bitFromWordArray3));
            Utils.setBitInWordArray(iArr, i, ((bitFromWordArray ^ bitFromWordArray2) ^ bitFromWordArray3) ^ (bitFromWordArray & bitFromWordArray2));
        }
    }

    private void xor_three(int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4) {
        for (int i = 0; i < this.stateSizeWords; i++) {
            iArr[i] = (iArr2[i] ^ iArr3[i]) ^ iArr4[i];
        }
    }

    protected void xor_array(int[] iArr, int[] iArr2, int[] iArr3, int i) {
        for (int i2 = 0; i2 < this.stateSizeWords; i2++) {
            iArr[i2] = iArr2[i2] ^ iArr3[i2 + i];
        }
    }

    protected void matrix_mul(int[] iArr, int[] iArr2, int[] iArr3, int i) {
        matrix_mul_offset(iArr, 0, iArr2, 0, iArr3, i);
    }

    protected void matrix_mul_offset(int[] iArr, int i, int[] iArr2, int i2, int[] iArr3, int i3) {
        int[] iArr4 = new int[16];
        iArr4[this.stateSizeWords - 1] = 0;
        int i4 = this.stateSizeBits / 32;
        int i5 = (this.stateSizeWords * 32) - this.stateSizeBits;
        int iBitPermuteStepSimple = Bits.bitPermuteStepSimple(Bits.bitPermuteStepSimple(Bits.bitPermuteStepSimple((-1) >>> i5, 1431655765, 1), 858993459, 2), 252645135, 4);
        for (int i6 = 0; i6 < this.stateSizeBits; i6++) {
            int i7 = 0;
            for (int i8 = 0; i8 < i4; i8++) {
                i7 ^= iArr2[i2 + i8] & iArr3[i3 + ((i6 * this.stateSizeWords) + i8)];
            }
            if (i5 > 0) {
                i7 ^= (iArr2[i2 + i4] & iArr3[i3 + ((i6 * this.stateSizeWords) + i4)]) & iBitPermuteStepSimple;
            }
            Utils.setBit(iArr4, i6, Utils.parity32(i7));
        }
        System.arraycopy(iArr4, 0, iArr, i, this.stateSizeWords);
    }
}
