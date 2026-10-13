package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/DispatchListener.class */
@Structure.FieldOrder({"vtbl"})
public class DispatchListener extends Structure {
    public DispatchVTable.ByReference vtbl = constructVTable();

    public DispatchListener(IDispatchCallback callback) {
        initVTable(callback);
        super.write();
    }

    protected DispatchVTable.ByReference constructVTable() {
        return new DispatchVTable.ByReference();
    }

    protected void initVTable(final IDispatchCallback callback) {
        this.vtbl.QueryInterfaceCallback = new DispatchVTable.QueryInterfaceCallback() { // from class: com.sun.jna.platform.win32.COM.DispatchListener.1
            @Override // com.sun.jna.platform.win32.COM.DispatchVTable.QueryInterfaceCallback
            public WinNT.HRESULT invoke(Pointer thisPointer, Guid.REFIID refid, PointerByReference ppvObject) {
                return callback.QueryInterface(refid, ppvObject);
            }
        };
        this.vtbl.AddRefCallback = new DispatchVTable.AddRefCallback() { // from class: com.sun.jna.platform.win32.COM.DispatchListener.2
            @Override // com.sun.jna.platform.win32.COM.DispatchVTable.AddRefCallback
            public int invoke(Pointer thisPointer) {
                return callback.AddRef();
            }
        };
        this.vtbl.ReleaseCallback = new DispatchVTable.ReleaseCallback() { // from class: com.sun.jna.platform.win32.COM.DispatchListener.3
            @Override // com.sun.jna.platform.win32.COM.DispatchVTable.ReleaseCallback
            public int invoke(Pointer thisPointer) {
                return callback.Release();
            }
        };
        this.vtbl.GetTypeInfoCountCallback = new DispatchVTable.GetTypeInfoCountCallback() { // from class: com.sun.jna.platform.win32.COM.DispatchListener.4
            @Override // com.sun.jna.platform.win32.COM.DispatchVTable.GetTypeInfoCountCallback
            public WinNT.HRESULT invoke(Pointer thisPointer, WinDef.UINTByReference pctinfo) {
                return callback.GetTypeInfoCount(pctinfo);
            }
        };
        this.vtbl.GetTypeInfoCallback = new DispatchVTable.GetTypeInfoCallback() { // from class: com.sun.jna.platform.win32.COM.DispatchListener.5
            @Override // com.sun.jna.platform.win32.COM.DispatchVTable.GetTypeInfoCallback
            public WinNT.HRESULT invoke(Pointer thisPointer, WinDef.UINT iTInfo, WinDef.LCID lcid, PointerByReference ppTInfo) {
                return callback.GetTypeInfo(iTInfo, lcid, ppTInfo);
            }
        };
        this.vtbl.GetIDsOfNamesCallback = new DispatchVTable.GetIDsOfNamesCallback() { // from class: com.sun.jna.platform.win32.COM.DispatchListener.6
            @Override // com.sun.jna.platform.win32.COM.DispatchVTable.GetIDsOfNamesCallback
            public WinNT.HRESULT invoke(Pointer thisPointer, Guid.REFIID riid, WString[] rgszNames, int cNames, WinDef.LCID lcid, OaIdl.DISPIDByReference rgDispId) {
                return callback.GetIDsOfNames(riid, rgszNames, cNames, lcid, rgDispId);
            }
        };
        this.vtbl.InvokeCallback = new DispatchVTable.InvokeCallback() { // from class: com.sun.jna.platform.win32.COM.DispatchListener.7
            @Override // com.sun.jna.platform.win32.COM.DispatchVTable.InvokeCallback
            public WinNT.HRESULT invoke(Pointer thisPointer, OaIdl.DISPID dispIdMember, Guid.REFIID riid, WinDef.LCID lcid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams, Variant.VARIANT.ByReference pVarResult, OaIdl.EXCEPINFO.ByReference pExcepInfo, IntByReference puArgErr) {
                return callback.Invoke(dispIdMember, riid, lcid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr);
            }
        };
    }
}
