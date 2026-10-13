package com.sun.jna.platform.win32;

import com.sun.jna.Native;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Ole32Util.class */
public abstract class Ole32Util {
    public static Guid.GUID getGUIDFromString(String guidString) {
        Guid.GUID lpiid = new Guid.GUID();
        WinNT.HRESULT hr = Ole32.INSTANCE.IIDFromString(guidString, lpiid);
        if (!hr.equals(W32Errors.S_OK)) {
            throw new RuntimeException(hr.toString());
        }
        return lpiid;
    }

    public static String getStringFromGUID(Guid.GUID guid) {
        Guid.GUID pguid = new Guid.GUID(guid.getPointer());
        char[] lpsz = new char[39];
        int len = Ole32.INSTANCE.StringFromGUID2(pguid, lpsz, 39);
        if (len == 0) {
            throw new RuntimeException("StringFromGUID2");
        }
        lpsz[len - 1] = 0;
        return Native.toString(lpsz);
    }

    public static Guid.GUID generateGUID() {
        Guid.GUID pguid = new Guid.GUID();
        WinNT.HRESULT hr = Ole32.INSTANCE.CoCreateGuid(pguid);
        if (!hr.equals(W32Errors.S_OK)) {
            throw new RuntimeException(hr.toString());
        }
        return pguid;
    }
}
