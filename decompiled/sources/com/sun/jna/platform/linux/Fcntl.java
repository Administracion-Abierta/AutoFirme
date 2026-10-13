package com.sun.jna.platform.linux;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/linux/Fcntl.class */
public interface Fcntl {
    public static final int O_RDONLY = 0;
    public static final int O_WRONLY = 1;
    public static final int O_RDWR = 2;
    public static final int O_CREAT = 64;
    public static final int O_EXCL = 128;
    public static final int O_TRUNC = 512;
    public static final int S_IRUSR = 256;
    public static final int S_IWUSR = 128;
    public static final int S_IXUSR = 64;
    public static final int S_IRWXU = 448;
    public static final int S_IRGRP = 32;
    public static final int S_IWGRP = 16;
    public static final int S_IXGRP = 8;
    public static final int S_IRWXG = 56;
    public static final int S_IROTH = 4;
    public static final int S_IWOTH = 2;
    public static final int S_IXOTH = 1;
    public static final int S_IRWXO = 7;
    public static final int S_ISUID = 2048;
    public static final int S_ISGID = 1024;
    public static final int S_ISVTX = 512;
}
