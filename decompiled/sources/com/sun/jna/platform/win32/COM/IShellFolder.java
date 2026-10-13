package com.sun.jna.platform.win32.COM;

import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.ShTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/IShellFolder.class */
public interface IShellFolder {
    public static final Guid.IID IID_ISHELLFOLDER = new Guid.IID("{000214E6-0000-0000-C000-000000000046}");

    WinNT.HRESULT QueryInterface(Guid.REFIID refiid, PointerByReference pointerByReference);

    int AddRef();

    int Release();

    WinNT.HRESULT ParseDisplayName(WinDef.HWND hwnd, Pointer pointer, String str, IntByReference intByReference, PointerByReference pointerByReference, IntByReference intByReference2);

    WinNT.HRESULT EnumObjects(WinDef.HWND hwnd, int i, PointerByReference pointerByReference);

    WinNT.HRESULT BindToObject(Pointer pointer, Pointer pointer2, Guid.REFIID refiid, PointerByReference pointerByReference);

    WinNT.HRESULT BindToStorage(Pointer pointer, Pointer pointer2, Guid.REFIID refiid, PointerByReference pointerByReference);

    WinNT.HRESULT CompareIDs(WinDef.LPARAM lparam, Pointer pointer, Pointer pointer2);

    WinNT.HRESULT CreateViewObject(WinDef.HWND hwnd, Guid.REFIID refiid, PointerByReference pointerByReference);

    WinNT.HRESULT GetAttributesOf(int i, Pointer pointer, IntByReference intByReference);

    WinNT.HRESULT GetUIObjectOf(WinDef.HWND hwnd, int i, Pointer pointer, Guid.REFIID refiid, IntByReference intByReference, PointerByReference pointerByReference);

    WinNT.HRESULT GetDisplayNameOf(Pointer pointer, int i, ShTypes.STRRET strret);

    WinNT.HRESULT SetNameOf(WinDef.HWND hwnd, Pointer pointer, String str, int i, PointerByReference pointerByReference);

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/IShellFolder$Converter.class */
    public static class Converter {
        public static IShellFolder PointerToIShellFolder(PointerByReference ptr) {
            final Pointer interfacePointer = ptr.getValue();
            Pointer vTablePointer = interfacePointer.getPointer(0L);
            final Pointer[] vTable = new Pointer[13];
            vTablePointer.read(0L, vTable, 0, 13);
            return new IShellFolder() { // from class: com.sun.jna.platform.win32.COM.IShellFolder.Converter.1
                @Override // com.sun.jna.platform.win32.COM.IShellFolder
                public WinNT.HRESULT QueryInterface(Guid.REFIID byValue, PointerByReference pointerByReference) {
                    Function f = Function.getFunction(vTable[0], 63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, byValue, pointerByReference}));
                }

                @Override // com.sun.jna.platform.win32.COM.IShellFolder
                public int AddRef() {
                    Function f = Function.getFunction(vTable[1], 63);
                    return f.invokeInt(new Object[]{interfacePointer});
                }

                @Override // com.sun.jna.platform.win32.COM.IShellFolder
                public int Release() {
                    Function f = Function.getFunction(vTable[2], 63);
                    return f.invokeInt(new Object[]{interfacePointer});
                }

                @Override // com.sun.jna.platform.win32.COM.IShellFolder
                public WinNT.HRESULT ParseDisplayName(WinDef.HWND hwnd, Pointer pbc, String pszDisplayName, IntByReference pchEaten, PointerByReference ppidl, IntByReference pdwAttributes) {
                    Function f = Function.getFunction(vTable[3], 63);
                    char[] pszDisplayNameNative = Native.toCharArray(pszDisplayName);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, hwnd, pbc, pszDisplayNameNative, pchEaten, ppidl, pdwAttributes}));
                }

                @Override // com.sun.jna.platform.win32.COM.IShellFolder
                public WinNT.HRESULT EnumObjects(WinDef.HWND hwnd, int grfFlags, PointerByReference ppenumIDList) {
                    Function f = Function.getFunction(vTable[4], 63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, hwnd, Integer.valueOf(grfFlags), ppenumIDList}));
                }

                @Override // com.sun.jna.platform.win32.COM.IShellFolder
                public WinNT.HRESULT BindToObject(Pointer pidl, Pointer pbc, Guid.REFIID riid, PointerByReference ppv) {
                    Function f = Function.getFunction(vTable[5], 63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, pidl, pbc, riid, ppv}));
                }

                @Override // com.sun.jna.platform.win32.COM.IShellFolder
                public WinNT.HRESULT BindToStorage(Pointer pidl, Pointer pbc, Guid.REFIID riid, PointerByReference ppv) {
                    Function f = Function.getFunction(vTable[6], 63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, pidl, pbc, riid, ppv}));
                }

                @Override // com.sun.jna.platform.win32.COM.IShellFolder
                public WinNT.HRESULT CompareIDs(WinDef.LPARAM lParam, Pointer pidl1, Pointer pidl2) {
                    Function f = Function.getFunction(vTable[7], 63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, lParam, pidl1, pidl2}));
                }

                @Override // com.sun.jna.platform.win32.COM.IShellFolder
                public WinNT.HRESULT CreateViewObject(WinDef.HWND hwndOwner, Guid.REFIID riid, PointerByReference ppv) {
                    Function f = Function.getFunction(vTable[8], 63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, hwndOwner, riid, ppv}));
                }

                @Override // com.sun.jna.platform.win32.COM.IShellFolder
                public WinNT.HRESULT GetAttributesOf(int cidl, Pointer apidl, IntByReference rgfInOut) {
                    Function f = Function.getFunction(vTable[9], 63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, Integer.valueOf(cidl), apidl, rgfInOut}));
                }

                @Override // com.sun.jna.platform.win32.COM.IShellFolder
                public WinNT.HRESULT GetUIObjectOf(WinDef.HWND hwndOwner, int cidl, Pointer apidl, Guid.REFIID riid, IntByReference rgfReserved, PointerByReference ppv) {
                    Function f = Function.getFunction(vTable[10], 63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, hwndOwner, Integer.valueOf(cidl), apidl, riid, rgfReserved, ppv}));
                }

                @Override // com.sun.jna.platform.win32.COM.IShellFolder
                public WinNT.HRESULT GetDisplayNameOf(Pointer pidl, int flags, ShTypes.STRRET pName) {
                    Function f = Function.getFunction(vTable[11], 63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, pidl, Integer.valueOf(flags), pName}));
                }

                @Override // com.sun.jna.platform.win32.COM.IShellFolder
                public WinNT.HRESULT SetNameOf(WinDef.HWND hwnd, Pointer pidl, String pszName, int uFlags, PointerByReference ppidlOut) {
                    Function f = Function.getFunction(vTable[12], 63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, hwnd, pidl, pszName, Integer.valueOf(uFlags), ppidlOut}));
                }
            };
        }
    }
}
