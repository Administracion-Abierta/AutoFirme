package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/ConnectionPoint.class */
public class ConnectionPoint extends Unknown implements IConnectionPoint {
    public ConnectionPoint(Pointer pointer) {
        super(pointer);
    }

    @Override // com.sun.jna.platform.win32.COM.IConnectionPoint
    public WinNT.HRESULT GetConnectionInterface(Guid.IID iid) {
        return (WinNT.HRESULT) _invokeNativeObject(3, new Object[]{getPointer(), iid}, WinNT.HRESULT.class);
    }

    void GetConnectionPointContainer() {
    }

    @Override // com.sun.jna.platform.win32.COM.IConnectionPoint
    public WinNT.HRESULT Advise(IUnknownCallback pUnkSink, WinDef.DWORDByReference pdwCookie) {
        return (WinNT.HRESULT) _invokeNativeObject(5, new Object[]{getPointer(), pUnkSink.getPointer(), pdwCookie}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.IConnectionPoint
    public WinNT.HRESULT Unadvise(WinDef.DWORD dwCookie) {
        return (WinNT.HRESULT) _invokeNativeObject(6, new Object[]{getPointer(), dwCookie}, WinNT.HRESULT.class);
    }

    void EnumConnections() {
    }
}
