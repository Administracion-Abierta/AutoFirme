package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WininetUtil.class */
public class WininetUtil {
    /* JADX WARN: Code duplicated, block: B:38:0x010e A[Catch: Win32Exception -> 0x01af, all -> 0x01e0, TryCatch #2 {Win32Exception -> 0x01af, all -> 0x01e0, blocks: (B:3:0x0017, B:18:0x0072, B:19:0x007a, B:20:0x007b, B:22:0x009b, B:23:0x00a5, B:24:0x00a6, B:25:0x00af, B:27:0x00cb, B:48:0x013b, B:49:0x0143, B:51:0x014d, B:55:0x017a, B:54:0x0171, B:34:0x00e3, B:35:0x00eb, B:36:0x00ec, B:38:0x010e, B:45:0x0126, B:46:0x012e, B:47:0x012f), top: B:87:0x0017 }] */
    /* JADX WARN: Code duplicated, block: B:41:0x011c  */
    /* JADX WARN: Code duplicated, block: B:89:0x0119 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:92:0x012f A[SYNTHETIC] */
    public static Map<String, String> getCache() {
        boolean result;
        int lastError;
        List<Wininet.INTERNET_CACHE_ENTRY_INFO> items = new ArrayList<>();
        Win32Exception we = null;
        Map<String, String> cacheItems = new LinkedHashMap<>();
        try {
            IntByReference size = new IntByReference();
            WinNT.HANDLE cacheHandle = Wininet.INSTANCE.FindFirstUrlCacheEntry(null, null, size);
            int lastError2 = Native.getLastError();
            if (lastError2 == 259) {
                if (cacheHandle != null && !Wininet.INSTANCE.FindCloseUrlCache(cacheHandle) && 0 != 0) {
                    Win32Exception e = new Win32Exception(Native.getLastError());
                    e.addSuppressedReflected(null);
                }
                return cacheItems;
            }
            if (lastError2 != 0 && lastError2 != 122) {
                throw new Win32Exception(lastError2);
            }
            Wininet.INTERNET_CACHE_ENTRY_INFO entry = new Wininet.INTERNET_CACHE_ENTRY_INFO(size.getValue());
            WinNT.HANDLE cacheHandle2 = Wininet.INSTANCE.FindFirstUrlCacheEntry(null, entry, size);
            if (cacheHandle2 == null) {
                throw new Win32Exception(Native.getLastError());
            }
            items.add(entry);
            while (true) {
                IntByReference size2 = new IntByReference();
                boolean result2 = Wininet.INSTANCE.FindNextUrlCacheEntry(cacheHandle2, null, size2);
                if (!result2) {
                    int lastError3 = Native.getLastError();
                    if (lastError3 == 259) {
                        break;
                    }
                    if (lastError3 != 0 && lastError3 != 122) {
                        throw new Win32Exception(lastError3);
                    }
                    Wininet.INTERNET_CACHE_ENTRY_INFO entry2 = new Wininet.INTERNET_CACHE_ENTRY_INFO(size2.getValue());
                    result = Wininet.INSTANCE.FindNextUrlCacheEntry(cacheHandle2, entry2, size2);
                    if (result) {
                        lastError = Native.getLastError();
                        if (lastError == 259) {
                            break;
                            break;
                        }
                        if (lastError != 0) {
                            continue;
                        }
                    }
                    items.add(entry2);
                } else {
                    Wininet.INTERNET_CACHE_ENTRY_INFO entry3 = new Wininet.INTERNET_CACHE_ENTRY_INFO(size2.getValue());
                    result = Wininet.INSTANCE.FindNextUrlCacheEntry(cacheHandle2, entry3, size2);
                    if (result) {
                        lastError = Native.getLastError();
                        if (lastError == 259) {
                            break;
                        }
                        if (lastError != 0 && lastError != 122) {
                            throw new Win32Exception(lastError);
                        }
                    }
                    items.add(entry3);
                }
            }
            for (Wininet.INTERNET_CACHE_ENTRY_INFO item : items) {
                cacheItems.put(item.lpszSourceUrlName.getWideString(0L), item.lpszLocalFileName == null ? "" : item.lpszLocalFileName.getWideString(0L));
            }
            if (cacheHandle2 != null && !Wininet.INSTANCE.FindCloseUrlCache(cacheHandle2) && 0 != 0) {
                Win32Exception e2 = new Win32Exception(Native.getLastError());
                e2.addSuppressedReflected(null);
                we = e2;
            }
            if (we != null) {
                throw we;
            }
            return cacheItems;
        } catch (Win32Exception e3) {
            we = e3;
            if (0 != 0 && !Wininet.INSTANCE.FindCloseUrlCache(null) && we != null) {
                Win32Exception e4 = new Win32Exception(Native.getLastError());
                e4.addSuppressedReflected(we);
                we = e4;
            }
        } catch (Throwable th) {
            if (0 != 0 && !Wininet.INSTANCE.FindCloseUrlCache(null) && 0 != 0) {
                Win32Exception e5 = new Win32Exception(Native.getLastError());
                e5.addSuppressedReflected(null);
            }
            throw th;
        }
    }
}
