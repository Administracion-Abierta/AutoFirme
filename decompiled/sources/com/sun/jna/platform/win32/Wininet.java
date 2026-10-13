package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Wininet.class */
public interface Wininet extends StdCallLibrary {
    public static final Wininet INSTANCE = (Wininet) Native.load("wininet", Wininet.class, W32APIOptions.DEFAULT_OPTIONS);
    public static final int NORMAL_CACHE_ENTRY = 1;
    public static final int STICKY_CACHE_ENTRY = 4;
    public static final int EDITED_CACHE_ENTRY = 8;
    public static final int TRACK_OFFLINE_CACHE_ENTRY = 16;
    public static final int TRACK_ONLINE_CACHE_ENTRY = 32;
    public static final int SPARSE_CACHE_ENTRY = 65536;
    public static final int COOKIE_CACHE_ENTRY = 1048576;
    public static final int URLHISTORY_CACHE_ENTRY = 2097152;

    boolean FindCloseUrlCache(WinNT.HANDLE handle);

    boolean DeleteUrlCacheEntry(String str);

    WinNT.HANDLE FindFirstUrlCacheEntry(String str, INTERNET_CACHE_ENTRY_INFO internet_cache_entry_info, IntByReference intByReference);

    boolean FindNextUrlCacheEntry(WinNT.HANDLE handle, INTERNET_CACHE_ENTRY_INFO internet_cache_entry_info, IntByReference intByReference);

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Wininet$INTERNET_CACHE_ENTRY_INFO.class */
    @Structure.FieldOrder({"dwStructSize", "lpszSourceUrlName", "lpszLocalFileName", "CacheEntryType", "dwUseCount", "dwHitRate", "dwSizeLow", "dwSizeHigh", "LastModifiedTime", "ExpireTime", "LastAccessTime", "LastSyncTime", "lpHeaderInfo", "dwHeaderInfoSize", "lpszFileExtension", "u", "additional"})
    public static class INTERNET_CACHE_ENTRY_INFO extends Structure {
        public int dwStructSize;
        public Pointer lpszSourceUrlName;
        public Pointer lpszLocalFileName;
        public int CacheEntryType;
        public int dwUseCount;
        public int dwHitRate;
        public int dwSizeLow;
        public int dwSizeHigh;
        public WinBase.FILETIME LastModifiedTime;
        public WinBase.FILETIME ExpireTime;
        public WinBase.FILETIME LastAccessTime;
        public WinBase.FILETIME LastSyncTime;
        public Pointer lpHeaderInfo;
        public int dwHeaderInfoSize;
        public Pointer lpszFileExtension;
        public UNION u;
        public byte[] additional;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Wininet$INTERNET_CACHE_ENTRY_INFO$UNION.class */
        public static class UNION extends Union {
            public int dwReserved;
            public int dwExemptDelta;
        }

        public INTERNET_CACHE_ENTRY_INFO(int size) {
            this.additional = new byte[size];
        }

        @Override // com.sun.jna.Structure
        public String toString() {
            return (this.lpszLocalFileName == null ? "" : this.lpszLocalFileName.getWideString(0L) + " => ") + (this.lpszSourceUrlName == null ? "null" : this.lpszSourceUrlName.getWideString(0L));
        }
    }
}
