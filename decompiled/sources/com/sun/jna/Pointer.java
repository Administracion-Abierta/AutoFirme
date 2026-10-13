package com.sun.jna;

import com.aowagie.text.pdf.ColumnText;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/Pointer.class */
public class Pointer {
    public static final Pointer NULL = null;
    protected long peer;

    public static final Pointer createConstant(long peer) {
        return new Opaque(peer);
    }

    public static final Pointer createConstant(int peer) {
        return new Opaque(((long) peer) & 4294967295L);
    }

    Pointer() {
    }

    public Pointer(long peer) {
        this.peer = peer;
    }

    public Pointer share(long offset) {
        return share(offset, 0L);
    }

    public Pointer share(long offset, long sz) {
        if (offset == 0) {
            return this;
        }
        return new Pointer(this.peer + offset);
    }

    public void clear(long size) {
        setMemory(0L, size, (byte) 0);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        return o != null && (o instanceof Pointer) && ((Pointer) o).peer == this.peer;
    }

    public int hashCode() {
        return (int) ((this.peer >>> 32) + (this.peer & 4294967295L));
    }

    public long indexOf(long offset, byte value) {
        return Native.indexOf(this, this.peer, offset, value);
    }

    public void read(long offset, byte[] buf, int index, int length) {
        Native.read(this, this.peer, offset, buf, index, length);
    }

    public void read(long offset, short[] buf, int index, int length) {
        Native.read(this, this.peer, offset, buf, index, length);
    }

    public void read(long offset, char[] buf, int index, int length) {
        Native.read(this, this.peer, offset, buf, index, length);
    }

    public void read(long offset, int[] buf, int index, int length) {
        Native.read(this, this.peer, offset, buf, index, length);
    }

    public void read(long offset, long[] buf, int index, int length) {
        Native.read(this, this.peer, offset, buf, index, length);
    }

    public void read(long offset, float[] buf, int index, int length) {
        Native.read(this, this.peer, offset, buf, index, length);
    }

    public void read(long offset, double[] buf, int index, int length) {
        Native.read(this, this.peer, offset, buf, index, length);
    }

    public void read(long offset, Pointer[] buf, int index, int length) {
        for (int i = 0; i < length; i++) {
            Pointer p = getPointer(offset + ((long) (i * Native.POINTER_SIZE)));
            Pointer oldp = buf[i + index];
            if (oldp == null || p == null || p.peer != oldp.peer) {
                buf[i + index] = p;
            }
        }
    }

    public void write(long offset, byte[] buf, int index, int length) {
        Native.write(this, this.peer, offset, buf, index, length);
    }

    public void write(long offset, short[] buf, int index, int length) {
        Native.write(this, this.peer, offset, buf, index, length);
    }

    public void write(long offset, char[] buf, int index, int length) {
        Native.write(this, this.peer, offset, buf, index, length);
    }

    public void write(long offset, int[] buf, int index, int length) {
        Native.write(this, this.peer, offset, buf, index, length);
    }

    public void write(long offset, long[] buf, int index, int length) {
        Native.write(this, this.peer, offset, buf, index, length);
    }

    public void write(long offset, float[] buf, int index, int length) {
        Native.write(this, this.peer, offset, buf, index, length);
    }

    public void write(long offset, double[] buf, int index, int length) {
        Native.write(this, this.peer, offset, buf, index, length);
    }

    public void write(long bOff, Pointer[] buf, int index, int length) {
        for (int i = 0; i < length; i++) {
            setPointer(bOff + ((long) (i * Native.POINTER_SIZE)), buf[index + i]);
        }
    }

    Object getValue(long j, Class<?> cls, Object obj) {
        Object objFromNative;
        Object objValueOf = null;
        if (Structure.class.isAssignableFrom(cls)) {
            Structure structureUpdateStructureByReference = (Structure) obj;
            if (Structure.ByReference.class.isAssignableFrom(cls)) {
                structureUpdateStructureByReference = Structure.updateStructureByReference(cls, structureUpdateStructureByReference, getPointer(j));
            } else {
                structureUpdateStructureByReference.useMemory(this, (int) j, true);
                structureUpdateStructureByReference.read();
            }
            objValueOf = structureUpdateStructureByReference;
        } else if (cls == Boolean.TYPE || cls == Boolean.class) {
            objValueOf = Function.valueOf(getInt(j) != 0);
        } else if (cls == Byte.TYPE || cls == Byte.class) {
            objValueOf = Byte.valueOf(getByte(j));
        } else if (cls == Short.TYPE || cls == Short.class) {
            objValueOf = Short.valueOf(getShort(j));
        } else if (cls == Character.TYPE || cls == Character.class) {
            objValueOf = Character.valueOf(getChar(j));
        } else if (cls == Integer.TYPE || cls == Integer.class) {
            objValueOf = Integer.valueOf(getInt(j));
        } else if (cls == Long.TYPE || cls == Long.class) {
            objValueOf = Long.valueOf(getLong(j));
        } else if (cls == Float.TYPE || cls == Float.class) {
            objValueOf = Float.valueOf(getFloat(j));
        } else if (cls == Double.TYPE || cls == Double.class) {
            objValueOf = Double.valueOf(getDouble(j));
        } else if (Pointer.class.isAssignableFrom(cls)) {
            Pointer pointer = getPointer(j);
            if (pointer != null) {
                Pointer pointer2 = obj instanceof Pointer ? (Pointer) obj : null;
                if (pointer2 == null || pointer.peer != pointer2.peer) {
                    objValueOf = pointer;
                } else {
                    objValueOf = pointer2;
                }
            }
        } else if (cls == String.class) {
            Pointer pointer3 = getPointer(j);
            objValueOf = pointer3 != null ? pointer3.getString(0L) : null;
        } else if (cls == WString.class) {
            Pointer pointer4 = getPointer(j);
            objValueOf = pointer4 != null ? new WString(pointer4.getWideString(0L)) : null;
        } else if (Callback.class.isAssignableFrom(cls)) {
            Pointer pointer5 = getPointer(j);
            if (pointer5 == null) {
                objValueOf = null;
            } else {
                Callback callback = (Callback) obj;
                if (!pointer5.equals(CallbackReference.getFunctionPointer(callback))) {
                    callback = CallbackReference.getCallback(cls, pointer5);
                }
                objValueOf = callback;
            }
        } else if (Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(cls)) {
            Pointer pointer6 = getPointer(j);
            if (pointer6 == null) {
                objValueOf = null;
            } else {
                Pointer directBufferPointer = obj == null ? null : Native.getDirectBufferPointer((Buffer) obj);
                if (directBufferPointer == null || !directBufferPointer.equals(pointer6)) {
                    throw new IllegalStateException("Can't autogenerate a direct buffer on memory read");
                }
                objValueOf = obj;
            }
        } else if (NativeMapped.class.isAssignableFrom(cls)) {
            NativeMapped nativeMapped = (NativeMapped) obj;
            if (nativeMapped != null) {
                objFromNative = nativeMapped.fromNative(getValue(j, nativeMapped.nativeType(), null), new FromNativeContext(cls));
                if (nativeMapped.equals(objFromNative)) {
                    objValueOf = objFromNative;
                    objValueOf = nativeMapped;
                }
            } else {
                NativeMappedConverter nativeMappedConverter = NativeMappedConverter.getInstance(cls);
                objValueOf = nativeMappedConverter.fromNative(getValue(j, nativeMappedConverter.nativeType(), null), new FromNativeContext(cls));
            }
        } else if (cls.isArray()) {
            Object obj2 = obj;
            if (obj2 == null) {
                throw new IllegalStateException("Need an initialized array");
            }
            readArray(j, obj2, cls.getComponentType());
            objValueOf = obj2;
        } else {
            throw new IllegalArgumentException("Reading \"" + cls + "\" from memory is not supported");
        }
        objValueOf = objFromNative;
        return objValueOf;
    }

    private void readArray(long offset, Object o, Class<?> cls) {
        int length = Array.getLength(o);
        if (cls == Byte.TYPE) {
            read(offset, (byte[]) o, 0, length);
            return;
        }
        if (cls == Short.TYPE) {
            read(offset, (short[]) o, 0, length);
            return;
        }
        if (cls == Character.TYPE) {
            read(offset, (char[]) o, 0, length);
            return;
        }
        if (cls == Integer.TYPE) {
            read(offset, (int[]) o, 0, length);
            return;
        }
        if (cls == Long.TYPE) {
            read(offset, (long[]) o, 0, length);
            return;
        }
        if (cls == Float.TYPE) {
            read(offset, (float[]) o, 0, length);
            return;
        }
        if (cls == Double.TYPE) {
            read(offset, (double[]) o, 0, length);
            return;
        }
        if (Pointer.class.isAssignableFrom(cls)) {
            read(offset, (Pointer[]) o, 0, length);
            return;
        }
        if (Structure.class.isAssignableFrom(cls)) {
            Structure[] sarray = (Structure[]) o;
            if (Structure.ByReference.class.isAssignableFrom(cls)) {
                Pointer[] parray = getPointerArray(offset, sarray.length);
                for (int i = 0; i < sarray.length; i++) {
                    sarray[i] = Structure.updateStructureByReference(cls, sarray[i], parray[i]);
                }
                return;
            }
            Structure first = sarray[0];
            if (first == null) {
                first = Structure.newInstance((Class<Structure>) cls, share(offset));
                first.conditionalAutoRead();
                sarray[0] = first;
            } else {
                first.useMemory(this, (int) offset, true);
                first.read();
            }
            Structure[] tmp = first.toArray(sarray.length);
            for (int i2 = 1; i2 < sarray.length; i2++) {
                if (sarray[i2] == null) {
                    sarray[i2] = tmp[i2];
                } else {
                    sarray[i2].useMemory(this, (int) (offset + ((long) (i2 * sarray[i2].size()))), true);
                    sarray[i2].read();
                }
            }
            return;
        }
        if (NativeMapped.class.isAssignableFrom(cls)) {
            NativeMapped[] array = (NativeMapped[]) o;
            NativeMappedConverter tc = NativeMappedConverter.getInstance(cls);
            int size = Native.getNativeSize(o.getClass(), o) / array.length;
            for (int i3 = 0; i3 < array.length; i3++) {
                Object value = getValue(offset + ((long) (size * i3)), tc.nativeType(), array[i3]);
                array[i3] = (NativeMapped) tc.fromNative(value, new FromNativeContext(cls));
            }
            return;
        }
        throw new IllegalArgumentException("Reading array of " + cls + " from memory not supported");
    }

    public byte getByte(long offset) {
        return Native.getByte(this, this.peer, offset);
    }

    public char getChar(long offset) {
        return Native.getChar(this, this.peer, offset);
    }

    public short getShort(long offset) {
        return Native.getShort(this, this.peer, offset);
    }

    public int getInt(long offset) {
        return Native.getInt(this, this.peer, offset);
    }

    public long getLong(long offset) {
        return Native.getLong(this, this.peer, offset);
    }

    public NativeLong getNativeLong(long offset) {
        return new NativeLong(NativeLong.SIZE == 8 ? getLong(offset) : getInt(offset));
    }

    public float getFloat(long offset) {
        return Native.getFloat(this, this.peer, offset);
    }

    public double getDouble(long offset) {
        return Native.getDouble(this, this.peer, offset);
    }

    public Pointer getPointer(long offset) {
        return Native.getPointer(this.peer + offset);
    }

    public ByteBuffer getByteBuffer(long offset, long length) {
        return Native.getDirectByteBuffer(this, this.peer, offset, length).order(ByteOrder.nativeOrder());
    }

    public String getWideString(long offset) {
        return Native.getWideString(this, this.peer, offset);
    }

    public String getString(long offset) {
        return getString(offset, Native.getDefaultStringEncoding());
    }

    public String getString(long offset, String encoding) {
        return Native.getString(this, offset, encoding);
    }

    public byte[] getByteArray(long offset, int arraySize) {
        byte[] buf = new byte[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public char[] getCharArray(long offset, int arraySize) {
        char[] buf = new char[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public short[] getShortArray(long offset, int arraySize) {
        short[] buf = new short[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public int[] getIntArray(long offset, int arraySize) {
        int[] buf = new int[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public long[] getLongArray(long offset, int arraySize) {
        long[] buf = new long[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public float[] getFloatArray(long offset, int arraySize) {
        float[] buf = new float[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public double[] getDoubleArray(long offset, int arraySize) {
        double[] buf = new double[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public Pointer[] getPointerArray(long offset) {
        List<Pointer> array = new ArrayList<>();
        int addOffset = 0;
        Pointer pointer = getPointer(offset);
        while (true) {
            Pointer p = pointer;
            if (p != null) {
                array.add(p);
                addOffset += Native.POINTER_SIZE;
                pointer = getPointer(offset + ((long) addOffset));
            } else {
                return (Pointer[]) array.toArray(new Pointer[0]);
            }
        }
    }

    public Pointer[] getPointerArray(long offset, int arraySize) {
        Pointer[] buf = new Pointer[arraySize];
        read(offset, buf, 0, arraySize);
        return buf;
    }

    public String[] getStringArray(long offset) {
        return getStringArray(offset, -1, Native.getDefaultStringEncoding());
    }

    public String[] getStringArray(long offset, String encoding) {
        return getStringArray(offset, -1, encoding);
    }

    public String[] getStringArray(long offset, int length) {
        return getStringArray(offset, length, Native.getDefaultStringEncoding());
    }

    public String[] getWideStringArray(long offset) {
        return getWideStringArray(offset, -1);
    }

    public String[] getWideStringArray(long offset, int length) {
        return getStringArray(offset, length, "--WIDE-STRING--");
    }

    public String[] getStringArray(long offset, int length, String encoding) {
        String string;
        String wideString;
        List<String> strings = new ArrayList<>();
        int addOffset = 0;
        if (length == -1) {
            while (true) {
                Pointer p = getPointer(offset + ((long) addOffset));
                if (p == null) {
                    break;
                }
                if ("--WIDE-STRING--".equals(encoding)) {
                    string = p.getWideString(0L);
                } else {
                    string = p.getString(0L, encoding);
                }
                String s = string;
                strings.add(s);
                addOffset += Native.POINTER_SIZE;
            }
        } else {
            Pointer p2 = getPointer(offset + ((long) 0));
            int count = 0;
            while (true) {
                int i = count;
                count++;
                if (i >= length) {
                    break;
                }
                if (p2 == null) {
                    wideString = null;
                } else {
                    wideString = "--WIDE-STRING--".equals(encoding) ? p2.getWideString(0L) : p2.getString(0L, encoding);
                }
                String s2 = wideString;
                strings.add(s2);
                if (count < length) {
                    addOffset += Native.POINTER_SIZE;
                    p2 = getPointer(offset + ((long) addOffset));
                }
            }
        }
        return (String[]) strings.toArray(new String[0]);
    }

    void setValue(long offset, Object value, Class<?> type) {
        if (type == Boolean.TYPE || type == Boolean.class) {
            setInt(offset, Boolean.TRUE.equals(value) ? -1 : 0);
            return;
        }
        if (type == Byte.TYPE || type == Byte.class) {
            setByte(offset, value == null ? (byte) 0 : ((Byte) value).byteValue());
            return;
        }
        if (type == Short.TYPE || type == Short.class) {
            setShort(offset, value == null ? (short) 0 : ((Short) value).shortValue());
            return;
        }
        if (type == Character.TYPE || type == Character.class) {
            setChar(offset, value == null ? (char) 0 : ((Character) value).charValue());
            return;
        }
        if (type == Integer.TYPE || type == Integer.class) {
            setInt(offset, value == null ? 0 : ((Integer) value).intValue());
            return;
        }
        if (type == Long.TYPE || type == Long.class) {
            setLong(offset, value == null ? 0L : ((Long) value).longValue());
            return;
        }
        if (type == Float.TYPE || type == Float.class) {
            setFloat(offset, value == null ? ColumnText.GLOBAL_SPACE_CHAR_RATIO : ((Float) value).floatValue());
            return;
        }
        if (type == Double.TYPE || type == Double.class) {
            setDouble(offset, value == null ? 0.0d : ((Double) value).doubleValue());
            return;
        }
        if (type == Pointer.class) {
            setPointer(offset, (Pointer) value);
            return;
        }
        if (type == String.class) {
            setPointer(offset, (Pointer) value);
            return;
        }
        if (type == WString.class) {
            setPointer(offset, (Pointer) value);
            return;
        }
        if (Structure.class.isAssignableFrom(type)) {
            Structure s = (Structure) value;
            if (Structure.ByReference.class.isAssignableFrom(type)) {
                setPointer(offset, s == null ? null : s.getPointer());
                if (s != null) {
                    s.autoWrite();
                    return;
                }
                return;
            }
            s.useMemory(this, (int) offset, true);
            s.write();
            return;
        }
        if (Callback.class.isAssignableFrom(type)) {
            setPointer(offset, CallbackReference.getFunctionPointer((Callback) value));
            return;
        }
        if (Platform.HAS_BUFFERS && Buffer.class.isAssignableFrom(type)) {
            Pointer p = value == null ? null : Native.getDirectBufferPointer((Buffer) value);
            setPointer(offset, p);
        } else if (NativeMapped.class.isAssignableFrom(type)) {
            NativeMappedConverter tc = NativeMappedConverter.getInstance(type);
            Class<?> nativeType = tc.nativeType();
            setValue(offset, tc.toNative(value, new ToNativeContext()), nativeType);
        } else {
            if (type.isArray()) {
                writeArray(offset, value, type.getComponentType());
                return;
            }
            throw new IllegalArgumentException("Writing " + type + " to memory is not supported");
        }
    }

    private void writeArray(long offset, Object value, Class<?> cls) {
        if (cls == Byte.TYPE) {
            byte[] buf = (byte[]) value;
            write(offset, buf, 0, buf.length);
            return;
        }
        if (cls == Short.TYPE) {
            short[] buf2 = (short[]) value;
            write(offset, buf2, 0, buf2.length);
            return;
        }
        if (cls == Character.TYPE) {
            char[] buf3 = (char[]) value;
            write(offset, buf3, 0, buf3.length);
            return;
        }
        if (cls == Integer.TYPE) {
            int[] buf4 = (int[]) value;
            write(offset, buf4, 0, buf4.length);
            return;
        }
        if (cls == Long.TYPE) {
            long[] buf5 = (long[]) value;
            write(offset, buf5, 0, buf5.length);
            return;
        }
        if (cls == Float.TYPE) {
            float[] buf6 = (float[]) value;
            write(offset, buf6, 0, buf6.length);
            return;
        }
        if (cls == Double.TYPE) {
            double[] buf7 = (double[]) value;
            write(offset, buf7, 0, buf7.length);
            return;
        }
        if (Pointer.class.isAssignableFrom(cls)) {
            Pointer[] buf8 = (Pointer[]) value;
            write(offset, buf8, 0, buf8.length);
            return;
        }
        if (!Structure.class.isAssignableFrom(cls)) {
            if (NativeMapped.class.isAssignableFrom(cls)) {
                NativeMapped[] buf9 = (NativeMapped[]) value;
                NativeMappedConverter tc = NativeMappedConverter.getInstance(cls);
                Class<?> nativeType = tc.nativeType();
                int size = Native.getNativeSize(value.getClass(), value) / buf9.length;
                for (int i = 0; i < buf9.length; i++) {
                    Object element = tc.toNative(buf9[i], new ToNativeContext());
                    setValue(offset + ((long) (i * size)), element, nativeType);
                }
                return;
            }
            throw new IllegalArgumentException("Writing array of " + cls + " to memory not supported");
        }
        Structure[] sbuf = (Structure[]) value;
        if (Structure.ByReference.class.isAssignableFrom(cls)) {
            Pointer[] buf10 = new Pointer[sbuf.length];
            for (int i2 = 0; i2 < sbuf.length; i2++) {
                if (sbuf[i2] == null) {
                    buf10[i2] = null;
                } else {
                    buf10[i2] = sbuf[i2].getPointer();
                    sbuf[i2].write();
                }
            }
            write(offset, buf10, 0, buf10.length);
            return;
        }
        Structure first = sbuf[0];
        if (first == null) {
            first = Structure.newInstance((Class<Structure>) cls, share(offset));
            sbuf[0] = first;
        } else {
            first.useMemory(this, (int) offset, true);
        }
        first.write();
        Structure[] tmp = first.toArray(sbuf.length);
        for (int i3 = 1; i3 < sbuf.length; i3++) {
            if (sbuf[i3] == null) {
                sbuf[i3] = tmp[i3];
            } else {
                sbuf[i3].useMemory(this, (int) (offset + ((long) (i3 * sbuf[i3].size()))), true);
            }
            sbuf[i3].write();
        }
    }

    public void setMemory(long offset, long length, byte value) {
        Native.setMemory(this, this.peer, offset, length, value);
    }

    public void setByte(long offset, byte value) {
        Native.setByte(this, this.peer, offset, value);
    }

    public void setShort(long offset, short value) {
        Native.setShort(this, this.peer, offset, value);
    }

    public void setChar(long offset, char value) {
        Native.setChar(this, this.peer, offset, value);
    }

    public void setInt(long offset, int value) {
        Native.setInt(this, this.peer, offset, value);
    }

    public void setLong(long offset, long value) {
        Native.setLong(this, this.peer, offset, value);
    }

    public void setNativeLong(long offset, NativeLong value) {
        if (NativeLong.SIZE == 8) {
            setLong(offset, value.longValue());
        } else {
            setInt(offset, value.intValue());
        }
    }

    public void setFloat(long offset, float value) {
        Native.setFloat(this, this.peer, offset, value);
    }

    public void setDouble(long offset, double value) {
        Native.setDouble(this, this.peer, offset, value);
    }

    public void setPointer(long offset, Pointer value) {
        Native.setPointer(this, this.peer, offset, value != null ? value.peer : 0L);
    }

    public void setWideString(long offset, String value) {
        Native.setWideString(this, this.peer, offset, value);
    }

    public void setString(long offset, WString value) {
        setWideString(offset, value == null ? null : value.toString());
    }

    public void setString(long offset, String value) {
        setString(offset, value, Native.getDefaultStringEncoding());
    }

    public void setString(long offset, String value, String encoding) {
        byte[] data = Native.getBytes(value, encoding);
        write(offset, data, 0, data.length);
        setByte(offset + ((long) data.length), (byte) 0);
    }

    public String dump(long offset, int size) {
        StringWriter sw = new StringWriter("memory dump".length() + 2 + (size * 2) + ((size / 4) * 4));
        PrintWriter out = new PrintWriter(sw);
        out.println("memory dump");
        for (int i = 0; i < size; i++) {
            byte b = getByte(offset + ((long) i));
            if (i % 4 == 0) {
                out.print("[");
            }
            if (b >= 0 && b < 16) {
                out.print("0");
            }
            out.print(Integer.toHexString(b & 255));
            if (i % 4 == 3 && i < size - 1) {
                out.println("]");
            }
        }
        if (sw.getBuffer().charAt(sw.getBuffer().length() - 2) != ']') {
            out.println("]");
        }
        return sw.toString();
    }

    public String toString() {
        return "native@0x" + Long.toHexString(this.peer);
    }

    public static long nativeValue(Pointer p) {
        if (p == null) {
            return 0L;
        }
        return p.peer;
    }

    public static void nativeValue(Pointer p, long value) {
        p.peer = value;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/Pointer$Opaque.class */
    private static class Opaque extends Pointer {
        private final String MSG;

        private Opaque(long peer) {
            super(peer);
            this.MSG = "This pointer is opaque: " + this;
        }

        @Override // com.sun.jna.Pointer
        public Pointer share(long offset, long size) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void clear(long size) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public long indexOf(long offset, byte value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void read(long bOff, byte[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void read(long bOff, char[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void read(long bOff, short[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void read(long bOff, int[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void read(long bOff, long[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void read(long bOff, float[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void read(long bOff, double[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void read(long bOff, Pointer[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void write(long bOff, byte[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void write(long bOff, char[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void write(long bOff, short[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void write(long bOff, int[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void write(long bOff, long[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void write(long bOff, float[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void write(long bOff, double[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void write(long bOff, Pointer[] buf, int index, int length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public ByteBuffer getByteBuffer(long offset, long length) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public byte getByte(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public char getChar(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public short getShort(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public int getInt(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public long getLong(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public float getFloat(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public double getDouble(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public Pointer getPointer(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public String getString(long bOff, String encoding) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public String getWideString(long bOff) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void setByte(long bOff, byte value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void setChar(long bOff, char value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void setShort(long bOff, short value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void setInt(long bOff, int value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void setLong(long bOff, long value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void setFloat(long bOff, float value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void setDouble(long bOff, double value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void setPointer(long offset, Pointer value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void setString(long offset, String value, String encoding) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void setWideString(long offset, String value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public void setMemory(long offset, long size, byte value) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public String dump(long offset, int size) {
            throw new UnsupportedOperationException(this.MSG);
        }

        @Override // com.sun.jna.Pointer
        public String toString() {
            return "const@0x" + Long.toHexString(this.peer);
        }
    }
}
