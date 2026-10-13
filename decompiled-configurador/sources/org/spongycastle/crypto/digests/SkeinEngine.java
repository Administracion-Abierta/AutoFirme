package org.spongycastle.crypto.digests;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.spongycastle.asn1.cmc.BodyPartID;
import org.spongycastle.crypto.OutputLengthException;
import org.spongycastle.crypto.engines.ThreefishEngine;
import org.spongycastle.crypto.params.SkeinParameters;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Memoable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/digests/SkeinEngine.class */
public class SkeinEngine implements Memoable {
    public static final int SKEIN_256 = 256;
    public static final int SKEIN_512 = 512;
    public static final int SKEIN_1024 = 1024;
    private static final int PARAM_TYPE_KEY = 0;
    private static final int PARAM_TYPE_CONFIG = 4;
    private static final int PARAM_TYPE_MESSAGE = 48;
    private static final int PARAM_TYPE_OUTPUT = 63;
    private static final Hashtable INITIAL_STATES = new Hashtable();
    final ThreefishEngine threefish;
    private final int outputSizeBytes;
    long[] chain;
    private long[] initialState;
    private byte[] key;
    private Parameter[] preMessageParameters;
    private Parameter[] postMessageParameters;
    private final UBI ubi;
    private final byte[] singleByte;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/digests/SkeinEngine$Configuration.class */
    private static class Configuration {
        private byte[] bytes = new byte[32];

        public Configuration(long outputSizeBits) {
            this.bytes[0] = 83;
            this.bytes[1] = 72;
            this.bytes[2] = 65;
            this.bytes[3] = 51;
            this.bytes[4] = 1;
            this.bytes[5] = 0;
            ThreefishEngine.wordToBytes(outputSizeBits, this.bytes, 8);
        }

        public byte[] getBytes() {
            return this.bytes;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/digests/SkeinEngine$Parameter.class */
    public static class Parameter {
        private int type;
        private byte[] value;

        public Parameter(int type, byte[] value) {
            this.type = type;
            this.value = value;
        }

        public int getType() {
            return this.type;
        }

        public byte[] getValue() {
            return this.value;
        }
    }

    static {
        initialState(256, 128, new long[]{-2228972824489528736L, -8629553674646093540L, 1155188648486244218L, -3677226592081559102L});
        initialState(256, CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256, new long[]{1450197650740764312L, 3081844928540042640L, -3136097061834271170L, 3301952811952417661L});
        initialState(256, 224, new long[]{-4176654842910610933L, -8688192972455077604L, -7364642305011795836L, 4056579644589979102L});
        initialState(256, 256, new long[]{-243853671043386295L, 3443677322885453875L, -5531612722399640561L, 7662005193972177513L});
        initialState(512, 128, new long[]{-6288014694233956526L, 2204638249859346602L, 3502419045458743507L, -4829063503441264548L, 983504137758028059L, 1880512238245786339L, -6715892782214108542L, 7602827311880509485L});
        initialState(512, CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256, new long[]{2934123928682216849L, -4399710721982728305L, 1684584802963255058L, 5744138295201861711L, 2444857010922934358L, -2807833639722848072L, -5121587834665610502L, 118355523173251694L});
        initialState(512, 224, new long[]{-3688341020067007964L, -3772225436291745297L, -8300862168937575580L, 4146387520469897396L, 1106145742801415120L, 7455425944880474941L, -7351063101234211863L, -7048981346965512457L});
        initialState(512, 384, new long[]{-6631894876634615969L, -5692838220127733084L, -7099962856338682626L, -2911352911530754598L, 2000907093792408677L, 9140007292425499655L, 6093301768906360022L, 2769176472213098488L});
        initialState(512, 512, new long[]{5261240102383538638L, 978932832955457283L, -8083517948103779378L, -7339365279355032399L, 6752626034097301424L, -1531723821829733388L, -7417126464950782685L, -5901786942805128141L});
    }

    private static void initialState(int blockSize, int outputSize, long[] state) {
        INITIAL_STATES.put(variantIdentifier(blockSize / 8, outputSize / 8), state);
    }

    private static Integer variantIdentifier(int blockSizeBytes, int outputSizeBytes) {
        return new Integer((outputSizeBytes << 16) | blockSizeBytes);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/digests/SkeinEngine$UbiTweak.class */
    private static class UbiTweak {
        private static final long LOW_RANGE = 9223372034707292160L;
        private static final long T1_FINAL = Long.MIN_VALUE;
        private static final long T1_FIRST = 4611686018427387904L;
        private long[] tweak = new long[2];
        private boolean extendedPosition;

        public UbiTweak() {
            reset();
        }

        public void reset(UbiTweak tweak) {
            this.tweak = Arrays.clone(tweak.tweak, this.tweak);
            this.extendedPosition = tweak.extendedPosition;
        }

        public void reset() {
            this.tweak[0] = 0;
            this.tweak[1] = 0;
            this.extendedPosition = false;
            setFirst(true);
        }

        public void setType(int type) {
            this.tweak[1] = (this.tweak[1] & (-274877906944L)) | ((((long) type) & 63) << 56);
        }

        public int getType() {
            return (int) ((this.tweak[1] >>> 56) & 63);
        }

        public void setFirst(boolean first) {
            if (first) {
                long[] jArr = this.tweak;
                jArr[1] = jArr[1] | T1_FIRST;
            } else {
                long[] jArr2 = this.tweak;
                jArr2[1] = jArr2[1] & (-4611686018427387905L);
            }
        }

        public boolean isFirst() {
            return (this.tweak[1] & T1_FIRST) != 0;
        }

        public void setFinal(boolean last) {
            if (last) {
                long[] jArr = this.tweak;
                jArr[1] = jArr[1] | T1_FINAL;
            } else {
                long[] jArr2 = this.tweak;
                jArr2[1] = jArr2[1] & Long.MAX_VALUE;
            }
        }

        public boolean isFinal() {
            return (this.tweak[1] & T1_FINAL) != 0;
        }

        public void advancePosition(int advance) {
            if (this.extendedPosition) {
                long[] parts = new long[3];
                parts[0] = this.tweak[0] & BodyPartID.bodyIdMax;
                parts[1] = (this.tweak[0] >>> 32) & BodyPartID.bodyIdMax;
                parts[2] = this.tweak[1] & BodyPartID.bodyIdMax;
                long carry = advance;
                for (int i = 0; i < parts.length; i++) {
                    long carry2 = carry + parts[i];
                    parts[i] = carry2;
                    carry = carry2 >>> 32;
                }
                this.tweak[0] = ((parts[1] & BodyPartID.bodyIdMax) << 32) | (parts[0] & BodyPartID.bodyIdMax);
                this.tweak[1] = (this.tweak[1] & (-4294967296L)) | (parts[2] & BodyPartID.bodyIdMax);
                return;
            }
            long position = this.tweak[0] + ((long) advance);
            this.tweak[0] = position;
            if (position > LOW_RANGE) {
                this.extendedPosition = true;
            }
        }

        public long[] getWords() {
            return this.tweak;
        }

        public String toString() {
            return getType() + " first: " + isFirst() + ", final: " + isFinal();
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/digests/SkeinEngine$UBI.class */
    private class UBI {
        private final UbiTweak tweak = new UbiTweak();
        private byte[] currentBlock;
        private int currentOffset;
        private long[] message;

        public UBI(int blockSize) {
            this.currentBlock = new byte[blockSize];
            this.message = new long[this.currentBlock.length / 8];
        }

        public void reset(UBI ubi) {
            this.currentBlock = Arrays.clone(ubi.currentBlock, this.currentBlock);
            this.currentOffset = ubi.currentOffset;
            this.message = Arrays.clone(ubi.message, this.message);
            this.tweak.reset(ubi.tweak);
        }

        public void reset(int type) {
            this.tweak.reset();
            this.tweak.setType(type);
            this.currentOffset = 0;
        }

        public void update(byte[] value, int offset, int len, long[] output) {
            int copied = 0;
            while (len > copied) {
                if (this.currentOffset == this.currentBlock.length) {
                    processBlock(output);
                    this.tweak.setFirst(false);
                    this.currentOffset = 0;
                }
                int toCopy = Math.min(len - copied, this.currentBlock.length - this.currentOffset);
                System.arraycopy(value, offset + copied, this.currentBlock, this.currentOffset, toCopy);
                copied += toCopy;
                this.currentOffset += toCopy;
                this.tweak.advancePosition(toCopy);
            }
        }

        private void processBlock(long[] output) {
            SkeinEngine.this.threefish.init(true, SkeinEngine.this.chain, this.tweak.getWords());
            for (int i = 0; i < this.message.length; i++) {
                this.message[i] = ThreefishEngine.bytesToWord(this.currentBlock, i * 8);
            }
            SkeinEngine.this.threefish.processBlock(this.message, output);
            for (int i2 = 0; i2 < output.length; i2++) {
                int i3 = i2;
                output[i3] = output[i3] ^ this.message[i2];
            }
        }

        public void doFinal(long[] output) {
            for (int i = this.currentOffset; i < this.currentBlock.length; i++) {
                this.currentBlock[i] = 0;
            }
            this.tweak.setFinal(true);
            processBlock(output);
        }
    }

    public SkeinEngine(int blockSizeBits, int outputSizeBits) {
        this.singleByte = new byte[1];
        if (outputSizeBits % 8 != 0) {
            throw new IllegalArgumentException("Output size must be a multiple of 8 bits. :" + outputSizeBits);
        }
        this.outputSizeBytes = outputSizeBits / 8;
        this.threefish = new ThreefishEngine(blockSizeBits);
        this.ubi = new UBI(this.threefish.getBlockSize());
    }

    public SkeinEngine(SkeinEngine engine) {
        this(engine.getBlockSize() * 8, engine.getOutputSize() * 8);
        copyIn(engine);
    }

    private void copyIn(SkeinEngine engine) {
        this.ubi.reset(engine.ubi);
        this.chain = Arrays.clone(engine.chain, this.chain);
        this.initialState = Arrays.clone(engine.initialState, this.initialState);
        this.key = Arrays.clone(engine.key, this.key);
        this.preMessageParameters = clone(engine.preMessageParameters, this.preMessageParameters);
        this.postMessageParameters = clone(engine.postMessageParameters, this.postMessageParameters);
    }

    private static Parameter[] clone(Parameter[] data, Parameter[] existing) {
        if (data == null) {
            return null;
        }
        if (existing == null || existing.length != data.length) {
            existing = new Parameter[data.length];
        }
        System.arraycopy(data, 0, existing, 0, existing.length);
        return existing;
    }

    @Override // org.spongycastle.util.Memoable
    public Memoable copy() {
        return new SkeinEngine(this);
    }

    @Override // org.spongycastle.util.Memoable
    public void reset(Memoable other) {
        SkeinEngine s = (SkeinEngine) other;
        if (getBlockSize() != s.getBlockSize() || this.outputSizeBytes != s.outputSizeBytes) {
            throw new IllegalArgumentException("Incompatible parameters in provided SkeinEngine.");
        }
        copyIn(s);
    }

    public int getOutputSize() {
        return this.outputSizeBytes;
    }

    public int getBlockSize() {
        return this.threefish.getBlockSize();
    }

    public void init(SkeinParameters params) {
        this.chain = null;
        this.key = null;
        this.preMessageParameters = null;
        this.postMessageParameters = null;
        if (params != null) {
            byte[] key = params.getKey();
            if (key.length < 16) {
                throw new IllegalArgumentException("Skein key must be at least 128 bits.");
            }
            initParams(params.getParameters());
        }
        createInitialState();
        ubiInit(48);
    }

    private void initParams(Hashtable parameters) {
        Enumeration keys = parameters.keys();
        Vector pre = new Vector();
        Vector post = new Vector();
        while (keys.hasMoreElements()) {
            Integer type = (Integer) keys.nextElement();
            byte[] value = (byte[]) parameters.get(type);
            if (type.intValue() == 0) {
                this.key = value;
            } else if (type.intValue() < 48) {
                pre.addElement(new Parameter(type.intValue(), value));
            } else {
                post.addElement(new Parameter(type.intValue(), value));
            }
        }
        this.preMessageParameters = new Parameter[pre.size()];
        pre.copyInto(this.preMessageParameters);
        sort(this.preMessageParameters);
        this.postMessageParameters = new Parameter[post.size()];
        post.copyInto(this.postMessageParameters);
        sort(this.postMessageParameters);
    }

    private static void sort(Parameter[] params) {
        int hole;
        if (params == null) {
            return;
        }
        for (int i = 1; i < params.length; i++) {
            Parameter param = params[i];
            int i2 = i;
            while (true) {
                hole = i2;
                if (hole <= 0 || param.getType() >= params[hole - 1].getType()) {
                    break;
                }
                params[hole] = params[hole - 1];
                i2 = hole - 1;
            }
            params[hole] = param;
        }
    }

    private void createInitialState() {
        long[] precalc = (long[]) INITIAL_STATES.get(variantIdentifier(getBlockSize(), getOutputSize()));
        if (this.key == null && precalc != null) {
            this.chain = Arrays.clone(precalc);
        } else {
            this.chain = new long[getBlockSize() / 8];
            if (this.key != null) {
                ubiComplete(0, this.key);
            }
            ubiComplete(4, new Configuration(this.outputSizeBytes * 8).getBytes());
        }
        if (this.preMessageParameters != null) {
            for (int i = 0; i < this.preMessageParameters.length; i++) {
                Parameter param = this.preMessageParameters[i];
                ubiComplete(param.getType(), param.getValue());
            }
        }
        this.initialState = Arrays.clone(this.chain);
    }

    public void reset() {
        System.arraycopy(this.initialState, 0, this.chain, 0, this.chain.length);
        ubiInit(48);
    }

    private void ubiComplete(int type, byte[] value) {
        ubiInit(type);
        this.ubi.update(value, 0, value.length, this.chain);
        ubiFinal();
    }

    private void ubiInit(int type) {
        this.ubi.reset(type);
    }

    private void ubiFinal() {
        this.ubi.doFinal(this.chain);
    }

    private void checkInitialised() {
        if (this.ubi == null) {
            throw new IllegalArgumentException("Skein engine is not initialised.");
        }
    }

    public void update(byte in) {
        this.singleByte[0] = in;
        update(this.singleByte, 0, 1);
    }

    public void update(byte[] in, int inOff, int len) {
        checkInitialised();
        this.ubi.update(in, inOff, len, this.chain);
    }

    public int doFinal(byte[] out, int outOff) {
        checkInitialised();
        if (out.length < outOff + this.outputSizeBytes) {
            throw new OutputLengthException("Output buffer is too short to hold output");
        }
        ubiFinal();
        if (this.postMessageParameters != null) {
            for (int i = 0; i < this.postMessageParameters.length; i++) {
                Parameter param = this.postMessageParameters[i];
                ubiComplete(param.getType(), param.getValue());
            }
        }
        int blockSize = getBlockSize();
        int blocksRequired = ((this.outputSizeBytes + blockSize) - 1) / blockSize;
        for (int i2 = 0; i2 < blocksRequired; i2++) {
            int toWrite = Math.min(blockSize, this.outputSizeBytes - (i2 * blockSize));
            output(i2, out, outOff + (i2 * blockSize), toWrite);
        }
        reset();
        return this.outputSizeBytes;
    }

    private void output(long outputSequence, byte[] out, int outOff, int outputBytes) {
        byte[] currentBytes = new byte[8];
        ThreefishEngine.wordToBytes(outputSequence, currentBytes, 0);
        long[] outputWords = new long[this.chain.length];
        ubiInit(63);
        this.ubi.update(currentBytes, 0, currentBytes.length, outputWords);
        this.ubi.doFinal(outputWords);
        int wordsRequired = ((outputBytes + 8) - 1) / 8;
        for (int i = 0; i < wordsRequired; i++) {
            int toWrite = Math.min(8, outputBytes - (i * 8));
            if (toWrite == 8) {
                ThreefishEngine.wordToBytes(outputWords[i], out, outOff + (i * 8));
            } else {
                ThreefishEngine.wordToBytes(outputWords[i], currentBytes, 0);
                System.arraycopy(currentBytes, 0, out, outOff + (i * 8), toWrite);
            }
        }
    }
}
