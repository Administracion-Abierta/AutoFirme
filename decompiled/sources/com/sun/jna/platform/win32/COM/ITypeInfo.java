package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/ITypeInfo.class */
public interface ITypeInfo extends IUnknown {
    WinNT.HRESULT GetTypeAttr(PointerByReference pointerByReference);

    WinNT.HRESULT GetTypeComp(PointerByReference pointerByReference);

    WinNT.HRESULT GetFuncDesc(WinDef.UINT uint, PointerByReference pointerByReference);

    WinNT.HRESULT GetVarDesc(WinDef.UINT uint, PointerByReference pointerByReference);

    WinNT.HRESULT GetNames(OaIdl.MEMBERID memberid, WTypes.BSTR[] bstrArr, WinDef.UINT uint, WinDef.UINTByReference uINTByReference);

    WinNT.HRESULT GetRefTypeOfImplType(WinDef.UINT uint, OaIdl.HREFTYPEByReference hREFTYPEByReference);

    WinNT.HRESULT GetImplTypeFlags(WinDef.UINT uint, IntByReference intByReference);

    WinNT.HRESULT GetIDsOfNames(WTypes.LPOLESTR[] lpolestrArr, WinDef.UINT uint, OaIdl.MEMBERID[] memberidArr);

    WinNT.HRESULT Invoke(WinDef.PVOID pvoid, OaIdl.MEMBERID memberid, WinDef.WORD word, OleAuto.DISPPARAMS.ByReference byReference, Variant.VARIANT.ByReference byReference2, OaIdl.EXCEPINFO.ByReference byReference3, WinDef.UINTByReference uINTByReference);

    WinNT.HRESULT GetDocumentation(OaIdl.MEMBERID memberid, WTypes.BSTRByReference bSTRByReference, WTypes.BSTRByReference bSTRByReference2, WinDef.DWORDByReference dWORDByReference, WTypes.BSTRByReference bSTRByReference3);

    WinNT.HRESULT GetDllEntry(OaIdl.MEMBERID memberid, OaIdl.INVOKEKIND invokekind, WTypes.BSTRByReference bSTRByReference, WTypes.BSTRByReference bSTRByReference2, WinDef.WORDByReference wORDByReference);

    WinNT.HRESULT GetRefTypeInfo(OaIdl.HREFTYPE hreftype, PointerByReference pointerByReference);

    WinNT.HRESULT AddressOfMember(OaIdl.MEMBERID memberid, OaIdl.INVOKEKIND invokekind, PointerByReference pointerByReference);

    WinNT.HRESULT CreateInstance(IUnknown iUnknown, Guid.REFIID refiid, PointerByReference pointerByReference);

    WinNT.HRESULT GetMops(OaIdl.MEMBERID memberid, WTypes.BSTRByReference bSTRByReference);

    WinNT.HRESULT GetContainingTypeLib(PointerByReference pointerByReference, WinDef.UINTByReference uINTByReference);

    void ReleaseTypeAttr(OaIdl.TYPEATTR typeattr);

    void ReleaseFuncDesc(OaIdl.FUNCDESC funcdesc);

    void ReleaseVarDesc(OaIdl.VARDESC vardesc);
}
