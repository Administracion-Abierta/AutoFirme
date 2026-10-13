package org.apache.pdfbox.io;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/io/RandomAccessBufferedFileInputStream.class */
public class RandomAccessBufferedFileInputStream extends InputStream implements RandomAccessRead {
    private static final String TMP_FILE_PREFIX = "tmpPDFBox";
    private int pageSizeShift;
    private int pageSize;
    private long pageOffsetMask;
    private int maxCachedPages;
    private File tempFile;
    private byte[] lastRemovedCachePage;
    private final Map<Long, byte[]> pageCache;
    private long curPageOffset;
    private byte[] curPage;
    private int offsetWithinPage;
    private final java.io.RandomAccessFile raFile;
    private final long fileLength;
    private long fileOffset;
    private boolean isClosed;

    public RandomAccessBufferedFileInputStream(String filename) throws IOException {
        this(new File(filename));
    }

    public RandomAccessBufferedFileInputStream(File file) throws IOException {
        this.pageSizeShift = 12;
        this.pageSize = 1 << this.pageSizeShift;
        this.pageOffsetMask = (-1) << this.pageSizeShift;
        this.maxCachedPages = 1000;
        this.lastRemovedCachePage = null;
        this.pageCache = new LinkedHashMap<Long, byte[]>(this.maxCachedPages, 0.75f, true) { // from class: org.apache.pdfbox.io.RandomAccessBufferedFileInputStream.1
            private static final long serialVersionUID = -6302488539257741101L;

            @Override // java.util.LinkedHashMap
            protected boolean removeEldestEntry(Map.Entry<Long, byte[]> eldest) {
                boolean doRemove = size() > RandomAccessBufferedFileInputStream.this.maxCachedPages;
                if (doRemove) {
                    RandomAccessBufferedFileInputStream.this.lastRemovedCachePage = eldest.getValue();
                }
                return doRemove;
            }
        };
        this.curPageOffset = -1L;
        this.curPage = new byte[this.pageSize];
        this.offsetWithinPage = 0;
        this.fileOffset = 0L;
        this.raFile = new java.io.RandomAccessFile(file, PDPageLabelRange.STYLE_ROMAN_LOWER);
        this.fileLength = file.length();
        seek(0L);
    }

    public RandomAccessBufferedFileInputStream(InputStream input) throws IOException {
        this.pageSizeShift = 12;
        this.pageSize = 1 << this.pageSizeShift;
        this.pageOffsetMask = (-1) << this.pageSizeShift;
        this.maxCachedPages = 1000;
        this.lastRemovedCachePage = null;
        this.pageCache = new LinkedHashMap<Long, byte[]>(this.maxCachedPages, 0.75f, true) { // from class: org.apache.pdfbox.io.RandomAccessBufferedFileInputStream.1
            private static final long serialVersionUID = -6302488539257741101L;

            @Override // java.util.LinkedHashMap
            protected boolean removeEldestEntry(Map.Entry<Long, byte[]> eldest) {
                boolean doRemove = size() > RandomAccessBufferedFileInputStream.this.maxCachedPages;
                if (doRemove) {
                    RandomAccessBufferedFileInputStream.this.lastRemovedCachePage = eldest.getValue();
                }
                return doRemove;
            }
        };
        this.curPageOffset = -1L;
        this.curPage = new byte[this.pageSize];
        this.offsetWithinPage = 0;
        this.fileOffset = 0L;
        this.tempFile = createTmpFile(input);
        this.fileLength = this.tempFile.length();
        this.raFile = new java.io.RandomAccessFile(this.tempFile, PDPageLabelRange.STYLE_ROMAN_LOWER);
        seek(0L);
    }

    private File createTmpFile(InputStream input) throws IOException {
        FileOutputStream fos = null;
        try {
            File tmpFile = File.createTempFile(TMP_FILE_PREFIX, ".pdf");
            FileOutputStream fos2 = new FileOutputStream(tmpFile);
            IOUtils.copy(input, fos2);
            IOUtils.closeQuietly(input);
            return tmpFile;
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(fos);
        }
    }

    private void deleteTempFile() {
        if (this.tempFile != null) {
            this.tempFile.delete();
        }
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public long getPosition() {
        return this.fileOffset;
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public void seek(long newOffset) throws IOException {
        long newPageOffset = newOffset & this.pageOffsetMask;
        if (newPageOffset != this.curPageOffset) {
            byte[] newPage = this.pageCache.get(Long.valueOf(newPageOffset));
            if (newPage == null) {
                this.raFile.seek(newPageOffset);
                newPage = readPage();
                this.pageCache.put(Long.valueOf(newPageOffset), newPage);
            }
            this.curPageOffset = newPageOffset;
            this.curPage = newPage;
        }
        this.offsetWithinPage = (int) (newOffset - this.curPageOffset);
        this.fileOffset = newOffset;
    }

    private byte[] readPage() throws IOException {
        byte[] page;
        int curBytesRead;
        if (this.lastRemovedCachePage != null) {
            page = this.lastRemovedCachePage;
            this.lastRemovedCachePage = null;
        } else {
            page = new byte[this.pageSize];
        }
        int i = 0;
        while (true) {
            int readBytes = i;
            if (readBytes >= this.pageSize || (curBytesRead = this.raFile.read(page, readBytes, this.pageSize - readBytes)) < 0) {
                break;
            }
            i = readBytes + curBytesRead;
        }
        return page;
    }

    @Override // java.io.InputStream, org.apache.pdfbox.io.RandomAccessRead
    public int read() throws IOException {
        if (this.fileOffset >= this.fileLength) {
            return -1;
        }
        if (this.offsetWithinPage == this.pageSize) {
            seek(this.fileOffset);
        }
        this.fileOffset++;
        byte[] bArr = this.curPage;
        int i = this.offsetWithinPage;
        this.offsetWithinPage = i + 1;
        return bArr[i] & 255;
    }

    @Override // java.io.InputStream, org.apache.pdfbox.io.RandomAccessRead
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override // java.io.InputStream, org.apache.pdfbox.io.RandomAccessRead
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.fileOffset >= this.fileLength) {
            return -1;
        }
        if (this.offsetWithinPage == this.pageSize) {
            seek(this.fileOffset);
        }
        int commonLen = Math.min(this.pageSize - this.offsetWithinPage, len);
        if (this.fileLength - this.fileOffset < this.pageSize) {
            commonLen = Math.min(commonLen, (int) (this.fileLength - this.fileOffset));
        }
        System.arraycopy(this.curPage, this.offsetWithinPage, b, off, commonLen);
        this.offsetWithinPage += commonLen;
        this.fileOffset += (long) commonLen;
        return commonLen;
    }

    @Override // java.io.InputStream, org.apache.pdfbox.io.RandomAccessRead
    public int available() throws IOException {
        return (int) Math.min(this.fileLength - this.fileOffset, 2147483647L);
    }

    @Override // java.io.InputStream
    public long skip(long n) throws IOException {
        long toSkip = n;
        if (this.fileLength - this.fileOffset < toSkip) {
            toSkip = this.fileLength - this.fileOffset;
        }
        if (toSkip < this.pageSize && ((long) this.offsetWithinPage) + toSkip <= this.pageSize) {
            this.offsetWithinPage = (int) (((long) this.offsetWithinPage) + toSkip);
            this.fileOffset += toSkip;
        } else {
            seek(this.fileOffset + toSkip);
        }
        return toSkip;
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public long length() throws IOException {
        return this.fileLength;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.raFile.close();
        deleteTempFile();
        this.pageCache.clear();
        this.isClosed = true;
    }

    @Override // org.apache.pdfbox.io.RandomAccessRead
    public boolean isClosed() {
        return this.isClosed;
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
    public boolean isEOF() throws IOException {
        int peek = peek();
        return peek == -1;
    }
}
