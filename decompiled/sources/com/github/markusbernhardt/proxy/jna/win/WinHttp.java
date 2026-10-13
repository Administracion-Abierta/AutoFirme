package com.github.markusbernhardt.proxy.jna.win;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/jna/win/WinHttp.class */
public interface WinHttp extends StdCallLibrary {
    public static final WinHttp INSTANCE = (WinHttp) Native.load("winhttp", WinHttp.class, W32APIOptions.UNICODE_OPTIONS);

    boolean WinHttpDetectAutoProxyConfigUrl(WinDef.DWORD dword, PointerByReference pointerByReference) throws LastErrorException;

    boolean WinHttpGetDefaultProxyConfiguration(WinHttpProxyInfo winHttpProxyInfo);

    boolean WinHttpGetIEProxyConfigForCurrentUser(WinHttpCurrentUserIEProxyConfig winHttpCurrentUserIEProxyConfig);
}
