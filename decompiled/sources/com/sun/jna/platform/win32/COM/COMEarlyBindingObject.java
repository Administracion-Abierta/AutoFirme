package com.sun.jna.platform.win32.COM;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/COMEarlyBindingObject.class */
public class COMEarlyBindingObject extends COMBindingBaseObject implements IDispatch {
    public COMEarlyBindingObject(Guid.CLSID clsid, boolean useActiveInstance, int dwClsContext) {
        super(clsid, useActiveInstance, dwClsContext);
    }

    protected String getStringProperty(OaIdl.DISPID dispId) {
        Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
        oleMethod(2, result, dispId);
        return result.getValue().toString();
    }

    protected void setProperty(OaIdl.DISPID dispId, boolean value) {
        oleMethod(4, (Variant.VARIANT.ByReference) null, dispId, new Variant.VARIANT(value));
    }

    @Override // com.sun.jna.platform.win32.COM.IUnknown
    public WinNT.HRESULT QueryInterface(Guid.REFIID riid, PointerByReference ppvObject) {
        return getIDispatch().QueryInterface(riid, ppvObject);
    }

    @Override // com.sun.jna.platform.win32.COM.IUnknown
    public int AddRef() {
        return getIDispatch().AddRef();
    }

    @Override // com.sun.jna.platform.win32.COM.IUnknown
    public int Release() {
        return getIDispatch().Release();
    }

    @Override // com.sun.jna.platform.win32.COM.IDispatch
    public WinNT.HRESULT GetTypeInfoCount(WinDef.UINTByReference pctinfo) {
        return getIDispatch().GetTypeInfoCount(pctinfo);
    }

    @Override // com.sun.jna.platform.win32.COM.IDispatch
    public WinNT.HRESULT GetTypeInfo(WinDef.UINT iTInfo, WinDef.LCID lcid, PointerByReference ppTInfo) {
        return getIDispatch().GetTypeInfo(iTInfo, lcid, ppTInfo);
    }

    @Override // com.sun.jna.platform.win32.COM.IDispatch
    public WinNT.HRESULT GetIDsOfNames(Guid.REFIID riid, WString[] rgszNames, int cNames, WinDef.LCID lcid, OaIdl.DISPIDByReference rgDispId) {
        return getIDispatch().GetIDsOfNames(riid, rgszNames, cNames, lcid, rgDispId);
    }

    @Override // com.sun.jna.platform.win32.COM.IDispatch
    public WinNT.HRESULT Invoke(OaIdl.DISPID dispIdMember, Guid.REFIID riid, WinDef.LCID lcid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams, Variant.VARIANT.ByReference pVarResult, OaIdl.EXCEPINFO.ByReference pExcepInfo, IntByReference puArgErr) {
        return getIDispatch().Invoke(dispIdMember, riid, lcid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr);
    }
}
