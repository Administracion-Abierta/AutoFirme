package org.apache.commons.codec.digest;

import java.util.Arrays;
import java.util.Objects;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/digest/Blake3.class */
public final class Blake3 {
    private static final int BLOCK_LEN = 64;
    private static final int BLOCK_INTS = 16;
    private static final int KEY_LEN = 32;
    private static final int KEY_INTS = 8;
    private static final int OUT_LEN = 32;
    private static final int CHUNK_LEN = 1024;
    private static final int CHAINING_VALUE_INTS = 8;
    private static final int CHUNK_START = 1;
    private static final int CHUNK_END = 2;
    private static final int PARENT = 4;
    private static final int ROOT = 8;
    private static final int KEYED_HASH = 16;
    private static final int DERIVE_KEY_CONTEXT = 32;
    private static final int DERIVE_KEY_MATERIAL = 64;
    private final EngineState engineState;
    private static final int[] IV = {1779033703, -1150833019, 1013904242, -1521486534, 1359893119, -1694144372, 528734635, 1541459225};
    private static final byte[][] MSG_SCHEDULE = {new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}, new byte[]{2, 6, 3, 10, 7, 0, 4, 13, 1, 11, 12, 5, 9, 14, 15, 8}, new byte[]{3, 4, 10, 12, 13, 2, 7, 14, 6, 5, 9, 0, 11, 15, 8, 1}, new byte[]{10, 7, 12, 9, 14, 3, 13, 15, 4, 0, 11, 2, 5, 8, 1, 6}, new byte[]{12, 13, 9, 11, 15, 10, 14, 8, 7, 2, 5, 3, 0, 1, 6, 4}, new byte[]{9, 14, 11, 5, 8, 12, 15, 1, 13, 3, 0, 10, 2, 6, 4, 7}, new byte[]{11, 15, 5, 0, 1, 9, 8, 6, 14, 10, 2, 12, 3, 4, 7, 13}};

    /* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/digest/Blake3$ChunkState.class */
    private static final class ChunkState {
        private int[] chainingValue;
        private final long chunkCounter;
        private final int flags;
        private final byte[] block;
        private int blockLength;
        private int blocksCompressed;

        private ChunkState(int[] key, long chunkCounter, int flags) {
            this.block = new byte[64];
            this.chainingValue = key;
            this.chunkCounter = chunkCounter;
            this.flags = flags;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int length() {
            return (64 * this.blocksCompressed) + this.blockLength;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Output output() {
            int[] blockWords = Blake3.unpackInts(this.block, 16);
            int outputFlags = this.flags | startFlag() | 2;
            return new Output(this.chainingValue, blockWords, this.chunkCounter, this.blockLength, outputFlags);
        }

        private int startFlag() {
            return this.blocksCompressed == 0 ? 1 : 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void update(byte[] input, int offset, int length) {
            while (length > 0) {
                if (this.blockLength == 64) {
                    int[] blockWords = Blake3.unpackInts(this.block, 16);
                    this.chainingValue = Arrays.copyOf(Blake3.compress(this.chainingValue, blockWords, 64, this.chunkCounter, this.flags | startFlag()), 8);
                    this.blocksCompressed++;
                    this.blockLength = 0;
                    Arrays.fill(this.block, (byte) 0);
                }
                int want = 64 - this.blockLength;
                int take = Math.min(want, length);
                System.arraycopy(input, offset, this.block, this.blockLength, take);
                this.blockLength += take;
                offset += take;
                length -= take;
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/digest/Blake3$EngineState.class */
    private static final class EngineState {
        private final int[] key;
        private final int flags;
        private final int[][] cvStack;
        private int stackLen;
        private ChunkState state;

        private EngineState(int[] key, int flags) {
            this.cvStack = new int[54][];
            this.key = key;
            this.flags = flags;
            this.state = new ChunkState(key, 0L, flags);
        }

        private void addChunkCV(int[] firstCV, long totalChunks) {
            int[] newCV = firstCV;
            long j = totalChunks;
            while (true) {
                long chunkCounter = j;
                if ((chunkCounter & 1) == 0) {
                    newCV = Blake3.parentChainingValue(popCV(), newCV, this.key, this.flags);
                    j = chunkCounter >> 1;
                } else {
                    pushCV(newCV);
                    return;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void inputData(byte[] in, int offset, int length) {
            while (length > 0) {
                if (this.state.length() == 1024) {
                    int[] chunkCV = this.state.output().chainingValue();
                    long totalChunks = this.state.chunkCounter + 1;
                    addChunkCV(chunkCV, totalChunks);
                    this.state = new ChunkState(this.key, totalChunks, this.flags);
                }
                int want = 1024 - this.state.length();
                int take = Math.min(want, length);
                this.state.update(in, offset, take);
                offset += take;
                length -= take;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void outputHash(byte[] out, int offset, int length) {
            Output output = this.state.output();
            int parentNodesRemaining = this.stackLen;
            while (true) {
                int i = parentNodesRemaining;
                parentNodesRemaining--;
                if (i > 0) {
                    int[] parentCV = this.cvStack[parentNodesRemaining];
                    output = Blake3.parentOutput(parentCV, output.chainingValue(), this.key, this.flags);
                } else {
                    output.rootOutputBytes(out, offset, length);
                    return;
                }
            }
        }

        private int[] popCV() {
            int[][] iArr = this.cvStack;
            int i = this.stackLen - 1;
            this.stackLen = i;
            return iArr[i];
        }

        private void pushCV(int[] cv) {
            int[][] iArr = this.cvStack;
            int i = this.stackLen;
            this.stackLen = i + 1;
            iArr[i] = cv;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void reset() {
            this.stackLen = 0;
            Arrays.fill(this.cvStack, (Object) null);
            this.state = new ChunkState(this.key, 0L, this.flags);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/digest/Blake3$Output.class */
    private static final class Output {
        private final int[] inputChainingValue;
        private final int[] blockWords;
        private final long counter;
        private final int blockLength;
        private final int flags;

        private Output(int[] inputChainingValue, int[] blockWords, long counter, int blockLength, int flags) {
            this.inputChainingValue = inputChainingValue;
            this.blockWords = blockWords;
            this.counter = counter;
            this.blockLength = blockLength;
            this.flags = flags;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int[] chainingValue() {
            return Arrays.copyOf(Blake3.compress(this.inputChainingValue, this.blockWords, this.blockLength, this.counter, this.flags), 8);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void rootOutputBytes(byte[] out, int offset, int length) {
            int outputBlockCounter = 0;
            while (length > 0) {
                int chunkLength = Math.min(64, length);
                length -= chunkLength;
                int i = outputBlockCounter;
                outputBlockCounter++;
                int[] words = Blake3.compress(this.inputChainingValue, this.blockWords, this.blockLength, i, this.flags | 8);
                int wordCounter = 0;
                while (chunkLength > 0) {
                    int wordLength = Math.min(4, chunkLength);
                    int i2 = wordCounter;
                    wordCounter++;
                    Blake3.packInt(words[i2], out, offset, wordLength);
                    offset += wordLength;
                    chunkLength -= wordLength;
                }
            }
        }
    }

    private static void checkBufferArgs(byte[] buffer, int offset, int length) {
        Objects.requireNonNull(buffer);
        if (offset < 0) {
            throw new IndexOutOfBoundsException("Offset must be non-negative");
        }
        if (length < 0) {
            throw new IndexOutOfBoundsException("Length must be non-negative");
        }
        int bufferLength = buffer.length;
        if (offset > bufferLength - length) {
            throw new IndexOutOfBoundsException("Offset " + offset + " and length " + length + " out of bounds with buffer length " + bufferLength);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int[] compress(int[] chainingValue, int[] blockWords, int blockLength, long counter, int flags) {
        int[] state = Arrays.copyOf(chainingValue, 16);
        System.arraycopy(IV, 0, state, 8, 4);
        state[12] = (int) counter;
        state[13] = (int) (counter >> 32);
        state[14] = blockLength;
        state[15] = flags;
        for (int i = 0; i < 7; i++) {
            byte[] schedule = MSG_SCHEDULE[i];
            round(state, blockWords, schedule);
        }
        for (int i2 = 0; i2 < state.length / 2; i2++) {
            int i3 = i2;
            state[i3] = state[i3] ^ state[i2 + 8];
            int i4 = i2 + 8;
            state[i4] = state[i4] ^ chainingValue[i2];
        }
        return state;
    }

    private static void g(int[] state, int a, int b, int c, int d, int mx, int my) {
        state[a] = state[a] + state[b] + mx;
        state[d] = Integer.rotateRight(state[d] ^ state[a], 16);
        state[c] = state[c] + state[d];
        state[b] = Integer.rotateRight(state[b] ^ state[c], 12);
        state[a] = state[a] + state[b] + my;
        state[d] = Integer.rotateRight(state[d] ^ state[a], 8);
        state[c] = state[c] + state[d];
        state[b] = Integer.rotateRight(state[b] ^ state[c], 7);
    }

    public static byte[] hash(byte[] data) {
        return initHash().update(data).doFinalize(32);
    }

    public static Blake3 initHash() {
        return new Blake3(IV, 0);
    }

    public static Blake3 initKeyDerivationFunction(byte[] kdfContext) {
        Objects.requireNonNull(kdfContext);
        EngineState kdf = new EngineState(IV, 32);
        kdf.inputData(kdfContext, 0, kdfContext.length);
        byte[] key = new byte[32];
        kdf.outputHash(key, 0, key.length);
        return new Blake3(unpackInts(key, 8), 64);
    }

    public static Blake3 initKeyedHash(byte[] key) {
        Objects.requireNonNull(key);
        if (key.length != 32) {
            throw new IllegalArgumentException("Blake3 keys must be 32 bytes");
        }
        return new Blake3(unpackInts(key, 8), 16);
    }

    public static byte[] keyedHash(byte[] key, byte[] data) {
        return initKeyedHash(key).update(data).doFinalize(32);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void packInt(int value, byte[] dst, int off, int len) {
        for (int i = 0; i < len; i++) {
            dst[off + i] = (byte) (value >>> (i * 8));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int[] parentChainingValue(int[] leftChildCV, int[] rightChildCV, int[] key, int flags) {
        return parentOutput(leftChildCV, rightChildCV, key, flags).chainingValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Output parentOutput(int[] leftChildCV, int[] rightChildCV, int[] key, int flags) {
        int[] blockWords = Arrays.copyOf(leftChildCV, 16);
        System.arraycopy(rightChildCV, 0, blockWords, 8, 8);
        return new Output((int[]) key.clone(), blockWords, 0L, 64, flags | 4);
    }

    private static void round(int[] state, int[] msg, byte[] schedule) {
        g(state, 0, 4, 8, 12, msg[schedule[0]], msg[schedule[1]]);
        g(state, 1, 5, 9, 13, msg[schedule[2]], msg[schedule[3]]);
        g(state, 2, 6, 10, 14, msg[schedule[4]], msg[schedule[5]]);
        g(state, 3, 7, 11, 15, msg[schedule[6]], msg[schedule[7]]);
        g(state, 0, 5, 10, 15, msg[schedule[8]], msg[schedule[9]]);
        g(state, 1, 6, 11, 12, msg[schedule[10]], msg[schedule[11]]);
        g(state, 2, 7, 8, 13, msg[schedule[12]], msg[schedule[13]]);
        g(state, 3, 4, 9, 14, msg[schedule[14]], msg[schedule[15]]);
    }

    private static int unpackInt(byte[] buf, int off) {
        return (buf[off] & 255) | ((buf[off + 1] & 255) << 8) | ((buf[off + 2] & 255) << 16) | ((buf[off + 3] & 255) << 24);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int[] unpackInts(byte[] buf, int nrInts) {
        int[] values = new int[nrInts];
        int i = 0;
        int off = 0;
        while (i < nrInts) {
            values[i] = unpackInt(buf, off);
            i++;
            off += 4;
        }
        return values;
    }

    private Blake3(int[] key, int flags) {
        this.engineState = new EngineState(key, flags);
    }

    public Blake3 doFinalize(byte[] out) {
        return doFinalize(out, 0, out.length);
    }

    public Blake3 doFinalize(byte[] out, int offset, int length) {
        checkBufferArgs(out, offset, length);
        this.engineState.outputHash(out, offset, length);
        return this;
    }

    public byte[] doFinalize(int nrBytes) {
        if (nrBytes < 0) {
            throw new IllegalArgumentException("Requested bytes must be non-negative");
        }
        byte[] hash = new byte[nrBytes];
        doFinalize(hash);
        return hash;
    }

    public Blake3 reset() {
        this.engineState.reset();
        return this;
    }

    public Blake3 update(byte[] in) {
        return update(in, 0, in.length);
    }

    public Blake3 update(byte[] in, int offset, int length) {
        checkBufferArgs(in, offset, length);
        this.engineState.inputData(in, offset, length);
        return this;
    }
}
