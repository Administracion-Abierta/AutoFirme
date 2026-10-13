package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Shell32.class */
public interface Shell32 extends ShellAPI, StdCallLibrary {
    public static final Shell32 INSTANCE = (Shell32) Native.load("shell32", Shell32.class, W32APIOptions.DEFAULT_OPTIONS);
    public static final int SHERB_NOCONFIRMATION = 1;
    public static final int SHERB_NOPROGRESSUI = 2;
    public static final int SHERB_NOSOUND = 4;
    public static final int SEE_MASK_NOCLOSEPROCESS = 64;
    public static final int SEE_MASK_FLAG_NO_UI = 1024;

    int SHFileOperation(ShellAPI.SHFILEOPSTRUCT shfileopstruct);

    WinNT.HRESULT SHGetFolderPath(WinDef.HWND hwnd, int i, WinNT.HANDLE handle, WinDef.DWORD dword, char[] cArr);

    WinNT.HRESULT SHGetKnownFolderPath(Guid.GUID guid, int i, WinNT.HANDLE handle, PointerByReference pointerByReference);

    WinNT.HRESULT SHGetDesktopFolder(PointerByReference pointerByReference);

    WinDef.INT_PTR ShellExecute(WinDef.HWND hwnd, String str, String str2, String str3, String str4, int i);

    boolean SHGetSpecialFolderPath(WinDef.HWND hwnd, char[] cArr, int i, boolean z);

    WinDef.UINT_PTR SHAppBarMessage(WinDef.DWORD dword, ShellAPI.APPBARDATA appbardata);

    int SHEmptyRecycleBin(WinNT.HANDLE handle, String str, int i);

    boolean ShellExecuteEx(ShellAPI.SHELLEXECUTEINFO shellexecuteinfo);

    WinNT.HRESULT SHGetSpecialFolderLocation(WinDef.HWND hwnd, int i, PointerByReference pointerByReference);

    int ExtractIconEx(String str, int i, WinDef.HICON[] hiconArr, WinDef.HICON[] hiconArr2, int i2);

    WinNT.HRESULT GetCurrentProcessExplicitAppUserModelID(PointerByReference pointerByReference);

    WinNT.HRESULT SetCurrentProcessExplicitAppUserModelID(WString wString);

    Pointer CommandLineToArgvW(WString wString, IntByReference intByReference);
}
