package com.github.markusbernhardt.proxy.jna.win;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WTypes;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/jna/win/WinHttpCurrentUserIEProxyConfig.class */
public class WinHttpCurrentUserIEProxyConfig extends Structure {
    public boolean fAutoDetect;
    public WTypes.LPWSTR lpszAutoConfigUrl;
    public WTypes.LPWSTR lpszProxy;
    public WTypes.LPWSTR lpszProxyBypass;

    /* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/jna/win/WinHttpCurrentUserIEProxyConfig$ByReference.class */
    public static class ByReference extends WinHttpProxyInfo implements Structure.ByReference {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/jna/win/WinHttpCurrentUserIEProxyConfig$ByValue.class */
    public static class ByValue extends WinHttpProxyInfo implements Structure.ByValue {
    }

    public WinHttpCurrentUserIEProxyConfig() {
    }

    public WinHttpCurrentUserIEProxyConfig(Pointer pointer) {
        super(pointer);
        read();
    }

    @Override // com.sun.jna.Structure
    protected List<String> getFieldOrder() {
        return Arrays.asList("fAutoDetect", "lpszAutoConfigUrl", "lpszProxy", "lpszProxyBypass");
    }
}
