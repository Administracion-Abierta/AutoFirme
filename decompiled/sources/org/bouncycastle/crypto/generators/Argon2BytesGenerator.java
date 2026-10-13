package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Longs;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/generators/Argon2BytesGenerator.class */
public class Argon2BytesGenerator {
    private static final int ARGON2_BLOCK_SIZE = 1024;
    private static final int ARGON2_QWORDS_IN_BLOCK = 128;
    private static final int ARGON2_ADDRESSES_IN_BLOCK = 128;
    private static final int ARGON2_PREHASH_DIGEST_LENGTH = 64;
    private static final int ARGON2_PREHASH_SEED_LENGTH = 72;
    private static final int ARGON2_SYNC_POINTS = 4;
    private static final int MIN_PARALLELISM = 1;
    private static final int MAX_PARALLELISM = 16777215;
    private static final int MIN_OUTLEN = 4;
    private static final int MIN_ITERATIONS = 1;
    private static final long M32L = 4294967295L;
    private static final byte[] ZERO_BYTES = new byte[4];
    private Argon2Parameters parameters;
    private Block[] memory;
    private int segmentLength;
    private int laneLength;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/generators/Argon2BytesGenerator$Block.class */
    private static class Block {
        private static final int SIZE = 128;
        private final long[] v;

        private Block() {
            this.v = new long[128];
        }

        void fromBytes(byte[] bArr) {
            if (bArr.length < 1024) {
                throw new IllegalArgumentException("input shorter than blocksize");
            }
            Pack.littleEndianToLong(bArr, 0, this.v);
        }

        void toBytes(byte[] bArr) {
            if (bArr.length < 1024) {
                throw new IllegalArgumentException("output shorter than blocksize");
            }
            Pack.longToLittleEndian(this.v, bArr, 0);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void copyBlock(Block block) {
            System.arraycopy(block.v, 0, this.v, 0, 128);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void xor(Block block, Block block2) {
            long[] jArr = this.v;
            long[] jArr2 = block.v;
            long[] jArr3 = block2.v;
            for (int i = 0; i < 128; i++) {
                jArr[i] = jArr2[i] ^ jArr3[i];
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void xorWith(Block block) {
            long[] jArr = this.v;
            long[] jArr2 = block.v;
            for (int i = 0; i < 128; i++) {
                int i2 = i;
                jArr[i2] = jArr[i2] ^ jArr2[i];
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void xorWith(Block block, Block block2) {
            long[] jArr = this.v;
            long[] jArr2 = block.v;
            long[] jArr3 = block2.v;
            for (int i = 0; i < 128; i++) {
                int i2 = i;
                jArr[i2] = jArr[i2] ^ (jArr2[i] ^ jArr3[i]);
            }
        }

        public Block clear() {
            Arrays.fill(this.v, 0L);
            return this;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/generators/Argon2BytesGenerator$FillBlock.class */
    private static class FillBlock {
        Block R;
        Block Z;
        Block addressBlock;
        Block inputBlock;

        private FillBlock() {
            this.R = new Block();
            this.Z = new Block();
            this.addressBlock = new Block();
            this.inputBlock = new Block();
        }

        private void applyBlake() {
            for (int i = 0; i < 8; i++) {
                int i2 = 16 * i;
                Argon2BytesGenerator.roundFunction(this.Z, i2, i2 + 1, i2 + 2, i2 + 3, i2 + 4, i2 + 5, i2 + 6, i2 + 7, i2 + 8, i2 + 9, i2 + 10, i2 + 11, i2 + 12, i2 + 13, i2 + 14, i2 + 15);
            }
            for (int i3 = 0; i3 < 8; i3++) {
                int i4 = 2 * i3;
                Argon2BytesGenerator.roundFunction(this.Z, i4, i4 + 1, i4 + 16, i4 + 17, i4 + 32, i4 + 33, i4 + 48, i4 + 49, i4 + 64, i4 + 65, i4 + 80, i4 + 81, i4 + 96, i4 + 97, i4 + 112, i4 + 113);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void fillBlock(Block block, Block block2) {
            this.Z.copyBlock(block);
            applyBlake();
            block2.xor(block, this.Z);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void fillBlock(Block block, Block block2, Block block3) {
            this.R.xor(block, block2);
            this.Z.copyBlock(this.R);
            applyBlake();
            block3.xor(this.R, this.Z);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void fillBlockWithXor(Block block, Block block2, Block block3) {
            this.R.xor(block, block2);
            this.Z.copyBlock(this.R);
            applyBlake();
            block3.xorWith(this.R, this.Z);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/generators/Argon2BytesGenerator$Position.class */
    private static class Position {
        int pass;
        int lane;
        int slice;

        Position() {
        }
    }

    public void init(Argon2Parameters argon2Parameters) {
        if (argon2Parameters.getVersion() != 16 && argon2Parameters.getVersion() != 19) {
            throw new UnsupportedOperationException("unknown Argon2 version");
        }
        if (argon2Parameters.getType() != 0 && argon2Parameters.getType() != 1 && argon2Parameters.getType() != 2) {
            throw new UnsupportedOperationException("unknown Argon2 type");
        }
        if (argon2Parameters.getLanes() < 1) {
            throw new IllegalStateException("lanes must be at least 1");
        }
        if (argon2Parameters.getLanes() > MAX_PARALLELISM) {
            throw new IllegalStateException("lanes must be at most 16777215");
        }
        if (argon2Parameters.getIterations() < 1) {
            throw new IllegalStateException("iterations is less than: 1");
        }
        this.parameters = argon2Parameters;
        this.segmentLength = Math.max(argon2Parameters.getMemory(), 8 * argon2Parameters.getLanes()) / (4 * argon2Parameters.getLanes());
        this.laneLength = this.segmentLength * 4;
        this.memory = new Block[argon2Parameters.getLanes() * this.laneLength];
        for (int i = 0; i < this.memory.length; i++) {
            this.memory[i] = new Block();
        }
    }

    public int generateBytes(char[] cArr, byte[] bArr) {
        return generateBytes(this.parameters.getCharToByteConverter().convert(cArr), bArr);
    }

    public int generateBytes(char[] cArr, byte[] bArr, int i, int i2) {
        return generateBytes(this.parameters.getCharToByteConverter().convert(cArr), bArr, i, i2);
    }

    public int generateBytes(byte[] bArr, byte[] bArr2) {
        return generateBytes(bArr, bArr2, 0, bArr2.length);
    }

    public int generateBytes(byte[] bArr, byte[] bArr2, int i, int i2) {
        if (i2 < 4) {
            throw new IllegalStateException("output length less than 4");
        }
        byte[] bArr3 = new byte[1024];
        initialize(bArr3, bArr, i2);
        fillMemoryBlocks();
        digest(bArr3, bArr2, i, i2);
        reset();
        return i2;
    }

    private void reset() {
        if (null != this.memory) {
            for (int i = 0; i < this.memory.length; i++) {
                Block block = this.memory[i];
                if (null != block) {
                    block.clear();
                }
            }
        }
    }

    private void fillMemoryBlocks() {
        FillBlock fillBlock = new FillBlock();
        Position position = new Position();
        for (int i = 0; i < this.parameters.getIterations(); i++) {
            position.pass = i;
            for (int i2 = 0; i2 < 4; i2++) {
                position.slice = i2;
                for (int i3 = 0; i3 < this.parameters.getLanes(); i3++) {
                    position.lane = i3;
                    fillSegment(fillBlock, position);
                }
            }
        }
    }

    private void fillSegment(FillBlock fillBlock, Position position) {
        Block blockClear = null;
        Block blockClear2 = null;
        boolean zIsDataIndependentAddressing = isDataIndependentAddressing(position);
        int startingIndex = getStartingIndex(position);
        int i = (position.lane * this.laneLength) + (position.slice * this.segmentLength) + startingIndex;
        int prevOffset = getPrevOffset(i);
        if (zIsDataIndependentAddressing) {
            blockClear = fillBlock.addressBlock.clear();
            blockClear2 = fillBlock.inputBlock.clear();
            initAddressBlocks(fillBlock, position, blockClear2, blockClear);
        }
        boolean zIsWithXor = isWithXor(position);
        for (int i2 = startingIndex; i2 < this.segmentLength; i2++) {
            long pseudoRandom = getPseudoRandom(fillBlock, i2, blockClear, blockClear2, prevOffset, zIsDataIndependentAddressing);
            int refLane = getRefLane(position, pseudoRandom);
            int refColumn = getRefColumn(position, i2, pseudoRandom, refLane == position.lane);
            Block block = this.memory[prevOffset];
            Block block2 = this.memory[(this.laneLength * refLane) + refColumn];
            Block block3 = this.memory[i];
            if (zIsWithXor) {
                fillBlock.fillBlockWithXor(block, block2, block3);
            } else {
                fillBlock.fillBlock(block, block2, block3);
            }
            prevOffset = i;
            i++;
        }
    }

    private boolean isDataIndependentAddressing(Position position) {
        return this.parameters.getType() == 1 || (this.parameters.getType() == 2 && position.pass == 0 && position.slice < 2);
    }

    private void initAddressBlocks(FillBlock fillBlock, Position position, Block block, Block block2) {
        block.v[0] = intToLong(position.pass);
        block.v[1] = intToLong(position.lane);
        block.v[2] = intToLong(position.slice);
        block.v[3] = intToLong(this.memory.length);
        block.v[4] = intToLong(this.parameters.getIterations());
        block.v[5] = intToLong(this.parameters.getType());
        if (position.pass == 0 && position.slice == 0) {
            nextAddresses(fillBlock, block, block2);
        }
    }

    private boolean isWithXor(Position position) {
        return (position.pass == 0 || this.parameters.getVersion() == 16) ? false : true;
    }

    private int getPrevOffset(int i) {
        return i % this.laneLength == 0 ? (i + this.laneLength) - 1 : i - 1;
    }

    private static int getStartingIndex(Position position) {
        return (position.pass == 0 && position.slice == 0) ? 2 : 0;
    }

    private void nextAddresses(FillBlock fillBlock, Block block, Block block2) {
        long[] jArr = block.v;
        jArr[6] = jArr[6] + 1;
        fillBlock.fillBlock(block, block2);
        fillBlock.fillBlock(block2, block2);
    }

    private long getPseudoRandom(FillBlock fillBlock, int i, Block block, Block block2, int i2, boolean z) {
        if (!z) {
            return this.memory[i2].v[0];
        }
        int i3 = i % 128;
        if (i3 == 0) {
            nextAddresses(fillBlock, block2, block);
        }
        return block.v[i3];
    }

    private int getRefLane(Position position, long j) {
        int lanes = (int) ((j >>> 32) % ((long) this.parameters.getLanes()));
        if (position.pass == 0 && position.slice == 0) {
            lanes = position.lane;
        }
        return lanes;
    }

    private int getRefColumn(Position position, int i, long j, boolean z) {
        int i2;
        int i3;
        if (position.pass == 0) {
            i2 = 0;
            if (z) {
                i3 = ((position.slice * this.segmentLength) + i) - 1;
            } else {
                i3 = (position.slice * this.segmentLength) + (i == 0 ? -1 : 0);
            }
        } else {
            i2 = ((position.slice + 1) * this.segmentLength) % this.laneLength;
            if (z) {
                i3 = ((this.laneLength - this.segmentLength) + i) - 1;
            } else {
                i3 = (this.laneLength - this.segmentLength) + (i == 0 ? -1 : 0);
            }
        }
        long j2 = j & 4294967295L;
        return ((int) (((long) i2) + (((long) (i3 - 1)) - ((((long) i3) * ((j2 * j2) >>> 32)) >>> 32)))) % this.laneLength;
    }

    private void digest(byte[] bArr, byte[] bArr2, int i, int i2) {
        Block block = this.memory[this.laneLength - 1];
        for (int i3 = 1; i3 < this.parameters.getLanes(); i3++) {
            block.xorWith(this.memory[(i3 * this.laneLength) + (this.laneLength - 1)]);
        }
        block.toBytes(bArr);
        hash(bArr, bArr2, i, i2);
    }

    private void hash(byte[] bArr, byte[] bArr2, int i, int i2) {
        byte[] bArr3 = new byte[4];
        Pack.intToLittleEndian(i2, bArr3, 0);
        if (i2 <= 64) {
            Blake2bDigest blake2bDigest = new Blake2bDigest(i2 * 8);
            blake2bDigest.update(bArr3, 0, bArr3.length);
            blake2bDigest.update(bArr, 0, bArr.length);
            blake2bDigest.doFinal(bArr2, i);
            return;
        }
        Blake2bDigest blake2bDigest2 = new Blake2bDigest(64 * 8);
        byte[] bArr4 = new byte[64];
        blake2bDigest2.update(bArr3, 0, bArr3.length);
        blake2bDigest2.update(bArr, 0, bArr.length);
        blake2bDigest2.doFinal(bArr4, 0);
        int i3 = 64 / 2;
        System.arraycopy(bArr4, 0, bArr2, i, i3);
        int i4 = i + i3;
        int i5 = ((i2 + 31) / 32) - 2;
        int i6 = 2;
        while (i6 <= i5) {
            blake2bDigest2.update(bArr4, 0, bArr4.length);
            blake2bDigest2.doFinal(bArr4, 0);
            System.arraycopy(bArr4, 0, bArr2, i4, i3);
            i6++;
            i4 += i3;
        }
        Blake2bDigest blake2bDigest3 = new Blake2bDigest((i2 - (32 * i5)) * 8);
        blake2bDigest3.update(bArr4, 0, bArr4.length);
        blake2bDigest3.doFinal(bArr2, i4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void roundFunction(Block block, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int i16) {
        long[] jArr = block.v;
        F(jArr, i, i5, i9, i13);
        F(jArr, i2, i6, i10, i14);
        F(jArr, i3, i7, i11, i15);
        F(jArr, i4, i8, i12, i16);
        F(jArr, i, i6, i11, i16);
        F(jArr, i2, i7, i12, i13);
        F(jArr, i3, i8, i9, i14);
        F(jArr, i4, i5, i10, i15);
    }

    private static void F(long[] jArr, int i, int i2, int i3, int i4) {
        quarterRound(jArr, i, i2, i4, 32);
        quarterRound(jArr, i3, i4, i2, 24);
        quarterRound(jArr, i, i2, i4, 16);
        quarterRound(jArr, i3, i4, i2, 63);
    }

    private static void quarterRound(long[] jArr, int i, int i2, int i3, int i4) {
        long j = jArr[i];
        long j2 = jArr[i2];
        long j3 = j + j2 + (2 * (j & 4294967295L) * (j2 & 4294967295L));
        long jRotateRight = Longs.rotateRight(jArr[i3] ^ j3, i4);
        jArr[i] = j3;
        jArr[i3] = jRotateRight;
    }

    private void initialize(byte[] bArr, byte[] bArr2, int i) {
        Blake2bDigest blake2bDigest = new Blake2bDigest(512);
        int[] iArr = {this.parameters.getLanes(), i, this.parameters.getMemory(), this.parameters.getIterations(), this.parameters.getVersion(), this.parameters.getType()};
        Pack.intToLittleEndian(iArr, bArr, 0);
        blake2bDigest.update(bArr, 0, iArr.length * 4);
        addByteString(bArr, blake2bDigest, bArr2);
        addByteString(bArr, blake2bDigest, this.parameters.getSalt());
        addByteString(bArr, blake2bDigest, this.parameters.getSecret());
        addByteString(bArr, blake2bDigest, this.parameters.getAdditional());
        byte[] bArr3 = new byte[72];
        blake2bDigest.doFinal(bArr3, 0);
        fillFirstBlocks(bArr, bArr3);
    }

    private static void addByteString(byte[] bArr, Digest digest, byte[] bArr2) {
        if (null == bArr2) {
            digest.update(ZERO_BYTES, 0, 4);
            return;
        }
        Pack.intToLittleEndian(bArr2.length, bArr, 0);
        digest.update(bArr, 0, 4);
        digest.update(bArr2, 0, bArr2.length);
    }

    private void fillFirstBlocks(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[72];
        System.arraycopy(bArr2, 0, bArr3, 0, 64);
        bArr3[64] = 1;
        for (int i = 0; i < this.parameters.getLanes(); i++) {
            Pack.intToLittleEndian(i, bArr2, 68);
            Pack.intToLittleEndian(i, bArr3, 68);
            hash(bArr2, bArr, 0, 1024);
            this.memory[(i * this.laneLength) + 0].fromBytes(bArr);
            hash(bArr3, bArr, 0, 1024);
            this.memory[(i * this.laneLength) + 1].fromBytes(bArr);
        }
    }

    private long intToLong(int i) {
        return ((long) i) & 4294967295L;
    }
}
