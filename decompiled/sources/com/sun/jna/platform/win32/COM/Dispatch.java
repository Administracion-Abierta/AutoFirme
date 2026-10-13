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

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/Dispatch.class */
public class Dispatch extends Unknown implements IDispatch {

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/Dispatch$ByReference.class */
    public static class ByReference extends Dispatch implements Structure.ByReference {
    }

    public Dispatch() {
    }

    public Dispatch(Pointer pvInstance) {
        super(pvInstance);
    }

    @Override // com.sun.jna.platform.win32.COM.IDispatch
    public WinNT.HRESULT GetTypeInfoCount(WinDef.UINTByReference pctinfo) {
        return (WinNT.HRESULT) _invokeNativeObject(3, new Object[]{getPointer(), pctinfo}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.IDispatch
    public WinNT.HRESULT GetTypeInfo(WinDef.UINT iTInfo, WinDef.LCID lcid, PointerByReference ppTInfo) {
        return (WinNT.HRESULT) _invokeNativeObject(4, new Object[]{getPointer(), iTInfo, lcid, ppTInfo}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.IDispatch
    public WinNT.HRESULT GetIDsOfNames(Guid.REFIID riid, WString[] rgszNames, int cNames, WinDef.LCID lcid, OaIdl.DISPIDByReference rgDispId) {
        return (WinNT.HRESULT) _invokeNativeObject(5, new Object[]{getPointer(), riid, rgszNames, Integer.valueOf(cNames), lcid, rgDispId}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.IDispatch
    public WinNT.HRESULT Invoke(OaIdl.DISPID dispIdMember, Guid.REFIID riid, WinDef.LCID lcid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams, Variant.VARIANT.ByReference pVarResult, OaIdl.EXCEPINFO.ByReference pExcepInfo, IntByReference puArgErr) {
        return (WinNT.HRESULT) _invokeNativeObject(6, new Object[]{getPointer(), dispIdMember, riid, lcid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr}, WinNT.HRESULT.class);
    }
}
