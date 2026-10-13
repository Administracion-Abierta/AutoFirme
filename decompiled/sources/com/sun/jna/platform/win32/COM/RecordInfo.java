package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/RecordInfo.class */
public class RecordInfo extends Unknown implements IRecordInfo {

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/RecordInfo$ByReference.class */
    public static class ByReference extends RecordInfo implements Structure.ByReference {
    }

    public RecordInfo() {
    }

    public RecordInfo(Pointer pvInstance) {
        super(pvInstance);
    }

    @Override // com.sun.jna.platform.win32.COM.IRecordInfo
    public WinNT.HRESULT RecordInit(WinDef.PVOID pvNew) {
        return null;
    }

    @Override // com.sun.jna.platform.win32.COM.IRecordInfo
    public WinNT.HRESULT RecordClear(WinDef.PVOID pvExisting) {
        return null;
    }

    @Override // com.sun.jna.platform.win32.COM.IRecordInfo
    public WinNT.HRESULT RecordCopy(WinDef.PVOID pvExisting, WinDef.PVOID pvNew) {
        return null;
    }

    @Override // com.sun.jna.platform.win32.COM.IRecordInfo
    public WinNT.HRESULT GetGuid(Guid.GUID pguid) {
        return null;
    }

    @Override // com.sun.jna.platform.win32.COM.IRecordInfo
    public WinNT.HRESULT GetName(WTypes.BSTR pbstrName) {
        return null;
    }

    @Override // com.sun.jna.platform.win32.COM.IRecordInfo
    public WinNT.HRESULT GetSize(WinDef.ULONG pcbSize) {
        return null;
    }

    @Override // com.sun.jna.platform.win32.COM.IRecordInfo
    public WinNT.HRESULT GetTypeInfo(ITypeInfo ppTypeInfo) {
        return null;
    }

    @Override // com.sun.jna.platform.win32.COM.IRecordInfo
    public WinNT.HRESULT GetField(WinDef.PVOID pvData, WString szFieldName, Variant.VARIANT pvarField) {
        return null;
    }

    @Override // com.sun.jna.platform.win32.COM.IRecordInfo
    public WinNT.HRESULT GetFieldNoCopy(WinDef.PVOID pvData, WString szFieldName, Variant.VARIANT pvarField, WinDef.PVOID ppvDataCArray) {
        return null;
    }

    @Override // com.sun.jna.platform.win32.COM.IRecordInfo
    public WinNT.HRESULT PutField(WinDef.ULONG wFlags, WinDef.PVOID pvData, WString szFieldName, Variant.VARIANT pvarField) {
        return null;
    }

    @Override // com.sun.jna.platform.win32.COM.IRecordInfo
    public WinNT.HRESULT PutFieldNoCopy(WinDef.ULONG wFlags, WinDef.PVOID pvData, WString szFieldName, Variant.VARIANT pvarField) {
        return null;
    }

    @Override // com.sun.jna.platform.win32.COM.IRecordInfo
    public WinNT.HRESULT GetFieldNames(WinDef.ULONG pcNames, WTypes.BSTR rgBstrNames) {
        return null;
    }

    @Override // com.sun.jna.platform.win32.COM.IRecordInfo
    public WinDef.BOOL IsMatchingType(IRecordInfo pRecordInfo) {
        return null;
    }

    @Override // com.sun.jna.platform.win32.COM.IRecordInfo
    public WinDef.PVOID RecordCreate() {
        return null;
    }

    @Override // com.sun.jna.platform.win32.COM.IRecordInfo
    public WinNT.HRESULT RecordCreateCopy(WinDef.PVOID pvSource, WinDef.PVOID ppvDest) {
        return null;
    }

    @Override // com.sun.jna.platform.win32.COM.IRecordInfo
    public WinNT.HRESULT RecordDestroy(WinDef.PVOID pvRecord) {
        return null;
    }
}
