package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/IEnumMoniker.class */
public interface IEnumMoniker extends IUnknown {
    public static final Guid.IID IID = new Guid.IID("{00000102-0000-0000-C000-000000000046}");

    WinNT.HRESULT Clone(PointerByReference pointerByReference);

    WinNT.HRESULT Next(WinDef.ULONG ulong, PointerByReference pointerByReference, WinDef.ULONGByReference uLONGByReference);

    WinNT.HRESULT Reset();

    WinNT.HRESULT Skip(WinDef.ULONG ulong);
}
