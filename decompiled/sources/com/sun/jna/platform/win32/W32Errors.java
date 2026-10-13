package com.sun.jna.platform.win32;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/W32Errors.class */
public abstract class W32Errors implements WinError {
    public static final boolean SUCCEEDED(int hr) {
        return hr >= 0;
    }

    public static final boolean FAILED(int hr) {
        return hr < 0;
    }

    public static final boolean SUCCEEDED(WinNT.HRESULT hr) {
        return hr == null || SUCCEEDED(hr.intValue());
    }

    public static final boolean FAILED(WinNT.HRESULT hr) {
        return hr != null && FAILED(hr.intValue());
    }

    public static final int HRESULT_CODE(int hr) {
        return hr & 65535;
    }

    public static final int SCODE_CODE(int sc) {
        return sc & 65535;
    }

    public static final int HRESULT_FACILITY(int hr) {
        return (hr >> 16) & 8191;
    }

    public static final int SCODE_FACILITY(short sc) {
        return ((short) (sc >> 16)) & 8191;
    }

    public static short HRESULT_SEVERITY(int hr) {
        return (short) ((hr >> 31) & 1);
    }

    public static short SCODE_SEVERITY(short sc) {
        return (short) (((short) (sc >> 31)) & 1);
    }

    public static int MAKE_HRESULT(short sev, short fac, short code) {
        return (sev << 31) | (fac << 16) | code;
    }

    public static final int MAKE_SCODE(short sev, short fac, short code) {
        return (sev << 31) | (fac << 16) | code;
    }

    public static final WinNT.HRESULT HRESULT_FROM_WIN32(int x) {
        return new WinNT.HRESULT(x <= 0 ? x : (x & 65535) | (7 << 16) | Integer.MIN_VALUE);
    }

    public static final int FILTER_HRESULT_FROM_FLT_NTSTATUS(int x) {
        return (x & (-2147418113)) | (31 << 16);
    }
}
