package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/UnknownVTable.class */
@Structure.FieldOrder({"QueryInterfaceCallback", "AddRefCallback", "ReleaseCallback"})
public class UnknownVTable extends Structure {
    public QueryInterfaceCallback QueryInterfaceCallback;
    public AddRefCallback AddRefCallback;
    public ReleaseCallback ReleaseCallback;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/UnknownVTable$AddRefCallback.class */
    public interface AddRefCallback extends StdCallLibrary.StdCallCallback {
        int invoke(Pointer pointer);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/UnknownVTable$ByReference.class */
    public static class ByReference extends UnknownVTable implements Structure.ByReference {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/UnknownVTable$QueryInterfaceCallback.class */
    public interface QueryInterfaceCallback extends StdCallLibrary.StdCallCallback {
        WinNT.HRESULT invoke(Pointer pointer, Guid.REFIID refiid, PointerByReference pointerByReference);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/UnknownVTable$ReleaseCallback.class */
    public interface ReleaseCallback extends StdCallLibrary.StdCallCallback {
        int invoke(Pointer pointer);
    }
}
