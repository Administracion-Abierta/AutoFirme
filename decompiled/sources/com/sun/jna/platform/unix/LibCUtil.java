package com.sun.jna.platform.unix;

import com.aowagie.text.ElementTags;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/LibCUtil.class */
public class LibCUtil {
    private static final NativeLibrary LIBC = NativeLibrary.getInstance("c");
    private static Function mmap;
    private static boolean mmap64;
    private static Function ftruncate;
    private static boolean ftruncate64;

    static {
        mmap = null;
        mmap64 = false;
        ftruncate = null;
        ftruncate64 = false;
        try {
            mmap = LIBC.getFunction("mmap64", 64);
            mmap64 = true;
        } catch (UnsatisfiedLinkError e) {
            mmap = LIBC.getFunction("mmap", 64);
        }
        try {
            ftruncate = LIBC.getFunction("ftruncate64", 64);
            ftruncate64 = true;
        } catch (UnsatisfiedLinkError e2) {
            ftruncate = LIBC.getFunction("ftruncate", 64);
        }
    }

    private LibCUtil() {
    }

    public static Pointer mmap(Pointer addr, long length, int prot, int flags, int fd, long offset) {
        Object[] params = new Object[6];
        params[0] = addr;
        if (Native.SIZE_T_SIZE == 4) {
            require32Bit(length, XSDatatype.FACET_LENGTH);
            params[1] = Integer.valueOf((int) length);
        } else {
            params[1] = Long.valueOf(length);
        }
        params[2] = Integer.valueOf(prot);
        params[3] = Integer.valueOf(flags);
        params[4] = Integer.valueOf(fd);
        if (mmap64 || Native.LONG_SIZE > 4) {
            params[5] = Long.valueOf(offset);
        } else {
            require32Bit(offset, ElementTags.OFFSET);
            params[5] = Integer.valueOf((int) offset);
        }
        return mmap.invokePointer(params);
    }

    public static int ftruncate(int fd, long length) {
        Object[] params = new Object[2];
        params[0] = Integer.valueOf(fd);
        if (ftruncate64 || Native.LONG_SIZE > 4) {
            params[1] = Long.valueOf(length);
        } else {
            require32Bit(length, XSDatatype.FACET_LENGTH);
            params[1] = Integer.valueOf((int) length);
        }
        return ftruncate.invokeInt(params);
    }

    public static void require32Bit(long val, String value) {
        if (val > 2147483647L) {
            throw new IllegalArgumentException(value + " exceeds 32bit");
        }
    }
}
