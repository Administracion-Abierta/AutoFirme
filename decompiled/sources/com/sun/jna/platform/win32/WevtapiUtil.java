package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WevtapiUtil.class */
public abstract class WevtapiUtil {
    public static String EvtGetExtendedStatus() {
        IntByReference buffUsed = new IntByReference();
        int errorCode = Wevtapi.INSTANCE.EvtGetExtendedStatus(0, null, buffUsed);
        if (errorCode != 0 && errorCode != 122) {
            throw new Win32Exception(errorCode);
        }
        if (buffUsed.getValue() == 0) {
            return "";
        }
        char[] mem = new char[buffUsed.getValue()];
        int errorCode2 = Wevtapi.INSTANCE.EvtGetExtendedStatus(mem.length, mem, buffUsed);
        if (errorCode2 != 0) {
            throw new Win32Exception(errorCode2);
        }
        return Native.toString(mem);
    }

    public static Memory EvtRender(Winevt.EVT_HANDLE context, Winevt.EVT_HANDLE fragment, int flags, IntByReference propertyCount) {
        IntByReference buffUsed = new IntByReference();
        boolean result = Wevtapi.INSTANCE.EvtRender(context, fragment, flags, 0, null, buffUsed, propertyCount);
        int errorCode = Kernel32.INSTANCE.GetLastError();
        if (!result && errorCode != 122) {
            throw new Win32Exception(errorCode);
        }
        Memory mem = new Memory(buffUsed.getValue());
        boolean result2 = Wevtapi.INSTANCE.EvtRender(context, fragment, flags, (int) mem.size(), mem, buffUsed, propertyCount);
        if (!result2) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return mem;
    }

    public static String EvtFormatMessage(Winevt.EVT_HANDLE publisherMetadata, Winevt.EVT_HANDLE event, int messageId, int valueCount, Winevt.EVT_VARIANT[] values, int flags) {
        IntByReference bufferUsed = new IntByReference();
        boolean result = Wevtapi.INSTANCE.EvtFormatMessage(publisherMetadata, event, messageId, valueCount, values, flags, 0, null, bufferUsed);
        int errorCode = Kernel32.INSTANCE.GetLastError();
        if (!result && errorCode != 122) {
            throw new Win32Exception(errorCode);
        }
        char[] buffer = new char[bufferUsed.getValue()];
        boolean result2 = Wevtapi.INSTANCE.EvtFormatMessage(publisherMetadata, event, messageId, valueCount, values, flags, buffer.length, buffer, bufferUsed);
        if (!result2) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return Native.toString(buffer);
    }

    public static Winevt.EVT_VARIANT EvtGetChannelConfigProperty(Winevt.EVT_HANDLE channelHandle, int propertyId) {
        IntByReference propertyValueBufferUsed = new IntByReference();
        boolean result = Wevtapi.INSTANCE.EvtGetChannelConfigProperty(channelHandle, propertyId, 0, 0, null, propertyValueBufferUsed);
        int errorCode = Kernel32.INSTANCE.GetLastError();
        if (!result && errorCode != 122) {
            throw new Win32Exception(errorCode);
        }
        Memory propertyValueBuffer = new Memory(propertyValueBufferUsed.getValue());
        boolean result2 = Wevtapi.INSTANCE.EvtGetChannelConfigProperty(channelHandle, propertyId, 0, (int) propertyValueBuffer.size(), propertyValueBuffer, propertyValueBufferUsed);
        if (!result2) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        Winevt.EVT_VARIANT resultEvt = new Winevt.EVT_VARIANT(propertyValueBuffer);
        resultEvt.read();
        return resultEvt;
    }

    public static String EvtNextPublisherId(Winevt.EVT_HANDLE publisherEnum) {
        IntByReference publisherIdBufferUsed = new IntByReference();
        boolean result = Wevtapi.INSTANCE.EvtNextPublisherId(publisherEnum, 0, null, publisherIdBufferUsed);
        int errorCode = Kernel32.INSTANCE.GetLastError();
        if (!result && errorCode != 122) {
            throw new Win32Exception(errorCode);
        }
        char[] publisherIdBuffer = new char[publisherIdBufferUsed.getValue()];
        boolean result2 = Wevtapi.INSTANCE.EvtNextPublisherId(publisherEnum, publisherIdBuffer.length, publisherIdBuffer, publisherIdBufferUsed);
        if (!result2) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return Native.toString(publisherIdBuffer);
    }

    public static Memory EvtGetPublisherMetadataProperty(Winevt.EVT_HANDLE PublisherMetadata, int PropertyId, int Flags) {
        IntByReference publisherMetadataPropertyBufferUsed = new IntByReference();
        boolean result = Wevtapi.INSTANCE.EvtGetPublisherMetadataProperty(PublisherMetadata, PropertyId, Flags, 0, null, publisherMetadataPropertyBufferUsed);
        int errorCode = Kernel32.INSTANCE.GetLastError();
        if (!result && errorCode != 122) {
            throw new Win32Exception(errorCode);
        }
        Memory publisherMetadataPropertyBuffer = new Memory(publisherMetadataPropertyBufferUsed.getValue());
        boolean result2 = Wevtapi.INSTANCE.EvtGetPublisherMetadataProperty(PublisherMetadata, PropertyId, Flags, (int) publisherMetadataPropertyBuffer.size(), publisherMetadataPropertyBuffer, publisherMetadataPropertyBufferUsed);
        if (!result2) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return publisherMetadataPropertyBuffer;
    }
}
