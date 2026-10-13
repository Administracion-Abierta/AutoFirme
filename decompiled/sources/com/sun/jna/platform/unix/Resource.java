package com.sun.jna.platform.unix;

import com.sun.jna.Structure;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/Resource.class */
public interface Resource {
    public static final int RLIMIT_CPU = 0;
    public static final int RLIMIT_FSIZE = 1;
    public static final int RLIMIT_DATA = 2;
    public static final int RLIMIT_STACK = 3;
    public static final int RLIMIT_CORE = 4;
    public static final int RLIMIT_RSS = 5;
    public static final int RLIMIT_NOFILE = 7;
    public static final int RLIMIT_AS = 9;
    public static final int RLIMIT_NPROC = 6;
    public static final int RLIMIT_MEMLOCK = 8;
    public static final int RLIMIT_LOCKS = 10;
    public static final int RLIMIT_SIGPENDING = 11;
    public static final int RLIMIT_MSGQUEUE = 12;
    public static final int RLIMIT_NICE = 13;
    public static final int RLIMIT_RTPRIO = 14;
    public static final int RLIMIT_RTTIME = 15;
    public static final int RLIMIT_NLIMITS = 16;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/Resource$Rlimit.class */
    @Structure.FieldOrder({"rlim_cur", "rlim_max"})
    public static class Rlimit extends Structure {
        public long rlim_cur;
        public long rlim_max;
    }

    int getrlimit(int i, Rlimit rlimit);

    int setrlimit(int i, Rlimit rlimit);
}
