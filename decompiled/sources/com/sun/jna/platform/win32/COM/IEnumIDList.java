package com.sun.jna.platform.win32.COM;

import com.sun.jna.Function;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/IEnumIDList.class */
public interface IEnumIDList {
    public static final Guid.IID IID_IEnumIDList = new Guid.IID("{000214F2-0000-0000-C000-000000000046}");

    WinNT.HRESULT QueryInterface(Guid.REFIID refiid, PointerByReference pointerByReference);

    int AddRef();

    int Release();

    WinNT.HRESULT Next(int i, PointerByReference pointerByReference, IntByReference intByReference);

    WinNT.HRESULT Skip(int i);

    WinNT.HRESULT Reset();

    WinNT.HRESULT Clone(PointerByReference pointerByReference);

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/IEnumIDList$Converter.class */
    public static class Converter {
        public static IEnumIDList PointerToIEnumIDList(PointerByReference ptr) {
            final Pointer interfacePointer = ptr.getValue();
            Pointer vTablePointer = interfacePointer.getPointer(0L);
            final Pointer[] vTable = new Pointer[7];
            vTablePointer.read(0L, vTable, 0, 7);
            return new IEnumIDList() { // from class: com.sun.jna.platform.win32.COM.IEnumIDList.Converter.1
                @Override // com.sun.jna.platform.win32.COM.IEnumIDList
                public WinNT.HRESULT QueryInterface(Guid.REFIID byValue, PointerByReference pointerByReference) {
                    Function f = Function.getFunction(vTable[0], 63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, byValue, pointerByReference}));
                }

                @Override // com.sun.jna.platform.win32.COM.IEnumIDList
                public int AddRef() {
                    Function f = Function.getFunction(vTable[1], 63);
                    return f.invokeInt(new Object[]{interfacePointer});
                }

                @Override // com.sun.jna.platform.win32.COM.IEnumIDList
                public int Release() {
                    Function f = Function.getFunction(vTable[2], 63);
                    return f.invokeInt(new Object[]{interfacePointer});
                }

                @Override // com.sun.jna.platform.win32.COM.IEnumIDList
                public WinNT.HRESULT Next(int celt, PointerByReference rgelt, IntByReference pceltFetched) {
                    Function f = Function.getFunction(vTable[3], 63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, Integer.valueOf(celt), rgelt, pceltFetched}));
                }

                @Override // com.sun.jna.platform.win32.COM.IEnumIDList
                public WinNT.HRESULT Skip(int celt) {
                    Function f = Function.getFunction(vTable[4], 63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, Integer.valueOf(celt)}));
                }

                @Override // com.sun.jna.platform.win32.COM.IEnumIDList
                public WinNT.HRESULT Reset() {
                    Function f = Function.getFunction(vTable[5], 63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer}));
                }

                @Override // com.sun.jna.platform.win32.COM.IEnumIDList
                public WinNT.HRESULT Clone(PointerByReference ppenum) {
                    Function f = Function.getFunction(vTable[6], 63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, ppenum}));
                }
            };
        }
    }
}
