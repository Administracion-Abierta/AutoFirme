package com.github.markusbernhardt.proxy.jna.win;

import com.github.markusbernhardt.proxy.util.Logger;
import com.sun.jna.LastErrorException;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/jna/win/WinHttpHelpers.class */
public class WinHttpHelpers {
    private static final int ERROR_WINHTTP_AUTODETECTION_FAILED = 12180;

    private WinHttpHelpers() {
    }

    public static String detectAutoProxyConfigUrl(WinDef.DWORD dwAutoDetectFlags) {
        PointerByReference ppwszAutoConfigUrl = new PointerByReference();
        try {
            try {
                boolean result = WinHttp.INSTANCE.WinHttpDetectAutoProxyConfigUrl(dwAutoDetectFlags, ppwszAutoConfigUrl);
                if (!result) {
                    freeGlobalMemory(ppwszAutoConfigUrl.getValue());
                    return null;
                }
                Pointer h = ppwszAutoConfigUrl.getValue();
                String wideString = h == null ? null : h.getWideString(0L);
                freeGlobalMemory(ppwszAutoConfigUrl.getValue());
                return wideString;
            } catch (LastErrorException ex) {
                Logger.log(WinHttpHelpers.class, Logger.LogLevel.ERROR, "Windows function WinHttpDetectAutoProxyConfigUrl returned error : {}", Integer.valueOf(ex.getErrorCode()));
                if (ex.getErrorCode() == ERROR_WINHTTP_AUTODETECTION_FAILED) {
                    freeGlobalMemory(ppwszAutoConfigUrl.getValue());
                    return null;
                }
                Logger.log(WinHttpHelpers.class, Logger.LogLevel.ERROR, "Windows function WinHttpDetectAutoProxyConfigUrl returned error : {}", ex.getMessage());
                freeGlobalMemory(ppwszAutoConfigUrl.getValue());
                return null;
            }
        } catch (Throwable th) {
            freeGlobalMemory(ppwszAutoConfigUrl.getValue());
            throw th;
        }
        freeGlobalMemory(ppwszAutoConfigUrl.getValue());
        throw th;
    }

    public static void freeGlobalMemory(Pointer p) {
        if (p != null) {
            Pointer free = Kernel32.INSTANCE.GlobalFree(p);
            if (free != null) {
                Logger.log(WinHttpHelpers.class, Logger.LogLevel.ERROR, "Windows function GlobalFree failed while freeing memory", new Object[0]);
            }
        }
    }

    public static String getAndFreeGlobalString(WTypes.LPWSTR h) {
        if (h == null || h.getPointer() == null) {
            return null;
        }
        try {
            return h.getValue();
        } finally {
            freeGlobalMemory(h.getPointer());
            h.setPointer(null);
        }
    }

    private String sanitizeUrl(String urlStr) {
        String u = urlStr.trim();
        int pos = u.indexOf(10);
        if (pos == 0) {
            return "";
        }
        if (pos > 0) {
            return u.substring(0, pos);
        }
        return u;
    }
}
