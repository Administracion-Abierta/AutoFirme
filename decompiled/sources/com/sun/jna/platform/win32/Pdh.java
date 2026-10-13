package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Pdh.class */
public interface Pdh extends StdCallLibrary {
    public static final Pdh INSTANCE = (Pdh) Native.load("Pdh", Pdh.class, W32APIOptions.DEFAULT_OPTIONS);
    public static final int PDH_MAX_COUNTER_NAME = 1024;
    public static final int PDH_MAX_INSTANCE_NAME = 1024;
    public static final int PDH_MAX_COUNTER_PATH = 2048;
    public static final int PDH_MAX_DATASOURCE_PATH = 1024;
    public static final int PDH_MORE_DATA = -2147481646;
    public static final int PDH_INSUFFICIENT_BUFFER = -1073738814;
    public static final int PDH_INVALID_ARGUMENT = -1073738819;
    public static final int PDH_MEMORY_ALLOCATION_FAILURE = -1073738821;
    public static final int PDH_CSTATUS_NO_MACHINE = -2147481648;
    public static final int PDH_CSTATUS_NO_OBJECT = -1073738824;
    public static final int PDH_CVERSION_WIN40 = 1024;
    public static final int PDH_CVERSION_WIN50 = 1280;
    public static final int PDH_VERSION = 1283;
    public static final int PDH_PATH_WBEM_RESULT = 1;
    public static final int PDH_PATH_WBEM_INPUT = 2;
    public static final int PDH_FMT_RAW = 16;
    public static final int PDH_FMT_ANSI = 32;
    public static final int PDH_FMT_UNICODE = 64;
    public static final int PDH_FMT_LONG = 256;
    public static final int PDH_FMT_DOUBLE = 512;
    public static final int PDH_FMT_LARGE = 1024;
    public static final int PDH_FMT_NOSCALE = 4096;
    public static final int PDH_FMT_1000 = 8192;
    public static final int PDH_FMT_NODATA = 16384;
    public static final int PDH_FMT_NOCAP100 = 32768;
    public static final int PERF_DETAIL_COSTLY = 65536;
    public static final int PERF_DETAIL_STANDARD = 65535;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Pdh$PDH_COUNTER_PATH_ELEMENTS.class */
    @Structure.FieldOrder({"szMachineName", "szObjectName", "szInstanceName", "szParentInstance", "dwInstanceIndex", "szCounterName"})
    public static class PDH_COUNTER_PATH_ELEMENTS extends Structure {
        public String szMachineName;
        public String szObjectName;
        public String szInstanceName;
        public String szParentInstance;
        public int dwInstanceIndex;
        public String szCounterName;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Pdh$PDH_RAW_COUNTER.class */
    @Structure.FieldOrder({"CStatus", "TimeStamp", "FirstValue", "SecondValue", "MultiCount"})
    public static class PDH_RAW_COUNTER extends Structure {
        public int CStatus;
        public WinBase.FILETIME TimeStamp = new WinBase.FILETIME();
        public long FirstValue;
        public long SecondValue;
        public int MultiCount;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Pdh$PDH_TIME_INFO.class */
    @Structure.FieldOrder({"StartTime", "EndTime", "SampleCount"})
    public static class PDH_TIME_INFO extends Structure {
        public long StartTime;
        public long EndTime;
        public int SampleCount;
    }

    int PdhConnectMachine(String str);

    int PdhGetDllVersion(WinDef.DWORDByReference dWORDByReference);

    int PdhOpenQuery(String str, BaseTSD.DWORD_PTR dword_ptr, WinNT.HANDLEByReference hANDLEByReference);

    int PdhCloseQuery(WinNT.HANDLE handle);

    int PdhMakeCounterPath(PDH_COUNTER_PATH_ELEMENTS pdh_counter_path_elements, char[] cArr, WinDef.DWORDByReference dWORDByReference, int i);

    int PdhAddCounter(WinNT.HANDLE handle, String str, BaseTSD.DWORD_PTR dword_ptr, WinNT.HANDLEByReference hANDLEByReference);

    int PdhAddEnglishCounter(WinNT.HANDLE handle, String str, BaseTSD.DWORD_PTR dword_ptr, WinNT.HANDLEByReference hANDLEByReference);

    int PdhRemoveCounter(WinNT.HANDLE handle);

    int PdhGetRawCounterValue(WinNT.HANDLE handle, WinDef.DWORDByReference dWORDByReference, PDH_RAW_COUNTER pdh_raw_counter);

    int PdhValidatePath(String str);

    int PdhCollectQueryData(WinNT.HANDLE handle);

    int PdhCollectQueryDataEx(WinNT.HANDLE handle, int i, WinNT.HANDLE handle2);

    int PdhCollectQueryDataWithTime(WinNT.HANDLE handle, WinDef.LONGLONGByReference lONGLONGByReference);

    int PdhSetQueryTimeRange(WinNT.HANDLE handle, PDH_TIME_INFO pdh_time_info);

    int PdhEnumObjectItems(String str, String str2, String str3, Pointer pointer, WinDef.DWORDByReference dWORDByReference, Pointer pointer2, WinDef.DWORDByReference dWORDByReference2, int i, int i2);

    int PdhLookupPerfIndexByName(String str, String str2, WinDef.DWORDByReference dWORDByReference);

    int PdhLookupPerfNameByIndex(String str, int i, Pointer pointer, WinDef.DWORDByReference dWORDByReference);
}
