package com.sun.jna.platform.linux;

import com.sun.jna.Pointer;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/linux/Mman.class */
public interface Mman {
    public static final int PROT_READ = 1;
    public static final int PROT_WRITE = 2;
    public static final int PROT_EXEC = 4;
    public static final int PROT_NONE = 0;
    public static final int PROT_GROWSDOWN = 16777216;
    public static final int PROT_GROWSUP = 33554432;
    public static final int MAP_SHARED = 1;
    public static final int MAP_PRIVATE = 2;
    public static final int MAP_SHARED_VALIDATE = 3;
    public static final int MAP_TYPE = 15;
    public static final int MAP_FILE = 0;
    public static final int MAP_FIXED = 16;
    public static final int MAP_ANONYMOUS = 32;
    public static final int MAP_ANON = 32;
    public static final int MAP_32BIT = 64;
    public static final int MAP_GROWSDOWN = 256;
    public static final int MAP_DENYWRITE = 2048;
    public static final int MAP_EXECUTABLE = 4096;
    public static final int MAP_LOCKED = 8192;
    public static final int MAP_NORESERVE = 16384;
    public static final int MAP_POPULATE = 32768;
    public static final int MAP_NONBLOCK = 65536;
    public static final int MAP_STACK = 131072;
    public static final int MAP_HUGETLB = 262144;
    public static final int MAP_SYNC = 524288;
    public static final int MAP_FIXED_NOREPLACE = 1048576;
    public static final Pointer MAP_FAILED = new Pointer(-1);
    public static final int MS_ASYNC = 1;
    public static final int MS_SYNC = 2;
    public static final int MS_INVALIDATE = 4;
}
