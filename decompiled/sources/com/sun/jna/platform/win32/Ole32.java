package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Ole32.class */
public interface Ole32 extends StdCallLibrary {
    public static final Ole32 INSTANCE = (Ole32) Native.load("Ole32", Ole32.class, W32APIOptions.DEFAULT_OPTIONS);
    public static final int COINIT_APARTMENTTHREADED = 2;
    public static final int COINIT_MULTITHREADED = 0;
    public static final int COINIT_DISABLE_OLE1DDE = 4;
    public static final int COINIT_SPEED_OVER_MEMORY = 8;
    public static final int RPC_C_AUTHN_LEVEL_DEFAULT = 0;
    public static final int RPC_C_AUTHN_WINNT = 10;
    public static final int RPC_C_IMP_LEVEL_IMPERSONATE = 3;
    public static final int RPC_C_AUTHZ_NONE = 0;
    public static final int RPC_C_AUTHN_LEVEL_CALL = 3;
    public static final int EOAC_NONE = 0;

    WinNT.HRESULT CoCreateGuid(Guid.GUID guid);

    int StringFromGUID2(Guid.GUID guid, char[] cArr, int i);

    WinNT.HRESULT IIDFromString(String str, Guid.GUID guid);

    WinNT.HRESULT CoInitialize(WinDef.LPVOID lpvoid);

    WinNT.HRESULT CoInitializeEx(Pointer pointer, int i);

    WinNT.HRESULT CoInitializeSecurity(WinNT.SECURITY_DESCRIPTOR security_descriptor, int i, Pointer pointer, Pointer pointer2, int i2, int i3, Pointer pointer3, int i4, Pointer pointer4);

    WinNT.HRESULT CoSetProxyBlanket(Unknown unknown, int i, int i2, WTypes.LPOLESTR lpolestr, int i3, int i4, Pointer pointer, int i5);

    void CoUninitialize();

    WinNT.HRESULT CoCreateInstance(Guid.GUID guid, Pointer pointer, int i, Guid.GUID guid2, PointerByReference pointerByReference);

    WinNT.HRESULT CLSIDFromProgID(String str, Guid.CLSID.ByReference byReference);

    WinNT.HRESULT CLSIDFromString(String str, Guid.CLSID.ByReference byReference);

    Pointer CoTaskMemAlloc(long j);

    Pointer CoTaskMemRealloc(Pointer pointer, long j);

    void CoTaskMemFree(Pointer pointer);

    WinNT.HRESULT CoGetMalloc(WinDef.DWORD dword, PointerByReference pointerByReference);

    WinNT.HRESULT GetRunningObjectTable(WinDef.DWORD dword, PointerByReference pointerByReference);

    WinNT.HRESULT CreateBindCtx(WinDef.DWORD dword, PointerByReference pointerByReference);

    boolean CoIsHandlerConnected(Pointer pointer);

    WinNT.HRESULT OleInitialize(Pointer pointer);

    void OleUninitialize();

    WinNT.HRESULT OleFlushClipboard();

    WinNT.HRESULT OleRun(Pointer pointer);
}
