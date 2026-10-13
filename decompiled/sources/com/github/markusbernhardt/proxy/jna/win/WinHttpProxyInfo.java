package com.github.markusbernhardt.proxy.jna.win;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/jna/win/WinHttpProxyInfo.class */
public class WinHttpProxyInfo extends Structure {
    public WinDef.DWORD dwAccessType;
    public WTypes.LPWSTR lpszProxy;
    public WTypes.LPWSTR lpszProxyBypass;

    /* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/jna/win/WinHttpProxyInfo$ByReference.class */
    public static class ByReference extends WinHttpProxyInfo implements Structure.ByReference {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/jna/win/WinHttpProxyInfo$ByValue.class */
    public static class ByValue extends WinHttpProxyInfo implements Structure.ByValue {
    }

    public WinHttpProxyInfo() {
    }

    public WinHttpProxyInfo(Pointer pointer) {
        super(pointer);
        read();
    }

    @Override // com.sun.jna.Structure
    protected List<String> getFieldOrder() {
        return Arrays.asList("dwAccessType", "lpszProxy", "lpszProxyBypass");
    }
}
