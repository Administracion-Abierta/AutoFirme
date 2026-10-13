package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.WinDef;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/util/ComEventCallbackCookie.class */
public class ComEventCallbackCookie implements IComEventCallbackCookie {
    WinDef.DWORD value;

    public ComEventCallbackCookie(WinDef.DWORD value) {
        this.value = value;
    }

    public WinDef.DWORD getValue() {
        return this.value;
    }
}
