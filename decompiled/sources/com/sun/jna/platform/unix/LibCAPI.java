package com.sun.jna.platform.unix;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/LibCAPI.class */
public interface LibCAPI extends Reboot, Resource {
    public static final int HOST_NAME_MAX = 255;

    int getuid();

    int geteuid();

    int getgid();

    int getegid();

    int setuid(int i);

    int seteuid(int i);

    int setgid(int i);

    int setegid(int i);

    int gethostname(byte[] bArr, int i);

    int sethostname(String str, int i);

    int getdomainname(byte[] bArr, int i);

    int setdomainname(String str, int i);

    String getenv(String str);

    int setenv(String str, String str2, int i);

    int unsetenv(String str);

    int getloadavg(double[] dArr, int i);

    int close(int i);

    int msync(Pointer pointer, size_t size_tVar, int i);

    int munmap(Pointer pointer, size_t size_tVar);

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/LibCAPI$size_t.class */
    public static class size_t extends IntegerType {
        public static final size_t ZERO = new size_t();
        private static final long serialVersionUID = 1;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/LibCAPI$size_t$ByReference.class */
        public static class ByReference extends com.sun.jna.ptr.ByReference {
            public ByReference() {
                this(0L);
            }

            public ByReference(long value) {
                this(new size_t(value));
            }

            public ByReference(size_t value) {
                super(Native.SIZE_T_SIZE);
                setValue(value);
            }

            public void setValue(long value) {
                setValue(new size_t(value));
            }

            public void setValue(size_t value) {
                if (Native.SIZE_T_SIZE > 4) {
                    getPointer().setLong(0L, value.longValue());
                } else {
                    getPointer().setInt(0L, value.intValue());
                }
            }

            public long longValue() {
                return Native.SIZE_T_SIZE > 4 ? getPointer().getLong(0L) : getPointer().getInt(0L);
            }

            public size_t getValue() {
                return new size_t(longValue());
            }
        }

        public size_t() {
            this(0L);
        }

        public size_t(long value) {
            super(Native.SIZE_T_SIZE, value, true);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/LibCAPI$ssize_t.class */
    public static class ssize_t extends IntegerType {
        public static final ssize_t ZERO = new ssize_t();
        private static final long serialVersionUID = 1;

        public ssize_t() {
            this(0L);
        }

        public ssize_t(long value) {
            super(Native.SIZE_T_SIZE, value, false);
        }
    }
}
