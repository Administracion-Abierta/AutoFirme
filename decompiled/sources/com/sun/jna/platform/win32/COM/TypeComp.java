package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/TypeComp.class */
public class TypeComp extends Unknown {

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/TypeComp$ByReference.class */
    public static class ByReference extends TypeComp implements Structure.ByReference {
    }

    public TypeComp() {
    }

    public TypeComp(Pointer pvInstance) {
        super(pvInstance);
    }

    public WinNT.HRESULT Bind(WString szName, WinDef.ULONG lHashVal, WinDef.WORD wFlags, PointerByReference ppTInfo, OaIdl.DESCKIND.ByReference pDescKind, OaIdl.BINDPTR.ByReference pBindPtr) {
        return (WinNT.HRESULT) _invokeNativeObject(3, new Object[]{getPointer(), szName, lHashVal, wFlags, ppTInfo, pDescKind, pBindPtr}, WinNT.HRESULT.class);
    }

    public WinNT.HRESULT BindType(WString szName, WinDef.ULONG lHashVal, PointerByReference ppTInfo, PointerByReference ppTComp) {
        return (WinNT.HRESULT) _invokeNativeObject(4, new Object[]{getPointer(), szName, lHashVal, ppTInfo, ppTComp}, WinNT.HRESULT.class);
    }
}
