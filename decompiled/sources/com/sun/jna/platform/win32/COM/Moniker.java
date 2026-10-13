package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/Moniker.class */
public class Moniker extends Unknown implements IMoniker {
    static final int vTableIdStart = 7;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/Moniker$ByReference.class */
    public static class ByReference extends Moniker implements Structure.ByReference {
    }

    public Moniker() {
    }

    public Moniker(Pointer pointer) {
        super(pointer);
    }

    @Override // com.sun.jna.platform.win32.COM.IMoniker
    public void BindToObject() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sun.jna.platform.win32.COM.IMoniker
    public void BindToStorage() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sun.jna.platform.win32.COM.IMoniker
    public void Reduce() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sun.jna.platform.win32.COM.IMoniker
    public void ComposeWith() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sun.jna.platform.win32.COM.IMoniker
    public void Enum() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sun.jna.platform.win32.COM.IMoniker
    public void IsEqual() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sun.jna.platform.win32.COM.IMoniker
    public void Hash() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sun.jna.platform.win32.COM.IMoniker
    public void IsRunning() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sun.jna.platform.win32.COM.IMoniker
    public void GetTimeOfLastChange() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sun.jna.platform.win32.COM.IMoniker
    public void Inverse() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sun.jna.platform.win32.COM.IMoniker
    public void CommonPrefixWith() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sun.jna.platform.win32.COM.IMoniker
    public void RelativePathTo() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sun.jna.platform.win32.COM.IMoniker
    public String GetDisplayName(Pointer pbc, Pointer pmkToLeft) {
        PointerByReference ppszDisplayNameRef = new PointerByReference();
        WinNT.HRESULT hr = (WinNT.HRESULT) _invokeNativeObject(20, new Object[]{getPointer(), pbc, pmkToLeft, ppszDisplayNameRef}, WinNT.HRESULT.class);
        COMUtils.checkRC(hr);
        Pointer ppszDisplayName = ppszDisplayNameRef.getValue();
        if (ppszDisplayName == null) {
            return null;
        }
        WTypes.LPOLESTR oleStr = new WTypes.LPOLESTR(ppszDisplayName);
        String name = oleStr.getValue();
        Ole32.INSTANCE.CoTaskMemFree(ppszDisplayName);
        return name;
    }

    @Override // com.sun.jna.platform.win32.COM.IMoniker
    public void ParseDisplayName() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sun.jna.platform.win32.COM.IMoniker
    public void IsSystemMoniker() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sun.jna.platform.win32.COM.IPersistStream
    public boolean IsDirty() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sun.jna.platform.win32.COM.IPersistStream
    public void Load(IStream stm) {
        throw new UnsupportedOperationException();
    }

    @Override // com.sun.jna.platform.win32.COM.IPersistStream
    public void Save(IStream stm) {
        throw new UnsupportedOperationException();
    }

    @Override // com.sun.jna.platform.win32.COM.IPersistStream
    public void GetSizeMax() {
        throw new UnsupportedOperationException();
    }

    @Override // com.sun.jna.platform.win32.COM.IPersist
    public Guid.CLSID GetClassID() {
        throw new UnsupportedOperationException();
    }
}
