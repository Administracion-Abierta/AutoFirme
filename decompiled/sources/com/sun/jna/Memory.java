package com.sun.jna;

import com.sun.jna.internal.Cleaner;
import java.io.Closeable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/Memory.class */
public class Memory extends Pointer implements Closeable {
    private static final Map<Long, Reference<Memory>> allocatedMemory = new ConcurrentHashMap();
    private static final WeakMemoryHolder buffers = new WeakMemoryHolder();
    private final Cleaner.Cleanable cleanable;
    protected long size;

    public static void purge() {
        buffers.clean();
    }

    public static void disposeAll() {
        Collection<Reference<Memory>> refs = new ArrayList<>(allocatedMemory.values());
        for (Reference<Memory> r : refs) {
            Memory m = r.get();
            if (m != null) {
                m.close();
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/Memory$SharedMemory.class */
    private class SharedMemory extends Memory {
        public SharedMemory(long offset, long size) {
            this.size = size;
            this.peer = Memory.this.peer + offset;
        }

        @Override // com.sun.jna.Memory
        protected synchronized void dispose() {
            this.peer = 0L;
        }

        @Override // com.sun.jna.Memory
        protected void boundsCheck(long off, long sz) {
            Memory.this.boundsCheck((this.peer - Memory.this.peer) + off, sz);
        }

        @Override // com.sun.jna.Memory, com.sun.jna.Pointer
        public String toString() {
            return super.toString() + " (shared from " + Memory.this.toString() + ")";
        }
    }

    public Memory(long size) {
        this.size = size;
        if (size <= 0) {
            throw new IllegalArgumentException("Allocation size must be greater than zero");
        }
        this.peer = malloc(size);
        if (this.peer == 0) {
            throw new OutOfMemoryError("Cannot allocate " + size + " bytes");
        }
        allocatedMemory.put(Long.valueOf(this.peer), new WeakReference(this));
        this.cleanable = Cleaner.getCleaner().register(this, new MemoryDisposer(this.peer));
    }

    protected Memory() {
        this.cleanable = null;
    }

    @Override // com.sun.jna.Pointer
    public Pointer share(long offset) {
        return share(offset, size() - offset);
    }

    @Override // com.sun.jna.Pointer
    public Pointer share(long offset, long sz) {
        boundsCheck(offset, sz);
        return new SharedMemory(offset, sz);
    }

    public Memory align(int byteBoundary) {
        if (byteBoundary <= 0) {
            throw new IllegalArgumentException("Byte boundary must be positive: " + byteBoundary);
        }
        for (int i = 0; i < 32; i++) {
            if (byteBoundary == (1 << i)) {
                long mask = (((long) byteBoundary) - 1) ^ (-1);
                if ((this.peer & mask) != this.peer) {
                    long newPeer = ((this.peer + ((long) byteBoundary)) - 1) & mask;
                    long newSize = (this.peer + this.size) - newPeer;
                    if (newSize <= 0) {
                        throw new IllegalArgumentException("Insufficient memory to align to the requested boundary");
                    }
                    return (Memory) share(newPeer - this.peer, newSize);
                }
                return this;
            }
        }
        throw new IllegalArgumentException("Byte boundary must be a power of two");
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.peer = 0L;
        if (this.cleanable != null) {
            this.cleanable.clean();
        }
    }

    @Deprecated
    protected void dispose() {
        close();
    }

    public void clear() {
        clear(this.size);
    }

    public boolean valid() {
        return this.peer != 0;
    }

    public long size() {
        return this.size;
    }

    protected void boundsCheck(long off, long sz) {
        if (off < 0) {
            throw new IndexOutOfBoundsException("Invalid offset: " + off);
        }
        if (off + sz > this.size) {
            String msg = "Bounds exceeds available space : size=" + this.size + ", offset=" + (off + sz);
            throw new IndexOutOfBoundsException(msg);
        }
    }

    @Override // com.sun.jna.Pointer
    public void read(long bOff, byte[] buf, int index, int length) {
        boundsCheck(bOff, ((long) length) * 1);
        super.read(bOff, buf, index, length);
    }

    @Override // com.sun.jna.Pointer
    public void read(long bOff, short[] buf, int index, int length) {
        boundsCheck(bOff, ((long) length) * 2);
        super.read(bOff, buf, index, length);
    }

    @Override // com.sun.jna.Pointer
    public void read(long bOff, char[] buf, int index, int length) {
        boundsCheck(bOff, length * Native.WCHAR_SIZE);
        super.read(bOff, buf, index, length);
    }

    @Override // com.sun.jna.Pointer
    public void read(long bOff, int[] buf, int index, int length) {
        boundsCheck(bOff, ((long) length) * 4);
        super.read(bOff, buf, index, length);
    }

    @Override // com.sun.jna.Pointer
    public void read(long bOff, long[] buf, int index, int length) {
        boundsCheck(bOff, ((long) length) * 8);
        super.read(bOff, buf, index, length);
    }

    @Override // com.sun.jna.Pointer
    public void read(long bOff, float[] buf, int index, int length) {
        boundsCheck(bOff, ((long) length) * 4);
        super.read(bOff, buf, index, length);
    }

    @Override // com.sun.jna.Pointer
    public void read(long bOff, double[] buf, int index, int length) {
        boundsCheck(bOff, ((long) length) * 8);
        super.read(bOff, buf, index, length);
    }

    @Override // com.sun.jna.Pointer
    public void read(long bOff, Pointer[] buf, int index, int length) {
        boundsCheck(bOff, length * Native.POINTER_SIZE);
        super.read(bOff, buf, index, length);
    }

    @Override // com.sun.jna.Pointer
    public void write(long bOff, byte[] buf, int index, int length) {
        boundsCheck(bOff, ((long) length) * 1);
        super.write(bOff, buf, index, length);
    }

    @Override // com.sun.jna.Pointer
    public void write(long bOff, short[] buf, int index, int length) {
        boundsCheck(bOff, ((long) length) * 2);
        super.write(bOff, buf, index, length);
    }

    @Override // com.sun.jna.Pointer
    public void write(long bOff, char[] buf, int index, int length) {
        boundsCheck(bOff, length * Native.WCHAR_SIZE);
        super.write(bOff, buf, index, length);
    }

    @Override // com.sun.jna.Pointer
    public void write(long bOff, int[] buf, int index, int length) {
        boundsCheck(bOff, ((long) length) * 4);
        super.write(bOff, buf, index, length);
    }

    @Override // com.sun.jna.Pointer
    public void write(long bOff, long[] buf, int index, int length) {
        boundsCheck(bOff, ((long) length) * 8);
        super.write(bOff, buf, index, length);
    }

    @Override // com.sun.jna.Pointer
    public void write(long bOff, float[] buf, int index, int length) {
        boundsCheck(bOff, ((long) length) * 4);
        super.write(bOff, buf, index, length);
    }

    @Override // com.sun.jna.Pointer
    public void write(long bOff, double[] buf, int index, int length) {
        boundsCheck(bOff, ((long) length) * 8);
        super.write(bOff, buf, index, length);
    }

    @Override // com.sun.jna.Pointer
    public void write(long bOff, Pointer[] buf, int index, int length) {
        boundsCheck(bOff, length * Native.POINTER_SIZE);
        super.write(bOff, buf, index, length);
    }

    @Override // com.sun.jna.Pointer
    public byte getByte(long offset) {
        boundsCheck(offset, 1L);
        return super.getByte(offset);
    }

    @Override // com.sun.jna.Pointer
    public char getChar(long offset) {
        boundsCheck(offset, Native.WCHAR_SIZE);
        return super.getChar(offset);
    }

    @Override // com.sun.jna.Pointer
    public short getShort(long offset) {
        boundsCheck(offset, 2L);
        return super.getShort(offset);
    }

    @Override // com.sun.jna.Pointer
    public int getInt(long offset) {
        boundsCheck(offset, 4L);
        return super.getInt(offset);
    }

    @Override // com.sun.jna.Pointer
    public long getLong(long offset) {
        boundsCheck(offset, 8L);
        return super.getLong(offset);
    }

    @Override // com.sun.jna.Pointer
    public float getFloat(long offset) {
        boundsCheck(offset, 4L);
        return super.getFloat(offset);
    }

    @Override // com.sun.jna.Pointer
    public double getDouble(long offset) {
        boundsCheck(offset, 8L);
        return super.getDouble(offset);
    }

    @Override // com.sun.jna.Pointer
    public Pointer getPointer(long offset) {
        boundsCheck(offset, Native.POINTER_SIZE);
        return shareReferenceIfInBounds(super.getPointer(offset));
    }

    @Override // com.sun.jna.Pointer
    public ByteBuffer getByteBuffer(long offset, long length) {
        boundsCheck(offset, length);
        ByteBuffer b = super.getByteBuffer(offset, length);
        buffers.put(b, this);
        return b;
    }

    @Override // com.sun.jna.Pointer
    public String getString(long offset, String encoding) {
        boundsCheck(offset, 0L);
        return super.getString(offset, encoding);
    }

    @Override // com.sun.jna.Pointer
    public String getWideString(long offset) {
        boundsCheck(offset, 0L);
        return super.getWideString(offset);
    }

    @Override // com.sun.jna.Pointer
    public void setByte(long offset, byte value) {
        boundsCheck(offset, 1L);
        super.setByte(offset, value);
    }

    @Override // com.sun.jna.Pointer
    public void setChar(long offset, char value) {
        boundsCheck(offset, Native.WCHAR_SIZE);
        super.setChar(offset, value);
    }

    @Override // com.sun.jna.Pointer
    public void setShort(long offset, short value) {
        boundsCheck(offset, 2L);
        super.setShort(offset, value);
    }

    @Override // com.sun.jna.Pointer
    public void setInt(long offset, int value) {
        boundsCheck(offset, 4L);
        super.setInt(offset, value);
    }

    @Override // com.sun.jna.Pointer
    public void setLong(long offset, long value) {
        boundsCheck(offset, 8L);
        super.setLong(offset, value);
    }

    @Override // com.sun.jna.Pointer
    public void setFloat(long offset, float value) {
        boundsCheck(offset, 4L);
        super.setFloat(offset, value);
    }

    @Override // com.sun.jna.Pointer
    public void setDouble(long offset, double value) {
        boundsCheck(offset, 8L);
        super.setDouble(offset, value);
    }

    @Override // com.sun.jna.Pointer
    public void setPointer(long offset, Pointer value) {
        boundsCheck(offset, Native.POINTER_SIZE);
        super.setPointer(offset, value);
    }

    @Override // com.sun.jna.Pointer
    public void setString(long offset, String value, String encoding) {
        boundsCheck(offset, ((long) Native.getBytes(value, encoding).length) + 1);
        super.setString(offset, value, encoding);
    }

    @Override // com.sun.jna.Pointer
    public void setWideString(long offset, String value) {
        boundsCheck(offset, (((long) value.length()) + 1) * ((long) Native.WCHAR_SIZE));
        super.setWideString(offset, value);
    }

    @Override // com.sun.jna.Pointer
    public String toString() {
        return "allocated@0x" + Long.toHexString(this.peer) + " (" + this.size + " bytes)";
    }

    protected static void free(long p) {
        if (p != 0) {
            Native.free(p);
        }
    }

    protected static long malloc(long size) {
        return Native.malloc(size);
    }

    public String dump() {
        return dump(0L, (int) size());
    }

    private Pointer shareReferenceIfInBounds(Pointer target) {
        if (target == null) {
            return null;
        }
        long offset = target.peer - this.peer;
        if (offset >= 0 && offset < this.size) {
            return share(offset);
        }
        return target;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/Memory$MemoryDisposer.class */
    private static final class MemoryDisposer implements Runnable {
        private long peer;

        public MemoryDisposer(long peer) {
            this.peer = peer;
        }

        @Override // java.lang.Runnable
        public synchronized void run() {
            try {
                Memory.free(this.peer);
                Map map = Memory.allocatedMemory;
            } finally {
                Memory.allocatedMemory.remove(Long.valueOf(this.peer));
                this.peer = 0L;
            }
        }
    }
}
