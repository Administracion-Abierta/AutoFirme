package com.sun.jna.platform.win32;

import com.sun.jna.win32.StdCallLibrary;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/AccCtrl.class */
public abstract class AccCtrl implements StdCallLibrary {

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/AccCtrl$SE_OBJECT_TYPE.class */
    public abstract class SE_OBJECT_TYPE {
        public static final int SE_UNKNOWN_OBJECT_TYPE = 0;
        public static final int SE_FILE_OBJECT = 1;
        public static final int SE_SERVICE = 2;
        public static final int SE_PRINTER = 3;
        public static final int SE_REGISTRY_KEY = 4;
        public static final int SE_LMSHARE = 5;
        public static final int SE_KERNEL_OBJECT = 6;
        public static final int SE_WINDOW_OBJECT = 7;
        public static final int SE_DS_OBJECT = 8;
        public static final int SE_DS_OBJECT_ALL = 9;
        public static final int SE_PROVIDER_DEFINED_OBJECT = 10;
        public static final int SE_WMIGUID_OBJECT = 11;
        public static final int SE_REGISTRY_WOW64_32KEY = 12;

        public SE_OBJECT_TYPE() {
        }
    }
}
