package com.sun.jna.platform.win32;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/BaseTSD.class */
public interface BaseTSD {

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/BaseTSD$LONG_PTR.class */
    public static class LONG_PTR extends IntegerType {
        public LONG_PTR() {
            this(0L);
        }

        public LONG_PTR(long value) {
            super(Native.POINTER_SIZE, value);
        }

        public Pointer toPointer() {
            return Pointer.createConstant(longValue());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/BaseTSD$SSIZE_T.class */
    public static class SSIZE_T extends LONG_PTR {
        public SSIZE_T() {
            this(0L);
        }

        public SSIZE_T(long value) {
            super(value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/BaseTSD$ULONG_PTR.class */
    public static class ULONG_PTR extends IntegerType {
        public ULONG_PTR() {
            this(0L);
        }

        public ULONG_PTR(long value) {
            super(Native.POINTER_SIZE, value, true);
        }

        public Pointer toPointer() {
            return Pointer.createConstant(longValue());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/BaseTSD$ULONG_PTRByReference.class */
    public static class ULONG_PTRByReference extends ByReference {
        public ULONG_PTRByReference() {
            this(new ULONG_PTR(0L));
        }

        public ULONG_PTRByReference(ULONG_PTR value) {
            super(Native.POINTER_SIZE);
            setValue(value);
        }

        public void setValue(ULONG_PTR value) {
            if (Native.POINTER_SIZE == 4) {
                getPointer().setInt(0L, value.intValue());
            } else {
                getPointer().setLong(0L, value.longValue());
            }
        }

        public ULONG_PTR getValue() {
            long j;
            if (Native.POINTER_SIZE == 4) {
                j = getPointer().getInt(0L);
            } else {
                j = getPointer().getLong(0L);
            }
            return new ULONG_PTR(j);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/BaseTSD$DWORD_PTR.class */
    public static class DWORD_PTR extends IntegerType {
        public DWORD_PTR() {
            this(0L);
        }

        public DWORD_PTR(long value) {
            super(Native.POINTER_SIZE, value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/BaseTSD$SIZE_T.class */
    public static class SIZE_T extends ULONG_PTR {
        public SIZE_T() {
            this(0L);
        }

        public SIZE_T(long value) {
            super(value);
        }
    }
}
