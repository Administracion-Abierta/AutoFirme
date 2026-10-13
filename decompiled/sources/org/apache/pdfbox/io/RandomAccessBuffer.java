package org.apache.pdfbox.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/io/RandomAccessBuffer.class */
public class RandomAccessBuffer implements RandomAccess, Cloneable {
    private static final int DEFAULT_CHUNK_SIZE = 1024;
    private int chunkSize;
    private List<byte[]> bufferList;
    private byte[] currentBuffer;
    private long pointer;
    private int currentBufferPointer;
    private long size;
    private int bufferListIndex;
    private int bufferListMaxIndex;

    public RandomAccessBuffer() {
        this(1024);
    }

    private RandomAccessBuffer(int definedChunkSize) {
        this.chunkSize = 1024;
        this.bufferList = null;
        this.bufferList = new ArrayList();
        this.chunkSize = definedChunkSize;
        this.currentBuffer = new byte[this.chunkSize];
        this.bufferList.add(this.currentBuffer);
        this.pointer = 0L;
        this.currentBufferPointer = 0;
        this.size = 0L;
        this.bufferListIndex = 0;
        this.bufferListMaxIndex = 0;
    }

    public RandomAccessBuffer(byte[] input) {
        this.chunkSize = 1024;
        this.bufferList = null;
        this.bufferList = new ArrayList(1);
        this.chunkSize = input.length;
        this.currentBuffer = input;
        this.bufferList.add(this.currentBuffer);
        this.pointer = 0L;
        this.currentBufferPointer = 0;
        this.size = this.chunkSize;
        this.bufferListIndex = 0;
        this.bufferListMaxIndex = 0;
    }

    public RandomAccessBuffer(InputStream input) throws IOException {
        this();
        byte[] byteBuffer = new byte[8192];
        while (true) {
            int bytesRead = input.read(byteBuffer);
            if (bytesRead > -1) {
                write(byteBuffer, 0, bytesRead);
            } else {
                seek(0L);
                return;
            }
        }
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public RandomAccessBuffer m1096clone() {
        RandomAccessBuffer copy = new RandomAccessBuffer(this.chunkSize);
        copy.bufferList = new ArrayList(this.bufferList.size());
        for (byte[] buffer : this.bufferList) {
            byte[] newBuffer = new byte[buffer.length];
            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
            copy.bufferList.add(newBuffer);
        }
        if (this.currentBuffer != null) {
            copy.currentBuffer = copy.bufferList.get(copy.bufferList.size() - 1);
        } else {
            copy.currentBuffer = null;
        }
        copy.pointer = this.pointer;
        copy.currentBufferPointer = this.currentBufferPointer;
        copy.size = this.size;
        copy.bufferListIndex = this.bufferListIndex;
        copy.bufferListMaxIndex = this.bufferListMaxIndex;
        return copy;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.currentBuffer = null;
        this.bufferList.clear();
        this.pointer = 0L;
        this.currentBufferPointer = 0;
        this.size = 0L;
        this.bufferListIndex = 0;
    }

    @Override // org.apache.pdfbox.io.RandomAccessWrite
    public void clear() {
        this.bufferList.clear();
        this.currentBuffer = new byte[this.chunkSize];
        this.bufferList.add(this.currentBuffer);
        this.pointer = 0L;
        this.currentBufferPointer = 0;
        this.size = 0L;
        this.bufferListIndex = 0;
        this.bufferListMaxIndex = 0;
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public void seek(long position) throws IOException {
        checkClosed();
        if (position < 0) {
            throw new IOException("Invalid position " + position);
        }
        this.pointer = position;
        if (this.pointer < this.size) {
            this.bufferListIndex = (int) (this.pointer / ((long) this.chunkSize));
            this.currentBufferPointer = (int) (this.pointer % ((long) this.chunkSize));
            this.currentBuffer = this.bufferList.get(this.bufferListIndex);
        } else {
            this.bufferListIndex = this.bufferListMaxIndex;
            this.currentBuffer = this.bufferList.get(this.bufferListIndex);
            this.currentBufferPointer = (int) (this.size % ((long) this.chunkSize));
        }
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public long getPosition() throws IOException {
        checkClosed();
        return this.pointer;
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public int read() throws IOException {
        checkClosed();
        if (this.pointer >= this.size) {
            return -1;
        }
        if (this.currentBufferPointer >= this.chunkSize) {
            if (this.bufferListIndex >= this.bufferListMaxIndex) {
                return -1;
            }
            List<byte[]> list = this.bufferList;
            int i = this.bufferListIndex + 1;
            this.bufferListIndex = i;
            this.currentBuffer = list.get(i);
            this.currentBufferPointer = 0;
        }
        this.pointer++;
        byte[] bArr = this.currentBuffer;
        int i2 = this.currentBufferPointer;
        this.currentBufferPointer = i2 + 1;
        return bArr[i2] & 255;
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public int read(byte[] b, int offset, int length) throws IOException {
        checkClosed();
        if (this.pointer >= this.size) {
            return -1;
        }
        int bytesRead = readRemainingBytes(b, offset, length);
        while (bytesRead < length && available() > 0) {
            bytesRead += readRemainingBytes(b, offset + bytesRead, length - bytesRead);
            if (this.currentBufferPointer == this.chunkSize) {
                nextBuffer();
            }
        }
        return bytesRead;
    }

    private int readRemainingBytes(byte[] b, int offset, int length) {
        int maxLength = (int) Math.min(length, this.size - this.pointer);
        int remainingBytes = this.chunkSize - this.currentBufferPointer;
        if (remainingBytes == 0) {
            return 0;
        }
        if (maxLength >= remainingBytes) {
            System.arraycopy(this.currentBuffer, this.currentBufferPointer, b, offset, remainingBytes);
            this.currentBufferPointer += remainingBytes;
            this.pointer += (long) remainingBytes;
            return remainingBytes;
        }
        System.arraycopy(this.currentBuffer, this.currentBufferPointer, b, offset, maxLength);
        this.currentBufferPointer += maxLength;
        this.pointer += (long) maxLength;
        return maxLength;
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public long length() throws IOException {
        checkClosed();
        return this.size;
    }

    @Override // org.apache.pdfbox.io.RandomAccessWrite
    public void write(int b) throws IOException {
        checkClosed();
        if (this.currentBufferPointer >= this.chunkSize) {
            if (this.pointer + ((long) this.chunkSize) >= 2147483647L) {
                throw new IOException("RandomAccessBuffer overflow");
            }
            expandBuffer();
        }
        byte[] bArr = this.currentBuffer;
        int i = this.currentBufferPointer;
        this.currentBufferPointer = i + 1;
        bArr[i] = (byte) b;
        this.pointer++;
        if (this.pointer > this.size) {
            this.size = this.pointer;
        }
        if (this.currentBufferPointer >= this.chunkSize) {
            if (this.pointer + ((long) this.chunkSize) >= 2147483647L) {
                throw new IOException("RandomAccessBuffer overflow");
            }
            expandBuffer();
        }
    }

    @Override // org.apache.pdfbox.io.RandomAccessWrite
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override // org.apache.pdfbox.io.RandomAccessWrite
    public void write(byte[] b, int offset, int length) throws IOException {
        checkClosed();
        long newSize = this.pointer + ((long) length);
        int remainingBytes = this.chunkSize - this.currentBufferPointer;
        if (length >= remainingBytes) {
            if (newSize > 2147483647L) {
                throw new IOException("RandomAccessBuffer overflow");
            }
            System.arraycopy(b, offset, this.currentBuffer, this.currentBufferPointer, remainingBytes);
            int newOffset = offset + remainingBytes;
            long remainingBytes2Write = length - remainingBytes;
            int numberOfNewArrays = ((int) remainingBytes2Write) / this.chunkSize;
            for (int i = 0; i < numberOfNewArrays; i++) {
                expandBuffer();
                System.arraycopy(b, newOffset, this.currentBuffer, this.currentBufferPointer, this.chunkSize);
                newOffset += this.chunkSize;
            }
            long remainingBytes2Write2 = remainingBytes2Write - (((long) numberOfNewArrays) * ((long) this.chunkSize));
            if (remainingBytes2Write2 >= 0) {
                expandBuffer();
                if (remainingBytes2Write2 > 0) {
                    System.arraycopy(b, newOffset, this.currentBuffer, this.currentBufferPointer, (int) remainingBytes2Write2);
                }
                this.currentBufferPointer = (int) remainingBytes2Write2;
            }
        } else {
            System.arraycopy(b, offset, this.currentBuffer, this.currentBufferPointer, length);
            this.currentBufferPointer += length;
        }
        this.pointer += (long) length;
        if (this.pointer > this.size) {
            this.size = this.pointer;
        }
    }

    private void expandBuffer() throws IOException {
        if (this.bufferListMaxIndex > this.bufferListIndex) {
            nextBuffer();
            return;
        }
        this.currentBuffer = new byte[this.chunkSize];
        this.bufferList.add(this.currentBuffer);
        this.currentBufferPointer = 0;
        this.bufferListMaxIndex++;
        this.bufferListIndex++;
    }

    private void nextBuffer() throws IOException {
        if (this.bufferListIndex == this.bufferListMaxIndex) {
            throw new IOException("No more chunks available, end of buffer reached");
        }
        this.currentBufferPointer = 0;
        List<byte[]> list = this.bufferList;
        int i = this.bufferListIndex + 1;
        this.bufferListIndex = i;
        this.currentBuffer = list.get(i);
    }

    private void checkClosed() throws IOException {
        if (this.currentBuffer == null) {
            throw new IOException("RandomAccessBuffer already closed");
        }
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public boolean isClosed() {
        return this.currentBuffer == null;
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public boolean isEOF() throws IOException {
        checkClosed();
        return this.pointer >= this.size;
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public int available() throws IOException {
        return (int) Math.min(length() - getPosition(), 2147483647L);
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public int peek() throws IOException {
        int result = read();
        if (result != -1) {
            rewind(1);
        }
        return result;
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public void rewind(int bytes) throws IOException {
        checkClosed();
        seek(getPosition() - ((long) bytes));
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public byte[] readFully(int length) throws IOException {
        byte[] bytes = new byte[length];
        int bytesRead = 0;
        do {
            int count = read(bytes, bytesRead, length - bytesRead);
            if (count < 0) {
                throw new EOFException();
            }
            bytesRead += count;
        } while (bytesRead < length);
        return bytes;
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }
}
