package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/IRunningObjectTable.class */
public interface IRunningObjectTable extends IUnknown {
    public static final Guid.IID IID = new Guid.IID("{00000010-0000-0000-C000-000000000046}");

    WinNT.HRESULT EnumRunning(PointerByReference pointerByReference);

    WinNT.HRESULT GetObject(Pointer pointer, PointerByReference pointerByReference);

    WinNT.HRESULT GetTimeOfLastChange(Pointer pointer, WinBase.FILETIME.ByReference byReference);

    WinNT.HRESULT IsRunning(Pointer pointer);

    WinNT.HRESULT NoteChangeTime(WinDef.DWORD dword, WinBase.FILETIME filetime);

    WinNT.HRESULT Register(WinDef.DWORD dword, Pointer pointer, Pointer pointer2, WinDef.DWORDByReference dWORDByReference);

    WinNT.HRESULT Revoke(WinDef.DWORD dword);
}
