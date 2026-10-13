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

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/IDispatch.class */
public interface IDispatch extends IUnknown {
    public static final Guid.IID IID_IDISPATCH = new Guid.IID("00020400-0000-0000-C000-000000000046");

    WinNT.HRESULT GetTypeInfoCount(WinDef.UINTByReference uINTByReference);

    WinNT.HRESULT GetTypeInfo(WinDef.UINT uint, WinDef.LCID lcid, PointerByReference pointerByReference);

    WinNT.HRESULT GetIDsOfNames(Guid.REFIID refiid, WString[] wStringArr, int i, WinDef.LCID lcid, OaIdl.DISPIDByReference dISPIDByReference);

    WinNT.HRESULT Invoke(OaIdl.DISPID dispid, Guid.REFIID refiid, WinDef.LCID lcid, WinDef.WORD word, OleAuto.DISPPARAMS.ByReference byReference, Variant.VARIANT.ByReference byReference2, OaIdl.EXCEPINFO.ByReference byReference3, IntByReference intByReference);
}
