package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/IConnectionPoint.class */
public interface IConnectionPoint extends IUnknown {
    public static final Guid.IID IID_IConnectionPoint = new Guid.IID("B196B286-BAB4-101A-B69C-00AA00341D07");

    WinNT.HRESULT GetConnectionInterface(Guid.IID iid);

    WinNT.HRESULT Advise(IUnknownCallback iUnknownCallback, WinDef.DWORDByReference dWORDByReference);

    WinNT.HRESULT Unadvise(WinDef.DWORD dword);
}
