package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/RunningObjectTable.class */
public class RunningObjectTable extends Unknown implements IRunningObjectTable {

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/RunningObjectTable$ByReference.class */
    public static class ByReference extends RunningObjectTable implements Structure.ByReference {
    }

    public RunningObjectTable() {
    }

    public RunningObjectTable(Pointer pointer) {
        super(pointer);
    }

    @Override // com.sun.jna.platform.win32.COM.IRunningObjectTable
    public WinNT.HRESULT Register(WinDef.DWORD grfFlags, Pointer punkObject, Pointer pmkObjectName, WinDef.DWORDByReference pdwRegister) {
        WinNT.HRESULT hr = (WinNT.HRESULT) _invokeNativeObject(3, new Object[]{getPointer(), grfFlags, punkObject, pmkObjectName, pdwRegister}, WinNT.HRESULT.class);
        return hr;
    }

    @Override // com.sun.jna.platform.win32.COM.IRunningObjectTable
    public WinNT.HRESULT Revoke(WinDef.DWORD dwRegister) {
        WinNT.HRESULT hr = (WinNT.HRESULT) _invokeNativeObject(4, new Object[]{getPointer(), dwRegister}, WinNT.HRESULT.class);
        return hr;
    }

    @Override // com.sun.jna.platform.win32.COM.IRunningObjectTable
    public WinNT.HRESULT IsRunning(Pointer pmkObjectName) {
        WinNT.HRESULT hr = (WinNT.HRESULT) _invokeNativeObject(5, new Object[]{getPointer(), pmkObjectName}, WinNT.HRESULT.class);
        return hr;
    }

    @Override // com.sun.jna.platform.win32.COM.IRunningObjectTable
    public WinNT.HRESULT GetObject(Pointer pmkObjectName, PointerByReference ppunkObject) {
        WinNT.HRESULT hr = (WinNT.HRESULT) _invokeNativeObject(6, new Object[]{getPointer(), pmkObjectName, ppunkObject}, WinNT.HRESULT.class);
        return hr;
    }

    @Override // com.sun.jna.platform.win32.COM.IRunningObjectTable
    public WinNT.HRESULT NoteChangeTime(WinDef.DWORD dwRegister, WinBase.FILETIME pfiletime) {
        WinNT.HRESULT hr = (WinNT.HRESULT) _invokeNativeObject(7, new Object[]{getPointer(), dwRegister, pfiletime}, WinNT.HRESULT.class);
        return hr;
    }

    @Override // com.sun.jna.platform.win32.COM.IRunningObjectTable
    public WinNT.HRESULT GetTimeOfLastChange(Pointer pmkObjectName, WinBase.FILETIME.ByReference pfiletime) {
        WinNT.HRESULT hr = (WinNT.HRESULT) _invokeNativeObject(8, new Object[]{getPointer(), pmkObjectName, pfiletime}, WinNT.HRESULT.class);
        return hr;
    }

    @Override // com.sun.jna.platform.win32.COM.IRunningObjectTable
    public WinNT.HRESULT EnumRunning(PointerByReference ppenumMoniker) {
        WinNT.HRESULT hr = (WinNT.HRESULT) _invokeNativeObject(9, new Object[]{getPointer(), ppenumMoniker}, WinNT.HRESULT.class);
        return hr;
    }
}
