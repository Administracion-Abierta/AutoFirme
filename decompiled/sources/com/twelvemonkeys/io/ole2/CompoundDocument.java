package com.twelvemonkeys.io.ole2;

import com.twelvemonkeys.io.FileUtil;
import com.twelvemonkeys.io.LittleEndianDataInputStream;
import com.twelvemonkeys.io.LittleEndianRandomAccessFile;
import com.twelvemonkeys.io.MemoryCacheSeekableStream;
import com.twelvemonkeys.io.Seekable;
import com.twelvemonkeys.io.SeekableInputStream;
import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.lang.Validate;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import javax.imageio.stream.ImageInputStream;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/ole2/CompoundDocument.class */
public final class CompoundDocument implements AutoCloseable {
    static final byte[] MAGIC = {-48, -49, 17, -32, -95, -79, 26, -31};
    private static final int FREE_SID = -1;
    private static final int END_OF_CHAIN_SID = -2;
    private static final int SAT_SECTOR_SID = -3;
    private static final int MSAT_SECTOR_SID = -4;
    public static final int HEADER_SIZE = 512;
    public static final long EPOCH_OFFSET = -11644477200000L;
    private final DataInput input;
    private UUID uUID;
    private int sectorSize;
    private int shortSectorSize;
    private int directorySId;
    private int minStreamSize;
    private int shortSATSId;
    private int shortSATSize;
    private int[] masterSAT;
    private int[] SAT;
    private int[] shortSAT;
    private Entry rootEntry;
    private SIdChain shortStreamSIdChain;
    private SIdChain directorySIdChain;

    public CompoundDocument(File file) throws IOException {
        this.input = new LittleEndianRandomAccessFile(FileUtil.resolve(file), PDPageLabelRange.STYLE_ROMAN_LOWER);
        readHeader();
    }

    public CompoundDocument(InputStream inputStream) throws IOException {
        this((SeekableInputStream) new MemoryCacheSeekableStream(inputStream));
    }

    CompoundDocument(SeekableInputStream seekableInputStream) throws IOException {
        this.input = new SeekableLittleEndianDataInputStream(seekableInputStream);
        readHeader();
    }

    public CompoundDocument(ImageInputStream imageInputStream) throws IOException {
        this.input = (DataInput) Validate.notNull(imageInputStream, "input");
        imageInputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        readHeader();
    }

    @Override // java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.input instanceof RandomAccessFile) {
            ((RandomAccessFile) this.input).close();
        } else if (this.input instanceof LittleEndianRandomAccessFile) {
            ((LittleEndianRandomAccessFile) this.input).close();
        }
    }

    public static boolean canRead(DataInput dataInput) {
        return canRead(dataInput, true);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static boolean canRead(DataInput dataInput, boolean z) {
        long filePointer = -1;
        if (z) {
            try {
                if ((dataInput instanceof InputStream) && ((InputStream) dataInput).markSupported()) {
                    ((InputStream) dataInput).mark(8);
                } else if (dataInput instanceof ImageInputStream) {
                    ((ImageInputStream) dataInput).mark();
                } else if (dataInput instanceof RandomAccessFile) {
                    filePointer = ((RandomAccessFile) dataInput).getFilePointer();
                } else {
                    if (!(dataInput instanceof LittleEndianRandomAccessFile)) {
                        return false;
                    }
                    filePointer = ((LittleEndianRandomAccessFile) dataInput).getFilePointer();
                }
            } catch (IOException e) {
                return false;
            }
        }
        try {
            byte[] bArr = new byte[8];
            dataInput.readFully(bArr);
            boolean zEquals = Arrays.equals(bArr, MAGIC);
            if (z) {
                try {
                    if ((dataInput instanceof InputStream) && ((InputStream) dataInput).markSupported()) {
                        ((InputStream) dataInput).reset();
                    } else if (dataInput instanceof ImageInputStream) {
                        ((ImageInputStream) dataInput).reset();
                    } else if (dataInput instanceof RandomAccessFile) {
                        ((RandomAccessFile) dataInput).seek(filePointer);
                    } else if (dataInput instanceof LittleEndianRandomAccessFile) {
                        ((LittleEndianRandomAccessFile) dataInput).seek(filePointer);
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            return zEquals;
        } catch (IOException e3) {
            if (!z) {
                return false;
            }
            try {
                if ((dataInput instanceof InputStream) && ((InputStream) dataInput).markSupported()) {
                    ((InputStream) dataInput).reset();
                } else if (dataInput instanceof ImageInputStream) {
                    ((ImageInputStream) dataInput).reset();
                } else if (dataInput instanceof RandomAccessFile) {
                    ((RandomAccessFile) dataInput).seek(filePointer);
                } else if (dataInput instanceof LittleEndianRandomAccessFile) {
                    ((LittleEndianRandomAccessFile) dataInput).seek(filePointer);
                }
                return false;
            } catch (IOException e4) {
                e4.printStackTrace();
                return false;
            }
        } catch (Throwable th) {
            if (z) {
                try {
                    if ((dataInput instanceof InputStream) && ((InputStream) dataInput).markSupported()) {
                        ((InputStream) dataInput).reset();
                    } else if (dataInput instanceof ImageInputStream) {
                        ((ImageInputStream) dataInput).reset();
                    } else if (dataInput instanceof RandomAccessFile) {
                        ((RandomAccessFile) dataInput).seek(filePointer);
                    } else if (dataInput instanceof LittleEndianRandomAccessFile) {
                        ((LittleEndianRandomAccessFile) dataInput).seek(filePointer);
                    }
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
            throw th;
        }
    }

    private void readHeader() throws IOException {
        if (this.masterSAT != null) {
            return;
        }
        if (!canRead(this.input, false)) {
            throw new CorruptDocumentException("Not an OLE 2 Compound Document");
        }
        this.uUID = new UUID(this.input.readLong(), this.input.readLong());
        this.input.readUnsignedShort();
        this.input.readUnsignedShort();
        int unsignedShort = this.input.readUnsignedShort();
        if (unsignedShort == 65535) {
            throw new CorruptDocumentException("Cannot read big endian OLE 2 Compound Documents");
        }
        if (unsignedShort != 65534) {
            throw new CorruptDocumentException(String.format("Unknown byte order marker: 0x%04x, expected 0xfffe or 0xffff", Integer.valueOf(unsignedShort)));
        }
        this.sectorSize = 1 << this.input.readUnsignedShort();
        this.shortSectorSize = 1 << this.input.readUnsignedShort();
        if (skipBytesFully(10) != 10) {
            throw new CorruptDocumentException();
        }
        int i = this.input.readInt();
        this.directorySId = this.input.readInt();
        if (skipBytesFully(4) != 4) {
            throw new CorruptDocumentException();
        }
        this.minStreamSize = this.input.readInt();
        this.shortSATSId = this.input.readInt();
        this.shortSATSize = this.input.readInt();
        int i2 = this.input.readInt();
        int i3 = this.input.readInt();
        this.masterSAT = new int[i];
        int iMin = Math.min(i, 109);
        for (int i4 = 0; i4 < iMin; i4++) {
            this.masterSAT[i4] = this.input.readInt();
        }
        if (i2 == -2) {
            int i5 = 436 - (i * 4);
            if (skipBytesFully(i5) != i5) {
                throw new CorruptDocumentException();
            }
            return;
        }
        seekToSId(i2, -1L);
        int i6 = iMin;
        for (int i7 = 0; i7 < i3; i7++) {
            for (int i8 = 0; i8 < 127; i8++) {
                int i9 = this.input.readInt();
                switch (i9) {
                    case -1:
                        break;
                    default:
                        int i10 = i6;
                        i6++;
                        this.masterSAT[i10] = i9;
                        break;
                }
            }
            int i11 = this.input.readInt();
            if (i11 != -2) {
                seekToSId(i11, -1L);
            } else {
                return;
            }
        }
    }

    private int skipBytesFully(int i) throws IOException {
        int i2;
        int iSkipBytes;
        int i3 = i;
        while (true) {
            i2 = i3;
            if (i2 <= 0 || (iSkipBytes = this.input.skipBytes(i)) <= 0) {
                break;
            }
            i3 = i2 - iSkipBytes;
        }
        return i - i2;
    }

    private void readSAT() throws IOException {
        if (this.SAT != null) {
            return;
        }
        int i = this.sectorSize / 4;
        this.SAT = new int[this.masterSAT.length * i];
        for (int i2 = 0; i2 < this.masterSAT.length; i2++) {
            seekToSId(this.masterSAT[i2], -1L);
            for (int i3 = 0; i3 < i; i3++) {
                this.SAT[i3 + (i2 * i)] = this.input.readInt();
            }
        }
        SIdChain sIdChain = getSIdChain(this.shortSATSId, -1L);
        this.shortSAT = new int[this.shortSATSize * i];
        for (int i4 = 0; i4 < this.shortSATSize; i4++) {
            seekToSId(sIdChain.get(i4), -1L);
            for (int i5 = 0; i5 < i; i5++) {
                this.shortSAT[i5 + (i4 * i)] = this.input.readInt();
            }
        }
    }

    private SIdChain getSIdChain(int i, long j) throws IOException {
        SIdChain sIdChain = new SIdChain();
        int[] iArr = isShortStream(j) ? this.shortSAT : this.SAT;
        int i2 = i;
        while (true) {
            int i3 = i2;
            if (i3 == -2 || i3 == -1) {
                break;
            }
            sIdChain.addSID(i3);
            i2 = iArr[i3];
        }
        return sIdChain;
    }

    private boolean isShortStream(long j) {
        return j != -1 && j < ((long) this.minStreamSize);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void seekToSId(int i, long j) throws IOException {
        long j2;
        if (isShortStream(j)) {
            Entry rootEntry = getRootEntry();
            if (this.shortStreamSIdChain == null) {
                this.shortStreamSIdChain = getSIdChain(rootEntry.startSId, rootEntry.streamSize);
            }
            int i2 = this.sectorSize / this.shortSectorSize;
            int i3 = i / i2;
            j2 = 512 + (((long) this.shortStreamSIdChain.get(i3)) * ((long) this.sectorSize)) + (((long) (i - (i3 * i2))) * ((long) this.shortSectorSize));
        } else {
            j2 = 512 + (((long) i) * ((long) this.sectorSize));
        }
        if (this.input instanceof LittleEndianRandomAccessFile) {
            ((LittleEndianRandomAccessFile) this.input).seek(j2);
        } else if (this.input instanceof ImageInputStream) {
            this.input.seek(j2);
        } else {
            ((SeekableLittleEndianDataInputStream) this.input).seek(j2);
        }
    }

    private void seekToDId(int i) throws IOException {
        if (this.directorySIdChain == null) {
            this.directorySIdChain = getSIdChain(this.directorySId, -1L);
        }
        int i2 = this.sectorSize / 128;
        int i3 = i / i2;
        int i4 = i - (i3 * i2);
        seekToSId(this.directorySIdChain.get(i3), -1L);
        if (this.input instanceof LittleEndianRandomAccessFile) {
            LittleEndianRandomAccessFile littleEndianRandomAccessFile = (LittleEndianRandomAccessFile) this.input;
            littleEndianRandomAccessFile.seek(littleEndianRandomAccessFile.getFilePointer() + ((long) (i4 * 128)));
        } else if (this.input instanceof ImageInputStream) {
            ImageInputStream imageInputStream = this.input;
            imageInputStream.seek(imageInputStream.getStreamPosition() + ((long) (i4 * 128)));
        } else {
            SeekableLittleEndianDataInputStream seekableLittleEndianDataInputStream = (SeekableLittleEndianDataInputStream) this.input;
            seekableLittleEndianDataInputStream.seek(seekableLittleEndianDataInputStream.getStreamPosition() + ((long) (i4 * 128)));
        }
    }

    SeekableInputStream getInputStreamForSId(int i, int i2) throws IOException {
        return new MemoryCacheSeekableStream(new Stream(getSIdChain(i, i2), i2, i2 < this.minStreamSize ? this.shortSectorSize : this.sectorSize, this));
    }

    private InputStream getDirectoryStreamForDId(int i) throws IOException {
        byte[] bArr = new byte[128];
        seekToDId(i);
        this.input.readFully(bArr);
        return new ByteArrayInputStream(bArr);
    }

    Entry getEntry(int i, Entry entry) throws IOException {
        Entry entry2 = Entry.readEntry(new LittleEndianDataInputStream(getDirectoryStreamForDId(i)));
        entry2.parent = entry;
        entry2.document = this;
        return entry2;
    }

    SortedSet<Entry> getEntries(int i, Entry entry) throws IOException {
        return getEntriesRecursive(i, entry, new TreeSet());
    }

    private SortedSet<Entry> getEntriesRecursive(int i, Entry entry, SortedSet<Entry> sortedSet) throws IOException {
        Entry entry2 = getEntry(i, entry);
        if (!sortedSet.add(entry2)) {
            throw new CorruptDocumentException("Cyclic chain reference for entry: " + i);
        }
        if (entry2.prevDId != -1) {
            getEntriesRecursive(entry2.prevDId, entry, sortedSet);
        }
        if (entry2.nextDId != -1) {
            getEntriesRecursive(entry2.nextDId, entry, sortedSet);
        }
        return sortedSet;
    }

    Entry getEntry(String str) throws IOException {
        if (StringUtil.isEmpty(str) || !str.startsWith("/")) {
            throw new IllegalArgumentException("Path must be absolute, and contain a valid path: " + str);
        }
        Entry rootEntry = getRootEntry();
        if (str.equals("/")) {
            return rootEntry;
        }
        for (String str2 : StringUtil.toStringArray(str, "/")) {
            rootEntry = rootEntry.getChildEntry(str2);
            if (rootEntry == null) {
                break;
            }
        }
        return rootEntry;
    }

    public Entry getRootEntry() throws IOException {
        if (this.rootEntry == null) {
            readSAT();
            this.rootEntry = getEntry(0, null);
            if (this.rootEntry.type != 5) {
                throw new CorruptDocumentException("Invalid root storage type: " + ((int) this.rootEntry.type));
            }
        }
        return this.rootEntry;
    }

    public String toString() {
        return String.format("%s[uuid: %s, sector size: %d/%d bytes, directory SID: %d, master SAT: %s entries]", getClass().getSimpleName(), this.uUID, Integer.valueOf(this.sectorSize), Integer.valueOf(this.shortSectorSize), Integer.valueOf(this.directorySId), Integer.valueOf(this.masterSAT.length));
    }

    public static long toJavaTimeInMillis(long j) {
        if (j == 0) {
            return 0L;
        }
        return ((j >> 1) / 5000) + EPOCH_OFFSET;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/ole2/CompoundDocument$Stream.class */
    static class Stream extends InputStream {
        private final SIdChain chain;
        private final CompoundDocument document;
        private final long length;
        private long streamPos;
        private int nextSectorPos;
        private byte[] buffer;
        private int bufferPos;

        public Stream(SIdChain sIdChain, int i, int i2, CompoundDocument compoundDocument) {
            this.chain = sIdChain;
            this.length = i;
            this.buffer = new byte[i2];
            this.bufferPos = this.buffer.length;
            this.document = compoundDocument;
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            return (int) Math.min(this.buffer.length - this.bufferPos, this.length - this.streamPos);
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            if (available() <= 0 && !fillBuffer()) {
                return -1;
            }
            this.streamPos++;
            byte[] bArr = this.buffer;
            int i = this.bufferPos;
            this.bufferPos = i + 1;
            return bArr[i] & 255;
        }

        private boolean fillBuffer() throws IOException {
            if (this.streamPos < this.length && this.nextSectorPos < this.chain.length()) {
                synchronized (this.document) {
                    this.document.seekToSId(this.chain.get(this.nextSectorPos), this.length);
                    this.document.input.readFully(this.buffer);
                }
                this.nextSectorPos++;
                this.bufferPos = 0;
                return true;
            }
            return false;
        }

        @Override // java.io.InputStream
        public int read(byte[] bArr, int i, int i2) throws IOException {
            if (available() <= 0 && !fillBuffer()) {
                return -1;
            }
            int iMin = Math.min(i2, available());
            System.arraycopy(this.buffer, this.bufferPos, bArr, i, iMin);
            this.bufferPos += iMin;
            this.streamPos += (long) iMin;
            return iMin;
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.buffer = null;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/ole2/CompoundDocument$SeekableLittleEndianDataInputStream.class */
    static class SeekableLittleEndianDataInputStream extends LittleEndianDataInputStream implements Seekable {
        private final SeekableInputStream seekable;

        public SeekableLittleEndianDataInputStream(SeekableInputStream seekableInputStream) {
            super(seekableInputStream);
            this.seekable = seekableInputStream;
        }

        @Override // com.twelvemonkeys.io.Seekable
        public void seek(long j) throws IOException {
            this.seekable.seek(j);
        }

        @Override // com.twelvemonkeys.io.Seekable
        public boolean isCachedFile() {
            return this.seekable.isCachedFile();
        }

        @Override // com.twelvemonkeys.io.Seekable
        public boolean isCachedMemory() {
            return this.seekable.isCachedMemory();
        }

        @Override // com.twelvemonkeys.io.Seekable
        public boolean isCached() {
            return this.seekable.isCached();
        }

        @Override // com.twelvemonkeys.io.Seekable
        public long getStreamPosition() throws IOException {
            return this.seekable.getStreamPosition();
        }

        @Override // com.twelvemonkeys.io.Seekable
        public long getFlushedPosition() throws IOException {
            return this.seekable.getFlushedPosition();
        }

        @Override // com.twelvemonkeys.io.Seekable
        public void flushBefore(long j) throws IOException {
            this.seekable.flushBefore(j);
        }

        @Override // com.twelvemonkeys.io.Seekable
        public void flush() throws IOException {
            this.seekable.flush();
        }

        @Override // java.io.FilterInputStream, java.io.InputStream, com.twelvemonkeys.io.Seekable
        public void reset() throws IOException {
            this.seekable.reset();
        }

        @Override // com.twelvemonkeys.io.Seekable
        public void mark() {
            this.seekable.mark();
        }
    }
}
