package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/TypeLib.class */
public class TypeLib extends Unknown implements ITypeLib {

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/TypeLib$ByReference.class */
    public static class ByReference extends TypeLib implements Structure.ByReference {
    }

    public TypeLib() {
    }

    public TypeLib(Pointer pvInstance) {
        super(pvInstance);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeLib
    public WinDef.UINT GetTypeInfoCount() {
        return (WinDef.UINT) _invokeNativeObject(3, new Object[]{getPointer()}, WinDef.UINT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeLib
    public WinNT.HRESULT GetTypeInfo(WinDef.UINT index, PointerByReference pTInfo) {
        return (WinNT.HRESULT) _invokeNativeObject(4, new Object[]{getPointer(), index, pTInfo}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeLib
    public WinNT.HRESULT GetTypeInfoType(WinDef.UINT index, OaIdl.TYPEKIND.ByReference pTKind) {
        return (WinNT.HRESULT) _invokeNativeObject(5, new Object[]{getPointer(), index, pTKind}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeLib
    public WinNT.HRESULT GetTypeInfoOfGuid(Guid.GUID guid, PointerByReference pTinfo) {
        return (WinNT.HRESULT) _invokeNativeObject(6, new Object[]{getPointer(), guid, pTinfo}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeLib
    public WinNT.HRESULT GetLibAttr(PointerByReference ppTLibAttr) {
        return (WinNT.HRESULT) _invokeNativeObject(7, new Object[]{getPointer(), ppTLibAttr}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeLib
    public WinNT.HRESULT GetTypeComp(PointerByReference pTComp) {
        return (WinNT.HRESULT) _invokeNativeObject(8, new Object[]{getPointer(), pTComp}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeLib
    public WinNT.HRESULT GetDocumentation(int index, WTypes.BSTRByReference pBstrName, WTypes.BSTRByReference pBstrDocString, WinDef.DWORDByReference pdwHelpContext, WTypes.BSTRByReference pBstrHelpFile) {
        return (WinNT.HRESULT) _invokeNativeObject(9, new Object[]{getPointer(), Integer.valueOf(index), pBstrName, pBstrDocString, pdwHelpContext, pBstrHelpFile}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeLib
    public WinNT.HRESULT IsName(WTypes.LPOLESTR szNameBuf, WinDef.ULONG lHashVal, WinDef.BOOLByReference pfName) {
        return (WinNT.HRESULT) _invokeNativeObject(10, new Object[]{getPointer(), szNameBuf, lHashVal, pfName}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeLib
    public WinNT.HRESULT FindName(WTypes.LPOLESTR szNameBuf, WinDef.ULONG lHashVal, Pointer[] ppTInfo, OaIdl.MEMBERID[] rgMemId, WinDef.USHORTByReference pcFound) {
        return (WinNT.HRESULT) _invokeNativeObject(11, new Object[]{getPointer(), szNameBuf, lHashVal, ppTInfo, rgMemId, pcFound}, WinNT.HRESULT.class);
    }

    @Override // com.sun.jna.platform.win32.COM.ITypeLib
    public void ReleaseTLibAttr(OaIdl.TLIBATTR pTLibAttr) {
        _invokeNativeObject(12, new Object[]{getPointer(), pTLibAttr.getPointer()}, WinNT.HRESULT.class);
    }
}
