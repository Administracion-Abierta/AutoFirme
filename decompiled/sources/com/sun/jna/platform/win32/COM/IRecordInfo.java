package com.sun.jna.platform.win32.COM;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/IRecordInfo.class */
public interface IRecordInfo extends IUnknown {
    public static final Guid.IID IID_IRecordInfo = new Guid.IID("{0000002F-0000-0000-C000-000000000046}");

    WinNT.HRESULT RecordInit(WinDef.PVOID pvoid);

    WinNT.HRESULT RecordClear(WinDef.PVOID pvoid);

    WinNT.HRESULT RecordCopy(WinDef.PVOID pvoid, WinDef.PVOID pvoid2);

    WinNT.HRESULT GetGuid(Guid.GUID guid);

    WinNT.HRESULT GetName(WTypes.BSTR bstr);

    WinNT.HRESULT GetSize(WinDef.ULONG ulong);

    WinNT.HRESULT GetTypeInfo(ITypeInfo iTypeInfo);

    WinNT.HRESULT GetField(WinDef.PVOID pvoid, WString wString, Variant.VARIANT variant);

    WinNT.HRESULT GetFieldNoCopy(WinDef.PVOID pvoid, WString wString, Variant.VARIANT variant, WinDef.PVOID pvoid2);

    WinNT.HRESULT PutField(WinDef.ULONG ulong, WinDef.PVOID pvoid, WString wString, Variant.VARIANT variant);

    WinNT.HRESULT PutFieldNoCopy(WinDef.ULONG ulong, WinDef.PVOID pvoid, WString wString, Variant.VARIANT variant);

    WinNT.HRESULT GetFieldNames(WinDef.ULONG ulong, WTypes.BSTR bstr);

    WinDef.BOOL IsMatchingType(IRecordInfo iRecordInfo);

    WinDef.PVOID RecordCreate();

    WinNT.HRESULT RecordCreateCopy(WinDef.PVOID pvoid, WinDef.PVOID pvoid2);

    WinNT.HRESULT RecordDestroy(WinDef.PVOID pvoid);
}
