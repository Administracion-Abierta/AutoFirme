package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/TypeInfo.class */
public class TypeInfo extends Unknown implements ITypeInfo {

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/TypeInfo$ByReference.class */
    public static class ByReference extends TypeInfo implements Structure.ByReference {
    }

    public TypeInfo() {
    }

    public TypeInfo(Pointer pvInstance) {
        super(pvInstance);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeInfo
    public WinNT.HRESULT GetTypeAttr(PointerByReference ppTypeAttr) {
        return (WinNT.HRESULT) _invokeNativeObject(3, new Object[]{getPointer(), ppTypeAttr}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeInfo
    public WinNT.HRESULT GetTypeComp(PointerByReference ppTComp) {
        return (WinNT.HRESULT) _invokeNativeObject(4, new Object[]{getPointer(), ppTComp}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeInfo
    public WinNT.HRESULT GetFuncDesc(WinDef.UINT index, PointerByReference ppFuncDesc) {
        return (WinNT.HRESULT) _invokeNativeObject(5, new Object[]{getPointer(), index, ppFuncDesc}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeInfo
    public WinNT.HRESULT GetVarDesc(WinDef.UINT index, PointerByReference ppVarDesc) {
        return (WinNT.HRESULT) _invokeNativeObject(6, new Object[]{getPointer(), index, ppVarDesc}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeInfo
    public WinNT.HRESULT GetNames(OaIdl.MEMBERID memid, WTypes.BSTR[] rgBstrNames, WinDef.UINT cMaxNames, WinDef.UINTByReference pcNames) {
        return (WinNT.HRESULT) _invokeNativeObject(7, new Object[]{getPointer(), memid, rgBstrNames, cMaxNames, pcNames}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeInfo
    public WinNT.HRESULT GetRefTypeOfImplType(WinDef.UINT index, OaIdl.HREFTYPEByReference pRefType) {
        return (WinNT.HRESULT) _invokeNativeObject(8, new Object[]{getPointer(), index, pRefType}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeInfo
    public WinNT.HRESULT GetImplTypeFlags(WinDef.UINT index, IntByReference pImplTypeFlags) {
        return (WinNT.HRESULT) _invokeNativeObject(9, new Object[]{getPointer(), index, pImplTypeFlags}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeInfo
    public WinNT.HRESULT GetIDsOfNames(WTypes.LPOLESTR[] rgszNames, WinDef.UINT cNames, OaIdl.MEMBERID[] pMemId) {
        return (WinNT.HRESULT) _invokeNativeObject(10, new Object[]{getPointer(), rgszNames, cNames, pMemId}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeInfo
    public WinNT.HRESULT Invoke(WinDef.PVOID pvInstance, OaIdl.MEMBERID memid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams, Variant.VARIANT.ByReference pVarResult, OaIdl.EXCEPINFO.ByReference pExcepInfo, WinDef.UINTByReference puArgErr) {
        return (WinNT.HRESULT) _invokeNativeObject(11, new Object[]{getPointer(), pvInstance, memid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeInfo
    public WinNT.HRESULT GetDocumentation(OaIdl.MEMBERID memid, WTypes.BSTRByReference pBstrName, WTypes.BSTRByReference pBstrDocString, WinDef.DWORDByReference pdwHelpContext, WTypes.BSTRByReference pBstrHelpFile) {
        return (WinNT.HRESULT) _invokeNativeObject(12, new Object[]{getPointer(), memid, pBstrName, pBstrDocString, pdwHelpContext, pBstrHelpFile}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeInfo
    public WinNT.HRESULT GetDllEntry(OaIdl.MEMBERID memid, OaIdl.INVOKEKIND invKind, WTypes.BSTRByReference pBstrDllName, WTypes.BSTRByReference pBstrName, WinDef.WORDByReference pwOrdinal) {
        return (WinNT.HRESULT) _invokeNativeObject(13, new Object[]{getPointer(), memid, invKind, pBstrDllName, pBstrName, pwOrdinal}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeInfo
    public WinNT.HRESULT GetRefTypeInfo(OaIdl.HREFTYPE hRefType, PointerByReference ppTInfo) {
        return (WinNT.HRESULT) _invokeNativeObject(14, new Object[]{getPointer(), hRefType, ppTInfo}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeInfo
    public WinNT.HRESULT AddressOfMember(OaIdl.MEMBERID memid, OaIdl.INVOKEKIND invKind, PointerByReference ppv) {
        return (WinNT.HRESULT) _invokeNativeObject(15, new Object[]{getPointer(), memid, invKind, ppv}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeInfo
    public WinNT.HRESULT CreateInstance(IUnknown pUnkOuter, Guid.REFIID riid, PointerByReference ppvObj) {
        return (WinNT.HRESULT) _invokeNativeObject(16, new Object[]{getPointer(), pUnkOuter, riid, ppvObj}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeInfo
    public WinNT.HRESULT GetMops(OaIdl.MEMBERID memid, WTypes.BSTRByReference pBstrMops) {
        return (WinNT.HRESULT) _invokeNativeObject(17, new Object[]{getPointer(), memid, pBstrMops}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeInfo
    public WinNT.HRESULT GetContainingTypeLib(PointerByReference ppTLib, WinDef.UINTByReference pIndex) {
        return (WinNT.HRESULT) _invokeNativeObject(18, new Object[]{getPointer(), ppTLib, pIndex}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeInfo
    public void ReleaseTypeAttr(OaIdl.TYPEATTR pTypeAttr) {
        _invokeNativeVoid(19, new Object[]{getPointer(), pTypeAttr});
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeInfo
    public void ReleaseFuncDesc(OaIdl.FUNCDESC pFuncDesc) {
        _invokeNativeVoid(20, new Object[]{getPointer(), pFuncDesc});
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeInfo
    public void ReleaseVarDesc(OaIdl.VARDESC pVarDesc) {
        _invokeNativeVoid(21, new Object[]{getPointer(), pVarDesc});
    }
}
