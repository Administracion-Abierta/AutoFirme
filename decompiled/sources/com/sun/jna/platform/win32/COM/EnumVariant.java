package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/EnumVariant.class */
public class EnumVariant extends Unknown implements IEnumVariant {
    public static final Guid.IID IID = new Guid.IID("{00020404-0000-0000-C000-000000000046}");
    public static final Guid.REFIID REFIID = new Guid.REFIID(IID);

    public EnumVariant() {
    }

    public EnumVariant(Pointer p) {
        setPointer(p);
    }

    @Override // com.sun.jna.platform.win32.COM.IEnumVariant
    public Variant.VARIANT[] Next(int count) {
        Variant.VARIANT[] resultStaging = new Variant.VARIANT[count];
        IntByReference resultCount = new IntByReference();
        WinNT.HRESULT hresult = (WinNT.HRESULT) _invokeNativeObject(3, new Object[]{getPointer(), Integer.valueOf(resultStaging.length), resultStaging, resultCount}, WinNT.HRESULT.class);
        COMUtils.checkRC(hresult);
        Variant.VARIANT[] result = new Variant.VARIANT[resultCount.getValue()];
        System.arraycopy(resultStaging, 0, result, 0, resultCount.getValue());
        return result;
    }

    @Override // com.sun.jna.platform.win32.COM.IEnumVariant
    public void Skip(int count) {
        WinNT.HRESULT hresult = (WinNT.HRESULT) _invokeNativeObject(4, new Object[]{getPointer(), Integer.valueOf(count)}, WinNT.HRESULT.class);
        COMUtils.checkRC(hresult);
    }

    @Override // com.sun.jna.platform.win32.COM.IEnumVariant
    public void Reset() {
        WinNT.HRESULT hresult = (WinNT.HRESULT) _invokeNativeObject(5, new Object[]{getPointer()}, WinNT.HRESULT.class);
        COMUtils.checkRC(hresult);
    }

    @Override // com.sun.jna.platform.win32.COM.IEnumVariant
    public EnumVariant Clone() {
        PointerByReference pbr = new PointerByReference();
        WinNT.HRESULT hresult = (WinNT.HRESULT) _invokeNativeObject(6, new Object[]{getPointer(), pbr}, WinNT.HRESULT.class);
        COMUtils.checkRC(hresult);
        return new EnumVariant(pbr.getValue());
    }
}
