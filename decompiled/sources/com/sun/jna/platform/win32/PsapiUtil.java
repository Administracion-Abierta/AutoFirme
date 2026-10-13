package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import java.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/PsapiUtil.class */
public abstract class PsapiUtil {
    public static int[] enumProcesses() {
        int[] lpidProcess;
        int size = 0;
        IntByReference lpcbNeeded = new IntByReference();
        do {
            size += 1024;
            lpidProcess = new int[size];
            if (!Psapi.INSTANCE.EnumProcesses(lpidProcess, size * 4, lpcbNeeded)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
        } while (size == lpcbNeeded.getValue() / 4);
        return Arrays.copyOf(lpidProcess, lpcbNeeded.getValue() / 4);
    }

    public static String GetProcessImageFileName(WinNT.HANDLE hProcess) {
        int size = 2048;
        while (true) {
            char[] filePath = new char[size];
            int length = Psapi.INSTANCE.GetProcessImageFileName(hProcess, filePath, filePath.length);
            if (length == 0) {
                if (Native.getLastError() != 122) {
                    throw new Win32Exception(Native.getLastError());
                }
                size += 2048;
            } else {
                return Native.toString(filePath);
            }
        }
    }
}
