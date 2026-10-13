package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/DispatchVTable.class */
@Structure.FieldOrder({"QueryInterfaceCallback", "AddRefCallback", "ReleaseCallback", "GetTypeInfoCountCallback", "GetTypeInfoCallback", "GetIDsOfNamesCallback", "InvokeCallback"})
public class DispatchVTable extends Structure {
    public QueryInterfaceCallback QueryInterfaceCallback;
    public AddRefCallback AddRefCallback;
    public ReleaseCallback ReleaseCallback;
    public GetTypeInfoCountCallback GetTypeInfoCountCallback;
    public GetTypeInfoCallback GetTypeInfoCallback;
    public GetIDsOfNamesCallback GetIDsOfNamesCallback;
    public InvokeCallback InvokeCallback;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/DispatchVTable$AddRefCallback.class */
    public interface AddRefCallback extends StdCallLibrary.StdCallCallback {
        int invoke(Pointer pointer);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/DispatchVTable$ByReference.class */
    public static class ByReference extends DispatchVTable implements Structure.ByReference {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/DispatchVTable$GetIDsOfNamesCallback.class */
    public interface GetIDsOfNamesCallback extends StdCallLibrary.StdCallCallback {
        WinNT.HRESULT invoke(Pointer pointer, Guid.REFIID refiid, WString[] wStringArr, int i, WinDef.LCID lcid, OaIdl.DISPIDByReference dISPIDByReference);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/DispatchVTable$GetTypeInfoCallback.class */
    public interface GetTypeInfoCallback extends StdCallLibrary.StdCallCallback {
        WinNT.HRESULT invoke(Pointer pointer, WinDef.UINT uint, WinDef.LCID lcid, PointerByReference pointerByReference);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/DispatchVTable$GetTypeInfoCountCallback.class */
    public interface GetTypeInfoCountCallback extends StdCallLibrary.StdCallCallback {
        WinNT.HRESULT invoke(Pointer pointer, WinDef.UINTByReference uINTByReference);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/DispatchVTable$InvokeCallback.class */
    public interface InvokeCallback extends StdCallLibrary.StdCallCallback {
        WinNT.HRESULT invoke(Pointer pointer, OaIdl.DISPID dispid, Guid.REFIID refiid, WinDef.LCID lcid, WinDef.WORD word, OleAuto.DISPPARAMS.ByReference byReference, Variant.VARIANT.ByReference byReference2, OaIdl.EXCEPINFO.ByReference byReference3, IntByReference intByReference);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/DispatchVTable$QueryInterfaceCallback.class */
    public interface QueryInterfaceCallback extends StdCallLibrary.StdCallCallback {
        WinNT.HRESULT invoke(Pointer pointer, Guid.REFIID refiid, PointerByReference pointerByReference);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/DispatchVTable$ReleaseCallback.class */
    public interface ReleaseCallback extends StdCallLibrary.StdCallCallback {
        int invoke(Pointer pointer);
    }
}
