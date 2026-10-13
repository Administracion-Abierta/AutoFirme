package com.sun.jna.platform.win32;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/VersionHelpers.class */
public class VersionHelpers {
    public static boolean IsWindowsVersionOrGreater(int wMajorVersion, int wMinorVersion, int wServicePackMajor) {
        WinNT.OSVERSIONINFOEX osvi = new WinNT.OSVERSIONINFOEX();
        osvi.dwOSVersionInfoSize = new WinDef.DWORD(osvi.size());
        osvi.dwMajorVersion = new WinDef.DWORD(wMajorVersion);
        osvi.dwMinorVersion = new WinDef.DWORD(wMinorVersion);
        osvi.wServicePackMajor = new WinDef.WORD(wServicePackMajor);
        long dwlConditionMask = Kernel32.INSTANCE.VerSetConditionMask(0L, 2, (byte) 3);
        return Kernel32.INSTANCE.VerifyVersionInfoW(osvi, 35, Kernel32.INSTANCE.VerSetConditionMask(Kernel32.INSTANCE.VerSetConditionMask(dwlConditionMask, 1, (byte) 3), 32, (byte) 3));
    }

    public static boolean IsWindowsXPOrGreater() {
        return IsWindowsVersionOrGreater(5, 1, 0);
    }

    public static boolean IsWindowsXPSP1OrGreater() {
        return IsWindowsVersionOrGreater(5, 1, 1);
    }

    public static boolean IsWindowsXPSP2OrGreater() {
        return IsWindowsVersionOrGreater(5, 1, 2);
    }

    public static boolean IsWindowsXPSP3OrGreater() {
        return IsWindowsVersionOrGreater(5, 1, 3);
    }

    public static boolean IsWindowsVistaOrGreater() {
        return IsWindowsVersionOrGreater(6, 0, 0);
    }

    public static boolean IsWindowsVistaSP1OrGreater() {
        return IsWindowsVersionOrGreater(6, 0, 1);
    }

    public static boolean IsWindowsVistaSP2OrGreater() {
        return IsWindowsVersionOrGreater(6, 0, 2);
    }

    public static boolean IsWindows7OrGreater() {
        return IsWindowsVersionOrGreater(6, 1, 0);
    }

    public static boolean IsWindows7SP1OrGreater() {
        return IsWindowsVersionOrGreater(6, 1, 1);
    }

    public static boolean IsWindows8OrGreater() {
        return IsWindowsVersionOrGreater(6, 2, 0);
    }

    public static boolean IsWindows8Point1OrGreater() {
        return IsWindowsVersionOrGreater(6, 3, 0);
    }

    public static boolean IsWindows10OrGreater() {
        return IsWindowsVersionOrGreater(10, 0, 0);
    }

    public static boolean IsWindowsServer() {
        WinNT.OSVERSIONINFOEX osvi = new WinNT.OSVERSIONINFOEX();
        osvi.dwOSVersionInfoSize = new WinDef.DWORD(osvi.size());
        osvi.wProductType = (byte) 1;
        long dwlConditionMask = Kernel32.INSTANCE.VerSetConditionMask(0L, 128, (byte) 1);
        return !Kernel32.INSTANCE.VerifyVersionInfoW(osvi, 128, dwlConditionMask);
    }
}
