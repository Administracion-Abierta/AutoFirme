package org.apache.commons.codec.binary;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.CodecPolicy;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.spongycastle.asn1.cmc.BodyPartID;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/binary/BaseNCodec.class */
public abstract class BaseNCodec implements BinaryEncoder, BinaryDecoder {
    static final int EOF = -1;
    public static final int MIME_CHUNK_SIZE = 76;
    public static final int PEM_CHUNK_SIZE = 64;
    private static final int DEFAULT_BUFFER_RESIZE_FACTOR = 2;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int MAX_BUFFER_SIZE = 2147483639;
    protected static final int MASK_8BITS = 255;
    protected static final byte PAD_DEFAULT = 61;
    protected static final CodecPolicy DECODING_POLICY_DEFAULT = CodecPolicy.LENIENT;
    static final byte[] CHUNK_SEPARATOR = {13, 10};

    @Deprecated
    protected final byte PAD = 61;
    protected final byte pad;
    private final int unencodedBlockSize;
    private final int encodedBlockSize;
    protected final int lineLength;
    private final int chunkSeparatorLength;
    private final CodecPolicy decodingPolicy;

    abstract void decode(byte[] bArr, int i, int i2, Context context);

    abstract void encode(byte[] bArr, int i, int i2, Context context);

    protected abstract boolean isInAlphabet(byte b);

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/binary/BaseNCodec$AbstractBuilder.class */
    public static abstract class AbstractBuilder<T, B extends AbstractBuilder<T, B>> implements Supplier<T> {
        private int lineLength;
        private final byte[] defaultEncodeTable;
        private byte[] encodeTable;
        private CodecPolicy decodingPolicy = BaseNCodec.DECODING_POLICY_DEFAULT;
        private byte[] lineSeparator = BaseNCodec.CHUNK_SEPARATOR;
        private byte padding = 61;

        AbstractBuilder(byte[] defaultEncodeTable) {
            this.defaultEncodeTable = defaultEncodeTable;
            this.encodeTable = defaultEncodeTable;
        }

        B asThis() {
            return this;
        }

        CodecPolicy getDecodingPolicy() {
            return this.decodingPolicy;
        }

        byte[] getEncodeTable() {
            return this.encodeTable;
        }

        int getLineLength() {
            return this.lineLength;
        }

        byte[] getLineSeparator() {
            return this.lineSeparator;
        }

        byte getPadding() {
            return this.padding;
        }

        public B setDecodingPolicy(CodecPolicy codecPolicy) {
            this.decodingPolicy = codecPolicy != null ? codecPolicy : BaseNCodec.DECODING_POLICY_DEFAULT;
            return (B) asThis();
        }

        public B setEncodeTable(byte... bArr) {
            this.encodeTable = bArr != null ? bArr : this.defaultEncodeTable;
            return (B) asThis();
        }

        public B setLineLength(int i) {
            this.lineLength = Math.max(0, i);
            return (B) asThis();
        }

        public B setLineSeparator(byte... bArr) {
            this.lineSeparator = bArr != null ? bArr : BaseNCodec.CHUNK_SEPARATOR;
            return (B) asThis();
        }

        public B setPadding(byte b) {
            this.padding = b;
            return (B) asThis();
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/commons/codec/binary/BaseNCodec$Context.class */
    static class Context {
        int ibitWorkArea;
        long lbitWorkArea;
        byte[] buffer;
        int pos;
        int readPos;
        boolean eof;
        int currentLinePos;
        int modulus;

        Context() {
        }

        public String toString() {
            return String.format("%s[buffer=%s, currentLinePos=%s, eof=%s, ibitWorkArea=%s, lbitWorkArea=%s, modulus=%s, pos=%s, readPos=%s]", getClass().getSimpleName(), Arrays.toString(this.buffer), Integer.valueOf(this.currentLinePos), Boolean.valueOf(this.eof), Integer.valueOf(this.ibitWorkArea), Long.valueOf(this.lbitWorkArea), Integer.valueOf(this.modulus), Integer.valueOf(this.pos), Integer.valueOf(this.readPos));
        }
    }

    private static int createPositiveCapacity(int minCapacity) {
        if (minCapacity < 0) {
            throw new OutOfMemoryError("Unable to allocate array size: " + (((long) minCapacity) & BodyPartID.bodyIdMax));
        }
        return Math.max(minCapacity, MAX_BUFFER_SIZE);
    }

    public static byte[] getChunkSeparator() {
        return (byte[]) CHUNK_SEPARATOR.clone();
    }

    @Deprecated
    protected static boolean isWhiteSpace(byte byteToCheck) {
        return Character.isWhitespace(byteToCheck);
    }

    private static byte[] resizeBuffer(Context context, int minCapacity) {
        int oldCapacity = context.buffer.length;
        int newCapacity = oldCapacity * 2;
        if (Integer.compareUnsigned(newCapacity, minCapacity) < 0) {
            newCapacity = minCapacity;
        }
        if (Integer.compareUnsigned(newCapacity, MAX_BUFFER_SIZE) > 0) {
            newCapacity = createPositiveCapacity(minCapacity);
        }
        byte[] b = Arrays.copyOf(context.buffer, newCapacity);
        context.buffer = b;
        return b;
    }

    static int toLength(byte[] array) {
        if (array == null) {
            return 0;
        }
        return array.length;
    }

    protected BaseNCodec(int unencodedBlockSize, int encodedBlockSize, int lineLength, int chunkSeparatorLength) {
        this(unencodedBlockSize, encodedBlockSize, lineLength, chunkSeparatorLength, (byte) 61);
    }

    protected BaseNCodec(int unencodedBlockSize, int encodedBlockSize, int lineLength, int chunkSeparatorLength, byte pad) {
        this(unencodedBlockSize, encodedBlockSize, lineLength, chunkSeparatorLength, pad, DECODING_POLICY_DEFAULT);
    }

    protected BaseNCodec(int unencodedBlockSize, int encodedBlockSize, int lineLength, int chunkSeparatorLength, byte pad, CodecPolicy decodingPolicy) {
        this.PAD = (byte) 61;
        this.unencodedBlockSize = unencodedBlockSize;
        this.encodedBlockSize = encodedBlockSize;
        boolean useChunking = lineLength > 0 && chunkSeparatorLength > 0;
        this.lineLength = useChunking ? (lineLength / encodedBlockSize) * encodedBlockSize : 0;
        this.chunkSeparatorLength = chunkSeparatorLength;
        this.pad = pad;
        this.decodingPolicy = (CodecPolicy) Objects.requireNonNull(decodingPolicy, "codecPolicy");
    }

    int available(Context context) {
        if (hasData(context)) {
            return context.pos - context.readPos;
        }
        return 0;
    }

    protected boolean containsAlphabetOrPad(byte[] arrayOctet) {
        if (arrayOctet != null) {
            for (byte element : arrayOctet) {
                if (this.pad == element || isInAlphabet(element)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override // org.apache.commons.codec.BinaryDecoder
    public byte[] decode(byte[] pArray) {
        if (BinaryCodec.isEmpty(pArray)) {
            return pArray;
        }
        Context context = new Context();
        decode(pArray, 0, pArray.length, context);
        decode(pArray, 0, -1, context);
        byte[] result = new byte[context.pos];
        readResults(result, 0, result.length, context);
        return result;
    }

    @Override // org.apache.commons.codec.Decoder
    public Object decode(Object obj) throws DecoderException {
        if (obj instanceof byte[]) {
            return decode((byte[]) obj);
        }
        if (obj instanceof String) {
            return decode((String) obj);
        }
        throw new DecoderException("Parameter supplied to Base-N decode is not a byte[] or a String");
    }

    public byte[] decode(String pArray) {
        return decode(StringUtils.getBytesUtf8(pArray));
    }

    @Override // org.apache.commons.codec.BinaryEncoder
    public byte[] encode(byte[] pArray) {
        if (BinaryCodec.isEmpty(pArray)) {
            return pArray;
        }
        return encode(pArray, 0, pArray.length);
    }

    public byte[] encode(byte[] pArray, int offset, int length) {
        if (BinaryCodec.isEmpty(pArray)) {
            return pArray;
        }
        Context context = new Context();
        encode(pArray, offset, length, context);
        encode(pArray, offset, -1, context);
        byte[] buf = new byte[context.pos - context.readPos];
        readResults(buf, 0, buf.length, context);
        return buf;
    }

    @Override // org.apache.commons.codec.Encoder
    public Object encode(Object obj) throws EncoderException {
        if (!(obj instanceof byte[])) {
            throw new EncoderException("Parameter supplied to Base-N encode is not a byte[]");
        }
        return encode((byte[]) obj);
    }

    public String encodeAsString(byte[] pArray) {
        return StringUtils.newStringUtf8(encode(pArray));
    }

    public String encodeToString(byte[] pArray) {
        return StringUtils.newStringUtf8(encode(pArray));
    }

    protected byte[] ensureBufferSize(int size, Context context) {
        if (context.buffer == null) {
            context.buffer = new byte[Math.max(size, getDefaultBufferSize())];
            context.pos = 0;
            context.readPos = 0;
        } else if ((context.pos + size) - context.buffer.length > 0) {
            return resizeBuffer(context, context.pos + size);
        }
        return context.buffer;
    }

    public CodecPolicy getCodecPolicy() {
        return this.decodingPolicy;
    }

    protected int getDefaultBufferSize() {
        return 8192;
    }

    public long getEncodedLength(byte[] pArray) {
        long len = ((long) (((pArray.length + this.unencodedBlockSize) - 1) / this.unencodedBlockSize)) * ((long) this.encodedBlockSize);
        if (this.lineLength > 0) {
            len += (((len + ((long) this.lineLength)) - 1) / ((long) this.lineLength)) * ((long) this.chunkSeparatorLength);
        }
        return len;
    }

    boolean hasData(Context context) {
        return context.pos > context.readPos;
    }

    public boolean isInAlphabet(byte[] arrayOctet, boolean allowWSPad) {
        for (byte octet : arrayOctet) {
            if (!isInAlphabet(octet)) {
                if (!allowWSPad) {
                    return false;
                }
                if (octet != this.pad && !Character.isWhitespace(octet)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isInAlphabet(String basen) {
        return isInAlphabet(StringUtils.getBytesUtf8(basen), true);
    }

    public boolean isStrictDecoding() {
        return this.decodingPolicy == CodecPolicy.STRICT;
    }

    int readResults(byte[] b, int bPos, int bAvail, Context context) {
        if (!hasData(context)) {
            return context.eof ? -1 : 0;
        }
        int len = Math.min(available(context), bAvail);
        System.arraycopy(context.buffer, context.readPos, b, bPos, len);
        context.readPos += len;
        if (!hasData(context)) {
            context.readPos = 0;
            context.pos = 0;
        }
        return len;
    }
}
