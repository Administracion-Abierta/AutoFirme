package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/UnknownListener.class */
@Structure.FieldOrder({"vtbl"})
public class UnknownListener extends Structure {
    public UnknownVTable.ByReference vtbl = constructVTable();

    public UnknownListener(IUnknownCallback callback) {
        initVTable(callback);
        super.write();
    }

    protected UnknownVTable.ByReference constructVTable() {
        return new UnknownVTable.ByReference();
    }

    protected void initVTable(final IUnknownCallback callback) {
        this.vtbl.QueryInterfaceCallback = new UnknownVTable.QueryInterfaceCallback() { // from class: com.sun.jna.platform.win32.COM.UnknownListener.1
            @Override // com.sun.jna.platform.win32.COM.UnknownVTable.QueryInterfaceCallback
            public WinNT.HRESULT invoke(Pointer thisPointer, Guid.REFIID refid, PointerByReference ppvObject) {
                return callback.QueryInterface(refid, ppvObject);
            }
        };
        this.vtbl.AddRefCallback = new UnknownVTable.AddRefCallback() { // from class: com.sun.jna.platform.win32.COM.UnknownListener.2
            @Override // com.sun.jna.platform.win32.COM.UnknownVTable.AddRefCallback
            public int invoke(Pointer thisPointer) {
                return callback.AddRef();
            }
        };
        this.vtbl.ReleaseCallback = new UnknownVTable.ReleaseCallback() { // from class: com.sun.jna.platform.win32.COM.UnknownListener.3
            @Override // com.sun.jna.platform.win32.COM.UnknownVTable.ReleaseCallback
            public int invoke(Pointer thisPointer) {
                return callback.Release();
            }
        };
    }
}
