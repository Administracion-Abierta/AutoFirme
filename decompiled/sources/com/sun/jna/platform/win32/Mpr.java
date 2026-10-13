package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Mpr.class */
public interface Mpr extends StdCallLibrary {
    public static final Mpr INSTANCE = (Mpr) Native.load("Mpr", Mpr.class, W32APIOptions.DEFAULT_OPTIONS);

    int WNetOpenEnum(int i, int i2, int i3, Winnetwk.NETRESOURCE.ByReference byReference, WinNT.HANDLEByReference hANDLEByReference);

    int WNetEnumResource(WinNT.HANDLE handle, IntByReference intByReference, Pointer pointer, IntByReference intByReference2);

    int WNetCloseEnum(WinNT.HANDLE handle);

    int WNetGetUniversalName(String str, int i, Pointer pointer, IntByReference intByReference);

    int WNetUseConnection(WinDef.HWND hwnd, Winnetwk.NETRESOURCE netresource, String str, String str2, int i, Pointer pointer, IntByReference intByReference, IntByReference intByReference2);

    int WNetAddConnection3(WinDef.HWND hwnd, Winnetwk.NETRESOURCE netresource, String str, String str2, int i);

    int WNetCancelConnection2(String str, int i, boolean z);
}
