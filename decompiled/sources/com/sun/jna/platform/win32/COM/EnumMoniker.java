package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/EnumMoniker.class */
public class EnumMoniker extends Unknown implements IEnumMoniker {
    public EnumMoniker(Pointer pointer) {
        super(pointer);
    }

    @Override // com.sun.jna.platform.win32.COM.IEnumMoniker
    public WinNT.HRESULT Next(WinDef.ULONG celt, PointerByReference rgelt, WinDef.ULONGByReference pceltFetched) {
        WinNT.HRESULT hr = (WinNT.HRESULT) _invokeNativeObject(3, new Object[]{getPointer(), celt, rgelt, pceltFetched}, WinNT.HRESULT.class);
        return hr;
    }

    @Override // com.sun.jna.platform.win32.COM.IEnumMoniker
    public WinNT.HRESULT Skip(WinDef.ULONG celt) {
        WinNT.HRESULT hr = (WinNT.HRESULT) _invokeNativeObject(4, new Object[]{getPointer(), celt}, WinNT.HRESULT.class);
        return hr;
    }

    @Override // com.sun.jna.platform.win32.COM.IEnumMoniker
    public WinNT.HRESULT Reset() {
        WinNT.HRESULT hr = (WinNT.HRESULT) _invokeNativeObject(5, new Object[]{getPointer()}, WinNT.HRESULT.class);
        return hr;
    }

    @Override // com.sun.jna.platform.win32.COM.IEnumMoniker
    public WinNT.HRESULT Clone(PointerByReference ppenum) {
        WinNT.HRESULT hr = (WinNT.HRESULT) _invokeNativeObject(6, new Object[]{getPointer(), ppenum}, WinNT.HRESULT.class);
        return hr;
    }
}
