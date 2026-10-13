package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDPrintFieldAttributeObject;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Psapi.class */
public interface Psapi extends StdCallLibrary {
    public static final Psapi INSTANCE = (Psapi) Native.load("psapi", Psapi.class, W32APIOptions.DEFAULT_OPTIONS);

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Psapi$MODULEINFO.class */
    @Structure.FieldOrder({"lpBaseOfDll", "SizeOfImage", "EntryPoint"})
    public static class MODULEINFO extends Structure {
        public Pointer EntryPoint;
        public Pointer lpBaseOfDll;
        public int SizeOfImage;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Psapi$PERFORMANCE_INFORMATION.class */
    @Structure.FieldOrder({PDPrintFieldAttributeObject.ROLE_CB, "CommitTotal", "CommitLimit", "CommitPeak", "PhysicalTotal", "PhysicalAvailable", "SystemCache", "KernelTotal", "KernelPaged", "KernelNonpaged", "PageSize", "HandleCount", "ProcessCount", "ThreadCount"})
    public static class PERFORMANCE_INFORMATION extends Structure {
        public WinDef.DWORD cb;
        public BaseTSD.SIZE_T CommitTotal;
        public BaseTSD.SIZE_T CommitLimit;
        public BaseTSD.SIZE_T CommitPeak;
        public BaseTSD.SIZE_T PhysicalTotal;
        public BaseTSD.SIZE_T PhysicalAvailable;
        public BaseTSD.SIZE_T SystemCache;
        public BaseTSD.SIZE_T KernelTotal;
        public BaseTSD.SIZE_T KernelPaged;
        public BaseTSD.SIZE_T KernelNonpaged;
        public BaseTSD.SIZE_T PageSize;
        public WinDef.DWORD HandleCount;
        public WinDef.DWORD ProcessCount;
        public WinDef.DWORD ThreadCount;
    }

    int GetModuleFileNameExA(WinNT.HANDLE handle, WinNT.HANDLE handle2, byte[] bArr, int i);

    int GetModuleFileNameExW(WinNT.HANDLE handle, WinNT.HANDLE handle2, char[] cArr, int i);

    int GetModuleFileNameEx(WinNT.HANDLE handle, WinNT.HANDLE handle2, Pointer pointer, int i);

    boolean EnumProcessModules(WinNT.HANDLE handle, WinDef.HMODULE[] hmoduleArr, int i, IntByReference intByReference);

    boolean GetModuleInformation(WinNT.HANDLE handle, WinDef.HMODULE hmodule, MODULEINFO moduleinfo, int i);

    int GetProcessImageFileName(WinNT.HANDLE handle, char[] cArr, int i);

    boolean GetPerformanceInfo(PERFORMANCE_INFORMATION performance_information, int i);

    boolean EnumProcesses(int[] iArr, int i, IntByReference intByReference);
}
