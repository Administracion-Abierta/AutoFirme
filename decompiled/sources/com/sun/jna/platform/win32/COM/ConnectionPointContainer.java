package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/ConnectionPointContainer.class */
public class ConnectionPointContainer extends Unknown implements IConnectionPointContainer {
    public ConnectionPointContainer(Pointer pointer) {
        super(pointer);
    }

    public WinNT.HRESULT EnumConnectionPoints() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sun.jna.platform.win32.COM.IConnectionPointContainer
    public WinNT.HRESULT FindConnectionPoint(Guid.REFIID riid, PointerByReference ppCP) {
        return (WinNT.HRESULT) _invokeNativeObject(4, new Object[]{getPointer(), riid, ppCP}, WinNT.HRESULT.class);
    }
}
