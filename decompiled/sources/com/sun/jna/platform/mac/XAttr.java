package com.sun.jna.platform.mac;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/XAttr.class */
interface XAttr extends Library {
    public static final XAttr INSTANCE = (XAttr) Native.load((String) null, XAttr.class);
    public static final int XATTR_NOFOLLOW = 1;
    public static final int XATTR_CREATE = 2;
    public static final int XATTR_REPLACE = 4;
    public static final int XATTR_NOSECURITY = 8;
    public static final int XATTR_NODEFAULT = 16;
    public static final int XATTR_SHOWCOMPRESSION = 32;
    public static final int XATTR_MAXNAMELEN = 127;
    public static final String XATTR_FINDERINFO_NAME = "com.apple.FinderInfo";
    public static final String XATTR_RESOURCEFORK_NAME = "com.apple.ResourceFork";

    long getxattr(String str, String str2, Pointer pointer, long j, int i, int i2);

    int setxattr(String str, String str2, Pointer pointer, long j, int i, int i2);

    int removexattr(String str, String str2, int i);

    long listxattr(String str, Pointer pointer, long j, int i);
}
