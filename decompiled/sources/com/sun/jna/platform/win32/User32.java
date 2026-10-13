package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/User32.class */
public interface User32 extends StdCallLibrary, WinUser, WinNT {
    public static final User32 INSTANCE = (User32) Native.load("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);
    public static final WinDef.HWND HWND_MESSAGE = new WinDef.HWND(Pointer.createConstant(-3));
    public static final int CS_GLOBALCLASS = 16384;
    public static final int WS_EX_TOPMOST = 8;
    public static final int DEVICE_NOTIFY_WINDOW_HANDLE = 0;
    public static final int DEVICE_NOTIFY_SERVICE_HANDLE = 1;
    public static final int DEVICE_NOTIFY_ALL_INTERFACE_CLASSES = 4;
    public static final int SW_SHOWDEFAULT = 10;

    WinDef.HDC GetDC(WinDef.HWND hwnd);

    int ReleaseDC(WinDef.HWND hwnd, WinDef.HDC hdc);

    WinDef.HWND FindWindow(String str, String str2);

    int GetClassName(WinDef.HWND hwnd, char[] cArr, int i);

    boolean GetGUIThreadInfo(int i, WinUser.GUITHREADINFO guithreadinfo);

    boolean GetWindowInfo(WinDef.HWND hwnd, WinUser.WINDOWINFO windowinfo);

    boolean GetWindowRect(WinDef.HWND hwnd, WinDef.RECT rect);

    boolean GetClientRect(WinDef.HWND hwnd, WinDef.RECT rect);

    int GetWindowText(WinDef.HWND hwnd, char[] cArr, int i);

    int GetWindowTextLength(WinDef.HWND hwnd);

    int GetWindowModuleFileName(WinDef.HWND hwnd, char[] cArr, int i);

    int GetWindowThreadProcessId(WinDef.HWND hwnd, IntByReference intByReference);

    boolean EnumWindows(WinUser.WNDENUMPROC wndenumproc, Pointer pointer);

    boolean EnumChildWindows(WinDef.HWND hwnd, WinUser.WNDENUMPROC wndenumproc, Pointer pointer);

    boolean EnumThreadWindows(int i, WinUser.WNDENUMPROC wndenumproc, Pointer pointer);

    boolean BringWindowToTop(WinDef.HWND hwnd);

    boolean FlashWindowEx(WinUser.FLASHWINFO flashwinfo);

    WinDef.HICON LoadIcon(WinDef.HINSTANCE hinstance, String str);

    WinNT.HANDLE LoadImage(WinDef.HINSTANCE hinstance, String str, int i, int i2, int i3, int i4);

    boolean DestroyIcon(WinDef.HICON hicon);

    int GetWindowLong(WinDef.HWND hwnd, int i);

    int SetWindowLong(WinDef.HWND hwnd, int i, int i2);

    BaseTSD.LONG_PTR GetWindowLongPtr(WinDef.HWND hwnd, int i);

    Pointer SetWindowLongPtr(WinDef.HWND hwnd, int i, Pointer pointer);

    boolean SetLayeredWindowAttributes(WinDef.HWND hwnd, int i, byte b, int i2);

    boolean GetLayeredWindowAttributes(WinDef.HWND hwnd, IntByReference intByReference, ByteByReference byteByReference, IntByReference intByReference2);

    boolean UpdateLayeredWindow(WinDef.HWND hwnd, WinDef.HDC hdc, WinDef.POINT point, WinUser.SIZE size, WinDef.HDC hdc2, WinDef.POINT point2, int i, WinUser.BLENDFUNCTION blendfunction, int i2);

    int SetWindowRgn(WinDef.HWND hwnd, WinDef.HRGN hrgn, boolean z);

    boolean GetKeyboardState(byte[] bArr);

    short GetAsyncKeyState(int i);

    WinUser.HHOOK SetWindowsHookEx(int i, WinUser.HOOKPROC hookproc, WinDef.HINSTANCE hinstance, int i2);

    WinDef.LRESULT CallNextHookEx(WinUser.HHOOK hhook, int i, WinDef.WPARAM wparam, WinDef.LPARAM lparam);

    boolean UnhookWindowsHookEx(WinUser.HHOOK hhook);

    int GetMessage(WinUser.MSG msg, WinDef.HWND hwnd, int i, int i2);

    boolean PeekMessage(WinUser.MSG msg, WinDef.HWND hwnd, int i, int i2, int i3);

    boolean TranslateMessage(WinUser.MSG msg);

    WinDef.LRESULT DispatchMessage(WinUser.MSG msg);

    void PostMessage(WinDef.HWND hwnd, int i, WinDef.WPARAM wparam, WinDef.LPARAM lparam);

    int PostThreadMessage(int i, int i2, WinDef.WPARAM wparam, WinDef.LPARAM lparam);

    void PostQuitMessage(int i);

    int GetSystemMetrics(int i);

    WinDef.HWND SetParent(WinDef.HWND hwnd, WinDef.HWND hwnd2);

    boolean IsWindowVisible(WinDef.HWND hwnd);

    boolean MoveWindow(WinDef.HWND hwnd, int i, int i2, int i3, int i4, boolean z);

    boolean SetWindowPos(WinDef.HWND hwnd, WinDef.HWND hwnd2, int i, int i2, int i3, int i4, int i5);

    boolean AttachThreadInput(WinDef.DWORD dword, WinDef.DWORD dword2, boolean z);

    boolean SetForegroundWindow(WinDef.HWND hwnd);

    WinDef.HWND GetForegroundWindow();

    WinDef.HWND SetFocus(WinDef.HWND hwnd);

    WinDef.DWORD SendInput(WinDef.DWORD dword, WinUser.INPUT[] inputArr, int i);

    WinDef.DWORD WaitForInputIdle(WinNT.HANDLE handle, WinDef.DWORD dword);

    boolean InvalidateRect(WinDef.HWND hwnd, WinDef.RECT rect, boolean z);

    boolean RedrawWindow(WinDef.HWND hwnd, WinDef.RECT rect, WinDef.HRGN hrgn, WinDef.DWORD dword);

    WinDef.HWND GetWindow(WinDef.HWND hwnd, WinDef.DWORD dword);

    boolean UpdateWindow(WinDef.HWND hwnd);

    boolean ShowWindow(WinDef.HWND hwnd, int i);

    boolean CloseWindow(WinDef.HWND hwnd);

    boolean RegisterHotKey(WinDef.HWND hwnd, int i, int i2, int i3);

    boolean UnregisterHotKey(Pointer pointer, int i);

    boolean GetLastInputInfo(WinUser.LASTINPUTINFO lastinputinfo);

    WinDef.ATOM RegisterClassEx(WinUser.WNDCLASSEX wndclassex);

    boolean UnregisterClass(String str, WinDef.HINSTANCE hinstance);

    WinDef.HWND CreateWindowEx(int i, String str, String str2, int i2, int i3, int i4, int i5, int i6, WinDef.HWND hwnd, WinDef.HMENU hmenu, WinDef.HINSTANCE hinstance, WinDef.LPVOID lpvoid);

    boolean DestroyWindow(WinDef.HWND hwnd);

    boolean GetClassInfoEx(WinDef.HINSTANCE hinstance, String str, WinUser.WNDCLASSEX wndclassex);

    WinDef.LRESULT CallWindowProc(Pointer pointer, WinDef.HWND hwnd, int i, WinDef.WPARAM wparam, WinDef.LPARAM lparam);

    WinDef.LRESULT DefWindowProc(WinDef.HWND hwnd, int i, WinDef.WPARAM wparam, WinDef.LPARAM lparam);

    WinUser.HDEVNOTIFY RegisterDeviceNotification(WinNT.HANDLE handle, Structure structure, int i);

    boolean UnregisterDeviceNotification(WinUser.HDEVNOTIFY hdevnotify);

    int RegisterWindowMessage(String str);

    WinUser.HMONITOR MonitorFromPoint(WinDef.POINT.ByValue byValue, int i);

    WinUser.HMONITOR MonitorFromRect(WinDef.RECT rect, int i);

    WinUser.HMONITOR MonitorFromWindow(WinDef.HWND hwnd, int i);

    WinDef.BOOL GetMonitorInfo(WinUser.HMONITOR hmonitor, WinUser.MONITORINFO monitorinfo);

    WinDef.BOOL GetMonitorInfo(WinUser.HMONITOR hmonitor, WinUser.MONITORINFOEX monitorinfoex);

    WinDef.BOOL EnumDisplayMonitors(WinDef.HDC hdc, WinDef.RECT rect, WinUser.MONITORENUMPROC monitorenumproc, WinDef.LPARAM lparam);

    WinDef.BOOL GetWindowPlacement(WinDef.HWND hwnd, WinUser.WINDOWPLACEMENT windowplacement);

    WinDef.BOOL SetWindowPlacement(WinDef.HWND hwnd, WinUser.WINDOWPLACEMENT windowplacement);

    WinDef.BOOL AdjustWindowRect(WinDef.RECT rect, WinDef.DWORD dword, WinDef.BOOL bool);

    WinDef.BOOL AdjustWindowRectEx(WinDef.RECT rect, WinDef.DWORD dword, WinDef.BOOL bool, WinDef.DWORD dword2);

    WinDef.BOOL ExitWindowsEx(WinDef.UINT uint, WinDef.DWORD dword);

    WinDef.BOOL LockWorkStation();

    boolean GetIconInfo(WinDef.HICON hicon, WinGDI.ICONINFO iconinfo);

    WinDef.LRESULT SendMessageTimeout(WinDef.HWND hwnd, int i, WinDef.WPARAM wparam, WinDef.LPARAM lparam, int i2, int i3, WinDef.DWORDByReference dWORDByReference);

    BaseTSD.ULONG_PTR GetClassLongPtr(WinDef.HWND hwnd, int i);

    int GetRawInputDeviceList(WinUser.RAWINPUTDEVICELIST[] rawinputdevicelistArr, IntByReference intByReference, int i);

    WinDef.HWND GetDesktopWindow();

    boolean PrintWindow(WinDef.HWND hwnd, WinDef.HDC hdc, int i);

    boolean IsWindowEnabled(WinDef.HWND hwnd);

    boolean IsWindow(WinDef.HWND hwnd);

    WinDef.HWND FindWindowEx(WinDef.HWND hwnd, WinDef.HWND hwnd2, String str, String str2);

    WinDef.HWND GetAncestor(WinDef.HWND hwnd, int i);

    WinDef.HWND GetParent(WinDef.HWND hwnd);

    boolean GetCursorPos(WinDef.POINT point);

    boolean SetCursorPos(long j, long j2);

    WinNT.HANDLE SetWinEventHook(int i, int i2, WinDef.HMODULE hmodule, WinUser.WinEventProc winEventProc, int i3, int i4, int i5);

    boolean UnhookWinEvent(WinNT.HANDLE handle);

    WinDef.HICON CopyIcon(WinDef.HICON hicon);

    int GetClassLong(WinDef.HWND hwnd, int i);

    int RegisterClipboardFormat(String str);

    WinDef.HWND GetActiveWindow();

    WinDef.LRESULT SendMessage(WinDef.HWND hwnd, int i, WinDef.WPARAM wparam, WinDef.LPARAM lparam);

    int GetKeyboardLayoutList(int i, WinDef.HKL[] hklArr);

    WinDef.HKL GetKeyboardLayout(int i);

    boolean GetKeyboardLayoutName(char[] cArr);

    short VkKeyScanExA(byte b, WinDef.HKL hkl);

    short VkKeyScanExW(char c, WinDef.HKL hkl);

    int MapVirtualKeyEx(int i, int i2, WinDef.HKL hkl);

    int ToUnicodeEx(int i, int i2, byte[] bArr, char[] cArr, int i3, int i4, WinDef.HKL hkl);

    int LoadString(WinDef.HINSTANCE hinstance, int i, Pointer pointer, int i2);
}
