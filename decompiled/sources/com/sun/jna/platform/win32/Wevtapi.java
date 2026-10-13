package com.sun.jna.platform.win32;

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Wevtapi.class */
public interface Wevtapi extends StdCallLibrary {
    public static final Wevtapi INSTANCE = (Wevtapi) Native.load("wevtapi", Wevtapi.class, W32APIOptions.UNICODE_OPTIONS);

    Winevt.EVT_HANDLE EvtOpenSession(int i, Winevt.EVT_RPC_LOGIN evt_rpc_login, int i2, int i3);

    boolean EvtClose(Winevt.EVT_HANDLE evt_handle);

    boolean EvtCancel(Winevt.EVT_HANDLE evt_handle);

    int EvtGetExtendedStatus(int i, char[] cArr, IntByReference intByReference);

    Winevt.EVT_HANDLE EvtQuery(Winevt.EVT_HANDLE evt_handle, String str, String str2, int i);

    boolean EvtNext(Winevt.EVT_HANDLE evt_handle, int i, Winevt.EVT_HANDLE[] evt_handleArr, int i2, int i3, IntByReference intByReference);

    boolean EvtSeek(Winevt.EVT_HANDLE evt_handle, long j, Winevt.EVT_HANDLE evt_handle2, int i, int i2);

    Winevt.EVT_HANDLE EvtSubscribe(Winevt.EVT_HANDLE evt_handle, Winevt.EVT_HANDLE evt_handle2, String str, String str2, Winevt.EVT_HANDLE evt_handle3, Pointer pointer, Callback callback, int i);

    Winevt.EVT_HANDLE EvtCreateRenderContext(int i, String[] strArr, int i2);

    boolean EvtRender(Winevt.EVT_HANDLE evt_handle, Winevt.EVT_HANDLE evt_handle2, int i, int i2, Pointer pointer, IntByReference intByReference, IntByReference intByReference2);

    boolean EvtFormatMessage(Winevt.EVT_HANDLE evt_handle, Winevt.EVT_HANDLE evt_handle2, int i, int i2, Winevt.EVT_VARIANT[] evt_variantArr, int i3, int i4, char[] cArr, IntByReference intByReference);

    Winevt.EVT_HANDLE EvtOpenLog(Winevt.EVT_HANDLE evt_handle, String str, int i);

    boolean EvtGetLogInfo(Winevt.EVT_HANDLE evt_handle, int i, int i2, Pointer pointer, IntByReference intByReference);

    boolean EvtClearLog(Winevt.EVT_HANDLE evt_handle, String str, String str2, int i);

    boolean EvtExportLog(Winevt.EVT_HANDLE evt_handle, String str, String str2, String str3, int i);

    boolean EvtArchiveExportedLog(Winevt.EVT_HANDLE evt_handle, String str, int i, int i2);

    Winevt.EVT_HANDLE EvtOpenChannelEnum(Winevt.EVT_HANDLE evt_handle, int i);

    boolean EvtNextChannelPath(Winevt.EVT_HANDLE evt_handle, int i, char[] cArr, IntByReference intByReference);

    Winevt.EVT_HANDLE EvtOpenChannelConfig(Winevt.EVT_HANDLE evt_handle, String str, int i);

    boolean EvtSaveChannelConfig(Winevt.EVT_HANDLE evt_handle, int i);

    boolean EvtSetChannelConfigProperty(Winevt.EVT_HANDLE evt_handle, int i, int i2, Winevt.EVT_VARIANT evt_variant);

    boolean EvtGetChannelConfigProperty(Winevt.EVT_HANDLE evt_handle, int i, int i2, int i3, Pointer pointer, IntByReference intByReference);

    Winevt.EVT_HANDLE EvtOpenPublisherEnum(Winevt.EVT_HANDLE evt_handle, int i);

    boolean EvtNextPublisherId(Winevt.EVT_HANDLE evt_handle, int i, char[] cArr, IntByReference intByReference);

    Winevt.EVT_HANDLE EvtOpenPublisherMetadata(Winevt.EVT_HANDLE evt_handle, String str, String str2, int i, int i2);

    boolean EvtGetPublisherMetadataProperty(Winevt.EVT_HANDLE evt_handle, int i, int i2, int i3, Pointer pointer, IntByReference intByReference);

    Winevt.EVT_HANDLE EvtOpenEventMetadataEnum(Winevt.EVT_HANDLE evt_handle, int i);

    Winevt.EVT_HANDLE EvtNextEventMetadata(Winevt.EVT_HANDLE evt_handle, int i);

    boolean EvtGetEventMetadataProperty(Winevt.EVT_HANDLE evt_handle, int i, int i2, int i3, Pointer pointer, IntByReference intByReference);

    boolean EvtGetObjectArraySize(Pointer pointer, IntByReference intByReference);

    boolean EvtGetObjectArrayProperty(Pointer pointer, int i, int i2, int i3, int i4, Pointer pointer2, IntByReference intByReference);

    boolean EvtGetQueryInfo(Winevt.EVT_HANDLE evt_handle, int i, int i2, Pointer pointer, IntByReference intByReference);

    Winevt.EVT_HANDLE EvtCreateBookmark(String str);

    boolean EvtUpdateBookmark(Winevt.EVT_HANDLE evt_handle, Winevt.EVT_HANDLE evt_handle2);

    boolean EvtGetEventInfo(Winevt.EVT_HANDLE evt_handle, int i, int i2, Pointer pointer, IntByReference intByReference);
}
