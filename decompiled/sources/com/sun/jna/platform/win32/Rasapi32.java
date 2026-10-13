package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Rasapi32.class */
public interface Rasapi32 extends StdCallLibrary {
    public static final Rasapi32 INSTANCE = (Rasapi32) Native.load("Rasapi32", Rasapi32.class, W32APIOptions.DEFAULT_OPTIONS);

    int RasDial(WinRas.RASDIALEXTENSIONS.ByReference byReference, String str, WinRas.RASDIALPARAMS.ByReference byReference2, int i, WinRas.RasDialFunc2 rasDialFunc2, WinNT.HANDLEByReference hANDLEByReference);

    int RasEnumConnections(WinRas.RASCONN[] rasconnArr, IntByReference intByReference, IntByReference intByReference2);

    int RasGetConnectionStatistics(WinNT.HANDLE handle, Structure.ByReference byReference);

    int RasGetConnectStatus(WinNT.HANDLE handle, Structure.ByReference byReference);

    int RasGetCredentials(String str, String str2, WinRas.RASCREDENTIALS.ByReference byReference);

    int RasGetEntryProperties(String str, String str2, WinRas.RASENTRY.ByReference byReference, IntByReference intByReference, Pointer pointer, Pointer pointer2);

    int RasGetProjectionInfo(WinNT.HANDLE handle, int i, Pointer pointer, IntByReference intByReference);

    int RasHangUp(WinNT.HANDLE handle);

    int RasSetEntryProperties(String str, String str2, WinRas.RASENTRY.ByReference byReference, int i, byte[] bArr, int i2);

    int RasGetEntryDialParams(String str, WinRas.RASDIALPARAMS.ByReference byReference, WinDef.BOOLByReference bOOLByReference);

    int RasGetErrorString(int i, char[] cArr, int i2);
}
