package com.sun.jna.platform.win32.COM;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/COMUtils.class */
public abstract class COMUtils {
    public static final int S_OK = 0;
    public static final int S_FALSE = 1;
    public static final int E_UNEXPECTED = -2147418113;

    public static boolean SUCCEEDED(WinNT.HRESULT hr) {
        return SUCCEEDED(hr.intValue());
    }

    public static boolean SUCCEEDED(int hr) {
        return hr >= 0;
    }

    public static boolean FAILED(WinNT.HRESULT hr) {
        return FAILED(hr.intValue());
    }

    public static boolean FAILED(int hr) {
        return hr < 0;
    }

    public static void checkRC(WinNT.HRESULT hr) {
        String formatMessage;
        if (FAILED(hr)) {
            try {
                formatMessage = Kernel32Util.formatMessage(hr) + "(HRESULT: " + Integer.toHexString(hr.intValue()) + ")";
            } catch (LastErrorException e) {
                formatMessage = "(HRESULT: " + Integer.toHexString(hr.intValue()) + ")";
            }
            throw new COMException(formatMessage, hr);
        }
    }

    public static void checkRC(WinNT.HRESULT hr, OaIdl.EXCEPINFO pExcepInfo, IntByReference puArgErr) {
        if (FAILED(hr)) {
            StringBuilder formatMessage = new StringBuilder();
            Integer errorArg = null;
            Integer wCode = null;
            Integer scode = null;
            String description = null;
            String helpFile = null;
            Integer helpCtx = null;
            String source = null;
            if (puArgErr != null) {
                errorArg = Integer.valueOf(puArgErr.getValue());
            }
            try {
                formatMessage.append(Kernel32Util.formatMessage(hr));
            } catch (LastErrorException e) {
            }
            formatMessage.append("(HRESULT: ");
            formatMessage.append(Integer.toHexString(hr.intValue()));
            formatMessage.append(")");
            if (pExcepInfo != null) {
                wCode = Integer.valueOf(pExcepInfo.wCode.intValue());
                scode = Integer.valueOf(pExcepInfo.scode.intValue());
                helpCtx = Integer.valueOf(pExcepInfo.dwHelpContext.intValue());
                if (pExcepInfo.bstrSource != null) {
                    source = pExcepInfo.bstrSource.getValue();
                    formatMessage.append("\nSource:      ");
                    formatMessage.append(source);
                }
                if (pExcepInfo.bstrDescription != null) {
                    description = pExcepInfo.bstrDescription.getValue();
                    formatMessage.append("\nDescription: ");
                    formatMessage.append(description);
                }
                if (pExcepInfo.bstrHelpFile != null) {
                    helpFile = pExcepInfo.bstrHelpFile.getValue();
                }
            }
            throw new COMInvokeException(formatMessage.toString(), hr, errorArg, description, helpCtx, helpFile, scode, source, wCode);
        }
        if (pExcepInfo != null) {
            if (pExcepInfo.bstrSource != null) {
                OleAuto.INSTANCE.SysFreeString(pExcepInfo.bstrSource);
            }
            if (pExcepInfo.bstrDescription != null) {
                OleAuto.INSTANCE.SysFreeString(pExcepInfo.bstrDescription);
            }
            if (pExcepInfo.bstrHelpFile != null) {
                OleAuto.INSTANCE.SysFreeString(pExcepInfo.bstrHelpFile);
            }
        }
        if (0 != 0) {
            throw null;
        }
    }

    public static ArrayList<COMInfo> getAllCOMInfoOnSystem() {
        WinReg.HKEYByReference phkResult = new WinReg.HKEYByReference();
        WinReg.HKEYByReference phkResult2 = new WinReg.HKEYByReference();
        ArrayList<COMInfo> comInfos = new ArrayList<>();
        try {
            WinReg.HKEYByReference phkResult3 = Advapi32Util.registryGetKey(WinReg.HKEY_CLASSES_ROOT, "CLSID", 131097);
            Advapi32Util.InfoKey infoKey = Advapi32Util.registryQueryInfoKey(phkResult3.getValue(), 131097);
            for (int i = 0; i < infoKey.lpcSubKeys.getValue(); i++) {
                Advapi32Util.EnumKey enumKey = Advapi32Util.registryRegEnumKey(phkResult3.getValue(), i);
                String subKey = Native.toString(enumKey.lpName);
                COMInfo comInfo = new COMInfo(subKey);
                phkResult2 = Advapi32Util.registryGetKey(phkResult3.getValue(), subKey, 131097);
                Advapi32Util.InfoKey infoKey2 = Advapi32Util.registryQueryInfoKey(phkResult2.getValue(), 131097);
                for (int y = 0; y < infoKey2.lpcSubKeys.getValue(); y++) {
                    Advapi32Util.EnumKey enumKey2 = Advapi32Util.registryRegEnumKey(phkResult2.getValue(), y);
                    String subKey2 = Native.toString(enumKey2.lpName);
                    if (subKey2.equals("InprocHandler32")) {
                        comInfo.inprocHandler32 = (String) Advapi32Util.registryGetValue(phkResult2.getValue(), subKey2, null);
                    } else if (subKey2.equals("InprocServer32")) {
                        comInfo.inprocServer32 = (String) Advapi32Util.registryGetValue(phkResult2.getValue(), subKey2, null);
                    } else if (subKey2.equals("LocalServer32")) {
                        comInfo.localServer32 = (String) Advapi32Util.registryGetValue(phkResult2.getValue(), subKey2, null);
                    } else if (subKey2.equals("ProgID")) {
                        comInfo.progID = (String) Advapi32Util.registryGetValue(phkResult2.getValue(), subKey2, null);
                    } else if (subKey2.equals("TypeLib")) {
                        comInfo.typeLib = (String) Advapi32Util.registryGetValue(phkResult2.getValue(), subKey2, null);
                    }
                }
                Advapi32.INSTANCE.RegCloseKey(phkResult2.getValue());
                comInfos.add(comInfo);
            }
            Advapi32 advapi32 = Advapi32.INSTANCE;
            return comInfos;
        } finally {
            Advapi32.INSTANCE.RegCloseKey(phkResult.getValue());
            Advapi32.INSTANCE.RegCloseKey(phkResult2.getValue());
        }
    }

    public static boolean comIsInitialized() {
        WinNT.HRESULT hr = Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, 0);
        if (hr.equals(W32Errors.S_OK)) {
            Ole32.INSTANCE.CoUninitialize();
            return false;
        }
        if (hr.equals(W32Errors.S_FALSE)) {
            Ole32.INSTANCE.CoUninitialize();
            return true;
        }
        if (hr.intValue() == -2147417850) {
            return true;
        }
        checkRC(hr);
        return false;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/COMUtils$COMInfo.class */
    public static class COMInfo {
        public String clsid;
        public String inprocHandler32;
        public String inprocServer32;
        public String localServer32;
        public String progID;
        public String typeLib;

        public COMInfo() {
        }

        public COMInfo(String clsid) {
            this.clsid = clsid;
        }
    }
}
