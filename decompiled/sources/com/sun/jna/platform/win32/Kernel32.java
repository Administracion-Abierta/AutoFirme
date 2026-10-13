package com.sun.jna.platform.win32;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Kernel32.class */
public interface Kernel32 extends StdCallLibrary, WinNT, Wincon {
    public static final Kernel32 INSTANCE = (Kernel32) Native.load("kernel32", Kernel32.class, W32APIOptions.DEFAULT_OPTIONS);
    public static final int LOAD_LIBRARY_AS_DATAFILE = 2;
    public static final int MAX_PIPE_NAME_LENGTH = 256;

    boolean ReadFile(WinNT.HANDLE handle, byte[] bArr, int i, IntByReference intByReference, WinBase.OVERLAPPED overlapped);

    Pointer LocalFree(Pointer pointer);

    Pointer GlobalFree(Pointer pointer);

    WinDef.HMODULE GetModuleHandle(String str);

    void GetSystemTime(WinBase.SYSTEMTIME systemtime);

    boolean SetSystemTime(WinBase.SYSTEMTIME systemtime);

    void GetLocalTime(WinBase.SYSTEMTIME systemtime);

    boolean SetLocalTime(WinBase.SYSTEMTIME systemtime);

    boolean GetSystemTimes(WinBase.FILETIME filetime, WinBase.FILETIME filetime2, WinBase.FILETIME filetime3);

    int GetTickCount();

    long GetTickCount64();

    int GetCurrentThreadId();

    WinNT.HANDLE GetCurrentThread();

    int GetCurrentProcessId();

    WinNT.HANDLE GetCurrentProcess();

    int GetProcessId(WinNT.HANDLE handle);

    int GetProcessVersion(int i);

    boolean GetProcessAffinityMask(WinNT.HANDLE handle, BaseTSD.ULONG_PTRByReference uLONG_PTRByReference, BaseTSD.ULONG_PTRByReference uLONG_PTRByReference2);

    boolean SetProcessAffinityMask(WinNT.HANDLE handle, BaseTSD.ULONG_PTR ulong_ptr);

    boolean GetExitCodeProcess(WinNT.HANDLE handle, IntByReference intByReference);

    boolean TerminateProcess(WinNT.HANDLE handle, int i);

    int GetLastError();

    void SetLastError(int i);

    int GetDriveType(String str);

    int FormatMessage(int i, Pointer pointer, int i2, int i3, PointerByReference pointerByReference, int i4, Pointer pointer2);

    WinNT.HANDLE CreateFile(String str, int i, int i2, WinBase.SECURITY_ATTRIBUTES security_attributes, int i3, int i4, WinNT.HANDLE handle);

    boolean CopyFile(String str, String str2, boolean z);

    boolean MoveFile(String str, String str2);

    boolean MoveFileEx(String str, String str2, WinDef.DWORD dword);

    boolean CreateDirectory(String str, WinBase.SECURITY_ATTRIBUTES security_attributes);

    WinNT.HANDLE CreateIoCompletionPort(WinNT.HANDLE handle, WinNT.HANDLE handle2, Pointer pointer, int i);

    boolean GetQueuedCompletionStatus(WinNT.HANDLE handle, IntByReference intByReference, BaseTSD.ULONG_PTRByReference uLONG_PTRByReference, PointerByReference pointerByReference, int i);

    boolean PostQueuedCompletionStatus(WinNT.HANDLE handle, int i, Pointer pointer, WinBase.OVERLAPPED overlapped);

    int WaitForSingleObject(WinNT.HANDLE handle, int i);

    int WaitForMultipleObjects(int i, WinNT.HANDLE[] handleArr, boolean z, int i2);

    boolean DuplicateHandle(WinNT.HANDLE handle, WinNT.HANDLE handle2, WinNT.HANDLE handle3, WinNT.HANDLEByReference hANDLEByReference, int i, boolean z, int i2);

    boolean CloseHandle(WinNT.HANDLE handle);

    boolean ReadDirectoryChangesW(WinNT.HANDLE handle, WinNT.FILE_NOTIFY_INFORMATION file_notify_information, int i, boolean z, int i2, IntByReference intByReference, WinBase.OVERLAPPED overlapped, WinNT.OVERLAPPED_COMPLETION_ROUTINE overlapped_completion_routine);

    int GetShortPathName(String str, char[] cArr, int i);

    Pointer LocalAlloc(int i, int i2);

    boolean WriteFile(WinNT.HANDLE handle, byte[] bArr, int i, IntByReference intByReference, WinBase.OVERLAPPED overlapped);

    boolean FlushFileBuffers(WinNT.HANDLE handle);

    WinNT.HANDLE CreateEvent(WinBase.SECURITY_ATTRIBUTES security_attributes, boolean z, boolean z2, String str);

    WinNT.HANDLE OpenEvent(int i, boolean z, String str);

    boolean SetEvent(WinNT.HANDLE handle);

    boolean ResetEvent(WinNT.HANDLE handle);

    boolean PulseEvent(WinNT.HANDLE handle);

    WinNT.HANDLE CreateFileMapping(WinNT.HANDLE handle, WinBase.SECURITY_ATTRIBUTES security_attributes, int i, int i2, int i3, String str);

    WinNT.HANDLE OpenFileMapping(int i, boolean z, String str);

    Pointer MapViewOfFile(WinNT.HANDLE handle, int i, int i2, int i3, int i4);

    boolean UnmapViewOfFile(Pointer pointer);

    boolean GetComputerName(char[] cArr, IntByReference intByReference);

    boolean GetComputerNameEx(int i, char[] cArr, IntByReference intByReference);

    WinNT.HANDLE OpenThread(int i, boolean z, int i2);

    boolean CreateProcess(String str, String str2, WinBase.SECURITY_ATTRIBUTES security_attributes, WinBase.SECURITY_ATTRIBUTES security_attributes2, boolean z, WinDef.DWORD dword, Pointer pointer, String str3, WinBase.STARTUPINFO startupinfo, WinBase.PROCESS_INFORMATION process_information);

    boolean CreateProcessW(String str, char[] cArr, WinBase.SECURITY_ATTRIBUTES security_attributes, WinBase.SECURITY_ATTRIBUTES security_attributes2, boolean z, WinDef.DWORD dword, Pointer pointer, String str2, WinBase.STARTUPINFO startupinfo, WinBase.PROCESS_INFORMATION process_information);

    WinNT.HANDLE OpenProcess(int i, boolean z, int i2);

    boolean QueryFullProcessImageName(WinNT.HANDLE handle, int i, char[] cArr, IntByReference intByReference);

    WinDef.DWORD GetTempPath(WinDef.DWORD dword, char[] cArr);

    WinDef.DWORD GetVersion();

    boolean GetVersionEx(WinNT.OSVERSIONINFO osversioninfo);

    boolean GetVersionEx(WinNT.OSVERSIONINFOEX osversioninfoex);

    boolean VerifyVersionInfoW(WinNT.OSVERSIONINFOEX osversioninfoex, int i, long j);

    long VerSetConditionMask(long j, int i, byte b);

    void GetSystemInfo(WinBase.SYSTEM_INFO system_info);

    void GetNativeSystemInfo(WinBase.SYSTEM_INFO system_info);

    boolean IsWow64Process(WinNT.HANDLE handle, IntByReference intByReference);

    boolean GetLogicalProcessorInformation(Pointer pointer, WinDef.DWORDByReference dWORDByReference);

    boolean GetLogicalProcessorInformationEx(int i, Pointer pointer, WinDef.DWORDByReference dWORDByReference);

    boolean GlobalMemoryStatusEx(WinBase.MEMORYSTATUSEX memorystatusex);

    boolean GetFileInformationByHandleEx(WinNT.HANDLE handle, int i, Pointer pointer, WinDef.DWORD dword);

    boolean SetFileInformationByHandle(WinNT.HANDLE handle, int i, Pointer pointer, WinDef.DWORD dword);

    boolean GetFileTime(WinNT.HANDLE handle, WinBase.FILETIME filetime, WinBase.FILETIME filetime2, WinBase.FILETIME filetime3);

    int SetFileTime(WinNT.HANDLE handle, WinBase.FILETIME filetime, WinBase.FILETIME filetime2, WinBase.FILETIME filetime3);

    boolean SetFileAttributes(String str, WinDef.DWORD dword);

    WinDef.DWORD GetLogicalDriveStrings(WinDef.DWORD dword, char[] cArr);

    boolean GetDiskFreeSpace(String str, WinDef.DWORDByReference dWORDByReference, WinDef.DWORDByReference dWORDByReference2, WinDef.DWORDByReference dWORDByReference3, WinDef.DWORDByReference dWORDByReference4);

    boolean GetDiskFreeSpaceEx(String str, WinNT.LARGE_INTEGER large_integer, WinNT.LARGE_INTEGER large_integer2, WinNT.LARGE_INTEGER large_integer3);

    boolean DeleteFile(String str);

    boolean CreatePipe(WinNT.HANDLEByReference hANDLEByReference, WinNT.HANDLEByReference hANDLEByReference2, WinBase.SECURITY_ATTRIBUTES security_attributes, int i);

    boolean CallNamedPipe(String str, byte[] bArr, int i, byte[] bArr2, int i2, IntByReference intByReference, int i3);

    boolean ConnectNamedPipe(WinNT.HANDLE handle, WinBase.OVERLAPPED overlapped);

    WinNT.HANDLE CreateNamedPipe(String str, int i, int i2, int i3, int i4, int i5, int i6, WinBase.SECURITY_ATTRIBUTES security_attributes);

    boolean DisconnectNamedPipe(WinNT.HANDLE handle);

    boolean GetNamedPipeClientComputerName(WinNT.HANDLE handle, char[] cArr, int i);

    boolean GetNamedPipeClientProcessId(WinNT.HANDLE handle, WinDef.ULONGByReference uLONGByReference);

    boolean GetNamedPipeClientSessionId(WinNT.HANDLE handle, WinDef.ULONGByReference uLONGByReference);

    boolean GetNamedPipeHandleState(WinNT.HANDLE handle, IntByReference intByReference, IntByReference intByReference2, IntByReference intByReference3, IntByReference intByReference4, char[] cArr, int i);

    boolean GetNamedPipeInfo(WinNT.HANDLE handle, IntByReference intByReference, IntByReference intByReference2, IntByReference intByReference3, IntByReference intByReference4);

    boolean GetNamedPipeServerProcessId(WinNT.HANDLE handle, WinDef.ULONGByReference uLONGByReference);

    boolean GetNamedPipeServerSessionId(WinNT.HANDLE handle, WinDef.ULONGByReference uLONGByReference);

    boolean PeekNamedPipe(WinNT.HANDLE handle, byte[] bArr, int i, IntByReference intByReference, IntByReference intByReference2, IntByReference intByReference3);

    boolean SetNamedPipeHandleState(WinNT.HANDLE handle, IntByReference intByReference, IntByReference intByReference2, IntByReference intByReference3);

    boolean TransactNamedPipe(WinNT.HANDLE handle, byte[] bArr, int i, byte[] bArr2, int i2, IntByReference intByReference, WinBase.OVERLAPPED overlapped);

    boolean WaitNamedPipe(String str, int i);

    boolean SetHandleInformation(WinNT.HANDLE handle, int i, int i2);

    int GetFileAttributes(String str);

    int GetFileType(WinNT.HANDLE handle);

    boolean DeviceIoControl(WinNT.HANDLE handle, int i, Pointer pointer, int i2, Pointer pointer2, int i3, IntByReference intByReference, Pointer pointer3);

    WinNT.HANDLE CreateToolhelp32Snapshot(WinDef.DWORD dword, WinDef.DWORD dword2);

    boolean Process32First(WinNT.HANDLE handle, Tlhelp32.PROCESSENTRY32 processentry32);

    boolean Process32Next(WinNT.HANDLE handle, Tlhelp32.PROCESSENTRY32 processentry32);

    boolean Thread32First(WinNT.HANDLE handle, Tlhelp32.THREADENTRY32 threadentry32);

    boolean Thread32Next(WinNT.HANDLE handle, Tlhelp32.THREADENTRY32 threadentry32);

    boolean SetEnvironmentVariable(String str, String str2);

    int GetEnvironmentVariable(String str, char[] cArr, int i);

    Pointer GetEnvironmentStrings();

    boolean FreeEnvironmentStrings(Pointer pointer);

    WinDef.LCID GetSystemDefaultLCID();

    WinDef.LCID GetUserDefaultLCID();

    int GetPrivateProfileInt(String str, String str2, int i, String str3);

    WinDef.DWORD GetPrivateProfileString(String str, String str2, String str3, char[] cArr, WinDef.DWORD dword, String str4);

    boolean WritePrivateProfileString(String str, String str2, String str3, String str4);

    WinDef.DWORD GetPrivateProfileSection(String str, char[] cArr, WinDef.DWORD dword, String str2);

    WinDef.DWORD GetPrivateProfileSectionNames(char[] cArr, WinDef.DWORD dword, String str);

    boolean WritePrivateProfileSection(String str, String str2, String str3);

    boolean FileTimeToLocalFileTime(WinBase.FILETIME filetime, WinBase.FILETIME filetime2);

    boolean SystemTimeToTzSpecificLocalTime(WinBase.TIME_ZONE_INFORMATION time_zone_information, WinBase.SYSTEMTIME systemtime, WinBase.SYSTEMTIME systemtime2);

    boolean SystemTimeToFileTime(WinBase.SYSTEMTIME systemtime, WinBase.FILETIME filetime);

    boolean FileTimeToSystemTime(WinBase.FILETIME filetime, WinBase.SYSTEMTIME systemtime);

    @Deprecated
    WinNT.HANDLE CreateRemoteThread(WinNT.HANDLE handle, WinBase.SECURITY_ATTRIBUTES security_attributes, int i, WinBase.FOREIGN_THREAD_START_ROUTINE foreign_thread_start_routine, Pointer pointer, WinDef.DWORD dword, Pointer pointer2);

    WinNT.HANDLE CreateRemoteThread(WinNT.HANDLE handle, WinBase.SECURITY_ATTRIBUTES security_attributes, int i, Pointer pointer, Pointer pointer2, int i2, WinDef.DWORDByReference dWORDByReference);

    boolean WriteProcessMemory(WinNT.HANDLE handle, Pointer pointer, Pointer pointer2, int i, IntByReference intByReference);

    boolean ReadProcessMemory(WinNT.HANDLE handle, Pointer pointer, Pointer pointer2, int i, IntByReference intByReference);

    BaseTSD.SIZE_T VirtualQueryEx(WinNT.HANDLE handle, Pointer pointer, WinNT.MEMORY_BASIC_INFORMATION memory_basic_information, BaseTSD.SIZE_T size_t);

    boolean DefineDosDevice(int i, String str, String str2);

    int QueryDosDevice(String str, char[] cArr, int i);

    WinNT.HANDLE FindFirstFile(String str, Pointer pointer);

    WinNT.HANDLE FindFirstFileEx(String str, int i, Pointer pointer, int i2, Pointer pointer2, WinDef.DWORD dword);

    boolean FindNextFile(WinNT.HANDLE handle, Pointer pointer);

    boolean FindClose(WinNT.HANDLE handle);

    WinNT.HANDLE FindFirstVolumeMountPoint(String str, char[] cArr, int i);

    boolean FindNextVolumeMountPoint(WinNT.HANDLE handle, char[] cArr, int i);

    boolean FindVolumeMountPointClose(WinNT.HANDLE handle);

    boolean GetVolumeNameForVolumeMountPoint(String str, char[] cArr, int i);

    boolean SetVolumeLabel(String str, String str2);

    boolean SetVolumeMountPoint(String str, String str2);

    boolean DeleteVolumeMountPoint(String str);

    boolean GetVolumeInformation(String str, char[] cArr, int i, IntByReference intByReference, IntByReference intByReference2, IntByReference intByReference3, char[] cArr2, int i2);

    boolean GetVolumePathName(String str, char[] cArr, int i);

    boolean GetVolumePathNamesForVolumeName(String str, char[] cArr, int i, IntByReference intByReference);

    WinNT.HANDLE FindFirstVolume(char[] cArr, int i);

    boolean FindNextVolume(WinNT.HANDLE handle, char[] cArr, int i);

    boolean FindVolumeClose(WinNT.HANDLE handle);

    boolean GetCommState(WinNT.HANDLE handle, WinBase.DCB dcb);

    boolean GetCommTimeouts(WinNT.HANDLE handle, WinBase.COMMTIMEOUTS commtimeouts);

    boolean SetCommState(WinNT.HANDLE handle, WinBase.DCB dcb);

    boolean SetCommTimeouts(WinNT.HANDLE handle, WinBase.COMMTIMEOUTS commtimeouts);

    boolean ProcessIdToSessionId(int i, IntByReference intByReference);

    WinDef.HMODULE LoadLibraryEx(String str, WinNT.HANDLE handle, int i);

    WinDef.HRSRC FindResource(WinDef.HMODULE hmodule, Pointer pointer, Pointer pointer2);

    WinNT.HANDLE LoadResource(WinDef.HMODULE hmodule, WinDef.HRSRC hrsrc);

    Pointer LockResource(WinNT.HANDLE handle);

    int SizeofResource(WinDef.HMODULE hmodule, WinNT.HANDLE handle);

    boolean FreeLibrary(WinDef.HMODULE hmodule);

    boolean EnumResourceTypes(WinDef.HMODULE hmodule, WinBase.EnumResTypeProc enumResTypeProc, Pointer pointer);

    boolean EnumResourceNames(WinDef.HMODULE hmodule, Pointer pointer, WinBase.EnumResNameProc enumResNameProc, Pointer pointer2);

    boolean Module32FirstW(WinNT.HANDLE handle, Tlhelp32.MODULEENTRY32W moduleentry32w);

    boolean Module32NextW(WinNT.HANDLE handle, Tlhelp32.MODULEENTRY32W moduleentry32w);

    int SetErrorMode(int i);

    Pointer GetProcAddress(WinDef.HMODULE hmodule, int i) throws LastErrorException;

    int SetThreadExecutionState(int i);

    int ExpandEnvironmentStrings(String str, Pointer pointer, int i);

    boolean GetProcessTimes(WinNT.HANDLE handle, WinBase.FILETIME filetime, WinBase.FILETIME filetime2, WinBase.FILETIME filetime3, WinBase.FILETIME filetime4);

    boolean GetProcessIoCounters(WinNT.HANDLE handle, WinNT.IO_COUNTERS io_counters);

    WinNT.HANDLE CreateMutex(WinBase.SECURITY_ATTRIBUTES security_attributes, boolean z, String str);

    WinNT.HANDLE OpenMutex(int i, boolean z, String str);

    boolean ReleaseMutex(WinNT.HANDLE handle);

    void ExitProcess(int i);

    Pointer VirtualAllocEx(WinNT.HANDLE handle, Pointer pointer, BaseTSD.SIZE_T size_t, int i, int i2);

    boolean GetExitCodeThread(WinNT.HANDLE handle, IntByReference intByReference);

    boolean VirtualFreeEx(WinNT.HANDLE handle, Pointer pointer, BaseTSD.SIZE_T size_t, int i);

    WinNT.HRESULT RegisterApplicationRestart(char[] cArr, int i);

    WinNT.HRESULT UnregisterApplicationRestart();

    WinNT.HRESULT GetApplicationRestartSettings(WinNT.HANDLE handle, char[] cArr, IntByReference intByReference, IntByReference intByReference2);
}
