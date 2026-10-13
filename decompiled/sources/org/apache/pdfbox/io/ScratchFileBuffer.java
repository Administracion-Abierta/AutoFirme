package org.apache.pdfbox.io;

import java.io.EOFException;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/io/ScratchFileBuffer.class */
class ScratchFileBuffer implements RandomAccess {
    private final int pageSize;
    private ScratchFile pageHandler;
    private int currentPagePositionInPageIndexes;
    private long currentPageOffset;
    private byte[] currentPage;
    private int positionInPage;
    private static final Log LOG = LogFactory.getLog(ScratchFileBuffer.class);
    private long size = 0;
    private boolean currentPageContentChanged = false;
    private int[] pageIndexes = new int[16];
    private int pageCount = 0;

    ScratchFileBuffer(ScratchFile pageHandler) throws IOException {
        pageHandler.checkClosed();
        this.pageHandler = pageHandler;
        this.pageSize = this.pageHandler.getPageSize();
        addPage();
    }

    private void checkClosed() throws IOException {
        if (this.pageHandler == null) {
            throw new IOException("Buffer already closed");
        }
        this.pageHandler.checkClosed();
    }

    private void addPage() throws IOException {
        if (this.pageCount + 1 >= this.pageIndexes.length) {
            int newSize = this.pageIndexes.length * 2;
            if (newSize < this.pageIndexes.length) {
                if (this.pageIndexes.length == Integer.MAX_VALUE) {
                    throw new IOException("Maximum buffer size reached.");
                }
                newSize = Integer.MAX_VALUE;
            }
            int[] newPageIndexes = new int[newSize];
            System.arraycopy(this.pageIndexes, 0, newPageIndexes, 0, this.pageCount);
            this.pageIndexes = newPageIndexes;
        }
        int newPageIdx = this.pageHandler.getNewPage();
        this.pageIndexes[this.pageCount] = newPageIdx;
        this.currentPagePositionInPageIndexes = this.pageCount;
        this.currentPageOffset = ((long) this.pageCount) * ((long) this.pageSize);
        this.pageCount++;
        this.currentPage = new byte[this.pageSize];
        this.positionInPage = 0;
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public long length() throws IOException {
        return this.size;
    }

    private boolean ensureAvailableBytesInPage(boolean addNewPageIfNeeded) throws IOException {
        if (this.positionInPage >= this.pageSize) {
            if (this.currentPageContentChanged) {
                this.pageHandler.writePage(this.pageIndexes[this.currentPagePositionInPageIndexes], this.currentPage);
                this.currentPageContentChanged = false;
            }
            if (this.currentPagePositionInPageIndexes + 1 < this.pageCount) {
                ScratchFile scratchFile = this.pageHandler;
                int[] iArr = this.pageIndexes;
                int i = this.currentPagePositionInPageIndexes + 1;
                this.currentPagePositionInPageIndexes = i;
                this.currentPage = scratchFile.readPage(iArr[i]);
                this.currentPageOffset = ((long) this.currentPagePositionInPageIndexes) * ((long) this.pageSize);
                this.positionInPage = 0;
                return true;
            }
            if (addNewPageIfNeeded) {
                addPage();
                return true;
            }
            return false;
        }
        return true;
    }

    @Override // org.apache.pdfbox.io.RandomAccessWrite
    public void write(int b) throws IOException {
        checkClosed();
        ensureAvailableBytesInPage(true);
        byte[] bArr = this.currentPage;
        int i = this.positionInPage;
        this.positionInPage = i + 1;
        bArr[i] = (byte) b;
        this.currentPageContentChanged = true;
        if (this.currentPageOffset + ((long) this.positionInPage) > this.size) {
            this.size = this.currentPageOffset + ((long) this.positionInPage);
        }
    }

    @Override // org.apache.pdfbox.io.RandomAccessWrite
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override // org.apache.pdfbox.io.RandomAccessWrite
    public void write(byte[] b, int off, int len) throws IOException {
        checkClosed();
        int remain = len;
        int bOff = off;
        while (remain > 0) {
            ensureAvailableBytesInPage(true);
            int bytesToWrite = Math.min(remain, this.pageSize - this.positionInPage);
            System.arraycopy(b, bOff, this.currentPage, this.positionInPage, bytesToWrite);
            this.positionInPage += bytesToWrite;
            this.currentPageContentChanged = true;
            bOff += bytesToWrite;
            remain -= bytesToWrite;
        }
        if (this.currentPageOffset + ((long) this.positionInPage) > this.size) {
            this.size = this.currentPageOffset + ((long) this.positionInPage);
        }
    }

    @Override // org.apache.pdfbox.io.RandomAccessWrite
    public final void clear() throws IOException {
        checkClosed();
        this.pageHandler.markPagesAsFree(this.pageIndexes, 1, this.pageCount - 1);
        this.pageCount = 1;
        if (this.currentPagePositionInPageIndexes > 0) {
            this.currentPage = this.pageHandler.readPage(this.pageIndexes[0]);
            this.currentPagePositionInPageIndexes = 0;
            this.currentPageOffset = 0L;
        }
        this.positionInPage = 0;
        this.size = 0L;
        this.currentPageContentChanged = false;
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public long getPosition() throws IOException {
        checkClosed();
        return this.currentPageOffset + ((long) this.positionInPage);
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public void seek(long seekToPosition) throws IOException {
        checkClosed();
        if (seekToPosition > this.size) {
            throw new EOFException();
        }
        if (seekToPosition < 0) {
            throw new IOException("Negative seek offset: " + seekToPosition);
        }
        if (seekToPosition >= this.currentPageOffset && seekToPosition <= this.currentPageOffset + ((long) this.pageSize)) {
            this.positionInPage = (int) (seekToPosition - this.currentPageOffset);
            return;
        }
        if (this.currentPageContentChanged) {
            this.pageHandler.writePage(this.pageIndexes[this.currentPagePositionInPageIndexes], this.currentPage);
            this.currentPageContentChanged = false;
        }
        int newPagePosition = (int) (seekToPosition / ((long) this.pageSize));
        if (seekToPosition % ((long) this.pageSize) == 0 && seekToPosition == this.size) {
            newPagePosition--;
        }
        this.currentPage = this.pageHandler.readPage(this.pageIndexes[newPagePosition]);
        this.currentPagePositionInPageIndexes = newPagePosition;
        this.currentPageOffset = ((long) this.currentPagePositionInPageIndexes) * ((long) this.pageSize);
        this.positionInPage = (int) (seekToPosition - this.currentPageOffset);
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public boolean isClosed() {
        return this.pageHandler == null;
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
        seek((this.currentPageOffset + ((long) this.positionInPage)) - ((long) bytes));
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
    public boolean isEOF() throws IOException {
        checkClosed();
        return this.currentPageOffset + ((long) this.positionInPage) >= this.size;
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public int available() throws IOException {
        checkClosed();
        return (int) Math.min(this.size - (this.currentPageOffset + ((long) this.positionInPage)), 2147483647L);
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public int read() throws IOException {
        checkClosed();
        if (this.currentPageOffset + ((long) this.positionInPage) >= this.size) {
            return -1;
        }
        if (!ensureAvailableBytesInPage(false)) {
            throw new IOException("Unexpectedly no bytes available for read in buffer.");
        }
        byte[] bArr = this.currentPage;
        int i = this.positionInPage;
        this.positionInPage = i + 1;
        return bArr[i] & 255;
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public int read(byte[] b, int off, int len) throws IOException {
        checkClosed();
        if (this.currentPageOffset + ((long) this.positionInPage) >= this.size) {
            return -1;
        }
        int remain = (int) Math.min(len, this.size - (this.currentPageOffset + ((long) this.positionInPage)));
        int totalBytesRead = 0;
        int bOff = off;
        while (remain > 0) {
            if (!ensureAvailableBytesInPage(false)) {
                throw new IOException("Unexpectedly no bytes available for read in buffer.");
            }
            int readBytes = Math.min(remain, this.pageSize - this.positionInPage);
            System.arraycopy(this.currentPage, this.positionInPage, b, bOff, readBytes);
            this.positionInPage += readBytes;
            totalBytesRead += readBytes;
            bOff += readBytes;
            remain -= readBytes;
        }
        return totalBytesRead;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.pageHandler != null) {
            this.pageHandler.markPagesAsFree(this.pageIndexes, 0, this.pageCount);
            this.pageHandler = null;
            this.pageIndexes = null;
            this.currentPage = null;
            this.currentPageOffset = 0L;
            this.currentPagePositionInPageIndexes = -1;
            this.positionInPage = 0;
            this.size = 0L;
        }
    }

    protected void finalize() throws Throwable {
        try {
            if (this.pageHandler != null && LOG.isDebugEnabled()) {
                LOG.debug("ScratchFileBuffer not closed!");
            }
            close();
        } finally {
            super.finalize();
        }
    }
}
