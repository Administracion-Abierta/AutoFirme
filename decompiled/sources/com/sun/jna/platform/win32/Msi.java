package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Msi.class */
public interface Msi extends StdCallLibrary {
    public static final Msi INSTANCE = (Msi) Native.load("msi", Msi.class, W32APIOptions.DEFAULT_OPTIONS);
    public static final int INSTALLSTATE_NOTUSED = -7;
    public static final int INSTALLSTATE_BADCONFIG = -6;
    public static final int INSTALLSTATE_INCOMPLETE = -5;
    public static final int INSTALLSTATE_SOURCEABSENT = -4;
    public static final int INSTALLSTATE_MOREDATA = -3;
    public static final int INSTALLSTATE_INVALIDARG = -2;
    public static final int INSTALLSTATE_UNKNOWN = -1;
    public static final int INSTALLSTATE_BROKEN = 0;
    public static final int INSTALLSTATE_ADVERTISED = 1;
    public static final int INSTALLSTATE_REMOVED = 1;
    public static final int INSTALLSTATE_ABSENT = 2;
    public static final int INSTALLSTATE_LOCAL = 3;
    public static final int INSTALLSTATE_SOURCE = 4;
    public static final int INSTALLSTATE_DEFAULT = 5;

    int MsiGetComponentPath(String str, String str2, char[] cArr, IntByReference intByReference);

    int MsiLocateComponent(String str, char[] cArr, IntByReference intByReference);

    int MsiGetProductCode(String str, char[] cArr);

    int MsiEnumComponents(WinDef.DWORD dword, char[] cArr);
}
