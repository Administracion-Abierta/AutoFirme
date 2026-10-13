package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/IConnectionPointContainer.class */
public interface IConnectionPointContainer extends IUnknown {
    public static final Guid.IID IID_IConnectionPointContainer = new Guid.IID("B196B284-BAB4-101A-B69C-00AA00341D07");

    WinNT.HRESULT FindConnectionPoint(Guid.REFIID refiid, PointerByReference pointerByReference);
}
