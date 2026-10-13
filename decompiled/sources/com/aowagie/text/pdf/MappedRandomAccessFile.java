package com.aowagie.text.pdf;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.BufferUnderflowException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/MappedRandomAccessFile.class */
class MappedRandomAccessFile {
    private MappedByteBuffer mappedByteBuffer = null;
    private FileChannel channel = null;

    public MappedRandomAccessFile(String filename, String mode) throws IOException {
        if (mode.equals("rw")) {
            init(new RandomAccessFile(filename, mode).getChannel(), FileChannel.MapMode.READ_WRITE);
        } else {
            init(new FileInputStream(filename).getChannel(), FileChannel.MapMode.READ_ONLY);
        }
    }

    private void init(FileChannel channel, FileChannel.MapMode mapMode) throws IOException {
        this.channel = channel;
        this.mappedByteBuffer = channel.map(mapMode, 0L, channel.size());
        this.mappedByteBuffer.load();
    }

    public FileChannel getChannel() {
        return this.channel;
    }

    public int read() {
        try {
            byte b = this.mappedByteBuffer.get();
            int n = b & 255;
            return n;
        } catch (BufferUnderflowException e) {
            return -1;
        }
    }

    public int read(byte[] bytes, int off, int len) {
        int pos = this.mappedByteBuffer.position();
        int limit = this.mappedByteBuffer.limit();
        if (pos == limit) {
            return -1;
        }
        int newlimit = (pos + len) - off;
        if (newlimit > limit) {
            len = limit - pos;
        }
        this.mappedByteBuffer.get(bytes, off, len);
        return len;
    }

    public long getFilePointer() {
        return this.mappedByteBuffer.position();
    }

    public void seek(long pos) {
        this.mappedByteBuffer.position((int) pos);
    }

    public long length() {
        return this.mappedByteBuffer.limit();
    }

    public void close() throws IOException {
        clean(this.mappedByteBuffer);
        this.mappedByteBuffer = null;
        if (this.channel != null) {
            this.channel.close();
        }
        this.channel = null;
    }

    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    private static boolean clean(final java.nio.ByteBuffer buffer) {
        if (buffer == null || !buffer.isDirect()) {
            return false;
        }
        Boolean b = (Boolean) AccessController.doPrivileged(new PrivilegedAction() { // from class: com.aowagie.text.pdf.MappedRandomAccessFile.1
            @Override // java.security.PrivilegedAction
            public Object run() {
                Boolean success = Boolean.FALSE;
                try {
                    Method getCleanerMethod = buffer.getClass().getMethod("cleaner", (Class[]) null);
                    getCleanerMethod.setAccessible(true);
                    Object cleaner = getCleanerMethod.invoke(buffer, (Object[]) null);
                    Method clean = cleaner.getClass().getMethod("clean", (Class[]) null);
                    clean.invoke(cleaner, (Object[]) null);
                    success = Boolean.TRUE;
                } catch (Exception e) {
                }
                return success;
            }
        });
        return b.booleanValue();
    }
}
