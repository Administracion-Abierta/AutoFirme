package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.PointerType;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Cryptui.class */
public interface Cryptui extends StdCallLibrary {
    public static final Cryptui INSTANCE = (Cryptui) Native.load("Cryptui", Cryptui.class, W32APIOptions.UNICODE_OPTIONS);

    WinCrypt.CERT_CONTEXT.ByReference CryptUIDlgSelectCertificateFromStore(WinCrypt.HCERTSTORE hcertstore, WinDef.HWND hwnd, String str, String str2, int i, int i2, PointerType pointerType);
}
